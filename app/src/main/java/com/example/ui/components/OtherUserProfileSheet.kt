package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.ui.ProfileTab
import com.example.ui.theme.EditorialVerified
import com.example.util.LocationHelper

import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileSheet(
    user: OtherUserEntity,
    posts: List<PostEntity>,
    clips: List<ClipEntity>,
    onDismiss: () -> Unit,
    onFollowToggle: () -> Unit,
    onWaveClick: () -> Unit,
    onDirectMessageClick: () -> Unit,
    onPostClick: (PostEntity) -> Unit,
    onClipClick: (ClipEntity) -> Unit,
    onReportUser: ((String) -> Unit)? = null,
    onBlockUser: (() -> Unit)? = null,
    onReportCyberstalking: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ProfileTab.POSTS) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportSuccessSnackbar by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("other_user_profile_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header Row (Username, Close)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = EditorialVerified,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("other_user_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Report @${user.username} 🚩") },
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
                                text = { Text("Block @${user.username} 🚫") },
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
                                    onBlockUser?.invoke()
                                    onDismiss()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Report Cyberstalking & Ban ⚖️",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDismiss()
                                    onReportCyberstalking?.invoke(user.username)
                                }
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                item(span = { GridItemSpan(3) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Avatar and stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .padding(3.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user.avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = user.fullName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            var showListDialog by remember { mutableStateOf(false) }
                            var listDialogTitle by remember { mutableStateOf("") }
                            val isUserInGhostMode = user.username.contains("ghost") || user.distanceKm > 10.0
                            var otherConnectedSet by remember { mutableStateOf(setOf("maya_sound")) }
                            var viewingSubUserId by remember { mutableStateOf<Triple<String, String, String>?>(null) }
                            
                            val sampleConnectionsList = remember {
                                listOf(
                                    Triple("Maya Patel", "maya_sound", "Capitol Hill • Seattle"),
                                    Triple("Liam Vance", "liam_craft", "Fremont • Seattle"),
                                    Triple("Elena Rostova", "elena_visuals", "Ballard • Seattle"),
                                    Triple("Marcus Chen", "marcus_dev", "Belltown • Seattle"),
                                    Triple("Sofia Taylor", "sofia_lens", "Pioneer Square • Seattle")
                                )
                            }
                            
                            if (showListDialog) {
                                AlertDialog(
                                    onDismissRequest = { showListDialog = false },
                                    title = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (listDialogTitle == "Connections") Icons.Default.People else Icons.Default.PersonAdd,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(listDialogTitle, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    text = { 
                                        if (isUserInGhostMode) {
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
                                                    .heightIn(max = 320.dp)
                                                    .verticalScroll(rememberScrollState()),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Text(
                                                    text = "$listDialogTitle for @${user.username}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                sampleConnectionsList.forEach { (name, handle, loc) ->
                                                    val isConnected = otherConnectedSet.contains(handle)
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
                                                                modifier = Modifier.weight(1f).padding(end = 6.dp),
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
                                                                Surface(
                                                                    shape = RoundedCornerShape(100.dp),
                                                                    color = if (isConnected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(100.dp))
                                                                        .clickable {
                                                                            otherConnectedSet = if (isConnected) otherConnectedSet - handle else otherConnectedSet + handle
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

                                                                Surface(
                                                                    shape = RoundedCornerShape(100.dp),
                                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(100.dp))
                                                                        .clickable {
                                                                            viewingSubUserId = Triple(name, handle, loc)
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
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showListDialog = false }) { Text("Close") }
                                    }
                                )
                            }

                            if (viewingSubUserId != null) {
                                val item = viewingSubUserId!!
                                AlertDialog(
                                    onDismissRequest = { viewingSubUserId = null },
                                    text = {
                                        val nestedUser = com.example.data.UserProfileEntity(
                                            username = item.second,
                                            fullName = item.first,
                                            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                                            bio = "Active creator and local explorer in ${item.third}.",
                                            neighborhood = item.third,
                                            locationName = item.third,
                                            neighborsCount = 74,
                                            followingCount = 38,
                                            isVerified = true
                                        )
                                        ProfileIdentityCard(
                                            userProfile = nestedUser,
                                            postsCount = 8,
                                            clipsCount = 4,
                                            isOtherUser = true,
                                            isGhostMode = false
                                        )
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { viewingSubUserId = null }) { Text("Close") }
                                    }
                                )
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${posts.size}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Sparks", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { 
                                        listDialogTitle = "Connections"
                                        showListDialog = true
                                    }
                                ) {
                                    Text(
                                        text = formatCount(user.followersCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connections", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { 
                                        listDialogTitle = "Connected"
                                        showListDialog = true
                                    }
                                ) {
                                    Text(
                                        text = formatCount(user.followingCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "12",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "Shared Ties", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full name and bio
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Proximity & Neighborhood Banner
                        val distLabel = LocationHelper.formatDistanceLabel(user.distanceKm)
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "$distLabel • ${user.landmark ?: user.locationName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (user.website.isNotBlank()) {
                            Text(
                                text = "🔗 ${user.website}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Connect | Message | Wave 👋
                        var showIdDialog by remember { mutableStateOf(false) }

                        if (showIdDialog) {
                            AlertDialog(
                                onDismissRequest = { showIdDialog = false },
                                text = {
                                    // Map OtherUserEntity to UserProfileEntity for the identity card
                                    val mappedUser = com.example.data.UserProfileEntity(
                                        username = user.username,
                                        fullName = user.fullName,
                                        avatarUrl = user.avatarUrl,
                                        bio = user.bio,
                                        website = user.website,
                                        category = user.category,
                                        locationName = user.locationName,
                                        neighborhood = user.landmark ?: user.locationName,
                                        neighborsCount = user.followersCount,
                                        followingCount = user.followingCount,
                                        isVerified = user.isVerified
                                    )
                                    ProfileIdentityCard(
                                        userProfile = mappedUser,
                                        postsCount = user.postsCount,
                                        clipsCount = 0,
                                        marketItemsCount = 0,
                                        studioVideosCount = user.studioSubscribersCount,
                                        isOtherUser = true,
                                        isGhostMode = false
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = { showIdDialog = false }) { Text("Close") }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onFollowToggle,
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                    contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(38.dp)
                            ) {
                                Text(
                                    text = if (user.isFollowing) "Connected" else "Send Connection",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { showIdDialog = true },
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "View ID",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onDirectMessageClick,
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Message",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onWaveClick,
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isFriend) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (user.isFriend) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text(
                                    text = if (user.isFriend) "Friends 🤝" else "Wave 👋",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tab selectors (Posts / Clips)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = { activeTab = ProfileTab.POSTS }) {
                                Icon(
                                    imageVector = Icons.Outlined.GridOn,
                                    contentDescription = "Posts",
                                    tint = if (activeTab == ProfileTab.POSTS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { activeTab = ProfileTab.CLIPS }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayCircle,
                                    contentDescription = "Clips",
                                    tint = if (activeTab == ProfileTab.CLIPS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }

                // Posts / Clips Grid
                if (activeTab == ProfileTab.POSTS) {
                    items(posts, key = { it.id }) { post ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onPostClick(post) }
                        ) {
                            ImageWithFilter(
                                mediaUrl = post.mediaUrl,
                                filterName = post.filterName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    items(clips, key = { it.id }) { clip ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.65f)
                                .clickable { onClipClick(clip) }
                        ) {
                            ImageWithFilter(
                                mediaUrl = clip.mediaUrl,
                                filterName = clip.filterName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }

    // UGC Compliance: Report User Dialog
    if (showReportDialog) {
        val reportReasons = listOf(
            "Spam, Scam, or Counterfeit Account",
            "Harassment, Bullying, or Hate Speech",
            "Impersonation of Another Person/Business",
            "Posting Inappropriate or Explicit Content",
            "Threats, Violence, or Illegal Activity"
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
                Text("Report @${user.username}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Why are you reporting this user? Our team reviews all reports to keep Localiiiy safe.",
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
                        onReportUser?.invoke(selectedReason)
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
            onDismissRequest = {
                showReportSuccessSnackbar = false
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Report Submitted", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "We have received your report regarding @${user.username}. Thank you for helping keep Localiiiy safe and respectful for everyone.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportSuccessSnackbar = false
                        onDismiss()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}
