// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel + Screen: Diario e Scale rapide
// =============================================================
package com.afa.fitadapt.ui.diary


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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.DiaryEntryEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import com.afa.fitadapt.data.repository.DiaryRepository
import com.afa.fitadapt.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val scaleAsthenia: Float = 0f,
    val scalePain: Float = 0f,
    val scaleRestDyspnea: Float = 0f,
    val scaleExertionDyspnea: Float = 0f,
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
    private val diaryRepository: DiaryRepository
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
    fun updateScaleAsthenia(v: Float) { _uiState.update { it.copy(scaleAsthenia = v) } }
    fun updateScalePain(v: Float) { _uiState.update { it.copy(scalePain = v) } }
    fun updateScaleRestDyspnea(v: Float) { _uiState.update { it.copy(scaleRestDyspnea = v) } }
    fun updateScaleExertionDyspnea(v: Float) { _uiState.update { it.copy(scaleExertionDyspnea = v) } }
    fun updateSelectedDate(date: Long) { _uiState.update { it.copy(selectedDate = date) } }

    fun startEditingDiary(entry: DiaryEntryEntity) {
        val scale = _uiState.value.scaleEntries.find { DateUtils.isSameDay(it.date, entry.date) }
        _uiState.update { it.copy(
            editingDiaryEntry = entry,
            editingScaleEntry = scale,
            newDiaryText = entry.text,
            scaleAsthenia = scale?.asthenia?.toFloat() ?: 0f,
            scalePain = scale?.osteoarticularPain?.toFloat() ?: 0f,
            scaleRestDyspnea = scale?.restDyspnea?.toFloat() ?: 0f,
            scaleExertionDyspnea = scale?.exertionDyspnea?.toFloat() ?: 0f,
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
            scaleAsthenia = scale.asthenia?.toFloat() ?: 0f,
            scalePain = scale.osteoarticularPain?.toFloat() ?: 0f,
            scaleRestDyspnea = scale.restDyspnea?.toFloat() ?: 0f,
            scaleExertionDyspnea = scale.exertionDyspnea?.toFloat() ?: 0f,
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
                asthenia = s.scaleAsthenia.toInt().takeIf { it > 0 },
                osteoarticularPain = s.scalePain.toInt().takeIf { it > 0 },
                restDyspnea = s.scaleRestDyspnea.toInt().takeIf { it > 0 },
                exertionDyspnea = s.scaleExertionDyspnea.toInt().takeIf { it > 0 }
            )
            
            if (scaleEntry.id != 0L) {
                diaryRepository.updateScaleEntry(scaleEntry)
            } else if (scaleEntry.asthenia != null || scaleEntry.osteoarticularPain != null || 
                       scaleEntry.restDyspnea != null || scaleEntry.exertionDyspnea != null) {
                diaryRepository.addScaleEntry(scaleEntry)
            }

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
                scaleAsthenia = 0f, scalePain = 0f, scaleRestDyspnea = 0f, scaleExertionDyspnea = 0f,
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
}

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(diaryViewModel: DiaryViewModel) {
    val uiState by diaryViewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diario", color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    diaryViewModel.toggleAddDiary()
                    diaryViewModel.toggleAddScale() 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, "Aggiungi", tint = MaterialTheme.colorScheme.onPrimary) }
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
                                    editDiary != null -> "Modifica diario del ${DateUtils.toDisplayString(editDiary.date)}"
                                    editScale != null -> "Modifica scale del ${DateUtils.toDisplayString(editScale.date)}"
                                    else -> "Nuova rilevazione"
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
                            
                            Text("Scale rapide (0-10)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            ScaleSlider("Astenia", uiState.scaleAsthenia) { diaryViewModel.updateScaleAsthenia(it) }
                            ScaleSlider("Dolore", uiState.scalePain) { diaryViewModel.updateScalePain(it) }
                            ScaleSlider("Dispnea Riposo", uiState.scaleRestDyspnea) { diaryViewModel.updateScaleRestDyspnea(it) }
                            ScaleSlider("Dispnea Sforzo", uiState.scaleExertionDyspnea) { diaryViewModel.updateScaleExertionDyspnea(it) }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Diario libero", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.newDiaryText, 
                                onValueChange = { diaryViewModel.updateDiaryText(it) },
                                modifier = Modifier.fillMaxWidth().height(120.dp), 
                                maxLines = 5,
                                placeholder = { Text("Come ti senti? Note libere...") }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = { 
                                    diaryViewModel.toggleAddDiary()
                                }) { Text("Annulla") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { 
                                        diaryViewModel.saveCombinedEntry()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) { Text("Salva tutto") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Storico", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
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
                TextButton(onClick = { showDatePicker = false }) { Text("Annulla") }
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
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        group.scale.asthenia?.let { ScaleBadge("Astenia", it) }
                        group.scale.osteoarticularPain?.let { ScaleBadge("Dolore", it) }
                        group.scale.restDyspnea?.let { ScaleBadge("Dispnea R", it) }
                        group.scale.exertionDyspnea?.let { ScaleBadge("Dispnea S", it) }
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
private fun ScaleSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text("${value.toInt()}/10", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
                            Slider(
                                value = value, 
                                onValueChange = onValueChange, 
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
