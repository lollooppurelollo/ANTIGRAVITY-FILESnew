// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sulle sessioni di allenamento
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.data.local.entity.SessionExerciseEntity
import com.afa.fitadapt.data.local.entity.SessionWithExercises
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione delle sessioni di allenamento.
 * Include operazioni per registrare sessioni, tracciare gli esercizi
 * completati e calcolare statistiche per i progressi.
 */
@Dao
interface SessionDao {

    // ── SESSIONI ──

    // Ottieni tutte le sessioni di una scheda, ordinate per data
    @Query("SELECT * FROM sessions WHERE cardId = :cardId ORDER BY date DESC")
    fun getSessionsByCard(cardId: Long): Flow<List<SessionEntity>>

    // Ottieni tutte le sessioni, dalla più recente
    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    // Ottieni una sessione per ID
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?

    // Ottieni le sessioni di oggi
    @Query("SELECT * FROM sessions WHERE date >= :startOfDay AND date < :endOfDay")
    suspend fun getSessionsForDay(startOfDay: Long, endOfDay: Long): List<SessionEntity>

    // Inserisci una nuova sessione
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    // Aggiorna una sessione
    @Update
    suspend fun update(session: SessionEntity)

    // Elimina una sessione
    @androidx.room.Delete
    suspend fun delete(session: SessionEntity)

    // ── ESERCIZI DELLA SESSIONE ──

    // Inserisci il dettaglio degli esercizi completati in una sessione
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionExercises(exercises: List<SessionExerciseEntity>)

    // Ottieni una sessione con il dettaglio degli esercizi
    @Transaction
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    fun getSessionWithExercises(sessionId: Long): Flow<SessionWithExercises?>

    // ── STATISTICHE PER PROGRESSI ──

    // Conta le sessioni completate (totale: sia piene che parziali)
    @Query("SELECT COUNT(*) FROM sessions WHERE completed = 1")
    fun countCompletedSessions(): Flow<Int>

    // Conta le sessioni completate interamente
    @Query("SELECT COUNT(*) FROM sessions WHERE completed = 1 AND partial = 0")
    fun countFullSessions(): Flow<Int>

    // Conta le sessioni completate parzialmente
    @Query("SELECT COUNT(*) FROM sessions WHERE completed = 1 AND partial = 1")
    fun countPartialSessions(): Flow<Int>

    // Conta le sessioni totali (completate e non)
    @Query("SELECT COUNT(*) FROM sessions")
    fun countTotalSessions(): Flow<Int>

    // Conta le sessioni completate per una specifica scheda
    @Query("SELECT COUNT(*) FROM sessions WHERE cardId = :cardId AND completed = 1")
    suspend fun countCompletedByCard(cardId: Long): Int

    // Somma i minuti totali di attività
    @Query("SELECT COALESCE(SUM(actualDurationMin), 0) FROM sessions WHERE completed = 1")
    fun totalMinutes(): Flow<Int>

    // Ottieni le sessioni degli ultimi N giorni (per calcolo streak)
    @Query("""
        SELECT * FROM sessions 
        WHERE completed = 1 AND date >= :sinceDate 
        ORDER BY date ASC
    """)
    suspend fun getCompletedSessionsSince(sinceDate: Long): List<SessionEntity>

    // Ottieni tutte le sessioni completate ordinate per data (per calcolo streak)
    @Query("SELECT * FROM sessions WHERE completed = 1 ORDER BY date ASC")
    suspend fun getAllCompletedSessionsOrdered(): List<SessionEntity>

    // Ottieni le sessioni in un intervallo di date (per grafici progressi)
    @Query("SELECT * FROM sessions WHERE date >= :fromDate AND date <= :toDate ORDER BY date ASC")
    fun getSessionsInRange(fromDate: Long, toDate: Long): Flow<List<SessionEntity>>

    // Verifica se oggi è stata registrata una sessione (per notifica reminder)
    @Query("""
        SELECT COUNT(*) FROM sessions 
        WHERE date >= :startOfDay AND date < :endOfDay
    """)
    suspend fun hasSessionToday(startOfDay: Long, endOfDay: Long): Int
}
