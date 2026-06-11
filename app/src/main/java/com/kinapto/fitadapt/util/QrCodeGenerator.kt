// =============================================================
// KinApto - Attività Fisica Adattata
// Utility: Generazione QR Code
// =============================================================
package com.kinapto.fitadapt.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import android.util.Base64
import com.kinapto.fitadapt.model.KinAptoCrfChunk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Genera QR code a partire da stringhe di testo.
 *
 * COME FUNZIONA:
 * 1. Comprime il testo con GZIP per ridurre la dimensione
 * 2. Codifica in Base64 per garantire la compatibilità con tutti i lettori (ASCII-safe)
 * 3. Se il payload risultante entra in un QR code (≤ 2953 byte), genera il QR
 * 4. Se è troppo grande, restituisce null → il chiamante usa il fallback file o il chunking
 */
/**
 * Genera QR code a partire da stringhe di testo.
 * 
 * SECURITY FIX: Utilizza AES-256-GCM (via CrfCryptoUtils) per cifrare i dati prima del QR.
 * KinApto v1.1.
 */
@javax.inject.Singleton
class QrCodeGenerator @javax.inject.Inject constructor(
    private val crfCryptoUtils: CrfCryptoUtils
) {

    // Dimensione del QR code in pixel
    private val QR_SIZE = 800

    // Limite massimo per il payload di un QR code binario (Version 40, L)
    // Con crittografia e IV, il limite utile si riduce.
    private val MAX_QR_BYTES = 2953
    
    // Limite conservativo per i chunk QR (Version 40, M)
    private val CHUNK_MAX_BYTES = 1800

    /**
     * Genera una lista di QR code chunked per payload di grandi dimensioni.
     * KinApto v1.1 implementation for Problem 3.
     */
    fun generateChunkedQrCodes(
        content: String, 
        exportId: String, 
        patientCode: String
    ): List<Bitmap> {
        // 1. Comprimere, cifrare e codificare il payload completo
        val compressed = gzipCompress(content)
        val encrypted = crfCryptoUtils.encrypt(compressed)
        val fullBase64 = crfCryptoUtils.encodeBase64(encrypted)
        val globalChecksum = crfCryptoUtils.checksum(fullBase64)

        // 2. Suddivisione in chunk
        val totalChunks = (fullBase64.length + CHUNK_MAX_BYTES - 1) / CHUNK_MAX_BYTES
        val chunks = mutableListOf<KinAptoCrfChunk>()
        
        for (i in 0 until totalChunks) {
            val start = i * CHUNK_MAX_BYTES
            val end = minOf(start + CHUNK_MAX_BYTES, fullBase64.length)
            val part = fullBase64.substring(start, end)
            
            chunks.add(
                KinAptoCrfChunk(
                    export_id = exportId,
                    patient_study_code = patientCode,
                    chunk_index = i + 1,
                    total_chunks = totalChunks,
                    chunk_checksum = crfCryptoUtils.checksum(part),
                    global_checksum = globalChecksum,
                    payload = part
                )
            )
        }

        // 3. Generazione Bitmap
        return chunks.mapNotNull { chunk ->
            val chunkJson = Json.encodeToString(chunk)
            generateSimpleQrCode(chunkJson, QR_SIZE, ErrorCorrectionLevel.M)
        }
    }

    /**
     * Genera un QR code da una stringa di testo (JSON).
     * Flusso: Content -> GZIP -> ENCRYPT -> Base64 -> QR
     *
     * @param content il testo da codificare
     * @param size dimensione del QR in pixel
     * @return Bitmap con il QR code, oppure null se troppo grande
     */
    fun generateQrCode(content: String, size: Int = QR_SIZE): Bitmap? {
        return try {
            // 1. Comprime con GZIP
            val compressed = gzipCompress(content)

            // 2. SECURITY FIX: Cifra con Keystore
            val encrypted = crfCryptoUtils.encrypt(compressed)

            // 3. Codifica in Base64
            val base64Encoded = crfCryptoUtils.encodeBase64(encrypted)

            if (base64Encoded.length > MAX_QR_BYTES) {
                return null
            }

            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M, // Più robusto di L
                EncodeHintType.MARGIN to 2
            )

            val bitMatrix: BitMatrix = writer.encode(
                base64Encoded,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            createBitmapFromMatrix(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Genera un QR code senza compressione (per contenuti brevi come URI).
     *
     * @param content il testo da codificare
     * @param size dimensione del QR in pixel
     * @param errorCorrection livello di correzione errore
     * @return Bitmap con il QR code
     */
    fun generateSimpleQrCode(
        content: String, 
        size: Int = QR_SIZE, 
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M
    ): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to errorCorrection,
                EncodeHintType.MARGIN to 2
            )

            val bitMatrix = writer.encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            createBitmapFromMatrix(bitMatrix)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Converte una BitMatrix in un Bitmap Android.
     */
    private fun createBitmapFromMatrix(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        @Suppress("UseKtx") // Il loop pixel-per-pixel è necessario per la conversione BitMatrix
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Moduli neri su sfondo bianco
                @Suppress("UseKtx") // setPixel usato intenzionalmente nel loop BitMatrix
                bitmap.setPixel(
                    x, y,
                    if (matrix.get(x, y)) Color.BLACK else Color.WHITE
                )
            }
        }

        return bitmap
    }

    /**
     * Comprime una stringa con GZIP.
     */
    private fun gzipCompress(input: String): ByteArray {
        val byteStream = ByteArrayOutputStream()
        GZIPOutputStream(byteStream).use { gzip ->
            gzip.write(input.toByteArray(Charsets.UTF_8))
        }
        return byteStream.toByteArray()
    }
}
