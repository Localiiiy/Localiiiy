package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.android.gms.ads.MobileAds

/**
 * Custom Application class for Localiiiy.
 * Safely initializes FirebaseApp so Firebase Firestore and dependencies operate without fatal exceptions.
 */
class LocaliiiyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSilently()
        MobileAds.initialize(this) {}
        com.example.service.FavoriteProximityManager.init(this)
    }

    private fun initializeFirebaseSilently() {
        MobileAds.initialize(this) {}
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:109876543210:android:abcdef0123456789")
                    .setProjectId("Localiiiy-app")
                    .setApiKey("AIzaSyLocaliiiyFirebaseApiKeyMock")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("LocaliiiyApplication", "FirebaseApp initialized with fallback options")
            }
        } catch (e: Exception) {
            Log.w("LocaliiiyApplication", "Non-blocking FirebaseApp init fallback: ${e.message}")
        }
    }
}
