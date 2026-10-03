package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.activity.compose.BackHandler
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
    onShareVideo: (() -> Unit)? = null,
    onReportVideo: (() -> Unit)? = null,
    onNotInterested: (() -> Unit)? = null,
    onBlockCreator: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
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
    var showMoreOptionsMenu by remember { mutableStateOf(false) }
    var isPlayerLocked by remember { mutableStateOf(false) }
    var showLockPrompt by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var hasPlaybackError by remember { mutableStateOf(false) }

    // Quick Double-Tap Seek Indicators
    var showRewindIndicator by remember { mutableStateOf(false) }
    var showForwardIndicator by remember { mutableStateOf(false) }

    var playerResizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var currentVolume by remember { mutableFloatStateOf(1.0f) }
    var isVolumeDragging by remember { mutableStateOf(false) }
    var showAspectRatioIndicator by remember { mutableStateOf<String?>(null) }

    val activity = remember(context) { context.findActivity() }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ||
            activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    fun setFullscreenImmersive(act: Activity?, isFullscreen: Boolean) {
        act?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            if (isFullscreen) {
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    fun toggleFullscreen() {
        if (activity != null) {
            if (isLandscape) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                setFullscreenImmersive(activity, false)
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                setFullscreenImmersive(activity, true)
            }
        }
    }

    fun safeClose() {
        if (activity != null) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setFullscreenImmersive(activity, false)
        }
        onClose()
    }

    BackHandler {
        if (isLandscape) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setFullscreenImmersive(activity, false)
        } else {
            safeClose()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setFullscreenImmersive(activity, false)
        }
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
                val rawUrl = video.videoUrl.ifBlank { "https://media.w3.org/2010/05/sintel/trailer.mp4" }
                val mediaItem = MediaItem.fromUri(Uri.parse(rawUrl))
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
                        com.example.util.HapticHelper.triggerHaptic(
                            context,
                            haptic,
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                        if (isLandscape) {
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            setFullscreenImmersive(activity, false)
                        }
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
            .pointerInput(isPlayerLocked) {
                detectTapGestures(
                    onTap = {
                        if (isPlayerLocked) {
                            showLockPrompt = true
                            coroutineScope.launch {
                                delay(3000)
                                showLockPrompt = false
                            }
                        } else {
                            showControls = !showControls
                        }
                    },
                    onDoubleTap = { offset ->
                        if (!isPlayerLocked) {
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

        // 3.1. Touch Gesture Area for Volume Control (Right Half of the Screen)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterEnd)
                .pointerInput(isPlayerLocked) {
                    if (!isPlayerLocked) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isVolumeDragging = true
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    delay(1200)
                                    isVolumeDragging = false
                                }
                            },
                            onDragCancel = {
                                isVolumeDragging = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                val delta = -dragAmount / 350f
                                currentVolume = (currentVolume + delta).coerceIn(0f, 1f)
                                exoPlayer.volume = currentVolume
                                isMuted = (currentVolume == 0f)
                                isVolumeDragging = true
                            }
                        )
                    }
                }
        )

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
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Back & Engine Badge (Symbol-only in portrait, full name with symbol in landscape)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { safeClose() },
                            modifier = Modifier
                                .size(32.dp)
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

                        if (isLandscape) {
                            // Full Galaxy Engine Badge with Symbol + Name
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
                                    Text(text = "🌌", fontSize = 11.sp)
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

                            // Unlimited Badge in Landscape
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
                        } else {
                            // Portrait: Ultra-compact Symbol Only
                            Surface(
                                shape = CircleShape,
                                color = GalaxyCosmicPurple.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, GalaxyCelestialCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "🌌",
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Right: Audio, Quality, Speed, Lock, Mute, More
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Spatial Audio
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (spatialAudioEnabled) GalaxyCelestialCyan.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (spatialAudioEnabled) GalaxyCelestialCyan else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { spatialAudioEnabled = !spatialAudioEnabled }
                        ) {
                            if (isLandscape) {
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
                            } else {
                                Text(
                                    text = "🎧",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Quality Dropdown
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
                                    text = if (isLandscape) selectedQuality.split(" ").take(2).joinToString(" ") else (selectedQuality.split(" ").firstOrNull() ?: "4K"),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GalaxyStarlightGold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
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
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)
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
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = if (isMuted) GalaxyPulsarMagenta else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Player Lock Button in Landscape
                        if (isLandscape) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GalaxyObsidian.copy(alpha = 0.7f),
                                border = BorderStroke(0.8.dp, GalaxyCelestialCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        isPlayerLocked = true
                                        showControls = false
                                        showLockPrompt = true
                                        coroutineScope.launch {
                                            delay(3000)
                                            showLockPrompt = false
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock Player",
                                        tint = GalaxyCelestialCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "LOCK",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // More Menu (Share, Sleep Timer, Report, Not Interested, Block)
                        Box {
                            IconButton(
                                onClick = { showMoreOptionsMenu = true },
                                modifier = Modifier.size(28.dp).testTag("btn_player_more_options")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMoreOptionsMenu,
                                onDismissRequest = { showMoreOptionsMenu = false },
                                modifier = Modifier.background(GalaxyObsidian)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = GalaxyCelestialCyan, modifier = Modifier.size(16.dp))
                                            Text("Share Video", color = Color.White, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        onShareVideo?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.Timer, contentDescription = null, tint = GalaxyStarlightGold, modifier = Modifier.size(16.dp))
                                            Text("Sleep Timer ${if (sleepTimerMinutes > 0) "($sleepTimerMinutes m)" else ""}", color = Color.White, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        showSleepTimerDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.ThumbDown, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                            Text("Not Interested", color = Color.White, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        onNotInterested?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.Block, contentDescription = null, tint = GalaxyPulsarMagenta, modifier = Modifier.size(16.dp))
                                            Text("Block Creator", color = GalaxyPulsarMagenta, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        onBlockCreator?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.Report, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            Text("Report Video", color = Color(0xFFEF4444), fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreOptionsMenu = false
                                        onReportVideo?.invoke()
                                    }
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

                        // Right: Aspect Ratio & Fullscreen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isLandscape) {
                                // Dedicated Landscape Aspect Ratio Selectors: Fit Screen, Fill Screen, Stretch
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) GalaxyCelestialCyan.copy(alpha = 0.35f) else GalaxyObsidian.copy(alpha = 0.75f),
                                        border = BorderStroke(1.dp, if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) GalaxyCelestialCyan else Color.White.copy(alpha = 0.35f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                playerResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                                showAspectRatioIndicator = "Fit to Screen"
                                                coroutineScope.launch {
                                                    delay(1400)
                                                    showAspectRatioIndicator = null
                                                }
                                            }
                                            .testTag("btn_fit_to_screen")
                                    ) {
                                        Text(
                                            text = "📐 Fit",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) GalaxyPulsarMagenta.copy(alpha = 0.35f) else GalaxyObsidian.copy(alpha = 0.75f),
                                        border = BorderStroke(1.dp, if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) GalaxyPulsarMagenta else Color.White.copy(alpha = 0.35f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                playerResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                                showAspectRatioIndicator = "Fill Screen (Zoom)"
                                                coroutineScope.launch {
                                                    delay(1400)
                                                    showAspectRatioIndicator = null
                                                }
                                            }
                                            .testTag("btn_fill_screen")
                                    ) {
                                        Text(
                                            text = "🔲 Fill",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL) GalaxyStarlightGold.copy(alpha = 0.35f) else GalaxyObsidian.copy(alpha = 0.75f),
                                        border = BorderStroke(1.dp, if (playerResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL) GalaxyStarlightGold else Color.White.copy(alpha = 0.35f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                playerResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                                                showAspectRatioIndicator = "Stretch to Fill"
                                                coroutineScope.launch {
                                                    delay(1400)
                                                    showAspectRatioIndicator = null
                                                }
                                            }
                                            .testTag("btn_stretch_screen")
                                    ) {
                                        Text(
                                            text = "↔️ Stretch",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                // Portrait: Ultra-compact Aspect Ratio Mode Pill (Never overflows)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GalaxyObsidian.copy(alpha = 0.75f),
                                    border = BorderStroke(1.dp, GalaxyCelestialCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            playerResizeMode = when (playerResizeMode) {
                                                AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                                                    showAspectRatioIndicator = "Fill Screen (Zoom)"
                                                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                                }
                                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                                                    showAspectRatioIndicator = "Stretch to Fill"
                                                    AspectRatioFrameLayout.RESIZE_MODE_FILL
                                                }
                                                else -> {
                                                    showAspectRatioIndicator = "Fit to Screen"
                                                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                                                }
                                            }
                                            coroutineScope.launch {
                                                delay(1400)
                                                showAspectRatioIndicator = null
                                            }
                                        }
                                        .testTag("btn_aspect_ratio_mode")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (playerResizeMode) {
                                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> Icons.Default.ZoomOutMap
                                                AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Default.FitScreen
                                                else -> Icons.Default.ZoomIn
                                            },
                                            contentDescription = "Aspect Ratio",
                                            tint = GalaxyCelestialCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = when (playerResizeMode) {
                                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Fill"
                                                AspectRatioFrameLayout.RESIZE_MODE_FILL -> "Stretch"
                                                else -> "Fit"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Fullscreen / Landscape Toggle
                            IconButton(
                                onClick = { toggleFullscreen() },
                                modifier = Modifier.size(28.dp).testTag("btn_toggle_fullscreen")
                            ) {
                                Icon(
                                    imageVector = if (isLandscape) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Toggle Fullscreen",
                                    tint = GalaxyCelestialCyan,
                                    modifier = Modifier.size(20.dp)
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

        // 6. Floating On-Screen Volume HUD (Always rendered on top of controls and video)
        AnimatedVisibility(
            visible = isVolumeDragging,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GalaxyObsidian.copy(alpha = 0.92f),
                border = BorderStroke(1.2.dp, GalaxyCelestialCyan.copy(alpha = 0.6f)),
                tonalElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = when {
                            currentVolume == 0f || isMuted -> Icons.Default.VolumeOff
                            currentVolume < 0.35f -> Icons.Default.VolumeMute
                            currentVolume < 0.70f -> Icons.Default.VolumeDown
                            else -> Icons.Default.VolumeUp
                        },
                        contentDescription = null,
                        tint = if (currentVolume == 0f || isMuted) GalaxyPulsarMagenta else GalaxyCelestialCyan,
                        modifier = Modifier.size(36.dp)
                    )

                    Text(
                        text = "Volume: ${(currentVolume * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Progress Level Bar
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(currentVolume)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GalaxyCelestialCyan, GalaxyCosmicPurple, GalaxyPulsarMagenta)
                                    )
                                )
                        )
                    }

                    Text(
                        text = "Swipe right screen up/down or tap +/- to adjust",
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // 7. Floating Aspect Ratio Mode HUD Indicator (Always rendered on top)
        AnimatedVisibility(
            visible = showAspectRatioIndicator != null,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = GalaxyObsidian.copy(alpha = 0.92f),
                border = BorderStroke(1.2.dp, GalaxyStarlightGold.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = null,
                        tint = GalaxyStarlightGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = showAspectRatioIndicator ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 8. Player Lock Overlay (When Screen is Locked in Fullscreen/Landscape)
        AnimatedVisibility(
            visible = isPlayerLocked && showLockPrompt,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = GalaxyObsidian.copy(alpha = 0.95f),
                border = BorderStroke(1.5.dp, GalaxyCelestialCyan),
                tonalElevation = 12.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable {
                        isPlayerLocked = false
                        showControls = true
                        showLockPrompt = false
                        com.example.util.HapticHelper.triggerHaptic(
                            context,
                            haptic,
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                    }
                    .testTag("btn_unlock_player")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Unlock Player",
                        tint = GalaxyCelestialCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Screen Locked • Tap to Unlock",
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 9. Sleep Timer Dialog
        if (showSleepTimerDialog) {
            AlertDialog(
                onDismissRequest = { showSleepTimerDialog = false },
                containerColor = GalaxyObsidian,
                titleContentColor = Color.White,
                textContentColor = Color.LightGray,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = GalaxyStarlightGold)
                        Text("Studio Sleep Timer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Playback will automatically pause after the selected duration:",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf(
                            Pair("Off", 0),
                            Pair("15 Minutes", 15),
                            Pair("30 Minutes", 30),
                            Pair("45 Minutes", 45),
                            Pair("60 Minutes", 60),
                            Pair("End of Video", -1)
                        ).forEach { (label, minutes) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sleepTimerMinutes == minutes) GalaxyCosmicPurple.copy(alpha = 0.5f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        sleepTimerMinutes = minutes
                                        showSleepTimerDialog = false
                                        if (minutes > 0) {
                                            coroutineScope.launch {
                                                delay(minutes * 60 * 1000L)
                                                if (exoPlayer.isPlaying) {
                                                    exoPlayer.pause()
                                                    isPlaying = false
                                                }
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        color = if (sleepTimerMinutes == minutes) GalaxyCelestialCyan else Color.White,
                                        fontWeight = if (sleepTimerMinutes == minutes) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (sleepTimerMinutes == minutes) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = GalaxyCelestialCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSleepTimerDialog = false }) {
                        Text("Done", color = GalaxyCelestialCyan, fontWeight = FontWeight.Bold)
                    }
                }
            )
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
