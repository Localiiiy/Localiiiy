package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.StudioVideoEntity
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import kotlinx.coroutines.delay

/**
 * High-performance inline video preview player for Studio video cards while scrolling.
 * Supports:
 * - Real Media3 ExoPlayer video streaming
 * - Seamless Mute / Unmute toggle
 * - Auto-looping preview
 * - Mini player docking shortcut
 * - Fullscreen expand shortcut
 */
@OptIn(UnstableApi::class)
@Composable
fun StudioInlinePreviewPlayer(
    video: StudioVideoEntity,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onStartMiniPlayer: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isBuffering by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var progressFraction by remember { mutableFloatStateOf(0f) }

    val exoPlayer = remember(video.id) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            volume = if (isMuted) 0f else 1f
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(video.videoUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            } catch (e: Exception) {
                hasError = true
            }
        }
    }

    // Sync volume when isMuted changes
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Player events listener
    DisposableEffect(exoPlayer) {
        var fallbackAttempted = false
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> isBuffering = true
                    Player.STATE_READY -> isBuffering = false
                    Player.STATE_ENDED -> isBuffering = false
                    Player.STATE_IDLE -> isBuffering = false
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
                if (!fallbackAttempted && video.videoUrl != "https://media.w3.org/2010/05/sintel/trailer.mp4") {
                    fallbackAttempted = true
                    try {
                        val fallback = MediaItem.fromUri(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"))
                        exoPlayer.setMediaItem(fallback)
                        exoPlayer.prepare()
                        exoPlayer.play()
                    } catch (e: Exception) {
                        hasError = true
                        try { exoPlayer.stop() } catch (_: Exception) {}
                    }
                } else {
                    hasError = true
                    try { exoPlayer.stop() } catch (_: Exception) {}
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            try {
                exoPlayer.removeListener(listener)
                exoPlayer.stop()
                exoPlayer.release()
            } catch (_: Exception) {}
        }
    }

    // Lifecycle observer to pause when app in background
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isPlaying && !hasError) exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Periodic progress loop
    LaunchedEffect(exoPlayer, isPlaying) {
        while (true) {
            if (exoPlayer.isPlaying && exoPlayer.duration > 0) {
                progressFraction = (exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()).coerceIn(0f, 1f)
            }
            delay(400)
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable { onOpenFullPlayer() }
            .testTag("studio_inline_preview_${video.id}")
    ) {
        if (!hasError) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Gradient Scrim for readable controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
        )

        // Bottom Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Live Preview Badge (Top Left)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE50914).copy(alpha = 0.9f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PREVIEW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Text(
                    text = video.resolution,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocaliiiyAccentMint,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // Top-Right Action Controls (Mini Player & Expand Fullscreen)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            // Mini Player Dock Button
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.6f)),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onStartMiniPlayer() }
                    .testTag("inline_dock_mini_player_${video.id}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PictureInPictureAlt,
                        contentDescription = "Dock to Mini Player",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expand to Full Player Button
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onOpenFullPlayer() }
                    .testTag("inline_open_full_${video.id}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Full Player",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // Bottom-Left: MUTE / UNMUTE BUTTON with active sound indicator
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMuted) Color.Black.copy(alpha = 0.8f) else LocaliiiyPrimaryTeal.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isMuted) Color.White.copy(alpha = 0.4f) else LocaliiiyAccentMint
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clickable { onToggleMute() }
                .testTag("inline_mute_unmute_${video.id}")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isMuted) "Muted" else "Sound On",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Bottom-Right: Duration remaining or total
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "${(video.durationSeconds / 60)}:${String.format("%02d", video.durationSeconds % 60)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }

        // Buffering Spinner
        if (isBuffering && !hasError) {
            CircularProgressIndicator(
                color = LocaliiiyAccentMint,
                strokeWidth = 2.5.dp,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
            )
        }

        // Slim Progress Bar at the absolute bottom
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter),
            color = LocaliiiyAccentMint,
            trackColor = Color.White.copy(alpha = 0.2f),
        )
    }
}

/**
 * Docked Studio Mini Player:
 * Pinned above bottom navigation bar when a video is minimized or active while browsing the Studio feed.
 * Features:
 * - Mini 16:9 video player preview with live streaming
 * - Video title and Creator name with verified badge
 * - Inline MUTE / UNMUTE button
 * - Play / Pause button
 * - Expand / Fullscreen button to reopen full player
 * - Dismiss / Close button
 * - Linear progress indicator
 */
@OptIn(UnstableApi::class)
@Composable
fun StudioDockedMiniPlayer(
    video: StudioVideoEntity,
    isPlaying: Boolean,
    isMuted: Boolean,
    playbackProgress: Float,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onExpandToFullPlayer: () -> Unit,
    onCloseMiniPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var currentProgress by remember { mutableFloatStateOf(playbackProgress) }

    val exoPlayer = remember(video.id) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            volume = if (isMuted) 0f else 1f
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(video.videoUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = isPlaying
            } catch (e: Exception) {}
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) exoPlayer.play() else exoPlayer.pause()
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
                }
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                try { exoPlayer.stop() } catch (_: Exception) {}
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            try {
                exoPlayer.removeListener(listener)
                exoPlayer.stop()
                exoPlayer.release()
            } catch (_: Exception) {}
        }
    }

    // Lifecycle observer
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isPlaying) exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Update progress periodically
    LaunchedEffect(exoPlayer, isPlaying) {
        while (true) {
            if (exoPlayer.isPlaying && exoPlayer.duration > 0) {
                currentProgress = (exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()).coerceIn(0f, 1f)
            }
            delay(500)
        }
    }

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("studio_docked_mini_player")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Slim Progress Bar at top edge of mini player
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = LocaliiiyAccentMint,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // 1. Mini 16:9 Video Frame
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .clickable { onExpandToFullPlayer() }
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Small expand overlay icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // 2. Video Title & Creator info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onExpandToFullPlayer() }
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = video.creatorFullName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 3. Control Buttons: Mute/Unmute, Play/Pause, Expand, Close
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Mute / Unmute Button
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_toggle_mute")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) MaterialTheme.colorScheme.error else LocaliiiyAccentMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play / Pause Button
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_toggle_play_pause")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Fullscreen / Expand Button
                    IconButton(
                        onClick = onExpandToFullPlayer,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_expand")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand Full Player",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Close / Dismiss Mini Player Button
                    IconButton(
                        onClick = onCloseMiniPlayer,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Mini Player",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
