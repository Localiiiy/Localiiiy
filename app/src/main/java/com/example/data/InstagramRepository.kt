package com.example.data

import kotlinx.coroutines.flow.Flow

class InstagramRepository(private val dao: InstagramDao) {

    val allPosts: Flow<List<PostEntity>> = dao.getAllPosts()
    val feedPosts: Flow<List<PostEntity>> = dao.getFeedPosts()
    val savedPosts: Flow<List<PostEntity>> = dao.getSavedPosts()
    val allReels: Flow<List<ReelEntity>> = dao.getAllReels()
    val allStories: Flow<List<StoryEntity>> = dao.getAllStories()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val otherUsers: Flow<List<OtherUserEntity>> = dao.getAllOtherUsers()
    val notifications: Flow<List<NotificationEntity>> = dao.getNotifications()
    val conversations: Flow<List<DirectMessageEntity>> = dao.getConversations()
    val privacySettings: Flow<PrivacySettingsEntity?> = dao.getPrivacySettings()
    val allMarketplaceItems: Flow<List<MarketplaceItemEntity>> = dao.getAllMarketplaceItems()
    val savedMarketplaceItems: Flow<List<MarketplaceItemEntity>> = dao.getSavedMarketplaceItems()

    fun getPostsByUsername(username: String): Flow<List<PostEntity>> {
        return dao.getPostsByUsername(username)
    }

    fun getReelsByUsername(username: String): Flow<List<ReelEntity>> {
        return dao.getReelsByUsername(username)
    }

    fun getOtherUser(username: String): Flow<OtherUserEntity?> {
        return dao.getOtherUser(username)
    }

    fun getNearbyReels(maxKm: Double): Flow<List<ReelEntity>> {
        return dao.getNearbyReels(maxKm)
    }

    suspend fun togglePostLike(postId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updatePostLike(postId, !currentLiked, delta)
    }

    suspend fun togglePostSave(postId: Long, currentSaved: Boolean) {
        dao.updatePostSaved(postId, !currentSaved)
    }

    suspend fun toggleReelLike(reelId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updateReelLike(reelId, !currentLiked, delta)
    }

    suspend fun toggleReelSave(reelId: Long, currentSaved: Boolean) {
        dao.updateReelSaved(reelId, !currentSaved)
    }

    suspend fun toggleReelFollow(reelId: Long, currentFollowing: Boolean) {
        dao.updateReelFollowing(reelId, !currentFollowing)
    }

    suspend fun toggleOtherUserFollow(username: String, currentFollowing: Boolean) {
        val delta = if (currentFollowing) -1 else 1
        dao.updateOtherUserFollowing(username, !currentFollowing, delta)
    }

    suspend fun toggleOtherUserFriend(username: String, currentFriend: Boolean) {
        dao.updateOtherUserFriend(username, !currentFriend)
    }

    suspend fun markStoryViewed(storyId: Long) {
        dao.markStoryViewed(storyId)
    }

    fun getComments(targetType: String, targetId: Long): Flow<List<CommentEntity>> {
        return dao.getComments(targetType, targetId)
    }

    suspend fun addComment(targetType: String, targetId: Long, username: String, avatarUrl: String, text: String) {
        val comment = CommentEntity(
            targetType = targetType,
            targetId = targetId,
            username = username,
            userAvatar = avatarUrl,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(comment)
    }

    suspend fun toggleCommentLike(commentId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updateCommentLike(commentId, !currentLiked, delta)
    }

    suspend fun createPost(post: PostEntity): Long {
        return dao.insertPost(post)
    }

    suspend fun deletePost(postId: Long) {
        dao.deletePost(postId)
    }

    suspend fun createReel(reel: ReelEntity): Long {
        return dao.insertReel(reel)
    }

    suspend fun updateReelDetails(id: Long, caption: String, location: String?, landmark: String?) {
        dao.updateReelDetails(id, caption, location, landmark)
    }

    suspend fun deleteReel(reelId: Long) {
        dao.deleteReel(reelId)
    }

    suspend fun createStory(story: StoryEntity): Long {
        return dao.insertStory(story)
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    fun getChatMessages(conversationId: String): Flow<List<ChatMessageEntity>> {
        return dao.getChatMessages(conversationId)
    }

    suspend fun sendChatMessage(
        conversationId: String,
        senderUsername: String,
        senderAvatar: String,
        text: String,
        sharedMediaUrl: String? = null,
        sharedCaption: String? = null
    ) {
        val message = ChatMessageEntity(
            conversationId = conversationId,
            senderUsername = senderUsername,
            senderAvatar = senderAvatar,
            text = text,
            sharedMediaUrl = sharedMediaUrl,
            sharedCaption = sharedCaption,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        dao.insertChatMessage(message)

        // Update conversation last message
        val existing = dao.getConversationById(conversationId)
        if (existing != null) {
            dao.insertConversation(
                existing.copy(
                    lastMessage = if (sharedMediaUrl != null) "Shared a post: $text" else text,
                    timestamp = System.currentTimeMillis(),
                    isFromMe = true
                )
            )
        }
    }

    suspend fun createOrGetConversation(
        contactUsername: String,
        contactAvatar: String,
        landmark: String? = null,
        distanceKm: Double? = null
    ): String {
        val convId = "dm_$contactUsername"
        val existing = dao.getConversationById(convId)
        if (existing == null) {
            dao.insertConversation(
                DirectMessageEntity(
                    conversationId = convId,
                    contactUsername = contactUsername,
                    contactAvatar = contactAvatar,
                    lastMessage = "Started a conversation",
                    timestamp = System.currentTimeMillis(),
                    isFromMe = true,
                    isRead = true,
                    isOnline = true,
                    isGroup = false,
                    distanceKm = distanceKm,
                    landmark = landmark
                )
            )
        }
        return convId
    }

    suspend fun createGroupConversation(
        title: String,
        memberCount: Int,
        landmark: String? = null
    ): String {
        val convId = "group_${System.currentTimeMillis()}"
        dao.insertConversation(
            DirectMessageEntity(
                conversationId = convId,
                contactUsername = title,
                contactAvatar = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=500&auto=format&fit=crop&q=80",
                lastMessage = "Group created",
                timestamp = System.currentTimeMillis(),
                isFromMe = true,
                isRead = true,
                isOnline = true,
                isGroup = true,
                groupMembersCount = memberCount,
                distanceKm = 0.5,
                landmark = landmark ?: "Local Community"
            )
        )
        return convId
    }

    suspend fun sendNeighborWaveNotification(targetUsername: String, distanceKm: Double) {
        dao.insertNotification(
            NotificationEntity(
                username = targetUsername,
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                actionType = "NEARBY_WAVE",
                content = "You waved at $targetUsername (in your locality)! 👋",
                distanceKm = distanceKm,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun markNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun savePrivacySettings(settings: PrivacySettingsEntity) {
        dao.insertOrUpdatePrivacySettings(settings)
    }

    suspend fun updateLocationRadar(enabled: Boolean) {
        dao.updateLocationRadar(enabled)
    }

    suspend fun updatePrivateAccount(isPrivate: Boolean) {
        dao.updatePrivateAccount(isPrivate)
    }

    suspend fun updateNearbyDiscovery(allow: Boolean) {
        dao.updateNearbyDiscovery(allow)
    }

    suspend fun updatePreciseLocation(precise: Boolean) {
        dao.updatePreciseLocation(precise)
    }

    suspend fun updateNearbyWaves(allow: Boolean) {
        dao.updateNearbyWaves(allow)
    }

    suspend fun updateActiveStatus(show: Boolean) {
        dao.updateActiveStatus(show)
    }

    suspend fun updateReadReceipts(enabled: Boolean) {
        dao.updateReadReceipts(enabled)
    }

    suspend fun updateCommentsPrivacy(option: String) {
        dao.updateCommentsPrivacy(option)
    }

    suspend fun updateDirectMessagesPrivacy(option: String) {
        dao.updateDirectMessagesPrivacy(option)
    }

    suspend fun updateTagsAndMentionsPrivacy(option: String) {
        dao.updateTagsAndMentionsPrivacy(option)
    }

    suspend fun updateHideMomentsFromStrangers(hide: Boolean) {
        dao.updateHideMomentsFromStrangers(hide)
    }

    suspend fun updatePostResharing(allow: Boolean) {
        dao.updatePostResharing(allow)
    }

    suspend fun updateSensitiveContentFilter(filter: String) {
        dao.updateSensitiveContentFilter(filter)
    }

    // --- Marketplace ---
    fun getMarketplaceItemsByCategory(category: String): Flow<List<MarketplaceItemEntity>> {
        return dao.getMarketplaceItemsByCategory(category)
    }

    suspend fun createMarketplaceItem(item: MarketplaceItemEntity): Long {
        return dao.insertMarketplaceItem(item)
    }

    suspend fun toggleMarketItemSaved(itemId: Long, currentSaved: Boolean) {
        dao.updateMarketplaceItemSaved(itemId, !currentSaved)
    }

    suspend fun updateMarketItemAvailability(itemId: Long, isAvailable: Boolean) {
        dao.updateMarketplaceItemAvailability(itemId, isAvailable)
    }

    suspend fun updateMarketplaceItem(item: MarketplaceItemEntity) {
        dao.updateMarketplaceItem(item)
    }

    suspend fun deleteMarketplaceItem(itemId: Long) {
        dao.deleteMarketplaceItem(itemId)
    }
}


