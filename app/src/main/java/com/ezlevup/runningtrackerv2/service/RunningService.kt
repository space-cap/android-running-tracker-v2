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
import com.ezlevup.runningtrackerv2.BaseApplication
import com.ezlevup.runningtrackerv2.R
import com.ezlevup.runningtrackerv2.util.TrackingManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RunningService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_PAUSE -> pause()
            ACTION_RESUME -> resume()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val batteryReceiver =
            object : android.content.BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1)
                    val batteryPct = level * 100 / scale.toFloat()

                    if (batteryPct < 20 && TrackingManager.isTracking) {
                        forceSaveRun()
                    }
                }
            }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        if (TrackingManager.isTracking) {
                            result.locations.forEach { location ->
                                TrackingManager.addPathPoint(location)
                            }
                        }
                    }
                }
        val filter = android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    private fun start() {
        createNotificationChannel()
        val notification = createNotification("Tracking your run...")
        startForeground(1, notification)
        startLocationUpdates()
        TrackingManager.startResumeTimer()
    }

    private fun pause() {
        TrackingManager.pauseTimer()
        locationClient.removeLocationUpdates(locationCallback)
        updateNotification("Run Paused")
    }

    private fun resume() {
        TrackingManager.startResumeTimer()
        startLocationUpdates()
        updateNotification("Tracking your run...")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                    NotificationChannel(
                            CHANNEL_ID,
                            "Running Tracker",
                            NotificationManager.IMPORTANCE_LOW
                    )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): android.app.Notification {
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

        val builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Running Tracker")
                        .setContentText(text)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)

        // Add Pause/Resume action
        if (TrackingManager.isTracking) {
            val pauseIntent =
                    Intent(this, RunningService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent =
                    android.app.PendingIntent.getService(
                            this,
                            1,
                            pauseIntent,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    android.app.PendingIntent.FLAG_IMMUTABLE
                            else 0
                    )
            builder.addAction(R.drawable.ic_launcher_foreground, "Pause", pausePendingIntent)
        } else if (TrackingManager.durationInMillis > 0L) {
            val resumeIntent =
                    Intent(this, RunningService::class.java).apply { action = ACTION_RESUME }
            val resumePendingIntent =
                    android.app.PendingIntent.getService(
                            this,
                            2,
                            resumeIntent,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    android.app.PendingIntent.FLAG_IMMUTABLE
                            else 0
                    )
            builder.addAction(R.drawable.ic_launcher_foreground, "Resume", resumePendingIntent)
        }

        return builder.build()
    }

    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
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

    private fun forceSaveRun() {
        if (!TrackingManager.isTracking) return

        val runDao = (application as BaseApplication).database.getRunDao()
        val runRecord = TrackingManager.createRunRecord(null)

        CoroutineScope(Dispatchers.IO).launch {
            runDao.insertRun(runRecord)
            withContext(Dispatchers.Main) {
                stop()
                val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification =
                        NotificationCompat.Builder(this@RunningService, CHANNEL_ID)
                                .setContentTitle("Running Tracker")
                                .setContentText("Run saved due to low battery")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setAutoCancel(true)
                                .build()
                notificationManager.notify(2, notification)
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "running_channel"
    }
}
