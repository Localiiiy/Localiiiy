package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Data Models for Zero-Knowledge Registration & Onboarding
data class CoarseAnchorOption(
    val name: String,
    val geohash: String,
    val boundingBoxArea: String,
    val city: String
)

data class LocalCircleSphere(
    val id: String,
    val name: String,
    val membersCount: Int,
    val category: String,
    val icon: ImageVector,
    var isConnected: Boolean = false
)

data class WelcomeCreator(
    val username: String,
    val displayName: String,
    val category: String,
    val distanceLabel: String,
    val avatarBg: Color,
    var isConnected: Boolean = false
)

enum class OnboardingThemePalette(val title: String, val primaryColor: Color, val accentColor: Color, val bgTint: Color) {
    NEON_CYBER("Neon Cyber", Color(0xFF00838F), Color(0xFF00E676), Color(0xFF0B132B)),
    SUNSET_GOLD("Sunset Gold", Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFF1C1917)),
    DEEP_AMOLED("Deep AMOLED", Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFF000000))
}

val defaultAnchorOptions = listOf(
    CoarseAnchorOption("Capitol Hill District", "c23nb", "~4.9 km² Box", "Seattle, WA"),
    CoarseAnchorOption("SoHo Arts Hub", "dr5re", "~4.9 km² Box", "New York, NY"),
    CoarseAnchorOption("Mission Cultural Corridor", "9q8yu", "~4.9 km² Box", "San Francisco, CA"),
    CoarseAnchorOption("Downtown Waterfront", "9q8yy", "~4.9 km² Box", "Austin, TX"),
    CoarseAnchorOption("Custom Geographic Anchor", "coarse", "Fuzzy Bounding Box", "Select City")
)

val defaultLocalCircles = listOf(
    LocalCircleSphere("circle_1", "Indie Filmmakers & Editors", 412, "Visual Media", Icons.Default.Videocam),
    LocalCircleSphere("circle_2", "Hyperlocal Coffee & Chefs", 689, "Culinary", Icons.Default.Restaurant),
    LocalCircleSphere("circle_3", "Civic Tech & Maker Labs", 320, "Technology", Icons.Default.Code),
    LocalCircleSphere("circle_4", "Street Photographers Collective", 541, "Photography", Icons.Default.CameraAlt)
)

val defaultCoreInterests = listOf(
    "Local Food & Coffee", "Indie Film & Clips", "Live Music & Gigs",
    "Tech & Open Source", "Urban Photography", "Maker Crafts",
    "Community News", "Cycling & Trails", "Visual Art & Murals", "Night Markets"
)

val defaultLocalWelcomeCreators = listOf(
    WelcomeCreator("maya_lens", "Maya Chen", "Filmmaker 🎬", "📍 0.8 km", Color(0xFF00838F)),
    WelcomeCreator("sam_bakes", "Samir Patel", "Artisan Chef 🍳", "📍 1.4 km", Color(0xFFD97706)),
    WelcomeCreator("elena_beats", "Elena Silva", "Live Audio 🎵", "📍 2.1 km", Color(0xFF7C3AED))
)

// Feature 1: Ephemeral Spectator Entrance Banner
@Composable
fun EphemeralSpectatorBanner(
    onEnterAsGhostSpectator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ephemeral_spectator_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(LocaliiiyAccentMint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = LocaliiiyAccentMint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ephemeral Spectator Entrance",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = LocaliiiyAccentMint.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Zero Credentials",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocaliiiyAccentMint,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Explore neighborhood clips, radar density, and market without creating a permanent account or sharing email.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onEnterAsGhostSpectator,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocaliiiyDeepNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_enter_ghost_spectator")
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = LocaliiiyAccentMint
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Explore Instantly as Ghost Spectator",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// Feature 2: Zero-Knowledge Coarse Location Anchor Selection
@Composable
fun ZeroKnowledgeAnchorPicker(
    selectedAnchor: CoarseAnchorOption,
    onAnchorSelected: (CoarseAnchorOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Zero-Knowledge Location Anchor",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Precision 5 Geohash",
                    fontSize = 10.sp,
                    color = LocaliiiyPrimaryTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your exact GPS coordinates are NEVER saved. Your content is seeded within a coarse ~4.9 km bounding box.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Selected Anchor Pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .testTag("anchor_picker_dropdown")
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FmdGood,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "${selectedAnchor.name} (${selectedAnchor.city})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Geohash: ${selectedAnchor.geohash} • Bounding: ${selectedAnchor.boundingBoxArea}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    defaultAnchorOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onAnchorSelected(option)
                                    expanded = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = option.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (option == selectedAnchor) FontWeight.Bold else FontWeight.Normal,
                                    color = if (option == selectedAnchor) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${option.city} • Geohash: ${option.geohash}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (option == selectedAnchor) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = LocaliiiyPrimaryTeal,
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

// Feature 3: Dual-Persona Setup (Creator vs. Ghost)
@Composable
fun DualPersonaCardSetup(
    creatorHandle: String,
    onCreatorHandleChange: (String) -> Unit,
    ghostAlias: String,
    onGhostAliasChange: (String) -> Unit,
    onRegenerateGhostAlias: () -> Unit,
    activePersona: String,
    onPersonaToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Dual-Persona Setup",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Configure both your public creator identity and your unlinked ghost alias for stealth browsing.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: Creator Persona
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (activePersona == "CREATOR") LocaliiiyPrimaryTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.5.dp,
                    if (activePersona == "CREATOR") LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPersonaToggle("CREATOR") }
                    .testTag("persona_card_creator")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Public Creator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LocaliiiyPrimaryTeal
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (creatorHandle.isNotBlank()) "@$creatorHandle" else "@your_handle",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Publishes pulses, clips & joins local circles",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Card 2: Ghost Persona
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (activePersona == "GHOST") LocaliiiyDeepNavy.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.5.dp,
                    if (activePersona == "GHOST") LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPersonaToggle("GHOST") }
                    .testTag("persona_card_ghost")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ghost Alias",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LocaliiiyAccentMint
                        )
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ghostAlias,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Cloaked on radar, unlinked stealth browsing",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Feature 4: Mutual Connection Rings Genesis ("Join Local Circles")
@Composable
fun MutualConnectionRingsCard(
    circles: List<LocalCircleSphere>,
    onToggleConnection: (LocalCircleSphere) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Join Local Circles",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Connections Graph",
                fontSize = 10.sp,
                color = LocaliiiyPrimaryTeal,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Connect with coarse-geohash verified hubs. No address book harvesting.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        circles.forEach { circle ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
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
                                .size(32.dp)
                                .background(LocaliiiyPrimaryTeal.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = circle.icon,
                                contentDescription = null,
                                tint = LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = circle.name,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${circle.membersCount} Connected Neighbors • ${circle.category}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { onToggleConnection(circle) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_connect_circle_${circle.id}")
                    ) {
                        Text(
                            text = if (circle.isConnected) "Connected ✓" else "Connect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (circle.isConnected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// Feature 5: No-Tracking Privacy Guarantee Badge (Zero-Location-Storage Pledge)
@Composable
fun NoTrackingPrivacyGuaranteeBadge(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = LocaliiiyAccentMint.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("privacy_guarantee_badge")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        imageVector = Icons.Default.GppGood,
                        contentDescription = null,
                        tint = LocaliiiyAccentMint,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Zero-Location-Storage Pledge",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cryptographic Client-Side Isolation",
                            fontSize = 10.sp,
                            color = LocaliiiyAccentMint,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "1. Exact Lat/Lng coordinates are strictly resolved client-side in volatile memory and never persisted in database tables.\n" +
                                "2. Radar pings are obfuscated with a dynamic 150m-300m spatial jitter to prevent triangulation.\n" +
                                "3. In Ghost Mode, your device beacon is 100% disconnected from the proximity radar sweep.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// Feature 6: Localized Alias Generator (Biome motifs + digits)
@Composable
fun LocalizedAliasGenerator(
    currentAlias: String,
    onAliasGenerated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefixes = listOf("MetroSparrow", "BayFalcon", "TimberLynx", "HarborSeal", "HighlandHawk", "CascadeFox", "UrbanOwl", "CanyonWolf")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ghost Alias Generator",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentAlias,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocaliiiyAccentMint
                )
            }
            FilledTonalButton(
                onClick = {
                    val prefix = prefixes.random()
                    val suffix = (100..999).random()
                    onAliasGenerated("$prefix-$suffix")
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("btn_regenerate_alias")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Generate", fontSize = 11.sp)
            }
        }
    }
}

// Feature 7: Creator Distribution Preference Primer (3-slide visual carousel)
@Composable
fun CreatorDistributionPrimerCarousel(
    modifier: Modifier = Modifier
) {
    var activeSlide by remember { mutableStateOf(0) }

    val slides = listOf(
        Triple("1. Guaranteed Local Seed (0–5 km)", "Your pulses and clips are guaranteed 100 organic impressions to physical neighbors in your geohash, bypassing algorithmic gatekeepers.", Icons.Default.LocationOn),
        Triple("2. City Metro Pulse (5–50 km)", "Posts that resonate locally automatically expand across the entire metropolitan area, ensuring relevant citywide discovery.", Icons.Default.LocationCity),
        Triple("3. Earth Algorithmic Waves (Global)", "Authentic local resonance propels your content globally to worldwide audiences on equal footing with legacy influencers.", Icons.Default.Public)
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = LocaliiiyPrimaryTeal.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = slides[activeSlide].third,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Dual-Reach Distribution Primer",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "${activeSlide + 1} of 3",
                    fontSize = 11.sp,
                    color = LocaliiiyPrimaryTeal,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = slides[activeSlide].first,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = slides[activeSlide].second,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == activeSlide) 16.dp else 6.dp, 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (index == activeSlide) LocaliiiyPrimaryTeal else LocaliiiyOutlineVariant)
                        )
                    }
                }
                TextButton(
                    onClick = { activeSlide = (activeSlide + 1) % 3 },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Next Slide →", fontSize = 11.sp, color = LocaliiiyPrimaryTeal)
                }
            }
        }
    }
}

// Feature 8: Biometric Vault Protection Toggle
@Composable
fun BiometricVaultProtectionCard(
    isBiometricVaultEnabled: Boolean,
    onToggleBiometricVault: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
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
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = LocaliiiyPrimaryTeal,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Biometric Vault & Ghost Lock",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Require fingerprint/PIN before revealing DMs or turning off Ghost Mode",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isBiometricVaultEnabled,
                onCheckedChange = onToggleBiometricVault,
                modifier = Modifier.testTag("toggle_biometric_vault")
            )
        }
    }
}

// Feature 9: One-Tap Identity Scrub Button
@Composable
fun OneTapIdentityScrubButton(
    onScrubIdentity: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onScrubIdentity,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("btn_identity_scrub")
    ) {
        Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Erase & Reset Session (One-Tap Scrub)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Feature 10: Proximity-Based Mutual Connection Discovery ("Proximity Tap")
@Composable
fun ProximityConnectionDiscoveryView(
    isScanning: Boolean,
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var detectedNearbyCount by remember { mutableStateOf(2) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = LocaliiiyDeepNavy.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, LocaliiiyAccentCyan.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        imageVector = Icons.Default.Nfc,
                        contentDescription = null,
                        tint = LocaliiiyAccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Proximity Tap Handshake",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Acoustic / BLE peer discovery",
                            fontSize = 10.sp,
                            color = LocaliiiyAccentCyan
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onStartScan,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_proximity_tap_scan")
                ) {
                    Text(
                        text = if (isScanning) "Broadcasting..." else "Tap to Connect",
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hold two devices nearby to form an immediate mutual Connection without searching usernames.",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Feature 11: Interest Constellation Mapping (3-8 core interests)
@Composable
fun InterestConstellationMapping(
    selectedInterests: List<String>,
    onToggleInterest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Interest Constellation",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${selectedInterests.size} of 8 selected",
                fontSize = 11.sp,
                color = if (selectedInterests.size >= 3) LocaliiiyAccentMint else LocaliiiyTertiary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Calibrates both your coarse local pulse seed and global feed exploration.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Multi-line chip wrapping row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val chunked = defaultCoreInterests.chunked(3)
            chunked.forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { interest ->
                        val isSelected = selectedInterests.contains(interest)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleInterest(interest) },
                            label = { Text(interest, fontSize = 11.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LocaliiiyPrimaryTeal.copy(alpha = 0.2f),
                                selectedLabelColor = LocaliiiyPrimaryTeal
                            ),
                            modifier = Modifier.testTag("interest_chip_${interest.replace(" ", "_")}")
                        )
                    }
                }
            }
        }
    }
}

// Feature 12: Ghost Mode Default Pre-Selection with Radar Visual Preview
@Composable
fun GhostModePreSelectionToggle(
    isGhostModeDefault: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isGhostModeDefault) LocaliiiyDeepNavy.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isGhostModeDefault) LocaliiiyAccentMint.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isGhostModeDefault) LocaliiiyAccentMint.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGhostModeDefault) Icons.Default.VisibilityOff else Icons.Default.Radar,
                            contentDescription = null,
                            tint = if (isGhostModeDefault) LocaliiiyAccentMint else LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Start in Ghost Mode",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isGhostModeDefault) "Completely invisible on the Live Radar blip sweep" else "Visible as active beacon to nearby connections",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isGhostModeDefault,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("toggle_start_ghost_mode")
                )
            }
        }
    }
}

// Feature 13: Neighborhood Welcome Dispatch Modal Card
@Composable
fun NeighborhoodWelcomeDispatchCard(
    welcomeCreators: List<WelcomeCreator>,
    onToggleConnect: (WelcomeCreator) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, LocaliiiyTertiary.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WavingHand,
                    contentDescription = null,
                    tint = LocaliiiyTertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Local Welcome Dispatch",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Text(
                text = "Top active community creators in your anchor geohash ready to connect:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            welcomeCreators.forEach { creator ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(creator.avatarBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = creator.displayName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Column {
                            Text(
                                text = creator.displayName,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${creator.category} • ${creator.distanceLabel}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { onToggleConnect(creator) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (creator.isConnected) LocaliiiyPrimaryTeal.copy(alpha = 0.15f) else LocaliiiyPrimaryTeal,
                            contentColor = if (creator.isConnected) LocaliiiyPrimaryTeal else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_connect_welcome_${creator.username}")
                    ) {
                        Text(
                            text = if (creator.isConnected) "Connected ✓" else "Connect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Feature 14: Custom Connection Pitch Bio Field (Headline + Value)
@Composable
fun CustomConnectionPitchBioField(
    headline: String,
    onHeadlineChange: (String) -> Unit,
    connectionValue: String,
    onConnectionValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Custom Connection Pitch Bio",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Replace vanity metrics with authentic value you offer to local and global connections.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = headline,
            onValueChange = { if (it.length <= 60) onHeadlineChange(it) },
            label = { Text("Public Headline (max 60 chars)", fontSize = 12.sp) },
            placeholder = { Text("e.g. Visual storyteller & coffee roaster 📸☕", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_public_headline")
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = connectionValue,
            onValueChange = { if (it.length <= 150) onConnectionValueChange(it) },
            label = { Text("Why Connect With Me? (Value Offer)", fontSize = 12.sp) },
            placeholder = { Text("e.g. Sharing neighborhood hidden gems, lending camera gear & collaborating on films.", fontSize = 12.sp) },
            minLines = 2,
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_connection_value")
        )
    }
}

// Feature 15: Age-Appropriate Geographic Gating
@Composable
fun AgeAppropriateGatingCard(
    birthYear: Int,
    onBirthYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentYear = 2026
    val userAge = currentYear - birthYear
    val isMinor = userAge < 18

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isMinor) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isMinor) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Age & Proximity Safety Verification",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$userAge years old",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMinor) MaterialTheme.colorScheme.error else LocaliiiyPrimaryTeal
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isMinor)
                    "🔒 Under 18: Physical radar blip broadcasting and in-person marketplace meetup zones are automatically cloaked for child safety."
                else
                    "✓ 18+: Full access to physical radar discovery and safe in-person meetup hubs.",
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2000, 2004, 2007, 2010).forEach { year ->
                    FilterChip(
                        selected = birthYear == year,
                        onClick = { onBirthYearChange(year) },
                        label = { Text("Born $year", fontSize = 11.sp) },
                        modifier = Modifier.testTag("chip_birth_year_$year")
                    )
                }
            }
        }
    }
}

// Feature 16: Creator Category Certification
@Composable
fun CreatorCategoryCertification(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Filmmaker 🎬", "Musician 🎵", "Local Reporter 📰",
        "Chef / Food Artisan 🍳", "Visual Artist 🎨", "Craftsperson 🛠️", "Community Host 🎙️"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Creator Category Certification",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Badges displayed on your dual-reach pulse posts and clip overlays.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LocaliiiyPrimaryTeal,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("category_chip_${category.take(4)}")
                )
            }
        }
    }
}

// Feature 17: Dynamic Visual Theme Selection
@Composable
fun DynamicVisualThemeSelector(
    selectedTheme: OnboardingThemePalette,
    onThemeSelected: (OnboardingThemePalette) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Aesthetic Theme Selection",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OnboardingThemePalette.values().forEach { palette ->
                val isSelected = selectedTheme == palette
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.bgTint,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) palette.accentColor else Color.Gray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onThemeSelected(palette) }
                        .testTag("theme_card_${palette.name}")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(palette.primaryColor, CircleShape))
                            Box(modifier = Modifier.size(12.dp).background(palette.accentColor, CircleShape))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = palette.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Feature 18: Stealth Quick-Exit Gesture Setup ("Panic Cloak")
@Composable
fun StealthQuickExitGestureCard(
    isPanicCloakEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
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
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    tint = LocaliiiyAccentCoral,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Panic Cloak & Shake Gesture",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Shake device or two-finger tap to instantly lock and revert to Ghost Spectator",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isPanicCloakEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("toggle_panic_cloak")
            )
        }
    }
}

// Feature 19: Terms of Respect & Anti-Harassment Compact ("Community Covenant")
@Composable
fun TermsOfRespectCompactCard(
    hasAccepted: Boolean,
    onToggleAcceptance: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocaliiiyPrimaryTeal.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = hasAccepted,
                onCheckedChange = onToggleAcceptance,
                modifier = Modifier.testTag("checkbox_community_covenant")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Localiiiy Community Covenant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "I commit to respectful, authentic neighborhood interactions and zero tolerance for stalking, geo-harassment, or hate speech.",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Feature 20: Seamless Deep-Link Referral Handshake Field
@Composable
fun SeamlessDeepLinkReferralField(
    referralCode: String,
    onReferralCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = referralCode,
        onValueChange = onReferralCodeChange,
        label = { Text("Invited by a Connection? (Referral Link/Code)", fontSize = 12.sp) },
        placeholder = { Text("e.g. alex_creative or REF-882", fontSize = 12.sp) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = LocaliiiyPrimaryTeal
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag("input_referral_handshake")
    )
}
