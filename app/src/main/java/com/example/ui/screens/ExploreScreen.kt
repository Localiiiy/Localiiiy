package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.LiveRadarComponent
import com.example.ui.components.PostCard
import com.example.ui.theme.LocaliAccentCoral
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal
import com.example.util.LocationHelper

enum class ExploreViewMode {
    RADAR, // Circular Hyperlocal Map Interface
    GRID   // Traditional 3-Column Stream Grid
}

@Composable
fun ExploreScreen(
    posts: List<PostEntity>,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity> = emptyList(),
    selectedRadiusKm: Double? = null,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    onRadiusFilterChange: (Double?) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onPostClick: (PostEntity) -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentPost: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onSavePost: (PostEntity) -> Unit,
    onUserProfileClick: (String) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var localRadiusKm by remember(selectedRadiusKm) { mutableStateOf(selectedRadiusKm ?: 3.0) }
    var exploreViewMode by remember { mutableStateOf(ExploreViewMode.RADAR) }
    var selectedDetailPost by remember { mutableStateOf<PostEntity?>(null) }

    val radiusFilters = listOf(
        Pair("🏡 < 1 km", 1.0),
        Pair("📍 < 3 km", 3.0),
        Pair("🏙️ < 5 km", 5.0),
        Pair("🌐 < 10 km", 10.0)
    )

    // Filter posts by query or distance
    val filteredPosts = remember(posts, searchQuery, localRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else posts.filter { post ->
            val matchesQuery = if (searchQuery.isBlank()) true else {
                post.caption.contains(searchQuery, ignoreCase = true) ||
                post.username.contains(searchQuery, ignoreCase = true) ||
                (post.location?.contains(searchQuery, ignoreCase = true) == true) ||
                (post.landmark?.contains(searchQuery, ignoreCase = true) == true)
            }
            val matchesRadius = (post.distanceKm ?: 999.0) <= localRadiusKm
            matchesQuery && matchesRadius
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("explore_screen_container")
    ) {
        // Search & View Mode Switcher Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search landmark, street, creator...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("explore_search_field")
            )

            // Switch between Live Radar view and Grid View
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Radar View Button
                    IconButton(
                        onClick = { exploreViewMode = ExploreViewMode.RADAR },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (exploreViewMode == ExploreViewMode.RADAR) LocaliDeepNavy else Color.Transparent
                            )
                            .testTag("toggle_radar_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Radar,
                            contentDescription = "Live Circular Radar",
                            tint = if (exploreViewMode == ExploreViewMode.RADAR) LocaliAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Grid View Button
                    IconButton(
                        onClick = { exploreViewMode = ExploreViewMode.GRID },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (exploreViewMode == ExploreViewMode.GRID) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                            .testTag("toggle_grid_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = "Grid View",
                            tint = if (exploreViewMode == ExploreViewMode.GRID) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // VIEW MODE: LIVE RADAR (Distinct Circular Map & Interactive Blip Interface)
        if (exploreViewMode == ExploreViewMode.RADAR) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Main Interactive Live Radar Component
                LiveRadarComponent(
                    userProfile = userProfile,
                    nearbyUsers = nearbyUsers,
                    nearbyPosts = posts,
                    selectedRadiusKm = localRadiusKm,
                    isLocationEnabled = isLocationEnabled,
                    isPrivateAccount = isPrivateAccount,
                    onRadiusChange = { radius ->
                        localRadiusKm = radius
                        onRadiusFilterChange(radius)
                    },
                    onLocationToggle = onLocationToggle,
                    onPrivateToggle = onPrivateToggle,
                    onOpenPrivacySettings = onOpenPrivacySettings,
                    onUserClick = { user -> onUserProfileClick(user.username) },
                    onPostClick = { post -> selectedDetailPost = post },
                    onWaveAtUser = onWaveAtUser
                )

                // Landmark Quick Discovery Tray
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = LocaliPrimaryTeal,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Local Landmark Hotspots",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${filteredPosts.size} pulses",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocaliPrimaryTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val landmarks = listOf(
                        Pair("Pike Place Market", "0.1 km"),
                        Pair("Pioneer Square", "0.6 km"),
                        Pair("Belltown", "0.9 km"),
                        Pair("Capitol Hill", "1.3 km"),
                        Pair("Lake Union", "2.1 km"),
                        Pair("Fremont Troll", "4.8 km")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(landmarks) { (landmark, dist) ->
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable { searchQuery = landmark }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = landmark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "($dist)",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Pulses Detected on Radar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Recent Pulses on Radar",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredPosts) { post ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .width(130.dp)
                                    .clickable { selectedDetailPost = post }
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                    ) {
                                        ImageWithFilter(
                                            mediaUrl = post.mediaUrl,
                                            filterName = post.filterName,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = post.caption
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = LocationHelper.formatDistanceLabel(post.distanceKm),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = "@${post.username}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = post.landmark ?: post.location ?: "Nearby",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // VIEW MODE: GRID VIEW
            Column(modifier = Modifier.fillMaxSize()) {
                // Radius Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(radiusFilters) { (label, radius) ->
                        val isSelected = localRadiusKm == radius
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localRadiusKm = radius
                                onRadiusFilterChange(radius)
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(100.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Explore 3-Column Grid with Distance Badges
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                    verticalArrangement = Arrangement.spacedBy(1.5.dp)
                ) {
                    items(
                        items = filteredPosts,
                        key = { it.id }
                    ) { post ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { selectedDetailPost = post }
                                .testTag("explore_post_cell_${post.id}")
                        ) {
                            ImageWithFilter(
                                mediaUrl = post.mediaUrl,
                                filterName = post.filterName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                contentDescription = post.caption
                            )

                            // Gradient overlay at bottom for readability
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )

                            // Distance badge
                            val distLabel = LocationHelper.formatDistanceLabel(post.distanceKm)
                            Text(
                                text = "📍 $distLabel",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                            )

                            // Play icon if even id
                            if (post.id % 2L == 0L) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail modal
    if (selectedDetailPost != null) {
        val post = selectedDetailPost!!
        AlertDialog(
            onDismissRequest = { selectedDetailPost = null },
            confirmButton = {
                TextButton(onClick = { selectedDetailPost = null }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserClick = {
                            selectedDetailPost = null
                            onUserProfileClick(post.username)
                        }
                    )
                }
            }
        )
    }
}
