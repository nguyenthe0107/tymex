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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.component.LoadingComponent
import com.example.designsystem.component.ScaffoldTopAppbar
import com.example.domain.model.UserInfoResponse
import com.example.tymexproject.R
import com.example.tymexproject.ui.components.UserInfoCard
import com.example.tymexproject.ui.user_detail_screen.components.StatItem

/**
 * UserDetailScreen displays detailed information about a specific GitHub user
 *
 * @param userName GitHub username to fetch details for
 * @param navController Navigation controller for handling screen navigation
 * @param userDetailViewModel ViewModel that manages the user detail data and state
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserDetailScreen(
    userName: String?,
    navController: NavController,
    userDetailViewModel: UserDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Fetch user details when screen is launched
    LaunchedEffect(Unit) {
        userDetailViewModel.getUserDetailByUserName(userName)
    }
    // Handle UI events (like error messages)
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
    ScaffoldTopAppbar(
        title = stringResource(R.string.tvTitleDetails),
        onNavigationIconClick = {
            navController.popBackStack()
        }
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
                        count = "${userDetailInfo?.followers}+",
                        label = stringResource(id = R.string.tvFollower),
                        icon = Icons.Default.Person
                    )
                    StatItem(
                        count = "${userDetailInfo?.following}+",
                        label = stringResource(id = R.string.tvFollowing),
                        icon = Icons.Default.Star
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.tvBlog),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userDetailInfo?.htmlUrl ?: "",
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

