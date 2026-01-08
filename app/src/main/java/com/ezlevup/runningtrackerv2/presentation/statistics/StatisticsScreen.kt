package com.ezlevup.runningtrackerv2.presentation.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezlevup.runningtrackerv2.BaseApplication
import com.ezlevup.runningtrackerv2.presentation.rundetail.StatCard
import com.ezlevup.runningtrackerv2.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
        onBackClick: () -> Unit,
        viewModel: StatisticsViewModel =
                viewModel(
                        factory =
                                StatisticsViewModelFactory(
                                        (LocalContext.current.applicationContext as BaseApplication)
                                                .database.getRunDao()
                                )
                )
) {
        val state by viewModel.statisticsState.collectAsState()

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("러닝 통계") },
                                navigationIcon = {
                                        IconButton(onClick = onBackClick) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Back"
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor = Color.White,
                                                navigationIconContentColor = Color.White
                                        )
                        )
                }
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        StatisticsSection(title = "전체 기록", summary = state.total)
                        Spacer(modifier = Modifier.height(24.dp))
                        StatisticsSection(title = "이번 주 기록", summary = state.weekly)
                        Spacer(modifier = Modifier.height(24.dp))
                        StatisticsSection(title = "이번 달 기록", summary = state.monthly)
                }
        }
}

@Composable
fun StatisticsSection(title: String, summary: StatisticsSummary) {
        Column {
                Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(
                                label = "거리",
                                value = String.format("%.2f", summary.totalDistance / 1000f),
                                unit = "km",
                                modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        StatCard(
                                label = "시간",
                                value = FormatUtils.getFormattedStopWatchTime(summary.totalTime),
                                unit = "",
                                modifier = Modifier.weight(1f)
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(
                                label = "평균 속도",
                                value = String.format("%.1f", summary.avgSpeed),
                                unit = "km/h",
                                modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        StatCard(
                                label = "칼로리",
                                value = summary.totalCalories.toString(),
                                unit = "kcal",
                                modifier = Modifier.weight(1f)
                        )
                }
        }
}
