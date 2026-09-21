package com.example.ui.components

import com.example.data.MarketplaceItemEntity
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.components.AdBannerComponent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdPlacement
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.StoryEntity
import com.example.data.StudioVideoEntity
import com.example.data.UserProfileEntity
import androidx.compose.foundation.clickable
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage

import com.example.ui.components.feed.TactileTriDialFeedLens
import com.example.ui.components.feed.ConcentricSonarRefreshIndicator
import com.example.ui.components.feed.PriorityBanner
import com.example.ui.components.feed.PulsePollCard
import com.example.ui.components.feed.EphemeralPulseCard
import com.example.ui.components.feed.AudioVoicePulseCard
import com.example.ui.components.feed.GhostModeFeedWatermark
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.DataSaverOn

enum class PulseFeedFilter(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.Public),
    LIVE("Live", Icons.Default.Videocam),
    TRENDING("Trending", Icons.Default.TrendingUp),
    NEARBY("Nearby", Icons.Default.NearMe),
    CONNECTED("Connected", Icons.Default.Person),
    LATEST("Latest", Icons.Default.ElectricBolt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseFeedComponent(
    posts: List<PostEntity>,
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    stories: List<StoryEntity>,
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity> = emptyList(),
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    selectedRadiusKm: Double? = 3.0,
    onRadiusFilterChange: (Double) -> Unit = {},
    onStoryClick: (StoryEntity) -> Unit,
    onAddStoryClick: () -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentPost: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onSavePost: (PostEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onFollowUser: (OtherUserEntity) -> Unit = {},
    onWaveAtNeighbor: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    onReportPost: (PostEntity, String) -> Unit = { _, _ -> },
    sponsoredAds: List<AdPlacement> = emptyList(),
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    countryName: String? = null,
    onAdImpression: (String) -> Unit = {},
    onAdClick: (String) -> Unit = {},
    onBoostPostClick: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val refreshState = rememberPullToRefreshState()
    var isInitialLoad by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf(PulseFeedFilter.ALL) }
    var scaleDial by remember { mutableStateOf("NEIGHBOR") }
    
    // Section 2.17: Zero-Algorithm Chronological Toggle
    var isStrictChronological by remember { mutableStateOf(false) }
    
    // Section 2.20: Smart Data Saver Mode Toggle
    var isDataSaverMode by remember { mutableStateOf(false) }

    // Section 2.9: Ghost Mode Feed Watermark state
    var showGhostWatermark by remember { mutableStateOf(true) }

    // Section 2.10: Priority Alert Banner state
    var showPriorityBanner by remember { mutableStateOf(true) }

    // Section 2.4: Ephemeral Pulse Card state
    var showEphemeralCard by remember { mutableStateOf(true) }

    // Section 2.19: Dismissed posts set for swipe to dismiss
    var dismissedPostIds by remember { mutableStateOf(setOf<Long>()) }

    // Go Live Broadcast state
    var showGoLiveDialog by remember { mutableStateOf(false) }
    var isCurrentlyLive by remember { mutableStateOf(false) }
    var liveBroadcastTitle by remember { mutableStateOf("") }
    var liveBroadcastReach by remember { mutableStateOf("NEIGHBOR (5 km)") }
    var liveElapsedSeconds by remember { mutableStateOf(0) }
    var liveViewersCount by remember { mutableStateOf(1) }

    LaunchedEffect(isCurrentlyLive) {
        if (isCurrentlyLive) {
            while (isCurrentlyLive) {
                kotlinx.coroutines.delay(1000)
                liveElapsedSeconds++
                if (liveElapsedSeconds % 5 == 0 && liveViewersCount < 85) {
                    liveViewersCount += (1..4).random()
                }
            }
        }
    }

    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }
    val currentDistanceOpt = remember(selectedRadiusKm, countryName) {
        SystematicDistanceScale.findOption(selectedRadiusKm, countryName)
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        isInitialLoad = false
    }

    // Filter Posts by All, Trending, Nearby, Following, Latest + Blast Radius Dial + Strict Chronological
    val displayedPosts = remember(posts, selectedFilter, selectedRadiusKm, scaleDial, isStrictChronological, dismissedPostIds) {
        val baseFiltered = posts.filter { post ->
            if (dismissedPostIds.contains(post.id)) return@filter false
            when (scaleDial) {
                "NEIGHBOR" -> (post.distanceKm ?: 999.0) <= 5.0
                "CITY" -> (post.distanceKm ?: 999.0) <= 50.0
                "EARTH" -> true
                else -> true
            }
        }
        
        if (isStrictChronological) {
            baseFiltered.sortedByDescending { it.timestamp }
        } else {
            when (selectedFilter) {
                PulseFeedFilter.ALL -> baseFiltered.sortedByDescending { it.timestamp }
                PulseFeedFilter.LIVE -> baseFiltered.filter { it.isLive }.sortedByDescending { it.timestamp }
                PulseFeedFilter.TRENDING -> baseFiltered.sortedByDescending { (it.likesCount * 3) + it.commentsCount }
                PulseFeedFilter.NEARBY -> {
                    val radius = selectedRadiusKm ?: 3.0
                    val nearby = baseFiltered.filter { (it.distanceKm ?: 99.0) <= radius }
                    if (nearby.isNotEmpty()) nearby.sortedBy { it.distanceKm ?: 99.0 }
                    else baseFiltered.sortedBy { it.distanceKm ?: 99.0 }
                }
                PulseFeedFilter.CONNECTED -> {
                    val connected = baseFiltered.filter { it.isFollowing || it.isConnected }
                    if (connected.isNotEmpty()) connected.sortedByDescending { it.timestamp }
                    else baseFiltered.take(5)
                }
                PulseFeedFilter.LATEST -> baseFiltered.sortedByDescending { it.timestamp }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = refreshState,
            modifier = Modifier.fillMaxSize().testTag("pulse_feed_pull_to_refresh")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Section 2.13: Pull-to-Sonar Concentric Refresh Indicator
                if (isRefreshing) {
                    item {
                        ConcentricSonarRefreshIndicator(isRefreshing = true)
                    }
                }

                // Section 2.10: Priority Alert Banner (Weather/Emergency notice)
                if (showPriorityBanner) {
                    item {
                        PriorityBanner(
                            title = "⚠️ Flash Flood Advisory • Capitol Hill",
                            description = "Coarse radius: Capitol Hill / Central District. Stay alert near low-elevation crossings.",
                            onDismiss = { showPriorityBanner = false }
                        )
                    }
                }

                // (Removed TactileTriDialFeedLens)
                item {
                    StoriesTray(
                        stories = stories,
                        userProfile = userProfile,
                        onStoryClick = onStoryClick,
                        onAddStoryClick = onAddStoryClick
                    )
                }

                // Hyperlocal Discovery Filter Header
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    LocaliiiyAppLogoBadge(size = 20.dp)
                                    Text(
                                        text = "Localiiiy",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when {
                                        isStrictChronological -> "${displayedPosts.size} Pulses • Pure Chronological Feed"
                                        selectedFilter == PulseFeedFilter.ALL -> "${displayedPosts.size} Pulses • Showing all local posts"
                                        selectedFilter == PulseFeedFilter.TRENDING -> "${displayedPosts.size} Pulses • Trending in your community"
                                        selectedFilter == PulseFeedFilter.NEARBY -> "${displayedPosts.size} Pulses • Hyperlocal (<${selectedRadiusKm ?: 3.0} km)"
                                        selectedFilter == PulseFeedFilter.CONNECTED -> "${displayedPosts.size} Pulses • Connected connections"
                                        else -> "${displayedPosts.size} Pulses • Freshly shared moments"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Section 2.17 & 2.20 Controls (Zero Algorithm & Smart Data Saver)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        if (isCurrentlyLive) {
                                            isCurrentlyLive = false
                                        } else {
                                            showGoLiveDialog = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrentlyLive) Color(0xFF10B981) else Color.Red
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp).testTag("btn_go_live")
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentlyLive) Icons.Default.CheckCircle else Icons.Default.Videocam,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.White
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = if (isCurrentlyLive) "Live Active" else "Go Live",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { isStrictChronological = !isStrictChronological },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isStrictChronological) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("btn_chronological_toggle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = "Strict Chronological",
                                        tint = if (isStrictChronological) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { isDataSaverMode = !isDataSaverMode },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDataSaverMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("btn_data_saver_toggle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DataSaverOn,
                                        contentDescription = "Data Saver",
                                        tint = if (isDataSaverMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter UI Chips
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().testTag("pulse_feed_filter_row"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(PulseFeedFilter.values()) { filter ->
                                val isSelected = selectedFilter == filter && !isStrictChronological
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedFilter = filter
                                        isStrictChronological = false
                                    },
                                    label = {
                                        Text(
                                            text = filter.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = filter.icon,
                                            contentDescription = filter.label,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("pulse_filter_${filter.name.lowercase()}")
                                )
                            }
                        }

                        // Systematic Distance Filter Row (when NEARBY is selected)
                        if (selectedFilter == PulseFeedFilter.NEARBY && !isStrictChronological) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "DISTANCE RANGE:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pulse_feed_distance_row"),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(systematicOptions) { opt ->
                                    val isSelected = kotlin.math.abs((selectedRadiusKm ?: 3.0) - opt.km) < 0.1 ||
                                            (opt.key == "COUNTRY" && selectedRadiusKm == SystematicDistanceScale.COUNTRY_DEFAULT_KM) ||
                                            (opt.key == "EARTH" && selectedRadiusKm == SystematicDistanceScale.EARTH_KM) ||
                                            (opt.key == "GALAXY" && selectedRadiusKm == SystematicDistanceScale.GALAXY_KM)
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { onRadiusFilterChange(opt.km) }
                                            .testTag("pulse_distance_pill_${opt.key.lowercase()}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(text = opt.icon, fontSize = 11.sp)
                                            Text(
                                                text = opt.shortLabel,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Active Live Stream Broadcast Banner
                if (isCurrentlyLive) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.12f),
                            border = BorderStroke(1.2.dp, Color(0xFFDC2626)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                    Column {
                                        Text(
                                            text = "🔴 LIVE: ${liveBroadcastTitle.ifBlank { "Neighborhood Live" }}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "⏱️ ${liveElapsedSeconds / 60}m ${liveElapsedSeconds % 60}s • 👥 $liveViewersCount watching • Reach: $liveBroadcastReach",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Button(
                                    onClick = { isCurrentlyLive = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("End", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Section 2.4: Decaying Flash Pulse Card
                if (showEphemeralCard && posts.isNotEmpty()) {
                    item {
                        EphemeralPulseCard(
                            post = posts.first(),
                            totalDurationHours = 8,
                            remainingMinutesInitial = 265,
                            onDismiss = { showEphemeralCard = false }
                        )
                    }
                }

                // Section 2.11: Interactive Community Poll Pulse Card
                item {
                    PulsePollCard()
                }

                // Section 2.15: Audio Voice Pulse Card
                if (isInitialLoad || isRefreshing) {
                    items(4) {
                        PostSkeleton()
                    }
                } else if (displayedPosts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = selectedFilter.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No pulses found for '${selectedFilter.label}'",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap 'All' to browse community pulses or create one!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(items = displayedPosts, key = { _, post -> "post_${post.id}" }) { index, item ->
                        if (index > 0 && index % 3 == 0) {
                            AdBannerComponent()
                        }

                        // Section 2.19: Haptic Swipe to Dismiss for Read Pulses
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                    dismissedPostIds = dismissedPostIds + item.id
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val isDismissing = dismissState.targetValue != SwipeToDismissBoxValue.Settled
                                if (isDismissing) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp, vertical = 3.dp)
                                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(end = 20.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Dismiss Pulse",
                                                tint = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Text(
                                                text = "Dismiss Pulse",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            if (item.isFlashPulse) {
                                EphemeralPulseCard(
                                    post = item,
                                    totalDurationHours = 4,
                                    remainingMinutesInitial = 195,
                                    onDismiss = { dismissedPostIds = dismissedPostIds + item.id },
                                    modifier = Modifier.animateItem()
                                )
                            } else if (item.isVoicePrint) {
                                AudioVoicePulseCard(
                                    post = item,
                                    durationSeconds = item.voiceDurationSeconds.coerceAtLeast(15),
                                    modifier = Modifier.animateItem()
                                )
                            } else {
                                PostCard(
                                    post = item,
                                    onLikeClick = { onLikePost(item) },
                                    onCommentClick = { onCommentPost(item) },
                                    onShareClick = { onSharePost(item) },
                                    onSaveClick = { onSavePost(item) },
                                    onUserClick = { onUserProfileClick(item.username) },
                                    onReportClick = { reason -> onReportPost(item, reason) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                        
                        if (index > 0 && index % 5 == 0 && sponsoredAds.isNotEmpty()) {
                            val adIndex = (index / 5) % sponsoredAds.size
                            val ad = sponsoredAds[adIndex]
                            SponsoredAdCard(
                                ad = ad,
                                currentCurrency = currentCurrency,
                                currentLanguage = currentLanguage,
                                onAdImpression = { onAdImpression(ad.id) },
                                onAdClick = { onAdClick(ad.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }

        // Section 2.9: Ghost Mode Floating Watermark Pill
        GhostModeFeedWatermark(
            isVisible = showGhostWatermark,
            onDismiss = { showGhostWatermark = false },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Go Live Setup Modal
        if (showGoLiveDialog) {
            GoLiveSetupModal(
                onDismiss = { showGoLiveDialog = false },
                onStartBroadcast = { title, reach ->
                    liveBroadcastTitle = title
                    liveBroadcastReach = reach
                    isCurrentlyLive = true
                    liveElapsedSeconds = 0
                    liveViewersCount = 3
                    showGoLiveDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoLiveSetupModal(
    onDismiss: () -> Unit,
    onStartBroadcast: (title: String, reach: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedReach by remember { mutableStateOf("NEIGHBOR (5 km)") }
    var selectedCategory by remember { mutableStateOf("Community Moments") }
    var enableChat by remember { mutableStateOf(true) }

    val reachOptions = listOf("NEIGHBOR (5 km)", "CITY (50 km)", "EARTH (Global)")
    val categories = listOf("Community Moments", "Local News / Alert", "Live Music / Arts", "Marketplace Goods", "Open Discussion")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Text(
                        text = "Go Live to Community",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Broadcast Title
            Text("Broadcast Title:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g., Live Coffee Roasting at Market Square...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Blast Radius / Reach Selector
            Text("Audience Reach:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                reachOptions.forEach { reach ->
                    val isSelected = selectedReach == reach
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedReach = reach },
                        label = { Text(reach, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Chips
            Text("Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chat & Remarks Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Live Community Remarks", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Allow viewers to send live remarks & reactions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = enableChat,
                    onCheckedChange = { enableChat = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start Live Broadcast Button
            Button(
                onClick = {
                    val finalTitle = title.ifBlank { "Live Community Stream" }
                    onStartBroadcast(finalTitle, selectedReach)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Live Stream 🔴", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PostSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(shimmerColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Box(modifier = Modifier.height(14.dp).width(120.dp).background(shimmerColor, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.height(10.dp).width(80.dp).background(shimmerColor, RoundedCornerShape(4.dp)))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(14.dp).fillMaxWidth().background(shimmerColor, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.8f).background(shimmerColor, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(shimmerColor, RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
            }
        }
    }
}

@Composable
fun PulseClipCard(clip: ClipEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(clip.mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Clip thumbnail",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(48.dp)
                )
            }
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = clip.userAvatar,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = clip.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        CreatorBadgeIcon(followers = clip.creatorFollowers, showText = false)
                    }
                    Text(text = clip.caption, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun PulseMarketItemCard(item: MarketplaceItemEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "@${item.sellerUsername}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$${item.price}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
