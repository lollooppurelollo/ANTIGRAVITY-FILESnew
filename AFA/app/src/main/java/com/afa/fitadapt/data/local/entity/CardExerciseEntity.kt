// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Relazione tra Scheda e Esercizio
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabella ponte (bridge) che collega una scheda ai suoi esercizi.
 * Ogni riga rappresenta un esercizio presente in una specifica scheda,
 * con parametri personalizzati (durata, ripetizioni, intensità, note).
 *
 * I parametri custom sovrascrivono quelli di default dell'esercizio.
 * Se un campo custom è null, si usa il valore di default dell'esercizio.
 *
 * @property cardId riferimento alla scheda di allenamento
 * @property exerciseId riferimento all'esercizio nella libreria
 * @property orderIndex ordine dell'esercizio all'interno della scheda
 * @property customDurationSec durata personalizzata (sovrascrive il default)
 * @property customRepetitions ripetizioni personalizzate (sovrascrivono il default)
 * @property customIntensity intensità personalizzata (sovrascrive il default)
 * @property customNotes note personalizzate per questo esercizio in questa scheda
 */
@Entity(
    tableName = "card_exercises",
    foreignKeys = [
        ForeignKey(
            entity = TrainingCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("cardId"),
        Index("exerciseId")
    ]
)
data class CardExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "cardId")
    val cardId: Long,

    @ColumnInfo(name = "exerciseId")
    val exerciseId: Long,

    val orderIndex: Int = 0,

    val customDurationSec: Int? = null,

    val customRepetitions: Int? = null,

    val customIntensity: String? = null,

    val customNotes: String? = null
)
