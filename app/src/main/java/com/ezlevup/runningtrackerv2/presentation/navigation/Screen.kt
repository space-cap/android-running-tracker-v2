package com.ezlevup.runningtrackerv2.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RunList : Screen("run_list")
}
