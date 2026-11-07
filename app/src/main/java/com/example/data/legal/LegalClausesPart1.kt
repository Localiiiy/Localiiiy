package com.example.data.legal

object LegalClausesPart1 {

    val clauses: List<LegalClause> = listOf(
        LegalClause(
            id = "clause_1_location_sovereignty",
            clauseNumber = "Section 1.0",
            anchorId = "#location",
            title = "Voluntary Hyperlocation, Geofenced Hotspots, 1km–500km Obfuscation & Proximity Radar Rights",
            category = LegalCategory.LOCATION,
            plainSummary = "Your physical location and geofence alerts are 100% voluntary. You choose when to broadcast on Space Radar, when to engage Master Ghost Mode, how to calibrate your 1 km to 500 km GPS obfuscation slider, and when to turn radar off. If location is disabled ('Off-Grid'), you still enjoy full feeds curated by smart algorithms without any tracking.",
            legalText = """
1.1 Total Voluntary Location Sovereignty & Elective Telemetry:
Location sharing, Live Proximity Space Radar, Tactical Radar HUD, and geofenced hotspot alerts on Localiiiy ("Platform", "Application", or "Service") are 100% elective, revocable, and voluntary. At no point is continuous background location tracking or foreground GPS broadcasting mandatory for general application browsing, content viewing, or community participation.

1.2 Master Ghost Mode, Radar HUD & Granular Obfuscation Controls (1 km to 500 km):
Users maintain absolute, real-time operational autonomy over their spatial presence through the dedicated hardware and software controls below:
(a) Master Ghost & Passive Stealth Mode: A 100% free, zero-paywall privacy shield accessible at registration and in Settings & Privacy. When active, your radar blip is completely cloaked, reel/clip views are anonymous, story view receipts are suppressed, and zero coordinates are broadcasted to nearby users;
(b) Continuous GPS Obfuscation Slider (1 km to 500 km): Users can dynamically adjust their spatial precision radius across six graduated tiers: Exact Street (~500m–2 km), Neighborhood Level (2–10 km), City & District (10–50 km), Metropolitan Zone (50–150 km), State / Province (150–350 km), and Max Privacy Mask (350–500 km);
(c) Dedicated Hide Toggles: Users may independently toggle "Hide Location Completely", "Hide in Neighborhood", and "Hide Street Address & Physical Area" at any second;
(d) Radar Invisibility Schedule: Automated nighttime stealth scheduling (default 10:00 PM to 7:00 AM local time) automatically transitions the account into Ghost Mode during rest hours;
(e) Instant Footprint Erasure: The "Erase Location Footprint" action in Settings & Privacy purges all locally cached GPS coordinates and ephemeral session telemetry with immediate cryptographic and haptic confirmation.

1.3 Off-Grid Algorithmic Browsing & Guest Spectator Mode:
When location permissions are denied at the Android OS level, disabled in-app, or when browsing via Ephemeral Guest Spectator Mode ("Off-Grid"):
(a) Zero Coordinate Indexing: The Application will NOT index, store, triangulate, or transmit your device GPS, Wi-Fi SSID, Bluetooth beacon, or cellular tower coordinates;
(b) Generalized Discovery Fallback: The Pulse Feed, Explore Constellations, Clips 9:16 stream, Neighborhood Marketplace, and Studio 4K videos remain fully accessible via generalized interest-constellation and Home Community Anchor curation without location fingerprinting.

1.4 On-Device Haversine Processing & Ephemeral Session Storage:
Relative distance badges (e.g., "0.4 km away", "Same Block", "Neighbor") are computed using spherical Haversine trigonometry against ephemeral session coordinates or obfuscated grid centroids. Raw device coordinates are never auctioned, sold, or retained in historical movement trails.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "1.1",
                    subTitle = "Elective Foreground Permission Architecture",
                    content = "Localiiiy requests Android location permissions (ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION) strictly at the moment a user voluntarily activates the Live Space Radar, Tactical Radar HUD, or Nearby proximity filter. Denying OS permissions never locks the user out of the app."
                ),
                LegalSubSection(
                    subNumber = "1.2",
                    subTitle = "1 km to 500 km Spatial Masking Engine",
                    content = "Before any coordinate leaves the client device for proximity matching, the LocationHelper engine applies deterministic radial jitter corresponding to the user's selected km precision slider, ensuring third parties can never triangulate a user's exact residential doorway."
                ),
                LegalSubSection(
                    subNumber = "1.3",
                    subTitle = "Favorite Proximity & Connected Neighbor Alerts",
                    content = "Mutual Connections who explicitly opt into Favorite Proximity notifications receive proximity pings only when both Connected parties have disabled Ghost Mode and enabled mutual radar visibility."
                ),
                LegalSubSection(
                    subNumber = "1.4",
                    subTitle = "Ephemeral Ghost Spectator Sessions",
                    content = "Users entering via the Ghost Spectator banner on the AuthScreen browse with zero persistent identity token and zero GPS footprint, subject to read-only interaction gating until verified registration."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_2_data_confidentiality",
            clauseNumber = "Section 2.0",
            anchorId = "#privacy",
            title = "Zero Data Brokering, Confidentiality, Granular Hide/Show Controls & Universal Privacy Sovereignty",
            category = LegalCategory.PRIVACY,
            plainSummary = "We never sell, rent, or broker your personal data. You control every visibility toggle—Profile Picture, Posts, Clips, Email, Mobile Number, Street Address, View Receipts, and Direct Calls are never compulsory to expose and can be hidden instantly.",
            legalText = """
2.1 Absolute Zero Data Selling & Anti-Brokering Covenant:
The developers, architects, owners, and operators of Localiiiy unconditionally covenant and warrant that user personal data, identity records, direct message transcripts, audio/video call metadata, media archives, connection graphs, and behavioral telemetry are NEVER sold, rented, auctioned, leased, or disclosed to third-party data brokers, surveillance aggregators, external ad-targeting exchanges, or commercial telemarketers.

2.2 Granular Hide/Show Identity & Content Sovereignty:
Privacy on Localiiiy is modular, non-compulsory, and user-sovereign. Every registered user retains instant, unilateral control in Settings & Privacy to toggle:
(a) Hide Profile Picture: Replaces your public avatar with a generic anonymous silhouette for all unconnected users;
(b) Hide Posts: Restricts your Pulse Feed posts, photos, and text dispatches strictly to mutual Connections only;
(c) Hide Clips: Keeps your 9:16 short-form video clips invisible to public neighborhood discovery feeds;
(d) Hide Email Address: Conceals your registered email address on your Profile Identity Card and QR Space Card;
(e) Hide Mobile Number: Conceals your verified phone number from public profile cards, search indexing, and contact discovery;
(f) Hide Street Address & Physical Area: Strips street names, building numbers, and landmark pins from all published artifacts;
(g) Send View Receipts Toggle: Allows you to disable story and clip view receipts so creators do not see when you view public stories;
(h) Silence Notifications in Ghost Mode: Automatically mutes push banners while Master Ghost Mode is active.

2.3 Universal Civil Privacy Standards & Cryptographic Protection:
Localiiiy enforces privacy-by-design and privacy-by-default principles aligned with GDPR (EU/EEA), CCPA/CPRA (California), DPDP Act (India), LGPD (Brazil), PIPEDA (Canada), and international civil privacy charters:
(a) Strict Data Minimization: Only attributes strictly required to authenticate your account and render your chosen features are processed;
(b) Cryptographic Security in Transit & at Rest: All client-server traffic utilizes TLS 1.3 encryption, and local structured data is persisted in Android Room SQLite storage with cryptographic SHA-256 integrity verification;
(c) Local Transparency & Activity Audit Log: Users can inspect their complete on-device "Privacy Transparency Log & Audit Trail" (`UserActivityLogScreen`) and delete individual activity entries or clear all logs at any time.

2.4 Export Local Archive & Data Portability:
Users retain the unrestricted right to export their saved posts, clips, and drafts into an encrypted local archive ("Export Local Archive") or execute a complete local and cloud erasure ("Complete Account & Data Oblivion").
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "2.1",
                    subTitle = "Connections-Only Social Graph Terminology & Architecture",
                    content = "Localiiiy rejects asymmetric clout surveillance models. All social relationships on the Platform operate strictly as mutual or creator 'Connections' ('Connect', 'Connected', 'Connections'). Filtering feeds by social graph strictly uses the 'Connected' designation."
                ),
                LegalSubSection(
                    subNumber = "2.2",
                    subTitle = "Biometric Chat Vault & Call Boundary Enforcement",
                    content = "Users may lock Direct Messages behind Android BiometricPrompt hardware authentication (Fingerprint / Face / Device PIN) via the Biometric Chat Vault toggle, and may block all incoming audio/video calls via the 'Allow Direct Calls from Connections' switch."
                ),
                LegalSubSection(
                    subNumber = "2.3",
                    subTitle = "Decoy Launcher Icon & Visual Discretion",
                    content = "For users requiring physical device discretion, Settings & Privacy provides a 'Decoy App Icon' mode to disguise the launcher appearance as a standard Calculator utility."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_3_marketplace_liability",
            clauseNumber = "Section 3.0",
            anchorId = "#escrow",
            title = "Neighborhood Marketplace, Safe-Haven Digital Escrow (5% / 0% Pro), Barter, Service Gigs & Liability Waiver",
            category = LegalCategory.MARKETPLACE,
            plainSummary = "The Localiiiy Marketplace empowers neighbors to buy, sell, barter, and book local service gigs. Optional Safe-Haven Digital Escrow charges a 5% platform fee (0% for Localiiiy Premium Pro Titan members) and releases funds to the seller's Creator Wallet upon physical inspection and buyer confirmation. Direct P2P trades carry zero platform financial liability—always inspect items in person at verified safe public spots!",
            legalText = """
3.1 Dual-Mode Neighborhood Commerce (Direct P2P Bulletin & Safe-Haven Digital Escrow):
The Localiiiy Marketplace ("Local Market") operates as a hyperlocal neighborhood commerce hub supporting physical goods, handmade crafts, community barter/swaps, and hourly/flat-rate local service gigs:
(a) Safe-Haven Digital Escrow Protocol: When transacting through the integrated Safe-Haven Digital Escrow gateway, a standard five percent (5%) platform commission fee is automatically calculated and deducted from the gross transaction amount to cover escrow locking, cryptographic receipt generation, and payment processing. Active Localiiiy Premium Pro ("Titan") subscribers enjoy a zero percent (0%) Safe-Haven Market Escrow commission perk;
(b) Physical Handover & Escrow Release: Escrowed funds are held in a pending state and released to the seller's Creator Wallet only upon physical inspection and explicit buyer handover confirmation at meetup;
(c) Direct Peer-to-Peer (P2P) Noticeboard Mode: When users elect to communicate, barter, or settle offline directly peer-to-peer outside of Digital Escrow, Localiiiy acts solely as a discovery noticeboard and does not custody, intermediate, or guarantee payment.

3.2 Zero Banking Credential Storage & Merchant-of-Record Disclaimer:
Localiiiy is NOT a chartered bank, depository institution, or physical logistics carrier:
(a) No Raw Card Storage: Localiiiy does NOT store raw credit card numbers, CVV security codes, bank login passwords, or private cryptocurrency keys on its application servers;
(b) Post-Handover Finality: Once physical inspection is completed and the buyer confirms handover (or settles directly P2P), the transaction is final. Localiiiy is not liable for subsequent buyer remorse, off-platform payment reversals, or unverified item wear.

3.3 Absolute Financial & Counterparty Liability Waiver:
To the maximum extent permitted by applicable law, the developers, founders, affiliates, and operators of Localiiiy bear ZERO financial, civil, tort, or criminal liability for any monetary loss, defective merchandise, misdescribed barter item, unperformed service gig, counterparty default, or off-platform scam arising from user-to-user marketplace interactions.

3.4 Mandatory Safe Meetup Protocol & Cyber Fraud Advisory:
Users are strictly advised to:
(a) Never wire money, send gift cards, or click external phishing payment links prior to inspecting an item in person;
(b) Conduct all physical exchanges and service consultations in well-lit, camera-monitored public community locations, municipal police safe-exchange zones, or verified neighborhood coffee shops;
(c) Verify neighborhood price benchmarks using the built-in "Neighborhood Market Price Trends" analytics card before finalizing high-value trades.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "3.1",
                    subTitle = "Hourly Service Gigs & Transparent Visual Rate Cards",
                    content = "Service providers offering local skills (photography, tutoring, repair, design, fitness) must display accurate hourly or flat rates directly on the listing image badge and description without hidden surcharges."
                ),
                LegalSubSection(
                    subNumber = "3.2",
                    subTitle = "Prohibited Contraband & Regulated Goods",
                    content = "Listing firearms, ammunition, explosives, narcotics, prescription pharmaceuticals, illicit substances, stolen property, counterfeit luxury goods, human biological materials, or adult services is a first-strike permanent ban offense."
                ),
                LegalSubSection(
                    subNumber = "3.3",
                    subTitle = "Merchant Bounties & Hyperlocal Dispatch Fulfillment",
                    content = "Verified local businesses and creators may post Merchant Bounties in the Creator Studio Bounty Board. Fulfilling a bounty requires original, geotagged or timestamped media proof and adherence to honest advertising standards."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_4_community_safety",
            clauseNumber = "Section 4.0",
            anchorId = "#community",
            title = "Community Safety Charter, Neighbor Wave (👋) Etiquette, Direct Messaging & Call Boundaries",
            category = LegalCategory.COMMUNITY,
            plainSummary = "Localiiiy is built on respectful neighborhood trust. Neighbor Waves (👋), Direct Messages, Space Chat rooms, comments, and audio/video calls must remain civil. Harassment, hate speech, doxxing, and spam are strictly prohibited with instant 1-tap 2-way blocking and 24/7 priority moderation.",
            legalText = """
4.1 Core Community Conduct & Prohibited Behavior:
Every member of Localiiiy agrees to uphold neighborhood civility across Pulse posts, 24-hour Stories, 9:16 Clips, Studio videos, Space Chat rooms, Direct Messages, and Live Radar interactions. Users are strictly prohibited from:
(a) Engaging in hate speech, racial/religious/gender slurs, caste or ethnic discrimination, homophobia, xenophobia, or targeted bullying;
(b) Publishing non-consensual intimate imagery (NCII), deepfake impersonations, doxxing (exposing private residential addresses, phone numbers, or workplace schedules of others), or extortion/blackmail;
(c) Threatening physical violence, inciting neighborhood unrest, promoting self-harm, or endangering minors;
(d) Operating automated spam bots, mass-wave scripts, deceptive phishing links, or coordinated artificial engagement rings.

4.2 Neighbor Wave (👋), Direct Messaging & Audio/Video Call Boundaries:
(a) Neighbor Waves: Sending a proximity wave (👋) on the Space Radar is a polite greeting. Repeatedly waving at or pinging a user who has not reciprocated or who has dismissed the interaction is prohibited;
(b) Connected-Only Inbound Messaging: By default in Settings & Privacy, users can enforce "Connected-Only Inbound Messaging" so only mutual Connections can initiate Direct Message threads;
(c) Active Audio & Video Call Controls: Incoming audio and video calls (`ActiveCallBottomSheet`) are strictly governed by the user's "Allow Direct Calls from Connections" privacy toggle. When toggled OFF, all inbound signaling is rejected at the protocol layer.

4.3 Reciprocal 2-Way Blocking & Instant Purge:
Blocking any user (`BlockedUsersScreen`) executes an immediate, reciprocal two-way isolation:
(a) The blocked account is instantaneously purged from your Live Space Radar, Pulse Feed, Clips stream, Stories tray, Direct Messages, and Search results;
(b) Your profile, radar blip, posts, clips, and connection graph become completely invisible to the blocked account.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "4.1",
                    subTitle = "Space Chat & Community Hub Governance",
                    content = "Localiiiy Community Hub and Space Chat channels are moderated for neighborhood relevance. Messages containing abusive language or off-topic commercial spam are subject to automated filtering and removal."
                ),
                LegalSubSection(
                    subNumber = "4.2",
                    subTitle = "Media Sanitization & EXIF Metadata Stripping",
                    content = "All photos and videos uploaded through `MediaSanitizationHelper` undergo automated sanitization to strip hidden camera EXIF GPS serial tags unless the creator explicitly attaches a public neighborhood tag."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_5_intellectual_property",
            clauseNumber = "Section 5.0",
            anchorId = "#copyright",
            title = "Localiiiy Copyright & Decentralized Licensing Engine (All Rights Reserved vs. LCC, SHA-256 / pHash, 7-Day Counter-Notice & 3-Strike Rule)",
            category = LegalCategory.IP_RIGHTS,
            plainSummary = "You own 100% of your original photos, clips, and studio videos. At upload, you choose between 'All Rights Reserved (Standard)' or 'Localiiiy Creative Commons (LCC)' with granular toggles for Audio Reuse, Remix/Reactions, and Marketplace Showcase. Our MediaFingerprintEngine computes SHA-256 & perceptual hashes (phash_) to catch unauthorized re-uploads. Copyright claims trigger a 7-day counter-notification quarantine, 100% ad-revenue rerouting to the true creator, and a strict 3-Strike / 90-Day permanent suspension policy.",
            legalText = """
5.1 Absolute Creator Ownership & Upload Licensing Selection (`ContentLicensingConfig`):
Creators retain 100% legal copyright, moral rights, and intellectual property ownership over all original photographs, 9:16 vertical clips, 4K Studio videos, audio recordings, stories, and written captions uploaded to Localiiiy. At the time of upload (`LicensingAndReuseRightsUploadSection`), the creator defines the binding license governing the asset:
(a) "All Rights Reserved (Standard)" (`LICENSE_ALL_RIGHTS`): Default protective mode (`reusePermitted = false`). No other user may download, re-upload, sample audio, remix, or commercially showcase the media without explicit written license;
(b) "Localiiiy Creative Commons (LCC)" (`LICENSE_LCC`): Collaborative community license (`reusePermitted = true`) where the original creator grants fellow users permission to build upon the work with mandatory cryptographic attribution back to the originator's `@username`;
(c) Granular Reuse Rights Toggles: Creators independently configure:
    • `audioReuseAllowed`: Permits or forbids other creators from extracting or syncing original audio tracks in Clips;
    • `remixReactionAllowed`: Permits or forbids side-by-side duets, stitches, and reaction clips;
    • `marketplaceShowcaseAllowed`: Permits or forbids verified neighborhood merchants from embedding the clip as a product testimonial.

5.2 Automated Cryptographic & Perceptual Media Fingerprinting (`MediaFingerprintEngine`):
Upon every media upload across Posts, Clips, Studio, and Market:
(a) Cryptographic SHA-256 Digest: `MediaFingerprintEngine.generateFingerprint()` computes a deterministic hexadecimal SHA-256 signature of the media payload;
(b) Perceptual & Acoustic Hashing (`phash_`): `MediaFingerprintEngine.computePerceptualHash()` generates a composite perceptual signature combining media URI structures and caption semantics to detect re-encoded, cropped, or mirrored duplicate uploads;
(c) Pre-Flight Duplicate Infringement Check: `checkDuplicateInfringement()` cross-references new uploads against `registeredFingerprints`. If an upload matches an existing asset locked under `All Rights Reserved (reusePermitted = false)`, the system flags the upload for copyright quarantine.

5.3 Copyright Claim Quarantine & 7-Day Counter-Notification Protocol (`CopyrightClaim`):
When an original creator files a `CopyrightClaim` (specifying `claimId`, `contentId`, `contentType` ["POST", "CLIP", "STUDIO", "MARKET"], `claimantUsername`, `uploaderUsername`, and `infringementReason`):
(a) Immediate Quarantine (`ClaimStatus.QUARANTINED`): The disputed content is immediately shifted to an unlisted/quarantined review state and its accrued monetization is frozen in escrow;
(b) 7-Day Counter-Notification Window (`deadlineTimestamp = timestamp + 7 days`): The uploader has exactly seven (7) calendar days (`daysRemaining`) to submit a formal `counterNotificationText` providing cryptographic proof of prior creation, public domain status, fair use, or written license (`ClaimStatus.COUNTER_SUBMITTED`);
(c) Resolution & Dismissal (`ClaimStatus.RESOLVED_ACCEPTED` / `DISMISSED`): If the claimant accepts the counter-proof or withdraws the claim, the content is restored to public visibility with zero penalty.

5.4 Confirmed Infringement, 100% Revenue Rerouting & 3-Strike / 90-Day Suspension Rule (`UserCopyrightRecord`):
If the uploader fails to respond within 7 days or if theft is confirmed (`ClaimStatus.STRIKE_CONFIRMED`):
(a) Automatic 100% Revenue Rerouting (`reroutedRevenueUSD`): One hundred percent (100%) of all ad revenue, fan micro-tips, and boost earnings generated by the infringing upload are forfeited by the infringer and automatically rerouted to the verified original creator's Wallet;
(b) Three-Strike Penalty Escalation (`activeStrikes`):
    • Strike 1: Content permanently removed, 1 official copyright strike recorded on `UserCopyrightRecord`, mandatory copyright education warning;
    • Strike 2: Temporary 14-day suspension of Studio, Clips, and Marketplace upload privileges (`isUploadSuspended = true`, `isMarketplaceSuspended = true`);
    • Strike 3 (`activeStrikes >= 3` within 90 days): Immediate, permanent account suspension (`isPermanentlySuspended = true`), forfeiture of pending illegitimate balances, and device hardware blacklisting.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "5.1",
                    subTitle = "Limited Platform Distribution License",
                    content = "By publishing content on Localiiiy, you grant the Platform a non-exclusive, revocable, royalty-free license solely to host, transcode, cache via ExoPlayerCacheHelper, and stream your content to audiences within your selected Dual-Reach distribution tier (0 km Neighbor to Earth)."
                ),
                LegalSubSection(
                    subNumber = "5.2",
                    subTitle = "DMCA & Global Statutory Copyright Agent Compliance",
                    content = "Localiiiy complies with 17 U.S.C. § 512 (DMCA), the EU Copyright Directive, the Indian Copyright Act 1957, and WIPO treaties. False or bad-faith copyright claims submitted to harass competitors are subject to perjury penalties and strike reversal."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_6_emergency_navigation",
            clauseNumber = "Section 6.0",
            anchorId = "#emergency",
            title = "Social Discovery vs. Emergency Dispatch, Tactical Radar HUD & Medical Navigation Disclaimer",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "Our Space Radar, Tactical Radar HUD, Proximity Dial Arc, and geofence alerts are designed for recreational social discovery and local networking—never as an emergency beacon, maritime/aviation instrument, or medical alert system.",
            legalText = """
6.1 Recreational Social & Hyperlocal Discovery Purpose:
The Live Space Radar (`LiveRadarScreen`), Tactical Radar HUD (`TacticalRadarHUD`, `TacticalRadarView`), Proximity Dial Arc (`ProximityDialArc`), Radar Radius Scale (`RadarRadiusScale`), Google Map Moments (`GoogleMapMomentsComponent`), and Hyperlocal Grounded News cards are engineered exclusively for recreational social discovery, community networking, and neighborhood commerce.

6.2 Critical Emergency, Life-Safety & Navigation Disclaimer:
(a) Not an Emergency Service: Localiiiy is NOT a 911 / 112 / 999 / 100 public safety answering point (PSAP), emergency dispatch service, law enforcement alert network, or medical distress beacon;
(b) No High-Risk Navigation Reliance: Because user coordinates are intentionally obfuscated by privacy sliders (1 km to 500 km) and Ghost Mode shields, radar blips and map pins must NEVER be relied upon for physical navigation, search-and-rescue, aviation, maritime, or hazardous terrain traversal;
(c) Immediate Official Dispatch Instruction: In any situation involving imminent physical danger, crime in progress, fire, or medical emergency, users must immediately exit the application and dial their sovereign government emergency response authorities.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "6.1",
                    subTitle = "Hyperlocal Grounded News & AI Translation Disclaimers",
                    content = "Neighborhood news summaries (`HyperlocalGroundedNewsCard`) and automated translations (`AiTranslationBottomSheet`) are provided for informational convenience. Users should verify critical municipal advisories (weather, road closures, civic alerts) with official government agencies."
                )
            ),
            isHighlighted = false
        )
    )
}
