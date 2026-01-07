package com.ezlevup.runningtrackerv2.util

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object TrackingManager {

    var durationInMillis by mutableLongStateOf(0L)
        private set

    var isTracking by mutableStateOf(false)
        private set

    var distanceInMeters by mutableIntStateOf(0)
        private set

    val pathPoints = mutableStateListOf<LatLng>()

    var currentPace by mutableStateOf("0:00")
        private set

    private var timerJob: Job? = null
    private var startTime = 0L
    private var accumulatedTime = 0L
    private var lastPaceCalculationTime = 0L

    fun addPathPoint(location: Location) {
        val newLatLng = LatLng(location.latitude, location.longitude)

        if (pathPoints.isNotEmpty()) {
            val lastLocation =
                    Location("last").apply {
                        latitude = pathPoints.last().latitude
                        longitude = pathPoints.last().longitude
                    }
            val distance = lastLocation.distanceTo(location)
            distanceInMeters += distance.toInt()
        }
        pathPoints.add(newLatLng)
    }

    fun startResumeTimer() {
        if (isTracking) return

        isTracking = true
        startTime = System.currentTimeMillis()

        timerJob =
                CoroutineScope(Dispatchers.Main).launch {
                    while (isTracking && isActive) {
                        val now = System.currentTimeMillis()
                        durationInMillis = accumulatedTime + (now - startTime)

                        // Calculate pace every 5 seconds
                        if (now - lastPaceCalculationTime >= 5000L) {
                            calculatePace()
                            lastPaceCalculationTime = now
                        }

                        delay(50L) // Update every 50ms
                    }
                }
    }

    fun pauseTimer() {
        if (!isTracking) return

        isTracking = false
        timerJob?.cancel()
        accumulatedTime += System.currentTimeMillis() - startTime
    }

    private fun calculatePace() {
        val km = distanceInMeters / 1000f
        if (km > 0) {
            val minutes = (durationInMillis / 1000f / 60f)
            val paceVal = minutes / km
            val pMin = paceVal.toInt()
            val pSec = ((paceVal - pMin) * 60).toInt()
            currentPace = String.format("%d:%02d", pMin, pSec)
        } else {
            currentPace = "0:00"
        }
    }

    fun stopTimer() {
        isTracking = false
        timerJob?.cancel()
        durationInMillis = 0L
        distanceInMeters = 0
        pathPoints.clear()
        currentPace = "0:00"
        accumulatedTime = 0L
        startTime = 0L
        lastPaceCalculationTime = 0L
    }
}
