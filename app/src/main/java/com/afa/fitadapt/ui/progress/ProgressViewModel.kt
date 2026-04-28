// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Progressi (placeholder per Tranche 4)
// =============================================================
package com.afa.fitadapt.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.data.repository.DiaryRepository
import com.afa.fitadapt.data.repository.ProgressRepository
import com.afa.fitadapt.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val isLoading: Boolean = true,
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val fullSessions: Int = 0,
    val partialSessions: Int = 0,
    val adherencePercent: Float = 0f,
    val totalMinutes: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val activeGoals: List<GoalEntity> = emptyList(),
    val recentSessions: List<SessionEntity> = emptyList(),
    val scaleEntries: List<ScaleEntryEntity> = emptyList()
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionRepository: SessionRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProgress() }

    fun loadProgress() {
        viewModelScope.launch {
            progressRepository.completedSessionsCount().collect { completed ->
                _uiState.update { 
                    val adh = if (it.totalSessions > 0) (completed.toFloat() / it.totalSessions) * 100f else 0f
                    it.copy(completedSessions = completed, adherencePercent = adh) 
                }
            }
        }
        viewModelScope.launch {
            progressRepository.fullSessionsCount().collect { full ->
                _uiState.update { it.copy(fullSessions = full) }
            }
        }
        viewModelScope.launch {
            progressRepository.partialSessionsCount().collect { partial ->
                _uiState.update { it.copy(partialSessions = partial) }
            }
        }
        viewModelScope.launch {
            progressRepository.totalSessionsCount().collect { total ->
                _uiState.update {
                    val adh = if (total > 0) (it.completedSessions.toFloat() / total) * 100f else 0f
                    it.copy(totalSessions = total, adherencePercent = adh)
                }
            }
        }
        viewModelScope.launch {
            progressRepository.totalMinutes().collect { min ->
                _uiState.update { it.copy(totalMinutes = min) }
            }
        }
        viewModelScope.launch {
            val streak = sessionRepository.calculateCurrentStreak()
            val longest = sessionRepository.calculateLongestStreak()
            _uiState.update { it.copy(currentStreak = streak, longestStreak = longest) }
        }
        viewModelScope.launch {
            progressRepository.getActiveGoals().collect { allGoals ->
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
                            // Se non è tra i goals attivi (raro), lo nascondiamo per evitare chiamate sospese in filter
                            false
                        }
                    }
                }
                _uiState.update { it.copy(activeGoals = filteredGoals) }
            }
        }
        viewModelScope.launch {
            sessionRepository.getAllSessions().collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions) }
            }
        }
        viewModelScope.launch {
            diaryRepository.getAllScaleEntries().collect { scales ->
                _uiState.update { it.copy(scaleEntries = scales, isLoading = false) }
            }
        }
    }
}
