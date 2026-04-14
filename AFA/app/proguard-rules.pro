# =============================================================
# AFA - Regole ProGuard per la build release
# =============================================================

# ── SQLCipher ──
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# ── Room ──
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# ── Kotlinx Serialization ──
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.afa.fitadapt.**$$serializer { *; }
-keepclassmembers class com.afa.fitadapt.** {
    *** Companion;
}
-keepclasseswithmembers class com.afa.fitadapt.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Hilt ──
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ── ZXing ──
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**
