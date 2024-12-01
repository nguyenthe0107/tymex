package com.example.tymexproject.ui.user_detail_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.di.module.DispatcherProvider
import com.example.domain.model.UserInfo
import com.example.domain.usecase.UserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = mutableStateOf(UserDetailState())
    val state: State<UserDetailState> = _state

    private val _eventFlow = MutableSharedFlow<UiUserDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getUserDetailByUserName()
    }

    fun getUserDetailByUserName() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.value = state.value.copy(isLoading = true)
            _state.value = state.value.copy(userDetail = getMockUsers())
            _state.value = state.value.copy(isLoading = false)
//            userInfoUseCase.fetchUserList(1, 10)
//                .collect { result ->
//                    when(result){
//                        is ResultApi.Success -> {
//                            _state.value = state.value.copy(userList = result.data)
//                            _state.value = state.value.copy(isLoading = false)
//                        }
//                        is ResultApi.Error -> {
//                            _state.value = state.value.copy(isLoading = false)
//                        }
//                        is ResultApi.Loading -> {
//                            _state.value = state.value.copy(isLoading = true)
//                        }
//                    }
//                }
        }
    }

    private fun getMockUsers(): UserInfo {
        return UserInfo(
            userName = "johndoe",
            id = 1,
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            htmlUrl = "https://github.com/johndoe",
        )
    }
}