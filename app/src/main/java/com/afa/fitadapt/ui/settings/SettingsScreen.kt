// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Impostazioni
// =============================================================
package com.afa.fitadapt.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.datastore.UserPreferences
import com.afa.fitadapt.notification.WorkScheduler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.afa.fitadapt.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// ── ViewModel ──

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val notificationHour: Int = 9,
    val notificationMinute: Int = 0,
    val biometricsEnabled: Boolean = true,
    val motivationalEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val workScheduler: WorkScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { userPreferences.notificationEnabled.collect { v -> _uiState.update { it.copy(notificationsEnabled = v) } } }
        viewModelScope.launch { userPreferences.notificationHour.collect { v -> _uiState.update { it.copy(notificationHour = v) } } }
        viewModelScope.launch { userPreferences.notificationMinute.collect { v -> _uiState.update { it.copy(notificationMinute = v) } } }
        viewModelScope.launch { userPreferences.biometricsEnabled.collect { v -> _uiState.update { it.copy(biometricsEnabled = v) } } }
        viewModelScope.launch { userPreferences.motivationalMessagesEnabled.collect { v -> _uiState.update { it.copy(motivationalEnabled = v) } } }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationEnabled(enabled)
            if (enabled) {
                workScheduler.scheduleAll()
            } else {
                workScheduler.cancelAll()
            }
        }
    }

    fun toggleBiometrics(enabled: Boolean) { viewModelScope.launch { userPreferences.setBiometricsEnabled(enabled) } }

    fun toggleMotivational(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setMotivationalMessagesEnabled(enabled)
            if (enabled) {
                workScheduler.scheduleMotivationalMessages()
            } else {
                workScheduler.cancelMotivational()
            }
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferences.setNotificationTime(hour, minute)
            // Re-schedula per applicare il nuovo orario (se abilitate)
            if (_uiState.value.notificationsEnabled) {
                workScheduler.scheduleAll()
            }
        }
    }
}

// ── Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel, onBack: () -> Unit) {
    val uiState by settingsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        val themeViewModel: ThemeViewModel = hiltViewModel()
        val currentTheme by themeViewModel.currentTheme.collectAsState()

        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {

            // Sezione Tema Colore
            Text("Tema colore", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Personalizza l'aspetto dell'app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(ThemeOption.entries) { themeOption ->
                            ThemeSwatch(
                                themeOption = themeOption,
                                isSelected = currentTheme == themeOption,
                                onClick = { themeViewModel.setTheme(themeOption) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione notifiche
            Text("Notifiche", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitch("Promemoria allenamento", uiState.notificationsEnabled) { settingsViewModel.toggleNotifications(it) }
                    if (uiState.notificationsEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Orario promemoria", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                String.format(Locale.ROOT, "%02d:%02d", uiState.notificationHour, uiState.notificationMinute),
                                style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingSwitch("Messaggi motivazionali", uiState.motivationalEnabled) { settingsViewModel.toggleMotivational(it) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione sicurezza
            Text("Sicurezza", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitch("Accesso biometrico", uiState.biometricsEnabled) { settingsViewModel.toggleBiometrics(it) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info app
            Text("Info app", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow("Versione", "1.0.0")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow("Database", "Cifrato (SQLCipher)")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow("Connessione", "Offline (nessun dato inviato)")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeSwatch(
    themeOption: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(themeOption.primaryColor)
                .border(
                    width = 3.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = themeOption.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
