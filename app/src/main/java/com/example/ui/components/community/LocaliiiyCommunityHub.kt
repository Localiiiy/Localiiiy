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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.data.community.*
import com.example.ui.components.AnimatedLikeButton
import com.example.ui.components.CommentActionButton
import java.text.SimpleDateFormat
import java.util.*

private val MintAccent = Color(0xFF10B981)
private val DarkBg = Color(0xFF070A12)
private val CardBg = Color(0xFF111827)
private val CardBorder = Color(0xFF1F2937)
private val GoldAccent = Color(0xFFFFB800)
private val PurpleVip = Color(0xFF8B5CF6)

enum class CommunityHubTab(val title: String, val iconEmoji: String) {
    FEED("Feed & Alerts", "💬"),
    VOICE_SPACE("Live Voice", "🎙️"),
    POLLS("Polls & Votes", "🗳️"),
    RESOURCES("Resource Vault", "🧰"),
    PREMIUM_INTEL("VIP Lounge", "🌟")
}

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
    onSendInvite: (neighborUsername: String, neighborAvatar: String, communityName: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(CommunityHubTab.FEED) }

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
                ),
                CommunityEntity(
                    id = "comm_4",
                    name = "VIP Creator & Founders Circle",
                    description = "Exclusive high-throughput neighborhood network for verified founders and patrons.",
                    neighborhood = "Metropolitan Area",
                    iconEmoji = "🌟",
                    memberCount = 14,
                    isJoined = true
                )
            )
        )
    }

    var selectedCommunityId by remember { mutableStateOf(communities.first().id) }
    val currentCommunity = communities.find { it.id == selectedCommunityId } ?: communities.first()

    // Safety Alert SOS State
    var activeSafetyAlert by remember {
        mutableStateOf<CommunitySafetyAlert?>(
            CommunitySafetyAlert(
                id = "alert_1",
                title = "Weather Advisory • High Wind & Tide Notice",
                details = "Coarse radius: Harbor Boardwalk & Marina crossings. Safe-haven hubs open at Civic Center.",
                severity = "MODERATE",
                issuedAtText = "20m ago",
                safeHavenSpot = "Civic Center Hub"
            )
        )
    }

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

    // Community Polls State
    var communityPolls by remember {
        mutableStateOf(
            listOf(
                CommunityPoll(
                    id = "poll_1",
                    communityId = "comm_1",
                    authorName = "Civic Committee",
                    question = "Where should we host the 2026 Spring Neighborhood Artisan Fair?",
                    options = listOf(
                        CommunityPollOption("opt_1", "Civic Town Square Plaza", 24, true),
                        CommunityPollOption("opt_2", "Harbor Promenade Boardwalk", 18, false),
                        CommunityPollOption("opt_3", "Central Park East Green", 9, false)
                    ),
                    totalVotes = 51,
                    expiresAtText = "Ends in 2 days"
                ),
                CommunityPoll(
                    id = "poll_2",
                    communityId = "comm_1",
                    authorName = "Marcus Chen",
                    question = "Should we organize a weekly Neighborhood Sunset Studio filming walk?",
                    options = listOf(
                        CommunityPollOption("opt_21", "Yes! Every Friday 6 PM", 31, false),
                        CommunityPollOption("opt_22", "Saturday Mornings instead", 14, false),
                        CommunityPollOption("opt_23", "Bi-weekly sounds good", 6, false)
                    ),
                    totalVotes = 51,
                    expiresAtText = "Ends tomorrow"
                )
            )
        )
    }

    // Community Live Voice Space State
    var voiceSpace by remember {
        mutableStateOf(
            CommunityVoiceSpace(
                id = "vs_1",
                title = "Downtown Creator Jam & Neighborhood Coffee Lounge ☕",
                activeTopic = "Upcoming Artisan Market, Studio Collabs & Safe Meetups",
                isLive = true,
                listenersCount = 22,
                speakers = listOf(
                    CommunityVoiceSpeaker(
                        username = "elena_crafts",
                        fullName = "Elena Rivera",
                        avatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
                        isSpeaking = true,
                        isHost = true
                    ),
                    CommunityVoiceSpeaker(
                        username = "marcus_cinematics",
                        fullName = "Marcus Chen",
                        avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                        isSpeaking = false,
                        isHost = false
                    ),
                    CommunityVoiceSpeaker(
                        username = userProfile.username.ifBlank { "you" },
                        fullName = "${userProfile.fullName} (You)",
                        avatar = userProfile.avatarUrl,
                        isSpeaking = false,
                        isMuted = true,
                        isHost = false
                    )
                )
            )
        )
    }
    var isMyMicMuted by remember { mutableStateOf(true) }
    var isHandRaised by remember { mutableStateOf(false) }

    // Community Resource Sharing Items
    var resourceItems by remember {
        mutableStateOf(
            listOf(
                CommunityResourceItem(
                    id = "res_1",
                    title = "Sony Alpha 4K Video Kit with Prime Lens",
                    category = "Photography & Studio",
                    ownerName = "Marcus Chen",
                    ownerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                    availabilityStatus = "Available for 1-day loans",
                    imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&auto=format&fit=crop&q=80",
                    distanceKm = 0.2
                ),
                CommunityResourceItem(
                    id = "res_2",
                    title = "Heavy-Duty 12ft Folding Ladder & Tool Set",
                    category = "Home & Repair",
                    ownerName = "Elena Rivera",
                    ownerAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
                    availabilityStatus = "Free neighbor pickup",
                    imageUrl = "https://images.unsplash.com/photo-1581783342308-f792dbdd27c5?w=600&auto=format&fit=crop&q=80",
                    distanceKm = 0.4
                ),
                CommunityResourceItem(
                    id = "res_3",
                    title = "Portable Espresso Cart & Coffee Grinder",
                    category = "Event & Culinary",
                    ownerName = "Sofia Rossi",
                    ownerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                    availabilityStatus = "Weekend loan available",
                    imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
                    distanceKm = 0.6
                )
            )
        )
    }

    // Compose state
    var postText by remember { mutableStateOf("") }
    var isBroadcastMessage by remember { mutableStateOf(false) }
    var selectedAttachmentType by remember { mutableStateOf(CommunityAttachmentType.NONE) }
    var selectedMarketItem by remember { mutableStateOf<MarketplaceItemEntity?>(null) }
    var selectedClip by remember { mutableStateOf<ClipEntity?>(null) }

    // Dialogs
    var showInviteDialog by remember { mutableStateOf(false) }
    var showMembersSheet by remember { mutableStateOf(false) }
    var showCreateCommunityDialog by remember { mutableStateOf(false) }
    var showCreatePollDialog by remember { mutableStateOf(false) }
    var showAiDigestDialog by remember { mutableStateOf(false) }
    var showVipBroadcastDialog by remember { mutableStateOf(false) }
    var invitedNeighborUsernames by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("community_hub_screen")
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
                    .padding(horizontal = 12.dp, vertical = 10.dp)
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
                            modifier = Modifier.height(34.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(comm.iconEmoji, fontSize = 13.sp)
                                Text(
                                    text = comm.name,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MintAccent else Color.White,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "(${comm.memberCount})",
                                    fontSize = 9.5.sp,
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
                            modifier = Modifier.height(34.dp).testTag("btn_new_community_hub")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Create Community", tint = MintAccent, modifier = Modifier.size(15.dp))
                                Text("New Hub", fontSize = 11.5.sp, color = MintAccent, fontWeight = FontWeight.Bold, softWrap = false)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Community Info & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(currentCommunity.iconEmoji, fontSize = 18.sp)
                            Text(
                                text = currentCommunity.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "📍 ${currentCommunity.neighborhood} • ${currentCommunity.memberCount} Connected Neighbors",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Invite button
                        Button(
                            onClick = { showInviteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("btn_add_neighbor_to_community")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Neighbors", modifier = Modifier.size(13.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Neighbor", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, softWrap = false)
                        }

                        // Members list button
                        OutlinedButton(
                            onClick = { showMembersSheet = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("btn_community_members")
                        ) {
                            Icon(Icons.Default.Group, contentDescription = "Members", modifier = Modifier.size(13.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Members", fontSize = 10.5.sp, softWrap = false)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Feature Tabs Row (Feed, Voice, Polls, Resources, VIP)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(CommunityHubTab.values()) { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) {
                                if (tab == CommunityHubTab.PREMIUM_INTEL) GoldAccent.copy(alpha = 0.2f)
                                else MintAccent.copy(alpha = 0.18f)
                            } else Color(0xFF1E293B).copy(alpha = 0.7f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) {
                                    if (tab == CommunityHubTab.PREMIUM_INTEL) GoldAccent else MintAccent
                                } else Color(0xFF334155)
                            ),
                            onClick = { selectedTab = tab },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(tab.iconEmoji, fontSize = 11.sp)
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (tab == CommunityHubTab.PREMIUM_INTEL) GoldAccent else MintAccent
                                    } else Color.White,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SUB-VIEWS BASED ON SELECTED TAB ---
        when (selectedTab) {
            CommunityHubTab.FEED -> {
                CommunityFeedTabContent(
                    userProfile = userProfile,
                    currentCommunity = currentCommunity,
                    communityPosts = communityPosts,
                    activeSafetyAlert = activeSafetyAlert,
                    onDismissSafetyAlert = { activeSafetyAlert = null },
                    postText = postText,
                    onPostTextChange = { postText = it },
                    isBroadcastMessage = isBroadcastMessage,
                    onToggleBroadcast = { isBroadcastMessage = it },
                    selectedAttachmentType = selectedAttachmentType,
                    selectedMarketItem = selectedMarketItem,
                    selectedClip = selectedClip,
                    marketplaceItems = marketplaceItems,
                    clips = clips,
                    onSelectAttachment = { type, market, clip ->
                        selectedAttachmentType = type
                        selectedMarketItem = market
                        selectedClip = clip
                    },
                    onPublishPost = { newPost ->
                        communityPosts = listOf(newPost) + communityPosts
                        postText = ""
                        isBroadcastMessage = false
                        selectedAttachmentType = CommunityAttachmentType.NONE
                        selectedMarketItem = null
                        selectedClip = null
                        Toast.makeText(context, "Posted to ${currentCommunity.name}! 🎉", Toast.LENGTH_SHORT).show()
                    },
                    onLikePost = { post ->
                        communityPosts = communityPosts.map {
                            if (it.id == post.id) it.copy(
                                isLiked = !it.isLiked,
                                likesCount = if (it.isLiked) it.likesCount - 1 else it.likesCount + 1
                            ) else it
                        }
                    },
                    onOpenAiDigest = { showAiDigestDialog = true },
                    onNavigateToMarketItem = onNavigateToMarketItem,
                    onNavigateToClip = onNavigateToClip,
                    onNavigateToStudio = onNavigateToStudio,
                    onUserProfileClick = onUserProfileClick
                )
            }

            CommunityHubTab.VOICE_SPACE -> {
                CommunityVoiceSpaceTabContent(
                    voiceSpace = voiceSpace,
                    isMyMicMuted = isMyMicMuted,
                    isHandRaised = isHandRaised,
                    onToggleMute = {
                        isMyMicMuted = !isMyMicMuted
                        Toast.makeText(context, if (isMyMicMuted) "Microphone muted 🔇" else "Microphone unmuted 🎙️", Toast.LENGTH_SHORT).show()
                    },
                    onToggleHandRaise = {
                        isHandRaised = !isHandRaised
                        Toast.makeText(context, if (isHandRaised) "Hand raised to speak ✋" else "Hand lowered", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            CommunityHubTab.POLLS -> {
                CommunityPollsTabContent(
                    polls = communityPolls,
                    onVote = { pollId, optionId ->
                        communityPolls = communityPolls.map { poll ->
                            if (poll.id == pollId) {
                                val updatedOptions = poll.options.map { opt ->
                                    if (opt.id == optionId) opt.copy(votesCount = opt.votesCount + 1, isSelectedByMe = true)
                                    else opt.copy(isSelectedByMe = false)
                                }
                                poll.copy(options = updatedOptions, totalVotes = poll.totalVotes + 1)
                            } else poll
                        }
                        Toast.makeText(context, "Vote recorded in neighborhood ledger! 🗳️", Toast.LENGTH_SHORT).show()
                    },
                    onCreatePollClick = { showCreatePollDialog = true }
                )
            }

            CommunityHubTab.RESOURCES -> {
                CommunityResourcesTabContent(
                    resources = resourceItems,
                    onRequestItem = { item ->
                        Toast.makeText(context, "Borrow request sent to ${item.ownerName} for '${item.title}' 🤝", Toast.LENGTH_LONG).show()
                    }
                )
            }

            CommunityHubTab.PREMIUM_INTEL -> {
                CommunityPremiumIntelTabContent(
                    userProfile = userProfile,
                    currentCommunity = currentCommunity,
                    otherUsers = otherUsers,
                    onLaunchVipBroadcast = { showVipBroadcastDialog = true },
                    onCallMember = { memberName ->
                        Toast.makeText(context, "Connecting 1-on-1 encrypted audio call to $memberName 📞", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // --- DIALOG: ADD NEIGHBOR TO COMMUNITY ---
    if (showInviteDialog) {
        var neighborSearchQuery by remember { mutableStateOf("") }
        val filteredNeighbors = remember(neighborSearchQuery, otherUsers) {
            if (neighborSearchQuery.isBlank()) otherUsers
            else otherUsers.filter {
                it.fullName.contains(neighborSearchQuery, ignoreCase = true) ||
                it.username.contains(neighborSearchQuery, ignoreCase = true) ||
                it.locationName.contains(neighborSearchQuery, ignoreCase = true)
            }
        }

        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🏘️", fontSize = 18.sp)
                    Text("Add Neighbor to ${currentCommunity.name}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    Text(
                        text = "Adding a neighbor sends an instant community invitation request to their Pulse feed & activity notifications.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = neighborSearchQuery,
                        onValueChange = { neighborSearchQuery = it },
                        placeholder = { Text("Search nearby neighbors...", fontSize = 12.sp, color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MintAccent, modifier = Modifier.size(18.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintAccent,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0B1120),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredNeighbors.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("No nearby neighbors found", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredNeighbors) { neighbor ->
                                val isInvited = invitedNeighborUsernames.contains(neighbor.username)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, if (isInvited) MintAccent.copy(alpha = 0.5f) else Color(0xFF334155)),
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
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            AsyncImage(
                                                model = neighbor.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                                                contentDescription = neighbor.fullName,
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text(neighbor.fullName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                    if (neighbor.isVerified) {
                                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MintAccent, modifier = Modifier.size(12.dp))
                                                    }
                                                }
                                                Text("@${neighbor.username} • 📍 ${neighbor.locationName}", color = Color.Gray, fontSize = 10.sp, maxLines = 1)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (!isInvited) {
                                                    invitedNeighborUsernames = invitedNeighborUsernames + neighbor.username
                                                    // Trigger notification dispatch
                                                    onSendInvite(neighbor.username, neighbor.avatarUrl, currentCommunity.name)
                                                    communities = communities.map {
                                                        if (it.id == currentCommunity.id) it.copy(memberCount = it.memberCount + 1)
                                                        else it
                                                    }
                                                    Toast.makeText(
                                                        context,
                                                        "Request sent to ${neighbor.fullName}! Invitation notification dispatched to their Pulse feed 📨",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isInvited) Color(0xFF334155) else MintAccent
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = if (isInvited) "✓ Invited" else "+ Add",
                                                fontSize = 10.5.sp,
                                                color = if (isInvited) MintAccent else Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
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
                    Text("Done", color = MintAccent, fontWeight = FontWeight.Bold)
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
                Text("Community Members (${currentCommunity.memberCount})", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                    Text("Active neighbors participating in this hub:", color = Color.Gray, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Current user
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AsyncImage(model = userProfile.avatarUrl, contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${userProfile.fullName} (You)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("@${userProfile.username} • 🛡️ Community Elder", color = MintAccent, fontSize = 10.sp)
                                    }
                                    Text("🟢 Online", fontSize = 9.5.sp, color = MintAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Other members with roles
                        items(otherUsers.take(8)) { neighbor ->
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AsyncImage(model = neighbor.avatarUrl, contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(neighbor.fullName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("@${neighbor.username} • 🏡 Verified Resident", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF3B82F6).copy(alpha = 0.2f)) {
                                        Text("Connected", fontSize = 9.5.sp, color = Color(0xFF60A5FA), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
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
                Text("Create New Neighborhood Hub", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
                            Toast.makeText(context, "Community Hub '${newCommName}' created! 🎉", Toast.LENGTH_SHORT).show()
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

    // --- DIALOG: AI COMMUNITY DIGEST ---
    if (showAiDigestDialog) {
        AlertDialog(
            onDismissRequest = { showAiDigestDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("✨", fontSize = 18.sp)
                    Text("AI Neighborhood Digest", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📍 ${currentCommunity.name} • This Week", color = MintAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• 3 new artisan items posted in the Marketplace with exclusive member discounts.", color = Color(0xFFE2E8F0), fontSize = 11.5.sp)
                            Text("• 1 live Studio documentary episode published celebrating local harbor sunrises.", color = Color(0xFFE2E8F0), fontSize = 11.5.sp)
                            Text("• Active community poll deciding location for the 2026 Spring Artisan Fair.", color = Color(0xFFE2E8F0), fontSize = 11.5.sp)
                            Text("• 34 verified residents connected with zero safety flags this week.", color = Color(0xFFE2E8F0), fontSize = 11.5.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAiDigestDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MintAccent)) {
                    Text("Got it", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    // --- DIALOG: CREATE POLL ---
    if (showCreatePollDialog) {
        var pollQuestion by remember { mutableStateOf("") }
        var opt1 by remember { mutableStateOf("") }
        var opt2 by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreatePollDialog = false },
            title = { Text("Create Neighborhood Poll", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pollQuestion,
                        onValueChange = { pollQuestion = it },
                        label = { Text("Poll Question") },
                        placeholder = { Text("e.g. Preferred time for community meetup?") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MintAccent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = opt1,
                        onValueChange = { opt1 = it },
                        label = { Text("Option 1") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MintAccent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = opt2,
                        onValueChange = { opt2 = it },
                        label = { Text("Option 2") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MintAccent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pollQuestion.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank()) {
                            val newPoll = CommunityPoll(
                                id = "poll_${System.currentTimeMillis()}",
                                communityId = currentCommunity.id,
                                authorName = userProfile.fullName,
                                question = pollQuestion,
                                options = listOf(
                                    CommunityPollOption("opt_a", opt1, 1, true),
                                    CommunityPollOption("opt_b", opt2, 0, false)
                                ),
                                totalVotes = 1,
                                expiresAtText = "Ends in 3 days"
                            )
                            communityPolls = listOf(newPoll) + communityPolls
                            showCreatePollDialog = false
                            Toast.makeText(context, "Community poll published! 🗳️", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintAccent)
                ) {
                    Text("Publish Poll", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePollDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    // --- DIALOG: VIP PRIORITY BROADCAST ---
    if (showVipBroadcastDialog) {
        var vipMessage by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showVipBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🌟", fontSize = 18.sp)
                    Text("VIP Priority Broadcast", color = GoldAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "As a VIP Community Member, this broadcast delivers 100% reach notification across the entire neighborhood radius with golden priority styling.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )
                    OutlinedTextField(
                        value = vipMessage,
                        onValueChange = { vipMessage = it },
                        label = { Text("VIP Announcement Message") },
                        placeholder = { Text("e.g. Exclusive rooftop artisan showcase tonight...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vipMessage.isNotBlank()) {
                            val newPost = CommunityPostEntity(
                                id = "cp_${System.currentTimeMillis()}",
                                communityId = currentCommunity.id,
                                authorName = "${userProfile.fullName} 🌟",
                                authorHandle = "@${userProfile.username}",
                                authorAvatar = userProfile.avatarUrl,
                                content = vipMessage,
                                timestamp = System.currentTimeMillis(),
                                isBroadcastAlert = true,
                                attachmentType = CommunityAttachmentType.BROADCAST_ALERT,
                                attachmentTitle = "VIP Priority Broadcast",
                                attachmentSubtitle = "Dispatched to 100% of neighborhood radius",
                                attachmentBadge = "VIP BROADCAST 🌟",
                                likesCount = 1,
                                isLiked = true
                            )
                            communityPosts = listOf(newPost) + communityPosts
                            showVipBroadcastDialog = false
                            Toast.makeText(context, "VIP Priority Broadcast dispatched! 🌟📢", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Send Broadcast", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVipBroadcastDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: COMMUNITY FEED CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun CommunityFeedTabContent(
    userProfile: UserProfileEntity,
    currentCommunity: CommunityEntity,
    communityPosts: List<CommunityPostEntity>,
    activeSafetyAlert: CommunitySafetyAlert?,
    onDismissSafetyAlert: () -> Unit,
    postText: String,
    onPostTextChange: (String) -> Unit,
    isBroadcastMessage: Boolean,
    onToggleBroadcast: (Boolean) -> Unit,
    selectedAttachmentType: CommunityAttachmentType,
    selectedMarketItem: MarketplaceItemEntity?,
    selectedClip: ClipEntity?,
    marketplaceItems: List<MarketplaceItemEntity>,
    clips: List<ClipEntity>,
    onSelectAttachment: (CommunityAttachmentType, MarketplaceItemEntity?, ClipEntity?) -> Unit,
    onPublishPost: (CommunityPostEntity) -> Unit,
    onLikePost: (CommunityPostEntity) -> Unit,
    onOpenAiDigest: () -> Unit,
    onNavigateToMarketItem: (Long) -> Unit,
    onNavigateToClip: (ClipEntity) -> Unit,
    onNavigateToStudio: (String) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Pinned Safety SOS Alert Banner (if active)
        if (activeSafetyAlert != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF451A03),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth().testTag("community_safety_alert_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚨", fontSize = 20.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = activeSafetyAlert.title,
                                        color = Color(0xFFFDE68A),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B)) {
                                        Text(activeSafetyAlert.issuedAtText, color = Color.Black, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 3.dp))
                                    }
                                }
                                Text(
                                    text = activeSafetyAlert.details,
                                    color = Color(0xFFFEF3C7),
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismissSafetyAlert, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss Alert", tint = Color(0xFFFDE68A), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Quick AI Digest Pill Row
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                onClick = onOpenAiDigest,
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("✨", fontSize = 13.sp)
                        Text("Neighborhood Pulse Digest: Weekly AI Highlights", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                    }
                    Text("View ➔", color = MintAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Composer Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().testTag("community_composer_card")
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
                            onValueChange = onPostTextChange,
                            placeholder = {
                                Text(
                                    if (isBroadcastMessage) "📢 Broadcast alert to all ${currentCommunity.memberCount} members..."
                                    else "Post update to ${currentCommunity.name}...",
                                    fontSize = 12.5.sp,
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
                                .heightIn(min = 44.dp, max = 100.dp)
                        )
                    }

                    // Attachment preview if selected
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
                                    Icon(
                                        when (selectedAttachmentType) {
                                            CommunityAttachmentType.MARKET_ITEM -> Icons.Default.Storefront
                                            CommunityAttachmentType.STUDIO_VIDEO -> Icons.Default.Videocam
                                            CommunityAttachmentType.CLIP_REEL -> Icons.Default.PlayCircle
                                            CommunityAttachmentType.BROADCAST_ALERT -> Icons.Default.Campaign
                                            else -> Icons.Default.AttachFile
                                        },
                                        contentDescription = null,
                                        tint = MintAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = when (selectedAttachmentType) {
                                            CommunityAttachmentType.MARKET_ITEM -> selectedMarketItem?.title ?: "Attached Market Item"
                                            CommunityAttachmentType.CLIP_REEL -> selectedClip?.caption ?: "Attached Clip Reel"
                                            CommunityAttachmentType.STUDIO_VIDEO -> "Attached Studio Episode"
                                            CommunityAttachmentType.BROADCAST_ALERT -> "Urgent Broadcast Alert"
                                            else -> "Attachment"
                                        },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = { onSelectAttachment(CommunityAttachmentType.NONE, null, null) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Composer Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    val item = marketplaceItems.firstOrNull()
                                    onSelectAttachment(CommunityAttachmentType.MARKET_ITEM, item, null)
                                    Toast.makeText(context, "Marketplace item attached! 🛍️", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = "Attach Market", tint = if (selectedAttachmentType == CommunityAttachmentType.MARKET_ITEM) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    onSelectAttachment(CommunityAttachmentType.STUDIO_VIDEO, null, null)
                                    Toast.makeText(context, "Studio video attached! 🎥", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Attach Studio", tint = if (selectedAttachmentType == CommunityAttachmentType.STUDIO_VIDEO) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    val clip = clips.firstOrNull()
                                    onSelectAttachment(CommunityAttachmentType.CLIP_REEL, null, clip)
                                    Toast.makeText(context, "Clip reel attached! 🎬", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Attach Clip", tint = if (selectedAttachmentType == CommunityAttachmentType.CLIP_REEL) MintAccent else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    val next = !isBroadcastMessage
                                    onToggleBroadcast(next)
                                    if (next) onSelectAttachment(CommunityAttachmentType.BROADCAST_ALERT, null, null)
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = "Broadcast", tint = if (isBroadcastMessage) Color(0xFFF59E0B) else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }

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
                                        attachmentSubtitle = if (selectedMarketItem != null) "$${selectedMarketItem.price} • Available Nearby" else if (selectedClip != null) "Watch Full Clip" else "",
                                        attachmentImageUrl = selectedMarketItem?.imageUrl ?: selectedClip?.mediaUrl ?: "",
                                        attachmentBadge = when (selectedAttachmentType) {
                                            CommunityAttachmentType.MARKET_ITEM -> "MARKETPLACE 🛍️"
                                            CommunityAttachmentType.STUDIO_VIDEO -> "STUDIO VIDEO 🎥"
                                            CommunityAttachmentType.CLIP_REEL -> "CLIP REEL 🎬"
                                            CommunityAttachmentType.BROADCAST_ALERT -> "BROADCAST 📢"
                                            else -> ""
                                        }
                                    )
                                    onPublishPost(newPost)
                                }
                            },
                            enabled = postText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("btn_post_to_community")
                        ) {
                            Text("Post", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(11.dp))
                        }
                    }
                }
            }
        }

        // Post list items
        items(communityPosts, key = { it.id }) { post ->
            CommunityPostCard(
                post = post,
                onLikeClick = { onLikePost(post) },
                onNavigateToMarketItem = onNavigateToMarketItem,
                onNavigateToClip = onNavigateToClip,
                onNavigateToStudio = onNavigateToStudio,
                onUserProfileClick = onUserProfileClick
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: LIVE VOICE SPACE CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun CommunityVoiceSpaceTabContent(
    voiceSpace: CommunityVoiceSpace,
    isMyMicMuted: Boolean,
    isHandRaised: Boolean,
    onToggleMute: () -> Unit,
    onToggleHandRaise: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, MintAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("community_voice_space_card")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(shape = CircleShape, color = Color.Red, modifier = Modifier.size(8.dp)) {}
                            Text("LIVE HYPERLOCAL AUDIO STAGE", color = MintAccent, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Surface(shape = RoundedCornerShape(100.dp), color = Color(0xFF1E293B)) {
                            Text("🎧 ${voiceSpace.listenersCount} Listening", color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = voiceSpace.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Topic: ${voiceSpace.activeTopic}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speakers Stage Grid
                    Text("SPEAKERS ON STAGE", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        voiceSpace.speakers.forEach { speaker ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    AsyncImage(
                                        model = speaker.avatar.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80" },
                                        contentDescription = speaker.fullName,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .border(
                                                2.dp,
                                                if (speaker.isSpeaking) MintAccent else Color(0xFF334155),
                                                CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = if (speaker.isMuted) Color.Red else MintAccent,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            if (speaker.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = speaker.fullName,
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (speaker.isHost) {
                                    Text("👑 Host", color = MintAccent, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice Controls (Mute / Hand Raise / Leave)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onToggleMute,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMyMicMuted) Color(0xFF334155) else MintAccent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Icon(if (isMyMicMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (isMyMicMuted) Color.White else Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isMyMicMuted) "Unmute" else "Mute Mic", fontSize = 11.sp, color = if (isMyMicMuted) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onToggleHandRaise,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isHandRaised) MintAccent else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isHandRaised) MintAccent else Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text(if (isHandRaised) "✋ Hand Raised" else "✋ Raise Hand", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: COMMUNITY POLLS & VOTES CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun CommunityPollsTabContent(
    polls: List<CommunityPoll>,
    onVote: (String, String) -> Unit,
    onCreatePollClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ACTIVE NEIGHBORHOOD VOTES (${polls.size})", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onCreatePollClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp).testTag("btn_create_poll")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Create Poll", fontSize = 10.5.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(polls, key = { it.id }) { poll ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🗳️ By ${poll.authorName}", color = MintAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(poll.expiresAtText, color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(poll.question, color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    poll.options.forEach { opt ->
                        val pct = if (poll.totalVotes > 0) (opt.votesCount * 100 / poll.totalVotes) else 0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (opt.isSelectedByMe) MintAccent.copy(alpha = 0.15f) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (opt.isSelectedByMe) MintAccent else Color(0xFF334155)),
                            onClick = { onVote(poll.id, opt.id) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            if (opt.isSelectedByMe) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (opt.isSelectedByMe) MintAccent else Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(opt.text, color = Color.White, fontSize = 12.sp, fontWeight = if (opt.isSelectedByMe) FontWeight.Bold else FontWeight.Normal)
                                    }
                                    Text("$pct% (${opt.votesCount})", color = if (opt.isSelectedByMe) MintAccent else Color.Gray, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { if (poll.totalVotes > 0) opt.votesCount.toFloat() / poll.totalVotes else 0f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MintAccent,
                                    trackColor = Color(0xFF0F172A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Total Votes: ${poll.totalVotes} verified community participants", color = Color(0xFF64748B), fontSize = 10.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 4: RESOURCE VAULT CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun CommunityResourcesTabContent(
    resources: List<CommunityResourceItem>,
    onRequestItem: (CommunityResourceItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("BORROW & SHARE NEIGHBORHOOD GEAR (${resources.size})", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(resources, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MintAccent.copy(alpha = 0.15f)) {
                            Text(item.category, color = MintAccent, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.title, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Offered by ${item.ownerName} • 📍 ${item.distanceKm}km away", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        Text(item.availabilityStatus, color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { onRequestItem(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Borrow", fontSize = 10.5.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 5: PREMIUM INTELLIGENCE & VIP LOUNGE CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun CommunityPremiumIntelTabContent(
    userProfile: UserProfileEntity,
    currentCommunity: CommunityEntity,
    otherUsers: List<OtherUserEntity>,
    onLaunchVipBroadcast: () -> Unit,
    onCallMember: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // VIP Priority Broadcast Action Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().testTag("vip_priority_broadcast_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🌟", fontSize = 16.sp)
                            Text("VIP Priority Neighborhood Broadcast", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent) {
                            Text("VIP EXCLUSIVE", color = Color.Black, fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "Dispatch priority audio/visual broadcasts reaching 100% of community radius without algorithmic downranking.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onLaunchVipBroadcast,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_dispatch_vip_broadcast")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dispatch VIP Priority Broadcast", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Community Reach Heatmap & Analytics
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📊 COMMUNITY REACH & HEATMAP INTELLIGENCE", color = Color(0xFF94A3B8), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Active Reach", color = Color.Gray, fontSize = 10.sp)
                                Text("94.2%", color = MintAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("+18% this week", color = Color(0xFF34D399), fontSize = 9.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Engagement Velocity", color = Color.Gray, fontSize = 10.sp)
                                Text("3.8x", color = GoldAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("High viral velocity", color = GoldAccent, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // 1-Click Encrypted Direct Audio Calling to Neighbors
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📞 INSTANT DIRECT AUDIO CALLING", color = Color(0xFF94A3B8), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    otherUsers.take(4).forEach { neighbor ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AsyncImage(
                                    model = neighbor.avatarUrl,
                                    contentDescription = neighbor.fullName,
                                    modifier = Modifier.size(32.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column {
                                    Text(neighbor.fullName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("📍 ${neighbor.locationName}", color = Color.Gray, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = { onCallMember(neighbor.fullName) },
                                colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 10.5.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// POST CARD COMPONENT
// -------------------------------------------------------------------------------------------------
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
                .padding(12.dp)
        ) {
            // Header Row: Author + Badge + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    AsyncImage(
                        model = post.authorAvatar,
                        contentDescription = post.authorName,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { onUserProfileClick(post.authorHandle.removePrefix("@")) },
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = post.authorName,
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                            fontSize = 10.sp,
                            maxLines = 1
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
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Text Content
            Text(
                text = post.content,
                color = Color(0xFFF1F5F9),
                fontSize = 12.5.sp,
                lineHeight = 18.sp
            )

            // Rich Attachment Card (Market / Studio / Clip)
            if (post.attachmentType != CommunityAttachmentType.NONE && post.attachmentType != CommunityAttachmentType.BROADCAST_ALERT) {
                Spacer(modifier = Modifier.height(8.dp))
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
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (post.attachmentImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = post.attachmentImageUrl,
                                contentDescription = post.attachmentTitle,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.size(54.dp)
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
                                        modifier = Modifier.size(24.dp)
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
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = post.attachmentTitle,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = post.attachmentSubtitle,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Open",
                            tint = Color.Gray,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Actions: Like, Comment, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Original signature Like button ('👌' Pop)
                    AnimatedLikeButton(
                        isLiked = post.isLiked,
                        onLikeClick = onLikeClick,
                        likesCount = post.likesCount,
                        showCount = true,
                        symbolSize = 17.sp,
                        touchTargetSize = 32.dp,
                        labelColor = if (post.isLiked) MintAccent else Color(0xFF94A3B8),
                        testTag = "community_post_like_button_${post.id}"
                    )

                    // Original signature Remarks/Comment button ('✍️')
                    CommentActionButton(
                        onClick = {
                            Toast.makeText(context, "Community discussions & remarks open ✍️", Toast.LENGTH_SHORT).show()
                        },
                        commentsCount = post.commentsCount,
                        showCount = true,
                        symbolSize = 16.sp,
                        touchTargetSize = 32.dp,
                        testTag = "community_post_comment_button_${post.id}"
                    )
                }

                // Share button
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
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
