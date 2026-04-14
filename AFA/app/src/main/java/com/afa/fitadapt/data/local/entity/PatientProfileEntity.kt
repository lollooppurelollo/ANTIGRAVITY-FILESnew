// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Profilo locale della paziente
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Profilo locale della paziente — singleton (un solo record, id=1).
 * Contiene il codice paziente e lo stato dell'app.
 *
 * Il patientCode viene inserito manualmente dalla sezione protetta.
 * NON contiene dati personali (nome, email, ecc.) per privacy.
 *
 * @property id sempre 1 (singleton)
 * @property patientCode codice identificativo pseudonimizzato (es. "AFA-0001")
 * @property createdAt timestamp della prima configurazione
 * @property appInitialized se true, il setup iniziale è stato completato
 * @property biometricsEnabled se true, l'accesso biometrico è attivo
 * @property protectedSectionConfigured se true, la password è stata impostata
 * @property lastAccessAt ultimo accesso all'app (timestamp, opzionale)
 */
@Entity(tableName = "patient_profile")
data class PatientProfileEntity(
    @PrimaryKey
    val id: Long = 1,

    val patientCode: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val appInitialized: Boolean = false,

    val biometricsEnabled: Boolean = true,

    val protectedSectionConfigured: Boolean = false,

    val lastAccessAt: Long? = null
)
