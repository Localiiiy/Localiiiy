package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.StoryEntity
import com.example.ui.theme.LocaliiiyHeart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewerDialog(
    stories: List<StoryEntity>,
    initialStory: StoryEntity,
    onDismiss: () -> Unit,
    onStoryViewed: (Long) -> Unit,
    onSendReply: (String) -> Unit
) {
    var currentIndex by remember {
        mutableStateOf(stories.indexOfFirst { it.id == initialStory.id }.coerceAtLeast(0))
    }
    val currentStory = stories.getOrNull(currentIndex) ?: return

    val progress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isPaused by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex) {
        onStoryViewed(currentStory.id)
        progress.snapTo(0f)
        try {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
            )
            // Move to next story when timer completes
            if (currentIndex < stories.size - 1) {
                currentIndex++
            } else {
                onDismiss()
            }
        } catch (_: Exception) {
            // Cancelled on tap navigation
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_dialog")
        ) {
            // Story Media Background
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentStory.mediaUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = currentStory.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPaused = true
                                tryAwaitRelease()
                                isPaused = false
                            },
                            onTap = { offset ->
                                val screenWidth = size.width
                                if (offset.x < screenWidth * 0.35f) {
                                    // Previous Story
                                    if (currentIndex > 0) {
                                        currentIndex--
                                    }
                                } else {
                                    // Next Story
                                    if (currentIndex < stories.size - 1) {
                                        currentIndex++
                                    } else {
                                        onDismiss()
                                    }
                                }
                            }
                        )
                    }
            )

            // Top Progress Bars & User Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Progress segments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { index, _ ->
                        val segmentProgress = when {
                            index < currentIndex -> 1f
                            index == currentIndex -> progress.value
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { segmentProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                        )
                    }
                }

                // Story User Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentStory.userAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentStory.username,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentStory.username,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• 2h",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_story_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close story",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Caption overlay if present
            if (currentStory.caption.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = currentStory.caption,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Bottom Story Reply & Heart Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = {
                        Text(
                            text = "✍️ Send reply...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("story_reply_input"),
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    trailingIcon = {
                        if (replyText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onSendReply(replyText)
                                    replyText = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                AnimatedLikeButton(
                    isLiked = isLiked,
                    onLikeClick = { isLiked = !isLiked },
                    symbolSize = 26.sp,
                    touchTargetSize = 48.dp,
                    testTag = "story_like_button"
                )
            }
        }
    }
}
