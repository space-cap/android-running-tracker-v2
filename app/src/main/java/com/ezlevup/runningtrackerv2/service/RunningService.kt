package com.ezlevup.runningtrackerv2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.ezlevup.runningtrackerv2.R
import com.ezlevup.runningtrackerv2.util.TrackingManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class RunningService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.forEach { location ->
                            TrackingManager.addPathPoint(location)
                            val lat = location.latitude
                            val lng = location.longitude
                            android.util.Log.d("RunningService", "Location: $lat, $lng")
                        }
                    }
                }
    }

    private fun start() {
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                            CHANNEL_ID,
                            "Running Tracker",
                            NotificationManager.IMPORTANCE_LOW
                    )
            notificationManager.createNotificationChannel(channel)
        }

        val intent =
                Intent(this, com.ezlevup.runningtrackerv2.MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
        val pendingIntent =
                android.app.PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            android.app.PendingIntent.FLAG_IMMUTABLE or
                                    android.app.PendingIntent.FLAG_UPDATE_CURRENT
                        } else {
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT
                        }
                )

        val notification =
                NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Running Tracker")
                        .setContentText("Tracking your run...")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build()

        startForeground(1, notification)
        startLocationUpdates()
        TrackingManager.startResumeTimer()
    }

    private fun startLocationUpdates() {
        val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                        .apply { setMinUpdateIntervalMillis(2000L) }
                        .build()

        try {
            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            android.util.Log.e("RunningService", "Location permission lost", e)
        }
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        locationClient.removeLocationUpdates(locationCallback)
        TrackingManager.stopTimer()
        stopSelf()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "running_channel"
    }
}
