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
 * Rappresenta una sessione con il dettaglio degli esercizi completati.
 * Usata per le sessioni parziali dove serve sapere cosa è stato fatto.
 */
data class SessionWithExercises(
    @Embedded
    val session: SessionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val exerciseCompletions: List<SessionExerciseEntity>
)
