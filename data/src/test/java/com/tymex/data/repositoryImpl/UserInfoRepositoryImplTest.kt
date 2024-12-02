package com.tymex.data.repositoryImpl

import app.cash.turbine.test
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.UserInfoDTO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.*

class UserInfoRepositoryImplTest {
    private lateinit var repository: UserInfoRepositoryImpl
    private lateinit var apiService: ApiService
    private lateinit var userPreferencesManager: UserPreferencesManager

    @Before
    fun setup() {
        apiService = mockk()
        userPreferencesManager = mockk(relaxed = true)
        repository = UserInfoRepositoryImpl(apiService, userPreferencesManager)
    }

    @Test
    fun `fetchUserList success from API`() = runTest {
        // Given
        val mockUsers = listOf(createMockUserDTO())
        coEvery { apiService.fetchUserList(any(), any()) } returns Response.success(mockUsers)
        coEvery { userPreferencesManager.getUserList() } returns flowOf(null)
        // When & Then
        repository.fetchUserList(20, 0).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(mockUsers[0].userName, (result as ResultApi.Success).data[0].userName)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserList returns cached data when API fails`() = runTest {
        // Given
        val mockCachedUsers = listOf(createMockUserDTO(id = 999))
        coEvery { apiService.fetchUserList(any(), any()) } throws Exception("API Error")
        coEvery { userPreferencesManager.getUserList() } returns flowOf(mockCachedUsers)

        // When & Then
        repository.fetchUserList(20, 0).test {
            val errorResult = awaitItem()
            assertTrue(errorResult is ResultApi.Error)
            
            val cachedResult = awaitItem()
            assertTrue(cachedResult is ResultApi.Success)
            assertEquals(999, (cachedResult as ResultApi.Success).data[0].id)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns cached user when available`() = runTest {
        // Given
        val mockUser = createMockUserDTO()
        coEvery { userPreferencesManager.getUserList() } returns flowOf(listOf(mockUser))

        // When & Then
        repository.fetchUserDetail(mockUser.userName).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(mockUser.userName, (result as ResultApi.Success).data.userName)
            awaitComplete()
        }
    }

    private fun createMockUserDTO(id: Int = 1) = UserInfoDTO(
        id = id,
        userName = "test$id",
        avatarUrl = "url$id",
        htmlUrl = "html$id",
        location = null,
        followers = null,
        following = null
    )
} 