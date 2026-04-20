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

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.ui.theme.SoftRose

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Schermata Progressi — mostra statistiche, streak, aderenza e obiettivi.
 */
@OptIn(ExperimentalMaterial3Api::class)
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

        // Statistiche griglia
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.LocalFireDepartment, value = "${uiState.currentStreak}", label = "Streak attuale", color = SoftAmber, bgColor = SoftAmberLight)
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.EmojiEvents, value = "${uiState.longestStreak}", label = "Record streak", color = SageGreen, bgColor = SageGreenLight)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Card Sessioni Totali (Unificata)
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = FitlyBlueLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FitnessCenter, null, tint = NavyBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sessioni totali", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                }
                Text("${uiState.fullSessions + uiState.partialSessions}", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${uiState.fullSessions}", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                        Text("Sess. Intere", style = MaterialTheme.typography.labelSmall, color = NavyBlue.copy(alpha = 0.6f))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${uiState.partialSessions}", style = MaterialTheme.typography.titleSmall, color = FitlyBlue)
                        Text("Sess. Parziali", style = MaterialTheme.typography.labelSmall, color = FitlyBlue.copy(alpha = 0.7f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.Timer, value = "${uiState.totalMinutes}", label = "Minuti totali", color = NavyBlue, bgColor = FitlyBlueLight)
            Spacer(modifier = Modifier.weight(1f)) // Mantieni l'allineamento se necessario o metti altro
        }

        // Grafici Trend
        if (uiState.recentSessions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Trend Parametri", style = MaterialTheme.typography.titleMedium, color = NavyBlue, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(12.dp))

            TrendChartCard(
                title = "Andamento Parametri",
                sessions = uiState.recentSessions.reversed(),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        // Obiettivi
        if (uiState.activeGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Obiettivi", style = MaterialTheme.typography.titleMedium, color = NavyBlue, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(8.dp))

            uiState.activeGoals.forEach { goal ->
                val nextTarget = when {
                    goal.currentValue < goal.targetValue -> goal.targetValue
                    goal.silverValue != null && goal.currentValue < goal.silverValue -> goal.silverValue
                    goal.goldValue != null && goal.currentValue < goal.goldValue -> goal.goldValue
                    else -> goal.goldValue ?: goal.silverValue ?: goal.targetValue
                }
                val medalLabel = when {
                    goal.currentValue < goal.targetValue -> "Target: Bronze"
                    goal.silverValue != null && goal.currentValue < goal.silverValue -> "Target: Silver"
                    goal.goldValue != null && goal.currentValue < goal.goldValue -> "Target: Gold"
                    else -> "Obiettivo completato! 🏆"
                }
                val progress = if (nextTarget > 0) (goal.currentValue / nextTarget).coerceIn(0f, 1f) else 0f
                
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(goal.title, style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                                Text(medalLabel, style = MaterialTheme.typography.labelSmall, color = FitlyBlue)
                            }
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
                        Text("${goal.currentValue.toInt()} / ${nextTarget.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendChartCard(
    title: String,
    sessions: List<SessionEntity>,
    modifier: Modifier = Modifier
) {
    val completedSessions = sessions.filter { it.completed }
    var showEffort by remember { mutableStateOf(true) }
    var showMood by remember { mutableStateOf(true) }
    var showSleep by remember { mutableStateOf(false) }
    var showDuration by remember { mutableStateOf(false) }

    if (completedSessions.size < 2) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Dati insufficienti per il trend", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = NavyBlue)
            Spacer(modifier = Modifier.height(12.dp))

            // Filtri
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(selected = showEffort, onClick = { showEffort = !showEffort }, label = { Text("Fatica", fontSize = 9.sp) })
                FilterChip(selected = showMood, onClick = { showMood = !showMood }, label = { Text("Umore", fontSize = 9.sp) })
                FilterChip(selected = showSleep, onClick = { showSleep = !showSleep }, label = { Text("Sonno", fontSize = 9.sp) })
                FilterChip(selected = showDuration, onClick = { showDuration = !showDuration }, label = { Text("Durata", fontSize = 9.sp) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 24.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (completedSessions.size - 1)

                    // Linea Fatica (SoftRose)
                    if (showEffort) {
                        val effortPath = Path()
                        completedSessions.forEachIndexed { index, session ->
                            val effort = session.perceivedEffort?.toFloat() ?: 5f
                            val x = index * spacing
                            val y = height - (effort / 10f * height)
                            if (index == 0) effortPath.moveTo(x, y) else effortPath.lineTo(x, y)
                        }
                        drawPath(effortPath, color = SoftRose, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }

                    // Linea Umore (SageGreen)
                    if (showMood) {
                        val moodPath = Path()
                        completedSessions.forEachIndexed { index, session ->
                            val mood = session.mood?.toFloat() ?: 5f
                            val x = index * spacing
                            val y = height - (mood / 10f * height)
                            if (index == 0) moodPath.moveTo(x, y) else moodPath.lineTo(x, y)
                        }
                        drawPath(moodPath, color = SageGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }

                    // Linea Durata (FitlyBlue) - Normalizzata su 60 min o max
                    if (showDuration) {
                        val durationPath = Path()
                        val maxDur = completedSessions.maxOf { it.actualDurationMin ?: 30 }.toFloat().coerceAtLeast(60f)
                        completedSessions.forEachIndexed { index, session ->
                            val dur = session.actualDurationMin?.toFloat() ?: 0f
                            val x = index * spacing
                            val y = height - (dur / maxDur * height)
                            if (index == 0) durationPath.moveTo(x, y) else durationPath.lineTo(x, y)
                        }
                        drawPath(durationPath, color = FitlyBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }

                    // Linea Sonno (SoftAmber)
                    if (showSleep) {
                        val sleepPath = Path()
                        completedSessions.forEachIndexed { index, session ->
                            val sleep = session.sleepQuality?.toFloat() ?: 5f
                            val x = index * spacing
                            val y = height - (sleep / 10f * height)
                            if (index == 0) sleepPath.moveTo(x, y) else sleepPath.lineTo(x, y)
                        }
                        drawPath(sleepPath, color = SoftAmber, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }

            // X-Axis Labels (Dates)
            val sdf = SimpleDateFormat("dd/MM", Locale.ITALY)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelIndices = if (completedSessions.size <= 5) {
                    completedSessions.indices
                } else {
                    listOf(
                        0,
                        completedSessions.size / 4,
                        completedSessions.size / 2,
                        (completedSessions.size * 3) / 4,
                        completedSessions.size - 1
                    )
                }
                
                completedSessions.forEachIndexed { index, session ->
                    if (index in labelIndices) {
                        Text(
                            text = sdf.format(Date(session.date)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    } else if (completedSessions.size > 5) {
                         Spacer(modifier = Modifier.width(1.dp)) // Maintain some spacing for the row
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (showEffort) LegendItem("Fatica", SoftRose)
                if (showMood) LegendItem("Umore", SageGreen)
                if (showSleep) LegendItem("Sonno", SoftAmber)
                if (showDuration) LegendItem("Durata", FitlyBlue)
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
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
