package com.example.minigamesapp.ui.reaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun formatMs(ms: Long): String {
    val absMs = abs(ms)
    val s     = absMs / 1000
    val ms3   = absMs % 1000
    return if (ms < 0) "-%d.%03d s".format(s, ms3) else "%d.%03d s".format(s, ms3)
}

private fun feedbackMessage(gapMs: Long): String = when {
    gapMs < 100    -> "Parfait !"
    gapMs < 300    -> "Excellent !"
    gapMs < 600    -> "Très bien !"
    gapMs < 1_000  -> "Bien !"
    gapMs < 2_000  -> "Pas mal…"
    gapMs < 5_000  -> "Peut mieux faire"
    else           -> "À retravailler !"
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun ReactionScreen(
    playerName: String,
    onBackClick: () -> Unit,
    viewModel: ReactionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.phase) {
            ReactionViewModel.Phase.IDLE    -> IdlePhase(
                onStart = { viewModel.startGame(playerName) },
                onBack  = onBackClick
            )
            ReactionViewModel.Phase.PLAYING -> PlayingPhase(
                state   = state,
                onStop  = { viewModel.stopTimer() },
                onBack  = { viewModel.reset(); onBackClick() }
            )
            ReactionViewModel.Phase.RESULT  -> ResultPhase(
                state    = state,
                onReplay = { viewModel.startGame(playerName) },
                onBack   = { viewModel.reset(); onBackClick() }
            )
        }
    }
}

// ─── Phases ──────────────────────────────────────────────────────────────────

@Composable
private fun IdlePhase(onStart: () -> Unit, onBack: () -> Unit) {
    Text(
        text       = "Jeu de Réaction",
        fontSize   = 28.sp,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text  = "Arrêtez le timer au plus près de la valeur cible.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    Button(
        onClick  = onStart,
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        Text("Démarrer", fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(onClick = onBack) { Text("Retour") }
}

@Composable
private fun PlayingPhase(
    state: ReactionViewModel.UiState,
    onStop: () -> Unit,
    onBack: () -> Unit
) {
    // Cible
    Text("Cible", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(
        text       = formatMs(state.target),
        fontSize   = 28.sp,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Timer
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Timer", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                // Bonus timer aveugle
                text       = if (state.nearTarget) "???.??? s" else formatMs(state.elapsed),
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color      = if (state.nearTarget) MaterialTheme.colorScheme.error
                             else MaterialTheme.colorScheme.onSurface
            )
            if (state.nearTarget) {
                Text(
                    "Zone aveugle !",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    val direction = if (state.step >= 0L) "↑ croissant" else "↓ décroissant"
    Text("Sens : $direction", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick  = onStop,
        modifier = Modifier.fillMaxWidth(0.6f),
        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
    ) {
        Text("Stop !", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(onClick = onBack) { Text("Retour") }
}

@Composable
private fun ResultPhase(
    state: ReactionViewModel.UiState,
    onReplay: () -> Unit,
    onBack: () -> Unit
) {
    Text(
        text       = "Résultat",
        fontSize   = 28.sp,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResultRow("Cible",   formatMs(state.target))
            ResultRow("Atteint", formatMs(state.elapsed))
            ResultRow(
                label      = "Écart",
                value      = formatMs(state.gap),
                valueColor = when {
                    state.gap < 300    -> Color(0xFF2E7D32)
                    state.gap < 1_000  -> Color(0xFFF57F17)
                    else               -> MaterialTheme.colorScheme.error
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text       = feedbackMessage(state.gap),
        fontSize   = 24.sp,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.secondary
    )
    Spacer(modifier = Modifier.height(32.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onReplay)          { Text("Rejouer") }
        OutlinedButton(onClick = onBack)    { Text("Accueil") }
    }
}

@Composable
private fun ResultRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
