package com.example.minigamesapp.ui.wordgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

// ─── Entrée ──────────────────────────────────────────────────────────────────

@Composable
fun WordGameScreen(
    onBackClick: () -> Unit,
    viewModel: WordGameViewModel = viewModel()
) {
    // Lance une nouvelle partie dès que l'écran entre en composition
    LaunchedEffect(Unit) { viewModel.startGame() }

    val state by viewModel.uiState.collectAsState()

    when (state.phase) {
        WordGameViewModel.Phase.PLAYING   -> PlayingContent(state, viewModel, onBackClick)
        WordGameViewModel.Phase.GAME_OVER -> GameOverContent(state, onBackClick, { viewModel.reset() })
    }
}

// ─── Phase PLAYING ───────────────────────────────────────────────────────────

@Composable
private fun PlayingContent(
    state: WordGameViewModel.UiState,
    viewModel: WordGameViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Timer + Score ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.timeLeft <= 10) MaterialTheme.colorScheme.error
                                     else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "${state.timeLeft} s",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (state.timeLeft <= 10) MaterialTheme.colorScheme.onError
                            else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Score : ${state.score}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // ── Indice sur le nombre de lettres ──
        Text(
            text  = "Mot de ${state.wordLength} lettres",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ── Zone de saisie ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.showError) MaterialTheme.colorScheme.errorContainer
                                 else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = state.currentWord.ifEmpty { "Appuyez sur les lettres…" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (state.currentWord.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                            else if (state.showError) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onSurface
                )
                if (state.currentWord.isNotEmpty()) {
                    TextButton(onClick = { viewModel.eraseLast() }) {
                        Text(
                            text     = "⌫",
                            fontSize = 22.sp,
                            color    = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (state.showError) {
            Text(
                text  = "Mot incorrect – réessayez !",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        // ── Grille 3×3 ──
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.grid.chunked(3).forEachIndexed { rowIdx, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEachIndexed { colIdx, cell ->
                        val idx = rowIdx * 3 + colIdx
                        LetterCell(cell = cell, onClick = { viewModel.selectCell(idx) })
                    }
                }
            }
        }

        // ── Bouton indice (bonus) ──
        if (!state.hintUsed) {
            TextButton(onClick = { viewModel.hint() }) {
                Text(
                    text  = "Indice (−1 pt)",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 13.sp
                )
            }
        } else {
            Text(
                text  = "Indice utilisé",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // ── Boutons d'action ──
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick  = { viewModel.validate() },
                enabled  = state.currentWord.length == state.wordLength
            ) {
                Text("Valider")
            }
            OutlinedButton(onClick = { viewModel.pass() }) {
                Text("Passer")
            }
            OutlinedButton(onClick = onBack) {
                Text("Retour")
            }
        }
    }
}

// ─── Cellule de la grille ────────────────────────────────────────────────────

@Composable
private fun LetterCell(cell: WordGameViewModel.Cell, onClick: () -> Unit) {
    val containerColor = when {
        cell.isSelected -> MaterialTheme.colorScheme.surfaceVariant
        cell.isHinted   -> Color(0xFFFFF9C4)            // jaune doux
        else            -> MaterialTheme.colorScheme.primaryContainer
    }
    val textColor = when {
        cell.isSelected -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        cell.isHinted   -> Color(0xFFBF360C)            // orange foncé
        else            -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Button(
        onClick         = onClick,
        enabled         = !cell.isSelected,
        modifier        = Modifier.size(80.dp),
        shape           = RoundedCornerShape(12.dp),
        contentPadding  = PaddingValues(0.dp),
        colors          = ButtonDefaults.buttonColors(
            containerColor         = containerColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text       = cell.char.toString(),
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = textColor
        )
    }
}

// ─── Phase GAME_OVER ─────────────────────────────────────────────────────────

@Composable
private fun GameOverContent(
    state: WordGameViewModel.UiState,
    onBack: () -> Unit,
    onReplay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = "Partie terminée !",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Mots trouvés", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text       = "${state.score}",
                    fontSize   = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text  = "Meilleur score de la session : ${state.bestScore}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Étoiles (bonus visuel)
        val stars = when {
            state.score >= 8 -> "⭐⭐⭐"
            state.score >= 5 -> "⭐⭐"
            state.score >= 2 -> "⭐"
            else             -> ""
        }
        if (stars.isNotEmpty()) {
            Text(stars, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onReplay) { Text("Rejouer") }
            OutlinedButton(onClick = onBack) { Text("Accueil") }
        }
    }
}
