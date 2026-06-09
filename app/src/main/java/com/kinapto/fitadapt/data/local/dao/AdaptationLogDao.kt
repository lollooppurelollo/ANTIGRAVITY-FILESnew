package com.kinapto.fitadapt.data.local.dao

import androidx.room.*
import com.kinapto.fitadapt.data.local.entity.AdaptationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdaptationLogDao {
    @Query("SELECT * FROM adaptation_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AdaptationLogEntity>>

    @Query("SELECT * FROM adaptation_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsSync(): List<AdaptationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AdaptationLogEntity): Long

    @Query("DELETE FROM adaptation_logs")
    suspend fun deleteAll()
}
