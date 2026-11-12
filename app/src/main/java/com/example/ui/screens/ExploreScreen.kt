package com.example.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.PrivacySettingsEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.GoogleMapMomentsComponent
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.LiveRadarComponent
import com.example.ui.components.LocaliiiyAppLogoBadge
import com.example.ui.components.LocaliiiySearchBar
import com.example.ui.components.MasterGhostModeTooltip
import com.example.ui.components.EnergeticPulseBadge
import com.example.ui.components.PostCard
import com.example.ui.components.PulseClipCard
import com.example.ui.components.PulseMarketItemCard
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.theme.LocaliiiyAccentCoral
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocaliiiyStringKey
import com.example.util.LocalizationHelper
import com.example.util.LocationHelper
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

enum class ExploreViewMode {
    RADAR, // Circular Hyperlocal Proximity Radar Interface
    MAP,   // Interactive Google Maps SDK Map View
    GRID   // Traditional 3-Column Stream Grid
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    posts: List<PostEntity>,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity> = emptyList(),
    selectedRadiusKm: Double? = null,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    onRadiusFilterChange: (Double?) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onPostClick: (PostEntity) -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentPost: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onSavePost: (PostEntity) -> Unit,
    onReportPost: (PostEntity, String) -> Unit = { _, _ -> },
    onUserProfileClick: (String) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    privacySettings: PrivacySettingsEntity? = null,
    onToggleHidePreciseLocation: ((Boolean) -> Unit)? = null,
    onSelectObfuscatedRange: ((String) -> Unit)? = null,
    onSelectHudTheme: (String) -> Unit = {},
    onUnlockPremiumClick: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    activeRadarPerk: com.example.data.RadarVisibilityPerk? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    var bypassPermissionForPreview by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var ghostMode by remember(isPrivateAccount, privacySettings?.isGhostMode, privacySettings?.isPrivateAccount) {
        mutableStateOf(isPrivateAccount || (privacySettings?.isGhostMode == true) || (privacySettings?.isPrivateAccount == true))
    }
    var localRadiusKm by remember(selectedRadiusKm, privacySettings?.preferredRadarRadiusKm) {
        mutableStateOf(selectedRadiusKm ?: privacySettings?.preferredRadarRadiusKm ?: 50.0)
    }
    var exploreViewMode by remember { mutableStateOf(ExploreViewMode.RADAR) }
    var selectedDetailPost by remember { mutableStateOf<PostEntity?>(null) }

    // Resolve Detected State & Country Name via Google Maps / Android Geocoder + Profile Coordinates
    val initialDetectedGeo = remember(userProfile.latitude, userProfile.longitude, userProfile.locationName, privacySettings?.radarCountryName) {
        LocationHelper.inferStateAndCountryFromCoordinates(
            lat = userProfile.latitude,
            lng = userProfile.longitude,
            locationName = userProfile.locationName,
            fallbackCountry = privacySettings?.radarCountryName
        )
    }
    var detectedStateName by remember(initialDetectedGeo) { mutableStateOf(initialDetectedGeo.first) }
    var detectedCountryName by remember(initialDetectedGeo) { mutableStateOf(initialDetectedGeo.second) }

    LaunchedEffect(userProfile.latitude, userProfile.longitude, userProfile.locationName) {
        val resolved = LocationHelper.resolveStateAndCountry(
            context = context,
            lat = userProfile.latitude,
            lng = userProfile.longitude,
            locationName = userProfile.locationName,
            fallbackCountry = privacySettings?.radarCountryName
        )
        detectedStateName = resolved.first
        detectedCountryName = resolved.second
    }

    // Filter posts by query or dynamic radar distance scale
    val filteredPosts = remember(posts, searchQuery, localRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else posts.filter { post ->
            val matchesQuery = if (searchQuery.isBlank()) true else {
                post.caption.contains(searchQuery, ignoreCase = true) ||
                post.username.contains(searchQuery, ignoreCase = true) ||
                (post.location?.contains(searchQuery, ignoreCase = true) == true) ||
                (post.landmark?.contains(searchQuery, ignoreCase = true) == true)
            }
            val matchesRadius = (post.distanceKm ?: 999.0) <= localRadiusKm
            matchesQuery && matchesRadius
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("explore_screen_container")
    ) {
        // =====================================================================
        // 1. LOCALIIIY RADAR BRAND HEADER (Clean Single-Line Header Like Market, Studio & Clips)
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("localiiiy_radar_brand_header"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LocaliiiyAppLogoBadge(size = 30.dp)
                Text(
                    text = "Localiiiy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                Surface(
                    color = Color(0xFF00E5FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "RADAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF021019),
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Detected State & Country GPS Badge (Shows "Ghost Mode" when Ghost/Spectator Mode is active)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (ghostMode) Color(0xFF0A2436) else Color(0xFF042214),
                border = BorderStroke(
                    1.dp,
                    if (ghostMode) Color(0xFF00E5FF).copy(alpha = 0.75f) else Color(0xFF00FF66).copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (ghostMode) Icons.Default.VisibilityOff else Icons.Default.LocationOn,
                        contentDescription = if (ghostMode) "Ghost Mode" else "Detected State and Country",
                        tint = if (ghostMode) Color(0xFF00E5FF) else Color(0xFF00FF66),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (ghostMode) "Ghost Mode" else "$detectedStateName, $detectedCountryName",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ghostMode) Color(0xFF00E5FF) else Color(0xFF00FF66),
                        maxLines = 1
                    )
                }
            }
        }

        // =====================================================================
        // 1.5 DEDICATED PLAIN-ENGLISH RADAR STATUS LINE DIRECTLY BELOW LOCALIIIY RADAR
        // Explains Online / Offline / Ghost Mode, user visibility, and active KM scan range
        // =====================================================================
        val formattedSelectedRange = remember(localRadiusKm, detectedStateName, detectedCountryName) {
            when {
                localRadiusKm >= 15000.0 -> "Earth (Global)"
                localRadiusKm >= 2500.0 -> "$detectedCountryName (Countrywide)"
                localRadiusKm >= 300.0 -> "$detectedStateName (Statewide)"
                localRadiusKm >= 10.0 -> "${localRadiusKm.toInt()} km"
                else -> "${String.format("%.0f", localRadiusKm)} km"
            }
        }
        val visibleUsersInRangeCount = remember(nearbyUsers, localRadiusKm, isLocationEnabled, ghostMode) {
            if (!isLocationEnabled || ghostMode) 0 else nearbyUsers.count { it.distanceKm <= localRadiusKm }
        }
        val visibleMarketInRangeCount = remember(marketplaceItems, localRadiusKm, isLocationEnabled) {
            if (!isLocationEnabled) 0 else marketplaceItems.count { (it.distanceKm ?: 1.5) <= localRadiusKm }
        }
        val isObfuscatedFar = privacySettings?.hidePreciseLocationOnRadar == true
        val rawTransmitRange = privacySettings?.radarObfuscatedRange ?: "3KM"
        val broadcastRangeLabel = remember(rawTransmitRange, detectedStateName, detectedCountryName) {
            when (rawTransmitRange.uppercase()) {
                "1K", "3K", "3KM" -> "farther than 3KM range"
                "5K", "5KM" -> "farther than 5KM range"
                "10K", "10KM" -> "farther than 10KM range"
                "50K", "50KM" -> "farther than 50KM range"
                "100K", "500K", "500KM" -> "farther than 500KM range"
                "STATE" -> "across $detectedStateName (Statewide)"
                "COUNTRY" -> "across $detectedCountryName (Countrywide)"
                else -> if (rawTransmitRange.equals(detectedStateName, ignoreCase = true)) {
                    "across $detectedStateName (Statewide)"
                } else if (rawTransmitRange.equals(detectedCountryName, ignoreCase = true)) {
                    "across $detectedCountryName (Countrywide)"
                } else {
                    "farther than $rawTransmitRange range"
                }
            }
        }
        val plainEnglishRadarMessage = remember(
            isLocationEnabled,
            ghostMode,
            isObfuscatedFar,
            broadcastRangeLabel,
            formattedSelectedRange,
            visibleUsersInRangeCount,
            visibleMarketInRangeCount
        ) {
            when {
                ghostMode ->
                    "👻 Ghost Mode Active: You are silent & cloaked — not transmitting or receiving other users' radar details."
                !isLocationEnabled ->
                    "🔴 Signal Offline: You are not transmitting on any radar of other users. Turn on ONLINE to scan your area."
                isObfuscatedFar ->
                    "📡 Signal Transmitting: You are visible to other radars $broadcastRangeLabel (hidden from nearby locals) • Scanning $visibleUsersInRangeCount users & $visibleMarketInRangeCount listings within $formattedSelectedRange."
                else ->
                    "🟢 Signal Transmitting: You are visible to all radars of other users • Scanning $visibleUsersInRangeCount users & $visibleMarketInRangeCount listings within $formattedSelectedRange."
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                !isLocationEnabled -> Color(0xFF2B1115)
                ghostMode -> Color(0xFF0A1E2E)
                else -> Color(0xFF062315)
            },
            border = BorderStroke(
                1.dp,
                when {
                    !isLocationEnabled -> Color(0xFFFF5252).copy(alpha = 0.7f)
                    ghostMode -> Color(0xFF00E5FF).copy(alpha = 0.7f)
                    else -> Color(0xFF00FF66).copy(alpha = 0.65f)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp)
                .testTag("radar_plain_english_status_banner")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Dedicated Status & Range Summary Line
                Text(
                    text = plainEnglishRadarMessage,
                    fontSize = 10.5.sp,
                    lineHeight = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Dedicated Quick Controls Line: ONLINE/OFFLINE + GHOST MODE + RANGE PILL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Online / Offline Power Button
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isLocationEnabled) Color(0xFF093B1E) else Color(0xFF471515),
                            border = BorderStroke(1.dp, if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252)),
                            modifier = Modifier
                                .clickable { onLocationToggle(!isLocationEnabled) }
                                .testTag("header_radar_power_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = "Radar Power",
                                    tint = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isLocationEnabled) "ONLINE" else "OFFLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252),
                                    maxLines = 1
                                )
                            }
                        }

                        // Ghost Mode Toggle
                        MasterGhostModeTooltip {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (ghostMode) Color(0xFF0D2D44) else Color(0xFF0B2B1C),
                                border = BorderStroke(1.dp, if (ghostMode) Color(0xFF00E5FF) else Color(0xFF00FF66).copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .clickable {
                                        val nextGhost = !ghostMode
                                        ghostMode = nextGhost
                                        onPrivateToggle(nextGhost)
                                    }
                                    .testTag("toggle_explore_ghost_mode")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (ghostMode) Icons.Default.VisibilityOff else Icons.Default.CellTower,
                                        contentDescription = "Ghost Mode",
                                        tint = if (ghostMode) Color(0xFF00E5FF) else Color(0xFF00FF66),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (ghostMode) "GHOST MODE ON" else "VISIBLE MODE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (ghostMode) Color(0xFF00E5FF) else Color(0xFF00FF66),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Active Selected Scan Range Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF061A26),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "SCAN: $formattedSelectedRange",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
                        )
                    }
                }
            }
        }

        // Search & View Mode Switcher Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocaliiiySearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = LocalizationHelper.getString(LocaliiiyStringKey.SEARCH_HINT, currentLanguage),
                modifier = Modifier.weight(1f),
                testTag = "explore_search_field"
            )

            // Switch between Live Radar view, Map View, and Grid View
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Radar View Button
                    IconButton(
                        onClick = { exploreViewMode = ExploreViewMode.RADAR },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (exploreViewMode == ExploreViewMode.RADAR) LocaliiiyDeepNavy else Color.Transparent
                            )
                            .testTag("toggle_radar_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Radar,
                            contentDescription = "Live Circular Radar",
                            tint = if (exploreViewMode == ExploreViewMode.RADAR) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Map View Button (Google Maps SDK + Satellite/Hybrid/Terrain/Standard)
                    IconButton(
                        onClick = { exploreViewMode = ExploreViewMode.MAP },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (exploreViewMode == ExploreViewMode.MAP) LocaliiiyPrimaryTeal else Color.Transparent
                            )
                            .testTag("toggle_map_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = "Interactive Map View",
                            tint = if (exploreViewMode == ExploreViewMode.MAP) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Grid View Button
                    IconButton(
                        onClick = { exploreViewMode = ExploreViewMode.GRID },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (exploreViewMode == ExploreViewMode.GRID) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                            .testTag("toggle_grid_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = "Grid View",
                            tint = if (exploreViewMode == ExploreViewMode.GRID) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = rememberPullToRefreshState(),
            modifier = Modifier.fillMaxSize().testTag("explore_pull_to_refresh_box")
        ) {
            when (exploreViewMode) {
                ExploreViewMode.MAP -> {
                    GoogleMapMomentsComponent(
                        posts = filteredPosts,
                        userProfile = userProfile,
                        nearbyUsers = if (ghostMode) emptyList() else nearbyUsers,
                        selectedRadiusKm = localRadiusKm,
                        marketplaceItems = marketplaceItems,
                        clips = clips,
                        detectedStateName = if (ghostMode) "Ghost Mode" else detectedStateName,
                        detectedCountryName = if (ghostMode) "" else detectedCountryName,
                        onRadiusChange = { radius ->
                            localRadiusKm = radius
                            onRadiusFilterChange(radius)
                        },
                        onPostClick = { post -> selectedDetailPost = post },
                        onLikePost = onLikePost,
                        onUserProfileClick = onUserProfileClick,
                        onMarketItemClick = { item -> onUserProfileClick(item.sellerUsername) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ExploreViewMode.RADAR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))

                        LiveRadarComponent(
                            userProfile = userProfile,
                            nearbyUsers = if (ghostMode) emptyList() else nearbyUsers,
                            nearbyPosts = filteredPosts,
                            nearbyClips = clips,
                            nearbyMarketItems = marketplaceItems,
                            selectedRadiusKm = localRadiusKm,
                            isLocationEnabled = isLocationEnabled && !ghostMode,
                            isPrivateAccount = isPrivateAccount || ghostMode,
                            hidePreciseLocationOnRadar = privacySettings?.hidePreciseLocationOnRadar ?: false,
                            radarObfuscatedRange = privacySettings?.radarObfuscatedRange ?: "3KM",
                            radarCountryName = detectedCountryName,
                            detectedStateName = detectedStateName,
                            isPremiumSubscribed = privacySettings?.isPremiumConnected ?: false,
                            selectedHudThemeName = privacySettings?.preferredRadarHudTheme ?: "PHOSPHOR_GREEN",
                            onSelectHudTheme = onSelectHudTheme,
                            onUnlockPremiumClick = onUnlockPremiumClick,
                            onToggleHidePreciseLocation = { hide ->
                                if (hide) ghostMode = false
                                onToggleHidePreciseLocation?.invoke(hide)
                            },
                            onSelectObfuscatedRange = { range ->
                                ghostMode = false
                                onSelectObfuscatedRange?.invoke(range)
                            },
                            isRefreshing = isRefreshing,
                            onRadiusChange = { radius ->
                                localRadiusKm = radius
                                onRadiusFilterChange(radius)
                            },
                            onLocationToggle = onLocationToggle,
                            onPrivateToggle = onPrivateToggle,
                            onOpenPrivacySettings = onOpenPrivacySettings,
                            onUserClick = { user -> onUserProfileClick(user.username) },
                            onPostClick = { post -> selectedDetailPost = post },
                            onWaveAtUser = onWaveAtUser,
                            activeRadarPerk = activeRadarPerk
                        )

                        // Landmark Quick Discovery Tray
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = LocaliiiyPrimaryTeal,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Local Landmark Hotspots",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${filteredPosts.size} pulses",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliiiyPrimaryTeal
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Detail Radar Feed
                            filteredPosts.forEach { post ->
                                PostCard(
                                    post = post,
                                    onLikeClick = { onLikePost(post) },
                                    onCommentClick = { onCommentPost(post) },
                                    onShareClick = { onSharePost(post) },
                                    onSaveClick = { onSavePost(post) },
                                    onReportClick = { reason -> onReportPost(post, reason) },
                                    onUserClick = { onUserProfileClick(post.username) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            val finalClips = clips.filter { clip ->
                                (clip.distanceKm ?: 999.0) <= localRadiusKm
                            }
                            finalClips.forEach { clip ->
                                PulseClipCard(
                                    clip = clip,
                                    onClick = { onUserProfileClick(clip.username) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            val finalMarket = marketplaceItems.filter { item ->
                                (item.distanceKm ?: 999.0) <= localRadiusKm
                            }
                            finalMarket.forEach { item ->
                                PulseMarketItemCard(
                                    item = item,
                                    onClick = { onUserProfileClick(item.sellerUsername) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                ExploreViewMode.GRID -> {
                    // VIEW MODE: GRID VIEW
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                            verticalArrangement = Arrangement.spacedBy(1.5.dp)
                        ) {
                            items(
                                items = filteredPosts,
                                key = { it.id }
                            ) { post ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clickable { selectedDetailPost = post }
                                        .testTag("explore_post_cell_${post.id}")
                                ) {
                                    ImageWithFilter(
                                        mediaUrl = post.mediaUrl,
                                        filterName = post.filterName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = post.caption
                                    )

                                    // Gradient overlay at bottom for readability
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                )
                                            )
                                    )

                                    // Distance badge
                                    val distLabel = LocationHelper.formatDistanceLabel(post.distanceKm)
                                    Text(
                                        text = "📍 $distLabel",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                    )

                                    // Play icon if even id
                                    if (post.id % 2L == 0L) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Video",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail modal
    if (selectedDetailPost != null) {
        val post = selectedDetailPost!!
        AlertDialog(
            onDismissRequest = { selectedDetailPost = null },
            confirmButton = {
                TextButton(onClick = { selectedDetailPost = null }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onReportClick = { reason -> onReportPost(post, reason) },
                        onUserClick = {
                            selectedDetailPost = null
                            onUserProfileClick(post.username)
                        }
                    )
                }
            }
        )
    }
}
