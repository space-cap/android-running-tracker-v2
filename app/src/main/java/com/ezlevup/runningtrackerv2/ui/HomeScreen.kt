package com.ezlevup.runningtrackerv2.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                hasLocationPermission =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            }

    LaunchedEffect(Unit) {
        val permissions =
                mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!hasLocationPermission) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    val seoul = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoul, 15f)
    }

    var isTracking by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen Google Map
        GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )

        // Overlay for stats
        Column(
                modifier =
                        Modifier.align(Alignment.TopCenter)
                                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                                .background(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                    text = "00:00:00",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "0.00", unit = "km")
                StatItem(value = "0:00", unit = "min/km")
            }
        }

        // Bottom Controls
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
            Button(
                    onClick = {
                        isTracking = !isTracking
                        Intent(context, RunningService::class.java).also { intent ->
                            intent.action =
                                    if (isTracking) RunningService.ACTION_START
                                    else RunningService.ACTION_STOP
                            context.startService(intent)
                        }
                    },
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = if (isTracking) Color.Red else Color.Green
                            ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(80.dp).fillMaxWidth(0.8f) // 80% width
            ) {
                Icon(
                        imageVector =
                                if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isTracking) "Stop" else "Start",
                        tint = Color.White
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                        text = if (isTracking) "STOP RUN" else "START RUN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(text = unit, color = Color.Gray, fontSize = 14.sp)
    }
}
