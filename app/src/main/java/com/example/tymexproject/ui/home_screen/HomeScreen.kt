package com.example.tymexproject.ui.home_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.component.CustomErrorDialog
import com.example.designsystem.component.MySpacer
import com.example.designsystem.component.ScaffoldTopAppbar
import com.example.tymexproject.R
import com.example.tymexproject.routes.Screens
import com.example.tymexproject.ui.components.UserInfoCard

/**
 * HomeScreen is the main screen that displays a list of users with infinite scroll functionality
 *
 * @param navController Navigation controller for handling screen navigation
 * @param homeViewModel ViewModel that manages the UI state and business logic
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state = homeViewModel.state.value
    LaunchedEffect(Unit) {
        homeViewModel.getUserList(isLoadMore = false)
    }
    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    // Handle UI events
    LaunchedEffect(key1 = true) {
        homeViewModel.eventFlow.collect { event ->
            when (event) {
                is UiHomeEvent.ShowError -> {
                    showErrorDialog.value = true
                    errorMessage.value = event.message
                }
            }
        }
    }
    ScaffoldTopAppbar(
        title = stringResource(R.string.tvTitleHome),
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MySpacer(height = 8.dp)
                }
                items(state.userList, key = { userInfo ->
                    "${userInfo.id}_${userInfo.userName}_${state.userList.indexOf(userInfo)}_${System.nanoTime()}".hashCode()
                }) { userInfo ->
                    UserInfoCard(user = userInfo, onClick = {
                        navController.navigate(
                            Screens.UserDetailScreen.route.replace(
                                "{user_name}",
                                userInfo.userName
                            )
                        )
                    })
                }
                // Load more item
                item {
                    if (state.isLoadingMore && state.canLoadMore) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                // Trigger load more
                if (!state.isLoadingMore && state.canLoadMore) {
                    item {
                        LaunchedEffect(Unit) {
                            homeViewModel.getUserList(isLoadMore = true)
                        }
                    }
                }
            }
        }
    }

    // show dialog error
    if (showErrorDialog.value) {
        CustomErrorDialog(
            message = errorMessage.value,
            onDismiss = { showErrorDialog.value = false }
        )
    }
}