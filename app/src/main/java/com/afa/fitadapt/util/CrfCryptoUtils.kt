package com.afa.fitadapt.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Utility per la protezione dei dati CRF:
 * - Compressione GZIP
 * - Cifratura AES (con chiave derivata dal patient_study_code o fissa per il progetto)
 * - Base64 URL-safe
 * - Checksum SHA-256
 */
object CrfCryptoUtils {

    // Nota: In un ambiente reale, la chiave dovrebbe essere gestita via Keystore
    // Per questa implementazione usiamo una chiave derivata o fissa per semplicità di demo/scambio tra app.
    private const val AES_KEY = "KinApto_Secure_CRF_Key_2024_AFA" 

    fun compress(data: String): ByteArray {
        val bos = ByteArrayOutputStream(data.length)
        GZIPOutputStream(bos).use { it.write(data.toByteArray(Charsets.UTF_8)) }
        return bos.toByteArray()
    }

    fun decompress(compressed: ByteArray): String {
        val bis = ByteArrayInputStream(compressed)
        GZIPInputStream(bis).use { it.bufferedReader(Charsets.UTF_8).use { reader -> return reader.readText() } }
    }

    fun encrypt(data: ByteArray): ByteArray {
        val sha = MessageDigest.getInstance("SHA-256")
        val key = sha.digest(AES_KEY.toByteArray(Charsets.UTF_8)).copyOf(16) // 128 bit
        val secretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

    fun decrypt(encrypted: ByteArray): ByteArray {
        val sha = MessageDigest.getInstance("SHA-256")
        val key = sha.digest(AES_KEY.toByteArray(Charsets.UTF_8)).copyOf(16)
        val secretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(encrypted)
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
