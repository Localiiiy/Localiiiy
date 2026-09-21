package com.example.util

import android.content.Context
import android.content.Intent

/**
 * Utility for unified external sharing across Posts, Clips, Marketplace, Studio, Profiles, and App Invites.
 * Automatically appends a localized 'Download the Localiiiy app' note with a deep link to the current webapp URL.
 */
object ShareHelper {

    const val WEBAPP_BASE_URL = "https://localiiiy.web.app"
    const val GOOGLE_DRIVE_APK_URL = "https://drive.google.com/file/d/1CiMuxjfPOlGKg-aCDhhVZvnimhOiljMq/view?usp=drivesdk"
    const val PREVIEW_WEBAPP_URL = "https://ais-pre-4dr4uba5q4ohbxd3nqa7pf-906260591053.asia-southeast1.run.app"

    fun getAppDownloadNote(language: LocaliiiyLanguage = LocaliiiyLanguage.EN): String {
        val message = when (language) {
            LocaliiiyLanguage.HI -> "📲 Localiiiy डाउनलोड करें: वेबऐप से चलाएं या सीधे Android APK डाउनलोड करें।"
            LocaliiiyLanguage.TE -> "📲 Localiiiy ని డౌన్‌లోడ్ చేసుకోండి: వెబ్‌యాప్ ద్వారా ఉపయోగించండి లేదా Android APK ని నేరుగా డౌన్‌లోడ్ చేయండి."
            LocaliiiyLanguage.ES -> "📲 Descarga Localiiiy: Usa la webapp o descarga el APK oficial directamente."
            LocaliiiyLanguage.FR -> "📲 Téléchargez Localiiiy : Utilisez l'application web ou téléchargez directement le fichier APK officiel."
            LocaliiiyLanguage.DE -> "📲 Localiiiy herunterladen: Nutze die Webapp oder lade die offizielle Android-APK herunter."
            LocaliiiyLanguage.JA -> "📲 Localiiiyをダウンロード: Webアプリを使用するか、公式Android APKを直接ダウンロードしてください。"
            LocaliiiyLanguage.ZH -> "📲 下载 Localiiiy：使用 Web 应用或直接下载官方 Android APK。"
            LocaliiiyLanguage.AR -> "📲 حمّل Localiiiy: استخدم تطبيق الويب أو حمّل ملف APK الرسمي مباشرة."
            else -> "📲 Localiiiy — Connect with your neighborhood Earth through live radar, creator clips, and local pulses, Market and Studio."
        }
        return "$message\n🌐 Web App: $WEBAPP_BASE_URL\n📦 Android APK (Google Drive): $GOOGLE_DRIVE_APK_URL"
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
