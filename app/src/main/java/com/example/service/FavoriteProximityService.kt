package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import kotlinx.coroutines.*

/**
 * Background Service that monitors proximity of user's chosen favorite creators and marketplace sellers.
 * Triggers push notifications whenever a favorite enters the chosen radius (e.g. 5KM).
 */
class FavoriteProximityService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var isMonitoring = false

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 90210
        private const val SERVICE_CHANNEL_ID = "favorite_proximity_service_channel"

        fun start(context: Context) {
            val hasLocation = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasLocation) {
                Log.d("FavoriteProximityService", "Location permission not yet granted; skipping background service launch.")
                return
            }

            val intent = Intent(context, FavoriteProximityService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // Fallback for background start restrictions
                try {
                    context.startService(intent)
                } catch (ignored: Exception) {}
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FavoriteProximityService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createServiceNotificationChannel()
        startForegroundSafely()
        startProximityMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startProximityMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceJob.cancel()
    }

    private fun startProximityMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            // Default user anchor coordinates (San Francisco hub)
            val userLat = 37.7749
            val userLon = -122.4194

            while (isActive && isMonitoring) {
                try {
                    FavoriteProximityManager.checkProximityAndNotify(
                        applicationContext,
                        userLat = userLat,
                        userLon = userLon
                    )
                } catch (e: Exception) {
                    // Non-fatal exception in background poll
                }
                // Check every 30 seconds
                delay(30_000L)
            }
        }
    }

    private fun startForegroundSafely() {
        try {
            val notification = createServiceNotification()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    FOREGROUND_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            } else {
                startForeground(FOREGROUND_NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.w("FavoriteProximityService", "startForeground safely caught: ${e.message}")
        }
    }

    private fun createServiceNotification(): Notification {
        val radius = FavoriteProximityManager.proximityRadiusKm.value
        val title = "Localiiiy Proximity Guard Active"
        val message = "Monitoring ${String.format("%.0f", radius)}km radius for your favorite creators & sellers"

        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setColor(0xFF00C896.toInt())
            .build()
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Localiiiy Proximity Background Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Background service monitoring for nearby favorite creators and sellers."
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }
}
