// =============================================================
// KinApto - Attività Fisica Adattata
// Entity: Audit Log per azioni amministrative
// =============================================================
package com.kinapto.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registra azioni sensibili effettuate nell'area protetta.
 * Requisito di sicurezza per il monitoraggio clinico.
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String, // "EXPORT_CRF", "IMPORT_CRF", "DELETE_IMPORT", "REDCAP_GEN"
    val patientStudyCode: String,
    val exportId: String? = null,
    val details: String? = null,
    val success: Boolean = true
)
