package com.example.util

import android.content.Context
import android.content.Intent

/**
 * Utility for unified external sharing across Posts, Clips, Marketplace, Studio, Profiles, and App Invites.
 * Automatically appends a localized 'Download the Localiiiy app' note with a deep link to the current webapp URL.
 */
object ShareHelper {

    const val WEBAPP_BASE_URL = "https://localiiiy.web.app"
    const val PREVIEW_WEBAPP_URL = "https://ais-pre-4dr4uba5q4ohbxd3nqa7pf-906260591053.asia-southeast1.run.app"

    fun getAppDownloadNote(language: LocaliiiyLanguage = LocaliiiyLanguage.EN): String {
        val message = when (language) {
            LocaliiiyLanguage.HI -> "📲 Localiiiy ऐप डाउनलोड करें: अपने नजदीकी हाइपरलोकल पल्स, लाइव प्रॉक्सिमिटी रडार, क्रिएटर क्लिप्स और पड़ोस के मार्केटप्लेस सौदों की खोज करें।"
            LocaliiiyLanguage.TE -> "📲 Localiiiy యాప్‌ను డౌన్‌లోడ్ చేసుకోండి: మీ సమీపంలోని హైపర్‌లోకల్ పల్స్‌లు, లైవ్ సామీప్య రాడార్, క్రియేటర్ క్లిప్‌లు మరియు పరిసరాల మార్కెట్‌ప్లేస్ ఆఫర్‌లను కనుగొనండి."
            LocaliiiyLanguage.ES -> "📲 Descarga la app Localiiiy para descubrir pulsos hiperlocales, radar de proximidad en vivo, clips de creadores y ofertas del mercado vecinal cerca de ti."
            LocaliiiyLanguage.FR -> "📲 Téléchargez l'application Localiiiy pour découvrir les pulsations hyperlocales, le radar de proximité en direct, les clips de créateurs et les annonces de quartier près de chez vous."
            LocaliiiyLanguage.DE -> "📲 Lade die Localiiiy-App herunter: Entdecke hyperlokale Impulse, Live-Näherungsradar, Creator-Clips und Angebote des Nachbarschaftsmarktplatzes in deiner Nähe."
            LocaliiiyLanguage.JA -> "📲 Localiiiyアプリをダウンロード: お近くのハイパーローカルなパルス、ライブ近接レーダー、クリエイター動画、地域マーケットプレイスを発見しよう。"
            LocaliiiyLanguage.ZH -> "📲 下载 Localiiiy 应用程序：发现您附近的超本地脉搏、实时近距离雷达、创作者短片和社区集市好物。"
            LocaliiiyLanguage.AR -> "📲 حمّل تطبيق Localiiiy: اكتشف النبضات المحلية ورادار القرب المباشر ومقاطع المبدعين وعروض السوق بالقرب منك."
            else -> "📲 Localiiiy — Connect with your neighborhood Earth through live radar, creator clips, and local pulses, Market and Studio."
        }
        return "$message\n🔗 $WEBAPP_BASE_URL"
    }

    fun buildDeepLink(itemType: String, id: Any): String {
        val cleanType = itemType.lowercase().trim()
        return when (cleanType) {
            "clip", "clips" -> "$WEBAPP_BASE_URL/clip/$id"
            "post", "pulse" -> "$WEBAPP_BASE_URL/post/$id"
            "market", "marketplace", "goods" -> "$WEBAPP_BASE_URL/market/$id"
            "studio", "video" -> "$WEBAPP_BASE_URL/studio/$id"
            "profile", "user" -> "$WEBAPP_BASE_URL/user/$id"
            else -> "$WEBAPP_BASE_URL/share/$id"
        }
    }

    fun buildShareText(
        title: String,
        itemType: String = "Post", // "Post", "Clip", "Market", "Studio", "Profile", "Invite", "App"
        id: Any? = null,
        creatorHandle: String? = null,
        caption: String? = null,
        location: String? = null,
        priceFormatted: String? = null,
        language: LocaliiiyLanguage = LocaliiiyLanguage.EN
    ): String {
        val deepLink = if (id != null) buildDeepLink(itemType, id) else WEBAPP_BASE_URL
        val builder = StringBuilder()

        when (itemType.lowercase()) {
            "clip", "clips" -> {
                val handle = creatorHandle?.let { if (it.startsWith("@")) it else "@$it" } ?: "a creator"
                builder.append("Watch $handle's creator clip on Localiiiy! 🎥✨\n")
                if (!caption.isNullOrBlank()) {
                    builder.append("\"$caption\"\n")
                }
                if (!location.isNullOrBlank()) {
                    builder.append("📍 $location\n")
                }
                builder.append("\n🔗 Watch Clip: $deepLink\n")
            }
            "market", "marketplace", "goods" -> {
                builder.append("🛍️ $title\n")
                if (!priceFormatted.isNullOrBlank()) {
                    builder.append("💰 Price: $priceFormatted\n")
                }
                if (!creatorHandle.isNullOrBlank()) {
                    val handle = if (creatorHandle.startsWith("@")) creatorHandle else "@$creatorHandle"
                    builder.append("👤 Listed by: $handle\n")
                }
                if (!location.isNullOrBlank()) {
                    builder.append("📍 Location: $location\n")
                }
                if (!caption.isNullOrBlank()) {
                    builder.append("\n\"$caption\"\n")
                }
                builder.append("\n🔗 View Listing: $deepLink\n")
            }
            "studio", "video" -> {
                builder.append("🎬 Watch \"$title\" on Localiiiy Studio\n")
                if (!creatorHandle.isNullOrBlank()) {
                    val handle = if (creatorHandle.startsWith("@")) creatorHandle else "@$creatorHandle"
                    builder.append("Created by: $handle\n")
                }
                if (!caption.isNullOrBlank()) {
                    builder.append("\n$caption\n")
                }
                builder.append("\n🔗 Watch Video: $deepLink\n")
            }
            "profile", "user" -> {
                val handle = creatorHandle?.let { if (it.startsWith("@")) it else "@$it" } ?: title
                builder.append("👤 Check out $handle on Localiiiy!\n")
                if (!caption.isNullOrBlank()) {
                    builder.append("\"$caption\"\n")
                }
                if (!location.isNullOrBlank()) {
                    builder.append("📍 $location\n")
                }
                builder.append("\n🔗 View Profile: $deepLink\n")
            }
            "invite" -> {
                builder.append("🤝 Hey neighbor! Join me on Localiiiy, the tracker-free neighborhood network.\n")
                if (!caption.isNullOrBlank()) {
                    builder.append("$caption\n")
                }
                builder.append("\n🔗 Connect here: $deepLink\n")
            }
            else -> {
                // Generic post / pulse
                val handle = creatorHandle?.let { if (it.startsWith("@")) it else "@$it" }
                if (handle != null) {
                    builder.append("Pulse by $handle on Localiiiy:\n")
                } else {
                    builder.append("Check this out on Localiiiy:\n")
                }
                if (!caption.isNullOrBlank()) {
                    builder.append("\"$caption\"\n")
                } else {
                    builder.append("$title\n")
                }
                if (!location.isNullOrBlank()) {
                    builder.append("📍 $location\n")
                }
                builder.append("\n🔗 View Pulse: $deepLink\n")
            }
        }

        builder.append("\n━━━━━━━━━━━━━━━━━━━━\n")
        builder.append(getAppDownloadNote(language))

        return builder.toString()
    }

    fun launchNativeShare(
        context: Context,
        shareText: String,
        subject: String = "Shared via Localiiiy",
        chooserTitle: String = "Share Outside App"
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(sendIntent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
