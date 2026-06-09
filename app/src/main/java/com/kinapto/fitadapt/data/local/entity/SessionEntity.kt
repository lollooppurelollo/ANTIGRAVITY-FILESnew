// =============================================================
// KinApto - Attività Fisica Adattata
// Entity: Sessione di allenamento registrata
// =============================================================
package com.kinapto.fitadapt.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Rappresenta una singola sessione di allenamento registrata.
 * Ogni sessione è collegata a una scheda e contiene la risposta
 * obbligatoria "allenamento svolto sì/no" più campi opzionali.
 *
 * @property cardId riferimento alla scheda di allenamento
 * @property date data della sessione (timestamp)
 * @property completed OBBLIGATORIO: la paziente ha svolto l'allenamento? sì/no
 * @property partial se true, la sessione è stata parziale (non tutti gli esercizi completati)
 * @property actualDurationMin durata reale in minuti (opzionale)
 * @property perceivedEffort fatica percepita 0-10 (opzionale)
 * @property mood umore 0-10 (opzionale)
 * @property sleepQuality qualità del sonno 0-10 (opzionale)
 * @property notes note libere sulla sessione
 * @property createdAt timestamp di creazione del record
 */
@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = TrainingCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "cardId")
    val cardId: Long,

    val date: Long,

    val completed: Boolean,

    val partial: Boolean = false,

    val actualDurationMin: Int? = null,

    val perceivedEffort: Int? = null,

    val asthenia: Int? = null,

    val osteoarticularPain: Int? = null,

    val restDyspnea: Int? = null,

    val exertionDyspnea: Int? = null,

    val mood: Int? = null,

    val sleepQuality: Int? = null,

    val nausea: Int? = null,

    val appetite: Int? = null,

    val anxiety: Int? = null,

    // Nuovi parametri richiesti per unificazione e monitoraggio clinico
    val lymphoedema: Int? = null,
    val qualityOfLife: Int? = null,
    val wellBeing: Int? = null,
    val spo2: Int? = null,
    val heartRate: Int? = null,

    val notes: String? = null,

    val failureReason: String? = null, // Es. "TOO_FATIGUING", "LACK_OF_TIME", "PAIN", "OTHER"

    val createdAt: Long = System.currentTimeMillis()
)
