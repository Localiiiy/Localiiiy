package com.example.data.legal

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LegalCategory(val title: String, val iconEmoji: String) {
    ALL("All 36 Articles", "📜"),
    DEVELOPER_SAFE_HARBOR("Developer Non-Liability & User Choice", "🛡️⚖️"),
    RADAR_18_PLUS_GATE("18+ Radar Lock & Minor Protection", "🔞📡"),
    REGISTRATION_IDENTITY("Registration & 2-Way OTP", "🔐"),
    POST_POLICY("Post Policy & Dual-Reach (0km–Earth)", "📝🌍"),
    ALGORITHM_DUAL_REACH("Algorithm & Constellations", "🛰️✨"),
    PULSE_STORIES_CLIPS("Pulse, Stories & Clips", "🎬📱"),
    CIVIC_TOOLS_COMMUNITY("15 Civic Tools & Community Hub", "🏛️🚑"),
    STUDIO_CREATOR_TOOLS("4K Studio, Drafts & Bounties", "🎥🏆"),
    IP_RIGHTS("Localiiiy Copyright & LCC SHA-256 Hash", "©️🛡️"),
    MARKETPLACE("Market, 5% Escrow & Gigs", "🛍️🤝"),
    MONETIZATION("Monetization & $1k Payouts", "💰💵"),
    CYBERSTALKING("Anti-Cyberstalking, NDA & Forensic Logs", "⚖️🚫"),
    LOCATION("Location, Radar 2.0 & Ghost Mode", "📍👻"),
    PRIVACY("Privacy & Zero-Data-Sell", "🛡️🔒"),
    COMMUNITY("Connections & Conduct", "🤝💬"),
    REGULATORY("App Store, Play Store & Web Sync EULA", "🏛️✅"),
    ACCOUNT_DELETION_OBLIVION("Logout, Scrub & Data Oblivion", "🧹🗑️"),
    DISCLAIMERS("Emergency, Helpdesk & Disclaimers", "⚖️⚠️")
}

data class LegalSubSection(
    val subNumber: String,
    val subTitle: String,
    val content: String
)

data class LegalClause(
    val id: String,
    val clauseNumber: String,
    val title: String,
    val category: LegalCategory,
    val plainSummary: String, // TL;DR in engaging, plain language
    val legalText: String,    // Formal, legally enforceable terms
    val isHighlighted: Boolean = false,
    val anchorId: String = "#${id}",
    val subSections: List<LegalSubSection> = emptyList()
)

data class LegalConsentRecord(
    val username: String,
    val version: String = "v2026.11.07-OMNIBUS-GLOBAL-CHARTER",
    val timestamp: Long = System.currentTimeMillis(),
    val isConsentActive: Boolean = true,
    val legalJurisdiction: String = "Global Civil Law, Developer Safe-Harbor & Individual Privacy Sovereignty",
    val consentProofHash: String = "SHA256:9e47c2818ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9088"
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a (z)", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object LegalPolicyRepository {

    const val CURRENT_POLICY_VERSION = "v2026.11.07-OMNIBUS-GLOBAL-CHARTER"
    const val LAST_UPDATED_DATE = "November 7, 2026"
    const val TOTAL_MASTER_SECTIONS = 36
    const val OFFICIAL_HELPDESK_EMAIL = "Localiiiy.Helpdesk@gmail.com"

    val defaultConsentRecord = LegalConsentRecord(
        username = "alex_creative",
        version = CURRENT_POLICY_VERSION,
        timestamp = 1794038400000L, // November 7, 2026
        isConsentActive = true
    )

    val clauses: List<LegalClause> by lazy {
        LegalClausesPart1.clauses +
            LegalClausesPart2.clauses +
            LegalClausesPart3.clauses +
            LegalClausesPart4.clauses
    }

    /**
     * Generates the complete formatted Master Omnibus Legal Document for clipboard export,
     * registration scroll-to-accept inspection, and statutory archival.
     */
    fun buildFullOmnibusDocumentText(
        currentUsername: String = "alex_creative",
        isAccepted: Boolean = true
    ): String = buildString {
        appendLine("======================================================================")
        appendLine("LOCALIIIY MASTER OMNIBUS LEGAL AGREEMENT, COMMUNITY CHARTER,")
        appendLine("DEVELOPER SAFE-HARBOR, 18+ RADAR LOCK, LCC SHA-256 HASH PROTOCOL,")
        appendLine("NDA, DUAL-REACH (0 KM TO EARTH), 15 CIVIC TOOLS & PRIVACY POLICY")
        appendLine("======================================================================")
        appendLine("Version: $CURRENT_POLICY_VERSION")
        appendLine("Last Updated: $LAST_UPDATED_DATE")
        appendLine("Exclusive Official Helpdesk & Grievance Email: $OFFICIAL_HELPDESK_EMAIL")
        appendLine("Signatory Handle: @$currentUsername")
        appendLine("Consent Status: ${if (isAccepted) "Accepted, Signed & Binding" else "Pending User Acknowledgment"}")
        appendLine("Jurisdiction: Universal Civil Privacy, Developer Safe-Harbor & Individual Sovereignty")
        appendLine("Coverage: 36 Master Articles — Registration -> 2-Way OTP -> Pulse ->")
        appendLine("          15 Civic Tools -> Local Community Hub -> Stories -> Clips ->")
        appendLine("          0km-to-Earth Algorithm -> 18+ Tactical Radar 2.0 -> 4K Studio ->")
        appendLine("          Localiiiy Copyright (LCC SHA-256 + pHash / 3-Strike) ->")
        appendLine("          Market & 5% Escrow -> Monetization & $1,000 USD Payouts ->")
        appendLine("          Developer/Owner Non-Liability Safe-Harbor -> Anti-Cyberstalking NDA ->")
        appendLine("          Forensic /safety_logs/ -> Web-Android Sync -> Data Oblivion")
        appendLine("======================================================================\n")

        clauses.forEach { clause ->
            appendLine("----------------------------------------------------------------------")
            appendLine("${clause.clauseNumber} [${clause.anchorId}]: ${clause.title}")
            appendLine("Category: ${clause.category.iconEmoji} ${clause.category.title}")
            appendLine("----------------------------------------------------------------------")
            appendLine("PLAIN LANGUAGE SUMMARY (TL;DR):")
            appendLine(clause.plainSummary)
            appendLine()
            appendLine("FORMAL CONTRACTUAL TERMS:")
            appendLine(clause.legalText)
            if (clause.subSections.isNotEmpty()) {
                appendLine()
                appendLine("DETAILED SUB-CLAUSES & TECHNICAL PROTOCOLS:")
                clause.subSections.forEach { sub ->
                    appendLine("  • [${sub.subNumber}] ${sub.subTitle}: ${sub.content}")
                }
            }
            appendLine("\n")
        }
        appendLine("Official Helpdesk & Legal Contact: $OFFICIAL_HELPDESK_EMAIL")
        appendLine("================ END OF LOCALIIIY MASTER OMNIBUS CHARTER ================")
    }
}
