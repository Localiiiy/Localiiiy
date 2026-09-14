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

            // Initialize Firebase App Check
            val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
            
            // Use Debug provider in debug builds, Play Integrity in production
            if (BuildConfig.DEBUG) {
                firebaseAppCheck.installAppCheckProviderFactory(
                    com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
                )
                Log.d("LocaliiiyApplication", "Firebase AppCheck initialized with Debug provider")
            } else {
                firebaseAppCheck.installAppCheckProviderFactory(
                    com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory.getInstance()
                )
                Log.d("LocaliiiyApplication", "Firebase AppCheck initialized with PlayIntegrity provider")
            }
        } catch (e: Exception) {
            Log.w("LocaliiiyApplication", "Non-blocking FirebaseApp init fallback: ${e.message}")
        }
    }
}

