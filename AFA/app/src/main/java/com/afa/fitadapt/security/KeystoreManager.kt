// =============================================================
// AFA - Attività Fisica Adattata
// Gestione chiavi nel Keystore Android
// =============================================================
package com.afa.fitadapt.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestisce le chiavi crittografiche nel Keystore Android.
 *
 * Il Keystore Android è un sistema hardware-backed (su dispositivi compatibili)
 * che protegge le chiavi crittografiche. Le chiavi non possono essere estratte
 * dal dispositivo — possono solo essere usate per cifrare/decifrare.
 *
 * Questa classe gestisce una chiave AES-256-GCM usata per proteggere
 * la passphrase del database SQLCipher.
 *
 * COME FUNZIONA:
 * 1. Al primo utilizzo, genera una chiave AES-256 nel Keystore
 * 2. Per cifrare: usa la chiave per cifrare dati con AES-GCM
 * 3. Per decifrare: usa la stessa chiave per decifrare
 * 4. La chiave non lascia mai il Keystore
 */
@Singleton
class KeystoreManager @Inject constructor() {

    companion object {
        // Nome univoco della chiave nel Keystore
        private const val KEYSTORE_ALIAS = "afa_database_key"

        // Provider del Keystore Android
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"

        // Algoritmo di cifratura: AES in modalità GCM senza padding
        private const val TRANSFORMATION = "AES/GCM/NoPadding"

        // Dimensione del tag di autenticazione GCM (128 bit)
        private const val GCM_TAG_LENGTH = 128
    }

    // Carica il Keystore Android
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Ottiene la chiave esistente dal Keystore, oppure ne crea una nuova.
     * La chiave è AES-256 e viene usata per cifrare la passphrase del database.
     */
    fun getOrCreateKey(): SecretKey {
        // Prova a recuperare la chiave esistente
        val existingEntry = keyStore.getEntry(KEYSTORE_ALIAS, null)
        if (existingEntry != null && existingEntry is KeyStore.SecretKeyEntry) {
            return existingEntry.secretKey
        }

        // Se non esiste, crea una nuova chiave AES-256
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val spec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            // Non richiediamo autenticazione utente per la chiave,
            // perché l'autenticazione è gestita dalla BiometricPrompt a livello app
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Cifra un array di byte usando AES-256-GCM.
     *
     * @param data i dati da cifrare
     * @return Pair(datiCifrati, iv) — servono entrambi per decifrare
     */
    fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encryptedData = cipher.doFinal(data)
        // L'IV (Initialization Vector) è generato automaticamente da GCM
        // e serve per la decifratura
        val iv = cipher.iv
        return Pair(encryptedData, iv)
    }

    /**
     * Decifra un array di byte usando AES-256-GCM.
     *
     * @param encryptedData i dati cifrati
     * @param iv l'IV usato durante la cifratura
     * @return i dati originali decifrati
     */
    fun decrypt(encryptedData: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        return cipher.doFinal(encryptedData)
    }
}
