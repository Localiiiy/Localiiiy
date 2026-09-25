package com.example.data.community

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class CommunityAttachmentType {
    NONE,
    MARKET_ITEM,
    STUDIO_VIDEO,
    CLIP_REEL,
    BROADCAST_ALERT
}

data class CommunityAttachment(
    val type: CommunityAttachmentType = CommunityAttachmentType.NONE,
    val targetId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val priceOrStats: String = "",
    val actionUrl: String = ""
) : Serializable

@Entity(tableName = "localiiiy_communities")
data class CommunityEntity(
    @PrimaryKey val id: String = "comm_hyperlocal_1",
    val name: String = "Localiiiy Hyperlocal Hub",
    val description: String = "Official neighborhood hub for local creators, artisans, and neighbors to share updates, marketplace deals, studio clips, and community alerts.",
    val neighborhood: String = "Hyperlocal District",
    val iconEmoji: String = "🏘️",
    val bannerUrl: String = "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=1200&auto=format&fit=crop&q=80",
    val creatorUid: String = "owner_local",
    val creatorName: String = "Community Moderator",
    val memberCount: Int = 28,
    val isJoined: Boolean = true,
    val isPrivate: Boolean = false,
    val category: String = "General Community",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey val id: String = "post_${System.currentTimeMillis()}",
    val communityId: String = "comm_hyperlocal_1",
    val authorUid: String = "",
    val authorName: String = "Neighbor Creator",
    val authorHandle: String = "@neighbor",
    val authorAvatar: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isBroadcastAlert: Boolean = false,
    val attachmentType: CommunityAttachmentType = CommunityAttachmentType.NONE,
    val attachmentTargetId: String = "",
    val attachmentTitle: String = "",
    val attachmentSubtitle: String = "",
    val attachmentImageUrl: String = "",
    val attachmentBadge: String = "",
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0,
    val isPinned: Boolean = false
) : Serializable

data class CommunityMember(
    val uid: String,
    val name: String,
    val handle: String,
    val avatar: String,
    val role: String = "Neighbor", // Admin, Elder, Creator, Resident, VIP
    val distanceKm: Double = 0.5,
    val isOnline: Boolean = true,
    val isPremium: Boolean = false,
    val trustScore: Int = 98
) : Serializable

data class CommunityVoiceSpeaker(
    val username: String,
    val fullName: String,
    val avatar: String,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isHost: Boolean = false
) : Serializable

data class CommunityVoiceSpace(
    val id: String = "space_1",
    val title: String = "Neighborhood Coffee Lounge & Creator Jam ☕",
    val activeTopic: String = "Upcoming Summer Artisan Street Market & Studio Collabs",
    val isLive: Boolean = true,
    val listenersCount: Int = 18,
    val speakers: List<CommunityVoiceSpeaker> = emptyList()
) : Serializable

data class CommunityPollOption(
    val id: String,
    val text: String,
    val votesCount: Int = 0,
    val isSelectedByMe: Boolean = false
) : Serializable

data class CommunityPoll(
    val id: String,
    val communityId: String,
    val authorName: String,
    val question: String,
    val options: List<CommunityPollOption>,
    val totalVotes: Int,
    val expiresAtText: String = "Ends in 2 days"
) : Serializable

data class CommunityResourceItem(
    val id: String,
    val title: String,
    val category: String, // "Photography", "Home & Garden", "Maker Tools", "Audio Gear"
    val ownerName: String,
    val ownerAvatar: String,
    val availabilityStatus: String = "Available Now",
    val imageUrl: String,
    val distanceKm: Double = 0.3
) : Serializable

data class CommunitySafetyAlert(
    val id: String,
    val title: String,
    val details: String,
    val severity: String = "HIGH", // "HIGH", "MODERATE", "ADVISORY"
    val issuedAtText: String = "15m ago",
    val safeHavenSpot: String = "Civic Plaza Community Hall"
) : Serializable

