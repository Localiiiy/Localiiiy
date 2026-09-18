package com.example.data

object InitialData {

    val defaultProfile = UserProfileEntity(
        id = 1,
        username = "new_neighbor",
        fullName = "Local Neighbor",
        avatarUrl = "",
        bio = "Welcome to Localiiiy! 📡✨\nComplete your profile to start connecting nearby.",
        website = "",
        category = "Neighborhood Member",
        locationName = "Your Community",
        latitude = 0.0,
        longitude = 0.0,
        postsCount = 0,
        followersCount = 0,
        followingCount = 0,
        isVerified = false,
        studioUsername = "",
        studioChannelName = "",
        studioSubscribersCount = 0
    )

    val starterOtherUsers = emptyList<OtherUserEntity>()

    val starterStories = emptyList<StoryEntity>()

    val starterPosts = emptyList<PostEntity>()

    val starterClips = emptyList<ClipEntity>()

    val starterComments = emptyList<CommentEntity>()

    val starterNotifications = emptyList<NotificationEntity>()

    val starterConversations = emptyList<DirectMessageEntity>()

    val starterChatMessages = emptyList<ChatMessageEntity>()

    val defaultPrivacySettings = PrivacySettingsEntity(
        id = 1,
        isLocationRadarEnabled = true,
        isPrivateAccount = false,
        allowNearbyDiscovery = true,
        preciseLocationSharing = true,
        allowNearbyWaves = true,
        showActiveStatus = true,
        readReceiptsEnabled = true,
        allowDirectCallsFromConnections = true,
        allowCommentsFrom = "EVERYONE",
        allowDirectMessagesFrom = "EVERYONE",
        allowTagsAndMentions = "EVERYONE",
        hideMomentsFromStrangers = false,
        allowPostResharing = true,
        sensitiveContentFilter = "STANDARD",
        blockedAccountsCount = 0,
        hideMobileNumber = false,
        hideEmailAddress = false,
        hideAddress = false,
        isGhostMode = false,
        locationPrecisionKm = 10f,
        hideLocation = false,
        hideProfilePicture = false,
        hidePosts = false,
        hideClips = false,
        hideInNeighborhood = false,
        hidePreciseLocationOnRadar = false,
        radarObfuscatedRange = "3k",
        radarCountryName = "",
        appThemeBackground = "DEFAULT",
        customBackgroundImageUri = ""
    )

    val starterMarketplaceItems = emptyList<MarketplaceItemEntity>()
}
