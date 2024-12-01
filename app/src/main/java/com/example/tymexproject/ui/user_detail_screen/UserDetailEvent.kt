package com.example.tymexproject.ui.user_detail_screen

sealed class UserDetailEvent {
    data object LoadUserDetail: UserDetailEvent()
    data class ShowError(val message: String): UserDetailEvent()
}