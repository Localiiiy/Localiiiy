package com.example.data.legal

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LegalCategory(val title: String, val iconEmoji: String) {
    ALL("All Clauses", "📜"),
    CYBERSTALKING("Cyberstalking Zero-Tolerance", "⚖️🚫"),
    LOCATION("Location & Radar", "📍"),
    PRIVACY("Privacy & No-Sell", "🛡️"),
    MARKETPLACE("Market & Finance", "🛍️"),
    COMMUNITY("Safety & Conduct", "🤝"),
    IP_RIGHTS("Content & IP", "🎨"),
    DISCLAIMERS("Emergency & Legal", "⚖️")
}

data class LegalClause(
    val id: String,
    val clauseNumber: String,
    val title: String,
    val category: LegalCategory,
    val plainSummary: String, // TL;DR in engaging, plain language
    val legalText: String,    // Formal, legally enforceable terms
    val isHighlighted: Boolean = false
)

data class LegalConsentRecord(
    val username: String,
    val version: String = "v2026.9.1-EN",
    val timestamp: Long = System.currentTimeMillis(),
    val isConsentActive: Boolean = true,
    val legalJurisdiction: String = "Global Civil Law & Individual Privacy Sovereignty",
    val consentProofHash: String = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a (z)", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object LegalPolicyRepository {

    val CURRENT_POLICY_VERSION = "v2026.9.3-LEGAL"
    val LAST_UPDATED_DATE = "September 08, 2026"

    val defaultConsentRecord = LegalConsentRecord(
        username = "alex_creative",
        version = CURRENT_POLICY_VERSION,
        timestamp = 1788343200000L, // September 3, 2026
        isConsentActive = true
    )

    val clauses = listOf(
        LegalClause(
            id = "clause_1_location_sovereignty",
            clauseNumber = "Section 1.0",
            title = "Voluntary Hyperlocation, Geofenced Hotspots & Proximity Radar Rights",
            category = LegalCategory.LOCATION,
            plainSummary = "Your physical location and geofence alerts are 100% voluntary. You choose when to broadcast, when to go ghost mode, and when to turn radar off. If location is disabled, you still enjoy full feeds curated by smart algorithms without any tracking.",
            legalText = """
1.1 Total Voluntary Sovereignty: Location sharing, proximity radar, and geofenced hotspot alerts on Localiiiy are entirely elective and voluntary. At no point is continuous background or foreground location tracking mandatory for general application browsing.

1.2 Radar, Hotspot & Ghost Mode Controls: Users maintain full autonomy to toggle off the Live Proximity Radar, switch to "Ghost Mode" (private account), disable neighborhood hotspot push alerts, or blur location to approximate boundaries at any second via the Radar HUD, Live Map, or Privacy Settings.

1.3 Off-Grid Algorithmic Browsing: When location broadcast is disabled or unavailable ("Off-Grid"), the app will NOT index, store, or transmit your device coordinates. In this state, the Local Signals feed, Explore discoveries, Marketplace, and Studio videos are delivered via generalized algorithmic curation without location fingerprinting.

1.4 No Unsolicited Telemetry: Your precise device GPS coordinates are processed locally on-device for relative distance calculations and geofence checks and are never auctioned, broadcasted to unauthorized parties, or retained longer than required for live session discovery.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_2_data_confidentiality",
            clauseNumber = "Section 2.0",
            title = "Zero Data Brokering, Confidentiality & Granular Hide/Show Privacy Controls",
            category = LegalCategory.PRIVACY,
            plainSummary = "We never sell, rent, or secretly broker your personal data. You control what you want to show or hide—comments, DMs, tags, profile status, and visibility are never compulsory and can be modified instantly.",
            legalText = """
2.1 Zero Data Selling Guarantee: The developers, owners, and operators of Localiiiy explicitly confirm that user personal data, chat transcripts, media archives, contact lists, and behavioral profiles are NEVER sold, rented, leased, or disclosed to third-party data brokers, ad networks, or commercial telemarketers.

2.2 Granular Hide/Show User Sovereignty: Privacy on Localiiiy is modular and non-compulsory. Users retain absolute power to show or hide their profile activity, restrict comments (Everyone, Neighbors Only, Mentions Only, Off), control direct messages (Anyone, Neighbors Only, Off), and manage tags/mentions. Nothing is forced or compulsory.

2.3 Universal Privacy Standards & Global Data Protection: Localiiiy adheres to the highest global data protection and privacy-by-design standards worldwide. Universal principles of strict data minimization, purpose limitation, cryptographic protection in transit and at rest, and individual privacy sovereignty apply equally to all users worldwide without distinction.

2.4 Right to Erasure & Portability: You hold the absolute civil right to download an encrypted archive of your user data or permanently erase your profile and stored assets from our systems at your sole discretion.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_3_marketplace_liability",
            clauseNumber = "Section 3.0",
            title = "Peer-to-Peer Marketplace, Studio Creator Uploads & Absolute Financial Liability Waiver",
            category = LegalCategory.MARKETPLACE,
            plainSummary = "The marketplace and creator studio are direct P2P noticeboards. We do NOT ask for or store bank cards or wallets. We have zero financial involvement and zero liability for trades or video content. Meet neighbors in public safely!",
            legalText = """
3.1 Direct Peer-to-Peer Bulletin: The Localiiiy Marketplace and Studio function exclusively as localized discovery bulletins for neighborhood goods, crafts, and creator videos. Localiiiy is neither a financial institution, licensed broker, escrow agent, nor merchant of record.

3.2 No Financial Rights & No Stored Banking Credentials: Localiiiy does NOT request, process, collect, or store payment card details, bank account numbers, digital cryptocurrency wallets, or credit credentials. All financial exchanges occur strictly and mutually between the buyer and seller.

3.3 Absolute Financial & Content Loss Disclaimer: The developers, administrators, and platform hosts bear ZERO financial, civil, or legal liability for any monetary losses, damaged items, misdescribed listings, cyber fraud, stolen goods, or counterparty default arising from marketplace or studio engagements.

3.4 Cyber Fraud Advisory & Safe Meetup Protocol: Users are strongly advised to beware of online advance-fee scams, third-party payment links, and wire transfer fraud. Users should inspect items in person, conduct transactions in well-lit public community locations, and verify items prior to any monetary exchange.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_4_community_safety",
            clauseNumber = "Section 4.0",
            title = "Community Safety, Respectful Conduct & Anti-Harassment",
            category = LegalCategory.COMMUNITY,
            plainSummary = "Localiiiy is built on friendly neighborhood trust. Harassment, hate speech, illegal items, stalking, and spam are strictly prohibited with instant blocking and moderation tools.",
            legalText = """
4.1 Prohibited Conduct: Users are strictly forbidden from engaging in stalking, predatory behavior, hate speech, discrimination, non-consensual imagery, harassment, or listing illicit contraband, weapons, prescription drugs, or unlawful services.

4.2 Neighbor Wave & Interaction Boundaries: Proximity waves (👋) and direct messages must remain civil. Users can mute, block, or report any bad actor instantly. Blocked users are immediately purged from your live radar, messages, and social feeds.

4.3 Instant Content Moderation: The platform reserves the right to restrict, suspend, or permanently terminate accounts violating community integrity or creating unsafe physical or digital conditions for neighbors.
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_5_intellectual_property",
            clauseNumber = "Section 5.0",
            title = "User Content Ownership & Intellectual Property Rights",
            category = LegalCategory.IP_RIGHTS,
            plainSummary = "You own your photos, studio videos, stories, and artwork. You grant Localiiiy a license to display your sparks to neighbors according to your privacy settings.",
            legalText = """
5.1 Creator Ownership: You retain 100% intellectual property ownership, copyright, and moral rights over all photographs, long-form studio video clips, stories, text captions, and creative media uploaded to your profile.

5.2 Platform Display License: By uploading content, you grant Localiiiy a non-exclusive, worldwide, royalty-free license strictly necessary to store, cache, render, and broadcast your content to other users in alignment with your selected privacy filters.

5.3 Copyright & DMCA Protection: Localiiiy respects intellectual property rights. If you believe your copyrighted work has been reproduced without authorization, submit a takedown request for immediate investigation and removal.
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_6_emergency_navigation",
            clauseNumber = "Section 6.0",
            title = "Social Discovery vs. Emergency & Tactical Navigation Disclaimer",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "Our proximity radar and geofence alerts are designed for fun social discovery and finding nearby friends—not as an emergency beacon or medical alert system.",
            legalText = """
6.1 Non-Emergency Social Tool: The live radar, distance approximations, geofence hotspot alerts, and beacon visuals are provided solely for recreational social discovery, street spark exploration, and community networking.

6.2 Critical Safety Disclaimer: Localiiiy is NOT an emergency dispatch service, medical alert system, or tactical navigation utility. In the event of an emergency, physical threat, or medical distress, immediately contact official government emergency response agencies (e.g., 911, 112, 999).
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_7_eligibility_jurisdiction",
            clauseNumber = "Section 7.0",
            title = "Account Eligibility, Minor Safety & Dispute Resolution",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "Users must be at least 13 years old (or local age of digital consent). Disputes are handled with fair, good-faith civil communication and standard arbitration.",
            legalText = """
7.1 Age Eligibility: You must be at least 13 years of age (or 16 in jurisdictions governed by specific minor digital protection mandates) to create an account and access proximity services.

7.2 Good Faith Dispute Resolution: In the event of any platform controversy, users agree to engage in informal good-faith negotiation prior to initiating formal civil arbitration in their respective legal jurisdiction.

7.3 Severability: If any provision of this agreement is determined to be unenforceable by a court of competent jurisdiction, the remaining clauses shall continue in full force and effect.
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_8_consent_storage",
            clauseNumber = "Section 8.0",
            title = "Persistent Local Consent Proof & Timestamp Verification",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "Your agreement is recorded with a secure timestamp and stored in your device's local Room database for permanent legal transparency.",
            legalText = """
8.1 Binding Acceptance: Tapping "I Acknowledge & Agree" during account signup or inside settings constitutes your legally binding consent to this App Agreement, Community Charter, and Privacy Policy.

8.2 Local Room Database Certification: To ensure transparent proof of compliance without centralized telemetry, your acceptance timestamp, agreement version, and verification hash are archived locally on your device in the Room SQLite database.
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_9_cyberstalking_zero_tolerance",
            clauseNumber = "Section 9.0",
            title = "Universal Zero-Tolerance Anti-Cyberstalking Policy, Global Data Protection & Permanent Account Suspension",
            category = LegalCategory.CYBERSTALKING,
            plainSummary = "Cyberstalking, persistent harassment, and unauthorized location tracking are strictly prohibited under our universal Zero Tolerance Policy. Localiiiy enforces immediate and permanent account suspension and device hardware bans for offenders with zero right of appeal. All incidents and chat records can be securely exported to internal storage as non-tampered, timestamped digital evidence aligning with international evidence preservation standards.",
            legalText = """
9.1 Universal Definition of Cyberstalking Offenses:
Cyberstalking and malicious electronic pursuit are recognized as severe offenses under universal international digital standards. On Localiiiy, prohibited conduct includes:
(a) Persistently contacting, messaging, calling, or attempting to communicate with any individual via chat, proximity radar, stories, studio videos, or comments despite clear indications of disinterest;
(b) Monitoring, surveilling, or tracking a person's physical location, movement, or online interactions using radar, geofenced alerts, or electronic telemetry without explicit ongoing consent;
(c) Evasion of user blocks or restrictions by creating secondary, throwaway, or spoofed accounts;
(d) Intimidation, extortion, non-consensual sharing of private imagery, or transmitting obscene or threatening communications.

9.2 Universal Zero-Tolerance Enforcement & Permanent Account Suspension:
Localiiiy maintains an uncompromising Zero-Tolerance policy against all forms of cyberstalking and electronic harassment:
(a) Immediate Account Suspension: Any account substantiated to have engaged in cyberstalking, unauthorized tracking, or harassment will be immediately and permanently suspended;
(b) Hardware & Device Level Bans: Device identifiers, cryptographic hardware signatures, and network fingerprints are permanently blacklisted to prevent re-registration or bypass;
(c) Zero Right of Appeal: Sanctions imposed for verified stalking or harassment offenses are definitive, irrevocable, and non-negotiable.

9.3 Global Standards of Data Protection & Privacy-by-Design:
In strict compliance with international data protection protocols and privacy covenants worldwide:
(a) User data, location coordinates, and communication channels are protected with cryptographic safeguards and strict data minimization;
(b) Personal information is never exposed to unauthorized parties, and location precision is governed by granular user sovereignty;
(c) Complainants retain full rights to request immediate data preservation and protective privacy lockdown.

9.4 Universal Multi-Channel Reporting Mechanisms:
Localiiiy provides rapid, robust, and accessible reporting mechanisms for all users worldwide:
(a) Instant In-App Reporting: Users can trigger one-tap reporting and immediate blocking from any chat thread, user profile, radar sighting, or content post;
(b) 24/7 Priority Moderation: Reports involving cyberstalking, physical safety threats, or unauthorized tracking are automatically prioritized for emergency review and defensive mitigation;
(c) Immediate Protective Isolation: Initiating a report instantly severs all proximity radar broadcasts, direct messaging channels, and visibility between the complainant and accused offender.

9.5 Standard Digital Evidence Preservation & Safety Log Directory:
To support international digital evidence preservation practices (conforming to ISO/IEC 27037 standards):
(a) The application maintains a dedicated internal storage directory (`/safety_logs/`) allowing users to export chat histories and forensic metadata securely for their own records;
(b) Exported logs and on-device incident records are cryptographically sealed with SHA-256 digests ensuring non-tampered, timestamped integrity;
(c) Automated statutory evidence certificates are generated for direct presentation to legal counsel, magistrates, courts, and law enforcement agencies worldwide;
(d) Records are strictly append-only and cryptographically verified against post-generation alteration or modification.

9.6 Global Law Enforcement Cooperation Worldwide:
Localiiiy complies with international mutual legal assistance treaties, judicial subpoenas, and lawful orders from certified law enforcement agencies globally to hold offenders accountable under applicable penal laws worldwide.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_10_nda_confidentiality_cybercrime",
            clauseNumber = "Section 10.0",
            title = "NDA (Non-Disclosure Agreement) / Confidentiality & Cyber Crime Deterrence Agreement",
            category = LegalCategory.CYBERSTALKING,
            plainSummary = "Binding legal contract: You agree not to stalk neighbours as it is a recognized cyber crime, and never use this app for illegal activities or crime. If found, all details, logs, timestamps, and device records will be shared with concerned law enforcement authorities for investigation and are legally valid in a court of law. Violations carry severe punishment under the penal laws of your country. We are firm about the law and respect law and order globally.",
            legalText = """
10.1 Binding NDA & Confidentiality Legal Covenant:
This Non-Disclosure Agreement (NDA) and Confidentiality Covenant is an enforceable legal agreement executed between you (the registering user) and the Localiiiy platform. By proceeding with account registration, you unconditionally agree, attest, and warrant:

(a) Absolute Prohibition of Neighbour Stalking: You agree not to stalk neighbours, friends, or any individuals using this application. You acknowledge that stalking, electronic pursuit, and unauthorized surveillance are recognized cyber crimes under municipal, domestic, and international law;
(b) Prohibition of Illegal Activities: You agree never to use this platform, its proximity radar, creator studio, messaging, or geolocation services for any illegal activities, criminal acts, harassment, trespass, extortion, or intimidation;
(c) Mandatory Law Enforcement Evidence Sharing: If you are found or reasonably suspected of engaging in cyberstalking, harassment, unlawful surveillance, or illegal activities, you explicitly agree that all account details, identity records, device fingerprints, GPS timestamps, IP addresses, and communication logs will be immediately preserved and shared with concerned law enforcement authorities as per law for investigation;
(d) Admissibility in Court of Law: All forensic telemetry, cryptographic audit logs, and digital incident archives generated and maintained by Localiiiy are fully valid and admissible as evidence in a court of law in any jurisdiction;
(e) Severe Statutory Punishment Under Country's Law: Any breach or violation of this agreement will result in immediate and permanent account termination, hardware blacklisting, and direct criminal referral. Violators will face the most severe criminal and civil punishments prescribed under the penal code and statutes of their country;
(f) Firm Respect for Law and Order Worldwide: We are unwavering and firm about the law. Localiiiy strictly respects and enforces the law and order of all sovereign countries globally to protect community safety and deter unlawful misuse.
            """.trimIndent(),
            isHighlighted = true
        )
    )
}
