// =============================================================
// AFA - Attività Fisica Adattata
// Tema Material 3 Compose
// =============================================================
package com.afa.fitadapt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Crea uno schema colori Material 3 dinamico basato sul tema selezionato.
 */
@Composable
private fun dynamicAfaLightColorScheme(theme: ThemeOption) = lightColorScheme(
    primary = theme.primaryColor,
    onPrimary = Color.White,
    primaryContainer = theme.lightColor,
    onPrimaryContainer = theme.primaryColor,
    secondary = theme.secondaryColor,
    onSecondary = Color.White,
    secondaryContainer = theme.secondaryColor.copy(alpha = 0.12f),
    onSecondaryContainer = theme.secondaryColor,
    tertiary = theme.tertiaryColor,
    onTertiary = Color.White,
    tertiaryContainer = theme.tertiaryColor.copy(alpha = 0.12f),
    onTertiaryContainer = theme.tertiaryColor,
    error = SoftRose,
    onError = Color.White,
    errorContainer = SoftRoseLight,
    onErrorContainer = SoftRose,
    background = PremiumBackground,
    onBackground = Slate900,
    surface = PremiumCard,
    onSurface = Slate900,
    surfaceVariant = PremiumSurface,
    onSurfaceVariant = Slate500,
    outline = Slate400,
    outlineVariant = Color(0xFFE2E8F0),
)

/**
 * Tema principale dell'app Fitly.
 * Combina colori, tipografia e forme in un unico tema Material 3.
 */
@Composable
fun FitlyTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    MaterialTheme(
        colorScheme = dynamicAfaLightColorScheme(currentTheme),
        typography = AfaTypography,
        shapes = AfaShapes,
        content = content
    )
}
