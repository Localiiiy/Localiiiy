package com.example.data

object InitialData {

    val defaultProfile = UserProfileEntity(
        id = 1,
        username = "neighbor",
        fullName = "Localiiiy Neighbor",
        bio = "Connected with my local community on Localiiiy.",
        avatarUrl = "",
        postsCount = 0,
        connectionsCount = 0,
        connectedCount = 0,
        website = "localiiiy.app"
    )

    val defaultPrivacySettings = PrivacySettingsEntity()

    val starterOtherUsers = emptyList<OtherUserEntity>()

    val starterStories = emptyList<StoryEntity>()

    val starterPosts = emptyList<PostEntity>()

    val starterClips = emptyList<ClipEntity>()

    val starterNotifications = emptyList<NotificationEntity>()

    val starterMarketplaceItems = emptyList<MarketplaceItemEntity>()

    val starterConversations = emptyList<DirectMessageEntity>()

    val starterDirectMessages = emptyList<DirectMessageEntity>()

    val starterComments = emptyList<CommentEntity>()

    val starterStudioVideos = emptyList<StudioVideoEntity>()
}
