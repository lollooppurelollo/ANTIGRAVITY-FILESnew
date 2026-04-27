// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Home
// =============================================================
package com.afa.fitadapt.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.graphics.Color
import com.afa.fitadapt.util.DateUtils
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.afa.fitadapt.data.local.entity.ScheduledSessionEntity
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.colorResource
import com.afa.fitadapt.R

/**
 * Schermata Home — dashboard principale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    themeViewModel: com.afa.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
    onNavigateToSession: () -> Unit,
    onNavigateToCard: () -> Unit,
    onNavigateToArticle: (Long) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Header and CTA stay same) ...
        // ── Header con gradiente Premium ──
        val headerColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(headerColor, headerColor.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Buongiorno!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                if (uiState.patientCode.isNotBlank()) {
                    Text(
                        text = "Codice: ${uiState.patientCode}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Giorni consecutivi e statistiche rapide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.LocalFireDepartment,
                        value = "${uiState.currentStreak}",
                        label = "Giorni consecutivi",
                        color = Color.White
                    )
                    ModernStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.FitnessCenter,
                        value = "${uiState.totalCompleted}",
                        label = "Sessioni",
                        color = Color.White,
                        onClick = onNavigateToHistory
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Calendario Allenamenti ──
        val accentColor = MaterialTheme.colorScheme.primary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (uiState.isMonthlyView) "Calendario Mensile" else "Calendario Settimanale",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                IconButton(onClick = { 
                    selectedDateMillis = System.currentTimeMillis()
                    showAddDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi programmato", tint = accentColor)
                }
                IconButton(onClick = { homeViewModel.setCalendarView(!uiState.isMonthlyView) }) {
                    Icon(
                        if (uiState.isMonthlyView) Icons.Outlined.ViewWeek else Icons.Outlined.CalendarMonth,
                        contentDescription = "Cambia vista",
                        tint = accentColor
                    )
                }
            }
        }
        
    if (uiState.isMonthlyView) {
        MonthlyCalendar(
            scheduled = uiState.scheduledSessions,
            onDayClick = { date ->
                selectedDateMillis = date
                showAddDialog = true
            },
            onDeleteSession = { homeViewModel.deleteScheduledSession(it) }
        )
    } else {
        ModernWeeklyCalendar(
            scheduled = uiState.scheduledSessions,
            onDayClick = { date ->
                selectedDateMillis = date
                showAddDialog = true
            },
            onDeleteSession = { homeViewModel.deleteScheduledSession(it) }
        )
    }

        Spacer(modifier = Modifier.height(20.dp))

        // ── CTA Registra Sessione ──
        if (uiState.activeCard != null) {
            val buttonColor = MaterialTheme.colorScheme.primary
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (uiState.completedToday) {
                    val containerColor = if (useOriginalColors) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.secondaryContainer
                    val contentColor = if (useOriginalColors) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
                    val iconBgColor = if (useOriginalColors) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(iconBgColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, null, tint = if (useOriginalColors) Color.White else MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Allenamento registrato", style = MaterialTheme.typography.titleSmall, color = contentColor, fontWeight = FontWeight.Bold)
                                Text("Ottimo lavoro per oggi!", style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = onNavigateToSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Add, "Registra", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (uiState.completedToday) "Registra un altro allenamento" else "Registra allenamento",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Scheda Attiva ──
        uiState.activeCard?.let { card ->
            SectionTitle("Piano attivo")
            val cardIconColor = MaterialTheme.colorScheme.primary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onNavigateToCard() },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(cardIconColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.FitnessCenter, "Scheda", tint = cardIconColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(card.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            val info = listOfNotNull(
                                card.durationWeeks?.let { "$it settimane" },
                                card.targetSessions?.let { "$it sessioni obiettivo" }
                            ).joinToString(" • ")
                            if (info.isNotEmpty()) {
                                Text(info, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
?: run {
            // Nessuna scheda attiva
            SectionTitle("La tua scheda")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Nessuna scheda attiva", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Chiedi al tuo operatore di configurare una scheda", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Articolo in evidenza ──
        uiState.featuredArticle?.let { article ->
            SectionTitle("Consiglio della settimana")
            val articleIconColor = if (useOriginalColors) Color(0xFFFBC02D) else MaterialTheme.colorScheme.primary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onNavigateToArticle(article.id) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(articleIconColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.Article, "Articolo", tint = articleIconColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(article.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(article.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        // Obiettivi attivi
        if (uiState.activeGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("I tuoi obiettivi")
            uiState.activeGoals.take(3).forEach { goal ->
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
                val progress = if (nextTarget > 0) (goal.currentValue / nextTarget).coerceIn(0f, 1f) else 0f
                val goalAccentColor = MaterialTheme.colorScheme.primary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    goal.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        medalLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = goalAccentColor
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
                            Text(
                                "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (useOriginalColors) Color.Black else (if (progress >= 1f) MaterialTheme.colorScheme.secondary else goalAccentColor),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) MaterialTheme.colorScheme.secondary else goalAccentColor,
                            trackColor = goalAccentColor.copy(alpha = 0.1f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
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

    if (showAddDialog) {
        AddScheduledSessionDialog(
            initialDate = selectedDateMillis,
            activeCard = uiState.activeCard,
            onDismiss = { showAddDialog = false },
            onConfirm = { session ->
                homeViewModel.addScheduledSession(session)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MonthlyCalendar(
    scheduled: List<ScheduledSessionEntity>,
    onDayClick: (Long) -> Unit,
    onDeleteSession: (ScheduledSessionEntity) -> Unit
) {
    val calendar = Calendar.getInstance()
    var currentMonth by remember { mutableStateOf(calendar.clone() as Calendar) }
    var selectedDaySessions by remember { mutableStateOf<List<ScheduledSessionEntity>>(emptyList()) }
    var showDayDetails by remember { mutableStateOf(false) }

    val monthName = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ITALY)
        ?.replaceFirstChar { it.uppercase() } ?: ""
    val year = currentMonth.get(Calendar.YEAR)

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Mese
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, -1)
                        currentMonth = newMonth
                    }) {
                        Icon(Icons.Default.ChevronLeft, "Mese precedente", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("$monthName $year", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, 1)
                        currentMonth = newMonth
                    }) {
                        Icon(Icons.Default.ChevronRight, "Mese successivo", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Giorni della settimana
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Griglia giorni
                val firstDayOfMonth = currentMonth.clone() as Calendar
                firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
                val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
                val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                
                val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                val rows = (daysInMonth + offset + 6) / 7

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val dayNum = row * 7 + col - offset + 1
                            if (dayNum in 1..daysInMonth) {
                                val dayCal = currentMonth.clone() as Calendar
                                dayCal.set(Calendar.DAY_OF_MONTH, dayNum)
                                val isToday = DateUtils.isToday(dayCal.timeInMillis)
                                val daySessions = scheduled.filter { DateUtils.isSameDay(it.date, dayCal.timeInMillis) }
                                val hasSession = daySessions.isNotEmpty()

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isToday) MaterialTheme.colorScheme.primary 
                                            else if (hasSession) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            else Color.Transparent
                                        )
                                        .clickable { 
                                            if (hasSession) {
                                                selectedDaySessions = daySessions
                                                showDayDetails = true
                                            } else {
                                                onDayClick(dayCal.timeInMillis)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNum.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isToday || hasSession) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (hasSession && !isToday) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(bottom = 4.dp)
                                                .size(4.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        
        if (showDayDetails) {
            Spacer(modifier = Modifier.height(12.dp))
            DaySessionsList(
                sessions = selectedDaySessions,
                onDelete = { 
                    onDeleteSession(it)
                    selectedDaySessions = selectedDaySessions.filter { s -> s.id != it.id }
                    if (selectedDaySessions.isEmpty()) showDayDetails = false
                },
                onAddMore = {
                    onDayClick(selectedDaySessions.first().date)
                    showDayDetails = false
                }
            )
        }
    }
}

@Composable
fun DaySessionsList(
    sessions: List<ScheduledSessionEntity>,
    onDelete: (ScheduledSessionEntity) -> Unit,
    onAddMore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Allenamenti programmati", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onAddMore) {
                    Icon(Icons.Default.Add, "Aggiungi", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.FitnessCenter, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(session.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(session.startTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onDelete(session) }) {
                        Icon(Icons.Default.Delete, "Elimina", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduledSessionDialog(
    initialDate: Long,
    activeCard: TrainingCardEntity?,
    onDismiss: () -> Unit,
    onConfirm: (ScheduledSessionEntity) -> Unit
) {
    var title by remember { mutableStateOf("Allenamento") }
    var selectedDateMillis by remember { mutableStateOf(initialDate) }
    var startTime by remember { mutableStateOf("10:00") }
    var notificationEnabled by remember { mutableStateOf(true) }
    var recurrenceType by remember { mutableStateOf("NONE") } // NONE, WEEKLY, EVERY_X_DAYS
    var recurrenceValue by remember { mutableStateOf(1) } // Default 1 for Every X Days
    var showDatePicker by remember { mutableStateOf(false) }

    // State for weekly days
    var selectedDays by remember {
        mutableStateOf(
            if (initialDate > 0) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = initialDate
                // Calendar.DAY_OF_WEEK: 1=Sun, 2=Mon, ..., 7=Sat
                // We want 1=Mon, ..., 7=Sun
                val dow = cal.get(Calendar.DAY_OF_WEEK)
                val mapped = if (dow == Calendar.SUNDAY) 7 else dow - 1
                setOf(mapped)
            } else emptySet<Int>()
        )
    }

    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(selectedDateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programma Allenamento", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { },
                    label = { Text("Data di inizio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Outlined.CalendarMonth, "Seleziona data", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Orario (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Ripetizione", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        RecurrenceChip(selected = recurrenceType == "NONE", label = "Singola", onClick = { recurrenceType = "NONE" })
                    }
                    Box(modifier = Modifier.weight(1.5f)) {
                        RecurrenceChip(selected = recurrenceType == "WEEKLY", label = "Settimanale", onClick = { recurrenceType = "WEEKLY" })
                    }
                    Box(modifier = Modifier.weight(1.4f)) {
                        RecurrenceChip(selected = recurrenceType == "EVERY_X_DAYS", label = "Ogni X gg", onClick = { recurrenceType = "EVERY_X_DAYS" })
                    }
                }

                if (recurrenceType == "WEEKLY") {
                    val dayLabels = listOf("L", "M", "M", "G", "V", "S", "D")
                    Text("Giorni della settimana", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayLabels.forEachIndexed { index, label ->
                            val dayNumber = index + 1 // 1=Mon, 7=Sun
                            val isSelected = selectedDays.contains(dayNumber)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                    .clickable {
                                        selectedDays = if (isSelected) selectedDays - dayNumber else selectedDays + dayNumber
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                if (recurrenceType == "EVERY_X_DAYS") {
                    OutlinedTextField(
                        value = recurrenceValue.toString(),
                        onValueChange = { recurrenceValue = it.toIntOrNull() ?: 1 },
                        label = { Text("Ogni quanti giorni?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notifica promemoria", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { notificationEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ScheduledSessionEntity(
                            date = selectedDateMillis,
                            startTime = startTime,
                            title = title,
                            notificationEnabled = notificationEnabled,
                            recurrenceType = recurrenceType,
                            recurrenceValue = recurrenceValue,
                            recurrenceDays = if (recurrenceType == "WEEKLY") {
                                selectedDays.sorted().joinToString(",")
                            } else null,
                            cardId = activeCard?.id
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salva")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
    // ... DatePicker logic stays same ...

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateMillis = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annulla")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ModernWeeklyCalendar(
    scheduled: List<ScheduledSessionEntity>,
    onDayClick: (Long) -> Unit,
    onDeleteSession: (ScheduledSessionEntity) -> Unit
) {
    val calendar = Calendar.getInstance()
    val days = (0..6).map {
        val c = calendar.clone() as Calendar
        c.add(Calendar.DAY_OF_YEAR, it)
        c
    }
    
    var selectedDaySessions by remember { mutableStateOf<List<ScheduledSessionEntity>>(emptyList()) }
    var showDayDetails by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val isToday = DateUtils.isToday(day.timeInMillis)
                val daySessions = scheduled.filter { DateUtils.isSameDay(it.date, day.timeInMillis) }
                val hasSession = daySessions.isNotEmpty()
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isToday) MaterialTheme.colorScheme.primary else if (hasSession) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface)
                        .clickable {
                            onDayClick(day.timeInMillis)
                            if (hasSession) {
                                selectedDaySessions = daySessions
                                showDayDetails = true
                            }
                        }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ITALY)?.take(1) ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        day.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (hasSession) {
                        Box(modifier = Modifier.padding(top = 6.dp).size(6.dp).background(if (isToday) Color.White else MaterialTheme.colorScheme.primary, CircleShape))
                    }
                }
            }
        }
        
        if (showDayDetails) {
            Spacer(modifier = Modifier.height(12.dp))
            DaySessionsList(
                sessions = selectedDaySessions,
                onDelete = {
                    onDeleteSession(it)
                    selectedDaySessions = selectedDaySessions.filter { s -> s.id != it.id }
                    if (selectedDaySessions.isEmpty()) showDayDetails = false
                },
                onAddMore = { onDayClick(days.first().timeInMillis) } // Simplified, will use the selected date from state anyway
            )
        }
    }
}

@Composable
private fun ModernStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(color.copy(alpha = 0.15f))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun RecurrenceChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun StatBadge(
    icon: ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
    }
}
