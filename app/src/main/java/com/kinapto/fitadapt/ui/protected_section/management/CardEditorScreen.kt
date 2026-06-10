// =============================================================
// KinApto - Attività Fisica Adattata
// Screen: Editor Scheda (Area Protetta)
// =============================================================
package com.kinapto.fitadapt.ui.protected_section.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kinapto.fitadapt.R
import com.kinapto.fitadapt.data.local.entity.AdaptationRuleEntity
import com.kinapto.fitadapt.domain.AdaptationDelta
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    viewModel: CardEditorViewModel,
    onBack: () -> Unit,
    onPickExercise: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }
    
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
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Aiuto")
                    }
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

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Regole Cliniche (Logic Builder)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Le regole nello stesso gruppo sono in AND. Gruppi diversi sono in OR.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val ruleGroups = uiState.rules.groupBy { it.groupId }
                    var groupIndex = 1
                    
                    ruleGroups.forEach { (groupId, rulesInGroup) ->
                        RuleGroupItem(
                            groupName = stringResource(R.string.rules_group_title, groupIndex++),
                            rules = rulesInGroup,
                            onAddCondition = { viewModel.addRule(groupId) },
                            onUpdateRule = { indexInGroup, updatedRule ->
                                val globalIndex = uiState.rules.indexOf(rulesInGroup[indexInGroup])
                                viewModel.updateRuleAtIndex(globalIndex, updatedRule)
                            },
                            onDeleteRule = { indexInGroup ->
                                val globalIndex = uiState.rules.indexOf(rulesInGroup[indexInGroup])
                                viewModel.removeRule(globalIndex)
                            },
                            onUpdateDelta = { indexInGroup, reps, intensity, dur ->
                                val globalIndex = uiState.rules.indexOf(rulesInGroup[indexInGroup])
                                viewModel.updateRuleActionDelta(globalIndex, reps, intensity, dur)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedButton(
                        onClick = { viewModel.addRule() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.rules_add_group))
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

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.rules_help_title)) },
            text = { Text(stringResource(R.string.rules_help_content)) },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(R.string.label_ok))
                }
            }
        )
    }
}

@Composable
fun RuleGroupItem(
    groupName: String,
    rules: List<AdaptationRuleEntity>,
    onAddCondition: () -> Unit,
    onUpdateRule: (Int, AdaptationRuleEntity) -> Unit,
    onDeleteRule: (Int) -> Unit,
    onUpdateDelta: (Int, Int?, Int?, Float?) -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(groupName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            rules.forEachIndexed { index, rule ->
                RuleEditorItem(
                    rule = rule,
                    onUpdate = { onUpdateRule(index, it) },
                    onDelete = { onDeleteRule(index) },
                    onUpdateDelta = { reps, intensity, dur -> onUpdateDelta(index, reps, intensity, dur) }
                )
                if (index < rules.size - 1) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                        Text("AND", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onAddCondition,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.rules_add_condition))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleEditorItem(
    rule: AdaptationRuleEntity,
    onUpdate: (AdaptationRuleEntity) -> Unit,
    onDelete: () -> Unit,
    onUpdateDelta: (Int?, Int?, Float?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var showTypeMenu by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = showTypeMenu,
                    onExpandedChange = { showTypeMenu = !showTypeMenu },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = when(rule.triggerType) {
                            "BIOMETRIC" -> stringResource(R.string.rules_trigger_biometric)
                            "COMPLETION" -> stringResource(R.string.rules_trigger_completion)
                            "FAILURE_REASON" -> stringResource(R.string.rules_trigger_failure)
                            else -> rule.triggerType
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo Trigger") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeMenu) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.rules_trigger_biometric)) },
                            onClick = { onUpdate(rule.copy(triggerType = "BIOMETRIC", parameter = "PAIN")); showTypeMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.rules_trigger_completion)) },
                            onClick = { onUpdate(rule.copy(triggerType = "COMPLETION", parameter = "MISSED_SESSIONS")); showTypeMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.rules_trigger_failure)) },
                            onClick = { onUpdate(rule.copy(triggerType = "FAILURE_REASON", parameter = "TOO_FATIGUING")); showTypeMenu = false }
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (rule.triggerType == "BIOMETRIC") {
                BiometricTriggerFields(rule, onUpdate)
            } else if (rule.triggerType == "COMPLETION") {
                CompletionTriggerFields(rule, onUpdate)
            } else if (rule.triggerType == "FAILURE_REASON") {
                FailureReasonTriggerFields(rule, onUpdate)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Azione Conseguente", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            
            DeltaActionFields(rule, onUpdateDelta)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricTriggerFields(rule: AdaptationRuleEntity, onUpdate: (AdaptationRuleEntity) -> Unit) {
    var showParamMenu by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = showParamMenu,
        onExpandedChange = { showParamMenu = !showParamMenu }
    ) {
        OutlinedTextField(
            value = rule.parameter ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Parametro") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showParamMenu) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = showParamMenu, onDismissRequest = { showParamMenu = false }) {
            listOf("PAIN", "ASTHENIA", "EFFORT", "SPO2", "HEART_RATE", "REST_DYSPNEA", "EXERTION_DYSPNEA").forEach { p ->
                DropdownMenuItem(text = { Text(p) }, onClick = { onUpdate(rule.copy(parameter = p)); showParamMenu = false })
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        var showOpMenu by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = showOpMenu,
            onExpandedChange = { showOpMenu = !showOpMenu },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = when(rule.operator) {
                    "GT" -> ">"
                    "LT" -> "<"
                    "EQ" -> "="
                    else -> rule.operator
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Operatore") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOpMenu) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = showOpMenu, onDismissRequest = { showOpMenu = false }) {
                DropdownMenuItem(text = { Text(">") }, onClick = { onUpdate(rule.copy(operator = "GT")); showOpMenu = false })
                DropdownMenuItem(text = { Text("<") }, onClick = { onUpdate(rule.copy(operator = "LT")); showOpMenu = false })
                DropdownMenuItem(text = { Text("=") }, onClick = { onUpdate(rule.copy(operator = "EQ")); showOpMenu = false })
            }
        }
        
        OutlinedTextField(
            value = rule.threshold.toString(),
            onValueChange = { onUpdate(rule.copy(threshold = it.toFloatOrNull() ?: 0f)) },
            label = { Text("Soglia") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = rule.windowDays.toString(),
            onValueChange = { onUpdate(rule.copy(windowDays = it.toIntOrNull() ?: 0)) },
            label = { Text("Finestra (GG)") },
            modifier = Modifier.width(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(checked = rule.useAverage, onCheckedChange = { onUpdate(rule.copy(useAverage = it)) })
        Text("Usa Media", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CompletionTriggerFields(rule: AdaptationRuleEntity, onUpdate: (AdaptationRuleEntity) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = rule.minOccurrences.toString(),
            onValueChange = { onUpdate(rule.copy(minOccurrences = it.toIntOrNull() ?: 1)) },
            label = { Text("Sessioni Saltate") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rule.requireConsecutive, onCheckedChange = { onUpdate(rule.copy(requireConsecutive = it)) })
            Text("Consecutive", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailureReasonTriggerFields(rule: AdaptationRuleEntity, onUpdate: (AdaptationRuleEntity) -> Unit) {
    var showReasonMenu by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = showReasonMenu,
        onExpandedChange = { showReasonMenu = !showReasonMenu }
    ) {
        OutlinedTextField(
            value = rule.parameter ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Motivo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showReasonMenu) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = showReasonMenu, onDismissRequest = { showReasonMenu = false }) {
            listOf("TOO_FATIGUING", "PAIN_INCREASED", "LACK_OF_TIME", "OTHER").forEach { r ->
                DropdownMenuItem(text = { Text(r) }, onClick = { onUpdate(rule.copy(parameter = r)); showReasonMenu = false })
            }
        }
    }
}

@Composable
fun DeltaActionFields(rule: AdaptationRuleEntity, onUpdateDelta: (Int?, Int?, Float?) -> Unit) {
    val delta = try {
        Json.decodeFromString<AdaptationDelta>(rule.actionValue)
    } catch (e: Exception) {
        AdaptationDelta()
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DeltaIncrementer(
            label = stringResource(R.string.rules_action_delta_reps),
            value = delta.reps,
            onValueChange = { onUpdateDelta(it, null, null) },
            modifier = Modifier.weight(1f)
        )
        DeltaIncrementer(
            label = stringResource(R.string.rules_action_delta_intensity),
            value = delta.intensity,
            onValueChange = { onUpdateDelta(null, it, null) },
            modifier = Modifier.weight(1f)
        )
        DeltaIncrementer(
            label = stringResource(R.string.rules_action_delta_duration),
            value = (delta.durationPercent * 100).toInt(),
            onValueChange = { onUpdateDelta(null, null, it.toFloat() / 100f) },
            suffix = "%",
            modifier = Modifier.weight(1f),
            step = 5
        )
    }
}

@Composable
fun DeltaIncrementer(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String = "",
    step: Int = 1
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onValueChange(value - step) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }
            Text(
                text = "${if (value > 0) "+" else ""}$value$suffix",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(onClick = { onValueChange(value + step) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
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
