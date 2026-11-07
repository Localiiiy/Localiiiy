package com.example.data

/**
 * Data structures for Localiiiy's Worldwide Creator Monetization,
 * Global Ad Revenue Sharing (55% Creator / 45% Platform),
 * and Multi-Currency Payout Infrastructure.
 */

data class AdPlacement(
    val id: String,
    val advertiserName: String,
    val advertiserAvatar: String,
    val headline: String,
    val description: String,
    val mediaUrl: String,
    val callToAction: String,
    val targetUrl: String,
    val cpmRateUSD: Double = 5.20,
    val cpcRateUSD: Double = 0.45,
    val isVerifiedAdvertiser: Boolean = true,
    val targetRegion: String = "Worldwide",
    val impressions: Long = 0L,
    val clicks: Long = 0L,
    val revenueGeneratedUSD: Double = 0.0
)

data class CreatorEarningsSummary(
    val monetizedViews: Long = 0L,
    val inStreamVideoAdUSD: Double = 0.0,
    val feedSponsoredAdUSD: Double = 0.0,
    val superThanksTipsUSD: Double = 0.0,
    val marketplaceSalesUSD: Double = 0.0,
    val totalGrossEarnedUSD: Double = 0.0,
    val availableBalanceUSD: Double = 0.0,
    val pendingPayoutUSD: Double = 0.0,
    val lifetimePayoutsUSD: Double = 0.0,
    val creatorSharePercentage: Int = 55, // 55% Creator Share
    val platformSharePercentage: Int = 45  // 45% Platform Share
)

data class CreatorPayoutAccount(
    val payoutMethod: String = "STRIPE_CONNECT",
    val accountHolderName: String = "",
    val accountIdentifier: String = "",
    val bankName: String = "",
    val swiftOrBic: String = "",
    val countryCode: String = "US",
    val preferredCurrencyCode: String = "USD",
    val taxComplianceStatus: String = "Pending Verification",
    val minimumPayoutUSD: Double = 1000.0,
    val autoMonthlyPayoutEnabled: Boolean = true
)

data class PayoutTransaction(
    val id: String,
    val amountUSD: Double,
    val targetCurrencyCode: String,
    val amountInLocalCurrency: Double,
    val status: String,
    val payoutMethod: String,
    val referenceId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlatformAdRevenueMetrics(
    val grossAdRevenueWorldwideUSD: Double = 0.0,
    val platformNetCommissionUSD: Double = 0.0,
    val creatorsDisbursedUSD: Double = 0.0,
    val activeGlobalAdvertisers: Int = 0,
    val countriesMonetized: Int = 0,
    val totalAdImpressionsServed: Long = 0L
)

data class BoostCampaignRequest(
    val postIdOrClipId: Long,
    val targetAudience: String = "Worldwide",
    val dailyBudgetUSD: Double = 15.0,
    val durationDays: Int = 7,
    val callToAction: String = "Visit Profile",
    val estimatedReach: String = "25,000 – 60,000 accounts"
)

object StarterMonetizationData {
    val sampleSponsoredAds = emptyList<AdPlacement>()
    val starterPayoutHistory = emptyList<PayoutTransaction>()
}

/**
 * Local Creator Support & Neighborhood Patron Sub-Tier Models.
 * Allows creators to configure a local tipping jar and offer a ₹99/mo ($0.99) neighborhood patron subscription.
 */
data class CreatorSupportSettings(
    val isTippingJarEnabled: Boolean = true,
    val tippingPrompt: String = "Support local indie clips & neighborhood reporting ☕",
    val presetTipAmountsINR: List<Int> = listOf(50, 100, 250, 500),
    val presetTipAmountsUSD: List<Double> = listOf(1.0, 3.0, 5.0, 10.0),
    val isPatronTierEnabled: Boolean = true,
    val patronTierName: String = "Neighborhood Patron",
    val patronTierPriceINR: Int = 99,
    val patronTierPriceUSD: Double = 0.99,
    val patronPerks: List<String> = listOf(
        "Exclusive behind-the-scenes clips & local outtakes",
        "Gold Patron badge beside your name on radar and comments",
        "Direct priority creator message access",
        "Early flash access to local marketplace listings"
    ),
    val activePatronCount: Int = 0,
    val monthlyPatronRevenueINR: Int = 0,
    val totalTipsReceivedCount: Int = 0,
    val totalTipsEarnedINR: Int = 0
)

data class CreatorTipTransaction(
    val id: String,
    val creatorUsername: String,
    val senderUsername: String,
    val amount: Double,
    val currencyCode: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 1. Flash Pin Drop Campaign (1–6 Hour Live Radar Surge)
 */
data class FlashPinDropCampaign(
    val id: String,
    val creatorUsername: String,
    val title: String,
    val offerHeadline: String,
    val landmark: String,
    val durationHours: Int = 2,
    val radiusKm: Double = 1.5,
    val feePaidUSD: Double = 9.99,
    val claimsCount: Int = 0,
    val maxClaims: Int = 50,
    val isActive: Boolean = true,
    val expiresAtTimestamp: Long = System.currentTimeMillis() + (durationHours * 3600_000L)
)

/**
 * 2. Hyperlocal Deal Passport (Neighborhood Bundle Pass)
 */
data class HyperlocalDealPassport(
    val id: String,
    val curatorUsername: String,
    val title: String,
    val neighborhood: String,
    val includedSpots: List<String>,
    val priceUSD: Double = 14.99,
    val soldCount: Int = 0,
    val merchantSharePercent: Int = 70,
    val curatorSharePercent: Int = 15,
    val platformSharePercent: Int = 15,
    val isActive: Boolean = true
) {
    val curatorEarnedUSD: Double
        get() = soldCount * priceUSD * (curatorSharePercent / 100.0)
}

/**
 * 3. Neighborhood Squad Goal (Community Crowd-Tipping Pool)
 */
data class NeighborhoodSquadGoal(
    val id: String,
    val creatorUsername: String,
    val title: String,
    val description: String,
    val landmark: String,
    val targetAmountUSD: Double,
    val fundedAmountUSD: Double,
    val backersCount: Int,
    val isCompleted: Boolean = false
)

/**
 * 4. Safe-Haven Verified Escrow Hub Reservation ($0.99 Flat Shield)
 */
data class SafeHavenEscrowShieldState(
    val isShieldActive: Boolean = true,
    val flatFeeUSD: Double = 0.99,
    val protectedHandshakesCount: Int = 0,
    val defaultSafeHavenHub: String = "Verified Local Police Precinct / 24/7 CCTV Public Atrium",
    val totalRebatesEarnedUSD: Double = 0.0
)

object HyperlocalExpansionStarterData {
    val starterFlashPinDrops = emptyList<FlashPinDropCampaign>()
    val starterDealPassports = emptyList<HyperlocalDealPassport>()
    val starterSquadGoals = emptyList<NeighborhoodSquadGoal>()
}
