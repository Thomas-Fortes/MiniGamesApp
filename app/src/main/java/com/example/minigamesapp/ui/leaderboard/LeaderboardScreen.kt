package com.example.minigamesapp.ui.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

@Composable
fun LeaderboardScreen(
    playerName: String,
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.init(playerName) }

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = "Leaderboard",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // ── Filtres ──
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterButton(
                label     = "Tous",
                selected  = state.filter == LeaderboardViewModel.Filter.ALL,
                onClick   = { viewModel.setFilter(LeaderboardViewModel.Filter.ALL) },
                modifier  = Modifier.weight(1f)
            )
            FilterButton(
                label     = "Réaction",
                selected  = state.filter == LeaderboardViewModel.Filter.REACTION,
                onClick   = { viewModel.setFilter(LeaderboardViewModel.Filter.REACTION) },
                modifier  = Modifier.weight(1f)
            )
            FilterButton(
                label     = "Mot Caché",
                selected  = state.filter == LeaderboardViewModel.Filter.WORD,
                onClick   = { viewModel.setFilter(LeaderboardViewModel.Filter.WORD) },
                modifier  = Modifier.weight(1f)
            )
        }

        // ── Stats personnelles (bonus) ──
        if (playerName.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text  = "Mes stats ($playerName)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text  = "Parties jouées : ${state.gamesCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text  = "Score moyen : ${"%.1f".format(state.avgScore)}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Liste des scores ──
        if (state.isLoading) {
            Text("Chargement…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else if (state.scores.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text  = "Aucun score pour l'instant.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.scores) { index, score ->
                    ScoreRow(rank = index + 1, score = score, isCurrentPlayer = score.playerName == playerName)
                }
            }
        }

        if (!state.isLoading && state.scores.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Boutons ──
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick  = onBackClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Retour")
            }
            Button(
                onClick  = { viewModel.deleteAllScores() },
                colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) {
                Text("Réinitialiser")
            }
        }
    }
}

@Composable
private fun FilterButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) {
            Text(label, fontSize = 12.sp)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) {
            Text(label, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ScoreRow(
    rank: Int,
    score: com.example.minigamesapp.data.Score,
    isCurrentPlayer: Boolean
) {
    val containerColor = if (isCurrentPlayer)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rang
            Text(
                text       = "#$rank",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = when (rank) {
                    1    -> MaterialTheme.colorScheme.primary
                    2    -> MaterialTheme.colorScheme.secondary
                    3    -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier   = Modifier.weight(0.5f)
            )
            // Pseudo + jeu
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text       = score.playerName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                Text(
                    text     = score.gameName,
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Score
            Text(
                text       = "${score.score}",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                modifier   = Modifier.weight(0.8f)
            )
            // Date
            Text(
                text     = dateFormat.format(Date(score.date)),
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}
