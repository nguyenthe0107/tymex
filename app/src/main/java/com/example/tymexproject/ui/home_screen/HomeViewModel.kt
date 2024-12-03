package com.example.tymexproject.ui.home_screen

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

    companion object {
        const val PER_PAGE = 20
    }

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _eventFlow = MutableSharedFlow<UiHomeEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getUserList(isLoadMore: Boolean = false) {
        if (!isLoadMore && state.value.isLoading) return
        if (isLoadMore && (state.value.isLoadingMore || !state.value.canLoadMore)){
            _state.value = state.value.copy(
                isLoading = false,
                isLoadingMore = false,
            )
            return
        }
        viewModelScope.launch(dispatcherProvider.io) {
            if (isLoadMore) {
                _state.value = state.value.copy(isLoadingMore = true)
            } else {
                _state.value = state.value.copy(isLoading = true)
            }
            val since = if (isLoadMore) {
                state.value.userList.lastOrNull()?.id ?: 1
            } else {
                1
            }
            userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = since)
                .collect { result ->
                    when(result) {
                        is ResultApi.Success -> {
                            val newList = if (isLoadMore) {
                                state.value.userList.plus(result.data)
                            } else {
                                result.data
                            }
                            _state.value = state.value.copy(
                                userList = newList,
                                isLoading = false,
                                isLoadingMore = false,
                                currentPage = state.value.currentPage + 1,
                                canLoadMore = result.data.isNotEmpty()
                            )
                        }
                        is ResultApi.Error -> {
                            _state.value = state.value.copy(
                                isLoading = false,
                                isLoadingMore = false
                            )
                            _eventFlow.emit(UiHomeEvent.ShowError(
                                result.message
                            ))
                        }
                        is ResultApi.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true,
                            )
                        }
                    }
                }
        }
    }
}