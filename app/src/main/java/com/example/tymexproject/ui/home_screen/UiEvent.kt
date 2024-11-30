package com.example.tymexproject.ui.home_screen

sealed class UiHomeEvent {
    data class ShowError(val message: String): UiHomeEvent()
}