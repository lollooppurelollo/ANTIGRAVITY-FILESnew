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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import com.afa.fitadapt.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity

/**
 * Schermata della scheda attiva.
 * Mostra la lista degli esercizi con i parametri personalizzati.
 */
@Composable
fun ActiveCardScreen(
    cardViewModel: CardViewModel,
    themeViewModel: com.afa.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
    onExerciseClick: (exerciseId: Long, cardExerciseId: Long) -> Unit
) {
    val uiState by cardViewModel.cardState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()

    if (uiState.noActiveCard) {
        // Nessuna scheda attiva
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📋", style = MaterialTheme.typography.displaySmall)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Nessuna scheda attiva",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Chiedi al tuo operatore di attivare una scheda dalla sezione protetta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
        // Header della scheda - Floating Gradient Card
        item {
            val headerColor = MaterialTheme.colorScheme.primary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                headerColor,
                                headerColor.copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = cardWithExercises.card.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        cardWithExercises.card.targetSessions?.let { target ->
                            InfoChip(
                                icon = Icons.Outlined.FitnessCenter,
                                text = "${uiState.completedSessionsCount}/$target sessioni",
                                isLight = true,
                                useOriginalColors = useOriginalColors
                            )
                        }
                        cardWithExercises.card.durationWeeks?.let { weeks ->
                            InfoChip(
                                icon = Icons.Outlined.Timer,
                                text = "$weeks settimane",
                                isLight = true,
                                useOriginalColors = useOriginalColors
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${cardWithExercises.cardExercises.size} esercizi in programma",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item { 
            Text(
                "Esercizi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

                itemsIndexed(
            cardWithExercises.cardExercises.sortedBy { it.orderIndex }
        ) { index, cardExercise ->
            val exercise = uiState.exerciseDetails[cardExercise.exerciseId]
            if (exercise != null) {
                ExerciseListItem(
                    index = index + 1,
                    exercise = exercise,
                    cardExercise = cardExercise,
                    useOriginalColors = useOriginalColors,
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
    useOriginalColors: Boolean,
    onClick: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numero ordine
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info esercizio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Freccia
            Icon(
                Icons.Outlined.PlayArrow,
                "Dettaglio",
                tint = accentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isLight: Boolean = false,
    useOriginalColors: Boolean = false
) {
    val contentColor = if (isLight) {
        if (useOriginalColors) Color.White else MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val bgColor = if (isLight) {
        if (useOriginalColors) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = contentColor
        )
    }
}
