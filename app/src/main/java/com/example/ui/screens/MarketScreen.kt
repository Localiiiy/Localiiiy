package com.example.ui.screens
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.example.util.LocationHelper

data class MarketCategoryItem(
    val name: String,
    val icon: ImageVector
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
    GOODS,
    SERVICES,
    POSTS,
    CLIPS,
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
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(MarketSubTab.GOODS) }
    var showLocationSearchExpanded by remember { mutableStateOf(false) }

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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Hyperlocal Buy, Sell, Posts & Clips",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Unified Sell / Post Button
                Button(
                    onClick = onOpenSellDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("market_sell_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Sell or Post",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "List / Post",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Search goods, gear, buy/sell posts & clips...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .testTag("market_search_field")
            )

            // Location-Wise Search Bar & Quick World Location Filter Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = locationQuery,
                        onValueChange = onLocationQueryChange,
                        placeholder = {
                            Text(
                                "Search by location anywhere (e.g. Tokyo, NY, Paris)...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location Search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (locationQuery.isNotEmpty()) {
                                IconButton(onClick = { onLocationQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear location search",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("market_location_search_field")
                    )

                    if (locationQuery.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onLocationQueryChange("") }
                        ) {
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Quick Popular World Location Pills
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickLocations) { (locName, icon) ->
                        val isSelected = locationQuery.equals(locName, ignoreCase = true) ||
                                (locName == "Anywhere (Global)" && locationQuery.isBlank())
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
                                    if (locName == "Anywhere (Global)") {
                                        onLocationQueryChange("")
                                    } else {
                                        onLocationQueryChange(locName)
                                    }
                                }
                                .testTag("quick_loc_${locName.replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(icon, fontSize = 10.sp)
                                Text(
                                    text = locName,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(MarketSubTab.GOODS, "Goods", Icons.Default.Storefront),
                    Triple(MarketSubTab.SERVICES, "Services", Icons.Default.Build),
                    Triple(MarketSubTab.POSTS, "Posts", Icons.Default.PhotoLibrary),
                    Triple(MarketSubTab.CLIPS, "Clips", Icons.Default.PlayCircle),
                    Triple(MarketSubTab.WATCHLIST, "Saved", Icons.Default.PushPin)
                ).forEach { (tab, label, icon) ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { activeSubTab = tab }
                            .testTag("market_subtab_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                    MarketSubTab.GOODS -> {
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
                            onItemClick = { clip ->
                                val match = items.firstOrNull { it.title.contains(clip.caption.take(15), ignoreCase = true) }
                                if (match != null) onItemClick(match)
                            },
                            onOpenSellDialog = onOpenSellDialog,
                            onOpenComments = onOpenComments
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
                    onFlagItem(selectedItem, reason)
                    hiddenItemIds = hiddenItemIds + id
                    onCloseDetailSheet()
                },
                currentCurrency = currentCurrency,
                currentLanguage = currentLanguage,
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
                    onFlagItem(itemToFlag!!, reason)
                    hiddenItemIds = hiddenItemIds + itemToFlag!!.id
                    itemToFlag = null
                },
                onDismiss = { itemToFlag = null }
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
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Photo listings & requests from verified neighbors",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenSellDialog,
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+ Add Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text("Comment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketClipsFeedView(
    clips: List<ClipEntity>,
    onItemClick: (ClipEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onOpenComments: (String, Long) -> Unit
) {
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
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
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
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Short video showcases of items for sale",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenSellDialog,
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+ Sell Clip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(clips, key = { it.id }) { clip ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clickable { onItemClick(clip) }
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
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Top Clip Play Tag
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text("Clip", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Top Right Like hand button
                    var isClipLiked by remember(clip.id) { mutableStateOf(clip.isLiked) }
                    var clipLikesCount by remember(clip.id) { mutableStateOf(clip.likesCount) }
                    com.example.ui.components.AnimatedLikeButton(
                        isLiked = isClipLiked,
                        onLikeClick = {
                            isClipLiked = !isClipLiked
                            clipLikesCount += if (isClipLiked) 1 else -1
                        },
                        likesCount = clipLikesCount,
                        showCount = true,
                        symbolSize = 16.sp,
                        touchTargetSize = 32.dp,
                        labelColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        testTag = "market_clip_like_${clip.id}"
                    )

                    // Bottom info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = clip.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = clip.caption,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "💬 Chat Seller",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
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
    var selectedPhotoUrl by remember { mutableStateOf(sampleMarketPhotos.first()) }
    var selectedDelivery by remember { mutableStateOf("Local Meetup / Pickup") }
    var landmark by remember { mutableStateOf("Pike Place Market") }
    var acceptedPaymentMethods by remember { mutableStateOf(listOf("💵 Cash on Meetup", "📱 Digital Transfer")) }
    var itemDimensionsText by remember { mutableStateOf("") }
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

                // Select Media
                Text(
                    text = if (creationFormat == SellCreationFormat.SHOWCASE_CLIP) "1. Product Video Preview" else "1. Item Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

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
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Sample Photo Picker
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    items(sampleMarketPhotos) { photo ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedPhotoUrl = photo }
                                .then(
                                    if (selectedPhotoUrl == photo) Modifier.background(MaterialTheme.colorScheme.primary)
                                    else Modifier
                                )
                                .padding(if (selectedPhotoUrl == photo) 2.dp else 0.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photo)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showStandardMediaSelector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Media Selector 📁", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showStandardMediaSelector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Camera 📷", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (showStandardMediaSelector) {
                    StandardMediaSelectorBottomSheet(
                        onDismiss = { showStandardMediaSelector = false },
                        onMediaSelected = { selectedList ->
                            if (selectedList.isNotEmpty()) {
                                selectedPhotoUrl = selectedList.first()
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

                // Dimensions (Section 4.18)
                OutlinedTextField(
                    value = itemDimensionsText,
                    onValueChange = { itemDimensionsText = it },
                    label = { Text("Approx. Dimensions / Scale (e.g. 45 x 30 x 15 cm)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
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

                // Publish Button
                val isFormValid = title.isNotBlank() && (isBarterTrade || (priceText.toDoubleOrNull() ?: 0.0) >= 0.0)

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
                        val dimensionsInfo = if (itemDimensionsText.isNotBlank()) "\n[Dimensions: $itemDimensionsText]" else ""
                        val finalDesc = description.ifBlank { "Available for local meetup near $landmark on Localiiiy." } + barterInfo + discountInfo + artisanInfo + urgentInfo + reachText + paymentInfo + dimensionsInfo
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
                    },
                    enabled = isFormValid,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_market_item_button")
                ) {
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
                            SellCreationFormat.BUY_SELL_POST -> "Broadcast Buy / Sell Post"
                            SellCreationFormat.SHOWCASE_CLIP -> "Publish Video Showcase Clip"
                            SellCreationFormat.CATALOG_ITEM -> "Publish to Localiiiy Market"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
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
                                contentDescription = "Comments",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Message Seller Button
                        Button(
                            onClick = onMessageSeller,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("message_seller_button")
                        ) {
                            Text("✍️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Message", fontWeight = FontWeight.Bold)
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
