package com.tymex.data.repositoryImpl

import com.example.domain.model.UserDetailInfo
import com.example.domain.model.UserInfo
import com.example.domain.repository.UserInfoRepository
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.toUserDetailInfo
import com.tymex.data.model.toUserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserInfoRepository {

    override suspend fun fetchUserList(perPage: Int, since: Int): Flow<ResultApi<List<UserInfo>>> {
        return flow {
            kotlin.runCatching {
                apiService.fetchUserList(
                    perPage,
                    since
                )
            }.onSuccess {
                if (it.isSuccessful) {
                    it.body()?.let { response ->
                        emit(
                            ResultApi.Success(response.map { it1 ->
                                it1.toUserInfo()
                            }.toList())
                        )
                    }
                }
            }
                .onFailure {
                }
        }
    }

    override suspend fun fetchUserDetail(userName: String): Flow<ResultApi<UserDetailInfo>> {
        return flow {
            kotlin.runCatching {
                apiService.fetchUserDetail(userName)
            }.onSuccess {
                if (it.isSuccessful) {
                    it.body()?.let { response ->
                        emit(
                            ResultApi.Success(response.toUserDetailInfo())
                        )
                    }
                }
            }
                .onFailure {
                }
        }
    }

}