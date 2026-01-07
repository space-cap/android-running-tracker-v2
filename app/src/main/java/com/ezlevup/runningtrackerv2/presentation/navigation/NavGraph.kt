package com.ezlevup.runningtrackerv2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ezlevup.runningtrackerv2.presentation.home.HomeScreen
import com.ezlevup.runningtrackerv2.presentation.rundetail.RunDetailScreen
import com.ezlevup.runningtrackerv2.presentation.runlist.RunListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToRunList = { navController.navigate(Screen.RunList.route) })
        }
        composable(Screen.RunList.route) {
            RunListScreen(
                    onRunClick = { runId ->
                        navController.navigate(Screen.RunDetail.createRoute(runId))
                    }
            )
        }
        composable(
                route = Screen.RunDetail.route,
                arguments = listOf(navArgument("runId") { type = NavType.IntType })
        ) { backStackEntry ->
            val runId = backStackEntry.arguments?.getInt("runId") ?: 0
            RunDetailScreen(runId = runId, onBackClick = { navController.popBackStack() })
        }
    }
}
