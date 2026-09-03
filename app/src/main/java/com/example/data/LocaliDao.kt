package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocaliDao {
    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isFollowing = 1 OR isNeighbor = 1 ORDER BY timestamp DESC")
    fun getFeedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?

    @Query("SELECT * FROM posts WHERE username = :username ORDER BY timestamp DESC")
    fun getPostsByUsername(username: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updatePostLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :id")
    suspend fun updatePostSaved(id: Long, isSaved: Boolean)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePost(id: Long)

    // --- Reels ---
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE distanceKm <= :maxKm ORDER BY distanceKm ASC, timestamp DESC")
    fun getNearbyReels(maxKm: Double): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE username = :username ORDER BY timestamp DESC")
    fun getReelsByUsername(username: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE id = :id")
    suspend fun getReelById(id: Long): ReelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateReelLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE reels SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateReelSaved(id: Long, isSaved: Boolean)

    @Query("UPDATE reels SET isFollowing = :isFollowing WHERE id = :id")
    suspend fun updateReelFollowing(id: Long, isFollowing: Boolean)

    @Query("UPDATE reels SET caption = :caption, location = :location, landmark = :landmark WHERE id = :id")
    suspend fun updateReelDetails(id: Long, caption: String, location: String?, landmark: String?)

    @Query("DELETE FROM reels WHERE id = :id")
    suspend fun deleteReel(id: Long)

    // --- Stories ---
    @Query("SELECT * FROM stories ORDER BY isUserStory DESC, timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :id")
    suspend fun markStoryViewed(id: Long)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE targetType = :targetType AND targetId = :targetId ORDER BY timestamp ASC")
    fun getComments(targetType: String, targetId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateCommentLike(id: Long, isLiked: Boolean, delta: Int)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // --- Other Users ---
    @Query("SELECT * FROM other_users ORDER BY distanceKm ASC")
    fun getAllOtherUsers(): Flow<List<OtherUserEntity>>

    @Query("SELECT * FROM other_users WHERE username = :username")
    fun getOtherUser(username: String): Flow<OtherUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherUsers(users: List<OtherUserEntity>)

    @Query("UPDATE other_users SET isFollowing = :isFollowing, followersCount = followersCount + :delta WHERE username = :username")
    suspend fun updateOtherUserFollowing(username: String, isFollowing: Boolean, delta: Int)

    @Query("UPDATE other_users SET isFriend = :isFriend WHERE username = :username")
    suspend fun updateOtherUserFriend(username: String, isFriend: Boolean)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // --- Conversations & Messages ---
    @Query("SELECT * FROM conversations ORDER BY timestamp DESC")
    fun getConversations(): Flow<List<DirectMessageEntity>>

    @Query("SELECT * FROM conversations WHERE conversationId = :id")
    suspend fun getConversationById(id: String): DirectMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<DirectMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: DirectMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getChatMessages(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    // --- Privacy Settings ---
    @Query("SELECT * FROM privacy_settings WHERE id = 1 LIMIT 1")
    fun getPrivacySettings(): Flow<PrivacySettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrivacySettings(settings: PrivacySettingsEntity)

    @Query("UPDATE privacy_settings SET isLocationRadarEnabled = :enabled WHERE id = 1")
    suspend fun updateLocationRadar(enabled: Boolean)

    @Query("UPDATE privacy_settings SET isPrivateAccount = :isPrivate WHERE id = 1")
    suspend fun updatePrivateAccount(isPrivate: Boolean)

    @Query("UPDATE privacy_settings SET allowNearbyDiscovery = :allow WHERE id = 1")
    suspend fun updateNearbyDiscovery(allow: Boolean)

    @Query("UPDATE privacy_settings SET preciseLocationSharing = :precise WHERE id = 1")
    suspend fun updatePreciseLocation(precise: Boolean)

    @Query("UPDATE privacy_settings SET allowNearbyWaves = :allow WHERE id = 1")
    suspend fun updateNearbyWaves(allow: Boolean)

    @Query("UPDATE privacy_settings SET showActiveStatus = :show WHERE id = 1")
    suspend fun updateActiveStatus(show: Boolean)

    @Query("UPDATE privacy_settings SET readReceiptsEnabled = :enabled WHERE id = 1")
    suspend fun updateReadReceipts(enabled: Boolean)

    @Query("UPDATE privacy_settings SET allowCommentsFrom = :option WHERE id = 1")
    suspend fun updateCommentsPrivacy(option: String)

    @Query("UPDATE privacy_settings SET allowDirectMessagesFrom = :option WHERE id = 1")
    suspend fun updateDirectMessagesPrivacy(option: String)

    @Query("UPDATE privacy_settings SET allowTagsAndMentions = :option WHERE id = 1")
    suspend fun updateTagsAndMentionsPrivacy(option: String)

    @Query("UPDATE privacy_settings SET hideMomentsFromStrangers = :hide WHERE id = 1")
    suspend fun updateHideMomentsFromStrangers(hide: Boolean)

    @Query("UPDATE privacy_settings SET allowPostResharing = :allow WHERE id = 1")
    suspend fun updatePostResharing(allow: Boolean)

    @Query("UPDATE privacy_settings SET sensitiveContentFilter = :filter WHERE id = 1")
    suspend fun updateSensitiveContentFilter(filter: String)

    // --- Marketplace Items ---
    @Query("SELECT * FROM marketplace_items ORDER BY timestamp DESC")
    fun getAllMarketplaceItems(): Flow<List<MarketplaceItemEntity>>

    @Query("SELECT * FROM marketplace_items WHERE category = :category ORDER BY timestamp DESC")
    fun getMarketplaceItemsByCategory(category: String): Flow<List<MarketplaceItemEntity>>

    @Query("SELECT * FROM marketplace_items WHERE id = :id")
    suspend fun getMarketplaceItemById(id: Long): MarketplaceItemEntity?

    @Query("SELECT * FROM marketplace_items WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedMarketplaceItems(): Flow<List<MarketplaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItem(item: MarketplaceItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItems(items: List<MarketplaceItemEntity>)

    @Update
    suspend fun updateMarketplaceItem(item: MarketplaceItemEntity)

    @Query("UPDATE marketplace_items SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateMarketplaceItemSaved(id: Long, isSaved: Boolean)

    @Query("UPDATE marketplace_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateMarketplaceItemAvailability(id: Long, isAvailable: Boolean)

    @Query("DELETE FROM marketplace_items WHERE id = :id")
    suspend fun deleteMarketplaceItem(id: Long)

    // --- Localiiiy Studio Long Videos ---
    @Query("SELECT * FROM studio_videos ORDER BY timestamp DESC")
    fun getAllStudioVideos(): Flow<List<StudioVideoEntity>>

    @Query("SELECT * FROM studio_videos WHERE category = :category ORDER BY timestamp DESC")
    fun getStudioVideosByCategory(category: String): Flow<List<StudioVideoEntity>>

    @Query("SELECT * FROM studio_videos WHERE id = :id")
    suspend fun getStudioVideoById(id: Long): StudioVideoEntity?

    @Query("SELECT * FROM studio_videos WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedStudioVideos(): Flow<List<StudioVideoEntity>>

    @Query("SELECT * FROM studio_videos WHERE creatorUsername = :username ORDER BY timestamp DESC")
    fun getStudioVideosByCreator(username: String): Flow<List<StudioVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudioVideo(video: StudioVideoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudioVideos(videos: List<StudioVideoEntity>)

    @Update
    suspend fun updateStudioVideo(video: StudioVideoEntity)

    @Query("UPDATE studio_videos SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateStudioVideoLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE studio_videos SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateStudioVideoSaved(id: Long, isSaved: Boolean)

    @Query("UPDATE studio_videos SET isSubscribed = :isSubscribed WHERE creatorUsername = :creatorUsername")
    suspend fun updateStudioCreatorSubscribed(creatorUsername: String, isSubscribed: Boolean)

    @Query("UPDATE studio_videos SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementStudioVideoViews(id: Long)

    @Query("DELETE FROM studio_videos WHERE id = :id")
    suspend fun deleteStudioVideo(id: Long)

    // --- Local Cache: User Activity ---
    @Query("SELECT * FROM user_activity_cache ORDER BY timestamp DESC")
    fun getAllUserActivities(): Flow<List<UserActivityEntity>>

    @Query("SELECT * FROM user_activity_cache ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentUserActivities(limit: Int = 20): Flow<List<UserActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserActivity(activity: UserActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserActivities(activities: List<UserActivityEntity>)

    @Query("DELETE FROM user_activity_cache WHERE id = :id")
    suspend fun deleteUserActivity(id: Long)

    @Query("DELETE FROM user_activity_cache")
    suspend fun clearUserActivities()

    // --- Local Cache: Saved Posts ---
    @Query("SELECT * FROM saved_posts_cache ORDER BY savedTimestamp DESC")
    fun getAllSavedPostsCache(): Flow<List<SavedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPostCache(savedPost: SavedPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPostsCache(savedPosts: List<SavedPostEntity>)

    @Query("DELETE FROM saved_posts_cache WHERE postId = :postId")
    suspend fun deleteSavedPostCache(postId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_posts_cache WHERE postId = :postId)")
    fun isPostInSavedCache(postId: Long): Flow<Boolean>

    // --- Local Cache: Studio Drafts ---
    @Query("SELECT * FROM studio_drafts_cache ORDER BY lastEditedTimestamp DESC")
    fun getAllStudioDrafts(): Flow<List<StudioDraftEntity>>

    @Query("SELECT * FROM studio_drafts_cache WHERE id = :id")
    suspend fun getStudioDraftById(id: Long): StudioDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStudioDraft(draft: StudioDraftEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudioDrafts(drafts: List<StudioDraftEntity>)

    @Query("DELETE FROM studio_drafts_cache WHERE id = :id")
    suspend fun deleteStudioDraft(id: Long)

    @Query("DELETE FROM studio_drafts_cache")
    suspend fun clearStudioDrafts()
}


