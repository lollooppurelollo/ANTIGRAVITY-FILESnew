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
import javax.inject.Singleton

@Singleton
class AdaptationManager @Inject constructor(
    private val cardRepository: TrainingCardRepository,
    private val scaleRepository: ScaleEntryRepository,
    private val notificationHelper: NotificationHelper
) {
    /**
     * Verifica se la scheda attiva necessita di un adattamento basato sui bio-feedback.
     * Viene chiamata dopo la registrazione di una sessione o periodicamente.
     */
    suspend fun checkAndApplyAdaptation(card: TrainingCardEntity) {
        if (card.isAdapted) return // Non riadattare se già adattata

        val biometricType = card.adaptationBiometricType ?: return
        val threshold = card.adaptationThreshold ?: return
        val windowDays = card.adaptationWindowDays ?: 15
        val action = card.adaptationAction ?: return

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
            else -> null
        }

        // Per i parametri "positivi" (Appetito, Umore, Sonno), l'adattamento scatta se il valore è BASSO.
        // Per i sintomi (Dolore, Nausea, ecc.), scatta se il valore è ALTO.
        val isInverseTrigger = biometricType in listOf("APPETITE", "MOOD", "SLEEP")
        
        val shouldTrigger = if (isInverseTrigger) {
            averageValue != null && averageValue < threshold
        } else {
            averageValue != null && averageValue > threshold
        }

        if (shouldTrigger) {
            applyAction(card, action)
        }
    }

    private suspend fun applyAction(card: TrainingCardEntity, action: String) {
        when (action) {
            "SWITCH_CARD" -> {
                cardRepository.completeCard(card.id)
                cardRepository.advanceToNextCard()
                notificationHelper.showAdaptationNotification(
                    "Nuova scheda sbloccata! 🌟",
                    "In base ai tuoi feedback, abbiamo attivato la fase successiva del tuo percorso."
                )
            }
            "DOWNREGULATE" -> {
                downregulateCard(card)
                notificationHelper.showAdaptationNotification(
                    "Allenamento ottimizzato ✨",
                    "Abbiamo ridotto leggermente l'intensità per aiutarti a recuperare meglio."
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
