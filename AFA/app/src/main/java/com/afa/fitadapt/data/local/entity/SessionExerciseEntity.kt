// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Esercizio completato in una sessione
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registra quali esercizi sono stati completati in una sessione.
 * Usato soprattutto per le sessioni parziali, dove solo alcuni
 * esercizi della scheda sono stati svolti.
 *
 * @property sessionId riferimento alla sessione
 * @property cardExerciseId riferimento all'esercizio nella scheda
 * @property completed se l'esercizio è stato completato in questa sessione
 */
@Entity(
    tableName = "session_exercises",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CardExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId"),
        Index("cardExerciseId")
    ]
)
data class SessionExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "sessionId")
    val sessionId: Long,

    @ColumnInfo(name = "cardExerciseId")
    val cardExerciseId: Long,

    val completed: Boolean = false
)
