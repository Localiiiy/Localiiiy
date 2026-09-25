package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.content.res.Configuration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.CreatorEarningsSummary
import com.example.data.StudioVideoEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.StudioVideoPlayerComponent
import com.example.ui.components.StudioInlinePreviewPlayer
import com.example.ui.components.StudioDockedMiniPlayer
import com.example.util.CurrencyHelper
import com.example.util.LocalAppLanguage
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import kotlin.math.abs

import com.example.ui.components.SystematicDistanceScale
import com.example.ui.components.SystematicDistanceOption
import com.example.ui.components.studio.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

val StudioCategories = listOf(
    "All",
    "Music",
    "News",
    "Tech",
    "Lifestyle",
    "Podcasts",
    "Documentaries",
    "Food & Cooking",
    "Gaming",
    "Neighborhood & Culture",
    "Education",
    "Entertainment"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    videos: List<StudioVideoEntity>,
    userProfile: UserProfileEntity,
    selectedCategory: String,
    searchQuery: String,
    activeVideo: StudioVideoEntity?,
    isPlaying: Boolean,
    playbackProgress: Float,
    isUploadSheetOpen: Boolean,
    showCreatorDashboard: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onVideoClick: (StudioVideoEntity) -> Unit,
    onCloseVideo: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onLikeVideo: (StudioVideoEntity) -> Unit,
    onSaveVideo: (StudioVideoEntity) -> Unit,
    onSubscribeCreator: (String) -> Unit,
    onOpenUploadSheet: () -> Unit,
    onCloseUploadSheet: () -> Unit,
    onToggleCreatorDashboard: () -> Unit,
    onUploadVideo: (
        title: String,
        description: String,
        category: String,
        durationSeconds: Int,
        videoUrl: String,
        thumbnailUrl: String,
        resolution: String,
        tags: String,
        chapters: String
    ) -> Boolean,
    onDeleteVideo: (Long) -> Unit,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    studioSortOption: String = "Most Popular (Views)",
    onStudioSortChange: (String) -> Unit = {},
    studioScopeFilter: String = "All Locations",
    onStudioScopeChange: (String) -> Unit = {},
    countryName: String? = null,
    creatorEarnings: CreatorEarningsSummary? = null,
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {},
    onOpenLanguageCurrency: () -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    bounties: List<com.example.data.MerchantBountyEntity> = emptyList(),
    onClaimBounty: (com.example.data.MerchantBountyEntity) -> Unit = {},
    drafts: List<com.example.data.DraftClipEntity> = emptyList(),
    onRouteDraftToClips: (com.example.data.DraftClipEntity) -> Unit = {},
    onRouteDraftToMarket: (com.example.data.DraftClipEntity) -> Unit = {},
    onRouteDraftToPulse: (com.example.data.DraftClipEntity) -> Unit = {},
    onDeleteDraft: (com.example.data.DraftClipEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }

    // Filter videos by category and search
    val filteredVideos = remember(videos, selectedCategory, searchQuery) {
        videos.filter { video ->
            val matchesCategory = selectedCategory == "All" || video.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    video.title.contains(searchQuery, ignoreCase = true) ||
                    video.description.contains(searchQuery, ignoreCase = true) ||
                    video.creatorFullName.contains(searchQuery, ignoreCase = true) ||
                    video.creatorUsername.contains(searchQuery, ignoreCase = true) ||
                    video.tags.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val sortedVideos = remember(filteredVideos, studioSortOption, studioScopeFilter, systematicOptions) {
        val matchedOption = systematicOptions.firstOrNull {
            it.shortLabel.equals(studioScopeFilter, ignoreCase = true) ||
            it.key.equals(studioScopeFilter, ignoreCase = true)
        }
        val scopeFiltered = filteredVideos.filter { video ->
            if (matchedOption != null) {
                if (matchedOption.key == "EARTH" || matchedOption.key == "GALAXY") true
                else video.distanceKm <= matchedOption.km
            } else when (studioScopeFilter) {
                "Neighborhood (3km)", "3KM" -> video.distanceKm <= 3.0
                "5KM" -> video.distanceKm <= 5.0
                "50KM" -> video.distanceKm <= 50.0
                "100KM" -> video.distanceKm <= 100.0
                "500KM" -> video.distanceKm <= 500.0
                "1000KM" -> video.distanceKm <= 1000.0
                "Country" -> video.distanceKm <= 5000.0
                "Earth", "Earth 🌍", "Galaxy" -> true
                else -> true
            }
        }
        when (studioSortOption) {
            "Most Popular (Views)" -> scopeFiltered.sortedByDescending { it.viewsCount }
            "Newest Upload" -> scopeFiltered.sortedByDescending { it.timestamp }
            "Duration: Longest" -> scopeFiltered.sortedByDescending { it.durationSeconds }
            "Duration: Shortest" -> scopeFiltered.sortedBy { it.durationSeconds }
            "Top Rated (Likes)" -> scopeFiltered.sortedByDescending { it.likesCount }
            "Creator Connected", "Creator Connections" -> scopeFiltered.sortedByDescending { it.subscriberCountInt }
            else -> scopeFiltered
        }
    }

    // Spotlit featured video (first trending or creator pick)
    val featuredVideo = remember(videos) {
        videos.firstOrNull { it.isCreatorPick || it.isTrending } ?: videos.firstOrNull()
    }

    var selectedVideoForMenu by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var showReportDialog by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var showTipDialog by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var showBountyBoardDialog by remember { mutableStateOf(false) }
    var showDraftVaultDialog by remember { mutableStateOf(false) }

    // Video Preview on Scroll & Mini Player States
    var isVideoPreviewOnScrollEnabled by remember { mutableStateOf(true) }
    var isPreviewMuted by remember { mutableStateOf(true) }
    var manualPreviewVideoId by remember { mutableStateOf<Long?>(null) }
    var miniPlayerVideo by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var isMiniPlayerPlaying by remember { mutableStateOf(true) }
    var isMiniPlayerMuted by remember { mutableStateOf(false) }

    val studioListState = rememberLazyListState()

    // Real-time tracking of which video card is currently in view while scrolling
    val visibleVideoId by remember {
        derivedStateOf {
            val layoutInfo = studioListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) null
            else {
                val viewportCenter = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                val centerItem = visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    abs(itemCenter - viewportCenter)
                }
                centerItem?.key as? Long
            }
        }
    }

    val activePreviewVideoId = manualPreviewVideoId ?: if (isVideoPreviewOnScrollEnabled) visibleVideoId else null

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = rememberPullToRefreshState(),
            modifier = Modifier.fillMaxSize().testTag("studio_pull_to_refresh_box")
        ) {
            LazyColumn(
                state = studioListState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("studio_feed_list")
            ) {
            // 1. Localiiiy Studio Brand Header & Creator Studio Switch
            item {
                StudioHeader(
                    userProfile = userProfile,
                    showCreatorDashboard = showCreatorDashboard,
                    onToggleDashboard = onToggleCreatorDashboard,
                    onOpenUpload = onOpenUploadSheet,
                    onOpenLanguageCurrency = onOpenLanguageCurrency
                )
            }

            // 2. Search Bar
            item {
                StudioSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange
                )
            }

            // PROMPT 11 & 12: Sponsor Bounty Board & Unified Draft Vault Action Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showBountyBoardDialog = true }
                            .testTag("btn_merchant_bounties_hub")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("💼", fontSize = 15.sp)
                            Column {
                                Text(
                                    text = "Bounty Board",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "${bounties.count { !it.isClaimed }} Local Bounties",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDraftVaultDialog = true }
                            .testTag("btn_draft_vault_hub")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🗄️", fontSize = 15.sp)
                            Column {
                                Text(
                                    text = "Draft Vault",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${drafts.size} Vault Drafts",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Standard Category Filter Chips
            item {
                StudioCategoryChips(
                    categories = StudioCategories,
                    selectedCategory = selectedCategory,
                    onSelect = onCategorySelected
                )
            }

            // 3.1 Advanced Sort & Scope Filter Chips (Popularity, Duration, Neighborhood/Country/Earth)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // Sort options row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sort:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val sortOptions = listOf(
                                "Most Popular (Views)",
                                "Newest Upload",
                                "Duration: Longest",
                                "Duration: Shortest",
                                "Top Rated (Likes)",
                                "Creator Connected"
                            )
                            items(sortOptions) { sOpt ->
                                val isSelected = studioSortOption == sOpt
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable { onStudioSortChange(sOpt) }
                                        .testTag("studio_sort_$sOpt")
                                 ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = when (sOpt) {
                                                "Most Popular (Views)" -> "🔥"
                                                "Newest Upload" -> "🕒"
                                                "Duration: Longest" -> "⏳"
                                                "Duration: Shortest" -> "⚡"
                                                "Top Rated (Likes)" -> "👌"
                                                "Creator Connected", "Creator Connections" -> "👥"
                                                else -> "🎬"
                                            },
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = LocalizationHelper.translate(sOpt, currentLanguage),
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Geographic Scope Filter row (Neighborhood, Country, Earth)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Scope:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                val isAll = studioScopeFilter == "All Locations" || studioScopeFilter.isBlank()
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isAll) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable { onStudioScopeChange("All Locations") }
                                        .testTag("studio_scope_all")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text("🌐", fontSize = 10.sp)
                                        Text(
                                            text = "All Locations",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isAll) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            items(systematicOptions) { opt ->
                                val isSelected = studioScopeFilter.equals(opt.shortLabel, ignoreCase = true) ||
                                        studioScopeFilter.equals(opt.key, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable { onStudioScopeChange(opt.shortLabel) }
                                        .testTag("studio_scope_${opt.key.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(opt.icon, fontSize = 10.sp)
                                        Text(
                                            text = opt.shortLabel,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Playback & Video Preview Options Row (Mute/Unmute, Auto-preview on scroll, Mini Player)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Options:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 1. Video Preview on Scroll Toggle
                            item {
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isVideoPreviewOnScrollEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isVideoPreviewOnScrollEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable {
                                            isVideoPreviewOnScrollEnabled = !isVideoPreviewOnScrollEnabled
                                            if (!isVideoPreviewOnScrollEnabled) manualPreviewVideoId = null
                                        }
                                        .testTag("studio_toggle_video_preview")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isVideoPreviewOnScrollEnabled) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                                            contentDescription = null,
                                            tint = if (isVideoPreviewOnScrollEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isVideoPreviewOnScrollEnabled) "Video Preview: ON" else "Video Preview: OFF",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isVideoPreviewOnScrollEnabled) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isVideoPreviewOnScrollEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // 2. Mute / Unmute Toggle
                            item {
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (!isPreviewMuted) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (!isPreviewMuted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable { isPreviewMuted = !isPreviewMuted }
                                        .testTag("studio_toggle_preview_mute")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPreviewMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = if (!isPreviewMuted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isPreviewMuted) "Muted" else "Sound On",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (!isPreviewMuted) FontWeight.Bold else FontWeight.Normal,
                                            color = if (!isPreviewMuted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // 3. Mini Player in Studio Option
                            item {
                                val isMiniActive = miniPlayerVideo != null
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isMiniActive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isMiniActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable {
                                            if (miniPlayerVideo != null) {
                                                val v = miniPlayerVideo!!
                                                miniPlayerVideo = null
                                                onVideoClick(v)
                                            } else if (sortedVideos.isNotEmpty()) {
                                                miniPlayerVideo = sortedVideos.first()
                                                isMiniPlayerPlaying = true
                                                isMiniPlayerMuted = false
                                            }
                                        }
                                        .testTag("studio_mini_player_toggle")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureInPictureAlt,
                                            contentDescription = null,
                                            tint = if (isMiniActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isMiniActive) "Mini Player: Active" else "Mini Player",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isMiniActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isMiniActive) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Creator Studio Dashboard Banner (Expandable)
            if (showCreatorDashboard) {
                item {
                    CreatorStudioDashboardPanel(
                        userProfile = userProfile,
                        userVideos = videos.filter { it.creatorUsername == userProfile.username },
                        currentCurrency = currentCurrency,
                        creatorEarnings = creatorEarnings,
                        onUploadClick = onOpenUploadSheet,
                        onOpenMonetizationHub = onOpenMonetizationHub,
                        onOpenBoostAds = onOpenBoostAds
                    )
                }
            }

            // 5. Featured Long-Form Hero Premiere Banner (Only if All is selected and no search)
            if (selectedCategory == "All" && searchQuery.isBlank() && featuredVideo != null) {
                item {
                    StudioSpotlightHero(
                        video = featuredVideo,
                        onClick = { onVideoClick(featuredVideo) }
                    )
                }
            }

            // 6. Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedCategory == "All") "Long-Form Creator Catalog" else "$selectedCategory Videos",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${sortedVideos.size} full-length videos (60s – Unlimited)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "4K / 1080p",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 7. Video Feed Items (Standard Large 16:9 Cards)
            if (sortedVideos.isEmpty()) {
                item {
                    StudioEmptyState(
                        category = selectedCategory,
                        onClearFilters = {
                            onCategorySelected("All")
                            onSearchQueryChange("")
                        },
                        onUploadClick = onOpenUploadSheet
                    )
                }
            } else {
                items(sortedVideos, key = { it.id }) { video ->
                    val isPreviewActive = (activePreviewVideoId == video.id)
                    StudioVideoCard(
                        video = video,
                        isPreviewActive = isPreviewActive,
                        isPreviewMuted = isPreviewMuted,
                        onToggleMute = { isPreviewMuted = !isPreviewMuted },
                        onStartPreview = {
                            manualPreviewVideoId = video.id
                        },
                        onStartMiniPlayer = {
                            miniPlayerVideo = video
                            isMiniPlayerPlaying = true
                            isMiniPlayerMuted = false
                        },
                        onClick = { onVideoClick(video) },
                        onMenuClick = { selectedVideoForMenu = video }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        }

        // Docked Studio Mini Player (Pinned to bottom of screen while browsing)
        if (miniPlayerVideo != null && activeVideo == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                StudioDockedMiniPlayer(
                    video = miniPlayerVideo!!,
                    isPlaying = isMiniPlayerPlaying,
                    isMuted = isMiniPlayerMuted,
                    playbackProgress = 0f,
                    onTogglePlayPause = { isMiniPlayerPlaying = !isMiniPlayerPlaying },
                    onToggleMute = { isMiniPlayerMuted = !isMiniPlayerMuted },
                    onExpandToFullPlayer = {
                        val v = miniPlayerVideo!!
                        miniPlayerVideo = null
                        onVideoClick(v)
                    },
                    onCloseMiniPlayer = { miniPlayerVideo = null }
                )
            }
        }

        // Active Long Video Player Screen (Theater overlay)
        if (activeVideo != null) {
            StudioPlayerModal(
                video = activeVideo,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                onClose = onCloseVideo,
                onTogglePlayPause = onTogglePlayPause,
                onSeek = onSeek,
                onLike = { onLikeVideo(activeVideo) },
                onSave = { onSaveVideo(activeVideo) },
                onSubscribe = { onSubscribeCreator(activeVideo.creatorUsername) },
                onUserProfileClick = onUserProfileClick,
                onTip = { showTipDialog = activeVideo },
                onReport = { showReportDialog = activeVideo },
                onMinimizeToMiniPlayer = {
                    val v = activeVideo
                    onCloseVideo()
                    miniPlayerVideo = v
                    isMiniPlayerPlaying = isPlaying
                    isMiniPlayerMuted = false
                },
                relatedVideos = videos.filter { it.id != activeVideo.id },
                onSelectRelatedVideo = { onVideoClick(it) }
            )
        }

        // Upload Long Video Dialog/Sheet (60s to 240 mins)
        if (isUploadSheetOpen) {
            StudioUploadDialog(
                onDismiss = onCloseUploadSheet,
                onPublish = onUploadVideo
            )
        }

        // Video Action Context Menu
        if (selectedVideoForMenu != null) {
            val v = selectedVideoForMenu!!
            AlertDialog(
                onDismissRequest = { selectedVideoForMenu = null },
                title = {
                    Text(
                        text = v.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSaveVideo(v)
                                    selectedVideoForMenu = null
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (v.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(if (v.isSaved) "Remove from Watch Later" else "Save to Watch Later")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showTipDialog = v
                                    selectedVideoForMenu = null
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            )
                            Text("Send Super Thanks / Tip Creator")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showReportDialog = v
                                    selectedVideoForMenu = null
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text("Report Video (Content Policy)")
                        }

                        if (v.creatorUsername == userProfile.username) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onDeleteVideo(v.id)
                                        selectedVideoForMenu = null
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text("Delete My Video from Studio", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedVideoForMenu = null }) {
                        Text("Close")
                    }
                }
            )
        }

        // Report Video Dialog
        if (showReportDialog != null) {
            val rVideo = showReportDialog!!
            AlertDialog(
                onDismissRequest = { showReportDialog = null },
                title = { Text("Report Studio Video") },
                text = {
                    Text("Report \"${rVideo.title}\" for review by Localiiiy community trust & safety team.")
                },
                confirmButton = {
                    Button(
                        onClick = { showReportDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Submit Report", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Super Thanks / Tip Creator Dialog
        if (showTipDialog != null) {
            val tVideo = showTipDialog!!
            var tipAmount by remember { mutableStateOf("$5.00") }
            AlertDialog(
                onDismissRequest = { showTipDialog = null },
                icon = {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = { Text("Super Thanks to @${tVideo.creatorUsername}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Support long-form creator content directly. 100% of community tips go to the creator channel.")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("$2.00", "$5.00", "$10.00", "$25.00").forEach { amt ->
                                FilterChip(
                                    selected = tipAmount == amt,
                                    onClick = { tipAmount = amt },
                                    label = { Text(amt, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showTipDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Text("Send $tipAmount 💖", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTipDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showBountyBoardDialog) {
            MerchantBountyBoardDialog(
                bounties = bounties,
                onClaimBounty = { bounty ->
                    onClaimBounty(bounty)
                },
                onDismiss = { showBountyBoardDialog = false }
            )
        }

        if (showDraftVaultDialog) {
            UnifiedDraftVaultDialog(
                drafts = drafts,
                onRouteDraftToClips = onRouteDraftToClips,
                onRouteDraftToMarket = onRouteDraftToMarket,
                onRouteDraftToPulse = onRouteDraftToPulse,
                onDeleteDraft = onDeleteDraft,
                onDismiss = { showDraftVaultDialog = false }
            )
        }
    }
}

// -------------------------------------------------------------
// Studio Top Header
// -------------------------------------------------------------
@Composable
private fun StudioHeader(
    userProfile: UserProfileEntity,
    showCreatorDashboard: Boolean,
    onToggleDashboard: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenLanguageCurrency: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFF3366), Color(0xFFFF5E3A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Studio Logo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Localiiiy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        color = Color(0xFFFF3366),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "STUDIO",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            letterSpacing = 1.sp
                        )
                    }
                }
                Text(
                    text = "Long-Form Video & Creator Hub",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons: Creator Dashboard & Upload Long Video
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onToggleDashboard,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (showCreatorDashboard) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .testTag("studio_dashboard_toggle")
            ) {
                Icon(
                    imageVector = if (showCreatorDashboard) Icons.Default.Analytics else Icons.Outlined.Analytics,
                    contentDescription = "Creator Analytics Dashboard",
                    tint = if (showCreatorDashboard) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Button(
                onClick = onOpenUpload,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3366)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("studio_upload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Upload",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Search Bar
// -------------------------------------------------------------
@Composable
private fun StudioSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("studio_search_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search long videos, podcasts, music, channels...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Standard Category Navigation Bar
// -------------------------------------------------------------
@Composable
fun StudioCategoryNavigationBar(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("studio_category_navigation_bar"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                val icon = when (cat) {
                    "Music" -> Icons.Default.MusicNote
                    "News" -> Icons.Default.Article
                    "Tech", "Tech & Gadgets" -> Icons.Default.Devices
                    "Lifestyle" -> Icons.Default.Spa
                    "Podcasts" -> Icons.Default.Podcasts
                    "Documentaries" -> Icons.Default.Movie
                    "Food & Cooking" -> Icons.Default.Restaurant
                    "Gaming" -> Icons.Default.SportsEsports
                    "Neighborhood & Culture" -> Icons.Default.LocationCity
                    "Education" -> Icons.Default.School
                    "Entertainment" -> Icons.Default.TheaterComedy
                    else -> Icons.Default.Explore
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { onSelect(cat) }
                        .testTag("studio_chip_$cat")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = LocalizationHelper.getCategoryName(cat, LocalAppLanguage.current),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioCategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit
) {
    StudioCategoryNavigationBar(
        categories = categories,
        selectedCategory = selectedCategory,
        onSelect = onSelect
    )
}

// -------------------------------------------------------------
// Creator Studio Dashboard (Expandable)
// -------------------------------------------------------------
@Composable
private fun CreatorStudioDashboardPanel(
    userProfile: UserProfileEntity,
    userVideos: List<StudioVideoEntity>,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    creatorEarnings: CreatorEarningsSummary? = null,
    onUploadClick: () -> Unit,
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {}
) {
    val totalEarnings = creatorEarnings?.availableBalanceUSD ?: 842.0
    val formattedEarnings = CurrencyHelper.format(totalEarnings, currentCurrency)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("creator_studio_dashboard"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, Color(0xFFFF3366).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = Color(0xFFFF3366),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Creator Studio Hub • @${userProfile.username}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                    modifier = Modifier.clickable { onOpenMonetizationHub() }
                ) {
                    Text(
                        text = "Monetization Active 🟢",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Card Analytics Grid (2x2 spacious layout)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioMetricCard(
                        title = "Watch Hours",
                        value = "1,840.2h",
                        trend = "+28.4%",
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetricCard(
                        title = "Studio Views",
                        value = "192.4K",
                        trend = "+14.8%",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioMetricCard(
                        title = "Connected",
                        value = "${userProfile.followersCount / 1000}K",
                        trend = "+182 new",
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetricCard(
                        title = "Est. Earnings",
                        value = formattedEarnings,
                        trend = "55% RevShare",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenMonetizationHub() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            
            // Dual-Velocity Analytics
            DualVelocityAnalyticsBreakdown(
                localSeedPercentage = 68,
                globalWavePercentage = 32,
                localImpressions = 8420,
                globalImpressions = 3960
            )

            Spacer(modifier = Modifier.height(14.dp))
            
            ContentPerformanceBenchmarkCard(
                postTitle = "Recent Dispatch Performance",
                percentAboveAverage = 34,
                daysCompared = 30
            )

            Spacer(modifier = Modifier.height(14.dp))
            
            Guaranteed100ImpressionProgress(
                deliveredImpressions = 84,
                targetImpressions = 100
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Monetization & Global Ads Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenMonetizationHub,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("studio_manage_payouts_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Payouts & Ads", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onOpenBoostAds,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("studio_boost_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFFFF3366),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Boost Video", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF3366))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upload Long Video prompt with Standard constraints
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Publish Long-Form Creator Video",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Duration: 60 seconds up to unlimited time with Media7 Galaxy 4K playback.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onUploadClick,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("+ New Upload", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StudioMetricCard(
    title: String,
    value: String,
    trend: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trend,
                fontSize = 9.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

// -------------------------------------------------------------
// Spotlight Hero Video (Premiere Widescreen Card)
// -------------------------------------------------------------
@Composable
private fun StudioSpotlightHero(
    video: StudioVideoEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("studio_hero_spotlight"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // 16:9 Thumbnail with Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                ),
                                startY = 100f
                            )
                        )
                )

                // Top Badges (Spotlight & Resolution)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF3366)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "STUDIO SPOTLIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = video.resolution,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Center Play Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3366).copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bottom Duration Pill & Category
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2196F3).copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = video.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = formatDuration(video.durationSeconds),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Info row below thumbnail
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.creatorAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = video.creatorFullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = "${video.creatorFullName} • ${video.viewsFormatted} • ${video.uploadDateFormatted}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Standard Video Card (16:9 Thumbnail / Live Inline Preview, Channel info, 3-dots)
// -------------------------------------------------------------
@Composable
private fun StudioVideoCard(
    video: StudioVideoEntity,
    isPreviewActive: Boolean = false,
    isPreviewMuted: Boolean = true,
    onToggleMute: () -> Unit = {},
    onStartPreview: () -> Unit = {},
    onStartMiniPlayer: () -> Unit = {},
    onLikeClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("studio_video_card_${video.id}")
    ) {
        // 16:9 Thumbnail or Inline Live Preview Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
        ) {
            if (isPreviewActive) {
                // Live Scrolling Video Preview with Mute/Unmute & Mini Player dock controls
                StudioInlinePreviewPlayer(
                    video = video,
                    isMuted = isPreviewMuted,
                    onToggleMute = onToggleMute,
                    onStartMiniPlayer = onStartMiniPlayer,
                    onOpenFullPlayer = onClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Quick Tap-To-Preview Chip (Top Right)
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clickable { onStartPreview() }
                        .testTag("card_preview_btn_${video.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Preview Video",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Preview",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Duration badge in bottom right (e.g. 48:30 or 2:15:00)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = formatDuration(video.durationSeconds),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Category & Resolution badge top left
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = video.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF3366).copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = video.resolution,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Info Row: Avatar + Title/Channel info + 3-dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.creatorAvatar)
                    .crossfade(true)
                    .build(),
                contentDescription = video.creatorFullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = video.creatorFullName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (video.isCreatorVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Creator",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "@${video.creatorUsername} • ${video.creatorConnectedCount}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                var isLiked by remember(video.id) { mutableStateOf(video.isLiked) }
                var likesCount by remember(video.id) { mutableStateOf(video.likesCount) }
                com.example.ui.components.AnimatedLikeButton(
                    isLiked = isLiked,
                    onLikeClick = {
                        isLiked = !isLiked
                        likesCount += if (isLiked) 1 else -1
                        onLikeClick?.invoke()
                    },
                    likesCount = likesCount,
                    showCount = true,
                    symbolSize = 17.sp,
                    touchTargetSize = 34.dp,
                    testTag = "studio_card_like_${video.id}"
                )
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Active Long Video Player Modal
// -------------------------------------------------------------
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun StudioPlayerModal(
    video: StudioVideoEntity,
    isPlaying: Boolean,
    playbackProgress: Float,
    onClose: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onSubscribe: () -> Unit,
    onUserProfileClick: (String) -> Unit,
    onTip: () -> Unit,
    onReport: () -> Unit,
    onMinimizeToMiniPlayer: () -> Unit = {},
    relatedVideos: List<StudioVideoEntity>,
    onSelectRelatedVideo: (StudioVideoEntity) -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf(
        "Amazing video! The quality is insane 🔥",
        "Keep up the great work! Can't wait for the next one.",
        "This helped me so much, thank you!",
        "First! 🥇",
        "Connected! Really love your content."
    )) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Video Player
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f).background(Color.Black)) {
                    val modalContext = LocalContext.current
                    val modalHaptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                    com.example.ui.components.StudioVideoPlayerComponent(
                        video = video,
                        onClose = onClose,
                        onVideoCompleted = {
                            com.example.util.HapticHelper.triggerHaptic(
                                modalContext,
                                modalHaptic,
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                        },
                        onMinimizeToMiniPlayer = onMinimizeToMiniPlayer
                    )
                }

                // Scrollable content
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${video.viewsFormatted} views • ${video.uploadDateFormatted} • ${video.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(video.creatorAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = video.creatorFullName,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { onUserProfileClick(video.creatorUsername) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f).clickable { onUserProfileClick(video.creatorUsername) }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = video.creatorFullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (video.isCreatorVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Creator",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "@${video.creatorUsername} • ${video.creatorConnectedCount}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        Button(
                            onClick = onSubscribe,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (video.isSubscribed) MaterialTheme.colorScheme.surfaceVariant
                                else Color(0xFFFF3366)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (video.isSubscribed) "Connected" else "Connect",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (video.isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                        }
                    }

                    // Studio Video Actions Row: Like (Animated 👌), Comments (✍️), Save, Tip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(36.dp)
                        ) {
                            com.example.ui.components.AnimatedLikeButton(
                                isLiked = video.isLiked,
                                onLikeClick = onLike,
                                likesCount = video.likesCount,
                                showCount = true,
                                symbolSize = 18.sp,
                                touchTargetSize = 36.dp,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                testTag = "studio_video_like_${video.id}"
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(36.dp)
                        ) {
                            com.example.ui.components.CommentActionButton(
                                onClick = { showComments = !showComments },
                                commentsCount = comments.size,
                                showCount = true,
                                symbolSize = 18.sp,
                                touchTargetSize = 36.dp,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                testTag = "studio_video_comments_${video.id}"
                            )
                        }

                        OutlinedButton(
                            onClick = onSave,
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (video.isSaved) "Saved" else "Save", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onTip,
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🪙 Tip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Expandable Description & Chapters
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Description & Chapters",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.description,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (video.chapters.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "TIMESTAMPS:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = video.chapters,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = if (isDescriptionExpanded) "Show Less ▲" else "...more ▼",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Comments Preview Accordion
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showComments = !showComments }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✍️ Remarks (${comments.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Icon(
                                    imageVector = if (showComments) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }

                            if (!showComments) {
                                Text(
                                    text = "\"${comments.firstOrNull() ?: "No remarks yet"}\"",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                comments.forEach { comment ->
                                    Text(
                                        text = "• $comment",
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newCommentText,
                                        onValueChange = { newCommentText = it },
                                        placeholder = { Text("✍️ Add a community remark...", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            if (newCommentText.isNotBlank()) {
                                                comments = listOf(newCommentText.trim()) + comments
                                                newCommentText = ""
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Up Next in Localiiiy Studio",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Related Videos List
                items(relatedVideos) { relVideo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRelatedVideo(relVideo) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(relVideo.thumbnailUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = relVideo.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = Color.Black.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = formatDuration(relVideo.durationSeconds),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = relVideo.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${relVideo.creatorFullName} • ${relVideo.viewsFormatted}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
        }
    }
}

// -------------------------------------------------------------
// Upload Long Video Dialog (Enforcing 60s to 240 mins)
// -------------------------------------------------------------
@Composable
private fun StudioUploadDialog(
    onDismiss: () -> Unit,
    onPublish: (
        title: String,
        description: String,
        category: String,
        durationSeconds: Int,
        videoUrl: String,
        thumbnailUrl: String,
        resolution: String,
        tags: String,
        chapters: String
    ) -> Boolean
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Music") }
    var durationHoursText by remember { mutableStateOf("0") }
    var durationMinutesText by remember { mutableStateOf("15") }
    var durationSecondsText by remember { mutableStateOf("00") }
    var videoUrl by remember { mutableStateOf("https://media.w3.org/2010/05/sintel/trailer.mp4") }
    var thumbnailUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80") }
    var resolution by remember { mutableStateOf("4K Ultra HD") }
    var tags by remember { mutableStateOf("#Localiiiy #Studio #Creator") }
    var chapters by remember { mutableStateOf("00:00 - Introduction\n04:15 - Main Segment\n12:30 - Conclusion") }

    // Distribution States
    var selectedReachMode by remember { mutableStateOf(AudienceReachMode.NEIGHBOR_FIRST) }
    var isEvergreen by remember { mutableStateOf(false) }
    var isLocalCommentsOnly by remember { mutableStateOf(false) }
    var micVolume by remember { mutableStateOf(0.8f) }
    var ambientVolume by remember { mutableStateOf(0.2f) }
    var selectedAmbientTrack by remember { mutableStateOf("None") }
    var geotagPrecision by remember { mutableStateOf(GeotagPrecision.NEIGHBORHOOD) }
    var coCreators by remember { mutableStateOf(listOf<String>()) }
    var isScheduled by remember { mutableStateOf(false) }
    var selectedScheduleWindow by remember { mutableStateOf("12:30 PM (Lunch Pulse)") }
    var notifyConnections by remember { mutableStateOf(true) }
    var isPublishing by remember { mutableStateOf(false) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            videoUrl = uri.toString()
        }
    }

    val thumbnailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            thumbnailUrl = uri.toString()
        }
    }

    val totalDurationSeconds = remember(durationHoursText, durationMinutesText, durationSecondsText) {
        val hrs = durationHoursText.toIntOrNull() ?: 0
        val mins = durationMinutesText.toIntOrNull() ?: 0
        val secs = durationSecondsText.toIntOrNull() ?: 0
        (hrs * 3600) + (mins * 60) + secs
    }

    // Flexible duration: any duration or default
    val effectiveDuration = if (totalDurationSeconds <= 0) 60 else totalDurationSeconds
    val isDurationValid = totalDurationSeconds >= 0
    val isFormValid = title.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = Color(0xFFFF3366),
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "Localiiiy Studio Upload",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Long-form creator video (60s – Unlimited time)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Video Title
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Video Title *") },
                            placeholder = { Text("e.g., Seattle Music Festival 2026 - 4K Full Concert") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Category Selector
                    item {
                        Text(
                            text = "Category *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(StudioCategories.filter { it != "All" }) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Standard Duration Picker (Hours + Minutes + Seconds) with Live Validation
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDurationValid) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isDurationValid) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Video Duration (Requirement: 60s minimum • Unlimited Time) *",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isDurationValid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = formatDuration(totalDurationSeconds),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isDurationValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = durationHoursText,
                                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) durationHoursText = it },
                                        label = { Text("Hours") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = durationMinutesText,
                                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) durationMinutesText = it },
                                        label = { Text("Mins") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = durationSecondsText,
                                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) durationSecondsText = it },
                                        label = { Text("Secs (0-59)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (!isDurationValid) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "⚠️ Minimum Studio video duration is 60 seconds. Short clips under 60 seconds belong in the Clips tab! (Unlimited maximum time supported).",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "✅ Valid long-form video duration: ${formatDuration(totalDurationSeconds)} (Unlimited playback enabled)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }

                    // Resolution Selector
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Resolution", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("4K Ultra HD", "1080p Full HD", "720p HD").forEach { res ->
                                    FilterChip(
                                        selected = resolution == res,
                                        onClick = { resolution = res },
                                        label = { Text(res, fontSize = 10.5.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // Description & Chapters
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description & Chapters") },
                            placeholder = { Text("Detailed story, gear used, and timestamps...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    // Thumbnail Presets / URL
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("16:9 Thumbnail Cover", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                TextButton(
                                    onClick = {
                                        thumbnailPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pick from Gallery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = thumbnailUrl,
                                onValueChange = { thumbnailUrl = it },
                                label = { Text("Cover Image URL or Gallery URI") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Video Stream URL with Device Picker & Stream Options
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Video Content File", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Button(
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Select Video File 📁", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = videoUrl,
                                onValueChange = { videoUrl = it },
                                label = { Text("Video MP4 / HLS Stream URL or Path") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Tags
                    item {
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Tags & Keywords") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    
                    // Advanced Studio Distribution Settings
                    item {
                        Text(
                            text = "Advanced Distribution Settings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    item {
                        AudienceReachSelector(
                            selectedMode = selectedReachMode,
                            onModeSelected = { selectedReachMode = it }
                        )
                    }

                    item {
                        GeotagPrecisionSelector(
                            selectedPrecision = geotagPrecision,
                            onSelect = { geotagPrecision = it }
                        )
                    }

                    item {
                        MultiTrackAudioMixer(
                            micVolume = micVolume,
                            onMicVolumeChange = { micVolume = it },
                            ambientVolume = ambientVolume,
                            onAmbientVolumeChange = { ambientVolume = it },
                            selectedAmbientTrack = selectedAmbientTrack,
                            onSelectAmbientTrack = { selectedAmbientTrack = it }
                        )
                    }

                    item {
                        CollaborativeCoAuthorSelector(
                            coCreators = coCreators,
                            onAddCoCreator = { coCreators = coCreators + it },
                            onRemoveCoCreator = { coCreators = coCreators - it }
                        )
                    }
                    
                    item {
                        LocalTimezonePublishScheduler(
                            isScheduled = isScheduled,
                            onToggleScheduled = { isScheduled = it },
                            selectedWindow = selectedScheduleWindow,
                            onSelectWindow = { selectedScheduleWindow = it }
                        )
                    }

                    item {
                        DirectConnectionBroadcastCheckbox(
                            notifyConnections = notifyConnections,
                            onToggle = { notifyConnections = it }
                        )
                    }

                    item {
                        ContentLongevityToggle(
                            isEvergreen = isEvergreen,
                            onToggle = { isEvergreen = it }
                        )
                    }

                    item {
                        ProximityGatedCommentsToggle(
                            isLocalOnly = isLocalCommentsOnly,
                            onToggle = { isLocalCommentsOnly = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom CTA Button
                Button(
                    onClick = {
                        if (!isPublishing) {
                            isPublishing = true
                            val success = onPublish(
                                title,
                                description,
                                selectedCategory,
                                effectiveDuration,
                                videoUrl,
                                thumbnailUrl,
                                resolution,
                                tags,
                                chapters
                            )
                            if (success) {
                                onDismiss()
                            } else {
                                isPublishing = false
                            }
                        }
                    },
                    enabled = isFormValid && !isPublishing,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3366)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("publish_studio_video_button")
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publishing to Studio...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publish to Localiiiy Studio 🚀",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Empty State
// -------------------------------------------------------------
@Composable
private fun StudioEmptyState(
    category: String,
    onClearFilters: () -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.VideoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "No Videos in $category",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Be the first creator to upload a long-form video (60s to unlimited time) to this category!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClearFilters) {
                Text("Show All")
            }
            Button(
                onClick = onUploadClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366))
            ) {
                Text("+ Upload Long Video", color = Color.White)
            }
        }
    }
}

// -------------------------------------------------------------
// Helper Duration Formatter
// -------------------------------------------------------------
private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

