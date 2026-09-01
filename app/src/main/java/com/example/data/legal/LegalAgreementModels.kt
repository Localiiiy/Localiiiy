package com.example.data.legal

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LegalCategory(val title: String, val iconEmoji: String) {
    ALL("All Clauses", "📜"),
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

    val CURRENT_POLICY_VERSION = "v2026.9.1-EN"
    val LAST_UPDATED_DATE = "September 01, 2026"

    val defaultConsentRecord = LegalConsentRecord(
        username = "alex_creative",
        version = CURRENT_POLICY_VERSION,
        timestamp = 1788256800000L, // September 1, 2026
        isConsentActive = true
    )

    val clauses = listOf(
        LegalClause(
            id = "clause_1_location_sovereignty",
            clauseNumber = "Section 1.0",
            title = "Hyperlocal Location Sovereignty & Proximity Radar Rights",
            category = LegalCategory.LOCATION,
            plainSummary = "Your physical location is 100% your choice. You choose when to broadcast, when to go ghost mode, and when to turn radar off. If location is disabled, you still enjoy full feeds curated by smart algorithms without any tracking.",
            legalText = """
1.1 Individual Sovereignty: Location sharing on Localiiiy is entirely elective and voluntary. At no point is continuous background or foreground location tracking mandatory for general application browsing.

1.2 Radar & Ghost Mode Controls: Users maintain full autonomy to toggle off the Live Proximity Radar, switch to "Ghost Mode" (private account), or blur location to approximate neighborhood boundaries at any second via the Radar HUD, Live Map, or Privacy Settings.

1.3 Off-Grid Algorithmic Browsing: When location broadcast is disabled or unavailable ("Off-Grid"), the app will NOT index, store, or transmit your device coordinates. In this state, the Local Signals feed, Explore discoveries, and Street Drops will be delivered via generalized algorithmic curation, trending local tags, or randomized discovery without location fingerprinting.

1.4 No Unsolicited Telemetry: Your precise device GPS coordinates are processed on-device for relative distance calculations and are never auctioned, broadcasted to unauthorized parties, or retained longer than required for live session discovery.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_2_data_confidentiality",
            clauseNumber = "Section 2.0",
            title = "Zero Data Brokering, Confidentiality & Global Privacy Compliance",
            category = LegalCategory.PRIVACY,
            plainSummary = "We never sell, rent, or secretly broker your personal data with anyone. Your data belongs to you. We honor global civil privacy laws (GDPR, CCPA, and national privacy codes) with instant user controls.",
            legalText = """
2.1 Zero Data Selling Guarantee: The developers, owners, and operators of Localiiiy explicitly confirm that user personal data, chat transcripts, media archives, contact lists, and biometric/behavioral profiles are NEVER sold, rented, leased, or disclosed to third-party data brokers, ad networks, or commercial telemarketers.

2.2 Global & Civil Privacy Adaptability: Privacy and civil protection laws vary across jurisdictions (including the European Union General Data Protection Regulation (GDPR), California Consumer Privacy Act (CCPA/CPRA), UK Data Protection Act, and diverse international civil codes). Localiiiy enforces the highest standard of universal privacy sovereignty across all regions.

2.3 Unrestricted Privacy Settings: Users are provided with comprehensive, instant in-app privacy controls. You may modify comments access, message permissions, story visibility, and discoverability at any time without fees, penalties, or artificial waiting periods.

2.4 Right to Erasure & Portability: You hold the absolute civil right to download an encrypted archive of your user data or permanently erase your profile and stored assets from our systems at your sole discretion.
            """.trimIndent(),
            isHighlighted = true
        ),

        LegalClause(
            id = "clause_3_marketplace_liability",
            clauseNumber = "Section 3.0",
            title = "Peer-to-Peer Marketplace & Absolute Financial Liability Waiver",
            category = LegalCategory.MARKETPLACE,
            plainSummary = "The neighborhood marketplace is a direct neighbor-to-neighbor noticeboard. We do NOT ask for or store bank cards or wallets. We have zero financial involvement and zero liability for losses. Beware of cyber scams—always meet neighbors in person in public spots!",
            legalText = """
3.1 Direct Peer-to-Peer Noticeboard: The Localiiiy Marketplace functions exclusively as a localized discovery bulletin for neighborhood goods, crafts, and equipment. Localiiiy is neither a financial institution, licensed broker, escrow agent, nor merchant of record.

3.2 No Financial Rights & No Stored Banking Credentials: Localiiiy does NOT request, process, collect, or store payment card details, bank account numbers, digital cryptocurrency wallets, or credit credentials for marketplace transactions. All financial exchanges occur strictly and mutually between the buyer and seller.

3.3 Absolute Financial Loss Disclaimer: The developers, administrators, contributors, and platform hosts bear ZERO financial, civil, or legal liability for any monetary losses, damaged items, misdescribed listings, cyber fraud, stolen goods, non-deliveries, or counterparty default arising from marketplace engagements.

3.4 Cyber Fraud Advisory & Safe Meetup Protocol: Users are strongly advised to beware of online advance-fee scams, third-party payment links, counterfeit digital payment screenshots, and wire transfer fraud. Users are advised to inspect items in person, conduct transactions in well-lit public community locations (e.g., community centers, daylight public squares), and verify items prior to any monetary exchange.
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
            plainSummary = "You own your photos, videos, stories, and artwork. You grant Localiiiy a license to display your sparks to neighbors according to your privacy settings.",
            legalText = """
5.1 Creator Ownership: You retain 100% intellectual property ownership, copyright, and moral rights over all photographs, clips, stories, text captions, and creative media uploaded to your profile.

5.2 Platform Display License: By uploading content, you grant Localiiiy a non-exclusive, worldwide, royalty-free license strictly necessary to store, cache, render, and broadcast your content to other users in alignment with your selected privacy filters.

5.3 Copyright & DMCA Protection: Localiiiy respects intellectual property rights. If you believe your copyrighted work has been reproduced without authorization, submit a takedown request for immediate investigation and removal.
            """.trimIndent()
        ),

        LegalClause(
            id = "clause_6_emergency_navigation",
            clauseNumber = "Section 6.0",
            title = "Social Discovery vs. Emergency & Tactical Navigation Disclaimer",
            category = LegalCategory.DISCLAIMERS,
            plainSummary = "Our proximity radar is designed for fun social discovery and finding nearby friends—it is not an emergency beacon, medical alert system, or aviation tool.",
            legalText = """
6.1 Non-Emergency Social Tool: The live radar, distance approximations, and beacon visuals are provided solely for recreational social discovery, street spark exploration, and community networking.

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
        )
    )
}
