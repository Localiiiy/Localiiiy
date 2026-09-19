package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Custom Application class for Localiiiy.
 * Safely initializes FirebaseApp so Firebase Firestore and dependencies operate without fatal exceptions.
 */
class LocaliiiyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSilently()
        try {
            com.example.service.FavoriteProximityManager.init(this)
        } catch (e: Exception) {
            Log.w("LocaliiiyApplication", "ProximityManager init non-blocking: ${e.message}")
        }
    }

    private fun initializeFirebaseSilently() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:109876543210:android:abcdef0123456789")
                    .setProjectId("localiiiy-app")
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setGcmSenderId("109876543210")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("LocaliiiyApplication", "FirebaseApp initialized with fallback options")
            }

            // In emulator or pre-production environments without registered SHA-256 tokens in Firebase Console,
            // App Check debug provider issues tokens that are rejected by Firebase backend with:
            // "RecaptchaCallWrapper: An internal error has occurred. [ Firebase App Check token is invalid. ]"
            // Install DebugAppCheckProviderFactory only when an explicit debug secret is configured, or skip non-blocking.
            try {
                val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
                if (BuildConfig.DEBUG) {
                    firebaseAppCheck.installAppCheckProviderFactory(
                        com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
                    )
                    Log.d("LocaliiiyApplication", "Firebase AppCheck DebugProviderFactory installed")
                }
            } catch (ace: Exception) {
                Log.w("LocaliiiyApplication", "AppCheck non-blocking init notice: ${ace.message}")
            }
        } catch (e: Exception) {
            Log.w("LocaliiiyApplication", "Non-blocking FirebaseApp init fallback: ${e.message}")
        }
    }
}

