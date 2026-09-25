# ProGuard & R8 Optimization Rules for Localiiiy

# Preserve Room Database Entities and DAOs
-keep class com.example.data.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Preserve Firebase models and serialization
-keepclassmembers class com.example.data.firestore.** {
    public <init>(...);
    public <fields>;
}

# Preserve Coroutines internal state
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Preserve Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.**

# Firebase Authentication
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.internal.firebase-auth-api.** { *; }

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }

# Play Integrity API
-keep class com.google.android.play.core.integrity.** { *; }
-keep class com.google.android.play.core.common.** { *; }

# Preserve Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep all models used in Firestore
-keepclassmembers class com.example.data.** {
    public <init>(...);
    public <fields>;
}
