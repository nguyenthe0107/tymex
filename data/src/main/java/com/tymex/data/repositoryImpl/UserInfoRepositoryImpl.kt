package com.tymex.data.repositoryImpl

import com.example.config.Constants.EMPTY_DATA
import com.example.config.Constants.ERR_COMMON
import com.example.domain.model.UserInfoResponse
import com.example.domain.repository.UserInfoRepository
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.UserInfoDTO
import com.tymex.data.model.toUserInfo
import com.tymex.data.repositoryImpl.HelperApi.emitCommonErr
import com.tymex.data.repositoryImpl.HelperApi.handleError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of UserInfoRepository that handles user data operations
 *
 * This repository implements caching strategy with local storage fallback
 * and handles both network and cached data operations.
 *
 * @property apiService Service for API calls
 * @property userPreferencesManager Manager for local data persistence
 */
class UserInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userPreferencesManager: UserPreferencesManager
) : UserInfoRepository {

    /**
     * Fetches paginated list of users
     *
     * @param perPage Number of items per page
     * @param since ID to start fetching from
     * @return Flow of ResultApi containing list of users
     *
     * Flow steps:
     * 1. Emit loading state
     * 2. Try fetching from API
     * 3. Cache successful response
     * 4. Fallback to cache on error
     */
    override suspend fun fetchUserList(
        perPage: Int,
        since: Int
    ): Flow<ResultApi<List<UserInfoResponse>>> {
        return flow {
            // Emit loading
            emit(ResultApi.Loading)
            try {
                val response = apiService.fetchUserList(perPage, since)
                if (response.isSuccessful) {
                    val users: List<UserInfoDTO> = response.body() ?: emptyList()
                    if (users.isEmpty()) {
                        emit(ResultApi.Error(EMPTY_DATA, response.code()))
                        return@flow
                    }
                    userPreferencesManager.saveUserList(users)
                    emit(ResultApi.Success(users.map { it.toUserInfo() }))
                } else {
                    // handle error
                    handleError(response)
                    return@flow
                }
            } catch (e: Exception) {
                // handle from cached
                val cachedUsers = userPreferencesManager.getUserList().first()
                if (!cachedUsers.isNullOrEmpty()) {
                    emit(ResultApi.Success(cachedUsers.map { it.toUserInfo() }))
                } else {
                    // emit error from exception
                    emit(ResultApi.Error(ERR_COMMON, code = -1, cause = e))
                    return@flow
                }
            }
        }
    }

    /**
     * Fetches detailed information for a specific user
     *
     * @param userName Username to fetch details for
     * @return Flow of ResultApi containing user details
     *
     * Flow steps:
     * 1. Emit loading state
     * 2. Try fetching from API
     * 3. Fallback to cache on error
     * 4. Handle various error cases
     */
    override suspend fun fetchUserDetail(userName: String): Flow<ResultApi<UserInfoResponse>> {
        return flow {
            emit(ResultApi.Loading)
            try {
                val response = apiService.fetchUserDetail(userName)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        emit(ResultApi.Success(user.toUserInfo()))
                    } else {
                        emit(ResultApi.Error(EMPTY_DATA, response.code()))
                    }
                } else {
                    handleError(response)
                    return@flow
                }
            } catch (e: Exception) {
                val cachedUsers = userPreferencesManager.getUserList().first()
                if (cachedUsers.isNullOrEmpty()) {
                    emitCommonErr(e)
                    return@flow
                } else {
                    val userDetail = cachedUsers.find { it.userName == userName }
                    if (userDetail == null) {
                        emitCommonErr(e)
                        return@flow
                    }
                    emit(ResultApi.Success(userDetail.toUserInfo()))
                }
            }
        }
    }
}
