package com.example.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.CreatorEarningsSummary
import com.example.data.CreatorExclusiveTier
import com.example.data.StudioVideoEntity
import com.example.data.UserProfileEntity
import com.example.data.copyright.ContentLicensingConfig
import com.example.ui.components.AnimatedLikeButton
import com.example.ui.components.CommentActionButton
import com.example.ui.components.CreatorExclusiveTiersDialog
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.LocaliiiyAppLogoBadge
import com.example.ui.components.StudioDockedMiniPlayer
import com.example.ui.components.StudioInlinePreviewPlayer
import com.example.ui.components.StudioVideoPlayerComponent
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.components.copyright.LicensingAndReuseRightsUploadSection
import com.example.ui.components.studio.*
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.ShareHelper

val StudioCategories: List<String> = listOf(
    "All",
    "Cartoons & Animation",
    "Anime & Manga",
    "Gaming & Esports",
    "Indie Cinema",
    "Vlogs & Daily Life",
    "Music & Live Shows",
    "Tech Reviews",
    "AI & Automation",
    "Comedy & Skits",
    "Street Food & Cooking",
    "DIY & Crafts",
    "Auto & Moto",
    "Sports & Fitness",
    "Street Dance",
    "Fashion & Styling",
    "Science & Explainer",
    "Pets & Wildlife",
    "ASMR & Audio",
    "News & Commentary",
    "Local Documentaries"
)

data class StudioRemarkItem(
    val id: Long,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String,
    val isVerified: Boolean = false,
    val text: String,
    val timeAgo: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<StudioRemarkItem> = emptyList()
)

private val defaultStudioVideos = listOf(
    StudioVideoEntity(
        id = 501L,
        title = "Seattle Midnight Jazz & Acoustic Sessions — Live 4K Studio Concert",
        description = "Recorded live at the Pioneer Square Acoustic Loft in 4K 60fps HDR with multi-track spatial audio mixing.\n\nTIMESTAMPS:\n00:00 - Introduction & Soundcheck\n03:20 - Midnight Espresso Groove\n09:45 - Rainy Avenue Saxophone Solo\n15:10 - Finale & Neighbor Q&A",
        creatorUsername = "dave_cinematics",
        creatorFullName = "David Kim Cinema",
        creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80",
        isCreatorVerified = true,
        videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1080&auto=format&fit=crop&q=85",
        category = "Music & Live Shows",
        durationSeconds = 1145,
        viewsCount = 48200,
        likesCount = 3940,
        isLiked = false,
        isSaved = false,
        isConnected = true,
        resolution = "4K Ultra HD • 60fps",
        tags = "#LiveMusic #4KConcert #LocaliiiyStudio #Jazz",
        chapters = "00:00 - Intro\n03:20 - Main Set\n09:45 - Solo\n15:10 - Finale",
        distanceKm = 1.4,
        creatorConnections = 14800,
        connectedCountInt = 14800
    ),
    StudioVideoEntity(
        id = 502L,
        title = "Inside the Neighborhood Artisan Roastery & Ceramic Craft Documentary",
        description = "A 4K local documentary exploring independent coffee roasters, ceramicists, and makers shaping our street culture.",
        creatorUsername = "elena_adventures",
        creatorFullName = "Elena Rostova",
        creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
        isCreatorVerified = true,
        videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=1080&auto=format&fit=crop&q=85",
        category = "Local Documentaries",
        durationSeconds = 860,
        viewsCount = 31900,
        likesCount = 2710,
        isLiked = true,
        isSaved = false,
        isConnected = false,
        resolution = "4K Ultra HD • 60fps",
        tags = "#Documentary #Artisans #LocalCulture #Cinema",
        chapters = "00:00 - The Roastery\n04:15 - Ceramic Wheel\n10:30 - Weekend Market",
        distanceKm = 2.6,
        creatorConnections = 9640,
        connectedCountInt = 9640
    ),
    StudioVideoEntity(
        id = 503L,
        title = "Next-Gen Spatial Audio & Indie Filmmaking Gear Breakdown (2026 Studio Rig)",
        description = "Complete walkthrough of our compact 4K mirrorless cinema setup, wireless lavalier routing, and color grading pipeline for hyperlocal creators.",
        creatorUsername = "sam_sound",
        creatorFullName = "Sam Rodriguez",
        creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80",
        isCreatorVerified = true,
        videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=1080&auto=format&fit=crop&q=85",
        category = "Tech Reviews",
        durationSeconds = 642,
        viewsCount = 22400,
        likesCount = 1890,
        isLiked = false,
        isSaved = true,
        isConnected = true,
        resolution = "4K Ultra HD",
        tags = "#TechReviews #Filmmaking #CameraGear #Studio",
        chapters = "00:00 - Camera Body\n03:00 - Audio Mixer\n07:45 - LUT Grading",
        distanceKm = 0.9,
        creatorConnections = 7420,
        connectedCountInt = 7420
    ),
    StudioVideoEntity(
        id = 504L,
        title = "Late Night Street Food Tour: Wood-Fired Sourdough & Night Market Specialties",
        description = "Visiting 5 hidden neighborhood street food stalls and night kitchens serving wood-fired flatbreads, handmade dumplings, and craft mocktails.",
        creatorUsername = "mayatravels",
        creatorFullName = "Maya Patel",
        creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80",
        isCreatorVerified = true,
        videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=1080&auto=format&fit=crop&q=85",
        category = "Street Food & Cooking",
        durationSeconds = 925,
        viewsCount = 64100,
        likesCount = 5420,
        isLiked = false,
        isSaved = false,
        isConnected = false,
        resolution = "4K Ultra HD • 60fps",
        tags = "#StreetFood #NightMarket #Foodie #LocalEats",
        chapters = "00:00 - Night Market Entrance\n02:45 - Wood-Fired Oven\n08:20 - Chef Interview",
        distanceKm = 1.8,
        creatorConnections = 19300,
        connectedCountInt = 19300
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    videos: List<StudioVideoEntity>,
    userProfile: UserProfileEntity,
    selectedCategory: String,
    studioSortOption: String = "Most Popular (Views)",
    onStudioSortChange: (String) -> Unit = {},
    studioScopeFilter: String = "ALL",
    onStudioScopeChange: (String) -> Unit = {},
    countryName: String? = null,
    searchQuery: String = "",
    activeVideo: StudioVideoEntity? = null,
    isPlaying: Boolean = false,
    playbackProgress: Float = 0f,
    isUploadSheetOpen: Boolean = false,
    showCreatorDashboard: Boolean = false,
    onCategorySelected: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onVideoClick: (StudioVideoEntity) -> Unit = {},
    onCloseVideo: () -> Unit = {},
    onTogglePlayPause: () -> Unit = {},
    onSeek: (Float) -> Unit = {},
    onLikeVideo: (StudioVideoEntity) -> Unit = {},
    onSaveVideo: (StudioVideoEntity) -> Unit = {},
    onConnectCreator: (String) -> Unit = {},
    onOpenUploadSheet: () -> Unit = {},
    onCloseUploadSheet: () -> Unit = {},
    onToggleCreatorDashboard: () -> Unit = {},
    onUploadVideo: (String, String, String, Int, String, String, String, String, String) -> Boolean = { _, _, _, _, _, _, _, _, _ -> true },
    onVideoViewed: (Long) -> Unit = {},
    onDeleteVideo: (Long) -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    creatorEarnings: CreatorEarningsSummary? = null,
    exclusiveTiers: List<CreatorExclusiveTier> = emptyList(),
    connectedTierIds: Set<String> = emptySet(),
    onUpdateTier: (CreatorExclusiveTier) -> Unit = {},
    onAddTier: (CreatorExclusiveTier) -> Unit = {},
    onToggleTierEnabled: (String, Boolean) -> Unit = { _, _ -> },
    onConnectTier: (CreatorExclusiveTier) -> Unit = {},
    isGhostSpectator: Boolean = false,
    onGhostActionPrompt: (String) -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {},
    onOpenLanguageCurrency: () -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val effectiveVideos = remember(videos) {
        if (videos.isNotEmpty()) videos else defaultStudioVideos
    }

    var selectedVideoForMenu by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showTipDialog by remember { mutableStateOf(false) }
    var showTiersDialog by remember { mutableStateOf(false) }
    var showDeleteVideoConfirmDialog by remember { mutableStateOf(false) }
    var videoToDeleteId by remember { mutableStateOf<Long?>(null) }

    var isVideoPreviewOnScrollEnabled by remember { mutableStateOf(true) }
    var manualPreviewVideoId by remember { mutableStateOf<Long?>(null) }
    var isPreviewMuted by remember { mutableStateOf(true) }

    var miniPlayerVideo by remember { mutableStateOf<StudioVideoEntity?>(null) }
    var isMiniPlayerPlaying by remember { mutableStateOf(true) }
    var isMiniPlayerMuted by remember { mutableStateOf(false) }

    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }

    val sortOptions = remember {
        listOf(
            "Most Popular (Views)",
            "Newest Upload",
            "Duration: Longest",
            "Duration: Shortest",
            "Top Rated (Likes)",
            "Creator Connections"
        )
    }

    val filteredVideos = remember(effectiveVideos, selectedCategory, searchQuery, studioScopeFilter, studioSortOption) {
        val base = effectiveVideos.filter { video ->
            val matchesCat = selectedCategory == "All" ||
                video.category.equals(selectedCategory, ignoreCase = true) ||
                (selectedCategory == "Tech Reviews" && video.category.contains("Tech", ignoreCase = true)) ||
                (selectedCategory == "Local Documentaries" && video.category.contains("Documentar", ignoreCase = true)) ||
                (selectedCategory == "Street Food & Cooking" && video.category.contains("Food", ignoreCase = true)) ||
                (selectedCategory == "Music & Live Shows" && video.category.contains("Music", ignoreCase = true))
            val matchesSearch = searchQuery.isBlank() ||
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.description.contains(searchQuery, ignoreCase = true) ||
                video.creatorFullName.contains(searchQuery, ignoreCase = true) ||
                video.creatorUsername.contains(searchQuery, ignoreCase = true) ||
                video.tags.contains(searchQuery, ignoreCase = true)
            val matchesScope = when (studioScopeFilter.uppercase()) {
                "ALL", "EARTH" -> true
                "1KM" -> video.distanceKm <= 1.0
                "3KM", "NEIGHBORHOOD (3KM)" -> video.distanceKm <= 3.0
                "5KM" -> video.distanceKm <= 5.0
                "10KM", "NEARBY" -> video.distanceKm <= 10.0
                "25KM" -> video.distanceKm <= 25.0
                "50KM" -> video.distanceKm <= 50.0
                "CONNECTED" -> video.isConnected
                else -> true
            }
            matchesCat && matchesSearch && matchesScope
        }
        when (studioSortOption) {
            "Newest Upload", "NEWEST" -> base.sortedByDescending { it.timestamp }
            "Duration: Longest" -> base.sortedByDescending { it.durationSeconds }
            "Duration: Shortest" -> base.sortedBy { it.durationSeconds }
            "Top Rated (Likes)" -> base.sortedByDescending { it.likesCount }
            "Creator Connections", "Creator Connected" -> base.sortedByDescending { it.connectedCountInt }
            else -> base.sortedByDescending { it.viewsCount }
        }
    }

    val listState = rememberLazyListState()
    val visibleVideoId by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstOrNull { info ->
                info.key is Long
            }?.key as? Long
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("studio_screen")
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .testTag("studio_pull_to_refresh_box")
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 110.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("studio_feed_list")
            ) {
                // 1. Studio Header
                item {
                    StudioHeader(
                        userProfile = userProfile,
                        showCreatorDashboard = showCreatorDashboard,
                        onToggleDashboard = onToggleCreatorDashboard,
                        onOpenUpload = {
                            if (isGhostSpectator) {
                                onGhostActionPrompt("upload videos to Localiiiy Studio")
                            } else {
                                onOpenUploadSheet()
                            }
                        },
                        onOpenLanguageCurrency = onOpenLanguageCurrency
                    )
                }

                // 2. Studio Search Bar
                item {
                    StudioSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange
                    )
                }

                // 3. Studio Category Navigation Bar (21 Categories)
                item {
                    StudioCategoryNavigationBar(
                        categories = StudioCategories,
                        selectedCategory = selectedCategory,
                        onSelect = onCategorySelected
                    )
                }

                // 4. Sort, Scope & Playback Options Controls
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Sort Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Sort:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(sortOptions) { option ->
                                    val isSelected = studioSortOption == option ||
                                        (studioSortOption == "TRENDING" && option == "Most Popular (Views)")
                                    val emoji = when (option) {
                                        "Most Popular (Views)" -> "🔥"
                                        "Newest Upload" -> "🕒"
                                        "Duration: Longest" -> "⏳"
                                        "Duration: Shortest" -> "⚡"
                                        "Top Rated (Likes)" -> "👌"
                                        "Creator Connections" -> "👥"
                                        else -> "🎬"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable { onStudioSortChange(option) }
                                            .testTag("studio_sort_${option.lowercase().replace(" ", "_")}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(emoji, fontSize = 11.sp)
                                            Text(
                                                text = option,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Scope Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Scope:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                item {
                                    val isAll = studioScopeFilter.equals("ALL", ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isAll) Color(0xFF8B5CF6).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isAll) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable { onStudioScopeChange("ALL") }
                                            .testTag("studio_scope_all")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("🌐", fontSize = 11.sp)
                                            Text(
                                                text = "All Locations",
                                                fontSize = 11.sp,
                                                fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isAll) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                items(systematicOptions) { opt ->
                                    val isSelected = studioScopeFilter.equals(opt.key, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable { onStudioScopeChange(opt.key) }
                                            .testTag("studio_scope_${opt.key.lowercase()}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(opt.icon, fontSize = 11.sp)
                                            Text(
                                                text = opt.shortLabel,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Options Row (Video Preview, Mute, Mini Player)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Options:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isVideoPreviewOnScrollEnabled) Color(0xFF10B981).copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isVideoPreviewOnScrollEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable { isVideoPreviewOnScrollEnabled = !isVideoPreviewOnScrollEnabled }
                                            .testTag("studio_toggle_video_preview")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isVideoPreviewOnScrollEnabled) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                                                contentDescription = null,
                                                tint = if (isVideoPreviewOnScrollEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = if (isVideoPreviewOnScrollEnabled) "Video Preview: ON" else "Video Preview: OFF",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isVideoPreviewOnScrollEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable { isPreviewMuted = !isPreviewMuted }
                                            .testTag("studio_toggle_preview_mute")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isPreviewMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = if (isPreviewMuted) "Muted" else "Sound On",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                item {
                                    val isMiniActive = miniPlayerVideo != null
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isMiniActive) Color(0xFF00E5FF).copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isMiniActive) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable {
                                                if (miniPlayerVideo != null) {
                                                    miniPlayerVideo = null
                                                } else {
                                                    miniPlayerVideo = filteredVideos.firstOrNull()
                                                    isMiniPlayerPlaying = true
                                                }
                                            }
                                            .testTag("studio_mini_player_toggle")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PictureInPictureAlt,
                                                contentDescription = null,
                                                tint = if (isMiniActive) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = if (isMiniActive) "Mini Player: Active" else "Mini Player",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isMiniActive) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Creator Studio Dashboard Panel (Collapsible)
                if (showCreatorDashboard) {
                    item {
                        CreatorStudioDashboardPanel(
                            userProfile = userProfile,
                            videos = effectiveVideos,
                            currentCurrency = currentCurrency,
                            creatorEarnings = creatorEarnings,
                            exclusiveTiers = exclusiveTiers,
                            onOpenUpload = {
                                if (isGhostSpectator) {
                                    onGhostActionPrompt("upload videos to Localiiiy Studio")
                                } else {
                                    onOpenUploadSheet()
                                }
                            },
                            onOpenMonetizationHub = onOpenMonetizationHub,
                            onOpenBoostAds = onOpenBoostAds,
                            onOpenTiersDialog = { showTiersDialog = true }
                        )
                    }
                }

                // 6. Studio Spotlight Hero (Featured Video)
                val spotlightVideo = filteredVideos.firstOrNull()
                if (spotlightVideo != null && selectedCategory == "All" && searchQuery.isBlank()) {
                    item {
                        StudioSpotlightHero(
                            video = spotlightVideo,
                            onClick = {
                                onVideoViewed(spotlightVideo.id)
                                onVideoClick(spotlightVideo)
                            }
                        )
                    }
                }

                // 7. Long-Form Creator Catalog Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedCategory == "All") "Long-Form Creator Catalog" else "$selectedCategory Videos",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${filteredVideos.size} full-length videos (60s – Unlimited)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HighQuality,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "4K / 1080p",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6)
                                )
                            }
                        }
                    }
                }

                // 8. Video Cards or Empty State
                if (filteredVideos.isEmpty()) {
                    item {
                        StudioEmptyState(
                            category = selectedCategory,
                            onResetFilter = {
                                onCategorySelected("All")
                                onSearchQueryChange("")
                            },
                            onUploadClick = {
                                if (isGhostSpectator) {
                                    onGhostActionPrompt("upload videos to Localiiiy Studio")
                                } else {
                                    onOpenUploadSheet()
                                }
                            }
                        )
                    }
                } else {
                    items(filteredVideos, key = { it.id }) { video ->
                        val isPreviewActive = (manualPreviewVideoId == video.id) ||
                            (manualPreviewVideoId == null && isVideoPreviewOnScrollEnabled && visibleVideoId == video.id)
                        StudioVideoCard(
                            video = video,
                            isPreviewActive = isPreviewActive,
                            isPreviewMuted = isPreviewMuted,
                            onToggleMute = { isPreviewMuted = !isPreviewMuted },
                            onStartPreview = { manualPreviewVideoId = video.id },
                            onStartMiniPlayer = {
                                miniPlayerVideo = video
                                isMiniPlayerPlaying = true
                            },
                            onClick = {
                                onVideoViewed(video.id)
                                onVideoClick(video)
                            },
                            onLikeClick = { onLikeVideo(video) },
                            onMenuClick = { selectedVideoForMenu = video }
                        )
                    }
                }
            }
        }

        // Docked Mini-Player Overlay
        if (miniPlayerVideo != null && activeVideo == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                StudioDockedMiniPlayer(
                    video = miniPlayerVideo!!,
                    isPlaying = isMiniPlayerPlaying,
                    isMuted = isMiniPlayerMuted,
                    playbackProgress = playbackProgress,
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

        // Video Card More Options Bottom Sheet / Dialog
        if (selectedVideoForMenu != null) {
            val menuVideo = selectedVideoForMenu!!
            AlertDialog(
                onDismissRequest = { selectedVideoForMenu = null },
                title = {
                    Text(
                        text = menuVideo.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = {
                                miniPlayerVideo = menuVideo
                                isMiniPlayerPlaying = true
                                selectedVideoForMenu = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PictureInPictureAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play in Docked Mini-Player", modifier = Modifier.weight(1f))
                        }
                        TextButton(
                            onClick = {
                                onSaveVideo(menuVideo)
                                selectedVideoForMenu = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (menuVideo.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (menuVideo.isSaved) "Remove from Saved Studio Videos" else "Save Video for Offline / Watchlist", modifier = Modifier.weight(1f))
                        }
                        TextButton(
                            onClick = {
                                selectedVideoForMenu = null
                                showReportDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Report Studio Video", color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                        }
                        if (menuVideo.creatorUsername == userProfile.username || menuVideo.creatorUsername == userProfile.studioUsername) {
                            TextButton(
                                onClick = {
                                    videoToDeleteId = menuVideo.id
                                    selectedVideoForMenu = null
                                    showDeleteVideoConfirmDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Studio Video", color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
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

        if (showDeleteVideoConfirmDialog && videoToDeleteId != null) {
            DeleteConfirmationDialog(
                title = "Delete Studio Video?",
                message = "Are you sure you want to permanently remove this video from Localiiiy Studio?",
                confirmText = "Delete",
                onConfirm = {
                    onDeleteVideo(videoToDeleteId!!)
                    showDeleteVideoConfirmDialog = false
                    videoToDeleteId = null
                },
                onDismiss = {
                    showDeleteVideoConfirmDialog = false
                    videoToDeleteId = null
                }
            )
        }

        if (showTiersDialog) {
            CreatorExclusiveTiersDialog(
                tiers = exclusiveTiers,
                currentCurrency = currentCurrency,
                isCreatorView = true,
                connectedTierIds = connectedTierIds,
                onUpdateTier = onUpdateTier,
                onAddTier = onAddTier,
                onToggleTierEnabled = onToggleTierEnabled,
                onConnectTier = onConnectTier,
                onDismiss = { showTiersDialog = false }
            )
        }

        if (activeVideo != null) {
            StudioPlayerModal(
                video = activeVideo,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                isLockedByTier = false,
                onUnlockTier = { showTiersDialog = true },
                isCreatorConnected = activeVideo.isConnected,
                onUserProfileClick = onUserProfileClick,
                onClose = onCloseVideo,
                onTogglePlayPause = onTogglePlayPause,
                onSeek = onSeek,
                onLike = { onLikeVideo(activeVideo) },
                onSave = { onSaveVideo(activeVideo) },
                onConnect = { onConnectCreator(activeVideo.creatorUsername) },
                onReport = { _ -> },
                onMinimizeToMiniPlayer = {
                    miniPlayerVideo = activeVideo
                    isMiniPlayerPlaying = true
                    onCloseVideo()
                },
                onOpenTip = { showTipDialog = true },
                allVideos = effectiveVideos,
                onSelectNextVideo = { next ->
                    onVideoViewed(next.id)
                    onVideoClick(next)
                }
            )
        }

        if (isUploadSheetOpen) {
            StudioUploadDialog(
                exclusiveTiers = exclusiveTiers,
                onDismiss = onCloseUploadSheet,
                onPublish = onUploadVideo
            )
        }
    }
}

@Composable
fun StudioHeader(
    userProfile: UserProfileEntity,
    showCreatorDashboard: Boolean,
    onToggleDashboard: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenLanguageCurrency: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocaliiiyAppLogoBadge(size = 32.dp)
            Text(
                text = "Localiiiy",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Surface(
                color = Color(0xFF8B5CF6),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "STUDIO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (showCreatorDashboard) Color(0xFF8B5CF6).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    1.dp,
                    if (showCreatorDashboard) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onToggleDashboard() }
                    .testTag("studio_dashboard_toggle")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Creator Dashboard",
                        tint = if (showCreatorDashboard) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Button(
                onClick = onOpenUpload,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("studio_upload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Upload",
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Upload",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StudioSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search long videos, podcasts, music, channels...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color(0xFF8B5CF6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_search_input")
                )
            }
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(22.dp)
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

@Composable
fun StudioCategoryNavigationBar(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("studio_category_navigation_bar")
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val icon = when {
                category == "All" -> Icons.Default.Explore
                category.contains("Music") || category.contains("ASMR") -> Icons.Default.MusicNote
                category.contains("Tech") || category.contains("AI") || category.contains("Science") -> Icons.Default.Devices
                category.contains("Gaming") -> Icons.Default.SportsEsports
                category.contains("Cinema") || category.contains("Documentaries") || category.contains("Animation") || category.contains("Anime") -> Icons.Default.Movie
                category.contains("Comedy") || category.contains("Dance") -> Icons.Default.TheaterComedy
                category.contains("Food") -> Icons.Default.Restaurant
                category.contains("News") -> Icons.Default.Article
                category.contains("DIY") || category.contains("Education") -> Icons.Default.School
                else -> Icons.Default.VideoLibrary
            }
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (isSelected) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { onSelect(category) }
                    .testTag("studio_chip_${category.lowercase().replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StudioCategoryChips(
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

@Composable
fun CreatorStudioDashboardPanel(
    userProfile: UserProfileEntity,
    videos: List<StudioVideoEntity>,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    creatorEarnings: CreatorEarningsSummary? = null,
    exclusiveTiers: List<CreatorExclusiveTier> = emptyList(),
    onOpenUpload: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenBoostAds: () -> Unit = {},
    onOpenTiersDialog: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("creator_studio_dashboard")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = userProfile.studioChannelName.ifBlank { "${userProfile.fullName.ifBlank { "Local Creator" }} Studio" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "@${userProfile.studioUsername.ifBlank { userProfile.username.ifBlank { "creator_studio" } }} • ${userProfile.studioConnectionsCount} Connected",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Monetization Active 🟢",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudioMetricCard(
                    title = "Watch Hours",
                    value = "1,840.2h",
                    delta = "+28.4%",
                    modifier = Modifier.weight(1f)
                )
                StudioMetricCard(
                    title = "Studio Views",
                    value = "192.4K",
                    delta = "+14.8%",
                    modifier = Modifier.weight(1f)
                )
                StudioMetricCard(
                    title = "Est. Earnings",
                    value = CurrencyHelper.format(creatorEarnings?.availableBalanceUSD ?: 642.80, currentCurrency),
                    delta = "55% RevShare",
                    modifier = Modifier.weight(1f)
                )
            }

            DualVelocityAnalyticsBreakdown()
            Guaranteed100ImpressionProgress()
            ContentPerformanceBenchmarkCard(
                postTitle = videos.firstOrNull()?.title ?: "Latest 4K Studio Dispatch"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenMonetizationHub,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("studio_manage_payouts_button")
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Monetization", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onOpenBoostAds,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("studio_boost_video_button")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Boost Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_exclusive_tiers_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("👑", fontSize = 15.sp)
                            Text("Exclusive Content Tiers", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text(
                            text = "${exclusiveTiers.sumOf { it.activeConnectionsCount }} active connections • 90% RevShare",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onOpenTiersDialog,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Define", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Publish Long-Form Creator Video", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "Duration: 60 seconds up to unlimited time with Media3 4K playback.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onOpenUpload,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("+ New Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StudioMetricCard(
    title: String,
    value: String,
    delta: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = delta, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        }
    }
}

@Composable
fun StudioSpotlightHero(
    video: StudioVideoEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
            .testTag("studio_hero_spotlight")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFF8B5CF6),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = "${video.resolution} • ${formatDuration(video.durationSeconds)}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.creatorFullName} • ${video.viewsFormatted} • ${video.category}",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StudioVideoCard(
    video: StudioVideoEntity,
    isPreviewActive: Boolean,
    isPreviewMuted: Boolean,
    onToggleMute: () -> Unit,
    onStartPreview: () -> Unit,
    onStartMiniPlayer: () -> Unit,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable { onClick() }
            .testTag("studio_video_card_${video.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
            ) {
                if (isPreviewActive) {
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
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clickable { onStartPreview() }
                            .testTag("card_preview_btn_${video.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Preview Video",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Text("Preview", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.78f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "${video.resolution} • ${formatDuration(video.durationSeconds)}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = video.creatorAvatar,
                        contentDescription = video.creatorFullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFF8B5CF6), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "@${video.creatorUsername}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (video.isCreatorVerified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Creator",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = "• ${video.viewsFormatted} • ${video.uploadDateFormatted}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    AnimatedLikeButton(
                        isLiked = video.isLiked,
                        onLikeClick = onLikeClick,
                        likesCount = video.likesCount,
                        showCount = true,
                        testTag = "studio_card_like_${video.id}"
                    )
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.size(32.dp)
                    ) {
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
}

@Composable
fun StudioPlayerModal(
    video: StudioVideoEntity,
    isPlaying: Boolean,
    playbackProgress: Float,
    isLockedByTier: Boolean = false,
    onUnlockTier: () -> Unit = {},
    isCreatorConnected: Boolean = false,
    onUserProfileClick: (String) -> Unit = {},
    onClose: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onConnect: () -> Unit,
    onReport: (String) -> Unit,
    onMinimizeToMiniPlayer: () -> Unit = {},
    onOpenTip: () -> Unit = {},
    allVideos: List<StudioVideoEntity> = emptyList(),
    onSelectNextVideo: (StudioVideoEntity) -> Unit = {}
) {
    val context = LocalContext.current
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(true) }
    var newCommentText by remember { mutableStateOf("") }
    var replyingToRemarkId by remember { mutableStateOf<Long?>(null) }
    var replyText by remember { mutableStateOf("") }
    var showShareDialog by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }
    var selectedReportReason by remember { mutableStateOf("Inappropriate Content") }
    var showBlockConfirmDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val remarksList = remember(video.id) {
        mutableStateListOf(
            StudioRemarkItem(
                id = 1L,
                authorName = "Elena Rostova",
                authorUsername = "elena_adventures",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                isVerified = true,
                text = "The 4K cinematic grading here is unreal! What lens were you using at 02:40? 🎥✨",
                timeAgo = "2h ago",
                likesCount = 24,
                replies = listOf(
                    StudioRemarkItem(
                        id = 11L,
                        authorName = video.creatorFullName,
                        authorUsername = video.creatorUsername,
                        authorAvatar = video.creatorAvatar,
                        isVerified = true,
                        text = "Sony 24-70mm GM II! Thank you Elena, appreciate you watching! 🙌",
                        timeAgo = "1h ago",
                        likesCount = 9
                    )
                )
            ),
            StudioRemarkItem(
                id = 2L,
                authorName = "David Kim",
                authorUsername = "dave_cinematics",
                authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                isVerified = true,
                text = "Spatial audio sounds incredible with headphones. This sets a new benchmark for creator studios 🔥",
                timeAgo = "4h ago",
                likesCount = 18
            ),
            StudioRemarkItem(
                id = 3L,
                authorName = "Sam Rodriguez",
                authorUsername = "sam_sound",
                authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
                isVerified = false,
                text = "Audio mixing is pristine. Super smooth playback with zero buffering!",
                timeAgo = "1d ago",
                likesCount = 11
            )
        )
    }

    BackHandler {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onClose()
    }

    Dialog(
        onDismissRequest = {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            onClose()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top 4K Video Player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color.Black)
                ) {
                    if (isLockedByTier) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("👑", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "This studio video is reserved for @${video.creatorUsername}'s connections.\nRequires Bronze Backer tier or higher.",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = onUnlockTier) {
                                Text("Join Tier & Unlock 🔓", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        StudioVideoPlayerComponent(
                            video = video,
                            onClose = {
                                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                onClose()
                            },
                            onMinimizeToMiniPlayer = onMinimizeToMiniPlayer,
                            onShareVideo = { showShareDialog = true },
                            onReportVideo = { showReportModal = true },
                            onBlockCreator = { showBlockConfirmDialog = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Details, Creator Bar, Description & Remarks
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${video.viewsFormatted} views • ${video.uploadDateFormatted} • ${video.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Creator Info & Connect Button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onUserProfileClick(video.creatorUsername) }
                            ) {
                                AsyncImage(
                                    model = video.creatorAvatar,
                                    contentDescription = video.creatorFullName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(video.creatorFullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (video.isCreatorVerified) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified Creator",
                                                tint = Color(0xFF00E5FF),
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
                            }
                            Button(
                                onClick = onConnect,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCreatorConnected) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF8B5CF6)
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = if (isCreatorConnected) "Connected" else "Connect",
                                    color = if (isCreatorConnected) MaterialTheme.colorScheme.onSurface else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Action Row (Like, Remarks, Save, Tip, Share, More)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedLikeButton(
                                isLiked = video.isLiked,
                                onLikeClick = onLike,
                                likesCount = video.likesCount,
                                showCount = true,
                                testTag = "studio_video_like_${video.id}"
                            )
                            CommentActionButton(
                                onClick = { showComments = !showComments },
                                commentsCount = remarksList.size,
                                showCount = true,
                                testTag = "studio_video_comments_${video.id}"
                            )
                            OutlinedButton(
                                onClick = onSave,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (video.isSaved) "Saved" else "Save", fontSize = 11.5.sp)
                            }
                            OutlinedButton(
                                onClick = onOpenTip,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("🪙 Tip", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { showShareDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_share_studio_video")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 11.5.sp)
                            }
                            Box {
                                IconButton(
                                    onClick = { showMoreMenu = true },
                                    modifier = Modifier.testTag("btn_more_menu_studio")
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                                }
                                DropdownMenu(
                                    expanded = showMoreMenu,
                                    onDismissRequest = { showMoreMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Block Creator") },
                                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            showBlockConfirmDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Report Video") },
                                        leadingIcon = { Icon(Icons.Default.Report, contentDescription = null) },
                                        onClick = {
                                            showMoreMenu = false
                                            showReportModal = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Description & Chapters
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Description & Chapters", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = if (isDescriptionExpanded) "Show Less ▲" else "...more ▼",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF8B5CF6),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = video.description,
                                    fontSize = 12.5.sp,
                                    maxLines = if (isDescriptionExpanded) 25 else 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isDescriptionExpanded && video.chapters.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("TIMESTAMPS:", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF00E5FF))
                                    Text(video.chapters, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Community Remarks Section
                    if (showComments) {
                        item {
                            Text(
                                text = "✍️ Community Remarks (${remarksList.size})",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                        item {
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
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        if (newCommentText.isNotBlank()) {
                                            remarksList.add(
                                                0,
                                                StudioRemarkItem(
                                                    id = System.currentTimeMillis(),
                                                    authorName = "You",
                                                    authorUsername = "local_creator",
                                                    authorAvatar = video.creatorAvatar,
                                                    text = newCommentText.trim(),
                                                    timeAgo = "Just now"
                                                )
                                            )
                                            newCommentText = ""
                                        }
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                                }
                            }
                        }
                        items(remarksList, key = { it.id }) { remark ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = remark.authorAvatar,
                                            contentDescription = remark.authorName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "@${remark.authorUsername} • ${remark.timeAgo}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(remark.text, fontSize = 12.5.sp)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = "👌 ${remark.likesCount}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Reply",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF8B5CF6),
                                            modifier = Modifier.clickable {
                                                replyingToRemarkId = if (replyingToRemarkId == remark.id) null else remark.id
                                            }
                                        )
                                    }
                                    if (replyingToRemarkId == remark.id) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = replyText,
                                                onValueChange = { replyText = it },
                                                placeholder = { Text("Reply to @${remark.authorUsername}...", fontSize = 11.sp) },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    if (replyText.isNotBlank()) {
                                                        replyText = ""
                                                        replyingToRemarkId = null
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Up Next Videos
                    val upNext = allVideos.filter { it.id != video.id }
                    if (upNext.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Up Next in Studio", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                        items(upNext, key = { "next_${it.id}" }) { nextVid ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectNextVideo(nextVid) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = nextVid.thumbnailUrl,
                                    contentDescription = nextVid.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(110.dp, 64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(nextVid.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("${nextVid.creatorFullName} • ${nextVid.viewsFormatted}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Share Studio Video", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            showShareDialog = false
                            val text = ShareHelper.buildShareText(
                                title = video.title,
                                itemType = "Studio Video",
                                id = video.id,
                                creatorHandle = video.creatorUsername,
                                caption = video.description,
                                location = "Localiiiy Studio"
                            )
                            ShareHelper.launchNativeShare(context, text, video.title, "Share Studio Video")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Share to External Apps")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showReportModal) {
        val reasons = listOf(
            "Inappropriate Content",
            "Spam or Misleading",
            "Copyright or Intellectual Property Violation",
            "Harassment or Bullying"
        )
        AlertDialog(
            onDismissRequest = { showReportModal = false },
            title = { Text("Report Studio Video", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    reasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReportReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedReportReason == reason,
                                onClick = { selectedReportReason = reason }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReport(selectedReportReason)
                        showReportModal = false
                    }
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            title = { Text("Block @${video.creatorUsername}?", fontWeight = FontWeight.Bold) },
            text = { Text("You will no longer see Studio videos or remarks from this creator.") },
            confirmButton = {
                Button(
                    onClick = {
                        showBlockConfirmDialog = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Block", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioUploadDialog(
    exclusiveTiers: List<CreatorExclusiveTier> = emptyList(),
    onDismiss: () -> Unit,
    onPublish: (String, String, String, Int, String, String, String, String, String) -> Boolean
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Music & Live Shows") }
    var detectedDurationText by remember { mutableStateOf("04:22") }
    var detectedDurationSeconds by remember { mutableIntStateOf(262) }
    var detectedResolution by remember { mutableStateOf("4K Ultra HD • 60fps") }
    var detectedAspectRatio by remember { mutableStateOf("16:9 Widescreen") }
    var videoUrl by remember { mutableStateOf("https://media.w3.org/2010/05/sintel/trailer.mp4") }
    var thumbnailUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80") }
    var tags by remember { mutableStateOf("#Localiiiy #Studio #Creator") }
    var chapters by remember { mutableStateOf("00:00 - Introduction\n01:30 - Main Segment\n03:45 - Conclusion") }

    var licensingConfig by remember { mutableStateOf(ContentLicensingConfig()) }
    var isOriginalCreationConfirmed by remember { mutableStateOf(true) }
    var selectedReachMode by remember { mutableStateOf(AudienceReachMode.EARTH_WAVE) }
    var isEvergreen by remember { mutableStateOf(true) }
    var isLocalCommentsOnly by remember { mutableStateOf(false) }
    var micVolume by remember { mutableFloatStateOf(0.85f) }
    var ambientVolume by remember { mutableFloatStateOf(0.35f) }
    var selectedAmbientTrack by remember { mutableStateOf("None") }
    var geotagPrecision by remember { mutableStateOf(GeotagPrecision.NEIGHBORHOOD) }
    var coCreators by remember { mutableStateOf(listOf<String>()) }
    var isScheduled by remember { mutableStateOf(false) }
    var selectedScheduleWindow by remember { mutableStateOf("12:30 PM (Lunch Pulse)") }
    var notifyConnections by remember { mutableStateOf(true) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            videoUrl = uri.toString()
        }
    }

    val thumbnailFrames = remember {
        listOf(
            "00:15" to "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&auto=format&fit=crop&q=80",
            "01:30" to "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=500&auto=format&fit=crop&q=80",
            "03:10" to "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80",
            "04:20" to "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=500&auto=format&fit=crop&q=80"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Localiiiy Studio Upload",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "Automated video inspection & verified distribution",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🎥", fontSize = 18.sp)
                                        Text("Video File & Inspection", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Button(
                                        onClick = {
                                            videoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                                    ) {
                                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Select Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "⏱️ $detectedDurationText • 📺 $detectedResolution • 📐 $detectedAspectRatio",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF00E5FF),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Editable Video Thumbnail Strip
                    item {
                        Text("Editable Video Thumbnail Strip", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(thumbnailFrames) { (timeLabel, frameUrl) ->
                                val isChosen = thumbnailUrl == frameUrl
                                Box(
                                    modifier = Modifier
                                        .size(110.dp, 66.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isChosen) 2.dp else 1.dp,
                                            color = if (isChosen) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { thumbnailUrl = frameUrl }
                                ) {
                                    AsyncImage(
                                        model = frameUrl,
                                        contentDescription = "Thumbnail $timeLabel",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = timeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Title, Category & Description
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Video Title *") },
                            placeholder = { Text("e.g., Seattle Music Festival 2026 - 4K Full Concert") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(StudioCategories.filter { it != "All" }) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description & Chapters") },
                            placeholder = { Text("Detailed story, gear used, and timestamps...") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Tags & Keywords") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Licensing & Original Creation Verification
                    item {
                        LicensingAndReuseRightsUploadSection(
                            config = licensingConfig,
                            onConfigChange = { licensingConfig = it }
                        )
                    }

                    // Advanced Distribution Settings
                    item {
                        Text("Advanced Distribution Settings", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        AudienceReachSelector(
                            selectedMode = selectedReachMode,
                            onModeSelected = { selectedReachMode = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ContentLongevityToggle(
                            isEvergreen = isEvergreen,
                            onToggle = { isEvergreen = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProximityGatedCommentsToggle(
                            isLocalOnly = isLocalCommentsOnly,
                            onToggle = { isLocalCommentsOnly = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MultiTrackAudioMixer(
                            micVolume = micVolume,
                            onMicVolumeChange = { micVolume = it },
                            ambientVolume = ambientVolume,
                            onAmbientVolumeChange = { ambientVolume = it },
                            selectedAmbientTrack = selectedAmbientTrack,
                            onSelectAmbientTrack = { selectedAmbientTrack = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GeotagPrecisionSelector(
                            selectedPrecision = geotagPrecision,
                            onSelect = { geotagPrecision = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CollaborativeCoAuthorSelector(
                            coCreators = coCreators,
                            onAddCoCreator = { coCreators = coCreators + it },
                            onRemoveCoCreator = { coCreators = coCreators - it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LocalTimezonePublishScheduler(
                            isScheduled = isScheduled,
                            onToggleScheduled = { isScheduled = it },
                            selectedWindow = selectedScheduleWindow,
                            onSelectWindow = { selectedScheduleWindow = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DirectConnectionBroadcastCheckbox(
                            notifyConnections = notifyConnections,
                            onToggle = { notifyConnections = it }
                        )
                    }
                }

                // Publish Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { "Localiiiy 4K Studio Feature" }
                            val ok = onPublish(
                                finalTitle,
                                description.ifBlank { "Uploaded via Localiiiy Studio 4K." },
                                selectedCategory,
                                detectedDurationSeconds,
                                videoUrl,
                                thumbnailUrl,
                                detectedResolution,
                                tags,
                                chapters
                            )
                            if (ok && isOriginalCreationConfirmed) {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish 4K Studio Video", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StudioEmptyState(
    category: String,
    onResetFilter: () -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Videos in $category",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Be the first creator to upload a long-form video (60s to unlimited time) to this category!",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onResetFilter) {
                Text("Show All")
            }
            Button(
                onClick = onUploadClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) {
                Text("+ Upload Long Video", color = Color.White)
            }
        }
    }
}

fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
