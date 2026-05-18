// =============================================================
// KinApto - Attività Fisica Adattata
// ViewModel: Autenticazione e setup iniziale
// =============================================================
package com.kinapto.fitadapt.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinapto.fitadapt.R
import com.kinapto.fitadapt.data.local.datastore.UserPreferences
import com.kinapto.fitadapt.data.local.db.DatabaseSeeder
import com.kinapto.fitadapt.data.repository.PatientProfileRepository
import com.kinapto.fitadapt.security.PasswordManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stato dell'interfaccia di autenticazione.
 */
data class AuthUiState(
    val isLoading: Boolean = true,
    val isFirstLaunch: Boolean = true,
    val isAuthenticated: Boolean = false,
    val authError: String? = null,
    val setupStep: Int = 0,         // 0=password, 1=patientCode, 2=conferma
    val setupCompleted: Boolean = false,
    val passwordError: String? = null
)

/**
 * ViewModel per splash, setup wizard e blocco biometrico.
 *
 * Gestisce il flusso:
 * 1. Splash → verifica se è il primo avvio
 * 2. Se primo avvio → Setup Wizard (password + patientCode)
 * 3. Se già configurato → Blocco biometrico
 * 4. Dopo autenticazione → navigazione alla Home
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    private val passwordManager: PasswordManager,
    private val profileRepository: PatientProfileRepository,
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeApp()
    }

    /**
     * Controlla lo stato iniziale dell'app e prepara il database.
     */
    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Popola il database con esercizi e articoli se è la prima volta
                databaseSeeder.seedIfEmpty()

                // Inizializza il profilo paziente se non esiste
                profileRepository.initializeIfNeeded()

                // Controlla se il setup iniziale è stato completato
                val isSetupDone = userPreferences.isFirstSetupCompleted()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFirstLaunch = !isSetupDone
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authError = context.getString(R.string.error_init, e.message)
                    )
                }
            }
        }
    }

    // ── Biometria ──

    /** Chiamata quando l'autenticazione biometrica riesce */
    fun onBiometricSuccess() {
        viewModelScope.launch {
            profileRepository.recordAccess()
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    /** Chiamata quando l'autenticazione biometrica fallisce */
    fun onBiometricError(message: String) {
        _uiState.update { it.copy(authError = message) }
    }

    /** Pulisci l'errore di autenticazione */
    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

    // ── Setup Wizard ──

    /** Imposta la password della sezione protetta (Step 0) */
    fun setPassword(password: String, confirmPassword: String) {
        if (password.length < 4) {
            _uiState.update { it.copy(passwordError = context.getString(R.string.error_password_short)) }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(passwordError = context.getString(R.string.error_password_mismatch)) }
            return
        }
        viewModelScope.launch {
            passwordManager.setPassword(password)
            _uiState.update {
                it.copy(setupStep = 1, passwordError = null)
            }
        }
    }

    /** Imposta il codice paziente (Step 1) */
    fun setPatientCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            try {
                profileRepository.updatePatientCode(code)
                _uiState.update { it.copy(setupStep = 2, authError = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(authError = context.getString(R.string.error_save_code, e.message)) }
            }
        }
    }

    /** Completa il setup iniziale (Step 2) */
    fun completeSetup() {
        viewModelScope.launch {
            try {
                userPreferences.setFirstSetupCompleted()
                profileRepository.setAppInitialized()
                _uiState.update { it.copy(setupCompleted = true, authError = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(authError = context.getString(R.string.error_complete_setup, e.message)) }
            }
        }
    }

    /** Torna allo step precedente del setup */
    fun previousSetupStep() {
        _uiState.update {
            it.copy(setupStep = maxOf(0, it.setupStep - 1))
        }
    }
}
