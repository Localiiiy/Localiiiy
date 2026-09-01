package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.ui.CreationMode
import com.example.ui.InstagramViewModel
import com.example.ui.MainNavigationTab
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.LocaliAccentCoral
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliStoryGradient
import com.example.ui.theme.LocaliTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocaliTheme {
                LocaliApp()
            }
        }
    }
}

@Composable
fun LocaliApp(
    viewModel: InstagramViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val feedPosts by viewModel.feedPosts.collectAsStateWithLifecycle()
    val reels by viewModel.allReels.collectAsStateWithLifecycle()
    val stories by viewModel.allStories.collectAsStateWithLifecycle()
    val savedPosts by viewModel.savedPosts.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val otherUsers by viewModel.otherUsers.collectAsStateWithLifecycle()

    val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
    val activeCommentsTarget by viewModel.activeCommentsTarget.collectAsStateWithLifecycle()
    val activeSharePost by viewModel.activeSharePost.collectAsStateWithLifecycle()
    val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()
    val showDirectMessagesSheet by viewModel.showDirectMessagesSheet.collectAsStateWithLifecycle()
    val isSoundMuted by viewModel.isSoundMuted.collectAsStateWithLifecycle()
    val profileTab by viewModel.profileTab.collectAsStateWithLifecycle()
    val isLoggedOut by viewModel.isLoggedOut.collectAsStateWithLifecycle()

    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val isDetectingLocation by viewModel.isDetectingLocation.collectAsStateWithLifecycle()
    val isLocationEnabled by viewModel.isLocationEnabled.collectAsStateWithLifecycle()
    val isPrivateAccount by viewModel.isPrivateAccount.collectAsStateWithLifecycle()
    val nearbyRadiusKm by viewModel.nearbyRadiusKm.collectAsStateWithLifecycle()

    val selectedOtherUser by viewModel.selectedOtherUser.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val activeChatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()

    val privacySettings by viewModel.privacySettings.collectAsStateWithLifecycle()
    val showPrivacySettings by viewModel.showPrivacySettings.collectAsStateWithLifecycle()
    val showLegalAgreement by viewModel.showLegalAgreement.collectAsStateWithLifecycle()
    val showSignUpDialog by viewModel.showSignUpDialog.collectAsStateWithLifecycle()
    val legalConsentRecord by viewModel.legalConsentRecord.collectAsStateWithLifecycle()

    val creationMode by viewModel.creationMode.collectAsStateWithLifecycle()
    val selectedMediaUri by viewModel.selectedMediaUri.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val autoDetectedLocation by viewModel.autoDetectedLocation.collectAsStateWithLifecycle()

    val marketplaceItems by viewModel.allMarketplaceItems.collectAsStateWithLifecycle()
    val selectedMarketCategory by viewModel.selectedMarketCategory.collectAsStateWithLifecycle()
    val marketplaceSearchQuery by viewModel.marketplaceSearchQuery.collectAsStateWithLifecycle()
    val marketplaceLocationQuery by viewModel.marketplaceLocationQuery.collectAsStateWithLifecycle()
    val marketplaceRadiusKm by viewModel.marketplaceRadiusKm.collectAsStateWithLifecycle()
    val selectedMarketplaceItem by viewModel.selectedMarketplaceItem.collectAsStateWithLifecycle()
    val showSellItemDialog by viewModel.showSellItemDialog.collectAsStateWithLifecycle()

    var showOpeningAnimation by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (currentTab == MainNavigationTab.FEED) {
                    LocaliTopBar(
                        hasUnreadNotifications = notifications.any { !it.isRead },
                        hasUnreadMessages = conversations.any { !it.isRead },
                        currentLocationLabel = currentLocation?.landmark ?: "Pike Place, Seattle",
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        onLogoClick = { showOpeningAnimation = true },
                        onNotificationsClick = { viewModel.openNotificationsSheet() },
                        onDirectMessagesClick = { viewModel.openDirectMessagesSheet() },
                        onCreateClick = {
                            viewModel.setCreationMode(CreationMode.POST)
                            viewModel.selectTab(MainNavigationTab.CREATE)
                        },
                        onLocationClick = {
                            if (!isLocationEnabled) {
                                viewModel.setLocationEnabled(true)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Location Radar Enabled 📍")
                                }
                            } else {
                                viewModel.detectCurrentLocation(context)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("GPS Location updated: ${currentLocation?.landmark ?: "Pike Place"}")
                                }
                            }
                        }
                    )
                }
            },
        bottomBar = {
            if (currentTab != MainNavigationTab.CREATE) {
                LocaliBottomNavigationBar(
                    currentTab = currentTab,
                    userAvatarUrl = userProfile.avatarUrl,
                    onTabSelected = { tab ->
                        if (tab == MainNavigationTab.CREATE) {
                            viewModel.setCreationMode(CreationMode.POST)
                        }
                        viewModel.selectTab(tab)
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavigationTab.FEED -> {
                    FeedScreen(
                        posts = feedPosts.ifEmpty { posts },
                        stories = stories,
                        otherUsers = otherUsers,
                        userProfile = userProfile,
                        selectedRadiusKm = nearbyRadiusKm,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        onRadiusFilterChange = { radius -> viewModel.setNearbyRadiusFilter(radius) },
                        onLocationToggle = { enabled ->
                            viewModel.setLocationEnabled(enabled)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (enabled) "Location Radar Enabled 📡" else "Location Radar Disabled (Off Grid) 🔒")
                            }
                        },
                        onPrivateToggle = { isPrivate ->
                            viewModel.setPrivateAccount(isPrivate)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (isPrivate) "Private Account (Ghost Mode) Enabled 👻" else "Public Visibility Enabled 🌐")
                            }
                        },
                        onOpenPrivacySettings = { viewModel.openPrivacySettings() },
                        onStoryClick = { story -> viewModel.openStory(story) },
                        onAddStoryClick = {
                            viewModel.setCreationMode(CreationMode.STORY)
                            viewModel.selectTab(MainNavigationTab.CREATE)
                        },
                        onLikePost = { post -> viewModel.togglePostLike(post) },
                        onCommentPost = { post -> viewModel.openComments("POST", post.id) },
                        onSharePost = { post -> viewModel.openShareSheet(post) },
                        onSavePost = { post -> viewModel.togglePostSave(post) },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onFollowUser = { user -> viewModel.toggleOtherUserFollow(user) },
                        onWaveAtNeighbor = { user ->
                            viewModel.waveAtNeighbor(user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Waved at ${user.username}! 👋 Proximity pulse sent.")
                            }
                        },
                        onMessageNeighbor = { user ->
                            viewModel.startChatWithUser(user)
                        },
                        onPostClick = { post -> viewModel.selectExplorePost(post) }
                    )
                }

                MainNavigationTab.EXPLORE -> {
                    ExploreScreen(
                        posts = posts,
                        userProfile = userProfile,
                        nearbyUsers = otherUsers,
                        selectedRadiusKm = nearbyRadiusKm,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        onRadiusFilterChange = { radius -> viewModel.setNearbyRadiusFilter(radius) },
                        onLocationToggle = { enabled ->
                            viewModel.setLocationEnabled(enabled)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (enabled) "Location Radar Enabled 📡" else "Location Radar Disabled (Off Grid) 🔒")
                            }
                        },
                        onPrivateToggle = { isPrivate ->
                            viewModel.setPrivateAccount(isPrivate)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (isPrivate) "Private Account (Ghost Mode) Enabled 👻" else "Public Visibility Enabled 🌐")
                            }
                        },
                        onOpenPrivacySettings = { viewModel.openPrivacySettings() },
                        onPostClick = { post -> viewModel.selectExplorePost(post) },
                        onLikePost = { post -> viewModel.togglePostLike(post) },
                        onCommentPost = { post -> viewModel.openComments("POST", post.id) },
                        onSharePost = { post -> viewModel.openShareSheet(post) },
                        onSavePost = { post -> viewModel.togglePostSave(post) },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onWaveAtUser = { user ->
                            viewModel.waveAtNeighbor(user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Waved at ${user.username}! 👋")
                            }
                        }
                    )
                }

                MainNavigationTab.MARKET -> {
                    MarketScreen(
                        items = marketplaceItems,
                        posts = posts,
                        reels = reels,
                        selectedCategory = selectedMarketCategory,
                        searchQuery = marketplaceSearchQuery,
                        locationQuery = marketplaceLocationQuery,
                        radiusFilterKm = marketplaceRadiusKm,
                        selectedItem = selectedMarketplaceItem,
                        showSellDialog = showSellItemDialog,
                        onSelectCategory = { cat -> viewModel.selectMarketCategory(cat) },
                        onSearchQueryChange = { q -> viewModel.setMarketplaceSearchQuery(q) },
                        onLocationQueryChange = { loc -> viewModel.setMarketplaceLocationQuery(loc) },
                        onRadiusFilterChange = { r -> viewModel.setMarketplaceRadius(r) },
                        onItemClick = { item -> viewModel.selectMarketplaceItem(item) },
                        onToggleSaveItem = { item -> viewModel.toggleMarketItemSaved(item) },
                        onOpenSellDialog = { viewModel.openSellItemDialog() },
                        onCloseSellDialog = { viewModel.closeSellItemDialog() },
                        onPublishItem = { title, desc, price, cat, cond, img, delivery, loc, landmark ->
                            viewModel.publishMarketplaceItem(title, desc, price, cat, cond, img, delivery, loc, landmark)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Listing published to Localiiiy Market! 🛍️")
                            }
                        },
                        onPublishBuySellPost = { title, desc, price, cat, cond, img, delivery, loc, landmark ->
                            viewModel.publishMarketBuySellPost(title, desc, price, cat, cond, img, delivery, loc, landmark)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Buy/Sell Post published to Local Feed! 📸")
                            }
                        },
                        onPublishBuySellReel = { title, desc, price, cat, cond, videoUrl, soundTitle, loc, landmark ->
                            viewModel.publishMarketBuySellReel(title, desc, price, cat, cond, videoUrl, soundTitle, loc, landmark)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Market Reel published to Clips! 🎬")
                            }
                        },
                        onCloseDetailSheet = { viewModel.selectMarketplaceItem(null) },
                        onMessageSeller = { item -> viewModel.startChatForMarketItem(item) },
                        onToggleAvailability = { item -> viewModel.toggleMarketItemAvailability(item) }
                    )
                }

                MainNavigationTab.CREATE -> {
                    CreateScreen(
                        creationMode = creationMode,
                        selectedMediaUri = selectedMediaUri,
                        selectedFilter = selectedFilter,
                        detectedLocation = autoDetectedLocation ?: currentLocation,
                        onModeChange = { mode -> viewModel.setCreationMode(mode) },
                        onSelectMedia = { uri -> viewModel.setSelectedMediaUri(uri) },
                        onSelectFilter = { filter -> viewModel.selectFilter(filter) },
                        onPublish = { caption, loc, landmark, lat, lng, soundTitle ->
                            viewModel.publishContent(caption, loc, landmark, lat, lng, soundTitle)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Broadcasting to locality... Uploaded successfully! 🎉")
                            }
                        },
                        onDetectLocationClick = {
                            viewModel.detectCurrentLocation(context)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("GPS Location auto-detected: ${currentLocation?.landmark ?: "Pike Place"}")
                            }
                        },
                        onCancel = { viewModel.selectTab(MainNavigationTab.FEED) }
                    )
                }

                MainNavigationTab.REELS -> {
                    ReelsScreen(
                        reels = reels,
                        isSoundMuted = isSoundMuted,
                        onToggleSound = { viewModel.toggleSoundMute() },
                        onLikeReel = { reel -> viewModel.toggleReelLike(reel) },
                        onCommentReel = { reel -> viewModel.openComments("REEL", reel.id) },
                        onShareReel = { reel ->
                            val postEquivalent = posts.firstOrNull { it.id == reel.id }
                                ?: PostEntity(
                                    username = reel.username,
                                    userAvatar = reel.userAvatar,
                                    userHandle = reel.userHandle,
                                    isVerified = reel.isVerified,
                                    mediaUrl = reel.mediaUrl,
                                    mediaType = "VIDEO",
                                    caption = reel.caption,
                                    likesCount = reel.likesCount,
                                    commentsCount = reel.commentsCount,
                                    isLiked = reel.isLiked,
                                    isSaved = reel.isSaved,
                                    isFollowing = reel.isFollowing,
                                    location = reel.location,
                                    landmark = reel.landmark,
                                    distanceKm = reel.distanceKm,
                                    isNeighbor = reel.isNeighbor,
                                    soundTitle = reel.soundTitle,
                                    filterName = reel.filterName
                                )
                            viewModel.openShareSheet(postEquivalent)
                        },
                        onSaveReel = { reel -> viewModel.toggleReelSave(reel) },
                        onFollowToggle = { reel -> viewModel.toggleReelFollow(reel) },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onWaveClick = { reel ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Waved at @${reel.username}! 👋")
                            }
                        }
                    )
                }

                MainNavigationTab.PROFILE -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        posts = posts.filter { it.username == userProfile.username || it.id == 5L },
                        reels = reels,
                        marketplaceItems = marketplaceItems,
                        savedPosts = savedPosts,
                        activeTab = profileTab,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        isLoggedOut = isLoggedOut,
                        otherUsers = otherUsers,
                        onTabChange = { tab -> viewModel.setProfileTab(tab) },
                        onLocationToggle = { enabled ->
                            viewModel.setLocationEnabled(enabled)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (enabled) "Location Radar Enabled 📡" else "Location Radar Disabled 🔒")
                            }
                        },
                        onPrivateToggle = { isPrivate ->
                            viewModel.setPrivateAccount(isPrivate)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(if (isPrivate) "Private Account (Ghost Mode) Enabled 👻" else "Public Visibility Enabled 🌐")
                            }
                        },
                        onOpenPrivacySettings = { viewModel.openPrivacySettings() },
                        onEditProfile = { name, bio, site, avatar ->
                            viewModel.updateProfile(name, bio, site, avatar)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Profile updated!")
                            }
                        },
                        onPostClick = { post -> viewModel.openComments("POST", post.id) },
                        onReelClick = { reel -> viewModel.selectTab(MainNavigationTab.REELS) },
                        onDeletePost = { postId ->
                            viewModel.deletePost(postId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Post deleted from profile.")
                            }
                        },
                        onDeleteReel = { reelId ->
                            viewModel.deleteReel(reelId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Neighborhood clip deleted.")
                            }
                        },
                        onUpdateReelDetails = { reelId, caption, loc, landmark ->
                            viewModel.updateReelDetails(reelId, caption, loc, landmark)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip details updated.")
                            }
                        },
                        onDeleteMarketItem = { itemId ->
                            viewModel.deleteMarketplaceItem(itemId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Market listing deleted.")
                            }
                        },
                        onToggleMarketItemAvailability = { item ->
                            viewModel.toggleMarketItemAvailability(item)
                            coroutineScope.launch {
                                val status = if (item.isAvailable) "marked as Sold" else "marked as Active"
                                snackbarHostState.showSnackbar("Listing $status.")
                            }
                        },
                        onUpdateMarketItem = { item ->
                            viewModel.updateMarketplaceItem(item)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Market listing updated.")
                            }
                        },
                        onOpenSellItemDialog = { viewModel.openSellItemDialog() },
                        onMarketItemClick = { item -> viewModel.selectMarketplaceItem(item) },
                        onSwitchUser = { user ->
                            viewModel.switchUser(user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Switched profile to @${user.username}!")
                            }
                        },
                        onLogout = {
                            viewModel.logoutUser()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Logged out successfully.")
                            }
                        },
                        onLogin = {
                            viewModel.loginUser()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Welcome back, @${userProfile.username}!")
                            }
                        },
                        onOpenSignUp = { viewModel.openSignUpDialog() },
                        onOpenLegalPolicy = { viewModel.openLegalAgreement() },
                        onResetDemoData = {
                            viewModel.resetDemoProfile()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Demo profile and content reset.")
                            }
                        },
                        onCreateContentClick = {
                            viewModel.selectTab(MainNavigationTab.CREATE)
                        }
                    )
                }
            }
        }
    }

    // Other User Profile Bottom Sheet
    if (selectedOtherUser != null) {
        val otherUser = selectedOtherUser!!
        val userPosts by viewModel.getUserPosts(otherUser.username).collectAsStateWithLifecycle(emptyList())
        val userReels by viewModel.getUserReels(otherUser.username).collectAsStateWithLifecycle(emptyList())

        OtherUserProfileSheet(
            user = otherUser,
            posts = userPosts,
            reels = userReels,
            onDismiss = { viewModel.closeOtherUserProfile() },
            onFollowToggle = { viewModel.toggleOtherUserFollow(otherUser) },
            onWaveClick = {
                viewModel.waveAtNeighbor(otherUser)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Waved at ${otherUser.username}! 👋 Friend request sent.")
                }
            },
            onDirectMessageClick = {
                viewModel.closeOtherUserProfile()
                viewModel.startChatWithUser(otherUser)
            },
            onPostClick = { post -> viewModel.openComments("POST", post.id) },
            onReelClick = { reel -> viewModel.selectTab(MainNavigationTab.REELS) }
        )
    }

    // Story Fullscreen Viewer Dialog
    if (activeStory != null) {
        StoryViewerDialog(
            stories = stories,
            initialStory = activeStory!!,
            onDismiss = { viewModel.closeStory() },
            onStoryViewed = { id -> viewModel.openStory(stories.first { it.id == id }) },
            onSendReply = { reply ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Reply sent to ${activeStory?.username} ✈️")
                }
            }
        )
    }

    // Comments Sheet
    if (activeCommentsTarget != null) {
        val target = activeCommentsTarget!!
        val targetComments by viewModel.getCommentsForTarget(target.first, target.second)
            .collectAsStateWithLifecycle(emptyList())

        CommentsBottomSheet(
            comments = targetComments,
            userProfile = userProfile,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { text -> viewModel.addComment(text) },
            onToggleCommentLike = { id, liked -> viewModel.toggleCommentLike(id, liked) }
        )
    }

    // Share Sheet
    if (activeSharePost != null) {
        val post = activeSharePost!!
        ShareBottomSheet(
            targetTitle = "Pulse by ${post.username} • ${post.landmark ?: post.location ?: "Seattle"}",
            onDismiss = { viewModel.closeShareSheet() },
            onShareSuccess = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }

    // Notifications Sheet
    if (showNotificationsSheet) {
        NotificationsBottomSheet(
            notifications = notifications,
            onDismiss = { viewModel.closeNotificationsSheet() }
        )
    }

    // Direct Messages Sheet
    if (showDirectMessagesSheet) {
        DirectMessagesBottomSheet(
            conversations = conversations,
            activeConversation = activeConversation,
            chatMessages = activeChatMessages,
            userProfile = userProfile,
            onDismiss = { viewModel.closeDirectMessagesSheet() },
            onSelectConversation = { conv -> viewModel.openConversation(conv) },
            onBackToInbox = { viewModel.closeActiveConversation() },
            onSendMessage = { text -> viewModel.sendChatMessage(text) },
            onCreateGroupChat = { title ->
                viewModel.createGroupChat(title)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Group channel '$title' created! 👥")
                }
            }
        )
    }

    // Privacy & Security Settings Screen Overlay
    if (showPrivacySettings) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.closePrivacySettings() },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            PrivacySettingsScreen(
                privacySettings = privacySettings,
                onBackClick = { viewModel.closePrivacySettings() },
                onUpdateLocationRadar = { enabled ->
                    viewModel.setLocationEnabled(enabled)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (enabled) "Location Radar Enabled 📡" else "Location Radar Disabled (Off Grid) 🔒")
                    }
                },
                onUpdatePrivateAccount = { isPrivate ->
                    viewModel.setPrivateAccount(isPrivate)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (isPrivate) "Private Account (Ghost Mode) Enabled 👻" else "Public Visibility Enabled 🌐")
                    }
                },
                onUpdateNearbyDiscovery = { allow ->
                    viewModel.updateNearbyDiscovery(allow)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (allow) "Nearby discovery enabled 🗺️" else "Nearby discovery hidden 🙈")
                    }
                },
                onUpdatePreciseLocation = { precise ->
                    viewModel.updatePreciseLocation(precise)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (precise) "Precise distance enabled (e.g. 0.6 km)" else "Approximate neighborhood mode enabled")
                    }
                },
                onUpdateNearbyWaves = { allow ->
                    viewModel.updateNearbyWaves(allow)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (allow) "Proximity waves enabled 👋" else "Proximity waves muted 🔕")
                    }
                },
                onUpdateActiveStatus = { show ->
                    viewModel.updateActiveStatus(show)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (show) "Active status visible 🟢" else "Active status hidden")
                    }
                },
                onUpdateReadReceipts = { enabled ->
                    viewModel.updateReadReceipts(enabled)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (enabled) "Read receipts enabled (Seen) ✓✓" else "Read receipts disabled")
                    }
                },
                onUpdateCommentsPrivacy = { option ->
                    viewModel.updateCommentsPrivacy(option)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Comments privacy updated: $option")
                    }
                },
                onUpdateDirectMessagesPrivacy = { option ->
                    viewModel.updateDirectMessagesPrivacy(option)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Messages privacy updated: $option")
                    }
                },
                onUpdateTagsAndMentionsPrivacy = { option ->
                    viewModel.updateTagsAndMentionsPrivacy(option)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Tags & Mentions updated: $option")
                    }
                },
                onUpdateHideMomentsFromStrangers = { hide ->
                    viewModel.updateHideMomentsFromStrangers(hide)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (hide) "Stories hidden from strangers 🔒" else "Stories visible to nearby feed 🌐")
                    }
                },
                onUpdatePostResharing = { allow ->
                    viewModel.updatePostResharing(allow)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (allow) "Post resharing enabled 🔄" else "Post resharing disabled 🚫")
                    }
                },
                onUpdateSensitiveContentFilter = { filter ->
                    viewModel.updateSensitiveContentFilter(filter)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Sensitive content filter set to $filter")
                    }
                },
                onOpenLegalPolicy = { viewModel.openLegalAgreement() },
                onResetDefaults = {
                    viewModel.updatePrivacySettings(com.example.data.InitialData.defaultPrivacySettings)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Privacy settings reset to defaults 🔄")
                    }
                }
            )
        }
    }

    // Legal Agreement & Privacy Policy Screen Overlay
    if (showLegalAgreement) {
        LegalAgreementScreen(
            currentUsername = userProfile.username,
            consentRecord = legalConsentRecord,
            onBackClick = { viewModel.closeLegalAgreement() },
            onAcceptAgreement = {
                viewModel.acceptLegalAgreement()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Legal Agreement & Privacy Policy Acknowledged! ⚖️")
                }
            }
        )
    }

    // Sign Up & Account Creation Dialog with Mandatory Legal Consent
    if (showSignUpDialog) {
        SignUpDialog(
            onDismissRequest = { viewModel.closeSignUpDialog() },
            onOpenLegalPolicy = { viewModel.openLegalAgreement() },
            onSignUpSuccess = { username, fullName, avatarUrl, bio, neighborhood, enableLocationRadar, timestamp ->
                viewModel.createAccount(
                    username = username,
                    fullName = fullName,
                    avatarUrl = avatarUrl,
                    bio = bio,
                    neighborhood = neighborhood,
                    enableLocationRadar = enableLocationRadar,
                    legalConsentTimestamp = timestamp
                )
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Welcome to Localiiiy, @$username! 🎉 Legal consent recorded.")
                }
            }
        )
    }

    // Eyecatching Opening Animation Overlay
    if (showOpeningAnimation) {
        LocaliOpeningAnimation(
            onAnimationFinished = { showOpeningAnimation = false }
        )
    }
    }
}

// Alias for compatibility
@Composable
fun InstagramApp(
    viewModel: InstagramViewModel = viewModel()
) = LocaliApp(viewModel = viewModel)

@Composable
fun LocaliBottomNavigationBar(
    currentTab: MainNavigationTab,
    userAvatarUrl: String,
    onTabSelected: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulse Feed Tab
            LocaliNavItem(
                icon = if (currentTab == MainNavigationTab.FEED) Icons.Default.DynamicFeed else Icons.Outlined.DynamicFeed,
                label = "Pulse",
                isSelected = currentTab == MainNavigationTab.FEED,
                onClick = { onTabSelected(MainNavigationTab.FEED) },
                testTag = "nav_tab_feed"
            )

            // Radar Explore Tab
            LocaliNavItem(
                icon = if (currentTab == MainNavigationTab.EXPLORE) Icons.Default.Radar else Icons.Outlined.Radar,
                label = "Radar",
                isSelected = currentTab == MainNavigationTab.EXPLORE,
                onClick = { onTabSelected(MainNavigationTab.EXPLORE) },
                testTag = "nav_tab_explore"
            )

            // Market Tab
            LocaliNavItem(
                icon = if (currentTab == MainNavigationTab.MARKET) Icons.Default.Storefront else Icons.Outlined.Storefront,
                label = "Market",
                isSelected = currentTab == MainNavigationTab.MARKET,
                onClick = { onTabSelected(MainNavigationTab.MARKET) },
                testTag = "nav_tab_market"
            )

            // Vibes / Clips Tab
            LocaliNavItem(
                icon = if (currentTab == MainNavigationTab.REELS) Icons.Default.PlayCircle else Icons.Outlined.PlayCircle,
                label = "Clips",
                isSelected = currentTab == MainNavigationTab.REELS,
                onClick = { onTabSelected(MainNavigationTab.REELS) },
                testTag = "nav_tab_reels"
            )

            // Profile Tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(MainNavigationTab.PROFILE) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("nav_tab_profile"),
                contentAlignment = Alignment.Center
            ) {
                val isSelected = currentTab == MainNavigationTab.PROFILE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .then(
                                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .padding(if (isSelected) 2.dp else 0.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userAvatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        text = "Space",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Alias for compatibility
@Composable
fun InstagramBottomNavigationBar(
    currentTab: MainNavigationTab,
    userAvatarUrl: String,
    onTabSelected: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) = LocaliBottomNavigationBar(
    currentTab = currentTab,
    userAvatarUrl = userAvatarUrl,
    onTabSelected = onTabSelected,
    modifier = modifier
)

@Composable
private fun LocaliNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

