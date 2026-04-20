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
    val title: String = "",
    val targetType: String = "sessions_per_week",
    val bronzeValue: String = "",
    val silverValue: String = "",
    val goldValue: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class GoalEditorViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalEditorUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateTargetType(type: String) = _uiState.update { it.copy(targetType = type) }
    fun updateBronzeValue(value: String) = _uiState.update { it.copy(bronzeValue = value) }
    fun updateSilverValue(value: String) = _uiState.update { it.copy(silverValue = value) }
    fun updateGoldValue(value: String) = _uiState.update { it.copy(goldValue = value) }

    fun save() {
        val state = _uiState.value
        val goal = GoalEntity(
            title = state.title,
            targetType = state.targetType,
            targetValue = state.bronzeValue.toFloatOrNull() ?: 0f,
            silverValue = state.silverValue.toFloatOrNull(),
            goldValue = state.goldValue.toFloatOrNull()
        )
        viewModelScope.launch {
            goalRepository.createGoal(goal)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
