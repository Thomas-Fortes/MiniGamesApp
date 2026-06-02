package com.example.minigamesapp.ui.reaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigamesapp.data.AppDatabase
import com.example.minigamesapp.data.Score
import com.example.minigamesapp.data.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.random.Random

class ReactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(AppDatabase.getDatabase(application).scoreDao())
    private var playerName: String = ""

    enum class Phase { IDLE, PLAYING, RESULT }

    data class UiState(
        val phase: Phase = Phase.IDLE,
        val target: Long = 0L,
        val elapsed: Long = 0L,
        val step: Long = 10L,
        val gap: Long = 0L,
        val nearTarget: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /** Génère une partie aléatoire et lance immédiatement le timer. */
    fun startGame(playerName: String) {
        this.playerName = playerName
        timerJob?.cancel()
        val speedFactor = Random.nextDouble(0.5, 3.0)
        val direction   = if (Random.nextBoolean()) 1L else -1L
        val step = direction * (speedFactor * 10).toLong().coerceAtLeast(5L)

        // start et target générés de façon à ce que la cible soit toujours atteignable
        val (start, target) = if (direction > 0L) {
            // Timer croissant : start < target
            val s = Random.nextLong(0L, 15_000L)
            s to s + Random.nextLong(5_000L, 20_000L)
        } else {
            // Timer décroissant : start > target
            val t = Random.nextLong(0L, 15_000L)
            t + Random.nextLong(5_000L, 20_000L) to t
        }

        _uiState.value = UiState(
            phase   = Phase.PLAYING,
            target  = target,
            elapsed = start,
            step    = step
        )
        launchTimer()
    }

    private fun launchTimer() {
        var tickCount = 0
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.phase == Phase.PLAYING) {
                delay(10L)
                tickCount++
                _uiState.update { state ->
                    if (state.phase != Phase.PLAYING) return@update state
                    // Bonus vitesse variable : changement aléatoire toutes les ~5 s (500 ticks × 10 ms)
                    val newStep = if (tickCount % 500 == 0) {
                        val sf = Random.nextDouble(0.5, 3.0)
                        if (state.step >= 0L) (sf * 10).toLong().coerceAtLeast(5L)
                        else -((sf * 10).toLong().coerceAtLeast(5L))
                    } else state.step

                    val newElapsed = state.elapsed + newStep
                    state.copy(
                        elapsed    = newElapsed,
                        step       = newStep,
                        // Bonus timer aveugle : masquer quand à moins de 1 500 ms de la cible
                        nearTarget = abs(newElapsed - state.target) < 1_500L
                    )
                }
            }
        }
    }

    /** Stoppe le timer et calcule l'écart. */
    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { state ->
            state.copy(
                phase = Phase.RESULT,
                gap   = abs(state.elapsed - state.target)
            )
        }
        saveScore()
    }

    private fun saveScore() {
        val state = _uiState.value
        val scoreValue = maxOf(0, 10_000 - state.gap.toInt())
        viewModelScope.launch {
            repository.insertScore(
                Score(
                    playerName = playerName,
                    gameName   = "Réaction",
                    score      = scoreValue
                )
            )
        }
    }

    /** Retour à l'état initial (IDLE). */
    fun reset() {
        timerJob?.cancel()
        _uiState.value = UiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
