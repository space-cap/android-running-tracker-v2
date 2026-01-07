package com.ezlevup.runningtrackerv2.presentation.rundetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezlevup.runningtrackerv2.BaseApplication
import com.ezlevup.runningtrackerv2.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunDetailScreen(
        runId: Int,
        onBackClick: () -> Unit,
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

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("러닝 상세 정보") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        run?.let { record ->
            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .padding(padding)
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Map Snapshot
                Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                modifier = Modifier.fillMaxSize().background(Color.Gray),
                                contentAlignment = Alignment.Center
                        ) { Text("No Map Data", color = Color.White) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date and Time
                val dateFormat = SimpleDateFormat("yyyy년 M월 d일 (E) HH:mm", Locale.KOREAN)
                Text(
                        text = dateFormat.format(Date(record.timestamp)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Statistics Grid (Simplified with Rows)
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                            label = "거리",
                            value = String.format("%.2f", record.distanceInMeters / 1000f),
                            unit = "km",
                            modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                            label = "시간",
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
        }
                ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
    }
}

@Composable
fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier,
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = unit, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
            }
        }
    }
}
