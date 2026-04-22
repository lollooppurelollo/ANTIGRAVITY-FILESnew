package com.afa.fitadapt.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Opzioni di tema disponibili per l'utente.
 */
enum class ThemeOption(
    val label: String,
    val primaryColor: Color,
    val lightColor: Color
) {
    LILAC("Lilla", Color(0xFFC4B5FD), Color(0xFFEDE9FE)),
    PURPLE("Viola", Color(0xFF7C3AED), Color(0xFFF5F3FF)),
    PINK("Rosa", Color(0xFFF472B6), Color(0xFFFDF2F8)),
    SOFT_RED("Rosso", Color(0xFFEF4444), Color(0xFFFEF2F2)),
    GREEN("Verde", Color(0xFF22C55E), Color(0xFFF0FDF4)),
    BEIGE("Beige", Color(0xFFE7D3A3), Color(0xFFFEFCE8)),
    LIGHT_BLUE("Azzurro", Color(0xFF2563EB), Color(0xFFDBEAFE)),
    FOREST_GREEN("Foresta", Color(0xFF166534), Color(0xFFF0FDF4)),
    TEAL("Teal", Color(0xFF14B8A6), Color(0xFFF0FDFA)),
    INDIGO("Indaco", Color(0xFF4F46E5), Color(0xFFEEF2FF));

    companion object {
        fun fromOrdinal(ordinal: Int): ThemeOption {
            return entries.getOrElse(ordinal) { LIGHT_BLUE }
        }
    }
}
