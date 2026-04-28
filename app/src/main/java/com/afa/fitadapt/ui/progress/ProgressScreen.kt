// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Progressi
// =============================================================
package com.afa.fitadapt.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.outlined.Celebration
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
fun ProgressScreen(
    progressViewModel: ProgressViewModel,
    themeViewModel: com.afa.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit
) {
    val uiState by progressViewModel.uiState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()

    val headerColor = MaterialTheme.colorScheme.primary

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
                            headerColor,
                            headerColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "I tuoi Progressi",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Monitora il tuo percorso di salute",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                // Icona stilizzata rimossa (come richiesto)
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
        val streakColor = if (useOriginalColors) Color(0xFF42A5F5) else MaterialTheme.colorScheme.tertiary
        val recordColor = if (useOriginalColors) Color(0xFFFBC02D) else MaterialTheme.colorScheme.secondary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f).clickable { onNavigateToHistory() },
                icon = Icons.Outlined.LocalFireDepartment,
                value = "${uiState.currentStreak}",
                label = "Giorni Consecutivi",
                color = streakColor
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.EmojiEvents,
                value = "${uiState.longestStreak}",
                label = "Record personale",
                color = recordColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Sessioni Totali (Unificata)
        val activityColor = MaterialTheme.colorScheme.primary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clickable { onNavigateToHistory() },
            colors = CardDefaults.cardColors(
                containerColor = activityColor.copy(
                    alpha = 0.08f
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
                        tint = activityColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Attività totale",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = activityColor
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
                            .background(activityColor.copy(alpha = 0.1f))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.partialSessions}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = activityColor
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

        val minutesColor = MaterialTheme.colorScheme.primary
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
                color = minutesColor
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
            useOriginalColors = useOriginalColors,
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
                    goal.currentValue < goal.targetValue -> "Target: Bronzo"
                    goal.silverValue != null && goal.currentValue < goal.silverValue -> "Target: Argento"
                    goal.goldValue != null && goal.currentValue < goal.goldValue -> "Target: Oro"
                    else -> "Obiettivo completato! 🏆"
                }
                val progress =
                    if (nextTarget > 0) (goal.currentValue / nextTarget).coerceIn(0f, 1f) else 0f

                val currentGoalAccentColor = MaterialTheme.colorScheme.primary

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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        medalLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = currentGoalAccentColor
                                    )
                                    if (goal.currentValue >= goal.targetValue) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val reachedText = when {
                                            goal.goldValue != null && goal.currentValue >= goal.goldValue -> "Traguardi: 🥉 🥈 🥇"
                                            goal.silverValue != null && goal.currentValue >= goal.silverValue -> "Traguardi: 🥉 🥈"
                                            goal.currentValue >= goal.targetValue -> "Traguardi: 🥉"
                                            else -> ""
                                        }
                                        Text(
                                            reachedText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                            val colorProgress =
                                if (useOriginalColors) Color.Black else (if (progress >= 1f) MaterialTheme.colorScheme.secondary else currentGoalAccentColor)
                            Text(
                                "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                color = colorProgress,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val progressColor = if (progress >= 1f) MaterialTheme.colorScheme.secondary else currentGoalAccentColor
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = progressColor,
                            trackColor = currentGoalAccentColor.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
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

@Composable
private fun TrendChartCard(
    title: String,
    sessions: List<SessionEntity>,
    scaleEntries: List<ScaleEntryEntity>,
    useOriginalColors: Boolean,
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

    // Colori fissi per i parametri (per coerenza con i grafici medici)
    val colorAsthenia = if (useOriginalColors) Color(0xFFFBC02D) else Color(0xFFFF9800) // Giallo/Arancio allegro
    val colorPain = if (useOriginalColors) Color(0xFFE91E63) else Color(0xFFF44336) // Rosa/Rosso vivace
    val colorDyspneaRest = if (useOriginalColors) Color(0xFF00E676) else Color(0xFF4CAF50) // Verde
    val colorDyspneaExertion = if (useOriginalColors) Color(0xFF00BCD4) else Color(0xFF26C6DA) // Ciano acceso per differenziarlo da Astenia
    val colorEffort = if (useOriginalColors) Color(0xFF2196F3) else Color(0xFF03A9F4) // Blu
    val colorMood = if (useOriginalColors) Color(0xFFCDDC39) else Color(0xFF8BC34A) // Lime
    val colorSleep = if (useOriginalColors) Color(0xFF9C27B0) else Color(0xFF673AB7) // Viola

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
                    SmallFilterChip(
                        selected = showAsthenia,
                        label = "Astenia",
                        color = colorAsthenia,
                        onClick = { showAsthenia = !showAsthenia })
                    SmallFilterChip(
                        selected = showPain,
                        label = "Dolore",
                        color = colorPain,
                        onClick = { showPain = !showPain })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallFilterChip(
                        selected = showRestDyspnea,
                        label = "Dispnea Riposo",
                        color = colorDyspneaRest,
                        onClick = { showRestDyspnea = !showRestDyspnea })
                    SmallFilterChip(
                        selected = showExertionDyspnea,
                        label = "Dispnea Sforzo",
                        color = colorDyspneaExertion,
                        onClick = { showExertionDyspnea = !showExertionDyspnea })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallFilterChip(
                        selected = showEffort,
                        label = "Fatica",
                        color = colorEffort,
                        onClick = { showEffort = !showEffort })
                    SmallFilterChip(
                        selected = showMood,
                        label = "Umore",
                        color = colorMood,
                        onClick = { showMood = !showMood })
                    SmallFilterChip(
                        selected = showSleep,
                        label = "Sonno",
                        color = colorSleep,
                        onClick = { showSleep = !showSleep })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            drawTrendLine(pts, colorEffort)
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
                            drawTrendLine(pts, colorMood)
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
                            drawTrendLine(pts, colorAsthenia)
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
                            drawTrendLine(pts, colorPain)
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
                            if (pts.isNotEmpty()) drawTrendLine(pts, colorDyspneaRest)
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
                            if (pts.isNotEmpty()) drawTrendLine(pts, colorDyspneaExertion)
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
                            drawTrendLine(pts, colorSleep)
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val legendItems = listOfNotNull(
                    if (showAsthenia) "Astenia" to colorAsthenia else null,
                    if (showPain) "Dolore" to colorPain else null,
                    if (showRestDyspnea) "Dispnea Riposo" to colorDyspneaRest else null,
                    if (showExertionDyspnea) "Dispnea Sforzo" to colorDyspneaExertion else null,
                    if (showEffort) "Fatica" to colorEffort else null,
                    if (showMood) "Umore" to colorMood else null,
                    if (showSleep) "Sonno" to colorSleep else null
                )

                legendItems.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { (label, color) ->
                            Box(modifier = Modifier.weight(1f)) {
                                LegendItem(label, color)
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallFilterChip(selected: Boolean, label: String, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 10.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            selectedBorderColor = color
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
