// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel + Screen: Diario e Scale rapide
// =============================================================
package com.afa.fitadapt.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.DiaryEntryEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import com.afa.fitadapt.data.repository.DiaryRepository
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue
import com.afa.fitadapt.ui.theme.SageGreen
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
    val selectedTab: Int = 0
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

    fun selectTab(index: Int) { _uiState.update { it.copy(selectedTab = index) } }
    fun toggleAddDiary() { _uiState.update { it.copy(showAddDiary = !it.showAddDiary, newDiaryText = "") } }
    fun toggleAddScale() { _uiState.update { it.copy(showAddScale = !it.showAddScale) } }
    fun updateDiaryText(text: String) { _uiState.update { it.copy(newDiaryText = text) } }
    fun updateScaleAsthenia(v: Float) { _uiState.update { it.copy(scaleAsthenia = v) } }
    fun updateScalePain(v: Float) { _uiState.update { it.copy(scalePain = v) } }
    fun updateScaleRestDyspnea(v: Float) { _uiState.update { it.copy(scaleRestDyspnea = v) } }
    fun updateScaleExertionDyspnea(v: Float) { _uiState.update { it.copy(scaleExertionDyspnea = v) } }

    fun saveDiaryEntry() {
        val text = _uiState.value.newDiaryText.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            diaryRepository.addDiaryEntry(DiaryEntryEntity(date = System.currentTimeMillis(), text = text))
            _uiState.update { it.copy(showAddDiary = false, newDiaryText = "") }
        }
    }

    fun saveScaleEntry() {
        val s = _uiState.value
        viewModelScope.launch {
            diaryRepository.addScaleEntry(ScaleEntryEntity(
                date = System.currentTimeMillis(),
                asthenia = s.scaleAsthenia.toInt().takeIf { it > 0 },
                osteoarticularPain = s.scalePain.toInt().takeIf { it > 0 },
                restDyspnea = s.scaleRestDyspnea.toInt().takeIf { it > 0 },
                exertionDyspnea = s.scaleExertionDyspnea.toInt().takeIf { it > 0 }
            ))
            _uiState.update { it.copy(showAddScale = false, scaleAsthenia = 0f, scalePain = 0f, scaleRestDyspnea = 0f, scaleExertionDyspnea = 0f) }
        }
    }

    fun deleteDiaryEntry(entry: DiaryEntryEntity) {
        viewModelScope.launch { diaryRepository.deleteDiaryEntry(entry) }
    }
}

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(diaryViewModel: DiaryViewModel, onBack: () -> Unit) {
    val uiState by diaryViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diario", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (uiState.selectedTab == 0) diaryViewModel.toggleAddDiary() else diaryViewModel.toggleAddScale() },
                containerColor = CelestialBlue
            ) { Icon(Icons.Default.Add, "Aggiungi", tint = MaterialTheme.colorScheme.onPrimary) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab bar
            TabRow(selectedTabIndex = uiState.selectedTab, containerColor = MaterialTheme.colorScheme.background) {
                Tab(selected = uiState.selectedTab == 0, onClick = { diaryViewModel.selectTab(0) }, text = { Text("Diario libero") })
                Tab(selected = uiState.selectedTab == 1, onClick = { diaryViewModel.selectTab(1) }, text = { Text("Scale rapide") })
            }

            when (uiState.selectedTab) {
                0 -> DiaryTab(uiState, diaryViewModel)
                1 -> ScalesTab(uiState, diaryViewModel)
            }
        }
    }
}

@Composable
private fun DiaryTab(uiState: DiaryUiState, vm: DiaryViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Form per aggiungere
        if (uiState.showAddDiary) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PastelBlue), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Come ti senti oggi?", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.newDiaryText, onValueChange = { vm.updateDiaryText(it) },
                            modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5,
                            placeholder = { Text("Scrivi liberamente...") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { vm.toggleAddDiary() }) { Text("Annulla") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { vm.saveDiaryEntry() }, enabled = uiState.newDiaryText.isNotBlank()) { Text("Salva") }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (uiState.diaryEntries.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nessuna voce nel diario", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Premi + per iniziare a scrivere", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        items(uiState.diaryEntries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(DateUtils.toDisplayString(entry.date), style = MaterialTheme.typography.labelSmall, color = CelestialBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(entry.text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun ScalesTab(uiState: DiaryUiState, vm: DiaryViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.showAddScale) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PastelBlue), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Come stai oggi? (0-10)", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        ScaleSlider("Astenia/Stanchezza", uiState.scaleAsthenia) { vm.updateScaleAsthenia(it) }
                        ScaleSlider("Dolore osteoarticolare", uiState.scalePain) { vm.updateScalePain(it) }
                        ScaleSlider("Dispnea a riposo", uiState.scaleRestDyspnea) { vm.updateScaleRestDyspnea(it) }
                        ScaleSlider("Dispnea a sforzi lievi", uiState.scaleExertionDyspnea) { vm.updateScaleExertionDyspnea(it) }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { vm.toggleAddScale() }) { Text("Annulla") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { vm.saveScaleEntry() }, colors = ButtonDefaults.buttonColors(containerColor = SageGreen)) { Text("Salva") }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (uiState.scaleEntries.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nessuna rilevazione", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Premi + per registrare i tuoi sintomi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        items(uiState.scaleEntries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(DateUtils.toDisplayString(entry.date), style = MaterialTheme.typography.labelSmall, color = CelestialBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        entry.asthenia?.let { ScaleBadge("Astenia", it) }
                        entry.osteoarticularPain?.let { ScaleBadge("Dolore", it) }
                        entry.restDyspnea?.let { ScaleBadge("Dispnea R", it) }
                        entry.exertionDyspnea?.let { ScaleBadge("Dispnea S", it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScaleSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = NavyBlue)
            Text("${value.toInt()}/10", style = MaterialTheme.typography.labelLarge, color = CelestialBlue)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = 0f..10f, steps = 9)
    }
}

@Composable
private fun ScaleBadge(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$value", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = when { value <= 3 -> SageGreen; value <= 6 -> CelestialBlue; else -> MaterialTheme.colorScheme.error })
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
