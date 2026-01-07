package com.ezlevup.runningtrackerv2.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    private var timerJob: Job? = null
    private var startTime = 0L
    private var accumulatedTime = 0L

    fun startResumeTimer() {
        if (isTracking) return

        isTracking = true
        startTime = System.currentTimeMillis()

        timerJob =
                CoroutineScope(Dispatchers.Main).launch {
                    while (isTracking && isActive) {
                        durationInMillis =
                                accumulatedTime + (System.currentTimeMillis() - startTime)
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

    fun stopTimer() {
        isTracking = false
        timerJob?.cancel()
        durationInMillis = 0L
        accumulatedTime = 0L
        startTime = 0L
    }
}
