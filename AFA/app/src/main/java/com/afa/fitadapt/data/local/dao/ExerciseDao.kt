// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sulla tabella esercizi
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione degli esercizi nella libreria.
 * Gli esercizi sono precaricati e possono essere aggiunti dalla sezione protetta.
 */
@Dao
interface ExerciseDao {

    // Ottieni tutti gli esercizi attivi, ordinati per categoria e nome
    @Query("SELECT * FROM exercises WHERE isActive = 1 ORDER BY category, name")
    fun getAllActive(): Flow<List<ExerciseEntity>>

    // Ottieni gli esercizi di una specifica categoria
    @Query("SELECT * FROM exercises WHERE category = :category AND isActive = 1 ORDER BY name")
    fun getByCategory(category: String): Flow<List<ExerciseEntity>>

    // Ottieni un singolo esercizio per ID
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    // Cerca esercizi per nome (ricerca parziale)
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' AND isActive = 1 ORDER BY name")
    fun searchByName(query: String): Flow<List<ExerciseEntity>>

    // Inserisci un nuovo esercizio (restituisce l'ID generato)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity): Long

    // Inserisci più esercizi in blocco (per il seeding iniziale)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    // Aggiorna un esercizio esistente
    @Update
    suspend fun update(exercise: ExerciseEntity)

    // Disattiva un esercizio (non lo cancella, lo nasconde)
    @Query("UPDATE exercises SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    // Riattiva un esercizio precedentemente disattivato
    @Query("UPDATE exercises SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    // Conta il numero totale di esercizi (anche disattivati)
    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int

    // Conta gli esercizi attivi
    @Query("SELECT COUNT(*) FROM exercises WHERE isActive = 1")
    suspend fun countActive(): Int

    // Ottieni tutte le categorie che hanno almeno un esercizio attivo
    @Query("SELECT DISTINCT category FROM exercises WHERE isActive = 1 ORDER BY category")
    fun getActiveCategories(): Flow<List<String>>
}
