package com.example.tymexproject.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.designsystem.theme.TymexProjectTheme
import com.example.tymexproject.ui.home_screen.HomeScreen
import com.example.tymexproject.routes.Screens
import com.example.tymexproject.ui.user_detail_screen.UserDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TymexProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {
                        composable(route = Screens.HomeScreen.route) {
                            HomeScreen(navController)
                        }
                        composable(route = Screens.UserDetailScreen.route) {
                            val userName = it.arguments?.getString("user_name")
                            Log.e("WTF", "userName $userName")
                            UserDetailScreen(userName,navController)
                        }
                    }
                }
            }
        }
    }
}
