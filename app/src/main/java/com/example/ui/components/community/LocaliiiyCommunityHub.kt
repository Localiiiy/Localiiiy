package com.example.ui.components.community

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.data.community.*
import java.text.SimpleDateFormat
import java.util.*

private val MintAccent = Color(0xFF10B981)
private val DarkBg = Color(0xFF070A12)
private val CardBg = Color(0xFF111827)
private val CardBorder = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocaliiiyCommunityHub(
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity>,
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    clips: List<ClipEntity> = emptyList(),
    onNavigateToMarketItem: (Long) -> Unit = {},
    onNavigateToClip: (ClipEntity) -> Unit = {},
    onNavigateToStudio: (String) -> Unit = {},
    onUserProfileClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Sample default communities
    var communities by remember {
        mutableStateOf(
            listOf(
                CommunityEntity(
                    id = "comm_1",
                    name = "Localiiiy Hyperlocal Community",
                    description = "Official public neighborhood community for local creators, artisans, and neighbors. Share deals, studio clips, and community alerts.",
                    neighborhood = userProfile.locationName.ifBlank { "Local District" },
                    iconEmoji = "🏘️",
                    memberCount = 34,
                    isJoined = true
                ),
                CommunityEntity(
                    id = "comm_2",
                    name = "Artisans & Makers Guild",
                    description = "A collective of local craftspeople, bakers, and designers sharing marketplace items and creative studio workflows.",
                    neighborhood = "Creative Quarter",
                    iconEmoji = "🎨",
                    memberCount = 19,
                    isJoined = true
                ),
                CommunityEntity(
                    id = "comm_3",
                    name = "Neighborhood Safety & Civic Board",
                    description = "Direct broadcast network for neighborhood updates, mutual aid, and emergency bulletins.",
                    neighborhood = "Civic District",
                    iconEmoji = "🛡️",
                    memberCount = 52,
                    isJoined = false
                )
            )
        )
    }

    var selectedCommunityId by remember { mutableStateOf(communities.first().id) }
    val currentCommunity = communities.find { it.id == selectedCommunityId } ?: communities.first()

    // Community posts state
    var communityPosts by remember {
        mutableStateOf(
            listOf(
                CommunityPostEntity(
                    id = "cp_1",
                    communityId = "comm_1",
                    authorName = "Elena Rivera",
                    authorHandle = "@elena_crafts",
                    authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
                    content = "Hey neighbors! Just dropped fresh handmade ceramic mugs in the Marketplace. Members of this community get 15% off with code LOCAL15! ✨",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    attachmentType = CommunityAttachmentType.MARKET_ITEM,
                    attachmentTargetId = "101",
                    attachmentTitle = "Hand-Thrown Stoneware Ceramic Mugs",
                    attachmentSubtitle = "$28.00 • Local Pickup Available",
                    attachmentImageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
                    attachmentBadge = "MARKETPLACE ITEM 🛍️",
                    likesCount = 14,
                    isLiked = true,
                    commentsCount = 3,
                    isPinned = true
                ),
                CommunityPostEntity(
                    id = "cp_2",
                    communityId = "comm_1",
                    authorName = "Marcus Chen",
                    authorHandle = "@marcus_cinematics",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                    content = "Filmed our neighborhood sunrise over the harbor this morning! Check out the new 4K Studio episode and clip reel. 🎬🌅",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                    attachmentType = CommunityAttachmentType.STUDIO_VIDEO,
                    attachmentTargetId = "studio_ep_4",
                    attachmentTitle = "Harbor Dawn 4K Documentary Episode",
                    attachmentSubtitle = "Alex Rivera Studio • 14.2k Connections",
                    attachmentImageUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80",
                    attachmentBadge = "STUDIO VIDEO 🎥",
                    likesCount = 28,
                    commentsCount = 7
                ),
                CommunityPostEntity(
                    id = "cp_3",
                    communityId = "comm_1",
                    authorName = "Community Moderator",
                    authorHandle = "@civic_watch",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                    content = "📢 COMMUNITY BROADCAST: Neighborhood weekend farmers market starts this Saturday at 9 AM in the town square. All local creators welcome to set up tables!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 240,
                    isBroadcastAlert = true,
                    attachmentType = CommunityAttachmentType.BROADCAST_ALERT,
                    attachmentTitle = "Neighborhood Weekend Market Notice",
                    attachmentSubtitle = "Broadcast to all 34 community members",
                    attachmentBadge = "OFFICIAL BROADCAST 📢",
                    likesCount = 42,
                    commentsCount = 12
                )
            )
        )
    }

    // Compose state
    var isComposingPost by remember { mutableStateOf(false) }
    var postText by remember { mutableStateOf("") }
    var isBroadcastMessage by remember { mutableStateOf(false) }
    var selectedAttachmentType by remember { mutableStateOf(CommunityAttachmentType.NONE) }
    var selectedMarketItem by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var selectedClip by remember { mutableStateOf<ClipEntity?>(null) }
    var customAttachmentTitle by remember { mutableStateOf("") }

    // Dialogs
    var showInviteDialog by remember { mutableStateOf(false) }
    var showMembersSheet by remember { mutableStateOf(false) }
    var showCreateCommunityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- TOP COMMUNITY HEADER BAR ---
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Communities Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(communities) { comm ->
                        val isSelected = comm.id == selectedCommunityId
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MintAccent.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MintAccent else Color.Transparent
                            ),
                            onClick = { selectedCommunityId = comm.id },
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(comm.iconEmoji, fontSize = 14.sp)
                                Text(
                                    text = comm.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MintAccent else Color.White
                                )
                                Text(
                                    text = "(${comm.memberCount})",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            onClick = { showCreateCommunityDialog = true },
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Create Community", tint = MintAccent, modifier = Modifier.size(16.dp))
                                Text("New Hub", fontSize = 12.sp, color = MintAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Community Info & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(currentCommunity.iconEmoji, fontSize = 20.sp)
                            Text(
                                text = currentCommunity.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "📍 ${currentCommunity.neighborhood} • ${currentCommunity.memberCount} Connected Neighbors",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Invite button
                        Button(
                            onClick = { showInviteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Neighbors", modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Neighbor", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        // Members list button
                        OutlinedButton(
                            onClick = { showMembersSheet = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = "Members", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Members", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // --- POSTS LIST & COMPOSER ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick Composer Box
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = userProfile.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            OutlinedTextField(
                                value = postText,
                                onValueChange = { postText = it },
                                placeholder = {
                                    Text(
                                        if (isBroadcastMessage) "📢 Broadcast alert to all ${currentCommunity.memberCount} members..."
                                        else "Post update to ${currentCommunity.name}...",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MintAccent,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF0B1120),
                                    unfocusedContainerColor = Color(0xFF0B1120)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp, max = 110.dp)
                            )
                        }

                        // Selected Attachment Preview Card
                        if (selectedAttachmentType != CommunityAttachmentType.NONE) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, MintAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val icon = when (selectedAttachmentType) {
                                            CommunityAttachmentType.MARKET_ITEM -> Icons.Default.Storefront
                                            CommunityAttachmentType.STUDIO_VIDEO -> Icons.Default.Videocam
                                            CommunityAttachmentType.CLIP_REEL -> Icons.Default.PlayCircle
                                            CommunityAttachmentType.BROADCAST_ALERT -> Icons.Default.Campaign
                                            else -> Icons.Default.AttachFile
                                        }
                                        Icon(icon, contentDescription = null, tint = MintAccent, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text(
                                                text = when (selectedAttachmentType) {
                                                    CommunityAttachmentType.MARKET_ITEM -> selectedMarketItem?.title ?: "Attached Market Item"
                                                    CommunityAttachmentType.CLIP_REEL -> selectedClip?.caption ?: "Attached Clip Reel"
                                                    CommunityAttachmentType.STUDIO_VIDEO -> "Attached Studio Episode"
                                                    CommunityAttachmentType.BROADCAST_ALERT -> "Urgent Broadcast Alert"
                                                    else -> "Attachment"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Linked & interactive for all community members",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            selectedAttachmentType = CommunityAttachmentType.NONE
                                            selectedMarketItem = null
                                            selectedClip = null
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom Actions Row (Attachment Selectors & Publish)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Attach Market Item
                                IconButton(
                                    onClick = {
                                        selectedAttachmentType = CommunityAttachmentType.MARKET_ITEM
                                        if (marketplaceItems.isNotEmpty()) {
                                            selectedMarketItem = marketplaceItems.first()
                                        }
                                        Toast.makeText(context, "Marketplace item attached to post! 🛍️", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = "Attach Market", tint = if (selectedAttachmentType == CommunityAttachmentType.MARKET_ITEM) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                }

                                // Attach Studio Video
                                IconButton(
                                    onClick = {
                                        selectedAttachmentType = CommunityAttachmentType.STUDIO_VIDEO
                                        Toast.makeText(context, "Studio episode attached to post! 🎥", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Attach Studio", tint = if (selectedAttachmentType == CommunityAttachmentType.STUDIO_VIDEO) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                }

                                // Attach Clip Reel
                                IconButton(
                                    onClick = {
                                        selectedAttachmentType = CommunityAttachmentType.CLIP_REEL
                                        if (clips.isNotEmpty()) {
                                            selectedClip = clips.first()
                                        }
                                        Toast.makeText(context, "Clip reel attached to post! 🎬", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = "Attach Clip", tint = if (selectedAttachmentType == CommunityAttachmentType.CLIP_REEL) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                }

                                // Toggle Urgent Broadcast Alert
                                IconButton(
                                    onClick = {
                                        isBroadcastMessage = !isBroadcastMessage
                                        if (isBroadcastMessage) {
                                            selectedAttachmentType = CommunityAttachmentType.BROADCAST_ALERT
                                            Toast.makeText(context, "Broadcast Mode: Alerts all community members! 📢", Toast.LENGTH_SHORT).show()
                                        } else {
                                            selectedAttachmentType = CommunityAttachmentType.NONE
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = "Broadcast", tint = if (isBroadcastMessage) Color(0xFFF59E0B) else Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                }
                            }

                            // Submit Button
                            Button(
                                onClick = {
                                    if (postText.isNotBlank()) {
                                        val newPost = CommunityPostEntity(
                                            id = "cp_${System.currentTimeMillis()}",
                                            communityId = currentCommunity.id,
                                            authorName = userProfile.fullName.ifBlank { "Neighbor Creator" },
                                            authorHandle = "@${userProfile.username.ifBlank { "creator" }}",
                                            authorAvatar = userProfile.avatarUrl,
                                            content = postText,
                                            timestamp = System.currentTimeMillis(),
                                            isBroadcastAlert = isBroadcastMessage,
                                            attachmentType = selectedAttachmentType,
                                            attachmentTargetId = selectedMarketItem?.id?.toString() ?: selectedClip?.id?.toString() ?: "",
                                            attachmentTitle = selectedMarketItem?.title ?: selectedClip?.caption ?: if (isBroadcastMessage) "Community Announcement" else "",
                                            attachmentSubtitle = if (selectedMarketItem != null) "$${selectedMarketItem?.price} • Available Nearby" else if (selectedClip != null) "Watch Full Clip" else "",
                                            attachmentImageUrl = selectedMarketItem?.imageUrl ?: selectedClip?.mediaUrl ?: "",
                                            attachmentBadge = when (selectedAttachmentType) {
                                                CommunityAttachmentType.MARKET_ITEM -> "MARKETPLACE 🛍️"
                                                CommunityAttachmentType.STUDIO_VIDEO -> "STUDIO VIDEO 🎥"
                                                CommunityAttachmentType.CLIP_REEL -> "CLIP REEL 🎬"
                                                CommunityAttachmentType.BROADCAST_ALERT -> "BROADCAST 📢"
                                                else -> ""
                                            }
                                        )
                                        communityPosts = listOf(newPost) + communityPosts
                                        postText = ""
                                        isBroadcastMessage = false
                                        selectedAttachmentType = CommunityAttachmentType.NONE
                                        selectedMarketItem = null
                                        selectedClip = null
                                        Toast.makeText(context, "Posted to ${currentCommunity.name}! 🎉", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = postText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Post to Community", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMMUNITY FEED & BROADCASTS (${communityPosts.size})",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Text(
                        text = "Live Sync 🟢",
                        color = MintAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Posts Items
            val filteredPosts = communityPosts.filter { it.communityId == currentCommunity.id }
            if (filteredPosts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏘️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No community posts yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Be the first neighbor to start the conversation!", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
                        onLikeClick = {
                            communityPosts = communityPosts.map {
                                if (it.id == post.id) it.copy(isLiked = !it.isLiked, likesCount = if (it.isLiked) it.likesCount - 1 else it.likesCount + 1)
                                else it
                            }
                        },
                        onNavigateToMarketItem = onNavigateToMarketItem,
                        onNavigateToClip = onNavigateToClip,
                        onNavigateToStudio = onNavigateToStudio,
                        onUserProfileClick = onUserProfileClick
                    )
                }
            }
        }
    }

    // --- DIALOG: INVITE NEIGHBORS ---
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("👥", fontSize = 20.sp)
                    Text("Add Neighbors to Community", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Text(
                        "Select connected neighbors nearby to add to ${currentCommunity.name}:",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (otherUsers.isEmpty()) {
                        Text("No other neighbors currently detected on Radar.", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(otherUsers) { neighbor ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = neighbor.avatarUrl,
                                                contentDescription = neighbor.fullName,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                            )
                                            Column {
                                                Text(neighbor.fullName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("@${neighbor.username} • 📍 ${neighbor.locationName}", color = Color.Gray, fontSize = 10.sp)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                Toast.makeText(context, "${neighbor.fullName} added to community! ✅", Toast.LENGTH_SHORT).show()
                                                communities = communities.map {
                                                    if (it.id == currentCommunity.id) it.copy(memberCount = it.memberCount + 1)
                                                    else it
                                                }
                                                showInviteDialog = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("+ Add", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Done", color = MintAccent)
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    // --- DIALOG: MEMBERS LIST ---
    if (showMembersSheet) {
        AlertDialog(
            onDismissRequest = { showMembersSheet = false },
            title = {
                Text("Community Members (${currentCommunity.memberCount})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                    Text("Active neighbors participating in this hub:", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Current user
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AsyncImage(model = userProfile.avatarUrl, contentDescription = null, modifier = Modifier.size(30.dp).clip(CircleShape))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${userProfile.fullName} (You)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("@${userProfile.username} • Moderator", color = MintAccent, fontSize = 10.sp)
                                    }
                                    Text("🟢 Online", fontSize = 10.sp, color = MintAccent)
                                }
                            }
                        }

                        // Other members
                        items(otherUsers.take(8)) { neighbor ->
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AsyncImage(model = neighbor.avatarUrl, contentDescription = null, modifier = Modifier.size(30.dp).clip(CircleShape))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(neighbor.fullName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("@${neighbor.username} • 📍 ${neighbor.locationName}", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Text("Connected", fontSize = 10.sp, color = Color(0xFF60A5FA))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMembersSheet = false }) {
                    Text("Close", color = MintAccent)
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    // --- DIALOG: CREATE NEW COMMUNITY ---
    if (showCreateCommunityDialog) {
        var newCommName by remember { mutableStateOf("") }
        var newCommDesc by remember { mutableStateOf("") }
        var newCommEmoji by remember { mutableStateOf("🏡") }

        AlertDialog(
            onDismissRequest = { showCreateCommunityDialog = false },
            title = {
                Text("Create New Neighborhood Hub", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCommName,
                        onValueChange = { newCommName = it },
                        label = { Text("Community Name") },
                        placeholder = { Text("e.g. Westside Creator Collective") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintAccent,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCommDesc,
                        onValueChange = { newCommDesc = it },
                        label = { Text("Description & Purpose") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintAccent,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCommName.isNotBlank()) {
                            val newC = CommunityEntity(
                                id = "comm_${System.currentTimeMillis()}",
                                name = newCommName,
                                description = newCommDesc.ifBlank { "Neighborhood group for local creators and residents." },
                                neighborhood = userProfile.locationName.ifBlank { "Local District" },
                                iconEmoji = newCommEmoji,
                                memberCount = 1,
                                isJoined = true
                            )
                            communities = communities + newC
                            selectedCommunityId = newC.id
                            showCreateCommunityDialog = false
                            Toast.makeText(context, "Community created! 🎉", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = newCommName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MintAccent)
                ) {
                    Text("Create Hub", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCommunityDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPostEntity,
    onLikeClick: () -> Unit,
    onNavigateToMarketItem: (Long) -> Unit,
    onNavigateToClip: (ClipEntity) -> Unit,
    onNavigateToStudio: (String) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    val context = LocalContext.current
    val timeFormatted = remember(post.timestamp) {
        val diffMinutes = (System.currentTimeMillis() - post.timestamp) / (1000 * 60)
        when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "${diffMinutes}m ago"
            else -> "${diffMinutes / 60}h ago"
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (post.isBroadcastAlert) Color(0xFF1E1B4B) else CardBg
        ),
        border = BorderStroke(
            1.dp,
            if (post.isBroadcastAlert) Color(0xFF818CF8) else if (post.isPinned) MintAccent.copy(alpha = 0.6f) else CardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Author + Badge + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = post.authorAvatar,
                        contentDescription = post.authorName,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onUserProfileClick(post.authorHandle.removePrefix("@")) },
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = post.authorName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.isPinned) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MintAccent.copy(alpha = 0.2f)
                                ) {
                                    Text("📌 PINNED", color = MintAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(
                            text = "${post.authorHandle} • $timeFormatted",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.5.sp
                        )
                    }
                }

                if (post.isBroadcastAlert) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            "📢 BROADCAST",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text Content
            Text(
                text = post.content,
                color = Color(0xFFF1F5F9),
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )

            // --- RICH ATTACHMENT CARD (MARKET / STUDIO / CLIP) ---
            if (post.attachmentType != CommunityAttachmentType.NONE && post.attachmentType != CommunityAttachmentType.BROADCAST_ALERT) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (post.attachmentType) {
                                CommunityAttachmentType.MARKET_ITEM -> {
                                    Toast.makeText(context, "Opening Marketplace Item: ${post.attachmentTitle} 🛍️", Toast.LENGTH_SHORT).show()
                                }
                                CommunityAttachmentType.STUDIO_VIDEO -> {
                                    Toast.makeText(context, "Opening Studio Channel 🎥", Toast.LENGTH_SHORT).show()
                                    onNavigateToStudio(post.authorHandle)
                                }
                                CommunityAttachmentType.CLIP_REEL -> {
                                    Toast.makeText(context, "Playing Clip Reel 🎬", Toast.LENGTH_SHORT).show()
                                }
                                else -> {}
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (post.attachmentImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = post.attachmentImageUrl,
                                contentDescription = post.attachmentTitle,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        when (post.attachmentType) {
                                            CommunityAttachmentType.MARKET_ITEM -> Icons.Default.Storefront
                                            CommunityAttachmentType.STUDIO_VIDEO -> Icons.Default.Videocam
                                            CommunityAttachmentType.CLIP_REEL -> Icons.Default.PlayCircle
                                            else -> Icons.Default.Attachment
                                        },
                                        contentDescription = null,
                                        tint = MintAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MintAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = post.attachmentBadge,
                                    color = MintAccent,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = post.attachmentTitle,
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = post.attachmentSubtitle,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.5.sp,
                                maxLines = 1
                            )
                        }

                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Open",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Actions: Like, Comment, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Like button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onLikeClick() }
                    ) {
                        Icon(
                            if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color(0xFFF43F5E) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.likesCount}",
                            color = if (post.isLiked) Color(0xFFF43F5E) else Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    // Comments button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Comments section open 💬", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.commentsCount}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Share to outer network button
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Community link copied to clipboard! 🔗", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
