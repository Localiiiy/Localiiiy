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
