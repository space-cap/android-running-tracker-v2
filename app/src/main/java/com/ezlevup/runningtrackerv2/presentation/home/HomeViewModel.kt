package com.ezlevup.runningtrackerv2.presentation.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezlevup.runningtrackerv2.data.RunDao
import com.ezlevup.runningtrackerv2.data.RunRecord
import com.ezlevup.runningtrackerv2.util.TrackingManager
import kotlinx.coroutines.launch

class HomeViewModel(private val runDao: RunDao) : ViewModel() {

    val isTracking = TrackingManager.isTracking
    val durationInMillis = TrackingManager.durationInMillis
    val distanceInMeters = TrackingManager.distanceInMeters
    val pathPoints = TrackingManager.pathPoints
    val currentPace = TrackingManager.currentPace

    fun saveRun(bitmap: Bitmap?) {
        val timestamp = System.currentTimeMillis()
        val distance = TrackingManager.distanceInMeters
        val time = TrackingManager.durationInMillis
        val avgSpeed = if (time > 0) (distance / 1000f) / (time / 1000f / 3600f) else 0f
        val calories = (distance / 1000f * 60).toInt() // Dummy calories formula

        val run =
                RunRecord(
                        img = bitmap,
                        timestamp = timestamp,
                        avgSpeedInKMH = avgSpeed,
                        distanceInMeters = distance,
                        timeInMillis = time,
                        caloriesBurned = calories
                )

        viewModelScope.launch {
            runDao.insertRun(run)
            TrackingManager.stopTimer()
        }
    }
}
