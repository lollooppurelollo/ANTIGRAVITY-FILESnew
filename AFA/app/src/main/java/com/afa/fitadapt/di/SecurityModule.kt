// =============================================================
// AFA - Attività Fisica Adattata
// Modulo Hilt: Sicurezza
// =============================================================
package com.afa.fitadapt.di

import android.content.Context
import com.afa.fitadapt.security.BiometricHelper
import com.afa.fitadapt.security.CryptoManager
import com.afa.fitadapt.security.KeystoreManager
import com.afa.fitadapt.security.PasswordManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Hilt per i componenti di sicurezza.
 *
 * Tutti i componenti di sicurezza sono Singleton perché:
 * - KeystoreManager: gestisce una singola chiave nel Keystore
 * - CryptoManager: gestisce una singola passphrase del database
 * - PasswordManager: gestisce una singola password della sezione protetta
 * - BiometricHelper: wrapper stateless per BiometricPrompt
 *
 * NOTA: questi componenti usano @Inject constructor() nelle loro classi,
 * quindi Hilt può crearli automaticamente. Questo modulo esiste per
 * documentare chiaramente le dipendenze di sicurezza e il loro scope.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    // KeystoreManager, CryptoManager, PasswordManager e BiometricHelper
    // hanno tutti @Inject constructor() e @Singleton,
    // quindi Hilt li gestisce automaticamente senza bisogno di @Provides.
    //
    // Questo modulo serve come punto di documentazione centralizzato
    // per il layer di sicurezza. Se in futuro servissero configurazioni
    // specifiche, si aggiungono i @Provides qui.
}
