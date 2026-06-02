package com.example.minigamesapp.ui.wordgame

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

class WordGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(AppDatabase.getDatabase(application).scoreDao())
    private var playerName: String = ""

    enum class Phase { PLAYING, GAME_OVER }

    /** Une cellule de la grille 3×3. */
    data class Cell(
        val char: Char,
        val isSelected: Boolean = false,
        /** Vrai quand l'indice a révélé cette lettre. */
        val isHinted: Boolean = false
    )

    data class UiState(
        val phase: Phase          = Phase.PLAYING,
        val grid: List<Cell>      = emptyList(),
        val selectedIndices: List<Int> = emptyList(),
        val currentWord: String   = "",
        val targetWord: String    = "",
        val wordLength: Int       = 0,
        val score: Int            = 0,
        val timeLeft: Int         = 60,
        val bestScore: Int        = 0,
        val hintUsed: Boolean     = false,
        val showError: Boolean    = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionBest: Int = 0

    // ─── Liste de mots ───────────────────────────────────────────────────────

    private val wordList = listOf(
        "SOLEIL", "MAISON", "JARDIN", "CHEMIN", "BOUTON",
        "MIROIR", "PLANTE", "CARTON", "FUSEAU", "CITRON",
        "VIOLON", "RAPIDE", "BLOQUE", "MOUTON", "GATEAU",
        "FLECHE", "BRETON", "MARCHE", "TEMPLE", "BALCON"
    )

    // ─── Cycle de vie ────────────────────────────────────────────────────────

    /** Démarre le timer et charge la première grille. */
    fun startGame(playerName: String) {
        this.playerName = playerName
        timerJob?.cancel()
        _uiState.value = UiState(bestScore = sessionBest)
        loadNewGrid()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.timeLeft > 0 && _uiState.value.phase == Phase.PLAYING) {
                delay(1_000L)
                val newTime = _uiState.value.timeLeft - 1
                _uiState.update { it.copy(timeLeft = newTime) }
                if (newTime <= 0) {
                    endGame()
                    break
                }
            }
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        val final = _uiState.value.score
        if (final > sessionBest) sessionBest = final
        _uiState.update { it.copy(phase = Phase.GAME_OVER, bestScore = sessionBest) }
        saveScore()
    }

    private fun saveScore() {
        val scoreValue = _uiState.value.score
        viewModelScope.launch {
            repository.insertScore(
                Score(
                    playerName = playerName,
                    gameName   = "Mot Caché",
                    score      = scoreValue
                )
            )
        }
    }

    /** Repart pour une nouvelle partie complète. */
    fun reset() = startGame(playerName)

    // ─── Gestion de la grille ────────────────────────────────────────────────

    private fun loadNewGrid() {
        val word    = wordList.random()
        val letters = word.toMutableList()
        val used    = letters.toMutableSet()
        val alphabet = ('A'..'Z').toList()
        repeat(3) {
            var c: Char
            do { c = alphabet.random() } while (c in used)
            letters += c
            used += c
        }
        letters.shuffle()
        _uiState.update { state ->
            state.copy(
                grid            = letters.map { Cell(it) },
                selectedIndices = emptyList(),
                currentWord     = "",
                targetWord      = word,
                wordLength      = word.length,
                hintUsed        = false,
                showError       = false
            )
        }
    }

    // ─── Actions joueur ──────────────────────────────────────────────────────

    /** Sélectionne la cellule à [index] et ajoute sa lettre au mot courant. */
    fun selectCell(index: Int) {
        val state = _uiState.value
        if (state.phase != Phase.PLAYING) return
        if (index !in state.grid.indices)  return
        if (state.grid[index].isSelected)  return

        val newGrid    = state.grid.toMutableList().also { it[index] = it[index].copy(isSelected = true) }
        val newIndices = state.selectedIndices + index
        val newWord    = newIndices.joinToString("") { state.grid[it].char.toString() }
        _uiState.update { it.copy(grid = newGrid, selectedIndices = newIndices, currentWord = newWord, showError = false) }
    }

    /** Retire la dernière lettre sélectionnée et réactive la cellule. */
    fun eraseLast() {
        val state = _uiState.value
        if (state.selectedIndices.isEmpty()) return

        val last       = state.selectedIndices.last()
        val newGrid    = state.grid.toMutableList().also { it[last] = it[last].copy(isSelected = false, isHinted = false) }
        val newIndices = state.selectedIndices.dropLast(1)
        val newWord    = newIndices.joinToString("") { state.grid[it].char.toString() }
        _uiState.update { it.copy(grid = newGrid, selectedIndices = newIndices, currentWord = newWord, showError = false) }
    }

    /** Valide le mot courant. Si correct → score++ et nouvelle grille. */
    fun validate() {
        val state = _uiState.value
        if (state.phase != Phase.PLAYING) return
        if (state.currentWord == state.targetWord) {
            _uiState.update { it.copy(score = it.score + 1) }
            loadNewGrid()
        } else {
            _uiState.update { it.copy(showError = true) }
        }
    }

    /** Passe à une nouvelle grille sans marquer de point. */
    fun pass() {
        if (_uiState.value.phase != Phase.PLAYING) return
        loadNewGrid()
    }

    /**
     * Bonus indice : révèle visuellement la cellule contenant la première lettre du mot.
     * Pénalité de 1 point (minimum 0).
     */
    fun hint() {
        val state = _uiState.value
        if (state.phase != Phase.PLAYING || state.hintUsed) return

        val firstChar  = state.targetWord[0]
        val hintIndex  = state.grid.indexOfFirst { !it.isSelected && it.char == firstChar }
        if (hintIndex == -1) return

        val newGrid = state.grid.toMutableList().also { it[hintIndex] = it[hintIndex].copy(isHinted = true) }
        _uiState.update { it.copy(grid = newGrid, hintUsed = true, score = maxOf(0, it.score - 1)) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
