package com.example.ui.screens

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
import androidx.compose.material.icons.filled.NearMe
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
import com.example.data.AdPlacement
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.PostCard
import com.example.ui.components.PulseFeedComponent
import com.example.ui.components.StoriesTray
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocationHelper

@Composable
fun FeedScreen(
    clips: List<com.example.data.ClipEntity> = emptyList(),
    marketplaceItems: List<com.example.data.MarketplaceItemEntity> = emptyList(),
    posts: List<PostEntity>,
    stories: List<StoryEntity>,
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity> = emptyList(),
    selectedRadiusKm: Double? = 3.0,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
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
    sponsoredAds: List<AdPlacement> = emptyList(),
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    onAdImpression: (String) -> Unit = {},
    onAdClick: (String) -> Unit = {},
    onBoostPostClick: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    countryName: String? = null,
    modifier: Modifier = Modifier
) {
    PulseFeedComponent(
        clips = clips,
        marketplaceItems = marketplaceItems,
        posts = posts,
        stories = stories,
        userProfile = userProfile,
        otherUsers = otherUsers,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        selectedRadiusKm = selectedRadiusKm,
        onRadiusFilterChange = onRadiusFilterChange,
        countryName = countryName,
        onStoryClick = onStoryClick,
        onAddStoryClick = onAddStoryClick,
        onLikePost = onLikePost,
        onCommentPost = onCommentPost,
        onSharePost = onSharePost,
        onSavePost = onSavePost,
        onUserProfileClick = onUserProfileClick,
        onFollowUser = onFollowUser,
        onWaveAtNeighbor = onWaveAtNeighbor,
        onPostClick = onPostClick,
        sponsoredAds = sponsoredAds,
        currentCurrency = currentCurrency,
        currentLanguage = currentLanguage,
        onAdImpression = onAdImpression,
        onAdClick = onAdClick,
        onBoostPostClick = onBoostPostClick,
        onOpenMonetizationHub = onOpenMonetizationHub,
        modifier = modifier
    )
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
                        containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant else LocaliiiyPrimaryTeal,
                        contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(
                        text = if (user.isFollowing) "Connected" else "Connect",
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
