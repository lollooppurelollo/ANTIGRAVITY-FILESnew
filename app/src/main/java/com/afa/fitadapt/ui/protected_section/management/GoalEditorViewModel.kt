// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Editor Obiettivo
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalEditorUiState(
    val goalId: Long? = null,
    val title: String = "",
    val targetType: String = "sessions_per_week",
    val bronzeValue: String = "",
    val silverValue: String = "",
    val goldValue: String = "",
    val periodType: String = "none",
    val customPeriodValue: String = "",
    val customPeriodUnit: String = "days",
    val parentGoalId: Long? = null,
    val isSaved: Boolean = false,
    val availableGoals: List<GoalEntity> = emptyList()
)

@HiltViewModel
class GoalEditorViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalEditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                _uiState.update { it.copy(availableGoals = goals) }
            }
        }
    }

    fun loadGoal(goalId: Long) {
        if (goalId == -1L) return
        viewModelScope.launch {
            goalRepository.getById(goalId)?.let { goal ->
                _uiState.update { it.copy(
                    goalId = goal.id,
                    title = goal.title,
                    targetType = goal.targetType,
                    bronzeValue = goal.targetValue.toInt().toString(),
                    silverValue = goal.silverValue?.toInt()?.toString() ?: "",
                    goldValue = goal.goldValue?.toInt()?.toString() ?: "",
                    periodType = goal.periodType,
                    customPeriodValue = goal.customPeriodValue?.toString() ?: "",
                    customPeriodUnit = goal.customPeriodUnit ?: "days",
                    parentGoalId = goal.parentGoalId
                ) }
            }
        }
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateTargetType(type: String) = _uiState.update { it.copy(targetType = type) }
    fun updateBronzeValue(value: String) = _uiState.update { it.copy(bronzeValue = value) }
    fun updateSilverValue(value: String) = _uiState.update { it.copy(silverValue = value) }
    fun updateGoldValue(value: String) = _uiState.update { it.copy(goldValue = value) }
    
    fun updatePeriodType(type: String) = _uiState.update { it.copy(periodType = type) }
    fun updateCustomPeriodValue(value: String) = _uiState.update { it.copy(customPeriodValue = value) }
    fun updateCustomPeriodUnit(unit: String) = _uiState.update { it.copy(customPeriodUnit = unit) }
    fun updateParentGoalId(id: Long?) = _uiState.update { it.copy(parentGoalId = id) }

    fun save() {
        val state = _uiState.value
        val goal = GoalEntity(
            id = state.goalId ?: 0,
            title = state.title,
            targetType = state.targetType,
            targetValue = state.bronzeValue.toFloatOrNull() ?: 0f,
            silverValue = state.silverValue.toFloatOrNull(),
            goldValue = state.goldValue.toFloatOrNull(),
            periodType = state.periodType,
            customPeriodValue = state.customPeriodValue.toIntOrNull(),
            customPeriodUnit = state.customPeriodUnit,
            parentGoalId = state.parentGoalId
        )
        viewModelScope.launch {
            if (state.goalId == null) {
                goalRepository.createGoal(goal)
            } else {
                goalRepository.updateGoal(goal)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
