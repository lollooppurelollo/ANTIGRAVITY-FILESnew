// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sulle scale rapide
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione dei punteggi delle scale rapide (0-10).
 * Permette di registrare e monitorare nel tempo: astenia, dolore,
 * dispnea a riposo e dispnea a sforzi lievi.
 */
@Dao
interface ScaleEntryDao {

    // Ottieni tutte le rilevazioni, dalla più recente
    @Query("SELECT * FROM scale_entries ORDER BY date DESC")
    fun getAll(): Flow<List<ScaleEntryEntity>>

    // Ottieni una rilevazione per ID
    @Query("SELECT * FROM scale_entries WHERE id = :id")
    suspend fun getById(id: Long): ScaleEntryEntity?

    // Ottieni l'ultima rilevazione registrata
    @Query("SELECT * FROM scale_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): ScaleEntryEntity?

    // Ottieni le rilevazioni in un intervallo di date (per grafici andamento)
    @Query("SELECT * FROM scale_entries WHERE date >= :fromDate AND date <= :toDate ORDER BY date ASC")
    fun getEntriesInRange(fromDate: Long, toDate: Long): Flow<List<ScaleEntryEntity>>

    // Ottieni le ultime N rilevazioni (per trend rapido)
    @Query("SELECT * FROM scale_entries ORDER BY date DESC LIMIT :limit")
    fun getLatestEntries(limit: Int): Flow<List<ScaleEntryEntity>>

    // Inserisci una nuova rilevazione
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ScaleEntryEntity): Long

    // Aggiorna una rilevazione
    @Update
    suspend fun update(entry: ScaleEntryEntity)

    // Conta il numero totale di rilevazioni
    @Query("SELECT COUNT(*) FROM scale_entries")
    suspend fun count(): Int

    // Media dei punteggi di astenia nell'ultimo periodo (per priorità articoli)
    @Query("SELECT AVG(asthenia) FROM scale_entries WHERE date >= :sinceDate AND asthenia IS NOT NULL")
    suspend fun averageAstheniaSince(sinceDate: Long): Float?

    // Media del dolore nell'ultimo periodo
    @Query("SELECT AVG(osteoarticularPain) FROM scale_entries WHERE date >= :sinceDate AND osteoarticularPain IS NOT NULL")
    suspend fun averagePainSince(sinceDate: Long): Float?

    // Media dispnea a riposo nell'ultimo periodo
    @Query("SELECT AVG(restDyspnea) FROM scale_entries WHERE date >= :sinceDate AND restDyspnea IS NOT NULL")
    suspend fun averageRestDyspneaSince(sinceDate: Long): Float?

    // Media dispnea a sforzi nell'ultimo periodo
    @Query("SELECT AVG(exertionDyspnea) FROM scale_entries WHERE date >= :sinceDate AND exertionDyspnea IS NOT NULL")
    suspend fun averageExertionDyspneaSince(sinceDate: Long): Float?
}
