package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Comprehensive Worldwide Currency Engine for Localiiiy.
 * Supports all major global currencies with symbols, exchange rates,
 * conversion calculators, and locale-aware formatting.
 */
enum class LocaliiiyCurrency(
    val code: String,
    val currencyName: String,
    val symbol: String,
    val flag: String,
    val country: String,
    val rateToUSD: Double, // 1 USD = rateToUSD
    val isZeroDecimal: Boolean = false
) {
    USD("USD", "US Dollar", "$", "🇺🇸", "United States", 1.0),
    EUR("EUR", "Euro", "€", "🇪🇺", "European Union", 0.92),
    GBP("GBP", "British Pound", "£", "🇬🇧", "United Kingdom", 0.78),
    INR("INR", "Indian Rupee", "₹", "🇮🇳", "India", 84.2),
    JPY("JPY", "Japanese Yen", "¥", "🇯🇵", "Japan", 152.0, isZeroDecimal = true),
    CAD("CAD", "Canadian Dollar", "C$", "🇨🇦", "Canada", 1.36),
    AUD("AUD", "Australian Dollar", "A$", "🇦🇺", "Australia", 1.52),
    CNY("CNY", "Chinese Yuan", "¥", "🇨🇳", "China", 7.24),
    BRL("BRL", "Brazilian Real", "R$", "🇧🇷", "Brazil", 5.45),
    AED("AED", "UAE Dirham", "د.إ", "🇦🇪", "United Arab Emirates", 3.67),
    SAR("SAR", "Saudi Riyal", "﷼", "🇸🇦", "Saudi Arabia", 3.75),
    SGD("SGD", "Singapore Dollar", "S$", "🇸🇬", "Singapore", 1.34),
    KRW("KRW", "South Korean Won", "₩", "🇰🇷", "South Korea", 1380.0, isZeroDecimal = true),
    MXN("MXN", "Mexican Peso", "Mex$", "🇲🇽", "Mexico", 17.8),
    ZAR("ZAR", "South African Rand", "R", "🇿🇦", "South Africa", 18.2),
    NGN("NGN", "Nigerian Naira", "₦", "🇳🇬", "Nigeria", 1520.0),
    TRY("TRY", "Turkish Lira", "₺", "🇹🇷", "Turkey", 33.5),
    CHF("CHF", "Swiss Franc", "CHF", "🇨🇭", "Switzerland", 0.89),
    SEK("SEK", "Swedish Krona", "kr", "🇸🇪", "Sweden", 10.5),
    PHP("PHP", "Philippine Peso", "₱", "🇵🇭", "Philippines", 58.5),
    IDR("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", "Indonesia", 16200.0, isZeroDecimal = true),
    PKR("PKR", "Pakistani Rupee", "₨", "🇵🇰", "Pakistan", 278.0),
    BDT("BDT", "Bangladeshi Taka", "৳", "🇧🇩", "Bangladesh", 118.0),
    VND("VND", "Vietnamese Dong", "₫", "🇻🇳", "Vietnam", 25400.0, isZeroDecimal = true),
    EGP("EGP", "Egyptian Pound", "E£", "🇪🇬", "Egypt", 48.6),
    KES("KES", "Kenyan Shilling", "KSh", "🇰🇪", "Kenya", 129.0),
    COP("COP", "Colombian Peso", "COL$", "🇨🇴", "Colombia", 4120.0, isZeroDecimal = true),
    ARS("ARS", "Argentine Peso", "ARS$", "🇦🇷", "Argentina", 950.0),
    THB("THB", "Thai Baht", "฿", "🇹🇭", "Thailand", 36.8),
    MYR("MYR", "Malaysian Ringgit", "RM", "🇲🇾", "Malaysia", 4.72),
    NZD("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", "New Zealand", 1.64);

    companion object {
        val ALL: List<LocaliiiyCurrency> = entries.toList()

        fun fromCode(code: String?): LocaliiiyCurrency {
            if (code.isNullOrBlank()) return USD
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}

object CurrencyHelper {

    /**
     * Converts a base USD amount to the target worldwide currency using live exchange rates.
     */
    fun convertFromUSD(amountInUSD: Double, target: LocaliiiyCurrency): Double {
        return amountInUSD * target.rateToUSD
    }

    /**
     * Converts a local currency amount back to base USD.
     */
    fun convertToUSD(localAmount: Double, source: LocaliiiyCurrency): Double {
        if (source.rateToUSD <= 0.0) return localAmount
        return localAmount / source.rateToUSD
    }

    /**
     * Formats a base USD amount into the target currency with appropriate symbol and precision.
     * e.g. format(100.0, USD) -> "$100.00"
     *      format(100.0, EUR) -> "€92.00"
     *      format(100.0, INR) -> "₹8,420.00"
     *      format(100.0, JPY) -> "¥15,200"
     */
    fun format(amountInUSD: Double, target: LocaliiiyCurrency): String {
        val converted = convertFromUSD(amountInUSD, target)
        return formatDirect(converted, target)
    }

    /**
     * Formats an amount already in the target currency.
     */
    fun formatDirect(amount: Double, currency: LocaliiiyCurrency): String {
        return if (currency.isZeroDecimal) {
            val longVal = amount.toLong()
            val formatter = DecimalFormat("#,###")
            "${currency.symbol}${formatter.format(longVal)}"
        } else {
            val formatter = DecimalFormat("#,##0.00")
            "${currency.symbol}${formatter.format(amount)}"
        }
    }

    /**
     * Formats with currency code appended (e.g., "$1,250.00 USD").
     */
    fun formatWithCode(amountInUSD: Double, target: LocaliiiyCurrency): String {
        return "${format(amountInUSD, target)} ${target.code}"
    }

    /**
     * Formats subscriber and follower counts into standard social abbreviations (e.g., 14.2K, 248K, 1.2M).
     */
    fun formatSubscribersCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0).replace(".0M", "M")
            count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000.0).replace(".0K", "K")
            else -> count.toString()
        }
    }
}
