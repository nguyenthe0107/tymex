package com.example.tymexproject.ui.user_detail_screen

import com.example.domain.model.UserInfo

data class UserDetailState(
   val userDetail: UserInfo?=null,
   val isLoading: Boolean = false,
)