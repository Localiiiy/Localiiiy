package com.example.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.util.CurrencyHelper
import com.example.util.LocaliCurrency
import com.example.util.LocaliLanguage
import com.example.util.LocaliStringKey
import com.example.util.LocalizationHelper
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.MarketplaceItemEntity
import com.example.data.PostEntity
import com.example.data.ReelEntity
import com.example.ui.components.RadarRadiusOption
import com.example.ui.components.RadarRadiusPresets
import com.example.ui.theme.LocaliAccentMint
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
    POSTS,
    REELS,
    WATCHLIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    items: List<MarketplaceItemEntity>,
    posts: List<PostEntity> = emptyList(),
    reels: List<ReelEntity> = emptyList(),
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
    onPublishBuySellReel: (title: String, desc: String, price: Double, category: String, condition: String, videoUrl: String, soundTitle: String?, loc: String?, landmark: String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onCloseDetailSheet: () -> Unit,
    onMessageSeller: (MarketplaceItemEntity) -> Unit,
    onToggleAvailability: (MarketplaceItemEntity) -> Unit,
    currentCurrency: LocaliCurrency = LocaliCurrency.USD,
    currentLanguage: LocaliLanguage = LocaliLanguage.EN,
    onUserProfileClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(MarketSubTab.GOODS) }
    var showLocationSearchExpanded by remember { mutableStateOf(false) }

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

    // Filter items based on category, keyword search, location search, and radius scale
    val filteredItems = remember(items, selectedCategory, searchQuery, locationQuery, radiusFilterKm) {
        items.filter { item ->
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
            matchesCategory && matchesSearch && matchesLocation && matchesRadius
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

    // Buy/Sell specific reels
    val buySellReels = remember(reels, searchQuery, locationQuery) {
        reels.filter { reel ->
            val matchesSearch = searchQuery.isBlank() || reel.caption.contains(searchQuery, ignoreCase = true)
            val matchesLoc = locationQuery.isBlank() || locationQuery.equals("Anywhere (Global)", ignoreCase = true) ||
                    (reel.location?.contains(locationQuery, ignoreCase = true) == true) ||
                    (reel.landmark?.contains(locationQuery, ignoreCase = true) == true)
            val isMarketTheme = reel.caption.contains("FOR SALE", ignoreCase = true) ||
                    reel.caption.contains("BUY", ignoreCase = true) ||
                    reel.caption.contains("SELL", ignoreCase = true) ||
                    reel.caption.contains("$") ||
                    reel.caption.contains("Market", ignoreCase = true)
            isMarketTheme && matchesSearch && matchesLoc
        }.ifEmpty { reels }
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
                        text = "Hyperlocal Buy, Sell, Posts & Reels",
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
                        "Search goods, gear, buy/sell posts & reels...",
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

            // Market Navigation Sub-Tabs (All Goods | Buy/Sell Posts | Showcase Reels | Watchlist)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(MarketSubTab.GOODS, "All Goods", Icons.Default.Storefront),
                    Triple(MarketSubTab.POSTS, "Posts", Icons.Default.PhotoLibrary),
                    Triple(MarketSubTab.REELS, "Reels", Icons.Default.PlayCircle),
                    Triple(MarketSubTab.WATCHLIST, "Watchlist", Icons.Default.PushPin)
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
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sub-Content Views
            when (activeSubTab) {
                MarketSubTab.GOODS -> {
                    GoodsCatalogView(
                        filteredItems = filteredItems,
                        selectedCategory = selectedCategory,
                        radiusFilterKm = radiusFilterKm,
                        onSelectCategory = onSelectCategory,
                        onRadiusFilterChange = onRadiusFilterChange,
                        onItemClick = onItemClick,
                        onToggleSaveItem = onToggleSaveItem,
                        onOpenSellDialog = onOpenSellDialog,
                        currentCurrency = currentCurrency
                    )
                }

                MarketSubTab.POSTS -> {
                    MarketPostsFeedView(
                        posts = buySellPosts,
                        onItemClick = { post ->
                            val match = items.firstOrNull { it.title.contains(post.caption.take(15), ignoreCase = true) }
                            if (match != null) onItemClick(match)
                        },
                        onOpenSellDialog = onOpenSellDialog
                    )
                }

                MarketSubTab.REELS -> {
                    MarketReelsFeedView(
                        reels = buySellReels,
                        onItemClick = { reel ->
                            val match = items.firstOrNull { it.title.contains(reel.caption.take(15), ignoreCase = true) }
                            if (match != null) onItemClick(match)
                        },
                        onOpenSellDialog = onOpenSellDialog
                    )
                }

                MarketSubTab.WATCHLIST -> {
                    WatchlistCatalogView(
                        savedItems = savedItems,
                        onItemClick = onItemClick,
                        onToggleSaveItem = onToggleSaveItem,
                        onOpenSellDialog = onOpenSellDialog,
                        currentCurrency = currentCurrency
                    )
                }
            }
        }

        // Sell an Item Dialog / Sheet
        if (showSellDialog) {
            MarketSellMultiDialog(
                onDismiss = onCloseSellDialog,
                onPublishListing = onPublishItem,
                onPublishPost = onPublishBuySellPost,
                onPublishReel = onPublishBuySellReel,
                currentCurrency = currentCurrency
            )
        }

        // Item Details Bottom Sheet / Modal
        if (selectedItem != null) {
            MarketItemDetailDialog(
                item = selectedItem,
                onDismiss = onCloseDetailSheet,
                onMessageSeller = { onMessageSeller(selectedItem) },
                onToggleSave = { onToggleSaveItem(selectedItem) },
                onToggleAvailability = { onToggleAvailability(selectedItem) },
                currentCurrency = currentCurrency,
                currentLanguage = currentLanguage,
                onUserProfileClick = onUserProfileClick
            )
        }
    }
}

@Composable
private fun GoodsCatalogView(
    filteredItems: List<MarketplaceItemEntity>,
    selectedCategory: String,
    radiusFilterKm: Double?,
    onSelectCategory: (String) -> Unit,
    onRadiusFilterChange: (Double?) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    currentCurrency: LocaliCurrency = LocaliCurrency.USD
) {
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

                        items(RadarRadiusPresets.ALL_OPTIONS) { opt ->
                            val isSelected = radiusFilterKm == opt.km
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
                                    .testTag("market_radius_${opt.km.toInt()}km")
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
                currentCurrency = currentCurrency
            )
        }
    }
}

@Composable
private fun MarketPostsFeedView(
    posts: List<PostEntity>,
    onItemClick: (PostEntity) -> Unit,
    onOpenSellDialog: () -> Unit
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
                onClick = { onItemClick(post) }
            )
        }
    }
}

@Composable
private fun MarketPostCard(
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat / Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketReelsFeedView(
    reels: List<ReelEntity>,
    onItemClick: (ReelEntity) -> Unit,
    onOpenSellDialog: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("market_reels_grid")
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
                                text = "Market Video Reels & Pitches",
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
                        Text("+ Sell Reel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(reels, key = { it.id }) { reel ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clickable { onItemClick(reel) }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(reel.mediaUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = reel.caption,
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

                    // Top Reel Play Tag
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
                            Text("Reel", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Bottom info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = reel.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = reel.caption,
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
    currentCurrency: LocaliCurrency = LocaliCurrency.USD
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
    currentCurrency: LocaliCurrency = LocaliCurrency.USD,
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
                            tint = LocaliAccentMint,
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

                // Pin / Save Button (Top Right)
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleSave)
                        .testTag("pin_item_button_${item.id}")
                ) {
                    Icon(
                        imageVector = if (item.isSaved) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin Item",
                        tint = if (item.isSaved) Color(0xFFFFB703) else Color.White,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize()
                    )
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

                // Price Badge (Bottom Left)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = CurrencyHelper.format(item.price, currentCurrency),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
    SHOWCASE_REEL
}

@Composable
fun MarketSellMultiDialog(
    onDismiss: () -> Unit,
    onPublishListing: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit,
    onPublishPost: (title: String, desc: String, price: Double, category: String, condition: String, imageUrl: String, delivery: String, loc: String?, landmark: String?) -> Unit,
    onPublishReel: (title: String, desc: String, price: Double, category: String, condition: String, videoUrl: String, soundTitle: String?, loc: String?, landmark: String?) -> Unit,
    currentCurrency: LocaliCurrency = LocaliCurrency.USD
) {
    var creationFormat by remember { mutableStateOf(SellCreationFormat.BUY_SELL_POST) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Cars & Vehicles") }
    var selectedCondition by remember { mutableStateOf("Like New") }
    var selectedPhotoUrl by remember { mutableStateOf(sampleMarketPhotos.first()) }
    var selectedDelivery by remember { mutableStateOf("Local Meetup / Pickup") }
    var landmark by remember { mutableStateOf("Pike Place Market") }
    var soundTrack by remember { mutableStateOf("Original Audio • Marketplace Pitch") }

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
                            text = "Choose whether to post a catalog item, photo post, or video reel",
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
                        SellCreationFormat.SHOWCASE_REEL to ("🎥 Video Reel"),
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
                    text = if (creationFormat == SellCreationFormat.SHOWCASE_REEL) "1. Product Video Preview" else "1. Item Photo",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                Text(
                    text = "2. Title & Price",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Name (e.g., Sony A7III Camera Kit)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sell_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                    label = { Text("Local Landmark / Neighborhood") },
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

                // Description
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

                Spacer(modifier = Modifier.height(16.dp))

                // Publish Button
                val isFormValid = title.isNotBlank() && (priceText.toDoubleOrNull() ?: 0.0) >= 0.0

                Button(
                    onClick = {
                        val rawPrice = priceText.toDoubleOrNull() ?: 0.0
                        val parsedPrice = CurrencyHelper.convertToUSD(rawPrice, currentCurrency)
                        when (creationFormat) {
                            SellCreationFormat.CATALOG_ITEM -> {
                                onPublishListing(
                                    title,
                                    description.ifBlank { "Available for local pickup near $landmark on Localiiiy." },
                                    parsedPrice,
                                    selectedCategory,
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
                                    description.ifBlank { "Available for local pickup near $landmark on Localiiiy." },
                                    parsedPrice,
                                    selectedCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    selectedDelivery,
                                    "Seattle",
                                    landmark
                                )
                            }
                            SellCreationFormat.SHOWCASE_REEL -> {
                                onPublishReel(
                                    title,
                                    description.ifBlank { "Showcase reel of $title near $landmark" },
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
                            SellCreationFormat.SHOWCASE_REEL -> Icons.Default.Videocam
                            SellCreationFormat.CATALOG_ITEM -> Icons.Default.Storefront
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (creationFormat) {
                            SellCreationFormat.BUY_SELL_POST -> "Broadcast Buy / Sell Post"
                            SellCreationFormat.SHOWCASE_REEL -> "Publish Video Showcase Reel"
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
    onDismiss: () -> Unit,
    onMessageSeller: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleAvailability: () -> Unit,
    currentCurrency: LocaliCurrency = LocaliCurrency.USD,
    currentLanguage: LocaliLanguage = LocaliLanguage.EN,
    onUserProfileClick: (String) -> Unit = {}
) {
    var offerAmount by remember(item, currentCurrency) {
        mutableStateOf("${CurrencyHelper.convertFromUSD(item.price, currentCurrency).toInt()}")
    }
    var showOfferDialog by remember { mutableStateOf(false) }

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
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("detail_pin_button")
                    ) {
                        Icon(
                            imageVector = if (item.isSaved) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (item.isSaved) Color(0xFFFFB703) else Color.White
                        )
                    }

                    // Price overlay badge
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = CurrencyHelper.format(item.price, currentCurrency),
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

                    Spacer(modifier = Modifier.height(20.dp))

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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Make Offer", fontWeight = FontWeight.Bold)
                        }

                        // Message Seller Button
                        Button(
                            onClick = onMessageSeller,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("message_seller_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Message Seller", fontWeight = FontWeight.Bold)
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
                }
            }
        }
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
