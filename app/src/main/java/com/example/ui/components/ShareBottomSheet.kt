package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.InitialData
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.LocaliiiyLanguage
import com.example.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    targetTitle: String,
    clipId: Long? = null,
    creatorHandle: String? = null,
    clipCaption: String? = null,
    itemType: String = if (clipId != null) "Clip" else "Post",
    itemId: Any? = clipId,
    location: String? = null,
    priceFormatted: String? = null,
    currentLanguage: LocaliiiyLanguage = LocaliiiyLanguage.EN,
    onDismiss: () -> Unit,
    onShareSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var sentUsers by remember { mutableStateOf(setOf<String>()) }
    var copiedLink by remember { mutableStateOf(false) }

    // Generate Deep Link URL for Creator Clips, Posts, Market, or Studio items
    val effectiveId = itemId ?: clipId ?: Math.abs(targetTitle.hashCode())
    val generatedDeepLink = remember(effectiveId, itemType) {
        ShareHelper.buildDeepLink(itemType, effectiveId)
    }

    val fullShareMessage = remember(targetTitle, itemType, effectiveId, creatorHandle, clipCaption, location, priceFormatted, currentLanguage) {
        ShareHelper.buildShareText(
            title = targetTitle,
            itemType = itemType,
            id = effectiveId,
            creatorHandle = creatorHandle,
            caption = clipCaption,
            location = location,
            priceFormatted = priceFormatted,
            language = currentLanguage
        )
    }

    val suggestedContacts = listOf(
        Pair("elena.design", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80"),
        Pair("sora.tokyo", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80"),
        Pair("marcus_travels", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80"),
        Pair("chloe.cafes", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80"),
        Pair("kai_sound", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80")
    )

    fun launchNativeShare() {
        ShareHelper.launchNativeShare(
            context = context,
            shareText = fullShareMessage,
            subject = "Share on Localiiiy",
            chooserTitle = "Share Outside App"
        )
        onShareSuccess("Opening native share sheet...")
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("share_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (clipId != null) "Share Creator Clip" else "Share to",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Deep Link Card Box (Generated Deep Link for specific creator clip)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("clip_deep_link_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (clipId != null) "Creator Clip Deep Link" else "Generated Deep Link",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = generatedDeepLink,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = LocaliiiyPrimaryTeal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedDeepLink))
                                copiedLink = true
                                onShareSuccess("Deep link copied to clipboard: $generatedDeepLink")
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("copy_deep_link_button")
                        ) {
                            Icon(
                                imageVector = if (copiedLink) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = if (copiedLink) LocaliiiyAccentMint else LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copiedLink) "Copied!" else "Copy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (copiedLink) LocaliiiyAccentMint else LocaliiiyPrimaryTeal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Direct Connections Send Row
            Text(
                text = "Send directly to Connections:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(suggestedContacts) { contact ->
                    val isSent = sentUsers.contains(contact.first)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(68.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(contact.second)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = contact.first,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                            if (isSent) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sent",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = contact.first,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (!isSent) {
                                    sentUsers = sentUsers + contact.first
                                    onShareSuccess("Sent to ${contact.first}")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(26.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = if (isSent) "Sent" else "Send",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSent) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Pills (Copy Deep Link, Native Share Outside App, Add to Story)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareActionItem(
                    icon = if (copiedLink) Icons.Default.Check else Icons.Default.ContentCopy,
                    label = if (copiedLink) "Link Copied" else "Copy Link",
                    onClick = {
                        clipboardManager.setText(AnnotatedString(generatedDeepLink))
                        copiedLink = true
                        onShareSuccess("Link copied to clipboard!")
                    }
                )
                ShareActionItem(
                    icon = Icons.Outlined.AddCircleOutline,
                    label = "Add to Story",
                    onClick = {
                        onShareSuccess("Added to your story!")
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.Share,
                    label = "Share Outside App",
                    onClick = {
                        launchNativeShare()
                    }
                )
            }
        }
    }
}

@Composable
private fun ShareActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("share_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

