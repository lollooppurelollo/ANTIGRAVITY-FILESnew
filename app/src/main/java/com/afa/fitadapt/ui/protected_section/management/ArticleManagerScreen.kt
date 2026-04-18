// =============================================================
// AFA - Attività Fisica Adattata
// Schermata Gestione Articoli
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleManagerScreen(
    viewModel: ArticleManagerViewModel,
    onBack: () -> Unit,
    onAddArticle: () -> Unit,
    onEditArticle: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<ArticleEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestione articoli", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddArticle, containerColor = FitlyBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Aggiungi articolo")
            }
        }
    ) { padding ->
        if (uiState.articles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nessun articolo presente", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)) {
                items(uiState.articles, key = { it.id }) { article ->
                    ArticleListItem(
                        article = article,
                        onEdit = { onEditArticle(article.id) },
                        onDelete = { showDeleteDialog = article },
                        onSetFeatured = { viewModel.setFeatured(article.id) }
                    )
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Elimina articolo") },
            text = { Text("Sei sicuro di voler eliminare l'articolo '${showDeleteDialog?.title}'?") },
            confirmButton = {
                TextButton(onClick = { 
                    showDeleteDialog?.let { viewModel.deleteArticle(it) }
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
fun ArticleListItem(article: ArticleEntity, onEdit: () -> Unit, onDelete: () -> Unit, onSetFeatured: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, style = MaterialTheme.typography.titleMedium, color = NavyBlue, fontWeight = FontWeight.Bold)
                Text(article.category, style = MaterialTheme.typography.bodySmall, color = FitlyBlue)
            }
            IconButton(onClick = onSetFeatured) {
                Icon(if (article.isFeatured) Icons.Default.Star else Icons.Default.StarBorder, "In evidenza", tint = if (article.isFeatured) Color(0xFFFFB300) else Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Modifica", tint = NavyBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Elimina", tint = Color.Red.copy(alpha = 0.7f)) }
        }
    }
}
