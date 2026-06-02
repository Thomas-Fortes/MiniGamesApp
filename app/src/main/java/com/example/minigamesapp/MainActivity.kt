package com.example.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.minigamesapp.ui.home.HomeScreen
import com.example.minigamesapp.ui.leaderboard.LeaderboardScreen
import com.example.minigamesapp.ui.reaction.ReactionScreen
import com.example.minigamesapp.ui.wordgame.WordGameScreen
import com.example.minigamesapp.ui.theme.MiniGamesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniGamesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MiniGamesApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MiniGamesApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // Pseudo partagé : persiste pendant toute la session, pré-remplit le champ au retour
    var playerName by remember { mutableStateOf("") }

    NavHost(
        navController    = navController,
        startDestination = Home,
        modifier         = modifier
    ) {
        composable<Home> { _ ->
            HomeScreen(
                playerName         = playerName,
                onPlayerNameChange = { playerName = it },
                onReactionClick    = { navController.navigate(Reaction(playerName)) },
                onWordGameClick    = { navController.navigate(WordGame(playerName)) },
                onLeaderboardClick = { navController.navigate(Leaderboard(playerName)) }
            )
        }
        composable<Reaction> { entry ->
            val route = entry.toRoute<Reaction>()
            ReactionScreen(playerName = route.playerName, onBackClick = { navController.popBackStack() })
        }
        composable<WordGame> { entry ->
            val route = entry.toRoute<WordGame>()
            WordGameScreen(playerName = route.playerName, onBackClick = { navController.popBackStack() })
        }
        composable<Leaderboard> { entry ->
            val route = entry.toRoute<Leaderboard>()
            LeaderboardScreen(playerName = route.playerName, onBackClick = { navController.popBackStack() })
        }
    }
}
