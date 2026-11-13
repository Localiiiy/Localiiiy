package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Captures invitation / referral handles strictly when the app is installed or opened via
 * a shared Play Store invitation link or deep link.
 * Remains completely empty when downloaded organically from Google Play Store.
 */
object InstallReferrerHelper {
    private const val TAG = "InstallReferrerHelper"
    private const val PREFS_NAME = "localiiiy_invite_prefs"
    private const val KEY_PENDING_INVITE = "pending_invite_handle"
    private const val KEY_REFERRER_CHECKED = "play_referrer_checked"

    private val invalidOrganicTokens = setOf(
        "google-play", "organic", "(not set)", "not_set", "play_store"
    )

    fun sanitizeInviteHandle(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.trim()
            .removePrefix("@")
            .lowercase()
            .replace(Regex("[^a-z0-9_.]"), "")
        if (cleaned.length < 2 || cleaned in invalidOrganicTokens) return null
        return cleaned
    }

    fun extractInviteFromUri(uri: Uri?): String? {
        if (uri == null) return null
        val queryCandidates = listOf("invite", "refer", "ref", "inviter")
        for (key in queryCandidates) {
            val valParam = uri.getQueryParameter(key)
            val handle = sanitizeInviteHandle(valParam)
            if (handle != null) return handle
        }
        val nestedReferrer = uri.getQueryParameter("referrer")
        val fromNested = parseReferrerString(nestedReferrer)
        if (fromNested != null) return fromNested

        val segments = uri.pathSegments
        for (marker in listOf("refer", "invite", "u")) {
            val idx = segments.indexOfFirst { it.equals(marker, ignoreCase = true) }
            if (idx != -1 && idx + 1 < segments.size) {
                val handle = sanitizeInviteHandle(segments[idx + 1])
                if (handle != null) return handle
            }
        }
        return null
    }

    fun parseReferrerString(rawReferrer: String?): String? {
        if (rawReferrer.isNullOrBlank()) return null
        val decoded = try {
            URLDecoder.decode(rawReferrer, StandardCharsets.UTF_8.name())
        } catch (_: Exception) {
            rawReferrer
        }
        val pairs = decoded.split("&")
        for (pair in pairs) {
            val eqIdx = pair.indexOf('=')
            if (eqIdx > 0) {
                val key = pair.substring(0, eqIdx).trim().lowercase()
                val value = pair.substring(eqIdx + 1).trim()
                if (key == "invite" || key == "refer" || key == "ref" || key == "inviter") {
                    val handle = sanitizeInviteHandle(value)
                    if (handle != null) return handle
                }
            }
        }
        // Only treat raw string as a handle if it isn't an organic Play Store utm payload
        if (!decoded.contains("utm_", ignoreCase = true) && !decoded.contains("=")) {
            return sanitizeInviteHandle(decoded)
        }
        return null
    }

    fun savePendingInvite(context: Context, handle: String?) {
        val clean = sanitizeInviteHandle(handle) ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PENDING_INVITE, clean)
            .apply()
    }

    fun getPendingInvite(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PENDING_INVITE, null)
            ?.let(::sanitizeInviteHandle)
    }

    fun clearPendingInvite(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_PENDING_INVITE)
            .apply()
    }

    /**
     * Queries Google Play Install Referrer API once on initial launch to check whether the app
     * was installed from a shared Play Store invitation link.
     */
    fun fetchPlayInstallReferrer(context: Context, onInviteDetected: (String) -> Unit) {
        val existing = getPendingInvite(context)
        if (existing != null) {
            onInviteDetected(existing)
            return
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_REFERRER_CHECKED, false)) {
            return
        }

        try {
            val client = InstallReferrerClient.newBuilder(context.applicationContext).build()
            client.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    try {
                        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                            val details = client.installReferrer
                            val invite = parseReferrerString(details?.installReferrer)
                            if (invite != null) {
                                savePendingInvite(context, invite)
                                onInviteDetected(invite)
                            }
                            prefs.edit().putBoolean(KEY_REFERRER_CHECKED, true).apply()
                        } else if (
                            responseCode == InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED ||
                            responseCode == InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED
                        ) {
                            prefs.edit().putBoolean(KEY_REFERRER_CHECKED, true).apply()
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error reading Play install referrer: ${e.message}")
                    } finally {
                        runCatching { client.endConnection() }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    runCatching { client.endConnection() }
                }
            })
        } catch (e: Exception) {
            Log.w(TAG, "Play Install Referrer unavailable: ${e.message}")
        }
    }
}
