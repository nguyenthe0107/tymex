package com.tymex.data.repositoryImpl
import com.example.domain.model.UserInfoResponse
import com.example.domain.repository.UserInfoRepository
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.toUserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userPreferencesManager: UserPreferencesManager
) : UserInfoRepository {

    override suspend fun fetchUserList(perPage: Int, since: Int): Flow<ResultApi<List<UserInfoResponse>>> {
        return flow {
            emit(ResultApi.Loading) // Emit trạng thái loading
            try {
                // Gọi API
                val response = apiService.fetchUserList(perPage, since)
                if (response.isSuccessful) {
                    val users = response.body()
                    if (!users.isNullOrEmpty()) {
                        userPreferencesManager.saveUserList(users)
                        emit(ResultApi.Success(users.map { it.toUserInfo() }))
                        return@flow
                    }
                    emit(ResultApi.Error("Empty response body", response.code()))
                } else {
                    // Xử lý lỗi từ HTTP response (ví dụ 404, 500)
                    emit(ResultApi.Error("API call failed: ${response.message()}", response.code()))
                }
            } catch (e: Exception) {
                // Nếu xảy ra lỗi, lấy từ cache
                val cachedUsers = userPreferencesManager.getUserList().first()
                if (!cachedUsers.isNullOrEmpty()) {
                    emit(ResultApi.Success(cachedUsers.map { it.toUserInfo() }))
                } else {
                    // Emit lỗi từ exception
                    emit(ResultApi.Error("Unexpected error occurred", code = -1, cause = e))
                }
            }
        }
    }

    override suspend fun fetchUserDetail(userName: String): Flow<ResultApi<UserInfoResponse>> {
        return flow {
            emit(ResultApi.Loading) // Emit trạng thái loading trước khi xử lý
            try {
                // Gọi API để lấy thông tin người dùng
                val response = apiService.fetchUserDetail(userName)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        emit(ResultApi.Success(user.toUserInfo()))
                    } else {
                        emit(ResultApi.Error("Empty response body", response.code()))
                    }
                } else {
                    emit(ResultApi.Error("API call failed: ${response.message()}", response.code()))
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ chung
                emit(ResultApi.Error("Unexpected error occurred: ${e.message}", code = -1, cause = e))
            }
        }
    }

}