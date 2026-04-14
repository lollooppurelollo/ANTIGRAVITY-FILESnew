// =============================================================
// AFA - Attività Fisica Adattata
// Activity principale (Single Activity pattern)
// =============================================================
package com.afa.fitadapt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.afa.fitadapt.security.BiometricHelper
import com.afa.fitadapt.ui.navigation.AfaNavGraph
import com.afa.fitadapt.ui.theme.AfaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Unica Activity dell'app — tutto il resto è gestito da Jetpack Compose.
 *
 * Questa activity:
 * 1. Abilita la visualizzazione edge-to-edge (contenuto sotto status bar)
 * 2. Imposta il tema Material 3 AFA
 * 3. Lancia il navigation graph Compose
 *
 * @AndroidEntryPoint permette a Hilt di iniettare dipendenze nei ViewModel
 * usati all'interno delle schermate Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * BiometricHelper viene iniettato da Hilt.
     * Lo passiamo al NavGraph perché la BiometricPrompt
     * ha bisogno della FragmentActivity come contesto.
     */
    @Inject
    lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abilita edge-to-edge per un'interfaccia moderna
        enableEdgeToEdge()

        setContent {
            AfaTheme {
                AfaNavGraph(biometricHelper = biometricHelper)
            }
        }
    }
}
