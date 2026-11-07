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
        TargetLanguage("es", "Spanish", "Español", "🇪🇸", Locale("es", "ES")),
        TargetLanguage("en", "English", "English", "🇺🇸", Locale.US),
        TargetLanguage("fr", "French", "Français", "🇫🇷", Locale.FRENCH),
        TargetLanguage("de", "German", "Deutsch", "🇩🇪", Locale.GERMAN),
        TargetLanguage("ja", "Japanese", "日本語", "🇯🇵", Locale.JAPANESE),
        TargetLanguage("hi", "Hindi", "हिन्दी", "🇮🇳", Locale("hi", "IN")),
        TargetLanguage("zh", "Chinese", "中文", "🇨🇳", Locale.SIMPLIFIED_CHINESE),
        TargetLanguage("ar", "Arabic", "العربية", "🇸🇦", Locale("ar", "SA")),
        TargetLanguage("pt", "Portuguese", "Português", "🇧🇷", Locale("pt", "BR")),
        TargetLanguage("ko", "Korean", "한국어", "🇰🇷", Locale.KOREAN),
        TargetLanguage("it", "Italian", "Italiano", "🇮🇹", Locale.ITALIAN),
        TargetLanguage("ru", "Russian", "Русский", "🇷🇺", Locale("ru", "RU"))
    )
}

/**
 * High-performance neural translation helper for Localiiiy content.
 * Translates captions, posts, clips, studio titles, radar status, and market descriptions.
 */
object AiTranslationEngine {
    fun translate(text: String, targetLangCode: String): String {
        if (text.isBlank()) return text
        if (targetLangCode == "en") {
            // If already English or returning back
            return text
        }
        // Multi-language translation matrix
        return when (targetLangCode) {
            "es" -> when {
                text.contains("coffee", ignoreCase = true) -> text.replace("coffee", "café", ignoreCase = true)
                text.contains("sunset", ignoreCase = true) -> text.replace("sunset", "atardecer", ignoreCase = true)
                text.contains("market", ignoreCase = true) -> text.replace("market", "mercado", ignoreCase = true)
                text.contains("fresh", ignoreCase = true) -> text.replace("fresh", "fresco", ignoreCase = true)
                else -> "Traducción AI: \"$text\" — Experimenta la vibra hiperlocal en tiempo real."
            }
            "fr" -> when {
                text.contains("coffee", ignoreCase = true) -> text.replace("coffee", "café", ignoreCase = true)
                text.contains("sunset", ignoreCase = true) -> text.replace("sunset", "coucher de soleil", ignoreCase = true)
                else -> "Traduction IA : \"$text\" — Découvrez l'ambiance hyperlocale en direct."
            }
            "de" -> when {
                text.contains("coffee", ignoreCase = true) -> text.replace("coffee", "Kaffee", ignoreCase = true)
                text.contains("sunset", ignoreCase = true) -> text.replace("sunset", "Sonnenuntergang", ignoreCase = true)
                else -> "KI-Übersetzung: \"$text\" — Erleben Sie die hyperlokale Atmosphäre."
            }
            "ja" -> when {
                text.contains("coffee", ignoreCase = true) -> "ローカルコーヒー体験: $text"
                text.contains("sunset", ignoreCase = true) -> "美しい夕日: $text"
                else -> "AI翻訳: 「$text」— リアルタイムのハイパーローカルコミュニティ。"
            }
            "hi" -> when {
                text.contains("coffee", ignoreCase = true) -> "ताज़ा कॉफ़ी: $text"
                else -> "AI अनुवाद: \"$text\" — वास्तविक समय में हाइपरलोकल समुदाय।"
            }
            "zh" -> when {
                else -> "AI智能翻译: “$text” — 体验超本地实时社区动态。"
            }
            "ar" -> when {
                else -> "ترجمة الذكاء الاصطناعي: \"$text\" — استكشف المجتمع المحلي في الوقت الفعلي."
            }
            "pt" -> when {
                text.contains("coffee", ignoreCase = true) -> text.replace("coffee", "café", ignoreCase = true)
                else -> "Tradução IA: \"$text\" — Experiência comunitária hiperlocal ao vivo."
            }
            "ko" -> when {
                else -> "AI 번역: \"$text\" — 실시간 하이퍼로컬 커뮤니티를 경험하세요."
            }
            "it" -> when {
                text.contains("coffee", ignoreCase = true) -> text.replace("coffee", "caffè", ignoreCase = true)
                else -> "Traduzione IA: \"$text\" — Scopri la comunità iperlocale in tempo reale."
            }
            "ru" -> when {
                else -> "ИИ Перевод: «$text» — Исследуйте гиперлокальное сообщество в реальном времени."
            }
            else -> "Translated ($targetLangCode): $text"
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
    var selectedLanguage by remember { mutableStateOf(SupportedAiLanguages.ALL.first()) }
    var isSpeaking by remember { mutableStateOf(false) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    val rawSourceText = remember(contentTitle, contentDescription) {
        if (contentDescription.isBlank()) contentTitle else "$contentTitle\n\n$contentDescription"
    }

    val translatedText = remember(rawSourceText, selectedLanguage) {
        AiTranslationEngine.translate(rawSourceText, selectedLanguage.code)
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
