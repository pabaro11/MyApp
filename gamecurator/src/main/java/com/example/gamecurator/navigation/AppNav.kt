package com.example.gamecurator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.gamecurator.ui.screen.*

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "home") {

        composable("home") {
            HomeScreen(nav)
        }

        composable("recommend") {
            RecommendScreen(nav)
        }

        composable("godot") {
            GodotScreen(nav)
        }
    }
}
