// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Splash Screen
// =============================================================
package com.afa.fitadapt.ui.splash

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.ui.auth.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Schermata Splash KinApto.
 * Mostra il brand e lo slogan nella parte centro-alta della schermata.
 */
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigateToSetup: () -> Unit,
    onNavigateToBiometric: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    // Navigazione dopo il caricamento
    LaunchedEffect(uiState.isLoading, uiState.isFirstLaunch) {
        if (!uiState.isLoading) {
            delay(1500) 
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
                Brush.linearGradient(
                    0.0f to Color(0xFF0077FF), // Blu KinApto profondo
                    1.0f to Color(0xFF00C853)  // Verde KinApto brillante
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Decorazione: Onde stilizzate di sfondo per dare dinamismo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Onda grande in basso
            val path1 = Path().apply {
                moveTo(0f, height * 0.75f)
                cubicTo(
                    width * 0.4f, height * 0.7f,
                    width * 0.7f, height * 0.9f,
                    width, height * 0.8f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path1, Color.White.copy(alpha = 0.1f))

            // Onda sottile
            val path2 = Path().apply {
                moveTo(0f, height * 0.82f)
                quadraticTo(width * 0.5f, height * 0.75f, width, height * 0.88f)
            }
            drawPath(
                path = path2,
                color = Color.White.copy(alpha = 0.15f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 160.dp), // Posiziona le scritte nella parte centro-alta
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

            // Spostiamo il caricamento più in basso
            Spacer(modifier = Modifier.height(180.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = Color.White.copy(alpha = 0.8f),
                strokeWidth = 4.dp
            )
        }
    }
}
