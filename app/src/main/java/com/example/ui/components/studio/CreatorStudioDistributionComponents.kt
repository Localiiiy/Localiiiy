package com.example.ui.components.studio

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ClipEntity
import com.example.data.PostEntity
import com.example.data.StudioDraftEntity
import com.example.data.StudioVideoEntity
import com.example.util.LocaliiiyCurrency
import com.example.util.CurrencyHelper
import com.example.ui.components.AudienceMatrixTooltip
import com.example.ui.components.EnergeticTooltipBox

// =========================================================================
// SECTION 5: CREATOR STUDIO & DISTRIBUTION CONTROL CENTER
// =========================================================================

/**
 * 5.1 Audience Distribution Launch Selector
 * Explicit slider on publish: Choose between Hyperlocal Focus, Metro Reach, or Dual-Velocity Earth.
 */
enum class AudienceReachMode(
    val title: String,
    val subtitle: String,
    val radiusKm: Int,
    val reachEstimate: String,
    val icon: ImageVector,
    val badgeColor: Color
) {
    NEIGHBOR_FIRST(
        title = "Neighbor First",
        subtitle = "Hyperlocal radius (0–5 km)",
        radiusKm = 5,
        reachEstimate = "~1,200 local connections & walk-in neighbors",
        icon = Icons.Default.NearMe,
        badgeColor = Color(0xFF00C853)
    ),
    CITY_PULSE(
        title = "City Pulse",
        subtitle = "Metropolitan reach (50 km)",
        radiusKm = 50,
        reachEstimate = "~45,000 metro area accounts & regional creators",
        icon = Icons.Default.LocationCity,
        badgeColor = Color(0xFF2979FF)
    ),
    EARTH_WAVE(
        title = "Earth Wave",
        subtitle = "Local seed + Global algorithmic velocity",
        radiusKm = 20000,
        reachEstimate = "Guaranteed 100 local seed + unlimited global discovery",
        icon = Icons.Default.Public,
        badgeColor = Color(0xFFFF3366)
    )
}

@Composable
fun AudienceReachSelector(
    selectedMode: AudienceReachMode,
    onModeSelected: (AudienceReachMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.testTag("audience_reach_selector")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            AudienceMatrixTooltip {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = selectedMode.badgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Audience Distribution Launch Selector",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = selectedMode.badgeColor.copy(alpha = 0.18f),
                        border = BorderStroke(0.6.dp, selectedMode.badgeColor)
                    ) {
                        Text(
                            text = "MATRIX 🌐 ⓘ",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = selectedMode.badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = "Give your dispatch explicit geographic intent. Guaranteed local seeding precedes world distribution (hover for details).",
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AudienceReachMode.values().forEach { mode ->
                    val isSelected = mode == selectedMode
                    val modeBullet = when (mode) {
                        AudienceReachMode.NEIGHBOR_FIRST -> "0–5 km geohash priority • 100 guaranteed walk-in impressions"
                        AudienceReachMode.CITY_PULSE -> "50 km metro circle • Regional hub broadcasting"
                        AudienceReachMode.EARTH_WAVE -> "195+ countries • Global velocity fueled by local resonance"
                    }

                    EnergeticTooltipBox(
                        title = mode.title,
                        description = mode.subtitle,
                        bullets = listOf(modeBullet, mode.reachEstimate),
                        icon = mode.icon,
                        accentColor = mode.badgeColor,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) mode.badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) mode.badgeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onModeSelected(mode) }
                                .testTag("reach_mode_${mode.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = mode.title,
                                    tint = if (isSelected) mode.badgeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) mode.badgeColor else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = mode.subtitle,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reach estimate pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = selectedMode.badgeColor.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = selectedMode.badgeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = selectedMode.reachEstimate,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * 5.2 Dual-Velocity Analytics Breakdown
 * Live charts showing what percentage of views originated from local neighbors vs. global exploration.
 */
@Composable
fun DualVelocityAnalyticsBreakdown(
    localSeedPercentage: Int = 68,
    globalWavePercentage: Int = 32,
    localImpressions: Int = 8420,
    globalImpressions: Int = 3960,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.testTag("dual_velocity_analytics_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = Color(0xFFFF3366),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Dual-Velocity Discovery Velocity",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF00C853).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Real-time Live",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-segment Dual Velocity Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(localSeedPercentage.toFloat())
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00C853), Color(0xFF00E676))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(globalWavePercentage.toFloat())
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF3366), Color(0xFFFF5252))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend & Counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00C853)))
                        Text(
                            text = "Local Geographic Seed ($localSeedPercentage%)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "$localImpressions views within 15 km",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF3366)))
                        Text(
                            text = "Global Earth Wave ($globalWavePercentage%)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "$globalImpressions algorithmic views",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 5.3 Guaranteed 100 Impression Tracking
 * Real-time progress ring tracking the initial 100 organic local neighbor views.
 */
@Composable
fun Guaranteed100ImpressionProgress(
    deliveredImpressions: Int = 84,
    targetImpressions: Int = 100,
    modifier: Modifier = Modifier
) {
    val progress = (deliveredImpressions.toFloat() / targetImpressions.toFloat()).coerceIn(0f, 1f)
    val isComplete = deliveredImpressions >= targetImpressions

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, if (isComplete) Color(0xFF00C853).copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth().testTag("guaranteed_100_impression_card")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = if (isComplete) Color(0xFF00C853) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Local Seed Health Guarantee",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$deliveredImpressions / $targetImpressions impressions",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) Color(0xFF00C853) else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isComplete) Color(0xFF00C853) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isComplete) "✅ 100/100 Local seed delivered. Post unlocked for global algorithmic expansion."
                else "Delivering guaranteed organic impressions to verified nearby connections in your area.",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 5.4 Content Longevity Engine (Anti-Decay)
 * Allow high-quality evergreen content to resurface locally when relevant (e.g. seasonal guides).
 */
@Composable
fun ContentLongevityToggle(
    isEvergreen: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, if (isEvergreen) Color(0xFF2E7D32).copy(alpha = 0.5f) else Color.Transparent),
        modifier = modifier.fillMaxWidth().testTag("content_longevity_toggle")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Park,
                    contentDescription = null,
                    tint = if (isEvergreen) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "Evergreen Archive (Anti-Decay)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Allows perennial guides & cultural maps to re-surface locally each season",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isEvergreen,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("evergreen_switch")
            )
        }
    }
}

/**
 * 5.5 Proximity-Gated Comments Toggle
 * Creators can restrict comments to people within 50km to avoid global internet trolls.
 */
@Composable
fun ProximityGatedCommentsToggle(
    isLocalOnly: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, if (isLocalOnly) Color(0xFF1976D2).copy(alpha = 0.5f) else Color.Transparent),
        modifier = modifier.fillMaxWidth().testTag("proximity_gated_comments_toggle")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = if (isLocalOnly) Color(0xFF1976D2) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "Local Remarkers Only (≤50 km)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Restricts remark replies strictly to verified neighbors within 50km",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isLocalOnly,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("proximity_comments_switch")
            )
        }
    }
}

/**
 * 5.6 Multi-Track Audio & Ambient Mixer
 * Layer localized ambient sounds beneath spoken voiceovers.
 */
@Composable
fun MultiTrackAudioMixer(
    micVolume: Float,
    onMicVolumeChange: (Float) -> Unit,
    ambientVolume: Float,
    onAmbientVolumeChange: (Float) -> Unit,
    selectedAmbientTrack: String,
    onSelectAmbientTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val ambientOptions = listOf("None", "🌧️ City Rain", "☕ Coffee Shop", "🚇 Subway Pulse", "🌳 Park Birds", "🎶 Lo-Fi Beats")

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().testTag("multi_track_audio_mixer")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = "2-Channel Audio & Ambient Mixer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Channel 1: Mic Voiceover
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Voiceover: ${(micVolume * 100).toInt()}%", fontSize = 10.5.sp, modifier = Modifier.width(90.dp))
                Slider(
                    value = micVolume,
                    onValueChange = onMicVolumeChange,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).height(24.dp)
                )
            }

            // Channel 2: Ambient Background Track
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF9800))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ambient: ${(ambientVolume * 100).toInt()}%", fontSize = 10.5.sp, modifier = Modifier.width(90.dp))
                Slider(
                    value = ambientVolume,
                    onValueChange = onAmbientVolumeChange,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).height(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Track Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ambientOptions) { track ->
                    val isSelected = track == selectedAmbientTrack
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { onSelectAmbientTrack(track) }
                    ) {
                        Text(
                            text = track,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5.7 Geotag Accuracy Jitter Control
 * Let creators choose whether their post shows exact neighborhood or broad metro area.
 */
enum class GeotagPrecision(val label: String, val description: String, val icon: ImageVector) {
    NEIGHBORHOOD("Neighborhood", "Exact landmark / district (e.g. Soho)", Icons.Default.Place),
    CITY_WIDE("City Wide", "Broader municipal metro (e.g. NYC)", Icons.Default.LocationCity),
    REGION_ONLY("Region Only", "County / Tri-State level", Icons.Default.Public)
}

@Composable
fun GeotagPrecisionSelector(
    selectedPrecision: GeotagPrecision,
    onSelect: (GeotagPrecision) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("geotag_precision_selector")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "📍 Geotag Accuracy & Jitter Control",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GeotagPrecision.values().forEach { precision ->
                    val isSel = precision == selectedPrecision
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(precision) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = precision.icon,
                                contentDescription = null,
                                tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = precision.label,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5.8 Collaborative Connection Co-Authoring
 * Tag up to 3 mutual connections as co-creators.
 */
@Composable
fun CollaborativeCoAuthorSelector(
    coCreators: List<String>,
    onAddCoCreator: (String) -> Unit,
    onRemoveCoCreator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }
    val suggestedConnections = listOf("elena_visuals", "marcus_sounds", "devon_cinematics", "claire_urban")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("collaborative_coauthor_selector")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Collaborative Connection Co-Authoring",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Co-author with up to 3 mutual connections. Publishes simultaneously to both feeds upon acceptance.",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (coCreators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    coCreators.forEach { handle ->
                        InputChip(
                            selected = true,
                            onClick = { onRemoveCoCreator(handle) },
                            label = { Text("@$handle", fontSize = 10.sp) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        )
                    }
                }
            }

            if (coCreators.size < 3) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(suggestedConnections.filter { it !in coCreators }) { handle ->
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.clip(RoundedCornerShape(100.dp)).clickable { onAddCoCreator(handle) }
                        ) {
                            Text(
                                text = "+ @$handle",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5.9 Publish Scheduling by Local Timezone
 * Schedule posts to launch when local neighbors are most active.
 */
@Composable
fun LocalTimezonePublishScheduler(
    isScheduled: Boolean,
    onToggleScheduled: (Boolean) -> Unit,
    selectedWindow: String,
    onSelectWindow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val windows = listOf(
        "8:00 AM (Morning Commute)",
        "12:30 PM (Lunch Pulse)",
        "6:30 PM (Evening Unwind)",
        "10:00 PM (Night Radar)"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("local_timezone_scheduler")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Column {
                        Text(
                            text = "Peak Neighborhood Active Hour",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Auto-dispatches when your local geo-cell is most active",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(checked = isScheduled, onCheckedChange = onToggleScheduled)
            }

            if (isScheduled) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(windows) { win ->
                        val isSel = win == selectedWindow
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.clip(RoundedCornerShape(100.dp)).clickable { onSelectWindow(win) }
                        ) {
                            Text(
                                text = win,
                                fontSize = 9.5.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5.10 Zero-Algorithm Draft Vault
 * Secure local-only draft storage with full offline thumbnail rendering.
 */
@Composable
fun ZeroAlgorithmDraftVaultCard(
    drafts: List<StudioDraftEntity>,
    onRestoreDraft: (StudioDraftEntity) -> Unit,
    onDeleteDraft: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (drafts.isEmpty()) return

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth().testTag("zero_algorithm_draft_vault")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.FolderZip, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Text(
                        text = "Zero-Algorithm Draft Vault (${drafts.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("🔒 Encrypted Local-Only", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(drafts) { draft ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.width(160.dp).clip(RoundedCornerShape(10.dp)).clickable { onRestoreDraft(draft) }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = draft.thumbnailUri.ifEmpty { "https://images.unsplash.com/photo-1516280440614-37939bbacd81?w=400&q=80" },
                                    contentDescription = draft.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = "DRAFT",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = draft.title.ifEmpty { "Untitled Draft" },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = draft.category, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(
                                    onClick = { onDeleteDraft(draft.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(13.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5.11 Instant Clip-to-Pulse Transclusion
 * Convert a published video clip into a still photo pulse with one tap.
 */
@Composable
fun ClipToPulseTransclusionButton(
    clipTitle: String,
    onTransclude: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onTransclude,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = modifier.testTag("clip_to_pulse_transclusion_btn")
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Extract Photo Pulse from Clip", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * 5.12 Direct Connection Broadcast Dispatches
 * Send a priority notification directly to mutual connections when dropping major releases.
 */
@Composable
fun DirectConnectionBroadcastCheckbox(
    notifyConnections: Boolean,
    onToggle: (Boolean) -> Unit,
    daysUntilNextAvailable: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("direct_connection_broadcast")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = if (notifyConnections) Color(0xFFFF3366) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "Priority Broadcast to Connections",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (daysUntilNextAvailable > 0) "Available in $daysUntilNextAvailable days (1 dispatch every 7 days rule)"
                        else "Sends high-priority in-app alert to all mutual Connections",
                        fontSize = 9.5.sp,
                        color = if (daysUntilNextAvailable > 0) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Checkbox(
                checked = notifyConnections,
                onCheckedChange = onToggle,
                enabled = daysUntilNextAvailable == 0
            )
        }
    }
}

/**
 * 5.13 Monetization & Micro-Tipping Ledger
 * Clean, transparent breakdown of peer tips received with direct bank payout setup.
 */
@Composable
fun MonetizationDashboard(
    totalTipsUSD: Double = 482.50,
    averageTipUSD: Double = 6.25,
    topSupporters: List<Pair<String, Double>> = listOf("alex_media" to 85.0, "sarah_k" to 60.0, "david_drone" to 45.0),
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    onWithdrawClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().testTag("monetization_dashboard")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    Text(
                        text = "Micro-Tipping Ledger & Payouts",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "0% Platform Fee",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Total Tips Earned", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = CurrencyHelper.format(totalTipsUSD, currentCurrency),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Average Tip Size", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = CurrencyHelper.format(averageTipUSD, currentCurrency),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top Community Supporters
            Text("Top Community Supporters:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topSupporters.forEach { (name, amount) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("@$name", fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(CurrencyHelper.format(amount, currentCurrency), fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val minWithdrawalFormatted = CurrencyHelper.format(1000.0, currentCurrency)
            Text(
                text = "Minimum withdrawal: $minWithdrawalFormatted ($1,000 USD equivalent for ${currentCurrency.country})",
                fontSize = 9.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Button(
                onClick = onWithdrawClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.fillMaxWidth().height(36.dp).testTag("withdraw_funds_btn")
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Withdraw Funds ($minWithdrawalFormatted Min)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * 5.14 Video Resolution & Aspect Ratio Master
 * Dynamic aspect ratio grid guides (9:16, 1:1, 16:9) with center cropping.
 */
enum class AspectRatioMode(val label: String, val ratioFloat: Float, val icon: ImageVector) {
    REEL_9_16("9:16 Reel", 9f / 16f, Icons.Default.StayCurrentPortrait),
    SQUARE_1_1("1:1 Square", 1f, Icons.Default.CropSquare),
    WIDESCREEN_16_9("16:9 Wide", 16f / 9f, Icons.Default.Tv)
}

@Composable
fun VideoResolutionAspectRatioSelector(
    selectedRatio: AspectRatioMode,
    onSelectRatio: (AspectRatioMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("aspect_ratio_selector")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "📐 Framing & Aspect Ratio Master",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AspectRatioMode.values().forEach { mode ->
                    val isSel = mode == selectedRatio
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable { onSelectRatio(mode) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(mode.icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode.label,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5.15 Hashtag & Geographic Keyword Recommender
 * Suggest trending local neighborhood tags alongside viral global tags.
 */
@Composable
fun HashtagGeographicKeywordRecommender(
    currentTags: String,
    onAddTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val localTags = listOf("#EastVillage", "#SohoNYC", "#BrooklynEats", "#HudsonRiver", "#WashingtonSq")
    val globalTags = listOf("#IndieFilm", "#Cinematography", "#SoundDesign", "#4KStudio", "#CreatorEconomy")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("hashtag_keyword_recommender")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "🏷️ Geographic & Global Keyword Suggestions",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Trending Nearby:", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00C853))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)) {
                items(localTags) { tag ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF00C853).copy(alpha = 0.12f),
                        modifier = Modifier.clip(RoundedCornerShape(100.dp)).clickable { onAddTag(tag) }
                    ) {
                        Text(tag, fontSize = 9.sp, color = Color(0xFF00C853), fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Text("Global Waves:", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF3366))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 2.dp)) {
                items(globalTags) { tag ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFFF3366).copy(alpha = 0.12f),
                        modifier = Modifier.clip(RoundedCornerShape(100.dp)).clickable { onAddTag(tag) }
                    ) {
                        Text(tag, fontSize = 9.sp, color = Color(0xFFFF3366), fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

/**
 * 5.16 Ghost Mode Exemption Toggle
 * Publish publicly as @handle while keeping live device location cloaked in Ghost Mode.
 */
@Composable
fun GhostModeExemptionToggle(
    isGhostExempt: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, if (isGhostExempt) Color(0xFF9C27B0).copy(alpha = 0.5f) else Color.Transparent),
        modifier = modifier.fillMaxWidth().testTag("ghost_mode_exemption_toggle")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = if (isGhostExempt) Color(0xFF9C27B0) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "Ghost Mode Cloaked Publishing",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Publish publicly under your @handle without revealing current real-time GPS coordinates",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isGhostExempt,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("ghost_exempt_switch")
            )
        }
    }
}

/**
 * 5.17 Video Thumbnail Custom Frame Scrubber
 * Scrub through video frames to select the exact visual cover thumbnail.
 */
@Composable
fun VideoThumbnailFrameScrubber(
    selectedFrameIndex: Int,
    onSelectFrame: (Int) -> Unit,
    thumbnails: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("thumbnail_frame_scrubber")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                Text(
                    text = "Keyframe Cover Scrubber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(thumbnails.indices.toList()) { idx ->
                    val isSelected = idx == selectedFrameIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier
                            .width(56.dp)
                            .height(42.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectFrame(idx) }
                    ) {
                        AsyncImage(
                            model = thumbnails[idx],
                            contentDescription = "Keyframe $idx",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5.18 Creative Commons & Copyright Licensing
 * Clearly tag creator content with remix and re-distribution permissions.
 */
enum class ContentLicense(val title: String, val description: String) {
    ALL_RIGHTS_RESERVED("All Rights Reserved", "Standard copyright; no unauthorized re-distribution"),
    CC_BY("CC-BY (Attribution)", "Free to adapt & remix with credit to @handle"),
    CC_BY_NC("CC-BY-NC (Non-Commercial)", "Free to remix for non-commercial purposes only"),
    PUBLIC_DOMAIN("Public Domain (CC0)", "Dedicated to the public domain without restriction")
}

@Composable
fun ContentLicenseSelector(
    selectedLicense: ContentLicense,
    onSelect: (ContentLicense) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier.fillMaxWidth().testTag("content_license_selector")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "📜 Copyright & Remix Licensing",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ContentLicense.values()) { license ->
                    val isSel = license == selectedLicense
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onSelect(license) }
                    ) {
                        Text(
                            text = license.title,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5.19 Batch Media Uploader & Queue
 * Queue multiple photo and video uploads with background resilience.
 */
data class QueuedUploadItem(
    val id: String,
    val title: String,
    val mediaType: String,
    val progress: Float,
    val status: String
)

@Composable
fun BatchMediaUploadQueueView(
    queue: List<QueuedUploadItem>,
    onRetry: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (queue.isEmpty()) return

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().testTag("batch_upload_queue_view")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Batch Upload Queue (${queue.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("WorkManager Resilient", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            queue.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        LinearProgressIndicator(
                            progress = { item.progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                        )
                    }
                    Text(item.status, fontSize = 9.5.sp, color = if (item.status == "Complete") Color(0xFF00C853) else MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * 5.20 Content Performance Benchmarking
 * Compare current post reach against creator's personal average without toxic public follower comparisons.
 */
@Composable
fun ContentPerformanceBenchmarkCard(
    postTitle: String,
    percentAboveAverage: Int = 34,
    daysCompared: Int = 30,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF00C853).copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color(0xFF00C853).copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth().testTag("performance_benchmark_card")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF00C853),
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = "Private Growth Benchmark",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00C853)
                )
                Text(
                    text = "This dispatch reached $percentAboveAverage% more local neighbors than your personal $daysCompared-day average. (Zero public vanity metric).",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
