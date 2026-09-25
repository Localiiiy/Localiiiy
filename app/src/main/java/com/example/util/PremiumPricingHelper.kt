package com.example.util

/**
 * Worldwide Rounded Pricing Matrix for Localiiiy Premium Pro.
 * Provides hyper-affordable, aesthetic rounded figures for all countries:
 * - India: ₹299 / month (Annual: ₹2,799 / year ~₹233/mo)
 * - USA: $2.99 / month (Annual: $27.99 / year ~$2.33/mo)
 * - Europe: €2.99 / month (Annual: €27.99 / year ~€2.33/mo)
 * - UK: £2.49 / month (Annual: £23.99 / year ~£1.99/mo)
 * - Plus rounded aesthetic figures for all 160+ world currencies.
 */
data class PremiumPlanPrice(
    val currency: LocaliiiyCurrency,
    val monthlyPriceDisplay: String,
    val monthlyPeriodDisplay: String,
    val annualPriceDisplay: String,
    val annualPeriodDisplay: String,
    val annualMonthlyBreakdown: String,
    val monthlySavingsTag: String,
    val annualSavingsTag: String,
    val dailyCostBreakdown: String
)

object PremiumPricingHelper {

    fun getPlanPrice(currency: LocaliiiyCurrency): PremiumPlanPrice {
        return when (currency) {
            LocaliiiyCurrency.INR -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "₹299",
                monthlyPeriodDisplay = "₹299 / month",
                annualPriceDisplay = "₹2,799",
                annualPeriodDisplay = "₹2,799 / year",
                annualMonthlyBreakdown = "Approx ₹233 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~₹9.90 / day • Cancel anytime"
            )
            LocaliiiyCurrency.USD -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "$2.99",
                monthlyPeriodDisplay = "$2.99 / month",
                annualPriceDisplay = "$27.99",
                annualPeriodDisplay = "$27.99 / year",
                annualMonthlyBreakdown = "Approx $2.33 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~$0.10 / day • Cancel anytime"
            )
            LocaliiiyCurrency.EUR -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "€2.99",
                monthlyPeriodDisplay = "€2.99 / month",
                annualPriceDisplay = "€27.99",
                annualPeriodDisplay = "€27.99 / year",
                annualMonthlyBreakdown = "Approx €2.33 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~€0.10 / day • Cancel anytime"
            )
            LocaliiiyCurrency.GBP -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "£2.49",
                monthlyPeriodDisplay = "£2.49 / month",
                annualPriceDisplay = "£23.99",
                annualPeriodDisplay = "£23.99 / year",
                annualMonthlyBreakdown = "Approx £1.99 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 20% • BEST VALUE",
                dailyCostBreakdown = "Only ~£0.08 / day • Cancel anytime"
            )
            LocaliiiyCurrency.CAD -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "C$3.99",
                monthlyPeriodDisplay = "C$3.99 / month",
                annualPriceDisplay = "C$36.99",
                annualPeriodDisplay = "C$36.99 / year",
                annualMonthlyBreakdown = "Approx C$3.08 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 23% • BEST VALUE",
                dailyCostBreakdown = "Only ~C$0.13 / day • Cancel anytime"
            )
            LocaliiiyCurrency.AUD -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "A$4.49",
                monthlyPeriodDisplay = "A$4.49 / month",
                annualPriceDisplay = "A$39.99",
                annualPeriodDisplay = "A$39.99 / year",
                annualMonthlyBreakdown = "Approx A$3.33 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 25% • BEST VALUE",
                dailyCostBreakdown = "Only ~A$0.15 / day • Cancel anytime"
            )
            LocaliiiyCurrency.JPY -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "¥450",
                monthlyPeriodDisplay = "¥450 / month",
                annualPriceDisplay = "¥4,200",
                annualPeriodDisplay = "¥4,200 / year",
                annualMonthlyBreakdown = "Approx ¥350 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~¥15 / day • Cancel anytime"
            )
            LocaliiiyCurrency.CNY -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "¥19.9",
                monthlyPeriodDisplay = "¥19.9 / month",
                annualPriceDisplay = "¥189",
                annualPeriodDisplay = "¥189 / year",
                annualMonthlyBreakdown = "Approx ¥15.7 / mo",
                monthlySavingsTag = "MOST POPULAR",
                annualSavingsTag = "SAVE 21% • BEST VALUE",
                dailyCostBreakdown = "Only ~¥0.66 / day • Cancel anytime"
            )
            LocaliiiyCurrency.AED -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "AED 10.99",
                monthlyPeriodDisplay = "AED 10.99 / month",
                annualPriceDisplay = "AED 99.99",
                annualPeriodDisplay = "AED 99.99 / year",
                annualMonthlyBreakdown = "Approx AED 8.33 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 24% • BEST VALUE",
                dailyCostBreakdown = "Only ~AED 0.36 / day • Cancel anytime"
            )
            LocaliiiyCurrency.SAR -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "SAR 11.49",
                monthlyPeriodDisplay = "SAR 11.49 / month",
                annualPriceDisplay = "SAR 104.99",
                annualPeriodDisplay = "SAR 104.99 / year",
                annualMonthlyBreakdown = "Approx SAR 8.75 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 24% • BEST VALUE",
                dailyCostBreakdown = "Only ~SAR 0.38 / day • Cancel anytime"
            )
            LocaliiiyCurrency.BRL -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "R$ 14.99",
                monthlyPeriodDisplay = "R$ 14.99 / month",
                annualPriceDisplay = "R$ 139.99",
                annualPeriodDisplay = "R$ 139.99 / year",
                annualMonthlyBreakdown = "Approx R$ 11.66 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~R$ 0.50 / day • Cancel anytime"
            )
            LocaliiiyCurrency.MXN -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "Mex$ 49.99",
                monthlyPeriodDisplay = "Mex$ 49.99 / month",
                annualPriceDisplay = "Mex$ 469",
                annualPeriodDisplay = "Mex$ 469 / year",
                annualMonthlyBreakdown = "Approx Mex$ 39 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 22% • BEST VALUE",
                dailyCostBreakdown = "Only ~Mex$ 1.60 / day • Cancel anytime"
            )
            LocaliiiyCurrency.SGD -> PremiumPlanPrice(
                currency = currency,
                monthlyPriceDisplay = "S$3.99",
                monthlyPeriodDisplay = "S$3.99 / month",
                annualPriceDisplay = "S$36.99",
                annualPeriodDisplay = "S$36.99 / year",
                annualMonthlyBreakdown = "Approx S$3.08 / mo",
                monthlySavingsTag = "POPULAR CHOICE",
                annualSavingsTag = "SAVE 23% • BEST VALUE",
                dailyCostBreakdown = "Only ~S$0.13 / day • Cancel anytime"
            )
            else -> {
                // Dynamically calculate aesthetically rounded prices for all other worldwide currencies
                val rawMonthly = 2.99 * currency.rateToUSD
                val symbol = currency.symbol
                if (currency.isZeroDecimal) {
                    val rawInt = rawMonthly.toInt()
                    val roundedMonthly = when {
                        rawInt >= 10000 -> ((rawInt / 1000) * 1000).coerceAtLeast(1000)
                        rawInt >= 1000 -> ((rawInt / 100) * 100).coerceAtLeast(100)
                        else -> ((rawInt / 10) * 10).coerceAtLeast(10)
                    }
                    val roundedAnnual = ((roundedMonthly * 9.3 / 100).toInt() * 100).coerceAtLeast(roundedMonthly * 8)
                    val monthlyApprox = roundedAnnual / 12
                    PremiumPlanPrice(
                        currency = currency,
                        monthlyPriceDisplay = "$symbol$roundedMonthly",
                        monthlyPeriodDisplay = "$symbol$roundedMonthly / month",
                        annualPriceDisplay = "$symbol$roundedAnnual",
                        annualPeriodDisplay = "$symbol$roundedAnnual / year",
                        annualMonthlyBreakdown = "Approx $symbol$monthlyApprox / mo",
                        monthlySavingsTag = "POPULAR CHOICE",
                        annualSavingsTag = "SAVE ~22% • BEST VALUE",
                        dailyCostBreakdown = "Affordable for everyone in ${currency.country}"
                    )
                } else {
                    val intPart = rawMonthly.toInt().coerceAtLeast(1)
                    val formattedMonthly = "$symbol$intPart.99"
                    val annualVal = (intPart * 9.3).toInt().coerceAtLeast(intPart * 8)
                    val formattedAnnual = "$symbol$annualVal.99"
                    val approxMo = annualVal / 12
                    PremiumPlanPrice(
                        currency = currency,
                        monthlyPriceDisplay = formattedMonthly,
                        monthlyPeriodDisplay = "$formattedMonthly / month",
                        annualPriceDisplay = formattedAnnual,
                        annualPeriodDisplay = "$formattedAnnual / year",
                        annualMonthlyBreakdown = "Approx $symbol$approxMo.99 / mo",
                        monthlySavingsTag = "POPULAR CHOICE",
                        annualSavingsTag = "SAVE ~22% • BEST VALUE",
                        dailyCostBreakdown = "Affordable for everyone in ${currency.country}"
                    )
                }
            }
        }
    }
}
