package com.example.data.legal

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LegalCategory(val title: String, val iconEmoji: String) {
    ALL("All 24 Articles", "📜"),
    REGISTRATION_IDENTITY("Registration & 2-Way OTP", "🔐"),
    POST_POLICY("Post Policy & Dual-Reach (0km–Earth)", "📝🌍"),
    ALGORITHM_DUAL_REACH("Algorithm & Constellations", "🛰️✨"),
    PULSE_STORIES_CLIPS("Pulse, Stories & Clips", "🎬📱"),
    STUDIO_CREATOR_TOOLS("4K Studio, Drafts & Bounties", "🎥🏆"),
    IP_RIGHTS("Localiiiy Copyright & LCC", "©️🛡️"),
    MARKETPLACE("Market, 5% Escrow & Gigs", "🛍️🤝"),
    MONETIZATION("Monetization & $1k Payouts", "💰💵"),
    CYBERSTALKING("Anti-Cyberstalking & NDA", "⚖️🚫"),
    LOCATION("Location, Radar & Ghost Mode", "📍👻"),
    PRIVACY("Privacy & Zero-Data-Sell", "🛡️🔒"),
    COMMUNITY("Connections & Conduct", "🤝💬"),
    REGULATORY("App Store & Play Store EULA", "🏛️✅"),
    ACCOUNT_DELETION_OBLIVION("Logout, Scrub & Data Oblivion", "🧹🗑️"),
    DISCLAIMERS("Emergency & Disclaimers", "⚖️⚠️")
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
    val version: String = "v2026.10.17-OMNIBUS-GLOBAL-CHARTER",
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

    const val CURRENT_POLICY_VERSION = "v2026.10.17-OMNIBUS-GLOBAL-CHARTER"
    const val LAST_UPDATED_DATE = "October 17, 2026"
    const val TOTAL_MASTER_SECTIONS = 24

    val defaultConsentRecord = LegalConsentRecord(
        username = "alex_creative",
        version = CURRENT_POLICY_VERSION,
        timestamp = 1792224000000L, // October 17, 2026
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
        appendLine("NDA, COPYRIGHT PROTOCOL, DUAL-REACH (0 KM TO EARTH) & PRIVACY POLICY")
        appendLine("======================================================================")
        appendLine("Version: $CURRENT_POLICY_VERSION")
        appendLine("Last Updated: $LAST_UPDATED_DATE")
        appendLine("Signatory Handle: @$currentUsername")
        appendLine("Consent Status: ${if (isAccepted) "Accepted, Signed & Binding" else "Pending User Acknowledgment"}")
        appendLine("Jurisdiction: Universal Civil Privacy & Individual Sovereignty")
        appendLine("Coverage: Registration -> 2-Way OTP -> Pulse -> Stories -> Clips ->")
        appendLine("          0km-to-Earth Algorithm -> Space Radar -> 4K Studio ->")
        appendLine("          Localiiiy Copyright (LCC / 3-Strike) -> Market & 5% Escrow ->")
        appendLine("          Monetization & $1,000 USD Payouts -> Anti-Cyberstalking NDA ->")
        appendLine("          Forensic /safety_logs/ -> Settings -> Logout & Data Oblivion")
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
        appendLine("================ END OF LOCALIIIY MASTER OMNIBUS CHARTER ================")
    }
}
