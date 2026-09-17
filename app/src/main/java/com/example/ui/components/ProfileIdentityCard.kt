package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.PostEntity
import com.example.data.StudioVideoEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.EditorialVerified

fun formatCompactNumber(count: Long): String {
    if (count < 1000) return count.toString()
    if (count < 100_000) { // 1K to 99.9K
        val k = count / 1000.0
        return if (k % 1.0 == 0.0 || count < 1100) "${count / 1000}K" else String.format("%.1fK", k)
    }
    if (count < 1_000_000) { // 1L to 9.9L
        val l = count / 100_000.0
        return if (l % 1.0 == 0.0 || count < 110_000) "${count / 100_000}L" else String.format("%.1fL", l)
    }
    val m = count / 1_000_000.0
    return if (m % 1.0 == 0.0 || count < 1_100_000) "${count / 1_000_000}M" else String.format("%.1fM", m)
}

@Composable
fun ProfileIdentityCard(
    userProfile: UserProfileEntity,
    postsCount: Int,
    clipsCount: Int,
    marketItemsCount: Int = 0,
    studioVideosCount: Int = 0,
    isOtherUser: Boolean = false,
    isGhostMode: Boolean = false,
    userPosts: List<PostEntity> = emptyList(),
    userClips: List<ClipEntity> = emptyList(),
    userMarketItems: List<MarketplaceItemEntity> = emptyList(),
    userStudioVideos: List<StudioVideoEntity> = emptyList(),
    onChangeAvatar: ((String) -> Unit)? = null,
    onPostClick: ((PostEntity) -> Unit)? = null,
    onClipClick: ((ClipEntity) -> Unit)? = null,
    onMarketItemClick: ((MarketplaceItemEntity) -> Unit)? = null,
    onStudioVideoClick: ((StudioVideoEntity) -> Unit)? = null,
    onCreatePostClick: (() -> Unit)? = null,
    onViewOtherUserId: ((String) -> Unit)? = null,
    onSendConnectionToUser: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // 3D Holographic Tilt State
    var tiltOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var showFullQrDialog by remember { mutableStateOf(false) }
    var showChangePhotoDialog by remember { mutableStateOf(false) }
    var showFullPhotoDialog by remember { mutableStateOf(false) }
    var showListDialog by remember { mutableStateOf(false) }
    var listDialogTitle by remember { mutableStateOf("") }
    var viewingDetailUser by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    
    val tiltX by animateFloatAsState(
        targetValue = if (isDragging) tiltOffset.y / 20f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tiltX"
    )
    val tiltY by animateFloatAsState(
        targetValue = if (isDragging) -tiltOffset.x / 20f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tiltY"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                rotationX = tiltX
                rotationY = tiltY
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { 
                        isDragging = false
                        tiltOffset = Offset.Zero 
                    },
                    onDragCancel = { 
                        isDragging = false
                        tiltOffset = Offset.Zero 
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        tiltOffset += dragAmount
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isDragging) 12.dp else 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        // Holographic Gradient Overlay
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isDragging) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(tiltOffset.x * 5, tiltOffset.y * 5)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                
        // Header: Localiiiy ID
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFF59E0B))))
                    .clickable { showFullQrDialog = true }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LOCALIIIY ID",
                color = Color.Transparent,
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFF59E0B))
                    ),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontSize = 24.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Details Row: Info (Left) - Photo (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info (Left)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 14.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = userProfile.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
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
                Text(
                    text = "@${userProfile.username}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = userProfile.neighborhood,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Photo (Right) - Clickable to change photo or view full
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        if (!isOtherUser) {
                            showChangePhotoDialog = true
                        } else {
                            showFullPhotoDialog = true
                        }
                    }
                    .testTag("profile_identity_photo_box")
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = if (!isOtherUser) "Tap to change photo" else userProfile.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(2.dp).clip(RoundedCornerShape(16.dp))
                )

                // Sleek camera badge for own profile
                if (!isOtherUser) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        Text(
            text = userProfile.bio,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (userProfile.website.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🔗 ${userProfile.website}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mutual Connections Section for Other User
        if (isOtherUser) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Can open mutual connections dialog
                    }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MUTUAL CONNECTIONS (12 SHARED)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stacked avatar row
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            listOf(
                                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80",
                                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop&q=80",
                                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&auto=format&fit=crop&q=80"
                            ).forEach { avatar ->
                                AsyncImage(
                                    model = avatar,
                                    contentDescription = "Mutual",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                )
                            }
                        }
                        Text(
                            text = "Maya Patel, Liam Vance, Elena Rostova and 9 others",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sample Connections & Connected Data for interactive lists
        val sampleMutualUsers = remember {
            listOf(
                Triple("Maya Patel", "maya_sound", "Capitol Hill • Seattle"),
                Triple("Liam Vance", "liam_craft", "Fremont • Seattle"),
                Triple("Elena Rostova", "elena_visuals", "Ballard • Seattle"),
                Triple("Marcus Chen", "marcus_dev", "Belltown • Seattle"),
                Triple("Sofia Taylor", "sofia_lens", "Pioneer Square • Seattle"),
                Triple("Jordan Lee", "jordan_local", "Queen Anne • Seattle")
            )
        }
        var connectedUserSet by remember { mutableStateOf(setOf("maya_sound")) }

        // Fallback/display lists for user's content
        val displayPosts = remember(userPosts, userProfile.username) {
            if (userPosts.isNotEmpty()) userPosts
            else listOf(
                PostEntity(
                    id = 101L,
                    username = userProfile.username,
                    userAvatar = userProfile.avatarUrl,
                    userHandle = "@${userProfile.username}",
                    isVerified = userProfile.isVerified,
                    mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                    caption = "Golden hour vibes across ${userProfile.neighborhood} 🌇 Exploring local spots.",
                    likesCount = 284,
                    commentsCount = 31,
                    location = userProfile.neighborhood,
                    landmark = "Scenic Viewpoint"
                ),
                PostEntity(
                    id = 102L,
                    username = userProfile.username,
                    userAvatar = userProfile.avatarUrl,
                    userHandle = "@${userProfile.username}",
                    isVerified = userProfile.isVerified,
                    mediaUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&auto=format&fit=crop&q=80",
                    caption = "Best morning espresso in town ☕ Support local neighborhood baristas!",
                    likesCount = 192,
                    commentsCount = 18,
                    location = userProfile.neighborhood,
                    landmark = "Artisan Cafe"
                ),
                PostEntity(
                    id = 103L,
                    username = userProfile.username,
                    userAvatar = userProfile.avatarUrl,
                    userHandle = "@${userProfile.username}",
                    isVerified = userProfile.isVerified,
                    mediaUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&auto=format&fit=crop&q=80",
                    caption = "Street art discovery walk 🎨 Always love the creative pulse in this corner.",
                    likesCount = 345,
                    commentsCount = 42,
                    location = userProfile.neighborhood,
                    landmark = "Street Art Alley"
                )
            )
        }

        val displayClips = remember(userClips, userProfile.username) {
            if (userClips.isNotEmpty()) userClips
            else listOf(
                ClipEntity(
                    id = 201L,
                    username = userProfile.username,
                    userAvatar = userProfile.avatarUrl,
                    userHandle = "@${userProfile.username}",
                    isVerified = userProfile.isVerified,
                    mediaUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800&auto=format&fit=crop&q=80",
                    caption = "Quick walk through the night market ✨ Street food & soundscapes #LocalVibes",
                    soundTitle = "Midnight Pulse • Original",
                    likesCount = 412,
                    commentsCount = 56,
                    viewsCount = "18.5K",
                    location = userProfile.neighborhood
                ),
                ClipEntity(
                    id = 202L,
                    username = userProfile.username,
                    userAvatar = userProfile.avatarUrl,
                    userHandle = "@${userProfile.username}",
                    isVerified = userProfile.isVerified,
                    mediaUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                    caption = "Rooftop view of the waterfront at dusk 🌊🎧",
                    soundTitle = "Pacific Chill • Ambient",
                    likesCount = 680,
                    commentsCount = 89,
                    viewsCount = "34.2K",
                    location = userProfile.neighborhood
                )
            )
        }

        val displayMarketItems = remember(userMarketItems, userProfile.username) {
            if (userMarketItems.isNotEmpty()) userMarketItems
            else listOf(
                MarketplaceItemEntity(
                    id = 301L,
                    title = "Vintage 35mm Manual Film Camera",
                    description = "Great condition, tested and working. Perfect for street photography in the neighborhood.",
                    price = 65.0,
                    category = "Tech & Gear",
                    condition = "Good",
                    imageUrl = "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&auto=format&fit=crop&q=80",
                    sellerUsername = userProfile.username,
                    sellerFullName = userProfile.fullName,
                    sellerAvatar = userProfile.avatarUrl,
                    location = userProfile.neighborhood
                ),
                MarketplaceItemEntity(
                    id = 302L,
                    title = "Handcrafted Ceramic Coffee Mug & Saucer",
                    description = "Locally fired stoneware clay. Microwave and dishwasher safe.",
                    price = 28.0,
                    category = "Art & Craft",
                    condition = "Brand New",
                    imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
                    sellerUsername = userProfile.username,
                    sellerFullName = userProfile.fullName,
                    sellerAvatar = userProfile.avatarUrl,
                    location = userProfile.neighborhood
                )
            )
        }

        val displayStudioVideos = remember(userStudioVideos, userProfile.username) {
            if (userStudioVideos.isNotEmpty()) userStudioVideos
            else listOf(
                StudioVideoEntity(
                    id = 401L,
                    title = "Exploring the Hidden Alleys of ${userProfile.neighborhood}",
                    description = "A deep dive walking tour and architectural history of our neighborhood's best spots.",
                    videoUrl = "https://example.com/studio1.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 840,
                    category = "Documentaries",
                    creatorUsername = userProfile.username,
                    creatorFullName = userProfile.fullName,
                    creatorAvatar = userProfile.avatarUrl,
                    viewsFormatted = "12.4K views"
                ),
                StudioVideoEntity(
                    id = 402L,
                    title = "Community Spotlight: Artisans & Creators",
                    description = "Interviews with local makers crafting unique goods right next door.",
                    videoUrl = "https://example.com/studio2.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 1260,
                    category = "Neighborhood & Culture",
                    creatorUsername = userProfile.username,
                    creatorFullName = userProfile.fullName,
                    creatorAvatar = userProfile.avatarUrl,
                    viewsFormatted = "28.1K views"
                )
            )
        }

        // Stats Grid
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ==========================================
            // CHANGE PHOTO DIALOG (User's Space Profile)
            // ==========================================
            if (showChangePhotoDialog) {
                var customUrlInput by remember { mutableStateOf("") }
                var selectedPresetUrl by remember { mutableStateOf(userProfile.avatarUrl) }

                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    uri?.let {
                        onChangeAvatar?.invoke(it.toString())
                        showChangePhotoDialog = false
                    }
                }

                val presetAvatars = listOf(
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=500&auto=format&fit=crop&q=80"
                )

                AlertDialog(
                    onDismissRequest = { showChangePhotoDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Change Space Photo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Current preview
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.linearGradient(
                                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(selectedPresetUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                                )
                            }

                            Text(
                                text = "Upload a photo from your device storage or pick a creator avatar for your Localiiiy ID.",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            // Option 1: Native Storage Photo Picker
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("photo_picker_storage_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Choose from Storage / Gallery 📱",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                            // Option 2: Quick Creator Avatars
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Or Choose Creator Avatar:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    presetAvatars.forEach { presetUrl ->
                                        val isSelected = selectedPresetUrl == presetUrl
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable {
                                                    selectedPresetUrl = presetUrl
                                                }
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(presetUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(13.dp))
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                            // Option 3: Custom URL
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Or Enter Photo URL:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = customUrlInput,
                                    onValueChange = {
                                        customUrlInput = it
                                        if (it.isNotBlank()) selectedPresetUrl = it
                                    },
                                    placeholder = { Text("https://example.com/photo.jpg", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onChangeAvatar?.invoke(selectedPresetUrl)
                                showChangePhotoDialog = false
                            },
                            modifier = Modifier.testTag("apply_photo_button")
                        ) {
                            Text("Save Photo")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showChangePhotoDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // ==========================================
            // FULL PHOTO DIALOG (Other Users)
            // ==========================================
            if (showFullPhotoDialog) {
                AlertDialog(
                    onDismissRequest = { showFullPhotoDialog = false },
                    title = {
                        Text(
                            text = "${userProfile.fullName}'s Photo",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(userProfile.avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = userProfile.username,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp))
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "@${userProfile.username}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFullPhotoDialog = false }) { Text("Close") }
                    }
                )
            }

            // ==========================================
            // STATS POP-UP DIALOG (Posts, Clips, Market, Studio, Mutual, Connections, Connected)
            // ==========================================
            if (showListDialog) {
                val (dialogIcon, dialogSubtitle) = when (listDialogTitle) {
                    "Posts" -> Icons.Default.GridOn to "Uploaded Posts by @${userProfile.username}"
                    "Clips" -> Icons.Default.PlayCircleOutline to "Short Clips & Videos by @${userProfile.username}"
                    "Market" -> Icons.Default.Storefront to "Marketplace Listings by @${userProfile.username}"
                    "Studio" -> Icons.Default.VideoLibrary to "Studio Productions by @${userProfile.username}"
                    "Mutual" -> Icons.Default.People to "Mutual Connections with @${userProfile.username}"
                    "Connections" -> Icons.Default.People to "Connections of @${userProfile.username}"
                    "Connected" -> Icons.Default.PersonAdd to "Connected with @${userProfile.username}"
                    else -> Icons.Default.People to "@${userProfile.username}"
                }

                AlertDialog(
                    onDismissRequest = { showListDialog = false },
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = dialogIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = when (listDialogTitle) {
                                        "Posts" -> "Uploaded Posts (${displayPosts.size})"
                                        "Clips" -> "Uploaded Clips (${displayClips.size})"
                                        "Market" -> "Market Listings (${displayMarketItems.size})"
                                        "Studio" -> "Studio Videos (${displayStudioVideos.size})"
                                        "Mutual" -> "Mutual Connections"
                                        else -> listDialogTitle
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = dialogSubtitle,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    text = { 
                        when (listDialogTitle) {
                            "Posts" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (displayPosts.isEmpty()) {
                                        EmptyListState(
                                            icon = Icons.Default.GridOn,
                                            message = "No uploaded posts found.",
                                            actionLabel = if (!isOtherUser) "+ Create Post" else null,
                                            onAction = {
                                                showListDialog = false
                                                onCreatePostClick?.invoke()
                                            }
                                        )
                                    } else {
                                        displayPosts.forEach { post ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable {
                                                        onPostClick?.invoke(post)
                                                        showListDialog = false
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(62.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(MaterialTheme.colorScheme.surface)
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(post.mediaUrl)
                                                                .crossfade(true)
                                                                .build(),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = post.caption,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(3.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Favorite,
                                                                    contentDescription = null,
                                                                    tint = Color(0xFFEF4444),
                                                                    modifier = Modifier.size(12.dp)
                                                                )
                                                                Text("${post.likesCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.ChatBubbleOutline,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    modifier = Modifier.size(12.dp)
                                                                )
                                                                Text("${post.commentsCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                            if (post.location != null) {
                                                                Text(
                                                                    text = "• ${post.location}",
                                                                    fontSize = 10.5.sp,
                                                                    color = MaterialTheme.colorScheme.primary,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(100.dp),
                                                        color = MaterialTheme.colorScheme.primaryContainer,
                                                        modifier = Modifier.clip(RoundedCornerShape(100.dp))
                                                    ) {
                                                        Text(
                                                            text = "View",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Clips" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (displayClips.isEmpty()) {
                                        EmptyListState(
                                            icon = Icons.Default.PlayCircleOutline,
                                            message = "No uploaded clips found.",
                                            actionLabel = if (!isOtherUser) "+ Record Clip" else null,
                                            onAction = {
                                                showListDialog = false
                                                onCreatePostClick?.invoke()
                                            }
                                        )
                                    } else {
                                        displayClips.forEach { clip ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable {
                                                        onClipClick?.invoke(clip)
                                                        showListDialog = false
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(52.dp, 72.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color.Black),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(clip.mediaUrl)
                                                                .crossfade(true)
                                                                .build(),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .clip(CircleShape)
                                                                .background(Color.Black.copy(alpha = 0.6f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.PlayArrow,
                                                                contentDescription = null,
                                                                tint = Color.White,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = clip.caption,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(3.dp))
                                                        Text(
                                                            text = "🎵 ${clip.soundTitle}",
                                                            fontSize = 10.5.sp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text("👁️ ${clip.viewsCount}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text("❤️ ${clip.likesCount}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(100.dp),
                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                        modifier = Modifier.clip(RoundedCornerShape(100.dp))
                                                    ) {
                                                        Text(
                                                            text = "Watch",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Market" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (displayMarketItems.isEmpty()) {
                                        EmptyListState(
                                            icon = Icons.Default.Storefront,
                                            message = "No marketplace listings found.",
                                            actionLabel = if (!isOtherUser) "+ List Item" else null,
                                            onAction = {
                                                showListDialog = false
                                                onCreatePostClick?.invoke()
                                            }
                                        )
                                    } else {
                                        displayMarketItems.forEach { item ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable {
                                                        onMarketItemClick?.invoke(item)
                                                        showListDialog = false
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(62.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(MaterialTheme.colorScheme.surface)
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(item.imageUrl)
                                                                .crossfade(true)
                                                                .build(),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = item.title,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = "${item.currencySymbol}${item.price.toInt()} • ${item.category}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Surface(
                                                                shape = RoundedCornerShape(100.dp),
                                                                color = if (item.isAvailable) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                                            ) {
                                                                Text(
                                                                    text = if (item.isAvailable) "Active" else "Sold",
                                                                    fontSize = 9.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (item.isAvailable) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                            Text(
                                                                text = item.condition,
                                                                fontSize = 10.5.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(100.dp),
                                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                                        modifier = Modifier.clip(RoundedCornerShape(100.dp))
                                                    ) {
                                                        Text(
                                                            text = "Details",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Studio" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (displayStudioVideos.isEmpty()) {
                                        EmptyListState(
                                            icon = Icons.Default.VideoLibrary,
                                            message = "No studio videos uploaded yet.",
                                            actionLabel = if (!isOtherUser) "+ Upload Video" else null,
                                            onAction = {
                                                showListDialog = false
                                                onCreatePostClick?.invoke()
                                            }
                                        )
                                    } else {
                                        displayStudioVideos.forEach { video ->
                                            val minutes = video.durationSeconds / 60
                                            val seconds = video.durationSeconds % 60
                                            val durationFormatted = String.format("%d:%02d", minutes, seconds)
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable {
                                                        onStudioVideoClick?.invoke(video)
                                                        showListDialog = false
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(76.dp, 48.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color.Black)
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(video.thumbnailUrl)
                                                                .crossfade(true)
                                                                .build(),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = Color.Black.copy(alpha = 0.75f),
                                                            modifier = Modifier
                                                                .align(Alignment.BottomEnd)
                                                                .padding(3.dp)
                                                        ) {
                                                            Text(
                                                                text = durationFormatted,
                                                                fontSize = 9.sp,
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = video.title,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 12.5.sp,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text(
                                                                text = video.category,
                                                                fontSize = 10.sp,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                            Text("• ${video.viewsFormatted}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(100.dp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.clip(RoundedCornerShape(100.dp))
                                                    ) {
                                                        Text(
                                                            text = "Play",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Mutual" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 340.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    sampleMutualUsers.take(3).forEach { (name, handle, loc) ->
                                        val isConnected = connectedUserSet.contains(handle)
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.primaryContainer,
                                                        modifier = Modifier.size(36.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = name.first().toString(),
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                            )
                                                        }
                                                    }
                                                    Column {
                                                        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        Text("@$handle • $loc", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Button(
                                                        onClick = {
                                                            connectedUserSet = if (isConnected) {
                                                                connectedUserSet - handle
                                                            } else {
                                                                onSendConnectionToUser?.invoke(handle)
                                                                connectedUserSet + handle
                                                            }
                                                        },
                                                        shape = RoundedCornerShape(100.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (isConnected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                                                        ),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isConnected) "Connected" else "Connect",
                                                            fontSize = 10.5.sp,
                                                            color = if (isConnected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {
                                if (isGhostMode || (isOtherUser && userProfile.username.contains("ghost"))) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("🛡️", fontSize = 24.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Ghost Mode Active",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Connections and Connected lists are confidential in Ghost Mode to protect user identity and privacy in the neighborhood.",
                                            fontSize = 12.5.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 340.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "$listDialogTitle for @${userProfile.username}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        sampleMutualUsers.forEach { (name, handle, loc) ->
                                            val isConnected = connectedUserSet.contains(handle)
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = MaterialTheme.colorScheme.primaryContainer,
                                                            modifier = Modifier.size(36.dp)
                                                        ) {
                                                            Box(contentAlignment = Alignment.Center) {
                                                                Text(
                                                                    text = name.first().toString(),
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                                )
                                                            }
                                                        }
                                                        Column {
                                                            Text(
                                                                text = name,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = "@$handle • $loc",
                                                                fontSize = 10.5.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }

                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        // Send Connection button
                                                        Surface(
                                                            shape = RoundedCornerShape(100.dp),
                                                            color = if (isConnected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(100.dp))
                                                                .clickable {
                                                                    connectedUserSet = if (isConnected) {
                                                                        connectedUserSet - handle
                                                                    } else {
                                                                        connectedUserSet + handle
                                                                    }
                                                                    onSendConnectionToUser?.invoke(handle)
                                                                }
                                                        ) {
                                                            Text(
                                                                text = if (isConnected) "Connected" else "Connect",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isConnected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                            )
                                                        }

                                                        // View Localiiiy ID button
                                                        Surface(
                                                            shape = RoundedCornerShape(100.dp),
                                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(100.dp))
                                                                .clickable {
                                                                    viewingDetailUser = Triple(name, handle, loc)
                                                                    onViewOtherUserId?.invoke(handle)
                                                                }
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.QrCode2,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                                    modifier = Modifier.size(11.dp)
                                                                )
                                                                Text(
                                                                    text = "ID",
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
                    },
                    confirmButton = {
                        TextButton(onClick = { showListDialog = false }) { Text("Done") }
                    }
                )
            }

            // Full QR Pass Dialog
            if (showFullQrDialog) {
                AlertDialog(
                    onDismissRequest = { showFullQrDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Localiiiy Space Pass", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = "Full QR Pass",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(180.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = userProfile.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "@${userProfile.username} • ${userProfile.neighborhood}",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showFullQrDialog = false }) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan ID")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFullQrDialog = false }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share ID")
                        }
                    }
                )
            }

            // Sub-dialog: View nested Localiiiy ID for clicked user from list
            if (viewingDetailUser != null) {
                val detailUser = viewingDetailUser!!
                AlertDialog(
                    onDismissRequest = { viewingDetailUser = null },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 520.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val nestedMappedUser = com.example.data.UserProfileEntity(
                                username = detailUser.second,
                                fullName = detailUser.first,
                                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                                bio = "Active community creator & explorer in ${detailUser.third}. Connecting neighbor-to-neighbor.",
                                neighborhood = detailUser.third,
                                locationName = detailUser.third,
                                neighborsCount = 89,
                                followingCount = 42,
                                isVerified = true
                            )
                            ProfileIdentityCard(
                                userProfile = nestedMappedUser,
                                postsCount = 14,
                                clipsCount = 6,
                                isOtherUser = true,
                                isGhostMode = false
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewingDetailUser = null }) { Text("Close") }
                    }
                )
            }
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IdentityStatColumn(
                        count = formatCompactNumber(postsCount.toLong()),
                        label = "Posts",
                        onClick = {
                            listDialogTitle = "Posts"
                            showListDialog = true
                        },
                        testTag = "stat_posts"
                    )
                    IdentityStatColumn(
                        count = formatCompactNumber(clipsCount.toLong()),
                        label = "Clips",
                        onClick = {
                            listDialogTitle = "Clips"
                            showListDialog = true
                        },
                        testTag = "stat_clips"
                    )
                    if (isOtherUser) {
                        IdentityStatColumn(
                            count = "12",
                            label = "Mutual",
                            onClick = {
                                listDialogTitle = "Mutual"
                                showListDialog = true
                            },
                            testTag = "stat_mutual"
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    listDialogTitle = "Connections"
                                    showListDialog = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .testTag("stat_connections"),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatCompactNumber(userProfile.neighborsCount.toLong()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Connections",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    listDialogTitle = "Connected"
                                    showListDialog = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .testTag("stat_connected"),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatCompactNumber(userProfile.followingCount.toLong()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Connected",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        IdentityStatColumn(
                            count = formatCompactNumber(marketItemsCount.toLong()),
                            label = "Market",
                            onClick = {
                                listDialogTitle = "Market"
                                showListDialog = true
                            },
                            testTag = "stat_market"
                        )
                        IdentityStatColumn(
                            count = formatCompactNumber(studioVideosCount.toLong()),
                            label = "Studio",
                            onClick = {
                                listDialogTitle = "Studio"
                                showListDialog = true
                            },
                            testTag = "stat_studio"
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    listDialogTitle = "Connections"
                                    showListDialog = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .testTag("stat_connections"),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatCompactNumber(userProfile.neighborsCount.toLong()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Connections",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    listDialogTitle = "Connected"
                                    showListDialog = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                .testTag("stat_connected"),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatCompactNumber(userProfile.followingCount.toLong()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Connected",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
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
private fun IdentityStatColumn(
    count: String,
    label: String,
    onClick: (() -> Unit)? = null,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            ),
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = if (onClick != null) androidx.compose.ui.text.style.TextDecoration.Underline else null,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyListState(
    icon: ImageVector,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = message,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(actionLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
