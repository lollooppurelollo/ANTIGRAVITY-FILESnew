// =============================================================
// AFA - Attività Fisica Adattata
// Navigation graph principale
// =============================================================
package com.afa.fitadapt.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.afa.fitadapt.security.BiometricHelper
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.articles.ArticleDetailScreen
import com.afa.fitadapt.ui.articles.ArticlesScreen
import com.afa.fitadapt.ui.articles.ArticlesViewModel
import com.afa.fitadapt.ui.auth.AuthViewModel
import com.afa.fitadapt.ui.auth.BiometricLockScreen
import com.afa.fitadapt.ui.auth.SetupWizardScreen
import com.afa.fitadapt.ui.card.ActiveCardScreen
import com.afa.fitadapt.ui.card.CardViewModel
import com.afa.fitadapt.ui.card.ExerciseDetailScreen
import com.afa.fitadapt.ui.diary.DiaryScreen
import com.afa.fitadapt.ui.diary.DiaryViewModel
import com.afa.fitadapt.ui.export.ExportScreen
import com.afa.fitadapt.ui.export.ExportViewModel
import com.afa.fitadapt.ui.home.HomeScreen
import com.afa.fitadapt.ui.home.HomeViewModel
import com.afa.fitadapt.ui.more.MoreScreen
import com.afa.fitadapt.ui.progress.ProgressScreen
import com.afa.fitadapt.ui.progress.ProgressViewModel
import com.afa.fitadapt.ui.protected_section.ProtectedDashboardScreen
import com.afa.fitadapt.ui.protected_section.ProtectedGateScreen
import com.afa.fitadapt.ui.protected_section.ProtectedViewModel
import com.afa.fitadapt.ui.session.SessionScreen
import com.afa.fitadapt.ui.session.SessionViewModel
import com.afa.fitadapt.ui.settings.SettingsScreen
import com.afa.fitadapt.ui.settings.SettingsViewModel
import com.afa.fitadapt.ui.splash.SplashScreen

/**
 * Route dove la bottom navigation bar è visibile.
 */
private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.ActiveCard.route,
    Screen.Progress.route,
    Screen.More.route
)

/**
 * NavGraph principale dell'app AFA.
 *
 * Flusso:
 * 1. Splash → verifica primo avvio
 * 2. Setup Wizard (se primo avvio) OPPURE BiometricLock (se già configurato)
 * 3. Home con bottom navigation
 * 4. Schermate dettaglio senza bottom nav
 */
@Composable
fun AfaNavGraph(biometricHelper: BiometricHelper) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // ══════════════════════════════════════════════
            //  FLUSSO INIZIALE
            // ══════════════════════════════════════════════

            composable(Screen.Splash.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                SplashScreen(
                    authViewModel = authViewModel,
                    onNavigateToSetup = {
                        navController.navigate(Screen.SetupWizard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToBiometric = {
                        navController.navigate(Screen.BiometricLock.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.SetupWizard.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                SetupWizardScreen(
                    authViewModel = authViewModel,
                    onSetupComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SetupWizard.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.BiometricLock.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                BiometricLockScreen(
                    authViewModel = authViewModel,
                    biometricHelper = biometricHelper,
                    onAuthenticated = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.BiometricLock.route) { inclusive = true }
                        }
                    }
                )
            }

            // ══════════════════════════════════════════════
            //  SCHERMATE PRINCIPALI (con bottom nav)
            // ══════════════════════════════════════════════

            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToSession = {
                        navController.navigate(Screen.Session.route)
                    },
                    onNavigateToCard = {
                        navController.navigate(Screen.ActiveCard.route)
                    },
                    onNavigateToArticle = { articleId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                    }
                )
            }

            composable(Screen.ActiveCard.route) {
                val cardViewModel: CardViewModel = hiltViewModel()
                ActiveCardScreen(
                    cardViewModel = cardViewModel,
                    onExerciseClick = { exerciseId, cardExerciseId ->
                        navController.navigate(
                            Screen.ExerciseDetail.createRoute(exerciseId, cardExerciseId)
                        )
                    }
                )
            }

            composable(Screen.Progress.route) {
                val progressViewModel: ProgressViewModel = hiltViewModel()
                ProgressScreen(progressViewModel = progressViewModel)
            }

            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToDiary = {
                        navController.navigate(Screen.Diary.route)
                    },
                    onNavigateToArticles = {
                        navController.navigate(Screen.Articles.route)
                    },
                    onNavigateToExport = {
                        navController.navigate(Screen.Export.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToProtected = {
                        navController.navigate(Screen.ProtectedGate.route)
                    }
                )
            }

            // ══════════════════════════════════════════════
            //  SCHERMATE DETTAGLIO (senza bottom nav)
            // ══════════════════════════════════════════════

            composable(Screen.Session.route) {
                val sessionViewModel: SessionViewModel = hiltViewModel()
                SessionScreen(
                    sessionViewModel = sessionViewModel,
                    onBack = { navController.popBackStack() },
                    onDone = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ExerciseDetail.route,
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.LongType },
                    navArgument("cardExerciseId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: return@composable
                val cardExerciseId = backStackEntry.arguments?.getLong("cardExerciseId") ?: -1L
                val cardViewModel: CardViewModel = hiltViewModel()
                ExerciseDetailScreen(
                    cardViewModel = cardViewModel,
                    exerciseId = exerciseId,
                    cardExerciseId = cardExerciseId,
                    onBack = { navController.popBackStack() }
                )
            }

            // ══════════════════════════════════════════════
            //  PLACEHOLDER per Tranche 4+
            //  (Diary, Articles, Export, Settings, Protected)
            // ══════════════════════════════════════════════

            composable(Screen.Diary.route) {
                val diaryViewModel: DiaryViewModel = hiltViewModel()
                DiaryScreen(
                    diaryViewModel = diaryViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Articles.route) {
                val articlesViewModel: ArticlesViewModel = hiltViewModel()
                ArticlesScreen(
                    articlesViewModel = articlesViewModel,
                    onBack = { navController.popBackStack() },
                    onArticleClick = { articleId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                    }
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                arguments = listOf(navArgument("articleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getLong("articleId") ?: return@composable
                val articlesViewModel: ArticlesViewModel = hiltViewModel()
                ArticleDetailScreen(
                    articlesViewModel = articlesViewModel,
                    articleId = articleId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Export.route) {
                val exportViewModel: ExportViewModel = hiltViewModel()
                ExportScreen(
                    exportViewModel = exportViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ProtectedGate.route) {
                val protectedViewModel: ProtectedViewModel = hiltViewModel()
                ProtectedGateScreen(
                    protectedViewModel = protectedViewModel,
                    onBack = { navController.popBackStack() },
                    onAuthenticated = {
                        navController.navigate(Screen.ProtectedDashboard.route) {
                            popUpTo(Screen.ProtectedGate.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ProtectedDashboard.route) {
                val protectedViewModel: ProtectedViewModel = hiltViewModel()
                ProtectedDashboardScreen(
                    protectedViewModel = protectedViewModel,
                    onBack = { navController.popBackStack() },
                    onManageCards = { /* TODO: CardEditor screen */ },
                    onManageGoals = { /* TODO: GoalEditor screen */ }
                )
            }
        }
    }
}

/**
 * Schermata placeholder per le funzionalità da implementare nelle prossime tranche.
 */
@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🚧", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "In fase di sviluppo — sarà disponibile presto",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

