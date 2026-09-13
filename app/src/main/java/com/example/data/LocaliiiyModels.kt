package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    val isAiGenerated: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val userAvatar: String,
    val userHandle: String,
    val isVerified: Boolean = false,
    val mediaUrl: String,
    val mediaType: String = "IMAGE", // IMAGE or VIDEO
    val caption: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowing: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String? = null,
    val landmark: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKm: Double? = null,
    val isNeighbor: Boolean = false,
    val soundTitle: String? = null,
    val filterName: String = "Normal",
    val creatorFollowers: Int = 0
) {
    val isConnected: Boolean get() = isFollowing
}

@Entity(tableName = "clips")
data class ClipEntity(
    val isAiGenerated: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val userAvatar: String,
    val userHandle: String,
    val isVerified: Boolean = false,
    val mediaUrl: String,
    val caption: String,
    val soundTitle: String = "Original audio",
    val soundArtist: String = "",
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int = 120,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowing: Boolean = false,
    val viewsCount: String = "245K",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String? = null,
    val landmark: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKm: Double? = null,
    val isNeighbor: Boolean = false,
    val filterName: String = "Normal",
    val creatorFollowers: Int = 0
) {
    val isConnected: Boolean get() = isFollowing
}

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val mediaUrl: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String? = null,
    val distanceKm: Double? = null,
    val isViewed: Boolean = false,
    val isUserStory: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "POST" or "CLIP" or "STUDIO"
    val targetId: Long,
    val username: String,
    val userAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val parentId: Long? = null,
    val videoTimestampSeconds: Int? = null,
    val repliesCount: Int = 0
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "alex_creative",
    val fullName: String = "Alex Rivera",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
    val bio: String = "Visual storyteller & digital creator 📸✨\nCapturing moments across the neighborhood & globe 🌍\nSeattle • Pike Place • Pioneer Sq",
    val website: String = "Localiiiy.com/alex",
    val category: String = "Local Creator / Photographer",
    val locationName: String = "Pike Place Market, Seattle",
    val neighborhood: String = "Pike Place Market, Seattle",
    val neighborsCount: Int = 142,
    val latitude: Double = 47.608013,
    val longitude: Double = -122.335167,
    val postsCount: Int = 42,
    val followersCount: Int = 14200,
    val followingCount: Int = 486,
    val isVerified: Boolean = true,
    val studioUsername: String = "alex_studio",
    val studioChannelName: String = "Alex Rivera Studio",
    val studioSubscribersCount: Int = 14200
) {
    val connectionsCount: Int get() = followersCount
    val connectedCount: Int get() = followingCount
    val studioConnectedCount: Int get() = studioSubscribersCount
}

@Entity(tableName = "other_users")
data class OtherUserEntity(
    @PrimaryKey val username: String,
    val fullName: String,
    val avatarUrl: String,
    val bio: String,
    val website: String = "",
    val category: String = "Creator",
    val locationName: String,
    val landmark: String? = null,
    val latitude: Double = 47.608013,
    val longitude: Double = -122.335167,
    val distanceKm: Double = 0.5,
    val isNeighbor: Boolean = true,
    val isFollowing: Boolean = false,
    val isFriend: Boolean = false,
    val followersCount: Int = 3400,
    val followingCount: Int = 290,
    val postsCount: Int = 18,
    val isVerified: Boolean = false,
    val studioUsername: String = "",
    val studioSubscribersCount: Int = 0,
    val isBlocked: Boolean = false,
    val isCyberstalkingBanned: Boolean = false
) {
    val isConnected: Boolean get() = isFollowing
    val connectionsCount: Int get() = followersCount
    val connectedCount: Int get() = followingCount
    val studioConnectedCount: Int get() = studioSubscribersCount
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val userAvatar: String,
    val actionType: String, // "LIKE", "COMMENT", "CONNECT", "FOLLOW", "NEARBY_WAVE", "FRIEND_REQUEST"
    val content: String,
    val mediaPreviewUrl: String? = null,
    val distanceKm: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isFollowingBack: Boolean = false
) {
    val isConnectedBack: Boolean get() = isFollowingBack
}

@Entity(tableName = "conversations")
data class DirectMessageEntity(
    @PrimaryKey val conversationId: String,
    val contactUsername: String,
    val contactAvatar: String,
    val lastMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val isRead: Boolean = true,
    val isOnline: Boolean = false,
    val isGroup: Boolean = false,
    val groupMembersCount: Int = 1,
    val distanceKm: Double? = null,
    val landmark: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val senderUsername: String,
    val senderAvatar: String,
    val text: String,
    val sharedMediaUrl: String? = null,
    val sharedCaption: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false
)

@Entity(tableName = "privacy_settings")
data class PrivacySettingsEntity(
    @PrimaryKey val id: Int = 1,
    // Hyperlocal Location Controls
    val isLocationRadarEnabled: Boolean = true,
    val isPrivateAccount: Boolean = false,
    val allowNearbyDiscovery: Boolean = true,
    val preciseLocationSharing: Boolean = true,
    val allowNearbyWaves: Boolean = true,
    // Activity & Interactions
    val showActiveStatus: Boolean = true,
    val readReceiptsEnabled: Boolean = true,
    val allowCommentsFrom: String = "EVERYONE", // "EVERYONE", "PEOPLE_YOU_FOLLOW", "NO_ONE"
    val allowDirectMessagesFrom: String = "EVERYONE", // "EVERYONE", "PEOPLE_YOU_FOLLOW", "NO_ONE"
    val allowTagsAndMentions: String = "EVERYONE", // "EVERYONE", "PEOPLE_YOU_FOLLOW", "NO_ONE"
    // Stories & Content
    val hideMomentsFromStrangers: Boolean = false,
    val allowPostResharing: Boolean = true,
    val sensitiveContentFilter: String = "STANDARD", // "STANDARD", "STRICT", "LESS"
    val blockedAccountsCount: Int = 0,
    val hideMobileNumber: Boolean = false,
    val hideEmailAddress: Boolean = false,
    val hideAddress: Boolean = false,
    // Master Ghost Mode & Granular Visibility Controls
    val isGhostMode: Boolean = false,
    val locationPrecisionKm: Float = 10f,
    val hideLocation: Boolean = false,
    val hideProfilePicture: Boolean = false,
    val hidePosts: Boolean = false,
    val hideClips: Boolean = false,
    val hideInNeighborhood: Boolean = false,
    // Live Radar Precise Location & Distance Obfuscation
    val hidePreciseLocationOnRadar: Boolean = false,
    val radarObfuscatedRange: String = "3k", // "3k", "10K", "100k", "500K", "Country", "Earth", "Galaxy"
    val radarCountryName: String = "United States",
    // App Background & Atmosphere
    val appThemeBackground: String = "DEFAULT", // "DEFAULT", "BLACK_HOLE", "MOON", "GALAXY", "CUSTOM"
    val customBackgroundImageUri: String = ""
)

@Entity(tableName = "marketplace_items")
data class MarketplaceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val currencySymbol: String = "$",
    val category: String, // "Tech & Gear", "Farm & Fresh", "Vintage & Style", "Home & Deco", "Mobility & Bikes", "Art & Craft", "Books & Media", "Services"
    val condition: String = "Like New", // "Brand New", "Like New", "Good", "Fair"
    val imageUrl: String,
    val sellerUsername: String,
    val sellerFullName: String,
    val creatorFollowers: Int = 0,
    val sellerFollowers: Int = 0,
    val sellerAvatar: String,
    val sellerRating: Double = 4.9,
    val sellerReviewCount: Int = 18,
    val location: String = "Pike Place, Seattle",
    val landmark: String? = "Pike Place Market",
    val distanceKm: Double = 0.4,
    val latitude: Double = 47.608013,
    val longitude: Double = -122.335167,
    val isAvailable: Boolean = true,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val deliveryOption: String = "Local Meetup / Pickup" // "Local Meetup", "Neighborhood Drop-off", "Contactless Pickup"
)

@Entity(tableName = "studio_videos")
data class StudioVideoEntity(
    val isAiGenerated: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int, // Long-form creator content: minimum 60 seconds with unlimited duration (no 240m ceiling)
    val category: String, // "Music", "News", "Podcasts", "Tech & Gadgets", "Neighborhood & Culture", "Documentaries", "Food & Cooking", "Gaming", "Education", "Entertainment"
    val creatorUsername: String,
    val creatorFullName: String,
    val creatorAvatar: String,
    val isCreatorVerified: Boolean = false,
    val creatorFollowers: Int = 0,
    val creatorSubscribersCount: String = "124K subscribers",
    val subscriberCountInt: Int = 124000,
    val distanceKm: Double = 1.2,
    val isSubscribed: Boolean = false,
    val viewsCount: Long = 14200,
    val viewsFormatted: String = "14.2K views",
    val likesCount: Int = 1250,
    val dislikesCount: Int = 12,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false, // Watch Later
    val uploadDateFormatted: String = "3 days ago",
    val timestamp: Long = System.currentTimeMillis(),
    val tags: String = "#Localiiiy #Studio #Creators",
    val resolution: String = "4K Ultra HD",
    val chapters: String = "00:00 - Introduction\n05:15 - Core Story\n18:30 - Deep Dive\n35:00 - Community Q&A",
    val isCreatorPick: Boolean = false,
    val isTrending: Boolean = false
) {
    val isConnected: Boolean get() = isSubscribed
    val creatorConnectedCount: String get() = creatorSubscribersCount
        .replace("subscribers", "Connected")
        .replace("subscriber", "Connected")
}

// --- Local Room Cache: User Activity ---
@Entity(tableName = "user_activity_cache")
data class UserActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String, // "LIKED_POST", "SAVED_POST", "WATCHED_STUDIO_VIDEO", "WAVED_NEIGHBOR", "CREATED_DRAFT", "PUBLISHED_STUDIO_VIDEO"
    val targetId: String,
    val targetTitle: String,
    val targetPreviewUrl: String? = null,
    val extraDetails: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Local Room Cache: Saved Posts ---
@Entity(tableName = "saved_posts_cache")
data class SavedPostEntity(
    @PrimaryKey val postId: Long,
    val username: String,
    val userAvatar: String,
    val userHandle: String = "",
    val mediaUrl: String,
    val mediaType: String = "IMAGE",
    val caption: String,
    val location: String? = null,
    val landmark: String? = null,
    val distanceKm: Double? = null,
    val likesCount: Int = 0,
    val collectionName: String = "All Saved",
    val savedTimestamp: Long = System.currentTimeMillis()
)

// --- Local Room Cache: Localiiiy Studio Drafts ---
@Entity(tableName = "studio_drafts_cache")
data class StudioDraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Music",
    val videoUri: String = "",
    val thumbnailUri: String = "",
    val durationSeconds: Int = 300, // Default 5 mins
    val tags: String = "#Localiiiy #Studio",
    val chapters: String = "00:00 - Introduction",
    val resolution: String = "4K Ultra HD",
    val isReadyToPublish: Boolean = false,
    val lastEditedTimestamp: Long = System.currentTimeMillis()
)


@Entity(tableName = "pulse_cache")
data class PulseCacheEntity(
    @PrimaryKey val id: String,
    val authorName: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val landmark: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceKm: Double = 1.2,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0,
    val tags: String = ""
)

@Entity(tableName = "drafts")
data class DraftClipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mediaUri: String,
    val draftType: String = "CLIP",
    val filtersApplied: String? = null,
    val lastEditedTimestamp: Long = System.currentTimeMillis()
)
