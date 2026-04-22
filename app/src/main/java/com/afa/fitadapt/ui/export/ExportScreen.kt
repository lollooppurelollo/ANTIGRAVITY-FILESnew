// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Export dati (JSON + QR code)
// =============================================================
package com.afa.fitadapt.ui.export

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ExportLogEntity
import com.afa.fitadapt.data.repository.ExportRepository
import com.afa.fitadapt.data.repository.ExportResult
import com.afa.fitadapt.model.*
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.data.repository.DiaryRepository
import com.afa.fitadapt.data.repository.GoalRepository
import com.afa.fitadapt.data.repository.PatientProfileRepository
import com.afa.fitadapt.data.repository.TrainingCardRepository
import com.afa.fitadapt.util.DateUtils
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
    val qrBitmap: Bitmap? = null,
    val filePath: String? = null,
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
        _uiState.update { it.copy(isExporting = true, qrBitmap = null, filePath = null, error = null) }
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
                                    mood = s.mood, sleepQuality = s.sleepQuality
                                )
                            }
                        )
                    },
                    scaleEntries = scaleEntries.map { s ->
                        ExportScaleEntry(date = DateUtils.toIsoString(s.date), asthenia = s.asthenia, osteoarticularPain = s.osteoarticularPain, restDyspnea = s.restDyspnea, exertionDyspnea = s.exertionDyspnea)
                    },
                    diaryEntries = diaryEntries.map { d -> ExportDiaryEntry(date = DateUtils.toIsoString(d.date), text = d.text) },
                    goals = goals.map { g -> ExportGoal(title = g.title, targetType = g.targetType, targetValue = g.targetValue, currentValue = g.currentValue, isActive = g.isActive) },
                    progress = ExportProgress(
                        totalSessions = sessions.size, completedSessions = completedCount,
                        adherencePercent = if (sessions.isNotEmpty()) (completedCount.toFloat() / sessions.size) * 100f else 0f,
                        totalMinutes = totalMinutes, currentStreak = currentStreak, longestStreak = longestStreak
                    )
                )

                when (val result = exportRepository.performExport(exportData)) {
                    is ExportResult.QrCode -> _uiState.update { it.copy(isExporting = false, qrBitmap = result.bitmap) }
                    is ExportResult.FileSaved -> _uiState.update { it.copy(isExporting = false, filePath = result.filePath) }
                    is ExportResult.Error -> _uiState.update { it.copy(isExporting = false, error = result.message) }
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
fun ExportScreen(exportViewModel: ExportViewModel, onBack: () -> Unit) {
    val uiState by exportViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export dati", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = MaterialTheme.colorScheme.onSurface) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCode, "Export", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Esporta i tuoi dati", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text("I dati esportati contengono solo il codice paziente.\nNessun dato personale viene incluso.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

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
                    Text("Esportazione...")
                } else {
                    Icon(Icons.Outlined.FileDownload, "Esporta")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Esporta ora")
                }
            }

            // Risultato: QR code
            uiState.qrBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("QR Code generato ✅", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = "QR Code export", modifier = Modifier.size(280.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Inquadra il QR code per importare i dati", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Risultato: file
            uiState.filePath?.let { path ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("File salvato ✅", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Il payload era troppo grande per un QR code. Il file JSON è stato salvato in:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(path, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Errore
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text("Errore: $error", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Ultimo export
            uiState.lastExport?.let { log ->
                Spacer(modifier = Modifier.height(24.dp))
                Text("Ultimo export: ${DateUtils.toDisplayString(log.timestamp)} (${log.format}, ${log.recordCount} record)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
