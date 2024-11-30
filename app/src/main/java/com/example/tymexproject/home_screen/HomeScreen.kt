package com.example.tymexproject.home_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tymexproject.common_components.LoadingComponent

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        homeViewModel.eventFlow.collect { event ->
            when (event) {
                is UiHomeEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val userList = homeViewModel.state.value.userList
    val shouldShowLoading = homeViewModel.state.value.isLoading
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = shouldShowLoading) {
            LoadingComponent()
        }
    }

}