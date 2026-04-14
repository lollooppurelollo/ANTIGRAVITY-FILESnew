// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Esercizio nella libreria precaricata
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un esercizio nella libreria.
 * Gli esercizi sono precaricati al primo avvio e possono essere aggiunti
 * dalla sezione protetta. Da questi si costruiscono le schede.
 *
 * @property name nome dell'esercizio (es. "Cammino veloce 15 min")
 * @property category categoria dall'enum ExerciseCategory (salvata come stringa)
 * @property description descrizione dettagliata dell'esercizio
 * @property videoUri percorso al file video locale (null se non disponibile)
 * @property defaultDurationSec durata predefinita in secondi (opzionale)
 * @property defaultRepetitions numero ripetizioni predefinito (opzionale)
 * @property defaultIntensity intensità predefinita: "bassa", "moderata", "alta"
 * @property notes note aggiuntive per l'esercizio
 * @property isActive se false, l'esercizio è stato disattivato
 * @property createdAt timestamp di creazione
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val category: String,

    val description: String,

    val videoUri: String? = null,

    val defaultDurationSec: Int? = null,

    val defaultRepetitions: Int? = null,

    val defaultIntensity: String = "moderata",

    val notes: String? = null,

    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
