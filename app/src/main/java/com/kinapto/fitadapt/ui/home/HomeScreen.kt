// =============================================================
// KinApto - Attività Fisica Adattata
// Schermata: Home
// =============================================================
package com.kinapto.fitadapt.ui.home

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
import com.kinapto.fitadapt.util.DateUtils
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
import com.kinapto.fitadapt.data.local.entity.ScheduledSessionEntity
import com.kinapto.fitadapt.data.local.entity.TrainingCardEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.kinapto.fitadapt.R

/**
 * Schermata Home — dashboard principale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    themeViewModel: com.kinapto.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
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
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                if (uiState.patientCode.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.home_patient_code, uiState.patientCode),
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
                        label = stringResource(R.string.home_streak_label),
                        color = Color.White
                    )
                    ModernStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.FitnessCenter,
                        value = "${(uiState.adherence * 100).toInt()}%",
                        label = stringResource(R.string.home_adherence_label),
                        color = Color.White
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
                if (uiState.isMonthlyView) stringResource(R.string.home_calendar_monthly) else stringResource(R.string.home_calendar_weekly),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                IconButton(onClick = { 
                    selectedDateMillis = System.currentTimeMillis()
                    showAddDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_add_scheduled), tint = accentColor)
                }
                IconButton(onClick = { homeViewModel.setCalendarView(!uiState.isMonthlyView) }) {
                    Icon(
                        if (uiState.isMonthlyView) Icons.Outlined.ViewWeek else Icons.Outlined.CalendarMonth,
                        contentDescription = stringResource(R.string.home_change_view),
                        tint = accentColor
                    )
                }
            }
        }
        
    if (uiState.isMonthlyView) {
        MonthlyCalendar(
            scheduled = uiState.scheduledSessions,
            completedDates = uiState.completedSessionsDates,
            onDayClick = { date ->
                selectedDateMillis = date
                showAddDialog = true
            },
            onDeleteSession = { homeViewModel.deleteScheduledSession(it) }
        )
    } else {
        ModernWeeklyCalendar(
            scheduled = uiState.scheduledSessions,
            completedDates = uiState.completedSessionsDates,
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
                                Text(stringResource(R.string.home_recorded_title), style = MaterialTheme.typography.titleSmall, color = contentColor, fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.home_recorded_subtitle), style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
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
                    Icon(Icons.Default.Add, stringResource(R.string.home_record_button_desc), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (uiState.completedToday) stringResource(R.string.session_record_another) else stringResource(R.string.home_record_now),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Scheda Attiva ──
        uiState.activeCard?.let { card ->
            SectionTitle(stringResource(R.string.home_active_plan))
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
                            Icon(Icons.Outlined.FitnessCenter, stringResource(R.string.home_card_icon_desc), tint = cardIconColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(card.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                if (card.isAdapted) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            stringResource(R.string.home_optimized_tag),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            val info = listOfNotNull(
                                card.durationWeeks?.let { stringResource(R.string.active_card_weeks_count, it) },
                                card.targetSessions?.let { stringResource(R.string.home_target_sessions, it) }
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
            SectionTitle(stringResource(R.string.home_your_card_title))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.active_card_no_card), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text(stringResource(R.string.active_card_no_card_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Articolo in evidenza ──
        uiState.featuredArticle?.let { article ->
            SectionTitle(stringResource(R.string.home_featured_article))
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
                            Icon(Icons.AutoMirrored.Outlined.Article, stringResource(R.string.home_article_icon_desc), tint = articleIconColor, modifier = Modifier.size(24.dp))
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
            SectionTitle(stringResource(R.string.home_goals_title))
            uiState.activeGoals.take(4).forEach { goal ->
                val nextTarget = when {
                    goal.currentValue < goal.targetValue -> goal.targetValue
                    goal.silverValue != null && goal.currentValue < goal.silverValue -> goal.silverValue
                    goal.goldValue != null && goal.currentValue < goal.goldValue -> goal.goldValue
                    else -> goal.goldValue ?: goal.silverValue ?: goal.targetValue
                }
                val medalLabel = when {
                    goal.currentValue < goal.targetValue -> stringResource(R.string.progress_goal_target_bronze)
                    goal.silverValue != null && goal.currentValue < goal.silverValue -> stringResource(R.string.progress_goal_target_silver)
                    goal.goldValue != null && goal.currentValue < goal.goldValue -> stringResource(R.string.progress_goal_target_gold)
                    else -> stringResource(R.string.progress_goal_completed)
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
                                            goal.goldValue != null && goal.currentValue >= goal.goldValue -> stringResource(R.string.home_milestones_gold)
                                            goal.silverValue != null && goal.currentValue >= goal.silverValue -> stringResource(R.string.home_milestones_silver)
                                            goal.currentValue >= goal.targetValue -> stringResource(R.string.home_milestones_bronze)
                                            else -> ""
                                        }
                                        if (reachedText.isNotEmpty()) {
                                            Text(
                                                stringResource(R.string.home_milestones_prefix) + reachedText,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
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
    completedDates: List<Long>,
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
                        Icon(Icons.Default.ChevronLeft, stringResource(R.string.home_calendar_prev_month), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("$monthName $year", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, 1)
                        currentMonth = newMonth
                    }) {
                        Icon(Icons.Default.ChevronRight, stringResource(R.string.home_calendar_next_month), tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                val isCompleted = completedDates.any { DateUtils.isSameDay(it, dayCal.timeInMillis) }

                                val hasPreviousCompleted = completedDates.any { DateUtils.isSameDay(it, dayCal.timeInMillis - 86400000) }
                                val hasNextCompleted = completedDates.any { DateUtils.isSameDay(it, dayCal.timeInMillis + 86400000) }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Streak highlighting
                                    if (isCompleted) {
                                        // Simplified way to combine shapes for the background "strip"
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp)
                                                .clip(if (!hasPreviousCompleted && !hasNextCompleted) CircleShape 
                                                      else if (!hasPreviousCompleted) RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                                      else if (!hasNextCompleted) RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                                      else RoundedCornerShape(0.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isToday) MaterialTheme.colorScheme.primary 
                                                else if (isCompleted) MaterialTheme.colorScheme.primary
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
                                            color = if (isToday || isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isToday || hasSession || isCompleted) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (hasSession && !isToday && !isCompleted) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .padding(bottom = 4.dp)
                                                    .size(4.dp)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            )
                                        }
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
                Text(stringResource(R.string.home_scheduled_sessions_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onAddMore) {
                    Icon(Icons.Default.Add, stringResource(R.string.home_scheduled_add_desc), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
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
                        Icon(Icons.Default.Delete, stringResource(R.string.home_scheduled_delete_desc), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
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
    var title by remember { mutableStateOf("") }
    val defaultTitle = stringResource(R.string.home_dialog_default_title)
    if (title.isEmpty()) {
        title = defaultTitle
    }
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
        title = { Text(stringResource(R.string.home_dialog_title), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.home_dialog_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.home_dialog_start_date)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Outlined.CalendarMonth, stringResource(R.string.home_dialog_select_date_desc), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text(stringResource(R.string.home_dialog_time)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(stringResource(R.string.home_dialog_recurrence), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        RecurrenceChip(selected = recurrenceType == "NONE", label = stringResource(R.string.home_dialog_recurrence_none), onClick = { recurrenceType = "NONE" })
                    }
                    Box(modifier = Modifier.weight(1.5f)) {
                        RecurrenceChip(selected = recurrenceType == "WEEKLY", label = stringResource(R.string.home_dialog_recurrence_weekly), onClick = { recurrenceType = "WEEKLY" })
                    }
                    Box(modifier = Modifier.weight(1.4f)) {
                        RecurrenceChip(selected = recurrenceType == "EVERY_X_DAYS", label = stringResource(R.string.home_dialog_recurrence_x_days), onClick = { recurrenceType = "EVERY_X_DAYS" })
                    }
                }

                if (recurrenceType == "WEEKLY") {
                    val dayLabels = listOf("L", "M", "M", "G", "V", "S", "D")
                    Text(stringResource(R.string.home_dialog_weekly_days), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        label = { Text(stringResource(R.string.home_dialog_every_x_days_label)) },
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
                    Text(stringResource(R.string.home_dialog_notification_label), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(stringResource(R.string.home_dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.home_dialog_cancel))
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
                    Text(stringResource(R.string.home_dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.home_dialog_cancel))
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
    completedDates: List<Long>,
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
                val isCompleted = completedDates.any { DateUtils.isSameDay(it, day.timeInMillis) }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isToday) MaterialTheme.colorScheme.primary 
                            else if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            else if (hasSession) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
                            else MaterialTheme.colorScheme.surface
                        )
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
                        color = if (isToday || isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        day.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday || isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (hasSession && !isCompleted) {
                        Box(modifier = Modifier.padding(top = 6.dp).size(6.dp).background(if (isToday) Color.White else MaterialTheme.colorScheme.primary, CircleShape))
                    } else if (isCompleted) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(10.dp).padding(top = 2.dp))
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
