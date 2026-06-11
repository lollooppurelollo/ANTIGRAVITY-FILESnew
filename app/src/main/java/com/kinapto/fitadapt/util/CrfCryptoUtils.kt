// =============================================================
// KinApto - Attività Fisica Adattata
// Utility per la protezione dei dati CRF
// =============================================================
package com.kinapto.fitadapt.util

import android.util.Base64
import com.kinapto.fitadapt.security.KeystoreManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility per la protezione dei dati CRF:
 * - Compressione GZIP
 * - Cifratura AES-256-GCM (via KeystoreManager)
 * - Base64 URL-safe
 * - Checksum SHA-256
 *
 * SECURITY FIX: Rimosse chiavi hardcoded e ECB mode. 
 * Ora utilizza AES-256-GCM con chiavi hardware-backed nel Keystore Android.
 * KinApto v1.1.
 */
@Singleton
class CrfCryptoUtils @Inject constructor(
    private val keystoreManager: KeystoreManager
) {

    fun compress(data: String): ByteArray {
        val bos = ByteArrayOutputStream(data.length)
        GZIPOutputStream(bos).use { it.write(data.toByteArray(Charsets.UTF_8)) }
        return bos.toByteArray()
    }

    fun decompress(compressed: ByteArray): String {
        val bis = ByteArrayInputStream(compressed)
        GZIPInputStream(bis).use { it.bufferedReader(Charsets.UTF_8).use { reader -> return reader.readText() } }
    }

    /**
     * Cifra i dati usando il KeystoreManager. 
     * Il risultato contiene l'IV seguito dai dati cifrati.
     */
    fun encrypt(data: ByteArray): ByteArray {
        val (encrypted, iv) = keystoreManager.encrypt(data)
        // Combiniamo IV e dati cifrati: [IV_SIZE_BYTE (1)][IV][DATA]
        // GCM IV è tipicamente 12 byte. KeystoreManager usa AES/GCM/NoPadding.
        val result = ByteArray(1 + iv.size + encrypted.size)
        result[0] = iv.size.toByte()
        System.arraycopy(iv, 0, result, 1, iv.size)
        System.arraycopy(encrypted, 0, result, 1 + iv.size, encrypted.size)
        return result
    }

    /**
     * Decifra i dati usando il KeystoreManager.
     * Si aspetta il formato generato da encrypt: [IV_SIZE][IV][DATA]
     */
    fun decrypt(encryptedWithIv: ByteArray): ByteArray {
        val ivSize = encryptedWithIv[0].toInt()
        val iv = ByteArray(ivSize)
        System.arraycopy(encryptedWithIv, 1, iv, 0, ivSize)
        
        val dataSize = encryptedWithIv.size - 1 - ivSize
        val encryptedData = ByteArray(dataSize)
        System.arraycopy(encryptedWithIv, 1 + ivSize, encryptedData, 0, dataSize)
        
        return keystoreManager.decrypt(encryptedData, iv)
    }

    fun encodeBase64(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun decodeBase64(data: String): ByteArray {
        return Base64.decode(data, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun checksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }.take(8) // Accorciato per i QR
    }

    fun checksum(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data)
        return hash.joinToString("") { "%02x".format(it) }.take(8)
    }
}
