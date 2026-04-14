// =============================================================
// AFA - Attività Fisica Adattata
// File: settings.gradle.kts
// Configurazione dei repository e dei moduli del progetto
// =============================================================

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Nome del progetto
rootProject.name = "AFA"

// Modulo principale dell'app
include(":app")
