// =============================================================
// KinApto - Attività Fisica Adattata
// Screen: Editor Scheda (Area Protetta)
// =============================================================
package com.kinapto.fitadapt.ui.protected_section.management

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
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R
import com.kinapto.fitadapt.data.local.entity.ExerciseEntity

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
                title = { Text(stringResource(R.string.editor_card_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.label_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveCard() }) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(R.string.label_save))
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
                    Text(stringResource(R.string.editor_card_info_header), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text(stringResource(R.string.editor_card_label_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.durationWeeks,
                            onValueChange = { viewModel.onDurationChange(it) },
                            label = { Text(stringResource(R.string.editor_card_label_weeks)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = uiState.targetSessions,
                            onValueChange = { viewModel.onSessionsChange(it) },
                            label = { Text(stringResource(R.string.editor_card_label_sessions)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.targetSessionsPerWeek,
                        onValueChange = { viewModel.onSessionsPerWeekChange(it) },
                        label = { Text(stringResource(R.string.editor_card_label_sessions_week)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.autoAdvance,
                            onCheckedChange = { viewModel.onAutoAdvanceChange(it) }
                        )
                        Text(stringResource(R.string.editor_card_autoadvance))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.editor_card_biofeedback_header), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    var showBiometricMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = showBiometricMenu,
                        onExpandedChange = { showBiometricMenu = !showBiometricMenu }
                    ) {
                            OutlinedTextField(
                                value = when(uiState.adaptationBiometricType) {
                                    "PAIN" -> stringResource(R.string.label_pain)
                                    "ASTHENIA" -> stringResource(R.string.label_asthenia)
                                    "REST_DYSPNEA" -> stringResource(R.string.label_rest_dyspnea)
                                    "EXERTION_DYSPNEA" -> stringResource(R.string.label_exertion_dyspnea)
                                    "NAUSEA" -> stringResource(R.string.label_nausea)
                                    "APPETITE" -> stringResource(R.string.label_appetite)
                                    "ANXIETY" -> stringResource(R.string.label_anxiety)
                                    "MOOD" -> stringResource(R.string.label_mood)
                                    "SLEEP" -> stringResource(R.string.label_sleep)
                                    else -> stringResource(R.string.editor_card_bio_none)
                                },
                                onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.editor_card_label_bio_param)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBiometricMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = showBiometricMenu,
                            onDismissRequest = { showBiometricMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.editor_card_bio_none)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange(null)
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_pain)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("PAIN")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_asthenia)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("ASTHENIA")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_rest_dyspnea)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("REST_DYSPNEA")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_exertion_dyspnea)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("EXERTION_DYSPNEA")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_nausea)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("NAUSEA")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_appetite)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("APPETITE")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_anxiety)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("ANXIETY")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_mood)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("MOOD")
                                    showBiometricMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.label_sleep)) },
                                onClick = {
                                    viewModel.onAdaptationBiometricTypeChange("SLEEP")
                                    showBiometricMenu = false
                                }
                            )
                        }
                    }

                    if (uiState.adaptationBiometricType != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.adaptationThreshold,
                                onValueChange = { viewModel.onAdaptationThresholdChange(it) },
                                label = { Text(stringResource(R.string.editor_card_label_threshold)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.adaptationWindowDays,
                                onValueChange = { viewModel.onAdaptationWindowDaysChange(it) },
                                label = { Text(stringResource(R.string.editor_card_label_window)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        var showActionMenu by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = showActionMenu,
                            onExpandedChange = { showActionMenu = !showActionMenu }
                        ) {
                            OutlinedTextField(
                                value = when(uiState.adaptationAction) {
                                    "DOWNREGULATE" -> stringResource(R.string.editor_card_action_downregulate)
                                    "SWITCH_CARD" -> stringResource(R.string.editor_card_action_switch)
                                    else -> stringResource(R.string.editor_card_action_select)
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.editor_card_label_action)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showActionMenu) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = showActionMenu,
                                onDismissRequest = { showActionMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.editor_card_action_downregulate)) },
                                    onClick = {
                                        viewModel.onAdaptationActionChange("DOWNREGULATE")
                                        showActionMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.editor_card_action_switch)) },
                                    onClick = {
                                        viewModel.onAdaptationActionChange("SWITCH_CARD")
                                        showActionMenu = false
                                    }
                                )
                            }
                        }
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
                        Text(stringResource(R.string.editor_card_exercises_header), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Button(
                            onClick = onPickExercise
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.editor_card_label_add))
                        }
                    }
                }
                
                if (uiState.exercises.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.editor_card_exercises_empty),
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
                        label = { Text(stringResource(R.string.editor_card_label_sec)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = item.cardExercise.customRepetitions?.toString() ?: "",
                        onValueChange = { onUpdate(item.cardExercise.customDurationSec, it.toIntOrNull(), item.cardExercise.customIntensity, item.cardExercise.customNotes) },
                        label = { Text(stringResource(R.string.editor_card_label_reps)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = item.cardExercise.customIntensity ?: "",
                        onValueChange = { onUpdate(item.cardExercise.customDurationSec, item.cardExercise.customRepetitions, it, item.cardExercise.customNotes) },
                        label = { Text(stringResource(R.string.editor_card_label_intensity)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = item.cardExercise.customNotes ?: "",
                    onValueChange = { onUpdate(item.cardExercise.customDurationSec, item.cardExercise.customRepetitions, item.cardExercise.customIntensity, it) },
                    label = { Text(stringResource(R.string.editor_card_label_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2
                )
            } else {
                Text(
                    text = "${item.cardExercise.customRepetitions ?: "X"} ${stringResource(R.string.editor_card_label_reps)} / ${item.cardExercise.customDurationSec ?: "X"} ${stringResource(R.string.editor_card_label_sec)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
