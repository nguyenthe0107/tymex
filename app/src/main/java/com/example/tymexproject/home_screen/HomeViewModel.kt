package com.example.tymexproject.home_screen

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

    private fun getUserList() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.value = state.value.copy(isLoading = true)
            userInfoUseCase.fetchUserList(1, 10)
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
}