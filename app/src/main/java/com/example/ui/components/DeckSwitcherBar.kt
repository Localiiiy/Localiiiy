package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainNavigationTab
import com.example.util.LocalAppLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyLanguage

/**
 * Global CompositionLocal so full-bleed screens (such as ClipsScreen) know whether
 * the bottom task bar is maximized or minimized and can dynamically adjust bottom overlays.
 */
val LocalIsDockMinimized = compositionLocalOf { false }

/**
 * UNIFIED SINGLE-ID DECK SWITCHER
 * Proprietary gesture-driven floating deck switcher with frosted glass blur backdrop
 * and colorful eye-catching signature icons.
 */
enum class DeckSlot(
    val tab: MainNavigationTab,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val accentColor: Color,
    val secondaryGlow: Color,
    val tag: String
) {
    PULSE(
        MainNavigationTab.FEED,
        "Pulse",
        Icons.Default.DynamicFeed,
        Icons.Outlined.DynamicFeed,
        Color(0xFF00E5FF),
        Color(0xFF38BDF8),
        "deck_pulse"
    ),
    RADAR(
        MainNavigationTab.EXPLORE,
        "Radar",
        Icons.Default.Radar,
        Icons.Outlined.Radar,
        Color(0xFF00FF87),
        Color(0xFF10B981),
        "deck_radar"
    ),
    MARKET(
        MainNavigationTab.MARKET,
        "Market",
        Icons.Default.Storefront,
        Icons.Outlined.Storefront,
        Color(0xFFFFB300),
        Color(0xFFFF6D00),
        "deck_market"
    ),
    CLIPS(
        MainNavigationTab.CLIPS,
        "Clips",
        Icons.Default.PlayCircle,
        Icons.Outlined.PlayCircle,
        Color(0xFFFF4081),
        Color(0xFFF43F5E),
        "deck_clips"
    ),
    STUDIO(
        MainNavigationTab.STUDIO,
        "Studio",
        Icons.Default.VideoLibrary,
        Icons.Outlined.VideoLibrary,
        Color(0xFFA855F7),
        Color(0xFF6366F1),
        "deck_studio"
    )
}

/**
 * Theme-Adaptive Visual Styling for the Bottom Task Bar & Task Bar Icons.
 * Dynamically transforms the frosted glass bar, border gradient, and each slot's
 * active/inactive icon vectors & colors to match the selected Atmospheric Theme.
 */
data class DeckSlotThemeStyle(
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val accentColor: Color,
    val secondaryGlow: Color
)

data class DeckBarThemePalette(
    val themeKey: String,
    val themeBadgeEmoji: String,
    val glassTop: Color,
    val glassBottom: Color,
    val borderColors: List<Color>,
    val spaceAccent: Color,
    val minimizeAccent: Color,
    val slotStyles: Map<DeckSlot, DeckSlotThemeStyle>
)

fun resolveDeckBarThemePalette(themeKey: String): DeckBarThemePalette {
    return when (themeKey.uppercase()) {
        "BLACK_HOLE" -> DeckBarThemePalette(
            themeKey = "BLACK_HOLE",
            themeBadgeEmoji = "🕳️",
            glassTop = Color(0xB4140726),
            glassBottom = Color(0xCC070312),
            borderColors = listOf(
                Color(0xFFFF6A00),
                Color(0xFFFFB703),
                Color(0xFF9333EA),
                Color(0xFFFF006E),
                Color(0xFFFF6A00)
            ),
            spaceAccent = Color(0xFFFF9E00),
            minimizeAccent = Color(0xFFFF6A00),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome, Color(0xFFFF6A00), Color(0xFFFFB703)),
                DeckSlot.RADAR to DeckSlotThemeStyle(Icons.Default.TrackChanges, Icons.Outlined.TrackChanges, Color(0xFFFF9E00), Color(0xFFFF5400)),
                DeckSlot.MARKET to DeckSlotThemeStyle(Icons.Default.Diamond, Icons.Outlined.Diamond, Color(0xFFFFD166), Color(0xFFF77F00)),
                DeckSlot.CLIPS to DeckSlotThemeStyle(Icons.Default.Whatshot, Icons.Outlined.Whatshot, Color(0xFFFF006E), Color(0xFFFB5607)),
                DeckSlot.STUDIO to DeckSlotThemeStyle(Icons.Default.MovieFilter, Icons.Outlined.MovieFilter, Color(0xFFC77DFF), Color(0xFF9333EA))
            )
        )
        "MOON" -> DeckBarThemePalette(
            themeKey = "MOON",
            themeBadgeEmoji = "🌕",
            glassTop = Color(0xB40F172A),
            glassBottom = Color(0xCC1E293B),
            borderColors = listOf(
                Color(0xFF38BDF8),
                Color(0xFFE2E8F0),
                Color(0xFF7DD3FC),
                Color(0xFF818CF8),
                Color(0xFF38BDF8)
            ),
            spaceAccent = Color(0xFF38BDF8),
            minimizeAccent = Color(0xFF7DD3FC),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(Icons.Default.Waves, Icons.Outlined.Waves, Color(0xFF38BDF8), Color(0xFF7DD3FC)),
                DeckSlot.RADAR to DeckSlotThemeStyle(Icons.Default.Explore, Icons.Outlined.Explore, Color(0xFF67E8F9), Color(0xFF06B6D4)),
                DeckSlot.MARKET to DeckSlotThemeStyle(Icons.Default.LocalMall, Icons.Outlined.LocalMall, Color(0xFFE2E8F0), Color(0xFF94A3B8)),
                DeckSlot.CLIPS to DeckSlotThemeStyle(Icons.Default.SlowMotionVideo, Icons.Outlined.SlowMotionVideo, Color(0xFF818CF8), Color(0xFFA5B4FC)),
                DeckSlot.STUDIO to DeckSlotThemeStyle(Icons.Default.LiveTv, Icons.Outlined.LiveTv, Color(0xFFC084FC), Color(0xFF60A5FA))
            )
        )
        "GALAXY" -> DeckBarThemePalette(
            themeKey = "GALAXY",
            themeBadgeEmoji = "🌌",
            glassTop = Color(0xB4170B3B),
            glassBottom = Color(0xCC09041A),
            borderColors = listOf(
                Color(0xFFD946EF),
                Color(0xFF6366F1),
                Color(0xFF00F0FF),
                Color(0xFFF43F5E),
                Color(0xFFD946EF)
            ),
            spaceAccent = Color(0xFFD946EF),
            minimizeAccent = Color(0xFF00F0FF),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(Icons.Default.Flare, Icons.Outlined.Flare, Color(0xFFD946EF), Color(0xFFA855F7)),
                DeckSlot.RADAR to DeckSlotThemeStyle(Icons.Default.SatelliteAlt, Icons.Outlined.SatelliteAlt, Color(0xFF00F0FF), Color(0xFF6366F1)),
                DeckSlot.MARKET to DeckSlotThemeStyle(Icons.Default.Store, Icons.Outlined.Store, Color(0xFFF472B6), Color(0xFFEC4899)),
                DeckSlot.CLIPS to DeckSlotThemeStyle(Icons.Default.Subscriptions, Icons.Outlined.Subscriptions, Color(0xFFC084FC), Color(0xFF8B5CF6)),
                DeckSlot.STUDIO to DeckSlotThemeStyle(Icons.Default.Theaters, Icons.Outlined.Theaters, Color(0xFF818CF8), Color(0xFFD946EF))
            )
        )
        "AURORA" -> DeckBarThemePalette(
            themeKey = "AURORA",
            themeBadgeEmoji = "🟢",
            glassTop = Color(0xB404241E),
            glassBottom = Color(0xCC05131E),
            borderColors = listOf(
                Color(0xFF00FF87),
                Color(0xFF00E5FF),
                Color(0xFF34D399),
                Color(0xFFA78BFA),
                Color(0xFF00FF87)
            ),
            spaceAccent = Color(0xFF00FF87),
            minimizeAccent = Color(0xFF00E5FF),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(Icons.Default.Bolt, Icons.Outlined.Bolt, Color(0xFF00FF87), Color(0xFF10B981)),
                DeckSlot.RADAR to DeckSlotThemeStyle(Icons.Default.Radar, Icons.Outlined.Radar, Color(0xFF00E5FF), Color(0xFF00FF87)),
                DeckSlot.MARKET to DeckSlotThemeStyle(Icons.Default.ShoppingBag, Icons.Outlined.ShoppingBag, Color(0xFF34D399), Color(0xFF059669)),
                DeckSlot.CLIPS to DeckSlotThemeStyle(Icons.Default.MotionPhotosOn, Icons.Outlined.MotionPhotosOn, Color(0xFF2DD4BF), Color(0xFF00E5FF)),
                DeckSlot.STUDIO to DeckSlotThemeStyle(Icons.Default.OndemandVideo, Icons.Outlined.OndemandVideo, Color(0xFFA78BFA), Color(0xFF38BDF8))
            )
        )
        "SUNSET" -> DeckBarThemePalette(
            themeKey = "SUNSET",
            themeBadgeEmoji = "🌅",
            glassTop = Color(0xB42B0B1D),
            glassBottom = Color(0xCC160510),
            borderColors = listOf(
                Color(0xFFFFB300),
                Color(0xFFFF4081),
                Color(0xFFFF5252),
                Color(0xFFFF8A65),
                Color(0xFFFFB300)
            ),
            spaceAccent = Color(0xFFFFB300),
            minimizeAccent = Color(0xFFFF4081),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(Icons.Default.WbSunny, Icons.Outlined.WbSunny, Color(0xFFFFB300), Color(0xFFFF6D00)),
                DeckSlot.RADAR to DeckSlotThemeStyle(Icons.Default.MyLocation, Icons.Outlined.MyLocation, Color(0xFFFF4081), Color(0xFFFF80AB)),
                DeckSlot.MARKET to DeckSlotThemeStyle(Icons.Default.Storefront, Icons.Outlined.Storefront, Color(0xFFFFCA28), Color(0xFFFF8F00)),
                DeckSlot.CLIPS to DeckSlotThemeStyle(Icons.Default.PlayCircleFilled, Icons.Outlined.PlayCircle, Color(0xFFFF5252), Color(0xFFFF1744)),
                DeckSlot.STUDIO to DeckSlotThemeStyle(Icons.Default.VideoCameraBack, Icons.Outlined.VideoCameraBack, Color(0xFFFF7043), Color(0xFFE91E63))
            )
        )
        else -> DeckBarThemePalette(
            themeKey = "CUSTOM",
            themeBadgeEmoji = "✨",
            glassTop = Color(0xA80B172A),
            glassBottom = Color(0xC4060E1C),
            borderColors = listOf(
                Color(0xFF00E5FF),
                Color(0xFF00FF87),
                Color(0xFFFFB300),
                Color(0xFFFF4081),
                Color(0xFFA855F7)
            ),
            spaceAccent = Color(0xFF00FF87),
            minimizeAccent = Color(0xFF00E5FF),
            slotStyles = mapOf(
                DeckSlot.PULSE to DeckSlotThemeStyle(DeckSlot.PULSE.activeIcon, DeckSlot.PULSE.inactiveIcon, DeckSlot.PULSE.accentColor, DeckSlot.PULSE.secondaryGlow),
                DeckSlot.RADAR to DeckSlotThemeStyle(DeckSlot.RADAR.activeIcon, DeckSlot.RADAR.inactiveIcon, DeckSlot.RADAR.accentColor, DeckSlot.RADAR.secondaryGlow),
                DeckSlot.MARKET to DeckSlotThemeStyle(DeckSlot.MARKET.activeIcon, DeckSlot.MARKET.inactiveIcon, DeckSlot.MARKET.accentColor, DeckSlot.MARKET.secondaryGlow),
                DeckSlot.CLIPS to DeckSlotThemeStyle(DeckSlot.CLIPS.activeIcon, DeckSlot.CLIPS.inactiveIcon, DeckSlot.CLIPS.accentColor, DeckSlot.CLIPS.secondaryGlow),
                DeckSlot.STUDIO to DeckSlotThemeStyle(DeckSlot.STUDIO.activeIcon, DeckSlot.STUDIO.inactiveIcon, DeckSlot.STUDIO.accentColor, DeckSlot.STUDIO.secondaryGlow)
            )
        )
    }
}

@Composable
fun DeckSwitcherBar(
    currentTab: MainNavigationTab,
    userAvatarUrl: String,
    onTabSelected: (MainNavigationTab) -> Unit,
    currentLanguage: LocaliiiyLanguage = LocalAppLanguage.current,
    activeThemeKey: String = "BLACK_HOLE",
    onThemeButtonClick: (() -> Unit)? = null,
    isDockMinimized: Boolean = false,
    onDockMinimizedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val deckSlots = remember { DeckSlot.values() }
    val currentSlotIndex = remember(currentTab) {
        deckSlots.indexOfFirst { it.tab == currentTab }
    }
    val palette = remember(activeThemeKey) {
        resolveDeckBarThemePalette(activeThemeKey)
    }

    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    var internalMinimized by remember { mutableStateOf(false) }
    val effectiveMinimized = if (onDockMinimizedChange != null) isDockMinimized else internalMinimized
    val setMinimized: (Boolean) -> Unit = { newValue ->
        if (onDockMinimizedChange != null) {
            onDockMinimizedChange(newValue)
        } else {
            internalMinimized = newValue
        }
    }

    val activeSlot = remember(currentTab) {
        deckSlots.firstOrNull { it.tab == currentTab }
    }
    val activeSlotStyle = activeSlot?.let { palette.slotStyles[it] }
    val activeAccentColor = activeSlotStyle?.accentColor ?: palette.spaceAccent
    val activeSlotTitle = remember(currentTab, currentLanguage) {
        if (currentTab == MainNavigationTab.PROFILE) {
            LocalizationHelper.getSlotTitle("Space", currentLanguage)
        } else {
            LocalizationHelper.getSlotTitle(activeSlot?.title ?: "Pulse", currentLanguage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp)
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
        contentAlignment = if (effectiveMinimized) Alignment.BottomEnd else Alignment.BottomCenter
    ) {
        if (effectiveMinimized) {
            // Compact Minimized Floating Task Switcher Orb
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.Transparent,
                tonalElevation = 0.dp,
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.4.dp,
                    brush = Brush.horizontalGradient(palette.borderColors)
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .heightIn(min = 42.dp)
                    .clickable { setMinimized(false) }
                    .testTag("minimized_deck_orb_button")
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    palette.glassTop,
                                    palette.glassBottom,
                                    palette.glassTop
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = activeSlotStyle?.activeIcon ?: Icons.Default.Person,
                        contentDescription = activeSlotTitle,
                        tint = activeAccentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = activeSlotTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textDecoration = TextDecoration.Underline
                        ),
                        color = activeAccentColor
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        deckSlots.forEach { slot ->
                            val sColor = palette.slotStyles[slot]?.accentColor ?: slot.accentColor
                            Box(
                                modifier = Modifier
                                    .size(if (slot.tab == currentTab) 6.dp else 4.5.dp)
                                    .clip(CircleShape)
                                    .background(sColor)
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Expand Task Bar",
                                tint = palette.minimizeAccent,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Frosted Glass Blur Task Bar — Clean Icons (No Circle Border) + Underlined Active Word Only
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent,
                tonalElevation = 0.dp,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.3.dp,
                    brush = Brush.horizontalGradient(
                        palette.borderColors.map { it.copy(alpha = 0.8f) }
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    palette.glassTop,
                                    palette.glassBottom
                                )
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    deckSlots.forEach { slot ->
                        val isSelected = currentTab == slot.tab
                        val slotThemeStyle = palette.slotStyles[slot] ?: DeckSlotThemeStyle(
                            slot.activeIcon,
                            slot.inactiveIcon,
                            slot.accentColor,
                            slot.secondaryGlow
                        )
                        DeckPill(
                            slot = slot,
                            themeStyle = slotThemeStyle,
                            isSelected = isSelected,
                            currentLanguage = currentLanguage,
                            onClick = { onTabSelected(slot.tab) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Profile ("Space") Deck Node — No outer selection circle; word underlined and icon/label enlarged inside existing box when page open
                    val isProfileSelected = currentTab == MainNavigationTab.PROFILE
                    val profileAccent = palette.spaceAccent
                    val profileIconSize by animateDpAsState(
                        targetValue = if (isProfileSelected) 25.dp else 19.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "profile_icon_size"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(MainNavigationTab.PROFILE) }
                            .testTag("deck_profile_node")
                    ) {
                        Box(
                            modifier = Modifier.size(profileIconSize),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userAvatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = userAvatarUrl,
                                    contentDescription = "Space Deck",
                                    modifier = Modifier
                                        .size(profileIconSize)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = if (isProfileSelected) Icons.Default.Person else Icons.Outlined.Person,
                                    contentDescription = "Space",
                                    tint = if (isProfileSelected) profileAccent else profileAccent.copy(alpha = 0.78f),
                                    modifier = Modifier.size(profileIconSize)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(if (isProfileSelected) 2.dp else 3.dp))
                        Text(
                            text = LocalizationHelper.getSlotTitle("Space", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = if (isProfileSelected) 11.5.sp else 9.5.sp,
                                fontWeight = if (isProfileSelected) FontWeight.Black else FontWeight.SemiBold,
                                textDecoration = if (isProfileSelected) TextDecoration.Underline else TextDecoration.None
                            ),
                            color = if (isProfileSelected) profileAccent else Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 1-Tap Minimize Task Bar Icon Button
                    Box(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { setMinimized(true) }
                            .testTag("minimize_deck_bar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize Task Bar to Full Screen",
                            tint = palette.minimizeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckPill(
    slot: DeckSlot,
    themeStyle: DeckSlotThemeStyle,
    isSelected: Boolean,
    currentLanguage: LocaliiiyLanguage = LocalAppLanguage.current,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) themeStyle.accentColor else themeStyle.accentColor.copy(alpha = 0.78f),
        label = "pill_icon_tint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) themeStyle.accentColor else Color.White.copy(alpha = 0.85f),
        label = "pill_label_color"
    )

    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 25.dp else 19.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pill_icon_size"
    )

    val localizedTitle = remember(slot.title, currentLanguage) {
        LocalizationHelper.getSlotTitle(slot.title, currentLanguage)
    }

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(horizontal = 2.dp)
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
            modifier = Modifier.padding(horizontal = 2.dp, vertical = if (isSelected) 2.dp else 4.dp)
        ) {
            // Clean Icon Directly (No Circle Around the Icon) — enlarged when selected within existing box
            Icon(
                imageVector = if (isSelected) themeStyle.activeIcon else themeStyle.inactiveIcon,
                contentDescription = localizedTitle,
                tint = iconTint,
                modifier = Modifier.size(animatedIconSize)
            )
            Spacer(modifier = Modifier.height(if (isSelected) 2.dp else 3.dp))
            // Word is enlarged and Underlined ONLY when that page is open
            Text(
                text = localizedTitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = if (isSelected) 11.5.sp else 9.5.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None
                ),
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

