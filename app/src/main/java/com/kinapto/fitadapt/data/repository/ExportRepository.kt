// =============================================================
// KinApto - Attività Fisica Adattata
// Repository: Export dati (JSON + QR)
// =============================================================
package com.kinapto.fitadapt.data.repository

import android.content.Context
import com.kinapto.fitadapt.data.local.dao.AuditLogDao
import com.kinapto.fitadapt.data.local.dao.ExportLogDao
import com.kinapto.fitadapt.data.local.entity.AuditLogEntity
import com.kinapto.fitadapt.data.local.entity.ExportLogEntity
import com.kinapto.fitadapt.model.ExportData
import com.kinapto.fitadapt.model.KinAptoCRF
import com.kinapto.fitadapt.util.CrfCryptoUtils
import com.kinapto.fitadapt.util.QrCodeGenerator
import com.kinapto.fitadapt.util.RedCapExportUtils
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

    /** Export riuscito come sequenza di QR code */
    data class QrSequence(val bitmaps: List<android.graphics.Bitmap>) : ExportResult()

    /** Export riuscito come file (JSON o ZIP) */
    data class FileSaved(val filePath: String, val isZip: Boolean = false) : ExportResult()

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
    private val auditLogDao: AuditLogDao,
    private val sessionRepository: SessionRepository,
    private val diaryRepository: DiaryRepository,
    private val patientProfileRepository: PatientProfileRepository,
    private val goalRepository: GoalRepository,
    private val crfCryptoUtils: CrfCryptoUtils,
    private val qrCodeGenerator: QrCodeGenerator
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
     * @param crf l'oggetto KinAptoCRF completo per REDCap
     * @return il risultato dell'export
     */
    suspend fun performExport(exportData: ExportData, crf: KinAptoCRF): ExportResult {
        return withContext(Dispatchers.IO) {
            val exportId = crf.metadata.exportId
            val patientCode = crf.metadata.patientStudyCode
            
            try {
                // GCP COMPLIANCE: Audit Trail Iniziale
                auditLogDao.insert(AuditLogEntity(
                    action = "CRF_EXPORT_INITIATED",
                    patientStudyCode = patientCode,
                    exportId = exportId,
                    details = "Format: REQUESTED, Counts: ${getDetailedRecordCounts(exportData)}"
                ))

                // Serializza in JSON
                val jsonString = json.encodeToString(exportData)
                val hash = sha256(jsonString)

                // 1. Prova QR Singolo
                val qrBitmap = qrCodeGenerator.generateQrCode(jsonString)
                if (qrBitmap != null) {
                    logExport("QR", hash, countRecords(exportData))
                    auditLogDao.insert(AuditLogEntity(
                        action = "CRF_EXPORT_COMPLETED",
                        patientStudyCode = patientCode,
                        exportId = exportId,
                        details = "Format: QR, Hash: $hash",
                        success = true
                    ))
                    return@withContext ExportResult.QrCode(qrBitmap)
                }

                // 2. Prova Chunked QR
                val qrBitmaps = qrCodeGenerator.generateChunkedQrCodes(jsonString, exportId, patientCode)
                if (qrBitmaps.isNotEmpty()) {
                    logExport("QR_CHUNKED", hash, countRecords(exportData))
                    auditLogDao.insert(AuditLogEntity(
                        action = "CRF_EXPORT_COMPLETED",
                        patientStudyCode = patientCode,
                        exportId = exportId,
                        details = "Format: QR_CHUNKED (${qrBitmaps.size} QRs), Hash: $hash",
                        success = true
                    ))
                    return@withContext ExportResult.QrSequence(qrBitmaps)
                }

                // 3. Fallback: REDCap ZIP (Metodo più affidabile)
                val outputDir = File(context.getExternalFilesDir(null), "exports")
                val zipFile = RedCapExportUtils.generateRedCapZip(crf, outputDir, crfCryptoUtils)
                
                logExport("ZIP", hash, countRecords(exportData))
                auditLogDao.insert(AuditLogEntity(
                    action = "CRF_EXPORT_COMPLETED",
                    patientStudyCode = patientCode,
                    exportId = exportId,
                    details = "Format: ZIP, Hash: $hash",
                    success = true
                ))
                
                ExportResult.FileSaved(zipFile.absolutePath, isZip = true)

            } catch (e: Exception) {
                auditLogDao.insert(AuditLogEntity(
                    action = "CRF_EXPORT_FAILED",
                    patientStudyCode = patientCode,
                    exportId = exportId,
                    details = "Error: ${e.message}",
                    success = false
                ))
                ExportResult.Error(e.message ?: "Errore sconosciuto durante l'export")
            }
        }
    }

    /**
     * Salva il JSON come file nella cartella Documents dell'app.
     */
    private fun saveToFile(jsonString: String): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "KinApto_export_${dateFormat.format(Date())}.json"

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
     * Conta i record per tipo per l'Audit Trail.
     */
    private fun getDetailedRecordCounts(data: ExportData): String {
        val sessions = data.trainingCards.sumOf { it.sessions.size }
        val scales = data.scaleEntries.size
        val diary = data.diaryEntries.size
        return "sessions:$sessions, scales:$scales, diary:$diary"
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
