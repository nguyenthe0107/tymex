package com.example.tymexproject.ui.home_screen

sealed class HomeEvent {
    data object LoadUserList: HomeEvent()
    data class ShowError(val message: String): HomeEvent()
}