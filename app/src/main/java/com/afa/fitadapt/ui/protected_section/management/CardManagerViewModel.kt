// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Gestione Schede
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import com.afa.fitadapt.data.repository.TrainingCardRepository
import com.afa.fitadapt.model.CardStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardManagerUiState(
    val cards: List<TrainingCardEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CardManagerViewModel @Inject constructor(
    private val cardRepository: TrainingCardRepository
) : ViewModel() {

    val uiState: StateFlow<CardManagerUiState> = cardRepository.getAllCards()
        .map { cards -> CardManagerUiState(cards = cards) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CardManagerUiState(isLoading = true))

    fun deleteCard(card: TrainingCardEntity) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }

    fun activateCard(id: Long) {
        viewModelScope.launch {
            cardRepository.activateCard(id)
        }
    }

    fun duplicateCard(card: TrainingCardEntity) {
        viewModelScope.launch {
            // Otteniamo la scheda completa con i suoi esercizi
            cardRepository.getCardWithExercises(card.id).collect { cardWithExercises ->
                if (cardWithExercises != null) {
                    val newCard = card.copy(
                        id = 0,
                        title = "${card.title} (Copia)",
                        status = CardStatus.PENDING.name,
                        startDate = null,
                        endDate = null
                    )
                    
                    val newExercises = cardWithExercises.cardExercises.map { 
                        it.copy(id = 0, cardId = 0) // cardId verrà impostato dal repository
                    }
                    
                    cardRepository.createCard(newCard, newExercises)
                }
            }
        }
    }
}
