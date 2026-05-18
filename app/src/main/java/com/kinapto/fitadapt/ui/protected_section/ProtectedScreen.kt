// =============================================================
// KinApto - Attività Fisica Adattata
// Sezione protetta: Gate (login con password) + Dashboard
// =============================================================
package com.kinapto.fitadapt.ui.protected_section


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Article
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kinapto.fitadapt.R
import com.kinapto.fitadapt.ui.theme.ThemeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.data.repository.PatientProfileRepository
import com.kinapto.fitadapt.security.PasswordManager
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
    val patientCode: String = "",
    val isPasswordConfigured: Boolean? = null // null indica che stiamo caricando
)

@HiltViewModel
class ProtectedViewModel @Inject constructor(
    private val passwordManager: PasswordManager,
    private val profileRepository: PatientProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProtectedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkPasswordStatus()
    }

    fun checkPasswordStatus() {
        viewModelScope.launch {
            val profile = profileRepository.getProfileSync()
            val isConfigured = passwordManager.isPasswordConfiguredSync()
            _uiState.update { 
                it.copy(
                    patientCode = profile?.patientCode ?: "",
                    isPasswordConfigured = isConfigured,
                    isAuthenticated = false // Reset obbligatorio ogni volta che si inizializza
                ) 
            }
        }
    }

    fun verifyPassword(password: String) {
        viewModelScope.launch {
            val valid = passwordManager.verifyPassword(password)
            _uiState.update {
                it.copy(
                    isAuthenticated = valid,
                    authError = if (!valid) "error_password_incorrect" else null
                )
            }
        }
    }

    fun setInitialPassword(password: String) {
        viewModelScope.launch {
            passwordManager.setPassword(password)
            _uiState.update { it.copy(isPasswordConfigured = true, isAuthenticated = true) }
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
    themeViewModel: ThemeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val uiState by protectedViewModel.uiState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()
    val legacyRed = colorResource(R.color.legacy_red)
    
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Forza il ricaricamento dello stato quando la schermata appare
    LaunchedEffect(Unit) {
        protectedViewModel.checkPasswordStatus()
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthenticated()
    }

    // Se stiamo ancora caricando lo stato dal DataStore, mostriamo un caricamento
    if (uiState.isPasswordConfigured == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isConfigured = uiState.isPasswordConfigured == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isConfigured) stringResource(R.string.protected_gate_title) else stringResource(R.string.protected_gate_setup_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isConfigured) Icons.Outlined.Lock else Icons.Outlined.AdminPanelSettings,
                contentDescription = "Protetta",
                tint = if (useOriginalColors) legacyRed else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isConfigured) stringResource(R.string.protected_gate_enter_pass) else stringResource(R.string.protected_gate_create_pass),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isConfigured) stringResource(R.string.protected_gate_desc) else stringResource(R.string.protected_gate_setup_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text(stringResource(R.string.setup_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (isConfigured) ImeAction.Done else ImeAction.Next),
                singleLine = true, isError = uiState.authError != null
            )
            
            if (!isConfigured) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.setup_password_confirm_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    singleLine = true
                )
            }

            uiState.authError?.let {
                val errorMsg = if (it == "error_password_incorrect") stringResource(R.string.protected_gate_error_pass) else it
                Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (isConfigured) {
                        protectedViewModel.verifyPassword(password)
                    } else {
                        if (password == confirmPassword && password.isNotEmpty()) {
                            protectedViewModel.setInitialPassword(password)
                        } else if (password != confirmPassword) {
                            // Errore password non corrispondenti gestito implicitamente dal bottone disabilitato o potresti aggiungere un errore nello stato
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = password.isNotEmpty() && (isConfigured || (confirmPassword.isNotEmpty() && password == confirmPassword)),
                shape = RoundedCornerShape(16.dp)
            ) { Text(if (isConfigured) stringResource(R.string.protected_gate_login) else stringResource(R.string.protected_gate_save_login)) }
        }
    }
}

// ── Dashboard Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectedDashboardScreen(
    protectedViewModel: ProtectedViewModel,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onManageCards: () -> Unit,
    onManageGoals: () -> Unit,
    onManageExercises: () -> Unit,
    onManageArticles: () -> Unit,
    onCrfExport: () -> Unit,
    onCrfImport: () -> Unit
) {
    val uiState by protectedViewModel.uiState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()

    val legacyRed = colorResource(R.color.legacy_red)
    val legacyPurple = colorResource(R.color.legacy_purple)
    val legacyGold = colorResource(R.color.legacy_gold)
    val legacyGreen = colorResource(R.color.legacy_green)

    var editingCode by remember { mutableStateOf(false) }
    var newCode by remember { mutableStateOf(uiState.patientCode) }

    // Controllo sicurezza: se non autenticato, torna indietro
    // (L'autenticazione è valida solo finché il ViewModel è in vita e lo stato è true)
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.protected_dash_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_back)) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            // Codice paziente
            Text(stringResource(R.string.setup_patient_code_title), style = MaterialTheme.typography.titleMedium, color = if (useOriginalColors) legacyRed else MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (editingCode) {
                        OutlinedTextField(value = newCode, onValueChange = { newCode = it }, label = { Text(stringResource(R.string.setup_patient_code_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { editingCode = false }) { Text(stringResource(R.string.label_cancel)) }
                            Button(
                                onClick = { protectedViewModel.updatePatientCode(newCode); editingCode = false },
                                colors = if (useOriginalColors) ButtonDefaults.buttonColors(containerColor = legacyRed) else ButtonDefaults.buttonColors()
                            ) { Text(stringResource(R.string.label_save)) }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(stringResource(R.string.protected_dash_patient_code), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(uiState.patientCode.ifBlank { stringResource(R.string.protected_dash_not_set) }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            TextButton(
                                onClick = { newCode = uiState.patientCode; editingCode = true },
                                colors = if (useOriginalColors) ButtonDefaults.textButtonColors(contentColor = legacyRed) else ButtonDefaults.textButtonColors()
                            ) { Text(stringResource(R.string.protected_dash_edit)) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.protected_dash_management), style = MaterialTheme.typography.titleMedium, color = if (useOriginalColors) legacyRed else MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            DashboardCard(Icons.Outlined.FitnessCenter, stringResource(R.string.protected_dash_cards), stringResource(R.string.protected_dash_cards_desc), if (useOriginalColors) legacyPurple else MaterialTheme.colorScheme.primary, onManageCards)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.Flag, stringResource(R.string.protected_dash_goals), stringResource(R.string.protected_dash_goals_desc), if (useOriginalColors) legacyRed else MaterialTheme.colorScheme.secondary, onManageGoals)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.Book, stringResource(R.string.protected_dash_exercises), stringResource(R.string.protected_dash_exercises_desc), if (useOriginalColors) legacyPurple else MaterialTheme.colorScheme.tertiary, onManageExercises)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.AutoMirrored.Outlined.Article, stringResource(R.string.protected_dash_articles), stringResource(R.string.protected_dash_articles_desc), if (useOriginalColors) legacyGold else MaterialTheme.colorScheme.primary, onManageArticles)

            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.protected_dash_crf_title), style = MaterialTheme.typography.titleMedium, color = if (useOriginalColors) legacyGreen else MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            DashboardCard(Icons.Outlined.FileUpload, stringResource(R.string.protected_dash_crf_export), stringResource(R.string.protected_dash_crf_export_desc), if (useOriginalColors) legacyGreen else MaterialTheme.colorScheme.secondary, onCrfExport)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardCard(Icons.Outlined.FileDownload, stringResource(R.string.protected_dash_crf_import), stringResource(R.string.protected_dash_crf_import_desc), if (useOriginalColors) legacyGreen else MaterialTheme.colorScheme.secondary, onCrfImport)

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
                Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
