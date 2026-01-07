package com.ezlevup.runningtrackerv2.presentation.runlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.ezlevup.runningtrackerv2.BaseApplication
import com.ezlevup.runningtrackerv2.data.RunRecord
import com.ezlevup.runningtrackerv2.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RunListScreen(
        onRunClick: (Int) -> Unit = {},
        viewModel: RunListViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                        factory =
                                RunListViewModelFactory(
                                        (LocalContext.current.applicationContext as BaseApplication)
                                                .database.getRunDao()
                                )
                )
) {
    val runs by viewModel.runs.collectAsState()

    Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(16.dp)
                ) {
                    Text(
                            text = "나의 러닝 기록",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(runs) { run ->
                RunItem(run = run, onClick = { run.id?.let { onRunClick(it) } })
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun RunItem(run: RunRecord, onClick: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // run.img를 로컬 변수에 할당하여 스마트 캐스트 오류 해결
            val imageBitmap = run.img
            // Display snapshot image or placeholder
            if (imageBitmap != null) {
                Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = "Run Snapshot",
                        modifier =
                                Modifier.size(80.dp)
                                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                )
            } else {
                Box(
                        modifier =
                                Modifier.size(80.dp)
                                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                ) { Text(text = "Map", color = Color.White, fontSize = 12.sp) }
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                val dateStr = dateFormat.format(Date(run.timestamp))

                Text(text = dateStr, fontSize = 14.sp, color = Color.Gray)
                Text(
                        text = String.format("%.2f km", run.distanceInMeters / 1000f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = FormatUtils.getFormattedStopWatchTime(run.timeInMillis),
                        fontSize = 16.sp
                )
            }
        }
    }
}
