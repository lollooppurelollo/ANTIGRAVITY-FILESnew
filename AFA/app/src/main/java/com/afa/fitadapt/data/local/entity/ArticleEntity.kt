// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Articolo / Consiglio
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un articolo o consiglio nella sezione informativa.
 * Gli articoli sono precaricati e possono essere aggiunti dalla sezione protetta.
 *
 * Categorie: attività fisica, alimentazione, sonno, fatigue, dolore,
 *            benessere psicologico, linfedema
 *
 * @property title titolo dell'articolo
 * @property category categoria dell'articolo
 * @property summary breve riassunto (mostrato nella lista)
 * @property body contenuto completo dell'articolo
 * @property isFeatured se è l'articolo in evidenza questa settimana
 * @property weekNumber numero della settimana per la programmazione in evidenza
 * @property year anno di riferimento
 * @property createdAt timestamp di creazione
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val category: String,

    val summary: String,

    val body: String,

    val isFeatured: Boolean = false,

    val weekNumber: Int? = null,

    val year: Int? = null,

    val createdAt: Long = System.currentTimeMillis()
)
