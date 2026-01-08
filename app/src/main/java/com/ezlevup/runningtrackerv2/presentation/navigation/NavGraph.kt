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
import com.ezlevup.runningtrackerv2.presentation.statistics.StatisticsScreen
import com.ezlevup.runningtrackerv2.presentation.success.SuccessResultScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                    onNavigateToRunList = { navController.navigate(Screen.RunList.route) },
                    onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                    onNavigateToSuccessResult = { runId ->
                        navController.navigate(Screen.SuccessResult.createRoute(runId))
                    }
            )
        }
        composable(Screen.RunList.route) {
            RunListScreen(
                    onRunClick = { runId ->
                        navController.navigate(Screen.RunDetail.createRoute(runId))
                    }
            )
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(
                route = Screen.SuccessResult.route,
                arguments = listOf(navArgument("runId") { type = NavType.IntType })
        ) { backStackEntry ->
            val runId = backStackEntry.arguments?.getInt("runId") ?: 0
            SuccessResultScreen(
                    runId = runId,
                    onFinishClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
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
