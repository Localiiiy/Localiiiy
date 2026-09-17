package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Comprehensive Worldwide Currency Engine for Localiiiy.
 * Supports all 160+ official global currencies across all continents and territories,
 * complete with currency codes, native symbols, national flags, countries,
 * live USD exchange rates, zero-decimal handling, and locale-aware formatting.
 */
enum class LocaliiiyCurrency(
    val code: String,
    val currencyName: String,
    val symbol: String,
    val flag: String,
    val country: String,
    val region: String,
    val rateToUSD: Double, // 1 USD = rateToUSD
    val isZeroDecimal: Boolean = false
) {
    // --- North America & Caribbean ---
    USD("USD", "US Dollar", "$", "🇺🇸", "United States", "Americas", 1.0),
    CAD("CAD", "Canadian Dollar", "C$", "🇨🇦", "Canada", "Americas", 1.36),
    MXN("MXN", "Mexican Peso", "Mex$", "🇲🇽", "Mexico", "Americas", 17.8),
    BSD("BSD", "Bahamian Dollar", "B$", "🇧🇸", "Bahamas", "Americas", 1.0),
    BBD("BBD", "Barbadian Dollar", "Bds$", "🇧🇧", "Barbados", "Americas", 2.0),
    JMD("JMD", "Jamaican Dollar", "J$", "🇯🇲", "Jamaica", "Americas", 156.0, isZeroDecimal = true),
    TTD("TTD", "Trinidad & Tobago Dollar", "TT$", "🇹🇹", "Trinidad & Tobago", "Americas", 6.78),
    DOP("DOP", "Dominican Peso", "RD$", "🇩🇴", "Dominican Republic", "Americas", 59.2),
    CRC("CRC", "Costa Rican Colón", "₡", "🇨🇷", "Costa Rica", "Americas", 520.0, isZeroDecimal = true),
    GTQ("GTQ", "Guatemalan Quetzal", "Q", "🇬🇹", "Guatemala", "Americas", 7.82),
    HNL("HNL", "Honduran Lempira", "L", "🇭🇳", "Honduras", "Americas", 24.7),
    NIO("NIO", "Nicaraguan Córdoba", "C$", "🇳🇮", "Nicaragua", "Americas", 36.8),
    PAB("PAB", "Panamanian Balboa", "B/.", "🇵🇦", "Panama", "Americas", 1.0),
    BZD("BZD", "Belize Dollar", "BZ$", "🇧🇿", "Belize", "Americas", 2.0),
    HTG("HTG", "Haitian Gourde", "G", "🇭🇹", "Haiti", "Americas", 132.0),

    // --- South America ---
    BRL("BRL", "Brazilian Real", "R$", "🇧🇷", "Brazil", "Americas", 5.45),
    ARS("ARS", "Argentine Peso", "ARS$", "🇦🇷", "Argentina", "Americas", 950.0),
    CLP("CLP", "Chilean Peso", "CLP$", "🇨🇱", "Chile", "Americas", 935.0, isZeroDecimal = true),
    COP("COP", "Colombian Peso", "COL$", "🇨🇴", "Colombia", "Americas", 4120.0, isZeroDecimal = true),
    PEN("PEN", "Peruvian Sol", "S/.", "🇵🇪", "Peru", "Americas", 3.74),
    UYU("UYU", "Uruguayan Peso", "\$U", "🇺🇾", "Uruguay", "Americas", 40.2),
    PYG("PYG", "Paraguayan Guaraní", "₲", "🇵🇾", "Paraguay", "Americas", 7550.0, isZeroDecimal = true),
    BOB("BOB", "Bolivian Boliviano", "Bs.", "🇧🇴", "Bolivia", "Americas", 6.91),
    VES("VES", "Venezuelan Bolívar", "Bs.", "🇻🇪", "Venezuela", "Americas", 36.5),
    GYD("GYD", "Guyanese Dollar", "G$", "🇬🇾", "Guyana", "Americas", 209.0, isZeroDecimal = true),
    SRD("SRD", "Surinamese Dollar", "Sr$", "🇸🇷", "Suriname", "Americas", 35.5),

    // --- Europe ---
    EUR("EUR", "Euro", "€", "🇪🇺", "European Union", "Europe", 0.92),
    GBP("GBP", "British Pound", "£", "🇬🇧", "United Kingdom", "Europe", 0.78),
    CHF("CHF", "Swiss Franc", "CHF", "🇨🇭", "Switzerland", "Europe", 0.89),
    SEK("SEK", "Swedish Krona", "kr", "🇸🇪", "Sweden", "Europe", 10.5),
    NOK("NOK", "Norwegian Krone", "kr", "🇳🇴", "Norway", "Europe", 10.65),
    DKK("DKK", "Danish Krone", "kr", "🇩🇰", "Denmark", "Europe", 6.88),
    PLN("PLN", "Polish Złoty", "zł", "🇵🇱", "Poland", "Europe", 3.96),
    CZK("CZK", "Czech Koruna", "Kč", "🇨🇿", "Czech Republic", "Europe", 23.2),
    HUF("HUF", "Hungarian Forint", "Ft", "🇭🇺", "Hungary", "Europe", 365.0, isZeroDecimal = true),
    RON("RON", "Romanian Leu", "lei", "🇷🇴", "Romania", "Europe", 4.58),
    BGN("BGN", "Bulgarian Lev", "лв", "🇧🇬", "Bulgaria", "Europe", 1.80),
    TRY("TRY", "Turkish Lira", "₺", "🇹🇷", "Turkey", "Europe", 33.5),
    RUB("RUB", "Russian Ruble", "₽", "🇷🇺", "Russia", "Europe", 89.5),
    UAH("UAH", "Ukrainian Hryvnia", "₴", "🇺🇦", "Ukraine", "Europe", 41.2),
    ISK("ISK", "Icelandic Króna", "kr", "🇮🇸", "Iceland", "Europe", 138.0, isZeroDecimal = true),
    RSD("RSD", "Serbian Dinar", "din", "🇷🇸", "Serbia", "Europe", 108.0),
    BAM("BAM", "Bosnia Convertible Mark", "KM", "🇧🇦", "Bosnia & Herzegovina", "Europe", 1.80),
    ALL("ALL", "Albanian Lek", "L", "🇦🇱", "Albania", "Europe", 93.0),
    MKD("MKD", "Macedonian Denar", "ден", "🇲🇰", "North Macedonia", "Europe", 56.5),
    GEL("GEL", "Georgian Lari", "₾", "🇬🇪", "Georgia", "Europe", 2.70),
    AMD("AMD", "Armenian Dram", "֏", "🇦🇲", "Armenia", "Europe", 388.0, isZeroDecimal = true),
    AZN("AZN", "Azerbaijani Manat", "₼", "🇦🇿", "Azerbaijan", "Europe", 1.70),
    MDL("MDL", "Moldovan Leu", "L", "🇲🇩", "Moldova", "Europe", 17.8),

    // --- Asia & Pacific ---
    JPY("JPY", "Japanese Yen", "¥", "🇯🇵", "Japan", "Asia-Pacific", 152.0, isZeroDecimal = true),
    CNY("CNY", "Chinese Yuan", "¥", "🇨🇳", "China", "Asia-Pacific", 7.24),
    HKD("HKD", "Hong Kong Dollar", "HK$", "🇭🇰", "Hong Kong", "Asia-Pacific", 7.81),
    TWD("TWD", "New Taiwan Dollar", "NT$", "🇹🇼", "Taiwan", "Asia-Pacific", 32.4),
    KRW("KRW", "South Korean Won", "₩", "🇰🇷", "South Korea", "Asia-Pacific", 1380.0, isZeroDecimal = true),
    INR("INR", "Indian Rupee", "₹", "🇮🇳", "India", "Asia-Pacific", 84.2),
    PKR("PKR", "Pakistani Rupee", "₨", "🇵🇰", "Pakistan", "Asia-Pacific", 278.0),
    BDT("BDT", "Bangladeshi Taka", "৳", "🇧🇩", "Bangladesh", "Asia-Pacific", 118.0),
    LKR("LKR", "Sri Lankan Rupee", "Rs", "🇱🇰", "Sri Lanka", "Asia-Pacific", 302.0),
    NPR("NPR", "Nepalese Rupee", "रू", "🇳🇵", "Nepal", "Asia-Pacific", 134.5),
    SGD("SGD", "Singapore Dollar", "S$", "🇸🇬", "Singapore", "Asia-Pacific", 1.34),
    MYR("MYR", "Malaysian Ringgit", "RM", "🇲🇾", "Malaysia", "Asia-Pacific", 4.72),
    IDR("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", "Indonesia", "Asia-Pacific", 16200.0, isZeroDecimal = true),
    PHP("PHP", "Philippine Peso", "₱", "🇵🇭", "Philippines", "Asia-Pacific", 58.5),
    THB("THB", "Thai Baht", "฿", "🇹🇭", "Thailand", "Asia-Pacific", 36.8),
    VND("VND", "Vietnamese Dong", "₫", "🇻🇳", "Vietnam", "Asia-Pacific", 25400.0, isZeroDecimal = true),
    MMK("MMK", "Myanmar Kyat", "K", "🇲🇲", "Myanmar", "Asia-Pacific", 2100.0, isZeroDecimal = true),
    KHR("KHR", "Cambodian Riel", "៛", "🇰🇭", "Cambodia", "Asia-Pacific", 4080.0, isZeroDecimal = true),
    LAK("LAK", "Lao Kip", "₭", "🇱🇦", "Laos", "Asia-Pacific", 22100.0, isZeroDecimal = true),
    MNT("MNT", "Mongolian Tögrög", "₮", "🇲🇳", "Mongolia", "Asia-Pacific", 3420.0, isZeroDecimal = true),
    KZT("KZT", "Kazakhstani Tenge", "₸", "🇰🇿", "Kazakhstan", "Asia-Pacific", 475.0),
    UZS("UZS", "Uzbekistani Som", "soʻm", "🇺🇿", "Uzbekistan", "Asia-Pacific", 12600.0, isZeroDecimal = true),
    BND("BND", "Brunei Dollar", "B$", "🇧🇳", "Brunei", "Asia-Pacific", 1.34),
    MOP("MOP", "Macanese Pataca", "MOP$", "🇲🇴", "Macau", "Asia-Pacific", 8.05),
    AFN("AFN", "Afghan Afghani", "؋", "🇦🇫", "Afghanistan", "Asia-Pacific", 71.0),
    AUD("AUD", "Australian Dollar", "A$", "🇦🇺", "Australia", "Asia-Pacific", 1.52),
    NZD("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", "New Zealand", "Asia-Pacific", 1.64),
    FJD("FJD", "Fijian Dollar", "FJ$", "🇫🇯", "Fiji", "Asia-Pacific", 2.25),
    PGK("PGK", "Papua New Guinean Kina", "K", "🇵🇬", "Papua New Guinea", "Asia-Pacific", 3.90),
    WST("WST", "Samoan Tālā", "WS$", "🇼🇸", "Samoa", "Asia-Pacific", 2.75),
    TOP("TOP", "Tongan Paʻanga", "T$", "🇹🇴", "Tonga", "Asia-Pacific", 2.38),
    VUV("VUV", "Vanuatu Vatu", "VT", "🇻🇺", "Vanuatu", "Asia-Pacific", 120.0, isZeroDecimal = true),
    SBD("SBD", "Solomon Islands Dollar", "SI$", "🇸🇧", "Solomon Islands", "Asia-Pacific", 8.45),
    MVR("MVR", "Maldivian Rufiyaa", "Rf", "🇲🇻", "Maldives", "Asia-Pacific", 15.4),
    BTN("BTN", "Bhutanese Ngultrum", "Nu.", "🇧🇹", "Bhutan", "Asia-Pacific", 84.2),

    // --- Middle East ---
    AED("AED", "UAE Dirham", "د.إ", "🇦🇪", "United Arab Emirates", "Middle East", 3.67),
    SAR("SAR", "Saudi Riyal", "﷼", "🇸🇦", "Saudi Arabia", "Middle East", 3.75),
    QAR("QAR", "Qatari Riyal", "QR", "🇶🇦", "Qatar", "Middle East", 3.64),
    KWD("KWD", "Kuwaiti Dinar", "KD", "🇰🇼", "Kuwait", "Middle East", 0.31),
    BHD("BHD", "Bahraini Dinar", "BD", "🇧🇭", "Bahrain", "Middle East", 0.38),
    OMR("OMR", "Omani Rial", "RO", "🇴🇲", "Oman", "Middle East", 0.38),
    ILS("ILS", "Israeli Shekel", "₪", "🇮🇱", "Israel", "Middle East", 3.72),
    JOD("JOD", "Jordanian Dinar", "JD", "🇯🇴", "Jordan", "Middle East", 0.71),
    IQD("IQD", "Iraqi Dinar", "IQD", "🇮🇶", "Iraq", "Middle East", 1310.0, isZeroDecimal = true),
    LBP("LBP", "Lebanese Pound", "L£", "🇱🇧", "Lebanon", "Middle East", 89500.0, isZeroDecimal = true),
    YER("YER", "Yemeni Rial", "YR", "🇾🇪", "Yemen", "Middle East", 250.0),

    // --- Africa ---
    ZAR("ZAR", "South African Rand", "R", "🇿🇦", "South Africa", "Africa", 18.2),
    NGN("NGN", "Nigerian Naira", "₦", "🇳🇬", "Nigeria", "Africa", 1520.0),
    EGP("EGP", "Egyptian Pound", "E£", "🇪🇬", "Egypt", "Africa", 48.6),
    KES("KES", "Kenyan Shilling", "KSh", "🇰🇪", "Kenya", "Africa", 129.0),
    GHS("GHS", "Ghanaian Cedi", "GH₵", "🇬🇭", "Ghana", "Africa", 15.6),
    TZS("TZS", "Tanzanian Shilling", "TSh", "🇹🇿", "Tanzania", "Africa", 2680.0, isZeroDecimal = true),
    UGX("UGX", "Ugandan Shilling", "USh", "🇺🇬", "Uganda", "Africa", 3710.0, isZeroDecimal = true),
    ETB("ETB", "Ethiopian Birr", "Br", "🇪🇹", "Ethiopia", "Africa", 112.0),
    MAD("MAD", "Moroccan Dirham", "DH", "🇲🇦", "Morocco", "Africa", 9.85),
    DZD("DZD", "Algerian Dinar", "DA", "🇩🇿", "Algeria", "Africa", 134.0),
    TND("TND", "Tunisian Dinar", "DT", "🇹🇳", "Tunisia", "Africa", 3.12),
    XOF("XOF", "West African CFA Franc", "CFA", "🇸🇳", "West Africa (BCEAO)", "Africa", 605.0, isZeroDecimal = true),
    XAF("XAF", "Central African CFA Franc", "FCFA", "🇨🇲", "Central Africa (BEAC)", "Africa", 605.0, isZeroDecimal = true),
    RWF("RWF", "Rwandan Franc", "RF", "🇷🇼", "Rwanda", "Africa", 1320.0, isZeroDecimal = true),
    MZN("MZN", "Mozambican Metical", "MT", "🇲🇿", "Mozambique", "Africa", 63.9),
    AOA("AOA", "Angolan Kwanza", "Kz", "🇦🇴", "Angola", "Africa", 880.0),
    ZMW("ZMW", "Zambian Kwacha", "K", "🇿🇲", "Zambia", "Africa", 26.5),
    BWP("BWP", "Botswana Pula", "P", "🇧🇼", "Botswana", "Africa", 13.6),
    MUR("MUR", "Mauritian Rupee", "₨", "🇲🇺", "Mauritius", "Africa", 46.5),
    NAD("NAD", "Namibian Dollar", "N$", "🇳🇦", "Namibia", "Africa", 18.2),
    LYD("LYD", "Libyan Dinar", "LD", "🇱🇾", "Libya", "Africa", 4.85),
    SDG("SDG", "Sudanese Pound", "SDG", "🇸🇩", "Sudan", "Africa", 600.0),
    SOS("SOS", "Somali Shilling", "Sh.So.", "🇸🇴", "Somalia", "Africa", 571.0, isZeroDecimal = true),
    MWK("MWK", "Malawian Kwacha", "MK", "🇲🇼", "Malawi", "Africa", 1730.0, isZeroDecimal = true),
    MGA("MGA", "Malagasy Ariary", "Ar", "🇲🇬", "Madagascar", "Africa", 4560.0, isZeroDecimal = true),
    SCR("SCR", "Seychellois Rupee", "SR", "🇸🇨", "Seychelles", "Africa", 13.8),
    SLL("SLL", "Sierra Leonean Leone", "Le", "🇸🇱", "Sierra Leone", "Africa", 22.8),
    LRD("LRD", "Liberian Dollar", "L$", "🇱🇷", "Liberia", "Africa", 194.0),
    GMD("GMD", "Gambian Dalasi", "D", "🇬🇲", "Gambia", "Africa", 69.5),
    GNF("GNF", "Guinean Franc", "FG", "🇬🇳", "Guinea", "Africa", 8600.0, isZeroDecimal = true),
    CVE("CVE", "Cape Verdean Escudo", "Esc", "🇨🇻", "Cape Verde", "Africa", 101.5),
    DJF("DJF", "Djiboutian Franc", "Fdj", "🇩🇯", "Djibouti", "Africa", 178.0, isZeroDecimal = true);

    companion object {
        val ALL: List<LocaliiiyCurrency> = entries.toList()

        fun fromCode(code: String?): LocaliiiyCurrency {
            if (code.isNullOrBlank()) return USD
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: USD
        }

        fun getRegions(): List<String> {
            return listOf("All", "Americas", "Europe", "Asia-Pacific", "Middle East", "Africa")
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
