package com.tymex.data.repositoryImpl
import com.example.domain.model.UserInfoResponse
import com.example.domain.repository.UserInfoRepository
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.UserInfoDTO
import com.tymex.data.model.toUserInfo
import com.tymex.data.repositoryImpl.Constants.EMPTY_DATA
import com.tymex.data.repositoryImpl.Constants.ERR_COMMON
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userPreferencesManager: UserPreferencesManager
) : UserInfoRepository {

    /*
    * function to get users from api
    * */
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
                    val users : List<UserInfoDTO> = response.body() ?: emptyList()
                    if(users.isEmpty()){
                        emit(ResultApi.Error(EMPTY_DATA, response.code()))
                        return@flow
                    }
                    userPreferencesManager.saveUserList(users)
                    emit(ResultApi.Success(users.map { it.toUserInfo() }))
                } else {
                    // handle error
                    emit(ResultApi.Error(
                        message = "$ERR_COMMON: ${response.message()}",
                        code = response.code()
                    ))
                }
            } catch (e: Exception) {
                // handle from cached
                val cachedUsers = userPreferencesManager.getUserList().first()
                if (!cachedUsers.isNullOrEmpty()) {
                    emit(ResultApi.Success(cachedUsers.map { it.toUserInfo() }))
                } else {
                    // emit error from exception
                    emit(ResultApi.Error(ERR_COMMON, code = -1, cause = e))
                }
            }
        }
    }

    /*
    * function to get detail user by userName
    * */
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
                    emit(ResultApi.Error(
                        message = "$ERR_COMMON: ${response.message()}",
                        code = response.code()
                    ))
                }
            } catch (e: Exception) {
                val cachedUsers = userPreferencesManager.getUserList().first()
                if(cachedUsers.isNullOrEmpty()){
                    emit(
                        ResultApi.Error(
                            "$ERR_COMMON: ${e.message}",
                            code = -1,
                            cause = e
                        )
                    )
                }else{
                    val userDetail = cachedUsers.find { it.userName == userName }
                    if(userDetail == null){
                        emit(
                            ResultApi.Error(
                                "$ERR_COMMON: ${e.message}",
                                code = -1,
                                cause = e
                            )
                        )
                        return@flow
                    }

                    emit(ResultApi.Success(userDetail.toUserInfo()))
                }
            }
        }
    }
}