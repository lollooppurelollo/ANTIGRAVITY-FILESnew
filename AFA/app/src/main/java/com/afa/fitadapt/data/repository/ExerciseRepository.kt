// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Esercizi
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.ExerciseDao
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per la gestione degli esercizi nella libreria.
 *
 * Fornisce un'interfaccia pulita tra ViewModel e DAO.
 * Tutti gli esercizi sono precaricati al primo avvio (vedi DatabaseSeeder).
 * Nuovi esercizi possono essere aggiunti dalla sezione protetta.
 */
@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    /** Ottieni tutti gli esercizi attivi come Flow (si aggiorna in automatico) */
    fun getAllActive(): Flow<List<ExerciseEntity>> =
        exerciseDao.getAllActive()

    /** Ottieni gli esercizi filtrati per categoria */
    fun getByCategory(category: String): Flow<List<ExerciseEntity>> =
        exerciseDao.getByCategory(category)

    /** Cerca esercizi per nome */
    fun searchByName(query: String): Flow<List<ExerciseEntity>> =
        exerciseDao.searchByName(query)

    /** Ottieni un esercizio per ID */
    suspend fun getById(id: Long): ExerciseEntity? =
        exerciseDao.getById(id)

    /** Ottieni le categorie che hanno esercizi attivi */
    fun getActiveCategories(): Flow<List<String>> =
        exerciseDao.getActiveCategories()

    /** Aggiungi un nuovo esercizio (dalla sezione protetta) */
    suspend fun addExercise(exercise: ExerciseEntity): Long =
        exerciseDao.insert(exercise)

    /** Aggiorna un esercizio esistente */
    suspend fun updateExercise(exercise: ExerciseEntity) =
        exerciseDao.update(exercise)

    /** Disattiva un esercizio (soft delete — non lo cancella) */
    suspend fun deactivateExercise(id: Long) =
        exerciseDao.deactivate(id)

    /** Riattiva un esercizio precedentemente disattivato */
    suspend fun activateExercise(id: Long) =
        exerciseDao.activate(id)

    /** Verifica se la libreria contiene esercizi (per il seeding) */
    suspend fun isEmpty(): Boolean =
        exerciseDao.count() == 0
}
