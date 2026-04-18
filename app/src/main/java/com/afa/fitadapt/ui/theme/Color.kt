// =============================================================
// AFA - Attività Fisica Adattata
// Palette colori Material 3
// =============================================================
package com.afa.fitadapt.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
// PALETTE PRINCIPALE FITLY
// Palette medicale-fitness moderna: Azzurro pastello vibrante, Navy Blue, Bianchi puliti
// ══════════════════════════════════════════════════════════

// Blu Navy — testi e icone principali, trasmette professionalità
val NavyBlue = Color(0xFF1B3A5C)
val NavyBlueDark = Color(0xFF0F1E33)

// Fitly Blue (Azzurro Pastello Moderno) — Il cuore dell'identità visiva
// Un azzurro bilanciato tra il sanitario e il fitness tech
val FitlyBlue = Color(0xFF7CB9E8)      // Azzurro pastello più deciso
val FitlyBlueLight = Color(0xFFE3F2FD) // Versione molto chiara per sfondi
val FitlyBlueDark = Color(0xFF4682B4)  // Versione più scura per accenti

// Bianchi e superfici pulite
val WarmWhite = Color(0xFFFAFCFE)
val IceWhite = Color(0xFFF4F9FF)       // Sfondo leggermente azzurrato
val SurfaceVariant = Color(0xFFEBF2F7)

// Colori funzionali (mantenuti ma armonizzati)
val SageGreen = Color(0xFF52B788)       // Successo moderno
val SageGreenLight = Color(0xFFD8F3DC)
val SoftAmber = Color(0xFFFFB347)       // Ambra pastello
val SoftAmberLight = Color(0xFFFFF3E0)
val SoftRose = Color(0xFFE57373)        // Rosso pastello
val SoftRoseLight = Color(0xFFFFEBEE)

// Testi
val TextPrimary = Color(0xFF2C3E50)
val TextSecondary = Color(0xFF5D6D7E)
val TextTertiary = Color(0xFF85929E)

// ══════════════════════════════════════════════════════════
// SCHEMA COLORI MATERIAL 3 (tema chiaro)
// ══════════════════════════════════════════════════════════

val md_light_primary = FitlyBlueDark
val md_light_onPrimary = Color.White
val md_light_primaryContainer = FitlyBlueLight
val md_light_onPrimaryContainer = NavyBlue

val md_light_secondary = FitlyBlue
val md_light_onSecondary = Color.White
val md_light_secondaryContainer = Color.White
val md_light_onSecondaryContainer = FitlyBlueDark

val md_light_tertiary = SageGreen
val md_light_onTertiary = Color.White
val md_light_tertiaryContainer = SageGreenLight
val md_light_onTertiaryContainer = Color(0xFF0A3D1F)

val md_light_error = SoftRose
val md_light_onError = Color.White
val md_light_errorContainer = SoftRoseLight
val md_light_onErrorContainer = Color(0xFF410002)

val md_light_background = IceWhite
val md_light_onBackground = TextPrimary
val md_light_surface = WarmWhite
val md_light_onSurface = TextPrimary
val md_light_surfaceVariant = SurfaceVariant
val md_light_onSurfaceVariant = TextSecondary
val md_light_outline = TextTertiary
val md_light_outlineVariant = Color(0xFFC4C7CF)
