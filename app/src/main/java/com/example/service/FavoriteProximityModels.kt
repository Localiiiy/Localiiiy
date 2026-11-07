package com.example.service

enum class FavoriteType {
    CREATOR,
    MARKETPLACE_SELLER
}

data class FavoriteProximityTarget(
    val id: String,
    val name: String,
    val username: String,
    val avatar: String,
    val type: FavoriteType,
    val latitude: Double,
    val longitude: Double,
    val categoryOrRole: String,
    val lastDistanceKm: Double,
    val isFavorite: Boolean = true
)

object FavoriteProximityDefaults {
    // Production: Zero hardcoded mock targets. Proximity alerts are only triggered for genuine favorites added by authenticated users.
    val defaultTargets: List<FavoriteProximityTarget> = emptyList()
}
