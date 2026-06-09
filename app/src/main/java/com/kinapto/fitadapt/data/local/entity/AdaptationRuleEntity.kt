package com.kinapto.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Definisce una regola di adattamento automatico per una scheda.
 * Più regole con lo stesso groupId formano un blocco AND.
 * Gruppi diversi formano un blocco OR.
 */
@Entity(
    tableName = "adaptation_rules",
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
data class AdaptationRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val cardId: Long,

    /** Identificativo del gruppo per logica AND */
    val groupId: String,

    /** Tipo di trigger: SYMPTOM, COMPLETION, BIOMETRIC, FAILURE_REASON */
    val triggerType: String,

    /** Parametro specifico (es. PAIN, EFFORT, SPO2, TOO_FATIGUING) */
    val parameter: String? = null,

    /** Operatore: GT (Greater Than), LT (Less Than), EQ (Equals) */
    val operator: String = "GT",

    val threshold: Float,

    /** Finestra temporale in giorni (0 = solo l'ultima occorrenza) */
    val windowDays: Int = 0,

    /** Numero di occorrenze richieste nella finestra */
    val minOccurrences: Int = 1,

    /** Se true, le occorrenze devono essere consecutive */
    val requireConsecutive: Boolean = false,

    /** Se true, usa la media dei valori nella finestra invece del conteggio occorrenze */
    val useAverage: Boolean = false,

    /** Azione: DELTA (modifica parametri) o SWITCH (cambia scheda) */
    val actionType: String,

    /** Valore dell'azione: ID della scheda target o JSON per il delta */
    val actionValue: String,

    /** Messaggio da mostrare alla paziente (notifica) */
    val notificationMessage: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)
