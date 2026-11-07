package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.round

data class VideoMetadata(
    val durationSeconds: Int,
    val width: Int,
    val height: Int,
    val isVertical: Boolean,
    val resolutionLabel: String
)

object MediaSanitizationHelper {

    private const val TAG = "MediaSanitization"

    /**
     * Strips all EXIF metadata (camera serial, hardware identifiers, raw GPS tags)
     * by downscaling with inSampleSize to prevent OutOfMemoryError, and re-compressing cleanly.
     */
    fun sanitizeAndCompressImage(
        context: Context,
        uri: Uri,
        quality: Int = 82,
        maxDimension: Int = 1440
    ): ByteArray? {
        var bitmap: Bitmap? = null
        return try {
            // Step 1: Decode bounds only to prevent OutOfMemoryError
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            val origWidth = options.outWidth
            val origHeight = options.outHeight
            if (origWidth <= 0 || origHeight <= 0) return null

            // Step 2: Compute power-of-2 inSampleSize
            var inSampleSize = 1
            val maxOriginal = max(origWidth, origHeight)
            while ((maxOriginal / inSampleSize) > maxDimension) {
                inSampleSize *= 2
            }

            // Step 3: Decode scaled bitmap
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.RGB_565 // Conserves 50% RAM compared to ARGB_8888
            }

            context.contentResolver.openInputStream(uri)?.use { stream ->
                bitmap = BitmapFactory.decodeStream(stream, null, decodeOptions)
            }

            val currentBitmap = bitmap ?: return null

            // Step 4: Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            byteArray
        } catch (oom: OutOfMemoryError) {
            Log.e(TAG, "Out of memory during media compression: ${oom.message}")
            System.gc()
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sanitize image: ${e.message}")
            null
        } finally {
            try {
                bitmap?.recycle()
            } catch (_: Throwable) {}
        }
    }

    /**
     * Compresses the selected image and writes to app temporary cache file.
     * Prevents large memory bloat during multi-photo uploads (e.g. 7+ marketplace photos).
     */
    fun compressAndSaveToCache(context: Context, uri: Uri): String? {
        return try {
            val bytes = sanitizeAndCompressImage(context, uri) ?: return null
            val cacheFile = File(context.cacheDir, "upload_cached_${System.currentTimeMillis()}_${(100..999).random()}.jpg")
            FileOutputStream(cacheFile).use { fos ->
                fos.write(bytes)
                fos.flush()
            }
            cacheFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error writing compressed media to cache: ${e.message}")
            null
        }
    }

    /**
     * Safely inspects video file metadata (duration, width, height, aspect ratio)
     * without throwing runtime verification crashes.
     */
    fun detectVideoMetadata(context: Context, uri: Uri): VideoMetadata {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 60000L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 1080
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 1920
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0

            val effectiveWidth = if (rotation == 90 || rotation == 270) height else width
            val effectiveHeight = if (rotation == 90 || rotation == 270) width else height

            val durationSeconds = (durationMs / 1000).toInt().coerceAtLeast(1)
            val isVertical = effectiveHeight > effectiveWidth
            val resolutionLabel = when {
                max(effectiveWidth, effectiveHeight) >= 3840 -> "4K Ultra HD"
                max(effectiveWidth, effectiveHeight) >= 1920 -> "1080p Full HD"
                max(effectiveWidth, effectiveHeight) >= 1280 -> "720p HD"
                else -> "Standard Definition"
            }
            VideoMetadata(
                durationSeconds = durationSeconds,
                width = effectiveWidth,
                height = effectiveHeight,
                isVertical = isVertical,
                resolutionLabel = resolutionLabel
            )
        } catch (e: Exception) {
            Log.w(TAG, "Video metadata inspection fallback used: ${e.message}")
            VideoMetadata(
                durationSeconds = 60,
                width = 1080,
                height = 1920,
                isVertical = true,
                resolutionLabel = "1080p Full HD"
            )
        } finally {
            try {
                retriever.release()
            } catch (_: Throwable) {}
        }
    }

    /**
     * Fuzzes exact GPS coordinates to ~100m to 1km precision
     * to protect user residential privacy unless in Ghost Mode.
     */
    fun fuzzCoordinates(lat: Double, lng: Double, precisionDecimals: Int = 3): Pair<Double, Double> {
        val factor = Math.pow(10.0, precisionDecimals.toDouble())
        val fuzzedLat = round(lat * factor) / factor
        val fuzzedLng = round(lng * factor) / factor
        return Pair(fuzzedLat, fuzzedLng)
    }
}
