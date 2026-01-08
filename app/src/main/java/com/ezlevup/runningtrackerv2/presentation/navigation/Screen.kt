package com.ezlevup.runningtrackerv2.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RunList : Screen("run_list")
    object Statistics : Screen("statistics")
    object RunDetail : Screen("run_detail/{runId}") {
        fun createRoute(runId: Int) = "run_detail/$runId"
    }
    object SuccessResult : Screen("success_result/{runId}") {
        fun createRoute(runId: Int) = "success_result/$runId"
    }
}
