// =============================================================
// AFA - Attività Fisica Adattata
// Activity principale (Single Activity pattern)
// =============================================================
package com.afa.fitadapt

import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.afa.fitadapt.security.BiometricHelper
import com.afa.fitadapt.ui.navigation.AfaNavGraph
import com.afa.fitadapt.ui.theme.FitlyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier

/**
 * Unica Activity dell'app — tutto il resto è gestito da Jetpack Compose.
 *
 * Questa activity:
 * 1. Abilita la visualizzazione edge-to-edge (contenuto sotto status bar)
 * 2. Imposta il tema Material 3 Fitly
 * 3. Lancia il navigation graph Compose
 *
 * @AndroidEntryPoint permette a Hilt di iniettare dipendenze nei ViewModel
 * usati all'interno delle schermate Compose.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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
            FitlyTheme {
                AfaNavGraph(biometricHelper = biometricHelper)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FitlyPreview() {
    FitlyTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fitly - Fit your life 🚀",
                fontSize = 24.sp
            )
        }
    }
}
