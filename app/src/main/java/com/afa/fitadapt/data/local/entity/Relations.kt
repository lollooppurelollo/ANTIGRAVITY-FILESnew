// =============================================================
// AFA - Attività Fisica Adattata
// Classi di relazione Room per query con JOIN
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Rappresenta una scheda con tutti i suoi esercizi associati.
 * Usata nelle query @Transaction per ottenere la scheda completa.
 */
data class CardWithExercises(
    @Embedded
    val card: TrainingCardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val cardExercises: List<CardExerciseEntity>
)

/**
 * Rappresenta una sessione con il dettaglio degli esercizi completati e i nomi degli esercizi.
 */
data class SessionWithExercises(
    @Embedded
    val session: SessionEntity,

    @Relation(
        entity = SessionExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val exerciseCompletions: List<SessionExerciseWithDetails>
)

/**
 * Dettaglio di un esercizio in una sessione, includendo i dati della scheda e dell'esercizio base.
 */
data class SessionExerciseWithDetails(
    @Embedded
    val sessionExercise: SessionExerciseEntity,

    @Relation(
        entity = CardExerciseEntity::class,
        parentColumn = "cardExerciseId",
        entityColumn = "id"
    )
    val cardExercise: CardExerciseWithExercise
)

/**
 * Associazione tra un esercizio in scheda e l'anagrafica dell'esercizio.
 */
data class CardExerciseWithExercise(
    @Embedded
    val cardExercise: CardExerciseEntity,

    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: ExerciseEntity
)
