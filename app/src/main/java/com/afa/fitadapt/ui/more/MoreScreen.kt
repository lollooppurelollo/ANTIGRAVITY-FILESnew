// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Altro (menu esteso)
// =============================================================
package com.afa.fitadapt.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook

import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue

import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SoftAmber

/**
 * Schermata "Altro" — menu esteso con collegamento a tutte le sezioni secondarie.
 */
@Composable
fun MoreScreen(
    onNavigateToDiary: () -> Unit,
    onNavigateToArticles: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProtected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Altro",
            style = MaterialTheme.typography.headlineLarge,
            color = NavyBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Tutte le funzionalità dell'app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Consigli e articoli
        MenuCard(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            title = "Consigli",
            subtitle = "Articoli su attività fisica, alimentazione, sonno e altro",
            color = SageGreen,
            onClick = onNavigateToArticles
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Export dati
        MenuCard(
            icon = Icons.Outlined.QrCode,
            title = "Export dati",
            subtitle = "Esporta i tuoi dati in formato JSON o QR code",
            color = SoftAmber,
            onClick = onNavigateToExport
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Impostazioni
        MenuCard(
            icon = Icons.Outlined.Settings,
            title = "Impostazioni",
            subtitle = "Notifiche, biometria, preferenze",
            color = NavyBlue,
            onClick = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sezione protetta — separata visivamente
        Text(
            "Sezione riservata",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        MenuCard(
            icon = Icons.Outlined.Lock,
            title = "Area protetta",
            subtitle = "Gestisci schede, esercizi, obiettivi (richiede password)",
            color = NavyBlue,
            onClick = onNavigateToProtected
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icona colorata
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, title, tint = color, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
