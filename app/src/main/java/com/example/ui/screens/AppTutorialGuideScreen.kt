package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.BadgeTier
import com.example.ui.components.HashAnchorItem
import com.example.ui.components.HashLinkAnchorNavBar
import com.example.ui.components.EnergeticPulseBadge
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocalAppLanguage
import kotlinx.coroutines.launch

enum class TutorialTab(val title: String, val emoji: String) {
    RUNNING_GUIDE("App Guide", "📱"),
    ACTIVATION_BADGES("Activation Badges", "🎖️"),
    MONETIZATION("Earn & Payouts", "💰")
}

data class TutorialGuideStep(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val description: String,
    val bulletPoints: List<String>,
    val tag: String
)

data class CommunityBadgeInfo(
    val title: String,
    val emoji: String,
    val badgeCategory: String,
    val color: Color,
    val requirement: String,
    val perk: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTutorialGuideScreen(
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    currentLanguage: LocaliiiyLanguage = LocalAppLanguage.current,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(TutorialTab.RUNNING_GUIDE) }
    var calculatorCurrency by remember { mutableStateOf(currentCurrency) }
    val guideListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedGuideAnchor by remember { mutableStateOf("#step-0") }

    val guideAnchors = remember {
        listOf(
            HashAnchorItem(id = "#step-0", label = "1. Pulse", targetIndex = 1),
            HashAnchorItem(id = "#step-1", label = "2. Radar", targetIndex = 2),
            HashAnchorItem(id = "#step-2", label = "3. Dual-Reach", targetIndex = 3),
            HashAnchorItem(id = "#step-3", label = "4. Escrow", targetIndex = 4),
            HashAnchorItem(id = "#step-4", label = "5. Studio", targetIndex = 5),
            HashAnchorItem(id = "#step-5", label = "6. Privacy", targetIndex = 6),
            HashAnchorItem(id = "#step-6", label = "7. Wallet", targetIndex = 7),
            HashAnchorItem(id = "#step-7", label = "8. Shop Clip", targetIndex = 8),
            HashAnchorItem(id = "#step-8", label = "9. Global", targetIndex = 9),
            HashAnchorItem(id = "#step-9", label = "10. Web App", targetIndex = 10),
            HashAnchorItem(id = "#step-10", label = "11. Stealth", targetIndex = 11),
            HashAnchorItem(id = "#step-11", label = "12. Play Safety", targetIndex = 12)
        )
    }

    val guideSteps = remember {
        listOf(
            TutorialGuideStep(
                title = "1. Pulse Feed & Local Signals",
                subtitle = "Ephemeral neighborhood moments, 24h stories & waves",
                imageUrl = "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=1080&auto=format&fit=crop&q=80",
                description = "The Pulse feed is your neighborhood's real-time heartbeat. Discover authentic local stories and discussions anchored to your geographical proximity.",
                bulletPoints = listOf(
                    "Discover ephemeral neighbor updates and 24-hour video stories",
                    "Send subtle reaction waves to connect with locals nearby",
                    "Filter streams by 'Connected' to view only mutual relationships",
                    "Radar power toggle is kept cleanly on the Radar page for pure feed immersion"
                ),
                tag = "Pulse Feed"
            ),
            TutorialGuideStep(
                title = "2. Live Circular Proximity Radar",
                subtitle = "Dynamic sonar sweep, Kalman filter & 500km scaling",
                imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1080&auto=format&fit=crop&q=80",
                description = "Our signature circular radar visualizes nearby neighbors and active signals within your selected distance range (100m to 500km).",
                bulletPoints = listOf(
                    "Dedicated Radar Power Switch: turn Radar ON or OFF exclusively on the Radar page",
                    "Pinch-to-zoom radar scale dynamically from 100 meters to 500 kilometers",
                    "Kalman filtering and hardware gyroscope smoothing for stable heading telemetry",
                    "1-tap Ghost Cloak mode to browse completely off-grid without exposing your pin"
                ),
                tag = "Live Radar"
            ),
            TutorialGuideStep(
                title = "3. Clips & Dual-Reach Algorithm",
                subtitle = "Neighbor to World organic scaling with creator sovereignty",
                imageUrl = "https://images.unsplash.com/photo-1533750516457-a7f992034fec?w=1080&auto=format&fit=crop&q=80",
                description = "Publish high-energy 9:16 vertical short clips. The dual-reach algorithm distributes your clip to nearby neighbors first, then expands algorithmically to the entire world.",
                bulletPoints = listOf(
                    "Hyperlocal grounding: Content resonates in your immediate neighborhood first",
                    "Algorithmic progression: Neighbor -> Neighborhood -> City -> State -> Earth (Worldwide)",
                    "Creator control: You choose whether to constrain reach locally or broadcast globally",
                    "Top bar remains dedicated to filters ('All', 'Trending', 'Nearby', 'Connected')"
                ),
                tag = "Clips & Reach"
            ),
            TutorialGuideStep(
                title = "4. Hyperlocal Market & Services",
                subtitle = "Hourly rates displayed on the image side with direct booking",
                imageUrl = "https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=1080&auto=format&fit=crop&q=80",
                description = "Trade physical goods, handcrafted items, and professional services with nearby neighbors in complete safety.",
                bulletPoints = listOf(
                    "Transparent service rates ($/hr) are shown directly below the image on the left side",
                    "Direct 1-tap inquiries with local service providers and instructors",
                    "Peer-to-peer goods marketplace with cash-on-meetup and cashless escrow",
                    "Safe exchange zones and emergency clinic landmarks on the radar"
                ),
                tag = "Services & Market"
            ),
            TutorialGuideStep(
                title = "5. Long-Form Creator Studio",
                subtitle = "Unlimited 4K video playback, custom aspect ratios & sound",
                imageUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=1080&auto=format&fit=crop&q=80",
                description = "Create and broadcast long-form cinematic documentaries, neighborhood podcasts, and studio productions.",
                bulletPoints = listOf(
                    "Supports 9:16 Reels, 1:1 Square, and 16:9 Cinema widescreen formats",
                    "Integrated audio equalizer and 639Hz ambient frequency synth",
                    "Direct in-stream ad revenue sharing and real-time community micro-tipping",
                    "Instant video deep links for sharing across external messaging apps"
                ),
                tag = "Creator Studio"
            ),
            TutorialGuideStep(
                title = "6. Universal Safety & Privacy Shield",
                subtitle = "Anti-stalking zero-tolerance, ISO evidence logs & panic decoy",
                imageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=1080&auto=format&fit=crop&q=80",
                description = "Localiiiy enforces zero-tolerance cyberstalking protocols, tamper-proof legal incident logs, and rapid emergency tools.",
                bulletPoints = listOf(
                    "Universal Zero-Tolerance Anti-Stalking Policy with permanent hardware bans",
                    "Cryptographically sealed ISO/IEC 27037 incident evidence export for courts",
                    "Motion-based Panic Cloak decoy mode and rapid two-way blocking",
                    "Complete Data Oblivion: permanently wipe all on-device Room data with 1 tap"
                ),
                tag = "Safety & Legal"
            ),
            TutorialGuideStep(
                title = "7. Gamified Rewards & Creator Wallet",
                subtitle = "Turn neighborhood engagement into actual revenue",
                imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=1080&auto=format&fit=crop&q=80",
                description = "Interact with the platform daily, refer friends, and build local networks to earn direct points in your Creator Wallet. Once you reach the $1,000 threshold, you can request an instant payout.",
                bulletPoints = listOf(
                    "Earn streak points for consecutive Gamified Daily Check-ins",
                    "Receive high-value Wallet bonuses for successful neighbor referrals",
                    "Transparent ledger detailing all micro-tips, ad shares, and market gig payouts",
                    "Seamless cross-border payouts with strict $1,000 minimum threshold"
                ),
                tag = "Growth & Wallet"
            ),
            TutorialGuideStep(
                title = "8. Video Clip to Marketplace Shop Conversion",
                subtitle = "Turn engaging short video clips directly into shoppable listings",
                imageUrl = "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=1080&auto=format&fit=crop&q=80",
                description = "Never separate content from commerce. In Localiiiy, any video clip can be converted into an active marketplace listing in a single tap.",
                bulletPoints = listOf(
                    "1-tap conversion: Click 'Sell' on any clip to attach price, condition, and pickup spot",
                    "Shoppable video player: Viewers can tap the buy button directly from the video overlay",
                    "Supports both physical goods and bookable hourly neighborhood services",
                    "Zero platform commission on peer-to-peer neighborhood transactions"
                ),
                tag = "Clip to Shop"
            ),
            TutorialGuideStep(
                title = "9. Worldwide 75+ Languages & 50+ Currencies",
                subtitle = "Global connectivity with localized language and fiat/crypto currencies",
                imageUrl = "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=1080&auto=format&fit=crop&q=80",
                description = "Localiiiy is built for every neighborhood on Earth. Switch between 75+ global languages and 50+ local currencies instantly across the entire application.",
                bulletPoints = listOf(
                    "Full international coverage across Europe, Americas, Asia, Middle East, and Africa",
                    "Native Right-to-Left (RTL) typography for Arabic, Hebrew, Urdu, Persian, and Pashto",
                    "Automatic live conversion for 50+ fiat currencies (USD, EUR, GBP, INR, JPY, CAD, AUD, etc.)",
                    "Settings persist locally and adapt the entire interface seamlessly"
                ),
                tag = "Global Language"
            ),
            TutorialGuideStep(
                title = "10. Firebase Hosting & Web App Deep Linking",
                subtitle = "Official web access at https://localiiiy.web.app for instant cross-platform sharing",
                imageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=1080&auto=format&fit=crop&q=80",
                description = "Every post, clip, and profile has a verified Firebase Hosting deep link (https://localiiiy.web.app). Users without the app can instantly view content in their mobile browser.",
                bulletPoints = listOf(
                    "Official live Firebase web app hosted at https://localiiiy.web.app",
                    "Deep link auto-routing opens posts, clips, and creator spaces directly in app or web",
                    "Full PWA offline capabilities with zero-latency cached media playback",
                    "Verified fallback domain at https://localiiiy.firebaseapp.com"
                ),
                tag = "Web & Links"
            ),
            TutorialGuideStep(
                title = "11. Radar Range Obfuscation & Stealth Ghost Cloak",
                subtitle = "Spatial coordinate blur and complete invisible observation mode",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=80",
                description = "Control your geographical footprint on the proximity radar. Blur your street location with 3km, 5km, or 10km obfuscation rings, or activate Ghost Cloak mode.",
                bulletPoints = listOf(
                    "Ghost Cloak (Stealth Observer): Browse the radar without broadcasting your location",
                    "Dynamic Spatial Obfuscation: Masks exact coordinates into a blurred neighborhood sector",
                    "Automatic minor protection: Users under 18 receive mandatory coordinate masking",
                    "Dedicated Radar Power switch: turn radar transmission OFF anytime with 1 tap"
                ),
                tag = "Radar Cloak"
            ),
            TutorialGuideStep(
                title = "12. Google Play Data Safety & 1-Tap Account Oblivion",
                subtitle = "Complete privacy compliance and instant irreversible data erasure",
                imageUrl = "https://images.unsplash.com/photo-1555421689-491a97ff2040?w=1080&auto=format&fit=crop&q=80",
                description = "Localiiiy is built to meet Google Play Store Developer Program standards. We declare our data practices transparently and provide complete user autonomy over personal data.",
                bulletPoints = listOf(
                    "Google Play Data Safety compliance: No contacts scraping, no background wiretaps, no audio recording",
                    "Zero-permission Photo Picker: Protects device storage by selecting media via system dialogs",
                    "1-Tap Account Oblivion: Permanently delete account, Firebase credentials, and local Room cache",
                    "Official live Privacy Policy & Terms hosted at https://localiiiy.web.app/privacy"
                ),
                tag = "Store Safety"
            )
        )
    }

    val communityBadges = remember {
        listOf(
            CommunityBadgeInfo(
                title = "Verified Resident / Local Pioneer",
                emoji = "🏘️",
                badgeCategory = "Residency",
                color = Color(0xFF00BFA5),
                requirement = "Verify address or maintain 60+ days active positive local signal participation",
                perk = "Priority neighborhood radar blip, vote on local community initiatives"
            ),
            CommunityBadgeInfo(
                title = "Certified Merchant / Provider",
                emoji = "🏪",
                badgeCategory = "Commerce",
                color = Color(0xFF2196F3),
                requirement = "Verified business identity or registered trade license with 10+ completed gigs",
                perk = "Dedicated service card with hourly rates displayed below provider image, verified escrow"
            ),
            CommunityBadgeInfo(
                title = "Safe Meetup Champion",
                emoji = "🛡️",
                badgeCategory = "Safety",
                color = Color(0xFF4CAF50),
                requirement = "Complete 25+ marketplace transactions or meetups with 5.0 safety rating",
                perk = "Golden safety shield icon on profile and instant trust badge for new neighbors"
            ),
            CommunityBadgeInfo(
                title = "Neighborhood Spark Starter",
                emoji = "🔥",
                badgeCategory = "Engagement",
                color = Color(0xFFFF9800),
                requirement = "Author 5+ local posts or clips that reach 'City' or 'Earth' distribution milestones",
                perk = "Featured creator carousel in Explore, algorithmic boost on new posts"
            ),
            CommunityBadgeInfo(
                title = "Daily Check-in Streak Master",
                emoji = "📅",
                badgeCategory = "Activity",
                color = Color(0xFF9C27B0),
                requirement = "Maintain a 7+ day consecutive daily check-in streak on the gamified board",
                perk = "Temporary 'Radar Visibility Glow' for 24h, drawing more organic views to your profile"
            ),
            CommunityBadgeInfo(
                title = "Community Growth Ambassador",
                emoji = "🤝",
                badgeCategory = "Referral",
                color = Color(0xFFE91E63),
                requirement = "Successfully invite 5+ verified local neighbors using your referral link",
                perk = "Permanent visibility boost in the local feed and bonus points in the Creator Wallet"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "App Guide & Tutorials",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Running guide • Badges • Monetization ($1,000 min)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Segmented Tab Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TutorialTab.entries.toTypedArray()) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { selectedTab = tab }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = tab.emoji, fontSize = 14.sp)
                            Text(
                                text = tab.title,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                TutorialTab.RUNNING_GUIDE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        HashLinkAnchorNavBar(
                            anchors = guideAnchors,
                            selectedAnchorId = selectedGuideAnchor,
                            onAnchorClick = { anchor ->
                                selectedGuideAnchor = anchor.id
                                coroutineScope.launch {
                                    guideListState.animateScrollToItem(index = anchor.targetIndex)
                                }
                            }
                        )

                        LazyColumn(
                            state = guideListState,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Complete operational guide to running Localiiiy. Learn how the circular proximity radar, dual-reach clips, and service rates work together.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        items(guideSteps) { step ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    // Visual Header Image
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                    ) {
                                        AsyncImage(
                                            model = step.imageUrl,
                                            contentDescription = step.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                    )
                                                )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = step.tag,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            text = step.title,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(12.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = step.subtitle,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = step.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        step.bulletPoints.forEach { point ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.Top,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = point,
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                }

                TutorialTab.ACTIVATION_BADGES -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF2A1B4E).copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("🎖️", fontSize = 22.sp)
                                        Text(
                                            text = "Activation Badges & Connection Tiers",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Badges represent verified community standing, authentic mutual connections, and neighborhood trust. Earn badges automatically as your network expands.",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Creator Connection Milestone Tiers",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val tiers = listOf(
                            BadgeTier.SPARK to "10,000+ Connections • Unlocks custom profile banner & tip jar",
                            BadgeTier.AERO to "100,000+ Connections • Unlocks 4K Studio video uploads & audio filters",
                            BadgeTier.ORBIT to "500,000+ Connections • Unlocks Priority Radar blip glow & badge display",
                            BadgeTier.TITAN to "1,000,000+ Connections • Unlocks global ad revenue share partner tier",
                            BadgeTier.SOLAR to "5,000,000+ Connections • Unlocks verified creator tick & direct brand deals",
                            BadgeTier.NEBULA to "10,000,000+ Connections • Unlocks Earth distribution algorithmic priority",
                            BadgeTier.VORTEX to "50,000,000+ Connections • Unlocks custom sound effect synthesizer presets",
                            BadgeTier.LOCALIIIY to "100,000,000+ Connections • Unlocks platform honorary ambassador crest",
                            BadgeTier.ZENITH to "500,000,000+ Connections • Unlocks celestial space theme exclusive badge",
                            BadgeTier.INFINITY to "1,000,000,000+ Connections • Universal master creator status (Earth Peak)"
                        )

                        items(tiers) { (tier, perkDescription) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, tier.color.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = tier.color.copy(alpha = 0.2f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(tier.emoji, fontSize = 20.sp)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = tier.title,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = tier.color
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "${tier.threshold / 1_000}K Connected",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = perkDescription,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Resident & Community Activation Badges",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(communityBadges) { badge ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, badge.color.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(badge.emoji, fontSize = 24.sp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = badge.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Category: ${badge.badgeCategory}",
                                                fontSize = 10.sp,
                                                color = badge.color,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Requirement: ${badge.requirement}",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Perk: ${badge.perk}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                TutorialTab.MONETIZATION -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Prominent $1,000 USD Minimum Withdrawal Card
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0F2E1B),
                                border = BorderStroke(1.5.dp, Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("💵", fontSize = 20.sp)
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "MINIMUM WITHDRAWAL THRESHOLD",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = Color(0xFF81C784),
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = "$1,000 USD Equivalent",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 20.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "To ensure international financial compliance, prevent banking fraud, and cover cross-border settlement fees, all creator withdrawals require a minimum threshold of 1,000 US Dollars or its exact equivalent in each country's respective currency.",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Dynamic Country Currency Selector & Converter
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.Black.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Select Country to View Exact Minimum Threshold:",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF81C784)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                val topCurrencies = listOf(
                                                    LocaliiiyCurrency.USD,
                                                    LocaliiiyCurrency.EUR,
                                                    LocaliiiyCurrency.GBP,
                                                    LocaliiiyCurrency.INR,
                                                    LocaliiiyCurrency.JPY,
                                                    LocaliiiyCurrency.CAD,
                                                    LocaliiiyCurrency.AUD,
                                                    LocaliiiyCurrency.AED,
                                                    LocaliiiyCurrency.SAR,
                                                    LocaliiiyCurrency.BRL,
                                                    LocaliiiyCurrency.SGD
                                                )
                                                items(topCurrencies) { curr ->
                                                    val isSelected = calculatorCurrency == curr
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) Color(0xFF4CAF50) else Color(0xFF1B3824),
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable { calculatorCurrency = curr }
                                                    ) {
                                                        Text(
                                                            text = "${curr.flag} ${curr.code}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.Black else Color.White,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "${calculatorCurrency.country} Minimum Payout:",
                                                        fontSize = 11.sp,
                                                        color = Color.LightGray
                                                    )
                                                    Text(
                                                        text = CurrencyHelper.format(1000.0, calculatorCurrency),
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color(0xFF4CAF50)
                                                    )
                                                }
                                                Text(
                                                    text = "Rate: 1 USD = ${calculatorCurrency.rateToUSD} ${calculatorCurrency.code}",
                                                    fontSize = 10.sp,
                                                    color = Color.LightGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "How Users Earn Money on Localiiiy",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val monetizationStreams = listOf(
                            Triple(
                                "1. In-Stream & Feed Ad Revenue (55% Creator Share)",
                                "Earn a guaranteed 55% share of verified gross ad revenue generated on your vertical Clips and long-form Studio videos. Localiiiy charges only a 45% infrastructure maintenance fee.",
                                "📺 Ad Revenue"
                            ),
                            Triple(
                                "2. Super Thanks & Micro-Tips (0% Platform Fee)",
                                "Viewers and neighbors can send direct monetary micro-tips on your posts, live radar signals, and studio streams. Creators keep 100% of fan tips with zero platform deductions.",
                                "❤️ Fan Tips"
                            ),
                            Triple(
                                "3. Hyperlocal Service Gigs & Hourly Consulting",
                                "List your professional skills (music production, tutoring, repairs, personal training, photography). Display your hourly rate ($/hr) prominently below your avatar on the left side and receive direct bookings.",
                                "💼 Service Gigs"
                            ),
                            Triple(
                                "4. Peer-to-Peer Marketplace Sales",
                                "Sell physical products to nearby neighbors. All transactions are protected by our Secure Digital Escrow system (5% platform fee) until the physical handover is confirmed.",
                                "🛍️ Marketplace"
                            ),
                            Triple(
                                "5. Neighborhood Bounties & Tasks",
                                "Earn bounties by fulfilling local assistance requests, community deliveries, and local signal dispatches in your neighborhood.",
                                "🎯 Bounties"
                            ),
                            Triple(
                                "6. Gamified Referrals & Wallet Bonus",
                                "Invite neighbors to Localiiiy using your unique referral code. Earn direct bonus points. Requires watching a short sponsored video to unlock rewards.",
                                "🤝 Referral Cash"
                            ),
                            Triple(
                                "7. Daily Check-in Rewards",
                                "Complete your gamified daily check-in streak by watching a sponsored video. Unlock visibility perks which translate directly to more profile views.",
                                "📅 Streak Earnings"
                            ),
                            Triple(
                                "8. Boost & Sponsor Dashboard",
                                "Invest in your local business by paying to boost market listings to the top of the feed or buying a glowing sponsored pin on the Live Radar.",
                                "🚀 Boost Ads"
                            ),
                            Triple(
                                "9. Localiiiy Premium Subscriptions (₹299/mo / $2.99)",
                                "Users can upgrade to Premium (₹299/mo / $2.99) for 5x Hyperlocal Feed Boosts, 0% Safe-Haven Market Escrow fees, Gold Pro badge, advanced skill radar filters, and 4K Studio. (Master Ghost Mode remains 100% free for all users).",
                                "🌟 Premium"
                            )
                        )

                        items(monetizationStreams) { (title, desc, badge) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.padding(start = 6.dp)
                                        ) {
                                            Text(
                                                text = badge,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Global Payout Methods Supported:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Stripe Global Connect (140+ countries)\n• PayPal Worldwide Instant Disbursal\n• International SWIFT / BIC Bank Wire Transfer\n• SEPA Direct Debit (European Union)\n• Unified Payments Interface - UPI (India)\n• PIX Instant Settlement (Brazil)",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
