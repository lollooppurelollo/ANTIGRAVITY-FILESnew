// =============================================================
// AFA - Attività Fisica Adattata
// Definizione di tutte le schermate (route) dell'app
// =============================================================
package com.afa.fitadapt.ui.navigation

/**
 * Sealed class che definisce tutte le schermate dell'app.
 * Ogni schermata ha una route univoca usata dal NavigationController.
 */
sealed class Screen(val route: String) {

    // ── Flusso iniziale ──
    object Splash : Screen("splash")
    object SetupWizard : Screen("setup_wizard")
    object BiometricLock : Screen("biometric_lock")

    // ── Schermate principali (con bottom nav) ──
    object Home : Screen("home")
    object ActiveCard : Screen("active_card")
    object Progress : Screen("progress")
    object More : Screen("more")

    // ── Dettagli ──
    object ExerciseDetail : Screen("exercise_detail/{exerciseId}/{cardExerciseId}") {
        fun createRoute(exerciseId: Long, cardExerciseId: Long = -1) =
            "exercise_detail/$exerciseId/$cardExerciseId"
    }

    object ArticleDetail : Screen("article_detail/{articleId}") {
        fun createRoute(articleId: Long) = "article_detail/$articleId"
    }

    // ── Sessione ──
    object Session : Screen("session")

    // ── Diario ──
    object Diary : Screen("diary")

    // ── Articoli ──
    object Articles : Screen("articles")

    // ── Export ──
    object Export : Screen("export")

    // ── Impostazioni ──
    object Settings : Screen("settings")

    // ── Sezione protetta ──
    object ProtectedGate : Screen("protected_gate")
    object ProtectedDashboard : Screen("protected_dashboard")
    object CardEditor : Screen("card_editor/{cardId}") {
        fun createRoute(cardId: Long = -1L) = "card_editor/$cardId"
    }
    object ExercisePicker : Screen("exercise_picker")
    object FutureCards : Screen("future_cards")
    object GoalEditor : Screen("goal_editor")
}
