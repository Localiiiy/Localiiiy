package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.*

data class UserLocationData(
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val landmark: String? = null,
    val neighborhood: String? = null,
    val city: String? = null
)

object LocationHelper {

    // Default reference location (Downtown cultural district) used strictly as last-resort fallback
    const val DEFAULT_LAT = 47.608013
    const val DEFAULT_LNG = -122.335167
    const val DEFAULT_NAME = "Local Radar Zone"

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(
        context: Context,
        fallbackNeighborhood: String? = null,
        fallbackCity: String? = null
    ): UserLocationData {
        return withContext(Dispatchers.IO) {
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            var bestLocation: Location? = null

            if (hasFine || hasCoarse) {
                try {
                    val fusedLocationClient: FusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(context)

                    // 1. First attempt: Quick check on lastLocation
                    val lastLoc: Location? = suspendCancellableCoroutine { continuation ->
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { loc -> continuation.resume(loc) }
                            .addOnFailureListener { continuation.resume(null) }
                    }

                    if (lastLoc != null && (System.currentTimeMillis() - lastLoc.time) < 1000 * 60 * 30) {
                        bestLocation = lastLoc
                    }

                    // 2. Second attempt: Fresh high-accuracy reading
                    if (bestLocation == null) {
                        val freshLoc: Location? = withTimeoutOrNull(8000) {
                            suspendCancellableCoroutine { continuation ->
                                val cancellationTokenSource = CancellationTokenSource()
                                fusedLocationClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY,
                                    cancellationTokenSource.token
                                ).addOnSuccessListener { loc ->
                                    continuation.resume(loc)
                                }.addOnFailureListener {
                                    continuation.resume(null)
                                }
                            }
                        }
                        if (freshLoc != null) {
                            bestLocation = freshLoc
                        }
                    }
                } catch (_: Exception) {}
            }

            if (bestLocation != null) {
                val geoData = reverseGeocode(context, bestLocation.latitude, bestLocation.longitude)
                UserLocationData(
                    latitude = bestLocation.latitude,
                    longitude = bestLocation.longitude,
                    locationName = geoData.locationName,
                    landmark = geoData.landmark,
                    neighborhood = geoData.neighborhood,
                    city = geoData.city
                )
            } else {
                // Return a specific 'Unknown' state instead of a fake Seattle location
                UserLocationData(
                    latitude = 0.0,
                    longitude = 0.0,
                    locationName = "Location Unavailable",
                    landmark = null,
                    neighborhood = null,
                    city = null
                )
            }
        }
    }

    suspend fun geocodeAddressString(context: Context, query: String): UserLocationData? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine<List<Address>?> { cont ->
                        geocoder.getFromLocationName(query, 1) { list -> cont.resume(list) }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val subLocality = addr.subLocality ?: addr.subAdminArea ?: query
                    val locality = addr.locality ?: addr.adminArea ?: ""
                    UserLocationData(
                        latitude = addr.latitude,
                        longitude = addr.longitude,
                        locationName = if (locality.isNotBlank()) "$subLocality, $locality" else subLocality,
                        landmark = addr.featureName ?: subLocality,
                        neighborhood = subLocality,
                        city = locality
                    )
                } else null
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): UserLocationData {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { list ->
                            cont.resume(list)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val feature = address.featureName
                    val subLocality = address.subLocality ?: address.subAdminArea
                    val locality = address.locality ?: address.adminArea ?: address.countryName ?: "Local District"
                    val landmark = if (!feature.isNullOrBlank() && feature != address.subThoroughfare) feature else null

                    val displayName = when {
                        landmark != null && subLocality != null -> "$landmark, $subLocality"
                        landmark != null -> "$landmark, $locality"
                        subLocality != null -> "$subLocality, $locality"
                        else -> locality
                    }

                    UserLocationData(
                        latitude = lat,
                        longitude = lng,
                        locationName = displayName,
                        landmark = landmark ?: subLocality,
                        neighborhood = subLocality ?: locality,
                        city = locality
                    )
                } else {
                    UserLocationData(
                        latitude = lat,
                        longitude = lng,
                        locationName = "Local District",
                        landmark = null,
                        neighborhood = "Locality",
                        city = "Metro Area"
                    )
                }
            } catch (e: Exception) {
                UserLocationData(
                    latitude = lat,
                    longitude = lng,
                    locationName = "Local District",
                    landmark = null,
                    neighborhood = "Locality",
                    city = "Metro Area"
                )
            }
        }
    }

    /**
     * Calculate Distance in Kilometers between two coordinates using Haversine formula
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371.0 // Radius of earth in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Calculate initial compass bearing in degrees (0..360) from point 1 to point 2
     */
    fun calculateBearing(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLonRad = Math.toRadians(lon2 - lon1)

        val y = sin(dLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLonRad)
        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        return (bearingDeg + 360.0) % 360.0
    }

    fun getCompassDirection(bearingDeg: Double): String {
        val normalized = (bearingDeg + 360.0) % 360.0
        return when {
            normalized >= 337.5 || normalized < 22.5 -> "N"
            normalized < 67.5 -> "NE"
            normalized < 112.5 -> "E"
            normalized < 157.5 -> "SE"
            normalized < 202.5 -> "S"
            normalized < 247.5 -> "SW"
            normalized < 292.5 -> "W"
            else -> "NW"
        }
    }

    fun getEstimatedTravelTime(distanceKm: Double): String {
        return when {
            distanceKm <= 0.1 -> "1 min walk"
            distanceKm <= 1.0 -> "${max(2, (distanceKm * 12).toInt())} min walk"
            distanceKm <= 5.0 -> "${max(5, (distanceKm * 3.5).toInt())} min bike"
            distanceKm <= 50.0 -> "${max(10, (distanceKm * 1.3).toInt())} min drive"
            distanceKm <= 300.0 -> "${(distanceKm / 85.0).toInt()} hr drive"
            distanceKm <= 1000.0 -> "${(distanceKm / 90.0).toInt()} hr drive / 1.5 hr flight"
            distanceKm <= 5000.0 -> "${String.format(Locale.getDefault(), "%.1f", distanceKm / 750.0)} hr flight"
            else -> "${(distanceKm / 850.0).toInt()} hr flight ✈️"
        }
    }

    fun formatDistanceLabel(distanceKm: Double?): String {
        if (distanceKm == null) return "Nearby"
        return when {
            distanceKm < 0.1 -> "Right here (< 100m)"
            distanceKm < 1.0 -> "${(distanceKm * 1000).toInt()}m away"
            distanceKm < 10.0 -> String.format(Locale.getDefault(), "%.1f km away", distanceKm)
            distanceKm < 1000.0 -> "${distanceKm.toInt()} km away"
            distanceKm < 10000.0 -> "${String.format(Locale.getDefault(), "%,d", distanceKm.toInt())} km away"
            else -> "${String.format(Locale.getDefault(), "%,d", distanceKm.toInt())} km (Global 🌍)"
        }
    }
}
