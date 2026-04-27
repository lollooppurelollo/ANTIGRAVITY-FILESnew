// =============================================================
// AFA - Attività Fisica Adattata
// Preferenze utente con DataStore
// =============================================================
package com.afa.fitadapt.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey

import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore per le impostazioni e preferenze dell'utente.
 *
 * DOVE VENGONO SALVATI I DATI:
 * File: afa_user_settings.preferences_pb (nella cartella interna dell'app)
 *
 * COSA VIENE SALVATO QUI:
 * - Impostazioni notifiche (orario, attivazione, messaggi motivazionali)
 * - Stato dell'app (primo avvio completato, biometria attiva)
 *
 * COSA NON VIENE SALVATO QUI:
 * - Passphrase del database → in afa_crypto_prefs (CryptoManager)
 * - Hash password sezione protetta → in afa_security_prefs (PasswordManager)
 * - Dati paziente → nel database Room cifrato
 */
private val Context.userSettingsDataStore by preferencesDataStore(
    name = "afa_user_settings"
)

/**
 * Classe per leggere e scrivere le preferenze utente.
 * Usa DataStore Preferences (asincrono, basato su Flow, type-safe).
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ══════════════════════════════════════════════════════════
    // CHIAVI PER LE PREFERENZE
    // ══════════════════════════════════════════════════════════

    companion object {
        // ── Notifiche ──
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val MOTIVATIONAL_MESSAGES_ENABLED = booleanPreferencesKey("motivational_enabled")

        // ── Stato app ──
        val FIRST_SETUP_COMPLETED = booleanPreferencesKey("first_setup_completed")
        val BIOMETRICS_ENABLED = booleanPreferencesKey("biometrics_enabled")
        val APP_THEME = intPreferencesKey("app_theme")
        val USE_ORIGINAL_COLORS = booleanPreferencesKey("use_original_colors")
        val CALENDAR_MONTHLY_VIEW = booleanPreferencesKey("calendar_monthly_view")
        val GUIDED_TRAINING_MODE = booleanPreferencesKey("guided_training_mode")

        // ── Valori predefiniti ──
        const val DEFAULT_NOTIFICATION_HOUR = 9
        const val DEFAULT_NOTIFICATION_MINUTE = 0
        const val DEFAULT_THEME_ORDINAL = 6 // Light Blue (indice 6 nell'enum che creerò)
    }

    // ══════════════════════════════════════════════════════════
    // LETTURA (Flow — si aggiornano automaticamente)
    // ══════════════════════════════════════════════════════════

    /** Vista calendario (mensile se true, settimanale se false) */
    val calendarMonthlyView: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[CALENDAR_MONTHLY_VIEW] ?: false }

    /** Tema dell'app selezionato */
    val appTheme: Flow<Int> = context.userSettingsDataStore.data
        .map { it[APP_THEME] ?: DEFAULT_THEME_ORDINAL }

    /** Se le notifiche di reminder allenamento sono attive */
    val notificationEnabled: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[NOTIFICATION_ENABLED] ?: true }

    /** Ora della notifica di reminder (0-23) */
    val notificationHour: Flow<Int> = context.userSettingsDataStore.data
        .map { it[NOTIFICATION_HOUR] ?: DEFAULT_NOTIFICATION_HOUR }

    /** Minuti della notifica di reminder (0-59) */
    val notificationMinute: Flow<Int> = context.userSettingsDataStore.data
        .map { it[NOTIFICATION_MINUTE] ?: DEFAULT_NOTIFICATION_MINUTE }

    /** Se i messaggi motivazionali sono attivi */
    val motivationalMessagesEnabled: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[MOTIVATIONAL_MESSAGES_ENABLED] ?: true }

    /** Se il setup iniziale è stato completato */
    @Suppress("unused") // Letta dall'AuthViewModel per decidere la destinazione iniziale
    val firstSetupCompleted: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[FIRST_SETUP_COMPLETED] ?: false }

    /** Se l'autenticazione biometrica è attiva */
    val biometricsEnabled: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[BIOMETRICS_ENABLED] ?: true }

    /** Se usare i colori originali per icone e riquadri allenamento */
    val useOriginalColors: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[USE_ORIGINAL_COLORS] ?: false }

    /** Se usare la modalità guidata (esercizi espansi in un'unica lista) */
    val guidedTrainingMode: Flow<Boolean> = context.userSettingsDataStore.data
        .map { it[GUIDED_TRAINING_MODE] ?: false }

    // ══════════════════════════════════════════════════════════
    // LETTURA SINCRONA (per controlli all'avvio)
    // ══════════════════════════════════════════════════════════

    /** Verifica sincrona se il setup è completato */
    suspend fun isFirstSetupCompleted(): Boolean {
        return context.userSettingsDataStore.data.first()[FIRST_SETUP_COMPLETED] ?: false
    }

    /** Verifica sincrona se la biometria è attiva */
    @Suppress("unused") // Usata da SettingsViewModel per stato biometria
    suspend fun isBiometricsEnabled(): Boolean {
        return context.userSettingsDataStore.data.first()[BIOMETRICS_ENABLED] ?: true
    }

    /** Verifica sincrona se le notifiche sono attive */
    suspend fun isNotificationEnabled(): Boolean {
        return context.userSettingsDataStore.data.first()[NOTIFICATION_ENABLED] ?: true
    }

    /** Verifica sincrona se i messaggi motivazionali sono attivi */
    suspend fun isMotivationalEnabled(): Boolean {
        return context.userSettingsDataStore.data.first()[MOTIVATIONAL_MESSAGES_ENABLED] ?: true
    }

    // ══════════════════════════════════════════════════════════
    // SCRITTURA
    // ══════════════════════════════════════════════════════════

    /** Attiva o disattiva le notifiche di reminder */
    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[NOTIFICATION_ENABLED] = enabled }
    }

    /** Imposta l'orario della notifica di reminder */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.userSettingsDataStore.edit {
            it[NOTIFICATION_HOUR] = hour
            it[NOTIFICATION_MINUTE] = minute
        }
    }

    /** Attiva o disattiva i messaggi motivazionali */
    suspend fun setMotivationalMessagesEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[MOTIVATIONAL_MESSAGES_ENABLED] = enabled }
    }

    /** Segna il setup iniziale come completato */
    suspend fun setFirstSetupCompleted() {
        context.userSettingsDataStore.edit { it[FIRST_SETUP_COMPLETED] = true }
    }

    /** Attiva o disattiva l'autenticazione biometrica */
    suspend fun setBiometricsEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[BIOMETRICS_ENABLED] = enabled }
    }

    /** Imposta il tema dell'app */
    suspend fun setAppTheme(themeOrdinal: Int) {
        context.userSettingsDataStore.edit { it[APP_THEME] = themeOrdinal }
    }

    /** Imposta se usare i colori originali */
    suspend fun setUseOriginalColors(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[USE_ORIGINAL_COLORS] = enabled }
    }

    /** Imposta la vista calendario predefinita */
    suspend fun setCalendarMonthlyView(isMonthly: Boolean) {
        context.userSettingsDataStore.edit { it[CALENDAR_MONTHLY_VIEW] = isMonthly }
    }

    /** Imposta se usare la modalità di allenamento guidata */
    suspend fun setGuidedTrainingMode(enabled: Boolean) {
        context.userSettingsDataStore.edit { it[GUIDED_TRAINING_MODE] = enabled }
    }
}
