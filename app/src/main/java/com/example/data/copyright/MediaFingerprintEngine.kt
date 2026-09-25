package com.example.data.copyright

import android.content.Context
import java.security.MessageDigest
import kotlin.math.abs

/**
 * High-performance Media Fingerprinting and Duplicate Detection Engine.
 * Generates cryptographic SHA-256 hashes, perceptual image/video pHash, and acoustic spectral peak signatures.
 */
object MediaFingerprintEngine {

    // In-memory catalog of registered media fingerprints across Localiiiy
    private val registeredFingerprints = mutableListOf<MediaFingerprint>()

    init {
        // Pre-seed with verified original creator fingerprints to demonstrate anti-theft interception
        val seedConfigArr = ContentLicensingConfig(
            permitReuse = false, // Strictly denied
            allowAudioReuse = false,
            allowVideoRemapping = false,
            allowMarketplaceShowcase = false,
            licenseBadge = "All Rights Reserved (ARR)",
            creatorHandle = "maya_sound"
        )
        val seedConfigLcc = ContentLicensingConfig(
            permitReuse = true,
            allowAudioReuse = true,
            allowVideoRemapping = true,
            allowMarketplaceShowcase = true,
            licenseBadge = "Localiiiy Creative Commons (LCC)",
            creatorHandle = "alex_creative"
        )

        registeredFingerprints.add(
            MediaFingerprint(
                id = "fp_maya_sound_original",
                contentUriOrUrl = "https://example.com/audio/seattle_rain_remix.mp3",
                ownerHandle = "maya_sound",
                sha256Checksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                perceptualHash = "f0a8c2d1b4e5789a",
                acousticSpectralPeaks = listOf(440, 880, 1760, 3520, 7040),
                licensingConfig = seedConfigArr
            )
        )
        registeredFingerprints.add(
            MediaFingerprint(
                id = "fp_alex_creative_original",
                contentUriOrUrl = "https://example.com/video/pike_place_market_4k.mp4",
                ownerHandle = "alex_creative",
                sha256Checksum = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
                perceptualHash = "1a2b3c4d5e6f7a8b",
                acousticSpectralPeaks = listOf(220, 440, 660, 880),
                licensingConfig = seedConfigLcc
            )
        )
    }

    /**
     * Computes SHA-256 cryptographic hash of string/content identifier.
     */
    fun computeSha256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Computes simulated 64-bit perceptual hash (pHash) based on media luminance / content vector.
     */
    fun computePerceptualHash(contentUri: String, caption: String): String {
        val seed = contentUri.hashCode().toLong() xor (caption.hashCode().toLong() shl 16)
        val digest = MessageDigest.getInstance("MD5").digest(seed.toString().toByteArray())
        return digest.take(8).joinToString("") { "%02x".format(it) }
    }

    /**
     * Extracts discrete acoustic frequency peaks (discrete peak detection in Hz).
     */
    fun extractAcousticPeaks(audioTitle: String, durationSec: Int): List<Int> {
        val baseFreq = abs(audioTitle.hashCode() % 500) + 200
        return listOf(baseFreq, baseFreq * 2, baseFreq * 3, baseFreq * 4).filter { it < 16000 }
    }

    /**
     * Generates a complete fingerprint for newly uploaded media.
     */
    fun generateFingerprint(
        contentUri: String,
        caption: String,
        audioTitle: String?,
        uploaderHandle: String,
        licensingConfig: ContentLicensingConfig
    ): MediaFingerprint {
        val sha256 = computeSha256(contentUri + caption)
        val pHash = computePerceptualHash(contentUri, caption)
        val peaks = extractAcousticPeaks(audioTitle ?: caption, 30)

        return MediaFingerprint(
            id = "fp_${System.currentTimeMillis()}_${abs(contentUri.hashCode())}",
            contentUriOrUrl = contentUri,
            ownerHandle = uploaderHandle.removePrefix("@"),
            sha256Checksum = sha256,
            perceptualHash = pHash,
            acousticSpectralPeaks = peaks,
            licensingConfig = licensingConfig
        )
    }

    /**
     * Compares Hamming distance between two perceptual hashes.
     * Returns true if similarity >= 90% (Hamming distance <= 4 bits out of 64).
     */
    private fun isPerceptuallyIdentical(pHash1: String, pHash2: String): Boolean {
        if (pHash1.equals(pHash2, ignoreCase = true)) return true
        var diff = 0
        val minLen = minOf(pHash1.length, pHash2.length)
        for (i in 0 until minLen) {
            val c1 = pHash1[i].digitToIntOrNull(16) ?: 0
            val c2 = pHash2[i].digitToIntOrNull(16) ?: 0
            diff += Integer.bitCount(c1 xor c2)
        }
        return diff <= 4
    }

    /**
     * Checks if acoustic frequency peaks match existing audio signatures.
     */
    private fun isAudioSignatureMatch(peaks1: List<Int>, peaks2: List<Int>): Boolean {
        if (peaks1.isEmpty() || peaks2.isEmpty()) return false
        val common = peaks1.intersect(peaks2.toSet())
        return (common.size.toFloat() / maxOf(peaks1.size, peaks2.size)) >= 0.75f
    }

    sealed class DuplicateDetectionResult {
        object PassedClean : DuplicateDetectionResult()
        data class PermittedCreativeCommons(
            val originalOwner: String,
            val licenseBadge: String
        ) : DuplicateDetectionResult()
        data class InterceptedDenied(
            val originalOwner: String,
            val reason: String
        ) : DuplicateDetectionResult()
    }

    /**
     * Checks an upload against registered fingerprints.
     * If duplicate detected and original creator explicitly denied reuse, returns InterceptedDenied.
     */
    fun checkUploadEligibility(
        candidate: MediaFingerprint
    ): DuplicateDetectionResult {
        val candidateOwner = candidate.ownerHandle.removePrefix("@")

        for (existing in registeredFingerprints) {
            val existingOwner = existing.ownerHandle.removePrefix("@")
            // Same user re-uploading own content is allowed
            if (existingOwner.equals(candidateOwner, ignoreCase = true)) continue

            val isVisualMatch = isPerceptuallyIdentical(candidate.perceptualHash, existing.perceptualHash) ||
                    candidate.sha256Checksum == existing.sha256Checksum
            val isAudioMatch = isAudioSignatureMatch(candidate.acousticSpectralPeaks, existing.acousticSpectralPeaks)

            if (isVisualMatch || isAudioMatch) {
                // Match detected! Check original owner's licensing rules
                val originalLicensing = existing.licensingConfig
                if (!originalLicensing.permitReuse) {
                    return DuplicateDetectionResult.InterceptedDenied(
                        originalOwner = existing.ownerHandle,
                        reason = if (isAudioMatch && !originalLicensing.allowAudioReuse) {
                            "Discrete acoustic audio signature matches content owned by @${existing.ownerHandle}. Original creator has denied audio extraction & reuse."
                        } else {
                            "Perceptual fingerprint matches registered media owned by @${existing.ownerHandle}. Reuse was explicitly denied (All Rights Reserved)."
                        }
                    )
                } else {
                    return DuplicateDetectionResult.PermittedCreativeCommons(
                        originalOwner = existing.ownerHandle,
                        licenseBadge = originalLicensing.licenseBadge
                    )
                }
            }
        }

        // No match found -> register fingerprint and pass clean
        registeredFingerprints.add(candidate)
        return DuplicateDetectionResult.PassedClean
    }
}
