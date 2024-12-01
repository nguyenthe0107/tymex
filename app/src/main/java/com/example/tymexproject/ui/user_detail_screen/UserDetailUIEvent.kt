package com.example.tymexproject.ui.user_detail_screen

sealed class UiUserDetailEvent {
    data class ShowError(val message: String): UiUserDetailEvent()
}