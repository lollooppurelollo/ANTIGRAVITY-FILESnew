package com.kinapto.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registra un evento di adattamento avvenuto.
 * Utile per lo storico clinico e l'esportazione CRF.
 */
@Entity(
    tableName = "adaptation_logs",
    foreignKeys = [
        ForeignKey(
            entity = TrainingCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["originalCardId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("originalCardId")]
)
data class AdaptationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    /** La scheda che ha scatenato l'adattamento */
    val originalCardId: Long?,

    /** La nuova scheda creata (se SWITCH) */
    val newCardId: Long? = null,

    /** Descrizione tecnica della regola soddisfatta (es. "PAIN > 7 AND EFFORT > 8") */
    val triggerDescription: String,

    /** Descrizione dell'azione intrapresa (es. "Reduced repetitions by 20%") */
    val actionTaken: String,

    /** Se l'adattamento è stato notificato all'utente */
    val notified: Boolean = true
)
