# ==============================================================================
# Localiiiy Production Hardened ProGuard / R8 Configuration
# ==============================================================================

# 1. Optimization & Obfuscation Directives
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-repackageclasses 'o'

# Strip verbose/debug logging in production release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve line numbers for Play Console crash symbolication while obfuscating source file names
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations

# 2. Preserve Firestore & Room Serialization Models Only (Obfuscate Business Logic & Security Guards)
-keep class com.example.data.firestore.HyperlocalPulseDocument { <init>(...); public *; }
-keep class com.example.data.firestore.HyperlocalPulseUpdate { <init>(...); public *; }
-keep @androidx.room.Entity class com.example.data.** { *; }
-keep @androidx.room.Dao interface com.example.data.** { *; }
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
    protected <methods>;
}

# 3. Kotlin Coroutines & Serialization
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# 4. Firebase & Google Play Services Rules
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# 5. OkHttp3 / Retrofit2 / Moshi
-dontwarn okhttp3.**
-dontwarn okio.**
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
