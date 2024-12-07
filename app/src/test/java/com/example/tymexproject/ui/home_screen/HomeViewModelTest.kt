package com.example.tymexproject.ui.home_screen

import app.cash.turbine.test
import com.example.common.di.module.DispatcherProvider
import com.example.config.Constants.ERR_COMMON
import com.example.domain.model.UserInfoResponse
import com.example.domain.usecase.UserInfoUseCase
import com.example.domain.utils.ResultApi
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var userInfoUseCase: UserInfoUseCase
    private lateinit var dispatcherProvider: DispatcherProvider
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockUser = UserInfoResponse(
        id = 1,
        userName = "test",
        avatarUrl = "url",
        htmlUrl = "html"
    )

    companion object{
        const val PER_PAGE = 20
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userInfoUseCase = mockk()
        dispatcherProvider = mockk {
            coEvery { io } returns testDispatcher
            coEvery { main } returns testDispatcher
        }
        viewModel = HomeViewModel(userInfoUseCase, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        val state = viewModel.state.value
        assertThat(state.userList).isEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoadingMore).isFalse()
        assertThat(state.canLoadMore).isTrue()
        assertThat(state.currentPage).isEqualTo(1)
    }

    @Test
    fun `getUserList should fetch first page with since=1`() = runTest {
        // Arrange
        val firstPageUsers = listOf(mockUser.copy(id = 1))
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Success(firstPageUsers))

        // Act
        viewModel.getUserList(isLoadMore = false)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertThat(state.userList).isEqualTo(firstPageUsers)
        assertThat(state.isLoading).isFalse()
        assertThat(state.currentPage).isEqualTo(2)
        assertThat(state.canLoadMore).isEqualTo(state.userList.size == PER_PAGE)
    }

    @Test
    fun getUserList_shouldLoadMore_withCorrectSinceValue() = runTest {
        // Arrange - First page (20 items)
        val firstPageUsers = List(PER_PAGE) { index ->
            mockUser.copy(id = index + 1)
        }
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Success(firstPageUsers))
        // Arrange - Second page (20 items)
        val secondPageUsers = List(PER_PAGE) { index ->
            mockUser.copy(id = index + 21) // IDs start from 21
        }
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 21) } returns flowOf(ResultApi.Success(secondPageUsers))
        // Act - Load first page
        viewModel.getUserList(isLoadMore = false)
        advanceUntilIdle()

        // Assert first page
        var state = viewModel.state.value
        assertThat(state.userList).isEqualTo(firstPageUsers)
        assertThat(state.isLoading).isFalse()
        assertThat(state.currentPage).isEqualTo(2)
        assertThat(state.canLoadMore).isTrue()

        // Act - Load second page
        viewModel.getUserList(isLoadMore = true)
        advanceUntilIdle()

        // Assert after load more
        state = viewModel.state.value
        assertThat(state.userList).isEqualTo(firstPageUsers + secondPageUsers)
        assertThat(state.isLoadingMore).isFalse()
        assertThat(state.currentPage).isEqualTo(3)
        assertThat(state.canLoadMore).isTrue()
        // Should have 40 items total
        assertThat(state.userList.size).isEqualTo(40)
    }

    @Test
    fun `getUserList should emit error event when API call fails`() = runTest {
        // Arrange
        val errorMessage = ERR_COMMON
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Error(errorMessage))

        // Act & Assert
        viewModel.eventFlow.test {
            viewModel.getUserList(isLoadMore = false)
            advanceUntilIdle()

            // Verify state
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoadingMore).isFalse()

            // Verify error event
            val event = awaitItem()
            assertThat(event).isInstanceOf(UiHomeEvent.ShowError::class.java)
            assertThat((event as UiHomeEvent.ShowError).message).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `getUserList should set canLoadMore false when receiving less than PER_PAGE items`() = runTest {
        // Arrange
        val incompletePageUsers = listOf(mockUser.copy(id = 1)) // Less than PER_PAGE items
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Success(incompletePageUsers))

        // Act
        viewModel.getUserList(isLoadMore = false)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertThat(state.canLoadMore).isFalse()
    }

    @Test
    fun `getUserList should not load more when already loading`() = runTest {
        // Arrange
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Loading)

        // Act
        viewModel.getUserList(isLoadMore = false)
        viewModel.getUserList(isLoadMore = false) // Second call should be ignored

        // Assert
        val state = viewModel.state.value
        assertThat(state.isLoading).isTrue()
    }

    @Test
    fun `getUserList should not load more when canLoadMore is false`() = runTest {
        // Arrange - Set up initial state with canLoadMore = false
        val incompletePageUsers = listOf(mockUser.copy(id = 1))
        coEvery { userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = 1) } returns flowOf(ResultApi.Success(incompletePageUsers))

        // Act - First load to set canLoadMore = false
        viewModel.getUserList(isLoadMore = false)
        advanceUntilIdle()

        // Act - Try to load more
        viewModel.getUserList(isLoadMore = true)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertThat(state.isLoadingMore).isFalse()
        assertThat(state.canLoadMore).isFalse()
        assertThat(state.userList).isEqualTo(incompletePageUsers)
    }
}

