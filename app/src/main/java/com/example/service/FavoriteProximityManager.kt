package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

object FavoriteProximityManager {
    const val CHANNEL_ID = "favorite_proximity_channel"
    private const val PREFS_NAME = "favorite_proximity_prefs"
    private const val KEY_SERVICE_ENABLED = "key_service_enabled"
    private const val KEY_PROXIMITY_RADIUS_KM = "key_proximity_radius_km"
    private const val KEY_FAVORITE_IDS = "key_favorite_ids"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isServiceEnabled = MutableStateFlow(true)
    val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

    private val _proximityRadiusKm = MutableStateFlow(5.0)
    val proximityRadiusKm: StateFlow<Double> = _proximityRadiusKm.asStateFlow()

    private val _favoriteTargets = MutableStateFlow<List<FavoriteProximityTarget>>(FavoriteProximityDefaults.defaultTargets)
    val favoriteTargets: StateFlow<List<FavoriteProximityTarget>> = _favoriteTargets.asStateFlow()

    // Last alert timestamp cache to prevent spamming
    private val lastAlertTimestampMap = mutableMapOf<String, Long>()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        createNotificationChannel(context)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_SERVICE_ENABLED, true)
        val radius = prefs.getFloat(KEY_PROXIMITY_RADIUS_KM, 5.0f).toDouble()
        val savedFavIds = prefs.getStringSet(KEY_FAVORITE_IDS, null)

        _isServiceEnabled.value = enabled
        _proximityRadiusKm.value = radius

        if (savedFavIds != null) {
            _favoriteTargets.value = FavoriteProximityDefaults.defaultTargets.map { target ->
                target.copy(isFavorite = savedFavIds.contains(target.id))
            }
        }

        if (enabled) {
            FavoriteProximityService.start(context)
        }
    }

    fun setServiceEnabled(context: Context, enabled: Boolean) {
        _isServiceEnabled.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()

        if (enabled) {
            FavoriteProximityService.start(context)
        } else {
            FavoriteProximityService.stop(context)
        }
    }

    fun setProximityRadiusKm(context: Context, radiusKm: Double) {
        _proximityRadiusKm.value = radiusKm
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_PROXIMITY_RADIUS_KM, radiusKm.toFloat()).apply()
    }

    fun toggleFavorite(context: Context, targetId: String) {
        val updated = _favoriteTargets.value.map { target ->
            if (target.id == targetId) target.copy(isFavorite = !target.isFavorite) else target
        }
        _favoriteTargets.value = updated

        val activeFavIds = updated.filter { it.isFavorite }.map { it.id }.toSet()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_FAVORITE_IDS, activeFavIds).apply()
    }

    fun addOrUpdateTarget(
        context: Context,
        id: String,
        name: String,
        username: String,
        avatar: String,
        type: FavoriteType,
        latitude: Double,
        longitude: Double,
        categoryOrRole: String,
        distanceKm: Double,
        isFavorite: Boolean = true
    ) {
        val current = _favoriteTargets.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == id }
        val newTarget = FavoriteProximityTarget(
            id = id,
            name = name,
            username = username,
            avatar = avatar,
            type = type,
            latitude = latitude,
            longitude = longitude,
            categoryOrRole = categoryOrRole,
            lastDistanceKm = distanceKm,
            isFavorite = isFavorite
        )

        if (existingIndex >= 0) {
            current[existingIndex] = newTarget
        } else {
            current.add(0, newTarget)
        }
        _favoriteTargets.value = current

        val activeFavIds = current.filter { it.isFavorite }.map { it.id }.toSet()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_FAVORITE_IDS, activeFavIds).apply()
    }

    /**
     * Checks distance of all favorite targets against user coordinate.
     * Fires push notification if a favorite target is within the proximity radius.
     */
    fun checkProximityAndNotify(context: Context, userLat: Double, userLon: Double) {
        if (!_isServiceEnabled.value) return

        val radiusLimit = _proximityRadiusKm.value
        val now = System.currentTimeMillis()

        _favoriteTargets.value.filter { it.isFavorite }.forEach { target ->
            val distance = calculateDistanceKm(userLat, userLon, target.latitude, target.longitude)
            if (distance <= radiusLimit) {
                val lastAlert = lastAlertTimestampMap[target.id] ?: 0L
                // Debounce alert per target (at most once every 5 minutes in background)
                if (now - lastAlert > 300_000L) {
                    lastAlertTimestampMap[target.id] = now
                    sendPushNotification(context, target, distance)
                }
            }
        }
    }

    /**
     * Instant test trigger for user verification in emulator or UI.
     */
    fun triggerSimulatedAlert(context: Context, targetId: String? = null) {
        val target = if (targetId != null) {
            _favoriteTargets.value.firstOrNull { it.id == targetId }
        } else {
            _favoriteTargets.value.firstOrNull { it.isFavorite } ?: _favoriteTargets.value.firstOrNull()
        } ?: FavoriteProximityDefaults.defaultTargets.first()

        val simulatedDistance = min(target.lastDistanceKm, _proximityRadiusKm.value.coerceAtLeast(1.0) * 0.7)
        sendPushNotification(context, target, simulatedDistance)
    }

    private fun sendPushNotification(context: Context, target: FavoriteProximityTarget, distanceKm: Double) {
        createNotificationChannel(context)

        val isCreator = target.type == FavoriteType.CREATOR
        val targetRole = if (isCreator) "Favorite Creator" else "Favorite Marketplace Seller"
        val formattedDist = String.format("%.1f", distanceKm)

        val title = "📍 $targetRole in ${formattedDist}KM!"
        val message = "${target.name} (@${target.username}) has entered your proximity radius (${formattedDist}km away). Tap to view profile & offers."

        // 1. In-app Notification insertion into Room Database
        scope.launch {
            try {
                val db = AppDatabase.getDatabase(context, this)
                db.localiiiyDao().insertNotification(
                    NotificationEntity(
                        username = target.username,
                        userAvatar = target.avatar,
                        actionType = "PROXIMITY_ALERT",
                        content = "📍 $targetRole is nearby! Entered your ${formattedDist}km proximity radius.",
                        distanceKm = distanceKm,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // Non-fatal fallback
            }
        }

        // 2. Android System Drawer Push Notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_NOTIFICATIONS", true)
            putExtra("TARGET_USERNAME", target.username)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            target.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setSound(defaultSound)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF00C896.toInt()) // Localiiiy Teal
            .build()

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(target.id.hashCode().coerceAtLeast(1000), notification)
            }
        } catch (e: Exception) {
            // Notification security fallback
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Favorite Proximity Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pushes instant notifications when favorite creators or marketplace sellers enter your proximity radius (e.g. 5KM)."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
