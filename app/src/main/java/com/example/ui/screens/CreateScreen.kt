package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MusicNote
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
import com.example.ui.CreationMode
import com.example.ui.FilterPreset
import com.example.ui.PhotoFilters
import com.example.ui.components.ImageWithFilter
import com.example.ui.components.StandardMediaSelectorBottomSheet
import com.example.data.copyright.ContentLicensingConfig
import com.example.data.copyright.MediaFingerprintEngine
import com.example.data.copyright.CopyrightManager
import com.example.ui.components.copyright.LicensingAndReuseRightsUploadSection
import com.example.util.LocationHelper
import com.example.util.UserLocationData
import com.example.util.HapticHelper
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch

@Composable
fun CreateScreen(
    creationMode: CreationMode,
    selectedMediaUri: String,
    selectedFilter: FilterPreset,
    detectedLocation: UserLocationData?,
    draftClips: List<com.example.data.DraftClipEntity> = emptyList(),
    editingDraft: com.example.data.DraftClipEntity? = null,
    onSaveDraftClip: (mediaUri: String, caption: String, soundTitle: String?, location: String?, landmark: String?, draftId: Long) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteDraftClip: (Long) -> Unit = {},
    onModeChange: (CreationMode) -> Unit,
    onSelectMedia: (String) -> Unit,
    onSelectFilter: (FilterPreset) -> Unit,
    onPublish: (caption: String, location: String?, landmark: String?, latitude: Double?, longitude: Double?, soundTitle: String?, isVoicePrint: Boolean, voiceDurationSeconds: Int) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onDetectLocationClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var showDraftsSheet by remember { mutableStateOf(false) }

    var caption by remember { mutableStateOf("") }
    var isAiContent by remember { mutableStateOf(false) }
    var locationOptional by remember { mutableStateOf(true) }
    var showLocationOnClip by remember { mutableStateOf(true) }
    var selectedLocation by remember { mutableStateOf(detectedLocation?.locationName ?: "Seattle, WA") }
    var selectedLandmark by remember { mutableStateOf(detectedLocation?.landmark ?: "Pike Place Market") }
    var selectedSound by remember { mutableStateOf<String?>("Original Audio • Locality Vibes") }
    var showLocationSelector by remember { mutableStateOf(false) }
    var showSoundSelector by remember { mutableStateOf(false) }

    // Audio recording & microphone states
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Microphone access granted 🎙️", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Microphone permission required for audio recording", Toast.LENGTH_SHORT).show()
        }
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                caption = if (caption.isBlank()) spokenText else "$caption $spokenText"
                Toast.makeText(context, "Transcribed: \"$spokenText\"", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var isRecordingAudioClip by remember { mutableStateOf(false) }
    var recordedSeconds by remember { mutableIntStateOf(0) }
    var hasAttachedAudioClip by remember { mutableStateOf(false) }
    var attachedAudioDuration by remember { mutableIntStateOf(15) }
    var isAudioPreviewPlaying by remember { mutableStateOf(false) }
    var audioPlaybackProgress by remember { mutableFloatStateOf(0f) }

    var licensingConfig by remember {
        mutableStateOf(
            ContentLicensingConfig(
                permitReuse = true,
                allowAudioReuse = true,
                allowVideoRemapping = true,
                allowMarketplaceShowcase = true,
                licenseBadge = "Localiiiy Creative Commons (LCC)"
            )
        )
    }
    var interceptionDialogReason by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isRecordingAudioClip) {
        if (isRecordingAudioClip) {
            recordedSeconds = 0
            while (isRecordingAudioClip && recordedSeconds < 60) {
                kotlinx.coroutines.delay(1000)
                recordedSeconds++
            }
            if (recordedSeconds >= 60) {
                isRecordingAudioClip = false
                hasAttachedAudioClip = true
                attachedAudioDuration = 60
                Toast.makeText(context, "Voice note max duration reached (60s) • Attached 🎙️", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isAudioPreviewPlaying) {
        if (isAudioPreviewPlaying) {
            audioPlaybackProgress = 0f
            val totalSteps = (attachedAudioDuration * 10).coerceAtLeast(10)
            for (i in 0..totalSteps) {
                if (!isAudioPreviewPlaying) break
                kotlinx.coroutines.delay(100)
                audioPlaybackProgress = i.toFloat() / totalSteps
            }
            isAudioPreviewPlaying = false
            audioPlaybackProgress = 0f
        }
    }

    fun startVoiceToText() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your neighborhood update...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Speech recognizer not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleAudioClipRecording() {
        if (!isRecordingAudioClip) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                return
            }
            isRecordingAudioClip = true
            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SELECTION)
        } else {
            isRecordingAudioClip = false
            hasAttachedAudioClip = true
            attachedAudioDuration = recordedSeconds.coerceAtLeast(3)
            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SUCCESS)
            Toast.makeText(context, "Voice dispatch (${attachedAudioDuration}s) attached to update! 🎙️", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(editingDraft) {
        if (editingDraft != null) {
            caption = editingDraft.caption
            editingDraft.soundTitle.let { selectedSound = it }
            editingDraft.location?.let { selectedLocation = it }
            editingDraft.landmark?.let { selectedLandmark = it }
            if (editingDraft.mediaUri.isNotBlank()) {
                onSelectMedia(editingDraft.mediaUri)
            }
        }
    }

    var showStandardMediaSelector by remember { mutableStateOf(false) }

    // Native Android Photo & Video Picker from Device Storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSelectMedia(uri.toString())
        }
    }

    LaunchedEffect(detectedLocation) {
        if (detectedLocation != null) {
            selectedLocation = detectedLocation.locationName
            selectedLandmark = detectedLocation.landmark ?: "Downtown"
        }
    }

    val presetGalleryMedia = listOf(
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1080&auto=format&fit=crop&q=85",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=1080&auto=format&fit=crop&q=85"
    )

    val localLandmarkPresets = listOf(
        Pair("Pike Place Market", "Seattle, WA"),
        Pair("Pioneer Square Art Walk", "Seattle, WA"),
        Pair("Belltown Espresso Bar", "Seattle, WA"),
        Pair("Capitol Hill Sound Record", "Seattle, WA"),
        Pair("Waterfront Pier 57", "Seattle, WA"),
        Pair("Fremont Bridge Market", "Seattle, WA"),
        Pair("South Lake Union Tech Hub", "Seattle, WA")
    )

    val viralMusicTracks = listOf(
        "🔥 Seattle Summer Anthem • 2026 Viral Hit",
        "🌊 Pacific Chillwave • Trending #1 on Charts",
        "🎸 Pike Place Acoustic Bounce • Global Viral",
        "⚡ Neon Nights Electronic • Global Club Viral",
        "🌙 Midnight Sunset Lo-Fi • Chill Vibes Viral",
        "🎧 Urban Soundscape • Trending Neighborhood Remix",
        "✨ Golden Hour Melody • Acoustic Viral",
        "🚀 Space Needle Synthwave • Viral Track"
    )

    var musicSearchQuery by remember { mutableStateOf("") }
    var showCameraScreen by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .testTag("create_screen_container")
    ) {
        // Top Header Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "New ${creationMode.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (editingDraft != null) {
                    Text(
                        text = "Editing Saved Draft #${editingDraft.id}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (creationMode == CreationMode.CLIP) {
                    IconButton(
                        onClick = {
                            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SELECTION)
                            showDraftsSheet = true
                        },
                        modifier = Modifier.testTag("open_clip_drafts_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (draftClips.isNotEmpty()) {
                                    Badge { Text("${draftClips.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Saved Drafts",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SUCCESS)
                            onSaveDraftClip(
                                selectedMediaUri,
                                caption,
                                selectedSound,
                                selectedLocation,
                                selectedLandmark,
                                editingDraft?.id ?: 0L
                            )
                        },
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("save_draft_clip_button")
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Save Draft", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        if (!isUploading) {
                            // 1. Check if user is suspended due to 3 active copyright strikes
                            if (CopyrightManager.isUserSuspended("current_user")) {
                                Toast.makeText(context, "Upload Suspended: Account has 3 active copyright strikes.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            // 2. Compute media fingerprint & check for unauthorized duplicates
                            val fingerprint = MediaFingerprintEngine.generateFingerprint(
                                contentUri = selectedMediaUri ?: "https://localiiiy.app/pulse/${System.currentTimeMillis()}",
                                caption = caption,
                                audioTitle = if (hasAttachedAudioClip) "Voice Dispatch (${attachedAudioDuration}s)" else selectedSound,
                                uploaderHandle = "current_user",
                                licensingConfig = licensingConfig
                            )

                            val eligibility = MediaFingerprintEngine.checkUploadEligibility(fingerprint)
                            if (eligibility is MediaFingerprintEngine.DuplicateDetectionResult.InterceptedDenied) {
                                interceptionDialogReason = eligibility.reason
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                return@Button
                            }

                            isUploading = true
                            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SUCCESS)
                            onPublish(
                                caption,
                                selectedLocation,
                                selectedLandmark,
                                detectedLocation?.latitude ?: LocationHelper.DEFAULT_LAT,
                                detectedLocation?.longitude ?: LocationHelper.DEFAULT_LNG,
                                selectedSound,
                                hasAttachedAudioClip,
                                attachedAudioDuration
                            )
                        }
                    },
                    enabled = !isUploading,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("publish_post_button")
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Posting...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "Broadcast",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Mode Switcher Tabs (POST | CLIP | STORY)
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CreationMode.values().forEach { mode ->
                    val isSelected = creationMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onModeChange(mode) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mode.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Real-Time GPS Location Auto-Detection Notification Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS Auto Detected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Auto-detected Location: ${selectedLandmark}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Visible to neighbors within 3 km in real-time",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                IconButton(
                    onClick = onDetectLocationClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh GPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Preview Window with Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (creationMode == CreationMode.POST) 1.05f else 9f / 16f)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            ImageWithFilter(
                mediaUrl = selectedMediaUri,
                filterName = selectedFilter.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = "Selected media preview"
            )

            // Filter Name Badge Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Filter: ${selectedFilter.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
            }

            // Location watermark badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.Black.copy(alpha = 0.65f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "$selectedLandmark • Real-Time",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filters Carousel
        Text(
            text = "PHOTO & VIDEO FILTERS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(PhotoFilters) { filter ->
                val isSelected = selectedFilter.name == filter.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectFilter(filter) }
                        .testTag("filter_preset_${filter.name}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                                else Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            )
                    ) {
                        ImageWithFilter(
                            mediaUrl = selectedMediaUri,
                            filterName = filter.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = filter.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Unrestricted Media & Camera Upload Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            if (creationMode == CreationMode.CLIP) ActivityResultContracts.PickVisualMedia.VideoOnly
                            else ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("upload_from_storage_button")
            ) {
                Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = false
                )
            }

            OutlinedButton(
                onClick = { showStandardMediaSelector = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("media_selector_sheet_button")
            ) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Studio",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = false
                )
            }

            Button(
                onClick = { showCameraScreen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("live_camera_button")
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Camera",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        if (showStandardMediaSelector) {
            StandardMediaSelectorBottomSheet(
                onDismiss = { showStandardMediaSelector = false },
                onMediaSelected = { selectedList ->
                    if (selectedList.isNotEmpty()) {
                        onSelectMedia(selectedList.first())
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gallery Media Selector
        Text(
            text = "CHOOSE FROM RECENT MEDIA",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(presetGalleryMedia) { mediaUrl ->
                val isSelected = selectedMediaUri == mediaUrl
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            else Modifier
                        )
                        .clickable { onSelectMedia(mediaUrl) }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(mediaUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Gallery item",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Caption & Tags Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Write a caption for your neighbors... #locality #Localiiiy") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_caption_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Section: Microphone & Voice Audio Tools (Voice-to-Text & Audio Clip Recorder)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_microphone_voice_tools_card")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (hasAudioPermission) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Microphone",
                                tint = if (isRecordingAudioClip) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Voice & Audio Notes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (!hasAudioPermission) {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                            ) {
                                Text(
                                    text = "Grant Mic Access",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons: Voice-to-Text & Record Voice Note
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Voice-to-Text Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { startVoiceToText() }
                                .testTag("btn_voice_to_text")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Voice-to-Text",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Record Audio Clip Button
                        val isRecording = isRecordingAudioClip
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isRecording) Color(0xFFDC2626) else if (hasAttachedAudioClip) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isRecording) Color(0xFFDC2626) else if (hasAttachedAudioClip) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { toggleAudioClipRecording() }
                                .testTag("btn_record_audio_clip")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isRecording) Color.White else if (hasAttachedAudioClip) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isRecording) "Stop (${recordedSeconds}s)" else if (hasAttachedAudioClip) "Audio (${attachedAudioDuration}s)" else "Record Clip",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRecording) Color.White else if (hasAttachedAudioClip) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Recording In-Progress Banner
                    if (isRecordingAudioClip) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                    Column {
                                        Text(
                                            text = "Recording Voice Note...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                        Text(
                                            text = "00:${if (recordedSeconds < 10) "0$recordedSeconds" else "$recordedSeconds"} / 01:00 (Speak clearly)",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Button(
                                    onClick = { toggleAudioClipRecording() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Done", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Attached Audio Clip Preview Bar
                    if (hasAttachedAudioClip && !isRecordingAudioClip) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
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
                                    IconButton(
                                        onClick = { isAudioPreviewPlaying = !isAudioPreviewPlaying },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(
                                            imageVector = if (isAudioPreviewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause Voice Clip",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "🎙️ Voice Dispatch Attached",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "• ${attachedAudioDuration}s",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        // Animated progress waveform
                                        LinearProgressIndicator(
                                            progress = { if (isAudioPreviewPlaying) audioPlaybackProgress else 0.4f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(100.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        hasAttachedAudioClip = false
                                        isAudioPreviewPlaying = false
                                        Toast.makeText(context, "Voice note detached", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Detach Voice Note",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Mandatory Licensing & Reuse Rights Configuration Section
            LicensingAndReuseRightsUploadSection(
                config = licensingConfig,
                onConfigChange = { licensingConfig = it }
            )

            // Location Selector Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLocationSelector = !showLocationSelector }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (locationOptional) "United States (Detected Country)" else "$selectedLandmark, $selectedLocation",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap to switch neighborhood or landmark",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Include precise location", fontSize = 12.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = !locationOptional,
                    onCheckedChange = { locationOptional = !it },
                    modifier = Modifier.scale(0.8f)
                )
            }
            if (showLocationSelector) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(localLandmarkPresets) { (landmark, city) ->
                        AssistChip(
                            onClick = {
                                selectedLandmark = landmark
                                selectedLocation = city
                                showLocationSelector = false
                            },
                            label = { Text("📍 $landmark", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Audio Track Selector Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSoundSelector = !showSoundSelector }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = "Music",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedSound ?: "Add Music",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // AI Content Switch
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoFixHigh,
                    contentDescription = "AI Content",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI-Generated Content", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Label this post as created with AI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isAiContent,
                    onCheckedChange = { isAiContent = it }
                )
            }
            if (showSoundSelector) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎵 Add Music & Viral Audio",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = musicSearchQuery,
                        onValueChange = { musicSearchQuery = it },
                        placeholder = { Text("Search music tracks, artists, or Google vibes...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (musicSearchQuery.isNotBlank()) {
                                IconButton(onClick = { musicSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )

                    if (musicSearchQuery.isNotBlank()) {
                        // Custom search result / Google query result option
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSound = "$musicSearchQuery • Custom Audio"
                                    showSoundSelector = false
                                    musicSearchQuery = ""
                                }
                        ) {
                            Text(
                                text = "🔍 Use Custom Search: \"$musicSearchQuery\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Text(
                        text = "🔥 New Viral Music (Top Charts)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    val filteredViralTracks = if (musicSearchQuery.isBlank()) {
                        viralMusicTracks
                    } else {
                        viralMusicTracks.filter { it.contains(musicSearchQuery, ignoreCase = true) }
                    }

                    filteredViralTracks.forEach { sound ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedSound == sound) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSound = sound
                                    showSoundSelector = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sound,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (selectedSound == sound) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedSound == sound) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedSound == sound) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced Locality Broadcast Upload Action Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("broadcast_upload_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (creationMode) {
                                        CreationMode.POST -> Icons.Default.Public
                                        CreationMode.CLIP -> Icons.Default.Videocam
                                        CreationMode.STORY -> Icons.Default.Bolt
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = when (creationMode) {
                                    CreationMode.POST -> "Locality Post Broadcast"
                                    CreationMode.CLIP -> "Viral Clip Broadcast"
                                    CreationMode.STORY -> "24h Locality Story"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$selectedLandmark • Connected & Locality Reach",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (showLocationOnClip) "GPS Visible" else "GPS Hidden",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Optional Location Visibility Toggle during Broadcast / Upload
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (showLocationOnClip) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Show Location on Clip",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (showLocationOnClip) "Display locality & landmark badge on clip" else "Location hidden • Private locality broadcast",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = showLocationOnClip,
                        onCheckedChange = { showLocationOnClip = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("toggle_show_location_on_clip")
                    )
                }

                if (isUploading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        if (!isUploading) {
                            isUploading = true
                            HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SUCCESS)
                            onPublish(
                                caption,
                                if (showLocationOnClip) selectedLocation else null,
                                if (showLocationOnClip) selectedLandmark else null,
                                if (showLocationOnClip) (detectedLocation?.latitude ?: LocationHelper.DEFAULT_LAT) else null,
                                if (showLocationOnClip) (detectedLocation?.longitude ?: LocationHelper.DEFAULT_LNG) else null,
                                selectedSound,
                                hasAttachedAudioClip,
                                attachedAudioDuration
                            )
                        }
                    },
                    enabled = !isUploading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_bottom_button")
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publishing to Locality...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Broadcast ${creationMode.name.lowercase().replaceFirstChar { it.uppercase() }} Now",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    if (showCameraScreen) {
        CreatorClipsCameraScreen(
            onClipRecorded = { uri ->
                onSelectMedia(uri.toString())
                showCameraScreen = false
            },
            onSaveDraft = { uri ->
                onSaveDraftClip(
                    uri.toString(),
                    caption,
                    selectedSound,
                    selectedLocation,
                    selectedLandmark,
                    editingDraft?.id ?: 0L
                )
                showCameraScreen = false
            },
            onDismiss = { showCameraScreen = false }
        )
    }

    if (showDraftsSheet) {
        com.example.ui.components.CreatorClipDraftsBottomSheet(
            drafts = draftClips,
            onSelectDraftForEdit = { draft ->
                caption = draft.caption
                draft.soundTitle.let { selectedSound = it }
                draft.location?.let { selectedLocation = it }
                draft.landmark?.let { selectedLandmark = it }
                if (draft.mediaUri.isNotBlank()) {
                    onSelectMedia(draft.mediaUri)
                }
                showDraftsSheet = false
            },
            onDeleteDraft = { draftId ->
                onDeleteDraftClip(draftId)
            },
            onCreateNewClip = {
                showDraftsSheet = false
            },
            onDismiss = { showDraftsSheet = false }
        )
    }

    // Media Fingerprint Anti-Theft Duplicate Interception Dialog
    interceptionDialogReason?.let { reason ->
        AlertDialog(
            onDismissRequest = { interceptionDialogReason = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFEF4444))
                    Text("Upload Intercepted: Media Match", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Anti-Theft Media Rights Enforcement intercepted this upload:",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF261010),
                        border = BorderStroke(0.8.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = reason,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5),
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }
                    Text(
                        text = "The original rights holder has designated this asset as All Rights Reserved (ARR). To protect creators, duplicate re-uploads without explicit license are blocked.",
                        fontSize = 10.5.sp,
                        color = Color.Gray,
                        lineHeight = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { interceptionDialogReason = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("I Understand", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
    }
}
