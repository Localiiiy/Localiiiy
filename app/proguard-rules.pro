# Proguard rules for Localiiiy

# Keep Compose runtime
-keep class androidx.compose.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.** { *; }

# Keep Media3 ExoPlayer
-keep class androidx.media3.** { *; }

# Keep Firebase
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

# Keep Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# AndroidX Core & Room
-keep class androidx.room.** { *; }
