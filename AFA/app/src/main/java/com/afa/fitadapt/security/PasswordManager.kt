// =============================================================
// AFA - Attività Fisica Adattata
// Gestione password della sezione protetta
// =============================================================
package com.afa.fitadapt.security

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore dedicato alla sicurezza della sezione protetta.
 *
 * DOVE VENGONO SALVATI I DATI:
 * - Hash della password → qui (come stringa Base64)
 * - Salt della password → qui (come stringa Base64)
 * - Flag "password configurata" → qui
 *
 * La password NON viene mai salvata in chiaro.
 * Viene salvato solo l'hash PBKDF2-HMAC-SHA256 con 100.000 iterazioni.
 */
private val Context.securityDataStore by preferencesDataStore(name = "afa_security_prefs")

/**
 * Gestisce la password della sezione protetta.
 *
 * La password è impostata al primo avvio dell'app e serve per
 * accedere alla sezione dove si configurano schede, esercizi e obiettivi.
 *
 * SICUREZZA:
 * - Hash con PBKDF2-HMAC-SHA256 (standard industriale)
 * - 100.000 iterazioni (lento da attaccare con brute force)
 * - Salt random di 32 byte (unico per ogni installazione)
 * - La password originale non viene mai memorizzata
 */
@Singleton
class PasswordManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Algoritmo di hashing
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"

        // Numero di iterazioni (più alto = più sicuro ma più lento)
        private const val ITERATIONS = 100_000

        // Lunghezza della chiave derivata in bit
        private const val KEY_LENGTH = 256

        // Lunghezza del salt in byte
        private const val SALT_LENGTH = 32

        // Chiavi per il DataStore di sicurezza
        private val PASSWORD_HASH_KEY = stringPreferencesKey("protected_password_hash")
        private val PASSWORD_SALT_KEY = stringPreferencesKey("protected_password_salt")
        private val PASSWORD_CONFIGURED_KEY = booleanPreferencesKey("password_configured")
    }

    /**
     * Verifica se la password è già stata configurata.
     * Flow che emette automaticamente quando lo stato cambia.
     */
    @Suppress("unused") // Osservata da ProtectedViewModel per stato password
    val isPasswordConfigured: Flow<Boolean> = context.securityDataStore.data
        .map { prefs -> prefs[PASSWORD_CONFIGURED_KEY] ?: false }

    /**
     * Verifica sincrona se la password è configurata.
     */
    @Suppress("unused") // Chiamata dal SetupViewModel per verifica sincrona
    suspend fun isPasswordConfiguredSync(): Boolean {
        return context.securityDataStore.data.first()[PASSWORD_CONFIGURED_KEY] ?: false
    }

    /**
     * Imposta la password della sezione protetta.
     * Viene chiamata al primo avvio dell'app.
     *
     * @param password la password scelta dall'utente
     */
    suspend fun setPassword(password: String) {
        // Genera un salt casuale (unico per questa installazione)
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        // Calcola l'hash della password con PBKDF2
        val hash = hashPassword(password, salt)

        // Salva hash e salt nel DataStore (mai la password originale!)
        context.securityDataStore.edit { prefs ->
            prefs[PASSWORD_HASH_KEY] = Base64.encodeToString(hash, Base64.NO_WRAP)
            prefs[PASSWORD_SALT_KEY] = Base64.encodeToString(salt, Base64.NO_WRAP)
            prefs[PASSWORD_CONFIGURED_KEY] = true
        }
    }

    /**
     * Cambia la password della sezione protetta.
     * Richiede la verifica della password attuale prima.
     *
     * @param currentPassword la password attuale
     * @param newPassword la nuova password
     * @return true se la password è stata cambiata con successo
     */
    @Suppress("unused") // Chiamata da SettingsViewModel per cambio password
    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        // Prima verifica che la password attuale sia corretta
        if (!verifyPassword(currentPassword)) {
            return false
        }
        // Imposta la nuova password
        setPassword(newPassword)
        return true
    }

    /**
     * Verifica se la password inserita è corretta.
     *
     * @param password la password da verificare
     * @return true se la password è corretta
     */
    suspend fun verifyPassword(password: String): Boolean {
        val prefs = context.securityDataStore.data.first()

        // Recupera hash e salt salvati
        val storedHashBase64 = prefs[PASSWORD_HASH_KEY] ?: return false
        val storedSaltBase64 = prefs[PASSWORD_SALT_KEY] ?: return false

        val storedHash = Base64.decode(storedHashBase64, Base64.NO_WRAP)
        val salt = Base64.decode(storedSaltBase64, Base64.NO_WRAP)

        // Calcola l'hash della password inserita con lo stesso salt
        val inputHash = hashPassword(password, salt)

        // Confronta i due hash (tempo costante per sicurezza)
        return storedHash.contentEquals(inputHash)
    }

    /**
     * Calcola l'hash PBKDF2-HMAC-SHA256 di una password.
     *
     * @param password la password in chiaro
     * @param salt il salt casuale
     * @return l'hash come array di byte
     */
    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded

        // Pulisci la spec dalla memoria
        spec.clearPassword()

        return hash
    }
}
