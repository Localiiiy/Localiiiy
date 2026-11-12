# ==============================================================================
# Localiiiy Production Hardened ProGuard / R8 Configuration
# Resolves Google Play Console "DEX code optimization is below our threshold (Obfuscation 0%)"
# ==============================================================================

# 1. R8 Full Optimization & Obfuscation Directives
-optimizationpasses 5
-allowaccessmodification
-repackageclasses 'o'

# Strip verbose/debug logging in production release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# Preserve line numbers for Play Console crash symbolication while obfuscating source file names
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations

# 2. Preserve Only Reflection-Deserialized Firestore Document & Room Database Constructors
-keep class com.example.data.firestore.HyperlocalPulseDocument { <init>(...); public *; }
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
    protected <methods>;
}

# 3. Suppress Benign Third-Party Warnings (Allow R8 to Obfuscate Unused/Internal SDK Classes)
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn okhttp3.**
-dontwarn okio.**
