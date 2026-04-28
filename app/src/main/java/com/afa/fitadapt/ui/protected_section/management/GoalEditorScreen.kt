// =============================================================
// AFA - Attività Fisica Adattata
// Schermata Editor Obiettivo
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalEditorScreen(
    viewModel: GoalEditorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val types = listOf(
        "sessions_per_week" to "Sedute alla settimana",
        "total_minutes_week" to "Minuti totali settimana",
        "streak_days" to "Giorni consecutivi",
        "total_sessions" to "Sessioni totali"
    )
    val periods = listOf(
        "none" to "Nessuna",
        "weekly" to "Settimanale",
        "monthly" to "Mensile",
        "bimonthly" to "Bimensile",
        "yearly" to "Annuale",
        "custom" to "Personalizzata"
    )
    val customUnits = listOf(
        "days" to "Giorni",
        "weeks" to "Settimane",
        "months" to "Mesi"
    )

    var showTypeMenu by remember { mutableStateOf(false) }
    var showPeriodMenu by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }
    var showParentMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.goalId == null) "Nuovo obiettivo" else "Modifica obiettivo") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.title, onValueChange = viewModel::updateTitle,
                label = { Text("Titolo (es. Obiettivo Gold)") }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("es. Gold: 4 allenamenti/settimana") },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showTypeMenu,
                onExpandedChange = { showTypeMenu = !showTypeMenu }
            ) {
                OutlinedTextField(
                    value = types.find { it.first == uiState.targetType }?.second ?: uiState.targetType,
                    onValueChange = {},
                    readOnly = true, label = { Text("Tipo di traguardo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                    types.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updateTargetType(key)
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Soglie traguardi", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.bronzeValue, onValueChange = viewModel::updateBronzeValue,
                    label = { Text("Bronze") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.silverValue, onValueChange = viewModel::updateSilverValue,
                    label = { Text("Silver") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.goldValue, onValueChange = viewModel::updateGoldValue,
                    label = { Text("Gold") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Scadenza temporale", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = showPeriodMenu,
                onExpandedChange = { showPeriodMenu = !showPeriodMenu }
            ) {
                OutlinedTextField(
                    value = periods.find { it.first == uiState.periodType }?.second ?: uiState.periodType,
                    onValueChange = {},
                    readOnly = true, label = { Text("Frequenza reset") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPeriodMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = showPeriodMenu, onDismissRequest = { showPeriodMenu = false }) {
                    periods.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updatePeriodType(key)
                                showPeriodMenu = false
                            }
                        )
                    }
                }
            }

            if (uiState.periodType == "custom") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.customPeriodValue, onValueChange = viewModel::updateCustomPeriodValue,
                        label = { Text("Ogni...") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = showUnitMenu,
                        onExpandedChange = { showUnitMenu = !showUnitMenu },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = customUnits.find { it.first == uiState.customPeriodUnit }?.second ?: uiState.customPeriodUnit,
                            onValueChange = {},
                            readOnly = true, label = { Text("Unità") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showUnitMenu, onDismissRequest = { showUnitMenu = false }) {
                            customUnits.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.updateCustomPeriodUnit(key)
                                        showUnitMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Sblocco sequenziale (Goal Scalabili)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = showParentMenu,
                onExpandedChange = { showParentMenu = !showParentMenu }
            ) {
                val parentGoal = uiState.availableGoals.find { it.id == uiState.parentGoalId }
                val parentText = parentGoal?.title ?: "Attiva subito (nessun requisito)"
                
                OutlinedTextField(
                    value = parentText,
                    onValueChange = {},
                    readOnly = true, label = { Text("Attiva dopo il raggiungimento di:") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showParentMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = showParentMenu, onDismissRequest = { showParentMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Attiva subito (nessun requisito)") },
                        onClick = {
                            viewModel.updateParentGoalId(null)
                            showParentMenu = false
                        }
                    )
                    uiState.availableGoals.filter { it.id != uiState.goalId }.forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal.title) },
                            onClick = {
                                viewModel.updateParentGoalId(goal.id)
                                showParentMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = uiState.title.isNotBlank() && uiState.bronzeValue.isNotBlank(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva obiettivo")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
