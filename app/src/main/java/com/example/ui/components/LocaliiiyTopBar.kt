package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyStringKey

@Composable
fun LocaliiiyTopBar(
    hasUnreadNotifications: Boolean,
    hasUnreadMessages: Boolean,
    currentLocationLabel: String? = null,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    searchQuery: String = "",
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    onSearchQueryChange: (String) -> Unit = {},
    onLogoClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onLanguageCurrencyClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit,
    onDirectMessagesClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = if (isSearchExpanded || searchQuery.isNotBlank()) 6.dp else 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .zIndex(100f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Proximity indicator
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LocaliiiyAppLogoBadge(size = 28.dp)

                        Text(
                            text = "Localiiiy",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable(onClick = onLogoClick)
                                .testTag("app_logo_title")
                        )
                    }

                    if (isLocationEnabled && !currentLocationLabel.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = currentLocationLabel,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Fixed Action Icons: Fully visible without scrolling; message button guaranteed accessible!
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Search Toggle Button
                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .testTag("top_bar_search_toggle")
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchExpanded || searchQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Atmospheric Space Themes Switcher
                    IconButton(
                        onClick = onThemeClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .testTag("top_bar_theme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Space & Atmospheric Themes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Broadcast / Add Pulse Button
                    IconButton(
                        onClick = onCreateClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("top_bar_create_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Post",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Notifications
                    Box {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .testTag("top_bar_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                        }
                    }

                    // Direct Community Chat (Message Button - ALWAYS VISIBLE & ACCESSIBLE)
                    Box {
                        IconButton(
                            onClick = onDirectMessagesClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasUnreadMessages) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .testTag("top_bar_messages_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Direct Community Chat",
                                tint = if (hasUnreadMessages) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (hasUnreadMessages) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            // High Z-Index Elevated Search Bar positioned cleanly above other feed elements
            AnimatedVisibility(
                visible = isSearchExpanded || searchQuery.isNotBlank(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp).zIndex(20f)) {
                    LocaliiiySearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholderText = LocalizationHelper.getString(LocaliiiyStringKey.SEARCH_HINT, currentLanguage),
                        testTag = "global_search_bar"
                    )
                }
            }
        }
    }
}
