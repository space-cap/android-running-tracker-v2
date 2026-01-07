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
import com.ezlevup.runningtrackerv2.presentation.home.HomeScreen
import com.ezlevup.runningtrackerv2.ui.theme.RunningTrackerV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunningTrackerV2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) { // Using Box to handle padding
                        HomeScreen()
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
