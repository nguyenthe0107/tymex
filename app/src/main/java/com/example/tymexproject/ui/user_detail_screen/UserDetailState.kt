package com.example.tymexproject.ui.user_detail_screen

import com.example.domain.model.UserInfoResponse

data class UserDetailState(
   val userDetail: UserInfoResponse?=null,
   val isLoading: Boolean = false,
)