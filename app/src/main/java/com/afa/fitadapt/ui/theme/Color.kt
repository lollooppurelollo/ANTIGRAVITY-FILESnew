// =============================================================
// AFA - Attività Fisica Adattata
// Palette colori Material 3
// =============================================================
package com.afa.fitadapt.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
// PALETTE PRINCIPALE FITLY (Premium Health Edition)
// Background: #F8FAFC (Off-white), Primary: #2563EB (Modern Blue), Secondary: #6EE7B7 (Sage)
// ══════════════════════════════════════════════════════════

// Sfondi e Superfici Premium
val PremiumBackground = Color(0xFFF8FAFC) // Off-white neutrale
val PremiumCard = Color(0xFFFFFFFF)       // Pure White
val PremiumSurface = Color(0xFFF1F5F9)     // Light Slate surface

// Colori Identitari
val PremiumBlue = Color(0xFF2563EB)       // Deep Modern Blue (Primary)
val PremiumBlueLight = Color(0xFFDBEAFE)  // Soft Blue tint
val PremiumSage = Color(0xFF10B981)       // Vibrant Sage Green (Secondary)
val PremiumSageLight = Color(0xFFD1FAE5)  // Soft Sage tint

// Testi (Slate Palette)
val Slate900 = Color(0xFF0F172A)          // Dark Slate (Primary Text)
val Slate700 = Color(0xFF334155)          // Medium Slate
val Slate500 = Color(0xFF64748B)          // Gray-Slate (Secondary Text)
val Slate400 = Color(0xFF94A3B8)          // Light Slate

// Colori di accento (Mantenuti per compatibilità funzionale)
val SoftAmber = Color(0xFFF59E0B)       // Modern Amber
val SoftAmberLight = Color(0xFFFEF3C7)
val SoftRose = Color(0xFFEF4444)        // Modern Red
val SoftRoseLight = Color(0xFFFEE2E2)

// Vecchi nomi mantenuti per non rompere riferimenti immediati ma mappati sui nuovi
val NavyBlue = Slate900
val FitlyBlue = PremiumBlue
val FitlyBlueLight = PremiumBlueLight
val SageGreen = PremiumSage
val SageGreenLight = PremiumSageLight
val WarmWhite = PremiumCard
val IceWhite = PremiumBackground
val TextSecondary = Slate500

// ══════════════════════════════════════════════════════════
// SCHEMA COLORI MATERIAL 3 (tema chiaro)
// ══════════════════════════════════════════════════════════

val md_light_primary = PremiumBlue
val md_light_onPrimary = Color.White
val md_light_primaryContainer = PremiumBlueLight
val md_light_onPrimaryContainer = PremiumBlue

val md_light_secondary = PremiumSage
val md_light_onSecondary = Color.White
val md_light_secondaryContainer = PremiumSageLight
val md_light_onSecondaryContainer = PremiumSage

val md_light_tertiary = SoftAmber
val md_light_onTertiary = Color.White
val md_light_tertiaryContainer = SoftAmberLight
val md_light_onTertiaryContainer = SoftAmber

val md_light_error = SoftRose
val md_light_onError = Color.White
val md_light_errorContainer = SoftRoseLight
val md_light_onErrorContainer = SoftRose

val md_light_background = PremiumBackground
val md_light_onBackground = Slate900
val md_light_surface = PremiumCard
val md_light_onSurface = Slate900
val md_light_surfaceVariant = PremiumSurface
val md_light_onSurfaceVariant = Slate500
val md_light_outline = Slate400
val md_light_outlineVariant = Color(0xFFE2E8F0)
