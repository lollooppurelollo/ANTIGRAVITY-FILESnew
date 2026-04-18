// =============================================================
// AFA - Attività Fisica Adattata
// Schermata: Setup Wizard (primo avvio)
// =============================================================
package com.afa.fitadapt.ui.auth

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.afa.fitadapt.ui.theme.FitlyBlue
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.FitlyBlueLight
import com.afa.fitadapt.ui.theme.SageGreen
import com.afa.fitadapt.ui.theme.TextSecondary

/**
 * Wizard di configurazione iniziale (3 step).
 * Step 0: Imposta password sezione protetta
 * Step 1: Inserisci codice paziente
 * Step 2: Conferma e completa
 */
@Composable
fun SetupWizardScreen(
    authViewModel: AuthViewModel,
    onSetupComplete: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.setupCompleted) {
        if (uiState.setupCompleted) onSetupComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Titolo rebranding Fitly
        Text(
            text = "Benvenuta in Fitly",
            style = MaterialTheme.typography.headlineLarge,
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Personalizziamo la tua esperienza",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Indicatore step
        StepIndicator(currentStep = uiState.setupStep)

        Spacer(modifier = Modifier.height(32.dp))

        // Contenuto dello step corrente
        AnimatedContent(
            targetState = uiState.setupStep,
            label = "setupStep"
        ) { step ->
            when (step) {
                0 -> PasswordStep(
                    error = uiState.passwordError,
                    onSetPassword = { pwd, confirm ->
                        authViewModel.setPassword(pwd, confirm)
                    }
                )
                1 -> PatientCodeStep(
                    onSetCode = { code -> authViewModel.setPatientCode(code) },
                    onBack = { authViewModel.previousSetupStep() }
                )
                2 -> ConfirmStep(
                    onConfirm = { authViewModel.completeSetup() },
                    onBack = { authViewModel.previousSetupStep() }
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..2) {
            val isCurrent = i == currentStep
            val isDone = i < currentStep
            val color = when {
                isDone -> SageGreen
                isCurrent -> FitlyBlue
                else -> FitlyBlueLight
            }

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 32.dp else 24.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Default.Check, "Completato", tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("${i + 1}", color = if (isCurrent) Color.White else NavyBlue, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (i < 2) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(32.dp).height(2.dp).background(if (isDone) SageGreen else FitlyBlueLight))
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun PasswordStep(
    error: String?,
    onSetPassword: (String, String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Default.Lock, "Password", tint = NavyBlue, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Crea una password", style = MaterialTheme.typography.titleLarge, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Questa password protegge la sezione di configurazione dell'app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, "Mostra password")
                    }
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                singleLine = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onSetPassword(password, confirmPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                Text("Avanti")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Avanti")
            }
        }
    }
}

@Composable
private fun PatientCodeStep(
    onSetCode: (String) -> Unit,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Default.Person, "Codice", tint = NavyBlue, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Codice paziente", style = MaterialTheme.typography.titleLarge, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Inserisci il codice assegnato dal tuo operatore sanitario. Questo codice identifica i tuoi dati in modo anonimo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Codice (es. AFA-0001)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onBack) { Text("Indietro") }
                Button(onClick = { onSetCode(code) }, enabled = code.isNotBlank()) {
                    Text("Avanti")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Avanti")
                }
            }
        }
    }
}

@Composable
private fun ConfirmStep(
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(SageGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, "OK", tint = Color.White, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Tutto pronto!", style = MaterialTheme.typography.titleLarge, color = NavyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "La configurazione di Fitly è terminata. Inizia subito il tuo percorso!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FitlyBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Entra in Fitly", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Indietro", color = TextSecondary) }
        }
    }
}
