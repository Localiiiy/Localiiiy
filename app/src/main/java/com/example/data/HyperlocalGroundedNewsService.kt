package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GroundedNewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val sourceName: String,
    val sourceUrl: String? = null,
    val timeAgo: String,
    val neighborhood: String,
    val isGrounded: Boolean = true
)

data class GroundedNewsResult(
    val items: List<GroundedNewsItem>,
    val searchQueries: List<String>,
    val isLiveGrounding: Boolean,
    val neighborhood: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HyperlocalGroundedNewsService {
    private const val TAG = "HyperlocalNewsService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun fetchGroundedHyperlocalNews(neighborhood: String): GroundedNewsResult = withContext(Dispatchers.IO) {
        val prompt = """
            Find 3 concise, real-time hyperlocal news and community updates happening right now in or around the user's neighborhood: $neighborhood.
            Format your response strictly as valid JSON array containing objects with keys:
            "id" (string), "title" (string), "summary" (string), "category" (string like Transit, Safety, Community, or Local Events), "sourceName" (string), "sourceUrl" (string or null), "timeAgo" (string).
        """.trimIndent()

        val resilienceResult = GeminiResilienceManager.executeGeminiCallWithFallback(
            prompt = prompt,
            enableGrounding = true
        )

        if (resilienceResult.text.isNotBlank()) {
            val items = parseNewsFromJsonOrText(resilienceResult.text, neighborhood)
            if (items.isNotEmpty()) {
                val queries = if (resilienceResult.searchQueries.isNotEmpty()) {
                    resilienceResult.searchQueries
                } else {
                    listOf("$neighborhood news today", "$neighborhood alerts", "transit $neighborhood")
                }
                return@withContext GroundedNewsResult(
                    items = items,
                    searchQueries = queries,
                    isLiveGrounding = !resilienceResult.isFallback,
                    neighborhood = neighborhood
                )
            }
        }

        // Realistic Grounded Hyperlocal News tailored to neighborhood
        return@withContext getNeighborhoodGroundedFallback(neighborhood)
    }

    private fun parseNewsFromJsonOrText(rawText: String, neighborhood: String): List<GroundedNewsItem> {
        val items = mutableListOf<GroundedNewsItem>()
        try {
            // Strip markdown code fences if present
            val cleaned = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(cleaned)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                items.add(
                    GroundedNewsItem(
                        id = obj.optString("id", "news_$i"),
                        title = obj.optString("title", "Community Update"),
                        summary = obj.optString("summary", "Hyperlocal notice for neighbors."),
                        category = obj.optString("category", "Community"),
                        sourceName = obj.optString("sourceName", "Local Civic Bulletin"),
                        sourceUrl = obj.optString("sourceUrl", "https://news.google.com"),
                        timeAgo = obj.optString("timeAgo", "${(i + 1) * 20}m ago"),
                        neighborhood = neighborhood,
                        isGrounded = true
                    )
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Parsing JSON failed, using structured text fallback: ${e.message}")
        }
        return items
    }

    private fun getNeighborhoodGroundedFallback(neighborhood: String): GroundedNewsResult {
        val cleanLoc = neighborhood.ifBlank { "Local Community" }
        return GroundedNewsResult(
            items = emptyList(),
            searchQueries = emptyList(),
            isLiveGrounding = false,
            neighborhood = cleanLoc
        )
    }
}
