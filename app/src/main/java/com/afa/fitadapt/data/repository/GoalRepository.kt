// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Obiettivi
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.GoalDao
import com.afa.fitadapt.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per la gestione degli obiettivi.
 * Gli obiettivi sono configurabili e modificabili dalla sezione protetta.
 */
@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {

    /** Obiettivi attivi */
    fun getActiveGoals(): Flow<List<GoalEntity>> =
        goalDao.getActiveGoals()

    /** Tutti gli obiettivi (anche disattivati) */
    fun getAllGoals(): Flow<List<GoalEntity>> =
        goalDao.getAll()

    /** Obiettivi raggiunti */
    fun getCompletedGoals(): Flow<List<GoalEntity>> =
        goalDao.getCompletedGoals()

    /** Un obiettivo per ID */
    suspend fun getById(id: Long): GoalEntity? =
        goalDao.getById(id)

    /** Crea un nuovo obiettivo */
    suspend fun createGoal(goal: GoalEntity): Long =
        goalDao.insert(goal)

    /** Aggiorna un obiettivo */
    suspend fun updateGoal(goal: GoalEntity) =
        goalDao.update(goal)

    /** Elimina un obiettivo */
    suspend fun deleteGoal(goal: GoalEntity) =
        goalDao.delete(goal)

    /** Aggiorna il valore corrente */
    suspend fun updateProgress(id: Long, currentValue: Float) =
        goalDao.updateCurrentValue(id, currentValue)

    /** Attiva/disattiva un obiettivo */
    suspend fun setActive(id: Long, active: Boolean) =
        goalDao.setActive(id, active)
}
