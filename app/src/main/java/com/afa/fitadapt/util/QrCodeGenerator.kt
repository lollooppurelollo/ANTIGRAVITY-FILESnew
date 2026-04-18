// =============================================================
// AFA - Attività Fisica Adattata
// Utility: Generazione QR Code
// =============================================================
package com.afa.fitadapt.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Genera QR code a partire da stringhe di testo.
 *
 * COME FUNZIONA:
 * 1. Comprime il testo con GZIP per ridurre la dimensione
 * 2. Codifica in Base64 per garantire la compatibilità con tutti i lettori (ASCII-safe)
 * 3. Se il payload risultante entra in un QR code (≤ 2953 byte), genera il QR
 * 4. Se è troppo grande, restituisce null → il chiamante usa il fallback file
 */
object QrCodeGenerator {

    // Dimensione del QR code in pixel
    private const val QR_SIZE = 800

    // Limite massimo per il payload di un QR code binario (Version 40, L)
    private const val MAX_QR_BYTES = 2953

    /**
     * Genera un QR code da una stringa di testo (JSON).
     * Flusso: Content -> GZIP -> Base64 -> QR
     *
     * @param content il testo da codificare
     * @param size dimensione del QR in pixel
     * @return Bitmap con il QR code, oppure null se troppo grande
     */
    fun generateQrCode(content: String, size: Int = QR_SIZE): Bitmap? {
        return try {
            // 1. Comprime con GZIP
            val compressed = gzipCompress(content)

            // 2. Codifica in Base64 (ASCII safe per i lettori QR standard)
            // L'overhead del Base64 è ~33%, ma garantisce che il QR sia leggibile
            // dalle app fotocamera standard senza corruzione di byte binari.
            val base64Encoded = Base64.encodeToString(compressed, Base64.NO_WRAP)

            if (base64Encoded.length > MAX_QR_BYTES) {
                return null
            }

            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
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
     * @return Bitmap con il QR code
     */
    @Suppress("unused") // Usata per QR code semplici (URI brevi)
    fun generateSimpleQrCode(content: String, size: Int = QR_SIZE): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
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
