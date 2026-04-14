// =============================================================
// AFA - Attività Fisica Adattata
// Palette colori Material 3
// =============================================================
package com.afa.fitadapt.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
// PALETTE PRINCIPALE AFA
// Palette medicale rassicurante: azzurro pastello, bianco, blu navy
// ══════════════════════════════════════════════════════════

// Blu Navy — colore primario, trasmette serietà e professionalità
val NavyBlue = Color(0xFF1B3A5C)
val NavyBlueDark = Color(0xFF0F1E33)
val NavyBlueLight = Color(0xFF2C5A8C)

// Azzurro Pastello — contenitore primario, calmo e rassicurante
val PastelBlue = Color(0xFFDCEEFB)
val PastelBlueLight = Color(0xFFE8F4FD)

// Celeste — colore secondario, vivace ma delicato
val CelestialBlue = Color(0xFF4A90D9)
val CelestialBlueSoft = Color(0xFF7BB3E8)

// Bianchi e superfici
val WarmWhite = Color(0xFFFAFCFE)
val IceWhite = Color(0xFFF0F5FA)
val SurfaceVariant = Color(0xFFE4E8ED)

// Colori funzionali
val SageGreen = Color(0xFF4CAF7D)       // Successo, streak, completamento
val SageGreenLight = Color(0xFFE8F5EE)  // Sfondo verde chiaro
val SoftAmber = Color(0xFFF5A623)       // Attenzione, obiettivi
val SoftAmberLight = Color(0xFFFFF4E0)  // Sfondo ambra chiaro
val SoftRose = Color(0xFFD9534F)        // Errore
val SoftRoseLight = Color(0xFFFDE8E8)   // Sfondo rosa chiaro

// Testi
val TextPrimary = Color(0xFF1A1C1E)
val TextSecondary = Color(0xFF44474E)
val TextTertiary = Color(0xFF74777F)
val TextOnDark = Color(0xFFFFFFFF)

// ══════════════════════════════════════════════════════════
// SCHEMA COLORI MATERIAL 3 (tema chiaro)
// ══════════════════════════════════════════════════════════

val md_light_primary = NavyBlue
val md_light_onPrimary = Color.White
val md_light_primaryContainer = PastelBlue
val md_light_onPrimaryContainer = NavyBlueDark

val md_light_secondary = CelestialBlue
val md_light_onSecondary = Color.White
val md_light_secondaryContainer = PastelBlueLight
val md_light_onSecondaryContainer = NavyBlue

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
