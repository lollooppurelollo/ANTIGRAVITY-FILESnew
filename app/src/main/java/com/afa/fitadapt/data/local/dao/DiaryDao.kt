// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sul diario libero
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione delle voci del diario libero.
 */
@Dao
interface DiaryDao {

    // Ottieni tutte le voci del diario, dalla più recente
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAll(): Flow<List<DiaryEntryEntity>>

    // Ottieni una voce specifica per ID
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getById(id: Long): DiaryEntryEntity?

    // Ottieni le voci di un giorno specifico
    @Query("SELECT * FROM diary_entries WHERE date >= :startOfDay AND date < :endOfDay ORDER BY createdAt DESC")
    fun getEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<DiaryEntryEntity>>

    // Ottieni le voci in un intervallo di date
    @Query("SELECT * FROM diary_entries WHERE date >= :fromDate AND date <= :toDate ORDER BY date DESC")
    fun getEntriesInRange(fromDate: Long, toDate: Long): Flow<List<DiaryEntryEntity>>

    // Inserisci una nuova voce
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntryEntity): Long

    // Aggiorna una voce esistente
    @Update
    suspend fun update(entry: DiaryEntryEntity)

    // Elimina una voce
    @Delete
    suspend fun delete(entry: DiaryEntryEntity)

    // Conta il numero totale di voci
    @Query("SELECT COUNT(*) FROM diary_entries")
    suspend fun count(): Int
}
