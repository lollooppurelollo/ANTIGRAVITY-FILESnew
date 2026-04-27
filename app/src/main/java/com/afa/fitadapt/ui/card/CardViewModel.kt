// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Scheda attiva e dettaglio esercizio
// =============================================================
package com.afa.fitadapt.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.CardWithExercises
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.repository.ExerciseRepository
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.data.repository.TrainingCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardUiState(
    val isLoading: Boolean = true,
    val cardWithExercises: CardWithExercises? = null,
    val exerciseDetails: Map<Long, ExerciseEntity> = emptyMap(),
    val completedSessionsCount: Int = 0,
    val noActiveCard: Boolean = false
)

data class ExerciseDetailUiState(
    val isLoading: Boolean = true,
    val exercise: ExerciseEntity? = null,
    val cardExercise: CardExerciseEntity? = null
)

/**
 * ViewModel per la scheda attiva e il dettaglio degli esercizi.
 */
@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: TrainingCardRepository,
    private val exerciseRepository: ExerciseRepository,
    private val sessionRepository: SessionRepository,
    private val userPreferences: com.afa.fitadapt.data.local.datastore.UserPreferences
) : ViewModel() {

    private val _cardState = MutableStateFlow(CardUiState())
    val cardState = _cardState.asStateFlow()

    private val _exerciseDetailState = MutableStateFlow(ExerciseDetailUiState())
    val exerciseDetailState = _exerciseDetailState.asStateFlow()

    val guidedTrainingMode = userPreferences.guidedTrainingMode

    fun toggleGuidedTrainingMode() {
        viewModelScope.launch {
            val current = userPreferences.guidedTrainingMode.first()
            userPreferences.setGuidedTrainingMode(!current)
        }
    }

    init {
        loadActiveCard()
    }

    fun loadActiveCard() {
        viewModelScope.launch {
            cardRepository.getActiveCardWithExercises().collect { cardWithEx ->
                if (cardWithEx == null) {
                    _cardState.update {
                        it.copy(isLoading = false, noActiveCard = true)
                    }
                } else {
                    // Carica i dettagli degli esercizi
                    val details = mutableMapOf<Long, ExerciseEntity>()
                    for (ce in cardWithEx.cardExercises) {
                        val exercise = exerciseRepository.getById(ce.exerciseId)
                        if (exercise != null) {
                            details[ce.exerciseId] = exercise
                        }
                    }

                    val completed = sessionRepository.countCompletedByCard(cardWithEx.card.id)

                    _cardState.update {
                        it.copy(
                            isLoading = false,
                            noActiveCard = false,
                            cardWithExercises = cardWithEx,
                            exerciseDetails = details,
                            completedSessionsCount = completed
                        )
                    }
                }
            }
        }
    }

    /** Carica il dettaglio di un singolo esercizio */
    fun loadExerciseDetail(exerciseId: Long, @Suppress("UNUSED_PARAMETER") cardExerciseId: Long = -1) {
        viewModelScope.launch {
            val exercise = exerciseRepository.getById(exerciseId)
            _exerciseDetailState.update {
                it.copy(
                    isLoading = false,
                    exercise = exercise
                )
            }
        }
    }
}
