package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.data.ReelEntity
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.formatCount
import com.example.ui.theme.EditorialHeart
import com.example.ui.theme.EditorialVerified
import com.example.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReelsScreen(
    reels: List<ReelEntity>,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onLikeReel: (ReelEntity) -> Unit,
    onCommentReel: (ReelEntity) -> Unit,
    onShareReel: (ReelEntity) -> Unit,
    onSaveReel: (ReelEntity) -> Unit,
    onFollowToggle: (ReelEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onWaveClick: (ReelEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showNearbyOnly by remember { mutableStateOf(false) }

    val displayedReels = remember(reels, showNearbyOnly) {
        if (showNearbyOnly) {
            reels.filter { (it.distanceKm ?: 99.0) <= 3.0 }.ifEmpty { reels }
        } else {
            reels
        }
    }

    if (displayedReels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Reels in range",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { displayedReels.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_screen_container")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = displayedReels[page]
            val isCurrentPage = pagerState.currentPage == page

            ReelItem(
                reel = reel,
                isCurrentPage = isCurrentPage,
                isSoundMuted = isSoundMuted,
                onToggleSound = onToggleSound,
                onLikeReel = { onLikeReel(reel) },
                onCommentReel = { onCommentReel(reel) },
                onShareReel = { onShareReel(reel) },
                onSaveReel = { onSaveReel(reel) },
                onFollowToggle = { onFollowToggle(reel) },
                onUserProfileClick = { onUserProfileClick(reel.username) }
            )
        }

        // Top Header: Locali Clips Feed Toggle (All Clips vs Nearby Locality)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Clips",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = if (!showNearbyOnly) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { showNearbyOnly = false }
                )

                Text(
                    text = "•",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 16.sp
                )

                // Locality Toggle Filter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (showNearbyOnly) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.4f))
                        .clickable { showNearbyOnly = !showNearbyOnly }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Nearby filter",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Nearby Radar (< 3km)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = Color.White
                    )
                }
            }

            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isSoundMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Sound toggle",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ReelItem(
    reel: ReelEntity,
    isCurrentPage: Boolean,
    isSoundMuted: Boolean,
    onToggleSound: () -> Unit,
    onLikeReel: () -> Unit,
    onCommentReel: () -> Unit,
    onShareReel: () -> Unit,
    onSaveReel: () -> Unit,
    onFollowToggle: () -> Unit,
    onUserProfileClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(true) }
    var showBigHeart by remember { mutableStateOf(false) }
    var showPlayPauseOverlay by remember { mutableStateOf(false) }
    var isCaptionExpanded by remember { mutableStateOf(false) }

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
        if (!reel.isLiked) {
            coroutineScope.launch {
                heartScale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                heartScale.animateTo(1f)
            }
            onLikeReel()
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
                    onTap = { togglePlayPause() }
                )
            }
            .testTag("reel_item_${reel.id}")
    ) {
        ImageWithFilter(
            mediaUrl = reel.mediaUrl,
            filterName = reel.filterName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = reel.caption
        )

        // Gradient overlay
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

        // Center Spark Burst
        AnimatedVisibility(
            visible = showBigHeart,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut(tween(300)) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Sparks",
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(105.dp)
            )
        }

        // Top Proximity & Landmark Banner Overlay
        val distLabel = LocationHelper.formatDistanceLabel(reel.distanceKm)
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = Color.Black.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 52.dp)
                .clickable(onClick = onUserProfileClick)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${reel.landmark ?: reel.location ?: "Locality"} • $distLabel",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (reel.isNeighbor) {
                    Text(
                        text = "🏡 Neighbor",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Right Side Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spark Action
            ReelActionButton(
                icon = if (reel.isLiked) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                label = formatCount(reel.likesCount),
                tint = if (reel.isLiked) EditorialHeart else Color.White,
                scale = heartScale.value,
                onClick = {
                    coroutineScope.launch {
                        heartScale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        heartScale.animateTo(1f)
                    }
                    onLikeReel()
                },
                testTag = "reel_like_button_${reel.id}"
            )

            // Discuss / Chat Action
            ReelActionButton(
                icon = Icons.AutoMirrored.Outlined.Chat,
                label = formatCount(reel.commentsCount),
                onClick = onCommentReel,
                testTag = "reel_comments_button_${reel.id}"
            )

            // Share / Relay Action
            ReelActionButton(
                icon = Icons.Default.Share,
                label = formatCount(reel.sharesCount),
                onClick = onShareReel,
                testTag = "reel_share_button_${reel.id}"
            )

            // Pin / Save
            ReelActionButton(
                icon = if (reel.isSaved) Icons.Default.PushPin else Icons.Outlined.PushPin,
                label = "",
                tint = if (reel.isSaved) Color(0xFFFFB703) else Color.White,
                onClick = onSaveReel,
                testTag = "reel_save_button_${reel.id}"
            )

            // Rotating Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .rotate(if (isPlaying) rotationAngle else 0f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(reel.userAvatar)
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
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 28.dp)
        ) {
            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(reel.userAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = reel.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onUserProfileClick)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = reel.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.clickable(onClick = onUserProfileClick)
                )

                if (reel.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = EditorialVerified,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Follow / Following Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(100.dp))
                        .background(if (reel.isFollowing) Color.Transparent else Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onFollowToggle)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("reel_follow_button_${reel.id}")
                ) {
                    Text(
                        text = if (reel.isFollowing) "In Orbit ⚡" else "+ Pulse Link",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            // Reel Caption with Location & Landmark Tag
            Text(
                text = reel.caption,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color.White,
                maxLines = if (isCaptionExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { isCaptionExpanded = !isCaptionExpanded }
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
                    text = "${reel.soundTitle} • ${reel.soundArtist}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Live Video Progress Bar
        LinearProgressIndicator(
            progress = { videoProgress.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun ReelActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
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
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(28.dp)
                .scale(scale)
        )
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
