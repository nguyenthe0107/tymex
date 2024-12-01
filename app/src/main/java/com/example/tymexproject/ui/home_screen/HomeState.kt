package com.example.tymexproject.ui.home_screen

import com.example.domain.model.UserInfoResponse

data class HomeState(
    val userList: List<UserInfoResponse> = emptyList(),
    val isLoading: Boolean = false,
)