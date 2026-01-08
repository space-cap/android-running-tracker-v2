package com.ezlevup.runningtrackerv2.presentation.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezlevup.runningtrackerv2.BaseApplication
import com.ezlevup.runningtrackerv2.presentation.rundetail.RunDetailViewModel
import com.ezlevup.runningtrackerv2.presentation.rundetail.RunDetailViewModelFactory
import com.ezlevup.runningtrackerv2.presentation.rundetail.StatCard
import com.ezlevup.runningtrackerv2.util.FormatUtils

@Composable
fun SuccessResultScreen(
        runId: Int,
        onFinishClick: () -> Unit,
        viewModel: RunDetailViewModel =
                viewModel(
                        factory =
                                RunDetailViewModelFactory(
                                        (LocalContext.current.applicationContext as BaseApplication)
                                                .database.getRunDao(),
                                        runId
                                )
                )
) {
    val run by viewModel.run.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                    text = "수고하셨습니다!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
            )

            Text(
                    text = "오늘의 러닝 기록입니다.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Map Snapshot
            run?.let { record ->
                Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    val bitmap = record.img
                    if (bitmap != null) {
                        Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Run Path",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                                contentAlignment = Alignment.Center
                        ) { Text("지도 데이터 없음", color = Color.DarkGray) }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats in a more prominent way
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                            label = "총 거리",
                            value = String.format("%.2f", record.distanceInMeters / 1000f),
                            unit = "km",
                            modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                            label = "총 시간",
                            value = FormatUtils.getFormattedStopWatchTime(record.timeInMillis),
                            unit = "",
                            modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                            label = "평균 속도",
                            value = String.format("%.1f", record.avgSpeedInKMH),
                            unit = "km/h",
                            modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                            label = "칼로리",
                            value = record.caloriesBurned.toString(),
                            unit = "kcal",
                            modifier = Modifier.weight(1f)
                    )
                }
            }
                    ?: Box(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                    onClick = onFinishClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                            )
            ) { Text(text = "확인", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
