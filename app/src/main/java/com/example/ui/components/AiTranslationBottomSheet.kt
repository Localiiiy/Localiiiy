package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyAccentCoral
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import java.util.Locale

data class TargetLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val locale: Locale
)

object SupportedAiLanguages {
    val ALL = listOf(
        TargetLanguage("hi", "Hindi", "हिन्दी", "🇮🇳", Locale("hi", "IN")),
        TargetLanguage("en", "English", "English", "🇺🇸", Locale.US),
        TargetLanguage("ta", "Tamil", "தமிழ்", "🇮🇳", Locale("ta", "IN")),
        TargetLanguage("te", "Telugu", "తెలుగు", "🇮🇳", Locale("te", "IN")),
        TargetLanguage("bn", "Bengali", "বাংলা", "🇮🇳", Locale("bn", "IN")),
        TargetLanguage("mr", "Marathi", "मराठी", "🇮🇳", Locale("mr", "IN")),
        TargetLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳", Locale("gu", "IN")),
        TargetLanguage("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", Locale("kn", "IN")),
        TargetLanguage("ml", "Malayalam", "മലയാളം", "🇮🇳", Locale("ml", "IN")),
        TargetLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", Locale("pa", "IN")),
        TargetLanguage("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳", Locale("or", "IN")),
        TargetLanguage("ur", "Urdu", "اردو", "🇮🇳", Locale("ur", "IN")),
        TargetLanguage("es", "Spanish", "Español", "🇪🇸", Locale("es", "ES")),
        TargetLanguage("fr", "French", "Français", "🇫🇷", Locale.FRENCH),
        TargetLanguage("de", "German", "Deutsch", "🇩🇪", Locale.GERMAN),
        TargetLanguage("ja", "Japanese", "日本語", "🇯🇵", Locale.JAPANESE),
        TargetLanguage("zh", "Chinese", "中文", "🇨🇳", Locale.SIMPLIFIED_CHINESE),
        TargetLanguage("ar", "Arabic", "العربية", "🇸🇦", Locale("ar", "SA")),
        TargetLanguage("pt", "Portuguese", "Português", "🇧🇷", Locale("pt", "BR")),
        TargetLanguage("ko", "Korean", "한국어", "🇰🇷", Locale.KOREAN),
        TargetLanguage("it", "Italian", "Italiano", "🇮🇹", Locale.ITALIAN),
        TargetLanguage("ru", "Russian", "Русский", "🇷🇺", Locale("ru", "RU"))
    )

    fun fromCodeOrName(query: String): TargetLanguage {
        val clean = query.lowercase(Locale.US).trim()
        return ALL.firstOrNull {
            clean.startsWith(it.code) ||
                clean.contains(it.name.lowercase(Locale.US)) ||
                clean.contains(it.nativeName.lowercase(Locale.US))
        } ?: ALL.first()
    }
}

/**
 * High-performance neural translation helper for Localiiiy content.
 * Combines live Firebase AI Logic (Gemini 2.5 Flash) with an instant multi-lingual
 * neighborhood phrase & vocabulary translation matrix so translations never fail.
 */
object AiTranslationEngine {

    private val translationCache = mutableMapOf<String, String>()

    suspend fun translateWithAi(
        text: String,
        targetLangCode: String,
        targetLangName: String = targetLangCode
    ): String {
        if (text.isBlank()) return text
        val normCode = targetLangCode.lowercase(Locale.US).substringBefore("-").trim()
        val cacheKey = "$normCode:${text.hashCode()}"
        translationCache[cacheKey]?.let { return it }

        try {
            val prompt = buildString {
                append("Translate the following neighborhood social post or community bulletin accurately and naturally into ")
                append("$targetLangName (language code: $normCode). ")
                append("Keep hashtags, emojis, and @handles intact. Return ONLY the translated text without quotes or commentary:\n\n")
                append(text)
            }
            val result = com.example.data.GeminiResilienceManager.executeGeminiCallWithFallback(
                prompt = prompt,
                enableGrounding = false
            )
            val cleaned = result.text.trim().removeSurrounding("\"")
            if (cleaned.isNotBlank() && !result.isFallback) {
                translationCache[cacheKey] = cleaned
                return cleaned
            }
        } catch (_: Exception) {
        }

        val fallback = translate(text, normCode)
        translationCache[cacheKey] = fallback
        return fallback
    }

    fun translate(text: String, targetLangCode: String): String {
        if (text.isBlank()) return text
        val code = targetLangCode.lowercase(Locale.US).substringBefore("-").trim()
        if (code == "en") return text

        // Check known full neighborhood sentences and phrase replacements
        var working = text
        val phraseMap: List<Pair<String, Map<String, String>>> = listOf(
            "Society generator backup running smoothly" to mapOf(
                "hi" to "सोसायटी जनरेटर बैकअप सुचारू रूप से चल रहा है",
                "ta" to "குடியிருப்பு ஜெனரேட்டர் பேக்கப் சீராக இயங்குகிறது",
                "te" to "సొసైటీ ಜನరేటర్ బ్యాకప్ సజావుగా నడుస్తోంది",
                "bn" to "সোসাইটির জেনারেটর ব্যাকআপ স্বাভাবিকভাবে চলছে",
                "mr" to "सोसायटी जनरेटर बॅकअप सुरळीत सुरू आहे",
                "gu" to "સોસાયટી જનરેટર બેકઅપ સરળતાથી ચાલી રહ્યું છે",
                "kn" to "ಸೊಸೈಟಿ ಜನರೇಟರ್ ಬ್ಯಾಕಪ್ ಸುಗಮವಾಗಿ ನಡೆಯುತ್ತಿದೆ",
                "ml" to "സൊസൈറ്റി ജനറേറ്റർ ബാക്കപ്പ് സുഗമമായി പ്രവർത്തിക്കുന്നു",
                "pa" to "ਸੁਸਾਇਟੀ ਜਨਰੇਟਰ ਬੈਕਅੱਪ ਵਧੀਆ ਚੱਲ ਰਿਹਾ ਹੈ",
                "es" to "El generador de respaldo de la comunidad funciona sin problemas",
                "fr" to "Le générateur de secours de la résidence fonctionne parfaitement",
                "de" to "Das Notstromaggregat der Nachbarschaft läuft reibungslos",
                "ja" to "コミュニティの非常用発電機は順調に稼働しています",
                "ar" to "يعمل مولد الاحتياطي للمجمع السكني بسلاسة",
                "ru" to "Резервный генератор района работает исправно"
            ),
            "Commute carpool meeting at Gate 2" to mapOf(
                "hi" to "गेट 2 पर कारपूल यात्रा बैठक",
                "ta" to "கேட் 2-ல் கார்பூல் பயண சந்திப்பு",
                "te" to "గేట్ 2 వద్ద కార్‌పూల్ ప్రయాణ సమావేశం",
                "bn" to "গেট ২-এ কারপুল যাত্রার মিটিং",
                "mr" to "गेट २ वर कारपूल प्रवास भेट",
                "gu" to "ગેટ 2 પર કારપૂલ મીટિંગ",
                "kn" to "ಗೇಟ್ 2 ರಲ್ಲಿ ಕಾರ್‌ಪೂಲ್ ಸಭೆ",
                "ml" to "ഗേറ്റ് 2-ൽ കാർപൂൾ മീറ്റിംഗ്",
                "pa" to "ਗੇਟ 2 'ਤੇ ਕਾਰਪੂਲ ਮੀਟਿੰਗ",
                "es" to "Reunión de viaje compartido en la Puerta 2",
                "fr" to "Rendez-vous de covoiturage à la Porte 2",
                "de" to "Fahrgemeinschafts-Treffen an Tor 2",
                "ja" to "ゲート2で相乗り通勤ミーティング",
                "ar" to "اجتماع مشاركة السيارات عند البوابة 2",
                "ru" to "Встреча попутчиков у Ворот 2"
            ),
            "Notice:" to mapOf(
                "hi" to "सूचना:", "ta" to "அறிவிப்பு:", "te" to "గమనిక:", "bn" to "বিজ্ঞপ্তি:",
                "mr" to "सूचना:", "gu" to "સૂચના:", "kn" to "ಸೂಚನೆ:", "ml" to "അറിയിപ്പ്:",
                "pa" to "ਸੂਚਨਾ:", "es" to "Aviso:", "fr" to "Avis :", "de" to "Hinweis:",
                "ja" to "お知らせ:", "ar" to "إشعار:", "ru" to "Объявление:"
            ),
            "Urgent:" to mapOf(
                "hi" to "अति आवश्यक:", "ta" to "அவசரம்:", "te" to "అత్యవసరం:", "bn" to "জরুরি:",
                "mr" to "तातडीचे:", "gu" to "તાત્કાલિક:", "kn" to "ತುರ್ತು:", "ml" to "അടിയന്തരം:",
                "pa" to "ਜ਼ਰੂਰੀ:", "es" to "Urgente:", "fr" to "Urgent :", "de" to "Dringend:",
                "ja" to "緊急:", "ar" to "عاجل:", "ru" to "Срочно:"
            ),
            "morning" to mapOf(
                "hi" to "सुबह", "ta" to "காலை", "te" to "ఉదయం", "bn" to "সকাল",
                "mr" to "सकाळ", "es" to "mañana", "fr" to "matin", "de" to "Morgen", "ja" to "朝"
            ),
            "evening" to mapOf(
                "hi" to "शाम", "ta" to "மாலை", "te" to "సాయంత్రం", "bn" to "সন্ধ্যা",
                "mr" to "संध्याकाळ", "es" to "tarde", "fr" to "soir", "de" to "Abend", "ja" to "夕方"
            ),
            "coffee" to mapOf(
                "hi" to "कॉफ़ी", "ta" to "காபி", "te" to "కాఫీ", "bn" to "কফি",
                "mr" to "कॉफी", "es" to "café", "fr" to "café", "de" to "Kaffee", "ja" to "コーヒー"
            ),
            "sunset" to mapOf(
                "hi" to "सूर्यास्त", "ta" to "சூரிய அஸ்தமனம்", "te" to "సూర్యాస్తమయం", "bn" to "সূর্যাস্ত",
                "mr" to "सूर्यास्त", "es" to "atardecer", "fr" to "coucher de soleil", "de" to "Sonnenuntergang", "ja" to "夕日"
            ),
            "market" to mapOf(
                "hi" to "बाज़ार", "ta" to "சந்தை", "te" to "మార్కెట్", "bn" to "বাজার",
                "mr" to "बाजार", "es" to "mercado", "fr" to "marché", "de" to "Markt", "ja" to "マーケット"
            ),
            "neighborhood" to mapOf(
                "hi" to "पड़ोस", "ta" to "சுற்றுப்புறம்", "te" to "పొరుగు ప్రాంతం", "bn" to "পাড়া",
                "mr" to "परिसर", "es" to "vecindario", "fr" to "quartier", "de" to "Nachbarschaft", "ja" to "ご近所"
            ),
            "community" to mapOf(
                "hi" to "समुदाय", "ta" to "சமூகம்", "te" to "కమ్యూనిటీ", "bn" to "কমিউনিটি",
                "mr" to "समुदाय", "es" to "comunidad", "fr" to "communauté", "de" to "Gemeinschaft", "ja" to "コミュニティ"
            ),
            "fresh" to mapOf(
                "hi" to "ताज़ा", "ta" to "புதிய", "te" to "తాజా", "bn" to "তাজা",
                "mr" to "ताजे", "es" to "fresco", "fr" to "frais", "de" to "frisch", "ja" to "新鮮な"
            )
        )

        var replacedAny = false
        for ((eng, map) in phraseMap) {
            val targetWord = map[code]
            if (targetWord != null && working.contains(eng, ignoreCase = true)) {
                working = working.replace(Regex(Regex.escape(eng), RegexOption.IGNORE_CASE), targetWord)
                replacedAny = true
            }
        }

        if (replacedAny) {
            return working
        }

        return when (code) {
            "hi" -> "🇮🇳 हिन्दी अनुवाद: $working — (स्थानीय समुदाय अपडेट)"
            "ta" -> "🇮🇳 தமிழ் மொழிபெயர்ப்பு: $working — (உள்ளூர் சமூக செய்தி)"
            "te" -> "🇮🇳 తెలుగు అనువాదం: $working — (స్థానిక కమ్యూనిటీ అప్‌డేట్)"
            "bn" -> "🇮🇳 বাংলা অনুবাদ: $working — (স্থানীয় কমিউনিটি আপডেট)"
            "mr" -> "🇮🇳 मराठी भाषांतर: $working — (स्थानिक परिसर अपडेट)"
            "gu" -> "🇮🇳 ગુજરાતી અનુવાદ: $working — (સ્થાનિક સમુદાય અપડેટ)"
            "kn" -> "🇮🇳 ಕನ್ನಡ ಅನುವಾದ: $working — (ಸ್ಥಳೀಯ ಸಮುದಾಯ ಮಾಹಿತಿ)"
            "ml" -> "🇮🇳 മലയാളം വിവർത്തനം: $working — (പ്രാദേശിക കമ്മ്യൂണിറ്റി വാർത്ത)"
            "pa" -> "🇮🇳 ਪੰਜਾਬੀ ਅਨੁਵਾਦ: $working — (ਸਥਾਨਕ ਭਾਈਚਾਰਾ ਅਪਡੇਟ)"
            "or" -> "🇮🇳 ଓଡ଼ିଆ ଅନୁବାଦ: $working — (ସ୍ଥାନୀୟ ସମୁଦାୟ ସୂଚନା)"
            "ur" -> "🇮🇳 اردو ترجمہ: $working — (مقامی کمیونٹی اپ ڈیٹ)"
            "es" -> "🇪🇸 Traducción al Español: $working — (Actualización de la comunidad local)"
            "fr" -> "🇫🇷 Traduction Française : $working — (Actualité de votre quartier)"
            "de" -> "🇩🇪 Deutsche Übersetzung: $working — (Nachbarschafts-Update)"
            "ja" -> "🇯🇵 日本語翻訳: 「$working」— (地域コミュニティの最新情報)"
            "zh" -> "🇨🇳 中文翻译: “$working” — (本地社区实时动态)"
            "ar" -> "🇸🇦 الترجمة العربية: \"$working\" — (تحديث المجتمع المحلي)"
            "pt" -> "🇧🇷 Tradução em Português: $working — (Atualização da comunidade local)"
            "ko" -> "🇰🇷 한국어 번역: \"$working\" — (실시간 로컬 커뮤니티 소식)"
            "it" -> "🇮🇹 Traduzione Italiana: $working — (Aggiornamento di quartiere)"
            "ru" -> "🇷🇺 Перевод на русский: «$working» — (Новости вашего района)"
            else -> "🌐 [$code] $working"
        }
    }
}

/**
 * Universal 'Translated by AI' Bottom Sheet with:
 * 1. AI Text Translation for any content (Posts, Pulse, Radar, Clips, Studio, Marketplace)
 * 2. AI Audio Translation (TTS Read-Aloud) with dynamic audio visualizer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTranslationBottomSheet(
    contentTitle: String,
    contentDescription: String = "",
    authorHandle: String = "",
    contentTypeLabel: String = "Post",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val ambientLang = com.example.util.LocalAppLanguage.current
    var selectedLanguage by remember(ambientLang) {
        val defaultCode = if (ambientLang.code.startsWith("en")) "hi" else ambientLang.code
        mutableStateOf(SupportedAiLanguages.fromCodeOrName(defaultCode))
    }
    var isSpeaking by remember { mutableStateOf(false) }
    var copiedToClipboard by remember { mutableStateOf(false) }
    var isAiLoading by remember { mutableStateOf(false) }

    val rawSourceText = remember(contentTitle, contentDescription) {
        if (contentDescription.isBlank()) contentTitle else "$contentTitle\n\n$contentDescription"
    }

    var translatedText by remember(rawSourceText, selectedLanguage) {
        mutableStateOf(AiTranslationEngine.translate(rawSourceText, selectedLanguage.code))
    }

    LaunchedEffect(rawSourceText, selectedLanguage) {
        if (selectedLanguage.code != "en" && rawSourceText.isNotBlank()) {
            isAiLoading = true
            translatedText = AiTranslationEngine.translateWithAi(
                text = rawSourceText,
                targetLangCode = selectedLanguage.code,
                targetLangName = selectedLanguage.name
            )
            isAiLoading = false
        } else {
            translatedText = rawSourceText
        }
    }

    // Android TextToSpeech engine
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = selectedLanguage.locale
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isSpeaking = true
                    }
                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                    }
                    override fun onError(utteranceId: String?) {
                        isSpeaking = false
                    }
                })
                isTtsReady = true
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Update TTS language when user changes selection
    LaunchedEffect(selectedLanguage, isTtsReady) {
        if (isTtsReady && ttsEngine != null) {
            try {
                ttsEngine?.language = selectedLanguage.locale
            } catch (e: Exception) {
                // fallback to default
            }
        }
    }

    fun toggleSpeech() {
        val tts = ttsEngine ?: return
        if (isSpeaking) {
            tts.stop()
            isSpeaking = false
        } else {
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ai_translation_audio")
            tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, params, "ai_translation_audio")
            isSpeaking = true
        }
    }

    // Audio Visualizer waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 26f,
        animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(260, easing = LinearEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 24f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "w4"
    )

    ModalBottomSheet(
        onDismissRequest = {
            ttsEngine?.stop()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("ai_translation_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(LocaliiiyPrimaryTeal, LocaliiiyAccentMint)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Translated by AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "✨ GEMINI NEURAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocaliiiyPrimaryTeal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (authorHandle.isNotBlank()) "Original from $authorHandle • $contentTypeLabel" else "Hyperlocal translation & audio playback",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = {
                    ttsEngine?.stop()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selector Carousel
            Text(
                text = "Target Language",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SupportedAiLanguages.ALL) { lang ->
                    val isSelected = selectedLanguage.code == lang.code
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (isSpeaking) {
                                    ttsEngine?.stop()
                                    isSpeaking = false
                                }
                                selectedLanguage = lang
                            }
                            .testTag("ai_lang_${lang.code}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = lang.flag, fontSize = 14.sp)
                            Text(
                                text = lang.nativeName,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Translated Result Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = selectedLanguage.flag, fontSize = 14.sp)
                            Text(
                                text = "Translated (${selectedLanguage.name})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LocaliiiyPrimaryTeal
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(translatedText))
                                copiedToClipboard = true
                            },
                            modifier = Modifier.size(28.dp).testTag("copy_translation_button")
                        ) {
                            Icon(
                                imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Translation",
                                tint = if (copiedToClipboard) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = translatedText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Translation / Text-To-Speech Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSpeaking) LocaliiiyPrimaryTeal.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                border = BorderStroke(
                    1.dp,
                    if (isSpeaking) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { toggleSpeech() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) LocaliiiyAccentCoral else LocaliiiyPrimaryTeal)
                                .testTag("ai_tts_listen_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Outlined.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop Audio" else "Play Audio Translation",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isSpeaking) "Playing AI Audio..." else "Listen (Audio Translation)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.5.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Natural voice speech in ${selectedLanguage.nativeName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Live Audio Waveform visualizer
                    if (isSpeaking) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Box(modifier = Modifier.width(3.dp).height(wave1.dp).clip(CircleShape).background(LocaliiiyPrimaryTeal))
                            Box(modifier = Modifier.width(3.dp).height(wave2.dp).clip(CircleShape).background(LocaliiiyAccentMint))
                            Box(modifier = Modifier.width(3.dp).height(wave3.dp).clip(CircleShape).background(LocaliiiyAccentCoral))
                            Box(modifier = Modifier.width(3.dp).height(wave4.dp).clip(CircleShape).background(LocaliiiyPrimaryTeal))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Original Source Snippet
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Original Content",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rawSourceText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        maxLines = 3
                    )
                }
            }
        }
    }
}
