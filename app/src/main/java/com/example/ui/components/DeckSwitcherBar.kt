package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainNavigationTab

/**
 * PROMPT 1: UNIFIED SINGLE-ID DECK SWITCHER
 * Proprietary gesture-driven floating deck switcher replacing traditional tab bars.
 * Seamlessly transitions canvas between Pulse, Radar, Market, Clips, Studio, and Profile.
 * Avoids rectangular boxy navigation layouts.
 */
enum class DeckSlot(
    val tab: MainNavigationTab,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val tag: String
) {
    PULSE(MainNavigationTab.FEED, "Pulse", Icons.Default.DynamicFeed, Icons.Outlined.DynamicFeed, "deck_pulse"),
    RADAR(MainNavigationTab.EXPLORE, "Radar", Icons.Default.Radar, Icons.Outlined.Radar, "deck_radar"),
    MARKET(MainNavigationTab.MARKET, "Market", Icons.Default.Storefront, Icons.Outlined.Storefront, "deck_market"),
    CLIPS(MainNavigationTab.CLIPS, "Clips", Icons.Default.PlayCircle, Icons.Outlined.PlayCircle, "deck_clips"),
    STUDIO(MainNavigationTab.STUDIO, "Studio", Icons.Default.VideoLibrary, Icons.Outlined.VideoLibrary, "deck_studio")
}

@Composable
fun DeckSwitcherBar(
    currentTab: MainNavigationTab,
    userAvatarUrl: String,
    onTabSelected: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val deckSlots = remember { DeckSlot.values() }
    val currentSlotIndex = remember(currentTab) {
        deckSlots.indexOfFirst { it.tab == currentTab }
    }

    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .pointerInput(currentTab) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAccumulator = 0f },
                    onDragEnd = {
                        if (dragAccumulator < -40f && currentSlotIndex in 0 until (deckSlots.size - 1)) {
                            onTabSelected(deckSlots[currentSlotIndex + 1].tab)
                        } else if (dragAccumulator > 40f && currentSlotIndex > 0) {
                            onTabSelected(deckSlots[currentSlotIndex - 1].tab)
                        }
                        dragAccumulator = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragAccumulator += dragAmount
                    }
                )
            }
            .testTag("unified_deck_switcher_bar"),
        contentAlignment = Alignment.Center
    ) {
        // Floating Aerodynamic Capsule Shell
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                deckSlots.forEach { slot ->
                    val isSelected = currentTab == slot.tab
                    DeckPill(
                        slot = slot,
                        isSelected = isSelected,
                        onClick = { onTabSelected(slot.tab) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Profile Deck Node
                val isProfileSelected = currentTab == MainNavigationTab.PROFILE
                val profileBorderColor by animateColorAsState(
                    targetValue = if (isProfileSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "profile_deck_border"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(2.dp, profileBorderColor, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(MainNavigationTab.PROFILE) }
                        .testTag("deck_profile_node"),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = userAvatarUrl,
                            contentDescription = "Space Deck",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Space",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckPill(
    slot: DeckSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillBgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pill_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
        label = "pill_content_color"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        label = "pill_elevation"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .padding(horizontal = 2.dp)
            .shadow(elevation, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(pillBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(slot.tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = if (isSelected) slot.activeIcon else slot.inactiveIcon,
                contentDescription = slot.title,
                tint = contentColor,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = slot.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
