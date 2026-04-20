// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Obiettivo configurabile
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un obiettivo configurabile dalla sezione protetta.
 * Gli obiettivi permettono di monitorare i progressi della paziente
 * rispetto a target specifici.
 *
 * Tipi di obiettivo (targetType):
 * - "sessions_per_week"   → sedute alla settimana
 * - "total_minutes_week"  → minuti totali alla settimana
 * - "streak_days"         → giorni consecutivi di allenamento
 * - "total_sessions"      → sessioni totali
 * - "adherence_percent"   → percentuale di aderenza
 * - "custom"              → obiettivo personalizzato
 *
 * @property title nome dell'obiettivo (es. "3 allenamenti a settimana")
 * @property description descrizione dettagliata (opzionale)
 * @property targetType tipo di obiettivo (vedi sopra)
 * @property targetValue valore target da raggiungere
 * @property currentValue valore attuale (aggiornato automaticamente)
 * @property isActive se true, l'obiettivo è attivo e monitorato
 * @property createdAt timestamp di creazione
 * @property updatedAt ultimo aggiornamento del valore corrente
 */
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val description: String? = null,

    val targetType: String,

    val targetValue: Float,

    val silverValue: Float? = null,

    val goldValue: Float? = null,

    val currentValue: Float = 0f,

    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
