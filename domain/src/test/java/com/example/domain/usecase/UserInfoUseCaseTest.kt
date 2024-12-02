package com.example.domain.usecase

import app.cash.turbine.test
import com.example.domain.model.UserInfoResponse
import com.example.domain.repository.UserInfoRepository
import com.example.domain.utils.ResultApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserInfoUseCaseTest {
    private lateinit var useCase: UserInfoUseCase
    private lateinit var repository: UserInfoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UserInfoUseCase(repository)
    }

    @Test
    fun `fetchUserList returns success with user list`() = runTest {
        // Given
        val mockUsers = listOf(
            UserInfoResponse(
                id = 1,
                userName = "test",
                avatarUrl = "url",
                htmlUrl = "html"
            )
        )
        coEvery { repository.fetchUserList(any(), any()) } returns flowOf(ResultApi.Success(mockUsers))

        // When & Then
        useCase.fetchUserList(perPage = 20, since = 0).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(mockUsers, (result as ResultApi.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserList returns error when repository fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        val errorCode = 404
        coEvery { repository.fetchUserList(any(), any()) } returns flowOf(
            ResultApi.Error(errorMessage, errorCode)
        )

        // When & Then
        useCase.fetchUserList(perPage = 20, since = 0).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Error)
            assertEquals(errorMessage, (result as ResultApi.Error).message)
            assertEquals(errorCode, result.code)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns success with user details`() = runTest {
        // Given
        val mockUser = UserInfoResponse(
            id = 1,
            userName = "test",
            avatarUrl = "url",
            htmlUrl = "html"
        )
        coEvery { repository.fetchUserDetail(any()) } returns flowOf(ResultApi.Success(mockUser))

        // When & Then
        useCase.fetchUserDetail("test").test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(mockUser, (result as ResultApi.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns error when repository fails`() = runTest {
        // Given
        val errorMessage = "User not found"
        val errorCode = 404
        coEvery { repository.fetchUserDetail(any()) } returns flowOf(
            ResultApi.Error(errorMessage, errorCode)
        )

        // When & Then
        useCase.fetchUserDetail("test").test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Error)
            assertEquals(errorMessage, (result as ResultApi.Error).message)
            assertEquals(errorCode, result.code)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserList with pagination works correctly`() = runTest {
        // Given
        val firstPage = listOf(
            UserInfoResponse(id = 1, userName = "user1", avatarUrl = "url1", htmlUrl = "html1")
        )
        val secondPage = listOf(
            UserInfoResponse(id = 2, userName = "user2", avatarUrl = "url2", htmlUrl = "html2")
        )

        coEvery { repository.fetchUserList(20, 0) } returns flowOf(ResultApi.Success(firstPage))
        coEvery { repository.fetchUserList(20, 1) } returns flowOf(ResultApi.Success(secondPage))

        // When & Then
        // First page
        useCase.fetchUserList(perPage = 20, since = 0).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(firstPage, (result as ResultApi.Success).data)
            awaitComplete()
        }

        // Second page
        useCase.fetchUserList(perPage = 20, since = 1).test {
            val result = awaitItem()
            assertTrue(result is ResultApi.Success)
            assertEquals(secondPage, (result as ResultApi.Success).data)
            awaitComplete()
        }
    }
} 