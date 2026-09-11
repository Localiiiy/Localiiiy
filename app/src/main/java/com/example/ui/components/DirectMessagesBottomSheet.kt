package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Shield
import android.widget.Toast
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.DoneAll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ChatMessageEntity
import com.example.data.DirectMessageEntity
import com.example.data.UserProfileEntity
import com.example.data.OtherUserEntity
import com.example.util.LocationHelper
import com.example.util.SafetyLogManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectMessagesBottomSheet(
    conversations: List<DirectMessageEntity>,
    activeConversation: DirectMessageEntity?,
    chatMessages: List<ChatMessageEntity>,
    userProfile: UserProfileEntity,
    otherUsers: List<OtherUserEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSelectConversation: (DirectMessageEntity) -> Unit,
    onBackToInbox: () -> Unit,
    onSendMessage: (String, String?) -> Unit,
    onCreateGroupChat: (title: String) -> Unit
) {
    var inputMessageText by remember { mutableStateOf("") }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var groupTitleInput by remember { mutableStateOf("") }
    var showMediaSelector by remember { mutableStateOf(false) }
    var showSafetyLogsSheet by remember { mutableStateOf(false) }

    if (showSafetyLogsSheet) {
        SafetyLogsViewerSheet(
            onDismiss = { showSafetyLogsSheet = false },
            onExportCurrentChat = if (activeConversation != null) {
                {
                    val ctx = context
                    val res = SafetyLogManager.exportChatSafetyLog(
                        context = ctx,
                        currentUser = userProfile.username,
                        targetUser = activeConversation.contactUsername,
                        conversationId = activeConversation.conversationId,
                        messages = chatMessages
                    )
                    if (res.isSuccess) {
                        Toast.makeText(ctx, "Chat exported to internal storage (/safety_logs/)! SHA-256 sealed 🛡️", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(ctx, "Export failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else null,
            activeChatContact = activeConversation?.contactUsername
        )
    }

    if (showMediaSelector) {
        StandardMediaSelectorBottomSheet(
            onDismiss = { showMediaSelector = false },
            onMediaSelected = { mediaUrls ->
                if (mediaUrls.isNotEmpty()) {
                    onSendMessage(inputMessageText, mediaUrls.first())
                    inputMessageText = ""
                }
                showMediaSelector = false
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("direct_messages_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .navigationBarsPadding()
        ) {
            if (activeConversation == null) {
                // Main Inbox List
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Direct & Group Messages",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Connect with neighbors & creator circles",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Safety Logs Archive Button
                        OutlinedButton(
                            onClick = { showSafetyLogsSheet = true },
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "Safety Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Create Local Group Button
                        Button(
                            onClick = { showCreateGroupDialog = true },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "New Group", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(conversations, key = { it.conversationId }) { conv ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectConversation(conv) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(conv.contactAvatar)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = conv.contactUsername,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                )
                                if (conv.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(Color(0xFF388E3C))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = conv.contactUsername,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (!conv.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (conv.isGroup) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "GROUP (${conv.groupMembersCount})",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Last message with locality label
                                Text(
                                    text = (if (conv.isFromMe) "You: " else "") + conv.lastMessage,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (!conv.isRead) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    color = if (!conv.isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (conv.distanceKm != null || conv.landmark != null) {
                                    val dist = LocationHelper.formatDistanceLabel(conv.distanceKm)
                                    Text(
                                        text = "📍 $dist • ${conv.landmark ?: "Locality"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatRelativeTime(conv.timestamp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Active Chat Conversation View
                val conv = activeConversation

                // Chat Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToInbox) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(conv.contactAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = conv.contactUsername,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conv.contactUsername,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val dist = LocationHelper.formatDistanceLabel(conv.distanceKm)
                        Text(
                            text = if (conv.isGroup) "Local Community Circle • $dist" else "📍 $dist • ${conv.landmark ?: "Nearby"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!conv.isGroup) {
                        val otherUser = otherUsers.find { it.username == conv.contactUsername }
                        val isFollowingMe = otherUser?.isFriend == true // Approximation for they follow me
                        
                        val context = LocalContext.current
                        
                        IconButton(onClick = {
                            if (isFollowingMe) {
                                Toast.makeText(context, "Calling ${conv.contactUsername}...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "You can only call users who are connected with you.", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        IconButton(onClick = {
                            val ctx = context
                            val res = SafetyLogManager.exportChatSafetyLog(
                                context = ctx,
                                currentUser = userProfile.username,
                                targetUser = conv.contactUsername,
                                conversationId = conv.conversationId,
                                messages = chatMessages
                            )
                            if (res.isSuccess) {
                                Toast.makeText(ctx, "Safety Log exported to internal storage! SHA-256 sealed 🛡️", Toast.LENGTH_SHORT).show()
                                showSafetyLogsSheet = true
                            } else {
                                Toast.makeText(ctx, "Export failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = "Export Safety Log (Evidence)",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )

                // Conversation Message Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    reverseLayout = true
                ) {
                    items(chatMessages.reversed(), key = { it.id }) { message ->
                        ChatBubbleItem(message = message, isCurrentUser = message.isFromMe)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Chat Input Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val otherUser = otherUsers.find { it.username == conv.contactUsername }
                        val isFollowingMe = otherUser?.isFriend == true
                        val context = LocalContext.current
                        
                        IconButton(
                            onClick = { 
                                if (isFollowingMe || conv.isGroup) {
                                    showMediaSelector = true 
                                } else {
                                    Toast.makeText(context, "You can only send media to users who are connected with you.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Attach",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        OutlinedTextField(
                            value = inputMessageText,
                            onValueChange = { inputMessageText = it },
                            placeholder = {
                                Text(
                                    text = "Send message to ${conv.contactUsername}...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("direct_message_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = {
                                if (inputMessageText.isNotBlank()) {
                                    onSendMessage(inputMessageText, null)
                                    inputMessageText = ""
                                }
                            },
                            enabled = inputMessageText.isNotBlank(),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (inputMessageText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (inputMessageText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Group Chat Dialog
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create Neighborhood Group Chat") },
            text = {
                Column {
                    Text(
                        text = "Make a group chat with creators and neighbors in your locality to plan meetups and collaborate.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = groupTitleInput,
                        onValueChange = { groupTitleInput = it },
                        placeholder = { Text("Group Name (e.g. Pike Place Creators)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupTitleInput.isNotBlank()) {
                            onCreateGroupChat(groupTitleInput.trim())
                            groupTitleInput = ""
                            showCreateGroupDialog = false
                        }
                    }
                ) {
                    Text("Create Group")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessageEntity, isCurrentUser: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isCurrentUser) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(message.senderAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = message.senderUsername,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                ),
                color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // If this message shares a post or clip preview
                    if (!message.sharedMediaUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(message.sharedMediaUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Shared Media",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        if (!message.sharedCaption.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.sharedCaption,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 2.dp, start = if (!isCurrentUser) 34.dp else 0.dp)
        ) {
            Text(
                text = formatRelativeTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            if (isCurrentUser) {
                // Read Receipt ticks
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
