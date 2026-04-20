package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.ScheduledSessionDao
import com.afa.fitadapt.data.local.entity.ScheduledSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(
    private val scheduledSessionDao: ScheduledSessionDao
) {
    fun getAllScheduled(): Flow<List<ScheduledSessionEntity>> =
        scheduledSessionDao.getAllScheduled()

    fun getScheduledInRange(start: Long, end: Long): Flow<List<ScheduledSessionEntity>> =
        scheduledSessionDao.getScheduledInRange(start, end)

    suspend fun addScheduledSession(session: ScheduledSessionEntity) =
        scheduledSessionDao.insert(session)

    suspend fun updateScheduledSession(session: ScheduledSessionEntity) =
        scheduledSessionDao.update(session)

    suspend fun getScheduledById(id: Long): ScheduledSessionEntity? =
        scheduledSessionDao.getById(id)

    suspend fun deleteScheduledSession(session: ScheduledSessionEntity) =
        scheduledSessionDao.delete(session)
}
