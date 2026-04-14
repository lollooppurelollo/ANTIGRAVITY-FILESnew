// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Dettaglio esercizio
// =============================================================
package com.afa.fitadapt.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue

/**
 * Dettaglio di un singolo esercizio.
 * Mostra: nome, categoria, parametri, descrizione, video placeholder, note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    cardViewModel: CardViewModel,
    exerciseId: Long,
    cardExerciseId: Long,
    onBack: () -> Unit
) {
    val uiState by cardViewModel.exerciseDetailState.collectAsState()

    LaunchedEffect(exerciseId) {
        cardViewModel.loadExerciseDetail(exerciseId, cardExerciseId)
    }

    val exercise = uiState.exercise

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Esercizio", color = NavyBlue) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        if (exercise == null) return@Column

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Nome e categoria
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineMedium,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PastelBlue)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    exercise.category.replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Video placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = CardDefaults.cardColors(containerColor = PastelBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Videocam,
                            "Video",
                            tint = NavyBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (exercise.videoUri != null) "Video disponibile"
                            else "Video non ancora disponibile",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyBlue.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Parametri
            Text("Parametri", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                exercise.defaultDurationSec?.let {
                    ParameterCard(
                        icon = Icons.Outlined.Timer,
                        value = "${it / 60} min",
                        label = "Durata"
                    )
                }
                exercise.defaultRepetitions?.let {
                    ParameterCard(
                        icon = Icons.Outlined.Repeat,
                        value = "$it",
                        label = "Ripetizioni"
                    )
                }
                ParameterCard(
                    icon = Icons.Outlined.Speed,
                    value = exercise.defaultIntensity.replaceFirstChar { it.uppercase() },
                    label = "Intensità"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descrizione
            Text("Descrizione", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                exercise.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Note
            exercise.notes?.let { notes ->
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PastelBlue.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("💡", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(notes, style = MaterialTheme.typography.bodyMedium, color = NavyBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ParameterCard(icon: ImageVector, value: String, label: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, label, tint = CelestialBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = NavyBlue, textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
