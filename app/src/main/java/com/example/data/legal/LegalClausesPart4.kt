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
                    content = "Security researchers who discover potential vulnerabilities are invited to report them confidentially to `Localiiiy.Helpdesk@gmail.com` under our safe-harbor responsible disclosure protocol."
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
            plainSummary = "You have 3 distinct levels of exit control at any second—(1) Standard Logout (preserves your account for next login), (2) One-Tap Session & Identity Scrub (wipes local caches & resets session), and (3) Complete Account & Data Oblivion (permanent, irreversible cryptographic erasure of your Firebase Auth, Firestore cloud data, @username, and local Room SQLite database via In-App or https://localiiiy.web.app/delete-account.html).",
            legalText = """
24.1 Three-Tier Sovereign Exit & Erasure Architecture:
Localiiiy guarantees that you are never trapped or locked into the Platform. From the moment of Registration through daily use, you hold three distinct, immediately executable exit mechanisms:

(a) Tier 1 — Standard Account Logout (`Profile -> Settings -> Log Out`):
    • Signs out your active Firebase Authentication session (`FirebaseAuthService.signOut()`) and returns the application to the `AuthScreen` gate;
    • Preserves your registered `@username`, Connections graph, published posts/clips, and Creator Wallet balance safely in the cloud for your next sign-in.

(b) Tier 2 — One-Tap Identity Scrub & Local Footprint Purge (`onScrubIdentity` / `resetSessionAndPurge()`):
    • Accessible via the top-right broom icon (`auth_topbar_scrub_button`) on `AuthScreen` or `"Erase Location Footprint"` / `"Data Saver & Media Cache"` in `PrivacySettingsScreen`;
    • Immediately purges all local session caches, ephemeral GPS coordinates, clipboard tokens, and temporary media buffers on the device.

(c) Tier 3 — Complete Account & Data Oblivion (`Settings & Privacy -> Complete Account & Data Oblivion` or `https://localiiiy.web.app/delete-account.html`):
    • Triggered by confirming the `"Complete Data Oblivion"` dialog (`"Purge Data & Delete Account"`) calling `viewModel.deleteAccountAndPurgeData()`;
    • Executes an immediate, irreversible, multi-layer cryptographic purge:
      1. Permanently deletes your Firebase Authentication credentials;
      2. Permanently deletes your Firestore `users/{uid}` profile, releases your `usernames/{username}` handle reservation, purges `radar_beacons/{uid}`, and removes your cloud posts, clips, stories, and connection links;
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
        ),

        LegalClause(
            id = "clause_25_developer_owner_non_liability_safe_harbor",
            clauseNumber = "Section 25.0",
            anchorId = "#safeharbor",
            title = "Absolute Developer, Founder & Owner Non-Liability Charter, User Choice Sovereignty (Radar/Location ON-OFF) & Cybercrime Forensic Self-Service Indemnity",
            category = LegalCategory.DEVELOPER_SAFE_HARBOR,
            plainSummary = "CRITICAL LEGAL COVENANT: The Developer, Creator, Founder, and Owner of Localiiiy is NOT responsible or liable for anything arising from user actions, offline meetups, cybercrime, financial fraud, or data exposure. Localiiiy has given 100% of privacy options directly to the user's own choice (Radar ON/OFF, Location ON/OFF, Master Ghost Mode ON/OFF, 1km–500km fuzzing, Hide Profile/Phone/Email toggles, Safe-Haven OTP Escrow) AND provides downloadable SHA-256 forensic logs (`/safety_logs/` & `audit_logs`) so users can report any cybercrime or fraud directly to police authorities themselves.",
            legalText = """
25.1 Absolute Developer, Creator, Founder & Owner Non-Liability & Immunity:
By installing, accessing, or registering on Localiiiy (across the Android APK `com.aistudio.localiiiy.live` or the Web App `https://localiiiy.web.app`), every user expressly acknowledges, stipulates, and covenants that the Developer, Creator, Architect, Founder, and Owner of Localiiiy shall NEVER be held civilly, criminally, financially, tortiously, or vicariously responsible or liable for any loss, damage, cybercrime, financial fraud, marketplace scam, identity dispute, physical altercation, harassment, stalking incident, or unauthorized disclosure of user-shared information.

25.2 Complete User Choice Sovereignty Over All Privacy & Visibility Switches:
The Developer and Owner of Localiiiy has engineered and placed comprehensive, 100% free self-protection controls directly into the hands of every user for their own voluntary choice:
(a) Radar Transceiver ON / OFF Power Switch: Users can turn the Tactical Radar completely OFF at any time;
(b) Master Ghost Mode ON / OFF Switch: Users can browse in 100% invisible Stealth Observer Mode (`isGhostMode = true`), which immediately purges any active beacon from `radar_beacons/{uid}`;
(c) Device Location Permission ON / OFF Choice: Users can deny or revoke Android/Browser GPS permissions at any second while still enjoying the app;
(d) 1 km to 500 km GPS Obfuscation & `roundCoord3` (~110m) Rounding: Users can blur their spatial presence up to 500 km so their exact street or home is never revealed;
(e) Granular Identity Concealment Toggles: Users can hide their Profile Picture, Posts, Clips, Email Address, Mobile Phone Number, and Street Address with 1 tap;
(f) Safe-Haven Police Precinct 6-Digit Handshake OTP Escrow: Users are given cryptographic escrow and police-precinct CCTV meetup tools to prevent peer-to-peer trade fraud.
Consequently, if any user chooses to turn Radar ON, turn Location ON, disable Ghost Mode, reveal personal details, meet a stranger offline, or bypass Safe-Haven Escrow, that user acts 100% at their own sole risk and personal responsibility.

25.3 Availability of Tamper-Evident Forensic Logs (`/safety_logs/` & `audit_logs`) for Cybercrime & Fraud Reporting:
In the event that any user experiences suspected cybercrime, online fraud, harassment, or scam attempts by another user:
(a) Self-Service Forensic Evidence Access: Localiiiy provides built-in, tamper-evident SHA-256 Activity Audit Logs (`UserActivityLogScreen` / `audit_logs`) and ISO/IEC 27037 Forensic Safety Logs (`/safety_logs/` in `CyberstalkingSafetyScreen`) right inside the user's account;
(b) Direct Law Enforcement Filing by User: The affected user can immediately download/export these cryptographically sealed logs (containing timestamps, offender `@username` handles, interaction hashes, and SHA-256 certificates) and submit them directly to their local Cyber Crime Police Cell, magistrate, or law enforcement agency;
(c) Statutory Intermediary Safe-Harbor: Under global intermediary liability statutes (including Section 79 of the Indian Information Technology Act, 2000, 47 U.S.C. § 230, EU Digital Services Act Articles 4–6, and global civil law), the Developer and Owner of Localiiiy is a neutral technology provider and bears ZERO responsibility or liability for third-party user conduct, cybercrime, or fraud.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "25.1",
                    subTitle = "User Indemnification & Hold-Harmless Obligation",
                    content = "You agree to fully indemnify, defend, and hold harmless the Developer and Owner of Localiiiy from and against any claims, liabilities, damages, losses, or legal fees arising out of your privacy toggle choices, your published content, your marketplace trades, or your interactions with other users."
                ),
                LegalSubSection(
                    subNumber = "25.2",
                    subTitle = "Zero Vicarious Liability for Peer-to-Peer Conduct",
                    content = "Localiiiy does not employ, supervise, or vouch for individual neighbors on the Platform. Providing software toggles (Radar ON/OFF, Ghost Mode, Audit Logs) discharges all duty of care of the Developer."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_26_strict_18_plus_radar_restriction",
            clauseNumber = "Section 26.0",
            anchorId = "#radar18plus",
            title = "Strict 18+ Adult Age Restriction on Tactical Radar & Complete Prohibition Below 18 Years of Age",
            category = LegalCategory.RADAR_18_PLUS_GATE,
            plainSummary = "MANDATORY MINOR PROTECTION RULE: The Tactical Radar option (`Radar` tab, Live Space Radar, Tactical Radar HUD, live proximity beacon broadcasting, and nearby stranger radar discovery) is TOTALLY RESTRICTED and PROHIBITED for anyone below 18 years of age (`calculatedAge < 18`). Only verified adults aged 18+ may use Radar.",
            legalText = """
26.1 Complete Prohibition of Tactical Radar for Users Under 18 Years of Age:
To ensure uncompromising child and adolescent safety across both the Android Application (`com.aistudio.localiiiy.live`) and Web Application (`https://localiiiy.web.app`), the Tactical Radar subsystem (`LiveRadarScreen`, `TacticalRadarHUD`, `radar_beacons/{uid}`, Active Sonar Sweep, and Nearby Stranger Radar Discovery) is strictly classified as an **18+ Adult-Only Feature**:
(a) Total Lockout Below Age 18 (`calculatedAge < 18`): Any user whose registered Date of Birth indicates an age under 18 years (including teen users aged 13 to 17) is TOTALLY RESTRICTED and blocked from enabling, broadcasting on, or scanning strangers via Tactical Radar;
(b) Permanent Mandatory Ghost Mode for Minors (13–17): Accounts belonging to users aged 13 to 17 are permanently locked in Master Ghost Mode (`isGhostMode = true`, `showInNeighborhood = false`), ensuring zero spatial coordinates or radar blips are ever transmitted;
(c) Zero Stranger DM & Escrow Exposure for Minors: Users under 18 are additionally restricted from entering adult Safe-Haven Escrow contracts or receiving unsolicited direct messages from unconnected adult accounts.

26.2 Strict 18+ Adult Verification & Anti-Circumvention Covenant:
(a) Mandatory Date-of-Birth Truthfulness: Every registrant must enter their true, legal Date of Birth during Section 10 of the Registration flow and attest to the age verification covenant;
(b) Liability for Age Misrepresentation: Any minor or parent/guardian who intentionally falsifies a Date of Birth to circumvent the Under-18 Tactical Radar restriction commits a fraudulent breach of this Charter, resulting in immediate permanent account termination and absolving the Developer and Owner of Localiiiy of any and all liability.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "26.1",
                    subTitle = "13–17 Safe Creative Access (Zero Radar)",
                    content = "While Tactical Radar is totally restricted below 18 years of age, teens aged 13–17 may safely view family-friendly creative Clips, educational 4K Studio videos, and neighborhood Civic advisories in permanent Ghost Mode."
                ),
                LegalSubSection(
                    subNumber = "26.2",
                    subTitle = "Parental Supervision & Instant Erasure",
                    content = "Parents or legal guardians may contact `Localiiiy.Helpdesk@gmail.com` or use `https://localiiiy.web.app/delete-account.html` at any time to immediately erase an underage account."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_27_lcc_cryptographic_hash_protocol",
            clauseNumber = "Section 27.0",
            anchorId = "#lcchash",
            title = "Localiiiy Creator Copyright (LCC) Cryptographic Hash Value Protocol (SHA-256 + 64-Bit DCT pHash Formula, Certificate Ledger & Remix Chain)",
            category = LegalCategory.IP_RIGHTS,
            plainSummary = "Deep technical specification of Localiiiy Creator Copyright (LCC): Every uploaded photo, 9:16 Clip, and 4K Studio video is sealed with a 256-bit SHA-256 cryptographic hash digest (`sha256:...`), a 64-bit DCT perceptual/acoustic hash (`phash_`), and a public certificate ID (`LCC-XXXXXX-2026`). Explains exact hash calculation, parent-child remix hash chaining, and automated anti-piracy enforcement.",
            legalText = """
27.1 Purpose & Legal Standing of the Localiiiy Creator Copyright (LCC) Hash Engine:
The Localiiiy Creator Copyright ("LCC") system is a decentralized, cryptographic authorship-attribution and anti-piracy ledger built directly into the Android and Web upload pipelines (`MediaFingerprintEngine`, `firestore_copyright_schema.json`). Every original work published on Localiiiy is cryptographically fingerprinted at the millisecond of creation to establish tamper-evident proof of prior authorship.

27.2 Exact Cryptographic Hash Value Formula & Dual-Hash Architecture:
When a creator selects media for a Pulse Post, 9:16 Clip, 4K Studio Film, or Market Showcase, Localiiiy computes two complementary cryptographic hash values before cloud commit:
(a) Layer 1 — Deterministic 256-Bit SHA-256 Payload & Ownership Hash (`LCC_SHA256_DIGEST`):
    The client and server compute the FIPS 180-4 compliant SHA-256 digest over the canonical pipe-delimited byte buffer:
    `LCC_SHA256_DIGEST = SHA-256( Creator_UID + "|" + Normalized_@Username + "|" + Media_Binary_Byte_Stream + "|" + Perceptual_pHash_Hex + "|" + Creation_UTC_Epoch_Millis + "|" + Licensing_Mode_Flag )`
    • Output Format: A 64-character lowercase hexadecimal string prefixed with `sha256:` (e.g., `sha256:9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08`);
    • Certificate Code Derivation (`LCC-XXXXXX-2026`): The first 6 alphanumeric characters (24 bits converted to uppercase Base-36) of `LCC_SHA256_DIGEST` are formatted into the human-readable badge `LCC-XXXXXX-2026` displayed on the Studio and Clip player overlay;
(b) Layer 2 — 64-Bit Discrete Cosine Transform (DCT) Perceptual & Acoustic Hash (`phash_`):
    Because malicious re-uploaders often re-compress, crop, add borders, flip horizontally, or screen-record stolen videos (which changes raw file bytes), `MediaFingerprintEngine.computePerceptualHash()` extracts normalized luminance matrices across keyframes and audio spectral peaks to generate a 16-character perceptual signature (`phash_<64-bit-hex>`). Any subsequent upload whose `phash_` Hamming distance is below the infringement threshold (`HammingDistance <= 6`) against a protected `LICENSE_ALL_RIGHTS` asset is automatically flagged as a derivative duplicate.

27.3 Parent-Child Hash Chaining for Authorized Remixes & Duets:
When an original creator selects `"Localiiiy Creative Commons (LCC)"` (`reusePermitted = true`) and enables `audioReuseAllowed` or `remixReactionAllowed`:
(a) Every downstream Remix or Duet clip generates its own child `LCC_SHA256_DIGEST` while embedding an immutable pointer to `parentLccHash` and `parentCreatorUid`;
(b) This cryptographic hash chain ensures the original creator receives permanent visual credit on the remix player and automatic royalty attribution in the Creator Wallet.

27.4 Hash Registry Verification, Automated Revenue Rerouting & Safe-Harbor:
(a) Public Hash Inspection: Anyone can inspect a video's `LCC-XXXXXX-2026` certificate and `SHA-256` digest in the Clip/Studio info sheet to verify the true original creator and upload timestamp;
(b) 100% Revenue Rerouting on Hash Match: If an unauthorized user re-uploads media matching a registered `LCC_SHA256_DIGEST` or `phash_` without license, 100% of ad revenue and tips accrued during the 7-day quarantine window are automatically rerouted to the verified original `LCC` hash owner upon strike confirmation.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "27.1",
                    subTitle = "Tamper-Evident Evidentiary Value of LCC Hashes",
                    content = "Because SHA-256 is a one-way collision-resistant cryptographic function, possessing the original media file and account UID that hashes to the earliest recorded `LCC_SHA256_DIGEST` serves as mathematical proof of priority in copyright disputes."
                ),
                LegalSubSection(
                    subNumber = "27.2",
                    subTitle = "Prohibition on Hash Spoofing & Watermark Stripping",
                    content = "Attempting to strip, obscure, crop out, or forge an `LCC-XXXXXX-2026` visual badge or manipulate upload metadata is a severe violation subject to immediate Strike 3 permanent suspension."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_28_civic_tools_15_categories_governance",
            clauseNumber = "Section 28.0",
            anchorId = "#civictools",
            title = "15-Category Hyperlocal Civic Tools Governance, Ascending Proximity (3km–50km), Voluntary Blood Donor Shield & Anti-Hoax SOS Policy",
            category = LegalCategory.CIVIC_TOOLS_COMMUNITY,
            plainSummary = "Governs the 15 Hyperlocal Civic Tools inside the Pulse tab (Blood SOS, Lost & Found, Civic Fix, Ride Pool, Food Rescue, Skill Swap, Flash Deal, Rent Tool, Play Match, Work Cafe, Yard Sale, Safe Walk, Street Art, Plant Swap, and Elder Care) sorted by ascending proximity (3 KM to 50 KM). Strictly prohibits fake SOS or emergency hoaxes.",
            legalText = """
28.1 Scope of the 15 Hyperlocal Civic Tools Channels:
Localiiiy's Civic Tools tab empowers verified neighbors to broadcast and respond to structured community needs across fifteen (15) dedicated categories sorted strictly by ascending distance (nearest-first from 3 KM, 5 KM, 10 KM, 15 KM, 20 KM, 30 KM, up to 50 KM):
(1) Blood SOS, (2) Lost & Found, (3) Civic Fix (potholes, street lights, water/power outages), (4) Ride Pool, (5) Food Rescue, (6) Skill Swap, (7) Flash Deal, (8) Rent Tool, (9) Play Match, (10) Work Cafe, (11) Yard Sale, (12) Safe Walk, (13) Street Art, (14) Plant Swap, and (15) Elder Care.

28.2 Voluntary Blood Donor & Elder Care Responder Protection:
Participation in Blood SOS or Elder Care assistance is 100% voluntary. Localiiiy is NOT a medical blood bank, hospital, or licensed healthcare provider. Users must verify all medical blood typing, cross-matching, and clinical screening solely through licensed hospitals and certified medical professionals.

28.3 Zero-Tolerance Prohibition on False SOS & Civic Hoaxes:
Publishing a fake Blood SOS, fabricated emergency alert, malicious false infrastructure hazard, or predatory Safe Walk/Ride Pool lure is a zero-tolerance offense resulting in immediate permanent account and hardware banishment and referral to law enforcement. The Developer and Owner of Localiiiy is not responsible for the accuracy or outcome of peer-to-peer civic broadcasts.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "28.1",
                    subTitle = "Ride Pool, Safe Walk & Food Rescue Disclaimer",
                    content = "Neighbors participating in peer-to-peer Ride Pools, Safe Walks, or Food Rescue exchanges do so voluntarily at their own risk and must verify identities, food safety, and vehicle insurance independently."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_29_ai_dialect_translator_creative_ai",
            clauseNumber = "Section 29.0",
            anchorId = "#aitranslator",
            title = "AI Multi-Lingual Local Dialect Translator & AI Studio Hook Generator Governance (Ephemeral Inference & Zero Model Training)",
            category = LegalCategory.CIVIC_TOOLS_COMMUNITY,
            plainSummary = "Covers the AI Multi-Lingual Local Translator in Civic Tools/Pulse and the AI Hook & Shot Script Generator in 4K Studio. Prompts are processed ephemerally for translation/scripting only and are never sold or used to train external advertising models.",
            legalText = """
29.1 Ephemeral Cloud AI Processing (`Firebase AI Logic` / Assistive Translation & Scripting):
Localiiiy integrates assistive AI modules—including the AI Multi-Lingual Local Dialect Translator (supporting regional and global languages with voice dictation) and the 4K Studio AI Hook & Shot Script Generator:
(a) Ephemeral Inference Only: Text or voice dictation submitted to the AI Translator or Script Generator is processed solely to return the requested translation or creative outline;
(b) Prohibition on Harmful Prompts: Users must not use AI tools to generate hate speech, fraudulent civic notices, medical misinformation, or defamatory content;
(c) Machine Translation Accuracy Disclaimer: Automated dialect translations are provided for neighborhood convenience and may contain linguistic nuances or inaccuracies.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "29.1",
                    subTitle = "Creator Responsibility for AI-Assisted Scripts",
                    content = "Creators who use the AI Hook Generator remain 100% responsible for reviewing and ensuring the factual and legal compliance of the final video they record and publish."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_30_local_community_hub_5_subtabs",
            clauseNumber = "Section 30.0",
            anchorId = "#communityhub",
            title = "Localiiiy Community Hub: 5 Sub-Tabs (Feed & Alerts, Live Spatial Voice Rooms, Polls & Votes, Resource Vault & VIP Lounge)",
            category = LegalCategory.CIVIC_TOOLS_COMMUNITY,
            plainSummary = "Governs the Local Community Hub inside Pulse and its 5 specialized sub-tabs: Feed & Alerts, Live Voice (Spatial Audio Rooms), Polls & Votes (`1 Verified Neighbor = 1 Vote`), Resource Vault, and VIP Lounge (100+ Sparks).",
            legalText = """
30.1 Five-Tab Neighborhood Micro-Community Architecture:
Verified neighbors may join or create hyperlocal clubs (e.g., Urban Gardeners, Local Dog Owners, Neighborhood Watch, Indie Creators) operating across five structured sub-tabs:
(a) Feed & Alerts: Community-specific updates with optional attachments of Market items, Studio episodes, or Clips;
(b) Live Voice (Spatial Audio Rooms): Real-time neighborhood voice rooms streamed ephemerally only while a user explicitly unmutes their microphone;
(c) Polls & Votes (`1 Verified Neighbor = 1 Vote`): Democratic neighborhood polls enforcing cryptographic single-vote deduplication per verified account while keeping individual ballot choices confidential;
(d) Resource Vault: Shared neighborhood directories, emergency helpline numbers, and community tool-lending registries;
(e) VIP Lounge: Merit-based community recognition channel unlocked via neighborhood Sparks (`100+ Sparks`).
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "30.1",
                    subTitle = "Live Voice Room Moderation & Mute Sovereignty",
                    content = "Participants in Spatial Audio Rooms must maintain civil discourse. Any user can mute their microphone, leave a room with 1 tap, or block/report an abusive speaker immediately."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_31_tactical_radar_2_0_hud_controls",
            clauseNumber = "Section 31.0",
            anchorId = "#tacticalradar2",
            title = "Tactical Radar 2.0 HUD: Dual Control Strips, 2D/3D Pitch, Active Sonar, +10km RF Boost, 5 HUD Themes & Kalman Telemetry",
            category = LegalCategory.LOCATION,
            plainSummary = "Covers Tactical Radar 2.0 (strictly 18+ Adults Only): Row 1 & Row 2 tactical control strips, 2D Top-Down vs. 3D Pitch 45°, Active Sonar shockwave, +10km Range Boost, 5 HUD palettes (PHOS, GOLD, CYAN, CRIM, AURA), and Circular/Street Map/Grid views.",
            legalText = """
31.1 Tactical Radar 2.0 Operational Controls (18+ Verified Adults Only):
For verified users aged 18 and older who voluntarily switch Radar ON, Tactical Radar 2.0 provides transparent hardware/software telemetry controls:
(a) Row 1 Transceiver Strip: 2D Top-Down / 3D Pitch 45° perspective toggle, Active Sonar Shockwave ping, +10km Range Boost, Privacy Shield quick-cloak, Broadcast Signal toggle, Night HUD dimmer, and Full Power vs. Eco Scan battery saver;
(b) Row 2 Telemetry Strip: Compass Heading lock, Sweep Interval selector (1.5s to 10s), Street Map Overlay, Alert Pins toggle, All Residents vs. Verified Only filter, and Kalman Filter coordinate smoothing;
(c) Five Custom HUD Palettes: Phosphor Green (`PHOS`), Amber Gold (`GOLD`), Cyber Cyan (`CYAN`), Crimson Night (`CRIM`), and Violet Aura (`AURA`).
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "31.1",
                    subTitle = "Instant Radar Standby & Ghost Override",
                    content = "Tapping the 'Turn OFF' power button below the radar scope or toggling Ghost Mode immediately halts all radar beacon transmissions regardless of active HUD presets."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_32_bidirectional_web_android_sync",
            clauseNumber = "Section 32.0",
            anchorId = "#websync",
            title = "Bidirectional Web App (`localiiiy.web.app`) ↔ Android APK (`com.aistudio.localiiiy.live`) Real-Time Cloud Sync & Unified `@username`/Email Auth",
            category = LegalCategory.REGULATORY,
            plainSummary = "Your Localiiiy account is 100% unified across the Android App and https://localiiiy.web.app. Register on either platform and log in seamlessly on both using either your `@username` or Email Address + Password.",
            legalText = """
32.1 Single Unified Firebase Identity & Firestore Synchronization:
Localiiiy operates a unified production backend across the Android application (`com.aistudio.localiiiy.live`) and the official Progressive Web Application (`https://localiiiy.web.app` / `https://localiiiy.firebaseapp.com`):
(a) Bidirectional `@username` & Email Authentication: Users registered on Android can sign in on the Web App (and vice versa) using either their unique `@username` handle (resolved via `usernames/{handle}`) or their registered Email Address and password, or via Google Sign-In;
(b) Real-Time Privacy & Ledger Parity: Updating your Ghost Mode state, blocked users list, Connections graph, Sparks balance, or Creator Wallet on either platform synchronizes immediately across all active sessions.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "32.1",
                    subTitle = "Official Store Download Banner & Zero Unofficial Mirrors",
                    content = "Localiiiy distributes its mobile application exclusively through the official Google Play Store (`com.aistudio.localiiiy.live`) and Apple App Store (Available Soon). Users are warned never to download modified APKs from unofficial third-party mirror websites."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_33_guest_spectator_read_only_mode",
            clauseNumber = "Section 33.0",
            anchorId = "#spectator",
            title = "Guest Spectator (Read-Only) Mode, Zero-GPS Footprint Browsing & Verified Neighbor Interaction Gate",
            category = LegalCategory.PRIVACY,
            plainSummary = "Unregistered visitors on Android or Web can explore public feeds in Guest Spectator Mode with zero GPS footprint, while all write/social actions (Liking, Remarks, Posting, Radar, Civic broadcasts, Escrow, Connecting) require signing in to a verified account.",
            legalText = """
33.1 Zero-Footprint Read-Only Exploration:
Visitors who select `"Enter as Ghost Spectator • Zero GPS Footprint"` on Android or browse `https://localiiiy.web.app` without logging in are placed in Guest Spectator Mode:
(a) Zero Telemetry Harvesting: No persistent user document or radar beacon is created for Guest Spectators;
(b) Mandatory Authentication Gate for Interactive Features: Attempting to like, comment/remark, publish a post/clip, broadcast a Civic alert, enter a Community Hub, initiate a Safe-Haven Escrow handshake, or Connect with a neighbor displays a transparent prompt requiring Sign-In or 11-Section Verified Registration.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "33.1",
                    subTitle = "Anti-Scraping & Rate-Limit Protection in Guest Mode",
                    content = "Automated scraping, bulk downloading, or bot crawling of public feeds via Guest Spectator Mode is strictly prohibited and blocked by Firebase App Check."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_34_panic_cloak_calculator_rapid_approach",
            clauseNumber = "Section 34.0",
            anchorId = "#paniccloak",
            title = "Panic Cloak Scientific Calculator Decoy (`7777` PIN / `C-C-C`), Rapid Approach Velocity Auto-Cloak & Domestic Safety Shield",
            category = LegalCategory.CYBERSTALKING,
            plainSummary = "Protects users in physical emergencies or domestic coercion: Rapid Approach Velocity Detection auto-cloaks your beacon if someone approaches faster than 25 km/h within 250m, and Panic Cloak disguises the entire app as a functional Calculator unlocked via `7777` or `C-C-C`.",
            legalText = """
34.1 Rapid Approach Velocity Auto-Cloak Shield:
When edge telemetry detects another active node closing physical distance toward your beacon at a velocity exceeding 25 km/h within a 250-meter safety perimeter, Localiiiy automatically transitions your account into Master Ghost Mode, purges `radar_beacons/{uid}`, and surfaces 1-tap Safe-Haven Police Precinct routing.

34.2 Panic Cloak Decoy Calculator (`7777` PIN / Triple-Clear Exit):
Users facing physical shoulder-surfing or domestic coercion may trigger Panic Cloak at any second from Space/Settings to transform the entire application into a working standard Calculator utility while purging live radar visibility in the background. Exiting the decoy requires entering the user's 4-digit security PIN (default `7777`) or tapping `C` three times (`C -> C -> C`).
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "34.1",
                    subTitle = "Best-Effort Assistive Safety Tool Disclaimer",
                    content = "Rapid Approach Velocity alerts and Panic Cloak are assistive software shields provided on a best-effort basis and do not guarantee physical immunity; users in danger must always contact emergency police services immediately."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_35_open_source_github_zero_secret_hygiene",
            clauseNumber = "Section 35.0",
            anchorId = "#githubsecurity",
            title = "Public GitHub Repository Security, Zero-Secret Exposure Policy & Anti-Cloning Intellectual Property Protection",
            category = LegalCategory.IP_RIGHTS,
            plainSummary = "Localiiiy's public repository (`github.com/Localiiiy/Localiiiy`) enforces strict zero-secret hygiene (no hardcoded private keys or production secrets in source code) while protecting the Localiiiy brand, trademarks, and original codebase against malicious commercial cloning.",
            legalText = """
35.1 Zero-Secret Source Code Hygiene:
All API keys and environment credentials in Localiiiy are injected securely at build time via `BuildConfig` and `.env` (excluded via `.gitignore`), ensuring zero private signing keys or secret credentials are exposed in public GitHub commits.

35.2 Anti-Cloning & Trademark Protection:
While Localiiiy publishes transparency documentation and web/policy assets on GitHub (`https://github.com/Localiiiy/Localiiiy`), the `"Localiiiy"` name, custom adaptive launcher logos, trade dress, and proprietary ecosystem branding remain the exclusive intellectual property of the Owner and Developer of Localiiiy. Publishing unauthorized clone apps or phishing websites impersonating Localiiiy is strictly prohibited.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "35.1",
                    subTitle = "Security Researcher Safe-Harbor Contact",
                    content = "Any security reports or repository inquiries must be sent exclusively to `Localiiiy.Helpdesk@gmail.com`."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_36_official_helpdesk_grievance_sla",
            clauseNumber = "Section 36.0",
            anchorId = "#helpdesk",
            title = "24/7 Official Helpdesk, Statutory Grievance Officer Redressal SLA & Exclusive Contact (`Localiiiy.Helpdesk@gmail.com`)",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "All official support, privacy requests, copyright notices, escrow disputes, and statutory grievances are handled exclusively through our single official email: `Localiiiy.Helpdesk@gmail.com` (and self-service account deletion at `https://localiiiy.web.app/delete-account.html`).",
            legalText = """
36.1 Exclusive Official Contact Channel (`Localiiiy.Helpdesk@gmail.com`):
For absolute clarity, security, and anti-phishing protection, Localiiiy operates a single, unified official email address for all User Support, Data Protection Officer (DPO) inquiries, DMCA/LCC Copyright notifications, Safe-Haven Escrow disputes, and Statutory Grievance Officer escalations:
• **Official Helpdesk, Privacy & Grievance Email:** `Localiiiy.Helpdesk@gmail.com`
• **Official Web Application:** `https://localiiiy.web.app`
• **Official Self-Service Account Deletion Portal:** `https://localiiiy.web.app/delete-account.html`
• **Official Google Play Store Listing:** `https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live` (Apple App Store: Available Soon)

36.2 Statutory Service Level Agreement (SLA) Timelines:
(a) Emergency Safety, CSAM/NCII & Cyberstalking Reports: Triaged within 15 minutes to 4 hours with immediate automated quarantine;
(b) Formal Grievance Acknowledgment: Within 24 hours of receipt at `Localiiiy.Helpdesk@gmail.com`;
(c) Complete Grievance Resolution: Within 15 calendar days in full compliance with the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021, India DPDP Act 2023, GDPR, and Google Play Developer Policies.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "36.1",
                    subTitle = "Anti-Phishing Communication Notice",
                    content = "Localiiiy will only communicate official support decisions via `Localiiiy.Helpdesk@gmail.com` or verified in-app system notifications, and will NEVER ask for your account password or 6-digit Handshake OTP."
                )
            ),
            isHighlighted = true
        )
    )
}
