package com.example.ui.screens

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.components.AdBannerComponent
import com.example.ui.components.SystematicDistanceScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ClipEntity
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.formatCount
import com.example.ui.theme.EditorialHeart
import com.example.ui.theme.EditorialVerified
import com.example.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

enum class ClipsFeedFilter(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.Public),
    TRENDING("Trending", Icons.AutoMirrored.Filled.TrendingUp),
    NEARBY("Nearby", Icons.Default.NearMe),
    CONNECTED("Connected", Icons.Default.Person)
}

@Composable
fun ClipsScreen(
    clips: List<ClipEntity>,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onLikeClip: (ClipEntity) -> Unit,
    onCommentClip: (ClipEntity) -> Unit,
    onShareClip: (ClipEntity) -> Unit,
    onSaveClip: (ClipEntity) -> Unit,
    onFollowToggle: (ClipEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onWaveClick: (ClipEntity) -> Unit = {},
    onReportClip: ((ClipEntity, String) -> Unit)? = null,
    onBlockCreator: ((String) -> Unit)? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    deepLinkClipId: Long? = null,
    onClearDeepLink: () -> Unit = {},
    countryName: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(ClipsFeedFilter.ALL) }
    var selectedNearbyDistanceKm by remember { mutableStateOf<Double?>(3.0) }
    var isAutoScrollEnabled by remember { mutableStateOf(false) }

    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }

    val coroutineScope = rememberCoroutineScope()

    val displayedClips = remember(clips, selectedFilter, selectedNearbyDistanceKm) {
        when (selectedFilter) {
            ClipsFeedFilter.ALL -> clips
            ClipsFeedFilter.TRENDING -> clips.sortedByDescending { (it.likesCount * 3) + it.commentsCount }
            ClipsFeedFilter.NEARBY -> {
                val limit = selectedNearbyDistanceKm ?: 3.0
                val filtered = clips.filter { (it.distanceKm ?: 99.0) <= limit }
                if (filtered.isNotEmpty()) filtered.sortedBy { it.distanceKm ?: 99.0 }
                else clips.sortedBy { it.distanceKm ?: 99.0 }
            }
            ClipsFeedFilter.CONNECTED -> {
                val connected = clips.filter { it.isFollowing }
                if (connected.isNotEmpty()) connected else clips
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { displayedClips.size })

    // Automatically navigate to deep-linked clip if opened from external link
    LaunchedEffect(deepLinkClipId, displayedClips) {
        if (deepLinkClipId != null && displayedClips.isNotEmpty()) {
            val targetIdx = displayedClips.indexOfFirst { it.id == deepLinkClipId }
            if (targetIdx != -1) {
                selectedFilter = ClipsFeedFilter.ALL
                pagerState.scrollToPage(targetIdx)
                onClearDeepLink()
            }
        }
    }


    // Background Video Preloading Queue
    LaunchedEffect(pagerState.currentPage, displayedClips) {
        val nextIndices = listOf(
            pagerState.currentPage + 1,
            pagerState.currentPage + 2
        ).filter { it < displayedClips.size }
        
        val urlsToCache = nextIndices.map { displayedClips[it].mediaUrl }
        if (urlsToCache.isNotEmpty()) {
            com.example.util.ExoPlayerCacheHelper.preCacheVideos(context, urlsToCache)
        }
    }

    if (isAutoScrollEnabled && displayedClips.isNotEmpty()) {
        LaunchedEffect(isAutoScrollEnabled, pagerState.settledPage) {
            delay(11000L) // Auto scroll 1 second before completion of clip (11s out of 12s)
            if (isAutoScrollEnabled && displayedClips.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % displayedClips.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("clips_screen_container")
    ) {
        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = rememberPullToRefreshState(),
            modifier = Modifier.fillMaxSize()
        ) {

            VerticalPager(
                state = pagerState,
                flingBehavior = androidx.compose.foundation.pager.PagerDefaults.flingBehavior(state = pagerState),
                modifier = Modifier.fillMaxSize()
            ) { page ->
            val clip = displayedClips[page]
            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
                ClipItem(
                    clip = clip,
                    isCurrentPage = isCurrentPage,
                    isSoundMuted = isSoundMuted,
                    onToggleSound = onToggleSound,
                    onLikeClip = { onLikeClip(clip) },
                    onCommentClip = { onCommentClip(clip) },
                    onShareClip = { onShareClip(clip) },
                    onSaveClip = { onSaveClip(clip) },
                    onFollowToggle = { onFollowToggle(clip) },
                    onUserProfileClick = { onUserProfileClick(clip.username) },
                    onReportClip = { reason -> onReportClip?.invoke(clip, reason) },
                    onBlockCreator = { onBlockCreator?.invoke(clip.username) },
                    isNearbyFilter = selectedFilter == ClipsFeedFilter.NEARBY,
                    systematicDistanceKm = selectedNearbyDistanceKm,
                    systematicOptions = systematicOptions,
                    onSelectDistanceKm = { selectedNearbyDistanceKm = it }
                )
                
                // Show AdBanner on every 4th clip, placed near the bottom
                if (page > 0 && page % 4 == 0 && isCurrentPage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .padding(bottom = 120.dp) // Avoid obscuring the bottom actions
                    ) {
                        AdBannerComponent()
                    }
                }
            }
            }
        }

        // Top Header: Sleek, compact single-row discovery filter bar that does not obscure video content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clips Title
                Text(
                    text = "Clips",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Discovery Filter Pills Row (All, Trending, Nearby, Connected)
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("clips_filter_row"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(ClipsFeedFilter.values()) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.55f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .clickable { selectedFilter = filter }
                                .testTag("clip_filter_${filter.name.lowercase()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = filter.icon,
                                    contentDescription = filter.label,
                                    tint = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Compact Controls (Auto-Scroll & Sound)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Auto-Scroll Toggle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isAutoScrollEnabled) MaterialTheme.colorScheme.tertiary else Color.Black.copy(alpha = 0.5f))
                            .clickable { isAutoScrollEnabled = !isAutoScrollEnabled }
                            .testTag("auto_scroll_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAutoScrollEnabled) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Auto-scroll toggle",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Sound Toggle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(onClick = onToggleSound),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSoundMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Sound toggle",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
}
}


@Composable
private fun ClipItem(
    clip: ClipEntity,
    isCurrentPage: Boolean,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onLikeClip: () -> Unit,
    onCommentClip: () -> Unit,
    onShareClip: () -> Unit,
    onSaveClip: () -> Unit,
    onFollowToggle: () -> Unit,
    onUserProfileClick: () -> Unit,
    onReportClip: ((String) -> Unit)? = null,
    onBlockCreator: (() -> Unit)? = null,
    isNearbyFilter: Boolean = false,
    systematicDistanceKm: Double? = 3.0,
    systematicOptions: List<com.example.ui.components.SystematicDistanceOption> = emptyList(),
    onSelectDistanceKm: (Double) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(true) }
    var showBigHeart by remember { mutableStateOf(false) }
    var showPlayPauseOverlay by remember { mutableStateOf(false) }
    var isCaptionExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportSuccessSnackbar by remember { mutableStateOf(false) }


    val haptic = LocalHapticFeedback.current
    var isCinemaMode by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1f) }
    var showCaptionsSheet by remember { mutableStateOf(false) }
    var showSoundtrackSheet by remember { mutableStateOf(false) }
    
    // Modify video progress tween based on playbackSpeed (mock behavior)
    val heartScale = remember { Animatable(1f) }

    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_angle"
    )

    val videoProgress = remember { Animatable(0f) }

    LaunchedEffect(isCurrentPage, isPlaying) {
        if (isCurrentPage && isPlaying) {
            while (true) {
                videoProgress.snapTo(0f)
                videoProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(12000, easing = LinearEasing)
                )
            }
        } else {
            videoProgress.stop()
        }
    }


    fun handleDoubleTap() {
        showBigHeart = true
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

        if (!clip.isLiked) {
            coroutineScope.launch {
                heartScale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                heartScale.animateTo(1f)
            }
            onLikeClip()
        }
        coroutineScope.launch {
            delay(800)
            showBigHeart = false
        }
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
        showPlayPauseOverlay = true
        coroutineScope.launch {
            delay(600)
            showPlayPauseOverlay = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { handleDoubleTap() },
                    onTap = { 
                        if (isCinemaMode) isCinemaMode = false else togglePlayPause() 
                    },
                    onPress = {
                        playbackSpeed = 2f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tryAwaitRelease()
                        playbackSpeed = 1f
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom > 1.2f && !isCinemaMode) {
                        isCinemaMode = true
                    }
                }
            }
            .testTag("clip_item_${clip.id}")
    ) {
        ImageWithFilter(
            mediaUrl = clip.mediaUrl,
            filterName = clip.filterName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = clip.caption
        )

        // Gradient overlay
        AnimatedVisibility(
            visible = !isCinemaMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )
        }

        // Center Play/Pause
        AnimatedVisibility(
            visible = showPlayPauseOverlay,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPlaying) "Playing" else "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        // Center Pop 👌 Burst
        AnimatedVisibility(
            visible = showBigHeart,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut(tween(300)) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "👌",
                fontSize = 96.sp
            )
        }

        // Right Side Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Action (Animated 👌 with 3D perspective depth, glowing burst, and tactile feedback)
            com.example.ui.components.AnimatedLikeButton(
                isLiked = clip.isLiked,
                onLikeClick = onLikeClip,
                likesCount = clip.likesCount,
                showCount = true,
                verticalOrientation = true,
                symbolSize = 26.sp,
                touchTargetSize = 44.dp,
                labelColor = Color.White,
                testTag = "clip_like_button_${clip.id}"
            )

            // Discuss / Chat Action (✍️ without animation)
            ClipActionButton(
                symbolText = "✍️",
                label = formatCount(clip.commentsCount),
                onClick = onCommentClip,
                testTag = "clip_comments_button_${clip.id}"
            )

            
            // Quick Reply Ephemeral Video Reaction
            ClipActionButton(
                icon = Icons.Default.Videocam,
                label = "React",
                onClick = { /* Launch circular camera */ },
                testTag = "clip_react_button_${clip.id}"
            )

            // Share / Relay Action
            ClipActionButton(
                icon = Icons.Default.Share,
                label = formatCount(clip.sharesCount),
                onClick = onShareClip,
                testTag = "clip_share_button_${clip.id}"
            )

            // Pin / Save
            ClipActionButton(
                icon = if (clip.isSaved) Icons.Default.PushPin else Icons.Outlined.PushPin,
                label = "",
                tint = if (clip.isSaved) Color(0xFFFFB703) else Color.White,
                onClick = onSaveClip,
                testTag = "clip_save_button_${clip.id}"
            )

            // Moderation 3-Dots Menu
            Box {
                ClipActionButton(
                    icon = Icons.Default.MoreVert,
                    label = "",
                    onClick = { showMenu = true },
                    testTag = "clip_more_menu_button_${clip.id}"
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Share Deep Link Outside App 🔗") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            val deepLinkUrl = "https://localiiiy.app/clip/${clip.id}"
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Watch @${clip.userHandle} on Localiiiy")
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "Check out this clip by @${clip.userHandle} on Localiiiy! 🎥✨\n\"${clip.caption}\"\n\nWatch here: $deepLinkUrl"
                                )
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Creator Clip Deep Link"))
                        }
                    )

                    
                    DropdownMenuItem(
                        text = { Text("Save Video ⬇️") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            // Download with Creator Attribution Watermark
                            // Downloads MP4 embedding @${clip.userHandle} and ${clip.location} in metadata
                            onSaveClip()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Report Clip 🚩") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            showReportDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Block @${clip.username} 🚫") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onBlockCreator?.invoke()
                        }
                    )
                }
            }

            // Rotating Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .rotate(if (isPlaying) rotationAngle else 0f)
                    .clickable { showSoundtrackSheet = true },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(clip.userAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Audio Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                )
            }
        }

        // Bottom Left Creator Info & Caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 28.dp)
        ) {
            // Systematic Distance Picker Pills (When Nearby filter is active)
            if (isNearbyFilter && systematicOptions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .testTag("clips_distance_row"),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(systematicOptions) { opt ->
                        val isSelected = systematicDistanceKm != null && (
                                kotlin.math.abs(systematicDistanceKm - opt.km) < 0.1 ||
                                (opt.key == "COUNTRY" && systematicDistanceKm == SystematicDistanceScale.COUNTRY_DEFAULT_KM) ||
                                (opt.key == "EARTH" && systematicDistanceKm == SystematicDistanceScale.EARTH_KM) ||
                                (opt.key == "GALAXY" && systematicDistanceKm == SystematicDistanceScale.GALAXY_KM)
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { onSelectDistanceKm(opt.km) }
                                .testTag("clips_distance_${opt.key.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(opt.icon, fontSize = 10.sp)
                                Text(
                                    text = opt.shortLabel,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Proximity & Landmark Badge (Positioned directly above creator info to prevent obscuring top header controls)
            val distLabel = LocationHelper.formatDistanceLabel(clip.distanceKm)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(onClick = onUserProfileClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${clip.landmark ?: clip.location ?: "Locality"} • $distLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (clip.isNeighbor) {
                        Text(
                            text = "🏡 Neighbor",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            
            // Dual-Reach Overlay Indicator & Featured Gear
            Row(
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isCitySeed = (clip.distanceKm ?: 99.0) <= 50.0
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isCitySeed) "📍 City Seed" else "🌐 Earth Reach",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCitySeed) Color(0xFF00C853) else Color(0xFF2979FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { /* Open market */ }
                ) {
                    Text(
                        text = "🛍️ Featured Gear",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB703),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                if ((clip.distanceKm ?: 99.0) <= 3.0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🔊 Hyperlocal Audio",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(clip.userAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = clip.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onUserProfileClick)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = clip.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.clickable(onClick = onUserProfileClick)
                )

                if (clip.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = EditorialVerified,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Connect / Connected Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(100.dp))
                        .background(if (clip.isFollowing) Color.Transparent else Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onFollowToggle)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("clip_follow_button_${clip.id}")
                ) {
                    Text(
                        text = if (clip.isFollowing) "Connected" else "Connect",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            // Clip Caption with Location & Landmark Tag
            Text(
                text = clip.caption + " ...more",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { showCaptionsSheet = true }
                    .padding(bottom = 8.dp)
            )

            // Music / Audio Ticker Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio track",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${clip.soundTitle} • ${clip.soundArtist}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Live Video Progress Bar with Chapters
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
        ) {
            val isHyperlocalAudio = (clip.distanceKm ?: 99.0) <= 3.0
            val barColor = if (isHyperlocalAudio) Color(0xFFFF9800) else Color.White
            
            LinearProgressIndicator(
                progress = { videoProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter),
                color = barColor,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            // Chapter markers (Mock positions)
            val chapterPositions = listOf(0.2f, 0.5f, 0.8f)
            chapterPositions.forEach { pos ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pos)
                        .align(Alignment.BottomStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }


    if (showCaptionsSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showCaptionsSheet = false },
            scrimColor = Color.Transparent,
            containerColor = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Caption", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = clip.caption,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }


    if (showSoundtrackSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showSoundtrackSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text(
                    text = "${clip.soundTitle} • ${clip.soundArtist}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "342 clips recorded with this ambient stem nearby",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Mock grid of clips would go here
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text("Related Clips Grid", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // UGC Compliance: Report Clip Dialog
    if (showReportDialog) {
        val reportReasons = listOf(
            "Spam, Scam or Misleading",
            "Harassment, Bullying or Hate Speech",
            "Sexually Explicit or Inappropriate Content",
            "Violence or Dangerous Behavior",
            "Copyright or Intellectual Property Violation",
            "Exposes Private Physical Address / Privacy Violation"
        )
        var selectedReason by remember { mutableStateOf(reportReasons.first()) }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text("Report Clip", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Why are you reporting this clip by @${clip.username}?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    reportReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        onReportClip?.invoke(selectedReason)
                        showReportSuccessSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Submit Report", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportSuccessSnackbar) {
        AlertDialog(
            onDismissRequest = { showReportSuccessSnackbar = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Report Received", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Thank you for reporting. This clip has been submitted for moderator review and hidden from your feed.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(onClick = { showReportSuccessSnackbar = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ClipActionButton(
    label: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    symbolText: String? = null,
    tint: Color = Color.White,
    scale: Float = 1f,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        if (symbolText != null) {
            val isLike = symbolText == "👌"
            Text(
                text = symbolText,
                fontSize = 26.sp,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        if (isLike && scale > 1.05f) {
                            rotationY = (scale - 1f) * 45f
                            rotationZ = (scale - 1f) * -30f
                            cameraDistance = 12f * density
                        }
                    }
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier
                    .size(28.dp)
                    .scale(scale)
            )
        }
        if (label.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White
            )
        }
    }
}
