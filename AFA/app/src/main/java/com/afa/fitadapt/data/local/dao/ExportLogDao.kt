// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sul log degli export
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.afa.fitadapt.data.local.entity.ExportLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la registrazione e consultazione degli export effettuati.
 * Ogni export (JSON, QR, file) viene tracciato qui.
 */
@Dao
interface ExportLogDao {

    // Ottieni tutti gli export, dal più recente
    @Query("SELECT * FROM export_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ExportLogEntity>>

    // Ottieni gli ultimi N export
    @Query("SELECT * FROM export_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getLatest(limit: Int): Flow<List<ExportLogEntity>>

    // Inserisci un nuovo record di export
    @Insert
    suspend fun insert(log: ExportLogEntity): Long

    // Conta gli export totali
    @Query("SELECT COUNT(*) FROM export_logs")
    suspend fun count(): Int

    // Ottieni l'ultimo export effettuato
    @Query("SELECT * FROM export_logs ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastExport(): ExportLogEntity?
}
