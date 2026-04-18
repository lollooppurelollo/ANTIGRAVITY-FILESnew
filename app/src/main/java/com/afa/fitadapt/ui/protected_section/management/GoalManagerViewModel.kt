// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Gestione Obiettivi
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalManagerUiState(
    val goals: List<GoalEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class GoalManagerViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    val uiState: StateFlow<GoalManagerUiState> = goalRepository.getAllGoals()
        .map { goals -> GoalManagerUiState(goals = goals) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalManagerUiState(isLoading = true))

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
        }
    }

    fun toggleGoalActive(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.updateGoal(goal.copy(isActive = !goal.isActive))
        }
    }
}
