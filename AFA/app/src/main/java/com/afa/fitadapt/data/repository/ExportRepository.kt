// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Export dati (JSON + QR)
// =============================================================
package com.afa.fitadapt.data.repository

import android.content.Context
import com.afa.fitadapt.data.local.dao.ExportLogDao
import com.afa.fitadapt.data.local.entity.ExportLogEntity
import com.afa.fitadapt.model.ExportData
import com.afa.fitadapt.util.QrCodeGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Risultato di un'operazione di export.
 */
sealed class ExportResult {
    /** Export riuscito come QR code singolo */
    data class QrCode(val bitmap: android.graphics.Bitmap) : ExportResult()

    /** Export riuscito come file JSON (payload troppo grande per QR) */
    data class FileSaved(val filePath: String) : ExportResult()

    /** Errore durante l'export */
    data class Error(val message: String) : ExportResult()
}

/**
 * Repository per l'export dei dati pseudonimizzati.
 *
 * FLUSSO EXPORT:
 * 1. Raccoglie tutti i dati dal database
 * 2. Costruisce l'oggetto ExportData pseudonimizzato
 * 3. Serializza in JSON
 * 4. Prova a generare un QR code
 * 5. Se il payload è troppo grande, salva come file
 * 6. Registra l'export nel log
 */
@Suppress("unused") // Repository necessari per export completo (sessioni, diario, profilo, obiettivi)
@Singleton
class ExportRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportLogDao: ExportLogDao,
    private val sessionRepository: SessionRepository,
    private val diaryRepository: DiaryRepository,
    private val patientProfileRepository: PatientProfileRepository,
    private val goalRepository: GoalRepository
) {

    // Formato JSON leggibile
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /** Log degli export effettuati */
    @Suppress("unused") // Chiamata dall'ExportViewModel per mostrare lo storico export
    fun getExportLogs(): Flow<List<ExportLogEntity>> =
        exportLogDao.getAll()

    /** Ultimo export */
    suspend fun getLastExport(): ExportLogEntity? =
        exportLogDao.getLastExport()

    /**
     * Esegue l'export completo dei dati.
     *
     * @param exportData i dati già assemblati dal ViewModel
     * @return il risultato dell'export (QR code, file, o errore)
     */
    suspend fun performExport(exportData: ExportData): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                // Serializza in JSON
                val jsonString = json.encodeToString(exportData)

                // Calcola hash SHA-256 per integrità
                val hash = sha256(jsonString)

                // Prova a generare un QR code
                val qrBitmap = QrCodeGenerator.generateQrCode(jsonString)

                if (qrBitmap != null) {
                    // Il payload entra in un QR code singolo
                    logExport("QR", hash, countRecords(exportData))
                    ExportResult.QrCode(qrBitmap)
                } else {
                    // Fallback: salva come file JSON
                    val filePath = saveToFile(jsonString)
                    logExport("FILE", hash, countRecords(exportData))
                    ExportResult.FileSaved(filePath)
                }
            } catch (e: Exception) {
                ExportResult.Error(e.message ?: "Errore sconosciuto durante l'export")
            }
        }
    }

    /**
     * Salva il JSON come file nella cartella Documents dell'app.
     */
    private fun saveToFile(jsonString: String): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "AFA_export_${dateFormat.format(Date())}.json"

        val documentsDir = File(context.getExternalFilesDir(null), "exports")
        if (!documentsDir.exists()) documentsDir.mkdirs()

        val file = File(documentsDir, fileName)
        FileOutputStream(file).use { stream ->
            stream.write(jsonString.toByteArray(Charsets.UTF_8))
        }

        return file.absolutePath
    }

    /**
     * Registra l'export nel log locale.
     */
    private suspend fun logExport(format: String, hash: String, recordCount: Int) {
        exportLogDao.insert(
            ExportLogEntity(
                format = format,
                hash = hash,
                recordCount = recordCount
            )
        )
    }

    /**
     * Calcola lo SHA-256 di una stringa (per integrità dell'export).
     */
    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Conta il numero totale di record inclusi nell'export.
     */
    private fun countRecords(data: ExportData): Int {
        return data.trainingCards.sumOf { it.sessions.size } +
                data.scaleEntries.size +
                data.diaryEntries.size +
                data.goals.size
    }
}
