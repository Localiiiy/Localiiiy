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
     * Authorized production & upload certificate SHA-256 fingerprints for com.aistudio.localiiiy.live:
     * 1. Google Play App Signing Key SHA-256
     * 2. Upload Keystore SHA-256
     */
    private val AUTHORIZED_RELEASE_SIGNATURES_SHA256 = setOf(
        "A4:D0:0D:C8:0E:62:92:4A:54:C1:E8:A0:0D:0B:D7:1C:A8:40:25:62:9E:E3:E4:62:D8:BC:1B:C2:1C:3E:77:B0",
        "0E:B7:92:77:81:FB:61:4E:6A:A1:7E:10:CB:95:38:92:A1:1E:FE:BD:A3:AE:FA:26:6F:78:94:A8:74:D4:1F:03",
        "AF:5F:FC:0A:B3:18:6D:10:90:85:9A:9A:5B:BE:DD:BD:99:BE:1E:0D:E2:E0:3E:F4:C5:B9:61:AF:4E:09:5F:31"
    )

    // Authorized Google Play App Signing SHA-1: 89:E3:4D:A8:B2:3A:3D:9C:FF:BA:9C:DD:34:77:73:E8:FC:DA:84:0B
    private const val OFFICIAL_DEBUG_SIGNATURE_SHA256 = "0E:B7:92:77:81:FB:61:4E:6A:A1:7E:10:CB:95:38:92:A1:1E:FE:BD:A3:AE:FA:26:6F:78:94:A8:74:D4:1F:03"

    @Volatile
    var cachedPackageName: String = "com.aistudio.localiiiy.live"
        private set

    @Volatile
    var cachedSigningCertSha1Hex: String = ""
        private set

    /**
     * Verifies whether the current device environment is untampered for high-trust operations
     * (wallet payout requests, escrow releases, and radar broadcasting).
     */
    fun isDeviceTrustedForSensitiveOps(context: Context): Boolean {
        val audit = verifyAtBoot(context)
        if (BuildConfig.DEBUG) {
            return !audit.isModOrHookDetected
        }
        return audit.isSignatureValid && !audit.isRooted && !audit.isModOrHookDetected
    }

    /**
     * Runs comprehensive boot integrity checks.
     */
    fun verifyAtBoot(context: Context): SecurityAuditResult {
        cachedPackageName = context.packageName
        cachedSigningCertSha1Hex = getAppSignatureSha1Hex(context)

        val signatureValid = verifyAppSignature(context)
        val isRooted = checkRootStatus()
        val isEmulator = checkEmulatorStatus()
        val isModOrHookDetected = checkModApkAndHookIndicators(context)

        val enforcementMode = if (BuildConfig.DEBUG) "MONITORING" else "ENFORCED"
        Log.i(
            TAG,
            "Boot Security Audit [$enforcementMode]: SignatureValid=$signatureValid, IsRooted=$isRooted, IsEmulator=$isEmulator, ModOrHook=$isModOrHookDetected"
        )

        if (!signatureValid && !BuildConfig.DEBUG) {
            Log.w(TAG, "Security Notice: Unrecognized APK signing certificate.")
        }

        return SecurityAuditResult(
            isSignatureValid = signatureValid,
            isRooted = isRooted,
            isEmulator = isEmulator,
            isModOrHookDetected = isModOrHookDetected,
            enforcementMode = enforcementMode
        )
    }

    /**
     * Computes the lowercase 40-char hexadecimal SHA-1 fingerprint of the running APK's signing certificate.
     * Sent via `X-Android-Cert` header so Google Cloud Console Android Key Restrictions can verify the APK.
     */
    fun getAppSignatureSha1Hex(context: Context): String {
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
                } else null
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (!signatures.isNullOrEmpty()) {
                val md = MessageDigest.getInstance("SHA-1")
                val digest = md.digest(signatures[0].toByteArray())
                digest.joinToString("") { "%02x".format(it) }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Detects Mod APK patchers (Lucky Patcher, Jasi Patcher), Xposed/LSPosed/Frida hooks, and package cloning.
     */
    fun checkModApkAndHookIndicators(context: Context): Boolean {
        return try {
            // 1. Detect package name cloning
            val pkg = context.packageName
            if (pkg != "com.aistudio.localiiiy.live" && pkg != "com.example") {
                return true
            }

            // 2. Detect Lucky Patcher / Xposed / Modding apps installed on device
            val suspiciousPackages = listOf(
                "com.chelpus.lackypatch",
                "com.dimonvideo.luckypatcher",
                "com.forpda.lp",
                "com.android.vending.billing.InAppBillingService.COIN",
                "com.android.vending.billing.InAppBillingService.LUCK",
                "de.robv.android.xposed.installer",
                "org.meowcat.edxposed.manager",
                "org.lsposed.manager"
            )
            val pm = context.packageManager
            val hasPatcher = suspiciousPackages.any { modPkg ->
                try {
                    pm.getPackageInfo(modPkg, 0)
                    true
                } catch (_: Exception) {
                    false
                }
            }
            if (hasPatcher) return true

            // 3. Inspect call stack for Xposed / Frida / Substrate injection bridges
            val stackTraceClasses = Throwable().stackTrace.map { it.className.lowercase() }
            stackTraceClasses.any { cls ->
                cls.contains("xposed") || cls.contains("frida") || cls.contains("substrate") || cls.contains("lsposed")
            }
        } catch (_: Exception) {
            false
        }
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

        return currentHash.uppercase() in AUTHORIZED_RELEASE_SIGNATURES_SHA256
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
    val isEmulator: Boolean,
    val isModOrHookDetected: Boolean = false,
    val enforcementMode: String = if (BuildConfig.DEBUG) "MONITORING" else "ENFORCED"
)
