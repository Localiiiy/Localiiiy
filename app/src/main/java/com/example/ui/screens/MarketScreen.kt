package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.PostEntity
import com.example.data.SellerCatalog
import com.example.ui.components.LocaliiiyAppLogoBadge
import com.example.ui.components.MarketSandboxUpgradesScreen
import com.example.ui.components.SystematicDistanceScale
import com.example.ui.components.market.*
import com.example.ui.viewmodel.LocalViewModel
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage

data class MarketCategoryItem(
    val name: String,
    val icon: ImageVector
)

enum class MarketSubTab {
    ALL,
    GOODS,
    SERVICES,
    CLIPS,
    CATALOGS,
    FLASH_DEALS,
    POSTS,
    WATCHLIST,
    UPGRADES
}

enum class SellCreationFormat {
    CATALOG_ITEM,
    BUY_SELL_POST,
    SHOWCASE_CLIP
}

val marketCategories: List<MarketCategoryItem> = listOf(
    MarketCategoryItem("All", Icons.Default.Storefront),
    MarketCategoryItem("Vehicles", Icons.Default.DirectionsCar),
    MarketCategoryItem("Properties & Land", Icons.Default.Landscape),
    MarketCategoryItem("Mobiles & Tech", Icons.Default.Smartphone),
    MarketCategoryItem("Home & Living", Icons.Default.Weekend),
    MarketCategoryItem("Fashion & Style", Icons.Default.Checkroom),
    MarketCategoryItem("Jobs & Services", Icons.Default.Work),
    MarketCategoryItem("Farm & Fresh", Icons.Default.LocalFlorist),
    MarketCategoryItem("Books & Sports", Icons.Default.MenuBook),
    MarketCategoryItem("Art & Crafts", Icons.Default.Palette),
    MarketCategoryItem("Pets & Care", Icons.Default.Pets),
    MarketCategoryItem("Borrow & Barter", Icons.Default.SwapHoriz)
)

val marketSubCategoriesMap: Map<String, List<String>> = mapOf(
    "Vehicles" to listOf(
        "Cycles",
        "Bikes & Scooters",
        "Cars",
        "EVs & Hybrids",
        "Commercial & Trucks",
        "Spare Parts",
        "Accessories"
    ),
    "Properties & Land" to listOf(
        "Land & Plots",
        "Flats & Apartments",
        "Houses & Villas",
        "Shops & Offices",
        "PG & Co-living",
        "Farmhouses"
    ),
    "Mobiles & Tech" to listOf(
        "Smartphones",
        "Tablets & iPads",
        "Laptops & PCs",
        "Cameras & Lenses",
        "Smartwatches",
        "Audio & Headphones",
        "Gaming Consoles"
    ),
    "Home & Living" to listOf(
        "Sofa & Dining",
        "Beds & Wardrobes",
        "Home Decor",
        "Kitchen Appliances",
        "TVs & Refrigerators",
        "Garden & Outdoor",
        "Tools & DIY"
    ),
    "Fashion & Style" to listOf(
        "Men's Apparel",
        "Women's Apparel",
        "Footwear & Sneakers",
        "Watches & Luxury",
        "Bags & Luggage",
        "Streetwear & Vintage"
    ),
    "Jobs & Services" to listOf(
        "Home Repairs",
        "Cleaning & Pest",
        "Tech & Web Help",
        "Tutoring & Classes",
        "Photography & Media",
        "Part-time & Local Gigs",
        "Moving & Delivery"
    ),
    "Farm & Fresh" to listOf(
        "Organic Vegetables",
        "Fresh Fruits",
        "Artisan Breads",
        "Local Honey & Dairy",
        "Indoor Plants & Seeds",
        "Homemade Pickles"
    ),
    "Books & Sports" to listOf(
        "Fiction & Novels",
        "Textbooks & Study",
        "Gym & Fitness Gear",
        "Camping & Trekking",
        "Musical Instruments",
        "Board Games"
    ),
    "Art & Crafts" to listOf(
        "Handmade Pottery",
        "Canvas Paintings",
        "Woodwork & Carvings",
        "Handwoven Textiles",
        "Custom Jewelry",
        "Sculptures"
    ),
    "Pets & Care" to listOf(
        "Pet Accessories",
        "Aquariums & Fish",
        "Pet Grooming",
        "Dog Walking & Sitting",
        "Organic Pet Treats"
    ),
    "Borrow & Barter" to listOf(
        "Power Tools",
        "Party & Event Gear",
        "Camera Rentals",
        "Camping Equipment",
        "Direct Skill Swap",
        "Free / Upcycle"
    )
)

val sampleMarketPhotos: List<String> = listOf(
    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1560343090-f0409e92791a?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1503602642458-232111445657?w=600&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1585386959984-a4155224a1ad?w=600&auto=format&fit=crop&q=80"
)

val defaultMarketplaceItems: List<MarketplaceItemEntity> = listOf(
    MarketplaceItemEntity(
        id = 7001L,
        title = "Sony Alpha a7 IV Mirrorless Body + 35mm f/1.4 GM",
        description = "Shutter count under 4,200. Includes original box, dual batteries, and Peak Design strap. Available for inspection at Civic Plaza Safe Zone.",
        price = 1850.0,
        category = "Mobiles & Tech",
        condition = "Like New",
        imageUrl = "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "maya_urban",
        sellerFullName = "Maya Lin",
        creatorConnections = 1840,
        sellerConnections = 1840,
        sellerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
        sellerRating = 4.9,
        sellerReviewCount = 42,
        location = "Arts District",
        landmark = "Arts District Foundry",
        distanceKm = 0.6,
        isAvailable = true,
        isSaved = false,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Civic Plaza Police Precinct (CCTV Zone)",
        catalogName = "Maya's Visual Gear Vault",
        catalogBadge = "Certified Studio",
        videoClipUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        videoDurationSeconds = 10,
        isFlashDeal = true
    ),
    MarketplaceItemEntity(
        id = 7002L,
        title = "Handcrafted Walnut Mid-Century Coffee Table",
        description = "Solid kiln-dried walnut with hand-rubbed Danish oil finish. Custom built in our local neighborhood woodshop.",
        price = 340.0,
        category = "Art & Crafts",
        condition = "Brand New",
        imageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "elena_creative",
        sellerFullName = "Elena Rostova",
        creatorConnections = 960,
        sellerConnections = 960,
        sellerAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&auto=format&fit=crop&q=80",
        sellerRating = 5.0,
        sellerReviewCount = 31,
        location = "Old Town",
        landmark = "Artisan Timber Collective",
        distanceKm = 0.9,
        isAvailable = true,
        isSaved = true,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Central Library Monitored Courtyard",
        catalogName = "Rostova Wood & Ceramic Studio",
        catalogBadge = "Master Artisan"
    ),
    MarketplaceItemEntity(
        id = 7003L,
        title = "VanMoof S5 City Electric Commuter Bike",
        description = "Matte grey, 60km range, integrated anti-theft kick lock and Apple Find My support. Test rides welcome at Safe-Haven Plaza.",
        price = 1290.0,
        category = "Vehicles",
        condition = "Like New",
        imageUrl = "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "alex_green",
        sellerFullName = "Alex Rivera",
        creatorConnections = 620,
        sellerConnections = 620,
        sellerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
        sellerRating = 4.8,
        sellerReviewCount = 19,
        location = "Greenway",
        landmark = "Eastside Bike Hub",
        distanceKm = 1.4,
        isAvailable = true,
        isSaved = false,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Transit Hub Monitored Plaza",
        catalogName = "Greenway EV & Mobility",
        catalogBadge = "Verified Workshop",
        isFlashDeal = true
    ),
    MarketplaceItemEntity(
        id = 7004L,
        title = "Organic Rooftop Wildflower Honey & Sourdough Starter Kit",
        description = "Raw unfiltered neighborhood rooftop apiary harvest (500g jar) paired with our 8-year heirloom sourdough starter.",
        price = 28.0,
        category = "Farm & Fresh",
        condition = "Brand New",
        imageUrl = "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "sam_coffee",
        sellerFullName = "Samira Patel",
        creatorConnections = 1420,
        sellerConnections = 1420,
        sellerAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
        sellerRating = 5.0,
        sellerReviewCount = 64,
        location = "Central Plaza",
        landmark = "Botanical Roastery Corner",
        distanceKm = 0.3,
        isAvailable = true,
        isSaved = false,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Botanical Roastery Safe Counter",
        catalogName = "Botanical Roastery & Pantry",
        catalogBadge = "Top Dealer"
    ),
    MarketplaceItemEntity(
        id = 7005L,
        title = "DeWalt 20V Max Cordless Drill & Impact Driver Kit (Borrow / Trade)",
        description = "Complete 2-tool combo with 2 batteries and charger. Free weekend borrow for connected neighbors or open to barter.",
        price = 0.0,
        category = "Borrow & Barter",
        condition = "Good",
        imageUrl = "https://images.unsplash.com/photo-1503602642458-232111445657?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "devon_builds",
        sellerFullName = "Devon Brooks",
        creatorConnections = 510,
        sellerConnections = 510,
        sellerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
        sellerRating = 4.9,
        sellerReviewCount = 27,
        location = "North Commons",
        landmark = "Northside Tool Library",
        distanceKm = 1.1,
        isAvailable = true,
        isSaved = false,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Northside Community Center Lobby"
    ),
    MarketplaceItemEntity(
        id = 7006L,
        title = "Teenage Engineering OP-1 Field Portable Synthesizer",
        description = "Mint condition aluminum workstation synth with protective case and USB-C braided audio cable. 360° video tour attached.",
        price = 1490.0,
        category = "Books & Sports",
        condition = "Like New",
        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80",
        sellerUsername = "kai_sound",
        sellerFullName = "Kai Takahashi",
        creatorConnections = 890,
        sellerConnections = 890,
        sellerAvatar = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=200&auto=format&fit=crop&q=80",
        sellerRating = 4.9,
        sellerReviewCount = 22,
        location = "Waterfront",
        landmark = "SoundLab Collective",
        distanceKm = 1.7,
        isAvailable = true,
        isSaved = false,
        deliveryOption = "Local Meetup / Pickup",
        safeHavenHubName = "Waterfront Ferry Terminal Safe Hub",
        videoClipUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        videoDurationSeconds = 10
    )
)

val defaultSellerCatalogs: List<SellerCatalog> = listOf(
    SellerCatalog(
        id = "cat_maya_studio",
        name = "Maya's Visual Gear Vault",
        sellerUsername = "maya_urban",
        sellerFullName = "Maya Lin",
        sellerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
        badge = "Certified Studio",
        bannerUrl = "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800&auto=format&fit=crop&q=80",
        category = "Mobiles & Tech",
        description = "Curated cinema cameras, prime lenses, and lighting kits maintained in our Arts District studio.",
        rating = 4.9,
        totalItems = 6
    ),
    SellerCatalog(
        id = "cat_elena_artisan",
        name = "Rostova Wood & Ceramic Studio",
        sellerUsername = "elena_creative",
        sellerFullName = "Elena Rostova",
        sellerAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&auto=format&fit=crop&q=80",
        badge = "Master Artisan",
        bannerUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&auto=format&fit=crop&q=80",
        category = "Art & Crafts",
        description = "Small-batch walnut furniture, stoneware ceramics, and hand-turned home goods crafted locally.",
        rating = 5.0,
        totalItems = 8
    ),
    SellerCatalog(
        id = "cat_sam_pantry",
        name = "Botanical Roastery & Pantry",
        sellerUsername = "sam_coffee",
        sellerFullName = "Samira Patel",
        sellerAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
        badge = "Top Dealer",
        bannerUrl = "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=800&auto=format&fit=crop&q=80",
        category = "Farm & Fresh",
        description = "Single-origin micro-lot beans, rooftop apiary honey, and organic urban harvest baskets.",
        rating = 5.0,
        totalItems = 5
    )
)

@Composable
fun MarketScreen(
    viewModel: LocalViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.marketListings.collectAsState()
    val activeCategory by viewModel.marketCategory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }
    var radiusFilterKm by remember { mutableStateOf<Double?>(10.0) }
    var selectedItem by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var showSellDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("NEAREST") }

    val mappedItems = remember(listings) {
        if (listings.isEmpty()) {
            defaultMarketplaceItems
        } else {
            listings.map { item ->
                MarketplaceItemEntity(
                    id = item.id.toLongOrNull() ?: 1L,
                    title = item.title,
                    description = item.description,
                    price = item.price,
                    category = item.category,
                    condition = item.condition,
                    imageUrl = item.imageUrl,
                    sellerUsername = item.sellerName.lowercase().replace(" ", "_"),
                    sellerFullName = item.sellerName,
                    sellerAvatar = item.sellerAvatarUrl,
                    distanceKm = item.locationDistanceKm,
                    isSaved = item.isSaved
                )
            }
        }
    }

    MarketScreen(
        items = mappedItems,
        selectedCategory = activeCategory,
        searchQuery = searchQuery,
        locationQuery = locationQuery,
        radiusFilterKm = radiusFilterKm,
        selectedItem = selectedItem,
        showSellDialog = showSellDialog,
        onSelectCategory = { viewModel.setMarketCategory(it) },
        onSearchQueryChange = { searchQuery = it },
        onLocationQueryChange = { locationQuery = it },
        onRadiusFilterChange = { radiusFilterKm = it },
        onItemClick = { selectedItem = it },
        onToggleSaveItem = { viewModel.toggleSaveMarketListing(it.id.toString()) },
        onOpenSellDialog = { showSellDialog = true },
        onCloseSellDialog = { showSellDialog = false },
        onPublishItem = { title, desc, price, cat, cond, img, _, _, _ ->
            viewModel.createMarketListing(title, price, cat, desc, cond, img)
            showSellDialog = false
        },
        onCloseDetailSheet = { selectedItem = null },
        sortOption = sortOption,
        onSortOptionChange = { sortOption = it },
        modifier = modifier
    )
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
    radiusFilterKm: Double? = 10.0,
    selectedItem: MarketplaceItemEntity? = null,
    showSellDialog: Boolean = false,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLocationQueryChange: (String) -> Unit = {},
    onRadiusFilterChange: (Double?) -> Unit = {},
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenSellDialog: () -> Unit,
    onCloseSellDialog: () -> Unit,
    onPublishItem: (String, String, Double, String, String, String, String, String?, String?) -> Unit,
    onPublishBuySellPost: (String, String, Double, String, String, String, String, String?, String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onPublishBuySellClip: (String, String, Double, String, String, String, String?, String?, String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onCloseDetailSheet: () -> Unit = {},
    onMessageSeller: (MarketplaceItemEntity) -> Unit = {},
    onToggleAvailability: (MarketplaceItemEntity) -> Unit = {},
    onFlagItem: (MarketplaceItemEntity, String) -> Unit = { _, _ -> },
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    sortOption: String = "NEAREST",
    onSortOptionChange: (String) -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    onOpenComments: (String, Long) -> Unit = { _, _ -> },
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    countryName: String? = null,
    onConvertClipToMarket: (Long, Double, String, String) -> Unit = { _, _, _, _ -> },
    isPremiumSubscribed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val activeItems = remember(items) {
        items.ifEmpty { defaultMarketplaceItems }
    }

    var activeSubTab by remember { mutableStateOf(MarketSubTab.ALL) }
    var selectedSubCategory by remember { mutableStateOf<String?>(null) }
    var showLocationPickerModal by remember { mutableStateOf(false) }
    var selectedCatalogForDetail by remember { mutableStateOf<SellerCatalog?>(null) }
    var selectedItemForTour by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var selectedItemForFlag by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var selectedItemForCheckout by remember { mutableStateOf<MarketplaceItemEntity?>(null) }

    LaunchedEffect(selectedCategory) {
        selectedSubCategory = null
    }

    val filteredItems = remember(
        activeItems,
        selectedCategory,
        selectedSubCategory,
        searchQuery,
        locationQuery,
        radiusFilterKm,
        sortOption,
        activeSubTab
    ) {
        val base = activeItems.filter { item ->
            val matchesTab = when (activeSubTab) {
                MarketSubTab.ALL -> true
                MarketSubTab.GOODS -> !item.isServiceOrGig
                MarketSubTab.SERVICES -> item.isServiceOrGig || item.category.equals("Jobs & Services", ignoreCase = true) || item.category.equals("Services", ignoreCase = true)
                MarketSubTab.FLASH_DEALS -> item.isFlashDeal || item.price <= 50.0
                MarketSubTab.WATCHLIST -> item.isSaved
                else -> true
            }
            val matchesCat = selectedCategory == "All" ||
                item.category.equals(selectedCategory, ignoreCase = true) ||
                (selectedCategory == "Mobiles & Tech" && item.category.contains("Tech", ignoreCase = true)) ||
                (selectedCategory == "Home & Living" && item.category.contains("Home", ignoreCase = true)) ||
                (selectedCategory == "Fashion & Style" && item.category.contains("Style", ignoreCase = true)) ||
                (selectedCategory == "Vehicles" && item.category.contains("Bikes", ignoreCase = true)) ||
                (selectedCategory == "Art & Crafts" && item.category.contains("Art", ignoreCase = true)) ||
                (selectedCategory == "Books & Sports" && item.category.contains("Books", ignoreCase = true))
            val subCat = selectedSubCategory
            val matchesSubCat = subCat == null ||
                item.title.contains(subCat, ignoreCase = true) ||
                item.description.contains(subCat, ignoreCase = true) ||
                item.category.contains(subCat, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.sellerFullName.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
            val matchesLoc = locationQuery.isBlank() ||
                item.location.contains(locationQuery, ignoreCase = true) ||
                (item.landmark?.contains(locationQuery, ignoreCase = true) == true)
            val matchesRadius = radiusFilterKm == null || item.distanceKm <= radiusFilterKm

            matchesTab && matchesCat && matchesSubCat && matchesSearch && matchesLoc && matchesRadius
        }
        when (sortOption) {
            "PRICE_LOW" -> base.sortedBy { it.price }
            "PRICE_HIGH" -> base.sortedByDescending { it.price }
            "POPULAR" -> base.sortedByDescending { it.sellerReviewCount }
            "NEWEST" -> base.sortedByDescending { it.timestamp }
            else -> base.sortedBy { it.distanceKm }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("market_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. TOP BRAND HEADER + "+ List" BUTTON
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
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
                            LocaliiiyAppLogoBadge(size = 30.dp)
                            Text(
                                text = "Localiiiy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = Color(0xFFFFB703).copy(alpha = 0.18f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFB703).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "MARKET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        fontSize = 10.sp
                                    ),
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Button(
                            onClick = onOpenSellDialog,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .testTag("market_sell_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "List Item",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+ List",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. UNIFIED SEARCH BAR + LOCATION SEGMENT BUTTON
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("market_unified_search_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Market",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("market_search_input"),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search Market, vehicles, crafts, tools...",
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Search",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showLocationPickerModal = true }
                                    .testTag("market_location_segment_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (locationQuery.isBlank()) "📍 Near Me" else "📍 $locationQuery",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. MARKET SUB-TABS ROW
                    val subTabs = listOf(
                        MarketSubTab.ALL to "🌐 All",
                        MarketSubTab.GOODS to "🛍️ Goods",
                        MarketSubTab.SERVICES to "🛠️ Services",
                        MarketSubTab.CLIPS to "🎬 Market Clips",
                        MarketSubTab.CATALOGS to "📚 Catalogs",
                        MarketSubTab.FLASH_DEALS to "⚡ Flash Deals",
                        MarketSubTab.POSTS to "📸 Posts",
                        MarketSubTab.WATCHLIST to "🔖 Saved",
                        MarketSubTab.UPGRADES to "⚙️ Exchange Tools"
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subTabs) { (tab, label) ->
                            val isSelected = activeSubTab == tab
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { activeSubTab = tab }
                                    .testTag("market_filter_${tab.name.lowercase()}")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // 4. CATEGORY & SUB-CATEGORY PILLS
                    if (activeSubTab == MarketSubTab.ALL ||
                        activeSubTab == MarketSubTab.GOODS ||
                        activeSubTab == MarketSubTab.FLASH_DEALS
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(marketCategories) { catItem ->
                                val isSelected = selectedCategory.equals(catItem.name, ignoreCase = true)
                                val safeTag = catItem.name.lowercase().replace(" & ", "_").replace(" ", "_")
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFFFFB703).copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFFFB703) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectCategory(catItem.name) }
                                        .testTag("market_subcat_$safeTag")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = catItem.icon,
                                            contentDescription = catItem.name,
                                            tint = if (isSelected) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = catItem.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        val subCats = marketSubCategoriesMap[selectedCategory].orEmpty()
                        if (subCats.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(subCats) { subCat ->
                                    val isSubSelected = selectedSubCategory == subCat
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSubSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .clickable {
                                                selectedSubCategory = if (isSubSelected) null else subCat
                                            }
                                            .testTag("market_subcat_pill_${subCat.lowercase().replace(" ", "_")}")
                                    ) {
                                        Text(
                                            text = subCat,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSubSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tool #5: 🛍️ Marketplace "Verified Urgent Sale" Gold Ribbon (₹49 • 100% Platform Fee + 4x Faster Sale)
                    val context = androidx.compose.ui.platform.LocalContext.current
                    var urgentRibbonActive by remember { mutableStateOf(false) }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (urgentRibbonActive) Color(0xFF0A2416) else Color(0xFF241B06),
                        border = BorderStroke(
                            1.dp,
                            if (urgentRibbonActive) Color(0xFF00E676) else Color(0xFFFFD700)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                urgentRibbonActive = true
                                android.widget.Toast.makeText(
                                    context,
                                    "🛍️ 'URGENT • VERIFIED SELLER' Gold Ribbon Activated (₹49)! Pinned to top of Market & Radar (100% Platform Fee)",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                            .testTag("market_urgent_sale_gold_ribbon_bar")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (urgentRibbonActive) {
                                        "👑 URGENT • VERIFIED SELLER GOLD RIBBON: LIVE ON YOUR LISTINGS ✓"
                                    } else {
                                        "🛍️ SELL 4X FASTER: 'URGENT • VERIFIED SELLER' GOLD RIBBON (₹49)"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (urgentRibbonActive) Color(0xFF00E676) else Color(0xFFFFD700)
                                )
                                Text(
                                    text = if (isPremiumSubscribed) {
                                        "Titan Pro 0% Escrow Fee Active • Spotlight your bike, phone or furniture on Radar"
                                    } else {
                                        "Pins your listing to top of Market & Radar • 5% Safe-Haven Escrow (0% with Titan)"
                                    },
                                    fontSize = 9.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (urgentRibbonActive) Color(0xFF00E676).copy(alpha = 0.25f) else Color(0xFFFFD700)
                            ) {
                                Text(
                                    text = if (urgentRibbonActive) "BOOSTED ✓" else "⚡ ₹49 Boost",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (urgentRibbonActive) Color(0xFF00E676) else Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. MAIN BODY BY ACTIVE SUB-TAB
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (activeSubTab) {
                    MarketSubTab.SERVICES -> {
                        LocalServicesCatalogView(
                            onInquireService = { gig ->
                                val tempItem = MarketplaceItemEntity(
                                    id = gig.id,
                                    title = gig.title,
                                    description = gig.description,
                                    price = 45.0,
                                    category = "Jobs & Services",
                                    condition = "Verified Pro",
                                    imageUrl = gig.providerAvatar,
                                    sellerUsername = gig.providerName.lowercase().replace(" ", "_"),
                                    sellerFullName = gig.providerName,
                                    sellerAvatar = gig.providerAvatar,
                                    distanceKm = gig.distanceKm,
                                    landmark = gig.landmark,
                                    isServiceOrGig = true
                                )
                                onMessageSeller(tempItem)
                            }
                        )
                    }

                    MarketSubTab.CLIPS -> {
                        MarketClipsFeedView(
                            clips = clips,
                            items = activeItems,
                            currentCurrency = currentCurrency,
                            onConvertClipToMarket = onConvertClipToMarket,
                            onItemClick = onItemClick,
                            onUserProfileClick = onUserProfileClick
                        )
                    }

                    MarketSubTab.CATALOGS -> {
                        SellerCatalogsCatalogView(
                            items = activeItems,
                            currentCurrency = currentCurrency,
                            onSelectCatalog = { selectedCatalogForDetail = it },
                            onItemClick = onItemClick
                        )
                    }

                    MarketSubTab.POSTS -> {
                        MarketPostsFeedView(
                            posts = posts,
                            items = activeItems,
                            currentCurrency = currentCurrency,
                            onItemClick = onItemClick,
                            onUserProfileClick = onUserProfileClick,
                            onOpenComments = onOpenComments,
                            onOpenSellDialog = onOpenSellDialog
                        )
                    }

                    MarketSubTab.WATCHLIST -> {
                        WatchlistCatalogView(
                            savedItems = activeItems.filter { it.isSaved },
                            currentCurrency = currentCurrency,
                            onItemClick = onItemClick,
                            onToggleSaveItem = onToggleSaveItem,
                            onOpenConditionTour = { selectedItemForTour = it },
                            onFlagItemClick = { selectedItemForFlag = it },
                            onUserProfileClick = onUserProfileClick
                        )
                    }

                    MarketSubTab.UPGRADES -> {
                        MarketSandboxUpgradesScreen(
                            currentCurrencySymbol = currentCurrency.symbol,
                            onBackToGoods = { activeSubTab = MarketSubTab.ALL }
                        )
                    }

                    else -> {
                        GoodsCatalogView(
                            items = filteredItems,
                            radiusFilterKm = radiusFilterKm,
                            onRadiusFilterChange = onRadiusFilterChange,
                            sortOption = sortOption,
                            onSortOptionChange = onSortOptionChange,
                            currentCurrency = currentCurrency,
                            countryName = countryName,
                            onItemClick = onItemClick,
                            onToggleSaveItem = onToggleSaveItem,
                            onOpenConditionTour = { selectedItemForTour = it },
                            onFlagItemClick = { selectedItemForFlag = it },
                            onUserProfileClick = onUserProfileClick,
                            onResetFilters = {
                                onSelectCategory("All")
                                selectedSubCategory = null
                                onSearchQueryChange("")
                                onLocationQueryChange("")
                                onRadiusFilterChange(null)
                            }
                        )
                    }
                }
            }
        }
    }

    // MODALS & DIALOGS
    if (showLocationPickerModal) {
        MarketLocationPickerModal(
            currentLocationQuery = locationQuery,
            currentRadiusKm = radiusFilterKm,
            countryName = countryName,
            onApplyLocation = { loc, rad ->
                onLocationQueryChange(loc)
                onRadiusFilterChange(rad)
                showLocationPickerModal = false
            },
            onDismiss = { showLocationPickerModal = false }
        )
    }

    if (selectedCatalogForDetail != null) {
        val cat = selectedCatalogForDetail!!
        val catItems = activeItems.filter {
            it.sellerUsername.equals(cat.sellerUsername, ignoreCase = true) ||
                it.catalogName?.equals(cat.name, ignoreCase = true) == true
        }.ifEmpty { activeItems.take(4) }

        SellerCatalogDetailDialog(
            catalog = cat,
            catalogItems = catItems,
            currentCurrency = currentCurrency,
            onItemClick = {
                selectedCatalogForDetail = null
                onItemClick(it)
            },
            onUserProfileClick = {
                selectedCatalogForDetail = null
                onUserProfileClick(it)
            },
            onDismiss = { selectedCatalogForDetail = null }
        )
    }

    if (selectedItem != null) {
        MarketItemDetailDialog(
            item = selectedItem,
            posts = posts,
            clips = clips,
            currentCurrency = currentCurrency,
            onDismiss = onCloseDetailSheet,
            onMessageSeller = {
                onCloseDetailSheet()
                onMessageSeller(selectedItem)
            },
            onToggleSave = { onToggleSaveItem(selectedItem) },
            onToggleAvailability = { onToggleAvailability(selectedItem) },
            onOpenComments = { onOpenComments("MARKET", selectedItem.id) },
            onOpenCheckoutEscrow = { selectedItemForCheckout = selectedItem },
            onOpenConditionTour = { selectedItemForTour = selectedItem },
            onFlagListing = { selectedItemForFlag = selectedItem },
            onUserProfileClick = { handle ->
                onCloseDetailSheet()
                onUserProfileClick(handle)
            }
        )
    }

    if (selectedItemForTour != null) {
        val tourItem = selectedItemForTour!!
        MarketConditionVideoTourModal(
            videoUrl = tourItem.videoClipUrl ?: tourItem.imageUrl,
            title = tourItem.title,
            onDismiss = { selectedItemForTour = null }
        )
    }

    if (selectedItemForFlag != null) {
        val flagTarget = selectedItemForFlag!!
        FlagListingConfirmationDialog(
            itemTitle = flagTarget.title,
            onConfirmFlag = { reason ->
                onFlagItem(flagTarget, reason)
                selectedItemForFlag = null
                if (selectedItem?.id == flagTarget.id) {
                    onCloseDetailSheet()
                }
            },
            onDismiss = { selectedItemForFlag = null }
        )
    }

    if (selectedItemForCheckout != null) {
        val checkoutItem = selectedItemForCheckout!!
        MarketCheckoutEscrowDialog(
            item = checkoutItem,
            currentCurrency = currentCurrency,
            onDismiss = { selectedItemForCheckout = null },
            onCompleteEscrow = {
                onToggleAvailability(checkoutItem)
                selectedItemForCheckout = null
                onCloseDetailSheet()
            }
        )
    }

    if (showSellDialog) {
        MarketSellMultiDialog(
            currentCurrency = currentCurrency,
            onDismiss = onCloseSellDialog,
            onPublishItem = onPublishItem,
            onPublishBuySellPost = onPublishBuySellPost,
            onPublishBuySellClip = onPublishBuySellClip
        )
    }
}

@Composable
fun GoodsCatalogView(
    items: List<MarketplaceItemEntity>,
    radiusFilterKm: Double?,
    onRadiusFilterChange: (Double?) -> Unit,
    sortOption: String,
    onSortOptionChange: (String) -> Unit,
    currentCurrency: LocaliiiyCurrency,
    countryName: String?,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenConditionTour: (MarketplaceItemEntity) -> Unit,
    onFlagItemClick: (MarketplaceItemEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onResetFilters: () -> Unit
) {
    val distanceOptions = remember(countryName) {
        SystematicDistanceScale.getOptions(countryName)
    }
    val sortOptions = listOf(
        "NEAREST" to "Distance: Nearest",
        "PRICE_LOW" to "Price: Low to High",
        "PRICE_HIGH" to "Price: High to Low",
        "POPULAR" to "Most Popular",
        "NEWEST" to "Newest Upload"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("market_goods_grid")
    ) {
        // Range & Sort Header Span
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Range Selector Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "Range:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (radiusFilterKm == 2.0) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (radiusFilterKm == 2.0) Color(0xFF10B981) else Color.Transparent),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { onRadiusFilterChange(if (radiusFilterKm == 2.0) null else 2.0) }
                            .testTag("market_walkable_filter_chip")
                    ) {
                        Text(
                            text = "🚶 Walkable (< 2 km)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (radiusFilterKm == 2.0) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (radiusFilterKm == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { onRadiusFilterChange(null) }
                    ) {
                        Text(
                            text = "Any Distance",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (radiusFilterKm == null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    distanceOptions.take(6).forEach { opt ->
                        val isSelected = radiusFilterKm == opt.km
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { onRadiusFilterChange(opt.km) }
                        ) {
                            Text(
                                text = "${opt.icon} ${opt.shortLabel}",
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sort Selector Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "Sort:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    sortOptions.forEach { (key, label) ->
                        val isSelected = sortOption == key
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { onSortOptionChange(key) }
                                .testTag("market_sort_$key")
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // AI Price Trends Widget
                NeighborhoodMarketPriceTrendsComponent(
                    currentCurrency = currentCurrency,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Safe Physical Exchange Banner
                VerifiedSafeExchangeSpotCard(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        if (items.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matching neighborhood listings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try expanding your distance radius or resetting active category filters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onResetFilters) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            items(items, key = { it.id }) { item ->
                MarketItemCard(
                    item = item,
                    currentCurrency = currentCurrency,
                    onClick = { onItemClick(item) },
                    onToggleSave = { onToggleSaveItem(item) },
                    onOpenConditionTour = { onOpenConditionTour(item) },
                    onFlagItemClick = { onFlagItemClick(item) },
                    onUserProfileClick = onUserProfileClick
                )
            }
        }
    }
}

@Composable
fun MarketItemCard(
    item: MarketplaceItemEntity,
    currentCurrency: LocaliiiyCurrency,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    onOpenConditionTour: () -> Unit,
    onFlagItemClick: () -> Unit,
    onUserProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("market_card_${item.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top-left urgency or borrow badge
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (item.isFlashDeal) {
                        UrgentTodayTimerBadge(hoursRemaining = 6)
                    } else if (item.price == 0.0 || item.category.equals("Borrow & Barter", ignoreCase = true)) {
                        BorrowAndLendBadge()
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = item.condition,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top-right Save & Flag buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .testTag("pin_item_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Listing",
                            tint = if (item.isSaved) Color(0xFFFFB703) else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(
                        onClick = onFlagItemClick,
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .testTag("flag_item_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Flag Listing",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Bottom-left 10s Tour Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenConditionTour() }
                        .testTag("market_card_tour_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "10s Video Tour",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "10s Tour",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Sold overlay if unavailable
                if (!item.isAvailable) {
                    ClaimedSoldRibbonOverlay()
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (item.price == 0.0) "FREE / BARTER" else CurrencyHelper.format(item.price, currentCurrency),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (item.category.equals("Art & Crafts", ignoreCase = true)) {
                    HandmadeArtisanBadge(
                        creatorWorkshop = item.catalogName ?: "Local Studio",
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${item.distanceKm} km • ${item.landmark ?: item.location}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUserProfileClick(item.sellerUsername) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                            model = item.sellerAvatar,
                            contentDescription = item.sellerFullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = item.sellerFullName,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "⭐ ${item.sellerRating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }
        }
    }
}

@Composable
fun SellerCatalogsCatalogView(
    items: List<MarketplaceItemEntity>,
    currentCurrency: LocaliiiyCurrency,
    onSelectCatalog: (SellerCatalog) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Verified Neighborhood Storefronts & Artisan Catalogs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Browse multi-item collections from trusted neighborhood creators and workshops.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(defaultSellerCatalogs, key = { it.id }) { catalog ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCatalog(catalog) }
                    .testTag("seller_catalog_${catalog.id}")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
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
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFB703),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "★ ${catalog.badge}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = catalog.sellerAvatar,
                                contentDescription = catalog.sellerFullName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                            Column {
                                Text(
                                    text = catalog.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "by ${catalog.sellerFullName} • ⭐ ${catalog.rating} (${catalog.totalItems} listings)",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = catalog.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val previewItems = items.filter {
                            it.sellerUsername.equals(catalog.sellerUsername, ignoreCase = true)
                        }.ifEmpty { items.take(3) }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(previewItems) { preview ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clickable { onItemClick(preview) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        AsyncImage(
                                            model = preview.imageUrl,
                                            contentDescription = preview.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = preview.title,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = CurrencyHelper.format(preview.price, currentCurrency),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
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
    catalogItems: List<MarketplaceItemEntity>,
    currentCurrency: LocaliiiyCurrency,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.84f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
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
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = catalog.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "${catalog.sellerFullName} • ★ ${catalog.badge}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.clickable { onUserProfileClick(catalog.sellerUsername) }
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Text(
                            text = catalog.description,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(catalogItems, key = { it.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(item) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = "${item.condition} • ${item.distanceKm} km away",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CurrencyHelper.format(item.price, currentCurrency),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketClipsFeedView(
    clips: List<ClipEntity>,
    items: List<MarketplaceItemEntity>,
    currentCurrency: LocaliiiyCurrency,
    onConvertClipToMarket: (Long, Double, String, String) -> Unit,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    var clipToConvert by remember { mutableStateOf<ClipEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "🎬 Shoppable Neighborhood Video Showcase",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Watch 10-second working demos and convert creator clips into instant marketplace listings.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "Play Clip",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyHelper.format(item.price, currentCurrency),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "@${item.sellerUsername} • ${item.distanceKm} km away",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { onUserProfileClick(item.sellerUsername) }
                        )
                    }
                }
            }
        }

        if (clips.isNotEmpty()) {
            items(clips, key = { it.id }) { clip ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = clip.caption,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                maxLines = 2
                            )
                            Text(
                                text = "by @${clip.username}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(onClick = { clipToConvert = clip }) {
                            Text("Tag Price", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (clipToConvert != null) {
        MarketConvertClipDialog(
            clip = clipToConvert!!,
            onDismiss = { clipToConvert = null },
            onConfirm = { price, cat, cond ->
                onConvertClipToMarket(clipToConvert!!.id, price, cat, cond)
                clipToConvert = null
            }
        )
    }
}

@Composable
fun MarketConvertClipDialog(
    clip: ClipEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var priceText by remember { mutableStateOf("45") }
    var category by remember { mutableStateOf("Mobiles & Tech") }
    var condition by remember { mutableStateOf("Like New") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Attach Market Listing to Clip", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = clip.caption, fontSize = 12.sp, maxLines = 2)
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(priceText.toDoubleOrNull() ?: 25.0, category, condition)
            }) {
                Text("Convert to Listing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MarketPostsFeedView(
    posts: List<PostEntity>,
    items: List<MarketplaceItemEntity>,
    currentCurrency: LocaliiiyCurrency,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onOpenComments: (String, Long) -> Unit,
    onOpenSellDialog: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📸 Neighborhood Buy / Sell / Trade Posts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Community-driven commerce pulses with direct Q&A.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(onClick = onOpenSellDialog) {
                    Text("+ Post Deal", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(items, key = { it.id }) { item ->
            MarketPostCard(
                item = item,
                currentCurrency = currentCurrency,
                onClick = { onItemClick(item) },
                onUserProfileClick = onUserProfileClick,
                onOpenComments = { onOpenComments("MARKET", item.id) }
            )
        }
    }
}

@Composable
fun MarketPostCard(
    item: MarketplaceItemEntity,
    currentCurrency: LocaliiiyCurrency,
    onClick: () -> Unit,
    onUserProfileClick: (String) -> Unit,
    onOpenComments: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onUserProfileClick(item.sellerUsername) }
                ) {
                    AsyncImage(
                        model = item.sellerAvatar,
                        contentDescription = item.sellerFullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Text(
                            text = item.sellerFullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${item.location} • ${item.distanceKm} km away",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (item.price == 0.0) "BARTER" else CurrencyHelper.format(item.price, currentCurrency),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerifiedMeetupsReputationBadge(safeHandoversCount = item.sellerReviewCount)
                TextButton(onClick = onOpenComments) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask Seller Q&A", fontSize = 11.5.sp)
                }
            }
        }
    }
}

@Composable
fun WatchlistCatalogView(
    savedItems: List<MarketplaceItemEntity>,
    currentCurrency: LocaliiiyCurrency,
    onItemClick: (MarketplaceItemEntity) -> Unit,
    onToggleSaveItem: (MarketplaceItemEntity) -> Unit,
    onOpenConditionTour: (MarketplaceItemEntity) -> Unit,
    onFlagItemClick: (MarketplaceItemEntity) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    if (savedItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Saved Watchlist is Empty",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap the bookmark icon on any neighborhood listing to track price drops.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                PriceDropAlertBanner(discountPercent = 15)
            }
            items(savedItems, key = { it.id }) { item ->
                MarketItemCard(
                    item = item,
                    currentCurrency = currentCurrency,
                    onClick = { onItemClick(item) },
                    onToggleSave = { onToggleSaveItem(item) },
                    onOpenConditionTour = { onOpenConditionTour(item) },
                    onFlagItemClick = { onFlagItemClick(item) },
                    onUserProfileClick = onUserProfileClick
                )
            }
        }
    }
}

@Composable
fun MarketLocationPickerModal(
    currentLocationQuery: String,
    currentRadiusKm: Double?,
    countryName: String?,
    onApplyLocation: (String, Double?) -> Unit,
    onDismiss: () -> Unit
) {
    var locInput by remember { mutableStateOf(currentLocationQuery) }
    var selectedRadius by remember { mutableStateOf(currentRadiusKm) }
    val presets = listOf(
        "" to "Near Me (Current GPS)",
        "Arts District" to "Arts District",
        "Central Plaza" to "Central Plaza",
        "Old Town" to "Old Town",
        "Waterfront" to "Waterfront",
        "Greenway" to "Greenway"
    )
    val distanceOpts = remember(countryName) { SystematicDistanceScale.getOptions(countryName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("📍 Market Location & Radius", fontWeight = FontWeight.ExtraBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = locInput,
                    onValueChange = { locInput = it },
                    label = { Text("Neighborhood, Landmark, or City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Quick Zones:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presets) { (value, label) ->
                        FilterChip(
                            selected = locInput == value,
                            onClick = { locInput = value },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
                Text("Discovery Radius:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedRadius == null,
                            onClick = { selectedRadius = null },
                            label = { Text("Any Distance", fontSize = 11.sp) }
                        )
                    }
                    items(distanceOpts.take(6)) { opt ->
                        FilterChip(
                            selected = selectedRadius == opt.km,
                            onClick = { selectedRadius = opt.km },
                            label = { Text("${opt.icon} ${opt.shortLabel}", fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApplyLocation(locInput.trim(), selectedRadius) }) {
                Text("Apply Zone")
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
fun MarketItemDetailDialog(
    item: MarketplaceItemEntity,
    posts: List<PostEntity>,
    clips: List<ClipEntity>,
    currentCurrency: LocaliiiyCurrency,
    onDismiss: () -> Unit,
    onMessageSeller: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleAvailability: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenCheckoutEscrow: () -> Unit,
    onOpenConditionTour: () -> Unit,
    onFlagListing: () -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    var isGhostActive by remember { mutableStateOf(false) }
    var showHandoverQrDialog by remember { mutableStateOf(false) }
    var autoNegotiateOffer by remember { mutableStateOf<Double?>(null) }

    val conditionScore = remember(item.condition) {
        when (item.condition.lowercase()) {
            "brand new" -> 98
            "like new" -> 92
            "good" -> 82
            else -> 72
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .testTag("market_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clickable { onOpenConditionTour() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Text("Watch 10s Condition Tour", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${item.category} • ${item.distanceKm} km away (${item.landmark ?: item.location})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (item.price == 0.0) "FREE" else CurrencyHelper.format(item.price, currentCurrency),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (item.price > 0.0) {
                        ConnectionDiscountBadge(
                            discountPercent = 15,
                            originalPrice = item.price,
                            currency = currentCurrency
                        )
                    }

                    // Condition Dial + Smart Offer Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConditionVisualizerDial(
                            conditionLabel = item.condition,
                            scorePercent = conditionScore
                        )
                        if (item.price > 0.0) {
                            AutoNegotiateButton(
                                originalPrice = item.price,
                                currentCurrency = currentCurrency,
                                onOfferGenerated = { autoNegotiateOffer = it }
                            )
                        }
                    }

                    if (autoNegotiateOffer != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF064E3B),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Smart Fair Offer Ready: ${CurrencyHelper.format(autoNegotiateOffer!!, currentCurrency)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                TextButton(onClick = onMessageSeller) {
                                    Text("Send Offer", color = Color(0xFF34D399), fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ItemDimensionsRulerBadge()

                    PaymentMethodChipsRow(
                        selectedMethods = listOf("💵 Cash on Meetup", "📱 Digital Transfer", "🔄 Barter & Trade")
                    )

                    VerifiedSafeExchangeSpotCard(
                        spotName = item.safeHavenHubName ?: "Civic Plaza Police Precinct & Monitored Station"
                    )

                    GhostNegotiationPrivacyShield(
                        isGhostActive = isGhostActive,
                        onToggleGhost = { isGhostActive = !isGhostActive }
                    )

                    QuickInquiryActionsRow(
                        onSendQuestion = { onMessageSeller() }
                    )

                    CreatorPortfolioLinkedGrid(
                        sellerUsername = item.sellerUsername,
                        posts = posts,
                        clips = clips
                    )

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onMessageSeller,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Message Seller", fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = onOpenCheckoutEscrow,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Safe Escrow", fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showHandoverQrDialog = true }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("QR Handshake", fontSize = 12.sp)
                        }
                        TextButton(onClick = onOpenComments) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Public Q&A", fontSize = 12.sp)
                        }
                        TextButton(onClick = onToggleAvailability) {
                            Text(if (item.isAvailable) "Mark Sold" else "Relist", fontSize = 12.sp)
                        }
                        IconButton(onClick = onFlagListing) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Flag Listing",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHandoverQrDialog) {
        InPersonHandoverQRDialog(
            itemTitle = item.title,
            sellerHandle = item.sellerUsername,
            onDismiss = { showHandoverQrDialog = false },
            onConfirmHandover = {
                showHandoverQrDialog = false
                onToggleAvailability()
            }
        )
    }
}

@Composable
fun ConditionVisualizerDial(
    conditionLabel: String,
    scorePercent: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.25f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF10B981),
                    startAngle = 135f,
                    sweepAngle = 270f * (scorePercent / 100f),
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = "$scorePercent%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF10B981)
            )
        }
        Column {
            Text(
                text = "Condition: $conditionLabel",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Verified Inspection Grade",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AutoNegotiateButton(
    originalPrice: Double,
    currentCurrency: LocaliiiyCurrency,
    onOfferGenerated: (Double) -> Unit
) {
    OutlinedButton(
        onClick = {
            val suggested = (originalPrice * 0.88).toInt().toDouble()
            onOfferGenerated(suggested)
        },
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Smart Negotiate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MarketCheckoutEscrowDialog(
    item: MarketplaceItemEntity,
    currentCurrency: LocaliiiyCurrency,
    onDismiss: () -> Unit,
    onCompleteEscrow: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981))
                Text("Safe-Haven Escrow Hold", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Reserve '${item.title}' for ${CurrencyHelper.format(item.price, currentCurrency)} in zero-fee neighborhood escrow.",
                    fontSize = 13.sp
                )
                Text(
                    text = "• Funds are held until you inspect the item in person at ${item.safeHavenHubName ?: "Civic Plaza Safe Zone"} and scan the mutual Handshake QR code.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCompleteEscrow,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Lock Escrow & Reserve", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketSellMultiDialog(
    currentCurrency: LocaliiiyCurrency,
    onDismiss: () -> Unit,
    onPublishItem: (String, String, Double, String, String, String, String, String?, String?) -> Unit,
    onPublishBuySellPost: (String, String, Double, String, String, String, String, String?, String?) -> Unit,
    onPublishBuySellClip: (String, String, Double, String, String, String, String?, String?, String?) -> Unit
) {
    var creationFormat by remember { mutableStateOf(SellCreationFormat.CATALOG_ITEM) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var isBarterOrFree by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Mobiles & Tech") }
    var selectedCondition by remember { mutableStateOf("Like New") }
    var selectedPhotoUrl by remember { mutableStateOf(sampleMarketPhotos.first()) }
    var safeHavenHub by remember { mutableStateOf("Civic Plaza Police Precinct (CCTV Zone)") }
    var catalogName by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "List on Localiiiy Market",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Format Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val formats = listOf(
                        SellCreationFormat.CATALOG_ITEM to "🏷️ Catalog Item",
                        SellCreationFormat.BUY_SELL_POST to "📸 Buy/Sell Post",
                        SellCreationFormat.SHOWCASE_CLIP to "🎥 Video Clip"
                    )
                    formats.forEach { (fmt, label) ->
                        val selected = creationFormat == fmt
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { creationFormat = fmt }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Item or Service Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = if (isBarterOrFree) "0" else priceText,
                            onValueChange = { priceText = it },
                            enabled = !isBarterOrFree,
                            label = { Text("Price (${currentCurrency.symbol})") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = isBarterOrFree,
                            onClick = { isBarterOrFree = !isBarterOrFree },
                            label = { Text("🔄 Free / Barter") }
                        )
                    }

                    Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(marketCategories.filter { it.name != "All" }) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = { selectedCategory = cat.name },
                                label = { Text(cat.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text("Condition:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val conditions = listOf("Brand New", "Like New", "Good", "Fair")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(conditions) { cond ->
                            FilterChip(
                                selected = selectedCondition == cond,
                                onClick = { selectedCondition = cond },
                                label = { Text(cond, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description, Specs & Meetup Notes") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = safeHavenHub,
                        onValueChange = { safeHavenHub = it },
                        label = { Text("Safe-Haven Exchange Spot") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = catalogName,
                        onValueChange = { catalogName = it },
                        label = { Text("Storefront Catalog Name (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Listing Photo:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sampleMarketPhotos) { photoUrl ->
                            val isSelected = selectedPhotoUrl == photoUrl
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedPhotoUrl = photoUrl }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val finalTitle = title.trim().ifEmpty { "Neighborhood Listing" }
                        val finalDesc = description.trim().ifEmpty { "Available for local inspection at $safeHavenHub." }
                        val finalPrice = if (isBarterOrFree) 0.0 else (priceText.toDoubleOrNull() ?: 25.0)
                        val catStore = catalogName.trim().ifEmpty { null }
                        when (creationFormat) {
                            SellCreationFormat.CATALOG_ITEM -> onPublishItem(
                                finalTitle,
                                finalDesc,
                                finalPrice,
                                selectedCategory,
                                selectedCondition,
                                selectedPhotoUrl,
                                "Local Meetup / Pickup",
                                safeHavenHub,
                                catStore
                            )
                            SellCreationFormat.BUY_SELL_POST -> onPublishBuySellPost(
                                finalTitle,
                                finalDesc,
                                finalPrice,
                                selectedCategory,
                                selectedCondition,
                                selectedPhotoUrl,
                                "Local Meetup / Pickup",
                                safeHavenHub,
                                catStore
                            )
                            SellCreationFormat.SHOWCASE_CLIP -> onPublishBuySellClip(
                                finalTitle,
                                finalDesc,
                                finalPrice,
                                selectedCategory,
                                selectedCondition,
                                selectedPhotoUrl,
                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                                safeHavenHub,
                                catStore
                            )
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Publish to Neighborhood Market", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
