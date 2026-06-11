// =============================================================
// KinApto - Attività Fisica Adattata
// Utility: Generazione QR Code
// =============================================================
package com.kinapto.fitadapt.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.kinapto.fitadapt.model.KinAptoCrfChunk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genera QR code a partire da stringhe di testo.
 * 
 * SECURITY FIX: Utilizza AES-256-GCM (via CrfCryptoUtils) per cifrare i dati prima del QR.
 * KinApto v1.1.
 */
@Singleton
class QrCodeGenerator @Inject constructor(
    private val crfCryptoUtils: CrfCryptoUtils
) {

    // Dimensione del QR code in pixel
    private val QR_SIZE = 800

    // Limite massimo per il payload di un QR code binario (Version 40, L)
    private val MAX_QR_BYTES = 2953
    
    // Limite conservativo per i chunk QR (Version 40, M)
    val CHUNK_MAX_BYTES = 1800

    /**
     * Genera una lista di QR code chunked per payload di grandi dimensioni.
     */
    fun generateChunks(
        content: String, 
        exportId: String, 
        patientCode: String
    ): List<KinAptoCrfChunk> {
        // 1. Comprimere, cifrare e codificare il payload completo
        val compressed = crfCryptoUtils.compress(content)
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

        return chunks
    }

    /**
     * Genera un QR code da una stringa di testo (JSON).
     */
    fun generateQrCode(content: String, size: Int = QR_SIZE): Bitmap? {
        return try {
            val compressed = crfCryptoUtils.compress(content)
            val encrypted = crfCryptoUtils.encrypt(compressed)
            val base64Encoded = crfCryptoUtils.encodeBase64(encrypted)

            if (base64Encoded.length > MAX_QR_BYTES) {
                return null
            }

            generateSimpleQrCode(base64Encoded, size, ErrorCorrectionLevel.M)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Genera un QR code senza compressione/cifratura (per contenuti già processati).
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
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y,
                    if (matrix.get(x, y)) Color.BLACK else Color.WHITE
                )
            }
        }

        return bitmap
    }
}
