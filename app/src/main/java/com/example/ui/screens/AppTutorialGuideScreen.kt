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
    RUNNING_GUIDE("App Guide (18 Steps)", "📱"),
    HELPDESK_FAQ("Helpdesk & FAQ", "🛟"),
    ACTIVATION_BADGES("Activation Badges", "🎖️"),
    MONETIZATION("Earn & Payouts", "💰")
}

data class HelpdeskFaqItem(
    val id: String,
    val category: String,
    val question: String,
    val answer: String,
    val quickTip: String
)

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
            HashAnchorItem(id = "#step-0", label = "1. Ghost vs Area", targetIndex = 1),
            HashAnchorItem(id = "#step-1", label = "2. Safe Exchange", targetIndex = 2),
            HashAnchorItem(id = "#step-2", label = "3. 9:16 Clips", targetIndex = 3),
            HashAnchorItem(id = "#step-3", label = "4. Copyright & Revenue", targetIndex = 4),
            HashAnchorItem(id = "#step-4", label = "5. Verified Badges", targetIndex = 5),
            HashAnchorItem(id = "#step-5", label = "6. Safety Shield", targetIndex = 6),
            HashAnchorItem(id = "#step-6", label = "7. Creator Wallet", targetIndex = 7),
            HashAnchorItem(id = "#step-7", label = "8. Clip to Shop", targetIndex = 8),
            HashAnchorItem(id = "#step-8", label = "9. 75+ Languages", targetIndex = 9),
            HashAnchorItem(id = "#step-9", label = "10. Web & Deep Links", targetIndex = 10),
            HashAnchorItem(id = "#step-10", label = "11. Radar Obfuscation", targetIndex = 11),
            HashAnchorItem(id = "#step-11", label = "12. Account Oblivion", targetIndex = 12),
            HashAnchorItem(id = "#step-12", label = "13. 15 Civic Tools", targetIndex = 13),
            HashAnchorItem(id = "#step-13", label = "14. Community Hub", targetIndex = 14),
            HashAnchorItem(id = "#step-14", label = "15. Tactical Radar 2.0", targetIndex = 15),
            HashAnchorItem(id = "#step-15", label = "16. Web-Android Sync", targetIndex = 16),
            HashAnchorItem(id = "#step-16", label = "17. Spectator & Teens", targetIndex = 17),
            HashAnchorItem(id = "#step-17", label = "18. 4K Studio & AI", targetIndex = 18)
        )
    }

    val guideSteps = remember {
        listOf(
            TutorialGuideStep(
                title = "1. How to Broadcast Presence: Ghost Mode vs. Show in Area",
                subtitle = "Sovereign privacy controls between stealth observation and local broadcast",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=80",
                description = "Choose how visible you want to be across the neighborhood. Toggle between full visibility on the circular radar or 100% anonymous Ghost Cloak mode.",
                bulletPoints = listOf(
                    "Ghost Mode (Stealth Observer): Browse radar, clips, and feeds with complete zero-broadcast anonymity without leaving coordinate traces",
                    "Show in Area (Local Presence): Broadcast your verified profile within your chosen radius (100m to 500km) to meet neighbors",
                    "Dynamic Spatial Obfuscation: Blur your precise GPS coordinates into a gentle 3km-5km neighborhood sector",
                    "Dedicated Radar Power Switch: Turn the radar hardware receiver and transmitter on or off with 1 tap"
                ),
                tag = "Ghost Mode"
            ),
            TutorialGuideStep(
                title = "2. How to List Items & Use Safe Exchange Spots in Market",
                subtitle = "Peer-to-peer commerce, verified safe haven spots & zero-commission sales",
                imageUrl = "https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=1080&auto=format&fit=crop&q=80",
                description = "Safely buy, sell, and trade physical products or book local services directly with nearby community members.",
                bulletPoints = listOf(
                    "List with ease: Upload photos, set hourly rates or fixed prices, and select your neighborhood landmark",
                    "Safe Exchange Spots: Meet at designated, well-lit civic spots (police stations, libraries, coffee commons) marked on the map",
                    "Smart Escrow & Cashless Handshake: Funds are held securely and released only when both parties scan the exchange QR pass",
                    "Transparent Service Taxonomy: Emergency Plumbing, Electrical Repairs, Mobile Wash, Handyman, and 15+ verified trades"
                ),
                tag = "Safe Market"
            ),
            TutorialGuideStep(
                title = "3. How to Record & Publish 9:16 Market Clips",
                subtitle = "Turn engaging vertical video reels into instant shoppable storefronts",
                imageUrl = "https://images.unsplash.com/photo-1533750516457-a7f992034fec?w=1080&auto=format&fit=crop&q=80",
                description = "Publish high-energy 9:16 vertical short clips. Convert any creative clip into a shoppable marketplace product with one tap.",
                bulletPoints = listOf(
                    "Ultra-responsive 9:16 camera: Record short videos, apply real-time LUT filters, and select local soundtracks",
                    "1-tap Clip to Shop: Tag your clip with an item title, price, and pickup location to create an interactive buy button",
                    "Dual-Reach Distribution: Clips start organically with your nearest neighbors before expanding worldwide to Earth",
                    "Cinema Mode: Tap the video viewport for an unobstructed full-screen viewing experience"
                ),
                tag = "9:16 Clips"
            ),
            TutorialGuideStep(
                title = "4. Understanding Creator Copyright & Revenue Re-Routing",
                subtitle = "Licensing controls, remix permissions & transparent micro-tipping",
                imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=1080&auto=format&fit=crop&q=80",
                description = "Localiiiy gives creators complete ownership of their intellectual property, sound samples, and commercial revenue streams.",
                bulletPoints = listOf(
                    "Permit Reuse Toggle: Grant or restrict other creators from reusing your original audio and visual cuts",
                    "Commercial Reuse Consent: License content for verified local sponsor promotions and neighborhood businesses",
                    "Revenue Re-Routing: Royalties from remixes and viral spread automatically route back to original creators' Creator Wallets",
                    "Cryptographic attribution watermark embedded in every exported video asset"
                ),
                tag = "Copyright & Revenue"
            ),
            TutorialGuideStep(
                title = "5. How to Earn & Display Verified Neighborhood Badges",
                subtitle = "Climb civic recognition tiers from Local Explorer to Neighborhood Icon",
                imageUrl = "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=1080&auto=format&fit=crop&q=80",
                description = "Earn verified civic badges based on genuine neighborhood participation, safe market transactions, and community leadership.",
                bulletPoints = listOf(
                    "Local Explorer (Tier 1): Automatically earned after completing zero-knowledge onboarding and profile setup",
                    "Trusted Neighbor (Tier 2): Awarded after 5 verified safe-exchange transactions with 5-star peer reviews",
                    "Civic Creator (Tier 3): Unlocked by publishing original clips and hosting community discussion threads",
                    "Badges display prominently on your Space profile card, next to your handle in comments, and on the radar"
                ),
                tag = "Verified Badges"
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
                    "Official live Privacy Policy & Terms hosted at https://localiiiy.web.app/privacy.html"
                ),
                tag = "Store Safety"
            ),
            TutorialGuideStep(
                title = "13. 15-Category Hyperlocal Civic Tools & AI Local Translator",
                subtitle = "Emergency SOS, civic infrastructure fixes & ascending proximity (3 KM to 50 KM)",
                imageUrl = "https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca?w=1080&auto=format&fit=crop&q=80",
                description = "Access the Civic Tools tab inside Pulse to broadcast and respond to verified neighborhood needs sorted strictly by ascending distance from 3 KM up to 50 KM.",
                bulletPoints = listOf(
                    "15 Dedicated Categories: Blood SOS, Lost & Found, Civic Fix, Ride Pool, Food Rescue, Skill Swap, Flash Deal, Rent Tool, Play Match, Work Cafe, Yard Sale, Safe Walk, Street Art, Plant Swap, and Elder Care",
                    "Tap the '+' button on any category pill to open a structured, category-specific broadcast form",
                    "Filter broadcasts by exact radius: 3 KM, 5 KM, 10 KM, 15 KM, 20 KM, 30 KM, or 50 KM",
                    "Built-in AI Multi-Lingual Local Translator with voice dictation across regional and global languages"
                ),
                tag = "Civic Tools"
            ),
            TutorialGuideStep(
                title = "14. Localiiiy Community Hub (5 Interactive Sub-Tabs)",
                subtitle = "Neighborhood clubs, Spatial Voice Rooms, Ranked Polls, Vault & VIP Lounge",
                imageUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1080&auto=format&fit=crop&q=80",
                description = "Join or create hyperlocal micro-communities (Urban Gardeners, Local Dog Owners, Neighborhood Watch, Indie Creators) with five specialized collaboration spaces.",
                bulletPoints = listOf(
                    "Feed & Alerts: Share neighborhood updates and attach Market items, Studio episodes, or Clips",
                    "Live Voice: Drop into low-latency Spatial Audio Rooms with connected neighbors",
                    "Polls & Votes: Participate in democratic neighborhood governance and budget priorities",
                    "Resource Vault & VIP Lounge: Access shared emergency contacts, tool registries, and 100+ Sparks perks"
                ),
                tag = "Community Hub"
            ),
            TutorialGuideStep(
                title = "15. Tactical Radar 2.0: 3D Pitch, Sonar, +10km Boost & HUD Themes",
                subtitle = "Dual actionable control strips, Kalman telemetry smoothing & geofence pins",
                imageUrl = "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=1080&auto=format&fit=crop&q=80",
                description = "Master the two actionable tactical control strips above the circular radar scope to customize range, perspective, power consumption, and visual HUD palettes.",
                bulletPoints = listOf(
                    "Row 1 Controls: 2D Top-Down / 3D Pitch 45°, Active Sonar Shockwave, +10km RF Boost, Privacy Shield, Broadcast Signal, Night HUD, and Full Power / Eco Scan",
                    "Row 2 Controls: Compass Heading Lock, Sweep Interval (1.5s–10s), Map Overlay, Alert Pins toggle, Verified Residents filter, and Kalman Filter smoothing",
                    "5 Custom HUD Themes: Switch between Phosphor Green (PHOS), Amber Gold (GOLD), Cyber Cyan (CYAN), Crimson Night (CRIM), and Violet Aura (AURA)",
                    "Switch seamlessly between Circular Radar View, Street Map View, and Neighbor Grid View"
                ),
                tag = "Tactical Radar"
            ),
            TutorialGuideStep(
                title = "16. 11-Section Registration & Bidirectional Web ↔ Android Sync",
                subtitle = "One unified account across the Android APK and https://localiiiy.web.app",
                imageUrl = "https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?w=1080&auto=format&fit=crop&q=80",
                description = "Your Localiiiy identity is unified through Firebase Authentication and Cloud Firestore (`users/{uid}` and `usernames/{handle}`). Register once and sign in on either Android or Web.",
                bulletPoints = listOf(
                    "Dual Sign-In Support: Log in using either your unique @username handle OR your registered Email Address + Password on both Android and Web",
                    "Dual 6-Digit Cryptographic OTP: Verifies both Email and Mobile SMS during registration with 5-attempt brute-force lockout protection",
                    "Live @username Reservation: Real-time availability check against Cloud Firestore with smart handle suggestions",
                    "Scroll-to-Accept Omnibus Legal Charter: Ensures every neighbor reads and accepts community safety standards"
                ),
                tag = "Web ↔ Android Sync"
            ),
            TutorialGuideStep(
                title = "17. Guest Spectator Mode & Strict Under-18 Radar Lock (18+ Only)",
                subtitle = "Zero-footprint browsing and total Radar restriction below 18 years of age",
                imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=1080&auto=format&fit=crop&q=80",
                description = "Explore public neighborhood feeds without logging in via Guest Spectator Mode, while strict age-gating totally restricts Tactical Radar for anyone below 18 years of age.",
                bulletPoints = listOf(
                    "Guest Spectator Mode: Allows read-only viewing of public posts, clips, studio videos, and market listings with zero GPS footprint",
                    "Tactical Radar Totally Restricted Below 18 Years: Users aged 13–17 are strictly prohibited and locked out from Tactical Radar broadcasting or stranger radar sweeps (18+ Verified Adults Only)",
                    "Developer Safe-Harbor: Users control Radar ON/OFF, Location ON/OFF, Ghost Mode, and SHA-256 Forensic Logs at their own sovereign choice"
                ),
                tag = "18+ Radar Gate"
            ),
            TutorialGuideStep(
                title = "18. 4K Studio Cinema, AI Hook Generator & LCC SHA-256 Hash Engine",
                subtitle = "16:9 neighborhood documentaries, AI shot scripts & SHA-256 + pHash copyright proof",
                imageUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=1080&auto=format&fit=crop&q=80",
                description = "Publish widescreen 4K episodes, mini-documentaries, and neighborhood spotlights in the Studio tab with built-in AI script assistance and SHA-256 + perceptual pHash copyright protection.",
                bulletPoints = listOf(
                    "AI Hook & Shot Script Generator: Enter any local topic to generate a 3-part hook, story sequence, and call-to-connect script",
                    "LCC Cryptographic Hash Formula: Computes SHA-256(Creator_UID | Media_Bytes | pHash | UTC_Timestamp) mapped to LCC-XXXXXX-2026 + 64-hex digest",
                    "100% Revenue Rerouting & 3-Strike Rule: Unauthorized re-uploads are flagged by pHash/SHA-256 and 100% of ad revenue is rerouted to the true LCC creator"
                ),
                tag = "Studio & LCC Hash"
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

                TutorialTab.HELPDESK_FAQ -> {
                    var faqSearchQuery by remember { mutableStateOf("") }
                    var selectedFaqCategory by remember { mutableStateOf("All") }
                    var expandedFaqId by remember { mutableStateOf<String?>("faq-1") }

                    val faqCategories = remember {
                        listOf(
                            "All",
                            "Account & Login",
                            "Radar & Ghost Mode",
                            "Civic Tools & Community",
                            "Market & Escrow",
                            "Creator Wallet & Payouts",
                            "Safety, SOS & Deletion"
                        )
                    }

                    val helpdeskFaqs = remember {
                        listOf(
                            HelpdeskFaqItem(
                                id = "faq-1",
                                category = "Account & Login",
                                question = "Can I use the same Localiiiy account on both the Android App and the Web App (localiiiy.web.app)?",
                                answer = "Yes! Localiiiy uses a unified Firebase Authentication and Cloud Firestore identity engine (`users/{uid}` and `usernames/{handle}`). Any account registered on the Android app or on https://localiiiy.web.app works immediately on both platforms.",
                                quickTip = "You can sign in using either your @username handle OR your registered Email Address + Password."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-2",
                                category = "Account & Login",
                                question = "Why do I need to verify both a 6-digit Email OTP and a 6-digit Mobile SMS OTP during registration?",
                                answer = "Dual 6-digit cryptographic OTP verification prevents bot networks, fake sybil accounts, and neighborhood spam. It ensures every neighbor on the radar and in Safe-Haven Marketplace transactions is an authentic human.",
                                quickTip = "After 5 incorrect OTP attempts, verification locks for 60 seconds to prevent brute-force guessing."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-3",
                                category = "Account & Login",
                                question = "What is Guest Spectator (Read-Only) Mode and what is restricted when I'm not signed in?",
                                answer = "Guest Spectator Mode lets you explore public Pulse posts, vertical Clips, Studio videos, and Marketplace listings without logging in or broadcasting any GPS footprint. However, interactive actions—Liking, Remarks, posting, uploading, Space IDs, Community Hubs, Civic Tool broadcasts, and Connecting—require signing in.",
                                quickTip = "Tap 'Log In or Register' on the top Spectator banner anytime to unlock full community participation."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-4",
                                category = "Account & Login",
                                question = "What is the minimum age requirement, and why is Tactical Radar totally restricted below 18 years of age?",
                                answer = "You must be at least 13 years old to register (COPPA, GDPR-K, and India DPDP Act compliance). However, the Tactical Radar option (live radar broadcasting, stranger radar sweeps, and proximity beacons) is TOTALLY RESTRICTED and LOCKED for anyone below 18 years of age (`calculatedAge < 18`). Users aged 13–17 have Ghost Mode permanently locked ON and cannot access Tactical Radar until they turn 18.",
                                quickTip = "Tactical Radar is strictly an 18+ adult feature; users under 13 are completely blocked from registration."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-5",
                                category = "Account & Login",
                                question = "Why does Localiiiy use 'Connect', 'Connected', and 'Connections' instead of 'Follow' or 'Followers'?",
                                answer = "Localiiiy is built around reciprocal neighborhood community rather than passive celebrity hierarchies. Every social link on Pulse, Clips, Studio, Radar, and Space strictly uses Connect / Connected / Connections.",
                                quickTip = "Use the 'Connected' filter tab in Pulse and Clips to view updates exclusively from your connected neighbors."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-6",
                                category = "Radar & Ghost Mode",
                                question = "How does Ghost Mode (Stealth Observer) protect my exact home or room location?",
                                answer = "When Ghost Mode is ON, your device immediately purges any active `radar_beacons/{uid}` document from Cloud Firestore and stops transmitting coordinates. Even when an 18+ adult enables broadcasting ('Show in Area'), all coordinates are rounded to 3 decimal places (`roundCoord3`, ~110m fuzzing grid) plus 1km–500km obfuscation sliders so your exact room or apartment door is never exposed.",
                                quickTip = "You can also select 3 KM, 5 KM, or 10 KM obfuscation rings below the radar scope."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-7",
                                category = "Radar & Ghost Mode",
                                question = "What do the two control rows above the circular Tactical Radar do?",
                                answer = "Row 1 controls perspective and transceiver modes: 2D Top-Down vs. 3D Pitch 45°, Active Sonar Pulse shockwave, +10km High-Gain RF Boost, Privacy Shield, Broadcast Signal, Night HUD, and Full Power vs. Eco Scan. Row 2 controls telemetry: Compass Heading lock, Sweep Interval (1.5s–10s), Map Overlay, Alert Pins toggle, All Residents vs. Verified Only, Node count, and Kalman Filter smoothing.",
                                quickTip = "Tap '+10km BOOST' to expand your effective radar reach by +10 KM instantly."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-8",
                                category = "Radar & Ghost Mode",
                                question = "Can I turn off the radar transceiver completely without logging out?",
                                answer = "Yes. Directly below the circular radar canvas is the 'Radar Online / Turn OFF' power card. Tapping 'Turn OFF' places the RF transceiver in standby mode until you turn it back ON.",
                                quickTip = "You can also switch between 5 HUD color palettes: Phosphor Green (PHOS), Gold, Cyan, Crimson, and Aura."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-9",
                                category = "Civic Tools & Community",
                                question = "What are the 15 Hyperlocal Civic Tools categories inside the Pulse tab?",
                                answer = "The Civic Tools tab provides 15 structured neighborhood utility channels sorted by ascending proximity (3 KM to 50 KM): (1) Blood SOS, (2) Lost & Found, (3) Civic Fix, (4) Ride Pool, (5) Food Rescue, (6) Skill Swap, (7) Flash Deal, (8) Rent Tool, (9) Play Match, (10) Work Cafe, (11) Yard Sale, (12) Safe Walk, (13) Street Art, (14) Plant Swap, and (15) Elder Care.",
                                quickTip = "Tap the '+' icon inside any of the 15 category pills to publish a verified alert for that specific category."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-10",
                                category = "Civic Tools & Community",
                                question = "How does the Ascending Proximity radius filter (3 KM to 50 KM) work in Civic Tools?",
                                answer = "Civic broadcasts are sorted nearest-first (0.1 KM -> 50 KM) so critical neighborhood alerts—such as an urgent Blood SOS or Lost Pet 400 meters away—appear at the top. You can filter the feed by 3 KM, 5 KM, 10 KM, 15 KM, 20 KM, 30 KM, or 50 KM.",
                                quickTip = "Use the built-in AI Multi-Lingual Local Translator inside Civic Tools to translate alerts into regional languages."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-11",
                                category = "Civic Tools & Community",
                                question = "What are the 5 sub-tabs inside the Local Community Hub?",
                                answer = "Each neighborhood hub (e.g., Urban Gardeners, Local Dog Owners, Neighborhood Watch, Indie Creators) includes 5 sub-tabs: (1) Feed & Alerts, (2) Live Voice (Spatial Audio Rooms), (3) Polls & Votes (ranked civic governance), (4) Resource Vault (emergency contacts & shared tool registry), and (5) VIP Lounge (for neighbors with 100+ Sparks).",
                                quickTip = "You can attach Marketplace items, Studio episodes, or Clips directly to your Community Hub posts."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-12",
                                category = "Civic Tools & Community",
                                question = "How does Creator Dual-Reach Distribution (Neighbor / City / Earth) work?",
                                answer = "When publishing a Pulse post, Clip, or Studio video, you choose its primary reach tier: Neighbor (5 KM hyperlocal ward), City (50 KM municipal sector), or Earth (global discovery). High-engagement local posts organically graduate from Neighbor to City and Earth.",
                                quickTip = "You can also toggle between Pure Chronological (zero-algorithm) and Algorithmic feeds at the top of Pulse."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-13",
                                category = "Market & Escrow",
                                question = "How does Safe-Haven Handshake Escrow protect buyers and sellers in the Market?",
                                answer = "When you tap 'Request Safe-Haven Handshake' on a Marketplace listing, funds are locked in cryptographic escrow and a 6-digit OTP handshake code is generated. Both parties meet at a verified 24/7 CCTV Police Safe-Haven Exchange Zone (Green Zone) and verify the 6-digit OTP to release payment.",
                                quickTip = "Never release the 6-digit Handshake OTP before inspecting the physical item in person."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-14",
                                category = "Market & Escrow",
                                question = "What is the platform fee for Marketplace sales and Service Gigs?",
                                answer = "Standard Safe-Haven Escrow transactions carry a 5% platform escrow protection fee. Upgrading to Localiiiy Pro / Premium reduces your Safe-Haven Escrow fee to 0%.",
                                quickTip = "Direct peer-to-peer neighborhood swaps and fan micro-tips always have 0% platform commission."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-15",
                                category = "Market & Escrow",
                                question = "How do I convert a 9:16 vertical Clip into a shoppable Marketplace product?",
                                answer = "When uploading or viewing your 9:16 vertical Clip, enable the 'Clip-to-Shop' toggle and attach an item title, price, and Safe-Haven pickup landmark. Viewers will see a golden shopping bag button on your Clip to initiate an instant Safe-Haven Handshake.",
                                quickTip = "Keep proximity and landmark badges anchored in the bottom-left info column of your Clip for clean visibility."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-16",
                                category = "Creator Wallet & Payouts",
                                question = "What is the minimum withdrawal threshold for the Creator Wallet, and why is it $1,000 USD equivalent?",
                                answer = "All creator withdrawals require a minimum balance of $1,000 USD (or its exact equivalent in your local currency, such as ₹83,500 INR, €920 EUR, or £790 GBP). This threshold ensures international AML/KYC compliance, tax reporting verification, and zero cross-border wire settlement friction.",
                                quickTip = "Use the Currency Converter inside the 'Earn & Payouts' tab to check the exact $1,000 equivalent in 50+ currencies."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-17",
                                category = "Creator Wallet & Payouts",
                                question = "How much ad revenue and fan tip revenue do creators keep?",
                                answer = "Creators earn a guaranteed 55% share of verified gross in-stream ad revenue on Clips and 4K Studio videos (45% platform infrastructure share). On direct Fan Micro-Tips & Super Support, creators keep 100% (0% platform commission).",
                                quickTip = "Supported payout methods include UPI (India), Stripe Connect, PayPal, SWIFT/IBAN, SEPA, and PIX."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-18",
                                category = "Creator Wallet & Payouts",
                                question = "How does the Localiiiy Creator Copyright (LCC) SHA-256 + pHash Hash Value Engine work?",
                                answer = "Every original Post, 9:16 Clip, and 4K Studio video is stamped with a deterministic cryptographic digest: `SHA-256(Creator_UID | @username | Media_Byte_Stream | Perceptual_pHash | UTC_Epoch_Timestamp | License_Salt)` formatted as both a 64-character hex SHA-256 hash (`sha256:8f4e...`) and a human-readable certificate (`LCC-XXXXXX-2026`). Our `MediaFingerprintEngine` checks both exact SHA-256 bytes and perceptual/acoustic `phash_` vectors on every upload—automatically quarantining stolen re-uploads and rerouting 100% of ad revenue to the verified original LCC creator.",
                                quickTip = "You can select 'All Rights Reserved' or 'Localiiiy Creative Commons (LCC)' and toggle Audio Reuse & Remix rights per upload."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-19",
                                category = "Creator Wallet & Payouts",
                                question = "How do Daily Check-In Sparks and Referral Bonuses work?",
                                answer = "Tapping 'Claim Daily Check-In' awards +25 Sparks per day and builds your streak toward a 24-hour Radar Visibility Glow. Referring a verified neighbor via your `@handle` invite code awards bonus Sparks to both accounts upon OTP completion.",
                                quickTip = "Sparks unlock VIP Community Lounge access and local feed boosts."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-20",
                                category = "Safety, SOS & Deletion",
                                question = "What is the Cyberstalking Safety Shield & Rapid Approach Velocity Alert?",
                                answer = "Localiiiy monitors relative coordinate closure rates. If another radar node closes distance toward you faster than 25 km/h within a 250m perimeter, the Cyberstalking Safety Shield automatically cloaks your profile into Ghost Mode and offers 1-tap Safe-Haven Police SOS routing.",
                                quickTip = "Localiiiy enforces a Zero-Tolerance Anti-Stalking Policy with permanent hardware bans."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-21",
                                category = "Safety, SOS & Deletion",
                                question = "How does the Panic Cloak Decoy Calculator work in an emergency?",
                                answer = "Tapping the Panic Cloak button (or shaking your device when enabled) immediately disguises the entire app as a functional standard Utility Calculator v1.0 while purging your live radar beacon in the background. Tap 'C' three times (`C` -> `C` -> `C`) or enter your 4-digit PIN (default `7777`) to exit the decoy.",
                                quickTip = "Accessible from the Space / Profile safety bar on both Android and Web."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-22",
                                category = "Safety, SOS & Deletion",
                                question = "How do I permanently delete my Localiiiy account and erase all cloud and local data?",
                                answer = "You can permanently delete your account in two ways: (1) In-App: Go to Space (Profile) -> tap 'Delete My Account Permanently (1-Tap Oblivion)', or (2) Web Self-Service Portal: Visit https://localiiiy.web.app/delete-account.html. Both methods immediately delete your Firebase Auth credentials, `users/{uid}` profile, `usernames/{handle}` reservation, `radar_beacons/{uid}`, and local storage.",
                                quickTip = "Account deletion is immediate and irreversible under GDPR Art. 17, CCPA, and India DPDP Act."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-23",
                                category = "Safety, SOS & Deletion",
                                question = "What is the SHA-256 Forensic Activity Audit Log?",
                                answer = "Every security-sensitive action (login, registration, Ghost Mode cloak, radar beacon purge, content publish) is recorded in a tamper-evident SHA-256 hash ledger (`audit_logs` & `/safety_logs/`) that you can inspect anytime in Space -> Forensic Audit Log or export as ISO/IEC 27037 court evidence.",
                                quickTip = "Helps verify that your account and location beacon have never been accessed without authorization."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-24",
                                category = "Safety, SOS & Deletion",
                                question = "Does Localiiiy access my contacts list, background microphone, or raw photo library?",
                                answer = "No. Localiiiy strictly complies with Google Play Developer Program policies: we never request `READ_CONTACTS`, background audio recording, or broad `READ_EXTERNAL_STORAGE` permissions. Media uploads use the zero-permission Android system Photo Picker.",
                                quickTip = "Location access is strictly optional—you can use Ghost Spectator Mode with zero permissions."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-25",
                                category = "Safety, SOS & Deletion",
                                question = "How do I report illegal content, harassment, CSAM, or a marketplace scam?",
                                answer = "Use the 1-tap Report / Flag button on any post, clip, profile, or market listing, or email our Official Helpdesk & Grievance Desk directly at Localiiiy.Helpdesk@gmail.com. Emergency safety reports are triaged within <15 minutes, and standard grievances are resolved within 24 hours.",
                                quickTip = "Blocking a user enforces a bidirectional Zero-Knowledge Block—making you completely invisible to each other."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-26",
                                category = "Account & Login",
                                question = "Where can I download the official Localiiiy mobile app?",
                                answer = "Download the official Android app directly from the Google Play Store (`https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live`). The Apple App Store (iOS) release is currently in final preparation and marked 'Available Soon'.",
                                quickTip = "Avoid unofficial third-party APK mirror sites—always use the official Google Play Store link or https://localiiiy.web.app."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-27",
                                category = "Safety, SOS & Deletion",
                                question = "Is the Developer or Owner of Localiiiy responsible for user actions, cybercrime, or fraud?",
                                answer = "No. The Developer, Creator, and Owner of Localiiiy holds ZERO civil, criminal, or financial responsibility or liability for any user's actions, offline meetups, cybercrime, or financial fraud because Localiiiy provides 100% sovereign self-protection switches directly to the user's choice—including Radar ON/OFF, Location ON/OFF, Master Ghost Mode ON/OFF, 1km–500km GPS obfuscation, Hide Profile/Phone/Email toggles, Safe-Haven Police Precinct 6-Digit OTP Escrow, and downloadable ISO/IEC 27037 SHA-256 Forensic `/safety_logs/` for direct submission to Cyber Crime authorities.",
                                quickTip = "Always keep Ghost Mode ON when you want stealth, never share OTPs before physical inspection, and export `/safety_logs/` if you ever need to file a police cybercrime report."
                            ),
                            HelpdeskFaqItem(
                                id = "faq-28",
                                category = "Radar & Ghost Mode",
                                question = "Why is the Tactical Radar option completely restricted and disabled for users under 18 years of age?",
                                answer = "To guarantee absolute minor protection under international child-safety frameworks, the Tactical Radar module (`LiveRadarScreen` / `TacticalRadarHUD` / live beacon broadcasting and nearby stranger radar sweeps) is strictly restricted to verified adults aged 18 and older (`18+ Only`). Any account with a Date of Birth under 18 has Tactical Radar completely locked and Master Ghost Mode permanently enforced.",
                                quickTip = "Minors aged 13–17 can safely enjoy creative Clips, Studio tutorials, and family-safe feeds with zero radar exposure."
                            )
                        )
                    }

                    val filteredFaqs = remember(faqSearchQuery, selectedFaqCategory, helpdeskFaqs) {
                        helpdeskFaqs.filter { item ->
                            val matchesCat = selectedFaqCategory == "All" || item.category == selectedFaqCategory
                            val q = faqSearchQuery.trim().lowercase()
                            val matchesQuery = q.isEmpty() ||
                                item.question.lowercase().contains(q) ||
                                item.answer.lowercase().contains(q) ||
                                item.category.lowercase().contains(q)
                            matchesCat && matchesQuery
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0B253A),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("🛟", fontSize = 24.sp)
                                        Column {
                                            Text(
                                                text = "Localiiiy 24/7 Helpdesk & FAQ Center",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.5.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "28 Updated Answers • 18+ Radar Gate • LCC SHA-256 Hash • Developer Safe-Harbor • Civic & Escrow",
                                                fontSize = 11.sp,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = faqSearchQuery,
                                        onValueChange = { faqSearchQuery = it },
                                        placeholder = { Text("Search Helpdesk questions (e.g. 18+ Radar, LCC Hash, Ghost Mode, Developer Liability)...", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search FAQ") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("helpdesk_search_input")
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(faqCategories) { cat ->
                                            val isSelected = selectedFaqCategory == cat
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.08f),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(100.dp))
                                                    .clickable { selectedFaqCategory = cat }
                                            ) {
                                                Text(
                                                    text = cat,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color(0xFF041B26) else Color.White,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        items(filteredFaqs, key = { it.id }) { faq ->
                            val isExpanded = expandedFaqId == faq.id
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(
                                    1.dp,
                                    if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedFaqId = if (isExpanded) null else faq.id }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = faq.category,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            text = if (isExpanded) "▲ Collapse" else "▼ Read Answer",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = faq.question,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(modifier = Modifier.padding(top = 8.dp)) {
                                            Text(
                                                text = faq.answer,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 18.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.12f),
                                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "💡 Quick Tip: ${faq.quickTip}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "📬 Direct Helpdesk & Grievance Officer Escalation",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Official Support & Grievance Email: Localiiiy.Helpdesk@gmail.com\n• Web Self-Service Account Deletion: https://localiiiy.web.app/delete-account.html\n• Response SLA: <15 mins for Emergency SOS / Safety • <24 hours for Account & Escrow tickets",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFFCBD5E1),
                                        lineHeight = 17.sp
                                    )
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
