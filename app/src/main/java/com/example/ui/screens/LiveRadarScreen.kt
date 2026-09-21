package com.example.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.LiveRadarComponent
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Main Screen View displaying the 'Live Radar' circular proximity interface
 * with interactive proximity rings and blips, accompanied by Accompanist Permissions runtime location handling.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveRadarScreen(
    posts: List<PostEntity>,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity> = emptyList(),
    selectedRadiusKm: Double? = 3.0,
    onRadiusFilterChange: (Double) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    onLikePost: (PostEntity) -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Accompanist Permissions boilerplate for runtime location permissions
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var exploreBypassAllowed by remember { mutableStateOf(false) }
    var currentRadius by remember { mutableStateOf(selectedRadiusKm ?: 3.0) }
    var isGhostModeActive by remember { mutableStateOf(false) }
    var isAppInForeground by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isAppInForeground = true
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        isAppInForeground = false
    }

    val hasLocationPermission = locationPermissionsState.allPermissionsGranted || exploreBypassAllowed
    val shouldShowBlips = !isGhostModeActive && isAppInForeground

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF010803))
            .testTag("live_radar_screen")
    ) {
        if (hasLocationPermission) {
            // Main Live Proximity Radar view
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    LiveRadarComponent(
                        userProfile = userProfile,
                        nearbyUsers = if (shouldShowBlips) nearbyUsers else emptyList(),
                        nearbyPosts = posts,
                        selectedRadiusKm = currentRadius,
                        isLocationEnabled = true,
                        isPrivateAccount = isGhostModeActive,
                        hidePreciseLocationOnRadar = isGhostModeActive,
                        onToggleHidePreciseLocation = { isGhostModeActive = it },
                        onRadiusChange = { radius ->
                            currentRadius = radius
                            onRadiusFilterChange(radius)
                        },
                        onUserClick = { user -> onUserProfileClick(user.username) },
                        onPostClick = onPostClick,
                        onWaveAtUser = { /* waved */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Accompanist Location Permission Request Rationale View
            LocationPermissionRationaleView(
                shouldShowRationale = locationPermissionsState.shouldShowRationale,
                onRequestPermission = {
                    locationPermissionsState.launchMultiplePermissionRequest()
                },
                onExploreWithoutPermission = {
                    exploreBypassAllowed = true
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
        }
    }
}

/**
 * Accompanist Permissions Rationale Card guiding users to grant location access for the Live Radar map.
 */
@Composable
private fun LocationPermissionRationaleView(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onExploreWithoutPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .testTag("location_permission_rationale_card")
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Radar Icon Graphic with Glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(LocaliiiyPrimaryTeal, LocaliiiyDeepNavy)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Radar,
                        contentDescription = "Radar Icon",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Enable Live Radar Map",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (shouldShowRationale) {
                        "Localiiiy requires location access to scan nearby community moments, street pins, and creators within 1 to 10 kilometers of your current position."
                    } else {
                        "Discover what's happening around your block right now. Grant location permission to activate the Google Maps Live Radar."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Privacy Commitment Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = "Privacy Shield",
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Fuzzy proximity enabled. Your exact home coordinates are never publicly broadcast.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Request Button
                Button(
                    onClick = onRequestPermission,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("grant_location_permission_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Grant Location Permission",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fallback Preview Button
                OutlinedButton(
                    onClick = onExploreWithoutPermission,
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("preview_map_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Preview Downtown Radar",
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}
