package com.example.tymexproject.ui.home_screen

sealed class HomeEvent {
    data object LoadSurveys: HomeEvent()
    data class ShowError(val message: String): HomeEvent()
}