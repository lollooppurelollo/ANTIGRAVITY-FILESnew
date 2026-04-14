// =============================================================
// AFA - Attività Fisica Adattata
// Modello dati per l'export JSON pseudonimizzato
// =============================================================
package com.afa.fitadapt.model

import kotlinx.serialization.Serializable

/**
 * Struttura principale dell'export JSON.
 * Contiene solo il patientCode come identificativo — nessun dato personale.
 */
@Serializable
data class ExportData(
    val exportVersion: String = "1.0",
    val exportDate: String,
    val patientCode: String,
    val trainingCards: List<ExportTrainingCard>,
    val scaleEntries: List<ExportScaleEntry>,
    val diaryEntries: List<ExportDiaryEntry>,
    val goals: List<ExportGoal>,
    val progress: ExportProgress
)

/**
 * Scheda di allenamento nell'export.
 */
@Serializable
data class ExportTrainingCard(
    val title: String,
    val status: String,
    val durationWeeks: Int? = null,
    val targetSessions: Int? = null,
    val sessions: List<ExportSession>
)

/**
 * Sessione di allenamento nell'export.
 */
@Serializable
data class ExportSession(
    val date: String,
    val completed: Boolean,
    val partial: Boolean = false,
    val durationMin: Int? = null,
    val perceivedEffort: Int? = null,
    val mood: Int? = null,
    val sleepQuality: Int? = null,
    val exercisesCompleted: List<String> = emptyList()
)

/**
 * Punteggi scale rapide nell'export.
 */
@Serializable
data class ExportScaleEntry(
    val date: String,
    val asthenia: Int? = null,
    val osteoarticularPain: Int? = null,
    val restDyspnea: Int? = null,
    val exertionDyspnea: Int? = null
)

/**
 * Voce del diario nell'export.
 */
@Serializable
data class ExportDiaryEntry(
    val date: String,
    val text: String
)

/**
 * Obiettivo nell'export.
 */
@Serializable
data class ExportGoal(
    val title: String,
    val targetType: String,
    val targetValue: Float,
    val currentValue: Float,
    val isActive: Boolean
)

/**
 * Riepilogo progressi nell'export.
 */
@Serializable
data class ExportProgress(
    val totalSessions: Int,
    val completedSessions: Int,
    val adherencePercent: Float,
    val totalMinutes: Int,
    val currentStreak: Int,
    val longestStreak: Int
)
