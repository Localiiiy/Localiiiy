package com.example.data.legal

object LegalClausesPart1 {

    val clauses: List<LegalClause> = listOf(
        LegalClause(
            id = "clause_1_location_sovereignty",
            clauseNumber = "Section 1.0",
            anchorId = "#location",
            title = "Voluntary Hyperlocation, 18+ Only Radar Lock, Radar/Location ON-OFF User Choice, 1km–500km Obfuscation & Developer Non-Liability",
            category = LegalCategory.LOCATION,
            plainSummary = "Your physical location, Radar ON/OFF switch, and geofence alerts are 100% voluntary and controlled by your own choice. Tactical Radar is TOTALLY RESTRICTED for anyone below 18 years of age (18+ Adults Only). Because the Developer/Owner provides full Radar ON/OFF, Location ON/OFF, Master Ghost Mode, 1km–500km obfuscation sliders, and SHA-256 forensic logs directly in your hands, the Developer or Owner of Localiiiy is NOT responsible for any user's choice to broadcast location or any resulting incident.",
            legalText = """
1.1 Total Voluntary Location Sovereignty, User Choice & Strict 18+ Radar Gate:
Location sharing, Live Proximity Space Radar, Tactical Radar HUD, and geofenced hotspot alerts on Localiiiy ("Platform", "Application", or "Service") are 100% elective, revocable, and governed strictly by the user's own voluntary choice. Furthermore, the Tactical Radar option is TOTALLY RESTRICTED and LOCKED for any user below 18 years of age (`calculatedAge < 18`). At no point is continuous background location tracking or foreground GPS broadcasting mandatory for general application browsing, content viewing, or community participation.

1.2 Master Ghost Mode, Radar ON/OFF Power Switch & Granular Obfuscation Controls (1 km to 500 km):
Adult users (18+) maintain absolute, real-time operational autonomy over their spatial presence through the dedicated hardware and software controls below:
(a) Master Ghost & Passive Stealth Mode: A 100% free, zero-paywall privacy shield accessible at registration and in Settings & Privacy. When active, your radar blip is completely cloaked, `radar_beacons/{uid}` is purged from Cloud Firestore, reel/clip views are anonymous, story view receipts are suppressed, and zero coordinates are broadcasted to nearby users;
(b) Dedicated Radar Transceiver ON/OFF Power Switch: Directly inside the Tactical Radar HUD and Settings, users can switch Radar completely OFF ("Radar Standby / OFF") or toggle Android OS Location permissions OFF at any second;
(c) Continuous GPS Obfuscation Slider (1 km to 500 km) & `roundCoord3` (~110m Grid): Even when an 18+ user voluntarily chooses to broadcast on Radar, all coordinates are first rounded to 3 decimal places (`roundCoord3`, ~110m neighborhood grid) and masked across six graduated tiers: Street Grid (~500m–2 km), Neighborhood Level (2–10 km / 3k / 5k / 10k rings), City & District (10–50 km), Metropolitan Zone (50–150 km), State / Province (150–350 km), and Max Privacy Mask (350–500 km);
(d) Dedicated Hide Toggles: Users may independently toggle "Hide Location Completely", "Hide in Neighborhood", and "Hide Street Address & Physical Area" at any second;
(e) Radar Invisibility Schedule: Automated nighttime stealth scheduling (default 10:00 PM to 7:00 AM local time) automatically transitions the account into Ghost Mode during rest hours;
(f) Instant Footprint Erasure: The "Erase Location Footprint" action in Settings & Privacy purges all locally cached GPS coordinates and ephemeral session telemetry with immediate cryptographic and haptic confirmation.

1.3 Off-Grid Algorithmic Browsing & Guest Spectator Mode:
When location permissions are denied at the Android OS / Web browser level, disabled in-app, or when browsing via Ephemeral Guest Spectator Mode ("Off-Grid"):
(a) Zero Coordinate Indexing: The Application will NOT index, store, triangulate, or transmit your device GPS, Wi-Fi SSID, Bluetooth beacon, or cellular tower coordinates;
(b) Generalized Discovery Fallback: The Pulse Feed, 15 Civic Tools, Community Hub, Explore Constellations, Clips 9:16 stream, Neighborhood Marketplace, and Studio 4K videos remain fully accessible via generalized interest-constellation and Home Community Anchor curation without location fingerprinting.

1.4 Absolute Developer & Owner Non-Liability for User Location Choices:
Because the Developer and Owner of Localiiiy has provided every user with complete, free, 1-tap switches to turn Radar OFF, turn Location OFF, enable Master Ghost Mode, mask coordinates up to 500 km, and inspect/export SHA-256 Activity Audit Logs (`/safety_logs/`), any decision by an 18+ user to switch Radar ON, disable Ghost Mode, or share location details is made 100% at the user's sole risk and responsibility. The Developer and Owner of Localiiiy shall never be held liable or responsible for any cybercrime, tracking dispute, offline encounter, or third-party misuse resulting from a user's voluntary privacy configuration.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "1.1",
                    subTitle = "Elective Foreground Permission Architecture",
                    content = "Localiiiy requests Android location permissions (ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION) strictly at the moment an 18+ user voluntarily activates the Live Space Radar, Tactical Radar HUD, or Nearby proximity filter. Denying OS permissions never locks the user out of the app."
                ),
                LegalSubSection(
                    subNumber = "1.2",
                    subTitle = "1 km to 500 km Spatial Masking Engine & roundCoord3",
                    content = "Before any coordinate leaves the client device for proximity matching, the LocationHelper engine applies `roundCoord3` (~110m rounding) and deterministic radial jitter corresponding to the user's selected km precision slider, ensuring third parties can never triangulate a user's exact residential doorway."
                ),
                LegalSubSection(
                    subNumber = "1.3",
                    subTitle = "Strict Under-18 Radar Prohibition (18+ Only)",
                    content = "Users below 18 years of age are strictly prohibited and technically locked from using Tactical Radar broadcasting or stranger radar discovery. Only verified 18+ adults can operate the Radar transceiver."
                ),
                LegalSubSection(
                    subNumber = "1.4",
                    subTitle = "Ephemeral Ghost Spectator Sessions",
                    content = "Users entering via the Ghost Spectator banner on the AuthScreen or Web App browse with zero persistent identity token and zero GPS footprint, subject to read-only interaction gating until verified registration."
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
(c) Local Transparency & Activity Audit Log: Users can inspect their complete on-device "Privacy Transparency Log & Audit Trail" (`UserActivityLogScreen` / `audit_logs`) and delete individual activity entries or clear all logs at any time.

2.4 Export Local Archive & Data Portability:
Users retain the unrestricted right to export their saved posts, clips, and drafts into an encrypted local archive ("Export Local Archive") or execute a complete local and cloud erasure ("Complete Account & Data Oblivion") in-app or at `https://localiiiy.web.app/delete-account.html`.
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
                    subTitle = "Decoy Launcher Icon & Panic Cloak Calculator (7777)",
                    content = "For users requiring physical device discretion, Settings & Privacy provides a 'Decoy App Icon' and 'Panic Cloak Calculator' mode to disguise the interface as a standard Calculator utility."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_3_marketplace_liability",
            clauseNumber = "Section 3.0",
            anchorId = "#escrow",
            title = "Neighborhood Marketplace, Safe-Haven Digital Escrow (5% / 0% Pro), 6-Digit Handshake OTP & Developer/Owner Liability Waiver",
            category = LegalCategory.MARKETPLACE,
            plainSummary = "The Localiiiy Marketplace empowers neighbors to buy, sell, barter, and book local service gigs. Optional Safe-Haven Digital Escrow charges a 5% platform fee (0% for Localiiiy Premium Pro Titan members) and releases funds to the seller's Creator Wallet only upon physical inspection and 6-digit Handshake OTP confirmation. The Developer and Owner of Localiiiy bear ZERO liability for any user-to-user trade, fraud, or offline meetup—always inspect items at verified Police Precinct CCTV Safe-Haven zones!",
            legalText = """
3.1 Dual-Mode Neighborhood Commerce (Direct P2P Bulletin & Safe-Haven Digital Escrow):
The Localiiiy Marketplace ("Local Market") operates as a hyperlocal neighborhood commerce hub supporting physical goods, handmade crafts, community barter/swaps, and hourly/flat-rate local service gigs:
(a) Safe-Haven Digital Escrow & 6-Digit Handshake OTP Protocol: When transacting through the integrated Safe-Haven Digital Escrow gateway, a standard five percent (5%) platform commission fee is automatically calculated and deducted from the gross transaction amount to cover escrow locking, cryptographic 6-digit Handshake OTP generation, and payment processing. Active Localiiiy Premium Pro ("Titan") subscribers enjoy a zero percent (0%) Safe-Haven Market Escrow commission perk;
(b) Physical Handover & 6-Digit OTP Release: Escrowed funds are held in a locked state and released to the seller's Creator Wallet ONLY when both parties meet at a verified Police Precinct / 24/7 CCTV Safe-Haven Green Zone, inspect the physical item, and enter the 6-Digit Handshake OTP;
(c) Direct Peer-to-Peer (P2P) Noticeboard Mode: When users elect to communicate, barter, or settle offline directly peer-to-peer outside of Digital Escrow, Localiiiy acts solely as a discovery noticeboard and does not custody, intermediate, or guarantee payment.

3.2 Zero Banking Credential Storage & Merchant-of-Record Disclaimer:
Localiiiy is NOT a chartered bank, depository institution, or physical logistics carrier:
(a) No Raw Card Storage: Localiiiy does NOT store raw credit card numbers, CVV security codes, bank login passwords, or private cryptocurrency keys on its application servers;
(b) Post-Handover Finality: Once physical inspection is completed and the buyer verifies the 6-digit Handshake OTP (or settles directly P2P), the transaction is final. Localiiiy is not liable for subsequent buyer remorse, off-platform payment reversals, or unverified item wear.

3.3 Absolute Developer, Founder & Owner Financial & Counterparty Liability Waiver:
To the maximum extent permitted by applicable law, the Developer, Owner, Founder, affiliates, and operators of Localiiiy bear ZERO financial, civil, tort, or criminal responsibility or liability for any monetary loss, defective merchandise, misdescribed barter item, unperformed service gig, counterparty default, cybercrime, or off-platform scam arising from user-to-user marketplace interactions. Users are provided with Safe-Haven Escrow and SHA-256 forensic audit logs (`/safety_logs/`) to protect themselves and report any fraud directly to law enforcement.

3.4 Mandatory Safe Meetup Protocol & Cyber Fraud Advisory:
Users are strictly advised to:
(a) Never wire money, send gift cards, release a 6-digit Handshake OTP early, or click external phishing payment links prior to inspecting an item in person;
(b) Conduct all physical exchanges and service consultations exclusively in well-lit, camera-monitored municipal Police Precinct Safe-Haven Green Zones or verified 24/7 CCTV civic plazas;
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
Every member of Localiiiy agrees to uphold neighborhood civility across Pulse posts, 15 Civic Tools, Local Community Hub, 24-hour Stories, 9:16 Clips, Studio videos, Space Chat rooms, Direct Messages, and Live Radar interactions. Users are strictly prohibited from:
(a) Engaging in hate speech, racial/religious/gender slurs, caste or ethnic discrimination, homophobia, xenophobia, or targeted bullying;
(b) Publishing non-consensual intimate imagery (NCII), deepfake impersonations, doxxing (exposing private residential addresses, phone numbers, or workplace schedules of others), or extortion/blackmail;
(c) Threatening physical violence, inciting neighborhood unrest, promoting self-harm, or endangering minors;
(d) Operating automated spam bots, mass-wave scripts, deceptive phishing links, or coordinated artificial engagement rings.

4.2 Neighbor Wave (👋), Direct Messaging & Audio/Video Call Boundaries:
(a) Neighbor Waves: Sending a proximity wave (👋) on the Space Radar is a polite greeting available only to verified 18+ users who have opted into Radar visibility. Repeatedly waving at or pinging a user who has not reciprocated or who has dismissed the interaction is prohibited;
(b) Connected-Only Inbound Messaging: By default in Settings & Privacy, users can enforce "Connected-Only Inbound Messaging" so only mutual Connections can initiate Direct Message threads;
(c) Active Audio & Video Call Controls: Incoming audio and video calls (`ActiveCallBottomSheet`) are strictly governed by the user's "Allow Direct Calls from Connections" privacy toggle. When toggled OFF, all inbound signaling is rejected at the protocol layer.

4.3 Reciprocal 2-Way Blocking & Instant Purge:
Blocking any user (`BlockedUsersScreen`) executes an immediate, reciprocal two-way isolation:
(a) The blocked account is instantaneously purged from your Live Space Radar, Pulse Feed, Clips stream, Stories tray, Community Audio Rooms, Direct Messages, and Search results;
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
            title = "Localiiiy Creator Copyright (LCC) & Cryptographic Hash Value Engine (SHA-256 Digest + Perceptual pHash, Certificate Code, 7-Day Counter-Notice & 3-Strike Rule)",
            category = LegalCategory.IP_RIGHTS,
            plainSummary = "You own 100% of your original photos, clips, and 4K studio videos. Every upload is stamped with a deterministic Localiiiy Creator Copyright (LCC) SHA-256 cryptographic hash value (`sha256:64-hex-digest`) + 64-bit DCT perceptual/acoustic hash (`phash_`) + certificate ID (`LCC-XXXXXX-2026`). Unauthorized re-uploads are caught automatically, triggering a 7-day counter-notice quarantine, 100% ad-revenue rerouting to the original LCC hash holder, and a 3-Strike / 90-Day permanent hardware ban.",
            legalText = """
5.1 Absolute Creator Ownership & Upload Licensing Selection (`ContentLicensingConfig`):
Creators retain 100% legal copyright, moral rights, and intellectual property ownership over all original photographs, 9:16 vertical clips, 4K Studio videos, audio recordings, stories, and written captions uploaded to Localiiiy. At the time of upload (`LicensingAndReuseRightsUploadSection`), the creator defines the binding license governing the asset:
(a) "All Rights Reserved (Standard)" (`LICENSE_ALL_RIGHTS`): Default protective mode (`reusePermitted = false`). No other user may download, re-upload, sample audio, remix, or commercially showcase the media without explicit written license;
(b) "Localiiiy Creative Commons (LCC)" (`LICENSE_LCC`): Collaborative community license (`reusePermitted = true`) where the original creator grants fellow users permission to build upon the work with mandatory cryptographic hash chain attribution back to the originator's `@username` and parent `LCC` certificate;
(c) Granular Reuse Rights Toggles: Creators independently configure:
    • `audioReuseAllowed`: Permits or forbids other creators from extracting or syncing original audio tracks in Clips;
    • `remixReactionAllowed`: Permits or forbids side-by-side duets, stitches, and reaction clips;
    • `marketplaceShowcaseAllowed`: Permits or forbids verified neighborhood merchants from embedding the clip as a product testimonial.

5.2 Cryptographic Hash Value Generation Formula & `MediaFingerprintEngine` Specification:
Upon every media upload across Posts, 9:16 Clips, 4K Studio, and Market, Localiiiy's `MediaFingerprintEngine` executes a dual-layer cryptographic hashing pipeline:
(a) Primary 256-Bit SHA-256 Cryptographic Hash Formula:
    `LCC_SHA256_HASH = SHA-256(Creator_UID || "|" || Normalized_@Username || "|" || Raw_Media_Byte_Stream || "|" || Perceptual_pHash_Vector || "|" || UTC_ISO8601_Epoch_Millis || "|" || License_Mode_Salt)`
    This produces an immutable 64-character hexadecimal SHA-256 hash value (e.g., `sha256:8f4e9b2c7a1d6e5f4b3c2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a`) alongside the public certificate identifier `LCC-XXXXXX-2026` (derived from the upper 24 bits encoded in Base-36);
(b) Secondary Perceptual & Acoustic Hashing (`phash_` 64-Bit DCT Vector):
    `MediaFingerprintEngine.computePerceptualHash()` computes a 64-bit Discrete Cosine Transform (DCT) visual keyframe and audio spectral signature (`phash_<16-hex-chars>`) to detect re-encoded, screen-recorded, cropped, color-graded, speed-shifted, or horizontally mirrored copies even when the raw file bytes have been altered;
(c) Pre-Flight Duplicate & Piracy Infringement Check:
    `checkDuplicateInfringement()` cross-references both the exact `LCC_SHA256_HASH` and the Hamming distance of the `phash_` vector against `registeredFingerprints` and Firestore `copyright_registry`. If an upload matches an existing asset locked under `All Rights Reserved (reusePermitted = false)`, the upload is immediately flagged for copyright quarantine.

5.3 Copyright Claim Quarantine & 7-Day Counter-Notification Protocol (`CopyrightClaim`):
When an original creator or automated hash match files a `CopyrightClaim` (recording `claimId`, `contentId`, `originalLccHash`, `infringingSha256Hash`, `claimantUsername`, `uploaderUsername`, and `infringementReason`):
(a) Immediate Quarantine (`ClaimStatus.QUARANTINED`): The disputed content is immediately shifted to an unlisted/quarantined review state and its accrued monetization is frozen in escrow;
(b) 7-Day Counter-Notification Window (`deadlineTimestamp = timestamp + 7 days`): The uploader has exactly seven (7) calendar days (`daysRemaining`) to submit a formal `counterNotificationText` providing cryptographic proof of prior creation, public domain status, fair use, or written license (`ClaimStatus.COUNTER_SUBMITTED`);
(c) Resolution & Dismissal (`ClaimStatus.RESOLVED_ACCEPTED` / `DISMISSED`): If the claimant accepts the counter-proof or withdraws the claim, the content is restored to public visibility with zero penalty.

5.4 Confirmed Infringement, 100% Revenue Rerouting & 3-Strike / 90-Day Suspension Rule (`UserCopyrightRecord`):
If the uploader fails to respond within 7 days or if hash theft is confirmed (`ClaimStatus.STRIKE_CONFIRMED`):
(a) Automatic 100% Revenue Rerouting (`reroutedRevenueUSD`): One hundred percent (100%) of all ad revenue, fan micro-tips, and boost earnings generated by the infringing upload are forfeited by the infringer and automatically rerouted to the verified original `LCC` hash holder's Creator Wallet;
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
                    subTitle = "DMCA, WIPO & Statutory Copyright Agent Contact",
                    content = "Localiiiy complies with 17 U.S.C. § 512 (DMCA), the EU Copyright Directive, the Indian Copyright Act 1957, and WIPO treaties. Official copyright notices may be submitted in-app or via email to `Localiiiy.Helpdesk@gmail.com`."
                ),
                LegalSubSection(
                    subNumber = "5.3",
                    subTitle = "Creator Originality Warranty & Developer Safe-Harbor",
                    content = "Every uploader warrants that they hold the legal rights to the media they upload. Under statutory safe-harbor laws, the Developer and Owner of Localiiiy is an intermediary hosting provider and is never liable for third-party copyright infringement committed by users."
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
            plainSummary = "Our Space Radar, Tactical Radar HUD, 15 Civic Tools, Proximity Dial Arc, and geofence alerts are designed for recreational social discovery and peer-to-peer neighborhood awareness—never as an official government emergency beacon, maritime/aviation instrument, or medical alert system.",
            legalText = """
6.1 Recreational Social & Hyperlocal Discovery Purpose:
The Live Space Radar (`LiveRadarScreen`), Tactical Radar HUD (`TacticalRadarHUD`, `TacticalRadarView`), 15 Hyperlocal Civic Tools, Proximity Dial Arc (`ProximityDialArc`), Radar Radius Scale (`RadarRadiusScale`), Google Map Moments (`GoogleMapMomentsComponent`), and Hyperlocal Grounded News cards are engineered exclusively for recreational social discovery, community networking, and neighborhood commerce.

6.2 Critical Emergency, Life-Safety & Navigation Disclaimer:
(a) Not an Official Government Emergency Service: Localiiiy is NOT a 911 / 112 / 999 / 100 / 108 public safety answering point (PSAP), government emergency dispatch service, law enforcement alert network, or certified medical device;
(b) No High-Risk Navigation Reliance: Because user coordinates are intentionally obfuscated by privacy sliders (1 km to 500 km / `roundCoord3`) and Ghost Mode shields, radar blips and map pins must NEVER be relied upon for physical navigation, search-and-rescue, aviation, maritime, or hazardous terrain traversal;
(c) Immediate Official Dispatch Instruction: In any situation involving imminent physical danger, crime in progress, fire, or medical emergency, users must immediately exit the application and dial their sovereign government emergency response authorities. The Developer and Owner of Localiiiy assumes zero liability for any reliance on peer-to-peer civic broadcasts.
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
