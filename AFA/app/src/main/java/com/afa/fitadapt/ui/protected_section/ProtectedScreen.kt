// =============================================================
// AFA - Attività Fisica Adattata
// Sezione protetta: Gate (login con password) + Dashboard
// =============================================================
package com.afa.fitadapt.ui.protected_section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.repository.PatientProfileRepository
import com.afa.fitadapt.security.PasswordManager
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SoftAmber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ──

data class ProtectedUiState(
    val isAuthenticated: Boolean = false,
    val authError: String? = null,
    val patientCode: String = ""
)

@HiltViewModel
class ProtectedViewModel @Inject constructor(
    private val passwordManager: PasswordManager,
    private val profileRepository: PatientProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProtectedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfileSync()
            _uiState.update { it.copy(patientCode = profile?.patientCode ?: "") }
        }
    }

    fun verifyPassword(password: String) {
        viewModelScope.launch {
            val valid = passwordManager.verifyPassword(password)
            _uiState.update {
                it.copy(
                    isAuthenticated = valid,
                    authError = if (!valid) "Password non corretta" else null
                )
            }
        }
    }

    fun updatePatientCode(code: String) {
        viewModelScope.launch {
            profileRepository.updatePatientCode(code)
            _uiState.update { it.copy(patientCode = code) }
        }
    }
}

// ── Gate Screen (login) ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectedGateScreen(
    protectedViewModel: ProtectedViewModel,
    onBack: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val uiState by protectedViewModel.uiState.collectAsState()
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthenticated()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Area protetta", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Lock, "Protetta", tint = NavyBlue, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Inserisci la password", style = MaterialTheme.typography.headlineSmall, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Questa sezione è riservata all'operatore", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true, isError = uiState.authError != null
            )
            uiState.authError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { protectedViewModel.verifyPassword(password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = password.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Accedi") }
        }
    }
}

// ── Dashboard Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectedDashboardScreen(
    protectedViewModel: ProtectedViewModel,
    onBack: () -> Unit,
    onManageCards: () -> Unit,
    onManageGoals: () -> Unit
) {
    val uiState by protectedViewModel.uiState.collectAsState()
    var editingCode by remember { mutableStateOf(false) }
    var newCode by remember { mutableStateOf(uiState.patientCode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurazione", color = NavyBlue) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = NavyBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            // Codice paziente
            Text("Codice paziente", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (editingCode) {
                        OutlinedTextField(value = newCode, onValueChange = { newCode = it }, label = { Text("Codice") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { editingCode = false }) { Text("Annulla") }
                            Button(onClick = { protectedViewModel.updatePatientCode(newCode); editingCode = false }) { Text("Salva") }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Codice attuale", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(uiState.patientCode.ifBlank { "Non impostato" }, style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                            }
                            TextButton(onClick = { newCode = uiState.patientCode; editingCode = true }) { Text("Modifica") }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Gestione", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))

            DashboardCard(Icons.Outlined.FitnessCenter, "Schede di allenamento", "Crea, modifica e gestisci le schede", CelestialBlue, onManageCards)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.Flag, "Obiettivi", "Configura gli obiettivi della paziente", SageGreen, onManageGoals)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.Book, "Libreria esercizi", "Aggiungi e gestisci gli esercizi", SoftAmber) { /* TODO: Tranche 5 */ }
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.Article, "Articoli", "Aggiungi e gestisci i consigli", NavyBlue) { /* TODO: Tranche 5 */ }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DashboardCard(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier.size(48.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, title, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
