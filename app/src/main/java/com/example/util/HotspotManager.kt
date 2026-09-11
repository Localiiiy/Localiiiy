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
    val predefinedHotspots = listOf(
        NeighborhoodHotspot("pike", "Pike Place Market", "Downtown", 47.6080, -122.3352, 1.5),
        NeighborhoodHotspot("capitol", "Capitol Hill Arts District", "Capitol Hill", 47.6190, -122.3210, 2.0),
        NeighborhoodHotspot("pioneer", "Pioneer Square Historic Core", "Pioneer Square", 47.6002, -122.3330, 1.2),
        NeighborhoodHotspot("belltown", "Belltown Social Hub", "Belltown", 47.6131, -122.3450, 1.5),
        NeighborhoodHotspot("ballard", "Ballard Avenue Strip", "Ballard", 47.6687, -122.3831, 2.0),
        NeighborhoodHotspot("slu", "South Lake Union Tech Center", "South Lake Union", 47.6229, -122.3365, 1.5)
    )

    fun checkHotspotEntry(
        lat: Double,
        lng: Double,
        posts: List<PostEntity>,
        lastAlertedHotspotId: String?
    ): Pair<NeighborhoodHotspot?, HotspotAlert?> {
        for (hotspot in predefinedHotspots) {
            val dist = LocationHelper.calculateDistanceKm(lat, lng, hotspot.latitude, hotspot.longitude)
            if (dist <= hotspot.radiusKm) {
                if (lastAlertedHotspotId != hotspot.id) {
                    val activePostsCount = posts.count { post ->
                        if (post.latitude != null && post.longitude != null) {
                            LocationHelper.calculateDistanceKm(lat, lng, post.latitude, post.longitude) <= 3.0
                        } else {
                            post.location?.contains(hotspot.neighborhood, ignoreCase = true) == true ||
                            post.location?.contains(hotspot.name, ignoreCase = true) == true ||
                            post.landmark?.contains(hotspot.neighborhood, ignoreCase = true) == true
                        }
                    }.coerceAtLeast(3)

                    val alert = HotspotAlert(
                        hotspotName = hotspot.name,
                        postCount = activePostsCount,
                        message = "🔥 Hotspot Alert: You entered ${hotspot.name}! ${activePostsCount} Pulse posts active right now."
                    )
                    return Pair(hotspot, alert)
                } else {
                    return Pair(hotspot, null)
                }
            }
        }
        return Pair(null, null)
    }
}
