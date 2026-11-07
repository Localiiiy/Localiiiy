package com.example.data.model

data class Pulse(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val locationName: String,
    val timestampFormatted: String,
    val textContent: String,
    val imageUrl: String? = null,
    val likesCount: Int,
    val commentsCount: Int,
    val category: String, // "Alert", "Tip", "Event", "General"
    val userDistanceKm: Double,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

data class RadarItem(
    val id: String,
    val title: String,
    val category: String, // "Event", "Spot", "Pulse", "Creator", "Market"
    val angleDegrees: Float, // 0 to 360 for radar visualization
    val distanceMeters: Int,
    val rating: Double = 4.8,
    val imageUrl: String,
    val address: String,
    val snippet: String,
    val isFavorite: Boolean = false
)

data class CreatorClip(
    val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatarUrl: String,
    val title: String,
    val videoThumbnailUrl: String,
    val locationName: String,
    val likesCount: Int,
    val sharesCount: Int,
    val commentsCount: Int,
    val taggedProduct: String? = null,
    val isLiked: Boolean = false
)

data class MarketListing(
    val id: String,
    val sellerName: String,
    val sellerRating: Double,
    val sellerAvatarUrl: String,
    val title: String,
    val price: Double,
    val category: String, // "Handmade", "Electronics", "Services", "Vintage", "Food"
    val imageUrl: String,
    val description: String,
    val locationDistanceKm: Double,
    val condition: String, // "New", "Like New", "Artisan Made", "Good"
    val isSaved: Boolean = false
)

data class UserProfile(
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val bio: String,
    val neighborhood: String,
    val points: Int,
    val badges: List<String>,
    val proximityAlertsEnabled: Boolean,
    val totalPulses: Int,
    val totalClips: Int,
    val totalListings: Int
)
