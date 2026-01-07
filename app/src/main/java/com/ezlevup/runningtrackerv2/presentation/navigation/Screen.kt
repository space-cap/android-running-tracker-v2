package com.ezlevup.runningtrackerv2.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RunList : Screen("run_list")
    object RunDetail : Screen("run_detail/{runId}") {
        fun createRoute(runId: Int) = "run_detail/$runId"
    }
}
