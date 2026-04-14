// =============================================================
// AFA - Attività Fisica Adattata
// File: app/build.gradle.kts
// Configurazione del modulo app con tutte le dipendenze
// =============================================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.afa.fitadapt"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.afa.fitadapt"
        minSdk = 26          // Android 8.0 — supporta BiometricPrompt e Keystore AES
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // ══════════════════════════════════════════════════════════
    // JETPACK COMPOSE (gestito tramite BOM per coerenza versioni)
    // ══════════════════════════════════════════════════════════
    val composeBom = platform("androidx.compose:compose-bom:2025.01.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ══════════════════════════════════════════════════════════
    // NAVIGAZIONE
    // ══════════════════════════════════════════════════════════
    implementation("androidx.navigation:navigation-compose:2.8.6")

    // ══════════════════════════════════════════════════════════
    // LIFECYCLE + VIEWMODEL
    // ══════════════════════════════════════════════════════════
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // ══════════════════════════════════════════════════════════
    // ROOM (database locale) + SQLCipher (cifratura database)
    // ══════════════════════════════════════════════════════════
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // SQLCipher — cifratura trasparente del database SQLite
    // Usiamo android-database-sqlcipher (net.sqlcipher.*) che è compatibile con Room
    implementation("net.zetetic:android-database-sqlcipher:4.6.1")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // ══════════════════════════════════════════════════════════
    // DATASTORE (preferenze e impostazioni locali)
    // ══════════════════════════════════════════════════════════
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // ══════════════════════════════════════════════════════════
    // HILT (iniezione dipendenze)
    // ══════════════════════════════════════════════════════════
    implementation("com.google.dagger:hilt-android:2.54")
    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // ══════════════════════════════════════════════════════════
    // BIOMETRIA (sblocco con impronta/volto)
    // Versione stabile 1.1.0 — niente alpha
    // ══════════════════════════════════════════════════════════
    implementation("androidx.biometric:biometric:1.1.0")

    // ══════════════════════════════════════════════════════════
    // WORKMANAGER (notifiche programmate in background)
    // ══════════════════════════════════════════════════════════
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // ══════════════════════════════════════════════════════════
    // QR CODE (generazione QR per export dati)
    // ══════════════════════════════════════════════════════════
    implementation("com.google.zxing:core:3.5.3")

    // ══════════════════════════════════════════════════════════
    // VIDEO PLAYER (riproduzione video esercizi)
    // ══════════════════════════════════════════════════════════
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")

    // ══════════════════════════════════════════════════════════
    // SERIALIZZAZIONE JSON (per export dati)
    // ══════════════════════════════════════════════════════════
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ══════════════════════════════════════════════════════════
    // CORE ANDROID
    // ══════════════════════════════════════════════════════════
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}
