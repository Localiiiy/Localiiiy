package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliAccentMint

@Composable
fun LocaliTopBar(
    hasUnreadNotifications: Boolean,
    hasUnreadMessages: Boolean,
    currentLocationLabel: String? = null,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    onLogoClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onNotificationsClick: () -> Unit,
    onDirectMessagesClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Title & Proximity indicator
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Localiiiy",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(onClick = onLogoClick)
                            .testTag("app_logo_title")
                    )

                    // Active Radar Pulse Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isLocationEnabled && !isPrivateAccount) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable(onClick = onLocationClick)
                            .testTag("top_bar_radar_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (!isLocationEnabled) MaterialTheme.colorScheme.error
                                        else if (isPrivateAccount) Color(0xFFFFB703)
                                        else LocaliAccentMint
                                    )
                            )
                            Text(
                                text = if (!isLocationEnabled) "RADAR OFF" else if (isPrivateAccount) "GHOST" else "RADAR ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocationEnabled && !isPrivateAccount) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                if (isLocationEnabled && !currentLocationLabel.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable(onClick = onLocationClick)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = currentLocationLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                } else if (!isLocationEnabled) {
                    Text(
                        text = "Location Disabled • Tap to Enable",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable(onClick = onLocationClick)
                    )
                }
            }

            // Right Action Icons (Create Pulse, Notifications, Direct Chats)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Broadcast / Add Pulse Button
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(onClick = onCreateClick)
                        .testTag("top_bar_create_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Broadcast Pulse",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Pulse",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Notifications
                Box {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .testTag("top_bar_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                        )
                    }
                }

                // Direct Community Chat
                Box {
                    IconButton(
                        onClick = onDirectMessagesClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .testTag("top_bar_messages_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Chat,
                            contentDescription = "Direct Community Chats",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (hasUnreadMessages) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}

// Alias for compatibility
@Composable
fun InstagramTopBar(
    hasUnreadNotifications: Boolean,
    hasUnreadMessages: Boolean,
    currentLocationLabel: String? = null,
    onLocationClick: () -> Unit = {},
    onNotificationsClick: () -> Unit,
    onDirectMessagesClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) = LocaliTopBar(
    hasUnreadNotifications = hasUnreadNotifications,
    hasUnreadMessages = hasUnreadMessages,
    currentLocationLabel = currentLocationLabel,
    onLocationClick = onLocationClick,
    onNotificationsClick = onNotificationsClick,
    onDirectMessagesClick = onDirectMessagesClick,
    onCreateClick = onCreateClick,
    modifier = modifier
)

