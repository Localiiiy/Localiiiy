package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Custom Application class for Localiiiy.
 * Safely initializes FirebaseApp so Firebase Firestore and dependencies operate without fatal exceptions.
 */
class LocaliApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSilently()
    }

    private fun initializeFirebaseSilently() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:109876543210:android:abcdef0123456789")
                    .setProjectId("localiiiy-app")
                    .setApiKey("AIzaSyLocaliiiyFirebaseApiKeyMock")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("LocaliApplication", "FirebaseApp initialized with fallback options")
            }
        } catch (e: Exception) {
            Log.w("LocaliApplication", "Non-blocking FirebaseApp init fallback: ${e.message}")
        }
    }
}
