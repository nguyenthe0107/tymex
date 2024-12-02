package com.example.tymexproject.ui.home_screen

import app.cash.turbine.test
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
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
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
        viewModel = HomeViewModel(useCase, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.state.value
        assertEquals(emptyList<UserInfoResponse>(), state.userList)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(0, state.currentPage)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun `getUserList success updates state correctly`() = runTest {
        // Given
        val mockUsers = listOf(
            UserInfoResponse(
                id = 1,
                userName = "test",
                avatarUrl = "url",
                htmlUrl = "html"
            )
        )
        coEvery { useCase.fetchUserList(any(), any()) } returns flowOf(ResultApi.Success(mockUsers))

        // When
        viewModel.getUserList(isLoadMore = false)

        // Then
        val state = viewModel.state.value
        assertEquals(mockUsers, state.userList)
        assertFalse(state.isLoading)
        assertTrue(state.canLoadMore)
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `getUserList error shows error message`() = runTest {
        // Given
        coEvery { useCase.fetchUserList(any(), any()) } returns flowOf(
            ResultApi.Error("Test error", 404)
        )

        // When
        viewModel.getUserList(isLoadMore = false)

        // Then
        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiHomeEvent.ShowError)
            assertEquals("Test error", (event as UiHomeEvent.ShowError).message)
        }
    }

    @Test
    fun `load more appends to existing list`() = runTest {
        // Given
        val initialUsers = listOf(
            UserInfoResponse(id = 1, userName = "user1", avatarUrl = "url1", htmlUrl = "html1")
        )
        val moreUsers = listOf(
            UserInfoResponse(id = 2, userName = "user2", avatarUrl = "url2", htmlUrl = "html2")
        )

        coEvery { useCase.fetchUserList(20, 0) } returns flowOf(ResultApi.Success(initialUsers))
        coEvery { useCase.fetchUserList(20, 1) } returns flowOf(ResultApi.Success(moreUsers))

        // When
        viewModel.getUserList(isLoadMore = false) // Initial load
        viewModel.getUserList(isLoadMore = true) // Load more

        // Then
        val state = viewModel.state.value
        assertEquals(initialUsers + moreUsers, state.userList)
        assertFalse(state.isLoadingMore)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `loading states are updated correctly`() = runTest {
        // Given
        coEvery { useCase.fetchUserList(any(), any()) } returns flowOf(
            ResultApi.Loading,
            ResultApi.Success(emptyList())
        )

        // When
        viewModel.getUserList(isLoadMore = false)

        // Then
        assertTrue(viewModel.state.value.isLoading)
    }
} 