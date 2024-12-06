package com.example.tymexproject.ui.user_detail_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.di.module.DispatcherProvider
import com.example.domain.usecase.UserInfoUseCase
import com.example.domain.utils.ResultApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the User Detail Screen that manages fetching and displaying user details
 *
 * @property userInfoUseCase Use case for fetching user information
 * @property dispatcherProvider Provides coroutine dispatchers for async operations
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = mutableStateOf(UserDetailState())
    val state: State<UserDetailState> = _state

    private val _eventFlow = MutableSharedFlow<UiUserDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    /**
     * Fetches detailed information for a specific GitHub user
     *
     * @param userName The GitHub username to fetch details for
     *
     * Flow:
     * 1. Set loading state
     * 2. Fetch user details
     * 3. Handle success/error states
     * 4. Update UI state accordingly
     */
    fun getUserDetailByUserName(userName: String?) {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.value = state.value.copy(isLoading = true)
            userName?.let {
                userInfoUseCase.fetchUserDetail(it)
                    .collect { result ->
                        when (result) {
                            is ResultApi.Success -> {
                                _state.value = state.value.copy(userDetail = result.data)
                                _state.value = state.value.copy(isLoading = false)
                            }

                            is ResultApi.Error -> {
                                _state.value = state.value.copy(isLoading = false)
                            }

                            is ResultApi.Loading -> {
                                _state.value = state.value.copy(isLoading = true)
                            }
                        }
                    }
            }
        }
    }
}