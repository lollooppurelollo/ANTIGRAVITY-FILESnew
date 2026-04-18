// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Editor Esercizio
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.repository.ExerciseRepository
import com.afa.fitadapt.model.ExerciseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseEditorUiState(
    val id: Long = 0,
    val name: String = "",
    val category: String = ExerciseCategory.ALTRO.displayName,
    val description: String = "",
    val videoUri: String = "",
    val defaultDurationSec: String = "",
    val defaultRepetitions: String = "",
    val defaultIntensity: String = "moderata",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class ExerciseEditorViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: -1L
    private val _uiState = MutableStateFlow(ExerciseEditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (exerciseId != -1L) {
            loadExercise(exerciseId)
        }
    }

    private fun loadExercise(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            exerciseRepository.getById(id)?.let { ex ->
                _uiState.update { 
                    it.copy(
                        id = ex.id,
                        name = ex.name,
                        category = ex.category,
                        description = ex.description,
                        videoUri = ex.videoUri ?: "",
                        defaultDurationSec = ex.defaultDurationSec?.toString() ?: "",
                        defaultRepetitions = ex.defaultRepetitions?.toString() ?: "",
                        defaultIntensity = ex.defaultIntensity,
                        notes = ex.notes ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name) }
    fun updateCategory(cat: String) = _uiState.update { it.copy(category = cat) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateVideoUri(uri: String) = _uiState.update { it.copy(videoUri = uri) }
    fun updateDuration(dur: String) = _uiState.update { it.copy(defaultDurationSec = dur) }
    fun updateRepetitions(reps: String) = _uiState.update { it.copy(defaultRepetitions = reps) }
    fun updateIntensity(intensity: String) = _uiState.update { it.copy(defaultIntensity = intensity) }
    fun updateNotes(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun save() {
        val state = _uiState.value
        val exercise = ExerciseEntity(
            id = if (exerciseId == -1L) 0 else exerciseId,
            name = state.name,
            category = state.category,
            description = state.description,
            videoUri = state.videoUri.ifBlank { null },
            defaultDurationSec = state.defaultDurationSec.toIntOrNull(),
            defaultRepetitions = state.defaultRepetitions.toIntOrNull(),
            defaultIntensity = state.defaultIntensity,
            notes = state.notes.ifBlank { null }
        )

        viewModelScope.launch {
            if (exerciseId == -1L) {
                exerciseRepository.addExercise(exercise)
            } else {
                exerciseRepository.updateExercise(exercise)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
