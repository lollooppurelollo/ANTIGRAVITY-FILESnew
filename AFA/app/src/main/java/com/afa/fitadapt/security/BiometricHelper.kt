// =============================================================
// AFA - Attività Fisica Adattata
// Helper per autenticazione biometrica
// =============================================================
package com.afa.fitadapt.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper per la BiometricPrompt di Android.
 *
 * Gestisce l'autenticazione biometrica (impronta digitale, riconoscimento
 * facciale) con fallback al PIN/pattern/password del dispositivo.
 *
 * COME FUNZIONA:
 * 1. Controlla se il dispositivo supporta la biometria
 * 2. Mostra il prompt di autenticazione
 * 3. Chiama onSuccess o onError in base al risultato
 *
 * Se il dispositivo non supporta la biometria, il fallback
 * al PIN/pattern del dispositivo è automatico grazie a DEVICE_CREDENTIAL.
 */
@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Combinazione di autenticatori supportati:
     * - BIOMETRIC_STRONG = impronta/volto (livello di sicurezza alto)
     * - DEVICE_CREDENTIAL = PIN/pattern/password del dispositivo (fallback)
     */
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL

    /**
     * Verifica se il dispositivo supporta almeno un metodo di autenticazione.
     *
     * @return true se biometria o credenziali dispositivo sono disponibili
     */
    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val result = biometricManager.canAuthenticate(authenticators)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Mostra il prompt di autenticazione biometrica.
     *
     * @param activity la FragmentActivity che ospita il prompt
     * @param onSuccess callback chiamata quando l'autenticazione riesce
     * @param onError callback chiamata in caso di errore, con il messaggio
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Usa il main executor per i callback
        val executor = ContextCompat.getMainExecutor(context)

        // Callback per i risultati dell'autenticazione
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                // Errore 13 = utente ha premuto "Annulla" — non è un vero errore
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_USER_CANCELED
                ) {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Tentativo fallito (es. impronta non riconosciuta)
                // Il sistema mostra automaticamente il messaggio di errore
                // e permette di riprovare — non serve fare nulla qui
            }
        }

        // Configura il prompt
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Accesso AFA")
            .setSubtitle("Autenticati per accedere all'app")
            .setDescription("Usa la biometria o le credenziali del dispositivo")
            .setAllowedAuthenticators(authenticators)
            // Nota: non serve setNegativeButtonText() quando si usa DEVICE_CREDENTIAL
            .build()

        // Crea e mostra il prompt
        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }
}
