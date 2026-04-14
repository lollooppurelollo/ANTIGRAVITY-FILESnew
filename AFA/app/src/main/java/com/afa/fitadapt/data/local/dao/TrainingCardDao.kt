// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sulle schede di allenamento
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.CardWithExercises
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione delle schede di allenamento.
 * Include operazioni per la scheda attiva, quelle future (pending)
 * e gli esercizi associati a ciascuna scheda.
 */
@Dao
interface TrainingCardDao {

    // ── SCHEDE ──

    // Ottieni tutte le schede ordinate per indice
    @Query("SELECT * FROM training_cards ORDER BY orderIndex")
    fun getAll(): Flow<List<TrainingCardEntity>>

    // Ottieni la scheda attualmente attiva (ce ne può essere solo una)
    @Query("SELECT * FROM training_cards WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveCard(): Flow<TrainingCardEntity?>

    // Ottieni le schede in attesa (future), ordinate per indice
    @Query("SELECT * FROM training_cards WHERE status = 'PENDING' ORDER BY orderIndex")
    fun getPendingCards(): Flow<List<TrainingCardEntity>>

    // Ottieni le schede completate
    @Query("SELECT * FROM training_cards WHERE status = 'COMPLETED' ORDER BY orderIndex")
    fun getCompletedCards(): Flow<List<TrainingCardEntity>>

    // Ottieni una singola scheda per ID
    @Query("SELECT * FROM training_cards WHERE id = :id")
    suspend fun getById(id: Long): TrainingCardEntity?

    // Inserisci una nuova scheda
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: TrainingCardEntity): Long

    // Aggiorna una scheda esistente
    @Update
    suspend fun update(card: TrainingCardEntity)

    // Elimina una scheda (e i suoi esercizi associati via CASCADE)
    @Delete
    suspend fun delete(card: TrainingCardEntity)

    // Aggiorna lo stato di una scheda
    @Query("UPDATE training_cards SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    // Aggiorna data inizio e fine di una scheda
    @Query("UPDATE training_cards SET startDate = :startDate, endDate = :endDate WHERE id = :id")
    suspend fun updateDates(id: Long, startDate: Long?, endDate: Long?)

    // Ottieni la prossima scheda in attesa (per auto-avanzamento)
    @Query("""
        SELECT * FROM training_cards 
        WHERE status = 'PENDING' 
        ORDER BY orderIndex ASC 
        LIMIT 1
    """)
    suspend fun getNextPendingCard(): TrainingCardEntity?

    // Conta le schede per stato
    @Query("SELECT COUNT(*) FROM training_cards WHERE status = :status")
    suspend fun countByStatus(status: String): Int

    // ── SCHEDA CON ESERCIZI (relazione) ──

    // Ottieni una scheda con tutti i suoi esercizi
    @Transaction
    @Query("SELECT * FROM training_cards WHERE id = :cardId")
    fun getCardWithExercises(cardId: Long): Flow<CardWithExercises?>

    // Ottieni la scheda attiva con tutti i suoi esercizi
    @Transaction
    @Query("SELECT * FROM training_cards WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveCardWithExercises(): Flow<CardWithExercises?>

    // ── ESERCIZI DELLA SCHEDA ──

    // Inserisci un esercizio in una scheda
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardExercise(cardExercise: CardExerciseEntity): Long

    // Inserisci più esercizi in una scheda
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardExercises(cardExercises: List<CardExerciseEntity>)

    // Aggiorna un esercizio nella scheda
    @Update
    suspend fun updateCardExercise(cardExercise: CardExerciseEntity)

    // Rimuovi un esercizio dalla scheda
    @Delete
    suspend fun deleteCardExercise(cardExercise: CardExerciseEntity)

    // Ottieni gli esercizi di una scheda, ordinati
    @Query("SELECT * FROM card_exercises WHERE cardId = :cardId ORDER BY orderIndex")
    fun getCardExercises(cardId: Long): Flow<List<CardExerciseEntity>>

    // Elimina tutti gli esercizi di una scheda
    @Query("DELETE FROM card_exercises WHERE cardId = :cardId")
    suspend fun deleteAllCardExercises(cardId: Long)
}
