package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.round

object MediaSanitizationHelper {

    /**
     * Strips all EXIF metadata (camera serial, hardware identifiers, raw GPS tags)
     * by decoding the pixel array directly and re-compressing cleanly.
     */
    fun sanitizeAndCompressImage(context: Context, uri: Uri, quality: Int = 85): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.toByteArray()
            } else {
                null
            }
        } catch (e: Exception) {
            null
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
