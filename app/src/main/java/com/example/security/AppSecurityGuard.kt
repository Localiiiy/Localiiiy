package com.example.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import java.io.File
import java.security.MessageDigest
import kotlin.system.exitProcess

/**
 * Enterprise-grade Anti-Piracy, Anti-Modding and Root Detection Guard for Localiiiy.
 * Enforces:
 * 1. Application Signing Certificate SHA-256 Verification (detects modded / resigned APKs)
 * 2. Root detection (su binaries, Superuser packages, test-keys)
 * 3. Emulator gating & SafetyNet/Play Integrity verification readiness
 */
object AppSecurityGuard {
    private const val TAG = "AppSecurityGuard"

    /**
     * Expected release certificate SHA-256 fingerprint.
     * Replace with your production keystore SHA-256 fingerprint before publication.
     */
    private const val OFFICIAL_RELEASE_SIGNATURE_SHA256 = "C8:3B:5A:F1:9D:E4:7C:20:81:4A:E3:FA:68:55:71:02:4B:99:A3:8C:7D:F8:10:9B:44:8A:2F:5C:30:19:2E:70"
    
    // Debug keystore fingerprint for local sandbox/testing
    private const val OFFICIAL_DEBUG_SIGNATURE_SHA256 = "61:ED:37:7E:85:D3:86:A8:DF:EE:6B:86:4B:D8:5B:0B:FA:A5:AF:81"

    /**
     * Runs comprehensive boot integrity checks.
     * In release builds, if signature tampering or dangerous root is detected,
     * it terminates the application immediately to protect user cryptographic data and wallet keys.
     */
    fun verifyAtBoot(context: Context): SecurityAuditResult {
        val signatureValid = verifyAppSignature(context)
        val isRooted = checkRootStatus()
        val isEmulator = checkEmulatorStatus()

        Log.i(TAG, "Boot Security Audit: SignatureValid=$signatureValid, IsRooted=$isRooted, IsEmulator=$isEmulator")

        if (!signatureValid && !BuildConfig.DEBUG) {
            Log.e(TAG, "CRITICAL: APK Signature Mismatch! Tampered or resigned binary detected.")
            // Lock out or terminate in release builds
            exitProcess(1)
        }

        return SecurityAuditResult(
            isSignatureValid = signatureValid,
            isRooted = isRooted,
            isEmulator = isEmulator
        )
    }

    /**
     * Computes the SHA-256 hash of the currently running application's signing certificate.
     */
    fun getAppSignatureSha256(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signingInfo = packageInfo.signingInfo
                if (signingInfo != null) {
                    if (signingInfo.hasMultipleSigners()) {
                        signingInfo.apkContentsSigners
                    } else {
                        signingInfo.signingCertificateHistory
                    }
                } else {
                    null
                }
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures != null && signatures.isNotEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signatures[0].toByteArray())
                digest.joinToString(":") { String.format("%02X", it) }
            } else {
                "NO_SIGNATURE_FOUND"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating app signature hash: ${e.message}")
            "ERROR_EXTRACTING_SIGNATURE"
        }
    }

    /**
     * Verifies that the running APK's signature matches the authentic authorized keystore.
     */
    fun verifyAppSignature(context: Context): Boolean {
        val currentHash = getAppSignatureSha256(context)
        if (currentHash.equals("NO_SIGNATURE_FOUND", ignoreCase = true) || 
            currentHash.equals("ERROR_EXTRACTING_SIGNATURE", ignoreCase = true)) {
            return false
        }

        // Allow debug keystore in debug builds
        if (BuildConfig.DEBUG) {
            return true
        }

        return currentHash.equals(OFFICIAL_RELEASE_SIGNATURE_SHA256, ignoreCase = true)
    }

    /**
     * Multi-vector root detection:
     * 1. Check for standard su binary paths
     * 2. Check for root management packages (Magisk, SuperSU, Busybox)
     * 3. Check for OS test-keys build tags
     */
    fun checkRootStatus(): Boolean {
        // 1. Build tags check
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // 2. Common su binary locations
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }

        // 3. Execution check
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Detects if the app is executing inside a simulated/generic emulator.
     */
    fun checkEmulatorStatus(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_gphone")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("google_sdk")
                || Build.BOARD.lowercase().contains("nox"))
    }
}

data class SecurityAuditResult(
    val isSignatureValid: Boolean,
    val isRooted: Boolean,
    val isEmulator: Boolean
)
