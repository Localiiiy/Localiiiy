package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyLanguage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Custom Application class for Localiiiy.
 * Safely initializes FirebaseApp so Firebase Firestore and dependencies operate without fatal exceptions.
 */
class LocaliiiyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize saved language locale
        try {
            val prefs = getSharedPreferences("localiiiy_auth_session", Context.MODE_PRIVATE)
            val savedLangCode = prefs.getString("saved_language_code", null)
            val lang = LocaliiiyLanguage.values().firstOrNull { it.code.equals(savedLangCode, ignoreCase = true) }
                ?: LocaliiiyLanguage.EN
            LocalizationHelper.updateConfigurationLocale(this, lang)
        } catch (_: Throwable) {}

        initializeFirebaseSilently()
        try {
            com.example.security.AppSecurityGuard.verifyAtBoot(this)
        } catch (se: Throwable) {
            Log.w("LocaliiiyApplication", "Security check non-fatal notice: ${se.message}")
        }
    }

    private fun initializeFirebaseSilently() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val apiKey = BuildConfig.FIREBASE_API_KEY.ifBlank { "AIzaSyB_ElgPsmAMHytEKmwPnbVgt8fzq5sFX-Y" }
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:1018994361953:android:c515f741e9454ed8ad4c20")
                    .setProjectId("localiiiy")
                    .setStorageBucket("localiiiy.firebasestorage.app")
                    .setApiKey(apiKey)
                    .setGcmSenderId("1018994361953")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("LocaliiiyApplication", "FirebaseApp initialized with fallback options")
            }
        } catch (e: Throwable) {
            Log.w("LocaliiiyApplication", "Non-blocking FirebaseApp init fallback: ${e.message}")
        }
    }
}

