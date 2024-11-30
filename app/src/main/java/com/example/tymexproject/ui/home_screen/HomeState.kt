package com.example.tymexproject.ui.home_screen

import com.example.domain.model.UserInfo

data class HomeState(
   val userList: List<UserInfo> = emptyList(),
   val isLoading: Boolean = false,
)