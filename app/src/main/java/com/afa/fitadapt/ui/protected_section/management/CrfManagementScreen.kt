package com.afa.fitadapt.ui.protected_section.management

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrfExportScreen(
    viewModel: CrfManagementViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.exportState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Esporta CRF") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.qrBitmap == null && !state.isLoading) {
                Icon(Icons.Outlined.FileUpload, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Generazione CRF", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Raccoglie tutti i dati della paziente, li cifra e genera una sequenza di QR code per l'importazione ospedaliera.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.generateCrfExport() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Inizia Esportazione")
                }
            } else if (state.isLoading) {
                CircularProgressIndicator()
                Text("Elaborazione dati...", modifier = Modifier.padding(top = 16.dp))
            } else if (state.qrBitmap != null) {
                Text("Scansiona i QR code", style = MaterialTheme.typography.titleLarge)
                Text(
                    "QR ${state.currentChunkIndex + 1} di ${state.chunks.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.size(280.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Image(
                        bitmap = state.qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code CRF",
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = { viewModel.prevChunk() },
                        enabled = state.currentChunkIndex > 0
                    ) {
                        Icon(Icons.Outlined.ChevronLeft, null)
                        Text("Indietro")
                    }
                    
                    TextButton(
                        onClick = { viewModel.nextChunk() },
                        enabled = state.currentChunkIndex < state.chunks.size - 1
                    ) {
                        Text("Avanti")
                        Icon(Icons.Outlined.ChevronRight, null)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { viewModel.generateCrfExport() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Rigenera export")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Scansionare tutti i QR con KinApto in modalità 'Importa CRF' sul dispositivo ospedaliero.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrfImportScreen(
    viewModel: CrfManagementViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.importState.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    
    var showScanner by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importa CRF") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.reconstructedCrf == null) {
                Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (state.exportId != null) {
                    Text("Paziente: ${state.patientStudyCode}", fontWeight = FontWeight.Bold)
                    Text("Avanzamento: ${state.receivedChunks.size}/${state.totalChunks} QR acquisiti")
                    
                    LinearProgressIndicator(
                        progress = { state.receivedChunks.size.toFloat() / state.totalChunks.toFloat() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    
                    val missing = (1..state.totalChunks).filter { !state.receivedChunks.containsKey(it) }
                    if (missing.isNotEmpty()) {
                        Text("Mancano i QR: ${missing.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Text("Nessuna acquisizione in corso", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showScanner = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scansiona QR")
                }
                
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                // ANTEPRIMA CRF
                Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Text("CRF Ricostruita", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Paziente: ${state.reconstructedCrf!!.metadata.patientStudyCode}", fontWeight = FontWeight.Bold)
                        Text("Sessioni eseguite: ${state.reconstructedCrf!!.performedSessions.size}")
                        Text("Obiettivi: ${state.reconstructedCrf!!.goals.size}")
                        Text("Voci diario: ${state.reconstructedCrf!!.diaryEntries.size}")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (exportState.exportComplete) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Esportazione REDCap completata!", fontWeight = FontWeight.Bold)
                            Text("File salvato in: Download/KinApto/RedCapExports/", style = MaterialTheme.typography.bodySmall)
                            Text(exportState.redcapFilePath?.substringAfterLast("/") ?: "", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Chiudi") }
                    
                    TextButton(
                        onClick = { viewModel.resetImport() },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Outlined.Delete, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Elimina dati importati")
                    }
                } else {
                    Button(
                        onClick = { viewModel.exportToRedCap() },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.Outlined.FileDownload, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Esporta file REDCap")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = { viewModel.resetImport() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Annulla e resetta")
                    }
                }
            }
        }
    }
    
    if (showScanner) {
        com.afa.fitadapt.ui.components.QrCodeScanner(
            onQrCodeScanned = { qr ->
                viewModel.processScannedQr(qr)
                showScanner = false
            },
            onDismiss = { showScanner = false }
        )
    }
}
