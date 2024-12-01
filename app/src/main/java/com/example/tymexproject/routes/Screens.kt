package com.example.tymexproject.routes

sealed class Screens(val route: String) {
    data object HomeScreen: Screens("home_screen")
    data object UserDetailScreen: Screens("user_detail_screen")
}
