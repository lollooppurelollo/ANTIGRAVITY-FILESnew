// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Blocco biometrico
// =============================================================
package com.afa.fitadapt.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.afa.fitadapt.security.BiometricHelper
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue

/**
 * Schermata di blocco biometrico.
 * Chiede l'autenticazione biometrica all'avvio dell'app.
 * Dopo successo, naviga alla Home.
 */
@Composable
fun BiometricLockScreen(
    authViewModel: AuthViewModel,
    biometricHelper: BiometricHelper,
    onAuthenticated: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val snackbarHostState = remember { SnackbarHostState() }

    // Naviga quando autenticato
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onAuthenticated()
    }

    // Mostra errori
    LaunchedEffect(uiState.authError) {
        uiState.authError?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearAuthError()
        }
    }

    // Lancia il prompt biometrico automaticamente
    LaunchedEffect(Unit) {
        if (activity != null && biometricHelper.canAuthenticate()) {
            biometricHelper.showBiometricPrompt(
                activity = activity,
                onSuccess = { authViewModel.onBiometricSuccess() },
                onError = { msg -> authViewModel.onBiometricError(msg) }
            )
        }
    }

    // ── UI ──
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyBlue, CelestialBlue)
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icona impronta
            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = "Autenticazione biometrica",
                modifier = Modifier.size(80.dp),
                tint = PastelBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Benvenuta",
                style = MaterialTheme.typography.displaySmall,
                color = PastelBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Autenticati per accedere\nal tuo percorso",
                style = MaterialTheme.typography.bodyLarge,
                color = PastelBlue.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pulsante per ritentare l'autenticazione
            Button(
                onClick = {
                    if (activity != null && biometricHelper.canAuthenticate()) {
                        biometricHelper.showBiometricPrompt(
                            activity = activity,
                            onSuccess = { authViewModel.onBiometricSuccess() },
                            onError = { msg -> authViewModel.onBiometricError(msg) }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelBlue,
                    contentColor = NavyBlue
                )
            ) {
                Icon(Icons.Outlined.Fingerprint, "Riprova", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("Sblocca l'app")
            }
        }

        // Snackbar per errori
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
