// =============================================================
// KinApto - Attività Fisica Adattata
// Schermata: Export dati (JSON + QR code)
// =============================================================
package com.kinapto.fitadapt.ui.export

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kinapto.fitadapt.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.model.*
import com.kinapto.fitadapt.data.local.entity.ExportLogEntity
import com.kinapto.fitadapt.data.repository.*
import com.kinapto.fitadapt.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ──

data class ExportUiState(
    val isExporting: Boolean = false,
    val qrBitmaps: List<Bitmap> = emptyList(),
    val filePath: String? = null,
    val isZip: Boolean = false,
    val error: String? = null,
    val lastExport: ExportLogEntity? = null
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportRepository: ExportRepository,
    private val profileRepository: PatientProfileRepository,
    private val cardRepository: TrainingCardRepository,
    private val sessionRepository: SessionRepository,
    private val diaryRepository: DiaryRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(lastExport = exportRepository.getLastExport()) }
        }
    }

    fun performExport() {
        _uiState.update { it.copy(isExporting = true, qrBitmaps = emptyList(), filePath = null, error = null) }
        viewModelScope.launch {
            try {
                val profile = profileRepository.getProfileSync()
                val sessions = sessionRepository.getAllSessions().first()
                val diaryEntries = diaryRepository.getAllDiaryEntries().first()
                val scaleEntries = diaryRepository.getAllScaleEntries().first()
                val goals = goalRepository.getActiveGoals().first()
                val cards = cardRepository.getAllCards().first()

                val currentStreak = sessionRepository.calculateCurrentStreak()
                val longestStreak = sessionRepository.calculateLongestStreak()
                val completedCount = sessions.count { it.completed }
                val totalMinutes = sessions.filter { it.completed }.sumOf { it.actualDurationMin ?: 0 }

                val exportData = ExportData(
                    exportDate = DateUtils.toIsoString(System.currentTimeMillis()),
                    patientCode = profile?.patientCode ?: "N/A",
                    trainingCards = cards.map { card ->
                        val cardSessions = sessions.filter { it.cardId == card.id }
                        ExportTrainingCard(
                            title = card.title, status = card.status,
                            durationWeeks = card.durationWeeks, targetSessions = card.targetSessions,
                            sessions = cardSessions.map { s ->
                                ExportSession(
                                    date = DateUtils.toIsoString(s.date), completed = s.completed, partial = s.partial,
                                    durationMin = s.actualDurationMin, perceivedEffort = s.perceivedEffort,
                                    asthenia = s.asthenia, osteoarticularPain = s.osteoarticularPain,
                                    restDyspnea = s.restDyspnea, exertionDyspnea = s.exertionDyspnea,
                                    mood = s.mood, sleepQuality = s.sleepQuality,
                                    nausea = s.nausea, appetite = s.appetite, anxiety = s.anxiety,
                                    lymphoedema = s.lymphoedema, qualityOfLife = s.qualityOfLife,
                                    wellBeing = s.wellBeing, spo2 = s.spo2, heartRate = s.heartRate
                                )
                            }
                        )
                    },
                    scaleEntries = scaleEntries.map { s ->
                        ExportScaleEntry(
                            date = DateUtils.toIsoString(s.date), 
                            perceivedEffort = s.perceivedEffort,
                            asthenia = s.asthenia, osteoarticularPain = s.osteoarticularPain, 
                            restDyspnea = s.restDyspnea, exertionDyspnea = s.exertionDyspnea,
                            mood = s.mood, sleepQuality = s.sleepQuality,
                            nausea = s.nausea, appetite = s.appetite, anxiety = s.anxiety,
                            lymphoedema = s.lymphoedema, qualityOfLife = s.qualityOfLife,
                            wellBeing = s.wellBeing, spo2 = s.spo2, heartRate = s.heartRate
                        )
                    },
                    diaryEntries = diaryEntries.map { d -> ExportDiaryEntry(date = DateUtils.toIsoString(d.date), text = d.text) },
                    goals = goals.map { g -> ExportGoal(title = g.title, targetType = g.targetType, targetValue = g.targetValue, currentValue = g.currentValue, isActive = g.isActive) },
                    progress = ExportProgress(
                        totalSessions = sessions.size, completedSessions = completedCount,
                        adherencePercent = if (sessions.isNotEmpty()) (completedCount.toFloat() / sessions.size) * 100f else 0f,
                        totalMinutes = totalMinutes, currentStreak = currentStreak, longestStreak = longestStreak
                    )
                )

                    val crf = KinAptoCRF(
                        metadata = CrfMetadata(
                            patientStudyCode = profile?.patientCode ?: "PENDING",
                            exportId = "EXP_${System.currentTimeMillis()}",
                            appVersion = "1.1.0",
                            exportTimestamp = System.currentTimeMillis()
                        ),
                        patientProfile = CrfPatientProfile(
                            patientStudyCode = profile?.patientCode ?: "PENDING",
                            createdAt = profile?.createdAt ?: 0L,
                            appInitialized = profile?.appInitialized ?: false,
                            biometricsEnabled = profile?.biometricsEnabled ?: false,
                            lastAccessAt = profile?.lastAccessAt
                        ),
                        goals = goals.map {
                            CrfGoal(it.id, it.title, it.description, it.targetType, it.targetValue, it.currentValue, it.silverValue, it.goldValue, it.parentGoalId, it.isActive, it.createdAt, it.updatedAt)
                        },
                        trainingProgram = cards.map { card ->
                            CrfTrainingCard(card.id, card.title, card.startDate, card.endDate, card.status, card.orderIndex, emptyList())
                        },
                        scheduledCalendar = emptyList(),
                        performedSessions = sessions.map { s ->
                            CrfPerformedSession(
                                id = s.id, cardId = s.cardId, date = s.date, completed = s.completed, partial = s.partial,
                                actualDurationMin = s.actualDurationMin, perceivedEffort = s.perceivedEffort,
                                asthenia = s.asthenia, osteoarticularPain = s.osteoarticularPain,
                                restDyspnea = s.restDyspnea, exertionDyspnea = s.exertionDyspnea,
                                mood = s.mood, sleepQuality = s.sleepQuality,
                                nausea = s.nausea, appetite = s.appetite, anxiety = s.anxiety,
                                lymphoedema = s.lymphoedema, qualityOfLife = s.qualityOfLife,
                                wellBeing = s.wellBeing, spo2 = s.spo2, heartRate = s.heartRate,
                                notes = s.notes, exercises = emptyList()
                            )
                        },
                        scaleEntries = scaleEntries.map { s ->
                            CrfScaleEntry(
                                s.id, s.date, s.perceivedEffort, s.asthenia, s.osteoarticularPain,
                                s.restDyspnea, s.exertionDyspnea, s.mood, s.sleepQuality, s.nausea,
                                s.appetite, s.anxiety, s.lymphoedema, s.qualityOfLife, s.wellBeing,
                                s.spo2, s.heartRate, s.createdAt
                            )
                        },
                        diaryEntries = diaryEntries.map { d -> CrfDiaryEntry(d.id, d.date, d.text, d.date) }
                    )

                    when (val result = exportRepository.performExport(exportData, crf)) {
                    is ExportResult.QrSequence -> _uiState.update { 
                        it.copy(isExporting = false, qrBitmaps = result.bitmaps) 
                    }
                    is ExportResult.QrCode -> _uiState.update { 
                        it.copy(isExporting = false, qrBitmaps = listOf(result.bitmap)) 
                    }
                    is ExportResult.FileSaved -> _uiState.update { 
                        it.copy(isExporting = false, filePath = result.filePath, isZip = result.isZip) 
                    }
                    is ExportResult.Error -> _uiState.update { 
                        it.copy(isExporting = false, error = result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isExporting = false, error = e.message ?: "Errore") }
            }
        }
    }
}

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    exportViewModel: ExportViewModel, 
    onBack: () -> Unit,
    onShareExport: (String) -> Unit
) {
    val uiState by exportViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.export_title), color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back), tint = MaterialTheme.colorScheme.onSurface) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCode, stringResource(R.string.export_title), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.export_header), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.export_disclaimer), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { exportViewModel.performExport() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isExporting,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.export_in_progress))
                } else {
                    Icon(Icons.Outlined.FileDownload, stringResource(R.string.export_button))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.export_button))
                }
            }

            // Sezione QR Code con supporto sequenza (Problem 3)
            uiState.qrBitmaps.let { bitmaps ->
                if (bitmaps.isNotEmpty()) {
                    var currentIndex by remember { mutableIntStateOf(0) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (bitmaps.size > 1) "QR Code ${currentIndex + 1} di ${bitmaps.size}" else "QR Code Esportazione",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Image(
                                bitmap = bitmaps[currentIndex].asImageBitmap(),
                                contentDescription = "QR Code ${currentIndex + 1}",
                                modifier = Modifier
                                    .size(280.dp)
                                    .padding(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        if (bitmaps.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (currentIndex > 0) currentIndex-- },
                                    enabled = currentIndex > 0
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Precedente")
                                }
                                
                                Text(
                                    text = "${currentIndex + 1} / ${bitmaps.size}",
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                IconButton(
                                    onClick = { if (currentIndex < bitmaps.size - 1) currentIndex++ },
                                    enabled = currentIndex < bitmaps.size - 1
                                ) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Successivo")
                                }
                            }
                        }
                        
                        Text(
                            text = "Scansiona tutti i QR in sequenza su REDCap",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // ZIP Export Option (Problem 3 - Priorità visiva)
            uiState.filePath?.let { path ->
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Esportazione ZIP completata",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "File salvato in:\n$path",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        Button(
                            onClick = { onShareExport(path) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Condividi / Invia ZIP")
                        }
                    }
                }
            }

            // Errore
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(stringResource(R.string.label_error, error), modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Ultimo export
            uiState.lastExport?.let { log ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    stringResource(R.string.export_last_log, DateUtils.toDisplayString(log.timestamp), log.format, log.recordCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
