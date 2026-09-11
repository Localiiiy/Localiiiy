package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardMediaSelectorBottomSheet(
    onDismiss: () -> Unit,
    onMediaSelected: (List<String>) -> Unit
) {
    // Local storage thumbnails simulating device gallery
    val localGalleryThumbnails = listOf(
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=85"
    )

    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    var showVideoEditor by remember { mutableStateOf(false) }

    // Video Editor states (Trim start/end seconds & volume slider)
    var trimStartSeconds by remember { mutableStateOf(0f) }
    var trimEndSeconds by remember { mutableStateOf(10f) }
    var audioVolume by remember { mutableStateOf(80f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("standard_media_selector_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (showVideoEditor) "🎬 Video Editor & Trimmer" else "Media Selector 📁",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (showVideoEditor) "Trim clips and adjust audio volume" else "Multi-select photos & videos from storage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedItems.isNotEmpty() && !showVideoEditor) {
                    Button(
                        onClick = { showVideoEditor = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Clip ✂️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (showVideoEditor) {
                // Video Editor Interface
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Clip Trim Range: ${trimStartSeconds.toInt()}s — ${trimEndSeconds.toInt()}s",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        RangeSlider(
                            value = trimStartSeconds..trimEndSeconds,
                            onValueChange = { range ->
                                trimStartSeconds = range.start
                                trimEndSeconds = range.endInclusive
                            },
                            valueRange = 0f..30f,
                            steps = 30,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Audio Volume: ${audioVolume.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Slider(
                            value = audioVolume,
                            onValueChange = { audioVolume = it },
                            valueRange = 0f..100f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { showVideoEditor = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Apply & Return to Gallery")
                        }
                    }
                }
            } else {
                // Quick Actions: Live Camera / Video / Storage Folder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val liveCamShots = listOf(
                                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=85",
                                "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=1080&auto=format&fit=crop&q=85"
                            )
                            val shot = liveCamShots.random()
                            selectedItems = selectedItems + shot
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("live_camera_button")
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Cam 📷", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val liveVideos = listOf(
                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4"
                            )
                            val vid = liveVideos.random()
                            selectedItems = selectedItems + vid
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("live_video_button")
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Video 🎥", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT STORAGE GALLERY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    if (selectedItems.isNotEmpty()) {
                        Text(
                            text = "${selectedItems.size} Selected",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                // Grid of local storage gallery thumbnails
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(localGalleryThumbnails) { mediaUrl ->
                        val isSelected = selectedItems.contains(mediaUrl)
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedItems = if (isSelected) {
                                        selectedItems - mediaUrl
                                    } else {
                                        selectedItems + mediaUrl
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = mediaUrl,
                                contentDescription = "Gallery media item",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confirm Button
            Button(
                onClick = {
                    if (selectedItems.isNotEmpty()) {
                        onMediaSelected(selectedItems.toList())
                    }
                    onDismiss()
                },
                enabled = selectedItems.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_media_selection_button")
            ) {
                Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedItems.isEmpty()) "Select Media to Attach" else "Attach ${selectedItems.size} Item(s) [Trim: ${trimStartSeconds.toInt()}-${trimEndSeconds.toInt()}s, Vol: ${audioVolume.toInt()}%]",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
