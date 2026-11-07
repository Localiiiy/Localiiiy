package com.example.data.legal

object LegalClausesPart4 {

    val clauses: List<LegalClause> = listOf(
        LegalClause(
            id = "clause_19_explore_constellations_trends",
            clauseNumber = "Section 19.0",
            anchorId = "#explore",
            title = "Explore Screen, 20 Interest Constellations, Neighborhood Pulse Trends & Live Map Moments",
            category = LegalCategory.ALGORITHM_DUAL_REACH,
            plainSummary = "Governs the Explore Screen, the 20 Interest Constellations, Neighborhood Pulse Trends analytics, and Live Google Map Moments. Shows how local cultural sparks and trending topics are aggregated without invasive personal profiling.",
            legalText = """
19.1 Explore Screen & 20 Interest Constellations (`ExploreScreen` & `NeighborhoodPulseTrendsComponent`):
(a) Interest-Driven Discovery: During registration and within Explore, users curate their personal constellation from 20 core neighborhood categories (including Local Food & Coffee, Indie Film & Clips, Urban Photography, Maker Crafts & Art, Tech & Coding, Street Music, Fitness & Running, Pet Neighbors, Vintage & Thrift, Sustainable Gardening, Book Clubs, Local Sports, Comedy, Architecture, Culinary Arts, Gaming, Civic Action, Nightlife, Family & Kids, and Outdoor Adventure);
(b) Aggregated Anonymized Neighborhood Pulse Trends: Trending neighborhood topics and velocity charts (`NeighborhoodPulseTrendsComponent`, `DataAnalysisSheet`) are computed using k-anonymized, differential-privacy style topic counts so individual user browsing histories are never exposed.

19.2 Live Google Map Background & Map Moments (`LiveGoogleMapBackground` & `GoogleMapMomentsComponent`):
Public Map Moments display only content where the creator has explicitly opted out of Ghost Mode and attached a public community anchor or landmark pin. Tapping a Map Moment respects all 2-way block boundaries and age-gate shields.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "19.1",
                    subTitle = "Zero Behavioral Shadow-Profiling",
                    content = "Unlike legacy ad-tech networks, Localiiiy does not construct hidden psychological shadow profiles or track cross-app web browsing habits."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_20_profile_identity_connections_manager",
            clauseNumber = "Section 20.0",
            anchorId = "#connections",
            title = "Profile Identity Card, QR Space Card, Mutual Connections Graph Manager & Verified Creator Badges",
            category = LegalCategory.COMMUNITY,
            plainSummary = "Covers your Profile Identity Card, scannable QR Space Card, Mutual Connections Social Graph Manager (strictly 'Connect', 'Connected', and 'Connections'), Edit Profile, Password Change, and Other User Profile inspection.",
            legalText = """
20.1 Sovereign Profile Identity Card (`ProfileScreen` & `ProfileIdentityCard`):
Your Localiiiy Profile serves as your digital neighborhood Space Card:
(a) Strictly "Connections" Social Graph (`ConnectionsManagerScreen`): Localiiiy is architected around mutual human connection rather than asymmetric clout chasing. All social counters, action buttons, and relationship managers exclusively use **"Connect"**, **"Connected"**, and **"Connections"** terminology;
(b) Scannable QR Space Card & Bio Link (`Localiiiy.app/@username`): Users can share their profile via cryptographic QR codes or deep links (`ShareHelper`). Scanning a neighbor's QR code in person allows instant mutual Connection upon acceptance;
(c) Profile Editing & Password Rotation (`EditProfileDialog` & `ChangePasswordDialog`): Users can update their Display Name, Bio, Home Community Anchor, Avatar, and rotate their account password at any time with full local and cloud synchronization.

20.2 Other User Profile Inspection & Safety Actions (`OtherUserProfileSheet`):
When viewing another neighbor's profile sheet, users can inspect mutual Connections, view public posts/clips, initiate a Direct Message or Audio/Video Call (if permitted by the recipient's privacy toggles), or execute 1-tap Mute, Block (`BlockedUsersScreen`), or Cyberstalking Forensic Report (`CyberstalkingSafetyScreen`).
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "20.1",
                    subTitle = "Revocation of Connection Status",
                    content = "Users may disconnect from any user at any time inside `ConnectionsManagerScreen`, immediately revoking 'Connected-Only' post, clip, and DM access."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_21_display_themes_localization_currency",
            clauseNumber = "Section 21.0",
            anchorId = "#customization",
            title = "App Display Scaling, Atmospheric Material 3 Themes, Haptic Calibration & Global Language/Currency Engine",
            category = LegalCategory.REGULATORY,
            plainSummary = "Covers accessibility and customization settings: App Display Size & Scaling (Compact, Standard, Large, Extra Large), Dynamic Material 3 Atmospheric Color Themes (Emerald, Amber, Cyan, Rose, Noir, Custom Photo), Haptic Calibration, and instant switching across 18+ Global Languages and Currencies.",
            legalText = """
21.1 Accessibility Display Scaling & Atmospheric Theme Engine (`AppDisplaySizeSheet` & `AtmosphericThemeBottomSheet`):
(a) Adaptive Display Scaling (`appDisplayScale`): To ensure universal accessibility across compact phones, foldables, and tablets, users can dynamically scale UI density and typography inside Settings & Privacy;
(b) Atmospheric Theme Presets & Custom Backgrounds: Users can personalize their interface using Emerald, Amber, Cyan, Rose, Noir, or a custom gallery wallpaper (`customBackgroundImageUri`). Custom background images remain strictly local to the user's device and are never uploaded to public servers;
(c) Audio-Haptic Calibration (`HapticHelper`): Users can calibrate tactile vibration feedback across three levels (Off, Standard, Strong).

21.2 Global Multi-Language & Multi-Currency Localization (`GlobalLanguageCurrencyDialog`, `LocalizationHelper`, `CurrencyHelper`):
Localiiiy supports seamless real-time switching across world languages (English, Hindi, Spanish, French, German, Japanese, Korean, Arabic, Portuguese, Russian, Mandarin, Italian, Turkish, Dutch, Indonesian, Vietnamese, Thai, Swahili, etc.) and live currency formatting across global currencies (USD, INR, EUR, GBP, JPY, CAD, AUD, AED, SAR, SGD, CHF, BRL, MXN, ZAR, NGN, KRW). Currency conversions displayed in Marketplace and Wallet are calculated using reference exchange rates in `CurrencyHelper`.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "21.1",
                    subTitle = "Governing Language of Legal Charter",
                    content = "While UI labels and AI translations are provided across multiple languages for accessibility, the English text of this Master Omnibus Legal Agreement serves as the authoritative reference instrument in the event of any linguistic ambiguity."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_22_network_offline_mesh_security",
            clauseNumber = "Section 22.0",
            anchorId = "#network",
            title = "Network Connectivity Resilience, P2P Radar Mesh Offline Cache, Cellular Data Saver & AppSecurityGuard Integrity",
            category = LegalCategory.PRIVACY,
            plainSummary = "Explains how Localiiiy handles offline/low-signal states (`NoInternetDialog`), P2P Radar Mesh Offline Caching, Cellular Data Saver mode, Hotspot management, and `AppSecurityGuard` runtime anti-tamper protection.",
            legalText = """
22.1 Network Connectivity & Offline Resilience (`NetworkConnectivityObserver` & `NoInternetDialog`):
Localiiiy monitors network availability solely to synchronize feeds and notify users gracefully when connectivity drops (`NoInternetDialog`). When offline, cached Room SQLite posts, saved drafts, and local forensic `/safety_logs/` remain accessible on-device.

22.2 P2P Radar Mesh Cache & Cellular Data Saver (`HotspotManager` & `ExoPlayerCacheHelper`):
(a) P2P Radar Mesh Offline Cache: When enabled in Settings & Privacy, non-sensitive local point-of-interest markers are cached on-device for rapid map rendering without redundant network polling;
(b) Cellular Data Saver: Enabling Cellular Data Saver throttles background video pre-fetching and streams 9:16 Clips in standard definition over metered mobile networks to protect user data plans;
(c) One-Tap Cache Purge: Tapping `"Data Saver & Media Cache"` in Settings & Privacy immediately purges temporary media caches.

22.3 Runtime Security & Anti-Tamper Protection (`AppSecurityGuard`):
To protect neighbor privacy and creator monetization ledgers from reverse-engineering, memory hooking, or unauthorized packet injection, `AppSecurityGuard` performs non-invasive integrity checks at launch. Attempting to decompile, inject malicious payloads, or spoof Firebase Authentication tokens will trigger an automatic security lock.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "22.1",
                    subTitle = "Responsible Vulnerability Disclosure",
                    content = "Security researchers who discover potential vulnerabilities are invited to report them confidentially to `support@localiiiy.web.app` under our safe-harbor responsible disclosure protocol."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_23_sandbox_upgrades_community_hub",
            clauseNumber = "Section 23.0",
            anchorId = "#upgrades",
            title = "Advanced Features, Pulse & Market Sandbox Upgrades, Localiiiy Community Hub & Interactive Tutorials",
            category = LegalCategory.POST_POLICY,
            plainSummary = "Covers experimental and advanced modules including Pulse Sandbox Upgrades, Market Sandbox Upgrades, the Localiiiy Community Hub, Interactive Step-by-Step Tutorials (`AppTutorialGuideScreen`), and Gamification Badges.",
            legalText = """
23.1 Advanced Features & Sandbox Upgrades (`AdvancedFeaturesScreen`, `PulseSandboxUpgradesScreen`, `MarketSandboxUpgradesScreen`):
Localiiiy continuously innovates with hyperlocal tools including Neighborhood Price Trends, Interactive Civic Polls, Live Audio/Video Spaces, and AR/Spatial Radar overlays. All experimental and sandbox features are governed by the same strict Zero-Data-Selling, Master Ghost Mode, and Anti-Cyberstalking protections as core modules.

23.2 Localiiiy Community Hub & Gamification Badges (`LocaliiiyCommunityHub` & `GamificationModels`):
Community reputation badges, neighborhood contributor milestones, and tutorial completion awards (`AppTutorialGuideScreen`) reflect positive civic engagement and carry no monetary transfer value outside the application.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "23.1",
                    subTitle = "Feature Evolution & Backward Compatibility",
                    content = "Localiiiy may enhance, refine, or graduate experimental sandbox modules in future releases while preserving user data portability and privacy settings."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_24_logout_scrub_complete_account_oblivion",
            clauseNumber = "Section 24.0",
            anchorId = "#oblivion",
            title = "Session Logout, One-Tap Identity Scrub & Complete Account & Data Oblivion (Permanent Right to Erasure)",
            category = LegalCategory.ACCOUNT_DELETION_OBLIVION,
            plainSummary = "The final stage of the user lifecycle: You have 3 distinct levels of exit control at any second—(1) Standard Logout (preserves your account for next login), (2) One-Tap Session & Identity Scrub (wipes local caches & resets session), and (3) Complete Account & Data Oblivion (permanent, irreversible cryptographic erasure of your Firebase Auth, Firestore cloud data, @username, and local Room SQLite database).",
            legalText = """
24.1 Three-Tier Sovereign Exit & Erasure Architecture:
Localiiiy guarantees that you are never trapped or locked into the Platform. From the moment of Registration through daily use, you hold three distinct, immediately executable exit mechanisms:

(a) Tier 1 — Standard Account Logout (`Profile -> Settings -> Log Out`):
    • Signs out your active Firebase Authentication session (`FirebaseAuthService.signOut()`) and returns the application to the `AuthScreen` gate;
    • Preserves your registered `@username`, Connections graph, published posts/clips, and Creator Wallet balance safely in the cloud for your next sign-in.

(b) Tier 2 — One-Tap Identity Scrub & Local Footprint Purge (`onScrubIdentity` / `resetSessionAndPurge()`):
    • Accessible via the top-right broom icon (`auth_topbar_scrub_button`) on `AuthScreen` or `"Erase Location Footprint"` / `"Data Saver & Media Cache"` in `PrivacySettingsScreen`;
    • Immediately purges all local session caches, ephemeral GPS coordinates, clipboard tokens, and temporary media buffers on the device.

(c) Tier 3 — Complete Account & Data Oblivion (`Settings & Privacy -> Complete Account & Data Oblivion`):
    • Triggered by confirming the `"Complete Data Oblivion"` dialog (`"Purge Data & Delete Account"`) calling `viewModel.deleteAccountAndPurgeData()`;
    • Executes an immediate, irreversible, multi-layer cryptographic purge:
      1. Permanently deletes your Firebase Authentication credentials;
      2. Permanently deletes your Firestore `users/{uid}` profile, releases your `usernames/{username}` handle reservation, and removes your cloud posts, clips, stories, and connection links;
      3. Executes a complete Android Room SQLite database wipe (`AppDatabase.clearAllTables()`), clears all SharedPreferences, and purges ExoPlayer media caches with zero residual database artifacts;
    • Exception for Law Enforcement Forensic Hold: Only accounts under an active, substantiated `CyberstalkingIncidentEntity` criminal hold or pending judicial subpoena are subject to statutory forensic preservation as mandated by Section 9.0 and Section 10.0.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "24.1",
                    subTitle = "Pre-Deletion Creator Wallet Withdrawal Advisory",
                    content = "Because Complete Account & Data Oblivion permanently erases all account ledgers with zero recovery path, creators with settled balances above the $1,000 USD (or local equivalent) threshold should complete their payout withdrawal prior to executing Complete Data Oblivion."
                ),
                LegalSubSection(
                    subNumber = "24.2",
                    subTitle = "Immediate Effect & Zero Dark Patterns",
                    content = "Localiiiy never uses deceptive 'dark patterns', hidden menus, or 30-day waiting traps to delay your account deletion. When you tap 'Purge Data & Delete Account', erasure executes immediately."
                )
            ),
            isHighlighted = true
        )
    )
}
