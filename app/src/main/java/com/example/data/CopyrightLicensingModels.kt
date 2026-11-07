package com.example.data

import java.security.MessageDigest

/**
 * Decentralized Content Licensing & Rights Configuration for Localiiiy.
 * Controls permission toggles at upload time across Clips, Posts, Market, and Studio.
 */
data class ContentLicensingConfig(
    val reusePermitted: Boolean = false,
    val audioReuseAllowed: Boolean = false,
    val remixReactionAllowed: Boolean = false,
    val marketplaceShowcaseAllowed: Boolean = false,
    val licenseType: String = if (reusePermitted) LICENSE_LCC else LICENSE_ALL_RIGHTS
) {
    companion object {
        const val LICENSE_ALL_RIGHTS = "All Rights Reserved (Standard)"
        const val LICENSE_LCC = "Localiiiy Creative Commons (LCC)"
    }
}

/**
 * Copyright Claim & Strike Management Model.
 * Implements YouTube-style 7-day counter-notification window and 3-strike / 90-day penalty rule.
 */
data class CopyrightClaim(
    val claimId: String,
    val contentId: Long,
    val contentType: String, // "POST", "CLIP", "STUDIO", "MARKET"
    val contentTitle: String,
    val mediaUrl: String,
    val claimantUsername: String,
    val uploaderUsername: String,
    val infringementReason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val deadlineTimestamp: Long = timestamp + (7L * 24 * 60 * 60 * 1000), // 7 days
    val counterNotificationText: String? = null,
    val counterSubmittedTimestamp: Long? = null,
    val status: ClaimStatus = ClaimStatus.QUARANTINED,
    val reroutedRevenueUSD: Double = 0.0,
    val strikeApplied: Boolean = false
) {
    val daysRemaining: Int
        get() = ((deadlineTimestamp - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0).toInt()
}

enum class ClaimStatus {
    QUARANTINED,       // Content shifted to unlisted / quarantined review state
    COUNTER_SUBMITTED, // Uploader filed proof of authorization or ownership
    RESOLVED_ACCEPTED, // Original creator dropped claim; restored to public without penalty
    STRIKE_CONFIRMED,  // Theft confirmed; 1 official strike issued; revenue rerouted
    DISMISSED          // Claim was invalid / withdrawn
}

data class UserCopyrightRecord(
    val username: String,
    val activeStrikes: Int = 0,
    val lastStrikeTimestamp: Long = 0L,
    val isUploadSuspended: Boolean = false,
    val isMarketplaceSuspended: Boolean = false
) {
    // 3 active strikes within 90 days results in permanent suspension
    val isPermanentlySuspended: Boolean
        get() = activeStrikes >= 3
}

/**
 * Media Fingerprinting & Duplicate Detection Utility.
 * Computes cryptographic SHA-256 hashes and simulated acoustic/perceptual fingerprints.
 */
object MediaFingerprintEngine {

    fun generateFingerprint(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }.take(16)
        } catch (e: Exception) {
            "fp_" + kotlin.math.abs(input.hashCode()).toString(16)
        }
    }

    fun computePerceptualHash(mediaUri: String, caption: String): String {
        val composite = "phash_${mediaUri.trim()}_${caption.take(30).trim()}"
        return generateFingerprint(composite)
    }

    /**
     * Checks if new upload matches an existing copyrighted asset where reuse was denied.
     */
    fun checkDuplicateInfringement(
        newMediaHash: String,
        registeredFingerprints: Map<String, Pair<String, ContentLicensingConfig>>
    ): Pair<String, ContentLicensingConfig>? {
        val match = registeredFingerprints[newMediaHash] ?: return null
        val (owner, config) = match
        if (!config.reusePermitted) {
            return Pair(owner, config)
        }
        return null
    }
}
