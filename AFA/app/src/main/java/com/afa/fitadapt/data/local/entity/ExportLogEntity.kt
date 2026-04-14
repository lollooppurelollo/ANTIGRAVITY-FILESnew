// =============================================================
// AFA - Attività Fisica Adattata
// Entity: Log degli export effettuati
// =============================================================
package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registra ogni export effettuato dall'app.
 * Serve per tracciabilità: ogni volta che i dati vengono esportati
 * (JSON o QR code), viene creato un record con dettagli.
 *
 * @property timestamp quando è stato effettuato l'export
 * @property format formato dell'export: "JSON", "QR", "QR_MULTI", "FILE"
 * @property hash hash SHA-256 del contenuto esportato (per integrità)
 * @property recordCount numero di record inclusi nell'export
 */
@Entity(tableName = "export_logs")
data class ExportLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    val format: String,

    val hash: String? = null,

    val recordCount: Int = 0
)
