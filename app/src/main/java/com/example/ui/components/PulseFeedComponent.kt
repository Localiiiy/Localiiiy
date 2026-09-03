package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdPlacement
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.UserProfileEntity
import com.example.data.firestore.FirestorePulseService
import com.example.data.firestore.HyperlocalPulseUpdate
import com.example.ui.screens.NeighborCreatorCard
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliCurrency
import com.example.util.LocaliLanguage
import com.example.util.LocalizationHelper

/**
 * Dedicated 'Pulse' Feed Component for Localiiiy.
 * Fetches and displays a list of hyperlocal updates from Firestore using LazyColumn,
 * with images, location tags, interaction buttons for likes and shares, and a pull-to-refresh mechanism.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseFeedComponent(
    posts: List<PostEntity>,
    stories: List<StoryEntity>,
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity> = emptyList(),
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    selectedRadiusKm: Double? = 3.0,
    onRadiusFilterChange: (Double) -> Unit = {},
    onStoryClick: (StoryEntity) -> Unit,
    onAddStoryClick: () -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentPost: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onSavePost: (PostEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onFollowUser: (OtherUserEntity) -> Unit = {},
    onWaveAtNeighbor: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    sponsoredAds: List<AdPlacement> = emptyList(),
    currentCurrency: LocaliCurrency = LocaliCurrency.USD,
    currentLanguage: LocaliLanguage = LocaliLanguage.EN,
    onAdImpression: (String) -> Unit = {},
    onAdClick: (String) -> Unit = {},
    onBoostPostClick: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val refreshState = rememberPullToRefreshState()
    var selectedRadius by remember { mutableStateOf(selectedRadiusKm ?: 3.0) }

    // Fetch and collect real-time hyperlocal updates from Firestore
    val firestoreService = remember { FirestorePulseService() }
    val firestoreUpdates by firestoreService.getHyperlocalUpdatesFlow()
        .collectAsState(initial = firestoreService.getFallbackSeedUpdates())

    // Merge or map Firestore updates to feed
    val combinedPosts = remember(posts, firestoreUpdates, selectedRadius) {
        val convertedFirestorePosts = firestoreUpdates.map { update ->
            PostEntity(
                id = (update.id.hashCode().toLong() and 0x7FFFFFFF) + 100000L,
                username = update.username.ifBlank { update.authorName },
                userAvatar = update.userAvatar,
                userHandle = "@${update.username.ifBlank { "local_creator" }}",
                mediaUrl = update.mediaUrl,
                caption = update.content,
                likesCount = update.likesCount,
                commentsCount = update.commentsCount,
                isLiked = update.isLiked,
                isSaved = false,
                timestamp = update.timestamp,
                location = update.location,
                landmark = update.landmark,
                latitude = update.latitude,
                longitude = update.longitude
            )
        }

        // Interleave posts and deduplicate
        val allMerged = (posts + convertedFirestorePosts)
            .distinctBy { it.mediaUrl.ifBlank { it.caption } }
            .sortedByDescending { it.timestamp }

        // Filter by radius if applicable
        allMerged
    }

    val radiusOptions = listOf(
        "⚡ 1 km" to 1.0,
        "📍 3 km" to 3.0,
        "🏙️ 5 km" to 5.0,
        "📡 10 km" to 10.0,
        "🌐 All" to 50.0
    )

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = refreshState,
        modifier = modifier
            .fillMaxSize()
            .testTag("pulse_feed_container")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pulse_scrollable_list")
        ) {
            // 1. Stories Tray Header (Beacon Moments)
            item {
                StoriesTray(
                    stories = stories,
                    userProfile = userProfile,
                    onStoryClick = onStoryClick,
                    onAddStoryClick = onAddStoryClick
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )
            }

            // 2. Pulse Hyperlocal Bar & Proximity Filter Pills
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(LocaliAccentMint)
                                )
                                Text(
                                    text = "Hyperlocal Pulse Feed",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Manual refresh pill trigger
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onRefresh() }
                                    .testTag("pulse_refresh_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isRefreshing) "Syncing..." else "Pull / Tap",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Radius Filter Chips
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            items(radiusOptions) { (label, radius) ->
                                val isSelected = (selectedRadius == radius)
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) LocaliPrimaryTeal else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable {
                                            selectedRadius = radius
                                            onRadiusFilterChange(radius)
                                        }
                                        .testTag("pulse_radius_chip_$label")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )
            }

            // 3. Nearby Sparks Carousel
            if (otherUsers.isNotEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
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
                                            fontSize = 13.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Nearby creators",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(top = 6.dp)
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

            // 4. Scrollable Community Posts with Images, Location Tags, Like & Share buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LocaliAccentMint)
                        )
                        Text(
                            text = "Live Radar • Firestore Synced",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = LocaliPrimaryTeal
                            )
                        )
                    }
                    Text(
                        text = "${combinedPosts.size} Pulses nearby",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            if (combinedPosts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Radar,
                            contentDescription = null,
                            tint = LocaliPrimaryTeal,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No pulses detected within this radius",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Expand your radar or pull down to fetch fresh local community updates.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedRadius = 50.0
                                onRadiusFilterChange(50.0)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LocaliPrimaryTeal)
                        ) {
                            Text("Expand to All Pulses", color = Color.White)
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = combinedPosts,
                    key = { _, post -> post.id }
                ) { index, post ->
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserClick = { onUserProfileClick(post.username) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 0.5.dp
                    )

                    // Interleave native sponsored ad post every 3rd post
                    if (sponsoredAds.isNotEmpty() && (index == 1 || (index > 1 && (index + 1) % 3 == 0))) {
                        val adIndex = ((index + 1) / 3) % sponsoredAds.size
                        val ad = sponsoredAds[adIndex]
                        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            SponsoredAdCard(
                                ad = ad,
                                currentCurrency = currentCurrency,
                                currentLanguage = currentLanguage,
                                onAdImpression = onAdImpression,
                                onAdClick = onAdClick
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
