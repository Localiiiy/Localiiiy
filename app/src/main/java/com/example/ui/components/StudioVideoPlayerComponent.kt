package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.StudioVideoEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Galaxy Design Tokens
private val GalaxyObsidian = Color(0xFF040612)
private val GalaxyCosmicPurple = Color(0xFF7928CA)
private val GalaxyPulsarMagenta = Color(0xFFFF0080)
private val GalaxyCelestialCyan = Color(0xFF00DFD8)
private val GalaxyStarlightGold = Color(0xFFFFBE0B)
private val GalaxyElectricMint = Color(0xFF10B981)

/**
 * Galaxy Media7 Realistic ExoPlayer for Localiiiy Studio.
 * Supports unlimited long-form video playback (60s up to unlimited hours)
 * with cosmic nebula visualizers, spatial 360 audio HUD, and cinematic controls.
 */
@OptIn(UnstableApi::class)
@Composable
fun StudioVideoPlayerComponent(
    video: StudioVideoEntity,
    onClose: () -> Unit,
    onVideoCompleted: () -> Unit = {},
    onMinimizeToMiniPlayer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember {
        mutableLongStateOf((video.durationSeconds.toLong().coerceAtLeast(60L)) * 1000L)
    }
    var isBuffering by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var spatialAudioEnabled by remember { mutableStateOf(true) }
    var selectedQuality by remember { mutableStateOf(video.resolution.ifBlank { "4K UHD 60fps" }) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var hasPlaybackError by remember { mutableStateOf(false) }

    // Quick Double-Tap Seek Indicators
    var showRewindIndicator by remember { mutableStateOf(false) }
    var showForwardIndicator by remember { mutableStateOf(false) }

    var playerResizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    val activity = remember(context) { context.findActivity() }
    var isLandscape by remember {
        mutableStateOf(activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    // Infinite cosmic animations
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy_nebula")
    val nebulaAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "nebulaAngle"
    )

    val starlightPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starlightPulse"
    )

    // Initialize Media7 ExoPlayer Engine with decoder fallback for system resource resilience
    val exoPlayer = remember(video.id) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(video.videoUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            } catch (e: Exception) {
                hasPlaybackError = true
            }
        }
    }

    // Attach ExoPlayer Event Listener
    DisposableEffect(exoPlayer) {
        var fallbackAttempted = false
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> isBuffering = true
                    Player.STATE_READY -> {
                        isBuffering = false
                        if (exoPlayer.duration > 0) {
                            durationMs = exoPlayer.duration
                        }
                    }
                    Player.STATE_ENDED -> {
                        isPlaying = false
                        onVideoCompleted()
                    }
                    Player.STATE_IDLE -> {
                        isBuffering = false
                    }
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
                        val fallbackItem = MediaItem.fromUri(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"))
                        exoPlayer.setMediaItem(fallbackItem)
                        exoPlayer.prepare()
                        exoPlayer.play()
                    } catch (e: Exception) {
                        hasPlaybackError = true
                        try { exoPlayer.stop() } catch (_: Exception) {}
                    }
                } else {
                    hasPlaybackError = true
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

    // Lifecycle observer to handle app foreground / background
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (isPlaying && !hasPlaybackError) exoPlayer.play()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Ticking progress update loop
    LaunchedEffect(isPlaying, hasPlaybackError) {
        while (true) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                if (exoPlayer.duration > 0) {
                    durationMs = exoPlayer.duration
                }
            }
            delay(500)
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4500)
            showControls = false
        }
    }

    val progressFraction = remember(currentPositionMs, durationMs) {
        if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }

    Box(
        modifier = modifier
            .background(GalaxyObsidian)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                    },
                    onDoubleTap = { offset ->
                        val isLeftHalf = offset.x < size.width / 2
                        if (isLeftHalf) {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                            showRewindIndicator = true
                            coroutineScope.launch {
                                delay(600)
                                showRewindIndicator = false
                            }
                        } else {
                            val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                            showForwardIndicator = true
                            coroutineScope.launch {
                                delay(600)
                                showForwardIndicator = false
                            }
                        }
                    }
                )
            }
            .testTag("studio_video_player_component")
    ) {
        // 1. Cosmic Ambient Glow Layer behind player
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val glowRadius = size.maxDimension * 0.7f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GalaxyCosmicPurple.copy(alpha = 0.18f * starlightPulse),
                        GalaxyCelestialCyan.copy(alpha = 0.10f * starlightPulse),
                        Color.Transparent
                    ),
                    center = center,
                    radius = glowRadius
                )
            )
        }

        // 2. AndroidView wrapping Media3 PlayerView or Fallback
        if (!hasPlaybackError) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false // Custom Galaxy HUD Jetpack Compose overlay
                        this.resizeMode = playerResizeMode
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = playerResizeMode
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Realistic Fallback Artwork with Cosmic Backdrop
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Cosmic overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    GalaxyObsidian.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    GalaxyObsidian.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
            }
        }

        // 3. Double-tap feedback ripples (Rewind / Forward)
        if (showRewindIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.45f)
                    .align(Alignment.CenterStart)
                    .background(
                        Brush.horizontalGradient(
                            listOf(GalaxyCelestialCyan.copy(alpha = 0.35f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "-10s",
                        tint = GalaxyCelestialCyan,
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        text = "-10s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GalaxyCelestialCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        if (showForwardIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.45f)
                    .align(Alignment.CenterEnd)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, GalaxyPulsarMagenta.copy(alpha = 0.35f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "+10s",
                        tint = GalaxyPulsarMagenta,
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        text = "+10s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GalaxyPulsarMagenta,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 4. Galaxy Starlight Buffering Spinner
        if (isBuffering && !hasPlaybackError) {
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                // Outer nebula ring
                Canvas(modifier = Modifier.size(68.dp)) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                GalaxyCelestialCyan,
                                GalaxyCosmicPurple,
                                GalaxyPulsarMagenta,
                                GalaxyCelestialCyan
                            )
                        ),
                        radius = size.minDimension / 2f * 0.9f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }
                CircularProgressIndicator(
                    color = GalaxyCelestialCyan,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(46.dp)
                )
                Text(
                    text = "🌌",
                    fontSize = 14.sp
                )
            }
        }

        // 5. Galaxy Media7 HUD Overlay (Full Glassmorphic Controls)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                // --- TOP GALAXY ACTION BAR ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Back & Galaxy Engine Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GalaxyObsidian.copy(alpha = 0.6f))
                                .testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Player",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Galaxy Engine Badge
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = GalaxyCosmicPurple.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, GalaxyCelestialCyan.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "🌌", fontSize = 10.sp)
                                Text(
                                    text = "MEDIA7 GALAXY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GalaxyCelestialCyan,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Unlimited Time Tag
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = GalaxyPulsarMagenta.copy(alpha = 0.25f),
                            border = BorderStroke(0.8.dp, GalaxyPulsarMagenta.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "∞ UNLIMITED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Right: Spatial Audio, Quality, Speed, Aspect Ratio, PiP
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Spatial 360° Audio Toggle
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (spatialAudioEnabled) GalaxyCelestialCyan.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (spatialAudioEnabled) GalaxyCelestialCyan else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { spatialAudioEnabled = !spatialAudioEnabled }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(text = "🎧", fontSize = 10.sp)
                                Text(
                                    text = if (spatialAudioEnabled) "360° ON" else "STEREO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (spatialAudioEnabled) GalaxyCelestialCyan else Color.LightGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Quality Dropdown Chip
                        Box {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GalaxyObsidian.copy(alpha = 0.7f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showQualityMenu = true }
                            ) {
                                Text(
                                    text = selectedQuality.split(" ").firstOrNull() ?: "4K",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GalaxyStarlightGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showQualityMenu,
                                onDismissRequest = { showQualityMenu = false },
                                modifier = Modifier.background(GalaxyObsidian)
                            ) {
                                listOf("8K Galaxy Cinema", "4K UHD 60fps", "1440p QHD", "1080p 60fps", "720p HD", "Auto HDR").forEach { q ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                q,
                                                fontSize = 12.sp,
                                                color = if (q == selectedQuality) GalaxyCelestialCyan else Color.White
                                            )
                                        },
                                        onClick = {
                                            selectedQuality = q
                                            showQualityMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Playback Speed Selector
                        Box {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GalaxyObsidian.copy(alpha = 0.7f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showSpeedMenu = true }
                            ) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showSpeedMenu,
                                onDismissRequest = { showSpeedMenu = false },
                                modifier = Modifier.background(GalaxyObsidian)
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${speed}x",
                                                fontSize = 12.sp,
                                                color = if (speed == playbackSpeed) GalaxyCelestialCyan else Color.White
                                            )
                                        },
                                        onClick = {
                                            playbackSpeed = speed
                                            exoPlayer.playbackParameters = PlaybackParameters(speed)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Mute/Unmute
                        IconButton(
                            onClick = {
                                isMuted = !isMuted
                                exoPlayer.volume = if (isMuted) 0f else 1f
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = if (isMuted) GalaxyPulsarMagenta else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // MiniPlayer
                        if (onMinimizeToMiniPlayer != null) {
                            IconButton(
                                onClick = onMinimizeToMiniPlayer,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureInPictureAlt,
                                    contentDescription = "Mini Player",
                                    tint = GalaxyCelestialCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // --- CENTER GALAXY ORBITAL PLAYBACK CONTROLS ---
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Orbital Rewind 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition - 10000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GalaxyObsidian.copy(alpha = 0.65f))
                            .border(1.dp, GalaxyCelestialCyan.copy(alpha = 0.5f), CircleShape)
                            .testTag("player_rewind_10s")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10 seconds",
                            tint = GalaxyCelestialCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Pulsing Radiant Core Play / Pause Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying = false
                                } else {
                                    if (hasPlaybackError) {
                                        hasPlaybackError = false
                                        exoPlayer.prepare()
                                    }
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            }
                            .testTag("player_play_pause_button")
                    ) {
                        // Sweep gradient halo
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        GalaxyCelestialCyan,
                                        GalaxyCosmicPurple,
                                        GalaxyPulsarMagenta,
                                        GalaxyCelestialCyan
                                    )
                                )
                            )
                        }
                        // Core center
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(GalaxyObsidian),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Orbital Forward 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10000L).coerceAtMost(durationMs)
                            exoPlayer.seekTo(newPos)
                            currentPositionMs = newPos
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GalaxyObsidian.copy(alpha = 0.65f))
                            .border(1.dp, GalaxyPulsarMagenta.copy(alpha = 0.5f), CircleShape)
                            .testTag("player_forward_10s")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10 seconds",
                            tint = GalaxyPulsarMagenta,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // --- BOTTOM GALAXY TIMELINE & SCRUBBER ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Time Stamps, Sound Visualizer & Aspect Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time stamp display supporting unlimited duration
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = formatMsToTimestamp(currentPositionMs),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GalaxyCelestialCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "/",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = formatMsToTimestamp(durationMs),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f),
                                fontFamily = FontFamily.Monospace
                            )

                            // Spatial Audio Equalizer Animation
                            if (spatialAudioEnabled && isPlaying) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.height(12.dp).padding(start = 4.dp)
                                ) {
                                    val bar1 by infiniteTransition.animateFloat(
                                        initialValue = 4f,
                                        targetValue = 12f,
                                        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
                                        label = "b1"
                                    )
                                    val bar2 by infiniteTransition.animateFloat(
                                        initialValue = 10f,
                                        targetValue = 3f,
                                        animationSpec = infiniteRepeatable(tween(280), RepeatMode.Reverse),
                                        label = "b2"
                                    )
                                    val bar3 by infiniteTransition.animateFloat(
                                        initialValue = 2f,
                                        targetValue = 11f,
                                        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
                                        label = "b3"
                                    )
                                    Box(modifier = Modifier.width(2.dp).height(bar1.dp).background(GalaxyCelestialCyan))
                                    Box(modifier = Modifier.width(2.dp).height(bar2.dp).background(GalaxyPulsarMagenta))
                                    Box(modifier = Modifier.width(2.dp).height(bar3.dp).background(GalaxyStarlightGold))
                                }
                            }
                        }

                        // Right: Zoom & Fullscreen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Zoom / Fit Toggle
                            IconButton(
                                onClick = {
                                    playerResizeMode = if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    } else {
                                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) Icons.Default.ZoomIn else Icons.Default.ZoomOutMap,
                                    contentDescription = "Zoom/Fit Aspect Ratio",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Fullscreen Toggle
                            IconButton(
                                onClick = {
                                    if (activity != null) {
                                        if (isLandscape) {
                                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                            isLandscape = false
                                        } else {
                                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                            isLandscape = true
                                        }
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLandscape) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Toggle Fullscreen",
                                    tint = GalaxyCelestialCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Stardust Scrubber Slider with Comet Thumb & Cosmic Gradient Track
                    Slider(
                        value = progressFraction,
                        onValueChange = { fraction ->
                            val targetMs = (durationMs * fraction).toLong()
                            currentPositionMs = targetMs
                            exoPlayer.seekTo(targetMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = GalaxyCelestialCyan,
                            activeTrackColor = GalaxyPulsarMagenta,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .testTag("player_scrubber_slider")
                    )
                }
            }
        }
    }
}

/**
 * Format milliseconds into HH:mm:ss or mm:ss for long-form creator video playback
 * supporting UNLIMITED duration without any 240 minute ceiling (e.g. 5 hours, 24 hours, days).
 */
fun formatMsToTimestamp(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * Safely find the Activity from a Context.
 */
fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}
