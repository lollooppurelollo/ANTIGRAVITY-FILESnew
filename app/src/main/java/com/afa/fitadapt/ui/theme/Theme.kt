// =============================================================
// AFA - Attività Fisica Adattata
// Tema Material 3 Compose
// =============================================================
package com.afa.fitadapt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Schema colori Material 3 chiaro per l'app AFA.
 */
private val AfaLightColorScheme = lightColorScheme(
    primary = md_light_primary,
    onPrimary = md_light_onPrimary,
    primaryContainer = md_light_primaryContainer,
    onPrimaryContainer = md_light_onPrimaryContainer,
    secondary = md_light_secondary,
    onSecondary = md_light_onSecondary,
    secondaryContainer = md_light_secondaryContainer,
    onSecondaryContainer = md_light_onSecondaryContainer,
    tertiary = md_light_tertiary,
    onTertiary = md_light_onTertiary,
    tertiaryContainer = md_light_tertiaryContainer,
    onTertiaryContainer = md_light_onTertiaryContainer,
    error = md_light_error,
    onError = md_light_onError,
    errorContainer = md_light_errorContainer,
    onErrorContainer = md_light_onErrorContainer,
    background = md_light_background,
    onBackground = md_light_onBackground,
    surface = md_light_surface,
    onSurface = md_light_onSurface,
    surfaceVariant = md_light_surfaceVariant,
    onSurfaceVariant = md_light_onSurfaceVariant,
    outline = md_light_outline,
    outlineVariant = md_light_outlineVariant,
)

/**
 * Tema principale dell'app Fitly.
 * Combina colori, tipografia e forme in un unico tema Material 3.
 *
 * Uso:
 * ```
 * FitlyTheme {
 *     // Il tuo contenuto Compose qui
 * }
 * ```
 */
@Composable
fun FitlyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AfaLightColorScheme,
        typography = AfaTypography,
        shapes = AfaShapes,
        content = content
    )
}
