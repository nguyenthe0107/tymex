package com.example.domain.repository
import com.example.domain.model.UserDetailInfo
import com.example.domain.model.UserInfo
import com.example.domain.utils.ResultApi
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    suspend fun fetchUserList(perPage:Int, since:Int): Flow<ResultApi<List<UserInfo>>>
    suspend fun fetchUserDetail(userName:String): Flow<ResultApi<UserDetailInfo>>
}