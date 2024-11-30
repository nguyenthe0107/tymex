package com.example.domain.usecase
import com.example.domain.repository.UserInfoRepository
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
){
    suspend fun fetchUserList(perPage:Int, since:Int) = userInfoRepository.fetchUserList(perPage, since)
    suspend fun fetchUserDetail(userName:String) = userInfoRepository.fetchUserDetail(userName)
}