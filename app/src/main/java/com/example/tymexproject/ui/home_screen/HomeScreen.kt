package com.example.tymexproject.ui.home_screen

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.component.ScaffoldTopAppbar
import com.example.tymexproject.R
import com.example.tymexproject.routes.Screens
import com.example.tymexproject.ui.components.UserInfoCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = homeViewModel.state.value
    LaunchedEffect(key1 = true) {
        homeViewModel.eventFlow.collect { event ->
            when (event) {
                is UiHomeEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
                items(state.userList, key = { it.id }) { userInfo ->
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
}