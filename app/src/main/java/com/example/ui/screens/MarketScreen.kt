package com.example.ui.screens
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.SmartToy
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Offset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocaliiiyStringKey
import com.example.util.LocalizationHelper
import com.example.ui.components.StandardMediaSelectorBottomSheet
import com.example.ui.components.feed.TactileTriDialFeedLens
import com.example.ui.components.market.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.MarketplaceItemEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.ui.components.RadarRadiusOption
import com.example.ui.components.RadarRadiusPresets
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.theme.LocaliiiyAccentMint
import androidx.compose.foundation.text.BasicTextField
import com.example.data.SellerCatalog
import com.example.util.LocationHelper

data class MarketCategoryItem(
    val name: String,
    val icon: ImageVector
)

val defaultSellerCatalogs = listOf(
    SellerCatalog(
        id = "cat_1",
        name = "West Coast Precision Cycles",
        sellerUsername = "pedal_power_sea",
        sellerFullName = "Marco Veloce",
        sellerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        badge = "Verified Workshop",
        bannerUrl = "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800&auto=format&fit=crop&q=80",
        category = "Bikes & Scooters",
        description = "Hand-built gravel, road, and commuter bikes tuned by certified race mechanics in Seattle.",
        rating = 4.9,
        totalItems = 6
    ),
    SellerCatalog(
        id = "cat_2",
        name = "Pacific Heritage Vintage Audio & Vinyl",
        sellerUsername = "analog_groove",
        sellerFullName = "Clara Kensington",
        sellerAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
        badge = "Vintage Curator",
        bannerUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
        category = "Electronics & Tech",
        description = "Restored Marantz, Technics turntables, McIntosh tube amps, and mint original vinyl pressings.",
        rating = 5.0,
        totalItems = 8
    ),
    SellerCatalog(
        id = "cat_3",
        name = "Emerald Coast Electric & Hybrid Mobility",
        sellerUsername = "emerald_fleet",
        sellerFullName = "Jordan Sterling",
        sellerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
        badge = "Top Dealer",
        bannerUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop&q=80",
        category = "Cars & Vehicles",
        description = "Certified pre-owned Tesla, Rivian, and European EV wagons with complete battery health audits.",
        rating = 4.9,
        totalItems = 5
    ),
    SellerCatalog(
        id = "cat_4",
        name = "Pike Sound Artisan Leather & Craft",
        sellerUsername = "artisan_crafts_co",
        sellerFullName = "Nora Lindqvist",
        sellerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
        badge = "Master Artisan",
        bannerUrl = "https://images.unsplash.com/photo-1473496169904-658ba7c44d8a?w=800&auto=format&fit=crop&q=80",
        category = "Fashion & Style",
        description = "Vegetable-tanned full-grain leather bags, minimalist wallets, and camera straps hand-stitched locally.",
        rating = 4.9,
        totalItems = 7
    )
)

val marketCategories = listOf(
    MarketCategoryItem("All", Icons.Default.Storefront),
    MarketCategoryItem("Cars & Vehicles", Icons.Default.DirectionsCar),
    MarketCategoryItem("Bikes & Scooters", Icons.Default.DirectionsBike),
    MarketCategoryItem("Lands & Plots", Icons.Default.Landscape),
    MarketCategoryItem("Mobiles & Tablets", Icons.Default.Smartphone),
    MarketCategoryItem("Electronics & Tech", Icons.Default.Tv),
    MarketCategoryItem("Jobs & Services", Icons.Default.Work),
    MarketCategoryItem("Furniture & Living", Icons.Default.Weekend),
    MarketCategoryItem("Fashion & Style", Icons.Default.Checkroom),
    MarketCategoryItem("Home & Garden", Icons.Default.Home),
    MarketCategoryItem("Farm & Fresh", Icons.Default.LocalFlorist),
    MarketCategoryItem("Borrow & Lend", Icons.Default.SwapHoriz),
    MarketCategoryItem("Barter & Trade", Icons.Default.SyncAlt),
    MarketCategoryItem("Art & Collectibles", Icons.Default.Palette),
    MarketCategoryItem("Books & Sports", Icons.Default.MenuBook),
    MarketCategoryItem("Pets & Pet Care", Icons.Default.Pets),
    MarketCategoryItem("Beauty & Care", Icons.Default.Spa)
)

val sampleMarketPhotos = listOf(
    "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&auto=format&fit=crop&q=80", // Car
    "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=600&auto=format&fit=crop&q=80", // Bike
    "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=600&auto=format&fit=crop&q=80", // Land / Plot
    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80", // Mobile
    "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80", // Camera / Tech
    "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600&auto=format&fit=crop&q=80", // Service / Craft
    "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&auto=format&fit=crop&q=80", // Furniture
    "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600&auto=format&fit=crop&q=80", // Fashion
    "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=600&auto=format&fit=crop&q=80", // Garden / Home
    "https://images.unsplash.com/photo-1528825871115-3581a5387919?w=600&auto=format&fit=crop&q=80", // Fresh food
    "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80", // Art
    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80"  // Gadget / Keyboard
)

enum class MarketSubTab {
    ALL,
    GOODS,
    SERVICES,
    CLIPS,
    CATALOGS,
    FLASH_DEALS,
    POSTS,
    WATCHLIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    items: List<MarketplaceItemEntity>,
    posts: List<PostEntity> = emptyList(),
    clips: List<ClipEntity> = emptyList(),
    selectedCategory: String,
    searchQuery: String,
    locationQuery: String = "",
    radiusFilterKm: Double?,
    selectedItem: MarketplaceItemEntity?,
    showSellDialog: Boolean,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLocationQueryChange: (String) -> Unit = {},
    onRadiusFilterChange: (Double?) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onCloseSellDialog: () -> Unit,
    onPublishItem: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit,
    onPublishBuySellPost: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onPublishBuySellClip: (title: String, desc: String, price: Double, category: String, condition: String, videoUrl: String, soundTitle: String?, loc: String?, landmark: String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onCloseDetailSheet: () -> Unit,
    onMessageSeller: (MarketplaceItemEntity) -> Unit,
    onToggleAvailability: (MarketplaceItemEntity) -> Unit,
    onFlagItem: (MarketplaceItemEntity, String) -> Unit = { _, _ -> },
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    sortOption: String = "Most Popular",
    onSortOptionChange: (String) -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    onOpenComments: (String, Long) -> Unit = { _, _ -> },
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    countryName: String? = null,
    onConvertClipToMarket: (clipId: Long, price: Double, condition: String, pickupSpot: String) -> Unit = { _, _, _, _ -> },
    isPremiumSubscribed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var activeSubTab by remember { mutableStateOf(MarketSubTab.GOODS) }
    var showLocationSearchExpanded by remember { mutableStateOf(false) }
    var showLocationPickerModal by remember { mutableStateOf(false) }
    var selectedLocationLabel by remember { mutableStateOf("Near Me") }
    var selectedSellerCatalogForDetail by remember { mutableStateOf<SellerCatalog?>(null) }

    val sortOptionsList = listOf("Most Popular", "Newest Upload", "Price: Low to High", "Price: High to Low", "Distance: Nearest")

    // Quick popular world location search presets
    val quickLocations = listOf(
        "Anywhere (Global)" to "🌍",
        "Seattle, WA" to "📍",
        "New York" to "🗽",
        "Tokyo" to "🗼",
        "London" to "🏰",
        "Paris" to "🗼",
        "San Francisco" to "🌉",
        "Berlin" to "🇩🇪",
        "Sydney" to "🇦🇺"
    )

    var hiddenItemIds by remember { mutableStateOf(setOf<Long>()) }
    var itemForVideoTour by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var itemToFlag by remember { mutableStateOf<MarketplaceItemEntity?>(null) }

    // Filter items based on category, keyword search, location search, and radius scale
    val filteredItems = remember(items, selectedCategory, searchQuery, locationQuery, radiusFilterKm, hiddenItemIds) {
        items.filter { item ->
            val notHidden = item.id !in hiddenItemIds
            val matchesCategory = selectedCategory == "All" ||
                    item.category.equals(selectedCategory, ignoreCase = true) ||
                    item.category.contains(selectedCategory, ignoreCase = true) ||
                    selectedCategory.contains(item.category, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    (item.landmark?.contains(searchQuery, ignoreCase = true) == true) ||
                    (item.location?.contains(searchQuery, ignoreCase = true) == true)
            val matchesLocation = locationQuery.isBlank() ||
                    locationQuery.equals("Anywhere (Global)", ignoreCase = true) ||
                    locationQuery.equals("Anywhere", ignoreCase = true) ||
                    locationQuery.equals("Global", ignoreCase = true) ||
                    (item.location?.contains(locationQuery, ignoreCase = true) == true) ||
                    (item.landmark?.contains(locationQuery, ignoreCase = true) == true) ||
                    (locationQuery.contains("New York", ignoreCase = true) && (item.location?.contains("New York", ignoreCase = true) == true || item.location?.contains("NY", ignoreCase = true) == true)) ||
                    (locationQuery.contains("Tokyo", ignoreCase = true) && (item.location?.contains("Tokyo", ignoreCase = true) == true || item.location?.contains("Japan", ignoreCase = true) == true)) ||
                    (locationQuery.contains("London", ignoreCase = true) && (item.location?.contains("London", ignoreCase = true) == true || item.location?.contains("UK", ignoreCase = true) == true)) ||
                    (locationQuery.contains("Paris", ignoreCase = true) && (item.location?.contains("Paris", ignoreCase = true) == true || item.location?.contains("France", ignoreCase = true) == true)) ||
                    (locationQuery.contains("Seattle", ignoreCase = true) && (item.location?.contains("Seattle", ignoreCase = true) == true || item.location?.contains("WA", ignoreCase = true) == true)) ||
                    (locationQuery.contains("San Francisco", ignoreCase = true) && (item.location?.contains("San Francisco", ignoreCase = true) == true || item.location?.contains("CA", ignoreCase = true) == true)) ||
                    (locationQuery.contains("Berlin", ignoreCase = true) && (item.location?.contains("Berlin", ignoreCase = true) == true || item.location?.contains("Germany", ignoreCase = true) == true)) ||
                    (locationQuery.contains("Sydney", ignoreCase = true) && (item.location?.contains("Sydney", ignoreCase = true) == true || item.location?.contains("Australia", ignoreCase = true) == true))

            val matchesRadius = radiusFilterKm == null || item.distanceKm <= radiusFilterKm
            notHidden && matchesCategory && matchesSearch && matchesLocation && matchesRadius
        }
    }

    val sortedItems = remember(filteredItems, sortOption) {
        when (sortOption) {
            "Most Popular" -> filteredItems.sortedByDescending { it.sellerReviewCount + (if (it.isSaved) 50 else 0) }
            "Newest Upload" -> filteredItems.sortedByDescending { it.timestamp }
            "Price: Low to High" -> filteredItems.sortedBy { it.price }
            "Price: High to Low" -> filteredItems.sortedByDescending { it.price }
            "Distance: Nearest" -> filteredItems.sortedBy { it.distanceKm }
            else -> filteredItems
        }
    }

    // Buy/Sell specific posts
    val buySellPosts = remember(posts, searchQuery, locationQuery) {
        posts.filter { post ->
            val matchesSearch = searchQuery.isBlank() || post.caption.contains(searchQuery, ignoreCase = true)
            val matchesLoc = locationQuery.isBlank() || locationQuery.equals("Anywhere (Global)", ignoreCase = true) ||
                    (post.location?.contains(locationQuery, ignoreCase = true) == true) ||
                    (post.landmark?.contains(locationQuery, ignoreCase = true) == true)
            val isMarketTheme = post.caption.contains("FOR SALE", ignoreCase = true) ||
                    post.caption.contains("BUY", ignoreCase = true) ||
                    post.caption.contains("SELL", ignoreCase = true) ||
                    post.caption.contains("$") ||
                    post.caption.contains("Market", ignoreCase = true)
            isMarketTheme && matchesSearch && matchesLoc
        }.ifEmpty { posts }
    }

    // Buy/Sell specific clips
    val buySellClips = remember(clips, searchQuery, locationQuery) {
        clips.filter { clip ->
            val matchesSearch = searchQuery.isBlank() || clip.caption.contains(searchQuery, ignoreCase = true)
            val matchesLoc = locationQuery.isBlank() || locationQuery.equals("Anywhere (Global)", ignoreCase = true) ||
                    (clip.location?.contains(locationQuery, ignoreCase = true) == true) ||
                    (clip.landmark?.contains(locationQuery, ignoreCase = true) == true)
            val isMarketTheme = clip.caption.contains("FOR SALE", ignoreCase = true) ||
                    clip.caption.contains("BUY", ignoreCase = true) ||
                    clip.caption.contains("SELL", ignoreCase = true) ||
                    clip.caption.contains("$") ||
                    clip.caption.contains("Market", ignoreCase = true)
            isMarketTheme && matchesSearch && matchesLoc
        }.ifEmpty { clips }
    }

    val savedItems = remember(items) { items.filter { it.isSaved } }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // UNIFIED HEADER, MERGED SEARCH & CLEAN FILTER STRIP
            // When in Market Clips mode, headers are dismissed to give 100% viewport visibility to 9:16 vertical video.
            if (activeSubTab != MarketSubTab.CLIPS) {
                // Row 1: Unified Single-Line Header (flush below status bar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Localiiiy Market",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // + List / Post button aligned on the same horizontal row
                    Button(
                        onClick = onOpenSellDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("market_sell_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "List or Post",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ List / Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    }
                }

                // Row 2: Merged Single-Line Search Bar [ 🔍 Search items, services, or sellers... | 📍 Near Me ▾ ]
                // Explicitly scoped to the Localiiiy Market database
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .height(46.dp)
                        .testTag("market_unified_search_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Market",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Search items, services, or sellers...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("market_search_input")
                        )

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // Divider between scoped text search and location segment
                        VerticalDivider(
                            modifier = Modifier
                                .height(22.dp)
                                .padding(horizontal = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Location Segment [ 📍 Near Me ▾ ] -> Tapping opens compact location picker modal
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedLocationLabel != "Near Me (Current GPS)" || locationQuery.isNotBlank())
                                MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showLocationPickerModal = true }
                                .testTag("market_location_segment_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = if (selectedLocationLabel != "Near Me (Current GPS)" || locationQuery.isNotBlank())
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                val displayLoc = when {
                                    locationQuery.isNotBlank() -> locationQuery.take(9)
                                    selectedLocationLabel.isNotBlank() -> selectedLocationLabel.replace(" (Current GPS)", "").take(9)
                                    else -> "Near Me"
                                }
                                Text(
                                    text = displayLoc,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedLocationLabel != "Near Me (Current GPS)" || locationQuery.isNotBlank())
                                        MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change location",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // Row 3: Clean Filter Strip (Single horizontal scroll row)
                val filterStripTabs = listOf(
                    Pair(MarketSubTab.ALL, "All"),
                    Pair(MarketSubTab.GOODS, "🛍️ Goods"),
                    Pair(MarketSubTab.SERVICES, "🛠️ Services"),
                    Pair(MarketSubTab.CLIPS, "🎬 Market Clips"),
                    Pair(MarketSubTab.CATALOGS, "📚 Catalogs"),
                    Pair(MarketSubTab.FLASH_DEALS, "⚡ Flash Deals"),
                    Pair(MarketSubTab.POSTS, "📸 Posts"),
                    Pair(MarketSubTab.WATCHLIST, "🔖 Saved")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    filterStripTabs.forEach { (tab, label) ->
                        val isSelected = activeSubTab == tab
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { activeSubTab = tab }
                                .testTag("market_filter_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Row 4: Dynamic Sub-category Row (only appears when "Goods" or "All" is active, styled as subtle 32dp secondary line)
                if (activeSubTab == MarketSubTab.GOODS || activeSubTab == MarketSubTab.ALL) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .padding(bottom = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(marketCategories) { cat ->
                            val isSelected = selectedCategory == cat.name
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onSelectCategory(cat.name) }
                                    .testTag("market_subcat_${cat.name.replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = cat.name,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sub-Content Views with Pull-to-Refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                state = rememberPullToRefreshState(),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("market_pull_to_refresh_box")
            ) {
                when (activeSubTab) {
                    MarketSubTab.ALL, MarketSubTab.GOODS -> {
                        GoodsCatalogView(
                            filteredItems = sortedItems,
                            selectedCategory = selectedCategory,
                            radiusFilterKm = radiusFilterKm,
                            sortOption = sortOption,
                            onSortOptionChange = onSortOptionChange,
                            onSelectCategory = onSelectCategory,
                            onRadiusFilterChange = onRadiusFilterChange,
                            onItemClick = onItemClick,
                            onToggleSaveItem = onToggleSaveItem,
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments,
                            onPreviewVideoTour = { itemForVideoTour = it },
                            onFlagItem = { itemToFlag = it },
                            currentCurrency = currentCurrency,
                            countryName = countryName
                        )
                    }

                    MarketSubTab.SERVICES -> {
                        LocalServicesCatalogView(
                            onInquireService = { gig ->
                                val sampleItem = items.firstOrNull() ?: MarketplaceItemEntity(
                                    title = gig.title,
                                    price = 0.0,
                                    category = "Jobs & Services",
                                    condition = "Professional Service",
                                    imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600&auto=format&fit=crop&q=80",
                                    sellerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                                    sellerUsername = gig.providerName.lowercase().replace(" ", ""),
                                    sellerFullName = gig.providerName,
                                    description = "Gig inquiry for ${gig.title}",
                                    deliveryOption = "Local & Remote"
                                )
                                onMessageSeller(sampleItem)
                            }
                        )
                    }

                    MarketSubTab.CATALOGS -> {
                        SellerCatalogsCatalogView(
                            catalogs = defaultSellerCatalogs,
                            items = sortedItems,
                            onSelectCatalog = { catalog ->
                                selectedSellerCatalogForDetail = catalog
                            },
                            onItemClick = onItemClick,
                            currentCurrency = currentCurrency
                        )
                    }

                    MarketSubTab.FLASH_DEALS -> {
                        val flashDeals = remember(sortedItems) {
                            sortedItems.filter { it.isFlashDeal || it.description.contains("Urgent", ignoreCase = true) || it.price < 45.0 }
                        }
                        GoodsCatalogView(
                            filteredItems = flashDeals,
                            selectedCategory = selectedCategory,
                            radiusFilterKm = radiusFilterKm,
                            sortOption = sortOption,
                            onSortOptionChange = onSortOptionChange,
                            onSelectCategory = onSelectCategory,
                            onRadiusFilterChange = onRadiusFilterChange,
                            onItemClick = onItemClick,
                            onToggleSaveItem = onToggleSaveItem,
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments,
                            onPreviewVideoTour = { itemForVideoTour = it },
                            onFlagItem = { itemToFlag = it },
                            currentCurrency = currentCurrency,
                            countryName = countryName
                        )
                    }

                    MarketSubTab.POSTS -> {
                        MarketPostsFeedView(
                            posts = buySellPosts,
                            onItemClick = { post ->
                                val match = items.firstOrNull { it.title.contains(post.caption.take(15), ignoreCase = true) }
                                if (match != null) onItemClick(match)
                            },
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments
                        )
                    }

                    MarketSubTab.CLIPS -> {
                        MarketClipsFeedView(
                            clips = buySellClips,
                            items = items,
                            onNavigateToGoods = { goodsItem ->
                                activeSubTab = MarketSubTab.GOODS
                                onItemClick(goodsItem)
                            },
                            onConvertClipToMarket = onConvertClipToMarket,
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments,
                            onBackToGoods = { activeSubTab = MarketSubTab.GOODS },
                            onSelectCatalog = { catalog ->
                                selectedSellerCatalogForDetail = catalog
                            },
                            currentCurrency = currentCurrency
                        )
                    }

                    MarketSubTab.WATCHLIST -> {
                        WatchlistCatalogView(
                            savedItems = savedItems,
                            onItemClick = onItemClick,
                            onToggleSaveItem = onToggleSaveItem,
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments,
                            currentCurrency = currentCurrency
                        )
                    }
                }
            }
        }

        // Sell an Item Dialog / Sheet
        if (showSellDialog) {
            MarketSellMultiDialog(
                onDismiss = onCloseSellDialog,
                onPublishListing = onPublishItem,
                onPublishPost = onPublishBuySellPost,
                onPublishClip = onPublishBuySellClip,
                currentCurrency = currentCurrency
            )
        }

        // Item Details Bottom Sheet / Modal
        if (selectedItem != null) {
            MarketItemDetailDialog(
                item = selectedItem,
                posts = posts,
                clips = clips,
                onDismiss = onCloseDetailSheet,
                onMessageSeller = { onMessageSeller(selectedItem) },
                onToggleSave = { onToggleSaveItem(selectedItem) },
                onToggleAvailability = { onToggleAvailability(selectedItem) },
                onFlagItem = { id, reason ->
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onFlagItem(selectedItem, reason)
                    hiddenItemIds = hiddenItemIds + id
                    onCloseDetailSheet()
                },
                currentCurrency = currentCurrency,
                currentLanguage = currentLanguage,
                isPremiumSubscribed = isPremiumSubscribed,
                onUserProfileClick = onUserProfileClick,
                onCommentClick = { onOpenComments("MARKET", selectedItem.id) }
            )
        }

        // Section 4.9: Condition Video Tour Modal
        if (itemForVideoTour != null) {
            MarketConditionVideoTourModal(
                videoUrl = itemForVideoTour!!.imageUrl,
                title = itemForVideoTour!!.title,
                onDismiss = { itemForVideoTour = null }
            )
        }

        // Section 4.16: Flag Suspicious Listing Dialog
        if (itemToFlag != null) {
            FlagListingConfirmationDialog(
                itemTitle = itemToFlag!!.title,
                onConfirmFlag = { reason ->
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onFlagItem(itemToFlag!!, reason)
                    hiddenItemIds = hiddenItemIds + itemToFlag!!.id
                    itemToFlag = null
                },
                onDismiss = { itemToFlag = null }
            )
        }

        // Compact Location Picker Modal (GPS / Saved Cities / Distance Slider)
        if (showLocationPickerModal) {
            MarketLocationPickerModal(
                currentLocation = selectedLocationLabel,
                currentRadiusKm = radiusFilterKm ?: 10.0,
                onSelectLocation = { locLabel ->
                    selectedLocationLabel = locLabel
                    onLocationQueryChange(locLabel)
                },
                onRadiusChange = { radius ->
                    onRadiusFilterChange(radius)
                },
                onDismiss = { showLocationPickerModal = false }
            )
        }

        // Branded Seller Catalog Details Sheet
        if (selectedSellerCatalogForDetail != null) {
            SellerCatalogDetailDialog(
                catalog = selectedSellerCatalogForDetail!!,
                items = items,
                onDismiss = { selectedSellerCatalogForDetail = null },
                onItemClick = { item ->
                    selectedSellerCatalogForDetail = null
                    onItemClick(item)
                },
                onMessageSeller = { item ->
                    selectedSellerCatalogForDetail = null
                    onMessageSeller(item)
                },
                currentCurrency = currentCurrency
            )
        }
    }
}

@Composable
private fun GoodsCatalogView(
    filteredItems: List<MarketplaceItemEntity>,
    selectedCategory: String,
    radiusFilterKm: Double?,
    sortOption: String = "Most Popular",
    onSortOptionChange: (String) -> Unit = {},
    onSelectCategory: (String) -> Unit,
    onRadiusFilterChange: (Double?) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onOpenComments: (String, Long) -> Unit,
    onPreviewVideoTour: (MarketplaceItemEntity) -> Unit = {},
    onFlagItem: (MarketplaceItemEntity) -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    countryName: String? = null
) {
    val systematicOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("marketplace_grid")
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Radius Filter Chips (Hyperlocal up to Country & Earth)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Range:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Section 4.3: Walkable Deals (< 2 km) Bargain Radar
                        item {
                            val isWalkable = radiusFilterKm != null && radiusFilterKm <= 2.1
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isWalkable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isWalkable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable {
                                        if (isWalkable) {
                                            onRadiusFilterChange(null)
                                        } else {
                                            onRadiusFilterChange(2.0)
                                            onSortOptionChange("Distance: Nearest")
                                        }
                                    }
                                    .testTag("market_radius_walkable_2km")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text("🚶", fontSize = 10.sp)
                                    Text(
                                        text = "Walkable (< 2 km)",
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isWalkable) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isWalkable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            val isAllSelected = radiusFilterKm == null
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isAllSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onRadiusFilterChange(null) }
                                    .testTag("market_radius_all")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text("🌐", fontSize = 10.sp)
                                    Text(
                                        text = "Any Distance",
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isAllSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(systematicOptions) { opt ->
                            val isSelected = radiusFilterKm != null && (
                                    kotlin.math.abs(radiusFilterKm - opt.km) < 0.1 ||
                                    (opt.key == "COUNTRY" && radiusFilterKm == SystematicDistanceScale.COUNTRY_DEFAULT_KM) ||
                                    (opt.key == "EARTH" && radiusFilterKm == SystematicDistanceScale.EARTH_KM) ||
                                    (opt.key == "GALAXY" && radiusFilterKm == SystematicDistanceScale.GALAXY_KM)
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onRadiusFilterChange(opt.km) }
                                    .testTag("market_radius_${opt.key.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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

                Spacer(modifier = Modifier.height(6.dp))

                // Sort Option Chips
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sortList = listOf("Most Popular", "Newest Upload", "Price: Low to High", "Price: High to Low", "Distance: Nearest")
                        items(sortList) { sOpt ->
                            val isSelected = sortOption == sOpt
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onSortOptionChange(sOpt) }
                                    .testTag("market_sort_$sOpt")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = when (sOpt) {
                                            "Most Popular" -> "🔥"
                                            "Newest Upload" -> "🕒"
                                            "Price: Low to High" -> "💵"
                                            "Price: High to Low" -> "💎"
                                            "Distance: Nearest" -> "📍"
                                            else -> "🔀"
                                        },
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = sOpt,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Categories Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(marketCategories) { cat ->
                        val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectCategory(cat.name) }
                                .testTag("market_cat_${cat.name.replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section 4 Hyperlocal Safe Zone Banner
                VerifiedSafeExchangeSpotCard(
                    spotName = "Seattle Downtown Public Plaza & Safe Trade Station",
                    distanceMeters = 350,
                    hasCctv = true,
                    isOpen24h = true
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (filteredItems.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No goods found in this radius",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = onOpenSellDialog,
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("List a Product or Post")
                        }
                    }
                }
            }
        }

        items(filteredItems, key = { it.id }) { item ->
            MarketItemCard(
                item = item,
                onClick = { onItemClick(item) },
                onToggleSave = { onToggleSaveItem(item) },
                onPreviewVideoTour = { onPreviewVideoTour(item) },
                onFlagClick = { onFlagItem(item) },
                currentCurrency = currentCurrency
            )
        }
    }
}

@Composable
private fun MarketPostsFeedView(
    posts: List<PostEntity>,
    onItemClick: (PostEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onOpenComments: (String, Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("market_posts_list")
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Local Buy & Sell Posts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Photo listings & requests from verified neighbors",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenSellDialog,
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+ Add Post", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        items(posts, key = { it.id }) { post ->
            MarketPostCard(
                post = post,
                onClick = {  },
                onCommentClick = { onOpenComments("POST", post.id) }
            )
        }
    }
}

@Composable
private fun MarketPostCard(
    onCommentClick: () -> Unit,
    post: PostEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("market_post_card_${post.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Seller Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(post.userAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = post.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = post.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = "${post.landmark ?: post.location ?: "Locality"} • ${post.distanceKm} km",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "BUY / SELL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Photo with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Caption and quick actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                var isLiked by remember(post.id) { mutableStateOf(post.isLiked) }
                var likesCount by remember(post.id) { mutableStateOf(post.likesCount) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.ui.components.AnimatedLikeButton(
                        isLiked = isLiked,
                        onLikeClick = {
                            isLiked = !isLiked
                            likesCount += if (isLiked) 1 else -1
                        },
                        likesCount = likesCount,
                        showCount = true,
                        testTag = "market_post_like_${post.id}"
                    )

                    Button(
                        onClick = onCommentClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("✍️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Chat / Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCommentClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Remarks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MarketLocationPickerModal(
    currentLocation: String,
    currentRadiusKm: Double,
    onSelectLocation: (String) -> Unit,
    onRadiusChange: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var customCity by remember { mutableStateOf(currentLocation) }
    var sliderRadius by remember { mutableFloatStateOf(currentRadiusKm.toFloat().coerceIn(1f, 100f)) }

    val popularCities = listOf(
        "Current GPS (Near Me)" to "📍",
        "Downtown Seattle" to "☕",
        "Capitol Hill" to "🏳️‍🌈",
        "Ballard & Fremont" to "🌉",
        "Bellevue & Eastside" to "🏢",
        "Tacoma & South Sound" to "🌲",
        "Portland, OR" to "🌹",
        "Vancouver, BC" to "🇨🇦",
        "San Francisco, CA" to "🌁",
        "New York, NY" to "🗽",
        "Tokyo, JP" to "🗼",
        "London, UK" to "🏰"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .testTag("market_location_picker_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Market Discovery Radius",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Isolated strictly to Market listings",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Distance Range: ${sliderRadius.toInt()} km",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = sliderRadius,
                    onValueChange = {
                        sliderRadius = it
                        onRadiusChange(it.toDouble())
                    },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("market_radius_slider")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 km (Walking)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("25 km (Metro)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("100 km (Regional)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customCity,
                    onValueChange = { customCity = it },
                    placeholder = { Text("Search city, neighborhood, or postal code...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (customCity.isNotBlank()) {
                            IconButton(onClick = { customCity = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("location_picker_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Saved & Suggested Locations",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularCities) { (city, icon) ->
                        val isSelected = (city.startsWith("Current GPS") && customCity.isBlank()) ||
                                customCity.equals(city, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable {
                                    if (city.startsWith("Current GPS")) {
                                        customCity = ""
                                        onSelectLocation("")
                                    } else {
                                        customCity = city
                                        onSelectLocation(city)
                                    }
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(icon, fontSize = 11.sp)
                                Text(
                                    text = city,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSelectLocation(customCity)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("apply_location_btn")
                ) {
                    Text("Apply Discovery Location", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SellerCatalogsCatalogView(
    catalogs: List<SellerCatalog>,
    items: List<MarketplaceItemEntity>,
    onSelectCatalog: (SellerCatalog) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD
) {
    var selectedFilterCategory by remember { mutableStateOf("All") }
    val bulkFilterCategories = listOf("All", "Vintage", "Audio", "Vehicles", "Electronics", "Handmade", "Bikes")

    val filteredCatalogs = remember(catalogs, selectedFilterCategory) {
        if (selectedFilterCategory == "All") catalogs
        else catalogs.filter { it.category.contains(selectedFilterCategory, ignoreCase = true) || it.name.contains(selectedFilterCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_catalogs_view"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(
                    text = "Curated Seller Storefronts & Catalogs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Explore verified local vendors, artisans, and boutique spaces",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(bulkFilterCategories) { category ->
                        val isSelected = selectedFilterCategory == category
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { selectedFilterCategory = category }
                                .testTag("catalog_filter_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        items(filteredCatalogs) { catalog ->
            val catalogItems = remember(items, catalog.name) {
                items.filter { it.catalogName == catalog.name || it.category.equals(catalog.category, ignoreCase = true) }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onSelectCatalog(catalog) }
                    .testTag("catalog_card_${catalog.id}")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        AsyncImage(
                            model = catalog.bannerUrl,
                            contentDescription = catalog.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.75f)
                                        )
                                    )
                                )
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Black)
                                Text(
                                    text = catalog.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🤝 ${catalog.totalItems} Items",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = catalog.sellerAvatar,
                                contentDescription = catalog.sellerFullName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                            Column {
                                Text(
                                    text = catalog.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "By ${catalog.sellerFullName} • ⭐ ${catalog.rating}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = catalog.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (catalogItems.isNotEmpty()) {
                            Text(
                                text = "Featured Catalog Stock (${catalogItems.size} items)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(catalogItems.take(5)) { item ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clickable { onItemClick(item) }
                                    ) {
                                        Column {
                                            Box(modifier = Modifier.fillMaxWidth().height(65.dp)) {
                                                AsyncImage(
                                                    model = item.imageUrl,
                                                    contentDescription = item.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color.Black.copy(alpha = 0.7f),
                                                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                                                ) {
                                                    Text(
                                                        text = "$${item.price.toInt()}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFBBF24),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = item.title,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📍 Seattle Metro Hub",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Open Storefront ➔",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerCatalogDetailDialog(
    catalog: SellerCatalog,
    items: List<MarketplaceItemEntity>,
    onDismiss: () -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onMessageSeller: (MarketplaceItemEntity) -> Unit,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD
) {
    var isConnectedWithSeller by remember { mutableStateOf(false) }

    val catalogItems = remember(items, catalog.name) {
        items.filter { it.catalogName == catalog.name || it.category.equals(catalog.category, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("seller_catalog_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    AsyncImage(
                        model = catalog.bannerUrl,
                        contentDescription = catalog.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = catalog.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = catalog.sellerAvatar,
                            contentDescription = catalog.sellerFullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                        Column {
                            Text(
                                text = catalog.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "${catalog.sellerFullName} • ⭐ ${catalog.rating} (${catalog.totalItems} Active Listings)",
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = catalog.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isConnectedWithSeller = !isConnectedWithSeller },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnectedWithSeller) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Text(
                                text = if (isConnectedWithSeller) "🤝 Connected" else "🤝 Connect",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isConnectedWithSeller) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val firstItem = catalogItems.firstOrNull() ?: items.first()
                                onMessageSeller(firstItem)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Message Store", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Storefront Inventory (${catalogItems.size} items)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    catalogItems.chunked(2).forEach { rowPair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPair.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onItemClick(item)
                                        }
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                        ) {
                                            AsyncImage(
                                                model = item.imageUrl,
                                                contentDescription = item.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.Black.copy(alpha = 0.75f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = "$${item.price.toInt()}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFBBF24),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = item.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = item.condition,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketClipsFeedView(
    clips: List<ClipEntity>,
    items: List<MarketplaceItemEntity>,
    onNavigateToGoods: (MarketplaceItemEntity) -> Unit,
    onConvertClipToMarket: (Long, Double, String, String) -> Unit,
    onOpenSellDialog: () -> Unit,
    onOpenComments: (String, Long) -> Unit,
    onBackToGoods: () -> Unit = {},
    onSelectCatalog: (SellerCatalog) -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD
) {
    var isReelViewMode by remember { mutableStateOf(true) }
    var clipToConvert by remember { mutableStateOf<ClipEntity?>(null) }
    var expandedClipItem by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var showOfferDialogForItem by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var showLocationPinDialogForClip by remember { mutableStateOf<ClipEntity?>(null) }
    var savedClipIds by remember { mutableStateOf(setOf<Long>()) }

    if (clips.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🎬", fontSize = 48.sp)
                Text(
                    text = "No Market Video Pitches Yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Showcase your goods, products, and services with a dynamic video pitch!",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Button(
                    onClick = onOpenSellDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Pitch Item with Video", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else if (isReelViewMode) {
        val pagerState = rememberPagerState(pageCount = { clips.size })
        // Full-Bleed 9:16 Vertical Viewport (100% viewing area, headers dismissed)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black).testTag("market_clips_pager_container")) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().testTag("market_clips_pager")
            ) { page ->
                val clip = clips[page]
                val matchedGoods = remember(clip, items) {
                    items.firstOrNull {
                        it.imageUrl == clip.mediaUrl ||
                                it.title.contains(clip.caption.take(20), ignoreCase = true) ||
                                (it.sellerUsername.isNotBlank() && it.sellerUsername.equals(clip.username, ignoreCase = true))
                    } ?: MarketplaceItemEntity(
                        title = clip.caption.take(35).ifBlank { "Local Market Item" },
                        description = "${clip.caption}\n\n[Market Video Showcase by @${clip.username}]",
                        price = if (clip.marketPriceUSD > 0) clip.marketPriceUSD else 35.0,
                        category = "Merchandise",
                        sellerUsername = clip.username,
                        sellerFullName = clip.soundArtist.ifBlank { clip.username },
                        sellerAvatar = clip.userAvatar,
                        imageUrl = clip.mediaUrl,
                        deliveryOption = "Safe-Haven Handshake Escrow",
                        location = clip.location ?: "Safe-Haven Hub",
                        landmark = clip.landmark ?: "Civic Plaza CCTV Safe Zone",
                        distanceKm = clip.distanceKm ?: 0.5,
                        safeHavenHubName = clip.marketPickupSpot.ifBlank { "Civic Plaza Police Precinct (CCTV Zone)" }
                    )
                }

                var isPlaying by remember { mutableStateOf(true) }
                var showPlayPauseIcon by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable {
                            isPlaying = !isPlaying
                            showPlayPauseIcon = true
                        }
                ) {
                    // Full-Bleed Vertical Video / Image Surface
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(clip.mediaUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = clip.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim Gradients for legible text over video
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.55f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Play/Pause momentary indicator
                    if (showPlayPauseIcon) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(16.dp)
                            )
                        }
                    }

                    // Top Bar Header Overlay (Dedicated Back to Goods & Pitch Controls)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 8.dp, start = 12.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Slim Return to Goods button
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { onBackToGoods() }
                                .testTag("market_clips_back_to_goods_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Return to Goods",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Goods",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Category Pill
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🎬", fontSize = 12.sp)
                                Text(
                                    text = "Market Clips",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFFDE68A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Toggle View Mode (Reel vs Grid)
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { isReelViewMode = false }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.GridView, contentDescription = "Grid View", tint = Color.White, modifier = Modifier.size(13.dp))
                                    Text("Grid", fontSize = 11.sp, color = Color.White)
                                }
                            }

                            // Post Pitch Button
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.clickable { onOpenSellDialog() }
                            ) {
                                Text(
                                    text = "+ Pitch",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // STREAMLINED MARKETPLACE RIGHT-HAND RAIL (6 COMMERCE-FOCUSED ACTIONS)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 10.dp, bottom = 90.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Like / Upvote (Heart counter)
                        var isLiked by remember(clip.id) { mutableStateOf(clip.isLiked) }
                        var likesCount by remember(clip.id) { mutableStateOf(clip.likesCount) }
                        com.example.ui.components.AnimatedLikeButton(
                            isLiked = isLiked,
                            onLikeClick = {
                                isLiked = !isLiked
                                likesCount += if (isLiked) 1 else -1
                            },
                            likesCount = likesCount,
                            showCount = true,
                            symbolSize = 22.sp,
                            touchTargetSize = 42.dp,
                            labelColor = Color.White,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape),
                            testTag = "market_reel_like_${clip.id}"
                        )

                        // 2. Inquire / Chat (Chat bubble with seller)
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.55f),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onOpenComments("CLIP", clip.id) }
                                .testTag("market_reel_chat_${clip.id}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                                    contentDescription = "Inquire with seller",
                                    tint = Color.White,
                                    modifier = Modifier.size(19.dp)
                                )
                                Text(
                                    text = "${clip.commentsCount}",
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 3. Buy Now / Make Offer (Direct checkout / escrow bid trigger)
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.95f),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { showOfferDialogForItem = matchedGoods }
                                .testTag("market_reel_buy_offer_${clip.id}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = "Make Escrow Offer",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Offer",
                                    fontSize = 8.5.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // 4. Save Item (Bookmark)
                        val isSaved = savedClipIds.contains(clip.id)
                        Surface(
                            shape = CircleShape,
                            color = if (isSaved) Color(0xFFF59E0B) else Color.Black.copy(alpha = 0.55f),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable {
                                    savedClipIds = if (isSaved) savedClipIds - clip.id else savedClipIds + clip.id
                                }
                                .testTag("market_reel_save_${clip.id}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save Item",
                                    tint = if (isSaved) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // 5. Seller Mini-Avatar (Tap opens Seller Space & Catalog)
                        val matchingCatalog = defaultSellerCatalogs.firstOrNull {
                            it.sellerUsername.equals(clip.username, ignoreCase = true)
                        } ?: defaultSellerCatalogs.first()

                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(2.dp, Color(0xFFF59E0B)),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onSelectCatalog(matchingCatalog) }
                                .testTag("market_reel_seller_avatar_${clip.id}")
                        ) {
                            Box {
                                AsyncImage(
                                    model = clip.userAvatar,
                                    contentDescription = "Seller: ${clip.username}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF59E0B),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.padding(1.5.dp)
                                    )
                                }
                            }
                        }

                        // 6. Item Location Pin (Tap reveals distance and pickup point)
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.55f),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { showLocationPinDialogForClip = clip }
                                .testTag("market_reel_location_pin_${clip.id}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location & Distance",
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = "${clip.distanceKm ?: 0.5}k",
                                    fontSize = 8.5.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // BOTTOM PRODUCT CARD OVERLAY (MAX 20% HEIGHT)
                    // Slim frosted-glass strip:
                    // Left: Item Title, Verified Seller badge, and Price in bold accent color.
                    // Right: "View Details" or "Buy / Handshake" button.
                    // Expandable: Tap opens a half-sheet with full item description and seller catalog.
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.82f),
                        border = BorderStroke(1.2.dp, Color(0xFFF59E0B).copy(alpha = 0.85f)),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 68.dp, bottom = 12.dp)
                            .testTag("market_clip_bottom_product_overlay_${clip.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Top Row: Creator, Verified Badge, and Price
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "@${clip.username}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF10B981)
                                    ) {
                                        Text(
                                            text = "Verified Seller",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                val displayPrice = if (clip.marketPriceUSD > 0) clip.marketPriceUSD else matchedGoods.price
                                Text(
                                    text = "$${displayPrice.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFBBF24)
                                )
                            }

                            // Item Title
                            Text(
                                text = matchedGoods.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Action Buttons Row: View Details ▾ & Buy / Handshake ➔
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { expandedClipItem = matchedGoods },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .testTag("clip_view_details_btn_${clip.id}")
                                ) {
                                    Text(
                                        text = "View Details ▾",
                                        fontSize = 10.5.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = { onNavigateToGoods(matchedGoods) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(28.dp)
                                        .testTag("redirect_to_goods_btn_${clip.id}")
                                ) {
                                    Text(
                                        text = "Buy / Handshake ➔",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Expandable Product Half-Sheet Modal
        if (expandedClipItem != null) {
            val item = expandedClipItem!!
            val matchingCatalog = defaultSellerCatalogs.firstOrNull {
                it.sellerUsername.equals(item.sellerUsername, ignoreCase = true)
            } ?: defaultSellerCatalogs.first()

            Dialog(
                onDismissRequest = { expandedClipItem = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f)
                        .padding(top = 100.dp)
                        .testTag("clip_expanded_product_sheet")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(36.dp)
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { expandedClipItem = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        // Price & Condition
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${item.price.toInt()}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = item.condition,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        HorizontalDivider()

                        // Full Description
                        Text(
                            text = "Item Description",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Seller Space & Catalog Link
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedClipItem = null
                                    onSelectCatalog(matchingCatalog)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = matchingCatalog.sellerAvatar,
                                    contentDescription = matchingCatalog.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = matchingCatalog.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = "Curated Storefront • Tap to browse full catalog",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Escrow protection guarantee
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Safe-Haven Handshake Escrow: Funds released only after meetup inspection.",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // CTA Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    expandedClipItem = null
                                    onNavigateToGoods(item)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Text("View in Market", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    expandedClipItem = null
                                    showOfferDialogForItem = item
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Text("Make Offer", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Direct Escrow Offer Modal
        if (showOfferDialogForItem != null) {
            val item = showOfferDialogForItem!!
            var offerAmount by remember { mutableStateOf("${item.price.toInt()}") }
            var offerSuccess by remember { mutableStateOf(false) }

            Dialog(onDismissRequest = { showOfferDialogForItem = null }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🤝 Make Handshake Escrow Offer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Item: ${item.title}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (!offerSuccess) {
                            OutlinedTextField(
                                value = offerAmount,
                                onValueChange = { offerAmount = it },
                                label = { Text("Your Offer Amount ($)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Funds are securely placed into local Safe-Haven escrow and only released after physical handshake inspection.",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { showOfferDialogForItem = null }) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { offerSuccess = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Submit Offer", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ Offer of $$offerAmount sent to @${item.sellerUsername} with Escrow Hold!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Button(
                                onClick = { showOfferDialogForItem = null },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }

        // Location Pin Details Modal
        if (showLocationPinDialogForClip != null) {
            val clip = showLocationPinDialogForClip!!
            Dialog(onDismissRequest = { showLocationPinDialogForClip = null }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF34D399))
                            Text("Item Location & Safe-Haven", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text(
                            text = "📍 Spot: ${clip.location ?: "Nearby Safe-Haven Meetup"}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "📏 Distance: ${clip.distanceKm ?: 0.5} km from your current GPS location",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "🏛️ Recommended CCTV Safe Hub: ${clip.marketPickupSpot.ifBlank { "Civic Plaza Police Precinct (CCTV Zone)" }}",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = { showLocationPinDialogForClip = null },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Got it")
                        }
                    }
                }
            }
        }
    } else {
        // Grid View Option
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 88.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("market_clips_grid")
        ) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Market Video Clips & Pitches",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Short video showcases of items for sale",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = { isReelViewMode = true },
                                shape = RoundedCornerShape(100.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("🎬 Reels", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onOpenSellDialog,
                                shape = RoundedCornerShape(100.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+ Sell", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            items(clips, key = { it.id }) { clip ->
                val matchedGoods = remember(clip, items) {
                    items.firstOrNull {
                        it.imageUrl == clip.mediaUrl ||
                                it.title.contains(clip.caption.take(20), ignoreCase = true) ||
                                (it.sellerUsername.isNotBlank() && it.sellerUsername.equals(clip.username, ignoreCase = true))
                    } ?: MarketplaceItemEntity(
                        title = clip.caption.take(35).ifBlank { "Local Market Item" },
                        description = "${clip.caption}\n\n[Market Video Showcase by @${clip.username}]",
                        price = if (clip.marketPriceUSD > 0) clip.marketPriceUSD else 35.0,
                        category = "Merchandise",
                        sellerUsername = clip.username,
                        sellerFullName = clip.soundArtist.ifBlank { clip.username },
                        sellerAvatar = clip.userAvatar,
                        imageUrl = clip.mediaUrl,
                        deliveryOption = "Safe-Haven Handshake Escrow",
                        location = clip.location ?: "Safe-Haven Hub",
                        landmark = clip.landmark ?: "Civic Plaza CCTV Safe Zone",
                        distanceKm = clip.distanceKm ?: 0.5,
                        safeHavenHubName = clip.marketPickupSpot.ifBlank { "Civic Plaza Police Precinct (CCTV Zone)" }
                    )
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clickable { onNavigateToGoods(matchedGoods) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(clip.mediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = clip.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Scrim
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.2f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Top Price Tag
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFF59E0B),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            val price = if (clip.marketPriceUSD > 0) clip.marketPriceUSD else matchedGoods.price
                            Text(
                                text = "$${price.toInt()}",
                                fontSize = 11.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Bottom info & Redirect CTA
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "@${clip.username}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = clip.caption,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Button(
                                onClick = { onNavigateToGoods(matchedGoods) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.fillMaxWidth().height(26.dp)
                            ) {
                                Text(
                                    text = "View Goods ➔",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Convert Clip to Market Dialog
    if (clipToConvert != null) {
        val target = clipToConvert!!
        MarketConvertClipDialog(
            clip = target,
            onDismiss = { clipToConvert = null },
            onConfirm = { price, condition, hub ->
                onConvertClipToMarket(target.id, price, condition, hub)
                clipToConvert = null
            }
        )
    }
}

@Composable
fun MarketConvertClipDialog(
    clip: ClipEntity,
    onDismiss: () -> Unit,
    onConfirm: (price: Double, condition: String, hub: String) -> Unit
) {
    var priceText by remember { mutableStateOf(if (clip.marketPriceUSD > 0) "${clip.marketPriceUSD.toInt()}" else "45") }
    var selectedCondition by remember { mutableStateOf(clip.marketCondition.ifBlank { "Like New" }) }
    var selectedHub by remember { mutableStateOf(clip.marketPickupSpot.ifBlank { "Civic Plaza Police Precinct (CCTV Zone)" }) }

    val conditions = listOf("Brand New", "Like New", "Gently Used", "Fair Condition")
    val safeHubs = listOf(
        "Civic Plaza Police Precinct (CCTV Zone)",
        "Downtown Transit Hub Verified Safe Zone",
        "Public Library Front Foyer (Monitored)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🛍️", fontSize = 20.sp)
                Column {
                    Text("Convert Clip to Market Goods", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Direct Commercial Listing", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Clip: ${clip.caption.take(45)}...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Listing Price ($ USD)") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Item Condition", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    conditions.forEach { cond ->
                        FilterChip(
                            selected = selectedCondition == cond,
                            onClick = { selectedCondition = cond },
                            label = { Text(cond, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Safe-Haven Offline Exchange Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    safeHubs.forEach { hub ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedHub == hub) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedHub = hub }
                        ) {
                            Text(
                                text = "🛡️ $hub",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = if (selectedHub == hub) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 35.0
                    onConfirm(price, selectedCondition, selectedHub)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save / List to Market 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
private fun WatchlistCatalogView(
    savedItems: List<MarketplaceItemEntity>,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onOpenComments: (String, Long) -> Unit,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD
) {
    if (savedItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Your Watchlist is empty",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pin goods or posts you like to track them and negotiate with neighbors!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 88.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(savedItems, key = { it.id }) { item ->
                MarketItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onToggleSave = { onToggleSaveItem(item) },
                    currentCurrency = currentCurrency
                )
            }
        }
    }
}

@Composable
fun MarketItemCard(
    item: MarketplaceItemEntity,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    onPreviewVideoTour: () -> Unit = {},
    onFlagClick: () -> Unit = {},
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("market_item_card_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )

                // Distance Chip (Top Left)
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = LocationHelper.formatDistanceLabel(item.distanceKm),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Like / Save Button (Top Right)
                com.example.ui.components.AnimatedLikeButton(
                    isLiked = item.isSaved,
                    onLikeClick = onToggleSave,
                    symbolSize = 16.sp,
                    touchTargetSize = 28.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                    testTag = "pin_item_button_${item.id}"
                )

                // Flag Listing Button (Under Like Button)
                IconButton(
                    onClick = onFlagClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 38.dp, end = 6.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("flag_item_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.OutlinedFlag,
                        contentDescription = "Flag Item",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(12.dp)
                    )
                }

                // Section 4.9: 10s Video Inspection Tour Quick Chip
                if (item.id % 2L == 0L) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable(onClick = onPreviewVideoTour)
                            .testTag("market_card_tour_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = "Video Tour", tint = Color.White, modifier = Modifier.size(11.dp))
                            Text("10s Tour", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Availability badge if not available
                if (!item.isAvailable) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "HOLD / RESERVED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                
                // Price / Barter / Service Badge (Bottom Left)
                val isService = item.category.contains("Service", ignoreCase = true)
                val isBarter = !isService && (item.price == 0.0 || item.category.contains("Barter", ignoreCase = true))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBarter) Color(0xFF0284C7) else if (isService) Color(0xFF9C27B0) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    val priceText = when {
                        isBarter -> "🔄 Barter"
                        isService && item.price == 0.0 -> "💼 Free Consult"
                        isService -> "${CurrencyHelper.format(item.price, currentCurrency)}/hr"
                        item.price == 0.0 -> "Free"
                        else -> CurrencyHelper.format(item.price, currentCurrency)
                    }
                    Text(
                        text = priceText,
                        fontSize = if (priceText.length > 7) 11.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
// Condition Chip (Bottom Right)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = item.condition,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                // Dynamic Flash / Urgent / Borrow badges
                if (item.category.contains("Borrow", ignoreCase = true) || item.price == 0.0) {
                    BorrowAndLendBadge(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 6.dp)
                    )
                } else if (item.id % 3L == 0L) {
                    UrgentTodayTimerBadge(
                        hoursRemaining = ((item.id % 9) + 2).toInt(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 6.dp)
                    )
                }

                // Sold Ribbon Overlay if not available
                if (!item.isAvailable) {
                    ClaimedSoldRibbonOverlay()
                }
            }

            // Info Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = item.landmark ?: item.location,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Safe handshakes reputation snippet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ Safe Meetup Ready",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E676)
                    )
                    Text("•", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${(item.sellerReviewCount * 3) + 12} exchanges",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.sellerAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.sellerUsername,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = item.sellerUsername,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = item.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

enum class SellCreationFormat {
    CATALOG_ITEM,
    BUY_SELL_POST,
    SHOWCASE_CLIP
}

@Composable
fun MarketSellMultiDialog(
    onDismiss: () -> Unit,
    onPublishListing: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit,
    onPublishPost: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit,
    onPublishClip: (title: String, desc: String, price: Double, category: String, condition: String, videoUrl: String, soundTitle: String?, loc: String?, landmark: String?) -> Unit,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD
) {
    var creationFormat by remember { mutableStateOf(SellCreationFormat.BUY_SELL_POST) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var isBarterTrade by remember { mutableStateOf(false) }
    var seekingInExchange by remember { mutableStateOf("") }
    var connectionDiscountPercent by remember { mutableIntStateOf(0) }
    var isHandmadeArtisan by remember { mutableStateOf(false) }
    var isUrgentSale by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Cars & Vehicles") }
    var selectedCondition by remember { mutableStateOf("Like New") }
    var selectedPhotos by remember { mutableStateOf(sampleMarketPhotos.take(7)) }
    var selectedPhotoUrl by remember { mutableStateOf(sampleMarketPhotos.first()) }
    var clipDurationSeconds by remember { mutableIntStateOf(60) } // Capped at 300s (5:00 max)
    var selectedDelivery by remember { mutableStateOf("Local Meetup / Pickup") }
    var landmark by remember { mutableStateOf("Pike Place Market") }
    var acceptedPaymentMethods by remember { mutableStateOf(listOf("💵 Cash on Meetup", "📱 Digital Transfer")) }
    var soundTrack by remember { mutableStateOf("Original Audio • Marketplace Pitch") }
    var musicSearchQuery by remember { mutableStateOf("") }
    var showSoundPicker by remember { mutableStateOf(false) }
    var blastRadiusIndex by remember { mutableFloatStateOf(0f) }
    val blastRadiusLabels = listOf("Hyper-Local (5km)", "City-Wide (50km)", "National", "Global (Earth)")
    val blastRadiusDescriptions = listOf(
        "Guaranteed feed placement for nearby neighbors. Ideal for local sales.",
        "Expands reach to the entire metro area. Good for high-ticket items.",
        "Broad national visibility. Shipping recommended.",
        "Algorithmic bypass: Open distribution to the entire world network."
    )
    var showStandardMediaSelector by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }

    val storageMediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newUrls = uris.map { it.toString() }
            selectedPhotos = (selectedPhotos + newUrls).distinct()
            selectedPhotoUrl = selectedPhotos.first()
        }
    }

    val viralMusicTracks = listOf(
        "🔥 Seattle Summer Anthem • 2026 Viral Hit",
        "🌊 Pacific Chillwave • Trending #1 on Charts",
        "🎸 Pike Place Acoustic Bounce • Global Viral",
        "⚡ Neon Nights Electronic • Global Club Viral",
        "🌙 Midnight Sunset Lo-Fi • Chill Vibes Viral",
        "🎧 Urban Soundscape • Trending Neighborhood Remix"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("market_sell_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Add Buy / Sell Listing",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Choose whether to post a catalog item, photo post, or video clip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Creation Format Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        SellCreationFormat.BUY_SELL_POST to ("📸 Buy/Sell Post"),
                        SellCreationFormat.SHOWCASE_CLIP to ("🎥 Video Clip"),
                        SellCreationFormat.CATALOG_ITEM to ("🏷️ Catalog Item")
                    ).forEach { (format, label) ->
                        val isSelected = creationFormat == format
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { creationFormat = format }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Select Media Section
                if (creationFormat == SellCreationFormat.SHOWCASE_CLIP) {
                    // DEDICATED 9:16 VERTICAL VIDEO CLIP (NO 16:9, CAPPED AT 5 MINUTES)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. Dedicated 9:16 Vertical Video Clip",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "9:16 Reel • Max 5:00",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Enforced vertical format disclaimer
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Enforcing 9:16 vertical format. Standard 16:9 landscape video is disabled to guarantee full-bleed immersive playback in Localiiiy Clips.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // 9:16 Vertical Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .aspectRatio(9f / 16f)
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedPhotoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "9:16 Clip Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Top overlay badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                                    Text("9:16 REEL", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }

                            // Bottom duration overlay
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${clipDurationSeconds / 60}:${(clipDurationSeconds % 60).toString().padStart(2, '0')} / 5:00",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Video Duration Slider (Capped strictly at 5:00 minutes / 300s)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Clip Duration (Max 5 Minutes):", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${clipDurationSeconds / 60}m ${clipDurationSeconds % 60}s",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = clipDurationSeconds.toFloat(),
                                onValueChange = { clipDurationSeconds = it.toInt().coerceIn(10, 300) },
                                valueRange = 10f..300f,
                                steps = 28,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0:10 min", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Capped at 5:00 min max ⏱️", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Video Source Selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    storageMediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pick 9:16 Video 🎥", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showStandardMediaSelector = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Media Vault 📁", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                } else {
                    // MANDATORY 7-PHOTO MINIMUM FOR GOODS, CATALOG, AND POSTS
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. Item Photos (7 Minimum Required)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedPhotos.size >= 7) Color(0xFF00C853).copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "${selectedPhotos.size}/7 Photos",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedPhotos.size >= 7) Color(0xFF00C853) else MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // 7 Photos Requirement Status Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedPhotos.size >= 7)
                                Color(0xFF00C853).copy(alpha = 0.12f)
                            else
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.dp,
                                if (selectedPhotos.size >= 7) Color(0xFF00C853).copy(alpha = 0.4f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedPhotos.size >= 7) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (selectedPhotos.size >= 7) Color(0xFF00C853) else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (selectedPhotos.size >= 7)
                                        "✓ Minimum 7-Photo Requirement Met (${selectedPhotos.size} uploaded). Comprehensive photos build buyer confidence."
                                    else
                                        "⚠️ Mandatory Minimum 7 Photos Required (${selectedPhotos.size}/7 uploaded). Please add at least ${7 - selectedPhotos.size} more angles (Front, Back, Side, Top, Detail, Label, Defect/Wear).",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedPhotos.size >= 7) Color(0xFF00C853) else MaterialTheme.colorScheme.onErrorContainer,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Active Selected Main Photo Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedPhotoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected media",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                            ) {
                                Text(
                                    text = "Cover Photo • Slot ${selectedPhotos.indexOf(selectedPhotoUrl) + 1}",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Multi-Photo Carousel with Slot Numbers
                        Text("Uploaded Photo Slots (Tap to preview as cover):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            itemsIndexed(selectedPhotos) { index, photo ->
                                val isCover = selectedPhotoUrl == photo
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            2.dp,
                                            if (isCover) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedPhotoUrl = photo }
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(photo)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Photo ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(bottomEnd = 6.dp),
                                        color = Color.Black.copy(alpha = 0.7f),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                    if (selectedPhotos.size > 7) {
                                        IconButton(
                                            onClick = {
                                                val updated = selectedPhotos.toMutableList().apply { removeAt(index) }
                                                selectedPhotos = updated
                                                if (selectedPhotoUrl == photo) {
                                                    selectedPhotoUrl = updated.firstOrNull() ?: ""
                                                }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(18.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Action Buttons: Multi-Photo Picker & Quick Auto-Fill Angles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    storageMediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Photos (${selectedPhotos.size}/7) 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (selectedPhotos.size < 7) {
                                Button(
                                    onClick = {
                                        val combined = (selectedPhotos + sampleMarketPhotos).distinct().take(7)
                                        selectedPhotos = combined
                                        selectedPhotoUrl = combined.first()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Fill 7 Angles ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                    }
                }

                if (showStandardMediaSelector) {
                    StandardMediaSelectorBottomSheet(
                        onDismiss = { showStandardMediaSelector = false },
                        onMediaSelected = { selectedList ->
                            if (selectedList.isNotEmpty()) {
                                if (creationFormat == SellCreationFormat.SHOWCASE_CLIP) {
                                    selectedPhotoUrl = selectedList.first()
                                } else {
                                    selectedPhotos = (selectedPhotos + selectedList).distinct()
                                    selectedPhotoUrl = selectedPhotos.first()
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & Price / Barter Mode
                Text(
                    text = "2. Title & Valuation / Barter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Mode Toggle: Fixed Price vs Barter / Skill Trade
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isBarterTrade) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isBarterTrade = false }
                            .testTag("sell_mode_price")
                    ) {
                        Text(
                            text = "💰 Fixed Price",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isBarterTrade) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isBarterTrade) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                isBarterTrade = true
                                selectedCategory = "Barter & Trade"
                            }
                            .testTag("sell_mode_barter")
                    ) {
                        Text(
                            text = "🔄 Barter / Skill Trade",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBarterTrade) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item / Skill Name (e.g., Sony A7III or Studio Sound Mixing)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sell_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isBarterTrade) {
                    OutlinedTextField(
                        value = seekingInExchange,
                        onValueChange = { seekingInExchange = it },
                        label = { Text("Seeking in Exchange (Gear, Studio Time, Skills)") },
                        placeholder = { Text("e.g. Willing to trade for video editing, audio mastering, or guitar lessons") },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_barter_seeking_input")
                    )
                } else {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price in ${currentCurrency.code} (${currentCurrency.symbol})") },
                        leadingIcon = { Text(currentCurrency.symbol, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_price_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Connection Discount Chips (Section 4.15)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🤝 Connected Discount:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(0 to "None", 10 to "10% off", 15 to "15% off", 20 to "20% off").forEach { (pct, lbl) ->
                            val isSel = connectionDiscountPercent == pct
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { connectionDiscountPercent = pct }
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Creator Badges & Urgent Flash Sale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isHandmadeArtisan,
                        onClick = { isHandmadeArtisan = !isHandmadeArtisan },
                        label = { Text("🎨 Handmade / Artisan", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isUrgentSale,
                        onClick = { isUrgentSale = !isUrgentSale },
                        label = { Text("⚡ Urgent (12h Flash)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                Text(
                    text = "3. Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(marketCategories.filter { it.name != "All" }) { cat ->
                        val isSelected = selectedCategory == cat.name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedCategory = cat.name }
                        ) {
                            Text(
                                text = cat.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Condition Selector
                Text(
                    text = "4. Condition",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Brand New", "Like New", "Good", "Fair").forEach { cond ->
                        val isSelected = selectedCondition == cond
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { selectedCondition = cond }
                        ) {
                            Text(
                                text = cond,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Location / Landmark
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Local Landmark / Neighborhood (Required)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sell_location_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accepted Settlement Methods (Section 4.14)
                Text(
                    text = "Accepted Settlement Methods",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                PaymentMethodChipsRow(
                    selectedMethods = acceptedPaymentMethods,
                    onToggleMethod = { method ->
                        acceptedPaymentMethods = if (acceptedPaymentMethods.contains(method)) {
                            if (acceptedPaymentMethods.size > 1) acceptedPaymentMethods - method else acceptedPaymentMethods
                        } else {
                            acceptedPaymentMethods + method
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (creationFormat == SellCreationFormat.SHOWCASE_CLIP || creationFormat == SellCreationFormat.BUY_SELL_POST) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSoundPicker = !showSoundPicker }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Music", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = soundTrack, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(text = "Tap to search or pick viral music track", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }

                    if (showSoundPicker) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎵 Search Music & Viral Tracks", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                            OutlinedTextField(
                                value = musicSearchQuery,
                                onValueChange = { musicSearchQuery = it },
                                placeholder = { Text("Search music or Google audio...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (musicSearchQuery.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            soundTrack = "$musicSearchQuery • Custom Audio"
                                            showSoundPicker = false
                                            musicSearchQuery = ""
                                        }
                                ) {
                                    Text(
                                        text = "🔍 Use Custom: \"$musicSearchQuery\"",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            val filteredTracks = if (musicSearchQuery.isBlank()) viralMusicTracks else viralMusicTracks.filter { it.contains(musicSearchQuery, ignoreCase = true) }
                            filteredTracks.forEach { track ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (soundTrack == track) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            soundTrack = track
                                            showSoundPicker = false
                                        }
                                ) {
                                    Text(
                                        text = track,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (soundTrack == track) FontWeight.Bold else FontWeight.Normal),
                                        color = if (soundTrack == track) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Description") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sell_desc_input")
                )

                // Reach / Blast Radius Selector (Algorithmic Bypass)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = "Reach", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Distribution Reach: ${blastRadiusLabels[blastRadiusIndex.toInt()]}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = blastRadiusDescriptions[blastRadiusIndex.toInt()],
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = blastRadiusIndex,
                            onValueChange = { blastRadiusIndex = it },
                            valueRange = 0f..3f,
                            steps = 2,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiary,
                                inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Local", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                            Text("Earth", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Publish Button Validation (7 Photos minimum for non-clips, dedicated 9:16 vertical capped at 5:00 max for clips)
                val hasRequiredMedia = if (creationFormat == SellCreationFormat.SHOWCASE_CLIP) {
                    selectedPhotoUrl.isNotBlank() && clipDurationSeconds <= 300
                } else {
                    selectedPhotos.size >= 7
                }
                val isFormValid = title.isNotBlank() && (isBarterTrade || (priceText.toDoubleOrNull() ?: 0.0) >= 0.0) && hasRequiredMedia

                Button(
                    onClick = {
                        val rawPrice = if (isBarterTrade) 0.0 else (priceText.toDoubleOrNull() ?: 0.0)
                        val parsedPrice = CurrencyHelper.convertToUSD(rawPrice, currentCurrency)
                        val finalCategory = if (isBarterTrade && selectedCategory == "Cars & Vehicles") "Barter & Trade" else selectedCategory
                        val barterInfo = if (isBarterTrade && seekingInExchange.isNotBlank()) "\n[🔄 Seeking in Exchange: $seekingInExchange]" else ""
                        val discountInfo = if (!isBarterTrade && connectionDiscountPercent > 0) "\n[🤝 $connectionDiscountPercent% Discount for Connected Neighbors]" else ""
                        val artisanInfo = if (isHandmadeArtisan) "\n[🎨 Handmade by Creator / Artisan]" else ""
                        val urgentInfo = if (isUrgentSale) "\n[⚡ Urgent Sale Today]" else ""
                        val reachText = "\n[Broadcast Reach: ${blastRadiusLabels[blastRadiusIndex.toInt()]}]"
                        val paymentInfo = "\n[Settlement: ${acceptedPaymentMethods.joinToString(", ")}]"
                        val finalDesc = description.ifBlank { "Available for local meetup near $landmark on Localiiiy." } + barterInfo + discountInfo + artisanInfo + urgentInfo + reachText + paymentInfo
                        when (creationFormat) {
                            SellCreationFormat.CATALOG_ITEM -> {
                                onPublishListing(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    finalCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    selectedDelivery,
                                    "Seattle",
                                    landmark
                                )
                            }
                            SellCreationFormat.BUY_SELL_POST -> {
                                onPublishPost(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    finalCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    selectedDelivery,
                                    "Seattle",
                                    landmark
                                )
                            }
                            SellCreationFormat.SHOWCASE_CLIP -> {
                                onPublishClip(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    selectedCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    soundTrack,
                                    "Seattle",
                                    landmark
                                )
                            }
                        }
                        onDismiss()
                    },
                    enabled = isFormValid && !isPublishing,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_market_item_button")
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publishing Listing...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else if (creationFormat != SellCreationFormat.SHOWCASE_CLIP && selectedPhotos.size < 7) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload 7 Photos Minimum (${selectedPhotos.size}/7)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    } else {
                        Icon(
                            imageVector = when (creationFormat) {
                                SellCreationFormat.BUY_SELL_POST -> Icons.Default.PhotoCamera
                                SellCreationFormat.SHOWCASE_CLIP -> Icons.Default.Videocam
                                SellCreationFormat.CATALOG_ITEM -> Icons.Default.Storefront
                            },
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (creationFormat) {
                                SellCreationFormat.BUY_SELL_POST -> "Broadcast Buy / Sell Post (7 Photos ✓)"
                                SellCreationFormat.SHOWCASE_CLIP -> "Publish 9:16 Video Clip 🎥"
                                SellCreationFormat.CATALOG_ITEM -> "Publish to Localiiiy Market (7 Photos ✓)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketItemDetailDialog(
    item: MarketplaceItemEntity,
    posts: List<PostEntity> = emptyList(),
    clips: List<ClipEntity> = emptyList(),
    onDismiss: () -> Unit,
    onMessageSeller: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleAvailability: () -> Unit,
    onFlagItem: (Long, String) -> Unit = { _, _ -> },
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    isPremiumSubscribed: Boolean = false,
    onUserProfileClick: (String) -> Unit = {},
    onCommentClick: () -> Unit = {}
) {
    var offerAmount by remember(item, currentCurrency) {
        mutableStateOf("${CurrencyHelper.convertFromUSD(item.price, currentCurrency).toInt()}")
    }
    var showOfferDialog by remember { mutableStateOf(false) }
    var isGhostShieldActive by remember { mutableStateOf(false) }
    var showHandoverQR by remember { mutableStateOf(false) }
    var showVideoTourModal by remember { mutableStateOf(false) }
    var showFlagModal by remember { mutableStateOf(false) }
    var selectedInquiryQuestion by remember { mutableStateOf<String?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("market_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Media Header with controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    // Pin / Save Button
                    // Like / Pin Button (Top Right)
                    com.example.ui.components.AnimatedLikeButton(
                        isLiked = item.isSaved,
                        onLikeClick = onToggleSave,
                        symbolSize = 20.sp,
                        touchTargetSize = 40.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        testTag = "detail_pin_button"
                    )

                    // Price overlay badge
                    val isDetailService = item.category.contains("Service", ignoreCase = true)
                    val isDetailBarter = !isDetailService && (item.price == 0.0 || item.category.contains("Barter", ignoreCase = true))
                    val detailBadgeColor = if (isDetailBarter) Color(0xFF0284C7) else if (isDetailService) Color(0xFF9C27B0) else MaterialTheme.colorScheme.primary
                    val detailPriceFormatted = when {
                        isDetailBarter -> "🔄 Barter / Trade"
                        isDetailService && item.price == 0.0 -> "💼 Free Consultation"
                        isDetailService -> "${CurrencyHelper.format(item.price, currentCurrency)} / hr"
                        item.price == 0.0 -> "Free"
                        else -> CurrencyHelper.format(item.price, currentCurrency)
                    }

                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = detailBadgeColor,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = detailPriceFormatted,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Details Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${item.landmark ?: item.location} • ${item.distanceKm} km away",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (item.isAvailable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = if (item.isAvailable) "AVAILABLE" else "RESERVED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isAvailable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Attribute Badges (Category, Condition, Delivery)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "🏷️ ${item.category}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "✨ ${item.condition}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "📍 ${item.deliveryOption}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Connected Friends Discount Badge (Section 4.6)
                    Spacer(modifier = Modifier.height(8.dp))
                    ConnectionDiscountBadge(
                        discountPercent = 15,
                        originalPrice = item.price,
                        currency = currentCurrency
                    )

                    // Handmade / Artisan badge if Art & Collectibles or Home (Section 4.19)
                    if (item.category.contains("Art", ignoreCase = true) || item.category.contains("Furniture", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(6.dp))
                        HandmadeArtisanBadge(creatorWorkshop = "${item.sellerFullName}'s Studio")
                    }

                    // Item physical dimensions ruler badge (Section 4.18)
                    Spacer(modifier = Modifier.height(6.dp))
                    ItemDimensionsRulerBadge(
                        widthCm = ((item.id % 40) + 30).toInt(),
                        heightCm = ((item.id % 30) + 20).toInt(),
                        depthCm = ((item.id % 20) + 10).toInt()
                    )

                    // Accepted Payment Methods (Section 4.14)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Accepted Settlement Methods",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PaymentMethodChipsRow(
                        selectedMethods = listOf("💵 Cash on Meetup", "📱 Digital Transfer", "🔄 Barter & Trade")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description text
                    Text(
                        text = "Description",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ConditionVisualizerDial(condition = item.condition)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Seller Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(item.sellerAvatar)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.sellerUsername,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                )

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = item.sellerFullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Neighbor",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }

                                    Text(
                                        text = "@${item.sellerUsername} • Verified Neighbor",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB703),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "${item.sellerRating} (${item.sellerReviewCount} reviews)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Unified Space ID profile navigation
                            OutlinedButton(
                                onClick = { onUserProfileClick(item.sellerUsername) },
                                shape = RoundedCornerShape(100.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("market_seller_space_btn")
                            ) {
                                Text("Space ID", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Section 4.13: Verified In-Person Meetups Reputation Badge
                    VerifiedMeetupsReputationBadge(
                        safeHandoversCount = 18 + (item.sellerReviewCount * 2),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Section 4.5: Creator Portfolio Linked Grid
                    CreatorPortfolioLinkedGrid(
                        creatorHandle = item.sellerUsername,
                        posts = posts,
                        clips = clips
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Section 4.1: Verified Safe Physical Exchange Spot Card
                    VerifiedSafeExchangeSpotCard(
                        spotName = "${item.landmark ?: item.location} Municipal & Police Monitored Safe Zone",
                        distanceMeters = ((item.distanceKm * 1000).toInt().coerceIn(120, 950)),
                        hasCctv = true,
                        isOpen24h = true
                    )

                    // PROMPT 7 & 13: Verified Safe-Haven Hub & Micro-Escrow Milestone Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🛡️", fontSize = 16.sp)
                                    Text(
                                        text = if (item.isServiceOrGig) "Service Micro-Escrow" else "Safe-Haven Offline Exchange",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    border = BorderStroke(0.8.dp, Color(0xFF10B981))
                                ) {
                                    Text(
                                        text = item.escrowStatus ?: "SECURED IN ESCROW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Exchange Hub: ${item.safeHavenHubName ?: "Civic Plaza Police Precinct (CCTV Zone)"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Funds locked securely until physical mutual verification or milestone sign-off.",
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (item.isServiceOrGig) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("30% Deposit", "40% Proof-of-Work", "30% Sign-off").forEach { step ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "✓ $step",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showHandoverQR = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🔒 Handshake QR Escrow Release", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 4.4: Ghost Negotiation Privacy Shield
                    GhostNegotiationPrivacyShield(
                        isGhostActive = isGhostShieldActive,
                        ghostAlias = "Spectator #${(item.id * 17) % 900 + 100}",
                        onToggleGhost = { isGhostShieldActive = !isGhostShieldActive }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 4.10: Quick Pre-filled Inquiry Questions
                    Text(
                        text = "Quick Inquiries",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    QuickInquiryActionsRow(
                        onSendQuestion = { q ->
                            selectedInquiryQuestion = q
                            onMessageSeller()
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Safe Local Meetup Advice Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Localiiiy Safe Meetup: Meet in well-lit public areas (e.g. coffee shops, plazas) and inspect before completing transaction.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // Section 4.12: Price Drop Alert Banner
                    if (item.id % 2L == 0L) {
                        Spacer(modifier = Modifier.height(10.dp))
                        PriceDropAlertBanner(
                            discountPercent = 20,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Section 4.9: 10s Video Inspection Tour
                    OutlinedButton(
                        onClick = { showVideoTourModal = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("video_tour_detail_btn")
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🎥 10-Second Looping Video Tour", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AutoNegotiateButton(sellerName = item.sellerFullName, price = item.price, currency = currentCurrency)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Section 4.7: Zero-Fee In-Person Handshake QR Button
                    OutlinedButton(
                        onClick = { showHandoverQR = true },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF00E676)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("in_person_handshake_btn")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("In-Person Zero-Fee QR Handshake", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Make Offer button
                        OutlinedButton(
                            onClick = { showOfferDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("make_offer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Comment Button
                        OutlinedButton(
                            onClick = onCommentClick,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("market_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Remarks",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Message Seller Button
                        Button(
                            onClick = onMessageSeller,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("message_seller_button")
                        ) {
                            Icon(imageVector = Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        
                        // Buy Now / Checkout Button
                        Button(
                            onClick = { showCheckoutDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("buy_now_button")
                        ) {
                            Text("💳 Buy Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle Reserve button
                    TextButton(
                        onClick = onToggleAvailability,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_reserve_button")
                    ) {
                        Text(
                            text = if (item.isAvailable) "Mark as Hold / Reserved" else "Mark as Available",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Section 4.16: Flag / Report Listing
                    TextButton(
                        onClick = { showFlagModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flag_listing_detail_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OutlinedFlag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Flag or Report Suspicious Listing",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Section 4.7: Handshake QR Dialog
    if (showHandoverQR) {
        InPersonHandoverQRDialog(
            itemTitle = item.title,
            sellerHandle = item.sellerUsername,
            onDismiss = { showHandoverQR = false },
            onConfirmHandover = {
                showHandoverQR = false
                onToggleAvailability()
            }
        )
    }

    // Section 4.9: Video Tour Modal
    if (showVideoTourModal) {
        MarketConditionVideoTourModal(
            videoUrl = item.imageUrl,
            title = item.title,
            onDismiss = { showVideoTourModal = false }
        )
    }

    // Section 4.16: Flag Confirmation Dialog
    if (showFlagModal) {
        FlagListingConfirmationDialog(
            itemTitle = item.title,
            onConfirmFlag = { reason ->
                showFlagModal = false
                onFlagItem(item.id, reason)
            },
            onDismiss = { showFlagModal = false }
        )
    }

    // Modal to make custom offer
    if (showOfferDialog) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = { Text("Make an Offer for ${item.title}") },
            text = {
                Column {
                    Text(
                        text = "Listed price is ${CurrencyHelper.format(item.price, currentCurrency)}. Enter your offer below:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = offerAmount,
                        onValueChange = { offerAmount = it },
                        label = { Text("Your Offer (${currentCurrency.symbol})") },
                        leadingIcon = { Text(currentCurrency.symbol, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOfferDialog = false
                        onMessageSeller()
                    }
                ) {
                    Text("Send Offer (${currentCurrency.symbol}$offerAmount)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Checkout Escrow Dialog
    if (showCheckoutDialog) {
        MarketCheckoutEscrowDialog(
            item = item,
            currentCurrency = currentCurrency,
            isPremiumSubscribed = isPremiumSubscribed,
            onDismiss = { showCheckoutDialog = false },
            onConfirmPurchase = {
                showCheckoutDialog = false
                // Complete purchase logic...
            }
        )
    }
}

@Composable
fun ConditionVisualizerDial(condition: String) {
    val conditionScore = when(condition.lowercase()) {
        "new" -> 1.0f
        "like new" -> 0.8f
        "good" -> 0.6f
        "fair" -> 0.4f
        "poor" -> 0.2f
        "junk" -> 0.05f
        else -> 0.7f
    }
    
    var animationPlayed by remember { mutableStateOf(false) }
    val sweepAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 180f * conditionScore else 0f,
        animationSpec = tween(1500, easing = LinearEasing)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Text("Item Condition: $condition", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.size(width = 120.dp, height = 60.dp), contentAlignment = Alignment.BottomCenter) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height * 2
                
                // Background track
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(canvasWidth, canvasHeight),
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )

                // Foreground track (Gradient based on condition)
                val strokeColor = when {
                    conditionScore > 0.7f -> Color(0xFF4CAF50) // Green
                    conditionScore > 0.4f -> Color(0xFFFFC107) // Yellow
                    else -> Color(0xFFF44336) // Red
                }

                drawArc(
                    color = strokeColor,
                    startAngle = 180f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(canvasWidth, canvasHeight),
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun AutoNegotiateButton(
    sellerName: String,
    price: Double,
    currency: LocaliiiyCurrency
) {
    var isNegotiating by remember { mutableStateOf(false) }
    var currentOffer by remember { mutableStateOf(price) }

    LaunchedEffect(isNegotiating) {
        if (isNegotiating) {
            delay(1000)
            currentOffer = price * 0.9
            delay(1500)
            currentOffer = price * 0.85
            delay(1500)
            currentOffer = price * 0.8
            isNegotiating = false
        }
    }

    Surface(
        onClick = { if (!isNegotiating) isNegotiating = true },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            if (isNegotiating) {
                Text(
                    text = "AI Negotiating... Best Offer: ${currency.symbol}${String.format("%.2f", currentOffer)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else if (currentOffer < price) {
                Text(
                    text = "Deal Agreed: ${currency.symbol}${String.format("%.2f", currentOffer)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else {
                Text(
                    text = "Auto-Negotiate with AI",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun MarketCheckoutEscrowDialog(
    item: com.example.data.MarketplaceItemEntity,
    currentCurrency: LocaliiiyCurrency,
    isPremiumSubscribed: Boolean = false,
    onDismiss: () -> Unit,
    onConfirmPurchase: () -> Unit
) {
    val basePrice = CurrencyHelper.convertFromUSD(item.price, currentCurrency)
    val standardFee = basePrice * 0.05
    val platformFee = if (isPremiumSubscribed) 0.0 else standardFee
    val totalAmount = basePrice + platformFee

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Security, contentDescription = "Escrow", tint = MaterialTheme.colorScheme.primary)
                Text("Secure Digital Escrow", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "You are purchasing ${item.title} from @${item.sellerUsername}.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Item Price", fontSize = 13.sp)
                            Text(CurrencyHelper.format(item.price, currentCurrency), fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    if (isPremiumSubscribed) "Platform Escrow (0% Free)" else "Platform Escrow Fee (5%)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isPremiumSubscribed) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPremiumSubscribed) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isPremiumSubscribed) {
                                    Surface(
                                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "PRO 👑",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFD700),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isPremiumSubscribed)
                                    "${currentCurrency.symbol}0.00 (Saved ${currentCurrency.symbol}${String.format("%.2f", standardFee)})"
                                else
                                    "${currentCurrency.symbol}${String.format("%.2f", platformFee)}",
                                fontSize = 13.sp,
                                fontWeight = if (isPremiumSubscribed) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPremiumSubscribed) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total to Pay", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "${currentCurrency.symbol}${String.format("%.2f", totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Funds are held securely in escrow until you physically inspect the item and confirm the handover.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmPurchase,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Pay ${currentCurrency.symbol}${String.format("%.2f", totalAmount)}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
