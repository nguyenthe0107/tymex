package com.example.tymexproject.home_screen

sealed class UiHomeEvent {
    data class ShowError(val message: String): UiHomeEvent()
}