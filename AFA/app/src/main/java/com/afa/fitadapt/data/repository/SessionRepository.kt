// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Sessioni di allenamento
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.SessionDao
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.data.local.entity.SessionExerciseEntity
import com.afa.fitadapt.data.local.entity.SessionWithExercises
import com.afa.fitadapt.util.DateUtils
import kotlinx.coroutines.flow.Flow
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
    private val sessionDao: SessionDao
) {

    // ── Lettura ──

    /** Tutte le sessioni dalla più recente */
    fun getAllSessions(): Flow<List<SessionEntity>> =
        sessionDao.getAllSessions()

    /** Sessioni di una scheda specifica */
    fun getSessionsByCard(cardId: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByCard(cardId)

    /** Una sessione con i dettagli degli esercizi completati */
    fun getSessionWithExercises(sessionId: Long): Flow<SessionWithExercises?> =
        sessionDao.getSessionWithExercises(sessionId)

    /** Sessioni in un intervallo di date */
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

        return sessionId
    }

    /** Aggiorna una sessione esistente */
    suspend fun updateSession(session: SessionEntity) =
        sessionDao.update(session)

    // ── Statistiche ──

    /** Numero totale di sessioni completate (Flow) */
    fun countCompletedSessions(): Flow<Int> =
        sessionDao.countCompletedSessions()

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
        val oneDayMs = 24 * 60 * 60 * 1000L

        // Lo streak conta solo se l'ultimo giorno è oggi o ieri
        val lastDay = sessionDays.first()
        if (lastDay < today - oneDayMs) return 0

        var streak = 1
        for (i in 1 until sessionDays.size) {
            val diff = sessionDays[i - 1] - sessionDays[i]
            if (diff == oneDayMs) {
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

        val oneDayMs = 24 * 60 * 60 * 1000L
        var maxStreak = 1
        var currentStreak = 1

        for (i in 1 until sessionDays.size) {
            val diff = sessionDays[i] - sessionDays[i - 1]
            if (diff == oneDayMs) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return maxStreak
    }
}
