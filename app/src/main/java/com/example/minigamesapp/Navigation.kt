package com.example.minigamesapp

import kotlinx.serialization.Serializable

@Serializable object Home
@Serializable data class Reaction(val playerName: String)
@Serializable data class WordGame(val playerName: String)
@Serializable data class Leaderboard(val playerName: String = "")
