// =============================================================
// KinApto - Attività Fisica Adattata
// Schermata: Registrazione sessione
// =============================================================
package com.kinapto.fitadapt.ui.session

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kinapto.fitadapt.R
import com.kinapto.fitadapt.ui.components.ActivatableScaleSlider
import com.kinapto.fitadapt.ui.theme.ThemeViewModel
import com.kinapto.fitadapt.util.DateUtils
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults

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
    themeViewModel: com.kinapto.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
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
            title = { Text(stringResource(R.string.session_title), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                if (uiState.phase != SessionPhase.SUBMITTED) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.session_back), tint = MaterialTheme.colorScheme.onBackground)
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
                    }) { Text(stringResource(R.string.session_ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.session_cancel)) }
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
                    text = if (DateUtils.isToday(selectedDate)) stringResource(R.string.session_today) 
                           else DateUtils.toDisplayString(selectedDate),
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
            stringResource(R.string.session_question),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.session_question_desc),
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
            Icon(Icons.Default.Check, null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.session_yes), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
            Icon(Icons.Outlined.Edit, null, tint = if (useOriginalColors) legacyBlue else themePrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.session_partial), style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // No
        TextButton(
            onClick = onNo,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(stringResource(R.string.session_no), color = if (useOriginalColors) legacyRed else themeError, style = MaterialTheme.typography.titleSmall)
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
                    if (uiState.partial) stringResource(R.string.session_partial_title) else stringResource(R.string.session_success_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
                Text(
                    stringResource(R.string.session_details_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicatore completamento
        val totalScales = 14
        val filledScales = uiState.touchedFields.size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AssistChip(
                onClick = {},
                label = { Text("$filledScales / $totalScales compilati") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (filledScales == totalScales) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (filledScales == totalScales) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                border = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Durata
        ActivatableScaleSlider(
            label = stringResource(R.string.session_duration),
            value = uiState.durationMin,
            isActive = "durationMin" in uiState.touchedFields,
            onValueChange = { viewModel.updateDuration(it) },
            onActivated = { viewModel.touchField("durationMin") },
            range = 0..120,
            minLabel = "0 min",
            maxLabel = "120 min"
        )

        // Fatica percepita
        ActivatableScaleSlider(
            label = stringResource(R.string.session_effort),
            value = uiState.perceivedEffort,
            isActive = "perceivedEffort" in uiState.touchedFields,
            onValueChange = { viewModel.updateEffort(it) },
            onActivated = { viewModel.touchField("perceivedEffort") },
            minLabel = "0 minima",
            maxLabel = "10 massima"
        )

        // Astenia
        ActivatableScaleSlider(
            label = stringResource(R.string.session_asthenia),
            value = uiState.asthenia,
            isActive = "asthenia" in uiState.touchedFields,
            onValueChange = { viewModel.updateAsthenia(it) },
            onActivated = { viewModel.touchField("asthenia") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Dolore
        ActivatableScaleSlider(
            label = stringResource(R.string.session_pain),
            value = uiState.osteoarticularPain,
            isActive = "osteoarticularPain" in uiState.touchedFields,
            onValueChange = { viewModel.updatePain(it) },
            onActivated = { viewModel.touchField("osteoarticularPain") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Dispnea a riposo
        ActivatableScaleSlider(
            label = stringResource(R.string.session_rest_dyspnea),
            value = uiState.restDyspnea,
            isActive = "restDyspnea" in uiState.touchedFields,
            onValueChange = { viewModel.updateRestDyspnea(it) },
            onActivated = { viewModel.touchField("restDyspnea") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Dispnea sotto sforzo
        ActivatableScaleSlider(
            label = stringResource(R.string.session_exertion_dyspnea),
            value = uiState.exertionDyspnea,
            isActive = "exertionDyspnea" in uiState.touchedFields,
            onValueChange = { viewModel.updateExertionDyspnea(it) },
            onActivated = { viewModel.touchField("exertionDyspnea") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Nausea
        ActivatableScaleSlider(
            label = stringResource(R.string.session_nausea),
            value = uiState.nausea,
            isActive = "nausea" in uiState.touchedFields,
            onValueChange = { viewModel.updateNausea(it) },
            onActivated = { viewModel.touchField("nausea") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Umore
        ActivatableScaleSlider(
            label = stringResource(R.string.session_mood),
            value = uiState.mood,
            isActive = "mood" in uiState.touchedFields,
            onValueChange = { viewModel.updateMood(it) },
            onActivated = { viewModel.touchField("mood") },
            minLabel = "0 pessimo",
            maxLabel = "10 ottimo"
        )

        // Qualità sonno
        ActivatableScaleSlider(
            label = stringResource(R.string.session_sleep),
            value = uiState.sleepQuality,
            isActive = "sleepQuality" in uiState.touchedFields,
            onValueChange = { viewModel.updateSleepQuality(it) },
            onActivated = { viewModel.touchField("sleepQuality") },
            minLabel = "0 pessima",
            maxLabel = "10 ottima"
        )

        // Appetito
        ActivatableScaleSlider(
            label = stringResource(R.string.session_appetite),
            value = uiState.appetite,
            isActive = "appetite" in uiState.touchedFields,
            onValueChange = { viewModel.updateAppetite(it) },
            onActivated = { viewModel.touchField("appetite") },
            minLabel = "0 nessun appetito",
            maxLabel = "10 ottimo"
        )

        // Ansia
        ActivatableScaleSlider(
            label = stringResource(R.string.session_anxiety),
            value = uiState.anxiety,
            isActive = "anxiety" in uiState.touchedFields,
            onValueChange = { viewModel.updateAnxiety(it) },
            onActivated = { viewModel.touchField("anxiety") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Linfedema
        ActivatableScaleSlider(
            label = stringResource(R.string.label_lymphoedema),
            value = uiState.lymphoedema,
            isActive = "lymphoedema" in uiState.touchedFields,
            onValueChange = { viewModel.updateLymphoedema(it) },
            onActivated = { viewModel.touchField("lymphoedema") },
            minLabel = "0 nessuna/o",
            maxLabel = "10 intensa/o"
        )

        // Qualità della vita
        ActivatableScaleSlider(
            label = stringResource(R.string.label_quality_of_life),
            value = uiState.qualityOfLife,
            isActive = "qualityOfLife" in uiState.touchedFields,
            onValueChange = { viewModel.updateQualityOfLife(it) },
            onActivated = { viewModel.touchField("qualityOfLife") },
            minLabel = "0 pessima",
            maxLabel = "10 ottima"
        )

        // Benessere
        ActivatableScaleSlider(
            label = stringResource(R.string.label_well_being),
            value = uiState.wellBeing,
            isActive = "wellBeing" in uiState.touchedFields,
            onValueChange = { viewModel.updateWellBeing(it) },
            onActivated = { viewModel.touchField("wellBeing") },
            minLabel = "0 pessimo",
            maxLabel = "10 ottimo"
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.spo2,
                onValueChange = { viewModel.updateSpo2(it) },
                label = { Text(stringResource(R.string.label_spo2)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    focusedLabelColor = accentColor,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = uiState.heartRate,
                onValueChange = { viewModel.updateHeartRate(it) },
                label = { Text(stringResource(R.string.label_heart_rate)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    focusedLabelColor = accentColor,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Appetito
        ActivatableScaleSlider(
            label = stringResource(R.string.session_appetite),
            value = uiState.appetite,
            isActive = "appetite" in uiState.touchedFields,
            minLabel = "0 nessun appetito",
            maxLabel = "10 ottimo",
            onValueChange = { viewModel.updateAppetite(it) },
            onActivated = { viewModel.touchField("appetite") }
        )


        // Checklist esercizi (solo per sessioni parziali)
        if (uiState.partial && uiState.cardExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(R.string.session_exercises_completed),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            uiState.cardExercises.forEach { ce ->
                val exercise = uiState.exerciseDetails[ce.cardExercise.exerciseId]
                val checked = uiState.exerciseChecklist[ce.cardExercise.id] ?: true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (checked) accentColor.copy(alpha = 0.05f) else Color.Transparent)
                        .clickable { viewModel.toggleExercise(ce.cardExercise.id) }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { viewModel.toggleExercise(ce.cardExercise.id) },
                        colors = CheckboxDefaults.colors(checkedColor = accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        exercise?.name ?: stringResource(R.string.session_exercise_prefix, ce.cardExercise.exerciseId),
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
            label = { Text(stringResource(R.string.session_notes_label)) },
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
                Text(stringResource(R.string.session_saving))
            } else {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.session_save_button), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
            Icon(Icons.Default.Check, null, tint = successColor, modifier = Modifier.size(56.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.session_submitted_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.session_submitted_desc),
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
            Text(stringResource(R.string.session_go_home), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNewSession,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (useOriginalColors) successColor else MaterialTheme.colorScheme.primary),
            border = androidx.compose.foundation.BorderStroke(1.dp, (if (useOriginalColors) successColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.3f))
        ) {
            Text(stringResource(R.string.session_record_another), style = MaterialTheme.typography.titleSmall)
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
    minLabel: String? = null,
    maxLabel: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(displayValue, style = MaterialTheme.typography.titleMedium, color = accentColor, fontWeight = FontWeight.Bold)
        }
        
        if (minLabel != null && maxLabel != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(minLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(maxLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
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
