// =============================================================
// AFA - Attività Fisica Adattata
// Schermata Editor Articolo
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditorScreen(
    viewModel: ArticleEditorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("Attività Fisica", "Alimentazione", "Sonno", "Fatigue", "Dolore", "Benessere", "Linfedema")
    var showCategoryMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == 0L) "Nuovo articolo" else "Modifica articolo", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                actions = {
                    IconButton(onClick = viewModel::save, enabled = uiState.title.isNotBlank() && uiState.body.isNotBlank()) {
                        Icon(Icons.Default.Save, "Salva", tint = if (uiState.title.isNotBlank()) FitlyBlue else NavyBlue.copy(alpha = 0.5f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            
            OutlinedTextField(
                value = uiState.title, onValueChange = viewModel::updateTitle,
                label = { Text("Titolo articolo") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = !showCategoryMenu }
            ) {
                OutlinedTextField(
                    value = uiState.category, onValueChange = {},
                    readOnly = true, label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.updateCategory(cat)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.summary, onValueChange = viewModel::updateSummary,
                label = { Text("Riassunto (breve)") }, modifier = Modifier.fillMaxWidth().height(100.dp),
                singleLine = false, maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.body, onValueChange = viewModel::updateBody,
                label = { Text("Contenuto articolo") }, modifier = Modifier.fillMaxWidth().height(300.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = uiState.title.isNotBlank() && uiState.body.isNotBlank(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva articolo")
            }
        }
    }
}
