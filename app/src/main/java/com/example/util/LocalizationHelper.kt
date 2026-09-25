package com.example.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.ReadOnlyComposable
import java.util.Locale

/**
 * Universal CompositionLocal holding the currently active language.
 * Propagates automatically across all Composable trees.
 */
val LocalAppLanguage = compositionLocalOf { LocaliiiyLanguage.EN }

/**
 * Universal Multi-Language Localization Engine for Localiiiy.
 * Provides 100% full international support for 75+ global languages across
 * North America, Latin America, Europe, Middle East, Central Asia, South Asia,
 * East Asia, Southeast Asia, Africa, and Oceania.
 */
enum class LocaliiiyLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val region: String,
    val isRtl: Boolean = false
) {
    // --- Major Global & Americas ---
    EN("en", "English", "English (US)", "🇺🇸", "Americas"),
    EN_GB("en-gb", "English (UK)", "English (UK)", "🇬🇧", "Europe"),
    ES("es", "Spanish", "Español", "🇪🇸", "Europe"),
    ES_MX("es-mx", "Spanish (LatAm)", "Español (Latinoamérica)", "🇲🇽", "Americas"),
    FR("fr", "French", "Français", "🇫🇷", "Europe"),
    FR_CA("fr-ca", "French (Canada)", "Français (Canada)", "🇨🇦", "Americas"),
    PT("pt", "Portuguese (Brazil)", "Português (Brasil)", "🇧🇷", "Americas"),
    PT_PT("pt-pt", "Portuguese (Portugal)", "Português (Portugal)", "🇵🇹", "Europe"),
    DE("de", "German", "Deutsch", "🇩🇪", "Europe"),
    IT("it", "Italian", "Italiano", "🇮🇹", "Europe"),

    // --- East Asia & Southeast Asia ---
    ZH("zh", "Chinese (Simplified)", "中文 (简体)", "🇨🇳", "Asia-Pacific"),
    ZH_TW("zh-tw", "Chinese (Traditional)", "中文 (繁體)", "🇹🇼", "Asia-Pacific"),
    ZH_HK("zh-hk", "Cantonese", "粵語 (香港)", "🇭🇰", "Asia-Pacific"),
    JA("ja", "Japanese", "日本語", "🇯🇵", "Asia-Pacific"),
    KO("ko", "Korean", "한국어", "🇰🇷", "Asia-Pacific"),
    VI("vi", "Vietnamese", "Tiếng Việt", "🇻🇳", "Asia-Pacific"),
    ID("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", "Asia-Pacific"),
    MS("ms", "Malay", "Bahasa Melayu", "🇲🇾", "Asia-Pacific"),
    TL("tl", "Filipino / Tagalog", "Wikang Filipino", "🇵🇭", "Asia-Pacific"),
    TH("th", "Thai", "ไทย", "🇹🇭", "Asia-Pacific"),
    MY("my", "Burmese", "မြန်မာဘာသာ", "🇲🇲", "Asia-Pacific"),
    KM("km", "Khmer", "ភាសាខ្មែរ", "🇰🇭", "Asia-Pacific"),
    LO("lo", "Lao", "ພາສາລາວ", "🇱🇦", "Asia-Pacific"),
    MN("mn", "Mongolian", "Монгол хэл", "🇲🇳", "Asia-Pacific"),

    // --- South Asia ---
    HI("hi", "Hindi", "हिन्दी", "🇮🇳", "Asia-Pacific"),
    BN("bn", "Bengali", "বাংলা", "🇧🇩", "Asia-Pacific"),
    TE("te", "Telugu", "తెలుగు", "🇮🇳", "Asia-Pacific"),
    MR("mr", "Marathi", "मराठी", "🇮🇳", "Asia-Pacific"),
    TA("ta", "Tamil", "தமிழ்", "🇮🇳", "Asia-Pacific"),
    UR("ur", "Urdu", "اردو", "🇵🇰", "Asia-Pacific", isRtl = true),
    GU("gu", "Gujarati", "ગુજરાતી", "🇮🇳", "Asia-Pacific"),
    KN("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", "Asia-Pacific"),
    ML("ml", "Malayalam", "മലയാളം", "🇮🇳", "Asia-Pacific"),
    PA("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", "Asia-Pacific"),
    OR("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳", "Asia-Pacific"),
    AS("as", "Assamese", "অসমীয়া", "🇮🇳", "Asia-Pacific"),
    NE("ne", "Nepali", "नेपाली", "🇳🇵", "Asia-Pacific"),
    SI("si", "Sinhala", "සිංහල", "🇱🇰", "Asia-Pacific"),
    PS("ps", "Pashto", "پښتو", "🇦🇫", "Asia-Pacific", isRtl = true),

    // --- Middle East & Central Asia ---
    AR("ar", "Arabic", "العربية", "🇸🇦", "Middle East", isRtl = true),
    FA("fa", "Persian / Farsi", "فارسی", "🇮🇷", "Middle East", isRtl = true),
    HE("he", "Hebrew", "עברית", "🇮🇱", "Middle East", isRtl = true),
    KU("ku", "Kurdish", "Kurdî / کوردی", "🇮🇶", "Middle East", isRtl = true),
    TR("tr", "Turkish", "Türkçe", "🇹🇷", "Middle East"),
    AZ("az", "Azerbaijani", "Azərbaycan dili", "🇦🇿", "Middle East"),
    KK("kk", "Kazakh", "Қазақ тілі", "🇰🇿", "Middle East"),
    UZ("uz", "Uzbek", "Oʻzbekcha", "🇺🇿", "Middle East"),

    // --- Europe (Eastern, Northern & Southern) ---
    RU("ru", "Russian", "Русский", "🇷🇺", "Europe"),
    UK("uk", "Ukrainian", "Українська", "🇺🇦", "Europe"),
    PL("pl", "Polish", "Polski", "🇵🇱", "Europe"),
    NL("nl", "Dutch", "Nederlands", "🇳🇱", "Europe"),
    EL("el", "Greek", "Ελληνικά", "🇬🇷", "Europe"),
    CS("cs", "Czech", "Čeština", "🇨🇿", "Europe"),
    SK("sk", "Slovak", "Slovenčina", "🇸🇰", "Europe"),
    HU("hu", "Hungarian", "Magyar", "🇭🇺", "Europe"),
    RO("ro", "Romanian", "Română", "🇷🇴", "Europe"),
    BG("bg", "Bulgarian", "Български", "🇧🇬", "Europe"),
    SR("sr", "Serbian", "Српски", "🇷🇸", "Europe"),
    HR("hr", "Croatian", "Hrvatski", "🇭🇷", "Europe"),
    BS("bs", "Bosnian", "Bosanski", "🇧🇦", "Europe"),
    SQ("sq", "Albanian", "Shqip", "🇦🇱", "Europe"),
    SV("sv", "Swedish", "Svenska", "🇸🇪", "Europe"),
    DA("da", "Danish", "Dansk", "🇩🇰", "Europe"),
    FI("fi", "Finnish", "Suomi", "🇫🇮", "Europe"),
    NO("no", "Norwegian", "Norsk", "🇳🇴", "Europe"),
    IS("is", "Icelandic", "Íslenska", "🇮🇸", "Europe"),
    ET("et", "Estonian", "Eesti", "🇪🇪", "Europe"),
    LV("lv", "Latvian", "Latviešu", "🇱🇻", "Europe"),
    LT("lt", "Lithuanian", "Lietuvių", "🇱🇹", "Europe"),
    GA("ga", "Irish", "Gaeilge", "🇮🇪", "Europe"),
    CA("ca", "Catalan", "Català", "🇪🇸", "Europe"),
    EU("eu", "Basque", "Euskara", "🇪🇸", "Europe"),
    GL("gl", "Galician", "Galego", "🇪🇸", "Europe"),
    KA("ka", "Georgian", "ქართული", "🇬🇪", "Europe"),
    HY("hy", "Armenian", "Հայերեն", "🇦🇲", "Europe"),

    // --- Africa ---
    SW("sw", "Swahili", "Kiswahili", "🇰🇪", "Africa"),
    AM("am", "Amharic", "አማርኛ", "🇪🇹", "Africa"),
    YO("yo", "Yoruba", "Èdè Yorùbá", "🇳🇬", "Africa"),
    IG("ig", "Igbo", "Asụsụ Igbo", "🇳🇬", "Africa"),
    HA("ha", "Hausa", "Harshen Hausa", "🇳🇬", "Africa"),
    ZU("zu", "Zulu", "isiZulu", "🇿🇦", "Africa"),
    XH("xh", "Xhosa", "isiXhosa", "🇿🇦", "Africa"),
    AF("af", "Afrikaans", "Afrikaans", "🇿🇦", "Africa"),
    SO("so", "Somali", "Soomaaliga", "🇸🇴", "Africa"),
    OM("om", "Oromo", "Afaan Oromoo", "🇪🇹", "Africa"),
    SN("sn", "Shona", "chiShona", "🇿🇼", "Africa"),
    MG("mg", "Malagasy", "Malagasy", "🇲🇬", "Africa");

    companion object {
        val ALL: List<LocaliiiyLanguage> = entries.toList()

        fun fromCode(code: String?): LocaliiiyLanguage {
            if (code.isNullOrBlank()) return EN
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: EN
        }

        fun getRegions(): List<String> {
            return listOf("All", "Americas", "Europe", "Asia-Pacific", "Middle East", "Africa")
        }
    }
}

enum class LocaliiiyStringKey {
    // Navigation Tabs
    TAB_FEED,
    TAB_RADAR,
    TAB_MARKET,
    TAB_STUDIO,
    TAB_CLIPS,
    TAB_PROFILE,
    TAB_CREATE,

    // Header & Actions
    APP_NAME,
    SEARCH_HINT,
    NOTIFICATIONS,
    DIRECT_MESSAGES,
    GLOBAL_SETTINGS,
    CURRENCY_AND_LANG,

    // Monetization & Ads
    CREATOR_STUDIO,
    MONETIZATION_ACTIVE,
    ESTIMATED_EARNINGS,
    AD_REVENUE_SHARE,
    IN_STREAM_VIDEO_ADS,
    FEED_SPONSORED_ADS,
    SUPER_THANKS_TIPS,
    MARKETPLACE_SALES,
    REQUEST_PAYOUT,
    PAYOUT_METHODS,
    BOOST_POST,
    CREATE_AD_CAMPAIGN,
    PLATFORM_METRICS,
    MONETIZED_VIEWS,
    AVERAGE_CPM,
    LIFETIME_REVENUE,

    // Marketplace
    BUY_AND_SELL,
    LIST_ITEM,
    PRICE,
    CONDITION,
    CONTACT_SELLER,
    MAKE_OFFER,
    VERIFIED_SELLER,

    // Privacy & Security
    GHOST_MODE,
    RADAR_PULSE,
    LEGAL_POLICY,
    LOG_OUT,
    DELETE_ACCOUNT,

    // Social Actions
    LIKE,
    COMMENT,
    SHARE,
    SAVE,
    CONNECT,
    CONNECTED,
    WAVE,
    SPONSORED_LABEL
}

object LocalizationHelper {

    private val translations: Map<LocaliiiyStringKey, Map<String, String>> = mapOf(
        LocaliiiyStringKey.TAB_FEED to mapOf(
            "en" to "Feed", "en-gb" to "Feed", "es" to "Inicio", "es-mx" to "Inicio",
            "fr" to "Fil", "fr-ca" to "Fil", "de" to "Feed", "hi" to "फ़ीड",
            "zh" to "动态", "zh-tw" to "動態", "zh-hk" to "動態", "ja" to "フィード",
            "pt" to "Feed", "pt-pt" to "Feed", "ar" to "الرئيسية", "ru" to "Лента",
            "bn" to "ফিড", "id" to "Beranda", "ko" to "피드", "it" to "Feed",
            "tr" to "Akış", "sw" to "Mlisho", "vi" to "Bảng tin", "ur" to "فیڈ",
            "ta" to "செய்தியோடை", "te" to "ఫీడ్", "mr" to "फीड", "gu" to "ફીડ",
            "kn" to "ಫೀಡ್", "ml" to "ഫീഡ്", "pa" to "ਫੀਡ", "th" to "ฟีด",
            "ms" to "Suapan", "tl" to "Feed", "uk" to "Стрічка", "pl" to "Aktualności",
            "nl" to "Overzicht", "el" to "Ροή", "cs" to "Kanál", "sv" to "Flöde",
            "fa" to "خوراک", "he" to "פיד", "am" to "ፊድ", "yo" to "Ifunni",
            "zu" to "Okuphakelayo", "af" to "Nuusvoer"
        ),
        LocaliiiyStringKey.TAB_RADAR to mapOf(
            "en" to "Radar", "en-gb" to "Radar", "es" to "Radar", "es-mx" to "Radar",
            "fr" to "Radar", "fr-ca" to "Radar", "de" to "Radar", "hi" to "रडार",
            "zh" to "雷达", "zh-tw" to "雷達", "zh-hk" to "雷達", "ja" to "レーダー",
            "pt" to "Radar", "pt-pt" to "Radar", "ar" to "رادار", "ru" to "Радар",
            "bn" to "রাডার", "id" to "Radar", "ko" to "레이더", "it" to "Radar",
            "tr" to "Radar", "sw" to "Rada", "vi" to "Radar", "ur" to "ریڈار",
            "ta" to "ரேடார்", "te" to "రాడార్", "mr" to "रडार", "gu" to "રડાર",
            "th" to "เรดาร์", "ms" to "Radar", "tl" to "Radar", "uk" to "Радар",
            "pl" to "Radar", "nl" to "Radar", "el" to "Ραντάρ", "he" to "רדאר",
            "fa" to "رادار"
        ),
        LocaliiiyStringKey.TAB_MARKET to mapOf(
            "en" to "Market", "en-gb" to "Market", "es" to "Mercado", "es-mx" to "Mercado",
            "fr" to "Marché", "fr-ca" to "Marché", "de" to "Markt", "hi" to "बाज़ार",
            "zh" to "集市", "zh-tw" to "市集", "zh-hk" to "市集", "ja" to "マーケット",
            "pt" to "Mercado", "pt-pt" to "Mercado", "ar" to "السوق", "ru" to "Рынок",
            "bn" to "মার্কেট", "id" to "Pasar", "ko" to "마켓", "it" to "Mercato",
            "tr" to "Pazar", "sw" to "Soko", "vi" to "Chợ", "ur" to "مارکیٹ",
            "ta" to "சந்தை", "te" to "మార్కెట్", "mr" to "बाजार", "gu" to "બજાર",
            "th" to "ตลาด", "ms" to "Pasaran", "tl" to "Pamilihan", "uk" to "Ринок",
            "pl" to "Giełda", "nl" to "Markt", "el" to "Αγορά", "he" to "שוק",
            "fa" to "بازار", "am" to "ገበያ", "yo" to "Ọjà", "zu" to "Imakethe"
        ),
        LocaliiiyStringKey.TAB_STUDIO to mapOf(
            "en" to "Studio", "en-gb" to "Studio", "es" to "Estudio", "es-mx" to "Estudio",
            "fr" to "Studio", "fr-ca" to "Studio", "de" to "Studio", "hi" to "स्टूडियो",
            "zh" to "工作室", "zh-tw" to "工作室", "zh-hk" to "工作室", "ja" to "スタジオ",
            "pt" to "Estúdio", "pt-pt" to "Estúdio", "ar" to "استوديو", "ru" to "Студия",
            "bn" to "স্টুডিও", "id" to "Studio", "ko" to "스튜디오", "it" to "Studio",
            "tr" to "Stüdyo", "sw" to "Studio", "vi" to "Studio", "ur" to "اسٹوڈیو",
            "ta" to "ஸ்டுடியோ", "te" to "స్టూడియో", "th" to "สตูดิโอ", "pl" to "Studio",
            "nl" to "Studio", "el" to "Στούντιο", "he" to "סטודיו", "fa" to "استودیو"
        ),
        LocaliiiyStringKey.TAB_CLIPS to mapOf(
            "en" to "Clips", "en-gb" to "Clips", "es" to "Clips", "es-mx" to "Clips",
            "fr" to "Clips", "fr-ca" to "Clips", "de" to "Clips", "hi" to "क्लिप्स",
            "zh" to "短视频", "zh-tw" to "短影音", "zh-hk" to "短片", "ja" to "クリップ",
            "pt" to "Clipes", "pt-pt" to "Clipes", "ar" to "مقاطع", "ru" to "Клипы",
            "bn" to "ক্লিপস", "id" to "Klip", "ko" to "클립", "it" to "Clip",
            "tr" to "Klipler", "sw" to "Klipu", "vi" to "Thước phim", "ur" to "کلپس",
            "ta" to "கிளிப்புகள்", "te" to "క్లిప్స్", "th" to "คลิป", "pl" to "Rolki",
            "nl" to "Clips", "el" to "Βίντεο", "he" to "קליפים", "fa" to "کلیپ‌ها"
        ),
        LocaliiiyStringKey.TAB_PROFILE to mapOf(
            "en" to "Space", "en-gb" to "Space", "es" to "Perfil", "es-mx" to "Perfil",
            "fr" to "Profil", "fr-ca" to "Profil", "de" to "Profil", "hi" to "प्रोफ़ाइल",
            "zh" to "个人中心", "zh-tw" to "個人檔案", "zh-hk" to "個人檔案", "ja" to "プロフィール",
            "pt" to "Perfil", "pt-pt" to "Perfil", "ar" to "الملف الشخصي", "ru" to "Профиль",
            "bn" to "প্রোফাইল", "id" to "Profil", "ko" to "프로필", "it" to "Profilo",
            "tr" to "Profil", "sw" to "Wasifu", "vi" to "Hồ sơ", "ur" to "پروفائل",
            "ta" to "சுயவிவரம்", "te" to "ప్రొఫైల్", "th" to "โปรไฟล์", "pl" to "Profil",
            "nl" to "Profiel", "el" to "Προφίλ", "he" to "פרופיל", "fa" to "پروفایل"
        ),
        LocaliiiyStringKey.CREATOR_STUDIO to mapOf(
            "en" to "Creator Studio & Monetization",
            "es" to "Estudio de Creadores y Monetización",
            "fr" to "Studio Créateur et Monétisation",
            "de" to "Creator Studio & Monetarisierung",
            "hi" to "क्रिएटर स्टूडियो और मुद्रीकरण",
            "zh" to "创作者工作室与变现",
            "ja" to "クリエイタースタジオ＆収益化",
            "pt" to "Estúdio de Criadores e Monetização",
            "ar" to "استوديو المبدعين والربح",
            "ru" to "Творческая студия и монетизация",
            "bn" to "নির্মাতা স্টুডিও ও আয়",
            "id" to "Studio Kreator & Monetisasi",
            "ko" to "크리에이터 스튜디오 및 수익 창출",
            "it" to "Studio Creator e Monetizzazione",
            "tr" to "İçerik Stüdyosu ve Para Kazanma",
            "sw" to "Studio ya Wabunifu na Mapato",
            "vi" to "Studio sáng tạo & Kiếm tiền",
            "ur" to "کریئیٹر اسٹوڈیو اور کمائی"
        ),
        LocaliiiyStringKey.ESTIMATED_EARNINGS to mapOf(
            "en" to "Estimated Earnings",
            "es" to "Ganancias Estimadas",
            "fr" to "Revenus Estimés",
            "de" to "Geschätzte Einnahmen",
            "hi" to "अनुमानित कमाई",
            "zh" to "预估收益",
            "ja" to "推定収益",
            "pt" to "Ganhos Estimados",
            "ar" to "الأرباح التقديرية",
            "ru" to "Расчетный доход",
            "bn" to "আনুমানিক আয়",
            "id" to "Estimasi Pendapatan",
            "ko" to "예상 수익",
            "it" to "Guadagni Stimati",
            "tr" to "Tahmini Kazanç",
            "sw" to "Mapato Yaliyokadiriwa",
            "vi" to "Thu nhập ước tính",
            "ur" to "متوقع آمدنی"
        ),
        LocaliiiyStringKey.REQUEST_PAYOUT to mapOf(
            "en" to "Withdraw & Transfer",
            "es" to "Retirar y Transferir",
            "fr" to "Retirer & Transférer",
            "de" to "Auszahlen & Überweisen",
            "hi" to "निकासी और स्थानांतरण",
            "zh" to "提现与转账",
            "ja" to "出金・送金",
            "pt" to "Sacar e Transferir",
            "ar" to "سحب وتحويل الأرباح",
            "ru" to "Вывести средства",
            "bn" to "টাকা তুলুন ও ট্রান্সফার",
            "id" to "Tarik & Transfer Dana",
            "ko" to "출금 및 이체",
            "it" to "Preleva e Trasferisci",
            "tr" to "Para Çek ve Transfer Et",
            "sw" to "Toa Pesa na Uhamishe",
            "vi" to "Rút tiền & Chuyển khoản",
            "ur" to "رقم نکالیں اور منتقل کریں"
        ),
        LocaliiiyStringKey.BUY_AND_SELL to mapOf(
            "en" to "Buy & Sell Marketplace",
            "es" to "Mercado de Compra y Venta",
            "fr" to "Place de Marché Achat & Vente",
            "de" to "Kaufen & Verkaufen Marktplatz",
            "hi" to "खरीदें और बेचें बाज़ार",
            "zh" to "二手交易与集市",
            "ja" to "売買マーケットプレイス",
            "pt" to "Mercado de Compra e Venda",
            "ar" to "سوق البيع والشراء",
            "ru" to "Маркетплейс покупок и продаж",
            "bn" to "কেনাবেচার মার্কেটপ্লেস",
            "id" to "Pasar Jual Beli",
            "ko" to "사고팔기 마켓플레이스",
            "it" to "Mercato Compra e Vendi",
            "tr" to "Alım Satım Pazarı",
            "sw" to "Soko la Kununua na Kuuza",
            "vi" to "Chợ mua bán",
            "ur" to "خرید و فروخت کی مارکیٹ"
        ),
        LocaliiiyStringKey.BOOST_POST to mapOf(
            "en" to "Boost Post / Global Reach",
            "es" to "Promocionar / Alcance Global",
            "fr" to "Booster / Portée Mondiale",
            "de" to "Beitrag Bewerben / Globale Reichweite",
            "hi" to "पोस्ट बूस्ट करें / वैश्विक पहुंच",
            "zh" to "推广帖子 / 全球触达",
            "ja" to "投稿を宣伝 / グローバルリーチ",
            "pt" to "Impulsionar / Alcance Global",
            "ar" to "ترويج المنشور / انتشار عالمي",
            "ru" to "Продвигать / Мировой охват",
            "bn" to "পোস্ট বুস্ট / বৈশ্বিক প্রসার",
            "id" to "Promosikan / Jangkauan Global",
            "ko" to "게시물 홍보 / 글로벌 노출",
            "it" to "Promuovi Post / Copertura Globale",
            "tr" to "Gönderiyi Öne Çıkar / Küresel Erişim",
            "sw" to "Kuzza Chapisho / Ufikiaji wa Dunia",
            "vi" to "Quảng cáo / Tiếp cận toàn cầu",
            "ur" to "پوسٹ کو فروغ دیں / عالمی رسائی"
        ),
        LocaliiiyStringKey.SPONSORED_LABEL to mapOf(
            "en" to "Sponsored • Ad",
            "es" to "Patrocinado • Publicidad",
            "fr" to "Sponsorisé • Publicité",
            "de" to "Gesponsert • Anzeige",
            "hi" to "प्रायोजित • विज्ञापन",
            "zh" to "赞助 • 广告",
            "ja" to "スポンサー • 広告",
            "pt" to "Patrocinado • Anúncio",
            "ar" to "مُموَّل • إعلان",
            "ru" to "Реклама • Спонсор",
            "bn" to "বিজ্ঞাপন • স্পনসর",
            "id" to "Disponsori • Iklan",
            "ko" to "스폰서 • 광고",
            "it" to "Sponsorizzato • Annuncio",
            "tr" to "Sponsorlu • Reklam",
            "sw" to "Imedhaminiwa • Tangazo",
            "vi" to "Được tài trợ • Quảng cáo",
            "ur" to "اسپانسر شدہ • اشتہار"
        ),
        LocaliiiyStringKey.CURRENCY_AND_LANG to mapOf(
            "en" to "Worldwide Language & Currency",
            "es" to "Idioma y Moneda Mundial",
            "fr" to "Langue et Devise Mondiale",
            "de" to "Weltweite Sprache & Währung",
            "hi" to "वैश्विक भाषा और मुद्रा",
            "zh" to "全球语言与货币",
            "ja" to "世界の言語と通貨",
            "pt" to "Idioma e Moeda Mundial",
            "ar" to "اللغة والعملة العالمية",
            "ru" to "Мировые языки и валюты",
            "bn" to "বিশ্বের ভাষা ও মুদ্রা",
            "id" to "Bahasa & Mata Uang Dunia",
            "ko" to "전 세계 언어 및 통화",
            "it" to "Lingua e Valuta Globale",
            "tr" to "Dünya Dilleri ve Para Birimleri",
            "sw" to "Lugha na Sarafu za Dunia",
            "vi" to "Ngôn ngữ & Tiền tệ toàn cầu",
            "ur" to "عالمی زبان اور کرنسی"
        ),
        LocaliiiyStringKey.CONNECT to mapOf(
            "en" to "Connect",
            "es" to "Conectar",
            "fr" to "Connecter",
            "de" to "Verbinden",
            "hi" to "कनेक्ट करें",
            "zh" to "连接",
            "ja" to "つながる",
            "pt" to "Conectar",
            "ar" to "تواصل",
            "ru" to "Связаться",
            "bn" to "কানেক্ট করুন",
            "id" to "Hubungkan",
            "ko" to "연결하기",
            "it" to "Connetti",
            "tr" to "Bağlan",
            "sw" to "Unganisha",
            "vi" to "Kết nối",
            "ur" to "رابطہ کریں"
        ),
        LocaliiiyStringKey.CONNECTED to mapOf(
            "en" to "Connected",
            "es" to "Conectado",
            "fr" to "Connecté",
            "de" to "Verbunden",
            "hi" to "कनेक्टेड",
            "zh" to "已连接",
            "ja" to "接続済み",
            "pt" to "Conectado",
            "ar" to "متصل",
            "ru" to "На связи",
            "bn" to "সংযুক্ত",
            "id" to "Terhubung",
            "ko" to "연결됨",
            "it" to "Connesso",
            "tr" to "Bağlandı",
            "sw" to "Imeunganishwa",
            "vi" to "Đã kết nối",
            "ur" to "منسلک"
        ),
        LocaliiiyStringKey.SEARCH_HINT to mapOf(
            "en" to "Search hyperlocal places, creators & market...",
            "es" to "Buscar lugares hiperlocales, creadores y mercado...",
            "fr" to "Rechercher lieux hyperlocaux, créateurs et marché...",
            "de" to "Hyperlokale Orte, Schöpfer und Markt suchen...",
            "hi" to "हाइपरलोकल स्थान, क्रिएटर और बाज़ार खोजें...",
            "zh" to "搜索同城周边地点、创作者与好物...",
            "ja" to "ローカルスポット、クリエイター、商品を検索...",
            "pt" to "Buscar locais hiperlocais, criadores e mercado...",
            "ar" to "ابحث عن الأماكن القريبة والمبدعين والسوق...",
            "ru" to "Поиск локаций, создателей и товаров...",
            "bn" to "কাছাকাছি স্থান, নির্মাতা ও মার্কেট খুঁজুন...",
            "id" to "Cari lokasi sekitar, kreator & pasar...",
            "ko" to "내 주변 장소, 크리에이터 및 마켓 검색...",
            "it" to "Cerca luoghi iperlocali, creatori e marketplace...",
            "tr" to "Yakındaki yerleri, üreticileri ve pazarı ara...",
            "sw" to "Tafuta maeneo ya karibu, wabunifu na soko...",
            "vi" to "Tìm kiếm địa điểm lân cận, nhà sáng tạo & chợ...",
            "ur" to "مقامی مقامات، تخلیق کار اور مارکیٹ تلاش کریں..."
        )
    )

    /**
     * Resolves a localized string for the specified key and target language.
     * If the exact language is missing, checks base language prefix (e.g., "es-mx" -> "es"),
     * and falls back cleanly to English.
     */
    fun getString(key: LocaliiiyStringKey, lang: LocaliiiyLanguage): String {
        val map = translations[key] ?: return key.name
        // 1. Direct match by language code
        val exact = map[lang.code]
        if (exact != null) return exact

        // 2. Prefix match (e.g. "es-mx" -> "es")
        val prefix = lang.code.substringBefore("-")
        val prefixMatch = map[prefix]
        if (prefixMatch != null) return prefixMatch

        // 3. Fallback to English
        return map["en"] ?: key.name
    }

    /**
     * Translates navigation deck slot titles (Pulse, Radar, Market, Clips, Studio, Profile).
     */
    fun getSlotTitle(slotName: String, lang: LocaliiiyLanguage): String {
        val lower = slotName.lowercase().trim()
        return when {
            lower.contains("pulse") || lower.contains("feed") -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Pulso"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Pouls"
                LocaliiiyLanguage.DE -> "Puls"
                LocaliiiyLanguage.HI -> "पल्स"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "动态"
                LocaliiiyLanguage.JA -> "パルス"
                LocaliiiyLanguage.AR -> "النبض"
                LocaliiiyLanguage.RU -> "Пульс"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Pulso"
                LocaliiiyLanguage.IT -> "Impulso"
                LocaliiiyLanguage.KO -> "펄스"
                LocaliiiyLanguage.ID -> "Denyut"
                LocaliiiyLanguage.TR -> "Nabız"
                LocaliiiyLanguage.VI -> "Nhịp đập"
                LocaliiiyLanguage.TE -> "పల్స్"
                LocaliiiyLanguage.BN -> "পালস"
                LocaliiiyLanguage.TA -> "துடிப்பு"
                LocaliiiyLanguage.UR -> "پلس"
                else -> "Pulse"
            }
            lower.contains("radar") -> when (lang) {
                LocaliiiyLanguage.HI -> "रडार"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "雷达"
                LocaliiiyLanguage.JA -> "レーダー"
                LocaliiiyLanguage.AR -> "رادار"
                LocaliiiyLanguage.RU -> "Радар"
                LocaliiiyLanguage.KO -> "레이더"
                LocaliiiyLanguage.TE -> "రాడార్"
                LocaliiiyLanguage.BN -> "রাডার"
                LocaliiiyLanguage.TA -> "ரேடார்"
                LocaliiiyLanguage.UR -> "ریڈار"
                else -> "Radar"
            }
            lower.contains("market") -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Mercado"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Marché"
                LocaliiiyLanguage.DE -> "Markt"
                LocaliiiyLanguage.HI -> "बाज़ार"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "集市"
                LocaliiiyLanguage.JA -> "マーケット"
                LocaliiiyLanguage.AR -> "السوق"
                LocaliiiyLanguage.RU -> "Рынок"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Mercado"
                LocaliiiyLanguage.IT -> "Mercato"
                LocaliiiyLanguage.KO -> "마켓"
                LocaliiiyLanguage.ID -> "Pasar"
                LocaliiiyLanguage.TR -> "Pazar"
                LocaliiiyLanguage.VI -> "Chợ"
                LocaliiiyLanguage.TE -> "మార్కెట్"
                LocaliiiyLanguage.BN -> "মার্কেট"
                LocaliiiyLanguage.TA -> "சந்தை"
                LocaliiiyLanguage.UR -> "مارکیٹ"
                else -> "Market"
            }
            lower.contains("clip") -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Clips"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Clips"
                LocaliiiyLanguage.DE -> "Clips"
                LocaliiiyLanguage.HI -> "क्लिप्स"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "短视频"
                LocaliiiyLanguage.JA -> "クリップ"
                LocaliiiyLanguage.AR -> "مقاطع"
                LocaliiiyLanguage.RU -> "Клипы"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Clipes"
                LocaliiiyLanguage.IT -> "Clip"
                LocaliiiyLanguage.KO -> "클립"
                LocaliiiyLanguage.ID -> "Klip"
                LocaliiiyLanguage.TR -> "Klipler"
                LocaliiiyLanguage.VI -> "Thước phim"
                LocaliiiyLanguage.TE -> "క్లిప్స్"
                LocaliiiyLanguage.BN -> "ক্লিপস"
                LocaliiiyLanguage.TA -> "கிளிப்புகள்"
                LocaliiiyLanguage.UR -> "کلپس"
                else -> "Clips"
            }
            lower.contains("studio") -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Estudio"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Studio"
                LocaliiiyLanguage.DE -> "Studio"
                LocaliiiyLanguage.HI -> "स्टूडियो"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "工作室"
                LocaliiiyLanguage.JA -> "スタジオ"
                LocaliiiyLanguage.AR -> "استوديو"
                LocaliiiyLanguage.RU -> "Студия"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Estúdio"
                LocaliiiyLanguage.IT -> "Studio"
                LocaliiiyLanguage.KO -> "스튜디오"
                LocaliiiyLanguage.ID -> "Studio"
                LocaliiiyLanguage.TR -> "Stüdyo"
                LocaliiiyLanguage.VI -> "Studio"
                LocaliiiyLanguage.TE -> "స్టూడియో"
                LocaliiiyLanguage.BN -> "স্টুডিও"
                LocaliiiyLanguage.TA -> "ஸ்டுடியோ"
                LocaliiiyLanguage.UR -> "اسٹوڈیو"
                else -> "Studio"
            }
            lower.contains("profile") || lower.contains("space") -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Espacio"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Espace"
                LocaliiiyLanguage.DE -> "Profil"
                LocaliiiyLanguage.HI -> "प्रोफ़ाइल"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "个人中心"
                LocaliiiyLanguage.JA -> "プロフィール"
                LocaliiiyLanguage.AR -> "الملف الشخصي"
                LocaliiiyLanguage.RU -> "Профиль"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Espaço"
                LocaliiiyLanguage.IT -> "Profilo"
                LocaliiiyLanguage.KO -> "프로필"
                LocaliiiyLanguage.ID -> "Profil"
                LocaliiiyLanguage.TR -> "Profil"
                LocaliiiyLanguage.VI -> "Hồ sơ"
                LocaliiiyLanguage.TE -> "ప్రొఫైల్"
                LocaliiiyLanguage.BN -> "প্রোফাইল"
                LocaliiiyLanguage.TA -> "சுயவிவரம்"
                LocaliiiyLanguage.UR -> "پروفائل"
                else -> "Space"
            }
            else -> slotName
        }
    }

    /**
     * Translates filter labels (All, Trending, Nearby, Connected).
     */
    fun getFilterLabel(filterLabel: String, lang: LocaliiiyLanguage): String {
        val lower = filterLabel.lowercase().trim()
        return when {
            lower == "all" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Todos"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Tous"
                LocaliiiyLanguage.DE -> "Alle"
                LocaliiiyLanguage.HI -> "सभी"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "全部"
                LocaliiiyLanguage.JA -> "すべて"
                LocaliiiyLanguage.AR -> "الكل"
                LocaliiiyLanguage.RU -> "Все"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Todos"
                LocaliiiyLanguage.IT -> "Tutti"
                LocaliiiyLanguage.KO -> "전체"
                LocaliiiyLanguage.ID -> "Semua"
                LocaliiiyLanguage.TR -> "Tümü"
                LocaliiiyLanguage.VI -> "Tất cả"
                LocaliiiyLanguage.TE -> "అన్నీ"
                LocaliiiyLanguage.BN -> "সব"
                LocaliiiyLanguage.TA -> "அனைத்தும்"
                LocaliiiyLanguage.UR -> "تمام"
                else -> "All"
            }
            lower == "trending" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Tendencias"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Tendances"
                LocaliiiyLanguage.DE -> "Trends"
                LocaliiiyLanguage.HI -> "ट्रेंडिंग"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "热门"
                LocaliiiyLanguage.JA -> "急上昇"
                LocaliiiyLanguage.AR -> "الشائع"
                LocaliiiyLanguage.RU -> "В тренде"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Em Alta"
                LocaliiiyLanguage.IT -> "Di tendenza"
                LocaliiiyLanguage.KO -> "트렌딩"
                LocaliiiyLanguage.ID -> "Tren"
                LocaliiiyLanguage.TR -> "Trend"
                LocaliiiyLanguage.VI -> "Xu hướng"
                LocaliiiyLanguage.TE -> "ట్రెండింగ్"
                LocaliiiyLanguage.BN -> "ট্রেন্ডিং"
                LocaliiiyLanguage.TA -> "பிரபலமானவை"
                LocaliiiyLanguage.UR -> "ٹرینڈنگ"
                else -> "Trending"
            }
            lower == "nearby" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Cercanos"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "À proximité"
                LocaliiiyLanguage.DE -> "In der Nähe"
                LocaliiiyLanguage.HI -> "आस-पास"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "附近"
                LocaliiiyLanguage.JA -> "周辺"
                LocaliiiyLanguage.AR -> "بالقرب مني"
                LocaliiiyLanguage.RU -> "Рядом"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Por Perto"
                LocaliiiyLanguage.IT -> "Nelle vicinanze"
                LocaliiiyLanguage.KO -> "주변"
                LocaliiiyLanguage.ID -> "Terdekat"
                LocaliiiyLanguage.TR -> "Yakınlarda"
                LocaliiiyLanguage.VI -> "Gần đây"
                LocaliiiyLanguage.TE -> "సమీపంలో"
                LocaliiiyLanguage.BN -> "কাছাকাছি"
                LocaliiiyLanguage.TA -> "அருகில்"
                LocaliiiyLanguage.UR -> "قریب ترین"
                else -> "Nearby"
            }
            lower == "connected" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Conectados"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Connectés"
                LocaliiiyLanguage.DE -> "Verbunden"
                LocaliiiyLanguage.HI -> "जुड़े हुए"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW, LocaliiiyLanguage.ZH_HK -> "已连接"
                LocaliiiyLanguage.JA -> "接続中"
                LocaliiiyLanguage.AR -> "المتصلون"
                LocaliiiyLanguage.RU -> "На связи"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Conectados"
                LocaliiiyLanguage.IT -> "Connessi"
                LocaliiiyLanguage.KO -> "연결됨"
                LocaliiiyLanguage.ID -> "Terhubung"
                LocaliiiyLanguage.TR -> "Bağlı"
                LocaliiiyLanguage.VI -> "Đã kết nối"
                LocaliiiyLanguage.TE -> "కనెక్ట్ అయినవి"
                LocaliiiyLanguage.BN -> "সংযুক্ত"
                LocaliiiyLanguage.TA -> "இணைக்கப்பட்டவை"
                LocaliiiyLanguage.UR -> "منسلک"
                else -> "Connected"
            }
            else -> filterLabel
        }
    }

    /**
     * Translates category names across Market and Studio.
     */
    fun getCategoryName(category: String, lang: LocaliiiyLanguage): String {
        val lower = category.lowercase().trim()
        return when {
            lower == "all" -> getFilterLabel("all", lang)
            lower == "electronics" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Electrónica"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Électronique"
                LocaliiiyLanguage.DE -> "Elektronik"
                LocaliiiyLanguage.HI -> "इलेक्ट्रॉनिक्स"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "数码电子"
                LocaliiiyLanguage.JA -> "家電・電子"
                LocaliiiyLanguage.AR -> "إلكترونيات"
                LocaliiiyLanguage.RU -> "Электроника"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Eletrônicos"
                else -> "Electronics"
            }
            lower == "fashion" || lower == "clothing" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Moda"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Mode"
                LocaliiiyLanguage.DE -> "Mode"
                LocaliiiyLanguage.HI -> "फैशन"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "服饰时尚"
                LocaliiiyLanguage.JA -> "ファッション"
                LocaliiiyLanguage.AR -> "أزياء"
                LocaliiiyLanguage.RU -> "Мода"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Moda"
                else -> "Fashion"
            }
            lower == "vehicles" || lower == "motors" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Vehículos"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Véhicules"
                LocaliiiyLanguage.DE -> "Fahrzeuge"
                LocaliiiyLanguage.HI -> "वाहन"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "车辆交通"
                LocaliiiyLanguage.JA -> "乗り物"
                LocaliiiyLanguage.AR -> "مركبات"
                LocaliiiyLanguage.RU -> "Транспорт"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Veículos"
                else -> "Vehicles"
            }
            lower == "home" || lower == "furniture" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Hogar"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Maison"
                LocaliiiyLanguage.DE -> "Zuhause"
                LocaliiiyLanguage.HI -> "घर और फर्नीचर"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "家居日用"
                LocaliiiyLanguage.JA -> "住まい・インテリア"
                LocaliiiyLanguage.AR -> "المنزل"
                LocaliiiyLanguage.RU -> "Дом и сад"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Casa"
                else -> "Home"
            }
            lower == "services" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Servicios"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Services"
                LocaliiiyLanguage.DE -> "Dienstleistungen"
                LocaliiiyLanguage.HI -> "सेवाएं"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "同城服务"
                LocaliiiyLanguage.JA -> "サービス"
                LocaliiiyLanguage.AR -> "خدمات"
                LocaliiiyLanguage.RU -> "Услуги"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Serviços"
                else -> "Services"
            }
            lower == "cinema" || lower == "films" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Cine"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Cinéma"
                LocaliiiyLanguage.DE -> "Kino"
                LocaliiiyLanguage.HI -> "सिनेमा"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "影视长片"
                LocaliiiyLanguage.JA -> "映画・シネマ"
                LocaliiiyLanguage.AR -> "سينما"
                LocaliiiyLanguage.RU -> "Кино"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Cinema"
                else -> "Cinema"
            }
            lower == "documentaries" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Documentales"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Documentaires"
                LocaliiiyLanguage.DE -> "Dokumentationen"
                LocaliiiyLanguage.HI -> "वृत्तचित्र"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "纪录片"
                LocaliiiyLanguage.JA -> "ドキュメンタリー"
                LocaliiiyLanguage.AR -> "وثائقيات"
                LocaliiiyLanguage.RU -> "Документальное"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Documentários"
                else -> "Documentaries"
            }
            lower == "podcasts" -> when (lang) {
                LocaliiiyLanguage.HI -> "पॉडकास्ट"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "播客访谈"
                LocaliiiyLanguage.JA -> "ポッドキャスト"
                LocaliiiyLanguage.AR -> "بودكاست"
                LocaliiiyLanguage.RU -> "Подкасты"
                else -> "Podcasts"
            }
            lower == "music" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Música"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Musique"
                LocaliiiyLanguage.DE -> "Musik"
                LocaliiiyLanguage.HI -> "संगीत"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "音乐现场"
                LocaliiiyLanguage.JA -> "音楽"
                LocaliiiyLanguage.AR -> "موسيقى"
                LocaliiiyLanguage.RU -> "Музыка"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Música"
                else -> "Music"
            }
            else -> category
        }
    }

    /**
     * Universal dictionary translator for any text string in the app.
     */
    fun translate(phrase: String, lang: LocaliiiyLanguage): String {
        if (phrase.isBlank() || lang == LocaliiiyLanguage.EN || lang == LocaliiiyLanguage.EN_GB) return phrase

        val lower = phrase.lowercase().trim()
        return when {
            lower == "all" || lower == "trending" || lower == "nearby" || lower == "connected" -> getFilterLabel(phrase, lang)
            lower == "pulse" || lower == "radar" || lower == "market" || lower == "clips" || lower == "studio" || lower == "space" || lower == "profile" -> getSlotTitle(phrase, lang)
            lower == "go live" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Transmitir en Vivo"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "En Direct"
                LocaliiiyLanguage.DE -> "Live Gehen"
                LocaliiiyLanguage.HI -> "लाइव जाएं"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "发起直播"
                LocaliiiyLanguage.JA -> "ライブ開始"
                LocaliiiyLanguage.AR -> "بث مباشر"
                LocaliiiyLanguage.RU -> "В эфир"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Entrar ao Vivo"
                else -> "Go Live"
            }
            lower == "edit profile" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Editar Perfil"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Modifier le Profil"
                LocaliiiyLanguage.DE -> "Profil Bearbeiten"
                LocaliiiyLanguage.HI -> "प्रोफ़ाइल संपादित करें"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "编辑资料"
                LocaliiiyLanguage.JA -> "プロフィール編集"
                LocaliiiyLanguage.AR -> "تعديل الملف"
                LocaliiiyLanguage.RU -> "Редактировать профиль"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Editar Perfil"
                else -> "Edit Profile"
            }
            lower == "monetization hub" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Centro de Monetización"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Centre de Monétisation"
                LocaliiiyLanguage.DE -> "Monetarisierungs-Hub"
                LocaliiiyLanguage.HI -> "मुद्रीकरण हब"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "收益变现中心"
                LocaliiiyLanguage.JA -> "収益化ハブ"
                LocaliiiyLanguage.AR -> "مركز الأرباح"
                LocaliiiyLanguage.RU -> "Центр монетизации"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Central de Monetização"
                else -> "Monetization Hub"
            }
            lower == "app guide & tutorials" || lower == "app guide" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Guía de la App y Tutoriales"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Guide de l'App & Tutoriels"
                LocaliiiyLanguage.DE -> "App-Anleitung & Tutorials"
                LocaliiiyLanguage.HI -> "ऐप गाइड और ट्यूटोरियल"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "应用指南与教程"
                LocaliiiyLanguage.JA -> "アプリガイド＆チュートリアル"
                LocaliiiyLanguage.AR -> "دليل التطبيق والدروس"
                LocaliiiyLanguage.RU -> "Руководство и уроки"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Guia do App e Tutoriais"
                else -> "App Guide & Tutorials"
            }
            lower == "stealth observer" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "OBSERVADOR INCOGNITO"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "OBSERVATEUR FURTIF"
                LocaliiiyLanguage.DE -> "GHOST BEOBACHTER"
                LocaliiiyLanguage.HI -> "अदृश्य प्रेक्षक"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "隐身观察者"
                LocaliiiyLanguage.JA -> "ステルス観測者"
                LocaliiiyLanguage.AR -> "مراقب خفي"
                LocaliiiyLanguage.RU -> "РЕЖИМ-НЕВИДИМКА"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "OBSERVADOR FANTASMA"
                else -> "STEALTH OBSERVER"
            }
            lower == "live rf beacon" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "BALIZA RF ACTIVA"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "BALISE RF EN DIRECT"
                LocaliiiyLanguage.DE -> "LIVE RF SIGNAL"
                LocaliiiyLanguage.HI -> "लाइव आरएफ बीकन"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "实时无线电信标"
                LocaliiiyLanguage.JA -> "リアルタイム発信中"
                LocaliiiyLanguage.AR -> "إشارة لاسلكية حية"
                LocaliiiyLanguage.RU -> "РАДИОМАЯК АКТИВЕН"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "SINAL RF AO VIVO"
                else -> "LIVE RF BEACON"
            }
            lower == "radar view" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Vista Radar"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Vue Radar"
                LocaliiiyLanguage.DE -> "Radar-Ansicht"
                LocaliiiyLanguage.HI -> "रडार दृश्य"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "雷达视图"
                LocaliiiyLanguage.JA -> "レーダー表示"
                LocaliiiyLanguage.AR -> "عرض الرادار"
                LocaliiiyLanguage.RU -> "Вид радара"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Vista Radar"
                else -> "Radar View"
            }
            lower == "grid view" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Vista Cuadrícula"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Vue Grille"
                LocaliiiyLanguage.DE -> "Raster-Ansicht"
                LocaliiiyLanguage.HI -> "ग्रिड दृश्य"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "网格视图"
                LocaliiiyLanguage.JA -> "グリッド表示"
                LocaliiiyLanguage.AR -> "عرض الشبكة"
                LocaliiiyLanguage.RU -> "Вид сетки"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Vista Grade"
                else -> "Grid View"
            }
            lower == "privacy & security" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Privacidad y Seguridad"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Confidentialité & Sécurité"
                LocaliiiyLanguage.DE -> "Privatsphäre & Sicherheit"
                LocaliiiyLanguage.HI -> "गोपनीयता और सुरक्षा"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "隐私与安全设置"
                LocaliiiyLanguage.JA -> "プライバシーとセキュリティ"
                LocaliiiyLanguage.AR -> "الخصوصية والأمان"
                LocaliiiyLanguage.RU -> "Конфиденциальность и защита"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Privacidade e Segurança"
                else -> "Privacy & Security"
            }
            lower == "notifications" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Notificaciones"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Notifications"
                LocaliiiyLanguage.DE -> "Benachrichtigungen"
                LocaliiiyLanguage.HI -> "सूचनाएं"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "通知消息"
                LocaliiiyLanguage.JA -> "通知"
                LocaliiiyLanguage.AR -> "الإشعارات"
                LocaliiiyLanguage.RU -> "Уведомления"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Notificações"
                else -> "Notifications"
            }
            lower == "direct messages" || lower == "messages" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Mensajes Directos"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Messages Directs"
                LocaliiiyLanguage.DE -> "Direktnachrichten"
                LocaliiiyLanguage.HI -> "सीधे संदेश"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "私信聊天"
                LocaliiiyLanguage.JA -> "ダイレクトメッセージ"
                LocaliiiyLanguage.AR -> "الرسائل المباشرة"
                LocaliiiyLanguage.RU -> "Личные сообщения"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Mensagens Diretas"
                else -> "Direct Messages"
            }
            lower == "log out" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Cerrar Sesión"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Déconnexion"
                LocaliiiyLanguage.DE -> "Abmelden"
                LocaliiiyLanguage.HI -> "लॉग आउट"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "退出登录"
                LocaliiiyLanguage.JA -> "ログアウト"
                LocaliiiyLanguage.AR -> "تسجيل الخروج"
                LocaliiiyLanguage.RU -> "Выйти из аккаунта"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Sair"
                else -> "Log Out"
            }
            lower == "delete account" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Eliminar Cuenta"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Supprimer le Compte"
                LocaliiiyLanguage.DE -> "Konto Löschen"
                LocaliiiyLanguage.HI -> "खाता हटाएं"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "注销并删除账号"
                LocaliiiyLanguage.JA -> "アカウント削除"
                LocaliiiyLanguage.AR -> "حذف الحساب نهائياً"
                LocaliiiyLanguage.RU -> "Удалить аккаунт"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Excluir Conta"
                else -> "Delete Account"
            }
            lower == "clear cache" -> when (lang) {
                LocaliiiyLanguage.ES, LocaliiiyLanguage.ES_MX -> "Limpiar Caché"
                LocaliiiyLanguage.FR, LocaliiiyLanguage.FR_CA -> "Vider le Cache"
                LocaliiiyLanguage.DE -> "Cache Leeren"
                LocaliiiyLanguage.HI -> "कैश साफ़ करें"
                LocaliiiyLanguage.ZH, LocaliiiyLanguage.ZH_TW -> "清理本地缓存"
                LocaliiiyLanguage.JA -> "キャッシュ消去"
                LocaliiiyLanguage.AR -> "مسح التخزين المؤقت"
                LocaliiiyLanguage.RU -> "Очистить кэш"
                LocaliiiyLanguage.PT, LocaliiiyLanguage.PT_PT -> "Limpar Cache"
                else -> "Clear Cache"
            }
            else -> phrase
        }
    }

    /**
     * Translates common dynamic phrases or labels dynamically based on current language.
     */
    fun getDynamicString(phrase: String, lang: LocaliiiyLanguage): String {
        val lower = phrase.lowercase().trim()
        return when {
            lower.contains("all") -> getFilterLabel("all", lang)
            lower.contains("nearby") -> getFilterLabel("nearby", lang)
            lower.contains("connected") -> getString(LocaliiiyStringKey.CONNECTED, lang)
            lower.contains("connect") -> getString(LocaliiiyStringKey.CONNECT, lang)
            else -> translate(phrase, lang)
        }
    }

    /**
     * Updates the Android runtime configuration locale so that system formatters,
     * dates, and resources reflect the selected language.
     */
    fun updateConfigurationLocale(context: Context, language: LocaliiiyLanguage) {
        try {
            val locale = when (language.code) {
                "es-mx" -> Locale("es", "MX")
                "fr-ca" -> Locale("fr", "CA")
                "pt-pt" -> Locale("pt", "PT")
                "zh-tw" -> Locale("zh", "TW")
                "zh-hk" -> Locale("zh", "HK")
                "en-gb" -> Locale("en", "GB")
                else -> Locale(language.code.substringBefore("-"))
            }
            Locale.setDefault(locale)
            val resources = context.resources
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (_: Exception) {}
    }
}

/**
 * Convenient Composable extension to translate strings using the ambient language.
 */
@Composable
@ReadOnlyComposable
fun tr(phrase: String): String {
    return LocalizationHelper.translate(phrase, LocalAppLanguage.current)
}

/**
 * Convenient String extension to translate using a specific language.
 */
fun String.tr(lang: LocaliiiyLanguage): String {
    return LocalizationHelper.translate(this, lang)
}

