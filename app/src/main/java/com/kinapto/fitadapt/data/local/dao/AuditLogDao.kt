// =============================================================
// KinApto - Attività Fisica Adattata
// DAO: Audit Log
// =============================================================
package com.kinapto.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kinapto.fitadapt.data.local.entity.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE patientStudyCode = :patientCode ORDER BY timestamp DESC")
    fun getByPatient(patientCode: String): Flow<List<AuditLogEntity>>

    @Query("DELETE FROM audit_logs WHERE timestamp < :threshold")
    suspend fun clearOldLogs(threshold: Long)
}
