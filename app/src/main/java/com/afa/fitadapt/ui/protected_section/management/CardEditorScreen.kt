// =============================================================
// AFA - Attività Fisica Adattata
// Screen: Editor Scheda (Area Protetta)
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.ExerciseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    viewModel: CardEditorViewModel,
    onBack: () -> Unit,
    onPickExercise: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor Scheda") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveCard() }) {
                        Icon(Icons.Default.Save, contentDescription = "Salva")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Informazioni Generali", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("Titolo Scheda") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.durationWeeks,
                            onValueChange = { viewModel.onDurationChange(it) },
                            label = { Text("Settimane") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = uiState.targetSessions,
                            onValueChange = { viewModel.onSessionsChange(it) },
                            label = { Text("Sedute target") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.autoAdvance,
                            onCheckedChange = { viewModel.onAutoAdvanceChange(it) }
                        )
                        Text("Avanzamento automatico al termine")
                    }
                }
                
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Esercizi in Scheda", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Button(
                            onClick = onPickExercise
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Aggiungi")
                        }
                    }
                }
                
                if (uiState.exercises.isEmpty()) {
                    item {
                        Text(
                            "Nessun esercizio aggiunto. Clicca su 'Aggiungi' per iniziare.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                
                itemsIndexed(uiState.exercises) { index, item ->
                    EditableExerciseItem(
                        item = item,
                        onUpdate = { duration, reps, intensity, notes ->
                            viewModel.updateExerciseParams(index, duration, reps, intensity, notes)
                        },
                        onRemove = { viewModel.removeExercise(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun EditableExerciseItem(
    item: CardExerciseWithDetails,
    onUpdate: (Int?, Int?, String?, String?) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${item.cardExercise.orderIndex + 1}. ${item.exercise.name}",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Rimuovi", tint = Color.Red)
                }
            }
            
            if (expanded) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = item.cardExercise.customDurationSec?.toString() ?: "",
                        onValueChange = { onUpdate(it.toIntOrNull(), item.cardExercise.customRepetitions, item.cardExercise.customIntensity, item.cardExercise.customNotes) },
                        label = { Text("Sec") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = item.cardExercise.customRepetitions?.toString() ?: "",
                        onValueChange = { onUpdate(item.cardExercise.customDurationSec, it.toIntOrNull(), item.cardExercise.customIntensity, item.cardExercise.customNotes) },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = item.cardExercise.customIntensity ?: "",
                        onValueChange = { onUpdate(item.cardExercise.customDurationSec, item.cardExercise.customRepetitions, it, item.cardExercise.customNotes) },
                        label = { Text("Intensità") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = item.cardExercise.customNotes ?: "",
                    onValueChange = { onUpdate(item.cardExercise.customDurationSec, item.cardExercise.customRepetitions, item.cardExercise.customIntensity, it) },
                    label = { Text("Note specifiche") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2
                )
            } else {
                Text(
                    text = "${item.cardExercise.customRepetitions ?: "X"} reps / ${item.cardExercise.customDurationSec ?: "X"} sec",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
