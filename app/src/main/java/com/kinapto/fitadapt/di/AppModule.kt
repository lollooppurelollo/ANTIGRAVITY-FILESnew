// =============================================================
// KinApto - Attività Fisica Adattata
// Modulo Hilt: Dipendenze generali dell'app
// =============================================================
package com.kinapto.fitadapt.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import com.kinapto.fitadapt.security.KeystoreManager
import com.kinapto.fitadapt.util.CrfCryptoUtils
import com.kinapto.fitadapt.util.QrCodeGenerator
import dagger.Provides
import javax.inject.Singleton

/**
 * Modulo Hilt per le dipendenze generali dell'app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideCrfCryptoUtils(keystoreManager: KeystoreManager): CrfCryptoUtils {
        return CrfCryptoUtils(keystoreManager)
    }

    @Provides
    @Singleton
    fun provideQrCodeGenerator(crfCryptoUtils: CrfCryptoUtils): QrCodeGenerator {
        return QrCodeGenerator(crfCryptoUtils)
    }
}
