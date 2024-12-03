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
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.tymex.data.repositoryImpl.Constants

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var userInfoUseCase: UserInfoUseCase
    private lateinit var dispatcherProvider: DispatcherProvider
    private val testDispatcher = UnconfinedTestDispatcher()

    companion object {
        const val PER_PAGE = 20
        const val PAGE = 1
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
        Dispatchers.resetMain() // Reset to the main dispatcher after the tests
    }

    @Test
    fun `getUserList should update state with users when API call is successful`() = runTest {
        // Arrange
        val mockUsers = listOf(
            UserInfoResponse(id = 1, userName = "test", avatarUrl = "url", htmlUrl = "html")
        )
        coEvery { userInfoUseCase.fetchUserList(PER_PAGE, 1) } returns flowOf(ResultApi.Success(mockUsers))
        // Act
        viewModel.getUserList(isLoadMore = false)
        // Assert
        advanceUntilIdle() // Ensure all coroutines finish
        val state = viewModel.state.value
        assertThat(state.userList).isEqualTo(mockUsers)
        assertThat(state.isLoading).isFalse()
        assertThat(state.canLoadMore).isTrue()
    }

    @Test
    fun `getUserList should emit error event when API call fails`() = runTest {
        // Arrange
        val errorMessage = Constants.ERR_COMMON
        coEvery { userInfoUseCase.fetchUserList(PER_PAGE, PAGE) } returns flowOf(ResultApi.Error(errorMessage))
        // Act
        viewModel.getUserList(isLoadMore = false)
        // Assert
        advanceUntilIdle()
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        viewModel.eventFlow.test {
            val event = awaitItem()
            //assertThat(event).isInstanceOf(UiHomeEvent.ShowError::class.java)
            //assertThat((event as UiHomeEvent.ShowError).message).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `getUserList should append users when loading more`() = runTest {
        val user1 = listOf(mockUser.copy(id = 1))
        val user2 = listOf(mockUser.copy(id = 2))

        coEvery { userInfoUseCase.fetchUserList(PER_PAGE, 1) } returns flowOf(ResultApi.Success(user1))
        coEvery { userInfoUseCase.fetchUserList(PER_PAGE, 2) } returns flowOf(ResultApi.Success(user2))

        viewModel.getUserList(isLoadMore = false)
        advanceUntilIdle()
        
        viewModel.getUserList(isLoadMore = true)
        advanceUntilIdle()

        val state = viewModel.state.value
        println(state.userList)
        assertThat(state.userList).isEqualTo(user1 + user2)
        assertThat(state.isLoadingMore).isFalse()
        assertThat(state.canLoadMore).isTrue()
    }

    @Test
    fun `getUserList should set loading state correctly`() = runTest {
        // Arrange
        coEvery { userInfoUseCase.fetchUserList(PER_PAGE, PAGE) } returns flowOf(ResultApi.Loading)
        // Act
        viewModel.getUserList(isLoadMore = false)
        // Assert
        val state = viewModel.state.value
        assertThat(state.isLoading).isTrue()
    }

    private val mockUser = UserInfoResponse(
        id = 1,
        userName = "test",
        avatarUrl = "url",
        htmlUrl = "html"
    )
}

