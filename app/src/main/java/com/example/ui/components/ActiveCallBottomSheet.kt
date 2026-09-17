package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.util.HapticHelper
import com.example.util.HapticHelper.HapticType
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay

enum class CallStatus {
    CONNECTING,
    RINGING,
    CONNECTED,
    ENDED
}

data class ActiveCallSession(
    val contactUsername: String,
    val contactAvatar: String,
    val isVideoCall: Boolean,
    val callStatus: CallStatus = CallStatus.CONNECTING,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val durationSeconds: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveCallBottomSheet(
    session: ActiveCallSession,
    myAvatarUrl: String = "",
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var callStatus by remember { mutableStateOf(session.callStatus) }
    var isMuted by remember { mutableStateOf(session.isMuted) }
    var isCameraOff by remember { mutableStateOf(session.isCameraOff) }
    var isSpeakerOn by remember { mutableStateOf(session.isSpeakerOn) }
    var isFrontCamera by remember { mutableStateOf(session.isFrontCamera) }
    var durationSeconds by remember { mutableIntStateOf(session.durationSeconds) }

    // Simulate WebRTC connection progression
    LaunchedEffect(Unit) {
        if (callStatus == CallStatus.CONNECTING) {
            delay(1200)
            callStatus = CallStatus.RINGING
            delay(1500)
            callStatus = CallStatus.CONNECTED
            HapticHelper.triggerHaptic(context, haptic, HapticType.SUCCESS)
        }
    }

    // Call duration timer
    LaunchedEffect(callStatus) {
        if (callStatus == CallStatus.CONNECTED) {
            while (true) {
                delay(1000)
                durationSeconds++
            }
        }
    }

    // Format timer
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    // Pulsing animation for audio waves or connecting states
    val infiniteTransition = rememberInfiniteTransition(label = "call_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    ModalBottomSheet(
        onDismissRequest = onEndCall,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF0D1117),
        dragHandle = null,
        modifier = modifier
            .fillMaxSize()
            .testTag("active_call_bottom_sheet")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1117))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (session.isVideoCall) {
                // ==========================================
                // TWO-WAY WEBRTC VIDEO CALL INTERFACE
                // ==========================================
                // Remote Video Stream Container / Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("remote_video_canvas")
                ) {
                    if (callStatus == CallStatus.CONNECTED) {
                        // Remote Video Stream Canvas (High quality live view with subtle atmosphere)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A),
                                            Color(0xFF020617)
                                        )
                                    )
                                )
                        ) {
                            // Video Stream Content (Avatar-based camera stream simulation with ambient glow)
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(session.contactAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Remote video stream",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(0.85f)
                            )

                            // Subtle dark vignette gradient for readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.65f),
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )
                        }
                    } else {
                        // Connecting / Ringing Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF111827)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(session.contactAvatar)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = session.contactUsername,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "@${session.contactUsername}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (callStatus == CallStatus.CONNECTING) "Initializing WebRTC P2P Channel..." else "Ringing...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Local Camera Preview PIP (Picture-In-Picture) in top-right
                    if (callStatus == CallStatus.CONNECTED) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f)),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 20.dp, end = 16.dp)
                                .width(96.dp)
                                .height(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    isFrontCamera = !isFrontCamera
                                    HapticHelper.triggerHaptic(context, haptic, HapticType.SELECTION)
                                }
                                .testTag("local_preview_pip")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (!isCameraOff) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(myAvatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300" })
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "My Camera Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Camera lens indicator
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isFrontCamera) "Front" else "Rear",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VideocamOff,
                                            contentDescription = "Camera Off",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // AUDIO CALL INTERFACE
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 36.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "End-to-End Encrypted Voice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(if (callStatus == CallStatus.CONNECTED) 1f else pulseScale)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(session.contactAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = session.contactUsername,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = session.contactUsername,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when (callStatus) {
                                CallStatus.CONNECTING -> "Connecting..."
                                CallStatus.RINGING -> "Ringing..."
                                CallStatus.CONNECTED -> timeFormatted
                                CallStatus.ENDED -> "Call Ended"
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (callStatus == CallStatus.CONNECTED) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Top Header Overlay (Status, Encryption, Time)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (callStatus == CallStatus.CONNECTED) Color(0xFF00E676) else Color(0xFFFFB300))
                        )
                        Text(
                            text = if (session.isVideoCall) "WebRTC Video • $timeFormatted" else "HD Voice • $timeFormatted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (session.isVideoCall) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SignalCellularAlt,
                                contentDescription = "Signal Strong",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "P2P Live",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BOTTOM CALL ACTION CONTROLS BAR
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Camera Flip (Only for video calls)
                        if (session.isVideoCall) {
                            CallControlButton(
                                icon = Icons.Default.FlipCameraAndroid,
                                label = "Flip",
                                isActive = false,
                                onClick = {
                                    isFrontCamera = !isFrontCamera
                                    HapticHelper.triggerHaptic(context, haptic, HapticType.SELECTION)
                                },
                                testTag = "call_flip_camera_button"
                            )

                            // Camera Toggle (Mute/Unmute video)
                            CallControlButton(
                                icon = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                label = if (isCameraOff) "Off" else "Video",
                                isActive = isCameraOff,
                                activeColor = Color(0xFFE53935),
                                onClick = {
                                    isCameraOff = !isCameraOff
                                    HapticHelper.triggerHaptic(context, haptic, HapticType.SELECTION)
                                },
                                testTag = "call_camera_toggle_button"
                            )
                        }

                        // Microphone Mute Toggle
                        CallControlButton(
                            icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            label = if (isMuted) "Muted" else "Mic",
                            isActive = isMuted,
                            activeColor = Color(0xFFE53935),
                            onClick = {
                                isMuted = !isMuted
                                HapticHelper.triggerHaptic(context, haptic, HapticType.SELECTION)
                            },
                            testTag = "call_mic_mute_button"
                        )

                        // Speakerphone Toggle
                        CallControlButton(
                            icon = if (isSpeakerOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.VolumeDown,
                            label = if (isSpeakerOn) "Speaker" else "Earpiece",
                            isActive = isSpeakerOn,
                            activeColor = MaterialTheme.colorScheme.primary,
                            onClick = {
                                isSpeakerOn = !isSpeakerOn
                                HapticHelper.triggerHaptic(context, haptic, HapticType.SELECTION)
                            },
                            testTag = "call_speaker_toggle_button"
                        )

                        // End Call Button
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                                .clickable {
                                    HapticHelper.triggerHaptic(context, haptic, HapticType.WARNING)
                                    onEndCall()
                                }
                                .testTag("call_end_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Call",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            softWrap = false
        )
    }
}
