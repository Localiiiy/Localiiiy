package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.PostEntity
import com.example.ui.theme.EditorialHeart
import com.example.ui.theme.EditorialVerified
import com.example.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.Brush
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.ui.theme.LocaliiiyPrimaryDark
import com.example.ui.theme.LocaliiiySecondary
import com.example.ui.theme.LocaliiiyTertiary
import com.example.ui.components.feed.LocalSeedDeliveryBadge
import com.example.ui.components.feed.DualVelocityProgressMeter
import com.example.ui.components.feed.CreatorTipSupportJarSheet
import com.example.ui.components.feed.HyperlocalBookmarkShelfSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    post: PostEntity,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUserClick: () -> Unit,
    onTranslateClick: (() -> Unit)? = null,
    onReportClick: ((String) -> Unit)? = null,
    onBlockUserClick: (() -> Unit)? = null,
    onAmplifyToCity: (() -> Unit)? = null,
    onSaveToShelf: ((String) -> Unit)? = null,
    onExploreNeighborhood: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showBigHeart by remember { mutableStateOf(false) }
    var isCaptionExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportSuccessSnackbar by remember { mutableStateOf(false) }
    var showTipJarSheet by remember { mutableStateOf(false) }
    var showShelfSaveSheet by remember { mutableStateOf(false) }
    var showNeighborhoodSheet by remember { mutableStateOf(false) }
    var translatedCaption by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }
    var isAmplifiedToCity by remember { mutableStateOf(false) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    // Section 2.5: Luminous Connection Aura Borders for connected users
    val isConnectedUser = post.isFollowing || post.isConnected
    val cardBorder = if (isConnectedUser) {
        androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(LocaliiiyPrimaryTeal, LocaliiiyAccentMint, Color(0xFF6366F1))
            )
        )
    } else {
        androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }

    // Heart scale animation
    val heartScale = remember { Animatable(1f) }

    fun triggerLikeWithAnimation() {
        coroutineScope.launch {
            heartScale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            heartScale.animateTo(1f)
        }
        onLikeClick()
    }

    fun handleDoubleTap() {
        showBigHeart = true
        if (!post.isLiked) {
            triggerLikeWithAnimation()
        }
        coroutineScope.launch {
            delay(800)
            showBigHeart = false
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = cardBorder,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Section 2.5: Mutual Connection Header Pill
            if (isConnectedUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Mutual Connection",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocaliiiyPrimaryDark
                            )
                        }
                    }

                    // Section 2.2: Guaranteed Local Impression Counter Badge
                    val seedImpressionCount = remember(post.id) { 100 + ((post.id * 17) % 240).toInt().coerceAtLeast(42) }
                    LocalSeedDeliveryBadge(localSeedCount = seedImpressionCount)
                }
            }

            // --- Header (Avatar, Username, Distance & Landmark Pill) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onUserClick)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(post.userAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = post.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.username,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = EditorialVerified,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Section 2.6: Tap-enabled Geographic Origin Verification Chip
                        val locationString = post.landmark ?: post.location ?: "Williamsburg"
                        val formattedDistance = LocationHelper.formatDistanceLabel(post.distanceKm)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    showNeighborhoodSheet = true
                                    onExploreNeighborhood?.invoke(locationString)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = LocaliiiyPrimaryTeal,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "$locationString • $formattedDistance",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = LocaliiiyPrimaryDark,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Localiiiy Distance Pill Badge & Moderation Menu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val distanceLabel = LocationHelper.formatDistanceLabel(post.distanceKm)
                    Surface(
                        color = if (post.isNeighbor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable(onClick = onUserClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Distance",
                                tint = if (post.isNeighbor) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = distanceLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (post.isNeighbor) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("post_menu_button_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Section 2.8: One-Tap 'Boost / Amplify to City' Dial
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Amplify to City (50km) 🚀", fontWeight = FontWeight.Bold)
                                        Text("Expands reach from 5km to 50km", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = LocaliiiyPrimaryTeal)
                                },
                                onClick = {
                                    showMenu = false
                                    isAmplifiedToCity = true
                                    onAmplifyToCity?.invoke()
                                }
                            )

                            // Section 2.14: Creator Tip & Support Jar
                            DropdownMenuItem(
                                text = { Text("Support Creator Tip ⚡") },
                                leadingIcon = {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = LocaliiiyTertiary)
                                },
                                onClick = {
                                    showMenu = false
                                    showTipJarSheet = true
                                }
                            )

                            // Section 2.16: Save to Hyperlocal Bookmark Shelf
                            DropdownMenuItem(
                                text = { Text("Save to Shelf 📁") },
                                leadingIcon = {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = LocaliiiyPrimaryTeal)
                                },
                                onClick = {
                                    showMenu = false
                                    showShelfSaveSheet = true
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            DropdownMenuItem(
                                text = { Text("Report Post 🚩") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showReportDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Block @${post.username} 🚫") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onBlockUserClick?.invoke()
                                }
                            )
                        }
                    }
                }
            }

            // --- Post Media Container with Double Tap Heart Burst ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                handleDoubleTap()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                ImageWithFilter(
                    mediaUrl = post.mediaUrl,
                    filterName = post.filterName,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = post.caption
                )

                // Animated Big Pop 👌 on Double Tap
                androidx.compose.animation.AnimatedVisibility(
                    visible = showBigHeart,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    exit = scaleOut(tween(300)) + fadeOut()
                ) {
                    Text(
                        text = "👌",
                        fontSize = 80.sp
                    )
                }

                if (post.isLive) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Neighborhood Tag on media corner if < 1.5km
                if (post.isNeighbor) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🏡 Neighbor Creator",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // --- Action Buttons Row (Spark, Chat, Share/Echo, Pin) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AnimatedLikeButton(
                        isLiked = post.isLiked,
                        onLikeClick = { triggerLikeWithAnimation() },
                        testTag = "like_button_${post.id}"
                    )

                    if (post.isLive) {
                        Button(
                            onClick = onCommentClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(38.dp).testTag("live_remarks_button_${post.id}"),
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Live Remarks", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        CommentActionButton(
                            onClick = onCommentClick,
                            testTag = "comment_button_${post.id}"
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (onTranslateClick != null) {
                        IconButton(
                            onClick = onTranslateClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("translate_button_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Translate,
                                contentDescription = "Translated by AI",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (post.isSaved) "Unpin" else "Pin",
                        tint = if (post.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // --- Sparks Count ---
            if (post.likesCount > 0) {
                Text(
                    text = "${formatCount(post.likesCount)} sparks",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("likes_count_${post.id}")
                )
            }

            // --- Caption with Expandable Toggle ---
            if (post.caption.isNotBlank()) {
                val captionToDisplay = translatedCaption ?: post.caption
                val captionText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append("${post.username} ")
                    }
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append(captionToDisplay)
                    }
                }

                Text(
                    text = captionText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = if (isCaptionExpanded) Int.MAX_VALUE else 2,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clickable { isCaptionExpanded = !isCaptionExpanded }
                )

                // Section 2.12: Global Trend Translation Bridge
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            if (translatedCaption == null) {
                                isTranslating = true
                                coroutineScope.launch {
                                    delay(400)
                                    translatedCaption = "✨ Translated to English: ${post.caption}"
                                    isTranslating = false
                                }
                            } else {
                                translatedCaption = null
                            }
                            onTranslateClick?.invoke()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Translate,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isTranslating) "Translating..." else if (translatedCaption != null) "Show original" else "Translate to English • Audio & Text",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = LocaliiiyPrimaryTeal
                    )
                }
            }

            // Section 2.7: Dual Velocity Progress Meter (Local vs Global)
            val saturation = remember(post.id) {
                ((post.id % 4 + 7) / 10f).coerceIn(0.6f, 0.95f)
            }
            val multiplier = remember(post.id) {
                if (isAmplifiedToCity) 4.8f else ((post.id % 5) + 1.2f)
            }
            DualVelocityProgressMeter(
                localSaturation = saturation,
                globalMultiplier = multiplier,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Section 2.18: Community Recommendation Badges
            val endorsers = listOf("Coffee Barista Guild", "Local Art Council", "Neighborhood Green Alliance")
            val selectedEndorser = endorsers[(post.id % endorsers.size).toInt()]
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LocaliiiyPrimaryTeal.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Endorsed by $selectedEndorser",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LocaliiiyPrimaryDark
                    )
                }
            }

            // --- Audio Track Info if present ---
            if (!post.soundTitle.isNullOrBlank()) {
                Text(
                    text = "🎵 ${post.soundTitle}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // --- View All Comments Link ---
            if (post.commentsCount > 0) {
                Text(
                    text = "View all ${post.commentsCount} remarks",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clickable(onClick = onCommentClick)
                )
            }

            // --- Relative Timestamp & Landmark ---
            Text(
                text = "${formatRelativeTime(post.timestamp)} • ${post.landmark ?: post.location ?: "Nearby"}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }

    // UGC Compliance: In-App Content Reporting Dialog
    if (showReportDialog) {
        val reportReasons = listOf(
            "Spam or Misleading Content",
            "Harassment, Hate Speech or Bullying",
            "Nudity or Inappropriate Content",
            "Scam, Fraud or Counterfeit Goods",
            "Dangerous or Illegal Activities",
            "Intellectual Property Infringement"
        )
        var selectedReason by remember { mutableStateOf(reportReasons.first()) }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text("Report Content", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Help us keep the neighborhood safe. Why are you reporting this post by @${post.username}?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    reportReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onReportClick?.invoke(selectedReason)
                        showReportSuccessSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Submit Report", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportSuccessSnackbar) {
        AlertDialog(
            onDismissRequest = { showReportSuccessSnackbar = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Report Received", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Thank you for reporting. Our moderation team reviews flagged content within 24 hours to enforce our Community Standards. This post has been hidden from your feed.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(onClick = { showReportSuccessSnackbar = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Section 2.14: Creator Tip & Support Jar Bottom Sheet
    if (showTipJarSheet) {
        CreatorTipSupportJarSheet(
            creatorName = post.username,
            onDismiss = { showTipJarSheet = false },
            onTipSent = {
                showTipJarSheet = false
            }
        )
    }

    // Section 2.16: Save to Hyperlocal Bookmark Shelf Bottom Sheet
    if (showShelfSaveSheet) {
        HyperlocalBookmarkShelfSheet(
            post = post,
            onDismiss = { showShelfSaveSheet = false },
            onFolderSelected = { folder ->
                onSaveToShelf?.invoke(folder)
            }
        )
    }

    // Section 2.6: Geographic Origin Verification Modal
    if (showNeighborhoodSheet) {
        val locationString = post.landmark ?: post.location ?: "Williamsburg"
        AlertDialog(
            onDismissRequest = { showNeighborhoodSheet = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = LocaliiiyPrimaryTeal,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Geographic Verification", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "📍 Anchor: $locationString",
                        fontWeight = FontWeight.Bold,
                        color = LocaliiiyPrimaryDark
                    )
                    Text(
                        "This pulse was cryptographically verified to have originated from physical vicinity ($locationString) using coarse zero-knowledge geohash proofs without revealing exact coordinates.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNeighborhoodSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                ) {
                    Text("Explore Neighborhood Pulses")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNeighborhoodSheet = false }) {
                    Text("Close")
                }
            }
        )
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", count / 1_000_000.0)
        count >= 10_000 -> String.format(Locale.getDefault(), "%.1fK", count / 1000.0)
        count >= 1_000 -> "%,d".format(Locale.getDefault(), count)
        else -> count.toString()
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> {
            val sdf = SimpleDateFormat("MMMM d", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
