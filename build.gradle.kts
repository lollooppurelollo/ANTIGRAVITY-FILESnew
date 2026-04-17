// =============================================================
// AFA - Attività Fisica Adattata
// File: build.gradle.kts (root)
// Plugin principali del progetto — dichiarati qui, applicati nei moduli
// =============================================================

plugins {
    // Plugin Android per costruire l'APK
    id("com.android.application") version "8.8.0" apply false

    // Kotlin per Android
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false

    // Supporto Jetpack Compose nel compilatore Kotlin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    // Serializzazione JSON (per export dati)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false

    // KSP — processore di annotazioni per Room e Hilt
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false

    // Hilt — iniezione delle dipendenze
    id("com.google.dagger.hilt.android") version "2.52" apply false
}
