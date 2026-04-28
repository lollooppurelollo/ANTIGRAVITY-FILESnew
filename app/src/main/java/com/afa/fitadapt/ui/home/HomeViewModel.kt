// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Home
// =============================================================
package com.afa.fitadapt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.local.entity.ScheduledSessionEntity
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import com.afa.fitadapt.data.repository.*
import com.afa.fitadapt.notification.WorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val patientCode: String = "",
    val activeCard: TrainingCardEntity? = null,
    val completedToday: Boolean = false,
    val currentStreak: Int = 0,
    val totalCompleted: Int = 0,
    val featuredArticle: ArticleEntity? = null,
    val activeGoals: List<GoalEntity> = emptyList(),
    val scheduledSessions: List<ScheduledSessionEntity> = emptyList(),
    val completedSessionsDates: List<Long> = emptyList(),
    val isMonthlyView: Boolean = false
)

/**
 * ViewModel per la schermata Home.
 * Carica i dati di riepilogo: scheda attiva, streak, articolo in evidenza e calendario.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: PatientProfileRepository,
    private val cardRepository: TrainingCardRepository,
    private val sessionRepository: SessionRepository,
    private val articleRepository: ArticleRepository,
    private val goalRepository: GoalRepository,
    private val calendarRepository: CalendarRepository,
    private val workScheduler: WorkScheduler,
    private val userPreferences: com.afa.fitadapt.data.local.datastore.UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        // Profilo paziente
        viewModelScope.launch {
            profileRepository.getProfile().collect { profile ->
                _uiState.update { it.copy(patientCode = profile?.patientCode ?: "") }
            }
        }
        // Scheda attiva
        viewModelScope.launch {
            cardRepository.getActiveCard().collect { card ->
                _uiState.update { it.copy(activeCard = card) }
            }
        }
        // Statistiche
        viewModelScope.launch {
            val streak = sessionRepository.calculateCurrentStreak()
            val todayDone = sessionRepository.hasSessionToday()
            _uiState.update {
                it.copy(
                    currentStreak = streak,
                    completedToday = todayDone
                )
            }
        }
        // Sessioni completate
        viewModelScope.launch {
            sessionRepository.countCompletedSessions().collect { count ->
                _uiState.update { it.copy(totalCompleted = count) }
            }
        }
        // Articolo in evidenza
        viewModelScope.launch {
            articleRepository.getFeaturedArticle().collect { article ->
                _uiState.update { it.copy(featuredArticle = article) }
            }
        }
        // Obiettivi attivi
        viewModelScope.launch {
            goalRepository.getActiveGoals().collect { allGoals ->
                // Filtra gli obiettivi scalabili: mostra solo se non hanno parent o se il parent è a "Gold"
                val filteredGoals = allGoals.filter { goal ->
                    if (goal.parentGoalId == null) {
                        true
                    } else {
                        // Verifica se il parent ha raggiunto il valore Gold
                        val parent = allGoals.find { it.id == goal.parentGoalId }
                        if (parent != null) {
                            val goldTarget = parent.goldValue ?: parent.targetValue
                            parent.currentValue >= goldTarget
                        } else {
                            // Se non è tra i goals attivi (raro), lo carichiamo (qui usiamo firstOrNull per non bloccare)
                            false // Per ora lo nascondiamo se il parent non è caricato
                        }
                    }
                }
                _uiState.update { it.copy(activeGoals = filteredGoals) }
                updateGoalsProgress(allGoals)
            }
        }
        // Calendario
        viewModelScope.launch {
            calendarRepository.getAllScheduled().collect { scheduled ->
                _uiState.update { it.copy(scheduledSessions = scheduled) }
            }
        }

        // Date sessioni completate per il calendario
        viewModelScope.launch {
            sessionRepository.getAllSessions().collect { sessions ->
                val completedDates = sessions.filter { it.completed }.map { it.date }
                _uiState.update { it.copy(completedSessionsDates = completedDates) }
            }
        }
        
        // Vista calendario salvata
        viewModelScope.launch {
            userPreferences.calendarMonthlyView.collect { isMonthly ->
                _uiState.update { it.copy(isMonthlyView = isMonthly, isLoading = false) }
            }
        }
    }

    fun setCalendarView(isMonthly: Boolean) {
        viewModelScope.launch {
            userPreferences.setCalendarMonthlyView(isMonthly)
        }
    }

    private fun updateGoalsProgress(goals: List<GoalEntity>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startOfWeek = com.afa.fitadapt.util.DateUtils.getStartOfWeek()
            val startOfMonth = com.afa.fitadapt.util.DateUtils.getStartOfMonth()

            // Statistiche globali
            val fullSessionsTotal = sessionRepository.countFullSessions().firstOrNull() ?: 0
            val streak = sessionRepository.calculateCurrentStreak()
            
            // Statistiche periodiche
            val sessionsThisWeek = sessionRepository.getSessionsInRange(startOfWeek, now).firstOrNull()?.count { it.completed && !it.partial } ?: 0
            val minutesThisWeek = sessionRepository.getSessionsInRange(startOfWeek, now).firstOrNull()?.filter { it.completed }?.sumOf { it.actualDurationMin ?: 0 } ?: 0
            
            val sessionsThisMonth = sessionRepository.getSessionsInRange(startOfMonth, now).firstOrNull()?.count { it.completed && !it.partial } ?: 0

            goals.forEach { goal ->
                // 1. Controllo reset temporale
                val needsReset = com.afa.fitadapt.util.DateUtils.isPeriodExpired(
                    goal.updatedAt, goal.periodType, goal.customPeriodValue, goal.customPeriodUnit
                )

                if (needsReset) {
                    goalRepository.updateProgress(goal.id, 0f)
                    return@forEach // Salta l'aggiornamento per questo frame, verrà aggiornato al prossimo ciclo o dopo il reset
                }

                // 2. Calcolo nuovo valore in base al tipo e alla periodicità
                val newValue = when (goal.targetType) {
                    "sessions_per_week" -> sessionsThisWeek.toFloat()
                    "total_minutes_week" -> minutesThisWeek.toFloat()
                    "streak_days" -> streak.toFloat()
                    "total_sessions" -> {
                        if (goal.periodType == "monthly") sessionsThisMonth.toFloat()
                        else fullSessionsTotal.toFloat()
                    }
                    else -> goal.currentValue
                }

                if (newValue != goal.currentValue) {
                    goalRepository.updateProgress(goal.id, newValue)
                }
            }
        }
    }

    fun addScheduledSession(session: ScheduledSessionEntity) {
        viewModelScope.launch {
            val id = calendarRepository.addScheduledSession(session)
            if (session.notificationEnabled) {
                // Parsing HH:mm to apply it to session.date
                val parts = session.startTime.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                
                val cal = Calendar.getInstance().apply {
                    timeInMillis = session.date
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                workScheduler.scheduleScheduledSession(id, session.title, cal.timeInMillis)
            }
        }
    }
    
    fun deleteScheduledSession(session: ScheduledSessionEntity) {
        viewModelScope.launch {
            calendarRepository.deleteScheduledSession(session)
            workScheduler.cancelScheduledSession(session.id)
        }
    }
}
