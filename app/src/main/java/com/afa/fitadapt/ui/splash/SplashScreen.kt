// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Splash Screen
// =============================================================
package com.afa.fitadapt.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.ui.auth.AuthViewModel
import com.afa.fitadapt.ui.theme.CelestialBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.PastelBlue
import kotlinx.coroutines.delay

/**
 * Schermata Splash.
 * Mostra il logo e il nome dell'app durante l'inizializzazione.
 * Poi naviga verso il setup wizard o il blocco biometrico.
 */
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigateToSetup: () -> Unit,
    onNavigateToBiometric: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    // Animazione fade-in
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splashAlpha"
    )

    @Suppress("CanBeVal") // visible parte a false intenzionalmente per l'animazione fade-in
    LaunchedEffect(Unit) {
        visible = true
    }

    // Navigazione dopo il caricamento
    LaunchedEffect(uiState.isLoading, uiState.isFirstLaunch) {
        if (!uiState.isLoading) {
            delay(1200) // Mostra lo splash per almeno 1.2 secondi
            if (uiState.isFirstLaunch) {
                onNavigateToSetup()
            } else {
                onNavigateToBiometric()
            }
        }
    }

    // ── UI ──
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyBlue, CelestialBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo — grande lettera "A" stilizzata
            Text(
                text = "A",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = PastelBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nome app
            Text(
                text = "AFA",
                style = MaterialTheme.typography.displayLarge,
                color = PastelBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sottotitolo
            Text(
                text = "Attività Fisica Adattata",
                style = MaterialTheme.typography.titleMedium,
                color = PastelBlue.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Indicatore di caricamento
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PastelBlue,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
