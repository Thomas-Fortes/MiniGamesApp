package com.example.minigamesapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigamesapp.data.AppDatabase
import com.example.minigamesapp.data.Score
import com.example.minigamesapp.data.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(AppDatabase.getDatabase(application).scoreDao())

    enum class Filter { ALL, REACTION, WORD }

    data class UiState(
        val scores: List<Score> = emptyList(),
        val filter: Filter = Filter.ALL,
        val gamesCount: Int = 0,
        val avgScore: Double = 0.0,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var playerName: String = ""

    fun init(playerName: String) {
        this.playerName = playerName
        loadScores()
        if (playerName.isNotBlank()) loadPlayerStats()
    }

    fun setFilter(filter: Filter) {
        _uiState.update { it.copy(filter = filter) }
        loadScores()
    }

    private fun loadScores() {
        viewModelScope.launch {
            val scores = when (_uiState.value.filter) {
                Filter.ALL      -> repository.getTopScores()
                Filter.REACTION -> repository.getTopScoresByGame("Réaction")
                Filter.WORD     -> repository.getTopScoresByGame("Mot Caché")
            }
            _uiState.update { it.copy(scores = scores, isLoading = false) }
        }
    }

    private fun loadPlayerStats() {
        viewModelScope.launch {
            val count = repository.getGamesCountByPlayer(playerName)
            val avg   = repository.getAvgScoreByPlayer(playerName)
            _uiState.update { it.copy(gamesCount = count, avgScore = avg) }
        }
    }

    fun deleteAllScores() {
        viewModelScope.launch {
            repository.deleteAllScores()
            loadScores()
            if (playerName.isNotBlank()) loadPlayerStats()
        }
    }
}
