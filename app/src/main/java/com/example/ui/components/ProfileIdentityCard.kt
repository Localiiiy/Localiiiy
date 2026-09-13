package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.UserProfileEntity
import com.example.ui.theme.EditorialVerified

@Composable
fun ProfileIdentityCard(
    userProfile: UserProfileEntity,
    postsCount: Int,
    clipsCount: Int,
    marketItemsCount: Int = 0,
    studioVideosCount: Int = 0,
    isOtherUser: Boolean = false,
    isGhostMode: Boolean = false,
    onViewOtherUserId: ((String) -> Unit)? = null,
    onSendConnectionToUser: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // 3D Holographic Tilt State
    var tiltOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
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

        // Main Details Row: QR (Left) - Info (Center) - Photo (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Code (Left)
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { /* TODO: Show full QR */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Scan to view space",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(76.dp)
                )
            }

            // Info (Center)
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile.fullName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
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
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = userProfile.neighborhood,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Photo (Right)
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { /* TODO: Change avatar */ }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = userProfile.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(2.dp).clip(RoundedCornerShape(14.dp))
                )
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
                            color = MaterialTheme.colorScheme.onSurface
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
        var viewingDetailUser by remember { mutableStateOf<Triple<String, String, String>?>(null) }

        // Stats Grid
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            var showListDialog by remember { mutableStateOf(false) }
            var listDialogTitle by remember { mutableStateOf("") }
            
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
                    },
                    confirmButton = {
                        TextButton(onClick = { showListDialog = false }) { Text("Done") }
                    }
                )
            }

            // Sub-dialog: View nested Localiiiy ID for clicked user from list
            if (viewingDetailUser != null) {
                val detailUser = viewingDetailUser!!
                AlertDialog(
                    onDismissRequest = { viewingDetailUser = null },
                    text = {
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
                    },
                    confirmButton = {
                        TextButton(onClick = { viewingDetailUser = null }) { Text("Close") }
                    }
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IdentityStatColumn(count = postsCount.toString(), label = "Posts")
                IdentityStatColumn(count = clipsCount.toString(), label = "Clips")
                if (isOtherUser) {
                    IdentityStatColumn(count = "12", label = "Mutual")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                listDialogTitle = "Connections"
                                showListDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${userProfile.neighborsCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connections", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                listDialogTitle = "Connected"
                                showListDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${userProfile.followingCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connected", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                    }
                } else {
                    IdentityStatColumn(count = marketItemsCount.toString(), label = "Market")
                    IdentityStatColumn(count = studioVideosCount.toString(), label = "Studio")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                listDialogTitle = "Connections"
                                showListDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${userProfile.neighborsCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connections", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                listDialogTitle = "Connected"
                                showListDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${userProfile.followingCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connected", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
}
        }
    }
}

@Composable
private fun IdentityStatColumn(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
