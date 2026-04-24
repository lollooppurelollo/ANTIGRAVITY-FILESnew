package com.afa.fitadapt.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Opzioni di tema disponibili per l'utente.
 */
enum class ThemeOption(
    val label: String,
    val primaryColor: Color,
    val lightColor: Color,
    val secondaryColor: Color,
    val tertiaryColor: Color
) {
    LILAC("Lilla", Color(0xFFC4B5FD), Color(0xFFEDE9FE), Color(0xFFA78BFA), Color(0xFF8B5CF6)),
    PURPLE("Viola", Color(0xFF7C3AED), Color(0xFFF5F3FF), Color(0xFF6D28D9), Color(0xFF5B21B6)),
    PINK("Rosa", Color(0xFFF472B6), Color(0xFFFDF2F8), Color(0xFFEC4899), Color(0xFFDB2777)),
    SOFT_RED("Rosso", Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFFDC2626), Color(0xFFB91C1C)),
    GREEN("Verde", Color(0xFF22C55E), Color(0xFFF0FDF4), Color(0xFF16A34A), Color(0xFF15803D)),
    BEIGE("Beige", Color(0xFFE7D3A3), Color(0xFFFEFCE8), Color(0xFFC5A059), Color(0xFFA67C52)),
    LIGHT_BLUE("Azzurro", Color(0xFF2563EB), Color(0xFFDBEAFE), Color(0xFF1D4ED8), Color(0xFF1E40AF)),
    FOREST_GREEN("Foresta", Color(0xFF166534), Color(0xFFF0FDF4), Color(0xFF14532D), Color(0xFF064E3B)),
    TEAL("Teal", Color(0xFF14B8A6), Color(0xFFF0FDFA), Color(0xFF0D9488), Color(0xFF0F766E)),
    INDIGO("Indaco", Color(0xFF4F46E5), Color(0xFFEEF2FF), Color(0xFF4338CA), Color(0xFF3730A3));

    companion object {
        fun fromOrdinal(ordinal: Int): ThemeOption {
            return entries.getOrElse(ordinal) { LIGHT_BLUE }
        }
    }
}
