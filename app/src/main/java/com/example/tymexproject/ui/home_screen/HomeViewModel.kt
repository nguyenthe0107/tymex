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

/**
 * ViewModel for the Home Screen that manages user list data and pagination
 *
 * @property userInfoUseCase Use case for fetching user information
 * @property dispatcherProvider Provides coroutine dispatchers for async operations
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    companion object {
        const val PER_PAGE = 20 // Number of items to load per page
    }

    // UI state holder
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _eventFlow = MutableSharedFlow<UiHomeEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    /**
     * Fetches user list with pagination support
     *
     * @param isLoadMore If true, loads next page; if false, refreshes from start
     */
    fun getUserList(isLoadMore: Boolean = false) {
        if (!isLoadMore && state.value.isLoading) return
        if (isLoadMore && (state.value.isLoadingMore || !state.value.canLoadMore)) {
            _state.value = state.value.copy(
                isLoading = false,
                isLoadingMore = false,
            )
            return
        }
        viewModelScope.launch(dispatcherProvider.io) {
            // Update loading state based on operation type
            if (isLoadMore) {
                _state.value = state.value.copy(isLoadingMore = true)
            } else {
                _state.value = state.value.copy(isLoading = true)
            }
            // Calculate starting point for pagination
            val since = if (isLoadMore) {
                (state.value.currentPage - 1) * PER_PAGE + 1
            } else {
                1
            }
            // Fetch users and handle different states
            userInfoUseCase.fetchUserList(perPage = PER_PAGE, since = since)
                .collect { result ->
                    when (result) {
                        is ResultApi.Success -> {
                            // Append new data for pagination, replace for refresh
                            val newList = if (isLoadMore) {
                                state.value.userList + result.data
                            } else {
                                result.data
                            }
                            _state.value = state.value.copy(
                                userList = newList,
                                isLoading = false,
                                isLoadingMore = false,
                                currentPage = state.value.currentPage + 1,
                                canLoadMore = result.data.size == PER_PAGE
                            )
                        }

                        is ResultApi.Error -> {
                            _state.value = state.value.copy(
                                isLoading = false,
                                isLoadingMore = false
                            )
                            _eventFlow.emit(
                                UiHomeEvent.ShowError(
                                    result.message
                                )
                            )
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