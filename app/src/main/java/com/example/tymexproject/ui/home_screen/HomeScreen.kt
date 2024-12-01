package com.example.tymexproject.ui.home_screen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.UserInfo
import com.example.tymexproject.common_components.LoadingComponent
import com.example.tymexproject.routes.Screens
import com.example.tymexproject.ui.components.UserInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        homeViewModel.getUserList()
    }
    LaunchedEffect(key1 = true) {
        homeViewModel.eventFlow.collect { event ->
            when (event) {
                is UiHomeEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val userList: List<UserInfo> = homeViewModel.state.value.userList
    val shouldShowLoading = homeViewModel.state.value.isLoading
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Github Users") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Log.e("WTF", " HomeScreen userList ${userList.size}", )
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userList, key = { it.id }) { userInfo ->
                    UserInfoCard(user = userInfo, onClick = {
                        navController.navigate(Screens.UserDetailScreen.route)
                    })
                }
            }
            AnimatedVisibility(visible = shouldShowLoading) {
                LoadingComponent()
            }
        }
    }
}