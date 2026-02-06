package com.proxicall_99.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proxicall_99.ui.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Landing.route) {
            LandingScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            Dashboard(onInfoClick = { navController.navigate(Screen.Info.route) })
        }
        composable(Screen.Info.route) {
            InfoScreen(onBack = { navController.popBackStack() })
        }
    }
}
