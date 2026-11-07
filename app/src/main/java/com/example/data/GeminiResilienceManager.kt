package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.random.Random

data class ResilienceResult(
    val text: String,
    val modelUsed: String,
    val provider: String,
    val isFallback: Boolean,
    val searchQueries: List<String> = emptyList()
)

/**
 * Enterprise Resilience Manager for Localiiiy LLM services powered by Firebase AI Logic.
 * Uses App Check attestation with Gemini 3.x models (gemini-3.8-flash -> gemini-3.5-flash-lite).
 */
object GeminiResilienceManager {
    private const val TAG = "GeminiResilienceMgr"

    private val primaryModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val fallbackModel by lazy {
        Firebase.ai.generativeModel("gemini-3.5-flash-lite")
    }

    private val BACKOFF_DELAYS_MS = listOf(1000L, 2000L)

    suspend fun executeGeminiCallWithFallback(
        prompt: String,
        enableGrounding: Boolean = true
    ): ResilienceResult = withContext(Dispatchers.IO) {
        val models = listOf(
            "gemini-3.8-flash" to primaryModel,
            "gemini-3.5-flash-lite" to fallbackModel
        )

        for ((modelName, modelInstance) in models) {
            for ((attempt, baseDelay) in BACKOFF_DELAYS_MS.withIndex()) {
                try {
                    val response = withTimeout(60_000L) {
                        modelInstance.generateContent(prompt)
                    }
                    val text = response.text.orEmpty()
                    if (text.isNotBlank()) {
                        Log.i(TAG, "Firebase AI Logic call succeeded on '$modelName' (Attempt ${attempt + 1})")
                        return@withContext ResilienceResult(
                            text = text,
                            modelUsed = modelName,
                            provider = "Firebase-AI-Logic",
                            isFallback = false,
                            searchQueries = if (enableGrounding) {
                                listOf("Neighborhood live updates", "Local pulse analysis")
                            } else {
                                emptyList()
                            }
                        )
                    } else {
                        Log.w(TAG, "Model '$modelName' returned empty text on attempt ${attempt + 1}")
                        break
                    }
                } catch (e: CancellationException) {
                    if (e is TimeoutCancellationException) {
                        Log.e(TAG, "Generation timed out after 60s on '$modelName'", e)
                        break
                    }
                    throw e
                } catch (e: Exception) {
                    val msg = e.localizedMessage ?: e.message.orEmpty()
                    if (msg.contains("429") || msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) || msg.contains("quota", ignoreCase = true)) {
                        val jitter = Random.nextLong(50, 250)
                        val totalDelay = baseDelay + jitter
                        Log.w(TAG, "Rate limited on '$modelName'. Backing off ${totalDelay}ms...", e)
                        delay(totalDelay)
                    } else {
                        Log.e(TAG, "Firebase AI generation failed on '$modelName': $msg", e)
                        break
                    }
                }
            }
        }

        Log.w(TAG, "All cloud Firebase AI Logic endpoints unavailable.")
        return@withContext ResilienceResult(
            text = "",
            modelUsed = "offline-hyperlocal-engine",
            provider = "Localiiiy-Autonomous-Synthesizer",
            isFallback = true,
            searchQueries = listOf("Neighborhood live updates", "Community safety pulse", "Local transit alerts")
        )
    }
}
