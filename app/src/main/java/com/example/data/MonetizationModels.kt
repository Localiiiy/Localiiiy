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
    val callToAction: String, // "Install App", "Shop Now", "Claim 40% Off", "Visit Website", "Subscribe"
    val targetUrl: String,
    val cpmRateUSD: Double = 5.20, // $5.20 CPM (per 1,000 impressions)
    val cpcRateUSD: Double = 0.45, // $0.45 per click
    val isVerifiedAdvertiser: Boolean = true,
    val targetRegion: String = "Worldwide",
    val impressions: Long = 24500L,
    val clicks: Long = 1840L,
    val revenueGeneratedUSD: Double = 127.40
)

data class CreatorEarningsSummary(
    val monetizedViews: Long = 342000L,
    val inStreamVideoAdUSD: Double = 1420.50,
    val feedSponsoredAdUSD: Double = 612.80,
    val superThanksTipsUSD: Double = 842.00,
    val marketplaceSalesUSD: Double = 350.00,
    val totalGrossEarnedUSD: Double = 3225.30,
    val availableBalanceUSD: Double = 1890.30,
    val pendingPayoutUSD: Double = 450.00,
    val lifetimePayoutsUSD: Double = 885.00,
    val creatorSharePercentage: Int = 55, // 55% Creator Share
    val platformSharePercentage: Int = 45  // 45% Platform Share
)

data class CreatorPayoutAccount(
    val payoutMethod: String = "STRIPE_CONNECT", // "STRIPE_CONNECT", "PAYPAL_WORLDWIDE", "BANK_WIRE_SWIFT", "UPI_INDIA", "PIX_BRAZIL", "SEPA_EUROPE"
    val accountHolderName: String = "Alex Rivera",
    val accountIdentifier: String = "alex.rivera.creator@globalpay.com",
    val bankName: String = "Stripe Global Connect / Standard Chartered",
    val swiftOrBic: String = "SCBLUS33XXX",
    val countryCode: String = "US",
    val preferredCurrencyCode: String = "USD",
    val taxComplianceStatus: String = "W-8BEN / W-9 Tax Verified",
    val minimumPayoutUSD: Double = 1000.0,
    val autoMonthlyPayoutEnabled: Boolean = true
)

data class PayoutTransaction(
    val id: String,
    val amountUSD: Double,
    val targetCurrencyCode: String,
    val amountInLocalCurrency: Double,
    val status: String, // "COMPLETED", "PROCESSING", "SCHEDULED"
    val payoutMethod: String,
    val referenceId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlatformAdRevenueMetrics(
    val grossAdRevenueWorldwideUSD: Double = 128450.00,
    val platformNetCommissionUSD: Double = 57802.50, // 45% platform cut
    val creatorsDisbursedUSD: Double = 70647.50,     // 55% creator payouts
    val activeGlobalAdvertisers: Int = 240,
    val countriesMonetized: Int = 142,
    val totalAdImpressionsServed: Long = 24800000L
)

data class BoostCampaignRequest(
    val postIdOrClipId: Long,
    val targetAudience: String = "Worldwide", // "Worldwide", "North America", "Europe", "Asia-Pacific", "Latin America", "Local Neighborhood"
    val dailyBudgetUSD: Double = 15.0,
    val durationDays: Int = 7,
    val callToAction: String = "Visit Profile",
    val estimatedReach: String = "25,000 – 60,000 accounts"
)

object StarterMonetizationData {
    val sampleSponsoredAds = listOf(
        AdPlacement(
            id = "ad_global_1",
            advertiserName = "NordVPN Global",
            advertiserAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
            headline = "Protect your creative workflow worldwide with ultra-fast encryption 🛡️",
            description = "Get 70% off + 3 extra months with creator code Localiiiy. Trusted in 111 countries.",
            mediaUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=1080&auto=format&fit=crop&q=80",
            callToAction = "Claim 70% Off",
            targetUrl = "https://nordvpn.com/Localiiiy",
            cpmRateUSD = 6.40,
            cpcRateUSD = 0.55,
            targetRegion = "Worldwide"
        ),
        AdPlacement(
            id = "ad_global_2",
            advertiserName = "Sony Alpha Creator Gear",
            advertiserAvatar = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=200&auto=format&fit=crop&q=80",
            headline = "Cinema Line FX3 & A7 IV: Built for Hyperlocal & Worldwide Storytellers 🎥",
            description = "Unmatched 4K 120p, cinematic S-Cinetone colors, and dynamic stabilization for modern creators.",
            mediaUrl = "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=1080&auto=format&fit=crop&q=80",
            callToAction = "Shop Now",
            targetUrl = "https://sony.com/alpha",
            cpmRateUSD = 7.80,
            cpcRateUSD = 0.70,
            targetRegion = "Worldwide"
        ),
        AdPlacement(
            id = "ad_global_3",
            advertiserName = "Canva Pro Global",
            advertiserAvatar = "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=200&auto=format&fit=crop&q=80",
            headline = "Design stunning clips, thumbnails & posters in seconds ✨",
            description = "Start your free 30-day trial with AI Magic Studio tools. Loved by 170M+ creators worldwide.",
            mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1080&auto=format&fit=crop&q=80",
            callToAction = "Start Free Trial",
            targetUrl = "https://canva.com/pro",
            cpmRateUSD = 5.00,
            cpcRateUSD = 0.40,
            targetRegion = "Worldwide"
        )
    )

    val starterPayoutHistory = listOf(
        PayoutTransaction(
            id = "TX-94812-GLB",
            amountUSD = 520.00,
            targetCurrencyCode = "USD",
            amountInLocalCurrency = 520.00,
            status = "COMPLETED",
            payoutMethod = "Stripe Connect Global",
            referenceId = "ch_3N48sK2eZvKYlo2C",
            timestamp = System.currentTimeMillis() - (14 * 86400000L)
        ),
        PayoutTransaction(
            id = "TX-89104-GLB",
            amountUSD = 365.00,
            targetCurrencyCode = "USD",
            amountInLocalCurrency = 365.00,
            status = "COMPLETED",
            payoutMethod = "PayPal Worldwide",
            referenceId = "PP-89218290-RT",
            timestamp = System.currentTimeMillis() - (45 * 86400000L)
        )
    )
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
    val patronTierPriceINR: Int = 99, // ₹99/mo hyper-affordable patron tier
    val patronTierPriceUSD: Double = 0.99, // $0.99/mo international patron tier
    val patronPerks: List<String> = listOf(
        "Exclusive behind-the-scenes clips & local outtakes",
        "Gold Patron badge beside your name on radar and comments",
        "Direct priority creator message access",
        "Early flash access to local marketplace listings"
    ),
    val activePatronCount: Int = 18,
    val monthlyPatronRevenueINR: Int = 1782, // 18 * 99
    val totalTipsReceivedCount: Int = 64,
    val totalTipsEarnedINR: Int = 3850
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

