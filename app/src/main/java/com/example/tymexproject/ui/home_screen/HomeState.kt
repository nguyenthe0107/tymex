package com.example.tymexproject.ui.home_screen

import com.example.domain.model.UserInfoResponse

/**
 * Represents the UI state for the Home Screen
 *
 * @property userList List of users to be displayed. Empty by default
 * @property isLoading Indicates if the initial data is being loaded
 * @property isLoadingMore Indicates if additional data is being loaded during pagination
 * @property currentPage Current page number for pagination, starts from 1
 * @property canLoadMore Indicates if more data can be loaded (used for infinite scroll)
 *
 */

data class HomeState(
    val userList: List<UserInfoResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val canLoadMore: Boolean = true
)