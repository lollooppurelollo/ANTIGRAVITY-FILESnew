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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.afa.fitadapt.ui.protected_section.management.*
import com.afa.fitadapt.ui.session.SessionScreen
import com.afa.fitadapt.ui.session.SessionViewModel
import com.afa.fitadapt.ui.session.SessionHistoryScreen
import com.afa.fitadapt.ui.session.SessionDetailScreen
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
    Screen.Diary.route,
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
                    onNavigateToHistory = {
                        navController.navigate(Screen.SessionHistory.route)
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
                var showCompletionDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    sessionViewModel.onSessionTargetReached = {
                        showCompletionDialog = true
                    }
                }

                if (showCompletionDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showCompletionDialog = false },
                        title = { Text("Obiettivo raggiunto!") },
                        text = { Text("Hai completato tutte le sessioni previste per questa scheda. Vuoi passare alla prossima?") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    showCompletionDialog = false
                                    // La logica di avanzamento è già gestita dal repository
                                    // ma qui potremmo voler navigare o fare altro.
                                    // Per ora chiudiamo solo il dialogo.
                                }
                            ) { Text("OK") }
                        }
                    )
                }

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

            composable(Screen.SessionHistory.route) {
                val sessionViewModel: SessionViewModel = hiltViewModel()
                SessionHistoryScreen(
                    viewModel = sessionViewModel,
                    onBack = { navController.popBackStack() },
                    onSessionClick = { sessionId ->
                        navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                    }
                )
            }

            composable(
                route = Screen.SessionDetail.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
                val sessionViewModel: SessionViewModel = hiltViewModel()
                SessionDetailScreen(
                    viewModel = sessionViewModel,
                    sessionId = sessionId,
                    onBack = { navController.popBackStack() }
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
                    diaryViewModel = diaryViewModel
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
                // Usiamo un ViewModel con scope legato a QUESTA rotta specifica,
                // così quando usciamo dalla sezione protetta, il ViewModel viene distrutto
                // e la password verrà richiesta di nuovo al prossimo accesso.
                val protectedViewModel: ProtectedViewModel = hiltViewModel()
                ProtectedGateScreen(
                    protectedViewModel = protectedViewModel,
                    onBack = { navController.popBackStack() },
                    onAuthenticated = {
                        navController.navigate(Screen.ProtectedDashboard.route)
                    }
                )
            }

            composable(Screen.ProtectedDashboard.route) {
                // Condividiamo lo stesso ViewModel (grazie allo scope del backstack se volessimo),
                // ma per semplicità e sicurezza qui richiediamo che l'utente arrivi dal Gate.
                // Se usiamo hiltViewModel() senza specificare uno scope, ne viene creato uno nuovo
                // che avrà isAuthenticated = false di default, forzando il ritorno al gate.
                val backStackEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Screen.ProtectedGate.route)
                }
                val protectedViewModel: ProtectedViewModel = hiltViewModel(backStackEntry)
                
                ProtectedDashboardScreen(
                    protectedViewModel = protectedViewModel,
                    onBack = { 
                        navController.popBackStack(Screen.ProtectedGate.route, inclusive = true)
                    },
                    onManageCards = { navController.navigate(Screen.CardManager.route) },
                    onManageGoals = { navController.navigate(Screen.GoalManager.route) },
                    onManageExercises = { navController.navigate(Screen.ExerciseLibraryManager.route) },
                    onManageArticles = { navController.navigate(Screen.ArticleManager.route) }
                )
            }

            // ── Rotte Gestione (Area Protetta) ──

            composable(Screen.CardManager.route) {
                val viewModel: CardManagerViewModel = hiltViewModel()
                CardManagerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAddCard = { navController.navigate(Screen.CardEditor.createRoute(-1L)) },
                    onEditCard = { id -> navController.navigate(Screen.CardEditor.createRoute(id)) }
                )
            }

            composable(
                route = Screen.CardEditor.route,
                arguments = listOf(navArgument("cardId") { type = NavType.LongType })
            ) { backStackEntry ->
                val viewModel: CardEditorViewModel = hiltViewModel()
                CardEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onPickExercise = { navController.navigate(Screen.ExercisePicker.route) }
                )
            }

            composable(Screen.ExercisePicker.route) {
                val viewModel: ExerciseLibraryViewModel = hiltViewModel()
                val backStackEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Screen.CardEditor.route)
                }
                val editorViewModel: CardEditorViewModel = hiltViewModel(backStackEntry)
                
                ExercisePickerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onExerciseSelected = { exercise ->
                        editorViewModel.addExercise(exercise)
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.GoalManager.route) {
                val viewModel: GoalManagerViewModel = hiltViewModel()
                GoalManagerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAddGoal = { navController.navigate(Screen.GoalEditor.createRoute(-1L)) },
                    onEditGoal = { id -> navController.navigate(Screen.GoalEditor.createRoute(id)) }
                )
            }

            composable(
                route = Screen.GoalEditor.route,
                arguments = listOf(navArgument("goalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getLong("goalId") ?: -1L
                val viewModel: GoalEditorViewModel = hiltViewModel()
                
                LaunchedEffect(goalId) {
                    if (goalId != -1L) {
                        viewModel.loadGoal(goalId)
                    }
                }

                GoalEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ExerciseLibraryManager.route) {
                val viewModel: ExerciseLibraryViewModel = hiltViewModel()
                ExerciseLibraryManagerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAddExercise = { navController.navigate(Screen.ExerciseEditor.createRoute(-1L)) },
                    onEditExercise = { id -> navController.navigate(Screen.ExerciseEditor.createRoute(id)) }
                )
            }

            composable(
                route = Screen.ExerciseEditor.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) {
                val viewModel: ExerciseEditorViewModel = hiltViewModel()
                ExerciseEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ArticleManager.route) {
                val viewModel: ArticleManagerViewModel = hiltViewModel()
                ArticleManagerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAddArticle = { navController.navigate(Screen.ArticleEditor.createRoute(-1L)) },
                    onEditArticle = { id -> navController.navigate(Screen.ArticleEditor.createRoute(id)) }
                )
            }

            composable(
                route = Screen.ArticleEditor.route,
                arguments = listOf(navArgument("articleId") { type = NavType.LongType })
            ) {
                val viewModel: ArticleEditorViewModel = hiltViewModel()
                ArticleEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Schermata placeholder per le funzionalità da implementare nelle prossime tranche.
 */
@Suppress("unused") // Usata per le route non ancora implementate nella navigazione
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

