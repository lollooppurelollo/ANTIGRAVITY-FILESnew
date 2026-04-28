// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Scheda attiva
// =============================================================
package com.afa.fitadapt.ui.card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.ui.components.VideoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Schermata della scheda attiva.
 * Mostra la lista degli esercizi con i parametri personalizzati.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveCardScreen(
    cardViewModel: CardViewModel,
    themeViewModel: com.afa.fitadapt.ui.theme.ThemeViewModel = hiltViewModel(),
    onExerciseClick: (exerciseId: Long, cardExerciseId: Long) -> Unit
) {
    val uiState by cardViewModel.cardState.collectAsState()
    val useOriginalColors by themeViewModel.useOriginalColors.collectAsState()
    val guidedTrainingMode by cardViewModel.guidedTrainingMode.collectAsState(initial = false)

    if (uiState.noActiveCard) {
        // ... (resto del codice per nessuna scheda attiva)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📋", style = MaterialTheme.typography.displaySmall)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Nessuna scheda attiva",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Chiedi al tuo operatore di attivare una scheda dalla sezione protetta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val cardWithExercises = uiState.cardWithExercises ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (guidedTrainingMode) "Allenamento Guidato" else "Scheda Attiva",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { cardViewModel.toggleGuidedTrainingMode() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (guidedTrainingMode) Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
                            contentDescription = "Cambia Vista",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (guidedTrainingMode) {
                GuidedTrainingView(
                    cardTitle = cardWithExercises.card.title,
                    cardExercises = cardWithExercises.cardExercises.sortedBy { it.orderIndex },
                    exerciseDetails = uiState.exerciseDetails,
                    useOriginalColors = useOriginalColors
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Header della scheda - Floating Gradient Card
                    item {
                        val headerColor = MaterialTheme.colorScheme.primary
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
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
                                Column {
                                    Text(
                                        text = cardWithExercises.card.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        cardWithExercises.card.targetSessions?.let { target ->
                                            InfoChip(
                                                icon = Icons.Outlined.FitnessCenter,
                                                text = "${uiState.completedSessionsCount}/$target sessioni",
                                                isLight = true,
                                                useOriginalColors = useOriginalColors
                                            )
                                        }
                                        cardWithExercises.card.durationWeeks?.let { weeks ->
                                            InfoChip(
                                                icon = Icons.Outlined.Timer,
                                                text = "$weeks settimane",
                                                isLight = true,
                                                useOriginalColors = useOriginalColors
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "${cardWithExercises.cardExercises.size} esercizi in programma",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Esercizi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    itemsIndexed(
                        cardWithExercises.cardExercises.sortedBy { it.orderIndex }
                    ) { index, cardExercise ->
                        val exercise = uiState.exerciseDetails[cardExercise.exerciseId]
                        if (exercise != null) {
                            ExerciseListItem(
                                index = index + 1,
                                exercise = exercise,
                                cardExercise = cardExercise,
                                useOriginalColors = useOriginalColors,
                                onClick = { onExerciseClick(exercise.id, cardExercise.id) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun GuidedTrainingView(
    cardTitle: String,
    cardExercises: List<CardExerciseEntity>,
    exerciseDetails: Map<Long, ExerciseEntity>,
    useOriginalColors: Boolean
) {
    val sortedExercises = remember(cardExercises) { cardExercises.sortedBy { it.orderIndex } }
    val pagerState = rememberPagerState(pageCount = { sortedExercises.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con progresso
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                cardTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / sortedExercises.size },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Text(
                "Esercizio ${pagerState.currentPage + 1} di ${sortedExercises.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 16.dp,
            verticalAlignment = Alignment.Top
        ) { page ->
            val cardExercise = sortedExercises[page]
            val exercise = exerciseDetails[cardExercise.exerciseId]
            if (exercise != null) {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    GuidedExerciseCard(
                        index = page + 1,
                        exercise = exercise,
                        cardExercise = cardExercise,
                        useOriginalColors = useOriginalColors,
                        onFinished = {
                            if (page < sortedExercises.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(page + 1)
                                }
                            }
                        }
                    )
                }
            }
        }
        
        // Footer navigation
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    enabled = pagerState.currentPage > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Precedente")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    enabled = pagerState.currentPage < sortedExercises.size - 1,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Successivo")
                }
            }
        }
    }
}


@Composable
fun GuidedExerciseCard(
    index: Int,
    exercise: ExerciseEntity,
    cardExercise: CardExerciseEntity,
    useOriginalColors: Boolean,
    onFinished: () -> Unit = {}
) {
    val duration = cardExercise.customDurationSec ?: exercise.defaultDurationSec
    val reps = cardExercise.customRepetitions ?: exercise.defaultRepetitions
    val intensity = cardExercise.customIntensity ?: exercise.defaultIntensity

    var isRunning by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp).animateContentSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$index",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Video Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black)
            ) {
                VideoPlayer(
                    videoUri = exercise.videoUri,
                    isPlaying = isRunning
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (reps != null) {
                        Text(
                            "$reps ripetizioni",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Intensità: $intensity",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (duration != null) {
                    TimerComponent(
                        durationSeconds = duration,
                        onRunningChange = { isRunning = it },
                        onFinished = onFinished
                    )
                } else {
                    // Pulsante Fatto per esercizi a ripetizioni
                    Button(
                        onClick = { 
                            if (isRunning) {
                                isRunning = false
                                onFinished()
                            } else {
                                isRunning = true
                            }
                        },
                        modifier = Modifier.height(56.dp).padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (isRunning) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            if (isRunning) Icons.Default.Check else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isRunning) "Completato" else "Inizia",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            
            if (exercise.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Istruzioni",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimerComponent(
    durationSeconds: Int,
    onRunningChange: (Boolean) -> Unit = {},
    onFinished: () -> Unit = {}
) {
    var timeLeft by remember { mutableIntStateOf(durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeLeft) {
        onRunningChange(isRunning)
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        } else if (timeLeft == 0 && isRunning) {
            isRunning = false
            onRunningChange(false)
            onFinished()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%02d:%02d", timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.width(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pausa" else "Avvia",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        if (timeLeft < durationSeconds) {
            IconButton(
                onClick = { 
                    timeLeft = durationSeconds
                    isRunning = false
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExerciseListItem(
    index: Int,
    exercise: ExerciseEntity,
    cardExercise: CardExerciseEntity,
    useOriginalColors: Boolean,
    onClick: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numero ordine (Ripristinato come cerchio semplice)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info esercizio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Parametri (custom > default)
                val duration = cardExercise.customDurationSec ?: exercise.defaultDurationSec
                val reps = cardExercise.customRepetitions ?: exercise.defaultRepetitions
                val intensity = cardExercise.customIntensity ?: exercise.defaultIntensity

                val params = buildList {
                    duration?.let { add("${it / 60} min") }
                    reps?.let { add("$it rip.") }
                    add(intensity)
                }
                Text(
                    params.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Freccia
            Icon(
                Icons.Outlined.PlayArrow,
                "Dettaglio",
                tint = accentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    isLight: Boolean = false,
    useOriginalColors: Boolean = false
) {
    val contentColor = if (isLight) {
        if (useOriginalColors) Color.White else MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val bgColor = if (isLight) {
        if (useOriginalColors) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = contentColor
        )
    }
}
