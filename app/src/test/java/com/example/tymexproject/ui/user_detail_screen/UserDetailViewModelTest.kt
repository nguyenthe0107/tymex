package com.example.tymexproject.ui.user_detail_screen

import com.example.common.di.module.DispatcherProvider
import com.example.domain.model.UserInfoResponse
import com.example.domain.usecase.UserInfoUseCase
import com.example.domain.utils.ResultApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {
    private lateinit var viewModel: UserDetailViewModel
    private lateinit var useCase: UserInfoUseCase
    private lateinit var dispatcherProvider: DispatcherProvider
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
        dispatcherProvider = mockk {
            coEvery { io } returns testDispatcher
            coEvery { main } returns testDispatcher
        }
        viewModel = UserDetailViewModel(useCase, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.state.value
        assertNull(state.userDetail)
        assertFalse(state.isLoading)
    }

    @Test
    fun `getUserDetailByUserName success updates state correctly`() = runTest {
        // Given
        val mockUser = UserInfoResponse(
            id = 1,
            userName = "test",
            avatarUrl = "url",
            htmlUrl = "html"
        )
        coEvery { useCase.fetchUserDetail(any()) } returns flowOf(ResultApi.Success(mockUser))

        // When
        viewModel.getUserDetailByUserName("test")

        // Then
        val state = viewModel.state.value
        assertEquals(mockUser, state.userDetail)
        assertFalse(state.isLoading)
    }

    @Test
    fun `getUserDetailByUserName error updates state correctly`() = runTest {
        // Given
        coEvery { useCase.fetchUserDetail(any()) } returns flowOf(
            ResultApi.Error("User not found", 404)
        )

        // When
        viewModel.getUserDetailByUserName("test")

        // Then
        val state = viewModel.state.value
        assertNull(state.userDetail)
        assertFalse(state.isLoading)
    }

    @Test
    fun `null username is handled correctly`() = runTest {
        // When
        viewModel.getUserDetailByUserName(null)
        // Then
        val state = viewModel.state.value
        assertNull(state.userDetail)
        assertFalse(state.isLoading)
    }
} 