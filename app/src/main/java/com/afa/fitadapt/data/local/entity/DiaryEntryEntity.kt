// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Voce del diario libero
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta una voce nel diario libero della paziente.
 * La paziente può scrivere liberamente come si sente,
 * annotare pensieri o eventi legati al percorso.
 *
 * @property date data della voce (timestamp del giorno)
 * @property text testo libero scritto dalla paziente
 * @property createdAt timestamp di creazione del record
 */
@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: Long,

    val text: String,

    val createdAt: Long = System.currentTimeMillis()
)
