package com.proxicall_99.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Landing : Screen("landing")
    object Dashboard : Screen("dashboard")
    object Info : Screen("info")
    object Permissions : Screen("permissions")
}
