// =============================================================
// KinApto - Attività Fisica Adattata
// Repository: Sessioni di allenamento
// =============================================================
package com.kinapto.fitadapt.data.repository

import com.kinapto.fitadapt.data.local.dao.SessionDao
import com.kinapto.fitadapt.data.local.entity.SessionEntity
import com.kinapto.fitadapt.data.local.entity.SessionExerciseEntity
import com.kinapto.fitadapt.data.local.entity.SessionWithExercises
import com.kinapto.fitadapt.domain.AdaptationManager
import com.kinapto.fitadapt.model.CardStatus
import com.kinapto.fitadapt.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per la gestione delle sessioni di allenamento.
 *
 * Ogni sessione registra:
 * - OBBLIGATORIO: allenamento svolto sì/no
 * - OPZIONALE: durata reale, fatica, umore, qualità sonno
 * - SE PARZIALE: dettaglio esercizi completati
 */
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val cardRepository: TrainingCardRepository,
    private val adaptationManager: AdaptationManager
) {

    // ── Lettura ──

    /** Tutte le sessioni dalla più recente */
    fun getAllSessions(): Flow<List<SessionEntity>> =
        sessionDao.getAllSessions()

    /** Sessioni di una scheda specifica */
    @Suppress("unused") // Usata dalla schermata storico sessioni per scheda (Tranche 5)
    fun getSessionsByCard(cardId: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByCard(cardId)

    /** Una sessione con i dettagli degli esercizi completati */
    @Suppress("unused") // Usata dalla schermata dettaglio sessione
    fun getSessionWithExercises(sessionId: Long): Flow<SessionWithExercises?> =
        sessionDao.getSessionWithExercises(sessionId)

    /** Sessioni in un intervallo di date */
    @Suppress("unused") // Usata dal ExportRepository per export per periodo
    fun getSessionsInRange(fromDate: Long, toDate: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsInRange(fromDate, toDate)

    // ── Registrazione sessione ──

    /**
     * Registra una nuova sessione di allenamento.
     *
     * @param session i dati della sessione
     * @param exerciseCompletions se parziale, la lista degli esercizi con stato
     * @return l'ID della sessione creata
     */
    suspend fun registerSession(
        session: SessionEntity,
        exerciseCompletions: List<SessionExerciseEntity> = emptyList()
    ): Long {
        val sessionId = sessionDao.insert(session)

        // Se ci sono dettagli sugli esercizi, salvali
        if (exerciseCompletions.isNotEmpty()) {
            val completionsWithSessionId = exerciseCompletions.map {
                it.copy(sessionId = sessionId)
            }
            sessionDao.insertSessionExercises(completionsWithSessionId)
        }

        // --- Logica Sequential Card: controlla se la scheda è finita ---
        checkAndAdvanceCard(session.cardId)

        // --- Bio-feedback Adaptation ---
        checkBioFeedbackAdaptation(session.cardId)

        return sessionId
    }

    /**
     * Controlla se una scheda ha raggiunto il limite di sedute e passa alla prossima.
     */
    private suspend fun checkAndAdvanceCard(cardId: Long) {
        val card = cardRepository.getById(cardId) ?: return
        if (!card.autoAdvance || card.status != CardStatus.ACTIVE.name) return

        val target = card.targetSessions ?: return
        val completedCount = sessionDao.countCompletedByCard(cardId)

        if (completedCount >= target) {
            // Scheda terminata! Completa e avanza
            cardRepository.completeCard(cardId)
            cardRepository.advanceToNextCard()
        }
    }

    /**
     * Controlla se la scheda attiva necessita di adattamento basato sui bio-feedback.
     */
    private suspend fun checkBioFeedbackAdaptation(cardId: Long) {
        val card = cardRepository.getById(cardId) ?: return
        if (card.status != CardStatus.ACTIVE.name) return
        
        adaptationManager.checkAndApplyAdaptation(card)
    }

    /** Aggiorna una sessione esistente */
    suspend fun updateSession(session: SessionEntity) =
        sessionDao.update(session)

    /** Elimina una sessione */
    suspend fun deleteSession(session: SessionEntity) =
        sessionDao.delete(session)

    // ── Statistiche ──

    /** Numero totale di sessioni completate (Flow) */
    fun countCompletedSessions(): Flow<Int> =
        sessionDao.countCompletedSessions()

    /** Numero totale di sessioni completate interamente (Flow) */
    fun countFullSessions(): Flow<Int> =
        sessionDao.countFullSessions()

    /** Numero totale di sessioni completate parzialmente (Flow) */
    fun countPartialSessions(): Flow<Int> =
        sessionDao.countPartialSessions()

    /** Numero totale di sessioni (Flow) */
    fun countTotalSessions(): Flow<Int> =
        sessionDao.countTotalSessions()

    /** Minuti totali di attività (Flow) */
    fun totalMinutes(): Flow<Int> =
        sessionDao.totalMinutes()

    /** Sessioni completate per una scheda */
    suspend fun countCompletedByCard(cardId: Long): Int =
        sessionDao.countCompletedByCard(cardId)

    /** Verifica se oggi è stata registrata una sessione */
    suspend fun hasSessionToday(): Boolean {
        val (start, end) = DateUtils.todayRange()
        return sessionDao.hasSessionToday(start, end) > 0
    }

    /** Verifica se ieri è stata registrata una sessione (usato dal MissedSessionWorker) */
    suspend fun hasSessionYesterday(startOfYesterday: Long, endOfYesterday: Long): Boolean {
        return sessionDao.hasSessionToday(startOfYesterday, endOfYesterday) > 0
    }

    /** Ottiene lo stato di completamento delle sessioni registrate per una scheda in una finestra temporale (o ultime N) */
    suspend fun getCompletionStatuses(cardId: Long, limit: Int, sinceDate: Long = 0): List<Boolean> =
        sessionDao.getCompletionStatuses(cardId, limit, sinceDate)

    /** Ottiene i motivi di fallimento delle sessioni registrate per una scheda in una finestra temporale (o ultime N) */
    suspend fun getFailureReasons(cardId: Long, limit: Int, sinceDate: Long = 0): List<String?> =
        sessionDao.getFailureReasons(cardId, limit, sinceDate)

    /** Media dello sforzo percepito per una scheda nell'ultimo periodo */
    suspend fun getAverageEffortSince(cardId: Long, sinceDate: Long): Float? =
        sessionDao.getAverageEffortSince(cardId, sinceDate)

    /**
     * Calcola lo streak attuale (giorni consecutivi di allenamento).
     * Conta all'indietro da oggi quanti giorni consecutivi hanno una sessione.
     */
    suspend fun calculateCurrentStreak(): Int {
        val sessions = sessionDao.getAllCompletedSessionsOrdered()
        if (sessions.isEmpty()) return 0

        // Raggruppa per giorno
        val sessionDays = sessions
            .map { DateUtils.toDayTimestamp(it.date) }
            .distinct()
            .sortedDescending()

        if (sessionDays.isEmpty()) return 0

        val today = DateUtils.toDayTimestamp(System.currentTimeMillis())
        
        // Lo streak conta solo se l'ultimo giorno è oggi o ieri
        val lastDay = sessionDays.first()
        val cal = Calendar.getInstance()
        cal.timeInMillis = today
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val yesterday = cal.timeInMillis
        
        if (lastDay < yesterday) return 0

        var streak = 1
        val checkCal = Calendar.getInstance()
        for (i in 1 until sessionDays.size) {
            checkCal.timeInMillis = sessionDays[i - 1]
            checkCal.add(Calendar.DAY_OF_MONTH, -1)
            val expectedDay = checkCal.timeInMillis
            
            if (sessionDays[i] == expectedDay) {
                streak++
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Calcola lo streak più lungo mai raggiunto.
     */
    suspend fun calculateLongestStreak(): Int {
        val sessions = sessionDao.getAllCompletedSessionsOrdered()
        if (sessions.isEmpty()) return 0

        val sessionDays = sessions
            .map { DateUtils.toDayTimestamp(it.date) }
            .distinct()
            .sorted()

        if (sessionDays.size <= 1) return sessionDays.size

        var maxStreak = 1
        var currentStreak = 1
        val checkCal = Calendar.getInstance()

        for (i in 1 until sessionDays.size) {
            checkCal.timeInMillis = sessionDays[i]
            checkCal.add(Calendar.DAY_OF_MONTH, -1)
            val expectedPreviousDay = checkCal.timeInMillis

            if (sessionDays[i - 1] == expectedPreviousDay) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return maxStreak
    }
}
