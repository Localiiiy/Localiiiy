package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal

data class AtmosphericThemeOption(
    val key: String,
    val name: String,
    val icon: String,
    val tag: String,
    val description: String,
    val previewGradients: List<Color>,
    val badgeColor: Color
)

object AtmosphericThemeOptions {
    val ALL = listOf(
        AtmosphericThemeOption(
            key = "BLACK_HOLE",
            name = "Black Hole",
            icon = "🕳️",
            tag = "SINGULARITY",
            description = "Ultra-deep void canvas with gravitational accretion plasma flare and lensing aura.",
            previewGradients = listOf(Color(0xFF020205), Color(0xFF1E0B38), Color(0xFFFF6A00)),
            badgeColor = Color(0xFFFF6A00)
        ),
        AtmosphericThemeOption(
            key = "MOON",
            name = "Moon",
            icon = "🌕",
            tag = "LUNAR SURFACE",
            description = "Pristine lunar craters, luminous silver contrast, and serene moonbeam cyan accents.",
            previewGradients = listOf(Color(0xFF0F172A), Color(0xFF334155), Color(0xFF38BDF8)),
            badgeColor = Color(0xFF38BDF8)
        ),
        AtmosphericThemeOption(
            key = "GALAXY",
            name = "Galaxy",
            icon = "🌌",
            tag = "DEEP COSMOS",
            description = "Interstellar deep cosmic purple, stellar cyan orbits, and pulsar magenta stardust.",
            previewGradients = listOf(Color(0xFF070414), Color(0xFF4338CA), Color(0xFFD946EF)),
            badgeColor = Color(0xFFD946EF)
        ),
        AtmosphericThemeOption(
            key = "CUSTOM",
            name = "Custom",
            icon = "🖼️",
            tag = "SELF IMAGE",
            description = "Personal photo, selfie, camera picture, or curated high-res wallpaper backdrop.",
            previewGradients = listOf(Color(0xFF00838F), Color(0xFF00B4D8), Color(0xFF0F172A)),
            badgeColor = LocaliiiyPrimaryTeal
        )
    )

    val CURATED_WALLPAPERS = listOf(
        Pair("Cosmic Nebula", "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&auto=format&fit=crop&q=80"),
        Pair("Night Skyline", "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop&q=80"),
        Pair("Golden Sunset", "https://images.unsplash.com/photo-1495616811223-4d98c6e9c869?w=800&auto=format&fit=crop&q=80"),
        Pair("Minimal Dark", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800&auto=format&fit=crop&q=80")
    )
}

/**
 * Persistent Bottom Sheet allowing users to switch between atmospheric themes
 * (Black hole, Moon, Galaxy, Custom), dynamically updating the app's color palette
 * and background assets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtmosphericThemeBottomSheet(
    currentThemeKey: String,
    currentCustomImageUri: String,
    onDismiss: () -> Unit,
    onSelectTheme: (String, String) -> Unit,
    onOpenWorldwideLocalization: (() -> Unit)? = null
) {
    var selectedKey by remember(currentThemeKey) { mutableStateOf(currentThemeKey.ifBlank { "BLACK_HOLE" }) }
    var customUri by remember(currentCustomImageUri) { mutableStateOf(currentCustomImageUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            customUri = uriStr
            selectedKey = "CUSTOM"
            onSelectTheme("CUSTOM", uriStr)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("atmospheric_theme_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF9333EA), Color(0xFFFF6A00), Color(0xFF00F0FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Atmospheric Themes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "DYNAMIC M3",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Switches color palette and cosmic background",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Themes List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(AtmosphericThemeOptions.ALL) { opt ->
                    val isSelected = selectedKey.equals(opt.key, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) opt.badgeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedKey = opt.key
                                onSelectTheme(opt.key, customUri)
                            }
                            .testTag("theme_card_${opt.key.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Swatch Preview
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(opt.previewGradients))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = opt.icon, fontSize = 22.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = opt.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = opt.badgeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = opt.tag,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = opt.badgeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = opt.description,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = opt.badgeColor,
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

            // Custom self-image section if CUSTOM is selected
            AnimatedVisibility(visible = selectedKey == "CUSTOM") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Custom Background Asset:",
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
                            Text("Pick from Photos", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Or choose curated space/nature wallpaper:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AtmosphericThemeOptions.CURATED_WALLPAPERS) { (label, url) ->
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

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("apply_atmospheric_theme_button")
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Done & Apply Theme", fontWeight = FontWeight.Bold)
            }
        }
    }
}
