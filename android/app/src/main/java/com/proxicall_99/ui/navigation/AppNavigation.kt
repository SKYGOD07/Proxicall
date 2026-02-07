package com.proxicall_99.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proxicall_99.ui.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Loading : Screen("loading")
    object Landing : Screen("landing")
    object Dashboard : Screen("dashboard")
    object Info : Screen("info")
    object AddDevice : Screen("add_device")
    object Permissions : Screen("permissions")
    
    // New screens
    object Account : Screen("account")
    object AuthCheck : Screen("auth_check")
    object ActivityLogs : Screen("activity_logs")
    object Contacts : Screen("contacts")
    object CallHistory : Screen("call_history")
    object BrainVerify : Screen("brain_verify")
    object Assistant : Screen("assistant")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Loading.route) {
            LoadingScreen(onComplete = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            Dashboard(
                onInfoClick = { navController.navigate(Screen.Info.route) },
                onAddDeviceClick = { navController.navigate(Screen.AddDevice.route) },
                onAccountClick = { navController.navigate(Screen.Account.route) },
                onAuthCheckClick = { navController.navigate(Screen.AuthCheck.route) },
                onActivityLogsClick = { navController.navigate(Screen.ActivityLogs.route) },
                onCallHistoryClick = { navController.navigate(Screen.CallHistory.route) },
                onContactsClick = { navController.navigate(Screen.Contacts.route) },
                onBrainVerifyClick = { navController.navigate(Screen.BrainVerify.route) },
                onAssistantClick = { navController.navigate(Screen.Assistant.route) }
            )
        }
        composable(Screen.Info.route) {
            InfoScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AddDevice.route) {
            AddDeviceScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Account.route) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AuthCheck.route) {
            AuthCheckScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ActivityLogs.route) {
            ActivityLogsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Contacts.route) {
            ContactsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CallHistory.route) {
            CallHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.BrainVerify.route) {
            BrainVerifyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Assistant.route) {
            GeminiChatScreen(onBack = { navController.popBackStack() })
        }
    }
}
