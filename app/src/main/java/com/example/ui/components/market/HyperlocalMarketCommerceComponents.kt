package com.example.ui.components.market

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.PostEntity
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency

/**
 * Section 4.1: Verified Safe Physical Exchange Spot Card
 */
@Composable
fun VerifiedSafeExchangeSpotCard(
    spotName: String = "Civic Plaza Police Precinct & Monitored Station",
    distanceMeters: Int = 340,
    hasCctv: Boolean = true,
    isOpen24h: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF062817),
        border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safe Zone",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Verified Safe Exchange Spot",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00E676)
                    )
                    Text("• $distanceMeters m", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                }
                Text(
                    text = spotName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    if (hasCctv) {
                        Text("📹 24/7 CCTV Monitored", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                    if (isOpen24h) {
                        Text("🏛️ Well-Lit Public Lobby", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

/**
 * Section 4.4: Ghost Negotiation Privacy Shield
 */
@Composable
fun GhostNegotiationPrivacyShield(
    isGhostActive: Boolean,
    ghostAlias: String = "Spectator #408",
    onToggleGhost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isGhostActive) Color(0xFF131A26) else Color(0xFF1E293B),
        border = BorderStroke(1.dp, if (isGhostActive) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.15f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(if (isGhostActive) "👻" else "👤", fontSize = 16.sp)
                Column {
                    Text(
                        text = if (isGhostActive) "Ghost Inquiry Shield Active" else "Public Profile Inquiry",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGhostActive) Color(0xFF38BDF8) else Color.White
                    )
                    Text(
                        text = if (isGhostActive) "Negotiating as $ghostAlias (Handle hidden)" else "Seller sees your Space ID handle",
                        fontSize = 9.5.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            TextButton(
                onClick = onToggleGhost,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isGhostActive) "Reveal ID" else "Cloak ID",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGhostActive) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Section 4.6: Direct Connection Friendly Discount Badge
 */
@Composable
fun ConnectionDiscountBadge(
    discountPercent: Int = 15,
    originalPrice: Double,
    currency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    modifier: Modifier = Modifier
) {
    val discountedPrice = originalPrice * (1.0 - (discountPercent / 100.0))
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2E1065),
        border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🤝", fontSize = 11.sp)
            Text(
                text = "Connected Friendly Price ($discountPercent% OFF):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD8B4FE)
            )
            Text(
                text = CurrencyHelper.format(discountedPrice, currency),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFF3E8FF)
            )
        }
    }
}

/**
 * Section 4.7: In-App In-Person Handover QR Handshake Dialog
 */
@Composable
fun InPersonHandoverQRDialog(
    itemTitle: String,
    sellerHandle: String,
    onDismiss: () -> Unit,
    onConfirmHandover: () -> Unit
) {
    var isConfirmed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF00E676))
                    Text(
                        text = "Zero-Fee In-Person Handshake",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Scan mutual cryptographic QR code during in-person inspection to safely mark '$itemTitle' as sold without platform fees.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Dynamic QR Visual Box
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 7
                        val cellW = size.width / gridSize
                        val cellH = size.height / gridSize
                        for (i in 0 until gridSize) {
                            for (j in 0 until gridSize) {
                                val isBlack = (i * 3 + j * 7 + i.xor(j)) % 2 == 0
                                if (isBlack) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = androidx.compose.ui.geometry.Offset(i * cellW, j * cellH),
                                        size = androidx.compose.ui.geometry.Size(cellW * 0.9f, cellH * 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AUTH CODE: #LOC-8942-HANDSHAKE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", color = Color.White)
                    }

                    Button(
                        onClick = {
                            isConfirmed = true
                            onConfirmHandover()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Complete Sale", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Section 4.10: Direct Pre-filled Question Inquiry Buttons
 */
@Composable
fun QuickInquiryActionsRow(
    onSendQuestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickQuestions = listOf(
        "Is this still available?",
        "Can we meet at Safe Zone?",
        "Would you consider a Barter?",
        "Any damage or wear?"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(quickQuestions) { question ->
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { onSendQuestion(question) }
            ) {
                Text(
                    text = question,
                    fontSize = 10.5.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * Section 4.11: Urgent Today / Flash Sale Red Countdown Tag
 */
@Composable
fun UrgentTodayTimerBadge(
    hoursRemaining: Int = 8,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFDC2626),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
            Text(
                text = "URGENT TODAY (${hoursRemaining}h left)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

/**
 * Section 4.12: Neighborhood Borrow & Lend Badge
 */
@Composable
fun BorrowAndLendBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0284C7),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("🔄", fontSize = 9.sp)
            Text(
                text = "BORROW & LEND (Free / Deposit)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Section 4.13: Seller Reputation by Verified Meetups Badge
 */
@Composable
fun VerifiedMeetupsReputationBadge(
    safeHandoversCount: Int = 24,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFF064E3B),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("⭐", fontSize = 10.sp)
            Text(
                text = "$safeHandoversCount Safe Local Handovers",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA7F3D0)
            )
        }
    }
}

/**
 * Section 4.14: Selectable Payment Methods Header Chips
 */
@Composable
fun PaymentMethodChipsRow(
    selectedMethods: List<String>,
    onToggleMethod: ((String) -> Unit)? = null,
    isEditable: Boolean = false,
    modifier: Modifier = Modifier
) {
    val allMethods = listOf(
        "💵 Cash on Meetup",
        "📱 Digital Transfer",
        "⚡ Lightning / Crypto",
        "🔄 Barter & Trade"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(if (isEditable) allMethods else selectedMethods) { method ->
            val isSelected = selectedMethods.contains(method)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF1E3A8A) else Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, if (isSelected) Color(0xFF60A5FA) else Color.White.copy(alpha = 0.15f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .then(if (isEditable && onToggleMethod != null) Modifier.clickable { onToggleMethod(method) } else Modifier)
            ) {
                Text(
                    text = method,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFFBFDBFE) else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Section 4.18: Interactive Item Dimension AR Ruler / Metadata
 */
@Composable
fun ItemDimensionsRulerBadge(
    widthCm: Int = 120,
    heightCm: Int = 85,
    depthCm: Int = 45,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Straighten, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                Text("Verified Physical Dimensions:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text(
                text = "${widthCm}W × ${heightCm}H × ${depthCm}D cm",
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF38BDF8)
            )
        }
    }
}

/**
 * Section 4.19: Local Artisan / Handmade Goods Badge
 */
@Composable
fun HandmadeArtisanBadge(
    creatorWorkshop: String = "Local Woodcraft Studio",
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFF78350F),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🎨", fontSize = 10.sp)
            Text(
                text = "Handmade by Creator • $creatorWorkshop",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDE68A)
            )
        }
    }
}

/**
 * Section 4.20: One-Tap Satisfying Claimed/Sold Red Ribbon Overlay
 */
@Composable
fun ClaimedSoldRibbonOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEF4444),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier.rotate(-15f)
        ) {
            Text(
                text = " CLAIMED / SOLD ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Section 4.5: Creator Portfolio Linking
 * Mini 3-column grid of seller's recent clips and pulses on marketplace store card
 */
@Composable
fun CreatorPortfolioLinkedGrid(
    sellerUsername: String = "",
    creatorHandle: String = sellerUsername,
    posts: List<PostEntity> = emptyList(),
    clips: List<ClipEntity> = emptyList(),
    onSelectPost: (PostEntity) -> Unit = {},
    onSelectClip: (ClipEntity) -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val targetHandle = if (creatorHandle.isNotBlank()) creatorHandle else sellerUsername
    val creatorClips = remember(clips, targetHandle) {
        val matches = clips.filter { it.username.equals(targetHandle, ignoreCase = true) || it.userHandle.equals(targetHandle, ignoreCase = true) }
        if (matches.isNotEmpty()) matches.take(3) else clips.take(3)
    }
    val creatorPosts = remember(posts, targetHandle) {
        val matches = posts.filter { it.username.equals(targetHandle, ignoreCase = true) || it.userHandle.equals(targetHandle, ignoreCase = true) }
        if (matches.isNotEmpty()) matches.take(3) else posts.take(3)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
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
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Creator Portfolio & Craft Proof",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Verified Artisan",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recent pulses and behind-the-scenes clips from @$targetHandle:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3-Column Mini Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (creatorClips.isNotEmpty()) {
                    creatorClips.take(3).forEach { clip ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.8f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .clickable {
                                    onSelectClip(clip)
                                    onItemClick(clip.id)
                                }
                        ) {
                            AsyncImage(
                                model = clip.mediaUrl,
                                contentDescription = clip.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(20.dp)
                            )
                            Text(
                                text = clip.caption.take(12),
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                            )
                        }
                    }
                } else {
                    creatorPosts.take(3).forEach { post ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.8f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.DarkGray)
                                .clickable {
                                    onSelectPost(post)
                                    onItemClick(post.id)
                                }
                        ) {
                            AsyncImage(
                                model = post.mediaUrl ?: "",
                                contentDescription = post.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = post.caption.take(10),
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 4.9: Item Condition 10-Second Looping Video Tour Preview Dialog
 */
@OptIn(UnstableApi::class)
@Composable
fun MarketConditionVideoTourModal(
    videoUrl: String,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(true) }
    var hasPlaybackError by remember { mutableStateOf(false) }

    // If videoUrl is an image URL, use a genuine demo condition tour MP4 or display fallback
    val resolvedVideoUrl = remember(videoUrl) {
        if (videoUrl.endsWith(".mp4", ignoreCase = true) ||
            videoUrl.contains(".mp4", ignoreCase = true) ||
            videoUrl.contains("video", ignoreCase = true)) {
            videoUrl
        } else {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        }
    }

    val exoPlayer = remember(resolvedVideoUrl) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    hasPlaybackError = true
                    try { stop() } catch (_: Exception) {}
                }
            })
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(resolvedVideoUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            } catch (_: Exception) {
                hasPlaybackError = true
            }
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer.stop()
                exoPlayer.release()
            } catch (_: Exception) {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "10s Looping Condition Tour",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    if (!hasPlaybackError) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = false
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = videoUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Audio Mute Toggle overlay
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Mute",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 360° Loop badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🔄", fontSize = 9.sp)
                            Text("360° Working Preview", fontSize = 9.5.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Zero cuts • 10-second continuous inspection video ensuring functional integrity prior to physical pickup.",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Section 4.8: Local Services Gig Model & Catalog View
 */
data class LocalServiceGig(
    val id: Long,
    val title: String,
    val providerName: String,
    val providerAvatar: String,
    val category: String, // "Audio/Video Production", "Repairs", "Lessons", "Emergency Help"
    val rateText: String,
    val distanceKm: Double,
    val landmark: String,
    val rating: Double = 4.9,
    val completedGigs: Int = 32,
    val description: String
)

val defaultLocalServiceGigs = listOf(
    LocalServiceGig(
        id = 1,
        title = "Mobile Podcast Recording & Audio Mastering",
        providerName = "Marcus Vance",
        providerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
        category = "Audio/Video Production",
        rateText = "$45 / hr",
        distanceKm = 0.4,
        landmark = "Capitol Hill Studio Hub",
        rating = 5.0,
        completedGigs = 48,
        description = "On-location multitrack recording with Shure SM7B mics and immediate rough mix."
    ),
    LocalServiceGig(
        id = 2,
        title = "Bicycle Tune-Up & Hydraulic Brake Bleed",
        providerName = "Elena Rostova",
        providerAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
        category = "Repairs",
        rateText = "$35 Flat",
        distanceKm = 0.8,
        landmark = "Pike Place Public Commons",
        rating = 4.9,
        completedGigs = 73,
        description = "Same-day drivetrain cleaning, gear indexing, brake pads, and tire inspection."
    ),
    LocalServiceGig(
        id = 3,
        title = "Analog Synth & Ableton Production Lessons",
        providerName = "Kaito Tanaka",
        providerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
        category = "Lessons",
        rateText = "$50 / hr",
        distanceKm = 1.1,
        landmark = "Fremont Sound Lab",
        rating = 4.8,
        completedGigs = 29,
        description = "Hands-on synthesis, patching modular gear, and mixing electronic music in person."
    ),
    LocalServiceGig(
        id = 4,
        title = "Emergency Battery Jump & Spare Tire Swapping",
        providerName = "Dave Miller",
        providerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
        category = "Emergency Help",
        rateText = "$25 Flat",
        distanceKm = 0.3,
        landmark = "Downtown Safe Exchange Point",
        rating = 5.0,
        completedGigs = 95,
        description = "Hyperlocal quick response within 15 minutes across Downtown and Belltown."
    ),
    LocalServiceGig(
        id = 5,
        title = "4K Drone Architectural & Showcase Videography",
        providerName = "Chloe Chen",
        providerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
        category = "Audio/Video Production",
        rateText = "$70 / hr",
        distanceKm = 1.6,
        landmark = "South Lake Union Park",
        rating = 4.9,
        completedGigs = 41,
        description = "FAA Part 107 certified drone operator for local businesses, creator sets, and real estate."
    )
)

@Composable
fun LocalServicesCatalogView(
    onInquireService: (LocalServiceGig) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedServiceCategory by remember { mutableStateOf("All Services") }
    val categories = listOf("All Services", "Audio/Video Production", "Repairs", "Lessons", "Emergency Help")

    val filteredGigs = remember(selectedServiceCategory) {
        if (selectedServiceCategory == "All Services") defaultLocalServiceGigs
        else defaultLocalServiceGigs.filter { it.category.equals(selectedServiceCategory, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        // Service Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedServiceCategory == cat
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { selectedServiceCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            items(filteredGigs, key = { it.id }) { gig ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left side: Image and below it the cost/money per hour
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.width(IntrinsicSize.Min)
                            ) {
                                AsyncImage(
                                    model = gig.providerAvatar,
                                    contentDescription = gig.providerName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                                )
                                // Cost / money per hour shown below image on the left side
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                    modifier = Modifier.testTag("service_cost_below_image")
                                ) {
                                    Text(
                                        text = gig.rateText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        maxLines = 1,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            // Right side: Gig Title, Provider Name, Ratings, and Category
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = gig.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text(
                                            text = gig.category,
                                            fontSize = 9.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = gig.providerName,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    Text(
                                        text = "⭐ ${gig.rating} (${gig.completedGigs} completed)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF59E0B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = gig.description,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${gig.landmark} (${gig.distanceKm} km)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { onInquireService(gig) },
                                shape = RoundedCornerShape(100.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Inquire Gig", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 4.16: One-Tap Report Suspicious / Fake Item Shield Modal
 */
@Composable
fun FlagListingConfirmationDialog(
    itemTitle: String,
    onConfirmFlag: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReason by remember { mutableStateOf("Suspicious / Fake Item") }
    val reasons = listOf(
        "Suspicious / Fake Item",
        "Unsafe In-Person Meetup Location",
        "Prohibited / Counterfeit Product",
        "Overpriced Scalping / Fraud"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
            modifier = modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    Text(
                        text = "Flag Suspicious Listing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hides '$itemTitle' immediately from your radar and dispatches an encrypted report to neighborhood moderators.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
                reasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFFEF4444).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEF4444) else Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedReason = reason }
                    ) {
                        Text(
                            text = reason,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirmFlag(selectedReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hide & Report", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Section 4.17: Price Drop Alert for Connections Banner
 */
@Composable
fun PriceDropAlertBanner(
    discountPercent: Int = 20,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF7C2D12),
        border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🔥", fontSize = 12.sp)
            Column {
                Text(
                    text = "Price Reduced by $discountPercent%!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFED7AA)
                )
                Text(
                    text = "Automated priority alert dispatched to creator's mutual Connections.",
                    fontSize = 9.5.sp,
                    color = Color(0xFFFED7AA).copy(alpha = 0.85f)
                )
            }
        }
    }
}

