// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sugli obiettivi
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione degli obiettivi.
 * Gli obiettivi sono configurabili e modificabili dalla sezione protetta.
 */
@Dao
interface GoalDao {

    // Ottieni tutti gli obiettivi attivi
    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<GoalEntity>>

    // Ottieni tutti gli obiettivi (anche disattivati)
    @Query("SELECT * FROM goals ORDER BY isActive DESC, createdAt DESC")
    fun getAll(): Flow<List<GoalEntity>>

    // Ottieni un obiettivo per ID
    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getById(id: Long): GoalEntity?

    // Inserisci un nuovo obiettivo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    // Aggiorna un obiettivo
    @Update
    suspend fun update(goal: GoalEntity)

    // Elimina un obiettivo
    @Delete
    suspend fun delete(goal: GoalEntity)

    // Aggiorna il valore corrente di un obiettivo
    @Query("UPDATE goals SET currentValue = :value, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCurrentValue(id: Long, value: Float, updatedAt: Long = System.currentTimeMillis())

    // Attiva/disattiva un obiettivo
    @Query("UPDATE goals SET isActive = :active, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean, updatedAt: Long = System.currentTimeMillis())

    // Conta gli obiettivi attivi
    @Query("SELECT COUNT(*) FROM goals WHERE isActive = 1")
    suspend fun countActive(): Int

    // Ottieni gli obiettivi raggiungibili (dove currentValue >= targetValue)
    @Query("SELECT * FROM goals WHERE isActive = 1 AND currentValue >= targetValue")
    fun getCompletedGoals(): Flow<List<GoalEntity>>
}
