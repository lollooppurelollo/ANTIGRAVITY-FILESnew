// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Dettaglio Sessione Registrata
// =============================================================
package com.afa.fitadapt.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.data.local.entity.SessionWithExercises
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SoftRose
import com.afa.fitadapt.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    viewModel: SessionViewModel,
    sessionId: Long,
    onBack: () -> Unit
) {
    val sessionWithExercises by viewModel.getSessionDetail(sessionId).collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina sessione") },
            text = { Text("Sei sicuro di voler eliminare questa sessione dallo storico? L'azione non è reversibile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionWithExercises?.session?.let { session ->
                            viewModel.deleteSession(session) {
                                showDeleteDialog = false
                                onBack()
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Sessione", color = NavyBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Elimina", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        sessionWithExercises?.let { data ->
            val session = data.session
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header con data e stato
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (session.completed) SageGreen.copy(alpha = 0.1f) else SoftRose.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = DateUtils.toDisplayString(session.date),
                                style = MaterialTheme.typography.headlineSmall,
                                color = NavyBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (session.completed) "Sessione Eseguita" else "Sessione saltata",
                                color = if (session.completed) SageGreen else SoftRose,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = if (session.completed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (session.completed) SageGreen else SoftRose,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Parametri (Durata, Fatica, Umore, Sonno)
                Text("Parametri registrati", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        label = "Durata",
                        value = session.actualDurationMin?.let { "$it min" } ?: "--",
                        color = FitlyBlue
                    )
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.FlashOn,
                        label = "Fatica",
                        value = session.perceivedEffort?.let { "$it/10" } ?: "--",
                        color = SoftRose
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Mood,
                        label = "Umore",
                        value = session.mood?.let { "$it/10" } ?: "--",
                        color = SageGreen
                    )
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Bedtime,
                        label = "Sonno",
                        value = session.sleepQuality?.let { "$it/10" } ?: "--",
                        color = NavyBlue
                    )
                }

                if (!session.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Note", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = session.notes ?: "",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Dettaglio esercizi se presenti
                if (data.exerciseCompletions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Dettaglio Esercizi", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    data.exerciseCompletions.forEach { se ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (se.sessionExercise.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (se.sessionExercise.completed) SageGreen else MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = se.cardExercise.exercise.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
