package com.kinapto.fitadapt.util

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.kinapto.fitadapt.security.KeystoreManager

class CrfCryptoUtilsTest {

    private lateinit var cryptoUtils: CrfCryptoUtils
    private val keystoreManager: KeystoreManager = mockk()
    private val testData = "clinical-trial-data-2025"

    @Before
    fun setup() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } answers { 
            java.util.Base64.getEncoder().encodeToString(it.invocation.args[0] as ByteArray) 
        }
        every { Base64.decode(any<String>(), any()) } answers { 
            java.util.Base64.getDecoder().decode(it.invocation.args[0] as String) 
        }

        cryptoUtils = CrfCryptoUtils(keystoreManager)
    }

    @Test
    fun `compression roundtrip should preserve data`() {
        val compressed = cryptoUtils.compress(testData)
        val decompressed = cryptoUtils.decompress(compressed)
        assertEquals(testData, decompressed)
    }

    @Test
    fun `encryption roundtrip should preserve data`() {
        // GIVEN
        val iv = ByteArray(12) { 0x01.toByte() }
        val encryptedData = "encrypted".toByteArray()
        val plainData = testData.toByteArray()

        every { keystoreManager.encrypt(any()) } returns Pair(encryptedData, iv)
        every { keystoreManager.decrypt(any(), any()) } returns plainData

        // WHEN
        val encryptedPackage = cryptoUtils.encrypt(plainData)
        val decryptedData = cryptoUtils.decrypt(encryptedPackage)

        // THEN
        assertEquals(testData, String(decryptedData))
    }

    @Test
    fun `checksum should be consistent`() {
        val hash1 = cryptoUtils.checksum(testData)
        val hash2 = cryptoUtils.checksum(testData)
        assertEquals(hash1, hash2)
        assertEquals(8, hash1.length)
    }
}
