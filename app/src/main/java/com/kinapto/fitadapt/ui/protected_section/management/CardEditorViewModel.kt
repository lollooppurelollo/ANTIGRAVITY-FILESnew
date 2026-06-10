// =============================================================
// KinApto - Attività Fisica Adattata
// ViewModel: Editor Scheda (Area Protetta)
// =============================================================
package com.kinapto.fitadapt.ui.protected_section.management

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity
import com.kinapto.fitadapt.data.local.entity.CardExerciseEntity
import com.kinapto.fitadapt.data.local.entity.ExerciseEntity
import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import com.kinapto.fitadapt.data.repository.ExerciseRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.domain.AdaptationDelta
import com.kinapto.fitadapt.model.CardStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

data class CardEditorUiState(
    val title: String = "",
    val durationWeeks: String = "",
    val targetSessions: String = "",
    val targetSessionsPerWeek: String = "",
    val status: String = CardStatus.PENDING.name,
    val autoAdvance: Boolean = true,
    val adaptationBiometricType: String? = null,
    val adaptationThreshold: String = "",
    val adaptationWindowDays: String = "15",
    val adaptationConsecutiveMisses: String = "",
    val adaptationMinDifficulty: String = "",
    val adaptationAction: String? = null,
    val exercises: List<CardExerciseWithDetails> = emptyList(),
    val rules: List<AdaptationRuleEntity> = emptyList(),
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
                        exercisesWithDetails.add(CardExerciseWithDetails(ce.cardExercise, ce.exercise))
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        title = card.title,
                        durationWeeks = card.durationWeeks?.toString() ?: "",
                        targetSessions = card.targetSessions?.toString() ?: "",
                        targetSessionsPerWeek = card.targetSessionsPerWeek?.toString() ?: "",
                        status = card.status,
                        autoAdvance = card.autoAdvance,
                        adaptationBiometricType = card.adaptationBiometricType,
                        adaptationThreshold = card.adaptationThreshold?.toString() ?: "",
                        adaptationWindowDays = card.adaptationWindowDays?.toString() ?: "15",
                        adaptationConsecutiveMisses = card.adaptationConsecutiveMisses?.toString() ?: "",
                        adaptationMinDifficulty = card.adaptationMinDifficulty?.toString() ?: "",
                        adaptationAction = card.adaptationAction,
                        exercises = exercisesWithDetails,
                        rules = cardRepository.getRulesForCardSync(cardId),
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

    fun onSessionsPerWeekChange(newSessions: String) {
        _uiState.value = _uiState.value.copy(targetSessionsPerWeek = newSessions)
    }

    fun onAutoAdvanceChange(newValue: Boolean) {
        _uiState.value = _uiState.value.copy(autoAdvance = newValue)
    }

    fun onAdaptationBiometricTypeChange(type: String?) {
        _uiState.value = _uiState.value.copy(adaptationBiometricType = type)
    }

    fun onAdaptationThresholdChange(threshold: String) {
        _uiState.value = _uiState.value.copy(adaptationThreshold = threshold)
    }

    fun onAdaptationWindowDaysChange(days: String) {
        _uiState.value = _uiState.value.copy(adaptationWindowDays = days)
    }

    fun onAdaptationConsecutiveMissesChange(value: String) {
        _uiState.value = _uiState.value.copy(adaptationConsecutiveMisses = value)
    }

    fun onAdaptationMinDifficultyChange(value: String) {
        _uiState.value = _uiState.value.copy(adaptationMinDifficulty = value)
    }

    fun onAdaptationActionChange(action: String?) {
        _uiState.value = _uiState.value.copy(adaptationAction = action)
    }

    // --- Dynamic Rules Management ---

    fun addRule(groupId: String? = null) {
        val newGroupId = groupId ?: java.util.UUID.randomUUID().toString()
        val newRule = AdaptationRuleEntity(
            cardId = cardId,
            groupId = newGroupId,
            triggerType = "BIOMETRIC",
            parameter = "PAIN",
            operator = "GT",
            threshold = 5f,
            windowDays = 15,
            minOccurrences = 3,
            useAverage = true,
            actionType = "DELTA",
            actionValue = Json.encodeToString(AdaptationDelta(reps = -1, intensity = -1))
        )
        _uiState.value = _uiState.value.copy(rules = _uiState.value.rules + newRule)
    }

    fun updateRuleActionDelta(index: Int, reps: Int? = null, intensity: Int? = null, durationPercent: Float? = null) {
        val rules = _uiState.value.rules.toMutableList()
        if (index !in rules.indices) return
        val rule = rules[index]
        
        val currentDelta = try {
            Json.decodeFromString<AdaptationDelta>(rule.actionValue)
        } catch (e: Exception) {
            AdaptationDelta()
        }

        val newDelta = currentDelta.copy(
            reps = reps ?: currentDelta.reps,
            intensity = intensity ?: currentDelta.intensity,
            durationPercent = durationPercent ?: currentDelta.durationPercent
        )
        
        rules[index] = rule.copy(actionValue = Json.encodeToString(newDelta))
        _uiState.value = _uiState.value.copy(rules = rules)
    }

    fun updateRuleTrigger(
        index: Int,
        type: String? = null,
        param: String? = null,
        windowDays: Int? = null,
        minOccurrences: Int? = null,
        requireConsecutive: Boolean? = null,
        useAverage: Boolean? = null,
        operator: String? = null,
        threshold: Float? = null
    ) {
        val rules = _uiState.value.rules.toMutableList()
        if (index !in rules.indices) return

        val currentRule = rules[index]
        val updatedRule = currentRule.copy(
            triggerType = type ?: currentRule.triggerType,
            parameter = param ?: if (type != null && type != currentRule.triggerType) {
                when (type) {
                    "BIOMETRIC" -> "PAIN"
                    "COMPLETION" -> "MISSED_SESSIONS"
                    "FAILURE_REASON" -> "TOO_FATIGUING"
                    else -> null
                }
            } else currentRule.parameter,
            windowDays = windowDays ?: currentRule.windowDays,
            minOccurrences = minOccurrences ?: currentRule.minOccurrences,
            requireConsecutive = requireConsecutive ?: currentRule.requireConsecutive,
            useAverage = useAverage ?: currentRule.useAverage,
            operator = operator ?: currentRule.operator,
            threshold = threshold ?: currentRule.threshold
        )
        rules[index] = updatedRule
        _uiState.value = _uiState.value.copy(rules = rules)
    }
    
    fun updateRuleAtIndex(index: Int, updatedRule: AdaptationRuleEntity) {
        val newRules = _uiState.value.rules.toMutableList()
        if (index in newRules.indices) {
            newRules[index] = updatedRule
            _uiState.value = _uiState.value.copy(rules = newRules)
        }
    }

    fun removeRule(index: Int) {
        val newRules = _uiState.value.rules.toMutableList()
        if (index in newRules.indices) {
            newRules.removeAt(index)
            _uiState.value = _uiState.value.copy(rules = newRules)
        }
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
            val allCards = cardRepository.getAllCards().first()
            val nextOrderIndex = if (cardId == -1L) {
                (allCards.maxOfOrNull { it.orderIndex } ?: -1) + 1
            } else {
                allCards.find { it.id == cardId }?.orderIndex ?: 0
            }

            val card = TrainingCardEntity(
                id = if (cardId != -1L) cardId else 0,
                title = state.title,
                durationWeeks = state.durationWeeks.toIntOrNull(),
                targetSessions = state.targetSessions.toIntOrNull(),
                targetSessionsPerWeek = state.targetSessionsPerWeek.toIntOrNull(),
                status = state.status,
                autoAdvance = state.autoAdvance,
                adaptationBiometricType = state.adaptationBiometricType,
                adaptationThreshold = state.adaptationThreshold.toFloatOrNull(),
                adaptationWindowDays = state.adaptationWindowDays.toIntOrNull(),
                adaptationConsecutiveMisses = state.adaptationConsecutiveMisses.toIntOrNull(),
                adaptationMinDifficulty = state.adaptationMinDifficulty.toIntOrNull(),
                adaptationAction = state.adaptationAction,
                orderIndex = nextOrderIndex
            )
            
            val exercises = state.exercises.map { it.cardExercise }
            val rules = state.rules

            if (cardId == -1L) {
                val newId = cardRepository.createCard(card, exercises)
                cardRepository.replaceRulesForCard(newId, rules.map { it.copy(cardId = newId) })
            } else {
                cardRepository.updateCard(card)
                cardRepository.replaceCardExercises(cardId, exercises)
                cardRepository.replaceRulesForCard(cardId, rules)
            }
            
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
