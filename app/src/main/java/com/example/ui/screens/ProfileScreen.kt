

package com.example.ui.screens
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.Warning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.CreatorEarningsSummary
import com.example.data.MarketplaceItemEntity
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.UserProfileEntity
import com.example.ui.ProfileTab
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.ProfileIdentityCard
import com.example.ui.theme.EditorialVerified
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.ui.theme.LocaliiiyStoryGradient
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity,
    posts: List<PostEntity>,
    clips: List<ClipEntity>,
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    savedPosts: List<PostEntity> = emptyList(),
    activeTab: ProfileTab = ProfileTab.POSTS,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    isLoggedOut: Boolean = false,
    otherUsers: List<OtherUserEntity> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onTabChange: (ProfileTab) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onEditProfile: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onPostClick: (PostEntity) -> Unit = {},
    onClipClick: (ClipEntity) -> Unit = {},
    onDeletePost: (Long) -> Unit = {},
    onDeleteClip: (Long) -> Unit = {},
    onUpdateClipDetails: (Long, String, String?, String?) -> Unit = { _, _, _, _ -> },
    onDeleteMarketItem: (Long) -> Unit = {},
    onToggleMarketItemAvailability: (MarketplaceItemEntity) -> Unit = {},
    onUpdateMarketItem: (MarketplaceItemEntity) -> Unit = {},
    onOpenSellItemDialog: () -> Unit = {},
    onMarketItemClick: (MarketplaceItemEntity) -> Unit = {},
    onSwitchUser: (OtherUserEntity) -> Unit = {},
    onLogout: () -> Unit = {},
    onLogin: () -> Unit = {},
    onOpenSignUp: () -> Unit = {},
    onOpenLegalPolicy: () -> Unit = {},
    onOpenCyberstalkingSafety: () -> Unit = {},
    onResetDemoData: () -> Unit = {},
    onCreateContentClick: () -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    creatorEarnings: CreatorEarningsSummary? = null,
    onOpenDataAnalysis: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenInformation: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {},
    onOpenLanguageCurrency: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showMoreSettingsSheet by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Clip / Clip Management State
    var selectedClipForManage by remember { mutableStateOf<ClipEntity?>(null) }
    var showEditClipDialog by remember { mutableStateOf(false) }
    var showDeleteClipConfirmDialog by remember { mutableStateOf(false) }

    // Post Management State
    var selectedPostForManage by remember { mutableStateOf<PostEntity?>(null) }
    var showDeletePostConfirmDialog by remember { mutableStateOf(false) }

    // Proximity Radar Quick Range Selection
    var selectedRadiusKm by remember { mutableStateOf(5.0) }

    val userClips = remember(clips, userProfile.username) {
        val filtered = clips.filter { it.username == userProfile.username || it.userHandle == "@${userProfile.username}" }
        if (filtered.isNotEmpty()) filtered else clips.take(2)
    }

    val storyHighlights = listOf(
        Pair("Local Hub 📍", "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=500&auto=format&fit=crop&q=80"),
        Pair("Street Art 🎨", "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=80"),
        Pair("Cafes ☕", "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=500&auto=format&fit=crop&q=80"),
        Pair("City Sunset 🌇", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500&auto=format&fit=crop&q=80"),
        Pair("Soundtrack 🎵", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80")
    )

    if (isLoggedOut) {
        LoggedOutProfileView(
            userProfile = userProfile,
            otherUsers = otherUsers,
            onLogin = onLogin,
            onOpenSignUp = onOpenSignUp,
            onOpenLegalPolicy = onOpenLegalPolicy,
            onSwitchUser = onSwitchUser,
            onResetDemo = onResetDemoData,
            modifier = modifier
        )
        return
    }

    @OptIn(ExperimentalMaterial3Api::class)
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = rememberPullToRefreshState(),
        modifier = modifier.fillMaxSize().testTag("profile_pull_to_refresh_box")
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("profile_screen_container")
    ) {
        // --- Profile Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("profile_username_header")
            ) {
                Text(
                    text = userProfile.username,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (userProfile.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = EditorialVerified,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Proximity Status Indicator Pill
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isLocationEnabled && !isPrivateAccount) {
                        LocaliiiyAccentMint.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    border = BorderStroke(
                        0.8.dp,
                        if (isLocationEnabled && !isPrivateAccount) LocaliiiyAccentMint.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable { onOpenPrivacySettings() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLocationEnabled && !isPrivateAccount) LocaliiiyAccentMint
                                    else if (isPrivateAccount) Color(0xFFFF9800)
                                    else Color(0xFF9E9E9E)
                                )
                        )
                        Text(
                            text = if (isLocationEnabled && !isPrivateAccount) "Radar Live 📡"
                            else if (isPrivateAccount) "Ghost 👻"
                            else "Hidden 🔒",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Language & Currency Button
                IconButton(
                    onClick = onOpenLanguageCurrency,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("profile_language_currency_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Language & Currency",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Privacy Settings Button
                IconButton(
                    onClick = onOpenPrivacySettings,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("profile_privacy_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy & Security Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // More Settings Menu Button
                IconButton(
                    onClick = { showMoreSettingsSheet = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("profile_more_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Account Settings & Preferences",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- Main Profile Content Grid ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            // Header Content spanning all 3 columns
            item(span = { GridItemSpan(3) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Avatar & Stats Row
                    ProfileIdentityCard(userProfile, posts.size, userClips.size, marketplaceItems.size, 0)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Primary Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit profile",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }

                        Button(
                            onClick = {
                                try {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "Check out @${userProfile.username}'s space on Localiiiy! https://Localiiiy.app/@${userProfile.username}"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Space")
                                    context.startActivity(shareIntent)
                                } catch (_: Exception) {}
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("profile_share_space_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Share Space",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        IconButton(
                            onClick = { onTabChange(ProfileTab.PROXIMITY) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(100.dp))
                                .testTag("profile_radar_shortcut_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Proximity Discovery Controls",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Proximity Discovery Quick Banner
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("profile_proximity_quick_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = null,
                                        tint = if (isLocationEnabled && !isPrivateAccount) LocaliiiyAccentMint else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Neighborhood Proximity Beacon",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                TextButton(
                                    onClick = onOpenPrivacySettings,
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text(
                                        text = "Privacy Settings ›",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isPrivateAccount) "Ghost Mode (Hidden from nearby strangers)"
                                    else if (!isLocationEnabled) "Radar broadcast paused"
                                    else "Broadcasting at ${userProfile.neighborhood} (within ${selectedRadiusKm.toInt()} km)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(
                                        selected = isLocationEnabled && !isPrivateAccount,
                                        onClick = {
                                            if (isPrivateAccount) {
                                                onPrivateToggle(false)
                                            }
                                            onLocationToggle(!isLocationEnabled)
                                        },
                                        label = {
                                            Text(
                                                text = if (isLocationEnabled && !isPrivateAccount) "Live" else "Pause",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isLocationEnabled && !isPrivateAccount) Icons.Default.Sensors else Icons.Default.SensorsOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        },
                                        modifier = Modifier.height(28.dp)
                                    )

                                    FilterChip(
                                        selected = isPrivateAccount,
                                        onClick = { onPrivateToggle(!isPrivateAccount) },
                                        label = {
                                            Text(
                                                text = "Ghost",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        },
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Story Highlights Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(storyHighlights) { highlight ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                                        .padding(3.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(highlight.second)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = highlight.first,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = highlight.first,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Multi-Tab Selector Icons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileTabItem(
                            icon = Icons.Outlined.GridOn,
                            label = "Posts",
                            badgeCount = posts.size,
                            isSelected = activeTab == ProfileTab.POSTS,
                            onClick = { onTabChange(ProfileTab.POSTS) },
                            testTag = "profile_tab_posts"
                        )
                        ProfileTabItem(
                            icon = Icons.Outlined.PlayCircle,
                            label = "Clips",
                            badgeCount = userClips.size,
                            isSelected = activeTab == ProfileTab.CLIPS,
                            onClick = { onTabChange(ProfileTab.CLIPS) },
                            testTag = "profile_tab_clips"
                        )
                        ProfileTabItem(
                            icon = Icons.Outlined.Radar,
                            label = "Radar",
                            isSelected = activeTab == ProfileTab.PROXIMITY,
                            onClick = { onTabChange(ProfileTab.PROXIMITY) },
                            testTag = "profile_tab_proximity"
                        )
                        ProfileTabItem(
                            icon = Icons.Outlined.BookmarkBorder,
                            label = "Saved",
                            badgeCount = savedPosts.size,
                            isSelected = activeTab == ProfileTab.SAVED,
                            onClick = { onTabChange(ProfileTab.SAVED) },
                            testTag = "profile_tab_saved"
                        )
                        ProfileTabItem(
                            icon = Icons.Outlined.PermIdentity,
                            label = "Tagged",
                            isSelected = activeTab == ProfileTab.TAGGED,
                            onClick = { onTabChange(ProfileTab.TAGGED) },
                            testTag = "profile_tab_tagged"
                        )
                        ProfileTabItem(
                            icon = Icons.Outlined.Analytics,
                            label = "Analytics",
                            isSelected = activeTab == ProfileTab.ANALYTICS,
                            onClick = { onTabChange(ProfileTab.ANALYTICS) },
                            testTag = "profile_tab_analytics"
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 0.5.dp
                    )
                }
            }

            // --- Tab Content Items ---
            when (activeTab) {
                ProfileTab.POSTS -> {
                    if (posts.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            EmptyProfileSectionCard(
                                icon = Icons.Outlined.AddPhotoAlternate,
                                title = "Share your first local photo",
                                description = "Post high-resolution neighborhood moments to your profile and feed.",
                                actionText = "Create Post",
                                onAction = onCreateContentClick,
                                testTag = "empty_posts_card"
                            )
                        }
                    } else {
                        items(posts, key = { it.id }) { post ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onPostClick(post) }
                                    .testTag("profile_post_item_${post.id}")
                            ) {
                                ImageWithFilter(
                                    mediaUrl = post.mediaUrl,
                                    filterName = post.filterName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        selectedPostForManage = post
                                        showDeletePostConfirmDialog = true
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(28.dp)
                                        .padding(4.dp)
                                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Post Options",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                if (post.likesCount > 0) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "👌",
                                            fontSize = 10.sp
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${post.likesCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileTab.CLIPS -> {
                    // Management banner for Neighborhood Clips
                    item(span = { GridItemSpan(3) }) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Posted Neighborhood Clips (${userClips.size})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Manage your proximity clips, edit landmarks, or remove clips",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                FilledTonalButton(
                                    onClick = onCreateContentClick,
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("+ New Clip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (userClips.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            EmptyProfileSectionCard(
                                icon = Icons.Outlined.Videocam,
                                title = "No neighborhood clips posted yet",
                                description = "Share short 9:16 videos from your favorite local coffee shops, walks, or community events.",
                                actionText = "Record Neighborhood Clip",
                                onAction = onCreateContentClick,
                                testTag = "empty_clips_card"
                            )
                        }
                    } else {
                        items(userClips, key = { it.id }) { clip ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .aspectRatio(0.68f)
                                    .padding(2.dp)
                                    .clickable { onClipClick(clip) }
                                    .testTag("profile_clip_card_${clip.id}")
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    ImageWithFilter(
                                        mediaUrl = clip.mediaUrl,
                                        filterName = clip.filterName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Gradient overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Black.copy(alpha = 0.4f),
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.85f)
                                                    )
                                                )
                                            )
                                    )

                                    // Top Landmark Tag
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = LocaliiiyAccentMint,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = clip.landmark?.take(12) ?: "Local Hub",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    // Manage Menu Button
                                    IconButton(
                                        onClick = {
                                            selectedClipForManage = clip
                                            showEditClipDialog = true
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(28.dp)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .testTag("manage_clip_btn_${clip.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Manage Clip",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    // Bottom details (views, caption snippet)
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                            .padding(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Views",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = clip.viewsCount,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "👌",
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "${clip.likesCount}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = clip.caption,
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileTab.PROXIMITY -> {
                    item(span = { GridItemSpan(3) }) {
                        ProfileProximitySettingsPanel(
                            userProfile = userProfile,
                            isLocationEnabled = isLocationEnabled,
                            isPrivateAccount = isPrivateAccount,
                            selectedRadiusKm = selectedRadiusKm,
                            onRadiusChange = { selectedRadiusKm = it },
                            onLocationToggle = onLocationToggle,
                            onPrivateToggle = onPrivateToggle,
                            onOpenFullPrivacySettings = onOpenPrivacySettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }

                ProfileTab.SAVED -> {
                    if (savedPosts.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            EmptyProfileSectionCard(
                                icon = Icons.Outlined.BookmarkBorder,
                                title = "No saved posts or listings yet",
                                description = "Save posts from your neighborhood feed or marketplace listings for quick access later.",
                                actionText = "Explore Local Feed",
                                onAction = { onTabChange(ProfileTab.POSTS) },
                                testTag = "empty_saved_card"
                            )
                        }
                    } else {
                        items(savedPosts, key = { it.id }) { post ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onPostClick(post) }
                            ) {
                                ImageWithFilter(
                                    mediaUrl = post.mediaUrl,
                                    filterName = post.filterName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                ProfileTab.TAGGED -> {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.PermIdentity,
                                    contentDescription = "Photos of you",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Photos and videos of you",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "When neighbors tag you in posts or local clips, they will appear here.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                ProfileTab.ANALYTICS -> {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                        com.example.ui.components.StudioAnalyticsComponent()
                    }
                }
            }
        }
    }
    }

    // --- Dialogs & Sheets ---

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            userProfile = userProfile,
            isLocationEnabled = isLocationEnabled,
            isPrivateAccount = isPrivateAccount,
            onLocationToggle = onLocationToggle,
            onPrivateToggle = onPrivateToggle,
            onDismiss = { showEditProfileDialog = false },
            onSaveProfile = onEditProfile
        )
    }

    // 2. Edit Clip Dialog
    if (showEditClipDialog && selectedClipForManage != null) {
        val clip = selectedClipForManage!!
        EditClipDetailsDialog(
            clip = clip,
            onDismiss = {
                showEditClipDialog = false
                selectedClipForManage = null
            },
            onSave = { updatedCaption, updatedLocation, updatedLandmark ->
                onUpdateClipDetails(clip.id, updatedCaption, updatedLocation, updatedLandmark)
                showEditClipDialog = false
                selectedClipForManage = null
            },
            onDelete = {
                showEditClipDialog = false
                showDeleteClipConfirmDialog = true
            },
            onPlayClip = {
                showEditClipDialog = false
                onClipClick(clip)
            }
        )
    }

    // 3. Delete Clip Confirmation Dialog
    if (showDeleteClipConfirmDialog && selectedClipForManage != null) {
        val clip = selectedClipForManage!!
        AlertDialog(
            onDismissRequest = { showDeleteClipConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Neighborhood Clip?") },
            text = {
                Text("Are you sure you want to permanently delete this clip from ${clip.landmark ?: "your profile"}? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClip(clip.id)
                        showDeleteClipConfirmDialog = false
                        selectedClipForManage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_clip_button")
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteClipConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Delete Post Confirmation Dialog
    if (showDeletePostConfirmDialog && selectedPostForManage != null) {
        val post = selectedPostForManage!!
        AlertDialog(
            onDismissRequest = { showDeletePostConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Post?") },
            text = { Text("Remove this photo post from your profile grid?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePost(post.id)
                        showDeletePostConfirmDialog = false
                        selectedPostForManage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. More Settings Bottom Sheet
    if (showMoreSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSettingsSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.testTag("profile_settings_bottom_sheet")
        ) {
            ProfileSettingsSheetContent(
                userProfile = userProfile,
                otherUsers = otherUsers,
                isLocationEnabled = isLocationEnabled,
                isPrivateAccount = isPrivateAccount,
                currentCurrency = currentCurrency,
                currentLanguage = currentLanguage,
                onOpenLanguageCurrency = {
                    showMoreSettingsSheet = false
                    onOpenLanguageCurrency()
                },
                onOpenDataAnalysis = {
                    showMoreSettingsSheet = false
                    onOpenDataAnalysis()
                },
                onOpenHelp = {
                    showMoreSettingsSheet = false
                    onOpenHelp()
                },
                onOpenInformation = {
                    showMoreSettingsSheet = false
                    onOpenInformation()
                },
                onOpenMonetizationHub = {
                    showMoreSettingsSheet = false
                    onOpenMonetizationHub()
                },
                onOpenBoostAds = {
                    showMoreSettingsSheet = false
                    onOpenBoostAds()
                },
                onEditProfileClick = {
                    showMoreSettingsSheet = false
                    showEditProfileDialog = true
                },
                onOpenPrivacySettings = {
                    showMoreSettingsSheet = false
                    onOpenPrivacySettings()
                },
                onOpenLegalPolicy = {
                    showMoreSettingsSheet = false
                    onOpenLegalPolicy()
                },
                onOpenCyberstalkingSafety = {
                    showMoreSettingsSheet = false
                    onOpenCyberstalkingSafety()
                },
                onSwitchUser = { user ->
                    showMoreSettingsSheet = false
                    onSwitchUser(user)
                },
                onResetDemo = {
                    showMoreSettingsSheet = false
                    onResetDemoData()
                },
                onLogoutClick = {
                    showMoreSettingsSheet = false
                    showLogoutConfirmDialog = true
                }
            )
        }
    }

    // 8. Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Log Out of Localiiiy?") },
            text = {
                Text("You are logged in as @${userProfile.username}. Logging out will pause your proximity beacon until you sign back in.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("Log Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Stay Logged In")
                }
            }
        )
    }
}

// --- Subcomponents & Helper Views ---

@Composable
private fun ProfileStatColumn(
    count: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileTabItem(
    icon: ImageVector,
    label: String,
    badgeCount: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.size(24.dp)
            )
            if (badgeCount != null && badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun EmptyProfileSectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileProximitySettingsPanel(
    userProfile: UserProfileEntity,
    isLocationEnabled: Boolean,
    isPrivateAccount: Boolean,
    selectedRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    onLocationToggle: (Boolean) -> Unit,
    onPrivateToggle: (Boolean) -> Unit,
    onOpenFullPrivacySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier.testTag("profile_proximity_settings_panel")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = null,
                    tint = LocaliiiyAccentMint,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Proximity Discovery Controls",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Control how local neighbors discover your profile & clips",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Radar Beacon Broadcast Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Proximity Radar Broadcast",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Allows neighbors within your radius to see you on the live radar map",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isLocationEnabled && !isPrivateAccount,
                    onCheckedChange = { checked ->
                        if (isPrivateAccount && checked) {
                            onPrivateToggle(false)
                        }
                        onLocationToggle(checked)
                    },
                    modifier = Modifier.testTag("toggle_radar_broadcast")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Ghost Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Stealth / Ghost Mode 👻",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Browse neighborhood clips and market without revealing your location",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isPrivateAccount,
                    onCheckedChange = { onPrivateToggle(it) },
                    modifier = Modifier.testTag("toggle_ghost_mode")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Proximity Broadcast Radius Selector
            Text(
                text = "Discovery Reach Radius: ${LocationHelper.formatDistanceLabel(selectedRadiusKm)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            val radiusOptions = listOf(
                Pair(1.0, "1 KM (Walk)"),
                Pair(5.0, "5 KM (Local)"),
                Pair(25.0, "25 KM (City)"),
                Pair(100.0, "100 KM (Metro)"),
                Pair(20000.0, "Global 🌍")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                radiusOptions.take(3).forEach { option ->
                    FilterChip(
                        selected = selectedRadiusKm == option.first,
                        onClick = { onRadiusChange(option.first) },
                        label = { Text(option.second, fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                radiusOptions.drop(3).forEach { option ->
                    FilterChip(
                        selected = selectedRadiusKm == option.first,
                        onClick = { onRadiusChange(option.first) },
                        label = { Text(option.second, fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Privacy Settings button
            Button(
                onClick = onOpenFullPrivacySettings,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Full Privacy & Security Settings",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProfileSettingsSheetContent(
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity>,
    isLocationEnabled: Boolean,
    isPrivateAccount: Boolean,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    onOpenLanguageCurrency: () -> Unit = {},
    onOpenDataAnalysis: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenInformation: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {},
    onEditProfileClick: () -> Unit,
    onOpenPrivacySettings: () -> Unit,
    onOpenLegalPolicy: () -> Unit,
    onOpenCyberstalkingSafety: () -> Unit = {},
    onSwitchUser: (OtherUserEntity) -> Unit,
    onResetDemo: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Settings & Account",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Account Profile Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = userProfile.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.fullName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "@${userProfile.username} • ${userProfile.locationName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onEditProfileClick) {
                    Text("Edit", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WORLDWIDE REACH & MONETIZATION",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(6.dp))

        SettingsRowItem(
            icon = Icons.Default.Public,
            title = "Worldwide Language & Currency",
            subtitle = "${currentLanguage.nativeName} (${currentLanguage.code.uppercase()}) • ${currentCurrency.name} (${currentCurrency.symbol})",
            onClick = onOpenLanguageCurrency
        )

        SettingsRowItem(
            icon = androidx.compose.material.icons.Icons.Default.Analytics,
            title = "Data Analysis",
            subtitle = "View account reach, connection data, and performance analytics",
            onClick = onOpenDataAnalysis
        )

        SettingsRowItem(
            icon = androidx.compose.material.icons.Icons.Default.MonetizationOn,
            title = "Monetization",
            subtitle = "View your earnings, ad revenue, and views",
            onClick = onOpenMonetizationHub
        )

        SettingsRowItem(
            icon = Icons.Default.Campaign,
            title = "Boost Post & Global Ads",
            subtitle = "Promote your posts across 195+ countries & earn worldwide reach",
            onClick = onOpenBoostAds
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PREFERENCES & SAFETY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        SettingsRowItem(
            icon = Icons.Default.Security,
            title = "Privacy & Security",
            subtitle = "Ghost mode, radar visibility, comments & DM permissions",
            onClick = onOpenPrivacySettings
        )

        SettingsRowItem(
            icon = Icons.Default.Gavel,
            title = "App Agreement & Legal Policy",
            subtitle = "Location rights, zero data selling, peer marketplace waiver (v2026.9.3)",
            onClick = onOpenLegalPolicy
        )

        SettingsRowItem(
            icon = Icons.Default.Shield,
            title = "Zero-Tolerance Anti-Stalking Protocol",
            subtitle = "Report offender, execute permanent ban & lock immutable electronic records",
            onClick = onOpenCyberstalkingSafety
        )

        SettingsRowItem(
            icon = Icons.Default.NotificationsNone,
            title = "Neighborhood Notifications",
            subtitle = "Nearby wave alerts, clip comments & market offers",
            onClick = { /* Notification toggles */ }
        )

        SettingsRowItem(
            icon = Icons.Default.Storage,
            title = "Storage & Cache",
            subtitle = "Clear cached clips & media (0.8 MB cached)",
            onClick = { /* Cache clear */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SUPPORT & INFO",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        SettingsRowItem(
            icon = Icons.Default.HelpOutline,
            title = "Help & Support",
            subtitle = "5W & 1H FAQ, advance search, app tasks",
            onClick = onOpenHelp
        )

        SettingsRowItem(
            icon = Icons.Default.Info,
            title = "Information & Badges",
            subtitle = "Creator tiers, terminology, app pages",
            onClick = onOpenInformation
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SWITCH LOCAL CREATOR ACCOUNT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Community Switcher
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(otherUsers.take(4)) { user ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .width(130.dp)
                        .clickable { onSwitchUser(user) }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = user.username,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "@${user.username}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Switch",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ACCOUNT ACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        GhostProtocolButton(
            onTriggered = { onResetDemo() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsRowItem(
            icon = Icons.Default.Refresh,
            title = "Reset Demo Data",
            subtitle = "Restores sample neighborhood posts, clips, and local creators",
            onClick = onResetDemo
        )

        SettingsRowItem(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            title = "Log Out of Localiiiy",
            subtitle = "Signs you out of @${userProfile.username}",
            isDestructive = true,
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isDestructive) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun LoggedOutProfileView(

    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity>,
    onLogin: () -> Unit,
    onOpenSignUp: () -> Unit,
    onOpenLegalPolicy: () -> Unit,
    onSwitchUser: (OtherUserEntity) -> Unit,
    onResetDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotPasswordDialog = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(LocaliiiyAccentMint.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LockPerson,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Localiiiy",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Connect with neighborhood creators, broadcast proximity radar, and share authentic local moments with your community.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Create New Account Button
        // Sign In with Firebase Button
        Button(
            onClick = onLogin,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("profile_login_button")
        ) {
            Icon(
                imageVector = Icons.Default.Login,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign In with Firebase", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Create New Account Button
        OutlinedButton(
            onClick = onOpenSignUp,
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("profile_create_account_button")
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Register with Email & Password", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { showForgotPasswordDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Forgot Password?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = LocaliiiyPrimaryTeal)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal Policy Inline Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenLegalPolicy)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "App Agreement, Location Rights & Privacy Policy",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Read ›",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "or Switch to Local Creator",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            otherUsers.take(3).forEach { user ->
                OutlinedButton(
                    onClick = {
                        onSwitchUser(user)
                        onLogin()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = user.username,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${user.fullName} (@${user.username})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onResetDemo) {
            Text("Reset Demo Experience", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Edit Neighborhood Clip Dialog ---
@Composable
private fun EditClipDetailsDialog(
    clip: ClipEntity,
    onDismiss: () -> Unit,
    onSave: (String, String?, String?) -> Unit,
    onDelete: () -> Unit,
    onPlayClip: () -> Unit
) {
    var caption by remember { mutableStateOf(clip.caption) }
    var location by remember { mutableStateOf(clip.location ?: "") }
    var landmark by remember { mutableStateOf(clip.landmark ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit Neighborhood Clip",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onPlayClip)
                ) {
                    ImageWithFilter(
                        mediaUrl = clip.mediaUrl,
                        filterName = clip.filterName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview Clip", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Clip Caption") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Neighborhood Landmark Tag") },
                    placeholder = { Text("e.g. Pike Place Market, Pier 66") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("City / Area") },
                    placeholder = { Text("Seattle, WA") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete Clip")
                    }

                    Button(
                        onClick = { onSave(caption, location.ifBlank { null }, landmark.ifBlank { null }) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}


@Composable
fun GhostProtocolButton(
    onTriggered: () -> Unit
) {
    var isActivated by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }
    val coroutineScope = rememberCoroutineScope()
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(isActivated) {
        if (isActivated) {
            while (countdown > 0) {
                pulseScale.animateTo(1.05f, tween(100, easing = LinearEasing))
                pulseScale.animateTo(1f, tween(100, easing = LinearEasing))
                delay(800)
                countdown -= 1
            }
            onTriggered()
            isActivated = false
            countdown = 5
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(pulseScale.value),
        shape = RoundedCornerShape(12.dp),
        color = if (isActivated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.errorContainer,
        onClick = {
            if (!isActivated) isActivated = true
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Ghost Protocol",
                tint = if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActivated) "GHOST PROTOCOL INITIATED ($countdown)" else "GHOST PROTOCOL",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Instantly encrypts local DB & forces absolute stealth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = (if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer).copy(alpha = 0.8f)
                )
            }
        }
    }
}
