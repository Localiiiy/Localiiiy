package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal

data class AtmospherePreset(
    val key: String,
    val name: String,
    val icon: String,
    val subtitle: String,
    val previewColors: List<Color>
)

object AppAtmospherePresets {
    val ALL = listOf(
        AtmospherePreset(
            key = "DEFAULT",
            name = "Default",
            icon = "📱",
            subtitle = "Sleek adaptive theme matching system settings",
            previewColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
        ),
        AtmospherePreset(
            key = "BLACK_HOLE",
            name = "Black Hole",
            icon = "🕳️",
            subtitle = "Ultra-deep singularity void & gravitational accretion glow",
            previewColors = listOf(Color(0xFF030206), Color(0xFF1D0B38), Color(0xFFFF6A00))
        ),
        AtmospherePreset(
            key = "MOON",
            name = "Moon (White)",
            icon = "🌕",
            subtitle = "Luminous lunar surface with pristine silver-white contrast",
            previewColors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        ),
        AtmospherePreset(
            key = "GALAXY",
            name = "Galaxy",
            icon = "🌌",
            subtitle = "Deep cosmic space, stellar cyan & pulsar magenta stardust",
            previewColors = listOf(Color(0xFF09061A), Color(0xFF4338CA), Color(0xFFD946EF))
        ),
        AtmospherePreset(
            key = "CUSTOM",
            name = "Custom (Self Image)",
            icon = "🖼️",
            subtitle = "Select your personal photo, selfie or custom backdrop",
            previewColors = listOf(Color(0xFF0D9488), Color(0xFF14B8A6), Color(0xFF0F172A))
        )
    )

    val CUSTOM_PRESET_IMAGES = listOf(
        Pair("Night City Skyline", "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop&q=80"),
        Pair("Cosmic Nebula", "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&auto=format&fit=crop&q=80"),
        Pair("Minimalist Architecture", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800&auto=format&fit=crop&q=80"),
        Pair("Golden Sunset", "https://images.unsplash.com/photo-1495616811223-4d98c6e9c869?w=800&auto=format&fit=crop&q=80")
    )
}

@Composable
fun AppAtmosphereBackground(
    themeKey: String,
    customImageUri: String = "",
    modifier: Modifier = Modifier
) {
    AppAtmosphereBackground(
        themeBackground = themeKey,
        customImageUri = customImageUri,
        modifier = modifier
    ) {}
}

@Composable
fun AppAtmosphereBackground(
    themeBackground: String,
    customImageUri: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (themeBackground.uppercase()) {
            "BLACK_HOLE" -> {
                BlackHoleAtmosphereCanvas()
            }
            "MOON" -> {
                MoonAtmosphereCanvas()
            }
            "GALAXY" -> {
                GalaxyAtmosphereCanvas()
            }
            "CUSTOM" -> {
                CustomAtmosphereCanvas(imageUri = customImageUri)
            }
            else -> {
                // Default clean background handled by Theme
            }
        }

        // Child App Content
        content()
    }
}

@Composable
private fun BlackHoleAtmosphereCanvas() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020205))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width * 0.5f, size.height * 0.35f)
            val radius = size.width * 0.65f

            // 1. Accretion Disk Plasma Radial Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF7A00).copy(alpha = 0.28f),
                        Color(0xFF9333EA).copy(alpha = 0.35f),
                        Color(0xFF1E0B38).copy(alpha = 0.65f),
                        Color(0xFF020205).copy(alpha = 0.95f),
                        Color(0xFF020205)
                    ),
                    center = centerOffset,
                    radius = radius
                ),
                center = centerOffset,
                radius = radius
            )

            // 2. Gravitational Singularity Event Horizon (Deep Black Void)
            drawCircle(
                color = Color.Black,
                center = centerOffset,
                radius = size.width * 0.22f
            )

            // 3. Event Horizon Corona Lensing Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFB74D).copy(alpha = 0.7f),
                        Color(0xFFFF5722).copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = size.width * 0.24f
                ),
                center = centerOffset,
                radius = size.width * 0.24f,
                style = Stroke(width = 4.dp.toPx())
            )

            // 4. Subtle Gravitational Lensing Arc Outer Ring
            drawCircle(
                color = Color(0xFFC084FC).copy(alpha = 0.25f),
                center = centerOffset,
                radius = size.width * 0.42f,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Composable
private fun MoonAtmosphereCanvas() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFAFBFC),
                        Color(0xFFF1F5F9),
                        Color(0xFFE2E8F0)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width * 0.8f, size.height * 0.15f)
            val moonRadius = size.width * 0.35f

            // Soft Lunar Halo Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color(0xFFE2E8F0).copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = moonRadius * 1.5f
                ),
                center = centerOffset,
                radius = moonRadius * 1.5f
            )

            // Lunar Sphere Highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF8FAFC),
                        Color(0xFFE2E8F0).copy(alpha = 0.6f)
                    ),
                    center = centerOffset,
                    radius = moonRadius
                ),
                center = centerOffset,
                radius = moonRadius
            )

            // Subtle Lunar Crater Accents
            drawCircle(
                color = Color(0xFFCBD5E1).copy(alpha = 0.22f),
                center = Offset(centerOffset.x - moonRadius * 0.25f, centerOffset.y - moonRadius * 0.15f),
                radius = moonRadius * 0.18f
            )
            drawCircle(
                color = Color(0xFFCBD5E1).copy(alpha = 0.18f),
                center = Offset(centerOffset.x + moonRadius * 0.1f, centerOffset.y + moonRadius * 0.2f),
                radius = moonRadius * 0.12f
            )
        }
    }
}

@Composable
private fun GalaxyAtmosphereCanvas() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF05030F),
                        Color(0xFF0F0B24),
                        Color(0xFF180E38),
                        Color(0xFF070414)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 1. Nebula Cluster 1 (Pulsar Magenta)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE040FB).copy(alpha = 0.28f),
                        Color(0xFF7C3AED).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.25f, size.height * 0.25f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.25f, size.height * 0.25f),
                radius = size.width * 0.7f
            )

            // 2. Nebula Cluster 2 (Cosmic Cyan)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.22f),
                        Color(0xFF3B82F6).copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.65f),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.65f),
                radius = size.width * 0.65f
            )

            // 3. Cosmic Core Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFC084FC).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.8f
            )
        }
    }
}

@Composable
private fun CustomAtmosphereCanvas(imageUri: String) {
    val context = LocalContext.current
    val effectiveModel = if (imageUri.isNotBlank()) {
        imageUri
    } else {
        "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop&q=80"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(effectiveModel)
                .crossfade(true)
                .build(),
            contentDescription = "Custom Background Atmosphere",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Adaptive semi-translucent scrim so that typography, cards, and buttons remain 100% readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.68f),
                            Color.Black.copy(alpha = 0.60f),
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )
    }
}

@Composable
fun AppAtmosphereSelectorDialog(
    currentTheme: String,
    currentCustomUri: String,
    onSelectTheme: (themeKey: String, customUri: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedKey by remember { mutableStateOf(currentTheme) }
    var customUri by remember { mutableStateOf(currentCustomUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customUri = uri.toString()
            selectedKey = "CUSTOM"
            onSelectTheme("CUSTOM", uri.toString())
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("app_atmosphere_selector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Palette,
                                    contentDescription = null,
                                    tint = LocaliiiyPrimaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "App Background & Atmosphere",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Choose cosmic themes or select your own self image",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Presets List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(AppAtmospherePresets.ALL) { preset ->
                        val isSelected = selectedKey.equals(preset.key, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedKey = preset.key
                                    onSelectTheme(preset.key, customUri)
                                }
                                .testTag("atmosphere_preset_${preset.key.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Visual Mini Preview Swatch
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(preset.previewColors))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = preset.icon, fontSize = 20.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = preset.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LocaliiiyPrimaryTeal,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Custom Self Image Picker Section
                AnimatedVisibility(visible = selectedKey == "CUSTOM") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Choose Your Self Image / Photo:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pick from Gallery", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Or choose curated backdrop presets:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(AppAtmospherePresets.CUSTOM_PRESET_IMAGES) { (label, url) ->
                                val isChosen = customUri == url
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        width = if (isChosen) 2.dp else 0.5.dp,
                                        color = if (isChosen) LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            customUri = url
                                            onSelectTheme("CUSTOM", url)
                                        }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apply Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("apply_atmosphere_button")
                ) {
                    Text("Done & Apply Background", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
