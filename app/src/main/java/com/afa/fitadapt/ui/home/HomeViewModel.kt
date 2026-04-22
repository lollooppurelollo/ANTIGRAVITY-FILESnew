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
    val scheduledSessions: List<ScheduledSessionEntity> = emptyList()
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
    private val workScheduler: WorkScheduler
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
            goalRepository.getActiveGoals().collect { goals ->
                _uiState.update { it.copy(activeGoals = goals) }
                updateGoalsProgress(goals)
            }
        }
        // Calendario
        viewModelScope.launch {
            calendarRepository.getAllScheduled().collect { scheduled ->
                _uiState.update { it.copy(scheduledSessions = scheduled, isLoading = false) }
            }
        }
    }

    private fun updateGoalsProgress(goals: List<GoalEntity>) {
        viewModelScope.launch {
            val totalSessions = sessionRepository.countTotalSessions().firstOrNull() ?: 0
            val fullSessions = sessionRepository.countFullSessions().firstOrNull() ?: 0
            val totalMin = sessionRepository.totalMinutes().firstOrNull() ?: 0
            val streak = sessionRepository.calculateCurrentStreak()

            goals.forEach { goal ->
                val newValue = when (goal.targetType) {
                    "total_sessions" -> fullSessions.toFloat()
                    "total_minutes_week" -> totalMin.toFloat()
                    "streak_days" -> streak.toFloat()
                    "sessions_per_week" -> fullSessions.toFloat()
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
