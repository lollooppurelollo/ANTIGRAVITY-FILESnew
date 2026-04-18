// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Scheda attiva
// =============================================================
package com.afa.fitadapt.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.FitlyBlueLight
import com.afa.fitadapt.ui.theme.SageGreen

/**
 * Schermata della scheda attiva.
 * Mostra la lista degli esercizi con i parametri personalizzati.
 */
@Composable
fun ActiveCardScreen(
    cardViewModel: CardViewModel,
    onExerciseClick: (exerciseId: Long, cardExerciseId: Long) -> Unit
) {
    val uiState by cardViewModel.cardState.collectAsState()

    if (uiState.noActiveCard) {
        // Nessuna scheda attiva
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📋", style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Nessuna scheda attiva",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NavyBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Chiedi al tuo operatore di attivare una scheda dalla sezione protetta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val cardWithExercises = uiState.cardWithExercises ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header della scheda
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FitlyBlueLight,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(24.dp)
                    .padding(top = 8.dp)
            ) {
                Column {
                    Text(
                        text = cardWithExercises.card.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        cardWithExercises.card.targetSessions?.let { target ->
                            InfoChip(
                                icon = Icons.Outlined.FitnessCenter,
                                text = "${uiState.completedSessionsCount}/$target sessioni"
                            )
                        }
                        cardWithExercises.card.durationWeeks?.let { weeks ->
                            InfoChip(
                                icon = Icons.Outlined.Timer,
                                text = "$weeks settimane"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${cardWithExercises.cardExercises.size} esercizi",
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyBlue.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Lista esercizi
        itemsIndexed(
            cardWithExercises.cardExercises.sortedBy { it.orderIndex }
        ) { index, cardExercise ->
            val exercise = uiState.exerciseDetails[cardExercise.exerciseId]
            if (exercise != null) {
                ExerciseListItem(
                    index = index + 1,
                    exercise = exercise,
                    cardExercise = cardExercise,
                    onClick = { onExerciseClick(exercise.id, cardExercise.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ExerciseListItem(
    index: Int,
    exercise: ExerciseEntity,
    cardExercise: CardExerciseEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numero ordine
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(FitlyBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("$index", style = MaterialTheme.typography.labelLarge, color = NavyBlue)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info esercizio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = NavyBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Parametri (custom > default)
                val duration = cardExercise.customDurationSec ?: exercise.defaultDurationSec
                val reps = cardExercise.customRepetitions ?: exercise.defaultRepetitions
                val intensity = cardExercise.customIntensity ?: exercise.defaultIntensity

                val params = buildList {
                    duration?.let { add("${it / 60} min") }
                    reps?.let { add("$it rip.") }
                    add(intensity)
                }
                Text(
                    params.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Freccia
            Icon(
                Icons.Outlined.PlayArrow,
                "Dettaglio",
                tint = FitlyBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = NavyBlue.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = NavyBlue.copy(alpha = 0.7f))
    }
}
