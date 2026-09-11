package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.LiveRadarComponent
import com.example.ui.components.PulseClipCard
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.components.PulseMarketItemCard
import com.example.ui.components.PostCard
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

import android.Manifest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

enum class ExploreViewMode {
    RADAR, // Circular Hyperlocal Proximity Radar Interface
    GRID   // Traditional 3-Column Stream Grid
}

@OptIn(ExperimentalPermissionsApi::class)
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
    onUserProfileClick: (String) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    privacySettings: com.example.data.PrivacySettingsEntity? = null,
    onToggleHidePreciseLocation: ((Boolean) -> Unit)? = null,
    onSelectObfuscatedRange: ((String) -> Unit)? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    var bypassPermissionForPreview by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var localRadiusKm by remember(selectedRadiusKm) { mutableStateOf(selectedRadiusKm ?: 3.0) }
    var exploreViewMode by remember { mutableStateOf(ExploreViewMode.RADAR) }
    var selectedDetailPost by remember { mutableStateOf<PostEntity?>(null) }

    val systematicOptions = remember(privacySettings?.radarCountryName) {
        SystematicDistanceScale.getOptions(privacySettings?.radarCountryName)
    }
    val radiusFilters = remember(systematicOptions) {
        systematicOptions.map { opt ->
            Pair("${opt.icon} ${opt.shortLabel}", opt.km)
        }
    }

    // Filter posts by query or distance
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
        // Search & View Mode Switcher Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = LocalizationHelper.getString(LocaliiiyStringKey.SEARCH_HINT, currentLanguage),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("explore_search_field")
            )
        }

        AnimatedVisibility(visible = searchQuery.isBlank()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val trendingTags = listOf("Coffee", "Hiking", "Farmers Market", "Live Music", "Art")
                items(trendingTags) { tag ->
                    SuggestionChip(
                        onClick = { searchQuery = tag },
                        label = { Text(tag) },
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        shape = CircleShape
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // Switch between Live Radar view and Grid View
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

        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = rememberPullToRefreshState(),
            modifier = Modifier.fillMaxSize().testTag("explore_pull_to_refresh_box")
        ) {
            when (exploreViewMode) {
                ExploreViewMode.RADAR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                    // Main Interactive Live Radar Component
                Spacer(modifier = Modifier.height(4.dp))

                LiveRadarComponent(
                    userProfile = userProfile,
                    nearbyUsers = nearbyUsers,
                    nearbyPosts = posts,
                    nearbyClips = clips,
                    nearbyMarketItems = marketplaceItems,
                    selectedRadiusKm = localRadiusKm,
                    isLocationEnabled = isLocationEnabled,
                    isPrivateAccount = isPrivateAccount,
                    hidePreciseLocationOnRadar = privacySettings?.hidePreciseLocationOnRadar ?: false,
                    radarObfuscatedRange = privacySettings?.radarObfuscatedRange ?: "3k",
                    radarCountryName = privacySettings?.radarCountryName ?: "United States",
                    onToggleHidePreciseLocation = onToggleHidePreciseLocation,
                    onSelectObfuscatedRange = onSelectObfuscatedRange,
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
                    onWaveAtUser = onWaveAtUser
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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

                    Spacer(modifier = Modifier.height(6.dp))

                    val landmarks = listOf(
                        Pair("Pike Place Market", "0.1 km"),
                        Pair("Pioneer Square", "0.6 km"),
                        Pair("Belltown", "0.9 km"),
                        Pair("Capitol Hill", "1.3 km"),
                        Pair("Lake Union", "2.1 km"),
                        Pair("Fremont Troll", "4.8 km")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(landmarks) { (landmark, dist) ->
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable { searchQuery = landmark }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = landmark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "($dist)",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Full Detail Radar Feed
                filteredPosts.forEach { post ->
                    com.example.ui.components.PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserClick = { onUserProfileClick(post.username) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                clips.filter { it.distanceKm != null && it.distanceKm <= (localRadiusKm ?: 1000.0) }.forEach { clip ->
                    com.example.ui.components.PulseClipCard(
                        clip = clip,
                        onClick = { onUserProfileClick(clip.username) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                marketplaceItems.forEach { item ->
                    com.example.ui.components.PulseMarketItemCard(
                        item = item,
                        onClick = { onUserProfileClick(item.sellerUsername) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        ExploreViewMode.GRID -> {

                // VIEW MODE: GRID VIEW
                Column(modifier = Modifier.fillMaxSize()) {
                // Radius Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(radiusFilters) { (label, radius) ->
                        val isSelected = localRadiusKm == radius
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localRadiusKm = radius
                                onRadiusFilterChange(radius)
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(100.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Explore 3-Column Grid with Distance Badges
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
}
