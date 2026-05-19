// =============================================================
// KinApto - Attività Fisica Adattata
// Repository: Scale rapide bio-feedback
// =============================================================
package com.kinapto.fitadapt.data.repository

import com.kinapto.fitadapt.data.local.dao.ScaleEntryDao
import com.kinapto.fitadapt.data.local.entity.ScaleEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScaleEntryRepository @Inject constructor(
    private val scaleEntryDao: ScaleEntryDao
) {
    fun getAllEntries(): Flow<List<ScaleEntryEntity>> = scaleEntryDao.getAll()

    suspend fun getLatestEntry(): ScaleEntryEntity? = scaleEntryDao.getLatest()

    suspend fun insertEntry(entry: ScaleEntryEntity) = scaleEntryDao.insert(entry)

    suspend fun getAveragePainSince(sinceDate: Long): Float? = scaleEntryDao.averagePainSince(sinceDate)

    suspend fun getAverageAstheniaSince(sinceDate: Long): Float? = scaleEntryDao.averageAstheniaSince(sinceDate)

    suspend fun getAverageRestDyspneaSince(sinceDate: Long): Float? = scaleEntryDao.averageRestDyspneaSince(sinceDate)

    suspend fun getAverageExertionDyspneaSince(sinceDate: Long): Float? = scaleEntryDao.averageExertionDyspneaSince(sinceDate)

    suspend fun getAverageNauseaSince(sinceDate: Long): Float? = scaleEntryDao.averageNauseaSince(sinceDate)

    suspend fun getAverageAppetiteSince(sinceDate: Long): Float? = scaleEntryDao.averageAppetiteSince(sinceDate)

    suspend fun getAverageAnxietySince(sinceDate: Long): Float? = scaleEntryDao.averageAnxietySince(sinceDate)

    suspend fun getAverageMoodSince(sinceDate: Long): Float? = scaleEntryDao.averageMoodSince(sinceDate)

    suspend fun getAverageSleepQualitySince(sinceDate: Long): Float? = scaleEntryDao.averageSleepQualitySince(sinceDate)

    suspend fun getAverageEffortSince(sinceDate: Long): Float? = scaleEntryDao.averageEffortSince(sinceDate)

    suspend fun getAverageLymphoedemaSince(sinceDate: Long): Float? = scaleEntryDao.averageLymphoedemaSince(sinceDate)

    suspend fun getAverageQolSince(sinceDate: Long): Float? = scaleEntryDao.averageQolSince(sinceDate)

    suspend fun getAverageWellBeingSince(sinceDate: Long): Float? = scaleEntryDao.averageWellBeingSince(sinceDate)

    suspend fun getAverageSpo2Since(sinceDate: Long): Float? = scaleEntryDao.averageSpo2Since(sinceDate)

    suspend fun getAverageHeartRateSince(sinceDate: Long): Float? = scaleEntryDao.averageHeartRateSince(sinceDate)
}
