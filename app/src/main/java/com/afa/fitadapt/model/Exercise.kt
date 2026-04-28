package com.afa.fitadapt.model

/**
 * Modello dati per l'esercizio.
 */
data class Exercise(
    val id: String,
    val title: String,
    val category: String,
    val durationMinutes: Int?,
    val intensity: String,
    val description: String,
    val videoUri: String? = null,
    val movementInstructions: String,
    val commonErrors: List<String>
)
