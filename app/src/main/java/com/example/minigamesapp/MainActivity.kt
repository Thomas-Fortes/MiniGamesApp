package com.example.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.minigamesapp.ui.home.HomeScreen
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

    NavHost(
        navController    = navController,
        startDestination = Home,
        modifier         = modifier
    ) {
        composable<Home> { _ ->
            HomeScreen(
                onReactionClick = { navController.navigate(Reaction) },
                onWordGameClick = { navController.navigate(WordGame) }
            )
        }
        composable<Reaction> { _ ->
            ReactionScreen(onBackClick = { navController.popBackStack() })
        }
        composable<WordGame> { _ ->
            WordGameScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
