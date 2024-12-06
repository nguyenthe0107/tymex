package com.example.domain.repository
import com.example.domain.model.UserInfoResponse
import com.example.domain.utils.ResultApi
import kotlinx.coroutines.flow.Flow
/**
 * Repository interface for handling user information operations
 *
 * This interface defines the contract for fetching user data from remote/local sources.
 * It uses Kotlin Flow for reactive data streaming and ResultApi for handling operation states.
 */
interface UserInfoRepository {
    suspend fun fetchUserList(perPage:Int, since:Int): Flow<ResultApi<List<UserInfoResponse>>>
    suspend fun fetchUserDetail(userName:String): Flow<ResultApi<UserInfoResponse>>
}