package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.LiveRadarComponent
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesTray
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal
import com.example.util.LocationHelper

@Composable
fun FeedScreen(
    posts: List<PostEntity>,
    stories: List<StoryEntity>,
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity> = emptyList(),
    selectedRadiusKm: Double? = 3.0,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    onRadiusFilterChange: (Double) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onStoryClick: (StoryEntity) -> Unit,
    onAddStoryClick: () -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentPost: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onSavePost: (PostEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onFollowUser: (OtherUserEntity) -> Unit = {},
    onWaveAtNeighbor: (OtherUserEntity) -> Unit = {},
    onMessageNeighbor: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isRadarExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_screen_list")
    ) {
        // 1. Stories Tray Header (Live Beacon Pulses)
        item {
            StoriesTray(
                stories = stories,
                userProfile = userProfile,
                onStoryClick = onStoryClick,
                onAddStoryClick = onAddStoryClick
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 0.5.dp
            )
        }

        // 2. Interactive Proximity Radar Header Banner & Component
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
            ) {
                // Radar HUD Toggle Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { isRadarExpanded = !isRadarExpanded }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LocaliPrimaryTeal.copy(alpha = 0.18f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Radar,
                                        contentDescription = "Radar HUD",
                                        tint = LocaliPrimaryTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "LIVE PROXIMITY RADAR",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.5.sp,
                                            letterSpacing = 0.8.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = LocaliAccentMint.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.5.sp
                                            ),
                                            color = LocaliAccentMint,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isRadarExpanded) "Scanning ${userProfile.locationName} (${otherUsers.size} allies in range)" else "Tap to expand proximity visualizer",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { isRadarExpanded = !isRadarExpanded },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isRadarExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isRadarExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Expandable Interactive Radar
                AnimatedVisibility(
                    visible = isRadarExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LiveRadarComponent(
                        userProfile = userProfile,
                        nearbyUsers = otherUsers,
                        nearbyPosts = posts,
                        selectedRadiusKm = selectedRadiusKm ?: 3.0,
                        isLocationEnabled = isLocationEnabled,
                        isPrivateAccount = isPrivateAccount,
                        onRadiusChange = onRadiusFilterChange,
                        onLocationToggle = onLocationToggle,
                        onPrivateToggle = onPrivateToggle,
                        onOpenPrivacySettings = onOpenPrivacySettings,
                        onUserClick = { user -> onUserProfileClick(user.username) },
                        onPostClick = onPostClick,
                        onWaveAtUser = onWaveAtNeighbor,
                        onPulseLinkUser = onFollowUser,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Orbit Allies & Nearby Sparks Carousel (Connecting nearby frequency creators)
        if (otherUsers.isNotEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = LocaliPrimaryTeal,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Orbit Allies & Live Sparks",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Locals currently broadcasting in your frequency",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(otherUsers, key = { it.username }) { user ->
                                NeighborCreatorCard(
                                    user = user,
                                    onUserClick = { onUserProfileClick(user.username) },
                                    onFollowClick = { onFollowUser(user) },
                                    onWaveClick = { onWaveAtNeighbor(user) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Local Signals & Street Drops Feed
        items(
            items = posts,
            key = { it.id }
        ) { post ->
            PostCard(
                post = post,
                onLikeClick = { onLikePost(post) },
                onCommentClick = { onCommentPost(post) },
                onShareClick = { onSharePost(post) },
                onSaveClick = { onSavePost(post) },
                onUserClick = { onUserProfileClick(post.username) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 0.5.dp
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NeighborCreatorCard(
    user: OtherUserEntity,
    onUserClick: () -> Unit,
    onFollowClick: () -> Unit,
    onWaveClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .width(145.dp)
            .clickable(onClick = onUserClick)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = user.fullName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )

                // Distance pill badge on avatar
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.offset(x = 4.dp, y = 4.dp)
                ) {
                    Text(
                        text = LocationHelper.formatDistanceLabel(user.distanceKm),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user.fullName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = user.landmark ?: user.locationName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Pulse Link or Wave
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onFollowClick,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant else LocaliPrimaryTeal,
                        contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(
                        text = if (user.isFollowing) "In Orbit ⚡" else "+ Pulse",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onWaveClick,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (user.isFriend) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (user.isFriend) Icons.Default.Check else Icons.Default.WavingHand,
                        contentDescription = "Wave to neighbor",
                        tint = if (user.isFriend) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
