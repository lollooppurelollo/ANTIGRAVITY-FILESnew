// =============================================================
// AFA - Attività Fisica Adattata
// Screen: Gestione Schede (Area Protetta)
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import com.afa.fitadapt.model.CardStatus
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagerScreen(
    viewModel: CardManagerViewModel,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onEditCard: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestione Schede", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCard,
                containerColor = FitlyBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Scheda")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.cards.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Text("Nessuna scheda presente", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.cards) { card ->
                        CardItem(
                            card = card,
                            onEdit = { onEditCard(card.id) },
                            onDelete = { viewModel.deleteCard(card) },
                            onActivate = { viewModel.activateCard(card.id) },
                            onDuplicate = { viewModel.duplicateCard(card) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: TrainingCardEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onActivate: () -> Unit,
    onDuplicate: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = card.status)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Durata: ${card.durationWeeks ?: "?"} settimane | Obiettivo: ${card.targetSessions ?: "?"} sedute",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                if (card.autoAdvance) {
                    Text(
                        text = "Avanzamento automatico attivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitlyBlue
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Modifica") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    if (card.status != CardStatus.ACTIVE.name) {
                        DropdownMenuItem(
                            text = { Text("Attiva ora") },
                            onClick = {
                                showMenu = false
                                onActivate()
                            },
                            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Duplica") },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Elimina", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        CardStatus.ACTIVE.name -> Color(0xFF4CAF50)
        CardStatus.COMPLETED.name -> Color.Gray
        else -> FitlyBlue
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
