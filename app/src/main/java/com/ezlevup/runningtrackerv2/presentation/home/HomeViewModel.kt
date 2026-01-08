package com.ezlevup.runningtrackerv2.presentation.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezlevup.runningtrackerv2.data.RunDao
import com.ezlevup.runningtrackerv2.util.TrackingManager
import kotlinx.coroutines.launch

class HomeViewModel(private val runDao: RunDao) : ViewModel() {

        val isTracking = TrackingManager.isTracking
        val durationInMillis = TrackingManager.durationInMillis
        val distanceInMeters = TrackingManager.distanceInMeters
        val pathPoints = TrackingManager.pathPoints
        val currentPace = TrackingManager.currentPace

        fun saveRun(
                context: android.content.Context,
                bitmap: Bitmap?,
                onSaveComplete: (Int) -> Unit
        ) {
                val distance = TrackingManager.distanceInMeters
                val time = TrackingManager.durationInMillis
                val timestamp = System.currentTimeMillis()

                // Don't save if there's no data
                if (time <= 0L && distance <= 0) {
                        TrackingManager.stopTimer()
                        android.content.Intent(
                                        context,
                                        com.ezlevup.runningtrackerv2.service.RunningService::class
                                                .java
                                )
                                .also { intent -> context.stopService(intent) }
                        return
                }

                val run = TrackingManager.createRunRecord(bitmap)

                viewModelScope.launch {
                        val runId = runDao.insertRun(run)
                        onSaveComplete(runId.toInt())
                }

                // Stop the timer and the service after capturing data
                TrackingManager.stopTimer()
                android.content.Intent(
                                context,
                                com.ezlevup.runningtrackerv2.service.RunningService::class.java
                        )
                        .also { intent -> context.stopService(intent) }
        }
}
