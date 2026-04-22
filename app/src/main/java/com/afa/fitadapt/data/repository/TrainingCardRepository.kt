// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Schede di allenamento
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.TrainingCardDao
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.CardWithExercises
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import com.afa.fitadapt.model.CardStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per la gestione delle schede di allenamento.
 *
 * Logica principale:
 * - Solo UNA scheda può essere ACTIVE alla volta
 * - Le schede PENDING sono in coda, ordinate per orderIndex
 * - Quando una scheda termina e ha autoAdvance=true, la prossima diventa attiva
 * - Le schede vengono create/modificate dalla sezione protetta
 */
@Singleton
class TrainingCardRepository @Inject constructor(
    private val trainingCardDao: TrainingCardDao
) {

    // ── Lettura ──

    /** Tutte le schede ordinate */
    fun getAllCards(): Flow<List<TrainingCardEntity>> =
        trainingCardDao.getAll()

    /** La scheda attualmente attiva (può essere null) */
    fun getActiveCard(): Flow<TrainingCardEntity?> =
        trainingCardDao.getActiveCard()

    /** La scheda attiva con tutti i suoi esercizi */
    fun getActiveCardWithExercises(): Flow<CardWithExercises?> =
        trainingCardDao.getActiveCardWithExercises()

    /** Una scheda con i suoi esercizi */
    @Suppress("unused") // Usata da CardEditor per mostrare esercizi scheda specifica
    fun getCardWithExercises(cardId: Long): Flow<CardWithExercises?> =
        trainingCardDao.getCardWithExercises(cardId)

    /** Le schede in attesa (future) */
    @Suppress("unused") // Vista schede future dalla sezione protetta
    fun getPendingCards(): Flow<List<TrainingCardEntity>> =
        trainingCardDao.getPendingCards()

    /** Le schede completate */
    @Suppress("unused") // Storico schede completate dalla sezione protetta
    fun getCompletedCards(): Flow<List<TrainingCardEntity>> =
        trainingCardDao.getCompletedCards()

    /** Una singola scheda per ID */
    suspend fun getById(id: Long): TrainingCardEntity? =
        trainingCardDao.getById(id)

    /** Gli esercizi di una scheda */
    @Suppress("unused") // Usata da CardEditor per lista esercizi assegnati
    fun getCardExercises(cardId: Long): Flow<List<CardExerciseEntity>> =
        trainingCardDao.getCardExercises(cardId)

    // ── Creazione e modifica (dalla sezione protetta) ──

    /** Crea una nuova scheda con i suoi esercizi */
    @Suppress("unused") // Chiamata da CardEditor al salvataggio nuova scheda
    suspend fun createCard(
        card: TrainingCardEntity,
        exercises: List<CardExerciseEntity>
    ): Long {
        val cardId = trainingCardDao.insert(card)
        val exercisesWithCardId = exercises.map { it.copy(cardId = cardId) }
        trainingCardDao.insertCardExercises(exercisesWithCardId)
        return cardId
    }

    /** Aggiorna una scheda esistente */
    @Suppress("unused") // Chiamata da CardEditor per modifica scheda esistente
    suspend fun updateCard(card: TrainingCardEntity) =
        trainingCardDao.update(card)

    /** Elimina una scheda e tutti i suoi esercizi (CASCADE) */
    @Suppress("unused") // Chiamata quando l'operatore elimina una scheda
    suspend fun deleteCard(card: TrainingCardEntity) =
        trainingCardDao.delete(card)

    /** Aggiorna gli esercizi di una scheda (sostituisce tutti) */
    @Suppress("unused") // Chiamata da CardEditor quando si modifica la lista esercizi
    suspend fun replaceCardExercises(
        cardId: Long,
        exercises: List<CardExerciseEntity>
    ) {
        trainingCardDao.deleteAllCardExercises(cardId)
        val exercisesWithCardId = exercises.map { it.copy(cardId = cardId) }
        trainingCardDao.insertCardExercises(exercisesWithCardId)
    }

    /** Aggiungi un singolo esercizio a una scheda */
    @Suppress("unused") // Chiamata da ExercisePicker per aggiungere un singolo esercizio
    suspend fun addExerciseToCard(cardExercise: CardExerciseEntity): Long =
        trainingCardDao.insertCardExercise(cardExercise)

    /** Rimuovi un esercizio da una scheda */
    suspend fun removeExerciseFromCard(cardExercise: CardExerciseEntity) =
        trainingCardDao.deleteCardExercise(cardExercise)

    // ── Gestione stato ──

    /** Attiva una scheda (e rende la precedente completata se presente) */
    suspend fun activateCard(cardId: Long) {
        // Prima: completa la scheda attualmente attiva (se c'è)
        completeCurrentActiveCard()

        // Poi: attiva la nuova scheda con data inizio = oggi
        trainingCardDao.updateStatus(cardId, CardStatus.ACTIVE.name)
        trainingCardDao.updateDates(
            id = cardId,
            startDate = System.currentTimeMillis(),
            endDate = null
        )
    }

    /** Completa la scheda attualmente attiva */
    private suspend fun completeCurrentActiveCard() {
        val currentActive = trainingCardDao.getActiveCardSync()
        if (currentActive != null) {
            completeCard(currentActive.id)
        }
    }

    /** Completa una scheda specifica */
    suspend fun completeCard(cardId: Long) {
        trainingCardDao.updateStatus(cardId, CardStatus.COMPLETED.name)
        trainingCardDao.updateDates(
            id = cardId,
            startDate = null, // mantieni la data originale
            endDate = System.currentTimeMillis()
        )
    }

    /**
     * Avanza automaticamente alla prossima scheda in coda.
     * Viene chiamata quando una scheda con autoAdvance=true viene completata.
     *
     * @return true se è stata attivata una nuova scheda
     */
    suspend fun advanceToNextCard(): Boolean {
        val nextCard = trainingCardDao.getNextPendingCard() ?: return false
        activateCard(nextCard.id)
        return true
    }
}
