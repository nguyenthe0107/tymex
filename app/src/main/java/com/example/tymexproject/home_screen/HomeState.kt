package com.example.tymexproject.home_screen

import com.example.domain.model.UserInfo

data class HomeState(
   val userList: List<UserInfo> = emptyList(),
   val isLoading: Boolean = false,
)