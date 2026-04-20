// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Progressi e statistiche aggregate
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.GoalDao

import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dati aggregati per la schermata progressi.
 */
data class ProgressSummary(
    val totalSessions: Int,
    val completedSessions: Int,
    val adherencePercent: Float,
    val totalMinutes: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val activeGoals: List<GoalEntity>
)

/**
 * Repository per il calcolo dei progressi e delle statistiche.
 *
 * Aggrega dati da più fonti:
 * - SessionRepository per conteggi e streak
 * - GoalDao per obiettivi
 * - ScaleEntryDao per andamento scale (nei grafici)
 *
 * Fornisce i dati necessari alla schermata Progressi.
 */
@Singleton
class ProgressRepository @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val goalDao: GoalDao,
    private val diaryRepository: DiaryRepository
) {

    /** Flow con il conteggio sessioni completate (totale) */
    fun completedSessionsCount(): Flow<Int> =
        sessionRepository.countCompletedSessions()

    /** Flow con il conteggio sessioni completate interamente */
    fun fullSessionsCount(): Flow<Int> =
        sessionRepository.countFullSessions()

    /** Flow con il conteggio sessioni completate parzialmente */
    fun partialSessionsCount(): Flow<Int> =
        sessionRepository.countPartialSessions()

    /** Flow con il conteggio sessioni totali */
    fun totalSessionsCount(): Flow<Int> =
        sessionRepository.countTotalSessions()

    /** Flow con i minuti totali */
    fun totalMinutes(): Flow<Int> =
        sessionRepository.totalMinutes()

    /**
     * Calcola il riepilogo progressi completo.
     * Questo è un metodo suspend perché alcune statistiche
     * richiedono calcoli (es. streak).
     */
    @Suppress("unused") // Sarà chiamata dal ProgressViewModel quando implementato
    suspend fun getProgressSummary(
        totalSessions: Int,
        completedSessions: Int,
        totalMinutes: Int
    ): ProgressSummary {
        val adherence = if (totalSessions > 0) {
            (completedSessions.toFloat() / totalSessions.toFloat()) * 100f
        } else {
            0f
        }

        return ProgressSummary(
            totalSessions = totalSessions,
            completedSessions = completedSessions,
            adherencePercent = adherence,
            totalMinutes = totalMinutes,
            currentStreak = sessionRepository.calculateCurrentStreak(),
            longestStreak = sessionRepository.calculateLongestStreak(),
            activeGoals = emptyList() // Verrà popolato dal Flow degli obiettivi
        )
    }

    /** Flow con gli obiettivi attivi */
    fun getActiveGoals(): Flow<List<GoalEntity>> =
        goalDao.getActiveGoals()

    /** Flow con gli obiettivi raggiunti */
    @Suppress("unused") // Sarà usata nella schermata progressi avanzata
    fun getCompletedGoals(): Flow<List<GoalEntity>> =
        goalDao.getCompletedGoals()

    /** Andamento scale rapide per i grafici */
    @Suppress("unused") // Sarà usata nei grafici andamento scale
    fun getScaleTrend(fromDate: Long, toDate: Long): Flow<List<ScaleEntryEntity>> =
        diaryRepository.getScaleEntriesInRange(fromDate, toDate)

    /**
     * Aggiorna il valore corrente degli obiettivi automatici.
     * Viene chiamata dopo ogni nuova sessione o periodicamente.
     */
    @Suppress("unused", "RedundantSuspendModifier") // API da usare dopo ogni sessione
    fun refreshGoalProgress(
        completedSessions: Int,
        totalMinutes: Int,
        currentStreak: Int,
        adherencePercent: Float
    ) {
        // L'aggiornamento viene delegato ai ViewModel che hanno accesso ai valori aggiornati
    }

    /** Aggiorna il progresso di un singolo obiettivo */
    @Suppress("unused") // Chiamata dal ViewModel dopo modifica manuale obiettivo
    suspend fun updateGoalProgress(goalId: Long, currentValue: Float) {
        goalDao.updateCurrentValue(goalId, currentValue)
    }
}
