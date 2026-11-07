package com.example.util

import com.example.data.PostEntity
import kotlin.math.*

data class NeighborhoodHotspot(
    val id: String,
    val name: String,
    val neighborhood: String,
    val latitude: Double,
    val longitude: Double,
    val radiusKm: Double = 1.8
)

data class HotspotAlert(
    val hotspotName: String,
    val postCount: Int,
    val message: String
)

object HotspotManager {
    // Production: Zero hardcoded mock hotspots. Only genuine community areas created by real users.
    val predefinedHotspots: List<NeighborhoodHotspot> = emptyList()

    fun checkHotspotEntry(
        lat: Double,
        lng: Double,
        posts: List<PostEntity>,
        lastAlertedHotspotId: String?
    ): Pair<NeighborhoodHotspot?, HotspotAlert?> {
        // Production: Disable phantom notifications
        return Pair(null, null)
    }
}
