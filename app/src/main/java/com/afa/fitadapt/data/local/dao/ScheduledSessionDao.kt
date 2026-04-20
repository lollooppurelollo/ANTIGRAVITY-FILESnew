package com.afa.fitadapt.data.local.dao

import androidx.room.*
import com.afa.fitadapt.data.local.entity.ScheduledSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledSessionDao {
    @Query("SELECT * FROM scheduled_sessions ORDER BY date ASC")
    fun getAllScheduled(): Flow<List<ScheduledSessionEntity>>

    @Query("SELECT * FROM scheduled_sessions WHERE date >= :start AND date <= :end")
    fun getScheduledInRange(start: Long, end: Long): Flow<List<ScheduledSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ScheduledSessionEntity): Long

    @Update
    suspend fun update(session: ScheduledSessionEntity)

    @Query("SELECT * FROM scheduled_sessions WHERE id = :id")
    suspend fun getById(id: Long): ScheduledSessionEntity?

    @Delete
    suspend fun delete(session: ScheduledSessionEntity)
}
