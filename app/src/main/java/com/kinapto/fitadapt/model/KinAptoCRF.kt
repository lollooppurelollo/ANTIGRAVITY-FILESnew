package com.kinapto.fitadapt.model

import kotlinx.serialization.Serializable

/**
 * Struttura completa della CRF (Case Report Form) per KinApto.
 * Segue i requisiti per l'esportazione verso centri clinici/REDCap.
 */
@Serializable
data class KinAptoCRF(
    val metadata: CrfMetadata,
    val patientProfile: CrfPatientProfile,
    val goals: List<CrfGoal>,
    val trainingProgram: List<CrfTrainingCard>,
    val scheduledCalendar: List<CrfScheduledSession>,
    val performedSessions: List<CrfPerformedSession>,
    val scaleEntries: List<CrfScaleEntry>,
    val diaryEntries: List<CrfDiaryEntry>,
    val adaptationLogs: List<CrfAdaptationLog> = emptyList(),
    val auditLog: List<CrfAuditEntry> = emptyList()
)

@Serializable
data class CrfAdaptationLog(
    val timestamp: Long,
    val originalCardId: Long?,
    val newCardId: Long?,
    val triggerDescription: String,
    val actionTaken: String,
    val notified: Boolean
)

@Serializable
data class CrfMetadata(
    val exportId: String,
    val patientStudyCode: String,
    val appVersion: String,
    val crfSchemaVersion: String = "1.0",
    val exportTimestamp: Long,
    val deviceModel: String? = null
)

@Serializable
data class CrfPatientProfile(
    val patientStudyCode: String,
    val createdAt: Long,
    val appInitialized: Boolean,
    val biometricsEnabled: Boolean,
    val lastAccessAt: Long?
)

@Serializable
data class CrfGoal(
    val id: Long,
    val title: String,
    val description: String?,
    val targetType: String,
    val targetValue: Float,
    val currentValue: Float,
    val silverValue: Float?,
    val goldValue: Float?,
    val parentGoalId: Long?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class CrfTrainingCard(
    val id: Long,
    val title: String,
    val startDate: Long?,
    val endDate: Long?,
    val status: String,
    val orderIndex: Int,
    val exercises: List<CrfCardExercise>
)

@Serializable
data class CrfCardExercise(
    val exerciseId: Long,
    val name: String,
    val category: String,
    val customDurationSec: Int?,
    val customRepetitions: Int?,
    val customIntensity: String?,
    val orderIndex: Int
)

@Serializable
data class CrfScheduledSession(
    val id: Long,
    val date: Long,
    val startTime: String,
    val title: String,
    val recurrenceType: String,
    val cardId: Long?
)

@Serializable
data class CrfPerformedSession(
    val id: Long,
    val cardId: Long,
    val date: Long,
    val completed: Boolean,
    val partial: Boolean,
    val actualDurationMin: Int?,
    val perceivedEffort: Int?, // RPE
    val asthenia: Int? = null,
    val osteoarticularPain: Int? = null,
    val restDyspnea: Int? = null,
    val exertionDyspnea: Int? = null,
    val mood: Int?,
    val sleepQuality: Int?,
    val nausea: Int? = null,
    val appetite: Int? = null,
    val anxiety: Int? = null,
    val lymphoedema: Int? = null,
    val qualityOfLife: Int? = null,
    val wellBeing: Int? = null,
    val spo2: Int? = null,
    val heartRate: Int? = null,
    val notes: String?,
    val symptomsBefore: String? = null, // Da aggiungere se disponibili o mappare da diary/scale
    val symptomsAfter: String? = null,
    val exercises: List<CrfPerformedExercise>
)

@Serializable
data class CrfPerformedExercise(
    val exerciseId: Long,
    val name: String,
    val completed: Boolean,
    val durationSec: Int? = null,
    val repetitions: Int? = null,
    val intensity: String? = null
)

@Serializable
data class CrfScaleEntry(
    val id: Long,
    val date: Long,
    val perceivedEffort: Int? = null,
    val asthenia: Int?,
    val osteoarticularPain: Int?,
    val restDyspnea: Int?,
    val exertionDyspnea: Int?,
    val mood: Int? = null,
    val sleepQuality: Int? = null,
    val nausea: Int? = null,
    val appetite: Int? = null,
    val anxiety: Int? = null,
    val lymphoedema: Int? = null,
    val qualityOfLife: Int? = null,
    val wellBeing: Int? = null,
    val spo2: Int? = null,
    val heartRate: Int? = null,
    val createdAt: Long
)

@Serializable
data class CrfDiaryEntry(
    val id: Long,
    val date: Long,
    val text: String,
    val createdAt: Long
)

@Serializable
data class CrfAuditEntry(
    val timestamp: Long,
    val action: String,
    val details: String?
)

/**
 * Struttura di un singolo chunk QR per la CRF.
 */
@Serializable
data class KinAptoCrfChunk(
    val type: String = "KINAPTO_CRF_CHUNK_V1",
    val export_id: String,
    val patient_study_code: String,
    val chunk_index: Int,
    val total_chunks: Int,
    val chunk_checksum: String,
    val global_checksum: String,
    val payload: String // Base64 encoded, encrypted, gzipped part
)
