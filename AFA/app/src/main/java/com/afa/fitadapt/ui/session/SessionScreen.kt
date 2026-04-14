// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Registrazione sessione
// =============================================================
package com.afa.fitadapt.ui.session

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SoftRose

/**
 * Schermata di registrazione sessione di allenamento.
 *
 * Flusso:
 * 1. Domanda: "Hai fatto l'allenamento oggi?"
 * 2. Se sì/parziale → dettagli opzionali (durata, fatica, umore, sonno, note)
 * 3. Conferma e salva
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    sessionViewModel: SessionViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val uiState by sessionViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Registra sessione", color = NavyBlue) },
            navigationIcon = {
                if (uiState.phase != SessionPhase.SUBMITTED) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        AnimatedContent(targetState = uiState.phase, label = "sessionPhase") { phase ->
            when (phase) {
                SessionPhase.QUESTION -> QuestionPhase(
                    onYes = { sessionViewModel.answerCompleted() },
                    onPartial = { sessionViewModel.answerPartial() },
                    onNo = { sessionViewModel.answerNotCompleted() }
                )
                SessionPhase.DETAILS -> DetailsPhase(
                    uiState = uiState,
                    viewModel = sessionViewModel,
                    onSubmit = { sessionViewModel.submitSession() }
                )
                SessionPhase.SUBMITTED -> SubmittedPhase(onDone = onDone)
            }
        }
    }
}

@Composable
private fun QuestionPhase(
    onYes: () -> Unit,
    onPartial: () -> Unit,
    onNo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏃‍♀️", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Hai fatto l'allenamento\noggi?",
            style = MaterialTheme.typography.headlineMedium,
            color = NavyBlue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Registra la tua sessione per tenere traccia dei progressi",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Sì — completo
        Button(
            onClick = onYes,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Check, "Sì", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sì, tutto completo!", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Parziale
        OutlinedButton(
            onClick = onPartial,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Outlined.Edit, "Parziale", tint = CelestialBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sì, ma solo in parte", color = CelestialBlue)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // No
        OutlinedButton(
            onClick = onNo,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftRose)
        ) {
            Icon(Icons.Outlined.Close, "No", tint = SoftRose)
            Spacer(modifier = Modifier.width(8.dp))
            Text("No, oggi no", color = SoftRose)
        }
    }
}

@Composable
private fun DetailsPhase(
    uiState: SessionUiState,
    viewModel: SessionViewModel,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            if (uiState.partial) "Sessione parziale" else "Ottimo lavoro! 🎉",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Aggiungi qualche dettaglio opzionale",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Durata
        OptionalSlider(
            label = "Durata (minuti)",
            value = uiState.durationMin?.toFloat() ?: 0f,
            range = 0f..120f,
            steps = 23,
            displayValue = "${uiState.durationMin ?: 0} min",
            onValueChange = { viewModel.updateDuration(it.toInt().takeIf { v -> v > 0 }) }
        )

        // Fatica percepita
        OptionalSlider(
            label = "Fatica percepita",
            value = uiState.perceivedEffort?.toFloat() ?: 0f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.perceivedEffort ?: 0}/10",
            onValueChange = { viewModel.updateEffort(it.toInt().takeIf { v -> v > 0 }) }
        )

        // Umore
        OptionalSlider(
            label = "Umore",
            value = uiState.mood?.toFloat() ?: 5f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.mood ?: 5}/10",
            onValueChange = { viewModel.updateMood(it.toInt()) }
        )

        // Qualità sonno
        OptionalSlider(
            label = "Qualità del sonno (notte scorsa)",
            value = uiState.sleepQuality?.toFloat() ?: 5f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.sleepQuality ?: 5}/10",
            onValueChange = { viewModel.updateSleepQuality(it.toInt()) }
        )

        // Checklist esercizi (solo per sessioni parziali)
        if (uiState.partial && uiState.cardExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Esercizi completati", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))

            uiState.cardExercises.forEach { ce ->
                val exercise = uiState.exerciseDetails[ce.exerciseId]
                val checked = uiState.exerciseChecklist[ce.id] ?: true
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = checked, onCheckedChange = { viewModel.toggleExercise(ce.id) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        exercise?.name ?: "Esercizio #${ce.exerciseId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (checked) NavyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Note
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            label = { Text("Note (opzionale)") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bottone invio
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isSubmitting,
            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.isSubmitting) {
                Text("Salvataggio...")
            } else {
                Icon(Icons.Default.Check, "Salva")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva sessione", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SubmittedPhase(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SageGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, "OK", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Sessione registrata!", style = MaterialTheme.typography.headlineSmall, color = NavyBlue)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Ogni passo conta. Continua così! 💪",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Torna alla Home")
        }
    }
}

@Composable
private fun OptionalSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = NavyBlue)
            Text(displayValue, style = MaterialTheme.typography.titleSmall, color = CelestialBlue, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = CelestialBlue,
                activeTrackColor = CelestialBlue
            )
        )
    }
}
