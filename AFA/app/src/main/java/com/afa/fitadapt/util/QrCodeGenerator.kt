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
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Genera QR code a partire da stringhe di testo.
 *
 * COME FUNZIONA:
 * 1. Comprime il testo con GZIP per ridurre la dimensione
 * 2. Se il payload compresso entra in un QR code (≤ 2953 byte), genera il QR
 * 3. Se è troppo grande, restituisce null → il chiamante usa il fallback file
 *
 * Capacità massima QR code (Version 40, Error Correction L):
 * - Binario: 2953 byte
 * - Alfanumerico: 4296 caratteri
 */
object QrCodeGenerator {

    // Dimensione del QR code in pixel
    private const val QR_SIZE = 800

    // Limite massimo per il payload di un QR code binario
    private const val MAX_QR_BYTES = 2953

    /**
     * Genera un QR code da una stringa di testo.
     *
     * @param content il testo da codificare (es. JSON)
     * @param size dimensione del QR in pixel (default 800x800)
     * @return Bitmap con il QR code, oppure null se il contenuto è troppo grande
     */
    fun generateQrCode(content: String, size: Int = QR_SIZE): Bitmap? {
        return try {
            // Prova prima con il testo compresso
            val compressed = gzipCompress(content)

            if (compressed.size > MAX_QR_BYTES) {
                // Troppo grande anche compresso — fallback necessario
                return null
            }

            // Converti i byte compressi in una stringa ISO-8859-1
            // (preserva tutti i byte 0-255 senza alterarli)
            val compressedString = String(compressed, Charsets.ISO_8859_1)

            val writer = QRCodeWriter()
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "ISO-8859-1",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
                EncodeHintType.MARGIN to 2
            )

            val bitMatrix: BitMatrix = writer.encode(
                compressedString,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            createBitmapFromMatrix(bitMatrix)
        } catch (_: WriterException) {
            null
        } catch (_: Exception) {
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
