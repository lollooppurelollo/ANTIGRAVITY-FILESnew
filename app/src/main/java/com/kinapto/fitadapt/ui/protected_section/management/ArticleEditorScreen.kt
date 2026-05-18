// =============================================================
// KinApto - Attività Fisica Adattata
// Schermata Editor Articolo
// =============================================================
package com.kinapto.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditorScreen(
    viewModel: ArticleEditorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf(
        stringResource(R.string.editor_article_category_physical_activity),
        stringResource(R.string.editor_article_category_nutrition),
        stringResource(R.string.editor_article_category_sleep),
        stringResource(R.string.editor_article_category_fatigue),
        stringResource(R.string.editor_article_category_pain),
        stringResource(R.string.editor_article_category_wellbeing),
        stringResource(R.string.editor_article_category_lymphedema)
    )
    var showCategoryMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == 0L) stringResource(R.string.editor_article_title_new) else stringResource(R.string.editor_article_title_edit)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } },
                actions = {
                    IconButton(onClick = viewModel::save, enabled = uiState.title.isNotBlank() && uiState.body.isNotBlank()) {
                        Icon(
                            Icons.Default.Save,
                            stringResource(R.string.label_save),
                            tint = if (uiState.title.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
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
                value = uiState.title, onValueChange = viewModel::updateTitle,
                label = { Text(stringResource(R.string.editor_article_label_title)) }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = !showCategoryMenu }
            ) {
                OutlinedTextField(
                    value = uiState.category, onValueChange = {},
                    readOnly = true, label = { Text(stringResource(R.string.editor_article_label_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                label = { Text(stringResource(R.string.editor_article_label_summary)) }, modifier = Modifier.fillMaxWidth().height(100.dp),
                singleLine = false, maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.body, onValueChange = viewModel::updateBody,
                label = { Text(stringResource(R.string.editor_article_label_body)) }, modifier = Modifier.fillMaxWidth().height(300.dp),
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
                Text(stringResource(R.string.editor_article_save))
            }
        }
    }
}
