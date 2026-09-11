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
    // Curated starter creators and marketplace sellers near user (default user at ~37.7749, -122.4194)
    val defaultTargets = listOf(
        FavoriteProximityTarget(
            id = "elena.design",
            name = "Elena Vance",
            username = "elena.design",
            avatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            type = FavoriteType.MARKETPLACE_SELLER,
            latitude = 37.7850,
            longitude = -122.4080,
            categoryOrRole = "Artisan Leather & Crafts",
            lastDistanceKm = 1.4,
            isFavorite = true
        ),
        FavoriteProximityTarget(
            id = "chloe.cafes",
            name = "Chloe Dupont",
            username = "chloe.cafes",
            avatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80",
            type = FavoriteType.CREATOR,
            latitude = 37.7650,
            longitude = -122.4350,
            categoryOrRole = "Coffee & Bakery Explorer",
            lastDistanceKm = 2.1,
            isFavorite = true
        ),
        FavoriteProximityTarget(
            id = "oliver_london",
            name = "Oliver Thorne",
            username = "oliver_london",
            avatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            type = FavoriteType.MARKETPLACE_SELLER,
            latitude = 37.7910,
            longitude = -122.3990,
            categoryOrRole = "Vintage Horology & Antiques",
            lastDistanceKm = 3.6,
            isFavorite = true
        ),
        FavoriteProximityTarget(
            id = "maya_sound",
            name = "Maya Lin",
            username = "maya_sound",
            avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            type = FavoriteType.CREATOR,
            latitude = 37.7550,
            longitude = -122.4150,
            categoryOrRole = "Ambient Sound & Beats Studio",
            lastDistanceKm = 4.2,
            isFavorite = false
        ),
        FavoriteProximityTarget(
            id = "yuki_shibuya",
            name = "Yuki Tanaka",
            username = "yuki_shibuya",
            avatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
            type = FavoriteType.MARKETPLACE_SELLER,
            latitude = 37.7420,
            longitude = -122.4220,
            categoryOrRole = "Tokyo Tech & Retro Collectibles",
            lastDistanceKm = 4.9,
            isFavorite = true
        )
    )
}
