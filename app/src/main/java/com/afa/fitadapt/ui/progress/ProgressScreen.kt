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
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
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
        Spacer(modifier = Modifier.height(24.dp))
        Text("Trend Parametri", style = MaterialTheme.typography.titleMedium, color = NavyBlue, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(12.dp))

        TrendChartCard(
            title = "Andamento Parametri",
            sessions = uiState.recentSessions.reversed(),
            scaleEntries = uiState.scaleEntries.sortedBy { it.date },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

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
    scaleEntries: List<ScaleEntryEntity>,
    modifier: Modifier = Modifier
) {
    val completedSessions = sessions.filter { it.completed }
    
    // Filtri visibilità
    var showEffort by remember { mutableStateOf(false) }
    var showMood by remember { mutableStateOf(false) }
    var showSleep by remember { mutableStateOf(false) }
    var showAsthenia by remember { mutableStateOf(true) }
    var showPain by remember { mutableStateOf(true) }
    var showRestDyspnea by remember { mutableStateOf(false) }
    var showExertionDyspnea by remember { mutableStateOf(false) }

    // Unifichiamo i dati per asse X (date)
    val allDates = (completedSessions.map { it.date } + scaleEntries.map { it.date })
        .map { com.afa.fitadapt.util.DateUtils.toDayTimestamp(it) }
        .distinct()
        .sorted()
        .takeLast(10) // Ultime 10 date con dati

    if (allDates.size < 2) {
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = showAsthenia, onClick = { showAsthenia = !showAsthenia }, label = { Text("Astenia", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                    FilterChip(selected = showPain, onClick = { showPain = !showPain }, label = { Text("Dolore", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                    FilterChip(selected = showRestDyspnea, onClick = { showRestDyspnea = !showRestDyspnea }, label = { Text("Dispnea a riposo", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = showExertionDyspnea, onClick = { showExertionDyspnea = !showExertionDyspnea }, label = { Text("Dispnea a sforzi minimi", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                    FilterChip(selected = showEffort, onClick = { showEffort = !showEffort }, label = { Text("Fatica Sessione", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = showMood, onClick = { showMood = !showMood }, label = { Text("Umore", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                    FilterChip(selected = showSleep, onClick = { showSleep = !showSleep }, label = { Text("Qualità sonno", fontSize = 9.sp) }, modifier = Modifier.height(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 24.dp, end = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (allDates.size - 1)

                    // Helper per disegnare linee
                    fun drawTrendLine(points: List<Pair<Int, Float>>, color: Color) {
                        if (points.size < 2) return
                        val path = Path()
                        points.forEachIndexed { i, p ->
                            val x = p.first * spacing
                            val y = height - (p.second / 10f * height)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(path, color = color, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }

                    // Preparazione punti
                    if (showEffort) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val s = completedSessions.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            s?.perceivedEffort?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, SoftRose)
                    }
                    if (showMood) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val s = completedSessions.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            s?.mood?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, SageGreen)
                    }
                    if (showAsthenia) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val e = scaleEntries.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            e?.asthenia?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, FitlyBlue)
                    }
                    if (showPain) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val e = scaleEntries.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            e?.osteoarticularPain?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, SoftAmber)
                    }
                    if (showRestDyspnea) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val e = scaleEntries.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            e?.restDyspnea?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, Color.Cyan)
                    }
                    if (showExertionDyspnea) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val e = scaleEntries.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            e?.exertionDyspnea?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, Color.Magenta)
                    }
                    if (showSleep) {
                        val pts = allDates.mapIndexedNotNull { i, d -> 
                            val s = completedSessions.find { com.afa.fitadapt.util.DateUtils.isSameDay(it.date, d) }
                            s?.sleepQuality?.let { i to it.toFloat() }
                        }
                        drawTrendLine(pts, Color.Gray)
                    }
                }
            }

            // X-Axis Labels (Dates)
            val sdf = SimpleDateFormat("dd/MM", Locale.ITALY)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                allDates.forEach { date ->
                    Text(
                        text = sdf.format(Date(date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Legenda dinamica
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showAsthenia) LegendItem("Astenia", FitlyBlue)
                    if (showPain) LegendItem("Dolore", SoftAmber)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showRestDyspnea) LegendItem("Dispnea a riposo", Color.Cyan)
                    if (showExertionDyspnea) LegendItem("Dispnea a sforzi minimi", Color.Magenta)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showEffort) LegendItem("Fatica Sessione", SoftRose)
                    if (showMood) LegendItem("Umore", SageGreen)
                    if (showSleep) LegendItem("Qualità sonno", Color.Gray)
                }
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
