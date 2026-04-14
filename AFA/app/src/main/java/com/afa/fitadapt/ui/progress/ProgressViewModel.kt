// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Progressi (placeholder per Tranche 4)
// =============================================================
package com.afa.fitadapt.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.GoalEntity
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
    val adherencePercent: Float = 0f,
    val totalMinutes: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val activeGoals: List<GoalEntity> = emptyList()
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProgress() }

    fun loadProgress() {
        viewModelScope.launch {
            progressRepository.completedSessionsCount().collect { completed ->
                _uiState.update { it.copy(completedSessions = completed) }
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
            progressRepository.getActiveGoals().collect { goals ->
                _uiState.update { it.copy(activeGoals = goals, isLoading = false) }
            }
        }
    }
}
