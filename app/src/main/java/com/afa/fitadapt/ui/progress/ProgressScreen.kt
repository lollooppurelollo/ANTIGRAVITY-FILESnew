// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Progressi
// =============================================================
package com.afa.fitadapt.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.FitlyBlueLight
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SageGreenLight
import com.afa.fitadapt.ui.theme.SoftAmber
import com.afa.fitadapt.ui.theme.SoftAmberLight

/**
 * Schermata Progressi — mostra statistiche, streak, aderenza e obiettivi.
 */
@Composable
fun ProgressScreen(progressViewModel: ProgressViewModel) {
    val uiState by progressViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FitlyBlueLight, shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .padding(24.dp)
                .padding(top = 8.dp)
        ) {
            Column {
                Text("I tuoi progressi", style = MaterialTheme.typography.headlineLarge, color = NavyBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Monitora il tuo percorso", style = MaterialTheme.typography.bodyMedium, color = NavyBlue.copy(alpha = 0.6f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Aderenza grande
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Aderenza", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "${uiState.adherencePercent.toInt()}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.adherencePercent >= 70) SageGreen else if (uiState.adherencePercent >= 40) SoftAmber else FitlyBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (uiState.adherencePercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (uiState.adherencePercent >= 70) SageGreen else FitlyBlue,
                    trackColor = FitlyBlueLight,
                    strokeCap = StrokeCap.Round,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${uiState.completedSessions} di ${uiState.totalSessions} sessioni completate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistiche griglia 2x2
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.LocalFireDepartment, value = "${uiState.currentStreak}", label = "Streak attuale", color = SoftAmber, bgColor = SoftAmberLight)
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.EmojiEvents, value = "${uiState.longestStreak}", label = "Record streak", color = SageGreen, bgColor = SageGreenLight)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.Timer, value = "${uiState.totalMinutes}", label = "Minuti totali", color = FitlyBlue, bgColor = FitlyBlueLight)
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.FitnessCenter, value = "${uiState.completedSessions}", label = "Sessioni fatte", color = NavyBlue, bgColor = FitlyBlueLight)
        }

        // Obiettivi
        if (uiState.activeGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Obiettivi", style = MaterialTheme.typography.titleMedium, color = NavyBlue, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(8.dp))

            uiState.activeGoals.forEach { goal ->
                val progress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).coerceIn(0f, 1f) else 0f
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(goal.title, style = MaterialTheme.typography.titleSmall, color = NavyBlue, modifier = Modifier.weight(1f))
                            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = if (progress >= 1f) SageGreen else FitlyBlue, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) SageGreen else FitlyBlue,
                            trackColor = FitlyBlueLight,
                            strokeCap = StrokeCap.Round,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${goal.currentValue.toInt()} / ${goal.targetValue.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String, color: Color, bgColor: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = bgColor), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, label, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
        }
    }
}
