package com.example.ui.components

import com.example.data.MarketplaceItemEntity
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.components.AdBannerComponent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdPlacement
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.StoryEntity
import com.example.data.StudioVideoEntity
import com.example.data.UserProfileEntity
import androidx.compose.foundation.clickable
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage

enum class PulseFeedFilter(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.Public),
    TRENDING("Trending", Icons.Default.TrendingUp),
    NEARBY("Nearby", Icons.Default.NearMe),
    CONNECTED("Connected", Icons.Default.Person),
    LATEST("Latest", Icons.Default.ElectricBolt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseFeedComponent(
    posts: List<PostEntity>,
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
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
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    countryName: String? = null,
    onAdImpression: (String) -> Unit = {},
    onAdClick: (String) -> Unit = {},
    onBoostPostClick: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val refreshState = rememberPullToRefreshState()
    var isInitialLoad by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf(PulseFeedFilter.ALL) }

    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }
    val currentDistanceOpt = remember(selectedRadiusKm, countryName) {
        SystematicDistanceScale.findOption(selectedRadiusKm, countryName)
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        isInitialLoad = false
    }

    // Filter Posts by All, Trending, Nearby, Following, Latest
    val displayedPosts = remember(posts, selectedFilter, selectedRadiusKm) {
        when (selectedFilter) {
            PulseFeedFilter.ALL -> posts.sortedByDescending { it.timestamp }
            PulseFeedFilter.TRENDING -> posts.sortedByDescending { (it.likesCount * 3) + it.commentsCount }
            PulseFeedFilter.NEARBY -> {
                val radius = selectedRadiusKm ?: 3.0
                val nearby = posts.filter { (it.distanceKm ?: 99.0) <= radius }
                if (nearby.isNotEmpty()) nearby.sortedBy { it.distanceKm ?: 99.0 }
                else posts.sortedBy { it.distanceKm ?: 99.0 }
            }
            PulseFeedFilter.CONNECTED -> {
                val connected = posts.filter { it.isFollowing }
                if (connected.isNotEmpty()) connected.sortedByDescending { it.timestamp }
                else posts.take(5)
            }
            PulseFeedFilter.LATEST -> posts.sortedByDescending { it.timestamp }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = refreshState,
        modifier = modifier.fillMaxSize().testTag("pulse_feed_pull_to_refresh")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                StoriesTray(
                    stories = stories,
                    userProfile = userProfile,
                    onStoryClick = onStoryClick,
                    onAddStoryClick = onAddStoryClick
                )
            }

            // Hyperlocal Discovery Filter Header
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        text = "Pulse Feed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (selectedFilter) {
                            PulseFeedFilter.ALL -> "${displayedPosts.size} Pulses • Showing all local posts"
                            PulseFeedFilter.TRENDING -> "${displayedPosts.size} Pulses • Trending in your community"
                            PulseFeedFilter.NEARBY -> "${displayedPosts.size} Pulses • Hyperlocal (within ${selectedRadiusKm ?: 3.0} km)"
                            PulseFeedFilter.CONNECTED -> "${displayedPosts.size} Pulses • From accounts you are connected with"
                            PulseFeedFilter.LATEST -> "${displayedPosts.size} Pulses • Freshly shared moments"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter UI Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().testTag("pulse_feed_filter_row"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(PulseFeedFilter.values()) { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = filter.icon,
                                        contentDescription = filter.label,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("pulse_filter_${filter.name.lowercase()}")
                            )
                        }
                    }

                    // Systematic Distance Filter Row (when NEARBY is selected)
                    if (selectedFilter == PulseFeedFilter.NEARBY) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DISTANCE RANGE:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pulse_feed_distance_row"),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(systematicOptions) { opt ->
                                val isSelected = kotlin.math.abs((selectedRadiusKm ?: 3.0) - opt.km) < 0.1 ||
                                        (opt.key == "COUNTRY" && selectedRadiusKm == SystematicDistanceScale.COUNTRY_DEFAULT_KM) ||
                                        (opt.key == "EARTH" && selectedRadiusKm == SystematicDistanceScale.EARTH_KM) ||
                                        (opt.key == "GALAXY" && selectedRadiusKm == SystematicDistanceScale.GALAXY_KM)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onRadiusFilterChange(opt.km) }
                                        .testTag("pulse_distance_pill_${opt.key.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = opt.icon, fontSize = 11.sp)
                                        Text(
                                            text = opt.shortLabel,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isInitialLoad || isRefreshing) {
                items(4) {
                    PostSkeleton()
                }
            } else if (displayedPosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = selectedFilter.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No pulses found for '${selectedFilter.label}'",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'All' to browse community pulses or create one!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(items = displayedPosts, key = { _, post -> "post_${post.id}" }) { index, item ->
                    if (index > 0 && index % 3 == 0) {
                        AdBannerComponent()
                    }
                    PostCard(
                        post = item,
                        onLikeClick = { onLikePost(item) },
                        onCommentClick = { onCommentPost(item) },
                        onShareClick = { onSharePost(item) },
                        onSaveClick = { onSavePost(item) },
                        onUserClick = { onUserProfileClick(item.username) },
                        modifier = Modifier.animateItem()
                    )
                    
                    if (index > 0 && index % 5 == 0 && sponsoredAds.isNotEmpty()) {
                        val adIndex = (index / 5) % sponsoredAds.size
                        val ad = sponsoredAds[adIndex]
                        SponsoredAdCard(
                            ad = ad,
                            currentCurrency = currentCurrency,
                            currentLanguage = currentLanguage,
                            onAdImpression = { onAdImpression(ad.id) },
                            onAdClick = { onAdClick(ad.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(shimmerColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Box(modifier = Modifier.height(14.dp).width(120.dp).background(shimmerColor, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.height(10.dp).width(80.dp).background(shimmerColor, RoundedCornerShape(4.dp)))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(14.dp).fillMaxWidth().background(shimmerColor, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.8f).background(shimmerColor, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(shimmerColor, RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
                Box(modifier = Modifier.size(24.dp).background(shimmerColor, CircleShape))
            }
        }
    }
}

@Composable
fun PulseClipCard(clip: ClipEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(clip.mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Clip thumbnail",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(48.dp)
                )
            }
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = clip.userAvatar,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = clip.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        CreatorBadgeIcon(followers = clip.creatorFollowers, showText = false)
                    }
                    Text(text = clip.caption, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun PulseMarketItemCard(item: MarketplaceItemEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "@${item.sellerUsername}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$${item.price}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
