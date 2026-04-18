// =============================================================
// AFA - Attività Fisica Adattata
// Schermata di Gestione Libreria Esercizi
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryManagerScreen(
    viewModel: ExerciseLibraryViewModel,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onEditExercise: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Libreria esercizi", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExercise, containerColor = FitlyBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Aggiungi esercizio")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Ricerca
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cerca esercizio...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitlyBlue)
            )

            // Filtri categoria
            LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.setCategory(null) },
                        label = { Text("Tutti") }
                    )
                }
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.setCategory(category) },
                        label = { Text(category) }
                    )
                }
            }

            // Lista esercizi
            if (uiState.exercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nessun esercizio trovato", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(uiState.exercises, key = { it.id }) { exercise ->
                        ExerciseListItem(
                            exercise = exercise,
                            onEdit = { onEditExercise(exercise.id) },
                            onDelete = { showDeleteDialog = exercise.id }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Elimina esercizio") },
            text = { Text("Sei sicuro di voler eliminare questo esercizio? Questa azione non può essere annullata.") },
            confirmButton = {
                TextButton(onClick = { 
                    showDeleteDialog?.let { viewModel.deleteExercise(it) }
                    showDeleteDialog = null
                }) { Text("Elimina", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Annulla") }
            }
        )
    }
}

@Composable
fun ExerciseListItem(exercise: ExerciseEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = NavyBlue, fontWeight = FontWeight.Bold)
                Text(exercise.category, style = MaterialTheme.typography.bodySmall, color = FitlyBlue)
                Spacer(modifier = Modifier.height(4.dp))
                exercise.videoUri?.let {
                    Text("Video presente", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Modifica", tint = NavyBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Elimina", tint = Color.Red.copy(alpha = 0.7f)) }
        }
    }
}
