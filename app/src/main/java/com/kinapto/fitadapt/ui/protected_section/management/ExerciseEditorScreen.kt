// =============================================================
// KinApto - Attività Fisica Adattata
// Schermata Editor Esercizio
// =============================================================
package com.kinapto.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kinapto.fitadapt.model.ExerciseCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditorScreen(
    viewModel: ExerciseEditorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategoryMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == 0L) stringResource(R.string.editor_exercise_title_new) else stringResource(R.string.editor_exercise_title_edit)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } },
                actions = {
                    IconButton(onClick = viewModel::save, enabled = uiState.name.isNotBlank() && uiState.description.isNotBlank()) {
                        Icon(
                            Icons.Default.Save,
                            stringResource(R.string.label_save),
                            tint = if (uiState.name.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            
            OutlinedTextField(
                value = uiState.name, onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.editor_exercise_label_name)) }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = !showCategoryMenu }
            ) {
                OutlinedTextField(
                    value = uiState.category, onValueChange = {},
                    readOnly = true, label = { Text(stringResource(R.string.editor_exercise_label_category)) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                    ExerciseCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                viewModel.updateCategory(cat.displayName)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.description, onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.editor_exercise_label_description)) }, modifier = Modifier.fillMaxWidth().height(120.dp),
                singleLine = false, maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.videoUri, onValueChange = viewModel::updateVideoUri,
                label = { Text(stringResource(R.string.editor_exercise_label_video_uri)) }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.editor_exercise_placeholder_video_uri)) }, singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.editor_exercise_header_params), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.defaultDurationSec, onValueChange = viewModel::updateDuration,
                    label = { Text(stringResource(R.string.editor_exercise_label_seconds)) }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                )
                OutlinedTextField(
                    value = uiState.defaultRepetitions, onValueChange = viewModel::updateRepetitions,
                    label = { Text(stringResource(R.string.editor_exercise_label_reps)) }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(stringResource(R.string.editor_exercise_header_intensity), style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.fillMaxWidth()) {
                val intensities = listOf(
                    "bassa" to stringResource(R.string.editor_exercise_intensity_low),
                    "moderata" to stringResource(R.string.editor_exercise_intensity_moderate),
                    "alta" to stringResource(R.string.editor_exercise_intensity_high)
                )
                intensities.forEach { (id, label) ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        RadioButton(selected = uiState.defaultIntensity == id, onClick = { viewModel.updateIntensity(id) })
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes, onValueChange = viewModel::updateNotes,
                label = { Text(stringResource(R.string.editor_exercise_label_notes)) }, modifier = Modifier.fillMaxWidth().height(100.dp),
                singleLine = false, maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = uiState.name.isNotBlank() && uiState.description.isNotBlank(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.editor_exercise_save))
            }
        }
    }
}
