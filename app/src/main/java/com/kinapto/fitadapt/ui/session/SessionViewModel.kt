// =============================================================
// KinApto - Attività Fisica Adattata
// ViewModel: Registrazione sessione di allenamento
// =============================================================
package com.kinapto.fitadapt.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.data.local.entity.CardExerciseEntity
import com.kinapto.fitadapt.data.local.entity.ExerciseEntity
import com.kinapto.fitadapt.data.local.entity.SessionEntity
import com.kinapto.fitadapt.data.local.entity.SessionExerciseEntity
import com.kinapto.fitadapt.data.repository.ExerciseRepository
import com.kinapto.fitadapt.data.repository.SessionRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
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
    val cardExercises: List<com.kinapto.fitadapt.data.local.entity.CardExerciseWithExercise> = emptyList(),
    val exerciseDetails: Map<Long, ExerciseEntity> = emptyMap(),

    // Risposta obbligatoria
    val completed: Boolean = false,
    val partial: Boolean = false,

    // Dettagli opzionali
    val durationMin: Int = 0,
    val perceivedEffort: Int = 0,
    val asthenia: Int = 0,
    val osteoarticularPain: Int = 0,
    val restDyspnea: Int = 0,
    val exertionDyspnea: Int = 0,
    val mood: Int = 5,
    val sleepQuality: Int = 5,
    val nausea: Int = 0,
    val appetite: Int = 5,
    val anxiety: Int = 0,
    val lymphoedema: Int = 0,
    val qualityOfLife: Int = 5,
    val wellBeing: Int = 5,
    val spo2: String = "",
    val heartRate: String = "",
    val notes: String = "",

    // Checklist esercizi (per sessioni parziali)
    val exerciseChecklist: Map<Long, Boolean> = emptyMap(),

    val selectedDate: Long = System.currentTimeMillis(),
    val isSubmitting: Boolean = false,
    val noActiveCard: Boolean = false,
    val touchedFields: Set<String> = emptySet()
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
                        details[ce.cardExercise.exerciseId] = ce.exercise
                        checklist[ce.cardExercise.id] = true // di default tutti selezionati
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
                    date = _uiState.value.selectedDate,
                    completed = false,
                    notes = _uiState.value.notes.ifBlank { null }
                )
            )
            _uiState.update { it.copy(phase = SessionPhase.SUBMITTED) }
        }
    }

    // ── Dettagli opzionali ──

    fun touchField(fieldName: String) {
        _uiState.update { it.copy(touchedFields = it.touchedFields + fieldName) }
    }

    fun updateDuration(min: Int) {
        _uiState.update { it.copy(durationMin = min, touchedFields = it.touchedFields + "durationMin") }
    }

    fun updateEffort(value: Int) {
        _uiState.update { it.copy(perceivedEffort = value, touchedFields = it.touchedFields + "perceivedEffort") }
    }

    fun updateAsthenia(value: Int) {
        _uiState.update { it.copy(asthenia = value, touchedFields = it.touchedFields + "asthenia") }
    }

    fun updatePain(value: Int) {
        _uiState.update { it.copy(osteoarticularPain = value, touchedFields = it.touchedFields + "osteoarticularPain") }
    }

    fun updateRestDyspnea(value: Int) {
        _uiState.update { it.copy(restDyspnea = value, touchedFields = it.touchedFields + "restDyspnea") }
    }

    fun updateExertionDyspnea(value: Int) {
        _uiState.update { it.copy(exertionDyspnea = value, touchedFields = it.touchedFields + "exertionDyspnea") }
    }

    fun updateMood(value: Int) {
        _uiState.update { it.copy(mood = value, touchedFields = it.touchedFields + "mood") }
    }

    fun updateSleepQuality(value: Int) {
        _uiState.update { it.copy(sleepQuality = value, touchedFields = it.touchedFields + "sleepQuality") }
    }

    fun updateNausea(value: Int) {
        _uiState.update { it.copy(nausea = value, touchedFields = it.touchedFields + "nausea") }
    }

    fun updateAppetite(value: Int) {
        _uiState.update { it.copy(appetite = value, touchedFields = it.touchedFields + "appetite") }
    }

    fun updateAnxiety(value: Int) {
        _uiState.update { it.copy(anxiety = value, touchedFields = it.touchedFields + "anxiety") }
    }

    fun updateLymphoedema(value: Int) {
        _uiState.update { it.copy(lymphoedema = value, touchedFields = it.touchedFields + "lymphoedema") }
    }

    fun updateQualityOfLife(value: Int) {
        _uiState.update { it.copy(qualityOfLife = value, touchedFields = it.touchedFields + "qualityOfLife") }
    }

    fun updateWellBeing(value: Int) {
        _uiState.update { it.copy(wellBeing = value, touchedFields = it.touchedFields + "wellBeing") }
    }

    fun updateSpo2(value: String) {
        _uiState.update { it.copy(spo2 = value) }
    }

    fun updateHeartRate(value: String) {
        _uiState.update { it.copy(heartRate = value) }
    }

    fun updateNotes(text: String) {
        _uiState.update { it.copy(notes = text) }
    }

    fun updateDate(timestamp: Long) {
        _uiState.update { it.copy(selectedDate = timestamp) }
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
                date = state.selectedDate,
                completed = state.completed,
                partial = state.partial,
                actualDurationMin = state.durationMin.takeIf { "durationMin" in state.touchedFields },
                perceivedEffort = state.perceivedEffort.takeIf { "perceivedEffort" in state.touchedFields },
                asthenia = state.asthenia.takeIf { "asthenia" in state.touchedFields },
                osteoarticularPain = state.osteoarticularPain.takeIf { "osteoarticularPain" in state.touchedFields },
                restDyspnea = state.restDyspnea.takeIf { "restDyspnea" in state.touchedFields },
                exertionDyspnea = state.exertionDyspnea.takeIf { "exertionDyspnea" in state.touchedFields },
                mood = state.mood.takeIf { "mood" in state.touchedFields },
                sleepQuality = state.sleepQuality.takeIf { "sleepQuality" in state.touchedFields },
                nausea = state.nausea.takeIf { "nausea" in state.touchedFields },
                appetite = state.appetite.takeIf { "appetite" in state.touchedFields },
                anxiety = state.anxiety.takeIf { "anxiety" in state.touchedFields },
                lymphoedema = state.lymphoedema.takeIf { "lymphoedema" in state.touchedFields },
                qualityOfLife = state.qualityOfLife.takeIf { "qualityOfLife" in state.touchedFields },
                wellBeing = state.wellBeing.takeIf { "wellBeing" in state.touchedFields },
                spo2 = state.spo2.toIntOrNull(),
                heartRate = state.heartRate.toIntOrNull(),
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

            // Gli obiettivi verranno aggiornati automaticamente al prossimo avvio della Home
            // o tramite il flow di Room se osservati correttamente.

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

    @Suppress("unused") // Chiamata da KinAptoNavGraph quando l'utente torna alla SessionScreen
    fun reset() {
        _uiState.update {
            it.copy(
                phase = SessionPhase.QUESTION,
                completed = false,
                partial = false,
                durationMin = 0,
                perceivedEffort = 0,
                asthenia = 0,
                osteoarticularPain = 0,
                restDyspnea = 0,
                exertionDyspnea = 0,
                mood = 5,
                sleepQuality = 5,
                nausea = 0,
                appetite = 5,
                anxiety = 0,
                lymphoedema = 0,
                qualityOfLife = 5,
                wellBeing = 5,
                spo2 = "",
                heartRate = "",
                notes = "",
                selectedDate = System.currentTimeMillis(),
                isSubmitting = false,
                touchedFields = emptySet()
            )
        }
    }
}
