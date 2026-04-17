// =============================================================
// AFA - Attività Fisica Adattata
// Gestione cifratura passphrase del database
// =============================================================
package com.afa.fitadapt.security

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore dedicato ai metadati crittografici del database.
 * File separato dagli altri DataStore per chiarezza.
 *
 * DOVE VENGONO SALVATI I DATI:
 * - Passphrase cifrata del database → qui (come stringa Base64)
 * - IV per la cifratura → qui (come stringa Base64)
 * - Flag di esistenza → qui
 *
 * La passphrase è cifrata con AES-256-GCM usando una chiave
 * nel Keystore Android (gestita da KeystoreManager).
 */
private val Context.cryptoDataStore by preferencesDataStore(name = "afa_crypto_prefs")

/**
 * Gestisce la passphrase del database SQLCipher.
 *
 * COME FUNZIONA:
 * 1. Al primo avvio: genera una passphrase casuale di 32 byte
 * 2. Cifra la passphrase con AES-256-GCM (chiave nel Keystore)
 * 3. Salva la passphrase cifrata + IV nel DataStore
 * 4. Ad ogni apertura del DB: legge, decifra, passa a SQLCipher
 *
 * La passphrase in chiaro non viene MAI salvata su disco.
 * Esiste solo in memoria per il tempo necessario ad aprire il database.
 */
@Singleton
class CryptoManager @Inject constructor(
    private val keystoreManager: KeystoreManager,
    @ApplicationContext private val context: Context
) {

    companion object {
        // Chiavi per il DataStore crittografico
        private val ENCRYPTED_PASSPHRASE_KEY = stringPreferencesKey("encrypted_db_passphrase")
        private val PASSPHRASE_IV_KEY = stringPreferencesKey("db_passphrase_iv")
        private val PASSPHRASE_EXISTS_KEY = booleanPreferencesKey("db_passphrase_exists")

        // Lunghezza della passphrase in byte (32 byte = 256 bit)
        private const val PASSPHRASE_LENGTH = 32
    }

    /**
     * Ottiene o crea la passphrase per il database SQLCipher.
     *
     * Questa funzione usa runBlocking perché viene chiamata durante
     * l'inizializzazione del database (nel modulo Hilt), dove serve
     * un risultato sincrono. Succede una sola volta all'avvio dell'app.
     *
     * USIAMO Dispatchers.IO per evitare deadlock con il Main Thread
     * quando si accede a DataStore durante l'iniezione Hilt.
     *
     * @return la passphrase come CharArray per SQLCipher
     */
    fun getOrCreateDatabasePassphrase(): CharArray {
        return runBlocking(Dispatchers.IO) {
            val preferences = context.cryptoDataStore.data.first()
            val exists = preferences[PASSPHRASE_EXISTS_KEY] ?: false

            if (exists) {
                // La passphrase esiste già: leggi, decifra e restituisci
                retrieveExistingPassphrase(preferences)
            } else {
                // Prima volta: genera, cifra, salva e restituisci
                createAndStoreNewPassphrase()
            }
        }
    }

    /**
     * Legge e decifra la passphrase esistente dal DataStore.
     */
    private fun retrieveExistingPassphrase(
        preferences: androidx.datastore.preferences.core.Preferences
    ): CharArray {
        // Leggi la passphrase cifrata e l'IV dal DataStore
        val encryptedBase64 = preferences[ENCRYPTED_PASSPHRASE_KEY]
            ?: throw IllegalStateException("Passphrase cifrata non trovata nel DataStore")
        val ivBase64 = preferences[PASSPHRASE_IV_KEY]
            ?: throw IllegalStateException("IV della passphrase non trovato nel DataStore")

        // Decodifica da Base64
        val encryptedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)
        val ivBytes = Base64.decode(ivBase64, Base64.NO_WRAP)

        // Decifra con il Keystore
        val decryptedBytes = keystoreManager.decrypt(encryptedBytes, ivBytes)

        // Converti in CharArray per SQLCipher
        val passphrase = String(decryptedBytes, Charsets.UTF_8).toCharArray()

        // Pulisci i byte decifrati dalla memoria
        decryptedBytes.fill(0)

        return passphrase
    }

    /**
     * Genera una nuova passphrase casuale, la cifra e la salva.
     */
    private suspend fun createAndStoreNewPassphrase(): CharArray {
        // Genera byte casuali con SecureRandom (crittograficamente sicuro)
        val randomBytes = ByteArray(PASSPHRASE_LENGTH)
        SecureRandom().nextBytes(randomBytes)

        // Converti in stringa Base64 (più sicura come passphrase)
        val passphraseString = Base64.encodeToString(randomBytes, Base64.NO_WRAP)

        // Cifra la passphrase con il Keystore
        val (encryptedBytes, iv) = keystoreManager.encrypt(
            passphraseString.toByteArray(Charsets.UTF_8)
        )

        // Salva la passphrase cifrata e l'IV nel DataStore
        context.cryptoDataStore.edit { prefs ->
            prefs[ENCRYPTED_PASSPHRASE_KEY] = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            prefs[PASSPHRASE_IV_KEY] = Base64.encodeToString(iv, Base64.NO_WRAP)
            prefs[PASSPHRASE_EXISTS_KEY] = true
        }

        // Pulisci i byte random dalla memoria
        randomBytes.fill(0)

        return passphraseString.toCharArray()
    }
}
