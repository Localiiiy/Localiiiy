package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object GeminiImageService {
    private const val TAG = "GeminiImageService"
    private const val MODEL_NAME = "gemini-nano-banana-2.1"

    private val imageModel by lazy {
        val config = generationConfig {
            responseModalities = listOf(ResponseModality.IMAGE)
        }
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = config
        )
    }

    private fun uriToBitmap(context: Context, uriString: String): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode URI to Bitmap: ${e.message}", e)
            null
        }
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): String? {
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, "ai_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, fos)
                fos.flush()
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save Bitmap to cache file: ${e.message}", e)
            null
        }
    }

    /**
     * Generate or edit an image using Firebase AI Logic (`gemini-nano-banana-2.1`).
     */
    suspend fun generateOrEditImage(
        context: Context,
        prompt: String,
        baseImageUri: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val app = try {
                com.google.firebase.FirebaseApp.getInstance()
            } catch (_: Exception) {
                null
            }
            val apiKey = app?.options?.apiKey.orEmpty()
            if (app == null || apiKey.isBlank() || apiKey.contains("PLACEHOLDER", ignoreCase = true) || apiKey.startsWith("YOUR_", ignoreCase = true)) {
                return@withContext Result.failure(Exception("Firebase AI Logic requires an active Firebase configuration."))
            }

            val inputBitmap = if (!baseImageUri.isNullOrBlank()) {
                uriToBitmap(context, baseImageUri)
            } else {
                null
            }

            val response = withTimeout(60_000L) {
                if (inputBitmap != null) {
                    imageModel.generateContent(
                        content {
                            image(inputBitmap)
                            text(prompt)
                        }
                    )
                } else {
                    imageModel.generateContent(prompt)
                }
            }

            val generatedBitmap = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()
                ?.image

            if (generatedBitmap != null) {
                val savedUri = saveBitmapToCache(context, generatedBitmap)
                if (savedUri != null) {
                    Result.success(savedUri)
                } else {
                    Result.failure(Exception("Failed to save generated image to cache."))
                }
            } else {
                Result.failure(Exception("No image was returned by the model."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Image generation timed out after 60 seconds", e)
            Result.failure(Exception("Image generation timed out after 60 seconds."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI image operation failed", e)
            Result.failure(e)
        }
    }
}
