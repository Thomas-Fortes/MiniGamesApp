package com.example.minigamesapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    onReactionClick: (String) -> Unit,
    onWordGameClick: (String) -> Unit,
    onLeaderboardClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = "MiniGames",
            fontSize   = 40.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text  = "Choisissez un jeu",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value         = playerName,
            onValueChange = onPlayerNameChange,
            label         = { Text("Votre pseudo") },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(0.85f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick  = { onReactionClick(playerName) },
            enabled  = playerName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Text("Jeu de Réaction", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick  = { onWordGameClick(playerName) },
            enabled  = playerName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Text("Mot Caché", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick  = { onLeaderboardClick(playerName) },
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Text("Leaderboard", fontSize = 16.sp)
        }
    }
}
