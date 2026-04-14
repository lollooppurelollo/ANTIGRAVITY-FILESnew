// =============================================================
// AFA - Attività Fisica Adattata
// Worker: Messaggi motivazionali periodici
// =============================================================
package com.afa.fitadapt.notification.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.afa.fitadapt.data.local.datastore.UserPreferences
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

/**
 * Worker per messaggi motivazionali periodici.
 *
 * Seleziona un messaggio contestuale in base allo streak e
 * alle sessioni completate, poi mostra la notifica.
 *
 * Schedulato ogni 2-3 giorni tramite WorkScheduler.
 */
@HiltWorker
class MotivationalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val sessionRepository: SessionRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "afa_motivational_messages"

        /** Messaggi generici */
        private val GENERIC_MESSAGES = listOf(
            "Ogni passo conta. Anche oggi, fai qualcosa per te stessa! 🌟",
            "Il tuo corpo ti ringrazierà. Anche pochi minuti fanno la differenza.",
            "Sei più forte di quanto pensi. Continua il tuo percorso! 💪",
            "Il movimento è una medicina potente. Regalati qualche minuto oggi.",
            "Non devi essere perfetta, devi solo essere costante. Ce la fai!",
            "Ogni giorno è un nuovo inizio. Come vuoi usare il tuo oggi?",
            "La pazienza è la chiave. I risultati arrivano con la costanza.",
            "Ascolta il tuo corpo. Se ti dice di muoverti, fallo con gioia! 🌸",
            "Una passeggiata di 10 minuti può cambiare la giornata. Provaci!",
            "Non contare i giorni, fa' che i giorni contino. 🌈"
        )

        /** Messaggi per streak attivo */
        private val STREAK_MESSAGES = listOf(
            "Che streak! Stai costruendo un'abitudine importante. Non fermarti! 🔥",
            "La tua costanza è ammirevole. Il tuo corpo sta cambiando in meglio.",
            "Giorno dopo giorno, il tuo impegno fa la differenza. Brava! ⭐",
            "La tua serie positiva è impressionante. Continua così!",
            "Stai dimostrando grande forza di volontà. Sei un esempio! 💫"
        )

        /** Messaggi quando lo streak è rotto */
        private val RESTART_MESSAGES = listOf(
            "Un giorno di pausa non cancella i progressi. Riparti oggi! 🌱",
            "Non è mai troppo tardi per ricominciare. Oggi è un buon giorno.",
            "Anche i campioni riposano. L'importante è tornare in pista. 🏃‍♀️",
            "Un passo alla volta. Riprendiamo da dove ci eravamo fermati.",
            "Ogni restart è un atto di coraggio. Sei pronta? 💛"
        )
    }

    override suspend fun doWork(): Result {
        return try {
            // Verifica se i messaggi motivazionali sono attivi
            if (!userPreferences.isMotivationalEnabled()) {
                return Result.success()
            }

            // Seleziona il messaggio in base al contesto
            val streak = sessionRepository.calculateCurrentStreak()

            val message = when {
                streak >= 3 -> STREAK_MESSAGES[Random.nextInt(STREAK_MESSAGES.size)]
                streak == 0 -> RESTART_MESSAGES[Random.nextInt(RESTART_MESSAGES.size)]
                else -> GENERIC_MESSAGES[Random.nextInt(GENERIC_MESSAGES.size)]
            }

            notificationHelper.showMotivationalNotification(message)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
