package com.example.tymexproject.ui.home_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.di.module.DispatcherProvider
import com.example.domain.model.UserInfoResponse
import com.example.domain.usecase.UserInfoUseCase
import com.example.domain.utils.ResultApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _eventFlow = MutableSharedFlow<UiHomeEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getUserList()
    }

    fun getUserList() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.value = state.value.copy(isLoading = true)
            userInfoUseCase.fetchUserList(10, 1)
                .collect { result ->
                    when(result){
                        is ResultApi.Success -> {
                            _state.value = state.value.copy(userList = result.data)
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

    private fun getMockUsers(): List<UserInfoResponse> {
        return listOf(
            UserInfoResponse(
                userName = "johndoe",
                id = 1,
                avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
                htmlUrl = "https://github.com/johndoe",
            ),
            UserInfoResponse(
                userName = "janedoe",
                id = 2,
                avatarUrl = "https://avatars.githubusercontent.com/u/2?v=4",
                htmlUrl = "https://github.com/janedoe",
            ),
            UserInfoResponse(
                userName = "bobsmith",
                id = 3,
                avatarUrl = "https://avatars.githubusercontent.com/u/3?v=4",
                htmlUrl = "https://github.com/bobsmith",
            ),
            UserInfoResponse(
                userName = "alicejones",
                id = 4,
                avatarUrl = "https://avatars.githubusercontent.com/u/4?v=4",
                htmlUrl = "https://github.com/alicejones",
            ),
            UserInfoResponse(
                userName = "mikebrown",
                id = 5,
                avatarUrl = "https://avatars.githubusercontent.com/u/5?v=4",
                htmlUrl = "https://github.com/mikebrown",
            )
        )
    }
}