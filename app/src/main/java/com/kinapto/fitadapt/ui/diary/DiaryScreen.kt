// =============================================================
// KinApto - Attività Fisica Adattata
// ViewModel + Screen: Diario e Scale rapide
// =============================================================
package com.kinapto.fitadapt.ui.diary


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.data.local.entity.DiaryEntryEntity
import com.kinapto.fitadapt.data.local.entity.ScaleEntryEntity
import com.kinapto.fitadapt.data.repository.DiaryRepository
import com.kinapto.fitadapt.data.repository.TrainingCardRepository
import com.kinapto.fitadapt.domain.AdaptationManager
import com.kinapto.fitadapt.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ──

data class DiaryUiState(
    val diaryEntries: List<DiaryEntryEntity> = emptyList(),
    val scaleEntries: List<ScaleEntryEntity> = emptyList(),
    val showAddDiary: Boolean = false,
    val showAddScale: Boolean = false,
    val newDiaryText: String = "",
    val scaleEffort: Int = 0,
    val scaleAsthenia: Int = 0,
    val scalePain: Int = 0,
    val scaleRestDyspnea: Int = 0,
    val scaleExertionDyspnea: Int = 0,
    val scaleMood: Int = 5,
    val scaleSleep: Int = 5,
    val scaleNausea: Int = 0,
    val scaleAppetite: Int = 5,
    val scaleAnxiety: Int = 0,
    val scaleLymphoedema: Int = 0,
    val scaleQualityOfLife: Int = 5,
    val scaleWellBeing: Int = 5,
    val spo2: String = "",
    val heartRate: String = "",
    val selectedTab: Int = 0,
    val editingDiaryEntry: DiaryEntryEntity? = null,
    val editingScaleEntry: ScaleEntryEntity? = null,
    val selectedDate: Long = System.currentTimeMillis()
)

data class DayGroup(
    val date: Long,
    val scale: ScaleEntryEntity?,
    val diaryEntries: List<DiaryEntryEntity>
)

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val cardRepository: TrainingCardRepository,
    private val adaptationManager: AdaptationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            diaryRepository.getAllDiaryEntries().collect { entries ->
                _uiState.update { it.copy(diaryEntries = entries) }
            }
        }
        viewModelScope.launch {
            diaryRepository.getLatestScaleEntries(30).collect { entries ->
                _uiState.update { it.copy(scaleEntries = entries) }
            }
        }
    }

    fun toggleAddDiary() {
        _uiState.update { it.copy(
            showAddDiary = !it.showAddDiary,
            newDiaryText = "",
            editingDiaryEntry = null,
            editingScaleEntry = null,
            selectedDate = System.currentTimeMillis()
        ) }
    }

    fun toggleAddScale() { _uiState.update { it.copy(showAddScale = !it.showAddScale) } }
    
    fun updateDiaryText(text: String) { _uiState.update { it.copy(newDiaryText = text) } }
    fun updateScaleEffort(v: Int) { _uiState.update { it.copy(scaleEffort = v) } }
    fun updateScaleAsthenia(v: Int) { _uiState.update { it.copy(scaleAsthenia = v) } }
    fun updateScalePain(v: Int) { _uiState.update { it.copy(scalePain = v) } }
    fun updateScaleRestDyspnea(v: Int) { _uiState.update { it.copy(scaleRestDyspnea = v) } }
    fun updateScaleExertionDyspnea(v: Int) { _uiState.update { it.copy(scaleExertionDyspnea = v) } }
    fun updateScaleMood(v: Int) { _uiState.update { it.copy(scaleMood = v) } }
    fun updateScaleSleep(v: Int) { _uiState.update { it.copy(scaleSleep = v) } }
    fun updateScaleNausea(v: Int) { _uiState.update { it.copy(scaleNausea = v) } }
    fun updateScaleAppetite(v: Int) { _uiState.update { it.copy(scaleAppetite = v) } }
    fun updateScaleAnxiety(v: Int) { _uiState.update { it.copy(scaleAnxiety = v) } }
    fun updateScaleLymphoedema(v: Int) { _uiState.update { it.copy(scaleLymphoedema = v) } }
    fun updateScaleQualityOfLife(v: Int) { _uiState.update { it.copy(scaleQualityOfLife = v) } }
    fun updateScaleWellBeing(v: Int) { _uiState.update { it.copy(scaleWellBeing = v) } }
    fun updateSpo2(v: String) { _uiState.update { it.copy(spo2 = v) } }
    fun updateHeartRate(v: String) { _uiState.update { it.copy(heartRate = v) } }
    fun updateSelectedDate(date: Long) { _uiState.update { it.copy(selectedDate = date) } }

    fun startEditingDiary(entry: DiaryEntryEntity) {
        val scale = _uiState.value.scaleEntries.find { DateUtils.isSameDay(it.date, entry.date) }
        _uiState.update { it.copy(
            editingDiaryEntry = entry,
            editingScaleEntry = scale,
            newDiaryText = entry.text,
            scaleEffort = scale?.perceivedEffort ?: 0,
            scaleAsthenia = scale?.asthenia ?: 0,
            scalePain = scale?.osteoarticularPain ?: 0,
            scaleRestDyspnea = scale?.restDyspnea ?: 0,
            scaleExertionDyspnea = scale?.exertionDyspnea ?: 0,
            scaleMood = scale?.mood ?: 5,
            scaleSleep = scale?.sleepQuality ?: 5,
            scaleNausea = scale?.nausea ?: 0,
            scaleAppetite = scale?.appetite ?: 5,
            scaleAnxiety = scale?.anxiety ?: 0,
            scaleLymphoedema = scale?.lymphoedema ?: 0,
            scaleQualityOfLife = scale?.qualityOfLife ?: 5,
            scaleWellBeing = scale?.wellBeing ?: 5,
            spo2 = scale?.spo2?.toString() ?: "",
            heartRate = scale?.heartRate?.toString() ?: "",
            selectedDate = entry.date,
            showAddDiary = true,
            showAddScale = true
        ) }
    }

    fun startEditingScale(scale: ScaleEntryEntity) {
        _uiState.update { it.copy(
            editingScaleEntry = scale,
            editingDiaryEntry = null,
            newDiaryText = "",
            scaleEffort = scale.perceivedEffort ?: 0,
            scaleAsthenia = scale.asthenia ?: 0,
            scalePain = scale.osteoarticularPain ?: 0,
            scaleRestDyspnea = scale.restDyspnea ?: 0,
            scaleExertionDyspnea = scale.exertionDyspnea ?: 0,
            scaleMood = scale.mood ?: 5,
            scaleSleep = scale.sleepQuality ?: 5,
            scaleNausea = scale.nausea ?: 0,
            scaleAppetite = scale.appetite ?: 5,
            scaleAnxiety = scale.anxiety ?: 0,
            scaleLymphoedema = scale.lymphoedema ?: 0,
            scaleQualityOfLife = scale.qualityOfLife ?: 5,
            scaleWellBeing = scale.wellBeing ?: 5,
            spo2 = scale.spo2?.toString() ?: "",
            heartRate = scale.heartRate?.toString() ?: "",
            selectedDate = scale.date,
            showAddDiary = true,
            showAddScale = true
        ) }
    }

    fun saveCombinedEntry() {
        val s = _uiState.value
        val targetDate = s.selectedDate
        
        viewModelScope.launch {
            // 1. Handle Scale Entry (One per day)
            // If we are editing a specific scale entry, use its ID.
            // Otherwise, look if there's already a scale for the target date.
            val existingScale = s.editingScaleEntry ?: s.scaleEntries.find { DateUtils.isSameDay(it.date, targetDate) }
            
            val scaleEntry = ScaleEntryEntity(
                id = existingScale?.id ?: 0,
                date = targetDate,
                perceivedEffort = s.scaleEffort.takeIf { it > 0 },
                asthenia = s.scaleAsthenia.takeIf { it > 0 },
                osteoarticularPain = s.scalePain.takeIf { it > 0 },
                restDyspnea = s.scaleRestDyspnea.takeIf { it > 0 },
                exertionDyspnea = s.scaleExertionDyspnea.takeIf { it > 0 },
                mood = s.scaleMood,
                sleepQuality = s.scaleSleep,
                nausea = s.scaleNausea.takeIf { it > 0 },
                appetite = s.scaleAppetite,
                anxiety = s.scaleAnxiety.takeIf { it > 0 },
                lymphoedema = s.scaleLymphoedema.takeIf { it > 0 },
                qualityOfLife = s.scaleQualityOfLife,
                wellBeing = s.scaleWellBeing,
                spo2 = s.spo2.toIntOrNull(),
                heartRate = s.heartRate.toIntOrNull()
            )
            
            if (scaleEntry.id != 0L) {
                diaryRepository.updateScaleEntry(scaleEntry)
            } else if (scaleEntry.asthenia != null || scaleEntry.osteoarticularPain != null || 
                       scaleEntry.restDyspnea != null || scaleEntry.exertionDyspnea != null ||
                       scaleEntry.nausea != null || scaleEntry.anxiety != null ||
                       scaleEntry.perceivedEffort != null || scaleEntry.lymphoedema != null ||
                       scaleEntry.spo2 != null || scaleEntry.heartRate != null) {
                diaryRepository.addScaleEntry(scaleEntry)
            }

            // --- Bio-feedback Adaptation ---
            checkBioFeedbackAdaptation()

            // 2. Handle Diary Entry
            if (s.newDiaryText.isNotBlank()) {
                if (s.editingDiaryEntry != null) {
                    diaryRepository.updateDiaryEntry(s.editingDiaryEntry.copy(text = s.newDiaryText.trim(), date = targetDate))
                } else {
                    diaryRepository.addDiaryEntry(DiaryEntryEntity(date = targetDate, text = s.newDiaryText.trim()))
                }
            }
            
            _uiState.update { it.copy(
                showAddDiary = false, 
                showAddScale = false,
                newDiaryText = "",
                scaleEffort = 0,
                scaleAsthenia = 0, 
                scalePain = 0, 
                scaleRestDyspnea = 0, 
                scaleExertionDyspnea = 0,
                scaleMood = 5,
                scaleSleep = 5,
                scaleNausea = 0,
                scaleAppetite = 5,
                scaleAnxiety = 0,
                scaleLymphoedema = 0,
                scaleQualityOfLife = 5,
                scaleWellBeing = 5,
                spo2 = "",
                heartRate = "",
                editingDiaryEntry = null,
                editingScaleEntry = null,
                selectedDate = System.currentTimeMillis()
            ) }
        }
    }

    @Suppress("unused") // API per swipe-to-delete (da collegare alla UI)
    fun deleteDiaryEntry(entry: DiaryEntryEntity) {
        viewModelScope.launch { diaryRepository.deleteDiaryEntry(entry) }
    }

    private suspend fun checkBioFeedbackAdaptation() {
        val activeCard = cardRepository.getActiveCard().firstOrNull() ?: return
        adaptationManager.checkAndApplyAdaptation(activeCard)
    }
}

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(diaryViewModel: DiaryViewModel) {
    val uiState by diaryViewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(vertical = 20.dp, horizontal = 24.dp)
                ) {
                    Text(
                        stringResource(R.string.diary_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    diaryViewModel.toggleAddDiary()
                    diaryViewModel.toggleAddScale() 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, stringResource(R.string.diary_add_entry), tint = MaterialTheme.colorScheme.onPrimary) }
        }
    ) { padding ->
        val groupedEntries = remember(uiState.diaryEntries, uiState.scaleEntries) {
            val allDates = (uiState.scaleEntries.map { DateUtils.toDayTimestamp(it.date) } + 
                           uiState.diaryEntries.map { DateUtils.toDayTimestamp(it.date) })
                .distinct()
                .sortedDescending()
            
            allDates.map { date ->
                DayGroup(
                    date = date,
                    scale = uiState.scaleEntries.find { DateUtils.isSameDay(it.date, date) },
                    diaryEntries = uiState.diaryEntries.filter { DateUtils.isSameDay(it.date, date) }
                        .sortedByDescending { it.createdAt }
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Sezione Scale Rapide (Sempre visibile se in modalità inserimento/modifica)
            if (uiState.showAddDiary || uiState.showAddScale) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val titleText = remember(uiState.editingDiaryEntry, uiState.editingScaleEntry) {
                                val editDiary = uiState.editingDiaryEntry
                                val editScale = uiState.editingScaleEntry
                                when {
                                    editDiary != null -> context.getString(R.string.diary_edit_title, DateUtils.toDisplayString(editDiary.date))
                                    editScale != null -> context.getString(R.string.diary_edit_scale_title, DateUtils.toDisplayString(editScale.date))
                                    else -> context.getString(R.string.diary_new_entry)
                                }
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(titleText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                
                                // Date selection for new entry
                                if (uiState.editingDiaryEntry == null && uiState.editingScaleEntry == null) {
                                    TextButton(
                                        onClick = { showDatePicker = true },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(DateUtils.toDisplayString(uiState.selectedDate), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(stringResource(R.string.diary_scales_title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            ScaleSlider(stringResource(R.string.diary_label_fatigue), uiState.scaleEffort) { diaryViewModel.updateScaleEffort(it) }
                            ScaleSlider(stringResource(R.string.label_asthenia), uiState.scaleAsthenia) { diaryViewModel.updateScaleAsthenia(it) }
                            ScaleSlider(stringResource(R.string.label_pain), uiState.scalePain) { diaryViewModel.updateScalePain(it) }
                            ScaleSlider(stringResource(R.string.diary_label_dyspnea_r), uiState.scaleRestDyspnea) { diaryViewModel.updateScaleRestDyspnea(it) }
                            ScaleSlider(stringResource(R.string.diary_label_dyspnea_s), uiState.scaleExertionDyspnea) { diaryViewModel.updateScaleExertionDyspnea(it) }
                            ScaleSlider(stringResource(R.string.label_nausea), uiState.scaleNausea) { diaryViewModel.updateScaleNausea(it) }
                            ScaleSlider(stringResource(R.string.label_mood), uiState.scaleMood) { diaryViewModel.updateScaleMood(it) }
                            ScaleSlider(stringResource(R.string.diary_label_ansia), uiState.scaleAnxiety) { diaryViewModel.updateScaleAnxiety(it) }
                            ScaleSlider(stringResource(R.string.label_sleep), uiState.scaleSleep) { diaryViewModel.updateScaleSleep(it) }
                            ScaleSlider(stringResource(R.string.label_appetite), uiState.scaleAppetite) { diaryViewModel.updateScaleAppetite(it) }
                            ScaleSlider(stringResource(R.string.label_lymphoedema), uiState.scaleLymphoedema) { diaryViewModel.updateScaleLymphoedema(it) }
                            ScaleSlider(stringResource(R.string.label_quality_of_life), uiState.scaleQualityOfLife) { diaryViewModel.updateScaleQualityOfLife(it) }
                            ScaleSlider(stringResource(R.string.label_well_being), uiState.scaleWellBeing) { diaryViewModel.updateScaleWellBeing(it) }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = uiState.spo2,
                                    onValueChange = { diaryViewModel.updateSpo2(it) },
                                    label = { Text(stringResource(R.string.label_spo2)) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = uiState.heartRate,
                                    onValueChange = { diaryViewModel.updateHeartRate(it) },
                                    label = { Text(stringResource(R.string.label_heart_rate)) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.diary_free_text_title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.newDiaryText, 
                                onValueChange = { diaryViewModel.updateDiaryText(it) },
                                modifier = Modifier.fillMaxWidth().height(120.dp), 
                                maxLines = 5,
                                placeholder = { Text(stringResource(R.string.diary_placeholder)) }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = { 
                                    diaryViewModel.toggleAddDiary()
                                }) { Text(stringResource(R.string.label_cancel)) }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { 
                                        diaryViewModel.saveCombinedEntry()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) { Text(stringResource(R.string.diary_save_all)) }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.diary_history), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(groupedEntries) { group ->
                DayGroupCard(
                    group = group, 
                    onEditDiary = { diaryViewModel.startEditingDiary(it) },
                    onEditScale = { diaryViewModel.startEditingScale(it) }
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        diaryViewModel.updateSelectedDate(it)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.label_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DayGroupCard(
    group: DayGroup, 
    onEditDiary: (DiaryEntryEntity) -> Unit,
    onEditScale: (ScaleEntryEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (group.scale != null && group.diaryEntries.isNotEmpty()) "📊📝" else if (group.scale != null) "📊" else "📝", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(DateUtils.toDisplayString(group.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            
            if (group.scale != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = { onEditScale(group.scale) },
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            group.scale.perceivedEffort?.let { ScaleBadge(stringResource(R.string.diary_label_fatigue), it) }
                            group.scale.asthenia?.let { ScaleBadge(stringResource(R.string.label_asthenia), it) }
                            group.scale.osteoarticularPain?.let { ScaleBadge(stringResource(R.string.label_pain), it) }
                            group.scale.restDyspnea?.let { ScaleBadge(stringResource(R.string.diary_label_dyspnea_r), it) }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            group.scale.exertionDyspnea?.let { ScaleBadge(stringResource(R.string.diary_label_dyspnea_s), it) }
                            group.scale.nausea?.let { ScaleBadge(stringResource(R.string.label_nausea), it) }
                            group.scale.mood?.let { ScaleBadge(stringResource(R.string.label_mood), it) }
                            group.scale.anxiety?.let { ScaleBadge(stringResource(R.string.diary_label_ansia), it) }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            group.scale.sleepQuality?.let { ScaleBadge(stringResource(R.string.label_sleep), it) }
                            group.scale.appetite?.let { ScaleBadge(stringResource(R.string.label_appetite), it) }
                        }
                    }
                }
            }
            
            group.diaryEntries.forEachIndexed { index, diary ->
                if (index > 0 || group.scale != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Surface(
                    onClick = { onEditDiary(diary) },
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        diary.text, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScaleSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text("${value}/10", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
                            Slider(
                                value = value.toFloat(), 
                                onValueChange = { onValueChange(it.toInt()) },
                                valueRange = 0f..10f, 
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                                    activeTickColor = Color.Transparent,
                                    inactiveTickColor = Color.Transparent
                                )
                            )
    }
}

@Composable
private fun ScaleBadge(label: String, value: Int) {
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeOnSurface = MaterialTheme.colorScheme.onSurface
    
    // Check if the primary color is "red-like" (very simple heuristic)
    // In a real app we might check the theme name or specific palette tokens
    val isRedTheme = themePrimary.red > 0.7f && themePrimary.green < 0.4f && themePrimary.blue < 0.4f

    val valueColor = when {
        value >= 7 -> MaterialTheme.colorScheme.error
        value <= 6 -> {
            if (isRedTheme) themeOnSurface else themePrimary
        }
        else -> themePrimary
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$value", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
