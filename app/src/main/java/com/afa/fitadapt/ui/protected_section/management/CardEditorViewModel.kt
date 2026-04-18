// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Editor Scheda (Area Protetta)
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import com.afa.fitadapt.data.repository.ExerciseRepository
import com.afa.fitadapt.data.repository.TrainingCardRepository
import com.afa.fitadapt.model.CardStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardEditorUiState(
    val title: String = "",
    val durationWeeks: String = "",
    val targetSessions: String = "",
    val status: String = CardStatus.PENDING.name,
    val autoAdvance: Boolean = true,
    val exercises: List<CardExerciseWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

data class CardExerciseWithDetails(
    val cardExercise: CardExerciseEntity,
    val exercise: ExerciseEntity
)

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val cardRepository: TrainingCardRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cardId: Long = savedStateHandle.get<Long>("cardId") ?: -1L
    
    private val _uiState = MutableStateFlow(CardEditorUiState())
    val uiState: StateFlow<CardEditorUiState> = _uiState.asStateFlow()

    init {
        if (cardId != -1L) {
            loadCard()
        }
    }

    private fun loadCard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            cardRepository.getCardWithExercises(cardId).collect { cardWithExercises ->
                if (cardWithExercises != null) {
                    val card = cardWithExercises.card
                    val exercisesWithDetails = mutableListOf<CardExerciseWithDetails>()
                    
                    for (ce in cardWithExercises.cardExercises) {
                        val exercise = exerciseRepository.getById(ce.exerciseId)
                        if (exercise != null) {
                            exercisesWithDetails.add(CardExerciseWithDetails(ce, exercise))
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        title = card.title,
                        durationWeeks = card.durationWeeks?.toString() ?: "",
                        targetSessions = card.targetSessions?.toString() ?: "",
                        status = card.status,
                        autoAdvance = card.autoAdvance,
                        exercises = exercisesWithDetails,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun onDurationChange(newDuration: String) {
        _uiState.value = _uiState.value.copy(durationWeeks = newDuration)
    }

    fun onSessionsChange(newSessions: String) {
        _uiState.value = _uiState.value.copy(targetSessions = newSessions)
    }

    fun onAutoAdvanceChange(newValue: Boolean) {
        _uiState.value = _uiState.value.copy(autoAdvance = newValue)
    }

    fun addExercise(exercise: ExerciseEntity) {
        val currentExercises = _uiState.value.exercises
        val nextOrder = (currentExercises.maxOfOrNull { it.cardExercise.orderIndex } ?: -1) + 1
        
        val newCardExercise = CardExerciseEntity(
            cardId = if (cardId != -1L) cardId else 0,
            exerciseId = exercise.id,
            orderIndex = nextOrder,
            customDurationSec = exercise.defaultDurationSec,
            customRepetitions = exercise.defaultRepetitions,
            customIntensity = exercise.defaultIntensity
        )
        
        _uiState.value = _uiState.value.copy(
            exercises = currentExercises + CardExerciseWithDetails(newCardExercise, exercise)
        )
    }

    fun removeExercise(index: Int) {
        val currentExercises = _uiState.value.exercises.toMutableList()
        if (index in currentExercises.indices) {
            currentExercises.removeAt(index)
            // Riordina
            val reordered = currentExercises.mapIndexed { i, item ->
                item.copy(cardExercise = item.cardExercise.copy(orderIndex = i))
            }
            _uiState.value = _uiState.value.copy(exercises = reordered)
        }
    }

    fun updateExerciseParams(index: Int, duration: Int?, reps: Int?, intensity: String?, notes: String?) {
        val currentExercises = _uiState.value.exercises.toMutableList()
        if (index in currentExercises.indices) {
            val item = currentExercises[index]
            currentExercises[index] = item.copy(
                cardExercise = item.cardExercise.copy(
                    customDurationSec = duration,
                    customRepetitions = reps,
                    customIntensity = intensity,
                    customNotes = notes
                )
            )
            _uiState.value = _uiState.value.copy(exercises = currentExercises)
        }
    }

    fun saveCard() {
        viewModelScope.launch {
            val state = _uiState.value
            val card = TrainingCardEntity(
                id = if (cardId != -1L) cardId else 0,
                title = state.title,
                durationWeeks = state.durationWeeks.toIntOrNull(),
                targetSessions = state.targetSessions.toIntOrNull(),
                status = state.status,
                autoAdvance = state.autoAdvance,
                orderIndex = if (cardId != -1L) 0 else 0 // TODO: gestire ordine globale
            )
            
            val exercises = state.exercises.map { it.cardExercise }
            
            if (cardId == -1L) {
                cardRepository.createCard(card, exercises)
            } else {
                cardRepository.updateCard(card)
                cardRepository.replaceCardExercises(cardId, exercises)
            }
            
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
