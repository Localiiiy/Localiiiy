package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.PulseFeedComponent
import com.example.ui.screens.CreatorClipsCameraScreen
import com.example.ui.screens.LiveRadarScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.LocaliiiyAccentCoral
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal

/**
 * Type-safe Destinations for Navigation Compose.
 */
sealed class LocaliiiyNavScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object PulseFeed : LocaliiiyNavScreen(
        route = "pulse_feed",
        title = "Pulse Feed",
        selectedIcon = Icons.Default.DynamicFeed,
        unselectedIcon = Icons.Outlined.DynamicFeed,
        testTag = "nav_tab_pulse_feed"
    )

    object LiveRadar : LocaliiiyNavScreen(
        route = "live_radar",
        title = "Live Radar",
        selectedIcon = Icons.Default.Radar,
        unselectedIcon = Icons.Outlined.Radar,
        testTag = "nav_tab_live_radar"
    )

    object Studio : LocaliiiyNavScreen(
        route = "studio",
        title = "Studio",
        selectedIcon = Icons.Default.VideoLibrary,
        unselectedIcon = Icons.Outlined.VideoLibrary,
        testTag = "nav_tab_studio"
    )

    object CreatorClips : LocaliiiyNavScreen(
        route = "creator_clips",
        title = "Clips",
        selectedIcon = Icons.Default.Videocam,
        unselectedIcon = Icons.Outlined.Videocam,
        testTag = "nav_tab_creator_clips"
    )

    object Profile : LocaliiiyNavScreen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_tab_profile"
    )

    companion object {
        val bottomNavItems = listOf(PulseFeed, LiveRadar, Studio, CreatorClips, Profile)
    }
}

/**
 * Bottom Navigation Bar implemented using Navigation Compose.
 * Enables seamless switching between Pulse Feed, Live Radar, and Localiiiy Studio views.
 */
@Composable
fun LocaliiiyNavigationComposeBottomBar(
    navController: NavHostController,
    userAvatarUrl: String,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: LocaliiiyNavScreen.PulseFeed.route

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("navigation_compose_bottom_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocaliiiyNavScreen.bottomNavItems.forEach { screen ->
                val isSelected = currentRoute == screen.route

                if (screen == LocaliiiyNavScreen.Profile && userAvatarUrl.isNotBlank()) {
                    // Profile Avatar tab item
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                navigateToTab(navController, screen.route)
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag(screen.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) LocaliiiyPrimaryTeal else Color.Transparent
                                    )
                                    .padding(if (isSelected) 1.5.dp else 0.dp)
                            ) {
                                AsyncImage(
                                    model = userAvatarUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    // Standard Navigation Items: Pulse Feed, Live Radar, Studio, Clips
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                navigateToTab(navController, screen.route)
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag(screen.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title,
                                tint = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard Navigation Compose tab transition with state preservation.
 */
private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
