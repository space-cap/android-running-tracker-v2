package com.ezlevup.runningtrackerv2.presentation.home

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezlevup.runningtrackerv2.service.RunningService
import com.ezlevup.runningtrackerv2.util.FormatUtils
import com.ezlevup.runningtrackerv2.util.TrackingManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
        onNavigateToRunList: () -> Unit = {},
        viewModel: HomeViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                        factory =
                                HomeViewModelFactory(
                                        (LocalContext.current.applicationContext as
                                                        com.ezlevup.runningtrackerv2.BaseApplication)
                                                .database.getRunDao()
                                )
                )
) {
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
                                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ==
                                                true
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

        LaunchedEffect(hasLocationPermission) {
                if (hasLocationPermission) {
                        val fusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(context)
                        try {
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                                cameraPositionState.position =
                                                        CameraPosition.fromLatLngZoom(
                                                                LatLng(
                                                                        location.latitude,
                                                                        location.longitude
                                                                ),
                                                                15f
                                                        )
                                        }
                                }
                        } catch (e: SecurityException) {
                                // Permission might be revoked
                        }
                }
        }

        Box(modifier = Modifier.fillMaxSize()) {
                // Full screen Google Map
                GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(zoomControlsEnabled = false),
                        onMapLoaded = {
                                // Future use for snapshot if needed immediately
                        }
                ) {
                        if (TrackingManager.pathPoints.isNotEmpty()) {
                                Polyline(
                                        points = TrackingManager.pathPoints.toList(),
                                        color = Color.Red,
                                        width = 10f
                                )
                        }
                }

                // Top-right History Button
                Button(
                        onClick = onNavigateToRunList,
                        modifier =
                                Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 16.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Color.Black.copy(alpha = 0.6f)
                                ),
                        shape = RoundedCornerShape(12.dp)
                ) { Text(text = "기록", color = Color.White) }

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
                                text =
                                        FormatUtils.getFormattedStopWatchTime(
                                                TrackingManager.durationInMillis
                                        ),
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                                val km = TrackingManager.distanceInMeters / 1000f

                                StatItem(value = String.format("%.2f", km), unit = "km")
                                StatItem(value = TrackingManager.currentPace, unit = "min/km")
                        }
                }

                // Bottom Controls
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                        Button(
                                onClick = {
                                        if (TrackingManager.isTracking) {
                                                // Action Stop: Capture snapshot and save
                                                // Note: In a real app we might want to show a
                                                // dialog first
                                                // cameraPositionState.position has the current
                                                // view, we can use it for snapshot
                                                // However, GoogleMap component doesn't expose
                                                // snapshot() directly in a simple way
                                                // standard way is to use a MapView or wait for a
                                                // stable way in Compose.
                                                // For now we will just save without bitmap or use a
                                                // dummy if snapshot is complex.
                                                // Actually, maps-compose v4+ supports it better but
                                                // let's stick to saving data first.

                                                Intent(context, RunningService::class.java).also {
                                                        intent ->
                                                        intent.action = RunningService.ACTION_STOP
                                                        context.startService(intent)
                                                }
                                                viewModel.saveRun(
                                                        null
                                                ) // Save without bitmap for now
                                        } else {
                                                // Action Start
                                                Intent(context, RunningService::class.java).also {
                                                        intent ->
                                                        intent.action = RunningService.ACTION_START
                                                        context.startService(intent)
                                                }
                                        }
                                },
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor =
                                                        if (TrackingManager.isTracking) Color.Red
                                                        else Color.Green
                                        ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(80.dp).fillMaxWidth(0.8f) // 80% width
                        ) {
                                Icon(
                                        imageVector =
                                                if (TrackingManager.isTracking) Icons.Default.Stop
                                                else Icons.Default.PlayArrow,
                                        contentDescription =
                                                if (TrackingManager.isTracking) "Stop" else "Start",
                                        tint = Color.White
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text(
                                        text =
                                                if (TrackingManager.isTracking) "STOP RUN"
                                                else "START RUN",
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
                Text(
                        text = value,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                )
                Text(text = unit, color = Color.Gray, fontSize = 14.sp)
        }
}
