package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.graphicsLayer
import com.example.data.copyright.CopyrightManager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ClipEntity
import com.example.ui.components.AdBannerComponent
import com.example.ui.components.AnimatedLikeButton
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.components.formatCount
import com.example.ui.theme.EditorialVerified
import com.example.util.LocationHelper
import com.example.util.LocalizationHelper
import com.example.util.LocalAppLanguage
import com.example.util.LocaliiiyLanguage
import com.example.util.ShareHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

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
    onConvertClipToMarket: (clipId: Long, price: Double, category: String, condition: String, pickupSpot: String, isService: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(ClipsFeedFilter.ALL) }
    var selectedNearbyDistanceKm by remember { mutableStateOf<Double?>(3.0) }
    var isAutoScrollEnabled by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var selectedSoundtrackClip by remember { mutableStateOf<ClipEntity?>(null) }

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

    val currentClip = displayedClips.getOrNull(pagerState.currentPage)

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
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val clip = displayedClips[page]
                val isCurrentPage = pagerState.currentPage == page

                Box(modifier = Modifier.fillMaxSize()) {
                    ClipItem(
                        clip = clip,
                        isCurrentPage = isCurrentPage,
                        isPlaying = isPlaying,
                        onTogglePlayPause = { isPlaying = !isPlaying },
                        isSoundMuted = isSoundMuted,
                        onToggleSound = onToggleSound,
                        onLikeClip = { onLikeClip(clip) },
                        onCommentClip = { onCommentClip(clip) },
                        onShareClip = { onShareClip(clip) },
                        onSaveClip = { onSaveClip(clip) },
                        onFollowToggle = { onFollowToggle(clip) },
                        onUserProfileClick = { onUserProfileClick(clip.username) },
                        onWaveClick = { onWaveClick(clip) },
                        onOpenSoundtrack = { selectedSoundtrackClip = clip },
                        onReportClip = { reason -> onReportClip?.invoke(clip, reason) },
                        onBlockCreator = { onBlockCreator?.invoke(clip.username) },
                        onConvertClipToMarket = onConvertClipToMarket,
                        currentLanguage = currentLanguage,
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
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 120.dp)
                        ) {
                            AdBannerComponent()
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 1. TOP APP BAR & HEADER (COMPACT & UNIFIED 3-ROW ERGONOMIC STACK)
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.88f),
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .zIndex(15f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1: Header Branding & Inline Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Localiiiy Clips branding matching Localiiiy Studio scale and uniformity
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Localiiiy",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                        Surface(
                            color = Color(0xFF00E5FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CLIPS",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Right: Inline controls horizontally with 8dp spacing
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Auto-Scroll Toggle (icon button with on/off active badge)
                        Surface(
                            shape = CircleShape,
                            color = if (isAutoScrollEnabled) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.dp,
                                if (isAutoScrollEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { isAutoScrollEnabled = !isAutoScrollEnabled }
                                .testTag("auto_scroll_toggle")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = "Auto-scroll toggle",
                                    tint = if (isAutoScrollEnabled) Color(0xFF00E5FF) else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // 2. Play / Pause Toggle button
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { isPlaying = !isPlaying }
                                .testTag("clips_play_pause_top_toggle")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // 3. Mute / Unmute Volume button
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(onClick = onToggleSound)
                                .testTag("clips_sound_top_toggle")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSoundMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isSoundMuted) "Unmute" else "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Row 2: Translucent Categories / Pills Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clips_filter_row"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClipsFeedFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f),
                            border = BorderStroke(
                                0.8.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.22f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFilter = filter }
                                .testTag("clip_filter_${filter.name.lowercase()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                val localizedLabel = remember(filter.label, currentLanguage) {
                                    LocalizationHelper.getFilterLabel(filter.label, currentLanguage)
                                }
                                Icon(
                                    imageVector = filter.icon,
                                    contentDescription = localizedLabel,
                                    tint = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = localizedLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Row 3: Audio Metadata Bar (Subtle single-line scrolling music ticker)
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.18f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (currentClip != null) selectedSoundtrackClip = currentClip
                        }
                        .testTag("clips_audio_metadata_ticker")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("🎵", fontSize = 11.sp)
                        val trackName = currentClip?.soundTitle?.ifBlank { "Original Audio" } ?: "Original Audio"
                        val artistName = currentClip?.soundArtist?.ifBlank { currentClip.username } ?: "Localiiiy Creator"
                        Text(
                            text = "Original Audio • $trackName • $artistName",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }
        }
    }

    // Top-Level Audio Soundtrack Details Bottom Sheet
    if (selectedSoundtrackClip != null) {
        val stClip = selectedSoundtrackClip!!
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { selectedSoundtrackClip = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎵", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "${stClip.soundTitle} • ${stClip.soundArtist}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Original Audio • 342 clips recorded nearby",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { selectedSoundtrackClip = null },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Use This Audio In New Clip 🎙️", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Floating Particle Model for Quick Emoji Reactions
class EmojiParticleItem(
    val id: Long,
    val emoji: String,
    val xOffsetDp: Float
)

@Composable
private fun FloatingEmojiView(item: EmojiParticleItem) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
    }
    val progress = animProgress.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 140.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = item.emoji,
            fontSize = (32 + (progress * 14)).sp,
            modifier = Modifier
                .offset(
                    x = (item.xOffsetDp + kotlin.math.sin(progress * 6f) * 22f).dp,
                    y = (-progress * 460).dp
                )
                .graphicsLayer {
                    alpha = (1f - progress).coerceIn(0f, 1f)
                    scaleX = 1f + progress * 0.4f
                    scaleY = 1f + progress * 0.4f
                }
        )
    }
}

@Composable
private fun ClipItem(
    clip: ClipEntity,
    isCurrentPage: Boolean,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onLikeClip: () -> Unit,
    onCommentClip: () -> Unit,
    onShareClip: () -> Unit,
    onSaveClip: () -> Unit,
    onFollowToggle: () -> Unit,
    onUserProfileClick: () -> Unit,
    onWaveClick: () -> Unit = {},
    onOpenSoundtrack: () -> Unit = {},
    onReportClip: ((String) -> Unit)? = null,
    onBlockCreator: (() -> Unit)? = null,
    onConvertClipToMarket: ((clipId: Long, price: Double, category: String, condition: String, pickupSpot: String, isService: Boolean) -> Unit)? = null,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    isNearbyFilter: Boolean = false,
    systematicDistanceKm: Double? = 3.0,
    systematicOptions: List<com.example.ui.components.SystematicDistanceOption> = emptyList(),
    onSelectDistanceKm: (Double) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var showBigHeart by remember { mutableStateOf(false) }
    var showPlayPauseOverlay by remember { mutableStateOf(false) }
    var showCaptionsSheet by remember { mutableStateOf(false) }
    var showRemarksSheet by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }
    var showReactTray by remember { mutableStateOf(false) }
    var showVideoReactionDialog by remember { mutableStateOf(false) }
    var showLocationDrawer by remember { mutableStateOf(false) }
    var showProofOfPresenceInfo by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportSuccessSnackbar by remember { mutableStateOf(false) }
    var showNotInterestedDialog by remember { mutableStateOf(false) }
    var isDismissedByAlgorithm by remember { mutableStateOf(false) }
    var showNotInterestedFeedbackBanner by remember { mutableStateOf(false) }
    var notInterestedReasonChosen by remember { mutableStateOf("") }
    var showAiTranslationDialog by remember { mutableStateOf(false) }
    var isAiTranslated by remember { mutableStateOf(false) }
    var showSendToMarketDialog by remember { mutableStateOf(false) }
    var showMarketSuccessSnackbar by remember { mutableStateOf(false) }

    var isCinemaMode by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1f) }

    val floatingEmojis = remember { mutableStateListOf<EmojiParticleItem>() }
    val heartScale = remember { Animatable(1f) }
    val videoProgress = remember { Animatable(0f) }

    LaunchedEffect(isCurrentPage, isPlaying) {
        if (isCurrentPage && isPlaying) {
            while (true) {
                videoProgress.snapTo(0f)
                videoProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(12000, easing = LinearEasing)
                )
                com.example.util.HapticHelper.triggerHaptic(context, haptic, HapticFeedbackType.LongPress)
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

    fun triggerPlayPause() {
        onTogglePlayPause()
        showPlayPauseOverlay = true
        coroutineScope.launch {
            delay(600)
            showPlayPauseOverlay = false
        }
    }

    fun triggerEmojiReaction(emoji: String) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        repeat(4) { idx ->
            coroutineScope.launch {
                delay(idx * 70L)
                val item = EmojiParticleItem(
                    id = System.nanoTime() + idx,
                    emoji = emoji,
                    xOffsetDp = Random.nextFloat() * 160f - 80f
                )
                floatingEmojis.add(item)
                delay(1600)
                floatingEmojis.remove(item)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { handleDoubleTap() },
                    onTap = {
                        if (isCinemaMode) isCinemaMode = false else triggerPlayPause()
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
        // =========================================================================
        // FULL-BLEED VIDEO BACKGROUND (ZERO OBSTRUCTION IN MIDDLE 70%)
        // =========================================================================
        ImageWithFilter(
            mediaUrl = clip.mediaUrl,
            filterName = clip.filterName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = clip.caption
        )

        // Gradient overlay (Restricted to edges, keeping center completely transparent)
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
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // Center Play/Pause Pop Overlay
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

        // Center Pop 👌 Burst (From double-tap anywhere on screen)
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

        // Floating Emoji Particles Layer
        floatingEmojis.forEach { item ->
            FloatingEmojiView(item)
        }

        // =========================================================================
        // 2. RIGHT-HAND INTERACTION RAIL (ERGONOMIC STACK - FLUSH ALONG RIGHT MARGIN)
        // =========================================================================
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(5f)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
                .padding(end = 12.dp, top = 60.dp, bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Like Button: 3D animated hand icon + compact counter
            com.example.ui.components.AnimatedLikeButton(
                isLiked = clip.isLiked,
                onLikeClick = onLikeClip,
                likesCount = clip.likesCount,
                showCount = true,
                verticalOrientation = true,
                symbolSize = 24.sp,
                touchTargetSize = 44.dp,
                labelColor = Color.White,
                testTag = "clip_like_button_${clip.id}"
            )

            // 2. Remarks: Uniform ✍️ symbol icon matching other pages + Remarks count
            ClipActionButton(
                symbolText = "✍️",
                label = formatCount(clip.commentsCount),
                onClick = { showRemarksSheet = true },
                testTag = "clip_comments_button_${clip.id}"
            )

            // 3. Share Button: Standard share arrow + share count
            ClipActionButton(
                icon = Icons.Default.Share,
                label = formatCount(clip.sharesCount),
                onClick = onShareClip,
                testTag = "clip_share_button_${clip.id}"
            )

            // 4. User Space Avatar: 44dp circular avatar with small "+" badge if not connected
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(44.dp)
                    .clickable { showProfileModal = true }
                    .testTag("clip_avatar_${clip.id}")
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(clip.userAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = clip.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                )
                if (!clip.isFollowing) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                            .border(1.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Connect",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // 5. Creator Space QR Code: Compact 28dp QR icon badge directly beneath avatar
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { showQrModal = true }
                    .testTag("clip_qr_badge_${clip.id}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Space QR Code",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 6. Save / Bookmark: Clean bookmark icon with instant haptic feedback
            ClipActionButton(
                icon = if (clip.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = "",
                tint = if (clip.isSaved) Color(0xFFFFB703) else Color.White,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSaveClip()
                },
                testTag = "clip_save_button_${clip.id}"
            )

            // 7. React Feature: Clear "React" action with animated fly-out tray & video reaction
            ClipActionButton(
                icon = Icons.Default.AddReaction,
                label = "React",
                tint = if (showReactTray) Color(0xFF00E5FF) else Color.White,
                onClick = {
                    showLocationDrawer = false
                    showReactTray = !showReactTray
                },
                testTag = "clip_react_button_${clip.id}"
            )

            // 8. Location Badge / Pin: Location pin icon with animated slide-out drawer (only if creator enabled location)
            val hasLocation = clip.showLocation && (!clip.landmark.isNullOrBlank() || !clip.location.isNullOrBlank())
            if (hasLocation) {
                ClipActionButton(
                    icon = Icons.Default.LocationOn,
                    label = "",
                    tint = if (showLocationDrawer) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.9f),
                    onClick = {
                        showReactTray = false
                        showLocationDrawer = !showLocationDrawer
                    },
                    testTag = "clip_location_pin_button_${clip.id}"
                )
            }

            // 9. Proof-of-Presence & 100% Geocentric Feed Delivery icon
            ClipActionButton(
                icon = Icons.Default.Diamond,
                label = "",
                tint = Color(0xFF10B981),
                onClick = { showProofOfPresenceInfo = true },
                testTag = "clip_proof_of_presence_button_${clip.id}"
            )

            // 10. Three Dots (MoreVert) Moderation & Utility Menu
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
                        text = { Text("Not Interested 🚫") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            showNotInterestedDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Translate Audio & Description (AI) ✨") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            showAiTranslationDialog = true
                        }
                    )

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
                            val shareText = ShareHelper.buildShareText(
                                title = clip.caption,
                                itemType = "Clip",
                                id = clip.id,
                                creatorHandle = clip.userHandle,
                                caption = clip.caption,
                                location = clip.landmark ?: clip.location,
                                language = currentLanguage
                            )
                            ShareHelper.launchNativeShare(
                                context = context,
                                shareText = shareText,
                                subject = "Watch @${clip.userHandle} on Localiiiy",
                                chooserTitle = "Share Creator Clip Outside App"
                            )
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
        }

        // =========================================================================
        // SLIDE-OUT FLY-OUT TRAYS (REACT & LOCATION)
        // =========================================================================
        // React Horizontal Fly-Out Tray (4 Quick Emojis + Video Reaction Button)
        AnimatedVisibility(
            visible = showReactTray,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 68.dp)
                .zIndex(10f)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("🔥", "😂", "👏", "📍").forEach { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    triggerEmojiReaction(emoji)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.clickable {
                            showReactTray = false
                            showVideoReactionDialog = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("Video React", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Location Drawer (Sliding out toward the left - displays location name only, no Google Maps redirect)
        val hasLocation = clip.showLocation && (!clip.landmark.isNullOrBlank() || !clip.location.isNullOrBlank())
        AnimatedVisibility(
            visible = showLocationDrawer && hasLocation,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 68.dp)
                .zIndex(10f)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f)),
                shadowElevation = 8.dp,
                modifier = Modifier.widthIn(max = 240.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("📍", fontSize = 15.sp)
                            Text(
                                text = "Locality",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }
                        IconButton(
                            onClick = { showLocationDrawer = false },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = clip.landmark ?: clip.location ?: "Local Community",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!clip.location.isNullOrBlank() && clip.landmark != null) {
                        Text(
                            text = clip.location,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    val distLabel = LocationHelper.formatDistanceLabel(clip.distanceKm)
                    Text(
                        text = "$distLabel • Proximity Verified",
                        fontSize = 10.5.sp,
                        color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        // =========================================================================
        // 3. MAIN VIEWPORT & BOTTOM CLEANUP (MAXIMUM VISIBILITY)
        // Redundant username, connect pill, and static stacked pills purged from bottom viewport
        // =========================================================================
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
                .fillMaxWidth(0.76f) // Clear space along right margin for interaction rail
                .padding(start = 14.dp, bottom = 22.dp)
        ) {
            // Systematic Distance Picker Pills (When Nearby filter is active)
            if (isNearbyFilter && systematicOptions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
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

            // Compact Locality / Landmark Badge (only if creator opted in)
            if (hasLocation) {
                val distLabel = LocationHelper.formatDistanceLabel(clip.distanceKm)
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.38f),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .clickable { showLocationDrawer = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "${clip.landmark ?: clip.location ?: "Locality"} • $distLabel",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // AI Translated Indicator
            if (isAiTranslated) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(12.dp))
                        Text("AI Translated (English) • Transcribed Audio Subtitles", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    }
                }
            }

            // Caption: Strictly 2 lines max with inline "...more" trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCaptionsSheet = true }
            ) {
                Text(
                    text = clip.caption,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 16.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (clip.caption.length > 50) {
                    Text(
                        text = " ...more",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF00E5FF)
                    )
                }
            }
        }

        // Live Video Progress Bar at Bottom Edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
        ) {
            LinearProgressIndicator(
                progress = { videoProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter),
                color = Color(0xFF00E5FF),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }

    // =========================================================================
    // MODALS & BOTTOM SHEETS
    // =========================================================================

    // Expanded Caption Sheet
    if (showCaptionsSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showCaptionsSheet = false },
            scrimColor = Color.Transparent,
            containerColor = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Caption & Hashtags", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

    // Remarks Bottom Sheet with Stickers
    if (showRemarksSheet) {
        ClipRemarksBottomSheet(
            clip = clip,
            onDismiss = { showRemarksSheet = false }
        )
    }

    // User Profile Space Sheet
    if (showProfileModal) {
        ClipUserProfileSheet(
            clip = clip,
            onFollowToggle = onFollowToggle,
            onUserProfileClick = onUserProfileClick,
            onWaveClick = onWaveClick,
            onDismiss = { showProfileModal = false }
        )
    }

    // User Profile Space QR Code Dialog
    if (showQrModal) {
        ClipQrCodeDialog(
            clip = clip,
            onDismiss = { showQrModal = false }
        )
    }

    // Video Reaction Recorder Intent Modal
    if (showVideoReactionDialog) {
        AlertDialog(
            onDismissRequest = { showVideoReactionDialog = false },
            icon = {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Record Reaction", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Record a 15-second picture-in-picture reaction to @${clip.username}'s clip.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black,
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                        modifier = Modifier.size(140.dp, 100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Camera Preview Ready", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVideoReactionDialog = false
                        triggerEmojiReaction("🎥")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Start Recording ⏺️", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVideoReactionDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Proof-of-Presence & Geocentric Engine Information Dialog
    if (showProofOfPresenceInfo) {
        AlertDialog(
            onDismissRequest = { showProofOfPresenceInfo = false },
            icon = {
                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Proof-of-Presence & Geocentric Engine", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "⚡ 100% Geocentric Feed Delivery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF6EE7B7)
                    )
                    Text(
                        text = "This clip is distributed with physical locality priority, guaranteeing authentic neighborhood visibility before algorithmic reach.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💎 Proof-of-Presence Monetization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFC7D2FE)
                    )
                    Text(
                        text = "Verified local viewers generate +$0.15/view in direct creator rewards through local safe-haven merchant sponsors.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProofOfPresenceInfo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Got It 👍", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
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
                        if (selectedReason == "Copyright or Intellectual Property Violation") {
                            CopyrightManager.fileCopyrightClaim(
                                targetContentId = "clip_${clip.id}",
                                targetContentType = "CLIP",
                                contentTitle = clip.caption.take(40),
                                claimantHandle = "current_user",
                                uploaderHandle = clip.username,
                                reason = "Intellectual property infringement reported by creator."
                            )
                        }
                        onReportClip?.invoke(selectedReason)
                        showReportSuccessSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (selectedReason == "Copyright or Intellectual Property Violation") "File Copyright Claim" else "Submit Report", color = Color.White)
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

    // UGC & Feed Tuning: Not Interested Dialog
    if (showNotInterestedDialog) {
        val notInterestedOptions = listOf(
            "I don't like this creator (@${clip.username})",
            "I've seen this clip too many times",
            "Not interested in this audio or topic",
            "Don't show clips from ${clip.landmark ?: clip.location ?: "this locality"}",
            "Repetitive, poor video quality, or unappealing"
        )
        var selectedOption by remember { mutableStateOf(notInterestedOptions.first()) }

        AlertDialog(
            onDismissRequest = { showNotInterestedDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Why aren't you interested?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your feedback directly tunes the local recommendation algorithm so you see less content like this.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    notInterestedOptions.forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedOption = opt }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedOption == opt,
                                onClick = { selectedOption = opt }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = opt,
                                fontSize = 12.5.sp,
                                color = if (selectedOption == opt) Color(0xFF00E5FF) else Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notInterestedReasonChosen = selectedOption
                        showNotInterestedDialog = false
                        isDismissedByAlgorithm = true
                        showNotInterestedFeedbackBanner = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("Submit Feedback", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotInterestedDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // AI Translation & Audio Transcription Dialog
    if (showAiTranslationDialog) {
        AlertDialog(
            onDismissRequest = { showAiTranslationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "AI Translation & Audio Subtitles",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Translate creator caption and transcribe ambient voice audio into your preferred language using on-device Gemini intelligence.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Original Caption:", fontSize = 10.sp, color = Color.Gray)
                            Text(clip.caption, fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("✨ English Translation Preview:", fontSize = 10.sp, color = Color(0xFF00E5FF))
                            Text("Verified local moments: \"${clip.caption.replace("#", "")}\"", fontSize = 12.sp, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("🎙️ Audio Dialog Transcription:", fontSize = 10.sp, color = Color(0xFF00E5FF))
                            Text("\"Welcome to our neighborhood space! Check out this fresh drop right here in the market.\"", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isAiTranslated = !isAiTranslated
                        showAiTranslationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text(if (isAiTranslated) "Revert to Original" else "Apply AI Translation ✨", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiTranslationDialog = false }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    if (showNotInterestedFeedbackBanner) {
        LaunchedEffect(Unit) {
            delay(3000)
            showNotInterestedFeedbackBanner = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                action = {
                    TextButton(onClick = {
                        isDismissedByAlgorithm = false
                        showNotInterestedFeedbackBanner = false
                    }) {
                        Text("Undo", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Text("Feed Tuned: Similar clips will not be shown.", fontSize = 12.sp)
            }
        }
    }
}

// =========================================================================
// INTERACTIVE QR CODE DIALOG
// =========================================================================
@Composable
fun ClipQrCodeDialog(
    clip: ClipEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Localiiiy Space QR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Scan with any camera to connect with @${clip.username}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // High contrast QR Canvas
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.size(200.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSize = 9
                            val cellSize = size.width / gridSize
                            val primaryColor = androidx.compose.ui.graphics.Color.Black
                            val accentColor = androidx.compose.ui.graphics.Color(0xFF00838F)

                            // Finder patterns: Top-Left, Top-Right, Bottom-Left
                            val finders = listOf(
                                Offset(0f, 0f),
                                Offset((gridSize - 3) * cellSize, 0f),
                                Offset(0f, (gridSize - 3) * cellSize)
                            )
                            finders.forEach { pos ->
                                drawRoundRect(
                                    color = primaryColor,
                                    topLeft = pos,
                                    size = Size(cellSize * 3, cellSize * 3),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                                drawRoundRect(
                                    color = androidx.compose.ui.graphics.Color.White,
                                    topLeft = Offset(pos.x + cellSize * 0.5f, pos.y + cellSize * 0.5f),
                                    size = Size(cellSize * 2, cellSize * 2),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                                drawRoundRect(
                                    color = accentColor,
                                    topLeft = Offset(pos.x + cellSize, pos.y + cellSize),
                                    size = Size(cellSize, cellSize),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                            }

                            // Dynamic deterministic pattern based on clip ID
                            val seed = clip.id.toInt()
                            for (r in 0 until gridSize) {
                                for (c in 0 until gridSize) {
                                    val inFinder = (r < 3 && c < 3) || (r < 3 && c >= gridSize - 3) || (r >= gridSize - 3 && c < 3)
                                    if (!inFinder) {
                                        val isFilled = ((r * 13 + c * 7 + seed) % 3) != 0
                                        if (isFilled) {
                                            drawRoundRect(
                                                color = if ((r + c) % 4 == 0) accentColor else primaryColor,
                                                topLeft = Offset(c * cellSize + 2f, r * cellSize + 2f),
                                                size = Size(cellSize - 4f, cellSize - 4f),
                                                cornerRadius = CornerRadius(3f, 3f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Center Avatar in QR
                        Surface(
                            shape = CircleShape,
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier.size(36.dp)
                        ) {
                            AsyncImage(
                                model = clip.userAvatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "Space ID: LOC-SP-${clip.userHandle.uppercase().replace("@", "")}-${clip.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    val shareText = "Connect with @${clip.username} on Localiiiy!\nSpace ID: LOC-SP-${clip.userHandle.uppercase().replace("@", "")}-${clip.id}\n${ShareHelper.WEBAPP_BASE_URL}/u/${clip.userHandle}\n\n📦 Android APK: ${ShareHelper.GOOGLE_DRIVE_APK_URL}"
                    ShareHelper.launchNativeShare(
                        context = context,
                        shareText = shareText,
                        subject = "Connect with @${clip.username} on Localiiiy",
                        chooserTitle = "Share Localiiiy Space QR"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                Text("Share Space QR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White)
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}

// =========================================================================
// USER PROFILE SPACE MODAL / BOTTOM SHEET
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipUserProfileSheet(
    clip: ClipEntity,
    onFollowToggle: () -> Unit,
    onUserProfileClick: () -> Unit,
    onWaveClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(72.dp)
            ) {
                AsyncImage(
                    model = clip.userAvatar,
                    contentDescription = clip.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF00E5FF), CircleShape)
                )
                if (clip.isVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = EditorialVerified,
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF0F172A), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = clip.username,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "@${clip.userHandle}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🆔", fontSize = 11.sp)
                    Text(
                        text = "Space ID: LOC-SP-${clip.userHandle.uppercase().replace("@", "")}-${clip.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Proximity & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${clip.creatorFollowers + if (clip.isFollowing) 1 else 0}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text("Connections", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = LocationHelper.formatDistanceLabel(clip.distanceKm),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF00E5FF)
                    )
                    Text("Distance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = clip.viewsCount,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text("Views", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Hyperlocal Creator & Verified Community Host. Sharing local stories, moments and crafts across our neighborhood.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Connect Action Button (STRICT "Connect" / "Connected" per AGENTS.md)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onFollowToggle() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clip.isFollowing) Color.White.copy(alpha = 0.15f) else Color(0xFF00E5FF),
                        contentColor = if (clip.isFollowing) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (clip.isFollowing) "Connected 🤝" else "Connect +",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        onWaveClick()
                        onDismiss()
                    },
                    modifier = Modifier.height(46.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Wave 👋", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    onDismiss()
                    onUserProfileClick()
                }
            ) {
                Text("View Full Space & Moments →", color = Color(0xFF00E5FF), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =========================================================================
// REMARKS DATA STRUCTURES & INTERACTIVE BOTTOM SHEET
// =========================================================================
data class ClipRemarkItem(
    val id: String,
    val username: String,
    val userHandle: String = "@$username",
    val text: String,
    val timeAgo: String,
    var likesCount: Int = 0,
    var isLiked: Boolean = false,
    val emojiCounts: androidx.compose.runtime.snapshots.SnapshotStateMap<String, Int> = androidx.compose.runtime.mutableStateMapOf("😊" to 0, "😢" to 0, "❤️" to 0, "🔥" to 0, "👏" to 0),
    val userSelectedEmojis: androidx.compose.runtime.snapshots.SnapshotStateList<String> = androidx.compose.runtime.mutableStateListOf(),
    val replies: androidx.compose.runtime.snapshots.SnapshotStateList<ClipRemarkReplyItem> = androidx.compose.runtime.mutableStateListOf()
)

data class ClipRemarkReplyItem(
    val id: String,
    val username: String,
    val text: String,
    val timeAgo: String,
    var likesCount: Int = 0,
    var isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipRemarksBottomSheet(
    clip: ClipEntity,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var replyingToRemark by remember { mutableStateOf<ClipRemarkItem?>(null) }
    var expandedRepliesRemarkId by remember { mutableStateOf<String?>(null) }

    val remarks = remember {
        androidx.compose.runtime.mutableStateListOf(
            ClipRemarkItem(
                id = "rem_1",
                username = "maya_urban",
                text = "Delicious! On my way to try some.",
                timeAgo = "10m ago",
                likesCount = 14,
                isLiked = false,
                emojiCounts = androidx.compose.runtime.mutableStateMapOf("😊" to 5, "😢" to 0, "❤️" to 8, "🔥" to 3, "👏" to 2),
                replies = androidx.compose.runtime.mutableStateListOf(
                    ClipRemarkReplyItem(
                        id = "rep_1_1",
                        username = "sam_coffee",
                        text = "Grab an extra for me! It's right around the corner.",
                        timeAgo = "4m ago",
                        likesCount = 3
                    )
                )
            ),
            ClipRemarkItem(
                id = "rem_2",
                username = "alex_green",
                text = "Love the local energy in this clip 👏 Proud of our community!",
                timeAgo = "35m ago",
                likesCount = 27,
                isLiked = true,
                emojiCounts = androidx.compose.runtime.mutableStateMapOf("😊" to 12, "😢" to 0, "❤️" to 9, "🔥" to 14, "👏" to 19)
            ),
            ClipRemarkItem(
                id = "rem_3",
                username = "jordan_pulse",
                text = "Are you open on weekends?",
                timeAgo = "2h ago",
                likesCount = 4,
                isLiked = false,
                emojiCounts = androidx.compose.runtime.mutableStateMapOf("😊" to 1, "😢" to 0, "❤️" to 2, "🔥" to 0, "👏" to 0),
                replies = androidx.compose.runtime.mutableStateListOf(
                    ClipRemarkReplyItem(
                        id = "rep_3_1",
                        username = clip.username,
                        text = "Yes! 9am to 6pm Saturday and Sunday ✨",
                        timeAgo = "1h ago",
                        likesCount = 6
                    )
                )
            ),
            ClipRemarkItem(
                id = "rem_4",
                username = "elena_creative",
                text = "Sharing with our neighborhood group! Keep creating!",
                timeAgo = "4h ago",
                likesCount = 9,
                isLiked = false,
                emojiCounts = androidx.compose.runtime.mutableStateMapOf("😊" to 4, "😢" to 0, "❤️" to 7, "🔥" to 5, "👏" to 8)
            )
        )
    }

    val stickers = listOf("☕", "🥖", "🚲", "🏡", "🛡️", "💎", "🍕", "🚀", "❤️", "👏", "🔥")
    val reactionEmojis = listOf("😊", "😢", "❤️", "🔥", "👏")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header: Title & Total Count
            val totalRemarksCount = remarks.size + remarks.sumOf { it.replies.size }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Remarks",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "$totalRemarksCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Neighborhood Stickers row
            Text(
                text = "Neighborhood Stickers:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00E5FF)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickers.forEach { sticker ->
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.12f),
                        border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                remarks.add(
                                    0,
                                    ClipRemarkItem(
                                        id = "rem_${System.currentTimeMillis()}",
                                        username = "you",
                                        text = "$sticker (Sticker reaction)",
                                        timeAgo = "Just now",
                                        likesCount = 1,
                                        isLiked = true
                                    )
                                )
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(sticker, fontSize = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Remarks List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(remarks, key = { it.id }) { remark ->
                    var isRemarkLiked by remember(remark.id) { mutableStateOf(remark.isLiked) }
                    var remarkLikesCount by remember(remark.id) { mutableStateOf(remark.likesCount) }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            // 1. Author row: Avatar circle, username & timestamp
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = remark.username.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF00E5FF)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "@${remark.username}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${remark.timeAgo}",
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }

                            // 2. Remark Text
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = remark.text,
                                fontSize = 12.5.sp,
                                color = Color.White.copy(alpha = 0.95f),
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(start = 34.dp)
                            )

                            // 3. Actions Row: Likes, Emojis, and Reply
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 34.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Like action
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            isRemarkLiked = !isRemarkLiked
                                            remark.isLiked = isRemarkLiked
                                            if (isRemarkLiked) {
                                                remarkLikesCount++
                                            } else {
                                                remarkLikesCount = maxOf(0, remarkLikesCount - 1)
                                            }
                                            remark.likesCount = remarkLikesCount
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isRemarkLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like Remark",
                                        tint = if (isRemarkLiked) Color(0xFFFF3366) else Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    if (remarkLikesCount > 0) {
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "$remarkLikesCount",
                                            fontSize = 10.5.sp,
                                            color = if (isRemarkLiked) Color(0xFFFF3366) else Color.White.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Emoji reactions (Smile, Sad, Heart, Fire, Clap) with counts
                                reactionEmojis.forEach { emoji ->
                                    val count = remark.emojiCounts[emoji] ?: 0
                                    val isSelected = remark.userSelectedEmojis.contains(emoji)

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                                        border = BorderStroke(
                                            0.6.dp,
                                            if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                if (isSelected) {
                                                    remark.userSelectedEmojis.remove(emoji)
                                                    remark.emojiCounts[emoji] = maxOf(0, count - 1)
                                                } else {
                                                    remark.userSelectedEmojis.add(emoji)
                                                    remark.emojiCounts[emoji] = count + 1
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(emoji, fontSize = 11.sp)
                                            if (count > 0 || isSelected) {
                                                Text(
                                                    text = "${remark.emojiCounts[emoji] ?: count}",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Reply Button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            replyingToRemark = remark
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Reply,
                                        contentDescription = "Reply",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Reply",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                            }

                            // 4. Threaded Replies Section
                            if (remark.replies.isNotEmpty()) {
                                val isExpanded = expandedRepliesRemarkId == remark.id
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(start = 34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            expandedRepliesRemarkId = if (isExpanded) null else remark.id
                                        }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isExpanded) "⎯ Hide replies" else "⎯ View ${remark.replies.size} ${if (remark.replies.size == 1) "reply" else "replies"}",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF00E5FF)
                                    )
                                }

                                if (isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 38.dp, top = 4.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        remark.replies.forEach { reply ->
                                            var isReplyLiked by remember(reply.id) { mutableStateOf(reply.isLiked) }
                                            var replyLikesCount by remember(reply.id) { mutableStateOf(reply.likesCount) }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White.copy(alpha = 0.04f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = "@${reply.username}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF00E5FF)
                                                            )
                                                            Spacer(modifier = Modifier.width(5.dp))
                                                            Text(
                                                                text = "• ${reply.timeAgo}",
                                                                fontSize = 9.5.sp,
                                                                color = Color.White.copy(alpha = 0.45f)
                                                            )
                                                        }
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.clickable {
                                                                isReplyLiked = !isReplyLiked
                                                                reply.isLiked = isReplyLiked
                                                                if (isReplyLiked) replyLikesCount++ else replyLikesCount = maxOf(0, replyLikesCount - 1)
                                                                reply.likesCount = replyLikesCount
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = if (isReplyLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                                contentDescription = null,
                                                                tint = if (isReplyLiked) Color(0xFFFF3366) else Color.White.copy(alpha = 0.5f),
                                                                modifier = Modifier.size(11.dp)
                                                            )
                                                            if (replyLikesCount > 0) {
                                                                Spacer(modifier = Modifier.width(2.dp))
                                                                Text(
                                                                    text = "$replyLikesCount",
                                                                    fontSize = 9.5.sp,
                                                                    color = Color.White.copy(alpha = 0.7f)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = reply.text,
                                                        fontSize = 11.5.sp,
                                                        color = Color.White.copy(alpha = 0.9f)
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reply Banner if replying to a specific remark
            if (replyingToRemark != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    border = BorderStroke(0.6.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Replying to @${replyingToRemark?.username}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00E5FF)
                        )
                        IconButton(
                            onClick = { replyingToRemark = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = {
                        Text(
                            text = if (replyingToRemark != null) "Reply to @${replyingToRemark?.username}..." else "Add a remark or sticker...",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        val trimmed = newCommentText.trim()
                        if (trimmed.isNotBlank()) {
                            val target = replyingToRemark
                            if (target != null) {
                                target.replies.add(
                                    ClipRemarkReplyItem(
                                        id = "rep_${System.currentTimeMillis()}",
                                        username = "you",
                                        text = trimmed,
                                        timeAgo = "Just now"
                                    )
                                )
                                expandedRepliesRemarkId = target.id
                                replyingToRemark = null
                            } else {
                                remarks.add(
                                    0,
                                    ClipRemarkItem(
                                        id = "rem_${System.currentTimeMillis()}",
                                        username = "you",
                                        text = trimmed,
                                        timeAgo = "Just now"
                                    )
                                )
                            }
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF00E5FF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post Remark",
                        tint = Color.Black,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// ERGONOMIC ACTION BUTTON COMPONENT
// =========================================================================
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
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .border(0.8.dp, Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (symbolText != null) {
                Text(
                    text = symbolText,
                    fontSize = 22.sp,
                    modifier = Modifier.scale(scale)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label.ifBlank { null },
                    tint = tint,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                )
            }
        }
        if (label.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun SendClipToMarketDialog(
    clip: ClipEntity,
    onDismiss: () -> Unit,
    onConfirm: (price: Double, category: String, condition: String, hub: String, isService: Boolean) -> Unit
) {
    var isServices by remember { mutableStateOf(false) }
    var priceText by remember { mutableStateOf(if (clip.marketPriceUSD > 0) "${clip.marketPriceUSD.toInt()}" else "45.00") }

    val goodsCategories = listOf("Merchandise", "Electronics", "Mobile Phones", "Fashion & Apparel", "Home & Garden", "Vehicles", "Art & Craft")
    val servicesCategories = listOf("Jobs & Services", "Tech Support", "Lessons & Tutoring", "Home & Handyman", "Beauty & Care", "Freelance Creative")

    var selectedCategory by remember(isServices) {
        mutableStateOf(if (isServices) servicesCategories.first() else goodsCategories.first())
    }

    val goodsConditions = listOf("Brand New", "Like New", "Gently Used", "Refurbished")
    val servicesConditions = listOf("Hourly Rate", "Fixed Project", "In-Person Service", "Remote/Online")

    var selectedCondition by remember(isServices) {
        mutableStateOf(if (isServices) servicesConditions.first() else (clip.marketCondition.ifBlank { goodsConditions[1] }))
    }

    var selectedHub by remember {
        mutableStateOf(clip.marketPickupSpot.ifBlank { "Civic Plaza Police Precinct (CCTV Zone)" })
    }

    val safeHubs = listOf(
        "Civic Plaza Police Precinct (CCTV Zone)",
        "Downtown Transit Hub Verified Safe Zone",
        "Public Library Front Foyer (Monitored)",
        "Seller Studio / Client On-Site Location"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (isServices) "💼" else "🛍️", fontSize = 22.sp)
                Column {
                    Text(
                        text = if (isServices) "Send Clip to Market Services" else "Send Clip to Market Goods",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Turn your uploaded clip into an active Market listing",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type Selector: Goods vs Services
                Text("Select Listing Type", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isServices) Color(0xFFF59E0B).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.5.dp, if (!isServices) Color(0xFFF59E0B) else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isServices = false
                                selectedCategory = goodsCategories.first()
                                selectedCondition = goodsConditions[1]
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🛍️", fontSize = 20.sp)
                            Text("Goods", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Physical products", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isServices) Color(0xFF2563EB).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.5.dp, if (isServices) Color(0xFF2563EB) else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isServices = true
                                selectedCategory = servicesCategories.first()
                                selectedCondition = servicesConditions.first()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💼", fontSize = 20.sp)
                            Text("Services", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Skills & freelance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Price Input
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(if (isServices) "Service Fee / Rate ($ USD)" else "Listing Price ($ USD)") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Text(
                    text = if (isServices) "Service Category" else "Goods Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val activeCategories = if (isServices) servicesCategories else goodsCategories
                    activeCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Condition or Delivery Mode
                Text(
                    text = if (isServices) "Engagement / Delivery Mode" else "Item Condition",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val activeConditions = if (isServices) servicesConditions else goodsConditions
                    activeConditions.forEach { cond ->
                        FilterChip(
                            selected = selectedCondition == cond,
                            onClick = { selectedCondition = cond },
                            label = { Text(cond, fontSize = 11.sp) }
                        )
                    }
                }

                // Safe-Haven Offline Exchange Hub / Location
                Text(
                    text = if (isServices) "Service Location / Hub" else "Safe-Haven Offline Exchange Hub",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    safeHubs.forEach { hub ->
                        val isSelected = selectedHub == hub
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedHub = hub }
                        ) {
                            Text(
                                text = "🛡️ $hub",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 35.0
                    onConfirm(price, selectedCategory, selectedCondition, selectedHub, isServices)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isServices) Color(0xFF2563EB) else Color(0xFFF59E0B)
                )
            ) {
                Text(
                    text = if (isServices) "Send to Market Services 🚀" else "Send to Market Goods 🚀",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
