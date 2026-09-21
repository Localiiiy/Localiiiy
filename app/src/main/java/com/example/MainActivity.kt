package com.example

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Context
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.util.HotspotAlert
import com.example.util.LocaliiiyLanguage
import com.example.util.LocaliiiyCurrency
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyStringKey
import com.example.ui.CreationMode
import com.example.ui.LocaliiiyViewModel
import com.example.ui.MainNavigationTab
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.LocaliiiyNavScreen
import com.example.ui.navigation.LocaliiiyNavigationComposeBottomBar
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.LocaliiiyAccentCoral
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyStoryGradient
import com.example.ui.theme.LocaliiiyTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity(), SensorEventListener {
    private val deepLinkClipIdState = mutableStateOf<Long?>(null)
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    
    // Emergency Panic Cloak Gesture
    private val isPanicCloakActive = mutableStateOf(false)
    private var lastUpdate: Long = 0
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f
    private val SHAKE_THRESHOLD = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        parseDeepLink(intent)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:109876543210:android:abcdef0123456789")
                    .setProjectId("Localiiiy-app")
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "FirebaseApp init fallback: ${e.message}")
        }
        enableEdgeToEdge()
        setContent {
            val viewModel: com.example.ui.LocaliiiyViewModel = viewModel()
            val privacySettings by viewModel.privacySettings.collectAsStateWithLifecycle()
            
            LocaliiiyTheme(
                themeKey = privacySettings.appThemeBackground,
                displayScale = privacySettings.appDisplayScale
            ) {
                if (isPanicCloakActive.value) {
                    // Decoy Screen (tap to return)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .clickable {
                                isPanicCloakActive.value = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Calculator",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Tap anywhere to exit decoy mode",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    LocaliiiyApp(
                        deepLinkClipId = deepLinkClipIdState.value,
                        onClearDeepLink = { deepLinkClipIdState.value = null }
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if (lastUpdate == 0L) {
                lastUpdate = curTime
                last_x = event.values[0]
                last_y = event.values[1]
                last_z = event.values[2]
                return
            }
            if ((curTime - lastUpdate) > 150) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > 3500) {
                    // Activate Panic Cloak
                    isPanicCloakActive.value = true
                }
                
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        parseDeepLink(intent)
    }

    private fun parseDeepLink(intent: android.content.Intent?) {
        val data = intent?.data ?: return
        try {
            val pathSegments = data.pathSegments
            val clipIndex = pathSegments.indexOf("clip")
            if (clipIndex != -1 && clipIndex + 1 < pathSegments.size) {
                val id = pathSegments[clipIndex + 1].toLongOrNull()
                if (id != null) deepLinkClipIdState.value = id
            } else if (pathSegments.isNotEmpty()) {
                val id = pathSegments.last().toLongOrNull()
                if (id != null) deepLinkClipIdState.value = id
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error parsing deep link: ${e.message}")
        }
    }
}

@Composable
fun LocaliiiyApp(
    viewModel: LocaliiiyViewModel = viewModel(),
    deepLinkClipId: Long? = null,
    onClearDeepLink: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val feedPosts by viewModel.feedPosts.collectAsStateWithLifecycle()
    val clips by viewModel.allClips.collectAsStateWithLifecycle()
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
    val activeHotspotAlert by viewModel.activeHotspotAlert.collectAsStateWithLifecycle()
    val isDetectingLocation by viewModel.isDetectingLocation.collectAsStateWithLifecycle()
    val isLocationEnabled by viewModel.isLocationEnabled.collectAsStateWithLifecycle()
    val isPrivateAccount by viewModel.isPrivateAccount.collectAsStateWithLifecycle()
    val nearbyRadiusKm by viewModel.nearbyRadiusKm.collectAsStateWithLifecycle()
    val radarRadiusKm by viewModel.radarRadiusKm.collectAsStateWithLifecycle()
    val radarBlipUsers by viewModel.radarBlipUsers.collectAsStateWithLifecycle()
    val showMeOnRadar by viewModel.showMeOnRadar.collectAsStateWithLifecycle()
    val isRefreshingFeed by viewModel.isRefreshingFeed.collectAsStateWithLifecycle()
    val isRefreshingExplore by viewModel.isRefreshingExplore.collectAsStateWithLifecycle()
    val isRefreshingMarket by viewModel.isRefreshingMarket.collectAsStateWithLifecycle()
    val isRefreshingStudio by viewModel.isRefreshingStudio.collectAsStateWithLifecycle()
    val isRefreshingClips by viewModel.isRefreshingClips.collectAsStateWithLifecycle()
    val isRefreshingProfile by viewModel.isRefreshingProfile.collectAsStateWithLifecycle()

    val selectedOtherUser by viewModel.selectedOtherUser.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val activeChatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()

    val privacySettings by viewModel.privacySettings.collectAsStateWithLifecycle()
    val showPrivacySettings by viewModel.showPrivacySettings.collectAsStateWithLifecycle()
    val showUserActivityLog by viewModel.showUserActivityLog.collectAsStateWithLifecycle()
    val allUserActivities by viewModel.allUserActivities.collectAsStateWithLifecycle()
    val showBlockedUsersScreen by viewModel.showBlockedUsersScreen.collectAsStateWithLifecycle()
    val blockedUsernames by viewModel.blockedUsernames.collectAsStateWithLifecycle()
    val showLegalAgreement by viewModel.showLegalAgreement.collectAsStateWithLifecycle()
    val showCyberstalkingSafetyScreen by viewModel.showCyberstalkingSafetyScreen.collectAsStateWithLifecycle()
    val cyberstalkingPrefilledUsername by viewModel.cyberstalkingPrefilledUsername.collectAsStateWithLifecycle()
    val allCyberstalkingIncidents by viewModel.allCyberstalkingIncidents.collectAsStateWithLifecycle()
    val showSignUpDialog by viewModel.showSignUpDialog.collectAsStateWithLifecycle()
    val showAuthScreen by viewModel.showAuthScreen.collectAsStateWithLifecycle()
    val authReason by viewModel.authReason.collectAsStateWithLifecycle()
    val authInitialMode by viewModel.authInitialMode.collectAsStateWithLifecycle()
    val legalConsentRecord by viewModel.legalConsentRecord.collectAsStateWithLifecycle()

    val creationMode by viewModel.creationMode.collectAsStateWithLifecycle()
    val selectedMediaUri by viewModel.selectedMediaUri.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val autoDetectedLocation by viewModel.autoDetectedLocation.collectAsStateWithLifecycle()

    val marketplaceItems by viewModel.allMarketplaceItems.collectAsStateWithLifecycle()
    val selectedMarketCategory by viewModel.selectedMarketCategory.collectAsStateWithLifecycle()
    val globalSearchQuery by viewModel.globalSearchQuery.collectAsStateWithLifecycle()
    val marketplaceSearchQuery by viewModel.marketplaceSearchQuery.collectAsStateWithLifecycle()
    val marketplaceLocationQuery by viewModel.marketplaceLocationQuery.collectAsStateWithLifecycle()
    val marketplaceRadiusKm by viewModel.marketplaceRadiusKm.collectAsStateWithLifecycle()
    val marketSortOption by viewModel.marketSortOption.collectAsStateWithLifecycle()
    val selectedMarketplaceItem by viewModel.selectedMarketplaceItem.collectAsStateWithLifecycle()
    val showSellItemDialog by viewModel.showSellItemDialog.collectAsStateWithLifecycle()

    val studioVideos by viewModel.allStudioVideos.collectAsStateWithLifecycle()
    val selectedStudioCategory by viewModel.selectedStudioCategory.collectAsStateWithLifecycle()
    val studioSortOption by viewModel.studioSortOption.collectAsStateWithLifecycle()
    val studioScopeFilter by viewModel.studioScopeFilter.collectAsStateWithLifecycle()
    val studioSearchQuery by viewModel.studioSearchQuery.collectAsStateWithLifecycle()
    val activeStudioVideo by viewModel.activeStudioVideo.collectAsStateWithLifecycle()
    val isStudioVideoPlaying by viewModel.isStudioVideoPlaying.collectAsStateWithLifecycle()
    val studioPlaybackProgress by viewModel.studioPlaybackProgress.collectAsStateWithLifecycle()
    val isStudioUploadSheetOpen by viewModel.isStudioUploadSheetOpen.collectAsStateWithLifecycle()
    val showStudioCreatorDashboard by viewModel.showStudioCreatorDashboard.collectAsStateWithLifecycle()

    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val showLanguageCurrencyDialog by viewModel.showLanguageCurrencyDialog.collectAsStateWithLifecycle()
    val showHelpSheet by viewModel.showHelpSheet.collectAsStateWithLifecycle()
    val showInformationSheet by viewModel.showInformationSheet.collectAsStateWithLifecycle()
    val showDataAnalysis by viewModel.showDataAnalysis.collectAsStateWithLifecycle()
    val showMonetizationHub by viewModel.showMonetizationHub.collectAsStateWithLifecycle()
    val showBoostAdDialog by viewModel.showBoostAdDialog.collectAsStateWithLifecycle()
    val showAtmosphericThemeBottomSheet by viewModel.showAtmosphericThemeBottomSheet.collectAsStateWithLifecycle()
    val creatorEarnings by viewModel.creatorEarnings.collectAsStateWithLifecycle()
    val payoutAccount by viewModel.payoutAccount.collectAsStateWithLifecycle()
    val payoutHistory by viewModel.payoutHistory.collectAsStateWithLifecycle()
    val dailyCheckInState by viewModel.dailyCheckInState.collectAsStateWithLifecycle()
    val referralState by viewModel.referralState.collectAsStateWithLifecycle()
    val platformMetrics by viewModel.platformMetrics.collectAsStateWithLifecycle()
    val sponsoredAds by viewModel.sponsoredAds.collectAsStateWithLifecycle()
    val allDraftClips by viewModel.allDraftClips.collectAsStateWithLifecycle()
    val selectedDraftClipForEdit by viewModel.selectedDraftClipForEdit.collectAsStateWithLifecycle()
    val merchantBounties by viewModel.merchantBounties.collectAsStateWithLifecycle()

    var showOpeningAnimation by remember { mutableStateOf(true) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.detectCurrentLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        val fineGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted && !coarseGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.detectCurrentLocation(context)
        }
        com.example.service.FavoriteProximityManager.init(context)
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START,
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> viewModel.setIsAppInForeground(true)
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> viewModel.setIsAppInForeground(false)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle deep link routing
    LaunchedEffect(deepLinkClipId) {
        if (deepLinkClipId != null) {
            viewModel.selectTab(MainNavigationTab.CLIPS)
            snackbarHostState.showSnackbar("Opening creator clip #$deepLinkClipId 🎥")
        }
    }



    val defaultBackground = MaterialTheme.colorScheme.background
    val backgroundModifier = remember(privacySettings.appThemeBackground, defaultBackground) {
        when (privacySettings.appThemeBackground) {
            "BLACK_HOLE" -> Modifier.background(Brush.linearGradient(listOf(Color(0xFF020205), Color(0xFF1E0B38), Color(0xFFFF6A00))))
            "MOON" -> Modifier.background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF334155), Color(0xFF38BDF8))))
            "GALAXY" -> Modifier.background(Brush.linearGradient(listOf(Color(0xFF070414), Color(0xFF4338CA), Color(0xFFD946EF))))
            else -> Modifier.background(defaultBackground)
        }
    }

    if (isLoggedOut) {
        AuthScreen(
            initialMode = authInitialMode,
            securityReason = authReason,
            onDismiss = {
                // Mandatory Gate: app requires login/registration before accessing dashboard
            },
            onGhostSpectatorSuccess = {
                viewModel.enterAsGhostSpectator()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Stealth Spectator active 👻 • Zero GPS Footprint")
                }
            },
            onScrubIdentity = {
                viewModel.resetSessionAndPurge()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Local caches purged & session reset. 🧹")
                }
            },
            onAuthSuccess = { user, username, fullName, neighborhood ->
                viewModel.handleAuthSuccess(user, username, fullName, neighborhood)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Welcome @$username! Logged in securely. 🔒✨")
                }
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().then(backgroundModifier)) {
        if (privacySettings.appThemeBackground == "CUSTOM" && privacySettings.customBackgroundImageUri.isNotBlank()) {
            AsyncImage(
                model = privacySettings.customBackgroundImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))
        }

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (currentTab == MainNavigationTab.FEED) {
                    Box(modifier = Modifier.zIndex(100f)) {
                        LocaliiiyTopBar(
                            hasUnreadNotifications = notifications.any { !it.isRead },
                            hasUnreadMessages = conversations.any { !it.isRead },
                            currentLocationLabel = currentLocation?.landmark ?: "Pike Place, Seattle",
                            isLocationEnabled = isLocationEnabled,
                            isPrivateAccount = isPrivateAccount,
                            searchQuery = globalSearchQuery,
                            currentLanguage = currentLanguage,
                            currentCurrency = currentCurrency,
                            onSearchQueryChange = { q -> viewModel.setGlobalSearchQuery(q) },
                            onLogoClick = { showOpeningAnimation = true },
                            onLanguageCurrencyClick = { viewModel.openLanguageCurrencyDialog() },
                            onNotificationsClick = { viewModel.openNotificationsSheet() },
                            onDirectMessagesClick = { viewModel.openDirectMessagesSheet() },
                            onThemeClick = { viewModel.openAtmosphericThemeBottomSheet() },
                            onCreateClick = {
                                viewModel.setCreationMode(CreationMode.POST)
                                viewModel.selectTab(MainNavigationTab.CREATE)
                            },
                            onLocationClick = {
                                val fineGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                val coarseGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                if (!fineGranted && !coarseGranted) {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else {
                                    if (!isLocationEnabled) {
                                        viewModel.setLocationEnabled(true)
                                    }
                                    viewModel.detectCurrentLocation(context)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Detecting accurate GPS radar location... 📍")
                                    }
                                }
                            }
                        )
                    }
                }
            },
        bottomBar = {
            if (currentTab != MainNavigationTab.CREATE) {
                DeckSwitcherBar(
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
            // App Atmospheric Background (Black Hole, Moon, Galaxy, Custom Image, etc.)
            AppAtmosphereBackground(
                themeKey = privacySettings.appThemeBackground,
                customImageUri = privacySettings.customBackgroundImageUri,
                modifier = Modifier.fillMaxSize()
            )

            when (currentTab) {
                MainNavigationTab.FEED -> {
                    val allClips by viewModel.allClips.collectAsState()
                    val allStudioVideos by viewModel.allStudioVideos.collectAsState()
                    FeedScreen(
                        clips = allClips.filter { it.isFollowing },
                        posts = feedPosts,
                        stories = stories,
                        userProfile = userProfile,
                        selectedRadiusKm = nearbyRadiusKm,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        isRefreshing = isRefreshingFeed,
                        onRefresh = { viewModel.refreshPulseFeed() },
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
                        onPostClick = { post -> viewModel.selectExplorePost(post) },
                        onReportPost = { post, reason ->
                            viewModel.reportPost(post.id, reason)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Post reported for moderation review.")
                            }
                        },
                        sponsoredAds = sponsoredAds,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        onAdImpression = { id -> viewModel.recordAdImpression(id) },
                        onAdClick = { id -> viewModel.recordAdClick(id) },
                        onBoostPostClick = { viewModel.openBoostAdDialog() },
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },
                        countryName = privacySettings.radarCountryName
                    )
                }

                MainNavigationTab.EXPLORE -> {
                    val allClips by viewModel.allClips.collectAsState()
                    ExploreScreen(
                        clips = allClips,
                        marketplaceItems = marketplaceItems,
                        posts = posts,
                        userProfile = userProfile,
                        nearbyUsers = radarBlipUsers,
                        selectedRadiusKm = nearbyRadiusKm,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        privacySettings = privacySettings,
                        onRadiusFilterChange = { radius ->
                            if (radius != null) {
                                viewModel.setRadarRadiusKm(radius.toFloat())
                            } else {
                                viewModel.setNearbyRadiusFilter(null)
                            }
                        },
                        onToggleHidePreciseLocation = { hide ->
                            viewModel.updatePrivacySettings(privacySettings.copy(hidePreciseLocationOnRadar = hide))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    if (hide) "Precise radar location hidden 🛡️ (Simulated as ${privacySettings.radarObfuscatedRange})"
                                    else "Precise radar location visible 📡"
                                )
                            }
                        },
                        onSelectObfuscatedRange = { range ->
                            viewModel.updatePrivacySettings(privacySettings.copy(radarObfuscatedRange = range))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Live Radar range set to '$range' 🌌")
                            }
                        },
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
                        onReportPost = { post, reason ->
                            viewModel.reportPost(post.id, reason)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Post reported for moderation review.")
                            }
                        },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onWaveAtUser = { user ->
                            viewModel.waveAtNeighbor(user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Waved at ${user.username}! 👋")
                            }
                        },
                        isRefreshing = isRefreshingExplore,
                        onRefresh = { viewModel.refreshExplore() },
                        activeRadarPerk = dailyCheckInState.activePerks.firstOrNull { !it.isExpired }
                    )
                }

                MainNavigationTab.MARKET -> {
                    MarketScreen(
                        items = marketplaceItems,
                        posts = posts,
                        clips = clips,
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
                        onOpenSellDialog = {
                            if (isLoggedOut) {
                                viewModel.openAuthScreen(
                                    reason = "Authentication Required: Please sign in or register with Firebase to list marketplace items and protect local commerce.",
                                    initialMode = com.example.ui.screens.AuthScreenMode.LOGIN
                                )
                            } else {
                                viewModel.openSellItemDialog()
                            }
                        },
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
                        onPublishBuySellClip = { title, desc, price, cat, cond, videoUrl, soundTitle, loc, landmark ->
                            viewModel.publishMarketBuySellClip(title, desc, price, cat, cond, videoUrl, soundTitle, loc, landmark)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Market Clip published to Clips! 🎬")
                            }
                        },
                        onCloseDetailSheet = { viewModel.selectMarketplaceItem(null) },
                        onMessageSeller = { item -> viewModel.startChatForMarketItem(item) },
                        onToggleAvailability = { item -> viewModel.toggleMarketItemAvailability(item) },
                        onFlagItem = { item, reason ->
                            viewModel.reportContent("MARKETPLACE", item.id, item.sellerUsername, reason)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Listing reported for moderation review.")
                            }
                        },
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        sortOption = marketSortOption,
                        onSortOptionChange = { opt -> viewModel.setMarketSortOption(opt) },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onOpenComments = { type, id -> viewModel.openComments(type, id) },
                        isRefreshing = isRefreshingMarket,
                        onRefresh = { viewModel.refreshMarket() },
                        countryName = privacySettings.radarCountryName,
                        onConvertClipToMarket = { clipId, price, condition, hubName ->
                            viewModel.convertClipToMarketplaceListing(clipId, price, condition, hubName)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip converted to Market Listing! 🛍️")
                            }
                        },
                        isPremiumSubscribed = privacySettings.isPremiumSubscribed
                    )
                }

                MainNavigationTab.STUDIO -> {
                    StudioScreen(
                        videos = studioVideos,
                        userProfile = userProfile,
                        selectedCategory = selectedStudioCategory,
                        studioSortOption = studioSortOption,
                        onStudioSortChange = { opt -> viewModel.setStudioSortOption(opt) },
                        studioScopeFilter = studioScopeFilter,
                        onStudioScopeChange = { scope -> viewModel.setStudioScopeFilter(scope) },
                        countryName = privacySettings.radarCountryName,
                        searchQuery = studioSearchQuery,
                        activeVideo = activeStudioVideo,
                        isPlaying = isStudioVideoPlaying,
                        playbackProgress = studioPlaybackProgress,
                        isUploadSheetOpen = isStudioUploadSheetOpen,
                        showCreatorDashboard = showStudioCreatorDashboard,
                        onCategorySelected = { cat -> viewModel.setStudioCategory(cat) },
                        onSearchQueryChange = { q -> viewModel.setStudioSearchQuery(q) },
                        onVideoClick = { video -> viewModel.openStudioVideo(video) },
                        onCloseVideo = { viewModel.closeStudioVideo() },
                        onTogglePlayPause = { viewModel.toggleStudioPlayPause() },
                        onSeek = { progress -> viewModel.setStudioPlaybackProgress(progress) },
                        onLikeVideo = { video -> viewModel.toggleStudioVideoLike(video) },
                        onSaveVideo = { video -> viewModel.toggleStudioVideoSave(video) },
                        onSubscribeCreator = { username -> viewModel.toggleStudioCreatorSubscription(username) },
                        onOpenUploadSheet = { viewModel.openStudioUploadSheet() },
                        onCloseUploadSheet = { viewModel.closeStudioUploadSheet() },
                        onToggleCreatorDashboard = { viewModel.toggleStudioCreatorDashboard() },
                        onUploadVideo = { title, desc, cat, duration, videoUrl, thumbUrl, res, tags, chapters ->
                            val success = viewModel.uploadStudioVideo(title, desc, cat, duration, videoUrl, thumbUrl, res, tags, chapters)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Video successfully published to Localiiiy Studio! 🎬")
                            }
                            success
                        },
                        onDeleteVideo = { id ->
                            viewModel.deleteStudioVideo(id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Video removed from Studio.")
                            }
                        },
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        creatorEarnings = creatorEarnings,
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },
                        onOpenBoostAds = { viewModel.openBoostAdDialog() },
                        onOpenLanguageCurrency = { viewModel.openLanguageCurrencyDialog() },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        isRefreshing = isRefreshingStudio,
                        onRefresh = { viewModel.refreshStudio() },
                        bounties = merchantBounties,
                        onClaimBounty = { bounty ->
                            viewModel.claimMerchantBounty(bounty)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Claimed $${bounty.bountyRewardUSD.toInt()} sponsorship bounty! Deposited to Wallet.")
                            }
                        },
                        drafts = allDraftClips,
                        onRouteDraftToClips = { draft ->
                            viewModel.routeDraftToDestination(draft, "CLIPS")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Draft routed to Clips Engine! ⚡")
                            }
                        },
                        onRouteDraftToMarket = { draft ->
                            viewModel.routeDraftToDestination(draft, "MARKET")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Draft routed to Market Listing! 🛍️")
                            }
                        },
                        onRouteDraftToPulse = { draft ->
                            viewModel.routeDraftToDestination(draft, "PULSE")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Draft routed to Pulse Feed! 📡")
                            }
                        },
                        onDeleteDraft = { draft ->
                            viewModel.deleteDraftClip(draft.id)
                        }
                    )
                }

                MainNavigationTab.CREATE -> {
                    if (isLoggedOut) {
                        AuthRequiredGatingView(
                            onAuthenticateClick = {
                                viewModel.openAuthScreen(
                                    reason = "Sign in or register with Firebase to publish posts, broadcast clips, and protect community content.",
                                    initialMode = com.example.ui.screens.AuthScreenMode.LOGIN
                                )
                            },
                            onCancel = { viewModel.selectTab(MainNavigationTab.FEED) }
                        )
                    } else {
                        CreateScreen(
                            creationMode = creationMode,
                            selectedMediaUri = selectedMediaUri,
                            selectedFilter = selectedFilter,
                            detectedLocation = autoDetectedLocation ?: currentLocation,
                            draftClips = allDraftClips,
                            editingDraft = selectedDraftClipForEdit,
                            onSaveDraftClip = { mediaUri, caption, soundTitle, loc, landmark, draftId ->
                                viewModel.saveDraftClip(
                                    mediaUri = mediaUri,
                                    caption = caption,
                                    soundTitle = soundTitle,
                                    location = loc,
                                    landmark = landmark,
                                    draftId = draftId
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Clip draft saved to local Room database 💾")
                                }
                            },
                            onDeleteDraftClip = { draftId ->
                                viewModel.deleteDraftClip(draftId)
                            },
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
                }

                MainNavigationTab.CLIPS -> {
                    ClipsScreen(
                        clips = clips,
                        isSoundMuted = isSoundMuted,
                        onToggleSound = { viewModel.toggleSoundMute() },
                        onLikeClip = { clip -> viewModel.toggleClipLike(clip) },
                        onCommentClip = { clip -> viewModel.openComments("CLIP", clip.id) },
                        onShareClip = { clip ->
                            val postEquivalent = posts.firstOrNull { it.id == clip.id }
                                ?: PostEntity(
                                    username = clip.username,
                                    userAvatar = clip.userAvatar,
                                    userHandle = clip.userHandle,
                                    isVerified = clip.isVerified,
                                    mediaUrl = clip.mediaUrl,
                                    mediaType = "VIDEO",
                                    caption = clip.caption,
                                    likesCount = clip.likesCount,
                                    commentsCount = clip.commentsCount,
                                    isLiked = clip.isLiked,
                                    isSaved = clip.isSaved,
                                    isFollowing = clip.isFollowing,
                                    location = clip.location,
                                    landmark = clip.landmark,
                                    distanceKm = clip.distanceKm,
                                    isNeighbor = clip.isNeighbor,
                                    soundTitle = clip.soundTitle,
                                    filterName = clip.filterName
                                )
                            viewModel.openShareSheet(postEquivalent)
                        },
                        onSaveClip = { clip -> viewModel.toggleClipSave(clip) },
                        onFollowToggle = { clip -> viewModel.toggleClipFollow(clip) },
                        onUserProfileClick = { username -> viewModel.openUserProfile(username) },
                        onWaveClick = { clip ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Waved at @${clip.username}! 👋")
                            }
                        },
                        onReportClip = { clip, reason ->
                            viewModel.reportPost(clip.id, reason)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Clip reported for review.")
                            }
                        },
                        onBlockCreator = { username ->
                            viewModel.blockUser(username)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Blocked @$username and hid their content.")
                            }
                        },
                        isRefreshing = isRefreshingClips,
                        onRefresh = { viewModel.refreshClips() },
                        deepLinkClipId = deepLinkClipId,
                        onClearDeepLink = onClearDeepLink,
                        countryName = privacySettings.radarCountryName,
                        onConvertClipToMarket = { clipId, price, category, condition, pickupSpot, isService ->
                            viewModel.convertClipToMarketplaceListing(
                                clipId = clipId,
                                priceUSD = price,
                                condition = condition,
                                pickupSpot = pickupSpot,
                                category = category,
                                isService = isService
                            )
                        }
                    )
                }

                MainNavigationTab.PROFILE -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        posts = posts.filter { it.username == userProfile.username || it.id == 5L },
                        clips = clips,
                        marketplaceItems = marketplaceItems,
                        savedPosts = savedPosts,
                        activeTab = profileTab,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        isLoggedOut = isLoggedOut,
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
                        onClipClick = { clip -> viewModel.selectTab(MainNavigationTab.CLIPS) },
                        onDeletePost = { postId ->
                            viewModel.deletePost(postId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Post deleted from profile.")
                            }
                        },
                        onDeleteClip = { clipId ->
                            viewModel.deleteClip(clipId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Neighborhood clip deleted.")
                            }
                        },
                        onUpdateClipDetails = { clipId, caption, loc, landmark ->
                            viewModel.updateClipDetails(clipId, caption, loc, landmark)
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
                        onOpenSellItemDialog = {
                            if (isLoggedOut) {
                                viewModel.openAuthScreen(
                                    reason = "Authentication Required: Please sign in or register with Firebase to list marketplace items.",
                                    initialMode = com.example.ui.screens.AuthScreenMode.LOGIN
                                )
                            } else {
                                viewModel.openSellItemDialog()
                            }
                        },
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
                                snackbarHostState.showSnackbar("Logged out of Firebase Auth successfully.")
                            }
                        },
                        onLogin = {
                            viewModel.openAuthScreen(
                                reason = null,
                                initialMode = com.example.ui.screens.AuthScreenMode.LOGIN
                            )
                        },
                        onOpenSignUp = {
                            viewModel.openAuthScreen(
                                reason = null,
                                initialMode = com.example.ui.screens.AuthScreenMode.REGISTER
                            )
                        },
                        onOpenLegalPolicy = { viewModel.openLegalAgreement() },
                        onOpenCyberstalkingSafety = { viewModel.openCyberstalkingSafety() },
                        onDeleteAccount = {
                            viewModel.deleteAccountAndPurgeData()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Your account and all personal data have been permanently erased.")
                            }
                        },
                        onClearCache = {
                            viewModel.clearLocalCache()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Local cache cleared successfully.")
                            }
                        },
                        onCreateContentClick = {
                            viewModel.selectTab(MainNavigationTab.CREATE)
                        },
                        draftClips = allDraftClips,
                        onSelectDraftForEdit = { draft ->
                            viewModel.selectDraftClipForEdit(draft)
                            viewModel.setCreationMode(CreationMode.CLIP)
                            viewModel.selectTab(MainNavigationTab.CREATE)
                        },
                        onDeleteDraftClip = { draftId ->
                            viewModel.deleteDraftClip(draftId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Draft clip removed from local storage.")
                            }
                        },
                        appDisplayScale = privacySettings.appDisplayScale,
                        onSelectDisplayScale = { newScale ->
                            viewModel.setAppDisplayScale(newScale)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("App display size updated to $newScale")
                            }
                        },
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        creatorEarnings = creatorEarnings,
                        onOpenHelp = { viewModel.openHelp() },
                        onOpenInformation = { viewModel.openInformation() },
                        onOpenDataAnalysis = { viewModel.openDataAnalysis() },
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },
                        onOpenBoostAds = { viewModel.openBoostAdDialog() },
                        onOpenLanguageCurrency = { viewModel.openLanguageCurrencyDialog() },
                        isPremiumSubscribed = privacySettings.isPremiumSubscribed,
                        isGhostMode = privacySettings.isGhostMode,
                        onOpenPremium = { viewModel.openPrivacySettings() },
                        isRefreshing = isRefreshingProfile,
                        onRefresh = { viewModel.refreshProfile() }
                    )
                }
            }
        }
    }

    // Other User Profile Bottom Sheet
    if (selectedOtherUser != null) {
        val otherUser = selectedOtherUser!!
        val userPosts by viewModel.getUserPosts(otherUser.username).collectAsStateWithLifecycle(emptyList())
        val userClips by viewModel.getUserClips(otherUser.username).collectAsStateWithLifecycle(emptyList())

        OtherUserProfileSheet(
            user = otherUser,
            posts = userPosts,
            clips = userClips,
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
            onClipClick = { clip -> viewModel.selectTab(MainNavigationTab.CLIPS) },
            onReportUser = { reason ->
                viewModel.reportUser(otherUser.username, reason)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("User @${otherUser.username} reported for moderation review.")
                }
            },
            onBlockUser = {
                viewModel.blockUser(otherUser.username)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Blocked @${otherUser.username}. Their content has been hidden.")
                }
            },
            onReportCyberstalking = { accused ->
                viewModel.openCyberstalkingSafety(accused)
            }
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
        val isClip = post.mediaType == "VIDEO" || post.mediaType == "CLIP"
        ShareBottomSheet(
            targetTitle = if (isClip) (post.caption.ifBlank { "Pulse by ${post.username}" }) else "Pulse by ${post.username} • ${post.landmark ?: post.location ?: "Seattle"}",
            clipId = if (isClip) post.id else null,
            creatorHandle = post.userHandle,
            clipCaption = post.caption,
            itemType = if (isClip) "Clip" else "Post",
            itemId = post.id,
            location = post.landmark ?: post.location,
            currentLanguage = currentLanguage,
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
            otherUsers = otherUsers,
            privacySettings = privacySettings,
            onDismiss = { viewModel.closeDirectMessagesSheet() },
            onSelectConversation = { conv -> viewModel.openConversation(conv) },
            onBackToInbox = { viewModel.closeActiveConversation() },
            onSendMessage = { text, media -> viewModel.sendChatMessage(text, media) },
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
        var showBlockedUsers by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var showConnectionsManager by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.closePrivacySettings() },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            if (showBlockedUsers) {
                BlockedUsersScreen(onNavigateBack = { showBlockedUsers = false })
            } else if (showConnectionsManager) {
                ConnectionsManagerScreen(onNavigateBack = { showConnectionsManager = false })
            } else {
                PrivacySettingsScreen(
                    privacySettings = privacySettings,
                    onUpdatePrivacySettings = { viewModel.updatePrivacySettings(it) },
                    dailyCheckInState = dailyCheckInState,
                    onClaimCheckIn = { viewModel.claimDailyCheckIn() },
                    referralState = referralState,
                    onRedeemFriendCode = { code -> viewModel.redeemFriendCode(code) },
                    creatorEarnings = creatorEarnings,
                    payoutAccount = payoutAccount,
                    payoutHistory = payoutHistory,
                    currentCurrency = currentCurrency,
                    onRequestPayout = { amount -> viewModel.requestPayout(amount) },
                    onSubscribeToPremium = { isAnnual -> viewModel.subscribeToPremium(isAnnual) },
                    onCancelPremium = { viewModel.cancelPremiumSubscription() },
                    onNavigateBack = { viewModel.closePrivacySettings() },
                    onNavigateToBlockedUsers = { showBlockedUsers = true },
                    onNavigateToConnections = { showConnectionsManager = true },
                    onDeleteAccount = { viewModel.deleteAccountAndPurgeData() }
                )
            }
        }
    }

    // User Activity Log Screen Overlay (Transparency & Audit Trail)
    if (showUserActivityLog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.closeUserActivityLog() },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            UserActivityLogScreen(
                activities = allUserActivities,
                onBackClick = { viewModel.closeUserActivityLog() },
                onDeleteActivity = { id -> viewModel.deleteUserActivity(id) },
                onClearAllActivities = { viewModel.clearAllUserActivities() }
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

    // Cyberstalking Zero-Tolerance Safety & Immutable Vault Screen Overlay (Universal Protocol)
    if (showCyberstalkingSafetyScreen) {
        CyberstalkingSafetyScreen(
            currentUsername = userProfile.username,
            incidents = allCyberstalkingIncidents,
            otherUsers = otherUsers,
            prefilledTargetUsername = cyberstalkingPrefilledUsername,
            onRecordIncident = { incident ->
                viewModel.recordCyberstalkingIncident(incident)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Offender @${incident.accusedUsername} permanently banned. Immutable record locked! 🔒")
                }
            },
            onNavigateBack = { viewModel.closeCyberstalkingSafety() }
        )
    }

    // Advance Firebase Authentication Screen
    if (showAuthScreen) {
        AuthScreen(
            initialMode = authInitialMode,
            securityReason = authReason,
            onDismiss = { viewModel.closeAuthScreen() },
            onGhostSpectatorSuccess = {
                viewModel.enterAsGhostSpectator()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Stealth Spectator active 👻 • Zero GPS Footprint")
                }
            },
            onScrubIdentity = {
                viewModel.resetSessionAndPurge()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Local caches purged & session reset. 🧹")
                }
            },
            onAuthSuccess = { user, username, fullName, neighborhood ->
                viewModel.handleAuthSuccess(user, username, fullName, neighborhood)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Authenticated with Firebase as @$username! Content creation unlocked. 🔒✨")
                }
            }
        )
    }

    // Sign Up & Account Creation Dialog with Mandatory Legal Consent
    if (showSignUpDialog) {
        SignUpDialog(
            onDismissRequest = { viewModel.closeSignUpDialog() },
            onOpenLegalPolicy = { viewModel.openLegalAgreement() },
            onSignUpSuccess = { username, fullName, avatarUrl, bio, neighborhood, enableLocationRadar, timestamp, password ->
                viewModel.createAccount(
                    username = username,
                    fullName = fullName,
                    avatarUrl = avatarUrl,
                    bio = bio,
                    neighborhood = neighborhood,
                    enableLocationRadar = enableLocationRadar,
                    legalConsentTimestamp = timestamp,
                    password = password
                )
                coroutineScope.launch {
                    com.example.auth.FirebaseAuthService.signUpWithEmail(
                        email = "$username@Localiiiy.app",
                        password = password,
                        displayName = fullName
                    )
                    snackbarHostState.showSnackbar("Welcome to Localiiiy, @$username! Account secured with Firebase Auth. 🔒🎉")
                }
            }
        )
    }

    // Worldwide Multi-Language & Currency Dialog
    if (showLanguageCurrencyDialog) {
        GlobalLanguageCurrencyDialog(
            currentLanguage = currentLanguage,
            currentCurrency = currentCurrency,
            onLanguageSelected = { lang ->
                viewModel.setLanguage(lang)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Language switched to ${lang.nativeName} (${lang.name}) 🌐")
                }
            },
            onCurrencySelected = { curr ->
                viewModel.setCurrency(curr)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Currency set to ${curr.name} (${curr.symbol}) 💱")
                }
            },
            onDismissRequest = { viewModel.closeLanguageCurrencyDialog() }
        )
    }

    // Creator Monetization Hub & Multi-Currency Payout Sheet
    if (showHelpSheet) {
            com.example.ui.components.HelpSheet(onDismiss = { viewModel.closeHelp() })
        }
        if (showInformationSheet) {
            com.example.ui.components.InformationSheet(onDismiss = { viewModel.closeInformation() })
        }
        if (showDataAnalysis) {
            com.example.ui.components.DataAnalysisSheet(
                userProfile = userProfile,
                onDismiss = { viewModel.closeDataAnalysis() }
            )
        }
        if (showMonetizationHub) {
        CreatorMonetizationHubSheet(
            earnings = creatorEarnings,
            payoutAccount = payoutAccount,
            payoutHistory = payoutHistory,
            platformMetrics = platformMetrics,
            currentCurrency = currentCurrency,
            currentLanguage = currentLanguage,
            onOpenCurrencyLanguageSelector = { viewModel.openLanguageCurrencyDialog() },
            onRequestPayout = { amountUSD ->
                val ok = viewModel.requestPayout(amountUSD)
                if (ok) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Payout request for $$amountUSD submitted! 💰 Transferred via multi-currency rails.")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Payout request failed. Check minimum threshold or available balance.")
                    }
                }
                ok
            },
            onUpdatePayoutAccount = { updatedAccount ->
                viewModel.updatePayoutAccount(updatedAccount)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Payout destination updated: ${updatedAccount.payoutMethod} ✅")
                }
            },
            onOpenBoostAdDialog = {
                viewModel.closeMonetizationHub()
                viewModel.openBoostAdDialog()
            },
            onDismissRequest = { viewModel.closeMonetizationHub() }
        )
    }

    if (showAtmosphericThemeBottomSheet) {
        AtmosphericThemeBottomSheet(
            currentThemeKey = privacySettings.appThemeBackground,
            currentCustomImageUri = privacySettings.customBackgroundImageUri,
            onSelectTheme = { key, uri -> viewModel.setAtmosphericTheme(key, uri) },
            onDismiss = { viewModel.closeAtmosphericThemeBottomSheet() },
            onOpenWorldwideLocalization = {
                viewModel.closeAtmosphericThemeBottomSheet()
                viewModel.openLanguageCurrencyDialog()
            }
        )
    }

    // Boost Post & Worldwide Sponsored Campaign Dialog
    if (showBoostAdDialog) {
        BoostPostDialog(
            currentCurrency = currentCurrency,
            onLaunchCampaign = { campaign ->
                val ok = viewModel.launchBoostCampaign(campaign)
                if (ok) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Boost campaign launched! Target: ${campaign.targetAudience} in 195+ countries 🚀")
                    }
                }
            },
            onDismissRequest = { viewModel.closeBoostAdDialog() }
        )
    }

    // Eyecatching Opening Animation Overlay
    if (showOpeningAnimation) {
        LocaliiiyOpeningAnimation(
            onAnimationFinished = { showOpeningAnimation = false }
        )
    }
    }
}

@Composable
fun LocaliiiyBottomNavigationBar(
    currentTab: MainNavigationTab,
    userAvatarUrl: String,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
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
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulse Feed Tab
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.FEED) Icons.Default.DynamicFeed else Icons.Outlined.DynamicFeed,
                label = LocalizationHelper.getString(LocaliiiyStringKey.TAB_FEED, currentLanguage),
                isSelected = currentTab == MainNavigationTab.FEED,
                onClick = { onTabSelected(MainNavigationTab.FEED) },
                testTag = "nav_tab_feed"
            )

            // Radar Explore Tab
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.EXPLORE) Icons.Default.Radar else Icons.Outlined.Radar,
                label = LocalizationHelper.getString(LocaliiiyStringKey.TAB_RADAR, currentLanguage),
                isSelected = currentTab == MainNavigationTab.EXPLORE,
                onClick = { onTabSelected(MainNavigationTab.EXPLORE) },
                testTag = "nav_tab_explore"
            )

            // Market Tab
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.MARKET) Icons.Default.Storefront else Icons.Outlined.Storefront,
                label = LocalizationHelper.getString(LocaliiiyStringKey.TAB_MARKET, currentLanguage),
                isSelected = currentTab == MainNavigationTab.MARKET,
                onClick = { onTabSelected(MainNavigationTab.MARKET) },
                testTag = "nav_tab_market"
            )

            // Localiiiy Studio Tab (Standard Long Videos: 60s - 240 mins)
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.STUDIO) Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
                label = LocalizationHelper.getString(LocaliiiyStringKey.TAB_STUDIO, currentLanguage),
                isSelected = currentTab == MainNavigationTab.STUDIO,
                onClick = { onTabSelected(MainNavigationTab.STUDIO) },
                testTag = "nav_tab_studio"
            )

            // Vibes / Clips Tab
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.CLIPS) Icons.Default.PlayCircle else Icons.Outlined.PlayCircle,
                label = LocalizationHelper.getString(LocaliiiyStringKey.TAB_CLIPS, currentLanguage),
                isSelected = currentTab == MainNavigationTab.CLIPS,
                onClick = { onTabSelected(MainNavigationTab.CLIPS) },
                testTag = "nav_tab_clips"
            )

            // Profile Tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onTabSelected(MainNavigationTab.PROFILE) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("nav_tab_profile"),
                contentAlignment = Alignment.Center
            ) {
                val isSelected = currentTab == MainNavigationTab.PROFILE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .padding(if (isSelected) 1.5.dp else 0.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userAvatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Space",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        text = LocalizationHelper.getString(LocaliiiyStringKey.TAB_PROFILE, currentLanguage),
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LocaliiiyNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 9.sp,
                maxLines = 1,
                softWrap = false,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

