package com.example.tymexproject.ui.user_detail_screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.UserInfoResponse
import com.example.tymexproject.common_components.LoadingComponent
import com.example.tymexproject.ui.components.UserInfoCard
import com.example.tymexproject.ui.user_detail_screen.components.StatItem

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserDetailScreen(
    userName:String?,
    navController: NavController,
    userDetailViewModel: UserDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userDetailViewModel.getUserDetailByUserName(userName)
    }
    LaunchedEffect(key1 = true) {
        userDetailViewModel.eventFlow.collect { event ->
            when (event) {
                is UiUserDetailEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val userDetailInfo: UserInfoResponse? = userDetailViewModel.state.value.userDetail
    val shouldShowLoading = userDetailViewModel.state.value.isLoading
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Reuse UserCard component
                userDetailInfo?.let { UserInfoCard(user = it) {} }
                Spacer(modifier = Modifier.height(24.dp))
                // Stats section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        count = "100+",
                        label = "Follower",
                        icon = Icons.Default.Person
                    )
                    StatItem(
                        count = "10+",
                        label = "Following",
                        icon = Icons.Default.Star
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Blog section
                Text(
                    text = "Blog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userDetailInfo?.htmlUrl ?: "https://blog.abc",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
            AnimatedVisibility(visible = shouldShowLoading) {
                LoadingComponent()
            }
        }
    }
}

