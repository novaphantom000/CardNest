# Add project specific ProGuard rules here.

# Keep Room entities
-keep class app.cardnest.vault.data.db.entities.** { *; }

# Keep Hilt generated classes
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class app.cardnest.vault.**$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class app.cardnest.vault.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Biometric
-keep class androidx.biometric.** { *; }

# Prevent logging of sensitive data in release
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
