// =============================================================
// KinApto - Attività Fisica Adattata
// Forme Material 3
// =============================================================
package com.kinapto.fitadapt.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Forme dell'app KinApto — angoli arrotondati generosi per un look morbido e rassicurante.
 */
val KinAptoShapes = Shapes(
    // Forme piccole — chip, badge, piccoli indicatori
    extraSmall = RoundedCornerShape(8.dp),

    // Forme piccole — bottoni, text field
    small = RoundedCornerShape(12.dp),

    // Forme medie — card standard
    medium = RoundedCornerShape(16.dp),

    // Forme grandi — card prominenti, dialog
    large = RoundedCornerShape(20.dp),

    // Forme extra-large — bottom sheet, pannelli grandi
    extraLarge = RoundedCornerShape(28.dp)
)
