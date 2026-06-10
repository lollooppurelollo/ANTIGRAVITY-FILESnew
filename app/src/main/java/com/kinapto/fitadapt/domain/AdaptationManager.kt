// =============================================================
// KinApto - Attività Fisica Adattata
// Domain: Gestore Adattamento Bio-feedback
// =============================================================
package com.kinapto.fitadapt.domain

import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import com.kinapto.fitadapt.data.repository.ScaleEntryRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.notification.NotificationHelper
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Serializable
data class AdaptationDelta(
    val reps: Int = 0,
    val intensity: Int = 0,
    val durationPercent: Float = 0f,
    // Permette adattamenti specifici per categoria (es. "AEROBICO": { "reps": -5 })
    val perCategory: Map<String, AdaptationDelta>? = null
)

@Singleton
class AdaptationManager @Inject constructor(
    private val cardRepository: TrainingCardRepository,
    private val scaleRepository: ScaleEntryRepository,
    private val sessionRepositoryProvider: Provider<com.kinapto.fitadapt.data.repository.SessionRepository>,
    private val notificationHelper: NotificationHelper,
    private val adaptationLogDao: com.kinapto.fitadapt.data.local.dao.AdaptationLogDao
) {
    private val sessionRepository get() = sessionRepositoryProvider.get()
    
    // Parser configurato per la resilienza
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    /**
     * Verifica se la scheda attiva necessita di un adattamento basato sulle regole cliniche dinamiche.
     */
    suspend fun checkAndApplyAdaptation(card: TrainingCardEntity) {
        if (card.isAdapted) return

        val rules = cardRepository.getRulesForCardSync(card.id)
        if (rules.isEmpty()) {
            // Fallback alle regole base se non ci sono regole dinamiche impostate dal medico
            checkLegacyRules(card)
            return
        }

        // Gruppi di regole (OR): basta che un gruppo sia soddisfatto
        val ruleGroups = rules.groupBy { it.groupId }

        for ((_, groupRules) in ruleGroups) {
            // Logica AND: tutte le regole del gruppo devono essere vere
            var groupSatisfied = true
            val triggeredDetails = mutableListOf<String>()

            for (rule in groupRules) {
                val result = evaluateRule(rule, card)
                if (!result.isSatisfied) {
                    groupSatisfied = false
                    break
                }
                triggeredDetails.add(result.description)
            }

            if (groupSatisfied) {
                val firstRule = groupRules.first()
                applyAction(
                    card = card,
                    actionType = firstRule.actionType,
                    actionValue = firstRule.actionValue,
                    reasonDescription = triggeredDetails.joinToString(" AND "),
                    notificationMsg = firstRule.notificationMessage
                )
                return // Esci dopo il primo adattamento riuscito
            }
        }
    }

    private data class RuleResult(val isSatisfied: Boolean, val description: String)

    private suspend fun evaluateRule(rule: com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity, card: TrainingCardEntity): RuleResult {
        val sinceDate = if (rule.windowDays > 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -rule.windowDays)
            // Arrotondiamo all'inizio del giorno per coerenza
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        } else 0L

        return when (rule.triggerType) {
            "SYMPTOM", "BIOMETRIC" -> evaluateBiometricRule(rule, sinceDate)
            "COMPLETION" -> evaluateCompletionRule(rule, card.id, sinceDate)
            "FAILURE_REASON" -> evaluateFailureReasonRule(rule, card.id, sinceDate)
            else -> RuleResult(false, "Unknown trigger")
        }
    }

    private suspend fun evaluateBiometricRule(rule: com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity, sinceDate: Long): RuleResult {
        val param = rule.parameter ?: return RuleResult(false, "Missing parameter")
        
        val value = if (rule.useAverage && sinceDate > 0) {
            when (param) {
                "PAIN" -> scaleRepository.getAveragePainSince(sinceDate)
                "ASTHENIA" -> scaleRepository.getAverageAstheniaSince(sinceDate)
                "REST_DYSPNEA" -> scaleRepository.getAverageRestDyspneaSince(sinceDate)
                "EXERTION_DYSPNEA" -> scaleRepository.getAverageExertionDyspneaSince(sinceDate)
                "NAUSEA" -> scaleRepository.getAverageNauseaSince(sinceDate)
                "APPETITE" -> scaleRepository.getAverageAppetiteSince(sinceDate)
                "ANXIETY" -> scaleRepository.getAverageAnxietySince(sinceDate)
                "MOOD" -> scaleRepository.getAverageMoodSince(sinceDate)
                "SLEEP" -> scaleRepository.getAverageSleepQualitySince(sinceDate)
                "EFFORT" -> scaleRepository.getAverageEffortSince(sinceDate)
                "LYMPHOEDEMA" -> scaleRepository.getAverageLymphoedemaSince(sinceDate)
                "QOL" -> scaleRepository.getAverageQolSince(sinceDate)
                "WELLBEING" -> scaleRepository.getAverageWellBeingSince(sinceDate)
                "SPO2" -> scaleRepository.getAverageSpo2Since(sinceDate)
                "HEART_RATE" -> scaleRepository.getAverageHeartRateSince(sinceDate)
                else -> null
            }
        } else {
            // Recupero dell'ultimo valore registrato con mapping corretto all'Entity
            scaleRepository.getLatestEntry()?.let { entry ->
                when (param) {
                    "PAIN" -> entry.osteoarticularPain?.toFloat()
                    "ASTHENIA" -> entry.asthenia?.toFloat()
                    "REST_DYSPNEA" -> entry.restDyspnea?.toFloat()
                    "EXERTION_DYSPNEA" -> entry.exertionDyspnea?.toFloat()
                    "NAUSEA" -> entry.nausea?.toFloat()
                    "APPETITE" -> entry.appetite?.toFloat()
                    "ANXIETY" -> entry.anxiety?.toFloat()
                    "MOOD" -> entry.mood?.toFloat()
                    "SLEEP" -> entry.sleepQuality?.toFloat()
                    "EFFORT" -> entry.perceivedEffort?.toFloat()
                    "LYMPHOEDEMA" -> entry.lymphoedema?.toFloat()
                    "QOL" -> entry.qualityOfLife?.toFloat()
                    "WELLBEING" -> entry.wellBeing?.toFloat()
                    "SPO2" -> entry.spo2?.toFloat()
                    "HEART_RATE" -> entry.heartRate?.toFloat()
                    else -> null
                }
            }
        } ?: return RuleResult(false, "Nessun dato per $param")

        val satisfied = when (rule.operator) {
            "GT" -> value > rule.threshold
            "LT" -> value < rule.threshold
            "EQ" -> kotlin.math.abs(value - rule.threshold) < 0.1f
            else -> false
        }
        
        return RuleResult(satisfied, "$param (${String.format(java.util.Locale.US, "%.1f", value)}) ${rule.operator} ${rule.threshold}")
    }

    private suspend fun evaluateCompletionRule(rule: com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity, cardId: Long, sinceDate: Long): RuleResult {
        // Se c'è una finestra temporale, prendiamo tutte le sessioni nel periodo (fino a un limite ragionevole di 100)
        // Se non c'è finestra, prendiamo esattamente minOccurrences
        val fetchLimit = if (rule.windowDays > 0) 100 else rule.minOccurrences
        val statuses = sessionRepository.getCompletionStatuses(cardId, fetchLimit, sinceDate)
        
        if (statuses.size < rule.minOccurrences) return RuleResult(false, "Dati insufficienti nel periodo")

        val satisfied = if (rule.requireConsecutive) {
            // Cerca una sequenza di minOccurrences "false" (missed)
            var consecutiveCount = 0
            var found = false
            for (status in statuses) {
                if (!status) {
                    consecutiveCount++
                    if (consecutiveCount >= rule.minOccurrences) {
                        found = true
                        break
                    }
                } else {
                    consecutiveCount = 0
                }
            }
            found
        } else {
            val missedCount = statuses.count { !it }
            missedCount >= rule.minOccurrences
        }

        val totalMissed = statuses.count { !it }
        return RuleResult(satisfied, "Rilevate $totalMissed sessioni saltate")
    }

    private suspend fun evaluateFailureReasonRule(rule: com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity, cardId: Long, sinceDate: Long): RuleResult {
        val fetchLimit = if (rule.windowDays > 0) 100 else rule.minOccurrences
        val reasons = sessionRepository.getFailureReasons(cardId, fetchLimit, sinceDate)
        val targetReason = rule.parameter // es. "TOO_FATIGUING"
        
        if (reasons.size < rule.minOccurrences) return RuleResult(false, "Dati insufficienti nel periodo")

        val satisfied = if (rule.requireConsecutive) {
            var consecutiveCount = 0
            var found = false
            for (reason in reasons) {
                if (reason == targetReason) {
                    consecutiveCount++
                    if (consecutiveCount >= rule.minOccurrences) {
                        found = true
                        break
                    }
                } else {
                    consecutiveCount = 0
                }
            }
            found
        } else {
            val count = reasons.count { it == targetReason }
            count >= rule.minOccurrences
        }

        val totalOccurrences = reasons.count { it == targetReason }
        return RuleResult(satisfied, "Motivo '$targetReason' rilevato $totalOccurrences volte")
    }

    private suspend fun applyAction(
        card: TrainingCardEntity,
        actionType: String,
        actionValue: String,
        reasonDescription: String,
        notificationMsg: String?
    ) {
        var actionTakenMsg = ""
        var newCardId: Long? = null

        when (actionType) {
            "SWITCH" -> {
                val targetCardId = actionValue.toLongOrNull()
                if (targetCardId != null) {
                    cardRepository.completeCard(card.id)
                    cardRepository.activateCard(targetCardId)
                    actionTakenMsg = "Switched to card ID $targetCardId"
                    newCardId = targetCardId
                }
            }
            "DELTA" -> {
                val delta = try {
                    json.decodeFromString<AdaptationDelta>(actionValue)
                } catch (e: Exception) {
                    // Fallback di sicurezza se il JSON è malformato
                    AdaptationDelta(reps = -2, intensity = -1)
                }

                newCardId = cardRepository.duplicateAndAdaptCard(
                    cardId = card.id,
                    adaptationDescription = reasonDescription,
                    globalRepsDelta = delta.reps,
                    globalIntensityStep = delta.intensity,
                    globalDurationDeltaPercent = delta.durationPercent,
                    categoryDeltas = delta.perCategory
                )
                cardRepository.completeCard(card.id)
                cardRepository.activateCard(newCardId)
                
                val details = mutableListOf<String>()
                if (delta.reps != 0) details.add("Reps ${if (delta.reps > 0) "+" else ""}${delta.reps}")
                if (delta.intensity != 0) details.add("Intensità ${if (delta.intensity > 0) "+" else ""}${delta.intensity}")
                if (delta.durationPercent != 0f) details.add("Durata ${if (delta.durationPercent > 0) "+" else ""}${(delta.durationPercent * 100).toInt()}%")
                
                actionTakenMsg = "Adattamento parametrico applicato: ${details.joinToString(", ")}"
            }
        }

        // Registra nel log per CRF
        adaptationLogDao.insert(
            com.kinapto.fitadapt.data.local.entity.AdaptationLogEntity(
                originalCardId = card.id,
                newCardId = newCardId,
                triggerDescription = reasonDescription,
                actionTaken = actionTakenMsg
            )
        )

        // Notifica
        notificationHelper.showAdaptationNotification(
            "Allenamento ottimizzato ✨",
            notificationMsg ?: "In base ai tuoi progressi, abbiamo aggiornato il tuo piano."
        )
    }

    /**
     * Mantiene compatibilità con le vecchie soglie hardcoded nel TrainingCardEntity
     */
    private suspend fun checkLegacyRules(card: TrainingCardEntity) {
        val action = card.adaptationAction ?: return
        if (checkBiometricTrigger(card) || checkDifficultyTrigger(card) || checkMissesTrigger(card)) {
            applyAction(card, if (action == "SWITCH_CARD") "SWITCH" else "DELTA", "", "Legacy trigger", null)
        }
    }

    private suspend fun checkBiometricTrigger(card: TrainingCardEntity): Boolean {
        val biometricType = card.adaptationBiometricType ?: return false
        val threshold = card.adaptationThreshold ?: return false
        val windowDays = card.adaptationWindowDays ?: 15

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -windowDays)
        val sinceDate = calendar.timeInMillis

        val averageValue = when (biometricType) {
            "PAIN" -> scaleRepository.getAveragePainSince(sinceDate)
            "ASTHENIA" -> scaleRepository.getAverageAstheniaSince(sinceDate)
            "REST_DYSPNEA" -> scaleRepository.getAverageRestDyspneaSince(sinceDate)
            "EXERTION_DYSPNEA" -> scaleRepository.getAverageExertionDyspneaSince(sinceDate)
            "NAUSEA" -> scaleRepository.getAverageNauseaSince(sinceDate)
            "APPETITE" -> scaleRepository.getAverageAppetiteSince(sinceDate)
            "ANXIETY" -> scaleRepository.getAverageAnxietySince(sinceDate)
            "MOOD" -> scaleRepository.getAverageMoodSince(sinceDate)
            "SLEEP" -> scaleRepository.getAverageSleepQualitySince(sinceDate)
            "EFFORT" -> scaleRepository.getAverageEffortSince(sinceDate)
            "LYMPHOEDEMA" -> scaleRepository.getAverageLymphoedemaSince(sinceDate)
            "QOL" -> scaleRepository.getAverageQolSince(sinceDate)
            "WELLBEING" -> scaleRepository.getAverageWellBeingSince(sinceDate)
            "SPO2" -> scaleRepository.getAverageSpo2Since(sinceDate)
            "HEART_RATE" -> scaleRepository.getAverageHeartRateSince(sinceDate)
            else -> null
        } ?: return false

        // Per i parametri "positivi", l'adattamento scatta se il valore è BASSO.
        // Per i sintomi o parametri fisiologici critici, scatta se il valore è FUORI SOGLIA.
        val isInverseTrigger = biometricType in listOf("APPETITE", "MOOD", "SLEEP", "QOL", "WELLBEING", "SPO2")
        
        return if (isInverseTrigger) {
            averageValue < threshold
        } else {
            averageValue > threshold
        }
    }

    private suspend fun checkDifficultyTrigger(card: TrainingCardEntity): Boolean {
        val minDifficulty = card.adaptationMinDifficulty ?: return false
        
        // Controlliamo la media dello sforzo percepito nelle sessioni della scheda attiva negli ultimi 7 giorni
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sinceDate = calendar.timeInMillis
        
        // Usiamo un metodo delegato al repository che interroga il SessionDao
        val avgEffort = sessionRepository.getAverageEffortSince(card.id, sinceDate)
        
        return avgEffort != null && avgEffort >= minDifficulty
    }

    private suspend fun checkMissesTrigger(card: TrainingCardEntity): Boolean {
        val consecutiveMisses = card.adaptationConsecutiveMisses ?: return false
        if (consecutiveMisses <= 0) return false

        // Ottieni lo stato di completamento delle ultime N sessioni registrate per questa scheda
        val lastStatuses = sessionRepository.getCompletionStatuses(card.id, consecutiveMisses)
        
        // Se non abbiamo abbastanza sessioni registrate, non possiamo far scattare la regola
        if (lastStatuses.size < consecutiveMisses) return false
        
        // Trigger se TUTTE le ultime N sessioni hanno completed = false
        return lastStatuses.all { !it }
    }


    // Metodo legacy rimosso perché sostituito da applyAction(DELTA...)

}
