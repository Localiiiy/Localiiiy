package com.example.data.legal

object LegalClausesPart2 {

    val clauses: List<LegalClause> = listOf(
        LegalClause(
            id = "clause_7_eligibility_jurisdiction",
            clauseNumber = "Section 7.0",
            anchorId = "#eligibility",
            title = "Account Eligibility, Age Verification Calendar, Total Radar Prohibition Below 18 Years (18+ Only) & Dispute Resolution",
            category = LegalCategory.RADAR_18_PLUS_GATE,
            plainSummary = "Users must be at least 13 years old to register via the Date of Birth Calendar. However, the Tactical Radar option is TOTALLY RESTRICTED and LOCKED for anyone below 18 years of age (`calculatedAge < 18`). Only verified 18+ adults can access or broadcast on Tactical Radar. Users aged 13–17 have Master Ghost Mode permanently locked ON by default. Disputes are resolved through good-faith civil negotiation via Localiiiy.Helpdesk@gmail.com.",
            legalText = """
7.1 Age Verification, 13+ Minimum Registration Gate & Total Under-18 Radar Prohibition:
During registration on `AuthScreen`, `SignUpDialog`, and `localiiiy.web.app`, every prospective user must input their true Date of Birth via the native calendar picker (`dobString` / `calculatedAge`):
(a) Minimum Registration Age (13+): Users under 13 years of age (`calculatedAge < 13`) are strictly blocked from creating an account under COPPA, GDPR-K, and India DPDP Act 2023;
(b) Total Prohibition of Tactical Radar Below 18 Years of Age (`calculatedAge < 18`): For ANY account where the user is below 18 years of age (ages 13 to 17), the Tactical Radar option (`LiveRadarScreen`, `TacticalRadarHUD`, live proximity beacon broadcasting, and nearby stranger radar sweeps) is TOTALLY RESTRICTED, LOCKED, and DISABLED. Minors under 18 are permanently locked into Master Ghost Mode (`isGhostMode = true`), restricted from adult marketplace escrow contracts, and shielded from unconnected adult DMs;
(c) Strict 18+ Adult Verification for Tactical Radar, Full Commerce & Monetization: Only verified adult users aged 18 years or older (`calculatedAge >= 18`) may access Tactical Radar, execute legally binding Safe-Haven Digital Escrow contracts, or withdraw Creator Wallet monetization payouts (`signup_age_checkbox`).

7.2 Good-Faith Civil Dispute Resolution & Informal Negotiation:
In the event of any controversy, claim, or dispute arising out of or relating to your use of Localiiiy or this Omnibus Charter:
(a) 30-Day Informal Resolution Period: Both parties agree to first attempt informal, good-faith written negotiation by submitting a formal notice to `Localiiiy.Helpdesk@gmail.com` before initiating litigation or arbitration;
(b) Individual Capacity & Severability: Claims shall be resolved on an individual basis. If any clause or sub-clause of this Agreement is held invalid or unenforceable by a court of competent jurisdiction, such provision shall be reformed to the minimum extent necessary and all remaining sections shall remain in full force and effect.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "7.1",
                    subTitle = "Strict 18+ Radar Lock & Teen Ghost Enforcement",
                    content = "Localiiiy enforces an unbypassable age lock on the Tactical Radar module for any user under 18. Falsifying a Date of Birth during registration to bypass the 18+ Radar restriction is a material breach of these Terms, absolving the Developer and Owner of any liability and resulting in immediate account suspension."
                ),
                LegalSubSection(
                    subNumber = "7.2",
                    subTitle = "Universal Civil Law & Individual Privacy Sovereignty",
                    content = "This Agreement is governed by universal principles of civil contract law, Developer Safe-Harbor immunity, and individual privacy sovereignty."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_8_consent_storage",
            clauseNumber = "Section 8.0",
            anchorId = "#consent",
            title = "Persistent Local Consent Proof, Scroll-to-Unlock Registration Gate & Cryptographic Timestamp Verification",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "During Registration, you must scroll through the mandatory legal charter to unlock the consent checkboxes. Your acceptance is recorded with a cryptographic SHA-256 verification hash and timestamp stored in your device's local Room SQLite database and displayed on your Legal Certificate.",
            legalText = """
8.1 Mandatory Scroll-to-Unlock Registration Gate (`AuthScreen` & `SignUpDialog`):
No account can be provisioned on Localiiiy without affirmative, informed legal consent:
(a) Scroll-to-Bottom Unlock Mechanism (`isNdaScrolledToBottom`): On the Registration screen, the legal agreement container tracks scroll offset (`ndaScrollState`). The mandatory consent checkboxes (`auth_nda_checkbox_1`, `auth_nda_checkbox_2`, `auth_nda_checkbox_3`) remain locked until the user scrolls through the agreement or reviews the full-screen Omnibus Legal Charter;
(b) Affirmative Multi-Box Attestation: The "Create Account & Secure" action button (`auth_register_button`) cannot be triggered until all identity validations, 2-Way OTP verifications (Email + Mobile), and legal NDA/Terms checkboxes are affirmatively checked.

8.2 Local Room Database Legal Certificate (`LegalConsentRecord`):
Upon clicking "Create Account & Secure" or "I Agree" inside `LegalAgreementScreen`:
(a) Cryptographic Proof Generation: The Platform records a `LegalConsentRecord` containing your `@username`, policy version (`CURRENT_POLICY_VERSION`), exact epoch millisecond timestamp (`System.currentTimeMillis()`), active consent flag (`isConsentActive = true`), legal jurisdiction, and a SHA-256 verification digest (`consentProofHash`);
(b) On-Device Room SQLite Persistence: This certificate is persisted locally on your device so you can audit, verify, or copy your signed legal status at the bottom of `LegalAgreementScreen` at any time without centralized tracking telemetry.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "8.1",
                    subTitle = "Version Upgrades & Re-Acknowledgment Rights",
                    content = "Whenever Localiiiy updates its Omnibus Charter version, users can review the diff, inspect every updated clause, and re-acknowledge the new policy version directly in `LegalAgreementScreen`."
                )
            ),
            isHighlighted = false
        ),

        LegalClause(
            id = "clause_9_cyberstalking_zero_tolerance",
            clauseNumber = "Section 9.0",
            anchorId = "#cyberstalking",
            title = "Universal Zero-Tolerance Anti-Cyberstalking Policy, Forensic `/safety_logs/` Vault (ISO/IEC 27037), Developer Safe-Harbor & Hardware Ban",
            category = LegalCategory.CYBERSTALKING,
            plainSummary = "Cyberstalking, persistent harassment, and unauthorized location tracking are strictly prohibited under our universal Zero-Tolerance Policy. Offenders face immediate, permanent account suspension and device hardware bans. Victims can record incidents in the Immutable Cyberstalking Evidence Vault and export SHA-256 sealed forensic logs (`/safety_logs/`) conforming to ISO/IEC 27037 court evidence standards to submit directly to Cyber Crime authorities. The Developer or Owner of Localiiiy is NOT responsible for any cybercrime or user misconduct.",
            legalText = """
9.1 Comprehensive Definition of Cyberstalking & Electronic Pursuit Offenses:
Cyberstalking, physical triangulation, and malicious electronic pursuit are grave criminal offenses under municipal, federal, and international penal statutes. On Localiiiy, prohibited conduct includes:
(a) Persistent Unwanted Contact: Repeatedly messaging, calling, waving (👋), commenting, or attempting to interact with any user after being ignored, asked to stop, or restricted;
(b) Predatory Spatial Surveillance: Attempting to use Live Space Radar, Tactical Radar HUD, proximity filters, or marketplace meetup coordinates to physically pursue, ambush, loiter near, or surveil a neighbor without their explicit ongoing consent;
(c) Block Evasion & Sockpuppetry: Creating secondary, burner, or spoofed accounts to bypass a user's 2-way block (`BlockedUsersScreen`) or connection restriction;
(d) Cyber-Extortion, Doxxing & Coercion: Threatening to publish private photos, fabricated screenshots, or personal location history to coerce or intimidate any person.

9.2 Universal Zero-Tolerance Enforcement & Irrevocable Hardware Blacklisting:
Localiiiy enforces an uncompromising Zero-Tolerance protocol (`CyberstalkingSafetyScreen`):
(a) Instantaneous Permanent Account Termination: Any account verified or substantiated to have engaged in cyberstalking or predatory tracking is immediately and permanently terminated;
(b) Cryptographic Hardware & Network Fingerprinting Ban: The offender's device signatures, installation UUIDs, and authentication tokens are permanently blacklisted by `AppSecurityGuard` to prevent account recreation;
(c) Zero Right of Appeal: Because physical and psychological neighborhood safety is paramount, permanent bans issued for verified stalking or physical endangerment carry zero right of administrative appeal.

9.3 Immutable Cyberstalking Evidence Vault, ISO/IEC 27037 Forensic `/safety_logs/` & Developer Non-Liability:
Localiiiy equips every user with a courtroom-grade defensive forensic system (`CyberstalkingEvidenceVault`, `CyberstalkingIncidentEntity`, `SafetyLogManager`, `SafetyLogsViewerSheet`):
(a) Dedicated `/safety_logs/` & `audit_logs` Vault: Users can log and export chat histories, radar sighting metadata, offender usernames, and incident descriptions directly into the protected `/safety_logs/` directory;
(b) Cryptographic SHA-256 Tamper-Evident Sealing: Every recorded incident and exported log file is stamped with an immutable epoch timestamp and a cryptographic SHA-256 hash digest, proving that the digital evidence has not been altered post-capture;
(c) ISO/IEC 27037 Statutory Evidence Certificate for Self-Service Police Filing: Exported safety logs automatically include a formatted Statutory Digital Evidence Certificate suitable for direct submission by the user to police cybercrime cells, magistrates, prosecutors, and civil courts worldwide;
(d) Developer & Owner Non-Liability for Cybercrime & Fraud: Because the Developer and Owner of Localiiiy provides full user-controlled privacy switches (Radar ON/OFF, Ghost Mode ON/OFF, Location ON/OFF, 2-way blocking) AND provides downloadable SHA-256 forensic logs (`/safety_logs/`) for direct law-enforcement reporting, the Developer and Owner of Localiiiy shall NEVER be held liable or responsible for any cybercrime, fraud, or unlawful act committed by any user.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "9.1",
                    subTitle = "One-Tap Emergency Isolation & Protective Lockdown",
                    content = "Recording an incident against an accused username in `CyberstalkingSafetyScreen` immediately severs all proximity radar visibility, blocks all DMs/calls, and locks the immutable forensic record in Room DB."
                ),
                LegalSubSection(
                    subNumber = "9.2",
                    subTitle = "24/7 Priority Law Enforcement Dispatch Cooperation",
                    content = "Localiiiy's Trust & Safety engineering unit (`Localiiiy.Helpdesk@gmail.com`) complies with lawful emergency disclosure requests, judicial subpoenas, and Mutual Legal Assistance Treaties (MLAT) to assist police in prosecuting cyberstalkers."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_10_nda_confidentiality_cybercrime",
            clauseNumber = "Section 10.0",
            anchorId = "#nda",
            title = "Binding NDA (Non-Disclosure Agreement), Neighbor Confidentiality & Cybercrime Deterrence Covenant",
            category = LegalCategory.CYBERSTALKING,
            plainSummary = "Binding legal contract signed at Registration: You covenant never to leak private neighbor communications, never to stalk neighbors (a recognized cybercrime), and never to use Localiiiy for illegal acts. If violated, all account details, IP hashes, GPS timestamps, and audit logs will be shared with law enforcement for criminal prosecution and are fully admissible in court.",
            legalText = """
10.1 Binding Mutual Non-Disclosure Agreement (NDA) & Neighbor Confidentiality:
This Non-Disclosure Agreement ("NDA") and Cybercrime Deterrence Covenant is a legally binding contract executed between you (the registering user) and Localiiiy, and for the third-party benefit of all Connected neighbors on the Platform. By checking the mandatory NDA boxes during Registration (`AuthScreen` / `SignUpDialog`) or acknowledging this Charter, you unconditionally covenant, warrant, and agree:
(a) Strict Confidentiality of Non-Public Neighbor Data: Any non-public information shared by a Connected neighbor—including Connected-only posts, private Direct Messages, voice/video call streams, approximate neighborhood anchor locations, or marketplace meetup arrangements—is strictly confidential. You shall not scrape, screen-record for malicious redistribution, publish on external websites, or disclose such private neighbor data without express permission;
(b) Absolute Prohibition of Neighbor Stalking: You agree NEVER to stalk, physically tail, harass, or electronically surveil neighbors, creators, or any individuals using Localiiiy. You explicitly acknowledge that cyberstalking is a serious felony/indictable cybercrime under domestic and international penal laws;
(c) Absolute Prohibition of Unlawful & Criminal Use: You agree never to use Localiiiy, its Space Radar, Tactical HUD, Clips camera, Studio, Marketplace, or Direct Messaging for any illegal activity, theft, fraud, drug trafficking, extortion, trespass, or violence;
(d) Mandatory Law Enforcement Evidence Handover: If you are found or reasonably suspected of engaging in cyberstalking, harassment, fraud, or criminal activity, you explicitly consent and agree that all your account registration details (Full Name, @Username, verified Email, verified Mobile Number, IP addresses, device hardware identifiers, GPS session timestamps, and audit logs) will be immediately preserved and handed over to concerned law enforcement authorities as per law for criminal investigation;
(e) Full Courtroom Admissibility Waiver: You stipulate and agree that Localiiiy's cryptographic SHA-256 audit logs, OTP verification records, and `/safety_logs/` forensic exports are authentic, reliable, and fully admissible as primary electronic evidence in any court of law worldwide;
(f) Maximum Statutory Penal Consequences: Violators will face immediate permanent account termination, hardware blacklisting, civil damages, and the most severe criminal prosecution and imprisonment prescribed under the penal laws of their country. Localiiiy is unwavering and firm in respecting and upholding law and order globally.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "10.1",
                    subTitle = "Survival of Confidentiality Obligations",
                    content = "Your NDA confidentiality obligations regarding private neighbor communications and non-public personal data survive indefinitely, even after you log out, deactivate, or delete your account."
                ),
                LegalSubSection(
                    subNumber = "10.2",
                    subTitle = "Zero Immunity for Anonymized or Secondary Accounts",
                    content = "Attempting to hide behind a pseudonym, VPN, or secondary account does not shield an offender from forensic trace correlation via 2-Way OTP records and hardware attestation."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_11_post_making_dual_reach_policy",
            clauseNumber = "Section 11.0",
            anchorId = "#dualreach",
            title = "Creator Post Making Policy & 0 km to Earth (20,000 km) Global-Local Dual-Reach Algorithm Charter",
            category = LegalCategory.POST_POLICY,
            plainSummary = "How the Localiiiy Algorithm works from 0 km to the World: Unlike legacy apps that require millions of connections to be seen, Localiiiy's Dual-Reach Engine starts at 0 km (your exact street/block) and scales organically across 7 concentric rings—Street (0–1 km) -> Neighborhood (1–5 km) -> District (5–25 km) -> City (25–100 km) -> State (100–500 km) -> Country (500–3,000 km) -> Earth / Worldwide (up to 20,000 km)—while giving creators full control over their target reach!",
            legalText = """
11.1 The 0 km to Earth (20,000 km) Concentric Dual-Reach Distribution Architecture:
Localiiiy pioneers a transparent, creator-sovereign "Global-Local Dual-Reach" algorithmic engine (`PulseFeedDualReachComponents`, `CreatorStudioDistributionComponents`, `RadarSpatialDiscoveryComponents`). Every post, 24-hour Story, 9:16 Clip, Studio 4K video, and Marketplace listing is governed by our seven-stage spatial and algorithmic propagation protocol starting from 0.0 km:
(a) Stage 1 — Immediate Hyperlocal Zero-Point (0.0 km to 1.0 km • Street & Block Spark): Content originates at 0 km around your selected Home Community Anchor or obfuscated session centroid. Even a brand-new creator with zero connections receives guaranteed initial impressions among immediate street/block neighbors;
(b) Stage 2 — Neighborhood Ring (1.0 km to 5.0 km • Local Community Anchor): When neighbors within 0–1 km engage (watch completion, likes, saves, civil comments, or shares), the distribution radius expands automatically across the entire Neighborhood Anchor;
(c) Stage 3 — District & Borough Ring (5.0 km to 25.0 km • Urban Cluster): High local velocity propels the artifact into surrounding districts and commuter corridors;
(d) Stage 4 — Metropolitan & City Ring (25.0 km to 100.0 km • Citywide Pulse): Content achieving strong watch-time retention and positive community resonance enters the Citywide Trending and Nearby feeds;
(e) Stage 5 — Regional & State Ring (100.0 km to 500.0 km • State/Province Horizon): Viral local sparks cross municipal borders into state-wide discovery constellations;
(f) Stage 6 — National Ring (500.0 km to 3,000.0 km • Nationwide Stream): Top-performing creative clips, studio documentaries, and grounded news reach nationwide audiences;
(g) Stage 7 — Earth / Worldwide Ring (3,000.0 km to 20,000+ km • Global Earth Broadcast): Content with universal human appeal—or content explicitly set by the creator to "Earth / Global" reach—is distributed across the entire planet without geographic ceiling.

11.2 Creator Distribution Sovereignty (Local-Only vs. Dual-Reach Earth Toggle):
Before publishing any Pulse post, Clip, or Studio video, the creator holds explicit control over distribution scope:
(a) "Neighbor / City Only" Lock: Creators may lock a post (such as a lost pet alert, yard sale, or neighborhood meetup) strictly to local proximity rings so it never leaks outside their city;
(b) "Dual-Reach (0 km Neighbor -> Earth)" Mode: Creators may enable full Dual-Reach so the post anchors locally at 0 km while simultaneously qualifying for worldwide algorithmic recommendation across all 20 Interest Constellations.

11.3 Algorithmic Ranking Signals & Anti-Manipulation Rules:
The Dual-Reach scoring function evaluates: (1) Spatial proximity (Haversine distance from 0 km), (2) Authentic watch-time completion and replay rate, (3) Mutual Connection graph affinity ("Connected" tab filter), (4) Interest Constellation alignment, and (5) Localiiiy Premium Pro 5x Boost multiplier. Artificial view-botting, engagement pods, or spam tags will result in immediate algorithmic demotion to 0 external reach.
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "11.1",
                    subTitle = "Clips Clean Header & Bottom-Left Hyperlocal Badge Rule",
                    content = "In `ClipsScreen`, the top header is reserved strictly for the screen title, Auto-Scroll / Sound toggles, and the four discovery filter pills ('All', 'Trending', 'Nearby', 'Connected'). All hyperlocal proximity, distance (0.2 km, Neighbor, City, Earth), and landmark badges are anchored cleanly in the bottom-left info column above the creator handle and caption."
                ),
                LegalSubSection(
                    subNumber = "11.2",
                    subTitle = "Truth in Local Advertising & Grounded News Integrity",
                    content = "Posts tagged as local community announcements, service gigs, or neighborhood news must be factually accurate and must not fabricate public emergencies or fraudulent commercial offers."
                )
            ),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_12_appstore_playstore_regulatory_eula",
            clauseNumber = "Section 12.0",
            anchorId = "#regulatory",
            title = "Apple App Store (Guideline 1.2) & Google Play Store Developer Program Regulatory Compliance & Universal EULA",
            category = LegalCategory.REGULATORY,
            plainSummary = "Full compliance with Apple App Store Review Guideline 1.2 and Google Play Developer Program Policies: binding End User License Agreement (EULA), zero tolerance for objectionable UGC, 1-tap reporting on every post/clip/message, 24-hour moderation SLA, instant 2-way blocking, zero-permission Android Photo Picker, and 1-tap permanent Account Deletion.",
            legalText = """
12.1 Apple App Store Review Guideline 1.2 (User-Generated Content) Mandatory Provisions:
To satisfy Apple Inc.'s App Store Review Guidelines for social networking and user-generated content (UGC) platforms:
(a) Mandatory EULA Prerequisite: No user may publish UGC or interact with neighbors without first accepting this binding End User License Agreement (EULA) during registration;
(b) Zero-Tolerance Automated & Human Content Filtering: Objectionable, sexually explicit, defamatory, violent, or unlawful content is filtered at upload and strictly prohibited;
(c) Ubiquitous 1-Tap Reporting Mechanism: Every Pulse post, 24h Story, 9:16 Clip, Studio video, Marketplace listing, Direct Message, and User Profile features a 1-tap Report action;
(d) 24-Hour Moderation Service Level Agreement (SLA): Our Trust & Safety team reviews and acts upon all flagged objectionable content within a maximum of twenty-four (24) hours by removing offending media and ejecting abusive accounts;
(e) Instantaneous 2-Way User Blocking: Blocking any user immediately removes their content from your feed and purges all bidirectional visibility.

12.2 Google Play Developer Program & Creator Track Policy Compliance:
In strict compliance with Google Play Store policies:
(a) Zero-Permission Media Selection (`PickVisualMedia`): Localiiiy uses the official Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) for selecting avatars, photos, and videos without requesting broad, invasive storage permissions (`READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES`);
(b) Least-Privilege Hardware Access: Camera, Microphone, Location, and Biometric APIs are invoked strictly in context when the user initiates a corresponding action (e.g. `CreatorClipsCameraScreen`, `LiveRadarScreen`, `BiometricPrompt`);
(c) Prohibition of Dynamic Code Loading: The application never downloads or executes external `.dex`, `.jar`, or unverified `.so` binaries at runtime;
(d) In-App & Web Account Deletion Parity: Users can permanently delete their account and purge all local/cloud data in-app (`Settings & Privacy -> Complete Account & Data Oblivion`) or via our web portal (`https://localiiiy.web.app/privacy.html`).
            """.trimIndent(),
            subSections = listOf(
                LegalSubSection(
                    subNumber = "12.1",
                    subTitle = "Third-Party Beneficiary Acknowledgment",
                    content = "You acknowledge that this EULA is concluded between you and Localiiiy only, and not with Apple Inc. or Google LLC, though Apple and Google and their subsidiaries are third-party beneficiaries of the EULA compliance provisions."
                )
            ),
            isHighlighted = true
        )
    )
}
