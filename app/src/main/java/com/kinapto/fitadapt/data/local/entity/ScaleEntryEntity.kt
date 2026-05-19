// =============================================================
// KinApto - Attività Fisica Adattata
// Entity: Punteggi scale rapide
// =============================================================
package com.kinapto.fitadapt.data.local.entity

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
    val lymphoedema: Int? = null,    // Percezione di gonfiore/tensione (0-10)
    val qualityOfLife: Int? = null, // Qualità della vita generale (0-10)
    val wellBeing: Int? = null,    // Benessere soggettivo (0-10)
    val spo2: Int? = null,         // Saturazione ossigeno (valore percentuale 70-100)
    val heartRate: Int? = null,    // Frequenza cardiaca (bpm)

    val createdAt: Long = System.currentTimeMillis()
)
