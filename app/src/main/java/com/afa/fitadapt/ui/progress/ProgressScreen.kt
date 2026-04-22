// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Progressi
// =============================================================
package com.afa.fitadapt.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import com.afa.fitadapt.data.local.entity.SessionEntity
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
                .padding(16.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "I tuoi progressi",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Monitora il tuo percorso di salute",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Aderenza grande
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Aderenza al piano",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                val adherenceColor = when {
                    uiState.adherencePercent >= 70 -> MaterialTheme.colorScheme.secondary
                    uiState.adherencePercent >= 40 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
                Text(
                    "${uiState.adherencePercent.toInt()}%",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = adherenceColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { (uiState.adherencePercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = adherenceColor,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    strokeCap = StrokeCap.Round,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "${uiState.completedSessions} di ${uiState.totalSessions} sessioni completate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistiche griglia
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.LocalFireDepartment,
                value = "${uiState.currentStreak}",
                label = "Streak attuale",
                color = MaterialTheme.colorScheme.tertiary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.EmojiEvents,
                value = "${uiState.longestStreak}",
                label = "Record personale",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Sessioni Totali (Unificata)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = 0.4f
                )
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.FitnessCenter,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Attività totale",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    "${uiState.fullSessions + uiState.partialSessions}",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.fullSessions}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Sess. Intere",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.partialSessions}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Sess. Parziali",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Timer,
                value = "${uiState.totalMinutes}",
                label = "Minuti totali",
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // Grafici Trend
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Andamento parametri",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        TrendChartCard(
            title = "Dati biometrici e percezione",
            sessions = uiState.recentSessions.reversed(),
            scaleEntries = uiState.scaleEntries.sortedBy { it.date },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Obiettivi
        if (uiState.activeGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Obiettivi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
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
                val progress =
                    if (nextTarget > 0) (goal.currentValue / nextTarget).coerceIn(0f, 1f) else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    goal.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    medalLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            val colorProgress =
                                if (progress >= 1f) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            Text(
                                "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                color = colorProgress,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${goal.currentValue.toInt()} / ${nextTarget.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
            Box(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text("Dati insufficienti per il trend", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtri
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showAsthenia,
                        onClick = { showAsthenia = !showAsthenia },
                        label = { Text("Astenia", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = showAsthenia,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                    FilterChip(
                        selected = showPain,
                        onClick = { showPain = !showPain },
                        label = { Text("Dolore", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                            selectedLabelColor = MaterialTheme.colorScheme.tertiary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = showPain,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            selectedBorderColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
                // ... (simplified chip style for others)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallFilterChip(
                        selected = showRestDyspnea,
                        label = "Dispnea Riposo",
                        onClick = { showRestDyspnea = !showRestDyspnea })
                    SmallFilterChip(
                        selected = showExertionDyspnea,
                        label = "Dispnea Sforzo",
                        onClick = { showExertionDyspnea = !showExertionDyspnea })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val errorColor = MaterialTheme.colorScheme.error
            val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Y-Axis NRS Labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    listOf("10", "8", "6", "4", "2", "0").forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Orizzontal grid lines (NRS levels)
                        for (i in 0..5) {
                            val y = height - (i * 2f / 10f * height)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val spacing = if (allDates.size > 1) width / (allDates.size - 1) else 0f

                        // Helper per disegnare linee
                        fun drawTrendLine(points: List<Pair<Int, Float>>, color: Color) {
                            if (points.size < 2) return
                            val path = Path()
                            points.forEachIndexed { i, p ->
                                val x = p.first * spacing
                                val y = height - (p.second / 10f * height)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(
                                path,
                                color = color,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Preparazione punti
                        if (showEffort) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val s = completedSessions.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                s?.perceivedEffort?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, errorColor)
                        }
                        if (showMood) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val s = completedSessions.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                s?.mood?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, secondaryColor)
                        }
                        if (showAsthenia) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val e = scaleEntries.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                e?.asthenia?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, primaryColor)
                        }
                        if (showPain) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val e = scaleEntries.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                e?.osteoarticularPain?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, tertiaryColor)
                        }
                        if (showRestDyspnea) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val e = scaleEntries.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                e?.restDyspnea?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, Color.Cyan)
                        }
                        if (showExertionDyspnea) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val e = scaleEntries.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                e?.exertionDyspnea?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, Color.Magenta)
                        }
                        if (showSleep) {
                            val pts = allDates.mapIndexedNotNull { i, d ->
                                val s = completedSessions.find {
                                    com.afa.fitadapt.util.DateUtils.isSameDay(
                                        it.date,
                                        d
                                    )
                                }
                                s?.sleepQuality?.let { i to it.toFloat() }
                            }
                            drawTrendLine(pts, Color.Gray)
                        }
                    }
                }
            }

            // X-Axis Labels (Dates)
            val sdf = SimpleDateFormat("dd/MM", Locale.ITALY)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Spacer matching Y-axis column width
                Spacer(modifier = Modifier.width(24.dp))

                Row(
                    modifier = Modifier.weight(1f),
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
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Legenda dinamica
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showAsthenia) LegendItem("Astenia", primaryColor)
                    if (showPain) LegendItem("Dolore", tertiaryColor)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showRestDyspnea) LegendItem("Dispnea a riposo", Color.Cyan)
                    if (showExertionDyspnea) LegendItem("Dispnea a sforzi minimi", Color.Magenta)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showEffort) LegendItem("Fatica Sessione", errorColor)
                    if (showMood) LegendItem("Umore", secondaryColor)
                    if (showSleep) LegendItem("Qualità sonno", Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun SmallFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 10.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            selectedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        ),
        modifier = Modifier.height(32.dp)
    )
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, label, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
