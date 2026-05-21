// =============================================================
// KinApto - Attività Fisica Adattata
// Domain: Gestore Adattamento Bio-feedback
// =============================================================
package com.kinapto.fitadapt.domain

import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import com.kinapto.fitadapt.data.repository.ScaleEntryRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.notification.NotificationHelper
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AdaptationManager @Inject constructor(
    private val cardRepository: TrainingCardRepository,
    private val scaleRepository: ScaleEntryRepository,
    private val sessionRepositoryProvider: Provider<com.kinapto.fitadapt.data.repository.SessionRepository>, // Usiamo Provider per evitare cicli di dipendenza
    private val notificationHelper: NotificationHelper
) {
    private val sessionRepository get() = sessionRepositoryProvider.get()
    /**
     * Verifica se la scheda attiva necessita di un adattamento basato sui bio-feedback o regole cliniche.
     * Viene chiamata dopo la registrazione di una sessione o una rilevazione nel diario.
     */
    suspend fun checkAndApplyAdaptation(card: TrainingCardEntity) {
        if (card.isAdapted) return // Non riadattare se già adattata

        val action = card.adaptationAction ?: return

        // 1. Verifica Soglia Biometrica (Media nel tempo)
        if (checkBiometricTrigger(card)) {
            applyAction(card, action, "biometric")
            return
        }

        // 2. Verifica Difficoltà/Fatica (Media recente o ultima sessione)
        if (checkDifficultyTrigger(card)) {
            applyAction(card, action, "difficulty")
            return
        }

        // 3. Verifica Sessioni Saltate Consecutive
        if (checkMissesTrigger(card)) {
            applyAction(card, action, "misses")
            return
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
        val lastStatuses = sessionRepository.getLastCompletionStatuses(card.id, consecutiveMisses)
        
        // Se non abbiamo abbastanza sessioni registrate, non possiamo far scattare la regola
        if (lastStatuses.size < consecutiveMisses) return false
        
        // Trigger se TUTTE le ultime N sessioni hanno completed = false
        return lastStatuses.all { completed -> !completed }
    }

    private suspend fun applyAction(card: TrainingCardEntity, action: String, reason: String) {
        val reasonMsg = when (reason) {
            "biometric" -> "In base ai tuoi parametri di salute"
            "difficulty" -> "In base alla fatica che stai riscontrando"
            "misses" -> "Abbiamo notato alcune difficoltà nel seguire il ritmo"
            else -> "In base ai tuoi feedback"
        }

        when (action) {
            "SWITCH_CARD" -> {
                cardRepository.completeCard(card.id)
                cardRepository.advanceToNextCard()
                notificationHelper.showAdaptationNotification(
                    "Nuova scheda sbloccata! 🌟",
                    "$reasonMsg, abbiamo attivato la fase successiva del tuo percorso."
                )
            }
            "DOWNREGULATE" -> {
                downregulateCard(card)
                notificationHelper.showAdaptationNotification(
                    "Allenamento ottimizzato ✨",
                    "$reasonMsg, abbiamo ridotto leggermente l'intensità per aiutarti a recuperare meglio."
                )
            }
        }
    }

    private suspend fun downregulateCard(card: TrainingCardEntity) {
        val exercises = cardRepository.getCardExercisesSync(card.id)
        exercises.forEach { ce ->
            val newIntensity = when (ce.customIntensity) {
                "alta" -> "moderata"
                "moderata" -> "bassa"
                else -> "bassa"
            }
            
            // Se l'intensità è già bassa o se stiamo riducendo, aggiorniamo anche durata/rep se necessario
            // In questo esempio semplificato, riduciamo solo l'intensità nominale
            if (newIntensity != ce.customIntensity) {
                cardRepository.updateCardExercise(ce.copy(customIntensity = newIntensity))
            }
        }
        
        // Segna la scheda come adattata per evitare cicli infiniti o ri-applicazioni
        cardRepository.updateCard(card.copy(isAdapted = true))
    }
}
