// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Scheda di allenamento (Training Card)
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta una scheda di allenamento personalizzata.
 * Una scheda contiene una lista di esercizi (tramite CardExerciseEntity)
 * e ha una durata in settimane o un numero di sedute obiettivo.
 *
 * Solo una scheda alla volta può essere ACTIVE.
 * Le schede future sono PENDING e possono avanzare automaticamente.
 *
 * @property title titolo della scheda (es. "Scheda Settimane 1-4")
 * @property durationWeeks durata in settimane (opzionale)
 * @property targetSessions numero di sedute obiettivo (opzionale)
 * @property status stato: PENDING, ACTIVE, COMPLETED
 * @property startDate data inizio (timestamp, null se non ancora iniziata)
 * @property endDate data fine prevista (timestamp, null se aperta)
 * @property orderIndex ordine per l'auto-avanzamento tra schede
 * @property autoAdvance se true, passa automaticamente alla scheda successiva
 * @property createdAt timestamp di creazione
 */
@Entity(tableName = "training_cards")
data class TrainingCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val durationWeeks: Int? = null,

    val targetSessions: Int? = null,

    val status: String = "PENDING",

    val startDate: Long? = null,

    val endDate: Long? = null,

    val orderIndex: Int = 0,

    val autoAdvance: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
