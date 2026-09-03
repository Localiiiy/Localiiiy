package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.CurrencyHelper
import com.example.util.LocaliCurrency
import com.example.util.LocaliLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocationHelper
import com.example.util.UserLocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainNavigationTab {
    FEED,
    EXPLORE,
    MARKET,
    STUDIO,
    CREATE,
    REELS,
    PROFILE
}

enum class ProfileTab {
    POSTS,
    REELS,
    PROXIMITY,
    SAVED,
    TAGGED
}

enum class CreationMode {
    POST,
    REEL,
    STORY
}

data class FilterPreset(
    val name: String,
    val colorOverlayHex: Long,
    val alpha: Float,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f
)

val PhotoFilters = listOf(
    FilterPreset("Normal", 0x00000000, 0f, 1.0f, 1.0f),
    FilterPreset("Clarendon", 0x220077FF, 0.15f, 1.2f, 1.25f),
    FilterPreset("Juno", 0x22FF5500, 0.15f, 1.15f, 1.3f),
    FilterPreset("Valencia", 0x22FFAA33, 0.20f, 1.05f, 0.95f),
    FilterPreset("Moon", 0x33000000, 0.35f, 1.3f, 0.0f),
    FilterPreset("Lark", 0x1533CCFF, 0.15f, 1.1f, 1.15f),
    FilterPreset("Vintage", 0x228B5A2B, 0.25f, 1.1f, 0.85f),
    FilterPreset("Neon", 0x22FF007F, 0.20f, 1.3f, 1.4f)
)

class LocaliViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = LocaliRepository(database.localiDao())

    private val _blockedUsernames = MutableStateFlow<Set<String>>(setOf("spam_bot_99", "crypto_promos"))
    val blockedUsernames: StateFlow<Set<String>> = _blockedUsernames.asStateFlow()

    private val _reportedContentRecords = MutableStateFlow<List<String>>(emptyList())
    val reportedContentRecords: StateFlow<List<String>> = _reportedContentRecords.asStateFlow()

    val allPosts: StateFlow<List<PostEntity>> = repository.allPosts
        .combine(_blockedUsernames) { posts, blocked -> posts.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedPosts: StateFlow<List<PostEntity>> = repository.feedPosts
        .combine(_blockedUsernames) { posts, blocked -> posts.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReels: StateFlow<List<ReelEntity>> = repository.allReels
        .combine(_blockedUsernames) { reels, blocked -> reels.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<StoryEntity>> = repository.allStories
        .combine(_blockedUsernames) { stories, blocked -> stories.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostEntity>> = repository.savedPosts
        .combine(_blockedUsernames) { posts, blocked -> posts.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .map { it ?: InitialData.defaultProfile }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.defaultProfile)

    val otherUsers: StateFlow<List<OtherUserEntity>> = repository.otherUsers
        .combine(_blockedUsernames) { users, blocked -> users.filter { it.username !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<DirectMessageEntity>> = repository.conversations
        .combine(_blockedUsernames) { convos, blocked -> convos.filter { it.contactUsername !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val privacySettings: StateFlow<PrivacySettingsEntity> = repository.privacySettings
        .map { it ?: InitialData.defaultPrivacySettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.defaultPrivacySettings)

    val allMarketplaceItems: StateFlow<List<MarketplaceItemEntity>> = repository.allMarketplaceItems
        .combine(_blockedUsernames) { items, blocked -> items.filter { it.sellerUsername !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedMarketplaceItems: StateFlow<List<MarketplaceItemEntity>> = repository.savedMarketplaceItems
        .combine(_blockedUsernames) { items, blocked -> items.filter { it.sellerUsername !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Localiiiy Studio Flows ---
    val allStudioVideos: StateFlow<List<StudioVideoEntity>> = repository.allStudioVideos
        .combine(_blockedUsernames) { videos, blocked -> videos.filter { it.creatorUsername !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedStudioVideos: StateFlow<List<StudioVideoEntity>> = repository.savedStudioVideos
        .combine(_blockedUsernames) { videos, blocked -> videos.filter { it.creatorUsername !in blocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStudioCategory = MutableStateFlow("All")
    val selectedStudioCategory: StateFlow<String> = _selectedStudioCategory.asStateFlow()

    private val _studioSearchQuery = MutableStateFlow("")
    val studioSearchQuery: StateFlow<String> = _studioSearchQuery.asStateFlow()

    private val _activeStudioVideo = MutableStateFlow<StudioVideoEntity?>(null)
    val activeStudioVideo: StateFlow<StudioVideoEntity?> = _activeStudioVideo.asStateFlow()

    private val _isStudioVideoPlaying = MutableStateFlow(true)
    val isStudioVideoPlaying: StateFlow<Boolean> = _isStudioVideoPlaying.asStateFlow()

    private val _studioPlaybackProgress = MutableStateFlow(0.12f)
    val studioPlaybackProgress: StateFlow<Float> = _studioPlaybackProgress.asStateFlow()

    private val _isStudioUploadSheetOpen = MutableStateFlow(false)
    val isStudioUploadSheetOpen: StateFlow<Boolean> = _isStudioUploadSheetOpen.asStateFlow()

    private val _showStudioCreatorDashboard = MutableStateFlow(false)
    val showStudioCreatorDashboard: StateFlow<Boolean> = _showStudioCreatorDashboard.asStateFlow()

    // --- Room Database Local Cache Flows (User Activities, Saved Posts, Studio Drafts) ---
    val allUserActivities: StateFlow<List<UserActivityEntity>> = repository.allUserActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSavedPostsCache: StateFlow<List<SavedPostEntity>> = repository.allSavedPostsCache
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudioDrafts: StateFlow<List<StudioDraftEntity>> = repository.allStudioDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Main Pulse Feed Pull-to-Refresh State ---
    private val _isRefreshingFeed = MutableStateFlow(false)
    val isRefreshingFeed: StateFlow<Boolean> = _isRefreshingFeed.asStateFlow()

    // --- Marketplace UI & Filter states ---
    private val _selectedMarketCategory = MutableStateFlow("All")
    val selectedMarketCategory: StateFlow<String> = _selectedMarketCategory.asStateFlow()

    private val _marketplaceSearchQuery = MutableStateFlow("")
    val marketplaceSearchQuery: StateFlow<String> = _marketplaceSearchQuery.asStateFlow()

    private val _marketplaceLocationQuery = MutableStateFlow("")
    val marketplaceLocationQuery: StateFlow<String> = _marketplaceLocationQuery.asStateFlow()

    private val _marketplaceRadiusKm = MutableStateFlow<Double?>(null)
    val marketplaceRadiusKm: StateFlow<Double?> = _marketplaceRadiusKm.asStateFlow()

    private val _selectedMarketplaceItem = MutableStateFlow<MarketplaceItemEntity?>(null)
    val selectedMarketplaceItem: StateFlow<MarketplaceItemEntity?> = _selectedMarketplaceItem.asStateFlow()

    private val _showSellItemDialog = MutableStateFlow(false)
    val showSellItemDialog: StateFlow<Boolean> = _showSellItemDialog.asStateFlow()

    // --- Navigation and UI states ---
    private val _currentTab = MutableStateFlow(MainNavigationTab.FEED)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    private val _showPrivacySettings = MutableStateFlow(false)
    val showPrivacySettings: StateFlow<Boolean> = _showPrivacySettings.asStateFlow()

    private val _showLegalAgreement = MutableStateFlow(false)
    val showLegalAgreement: StateFlow<Boolean> = _showLegalAgreement.asStateFlow()

    private val _showSignUpDialog = MutableStateFlow(false)
    val showSignUpDialog: StateFlow<Boolean> = _showSignUpDialog.asStateFlow()

    private val _legalConsentRecord = MutableStateFlow(com.example.data.legal.LegalPolicyRepository.defaultConsentRecord)
    val legalConsentRecord: StateFlow<com.example.data.legal.LegalConsentRecord> = _legalConsentRecord.asStateFlow()

    private val _activeStory = MutableStateFlow<StoryEntity?>(null)
    val activeStory: StateFlow<StoryEntity?> = _activeStory.asStateFlow()

    private val _activeCommentsTarget = MutableStateFlow<Pair<String, Long>?>(null)
    val activeCommentsTarget: StateFlow<Pair<String, Long>?> = _activeCommentsTarget.asStateFlow()

    private val _showNotificationsSheet = MutableStateFlow(false)
    val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

    private val _showDirectMessagesSheet = MutableStateFlow(false)
    val showDirectMessagesSheet: StateFlow<Boolean> = _showDirectMessagesSheet.asStateFlow()

    private val _activeSharePost = MutableStateFlow<PostEntity?>(null)
    val activeSharePost: StateFlow<PostEntity?> = _activeSharePost.asStateFlow()

    private val _isSoundMuted = MutableStateFlow(false)
    val isSoundMuted: StateFlow<Boolean> = _isSoundMuted.asStateFlow()

    private val _profileTab = MutableStateFlow(ProfileTab.POSTS)
    val profileTab: StateFlow<ProfileTab> = _profileTab.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()

    private val _selectedExplorePost = MutableStateFlow<PostEntity?>(null)
    val selectedExplorePost: StateFlow<PostEntity?> = _selectedExplorePost.asStateFlow()

    // --- Location & Nearby Radar State ---
    private val _currentLocation = MutableStateFlow<UserLocationData?>(null)
    val currentLocation: StateFlow<UserLocationData?> = _currentLocation.asStateFlow()

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()

    // Privacy toggles: user controls whether location radar is enabled and if account is private (Ghost mode)
    private val _isLocationEnabled = MutableStateFlow(true)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()

    private val _isPrivateAccount = MutableStateFlow(false)
    val isPrivateAccount: StateFlow<Boolean> = _isPrivateAccount.asStateFlow()

    // Selected nearby radius filter in KM (null = All)
    private val _nearbyRadiusKm = MutableStateFlow<Double?>(null)
    val nearbyRadiusKm: StateFlow<Double?> = _nearbyRadiusKm.asStateFlow()

    // --- Other User Profile View State ---
    private val _selectedOtherUser = MutableStateFlow<OtherUserEntity?>(null)
    val selectedOtherUser: StateFlow<OtherUserEntity?> = _selectedOtherUser.asStateFlow()

    // --- Active Chat / Direct Message Conversation State ---
    private val _activeConversation = MutableStateFlow<DirectMessageEntity?>(null)
    val activeConversation: StateFlow<DirectMessageEntity?> = _activeConversation.asStateFlow()

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeConversation
        .flatMapLatest { conv ->
            if (conv != null) {
                repository.getChatMessages(conv.conversationId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Creation State ---
    private val _creationMode = MutableStateFlow(CreationMode.POST)
    val creationMode: StateFlow<CreationMode> = _creationMode.asStateFlow()

    private val _selectedFilter = MutableStateFlow(PhotoFilters[0])
    val selectedFilter: StateFlow<FilterPreset> = _selectedFilter.asStateFlow()

    private val _selectedMediaUri = MutableStateFlow("https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1080&auto=format&fit=crop&q=85")
    val selectedMediaUri: StateFlow<String> = _selectedMediaUri.asStateFlow()

    private val _autoDetectedLocation = MutableStateFlow<UserLocationData?>(null)
    val autoDetectedLocation: StateFlow<UserLocationData?> = _autoDetectedLocation.asStateFlow()

    // --- Worldwide Currency & Multi-Language Localization State ---
    private val _currentCurrency = MutableStateFlow(LocaliCurrency.USD)
    val currentCurrency: StateFlow<LocaliCurrency> = _currentCurrency.asStateFlow()

    private val _currentLanguage = MutableStateFlow(LocaliLanguage.EN)
    val currentLanguage: StateFlow<LocaliLanguage> = _currentLanguage.asStateFlow()

    private val _showLanguageCurrencyDialog = MutableStateFlow(false)
    val showLanguageCurrencyDialog: StateFlow<Boolean> = _showLanguageCurrencyDialog.asStateFlow()

    // --- Worldwide Creator Monetization, Ads & Global Payout State ---
    private val _creatorEarnings = MutableStateFlow(CreatorEarningsSummary())
    val creatorEarnings: StateFlow<CreatorEarningsSummary> = _creatorEarnings.asStateFlow()

    private val _payoutAccount = MutableStateFlow(CreatorPayoutAccount())
    val payoutAccount: StateFlow<CreatorPayoutAccount> = _payoutAccount.asStateFlow()

    private val _payoutHistory = MutableStateFlow(StarterMonetizationData.starterPayoutHistory)
    val payoutHistory: StateFlow<List<PayoutTransaction>> = _payoutHistory.asStateFlow()

    private val _platformMetrics = MutableStateFlow(PlatformAdRevenueMetrics())
    val platformMetrics: StateFlow<PlatformAdRevenueMetrics> = _platformMetrics.asStateFlow()

    private val _sponsoredAds = MutableStateFlow(StarterMonetizationData.sampleSponsoredAds)
    val sponsoredAds: StateFlow<List<AdPlacement>> = _sponsoredAds.asStateFlow()

    private val _showMonetizationHub = MutableStateFlow(false)
    val showMonetizationHub: StateFlow<Boolean> = _showMonetizationHub.asStateFlow()

    private val _showBoostAdDialog = MutableStateFlow(false)
    val showBoostAdDialog: StateFlow<Boolean> = _showBoostAdDialog.asStateFlow()

    init {
        // Initial DB population & location detection
        viewModelScope.launch(Dispatchers.IO) {
            val dao = database.localiDao()
            if (dao.getPostById(1) == null) {
                dao.insertOrUpdateProfile(InitialData.defaultProfile)
                dao.insertStories(InitialData.starterStories)
                dao.insertPosts(InitialData.starterPosts)
                dao.insertReels(InitialData.starterReels)
                dao.insertOtherUsers(InitialData.starterOtherUsers)
                dao.insertComments(InitialData.starterComments)
                dao.insertNotifications(InitialData.starterNotifications)
                dao.insertConversations(InitialData.starterConversations)
                dao.insertChatMessages(InitialData.starterChatMessages)
                dao.insertOrUpdatePrivacySettings(InitialData.defaultPrivacySettings)
            }
        }
        viewModelScope.launch {
            repository.privacySettings.collect { settings ->
                if (settings != null) {
                    _isLocationEnabled.value = settings.isLocationRadarEnabled
                    _isPrivateAccount.value = settings.isPrivateAccount
                }
            }
        }
        detectCurrentLocation(application.applicationContext)
    }

    fun detectCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isDetectingLocation.value = true
            try {
                val loc = LocationHelper.getCurrentLocation(context)
                _currentLocation.value = loc
                _autoDetectedLocation.value = loc
            } catch (_: Exception) {
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }

    fun setNearbyRadiusFilter(radiusKm: Double?) {
        _nearbyRadiusKm.value = radiusKm
    }

    fun setLocationEnabled(enabled: Boolean) {
        _isLocationEnabled.value = enabled
        viewModelScope.launch {
            repository.updateLocationRadar(enabled)
        }
    }

    fun setPrivateAccount(isPrivate: Boolean) {
        _isPrivateAccount.value = isPrivate
        viewModelScope.launch {
            repository.updatePrivateAccount(isPrivate)
        }
    }

    fun openPrivacySettings() {
        _showPrivacySettings.value = true
    }

    fun closePrivacySettings() {
        _showPrivacySettings.value = false
    }

    fun openLegalAgreement() {
        _showLegalAgreement.value = true
    }

    fun closeLegalAgreement() {
        _showLegalAgreement.value = false
    }

    fun acceptLegalAgreement() {
        val current = _legalConsentRecord.value
        _legalConsentRecord.value = current.copy(
            timestamp = System.currentTimeMillis(),
            isConsentActive = true
        )
    }

    fun openSignUpDialog() {
        _showSignUpDialog.value = true
    }

    fun closeSignUpDialog() {
        _showSignUpDialog.value = false
    }

    fun createAccount(
        username: String,
        fullName: String,
        avatarUrl: String,
        bio: String,
        neighborhood: String,
        enableLocationRadar: Boolean,
        legalConsentTimestamp: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProfile = UserProfileEntity(
                id = 1,
                username = username,
                fullName = fullName,
                avatarUrl = avatarUrl,
                bio = bio,
                website = "localiiiy.app/$username",
                category = "Local Creator",
                locationName = neighborhood,
                neighborhood = neighborhood,
                neighborsCount = 18,
                latitude = 47.608013,
                longitude = -122.335167,
                postsCount = 0,
                followersCount = 1,
                followingCount = 1,
                isVerified = false
            )
            repository.updateProfile(newProfile)
            _legalConsentRecord.value = com.example.data.legal.LegalConsentRecord(
                username = username,
                timestamp = legalConsentTimestamp,
                isConsentActive = true
            )
            _isLocationEnabled.value = enableLocationRadar
            _isLoggedOut.value = false
            _showSignUpDialog.value = false
        }
    }

    fun updatePrivacySettings(settings: PrivacySettingsEntity) {
        _isLocationEnabled.value = settings.isLocationRadarEnabled
        _isPrivateAccount.value = settings.isPrivateAccount
        viewModelScope.launch {
            repository.savePrivacySettings(settings)
        }
    }

    fun updateNearbyDiscovery(allow: Boolean) {
        viewModelScope.launch {
            repository.updateNearbyDiscovery(allow)
        }
    }

    fun updatePreciseLocation(precise: Boolean) {
        viewModelScope.launch {
            repository.updatePreciseLocation(precise)
        }
    }

    fun updateNearbyWaves(allow: Boolean) {
        viewModelScope.launch {
            repository.updateNearbyWaves(allow)
        }
    }

    fun updateActiveStatus(show: Boolean) {
        viewModelScope.launch {
            repository.updateActiveStatus(show)
        }
    }

    fun updateReadReceipts(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReadReceipts(enabled)
        }
    }

    fun updateCommentsPrivacy(option: String) {
        viewModelScope.launch {
            repository.updateCommentsPrivacy(option)
        }
    }

    fun updateDirectMessagesPrivacy(option: String) {
        viewModelScope.launch {
            repository.updateDirectMessagesPrivacy(option)
        }
    }

    fun updateTagsAndMentionsPrivacy(option: String) {
        viewModelScope.launch {
            repository.updateTagsAndMentionsPrivacy(option)
        }
    }

    fun updateHideMomentsFromStrangers(hide: Boolean) {
        viewModelScope.launch {
            repository.updateHideMomentsFromStrangers(hide)
        }
    }

    fun updatePostResharing(allow: Boolean) {
        viewModelScope.launch {
            repository.updatePostResharing(allow)
        }
    }

    fun updateSensitiveContentFilter(filter: String) {
        viewModelScope.launch {
            repository.updateSensitiveContentFilter(filter)
        }
    }

    fun selectTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun setProfileTab(tab: ProfileTab) {
        _profileTab.value = tab
    }

    // --- Story Operations ---
    fun openStory(story: StoryEntity) {
        _activeStory.value = story
        viewModelScope.launch {
            repository.markStoryViewed(story.id)
        }
    }

    fun closeStory() {
        _activeStory.value = null
    }

    // --- Comments Operations ---
    fun openComments(targetType: String, targetId: Long) {
        _activeCommentsTarget.value = Pair(targetType, targetId)
    }

    fun closeComments() {
        _activeCommentsTarget.value = null
    }

    fun getCommentsForTarget(targetType: String, targetId: Long): Flow<List<CommentEntity>> {
        return repository.getComments(targetType, targetId)
    }

    fun addComment(text: String) {
        val target = _activeCommentsTarget.value ?: return
        if (text.isBlank()) return
        val profile = userProfile.value
        viewModelScope.launch {
            repository.addComment(
                targetType = target.first,
                targetId = target.second,
                username = profile.username,
                avatarUrl = profile.avatarUrl,
                text = text.trim()
            )
        }
    }

    fun toggleCommentLike(commentId: Long, isLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleCommentLike(commentId, isLiked)
        }
    }

    // --- Like / Save / Follow Operations & Local Activity Caching ---
    fun togglePostLike(post: PostEntity) {
        viewModelScope.launch {
            val willBeLiked = !post.isLiked
            repository.togglePostLike(post.id, post.isLiked)
            if (willBeLiked) {
                recordUserActivity(
                    activityType = "LIKED_POST",
                    targetId = post.id.toString(),
                    targetTitle = post.caption.take(80),
                    targetPreviewUrl = post.mediaUrl,
                    extraDetails = "Sparked post by @${post.username}"
                )
            }
        }
    }

    fun togglePostSave(post: PostEntity) {
        viewModelScope.launch {
            val willBeSaved = !post.isSaved
            repository.togglePostSave(post.id, post.isSaved)
            if (willBeSaved) {
                repository.cacheSavedPost(post)
                recordUserActivity(
                    activityType = "SAVED_POST",
                    targetId = post.id.toString(),
                    targetTitle = "${post.landmark ?: post.location ?: "Neighborhood"}: ${post.caption.take(60)}",
                    targetPreviewUrl = post.mediaUrl,
                    extraDetails = "Saved pulse from @${post.username}"
                )
            } else {
                repository.removeSavedPostFromCache(post.id)
            }
        }
    }

    fun toggleReelLike(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelLike(reel.id, reel.isLiked)
        }
    }

    fun toggleReelSave(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelSave(reel.id, reel.isSaved)
        }
    }

    fun toggleReelFollow(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelFollow(reel.id, reel.isFollowing)
        }
    }

    fun toggleSoundMute() {
        _isSoundMuted.value = !_isSoundMuted.value
    }

    // --- User Profile Navigation ---
    fun openUserProfile(username: String) {
        if (username == userProfile.value.username) {
            _selectedOtherUser.value = null
            _currentTab.value = MainNavigationTab.PROFILE
            return
        }
        viewModelScope.launch {
            val user = otherUsers.value.find { it.username == username }
            if (user != null) {
                _selectedOtherUser.value = user
            }
        }
    }

    fun closeOtherUserProfile() {
        _selectedOtherUser.value = null
    }

    fun toggleOtherUserFollow(user: OtherUserEntity) {
        viewModelScope.launch {
            repository.toggleOtherUserFollow(user.username, user.isFollowing)
            _selectedOtherUser.value = _selectedOtherUser.value?.copy(
                isFollowing = !user.isFollowing,
                followersCount = user.followersCount + (if (user.isFollowing) -1 else 1)
            )
        }
    }

    fun waveAtNeighbor(user: OtherUserEntity) {
        viewModelScope.launch {
            repository.sendNeighborWaveNotification(user.username, user.distanceKm)
            repository.toggleOtherUserFriend(user.username, user.isFriend)
            _selectedOtherUser.value = _selectedOtherUser.value?.copy(
                isFriend = true
            )
        }
    }

    fun getUserPosts(username: String): Flow<List<PostEntity>> {
        return repository.getPostsByUsername(username)
    }

    fun getUserReels(username: String): Flow<List<ReelEntity>> {
        return repository.getReelsByUsername(username)
    }

    // --- Direct Messaging & Group Chat ---
    fun openNotificationsSheet() {
        _showNotificationsSheet.value = true
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun closeNotificationsSheet() {
        _showNotificationsSheet.value = false
    }

    fun openDirectMessagesSheet() {
        _showDirectMessagesSheet.value = true
    }

    fun closeDirectMessagesSheet() {
        _showDirectMessagesSheet.value = false
        _activeConversation.value = null
    }

    fun openConversation(conversation: DirectMessageEntity) {
        _activeConversation.value = conversation
    }

    fun closeActiveConversation() {
        _activeConversation.value = null
    }

    fun startChatWithUser(user: OtherUserEntity) {
        viewModelScope.launch {
            val convId = repository.createOrGetConversation(
                contactUsername = user.username,
                contactAvatar = user.avatarUrl,
                landmark = user.landmark,
                distanceKm = user.distanceKm
            )
            val conv = DirectMessageEntity(
                conversationId = convId,
                contactUsername = user.username,
                contactAvatar = user.avatarUrl,
                lastMessage = "Started a conversation",
                isOnline = true,
                isGroup = false,
                distanceKm = user.distanceKm,
                landmark = user.landmark
            )
            _activeConversation.value = conv
            _showDirectMessagesSheet.value = true
        }
    }

    fun sendChatMessage(text: String) {
        val conv = _activeConversation.value ?: return
        if (text.isBlank()) return
        val profile = userProfile.value
        viewModelScope.launch {
            repository.sendChatMessage(
                conversationId = conv.conversationId,
                senderUsername = profile.username,
                senderAvatar = profile.avatarUrl,
                text = text.trim()
            )
        }
    }

    fun createGroupChat(title: String, memberCount: Int = 4, landmark: String? = "Local Hub") {
        if (title.isBlank()) return
        viewModelScope.launch {
            val convId = repository.createGroupConversation(title.trim(), memberCount, landmark)
            val newGroup = DirectMessageEntity(
                conversationId = convId,
                contactUsername = title.trim(),
                contactAvatar = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=500&auto=format&fit=crop&q=80",
                lastMessage = "Group created",
                isOnline = true,
                isGroup = true,
                groupMembersCount = memberCount,
                distanceKm = 0.5,
                landmark = landmark
            )
            _activeConversation.value = newGroup
        }
    }

    // --- Share Sheet & Sharing Posts to Direct Messages ---
    fun openShareSheet(post: PostEntity) {
        _activeSharePost.value = post
    }

    fun closeShareSheet() {
        _activeSharePost.value = null
    }

    fun sharePostToConversation(conversationId: String, post: PostEntity) {
        val profile = userProfile.value
        viewModelScope.launch {
            repository.sendChatMessage(
                conversationId = conversationId,
                senderUsername = profile.username,
                senderAvatar = profile.avatarUrl,
                text = "Check out this reel/post at ${post.landmark ?: post.location ?: "our neighborhood"}",
                sharedMediaUrl = post.mediaUrl,
                sharedCaption = post.caption
            )
            closeShareSheet()
            // Open DMs with this conversation
            val conv = conversations.value.find { it.conversationId == conversationId }
            if (conv != null) {
                _activeConversation.value = conv
                _showDirectMessagesSheet.value = true
            }
        }
    }

    fun selectExplorePost(post: PostEntity?) {
        _selectedExplorePost.value = post
    }

    // --- Creation & Publish with Auto Location ---
    fun setCreationMode(mode: CreationMode) {
        _creationMode.value = mode
    }

    fun selectFilter(filter: FilterPreset) {
        _selectedFilter.value = filter
    }

    fun setSelectedMediaUri(uri: String) {
        _selectedMediaUri.value = uri
    }

    fun publishContent(
        caption: String,
        location: String?,
        landmark: String?,
        latitude: Double?,
        longitude: Double?,
        soundTitle: String?
    ) {
        val profile = userProfile.value
        val mediaUri = _selectedMediaUri.value
        val filter = _selectedFilter.value
        val currentLoc = _autoDetectedLocation.value ?: _currentLocation.value

        val finalLocation = location?.ifBlank { null } ?: currentLoc?.locationName ?: "Seattle, WA"
        val finalLandmark = landmark?.ifBlank { null } ?: currentLoc?.landmark ?: "Pike Place Market"
        val finalLat = latitude ?: currentLoc?.latitude ?: LocationHelper.DEFAULT_LAT
        val finalLng = longitude ?: currentLoc?.longitude ?: LocationHelper.DEFAULT_LNG
        val distanceKm = LocationHelper.calculateDistanceKm(
            LocationHelper.DEFAULT_LAT, LocationHelper.DEFAULT_LNG,
            finalLat, finalLng
        )

        viewModelScope.launch {
            when (_creationMode.value) {
                CreationMode.POST -> {
                    val newPost = PostEntity(
                        username = profile.username,
                        userAvatar = profile.avatarUrl,
                        userHandle = "@${profile.username}",
                        isVerified = profile.isVerified,
                        mediaUrl = mediaUri,
                        mediaType = "IMAGE",
                        caption = caption,
                        likesCount = 1,
                        commentsCount = 0,
                        isLiked = true,
                        isSaved = false,
                        isFollowing = true,
                        location = finalLocation,
                        landmark = finalLandmark,
                        latitude = finalLat,
                        longitude = finalLng,
                        distanceKm = distanceKm,
                        isNeighbor = distanceKm < 3.0,
                        soundTitle = soundTitle?.ifBlank { null },
                        filterName = filter.name
                    )
                    repository.createPost(newPost)
                    _currentTab.value = MainNavigationTab.FEED
                }
                CreationMode.REEL -> {
                    val newReel = ReelEntity(
                        username = profile.username,
                        userAvatar = profile.avatarUrl,
                        userHandle = "@${profile.username}",
                        isVerified = profile.isVerified,
                        mediaUrl = mediaUri,
                        caption = caption.ifBlank { "New reel from $finalLandmark #nearby #localiiiy" },
                        soundTitle = soundTitle?.ifBlank { null } ?: "Original Audio • ${profile.username}",
                        soundArtist = profile.fullName,
                        likesCount = 1,
                        commentsCount = 0,
                        sharesCount = 0,
                        isLiked = true,
                        isSaved = false,
                        isFollowing = false,
                        viewsCount = "1",
                        location = finalLocation,
                        landmark = finalLandmark,
                        latitude = finalLat,
                        longitude = finalLng,
                        distanceKm = distanceKm,
                        isNeighbor = distanceKm < 3.0,
                        filterName = filter.name
                    )
                    repository.createReel(newReel)
                    _currentTab.value = MainNavigationTab.REELS
                }
                CreationMode.STORY -> {
                    val newStory = StoryEntity(
                        userId = profile.username,
                        username = "Your Story",
                        userAvatar = profile.avatarUrl,
                        mediaUrl = mediaUri,
                        caption = caption,
                        location = finalLocation,
                        distanceKm = distanceKm,
                        isViewed = false,
                        isUserStory = true
                    )
                    repository.createStory(newStory)
                    _currentTab.value = MainNavigationTab.FEED
                }
            }
        }
    }

    fun updateProfile(fullName: String, bio: String, website: String, avatarUrl: String) {
        val current = userProfile.value
        val updated = current.copy(
            fullName = fullName.trim(),
            bio = bio.trim(),
            website = website.trim(),
            avatarUrl = avatarUrl.trim()
        )
        viewModelScope.launch {
            repository.updateProfile(updated)
        }
    }

    // --- Marketplace Actions ---
    fun selectMarketCategory(category: String) {
        _selectedMarketCategory.value = category
    }

    fun setMarketplaceSearchQuery(query: String) {
        _marketplaceSearchQuery.value = query
    }

    fun setMarketplaceLocationQuery(query: String) {
        _marketplaceLocationQuery.value = query
    }

    fun setMarketplaceRadius(radiusKm: Double?) {
        _marketplaceRadiusKm.value = radiusKm
    }

    fun selectMarketplaceItem(item: MarketplaceItemEntity?) {
        _selectedMarketplaceItem.value = item
    }

    fun openSellItemDialog() {
        _showSellItemDialog.value = true
    }

    fun closeSellItemDialog() {
        _showSellItemDialog.value = false
    }

    fun toggleMarketItemSaved(item: MarketplaceItemEntity) {
        viewModelScope.launch {
            repository.toggleMarketItemSaved(item.id, item.isSaved)
            if (_selectedMarketplaceItem.value?.id == item.id) {
                _selectedMarketplaceItem.value = item.copy(isSaved = !item.isSaved)
            }
        }
    }

    fun toggleMarketItemAvailability(item: MarketplaceItemEntity) {
        viewModelScope.launch {
            repository.updateMarketItemAvailability(item.id, !item.isAvailable)
            if (_selectedMarketplaceItem.value?.id == item.id) {
                _selectedMarketplaceItem.value = item.copy(isAvailable = !item.isAvailable)
            }
        }
    }

    fun publishMarketplaceItem(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        imageUrl: String,
        deliveryOption: String,
        location: String?,
        landmark: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val currentLoc = currentLocation.value
            val finalLocation = location?.ifBlank { null }
                ?: currentLoc?.city
                ?: profile.locationName
            val finalLandmark = landmark?.ifBlank { null }
                ?: currentLoc?.landmark
                ?: "Pike Place Market"
            val finalLat = currentLoc?.latitude ?: profile.latitude
            val finalLng = currentLoc?.longitude ?: profile.longitude

            val newItem = MarketplaceItemEntity(
                title = title.trim(),
                description = description.trim(),
                price = price,
                category = category,
                condition = condition,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80" },
                sellerUsername = profile.username,
                sellerFullName = profile.fullName,
                sellerAvatar = profile.avatarUrl,
                sellerRating = 5.0,
                sellerReviewCount = 1,
                location = finalLocation,
                landmark = finalLandmark,
                distanceKm = 0.1,
                latitude = finalLat,
                longitude = finalLng,
                isAvailable = true,
                isSaved = false,
                deliveryOption = deliveryOption
            )
            repository.createMarketplaceItem(newItem)
            _showSellItemDialog.value = false
        }
    }

    fun publishMarketBuySellPost(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        imageUrl: String,
        deliveryOption: String,
        location: String?,
        landmark: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val currentLoc = currentLocation.value
            val finalLocation = location?.ifBlank { null }
                ?: currentLoc?.city
                ?: profile.locationName
            val finalLandmark = landmark?.ifBlank { null }
                ?: currentLoc?.landmark
                ?: "Pike Place Market"
            val finalLat = currentLoc?.latitude ?: profile.latitude
            val finalLng = currentLoc?.longitude ?: profile.longitude

            val newItem = MarketplaceItemEntity(
                title = title.trim(),
                description = description.trim(),
                price = price,
                category = category,
                condition = condition,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80" },
                sellerUsername = profile.username,
                sellerFullName = profile.fullName,
                sellerAvatar = profile.avatarUrl,
                sellerRating = 5.0,
                sellerReviewCount = 1,
                location = finalLocation,
                landmark = finalLandmark,
                distanceKm = 0.1,
                latitude = finalLat,
                longitude = finalLng,
                isAvailable = true,
                isSaved = false,
                deliveryOption = deliveryOption
            )
            repository.createMarketplaceItem(newItem)

            val newPost = PostEntity(
                username = profile.username,
                userAvatar = profile.avatarUrl,
                userHandle = "@${profile.username}",
                isVerified = profile.isVerified,
                mediaUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80" },
                mediaType = "IMAGE",
                caption = "🛍️ FOR SALE ($${price.toInt()}): ${title.trim()}\n\n${description.trim()}\n\nCondition: $condition • Pickup at $finalLandmark #LocalMarket #BuySell #Localiiiy",
                likesCount = 1,
                commentsCount = 0,
                isLiked = true,
                isSaved = false,
                isFollowing = true,
                location = finalLocation,
                landmark = finalLandmark,
                latitude = finalLat,
                longitude = finalLng,
                distanceKm = 0.1,
                isNeighbor = true,
                soundTitle = null,
                filterName = "Normal"
            )
            repository.createPost(newPost)
            _showSellItemDialog.value = false
        }
    }

    fun publishMarketBuySellReel(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        videoUrl: String,
        soundTitle: String?,
        location: String?,
        landmark: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val currentLoc = currentLocation.value
            val finalLocation = location?.ifBlank { null }
                ?: currentLoc?.city
                ?: profile.locationName
            val finalLandmark = landmark?.ifBlank { null }
                ?: currentLoc?.landmark
                ?: "Pike Place Market"
            val finalLat = currentLoc?.latitude ?: profile.latitude
            val finalLng = currentLoc?.longitude ?: profile.longitude

            val newItem = MarketplaceItemEntity(
                title = title.trim(),
                description = description.trim(),
                price = price,
                category = category,
                condition = condition,
                imageUrl = videoUrl.ifBlank { "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=600&auto=format&fit=crop&q=80" },
                sellerUsername = profile.username,
                sellerFullName = profile.fullName,
                sellerAvatar = profile.avatarUrl,
                sellerRating = 5.0,
                sellerReviewCount = 1,
                location = finalLocation,
                landmark = finalLandmark,
                distanceKm = 0.1,
                latitude = finalLat,
                longitude = finalLng,
                isAvailable = true,
                isSaved = false,
                deliveryOption = "Local Pickup & Meetup"
            )
            repository.createMarketplaceItem(newItem)

            val newReel = ReelEntity(
                username = profile.username,
                userAvatar = profile.avatarUrl,
                userHandle = "@${profile.username}",
                isVerified = profile.isVerified,
                mediaUrl = videoUrl.ifBlank { "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=600&auto=format&fit=crop&q=80" },
                caption = "🛍️ FOR SALE ($${price.toInt()}): ${title.trim()} • ${description.trim()} #MarketReel #Localiiiy",
                soundTitle = soundTitle?.ifBlank { null } ?: "Product Showcase • ${profile.username}",
                soundArtist = profile.fullName,
                likesCount = 1,
                commentsCount = 0,
                sharesCount = 0,
                isLiked = true,
                isSaved = false,
                isFollowing = false,
                viewsCount = "1",
                location = finalLocation,
                landmark = finalLandmark,
                latitude = finalLat,
                longitude = finalLng,
                distanceKm = 0.1,
                isNeighbor = true,
                filterName = "Normal"
            )
            repository.createReel(newReel)
            _showSellItemDialog.value = false
        }
    }

    fun startChatForMarketItem(item: MarketplaceItemEntity) {
        viewModelScope.launch {
            val convId = repository.createOrGetConversation(
                contactUsername = item.sellerUsername,
                contactAvatar = item.sellerAvatar,
                landmark = item.landmark,
                distanceKm = item.distanceKm
            )
            // Add automatic inquiry message
            repository.sendChatMessage(
                conversationId = convId,
                senderUsername = userProfile.value.username,
                senderAvatar = userProfile.value.avatarUrl,
                text = "Hi ${item.sellerFullName}! I saw your listing for '${item.title}' ($${item.price.toInt()}) on Localiiiy Market. Is it still available?",
                sharedMediaUrl = item.imageUrl,
                sharedCaption = "${item.title} • $${item.price.toInt()}"
            )
            _selectedMarketplaceItem.value = null
            _showDirectMessagesSheet.value = true
        }
    }

    // --- Profile Content Management Actions ---
    fun deletePost(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePost(postId)
        }
    }

    fun deleteReel(reelId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReel(reelId)
        }
    }

    fun updateReelDetails(reelId: Long, caption: String, location: String?, landmark: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReelDetails(reelId, caption, location, landmark)
        }
    }

    fun deleteMarketplaceItem(itemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMarketplaceItem(itemId)
            if (_selectedMarketplaceItem.value?.id == itemId) {
                _selectedMarketplaceItem.value = null
            }
        }
    }

    fun updateMarketplaceItem(item: MarketplaceItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMarketplaceItem(item)
            if (_selectedMarketplaceItem.value?.id == item.id) {
                _selectedMarketplaceItem.value = item
            }
        }
    }

    fun switchUser(otherUser: OtherUserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProfile = UserProfileEntity(
                id = 1,
                username = otherUser.username,
                fullName = otherUser.fullName,
                avatarUrl = otherUser.avatarUrl,
                bio = otherUser.bio,
                website = otherUser.website,
                category = otherUser.category,
                locationName = otherUser.locationName,
                neighborhood = otherUser.landmark ?: otherUser.locationName,
                latitude = otherUser.latitude,
                longitude = otherUser.longitude,
                postsCount = otherUser.postsCount,
                followersCount = otherUser.followersCount,
                followingCount = otherUser.followingCount,
                neighborsCount = (12..48).random(),
                isVerified = otherUser.isVerified
            )
            repository.updateProfile(newProfile)
        }
    }

    fun blockUser(username: String) {
        if (username.isBlank()) return
        _blockedUsernames.value = _blockedUsernames.value + username
        viewModelScope.launch(Dispatchers.IO) {
            val current = privacySettings.value
            repository.savePrivacySettings(current.copy(blockedAccountsCount = _blockedUsernames.value.size))
        }
    }

    fun unblockUser(username: String) {
        _blockedUsernames.value = _blockedUsernames.value - username
        viewModelScope.launch(Dispatchers.IO) {
            val current = privacySettings.value
            repository.savePrivacySettings(current.copy(blockedAccountsCount = _blockedUsernames.value.size))
        }
    }

    fun reportPost(postId: Long, reason: String) {
        val post = allPosts.value.firstOrNull { it.id == postId }
        val author = post?.username ?: "unknown"
        reportContent("POST", postId, author, reason)
    }

    fun reportUser(username: String, reason: String) {
        reportContent("USER", 0L, username, reason)
    }

    fun reportContent(contentType: String, contentId: Long, authorUsername: String, reason: String) {
        val record = "Reported $contentType #$contentId by @$authorUsername for '$reason' at ${System.currentTimeMillis()}"
        _reportedContentRecords.value = _reportedContentRecords.value + record
        // Instantly block or hide content from bad actor if offensive/harassment
        if (reason.contains("Harassment", ignoreCase = true) || reason.contains("Hate", ignoreCase = true) || reason.contains("Scam", ignoreCase = true)) {
            blockUser(authorUsername)
        }
    }

    fun deleteAccountAndPurgeData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUsername = userProfile.value.username
            // 1. Purge all user created content
            allPosts.value.filter { it.username == currentUsername }.forEach { repository.deletePost(it.id) }
            allReels.value.filter { it.username == currentUsername }.forEach { repository.deleteReel(it.id) }
            allMarketplaceItems.value.filter { it.sellerUsername == currentUsername }.forEach { repository.deleteMarketplaceItem(it.id) }
            
            // 2. Erase user profile data to default blank state (Apple 5.1.1(v) & GDPR Compliant)
            val blankProfile = UserProfileEntity(
                id = 1,
                username = "guest_${System.currentTimeMillis() % 10000}",
                fullName = "New Neighbor",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                bio = "Fresh local account.",
                website = "",
                category = "Neighbor",
                locationName = "Seattle, WA",
                neighborhood = "Downtown",
                latitude = 47.6062,
                longitude = -122.3321,
                postsCount = 0,
                followersCount = 0,
                followingCount = 0,
                neighborsCount = 0,
                isVerified = false
            )
            repository.updateProfile(blankProfile)
            _isLoggedOut.value = true
        }
    }

    fun logoutUser() {
        _isLoggedOut.value = true
    }

    fun loginUser() {
        _isLoggedOut.value = false
    }

    fun resetDemoProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(InitialData.defaultProfile)
            _isLoggedOut.value = false
        }
    }

    // --- Localiiiy Studio Methods ---
    fun setStudioCategory(category: String) {
        _selectedStudioCategory.value = category
    }

    fun setStudioSearchQuery(query: String) {
        _studioSearchQuery.value = query
    }

    fun openStudioVideo(video: StudioVideoEntity) {
        _activeStudioVideo.value = video
        _isStudioVideoPlaying.value = true
        _studioPlaybackProgress.value = 0.05f
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordStudioVideoView(video.id)
        }
    }

    fun closeStudioVideo() {
        _activeStudioVideo.value = null
        _isStudioVideoPlaying.value = false
    }

    fun toggleStudioPlayPause() {
        _isStudioVideoPlaying.value = !_isStudioVideoPlaying.value
    }

    fun setStudioPlaybackProgress(progress: Float) {
        _studioPlaybackProgress.value = progress.coerceIn(0f, 1f)
    }

    fun openStudioUploadSheet() {
        _isStudioUploadSheetOpen.value = true
    }

    fun closeStudioUploadSheet() {
        _isStudioUploadSheetOpen.value = false
    }

    fun toggleStudioCreatorDashboard() {
        _showStudioCreatorDashboard.value = !_showStudioCreatorDashboard.value
    }

    fun toggleStudioVideoLike(video: StudioVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleStudioVideoLike(video.id, video.isLiked)
            _activeStudioVideo.value?.let { current ->
                if (current.id == video.id) {
                    val delta = if (video.isLiked) -1 else 1
                    _activeStudioVideo.value = current.copy(
                        isLiked = !video.isLiked,
                        likesCount = current.likesCount + delta,
                        isDisliked = false
                    )
                }
            }
        }
    }

    fun toggleStudioVideoSave(video: StudioVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleStudioVideoSave(video.id, video.isSaved)
            _activeStudioVideo.value?.let { current ->
                if (current.id == video.id) {
                    _activeStudioVideo.value = current.copy(isSaved = !video.isSaved)
                }
            }
        }
    }

    fun toggleStudioCreatorSubscription(creatorUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSubscribed = allStudioVideos.value.firstOrNull { it.creatorUsername == creatorUsername }?.isSubscribed ?: false
            repository.toggleStudioCreatorSubscription(creatorUsername, currentSubscribed)
            _activeStudioVideo.value?.let { current ->
                if (current.creatorUsername == creatorUsername) {
                    _activeStudioVideo.value = current.copy(isSubscribed = !currentSubscribed)
                }
            }
            // Keep social graph synchronized
            otherUsers.value.find { it.username == creatorUsername }?.let { otherUser ->
                repository.toggleOtherUserFollow(creatorUsername, otherUser.isFollowing)
            }
        }
    }

    fun uploadStudioVideo(
        title: String,
        description: String,
        category: String,
        durationSeconds: Int,
        videoUrl: String,
        thumbnailUrl: String,
        resolution: String = "4K Ultra HD",
        tags: String = "#Localiiiy #Studio",
        chapters: String = ""
    ): Boolean {
        // Enforce YouTube long-video duration: minimum 60 seconds, maximum 240 minutes (14,400s)
        if (durationSeconds < 60 || durationSeconds > 14400) {
            return false
        }

        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            val formattedDuration = if (minutes >= 60) {
                val hours = minutes / 60
                val remainingMins = minutes % 60
                String.format("%d:%02d:%02d", hours, remainingMins, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }

            val newVideo = StudioVideoEntity(
                title = title.trim(),
                description = description.trim().ifEmpty { "Created with Localiiiy Studio. Full length creator video ($formattedDuration)." },
                videoUrl = videoUrl.trim().ifEmpty { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
                thumbnailUrl = thumbnailUrl.trim().ifEmpty { "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&auto=format&fit=crop&q=80" },
                durationSeconds = durationSeconds,
                category = category,
                creatorUsername = profile.username,
                creatorFullName = profile.fullName,
                creatorAvatar = profile.avatarUrl,
                creatorSubscribersCount = "${profile.followersCount / 1000}K subscribers",
                isSubscribed = true,
                viewsCount = 1,
                viewsFormatted = "1 view",
                likesCount = 1,
                isLiked = true,
                uploadDateFormatted = "Just now",
                tags = tags,
                resolution = resolution,
                chapters = chapters.ifEmpty { "00:00 - Introduction\n05:00 - Main Content\n$formattedDuration - Conclusion" },
                isCreatorPick = false,
                isTrending = false
            )
            repository.createStudioVideo(newVideo)
            _isStudioUploadSheetOpen.value = false
        }
        return true
    }

    fun deleteStudioVideo(videoId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudioVideo(videoId)
            if (_activeStudioVideo.value?.id == videoId) {
                _activeStudioVideo.value = null
            }
        }
    }

    // --- User Activity & Studio Draft Caching Operations ---
    fun recordUserActivity(
        activityType: String,
        targetId: String,
        targetTitle: String,
        targetPreviewUrl: String? = null,
        extraDetails: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cacheUserActivity(
                UserActivityEntity(
                    activityType = activityType,
                    targetId = targetId,
                    targetTitle = targetTitle,
                    targetPreviewUrl = targetPreviewUrl,
                    extraDetails = extraDetails,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun refreshPulseFeed() {
        viewModelScope.launch {
            _isRefreshingFeed.value = true
            recordUserActivity(
                activityType = "PULSE_REFRESH",
                targetId = "feed_radar",
                targetTitle = "Refreshed Hyperlocal Pulse Feed",
                extraDetails = "Synchronized neighborhood radar (${_nearbyRadiusKm.value ?: 3.0} km)"
            )
            kotlinx.coroutines.delay(900)
            _isRefreshingFeed.value = false
        }
    }

    fun saveStudioDraft(
        title: String,
        description: String = "",
        category: String = "Music",
        durationSeconds: Int = 300,
        videoUri: String = "",
        thumbnailUri: String = "",
        resolution: String = "4K Ultra HD",
        tags: String = "#Localiiiy #Studio",
        chapters: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val draft = StudioDraftEntity(
                title = title,
                description = description,
                category = category,
                durationSeconds = durationSeconds,
                videoUri = videoUri,
                thumbnailUri = thumbnailUri,
                resolution = resolution,
                tags = tags,
                chapters = chapters
            )
            val draftId = repository.saveStudioDraft(draft)
            recordUserActivity(
                activityType = "CREATED_DRAFT",
                targetId = draftId.toString(),
                targetTitle = title,
                targetPreviewUrl = thumbnailUri.ifBlank { null },
                extraDetails = "Category: $category • Length: ${durationSeconds / 60}m"
            )
        }
    }

    fun deleteStudioDraft(draftId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudioDraft(draftId)
        }
    }

    // --- Worldwide Currency & Multi-Language Functions ---
    fun setCurrency(currency: LocaliCurrency) {
        _currentCurrency.value = currency
    }

    fun setLanguage(language: LocaliLanguage) {
        _currentLanguage.value = language
    }

    fun openLanguageCurrencyDialog() {
        _showLanguageCurrencyDialog.value = true
    }

    fun closeLanguageCurrencyDialog() {
        _showLanguageCurrencyDialog.value = false
    }

    // --- Worldwide Creator Monetization & Ads Functions ---
    fun openMonetizationHub() {
        _showMonetizationHub.value = true
    }

    fun closeMonetizationHub() {
        _showMonetizationHub.value = false
    }

    fun openBoostAdDialog() {
        _showBoostAdDialog.value = true
    }

    fun closeBoostAdDialog() {
        _showBoostAdDialog.value = false
    }

    fun recordAdImpression(adId: String) {
        val currentAd = _sponsoredAds.value.find { it.id == adId } ?: return
        val cpmEarn = currentAd.cpmRateUSD / 1000.0
        val creatorCut = cpmEarn * 0.55
        val platformCut = cpmEarn * 0.45

        _creatorEarnings.update { cur ->
            cur.copy(
                monetizedViews = cur.monetizedViews + 1,
                feedSponsoredAdUSD = cur.feedSponsoredAdUSD + creatorCut,
                totalGrossEarnedUSD = cur.totalGrossEarnedUSD + creatorCut,
                availableBalanceUSD = cur.availableBalanceUSD + creatorCut
            )
        }

        _platformMetrics.update { cur ->
            cur.copy(
                grossAdRevenueWorldwideUSD = cur.grossAdRevenueWorldwideUSD + cpmEarn,
                platformNetCommissionUSD = cur.platformNetCommissionUSD + platformCut,
                creatorsDisbursedUSD = cur.creatorsDisbursedUSD + creatorCut,
                totalAdImpressionsServed = cur.totalAdImpressionsServed + 1
            )
        }
    }

    fun recordAdClick(adId: String) {
        val currentAd = _sponsoredAds.value.find { it.id == adId } ?: return
        val cpcEarn = currentAd.cpcRateUSD
        val creatorCut = cpcEarn * 0.55
        val platformCut = cpcEarn * 0.45

        _creatorEarnings.update { cur ->
            cur.copy(
                feedSponsoredAdUSD = cur.feedSponsoredAdUSD + creatorCut,
                totalGrossEarnedUSD = cur.totalGrossEarnedUSD + creatorCut,
                availableBalanceUSD = cur.availableBalanceUSD + creatorCut
            )
        }

        _platformMetrics.update { cur ->
            cur.copy(
                grossAdRevenueWorldwideUSD = cur.grossAdRevenueWorldwideUSD + cpcEarn,
                platformNetCommissionUSD = cur.platformNetCommissionUSD + platformCut,
                creatorsDisbursedUSD = cur.creatorsDisbursedUSD + creatorCut
            )
        }
    }

    fun requestPayout(amountUSD: Double): Boolean {
        val earnings = _creatorEarnings.value
        val minPayout = _payoutAccount.value.minimumPayoutUSD
        if (amountUSD < minPayout || amountUSD > earnings.availableBalanceUSD) {
            return false
        }

        val targetCurr = _currentCurrency.value
        val localAmount = CurrencyHelper.convertFromUSD(amountUSD, targetCurr)

        val tx = PayoutTransaction(
            id = "TX-${System.currentTimeMillis() % 100000}-GLB",
            amountUSD = amountUSD,
            targetCurrencyCode = targetCurr.code,
            amountInLocalCurrency = localAmount,
            status = "PROCESSING",
            payoutMethod = _payoutAccount.value.payoutMethod,
            referenceId = "PAY-${System.currentTimeMillis()}"
        )

        _payoutHistory.update { listOf(tx) + it }
        _creatorEarnings.update { cur ->
            cur.copy(
                availableBalanceUSD = cur.availableBalanceUSD - amountUSD,
                pendingPayoutUSD = cur.pendingPayoutUSD + amountUSD
            )
        }
        return true
    }

    fun updatePayoutAccount(updated: CreatorPayoutAccount) {
        _payoutAccount.value = updated
    }

    fun launchBoostCampaign(campaign: BoostCampaignRequest): Boolean {
        val grossCostUSD = campaign.dailyBudgetUSD * campaign.durationDays
        val platformCut = grossCostUSD * 0.45
        val creatorCut = grossCostUSD * 0.55

        _platformMetrics.update { cur ->
            cur.copy(
                grossAdRevenueWorldwideUSD = cur.grossAdRevenueWorldwideUSD + grossCostUSD,
                platformNetCommissionUSD = cur.platformNetCommissionUSD + platformCut,
                creatorsDisbursedUSD = cur.creatorsDisbursedUSD + creatorCut,
                activeGlobalAdvertisers = cur.activeGlobalAdvertisers + 1
            )
        }
        return true
    }

    fun tipCreatorSuperThanks(amountUSD: Double, creatorUsername: String): Boolean {
        if (amountUSD <= 0.0) return false
        _creatorEarnings.update { cur ->
            cur.copy(
                superThanksTipsUSD = cur.superThanksTipsUSD + amountUSD,
                totalGrossEarnedUSD = cur.totalGrossEarnedUSD + amountUSD,
                availableBalanceUSD = cur.availableBalanceUSD + amountUSD
            )
        }
        return true
    }
}
