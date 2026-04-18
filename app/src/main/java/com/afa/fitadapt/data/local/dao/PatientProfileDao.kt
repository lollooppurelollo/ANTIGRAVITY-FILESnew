// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sul profilo paziente
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.PatientProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione del profilo locale della paziente.
 * Il profilo è un singleton (un solo record con id=1).
 */
@Dao
interface PatientProfileDao {

    // Ottieni il profilo (ce n'è sempre e solo uno)
    @Query("SELECT * FROM patient_profile WHERE id = 1")
    fun getProfile(): Flow<PatientProfileEntity?>

    // Ottieni il profilo in modo sincrono (per avvio app)
    @Query("SELECT * FROM patient_profile WHERE id = 1")
    suspend fun getProfileSync(): PatientProfileEntity?

    // Inserisci o sostituisci il profilo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(profile: PatientProfileEntity)

    // Aggiorna il profilo esistente
    @Update
    suspend fun update(profile: PatientProfileEntity)

    // Aggiorna solo il codice paziente
    @Query("UPDATE patient_profile SET patientCode = :code WHERE id = 1")
    suspend fun updatePatientCode(code: String)

    // Segna l'app come inizializzata
    @Query("UPDATE patient_profile SET appInitialized = 1 WHERE id = 1")
    suspend fun setAppInitialized()

    // Aggiorna lo stato della biometria
    @Query("UPDATE patient_profile SET biometricsEnabled = :enabled WHERE id = 1")
    suspend fun setBiometricsEnabled(enabled: Boolean)

    // Aggiorna l'ultimo accesso
    @Query("UPDATE patient_profile SET lastAccessAt = :timestamp WHERE id = 1")
    suspend fun updateLastAccess(timestamp: Long)
}
