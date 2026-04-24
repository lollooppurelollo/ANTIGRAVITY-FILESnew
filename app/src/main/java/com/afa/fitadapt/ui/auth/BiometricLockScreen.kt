// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Blocco biometrico
// =============================================================
package com.afa.fitadapt.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.afa.fitadapt.security.BiometricHelper

/**
 * Schermata di blocco biometrico KinApto.
 * Posiziona il brand nella parte centro-alta per coerenza con lo splash.
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    0.0f to Color(0xFF0077FF),
                    1.0f to Color(0xFF00C853)
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Onde di sfondo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val path1 = Path().apply {
                moveTo(0f, height * 0.75f)
                cubicTo(width * 0.4f, height * 0.7f, width * 0.7f, height * 0.9f, width, height * 0.8f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path1, Color.White.copy(alpha = 0.08f))
        }

        Column(
            modifier = Modifier
                .padding(top = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Nome app
            Text(
                text = "KinApto",
                style = MaterialTheme.typography.displayLarge.copy(
                    letterSpacing = (-2).sp,
                    fontWeight = FontWeight.Black,
                    fontSize = 72.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Slogan
            Text(
                text = "Sempre in movimento",
                style = MaterialTheme.typography.headlineSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(180.dp))

            // Pulsante sblocco stilizzato
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
                    containerColor = Color.White,
                    contentColor = Color(0xFF0077FF)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(56.dp).padding(horizontal = 32.dp)
            ) {
                Icon(Icons.Outlined.Fingerprint, null)
                Spacer(modifier = Modifier.size(12.dp))
                Text("Sblocca per iniziare", fontWeight = FontWeight.Bold)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
