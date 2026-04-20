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
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.FitlyBlueLight
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.SageGreenLight
import com.afa.fitadapt.ui.theme.SoftAmber
import com.afa.fitadapt.ui.theme.SoftAmberLight

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
import com.afa.fitadapt.data.local.entity.ScheduledSessionEntity
import com.afa.fitadapt.data.local.entity.TrainingCardEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Schermata Home — dashboard principale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToSession: () -> Unit,
    onNavigateToCard: () -> Unit,
    onNavigateToArticle: (Long) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var isMonthlyView by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Header and CTA stay same) ...
        // ── Header con gradiente ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyBlue, FitlyBlue)
                    ),
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(24.dp)
                .padding(top = 16.dp)
        ) {
            Column {
                Text(
                    text = "Buongiorno! 👋",
                    style = MaterialTheme.typography.headlineLarge,
                    color = FitlyBlueLight
                )
                if (uiState.patientCode.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Codice: ${uiState.patientCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FitlyBlueLight.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Streak e statistiche rapide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBadge(
                        icon = Icons.Outlined.LocalFireDepartment,
                        value = "${uiState.currentStreak}",
                        label = "Streak",
                        color = SoftAmber
                    )
                    StatBadge(
                        icon = Icons.Outlined.FitnessCenter,
                        value = "${uiState.totalCompleted}",
                        label = "Sessioni",
                        color = SageGreen,
                        onClick = onNavigateToHistory
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Calendario Allenamenti ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(if (isMonthlyView) "Calendario Mensile" else "Calendario Settimanale", modifier = Modifier.padding(horizontal = 0.dp))
            Row {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi programmato", tint = FitlyBlue)
                }
                IconButton(onClick = { isMonthlyView = !isMonthlyView }) {
                    Icon(
                        if (isMonthlyView) Icons.Outlined.ViewWeek else Icons.Outlined.CalendarMonth,
                        contentDescription = "Cambia vista",
                        tint = FitlyBlue
                    )
                }
            }
        }
        
    if (isMonthlyView) {
        MonthlyCalendar(
            scheduled = uiState.scheduledSessions,
            onDayClick = { date ->
                selectedDateMillis = date
                showAddDialog = true
            },
            onDeleteSession = { homeViewModel.deleteScheduledSession(it) }
        )
    } else {
        WeeklyCalendar(
            scheduled = uiState.scheduledSessions,
            onDeleteSession = { homeViewModel.deleteScheduledSession(it) }
        )
    }

        Spacer(modifier = Modifier.height(20.dp))

        // ── CTA Registra Sessione ──
        if (!uiState.completedToday && uiState.activeCard != null) {
            Button(
                onClick = onNavigateToSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "Registra", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registra allenamento di oggi", style = MaterialTheme.typography.titleMedium)
            }
        } else if (uiState.completedToday) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreenLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Allenamento registrato!", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                        Text("Ottimo lavoro, continua così!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Scheda Attiva ──
        uiState.activeCard?.let { card ->
            SectionTitle("La tua scheda attiva")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onNavigateToCard() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FitlyBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.FitnessCenter, "Scheda", tint = NavyBlue, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(card.title, style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                            card.durationWeeks?.let {
                                Text("Durata: $it settimane", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    card.targetSessions?.let { target ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Obiettivo: $target sessioni",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } ?: run {
            // Nessuna scheda attiva
            SectionTitle("La tua scheda")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftAmberLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Nessuna scheda attiva", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                        Text("Chiedi al tuo operatore di configurare una scheda", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Articolo in evidenza ──
        uiState.featuredArticle?.let { article ->
            SectionTitle("Consiglio della settimana")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onNavigateToArticle(article.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FitlyBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.Article, "Articolo", tint = NavyBlue, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(article.title, style = MaterialTheme.typography.titleSmall, color = NavyBlue, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
                val progress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).coerceIn(0f, 1f) else 0f
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(goal.title, style = MaterialTheme.typography.bodyMedium, color = NavyBlue)
                            Text("${goal.currentValue.toInt()} / ${goal.targetValue.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = if (progress >= 1f) SageGreen else FitlyBlue, fontWeight = FontWeight.Bold)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(16.dp)
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
                        Icon(Icons.Default.ChevronLeft, "Mese precedente")
                    }
                    Text("$monthName $year", style = MaterialTheme.typography.titleMedium, color = NavyBlue)
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, 1)
                        currentMonth = newMonth
                    }) {
                        Icon(Icons.Default.ChevronRight, "Mese successivo")
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                            if (isToday) FitlyBlue 
                                            else if (hasSession) FitlyBlueLight.copy(alpha = 0.5f) 
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
                                        color = if (isToday) Color.White else NavyBlue,
                                        fontWeight = if (isToday || hasSession) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (hasSession && !isToday) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(bottom = 4.dp)
                                                .size(4.dp)
                                                .background(FitlyBlue, CircleShape)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Allenamenti programmati", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                IconButton(onClick = onAddMore) {
                    Icon(Icons.Default.Add, "Aggiungi", tint = FitlyBlue, modifier = Modifier.size(20.dp))
                }
            }
            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.FitnessCenter, null, tint = FitlyBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(session.title, style = MaterialTheme.typography.bodyMedium, color = NavyBlue)
                        Text(session.startTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onDelete(session) }) {
                        Icon(Icons.Default.Delete, "Elimina", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
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

    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(selectedDateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programma Allenamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { },
                    label = { Text("Data di inizio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Outlined.CalendarMonth, "Seleziona data")
                        }
                    }
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Orario (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Ripetizione", style = MaterialTheme.typography.titleSmall, color = NavyBlue)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecurrenceChip(selected = recurrenceType == "NONE", label = "Singolo", onClick = { recurrenceType = "NONE" })
                    RecurrenceChip(selected = recurrenceType == "WEEKLY", label = "Settimanale", onClick = { recurrenceType = "WEEKLY" })
                    RecurrenceChip(selected = recurrenceType == "EVERY_X_DAYS", label = "Ogni X gg", onClick = { recurrenceType = "EVERY_X_DAYS" })
                }

                if (recurrenceType == "EVERY_X_DAYS") {
                    OutlinedTextField(
                        value = recurrenceValue.toString(),
                        onValueChange = { recurrenceValue = it.toIntOrNull() ?: 1 },
                        label = { Text("Ogni quanti giorni?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notifica promemoria", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = notificationEnabled, onCheckedChange = { notificationEnabled = it })
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
                            recurrenceValue = if (recurrenceType == "WEEKLY") {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = selectedDateMillis
                                cal.get(Calendar.DAY_OF_WEEK)
                            } else recurrenceValue,
                            cardId = activeCard?.id
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitlyBlue)
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
fun WeeklyCalendar(
    scheduled: List<ScheduledSessionEntity>,
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isToday) FitlyBlue else if (hasSession) FitlyBlueLight else MaterialTheme.colorScheme.surface)
                        .clickable {
                            if (hasSession) {
                                selectedDaySessions = daySessions
                                showDayDetails = true
                            }
                        }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ITALY) ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) Color.White else NavyBlue
                    )
                    Text(
                        day.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) Color.White else NavyBlue
                    )
                    if (hasSession) {
                        Box(modifier = Modifier.padding(top = 4.dp).size(4.dp).background(if (isToday) Color.White else FitlyBlue, CircleShape))
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
                onAddMore = { showDayDetails = false }
            )
        }
    }
}

@Composable
fun RecurrenceChip(selected: Boolean, label: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 10.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FitlyBlue,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = NavyBlue,
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
        Text(value, style = MaterialTheme.typography.titleLarge, color = FitlyBlueLight, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = FitlyBlueLight.copy(alpha = 0.7f))
    }
}
