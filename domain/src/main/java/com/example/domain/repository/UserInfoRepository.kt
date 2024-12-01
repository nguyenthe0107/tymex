package com.example.domain.repository
import com.example.domain.model.UserInfoResponse
import com.example.domain.utils.ResultApi
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    suspend fun fetchUserList(perPage:Int, since:Int): Flow<ResultApi<List<UserInfoResponse>>>
    suspend fun fetchUserDetail(userName:String): Flow<ResultApi<UserInfoResponse>>
}