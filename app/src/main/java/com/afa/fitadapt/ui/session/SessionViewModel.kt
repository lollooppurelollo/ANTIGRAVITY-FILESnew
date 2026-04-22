// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Registrazione sessione di allenamento
// =============================================================
package com.afa.fitadapt.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.data.local.entity.SessionExerciseEntity
import com.afa.fitadapt.data.repository.ExerciseRepository
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.data.repository.TrainingCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fase della registrazione sessione.
 */
enum class SessionPhase {
    QUESTION,       // "Hai fatto l'allenamento?"
    DETAILS,        // Dettagli opzionali
    SUBMITTED       // Registrata con successo
}

data class SessionUiState(
    val phase: SessionPhase = SessionPhase.QUESTION,
    val activeCardId: Long? = null,
    val cardExercises: List<CardExerciseEntity> = emptyList(),
    val exerciseDetails: Map<Long, ExerciseEntity> = emptyMap(),

    // Risposta obbligatoria
    val completed: Boolean = false,
    val partial: Boolean = false,

    // Dettagli opzionali
    val durationMin: Int? = null,
    val perceivedEffort: Int? = null,
    val mood: Int? = null,
    val sleepQuality: Int? = null,
    val notes: String = "",

    // Checklist esercizi (per sessioni parziali)
    val exerciseChecklist: Map<Long, Boolean> = emptyMap(),

    val isSubmitting: Boolean = false,
    val noActiveCard: Boolean = false
)

/**
 * ViewModel per la registrazione delle sessioni di allenamento.
 *
 * Flusso:
 * 1. QUESTION: "Hai fatto l'allenamento oggi?" → Sì / No / Parziale
 * 2. DETAILS: se Sì/Parziale → compila dettagli opzionali
 * 3. SUBMITTED: salvata con successo
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val cardRepository: TrainingCardRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState = _uiState.asStateFlow()

    /** Flow con tutte le sessioni registrate */
    val allSessions = sessionRepository.getAllSessions()

    init {
        loadActiveCard()
    }

    private fun loadActiveCard() {
        viewModelScope.launch {
            cardRepository.getActiveCardWithExercises().collect { cardWithEx ->
                if (cardWithEx == null) {
                    _uiState.update { it.copy(noActiveCard = true) }
                } else {
                    // Carica dettagli esercizi per la checklist
                    val details = mutableMapOf<Long, ExerciseEntity>()
                    val checklist = mutableMapOf<Long, Boolean>()
                    for (ce in cardWithEx.cardExercises) {
                        val ex = exerciseRepository.getById(ce.exerciseId)
                        if (ex != null) details[ce.exerciseId] = ex
                        checklist[ce.id] = true // di default tutti selezionati
                    }
                    _uiState.update {
                        it.copy(
                            activeCardId = cardWithEx.card.id,
                            cardExercises = cardWithEx.cardExercises,
                            exerciseDetails = details,
                            exerciseChecklist = checklist
                        )
                    }
                }
            }
        }
    }

    // ── Risposta principale ──

    var onSessionTargetReached: (() -> Unit)? = null

    fun answerCompleted() {
        _uiState.update {
            it.copy(completed = true, partial = false, phase = SessionPhase.DETAILS)
        }
    }

    fun answerPartial() {
        _uiState.update {
            it.copy(completed = true, partial = true, phase = SessionPhase.DETAILS)
        }
    }

    fun answerNotCompleted() {
        // Sessione non svolta — salva direttamente con completed = false
        viewModelScope.launch {
            val cardId = _uiState.value.activeCardId ?: return@launch
            sessionRepository.registerSession(
                SessionEntity(
                    cardId = cardId,
                    date = System.currentTimeMillis(),
                    completed = false,
                    notes = _uiState.value.notes.ifBlank { null }
                )
            )
            _uiState.update { it.copy(phase = SessionPhase.SUBMITTED) }
        }
    }

    // ── Dettagli opzionali ──

    fun updateDuration(min: Int?) {
        _uiState.update { it.copy(durationMin = min) }
    }

    fun updateEffort(value: Int?) {
        _uiState.update { it.copy(perceivedEffort = value) }
    }

    fun updateMood(value: Int?) {
        _uiState.update { it.copy(mood = value) }
    }

    fun updateSleepQuality(value: Int?) {
        _uiState.update { it.copy(sleepQuality = value) }
    }

    fun updateNotes(text: String) {
        _uiState.update { it.copy(notes = text) }
    }

    fun toggleExercise(cardExerciseId: Long) {
        _uiState.update {
            val updated = it.exerciseChecklist.toMutableMap()
            updated[cardExerciseId] = !(updated[cardExerciseId] ?: true)
            it.copy(exerciseChecklist = updated)
        }
    }

    // ── Invio ──

    fun submitSession() {
        val state = _uiState.value
        val cardId = state.activeCardId ?: return

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val session = SessionEntity(
                cardId = cardId,
                date = System.currentTimeMillis(),
                completed = state.completed,
                partial = state.partial,
                actualDurationMin = state.durationMin,
                perceivedEffort = state.perceivedEffort,
                mood = state.mood,
                sleepQuality = state.sleepQuality,
                notes = state.notes.ifBlank { null }
            )

            // Per sessioni parziali, crea la lista di esercizi completati
            val exerciseCompletions = if (state.partial) {
                state.exerciseChecklist.map { (ceId, done) ->
                    SessionExerciseEntity(
                        sessionId = 0, // verrà impostato dal repository
                        cardExerciseId = ceId,
                        completed = done
                    )
                }
            } else {
                emptyList()
            }

            sessionRepository.registerSession(session, exerciseCompletions)

            // Controlla se abbiamo raggiunto il target della scheda
            val activeCard = cardRepository.getById(cardId)
            if (activeCard != null && activeCard.targetSessions != null) {
                val sessionsCount = sessionRepository.countCompletedByCard(activeCard.id)
                if (sessionsCount >= activeCard.targetSessions) {
                    onSessionTargetReached?.invoke()
                }
            }

            _uiState.update {
                it.copy(isSubmitting = false, phase = SessionPhase.SUBMITTED)
            }
        }
    }

    /** Carica una sessione specifica per il dettaglio */
    fun getSessionDetail(sessionId: Long) = sessionRepository.getSessionWithExercises(sessionId)

    /** Elimina una sessione */
    fun deleteSession(session: SessionEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
            onDeleted()
        }
    }

    @Suppress("unused") // Chiamata da AfaNavGraph quando l'utente torna alla SessionScreen
    fun reset() {
        _uiState.update {
            it.copy(
                phase = SessionPhase.QUESTION,
                completed = false,
                partial = false,
                durationMin = null,
                perceivedEffort = null,
                mood = null,
                sleepQuality = null,
                notes = "",
                isSubmitting = false
            )
        }
    }
}
