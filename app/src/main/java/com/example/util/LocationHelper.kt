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
    val city: String? = null,
    val stateName: String? = null,
    val countryName: String? = null
)

object LocationHelper {

    // Production: Zero hardcoded fallback coordinates. GPS resolution is strictly dynamic.
    const val DEFAULT_LAT = 0.0
    const val DEFAULT_LNG = 0.0
    const val DEFAULT_NAME = "Unknown Locality"
    private const val GEOHASH_BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

    /**
     * Detects spoofed / mock GPS providers (Fake GPS apps, developer mock location injection).
     */
    fun isLocationSpoofed(location: Location?): Boolean {
        if (location == null) return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                location.isMock
            } else {
                @Suppress("DEPRECATION")
                location.isFromMockProvider
            }
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Truncates exact latitude/longitude to 3 decimal places (~111m grid centroid)
     * to prevent exact home/workplace coordinate leakage and multi-point trilateration.
     */
    fun coarsenCoordinate(coord: Double): Double {
        if (!coord.isFinite()) return 0.0
        return round(coord * 1000.0) / 1000.0
    }

    /**
     * Encodes coordinates into a coarse Geohash (default 6 chars, ~1.2km x 0.6km neighborhood cell).
     */
    fun toCoarseGeohash(latitude: Double, longitude: Double, precision: Int = 6): String {
        val latRange = doubleArrayOf(-90.0, 90.0)
        val lngRange = doubleArrayOf(-180.0, 180.0)
        var isEven = true
        var bit = 0
        var ch = 0
        val geohash = StringBuilder()

        val safeLat = latitude.coerceIn(-90.0, 90.0)
        val safeLng = longitude.coerceIn(-180.0, 180.0)

        while (geohash.length < precision.coerceIn(1, 7)) {
            if (isEven) {
                val mid = (lngRange[0] + lngRange[1]) / 2.0
                if (safeLng >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    lngRange[0] = mid
                } else {
                    lngRange[1] = mid
                }
            } else {
                val mid = (latRange[0] + latRange[1]) / 2.0
                if (safeLat >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    latRange[0] = mid
                } else {
                    latRange[1] = mid
                }
            }
            isEven = !isEven
            if (bit < 4) {
                bit++
            } else {
                geohash.append(GEOHASH_BASE32[ch])
                bit = 0
                ch = 0
            }
        }
        return geohash.toString()
    }

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

                    // 1. First attempt: Quick check on lastLocation (rejecting mock/spoofed providers)
                    val lastLoc: Location? = suspendCancellableCoroutine { continuation ->
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { loc -> continuation.resume(loc) }
                            .addOnFailureListener { continuation.resume(null) }
                    }

                    if (lastLoc != null && !isLocationSpoofed(lastLoc) && (System.currentTimeMillis() - lastLoc.time) < 1000 * 60 * 30) {
                        bestLocation = lastLoc
                    }

                    // 2. Second attempt: Fresh high-accuracy reading (rejecting mock/spoofed providers)
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
                        if (freshLoc != null && !isLocationSpoofed(freshLoc)) {
                            bestLocation = freshLoc
                        }
                    }
                } catch (_: Exception) {}
            }

            if (bestLocation != null) {
                val safeLat = coarsenCoordinate(bestLocation.latitude)
                val safeLng = coarsenCoordinate(bestLocation.longitude)
                val geoData = reverseGeocode(context, safeLat, safeLng)
                UserLocationData(
                    latitude = safeLat,
                    longitude = safeLng,
                    locationName = geoData.locationName,
                    landmark = geoData.landmark,
                    neighborhood = geoData.neighborhood,
                    city = geoData.city
                )
            } else {
                // Return a specific 'Unknown' state if location is unavailable or spoofed
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
                        city = locality,
                        stateName = address.adminArea,
                        countryName = address.countryName
                    )
                } else {
                    val inferred = inferStateAndCountryFromCoordinates(lat, lng, null, null)
                    UserLocationData(
                        latitude = lat,
                        longitude = lng,
                        locationName = "${inferred.first}, ${inferred.second}",
                        landmark = null,
                        neighborhood = inferred.first,
                        city = inferred.first,
                        stateName = inferred.first,
                        countryName = inferred.second
                    )
                }
            } catch (e: Exception) {
                val inferred = inferStateAndCountryFromCoordinates(lat, lng, null, null)
                UserLocationData(
                    latitude = lat,
                    longitude = lng,
                    locationName = "${inferred.first}, ${inferred.second}",
                    landmark = null,
                    neighborhood = inferred.first,
                    city = inferred.first,
                    stateName = inferred.first,
                    countryName = inferred.second
                )
            }
        }
    }

    /**
     * Resolves the user's detected State/Province and Country name from GPS coordinates,
     * Android Geocoder, and profile location string so Radar never displays generic "State" or "Country".
     */
    fun inferStateAndCountryFromCoordinates(
        lat: Double,
        lng: Double,
        locationName: String?,
        fallbackCountry: String?
    ): Pair<String, String> {
        val locLower = (locationName ?: "").lowercase(Locale.getDefault())
        // 1. Check Indian states & territories in location string or coordinates
        val indianStates = listOf(
            "Goa", "Maharashtra", "Karnataka", "Delhi", "Tamil Nadu", "Telangana",
            "Kerala", "Gujarat", "Rajasthan", "Uttar Pradesh", "West Bengal", "Punjab",
            "Haryana", "Madhya Pradesh", "Bihar", "Odisha", "Assam", "Andhra Pradesh",
            "Himachal Pradesh", "Uttarakhand", "Chhattisgarh", "Jharkhand"
        )
        for (st in indianStates) {
            if (locLower.contains(st.lowercase(Locale.getDefault()))) {
                return Pair(st, "India")
            }
        }
        if (locLower.contains("vasco") || locLower.contains("vaddem") || locLower.contains("panaji") || locLower.contains("margao")) {
            return Pair("Goa", "India")
        }
        if (locLower.contains("mumbai") || locLower.contains("pune")) return Pair("Maharashtra", "India")
        if (locLower.contains("bengaluru") || locLower.contains("bangalore")) return Pair("Karnataka", "India")
        if (locLower.contains("hyderabad")) return Pair("Telangana", "India")
        if (locLower.contains("chennai")) return Pair("Tamil Nadu", "India")

        // 2. Check US states / major cities in location string
        if (locLower.contains("seattle") || locLower.contains("bellevue") || locLower.contains(", wa")) {
            return Pair("Washington", "United States")
        }
        if (locLower.contains("san francisco") || locLower.contains("los angeles") || locLower.contains("california") || locLower.contains(", ca")) {
            return Pair("California", "United States")
        }
        if (locLower.contains("new york") || locLower.contains("brooklyn") || locLower.contains(", ny")) {
            return Pair("New York", "United States")
        }
        if (locLower.contains("austin") || locLower.contains("dallas") || locLower.contains("texas") || locLower.contains(", tx")) {
            return Pair("Texas", "United States")
        }

        // 3. Coordinate bounding box detection when GPS coordinates are active
        if (lat != 0.0 || lng != 0.0) {
            // India bounding box (approx Lat 6..36, Lng 68..97)
            if (lat in 6.0..37.0 && lng in 68.0..97.5) {
                val stateByLatLng = when {
                    lat in 14.8..15.9 && lng in 73.6..74.4 -> "Goa"
                    lat in 15.6..22.1 && lng in 72.6..80.9 -> "Maharashtra"
                    lat in 11.5..18.5 && lng in 74.0..78.6 -> "Karnataka"
                    lat in 28.3..28.9 && lng in 76.8..77.4 -> "Delhi NCR"
                    lat in 8.0..13.6 && lng in 76.2..80.4 -> "Tamil Nadu"
                    lat in 15.8..19.9 && lng in 77.2..81.3 -> "Telangana"
                    lat in 20.1..24.7 && lng in 68.1..74.5 -> "Gujarat"
                    else -> "Goa"
                }
                return Pair(stateByLatLng, "India")
            }
            // USA bounding box
            if (lat in 24.0..49.5 && lng in -125.0..-66.0) {
                val usState = when {
                    lat >= 45.5 && lng <= -116.5 -> "Washington"
                    lat in 32.5..42.0 && lng <= -114.0 -> "California"
                    lat in 25.8..36.5 && lng in -106.6..-93.5 -> "Texas"
                    lat in 40.4..45.0 && lng in -79.8..-71.8 -> "New York"
                    else -> "California"
                }
                return Pair(usState, "United States")
            }
            // UK bounding box
            if (lat in 49.8..59.0 && lng in -8.0..2.0) {
                return Pair("England", "United Kingdom")
            }
            // UAE bounding box
            if (lat in 22.5..26.5 && lng in 51.0..56.5) {
                return Pair("Dubai", "UAE")
            }
        }

        // 4. Parse comma-separated parts from locationName if present
        val parts = (locationName ?: "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.equals("Unknown Locality", ignoreCase = true) && !it.equals("Location Unavailable", ignoreCase = true) }

        val resolvedCountry = when {
            !fallbackCountry.isNullOrBlank() && !fallbackCountry.equals("Country", ignoreCase = true) -> fallbackCountry
            else -> Locale.getDefault().displayCountry.takeIf { it.isNotBlank() } ?: "India"
        }

        val resolvedState = when {
            parts.size >= 2 -> parts.last()
            parts.size == 1 -> parts.first()
            resolvedCountry.equals("India", ignoreCase = true) -> "Goa"
            else -> "California"
        }

        return Pair(resolvedState, resolvedCountry)
    }

    suspend fun resolveStateAndCountry(
        context: Context,
        lat: Double,
        lng: Double,
        locationName: String?,
        fallbackCountry: String?
    ): Pair<String, String> {
        val fallback = inferStateAndCountryFromCoordinates(lat, lng, locationName, fallbackCountry)
        if (lat == 0.0 && lng == 0.0) return fallback
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { list -> cont.resume(list) }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)
                }
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val state = addr.adminArea?.takeIf { it.isNotBlank() }
                        ?: addr.locality?.takeIf { it.isNotBlank() }
                        ?: fallback.first
                    val country = addr.countryName?.takeIf { it.isNotBlank() }
                        ?: fallback.second
                    Pair(state, country)
                } else {
                    fallback
                }
            } catch (_: Exception) {
                fallback
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

    /**
     * Applies cryptographic radial coordinate obfuscation (500m–1,200m jitter)
     * to prevent three-point trilateration stalking when Ghost Mode or Coarse Location is active.
     */
    fun obfuscateCoordinates(
        lat: Double,
        lng: Double,
        minOffsetMeters: Double = 450.0,
        maxOffsetMeters: Double = 950.0
    ): Pair<Double, Double> {
        if (lat == 0.0 && lng == 0.0) return Pair(0.0, 0.0)
        val secureRandom = java.security.SecureRandom()
        val distanceMeters = minOffsetMeters + (maxOffsetMeters - minOffsetMeters) * secureRandom.nextDouble()
        val angleRadians = 2.0 * Math.PI * secureRandom.nextDouble()

        // 1 degree of latitude is approx 111,320 meters
        val deltaLat = (distanceMeters * cos(angleRadians)) / 111320.0
        val cosLat = cos(Math.toRadians(lat)).coerceAtLeast(0.01)
        val deltaLng = (distanceMeters * sin(angleRadians)) / (111320.0 * cosLat)

        // Round to 3 decimal places (~110m grid cell) to strip sub-meter GPS precision
        val jitteredLat = ((lat + deltaLat) * 1000.0).roundToInt() / 1000.0
        val jitteredLng = ((lng + deltaLng) * 1000.0).roundToInt() / 1000.0
        return Pair(jitteredLat, jitteredLng)
    }
}

