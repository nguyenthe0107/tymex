package com.example.domain.usecase

import com.example.domain.repository.UserInfoRepository
import javax.inject.Inject

/**
 * Use case class for handling user information operations
 *
 * This class serves as a business logic layer between the repository and the presentation layer.
 * It follows the Clean Architecture pattern and Single Responsibility Principle.
 *
 * @property userInfoRepository Repository instance for user data operations
 */
class UserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    /**
     * Fetches a paginated list of users
     *
     * Delegates the call to repository while maintaining the same contract.
     * Can be extended to add business logic if needed.
     *
     * @param perPage Number of users per page
     * @param since User ID to start fetching from
     * @return Flow<ResultApi<List<UserInfoResponse>>> Stream of user list states
     */
    suspend fun fetchUserList(perPage: Int, since: Int) =
        userInfoRepository.fetchUserList(perPage, since)

    /**
     * Fetches detailed information for a specific user
     *
     * Delegates the call to repository while maintaining the same contract.
     * Can be extended to add business logic if needed.
     *
     * @param userName Username to fetch details for
     * @return Flow<ResultApi<UserInfoResponse>> Stream of user detail states
     */
    suspend fun fetchUserDetail(userName: String) = userInfoRepository.fetchUserDetail(userName)
}