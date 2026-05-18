package com.kinapto.fitadapt.ui.protected_section.management

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.data.local.dao.*
import com.kinapto.fitadapt.data.local.entity.AuditLogEntity
import com.kinapto.fitadapt.data.local.entity.ExportLogEntity
import com.kinapto.fitadapt.model.*
import com.kinapto.fitadapt.util.CrfCryptoUtils
import com.kinapto.fitadapt.util.QrCodeGenerator
import com.kinapto.fitadapt.util.RedCapExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import javax.inject.Inject

data class CrfExportUiState(
    val crf: KinAptoCRF? = null,
    val chunks: List<KinAptoCrfChunk> = emptyList(),
    val currentChunkIndex: Int = 0,
    val qrBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val exportComplete: Boolean = false,
    val redcapFilePath: String? = null
)

data class CrfImportUiState(
    val exportId: String? = null,
    val patientStudyCode: String? = null,
    val receivedChunks: Map<Int, KinAptoCrfChunk> = emptyMap(),
    val totalChunks: Int = 0,
    val reconstructedCrf: KinAptoCRF? = null,
    val error: String? = null,
    val importSuccess: Boolean = false
)

@HiltViewModel
class CrfManagementViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileDao: PatientProfileDao,
    private val goalDao: GoalDao,
    private val cardDao: TrainingCardDao,
    private val exerciseDao: ExerciseDao,
    private val sessionDao: SessionDao,
    private val diaryDao: DiaryDao,
    private val scaleDao: ScaleEntryDao,
    private val exportLogDao: ExportLogDao,
    private val scheduledSessionDao: ScheduledSessionDao,
    private val auditLogDao: AuditLogDao
) : ViewModel() {

    private val _exportState = MutableStateFlow(CrfExportUiState())
    val exportState = _exportState.asStateFlow()

    private val _importState = MutableStateFlow(CrfImportUiState())
    val importState = _importState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    // ── ESPORTAZIONE ──

    fun generateCrfExport() {
        viewModelScope.launch {
            _exportState.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = profileDao.getProfileSync() ?: throw Exception("Profilo non trovato")
                val goals = goalDao.getAll().first()
                val cards = cardDao.getAll().first()
                val sessions = sessionDao.getAllSessions().first()
                val diary = diaryDao.getAll().first()
                val scales = scaleDao.getAll().first()
                val scheduled = scheduledSessionDao.getAllScheduled().first()
                val auditLogs = auditLogDao.getByPatient(profile.patientCode).first()

                // Costruisci oggetto CRF
                val crf = KinAptoCRF(
                    metadata = CrfMetadata(
                        exportId = UUID.randomUUID().toString(),
                        patientStudyCode = profile.patientCode,
                        appVersion = "1.2.0-KinApto",
                        exportTimestamp = System.currentTimeMillis()
                    ),
                    patientProfile = CrfPatientProfile(
                        patientStudyCode = profile.patientCode,
                        createdAt = profile.createdAt,
                        appInitialized = profile.appInitialized,
                        biometricsEnabled = profile.biometricsEnabled,
                        lastAccessAt = profile.lastAccessAt
                    ),
                    goals = goals.map {
                        CrfGoal(it.id, it.title, it.description, it.targetType, it.targetValue, it.currentValue, it.silverValue, it.goldValue, it.parentGoalId, it.isActive, it.createdAt, it.updatedAt)
                    },
                    trainingProgram = cards.map { card ->
                        val cardWithEx = cardDao.getCardWithExercises(card.id).first()
                        CrfTrainingCard(card.id, card.title, card.startDate, card.endDate, card.status, card.orderIndex, 
                            cardWithEx?.cardExercises?.map { ce ->
                                CrfCardExercise(
                                    ce.cardExercise.exerciseId, 
                                    ce.exercise.name, 
                                    ce.exercise.category, 
                                    ce.cardExercise.customDurationSec, 
                                    ce.cardExercise.customRepetitions, 
                                    ce.cardExercise.customIntensity, 
                                    ce.cardExercise.orderIndex
                                )
                            } ?: emptyList()
                        )
                    },
                    scheduledCalendar = scheduled.map { 
                        CrfScheduledSession(it.id, it.date, it.startTime, it.title, it.recurrenceType, it.cardId)
                    },
                    performedSessions = sessions.map { session ->
                         val sessWithEx = sessionDao.getSessionWithExercises(session.id).first()
                         CrfPerformedSession(
                             id = session.id, 
                             cardId = session.cardId, 
                             date = session.date, 
                             completed = session.completed, 
                             partial = session.partial, 
                             actualDurationMin = session.actualDurationMin, 
                             perceivedEffort = session.perceivedEffort, 
                             mood = session.mood, 
                             sleepQuality = session.sleepQuality, 
                             notes = session.notes, 
                             exercises = sessWithEx?.exerciseCompletions?.map { 
                                 CrfPerformedExercise(
                                     exerciseId = it.cardExercise.cardExercise.exerciseId, 
                                     name = it.cardExercise.exercise.name, 
                                     completed = it.sessionExercise.completed
                                 )
                             } ?: emptyList()
                         )
                    },
                    scaleEntries = scales.map { CrfScaleEntry(it.id, it.date, it.asthenia, it.osteoarticularPain, it.restDyspnea, it.exertionDyspnea, it.createdAt) },
                    diaryEntries = diary.map { CrfDiaryEntry(it.id, it.date, it.text, it.createdAt) },
                    auditLog = auditLogs.map { CrfAuditEntry(it.timestamp, it.action, it.details) }
                )

                // Serializza, Comprimi, Cifra
                val fullJson = json.encodeToString(crf)
                val compressed = CrfCryptoUtils.compress(fullJson)
                val encrypted = CrfCryptoUtils.encrypt(compressed)
                val base64 = CrfCryptoUtils.encodeBase64(encrypted)
                val globalChecksum = CrfCryptoUtils.checksum(base64)

                // Split in chunks (circa 1500 caratteri per QR per sicurezza)
                val chunkSize = 1200
                val totalChunks = (base64.length + chunkSize - 1) / chunkSize
                val chunks = (0 until totalChunks).map { i ->
                    val start = i * chunkSize
                    val end = minOf(start + chunkSize, base64.length)
                    val part = base64.substring(start, end)
                    KinAptoCrfChunk(
                        export_id = crf.metadata.exportId,
                        patient_study_code = crf.metadata.patientStudyCode,
                        chunk_index = i + 1,
                        total_chunks = totalChunks,
                        chunk_checksum = CrfCryptoUtils.checksum(part),
                        global_checksum = globalChecksum,
                        payload = part
                    )
                }

                _exportState.update { it.copy(crf = crf, chunks = chunks, currentChunkIndex = 0, isLoading = false) }
                generateQrForCurrentChunk()

                // Log export
                exportLogDao.insert(ExportLogEntity(format = "QR_MULTI", hash = globalChecksum, recordCount = sessions.size))
                auditLogDao.insert(AuditLogEntity(
                    action = "EXPORT_CRF",
                    patientStudyCode = profile.patientCode,
                    exportId = crf.metadata.exportId,
                    details = "Export effettuato tramite QR Multiplo (${chunks.size} blocchi)"
                ))

            } catch (e: Exception) {
                _exportState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun nextChunk() {
        val next = _exportState.value.currentChunkIndex + 1
        if (next < _exportState.value.chunks.size) {
            _exportState.update { it.copy(currentChunkIndex = next) }
            generateQrForCurrentChunk()
        }
    }

    fun prevChunk() {
        val prev = _exportState.value.currentChunkIndex - 1
        if (prev >= 0) {
            _exportState.update { it.copy(currentChunkIndex = prev) }
            generateQrForCurrentChunk()
        }
    }

    private fun generateQrForCurrentChunk() {
        val state = _exportState.value
        if (state.chunks.isEmpty()) return
        val chunk = state.chunks[state.currentChunkIndex]
        val chunkJson = Json.encodeToString(chunk)
        
        // Usiamo generateSimpleQrCode perché abbiamo già fatto compressione/base64 noi
        // e vogliamo che il lettore legga il JSON del chunk direttamente
        val bitmap = QrCodeGenerator.generateSimpleQrCode(chunkJson, 800)
        _exportState.update { it.copy(qrBitmap = bitmap) }
    }

    // ── IMPORTAZIONE ──

    fun processScannedQr(qrContent: String) {
        try {
            val chunk = json.decodeFromString<KinAptoCrfChunk>(qrContent)
            // Nessuna verifica del tipo "type" se non strettamente necessario, 
            // ma KinAptoCrfChunk ha un valore di default.

            val currentImport = _importState.value
            
            // Verifica se è un nuovo export
            if (currentImport.exportId != null && currentImport.exportId != chunk.export_id) {
                // Nuova sessione di import? Chiedi o resetta se coerente
            }

            // Verifica checksum blocco
            if (CrfCryptoUtils.checksum(chunk.payload) != chunk.chunk_checksum) {
                throw Exception("Checksum blocco errato")
            }

            val newChunks = currentImport.receivedChunks.toMutableMap()
            newChunks[chunk.chunk_index] = chunk

            _importState.update { 
                it.copy(
                    exportId = chunk.export_id,
                    patientStudyCode = chunk.patient_study_code,
                    receivedChunks = newChunks,
                    totalChunks = chunk.total_chunks,
                    error = null
                ) 
            }

            // Verifica se abbiamo finito
            if (newChunks.size == chunk.total_chunks) {
                reconstructCrf()
            }
        } catch (e: Exception) {
            _importState.update { it.copy(error = e.message ?: "Errore lettura QR") }
        }
    }

    private fun reconstructCrf() {
        viewModelScope.launch {
            try {
                val state = _importState.value
                val total = state.totalChunks
                val sb = StringBuilder()
                for (i in 1..total) {
                    val chunk = state.receivedChunks[i] ?: throw Exception("Manca il blocco $i")
                    sb.append(chunk.payload)
                }

                val fullBase64 = sb.toString()
                
                // Verifica checksum globale
                val firstChunk = state.receivedChunks[1]!!
                if (CrfCryptoUtils.checksum(fullBase64) != firstChunk.global_checksum) {
                    throw Exception("Checksum globale non corrisponde. Dati corrotti.")
                }

                val encrypted = CrfCryptoUtils.decodeBase64(fullBase64)
                val compressed = CrfCryptoUtils.decrypt(encrypted)
                val fullJson = CrfCryptoUtils.decompress(compressed)
                
                val crf = json.decodeFromString<KinAptoCRF>(fullJson)
                _importState.update { it.copy(reconstructedCrf = crf, importSuccess = true) }

                // Log audit
                auditLogDao.insert(AuditLogEntity(
                    action = "IMPORT_CRF",
                    patientStudyCode = crf.metadata.patientStudyCode,
                    exportId = crf.metadata.exportId,
                    details = "Ricostruzione CRF completata con successo"
                ))
            } catch (e: Exception) {
                _importState.update { it.copy(error = "Errore ricostruzione: ${e.message}") }
            }
        }
    }

    // ── ESPORTAZIONE REDCAP ──

    fun exportToRedCap() {
        val crf = _importState.value.reconstructedCrf ?: _exportState.value.crf ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadsDir = File(context.getExternalFilesDir(null), "RedCapExports")
                val zipFile = RedCapExportUtils.generateRedCapZip(crf, downloadsDir)
                _exportState.update { it.copy(redcapFilePath = zipFile.absolutePath, exportComplete = true) }
                
                auditLogDao.insert(AuditLogEntity(
                    action = "REDCAP_GEN",
                    patientStudyCode = crf.metadata.patientStudyCode,
                    exportId = crf.metadata.exportId,
                    details = "Generato pacchetto ZIP per REDCap"
                ))
            } catch (e: Exception) {
                _exportState.update { it.copy(error = "Errore export REDCap: ${e.message}") }
            }
        }
    }

    fun resetImport() {
        val currentCrf = _importState.value.reconstructedCrf
        viewModelScope.launch {
            if (currentCrf != null) {
                auditLogDao.insert(AuditLogEntity(
                    action = "DELETE_IMPORT",
                    patientStudyCode = currentCrf.metadata.patientStudyCode,
                    exportId = currentCrf.metadata.exportId,
                    details = "Dati importati rimossi dal dispositivo"
                ))
            }
            _importState.update { CrfImportUiState() }
            _exportState.update { it.copy(exportComplete = false, redcapFilePath = null) }
        }
    }
}
