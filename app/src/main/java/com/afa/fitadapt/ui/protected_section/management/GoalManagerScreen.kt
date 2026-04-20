// =============================================================
// AFA - Attività Fisica Adattata
// Schermata Gestione Obiettivi
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalManagerScreen(
    viewModel: GoalManagerViewModel,
    onBack: () -> Unit,
    onAddGoal: () -> Unit,
    onEditGoal: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<GoalEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Obiettivi paziente", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoal, containerColor = FitlyBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Aggiungi obiettivo")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "Imposta traguardi Bronze, Silver e Gold per motivare la paziente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nessun obiettivo impostato", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.goals, key = { it.id }) { goal ->
                        GoalListItem(
                            goal = goal,
                            onDelete = { showDeleteDialog = goal },
                            onToggleActive = { viewModel.toggleGoalActive(goal) },
                            onClick = { onEditGoal(goal.id) }
                        )
                    }
                }
            }
        }
    }
// ... rest of the file ...

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Elimina obiettivo") },
            text = { Text("Sei sicuro di voler eliminare l'obiettivo '${showDeleteDialog?.title}'?") },
            confirmButton = {
                TextButton(onClick = { 
                    showDeleteDialog?.let { viewModel.deleteGoal(it) }
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
fun GoalListItem(goal: GoalEntity, onDelete: () -> Unit, onToggleActive: () -> Unit, onClick: () -> Unit) {
    val medalColor = when {
        goal.title.contains("Gold", ignoreCase = true) -> Color(0xFFFFD700)
        goal.title.contains("Silver", ignoreCase = true) -> Color(0xFFC0C0C0)
        goal.title.contains("Bronze", ignoreCase = true) -> Color(0xFFCD7F32)
        else -> FitlyBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(medalColor.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.EmojiEvents, null, tint = medalColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, style = MaterialTheme.typography.titleSmall, color = NavyBlue, fontWeight = FontWeight.Bold)
                val thresholds = listOfNotNull(
                    "B: ${goal.targetValue.toInt()}",
                    goal.silverValue?.let { "S: ${it.toInt()}" },
                    goal.goldValue?.let { "G: ${it.toInt()}" }
                ).joinToString(" | ")
                Text("${goal.targetType} ($thresholds)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(checked = goal.isActive, onCheckedChange = { onToggleActive() })
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Elimina", tint = Color.Red.copy(alpha = 0.6f)) }
        }
    }
}
