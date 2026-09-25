package com.example.data.copyright

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * YouTube-style Copyright Claim, Revenue Rerouting, and Strike Management Engine.
 */
object CopyrightManager {

    private val _activeClaims = MutableStateFlow<List<CopyrightClaim>>(emptyList())
    val activeClaims: StateFlow<List<CopyrightClaim>> = _activeClaims.asStateFlow()

    private val _activeStrikes = MutableStateFlow<List<CopyrightStrike>>(emptyList())
    val activeStrikes: StateFlow<List<CopyrightStrike>> = _activeStrikes.asStateFlow()

    private val _reroutedBalancesUsd = MutableStateFlow<Map<String, Double>>(emptyMap())
    val reroutedBalancesUsd: StateFlow<Map<String, Double>> = _reroutedBalancesUsd.asStateFlow()

    init {
        // Pre-seed sample claim to demonstrate workflow
        val sampleClaim = CopyrightClaim(
            claimId = "claim_101",
            targetContentId = "clip_992",
            targetContentType = "CLIP",
            contentTitle = "Downtown Sunset Timelapse Remapping",
            claimantHandle = "alex_creative",
            uploaderHandle = "ripper_99",
            reason = "Unauthorized split-screen remapping without attribution or license agreement.",
            status = ClaimStatus.PENDING_JUSTIFICATION,
            reroutedAdRevenueUsd = 45.20,
            reroutedViewPayoutsUsd = 18.50,
            reroutedMarketCommissionsUsd = 0.0
        )
        _activeClaims.value = listOf(sampleClaim)
        _reroutedBalancesUsd.value = mapOf("alex_creative" to 63.70)
    }

    /**
     * File a 1-tap copyright claim on any clip, post, or marketplace item.
     * Content is immediately shifted to quarantined/unlisted review state.
     */
    fun fileCopyrightClaim(
        targetContentId: String,
        targetContentType: String,
        contentTitle: String,
        claimantHandle: String,
        uploaderHandle: String,
        reason: String
    ): CopyrightClaim {
        val newClaim = CopyrightClaim(
            claimId = "claim_${System.currentTimeMillis()}",
            targetContentId = targetContentId,
            targetContentType = targetContentType,
            contentTitle = contentTitle,
            claimantHandle = claimantHandle.removePrefix("@"),
            uploaderHandle = uploaderHandle.removePrefix("@"),
            reason = reason,
            status = ClaimStatus.PENDING_JUSTIFICATION,
            reroutedAdRevenueUsd = 0.0,
            reroutedViewPayoutsUsd = 0.0,
            reroutedMarketCommissionsUsd = 0.0
        )
        _activeClaims.value = _activeClaims.value + newClaim
        return newClaim
    }

    /**
     * Reroutes earnings: 100% of ad revenue, view payouts, and market commissions
     * from claimed content go to the original verified creator. Uploader receives 0.
     */
    fun rerouteRevenueForClaim(
        claimId: String,
        adAmountUsd: Double = 0.0,
        viewsPayoutUsd: Double = 0.0,
        marketCommissionUsd: Double = 0.0
    ) {
        val currentList = _activeClaims.value.toMutableList()
        val index = currentList.indexOfFirst { it.claimId == claimId }
        if (index != -1) {
            val claim = currentList[index]
            val updated = claim.copy(
                reroutedAdRevenueUsd = claim.reroutedAdRevenueUsd + adAmountUsd,
                reroutedViewPayoutsUsd = claim.reroutedViewPayoutsUsd + viewsPayoutUsd,
                reroutedMarketCommissionsUsd = claim.reroutedMarketCommissionsUsd + marketCommissionUsd
            )
            currentList[index] = updated
            _activeClaims.value = currentList

            // Credit original owner's wallet
            val currentBalances = _reroutedBalancesUsd.value.toMutableMap()
            val totalRerouted = adAmountUsd + viewsPayoutUsd + marketCommissionUsd
            currentBalances[claim.claimantHandle] = (currentBalances[claim.claimantHandle] ?: 0.0) + totalRerouted
            _reroutedBalancesUsd.value = currentBalances
        }
    }

    /**
     * Uploader submits a counter-notification within 7 days.
     */
    fun submitCounterNotification(
        claimId: String,
        justificationText: String,
        proofUrl: String? = null
    ): Boolean {
        val currentList = _activeClaims.value.toMutableList()
        val index = currentList.indexOfFirst { it.claimId == claimId }
        if (index == -1) return false

        val claim = currentList[index]
        if (System.currentTimeMillis() > claim.counterNotificationDeadline) {
            // Deadline expired
            return false
        }

        val updated = claim.copy(
            status = ClaimStatus.JUSTIFICATION_SUBMITTED,
            counterNotificationText = justificationText,
            counterNotificationProofUrl = proofUrl
        )
        currentList[index] = updated
        _activeClaims.value = currentList
        return true
    }

    /**
     * Original creator reviews counter-notification:
     * - If accept: claim dropped, content restored to public without penalty.
     * - If reject: willful theft confirmed, 1 Official Copyright Strike issued to uploader.
     */
    fun resolveCounterNotification(
        claimId: String,
        accepted: Boolean,
        strikeReason: String = "Willful copyright infringement verified by rights owner."
    ) {
        val currentList = _activeClaims.value.toMutableList()
        val index = currentList.indexOfFirst { it.claimId == claimId }
        if (index == -1) return

        val claim = currentList[index]
        if (accepted) {
            // Claim dropped, restored to public
            currentList[index] = claim.copy(status = ClaimStatus.JUSTIFIED_ACCEPTED)
            _activeClaims.value = currentList
        } else {
            // Claim rejected -> issue strike
            currentList[index] = claim.copy(status = ClaimStatus.STRIKE_ISSUED)
            _activeClaims.value = currentList

            val strike = CopyrightStrike(
                strikeId = "strike_${System.currentTimeMillis()}",
                offenderHandle = claim.uploaderHandle,
                claimantHandle = claim.claimantHandle,
                contentId = claim.targetContentId,
                contentType = claim.targetContentType,
                reason = strikeReason,
                isWillfulTheft = true
            )
            _activeStrikes.value = _activeStrikes.value + strike
        }
    }

    /**
     * Counts active strikes for a user within the last 90 days.
     */
    fun getActiveStrikesCount(userHandle: String): Int {
        val clean = userHandle.removePrefix("@")
        val now = System.currentTimeMillis()
        return _activeStrikes.value.count {
            it.offenderHandle.equals(clean, ignoreCase = true) && it.expiresAt > now
        }
    }

    /**
     * Strike Penalty check: 3 active strikes within 90 days results in permanent suspension
     * of upload capabilities and marketplace listing privileges.
     */
    fun isUserSuspended(userHandle: String): Boolean {
        return getActiveStrikesCount(userHandle) >= 3
    }

    /**
     * Checks if a specific piece of content is quarantined due to an active copyright claim.
     */
    fun isContentQuarantined(contentId: String): Boolean {
        return _activeClaims.value.any {
            it.targetContentId == contentId && it.isQuarantined
        }
    }
}
