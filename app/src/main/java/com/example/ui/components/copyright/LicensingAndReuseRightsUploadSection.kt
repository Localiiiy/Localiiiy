package com.example.ui.components.copyright

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.copyright.ContentLicensingConfig

/**
 * Mandatory "Licensing & Reuse Rights" configuration widget at upload time.
 * Supports Master Toggle, Granular Options (Audio, Video Remapping, Marketplace),
 * and Dynamic Terms LCC Badge.
 */
@Composable
fun LicensingAndReuseRightsUploadSection(
    config: ContentLicensingConfig,
    onConfigChange: (ContentLicensingConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (config.permitReuse) Color(0xFF041A12) else Color(0xFF1A0A0A)
        ),
        border = BorderStroke(
            1.dp,
            if (config.permitReuse) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFFF43F5E).copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("licensing_reuse_rights_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Dynamic Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (config.permitReuse) Color(0xFF065F46) else Color(0xFF881337)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (config.permitReuse) Icons.Default.VerifiedUser else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (config.permitReuse) Color(0xFF34D399) else Color(0xFFFB7185),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Licensing & Reuse Rights",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (config.permitReuse) "Localiiiy Creative Commons (LCC)" else "All Rights Reserved (ARR)",
                            fontSize = 10.sp,
                            color = if (config.permitReuse) Color(0xFF34D399) else Color(0xFFFB7185),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Dynamic Terms Tag
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (config.permitReuse) Color(0xFF059669).copy(alpha = 0.25f) else Color(0xFFE11D48).copy(alpha = 0.25f),
                    border = BorderStroke(
                        0.8.dp,
                        if (config.permitReuse) Color(0xFF10B981) else Color(0xFFF43F5E)
                    ),
                    modifier = Modifier.testTag("licensing_terms_tag")
                ) {
                    Text(
                        text = if (config.permitReuse) "LCC BADGE 🛡️" else "STRICT ARR 🔒",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (config.permitReuse) Color(0xFF6EE7B7) else Color(0xFFFDA4AF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Master Toggle
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.4f),
                border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                        Text(
                            text = "Permit creators to reuse content?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Grants other Localiiiy creators license to remix, react, or showcase while attributing you.",
                            fontSize = 10.sp,
                            color = Color.LightGray,
                            lineHeight = 13.sp
                        )
                    }

                    Switch(
                        checked = config.permitReuse,
                        onCheckedChange = { checked ->
                            onConfigChange(
                                config.copy(
                                    permitReuse = checked,
                                    allowAudioReuse = checked,
                                    allowVideoRemapping = checked,
                                    allowMarketplaceShowcase = checked,
                                    licenseBadge = if (checked) "Localiiiy Creative Commons (LCC)" else "All Rights Reserved (ARR)"
                                )
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color(0xFF3F3F46)
                        ),
                        modifier = Modifier.testTag("master_licensing_toggle")
                    )
                }
            }

            // Granular Options (if toggled ON)
            AnimatedVisibility(
                visible = config.permitReuse,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "GRANULAR PERMISSION PRESETS:",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399),
                        fontFamily = FontFamily.Monospace
                    )

                    // 1. Audio Reuse
                    GranularOptionRow(
                        title = "Audio / Sound Reuse Allowed",
                        description = "Permits audio extraction for neighborhood Clips & Reels.",
                        icon = Icons.Default.MusicNote,
                        checked = config.allowAudioReuse,
                        onCheckedChange = { onConfigChange(config.copy(allowAudioReuse = it)) },
                        testTag = "granular_audio_reuse"
                    )

                    // 2. Video Remapping / Reaction
                    GranularOptionRow(
                        title = "Video Remapping / Reaction Allowed",
                        description = "Permits split-screen, duet, and reaction commentary usage.",
                        icon = Icons.Default.VideoCall,
                        checked = config.allowVideoRemapping,
                        onCheckedChange = { onConfigChange(config.copy(allowVideoRemapping = it)) },
                        testTag = "granular_video_remapping"
                    )

                    // 3. Marketplace Showcase
                    GranularOptionRow(
                        title = "Marketplace Showcase Allowed",
                        description = "Permits cross-listing visuals into community trade catalogs.",
                        icon = Icons.Default.Storefront,
                        checked = config.allowMarketplaceShowcase,
                        onCheckedChange = { onConfigChange(config.copy(allowMarketplaceShowcase = it)) },
                        testTag = "granular_marketplace_showcase"
                    )

                    // Notice on Strike Exemption
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF022C22),
                        border = BorderStroke(0.6.dp, Color(0xFF059669).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "LCC Exemption: Permitted reuse is protected against automated copyright strikes.",
                                fontSize = 9.sp,
                                color = Color(0xFFA7F3D0),
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GranularOptionRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF062016),
        border = BorderStroke(0.5.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) Color(0xFF34D399) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (checked) Color.White else Color.Gray
                    )
                    Text(
                        text = description,
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
                }
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF10B981),
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 1-Tap Copyright Claim Dialog allowing verified creators to report unauthorized re-uploads.
 */
@Composable
fun CopyrightClaimDialog(
    contentId: String,
    contentTitle: String,
    uploaderHandle: String,
    onDismiss: () -> Unit,
    onConfirmClaim: (reason: String) -> Unit
) {
    var claimReason by remember { mutableStateOf("Unauthorized duplicate re-upload of my original media without permission.") }
    val presetReasons = listOf(
        "Direct visual duplication / stolen clip",
        "Acoustic audio extraction without permission",
        "Commercial marketplace usage violation",
        "Unauthorized remapping / missing attribution"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFFF5252))
                Text("File 1-Tap Copyright Claim", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Contested: \"$contentTitle\" uploaded by @${uploaderHandle.removePrefix("@")}",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF261010),
                    border = BorderStroke(0.8.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ Immediate Action: Contested content will be quarantined/unlisted. 100% of all ad revenues, view payouts, and commissions will be rerouted to you.",
                        fontSize = 10.sp,
                        color = Color(0xFFFFB4B4),
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 13.sp
                    )
                }

                Text("Select Infringement Reason:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                presetReasons.forEach { reason ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (claimReason == reason) Color(0xFF3B1212) else Color(0xFF1E1E1E),
                        border = BorderStroke(0.6.dp, if (claimReason == reason) Color(0xFFFF5252) else Color.DarkGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { claimReason = reason }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = reason,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmClaim(claimReason) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A2A))
            ) {
                Text("File Claim & Quarantining", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.LightGray)
            }
        }
    )
}
