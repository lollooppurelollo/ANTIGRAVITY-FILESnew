// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Punteggi scale rapide
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registra i punteggi delle scale rapide opzionali (0-10).
 * Queste scale permettono alla paziente di monitorare nel tempo
 * alcuni sintomi chiave legati al percorso di cura.
 *
 * Tutti i campi punteggio sono opzionali: la paziente può compilare
 * solo quelli che ritiene rilevanti in quel momento.
 *
 * @property date data della rilevazione (timestamp del giorno)
 * @property asthenia livello di astenia/stanchezza (0 = nessuna, 10 = massima)
 * @property osteoarticularPain dolore osteoarticolare (0 = nessuno, 10 = massimo)
 * @property restDyspnea dispnea a riposo (0 = nessuna, 10 = massima)
 * @property exertionDyspnea dispnea a sforzi lievi (0 = nessuna, 10 = massima)
 * @property createdAt timestamp di creazione del record
 */
@Entity(tableName = "scale_entries")
data class ScaleEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: Long,

    val asthenia: Int? = null,

    val osteoarticularPain: Int? = null,

    val restDyspnea: Int? = null,

    val exertionDyspnea: Int? = null,

    val createdAt: Long = System.currentTimeMillis()
)
