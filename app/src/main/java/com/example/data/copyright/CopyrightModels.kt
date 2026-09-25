package com.example.data.copyright

/**
 * Configuration for licensing and reuse rights chosen by a creator during media upload.
 */
data class ContentLicensingConfig(
    val permitReuse: Boolean = true,
    val allowAudioReuse: Boolean = true,
    val allowVideoRemapping: Boolean = true,
    val allowMarketplaceShowcase: Boolean = true,
    val licenseBadge: String = if (permitReuse) "Localiiiy Creative Commons (LCC)" else "All Rights Reserved (ARR)",
    val creatorHandle: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val isCreativeCommons: Boolean get() = permitReuse
    val shortBadge: String get() = if (permitReuse) "LCC 🛡️" else "ARR 🔒"
}

/**
 * Cryptographic and acoustic/perceptual fingerprint computed on upload.
 */
data class MediaFingerprint(
    val id: String,
    val contentUriOrUrl: String,
    val ownerHandle: String,
    val sha256Checksum: String,
    val perceptualHash: String, // 64-bit hex pHash for visual duplicate detection
    val acousticSpectralPeaks: List<Int>, // Discrete frequency peaks for audio matching
    val licensingConfig: ContentLicensingConfig,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Status of a YouTube-style copyright claim.
 */
enum class ClaimStatus {
    PENDING_JUSTIFICATION, // Contested content in unlisted/quarantined review state
    JUSTIFICATION_SUBMITTED,
    JUSTIFIED_ACCEPTED, // Original creator accepted justification, restored to public
    STRIKE_ISSUED, // Original creator rejected, confirmed willful theft
    RESOLVED_REVOKED
}

/**
 * A copyright claim filed against a clip, post, or marketplace listing.
 */
data class CopyrightClaim(
    val claimId: String,
    val targetContentId: String,
    val targetContentType: String, // "CLIP", "POST", "MARKET", "STUDIO"
    val contentTitle: String,
    val claimantHandle: String, // Original verified creator
    val uploaderHandle: String, // User who uploaded contested content
    val reason: String,
    val status: ClaimStatus = ClaimStatus.PENDING_JUSTIFICATION,
    val filedAt: Long = System.currentTimeMillis(),
    val counterNotificationDeadline: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // 7 days
    val counterNotificationText: String? = null,
    val counterNotificationProofUrl: String? = null,
    val reroutedAdRevenueUsd: Double = 0.0,
    val reroutedViewPayoutsUsd: Double = 0.0,
    val reroutedMarketCommissionsUsd: Double = 0.0
) {
    val totalReroutedEarningsUsd: Double
        get() = reroutedAdRevenueUsd + reroutedViewPayoutsUsd + reroutedMarketCommissionsUsd

    val isQuarantined: Boolean
        get() = status == ClaimStatus.PENDING_JUSTIFICATION || status == ClaimStatus.JUSTIFICATION_SUBMITTED || status == ClaimStatus.STRIKE_ISSUED

    val daysRemainingForJustification: Long
        get() = maxOf(0L, (counterNotificationDeadline - System.currentTimeMillis()) / (24 * 60 * 60 * 1000))
}

/**
 * Official Copyright Strike issued for willful media theft.
 * 3 active strikes within 90 days results in permanent suspension.
 */
data class CopyrightStrike(
    val strikeId: String,
    val offenderHandle: String,
    val claimantHandle: String,
    val contentId: String,
    val contentType: String,
    val reason: String,
    val issuedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000), // 90 days
    val isWillfulTheft: Boolean = true
) {
    val isActive: Boolean get() = System.currentTimeMillis() < expiresAt
}
