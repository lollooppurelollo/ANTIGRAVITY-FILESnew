package com.kinapto.fitadapt.ui.protected_section.management

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R
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
                title = { Text(stringResource(R.string.crf_export_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } }
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
                Text(stringResource(R.string.crf_export_header), style = MaterialTheme.typography.headlineSmall)
                Text(
                    stringResource(R.string.crf_export_description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.generateCrfExport() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.crf_export_start))
                }
            } else if (state.isLoading) {
                CircularProgressIndicator()
                Text(stringResource(R.string.crf_export_loading), modifier = Modifier.padding(top = 16.dp))
            } else if (state.qrBitmap != null) {
                Text(stringResource(R.string.crf_export_scan_instruction), style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.crf_export_chunk_info, state.currentChunkIndex + 1, state.chunks.size),
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
                        Text(stringResource(R.string.label_back))
                    }
                    
                    TextButton(
                        onClick = { viewModel.nextChunk() },
                        enabled = state.currentChunkIndex < state.chunks.size - 1
                    ) {
                        Text(stringResource(R.string.crf_export_next))
                        Icon(Icons.Outlined.ChevronRight, null)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { viewModel.generateCrfExport() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.crf_export_regenerate))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.crf_export_footer_note),
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
    val context = LocalContext.current
    
    var showScanner by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                showScanner = true
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crf_import_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } }
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
                    Text(stringResource(R.string.crf_import_patient, state.patientStudyCode ?: ""), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.crf_import_progress, state.receivedChunks.size, state.totalChunks))
                    
                    LinearProgressIndicator(
                        progress = { state.receivedChunks.size.toFloat() / state.totalChunks.toFloat() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    
                    val missing = (1..state.totalChunks).filter { !state.receivedChunks.containsKey(it) }
                    if (missing.isNotEmpty()) {
                        Text(stringResource(R.string.crf_import_missing, missing.joinToString(", ")), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Text(stringResource(R.string.crf_import_no_session), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            showScanner = true
                        } else {
                            showPermissionRationale = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.crf_import_scan_button))
                }
                
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                // ANTEPRIMA CRF
                Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.crf_import_success_title), style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.crf_import_patient, state.reconstructedCrf!!.metadata.patientStudyCode), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.crf_import_summary_sessions, state.reconstructedCrf!!.performedSessions.size))
                        Text(stringResource(R.string.crf_import_summary_goals, state.reconstructedCrf!!.goals.size))
                        Text(stringResource(R.string.crf_import_summary_diary, state.reconstructedCrf!!.diaryEntries.size))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (exportState.exportComplete) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.crf_import_redcap_success), fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.crf_import_save_path, "Download/KinApto/RedCapExports/"), style = MaterialTheme.typography.bodySmall)
                            Text(exportState.redcapFilePath?.substringAfterLast("/") ?: "", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.label_close)) }
                    
                    TextButton(
                        onClick = { viewModel.resetImport() },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Outlined.Delete, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.crf_import_delete_button))
                    }
                } else {
                    Button(
                        onClick = { viewModel.exportToRedCap() },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.Outlined.FileDownload, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.crf_import_export_button))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = { viewModel.resetImport() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.crf_import_reset_button))
                    }
                }
            }
        }
    }
    
    if (showScanner) {
        com.kinapto.fitadapt.ui.components.QrCodeScanner(
            onQrCodeScanned = { qr ->
                viewModel.processScannedQr(qr)
                showScanner = false
            },
            onDismiss = { showScanner = false }
        )
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(stringResource(R.string.crf_camera_permission_title)) },
            text = { Text(stringResource(R.string.crf_camera_permission_desc)) },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(stringResource(R.string.label_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(stringResource(R.string.label_cancel))
                }
            }
        )
    }
}
