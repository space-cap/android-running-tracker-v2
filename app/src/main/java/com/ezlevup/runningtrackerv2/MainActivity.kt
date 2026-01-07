package com.ezlevup.runningtrackerv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ezlevup.runningtrackerv2.presentation.home.HomeScreen
import com.ezlevup.runningtrackerv2.presentation.runlist.RunListScreen
import com.ezlevup.runningtrackerv2.ui.theme.RunningTrackerV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunningTrackerV2Theme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                HomeScreen(
                                        onNavigateToRunList = { navController.navigate("run_list") }
                                )
                            }
                            composable("run_list") { RunListScreen() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingPreview() {
    RunningTrackerV2Theme { HomeScreen() }
}
