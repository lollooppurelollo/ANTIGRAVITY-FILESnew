// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Registrazione sessione
// =============================================================
package com.afa.fitadapt.ui.session

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afa.fitadapt.R
import com.afa.fitadapt.ui.theme.ThemeViewModel

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
    themeViewModel: com.afa.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val uiState by sessionViewModel.uiState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Registra sessione", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                if (uiState.phase != SessionPhase.SUBMITTED) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDate)

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { sessionViewModel.updateDate(it) }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Annulla") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        AnimatedContent(targetState = uiState.phase, label = "sessionPhase") { phase ->
            when (phase) {
                SessionPhase.QUESTION -> QuestionPhase(
                    selectedDate = uiState.selectedDate,
                    onShowDatePicker = { showDatePicker = true },
                    useOriginalColors = useOriginalColors,
                    onYes = { sessionViewModel.answerCompleted() },
                    onPartial = { sessionViewModel.answerPartial() },
                    onNo = { sessionViewModel.answerNotCompleted() }
                )
                SessionPhase.DETAILS -> DetailsPhase(
                    uiState = uiState,
                    viewModel = sessionViewModel,
                    useOriginalColors = useOriginalColors,
                    onSubmit = { sessionViewModel.submitSession() }
                )
                SessionPhase.SUBMITTED -> SubmittedPhase(
                    useOriginalColors = useOriginalColors,
                    onDone = onDone,
                    onNewSession = { sessionViewModel.reset() }
                )
            }
        }
    }
}

@Composable
private fun QuestionPhase(
    selectedDate: Long,
    onShowDatePicker: () -> Unit,
    useOriginalColors: Boolean,
    onYes: () -> Unit,
    onPartial: () -> Unit,
    onNo: () -> Unit
) {
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSecondary = MaterialTheme.colorScheme.secondary
    val themeError = MaterialTheme.colorScheme.error

    val legacyPurple = colorResource(R.color.legacy_purple)
    val legacyRed = colorResource(R.color.legacy_red)
    val legacyBlue = colorResource(R.color.legacy_blue)
    val legacyGreen = colorResource(R.color.legacy_green)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Selettore Data
        Card(
            onClick = onShowDatePicker,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (com.afa.fitadapt.util.DateUtils.isToday(selectedDate)) "Oggi" 
                           else com.afa.fitadapt.util.DateUtils.toDisplayString(selectedDate),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .background((if (useOriginalColors) legacyPurple else themePrimary).copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🏃‍♀️", fontSize = 64.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Hai fatto l'allenamento\noggi?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Registra la tua sessione per monitorare i tuoi progressi di salute",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Sì — completo
        Button(
            onClick = onYes,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (useOriginalColors) legacyGreen else themeSecondary),
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Default.Check, "Sì", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sì, completato!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parziale
        OutlinedButton(
            onClick = onPartial,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (useOriginalColors) legacyBlue else themePrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, (if (useOriginalColors) legacyBlue else themePrimary).copy(alpha = 0.3f))
        ) {
            Icon(Icons.Outlined.Edit, "Parziale", tint = if (useOriginalColors) legacyBlue else themePrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sì, ma solo in parte", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // No
        TextButton(
            onClick = onNo,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("No, oggi no", color = if (useOriginalColors) legacyRed else themeError, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun DetailsPhase(
    uiState: SessionUiState,
    viewModel: SessionViewModel,
    useOriginalColors: Boolean,
    onSubmit: () -> Unit
) {
    val legacyPurple = colorResource(R.color.legacy_purple)
    val accentColor = if (useOriginalColors) legacyPurple else MaterialTheme.colorScheme.primary
    val secondaryColor = if (useOriginalColors) colorResource(R.color.legacy_green) else MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    if (uiState.partial) "Sessione parziale" else "Ottimo lavoro! 🎉",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
                Text(
                    "Aggiungi qualche dettaglio sulla tua attività",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Durata
        OptionalSlider(
            label = "Durata (minuti)",
            value = uiState.durationMin?.toFloat() ?: 0f,
            range = 0f..120f,
            steps = 23,
            displayValue = "${uiState.durationMin ?: 0} min",
            accentColor = accentColor,
            onValueChange = { viewModel.updateDuration(it.toInt().takeIf { v -> v > 0 }) }
        )

        // Fatica percepita
        OptionalSlider(
            label = "Fatica percepita",
            value = uiState.perceivedEffort?.toFloat() ?: 0f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.perceivedEffort ?: 0}/10",
            accentColor = accentColor,
            onValueChange = { viewModel.updateEffort(it.toInt().takeIf { v -> v > 0 }) }
        )

        // Umore
        OptionalSlider(
            label = "Umore",
            value = uiState.mood?.toFloat() ?: 5f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.mood ?: 5}/10",
            accentColor = accentColor,
            onValueChange = { viewModel.updateMood(it.toInt()) }
        )

        // Qualità sonno
        OptionalSlider(
            label = "Qualità del sonno",
            value = uiState.sleepQuality?.toFloat() ?: 5f,
            range = 0f..10f,
            steps = 9,
            displayValue = "${uiState.sleepQuality ?: 5}/10",
            accentColor = accentColor,
            onValueChange = { viewModel.updateSleepQuality(it.toInt()) }
        )

        // Checklist esercizi (solo per sessioni parziali)
        if (uiState.partial && uiState.cardExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Esercizi completati",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            uiState.cardExercises.forEach { ce ->
                val exercise = uiState.exerciseDetails[ce.exerciseId]
                val checked = uiState.exerciseChecklist[ce.id] ?: true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (checked) accentColor.copy(alpha = 0.05f) else Color.Transparent)
                        .clickable { viewModel.toggleExercise(ce.id) }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { viewModel.toggleExercise(ce.id) },
                        colors = CheckboxDefaults.colors(checkedColor = accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        exercise?.name ?: "Esercizio #${ce.exerciseId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (checked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Note
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            label = { Text("Note (opzionale)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            maxLines = 4,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                focusedLabelColor = accentColor,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bottone invio
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            enabled = !uiState.isSubmitting,
            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            if (uiState.isSubmitting) {
                Text("Salvataggio...")
            } else {
                Icon(Icons.Default.Check, "Salva")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Salva sessione", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun SubmittedPhase(
    useOriginalColors: Boolean, 
    onDone: () -> Unit,
    onNewSession: () -> Unit
) {
    val legacyGreen = colorResource(R.color.legacy_green)
    val successColor = if (useOriginalColors) legacyGreen else MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(successColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, "OK", tint = successColor, modifier = Modifier.size(56.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Sessione registrata!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Ottimo lavoro. Ogni passo conta per il tuo benessere! 💪",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (useOriginalColors) successColor else MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Torna alla Home", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNewSession,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (useOriginalColors) successColor else MaterialTheme.colorScheme.primary),
            border = androidx.compose.foundation.BorderStroke(1.dp, (if (useOriginalColors) successColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.3f))
        ) {
            Text("Registra un altro allenamento", style = MaterialTheme.typography.titleSmall)
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
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(displayValue, style = MaterialTheme.typography.titleMedium, color = accentColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )
        )
    }
}
