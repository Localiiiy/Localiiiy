package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.NotificationEntity
import com.example.service.FavoriteProximityManager
import com.example.service.FavoriteType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var followedUsers by remember { mutableStateOf(setOf<String>()) }

    val isProximityEnabled by FavoriteProximityManager.isServiceEnabled.collectAsState()
    val proximityRadiusKm by FavoriteProximityManager.proximityRadiusKm.collectAsState()
    val favoriteTargets by FavoriteProximityManager.favoriteTargets.collectAsState()
    var isProximityConfigExpanded by remember { mutableStateOf(true) }
    var testAlertFeedback by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("notifications_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .navigationBarsPadding()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Notifications & Proximity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.5.dp
            )

            // -------------------------------------------------------------
            // Favorite Proximity Alerts Background Service Toggle Panel
            // -------------------------------------------------------------
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isProximityEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(
                    1.dp,
                    if (isProximityEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("favorite_proximity_toggle_panel")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Master Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isProximityEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isProximityEnabled) Icons.Default.NearMe else Icons.Outlined.NearMeDisabled,
                                    contentDescription = null,
                                    tint = if (isProximityEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Favorite Proximity Push",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isProximityEnabled) Color(0xFF00C896).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${String.format("%.0f", proximityRadiusKm)}KM",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isProximityEnabled) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isProximityEnabled) "Background service active • triggers push alert" else "Service paused • toggle on to monitor radius",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isProximityEnabled,
                            onCheckedChange = { enabled ->
                                FavoriteProximityManager.setServiceEnabled(context, enabled)
                            },
                            modifier = Modifier.testTag("proximity_alerts_master_toggle")
                        )
                    }

                    // Expandable Details (Radius Selector, Quick Test, Favorite List)
                    AnimatedVisibility(visible = isProximityEnabled) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            // Radius Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Alert Radius:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(1.0, 3.0, 5.0, 10.0).forEach { radius ->
                                        val isSelected = proximityRadiusKm == radius
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            border = BorderStroke(
                                                0.8.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    FavoriteProximityManager.setProximityRadiusKm(context, radius)
                                                }
                                                .testTag("radius_chip_${radius.toInt()}km")
                                        ) {
                                            Text(
                                                text = "${radius.toInt()} KM${if (radius == 5.0) " ★" else ""}",
                                                fontSize = 10.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Test Trigger Button
                            Button(
                                onClick = {
                                    FavoriteProximityManager.triggerSimulatedAlert(context)
                                    testAlertFeedback = "Push alert fired for 5KM radius! Check drawer."
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .testTag("trigger_test_proximity_push_alert")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test 5KM Push Alert (Send Notification Now)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (testAlertFeedback != null) {
                                Text(
                                    text = testAlertFeedback!!,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF00875A),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Favorite Targets Quick List
                            Text(
                                text = "Favorite Targets (${favoriteTargets.count { it.isFavorite }} active):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(favoriteTargets, key = { it.id }) { target ->
                                    val isFav = target.isFavorite
                                    val isCreator = target.type == FavoriteType.CREATOR

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isFav) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isFav) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                FavoriteProximityManager.toggleFavorite(context, target.id)
                                            }
                                            .testTag("fav_target_chip_${target.id}")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(target.avatar)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = target.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(CircleShape)
                                            )

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                    Text(
                                                        text = target.name,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = if (isCreator) "🎬" else "🛍️",
                                                        fontSize = 9.sp
                                                    )
                                                }
                                                Text(
                                                    text = "${target.lastDistanceKm}km away",
                                                    fontSize = 9.5.sp,
                                                    color = if (target.lastDistanceKm <= proximityRadiusKm) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Icon(
                                                imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "Toggle favorite alert",
                                                tint = if (isFav) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notifications List
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications, key = { it.id }) { notification ->
                        val isFollowing = followedUsers.contains(notification.username) || notification.isFollowingBack
                        val isProximityAlert = notification.actionType == "PROXIMITY_ALERT"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isProximityAlert) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent)
                                .padding(if (isProximityAlert) 6.dp else 0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(notification.userAvatar)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = notification.username,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                val notifText = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        append("${notification.username} ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        append(notification.content)
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        append("  ${formatRelativeTime(notification.timestamp)}")
                                    }
                                }

                                Column {
                                    if (isProximityAlert) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF00C896).copy(alpha = 0.2f),
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        ) {
                                            Text(
                                                text = "📍 5KM PROXIMITY ALERT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00875A),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = notifText,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            if (notification.actionType == "FOLLOW" || notification.actionType == "CONNECT") {
                                Button(
                                    onClick = {
                                        followedUsers = if (isFollowing) {
                                            followedUsers - notification.username
                                        } else {
                                            followedUsers + notification.username
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (isFollowing) "Connected" else "Connect",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                    )
                                }
                            } else if (!notification.mediaPreviewUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(notification.mediaPreviewUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Post preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

