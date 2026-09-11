package com.example.data

import kotlinx.coroutines.flow.Flow

class LocaliiiyRepository(private val dao: LocaliiiyDao) {

    val allPulseCache: Flow<List<PulseCacheEntity>> = dao.getAllPulseCache()

    suspend fun refreshPulseCache(pulses: List<PulseCacheEntity>) {
        dao.clearPulseCache()
        dao.insertPulseCache(pulses)
    }

    val allPosts: Flow<List<PostEntity>> = dao.getAllPosts()
    val feedPosts: Flow<List<PostEntity>> = dao.getFeedPosts()
    val savedPosts: Flow<List<PostEntity>> = dao.getSavedPosts()
    val allClips: Flow<List<ClipEntity>> = dao.getAllClips()
    val allStories: Flow<List<StoryEntity>> = dao.getAllStories()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val otherUsers: Flow<List<OtherUserEntity>> = dao.getAllOtherUsers()
    val notifications: Flow<List<NotificationEntity>> = dao.getNotifications()
    val conversations: Flow<List<DirectMessageEntity>> = dao.getConversations()
    val privacySettings: Flow<PrivacySettingsEntity?> = dao.getPrivacySettings()
    val allMarketplaceItems: Flow<List<MarketplaceItemEntity>> = dao.getAllMarketplaceItems()
    val savedMarketplaceItems: Flow<List<MarketplaceItemEntity>> = dao.getSavedMarketplaceItems()
    val allStudioVideos: Flow<List<StudioVideoEntity>> = dao.getAllStudioVideos()
    val savedStudioVideos: Flow<List<StudioVideoEntity>> = dao.getSavedStudioVideos()

    fun getPostsByUsername(username: String): Flow<List<PostEntity>> {
        return dao.getPostsByUsername(username)
    }

    fun getClipsByUsername(username: String): Flow<List<ClipEntity>> {
        return dao.getClipsByUsername(username)
    }

    fun getOtherUser(username: String): Flow<OtherUserEntity?> {
        return dao.getOtherUser(username)
    }

    fun getNearbyClips(maxKm: Double): Flow<List<ClipEntity>> {
        return dao.getNearbyClips(maxKm)
    }

    suspend fun togglePostLike(postId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updatePostLike(postId, !currentLiked, delta)
    }

    suspend fun togglePostSave(postId: Long, currentSaved: Boolean) {
        dao.updatePostSaved(postId, !currentSaved)
    }

    suspend fun toggleClipLike(clipId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updateClipLike(clipId, !currentLiked, delta)
    }

    suspend fun toggleClipSave(clipId: Long, currentSaved: Boolean) {
        dao.updateClipSaved(clipId, !currentSaved)
    }

    suspend fun toggleClipFollow(clipId: Long, currentFollowing: Boolean) {
        dao.updateClipFollowing(clipId, !currentFollowing)
    }

    suspend fun toggleClipConnect(clipId: Long, currentConnected: Boolean) {
        toggleClipFollow(clipId, currentConnected)
    }

    suspend fun toggleOtherUserFollow(username: String, currentFollowing: Boolean) {
        val delta = if (currentFollowing) -1 else 1
        dao.updateOtherUserFollowing(username, !currentFollowing, delta)
    }

    suspend fun toggleOtherUserConnect(username: String, currentConnected: Boolean) {
        toggleOtherUserFollow(username, currentConnected)
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

    suspend fun createClip(clip: ClipEntity): Long {
        return dao.insertClip(clip)
    }

    suspend fun updateClipDetails(id: Long, caption: String, location: String?, landmark: String?) {
        dao.updateClipDetails(id, caption, location, landmark)
    }

    suspend fun deleteClip(clipId: Long) {
        dao.deleteClip(clipId)
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

    suspend fun sendHotspotNotification(message: String) {
        dao.insertNotification(
            NotificationEntity(
                username = "Neighborhood Hotspot 🚨",
                userAvatar = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=500&auto=format&fit=crop&q=80",
                actionType = "HOTSPOT",
                content = message,
                timestamp = System.currentTimeMillis()
            )
        )
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

    // --- Studio Long Videos ---
    fun getStudioVideosByCategory(category: String): Flow<List<StudioVideoEntity>> {
        return dao.getStudioVideosByCategory(category)
    }

    suspend fun createStudioVideo(video: StudioVideoEntity): Long {
        return dao.insertStudioVideo(video)
    }

    suspend fun toggleStudioVideoLike(videoId: Long, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updateStudioVideoLike(videoId, !currentLiked, delta)
    }

    suspend fun toggleStudioVideoSave(videoId: Long, currentSaved: Boolean) {
        dao.updateStudioVideoSaved(videoId, !currentSaved)
    }

    suspend fun toggleStudioCreatorSubscription(creatorUsername: String, isSubscribed: Boolean) {
        dao.updateStudioCreatorSubscribed(creatorUsername, !isSubscribed)
    }

    suspend fun toggleStudioCreatorConnect(creatorUsername: String, isConnected: Boolean) {
        toggleStudioCreatorSubscription(creatorUsername, isConnected)
    }

    suspend fun recordStudioVideoView(videoId: Long) {
        dao.incrementStudioVideoViews(videoId)
    }

    suspend fun deleteStudioVideo(videoId: Long) {
        dao.deleteStudioVideo(videoId)
    }

    // --- Local Room Cache: User Activity ---
    val allUserActivities: Flow<List<UserActivityEntity>> = dao.getAllUserActivities()

    fun getRecentUserActivities(limit: Int = 20): Flow<List<UserActivityEntity>> {
        return dao.getRecentUserActivities(limit)
    }

    suspend fun cacheUserActivity(activity: UserActivityEntity): Long {
        return dao.insertUserActivity(activity)
    }

    suspend fun deleteUserActivity(id: Long) {
        dao.deleteUserActivity(id)
    }

    suspend fun clearUserActivities() {
        dao.clearUserActivities()
    }

    // --- Local Room Cache: Saved Posts ---
    val allSavedPostsCache: Flow<List<SavedPostEntity>> = dao.getAllSavedPostsCache()

    suspend fun cacheSavedPost(post: PostEntity, collection: String = "All Saved") {
        dao.insertSavedPostCache(
            SavedPostEntity(
                postId = post.id,
                username = post.username,
                userAvatar = post.userAvatar,
                userHandle = post.userHandle,
                mediaUrl = post.mediaUrl,
                mediaType = post.mediaType,
                caption = post.caption,
                location = post.location,
                landmark = post.landmark,
                distanceKm = post.distanceKm,
                likesCount = post.likesCount,
                collectionName = collection,
                savedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeSavedPostFromCache(postId: Long) {
        dao.deleteSavedPostCache(postId)
    }

    fun isPostInSavedCache(postId: Long): Flow<Boolean> {
        return dao.isPostInSavedCache(postId)
    }

    // --- Local Room Cache: Localiiiy Studio Drafts ---
    val allStudioDrafts: Flow<List<StudioDraftEntity>> = dao.getAllStudioDrafts()

    suspend fun getStudioDraftById(id: Long): StudioDraftEntity? {
        return dao.getStudioDraftById(id)
    }

    suspend fun saveStudioDraft(draft: StudioDraftEntity): Long {
        return dao.insertOrUpdateStudioDraft(draft)
    }

    suspend fun deleteStudioDraft(id: Long) {
        dao.deleteStudioDraft(id)
    }

    suspend fun clearStudioDrafts() {
        dao.clearStudioDrafts()
    }




    // --- Draft Clips (Creator Clips) ---
    val allDraftClips: Flow<List<DraftClipEntity>> = dao.getAllDrafts()
    
    suspend fun saveDraftClip(draft: DraftClipEntity) {
        dao.insertDraft(draft)
    }
    
    suspend fun deleteDraftClip(draft: DraftClipEntity) {
        dao.deleteDraft(draft)
    }

    // --- Cyberstalking Immutable Evidence Vault (Universal Anti-Stalking Protocol) ---
    val allCyberstalkingIncidents: Flow<List<CyberstalkingIncidentEntity>> = dao.getAllCyberstalkingIncidents()
    val cyberstalkingIncidentCount: Flow<Int> = dao.getCyberstalkingIncidentCount()

    suspend fun getCyberstalkingIncidentById(id: String): CyberstalkingIncidentEntity? {
        return dao.getCyberstalkingIncidentById(id)
    }

    suspend fun recordCyberstalkingIncident(incident: CyberstalkingIncidentEntity) {
        dao.recordCyberstalkingIncident(incident)
    }
}
