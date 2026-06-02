package com.example.minigamesapp.data

import androidx.room.*

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<Score>

    @Query("SELECT * FROM scores WHERE game_name = :gameName ORDER BY score DESC LIMIT 10")
    suspend fun getTopScoresByGame(gameName: String): List<Score>

    @Query("DELETE FROM scores")
    suspend fun deleteAllScores()

    @Query("SELECT COUNT(*) FROM scores WHERE player_name = :playerName")
    suspend fun getGamesCountByPlayer(playerName: String): Int

    @Query("SELECT AVG(score) FROM scores WHERE player_name = :playerName")
    suspend fun getAvgScoreByPlayer(playerName: String): Double
}
