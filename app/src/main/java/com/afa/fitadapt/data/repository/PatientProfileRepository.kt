// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Profilo paziente
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.PatientProfileDao
import com.afa.fitadapt.data.local.entity.PatientProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per il profilo locale della paziente.
 * Il profilo è un singleton — c'è sempre un solo record.
 */
@Singleton
class PatientProfileRepository @Inject constructor(
    private val profileDao: PatientProfileDao
) {

    /** Il profilo come Flow (aggiornamenti automatici) */
    fun getProfile(): Flow<PatientProfileEntity?> =
        profileDao.getProfile()

    /** Il profilo in modo sincrono */
    suspend fun getProfileSync(): PatientProfileEntity? =
        profileDao.getProfileSync()

    /** Crea o aggiorna il profilo */
    suspend fun saveProfile(profile: PatientProfileEntity) =
        profileDao.insertOrReplace(profile)

    /** Aggiorna il codice paziente (dalla sezione protetta) */
    suspend fun updatePatientCode(code: String) {
        val existing = profileDao.getProfileSync()
        if (existing == null) {
            profileDao.insertOrReplace(PatientProfileEntity(patientCode = code))
        } else {
            profileDao.updatePatientCode(code)
        }
    }

    /** Segna l'app come inizializzata */
    suspend fun setAppInitialized() =
        profileDao.setAppInitialized()

    /** Abilita/disabilita biometria */
    suspend fun setBiometricsEnabled(enabled: Boolean) =
        profileDao.setBiometricsEnabled(enabled)

    /** Registra un accesso */
    suspend fun recordAccess() =
        profileDao.updateLastAccess(System.currentTimeMillis())

    /**
     * Inizializza il profilo al primo avvio.
     * Crea il record singleton se non esiste.
     */
    suspend fun initializeIfNeeded() {
        val existing = profileDao.getProfileSync()
        if (existing == null) {
            profileDao.insertOrReplace(PatientProfileEntity())
        }
    }
}
