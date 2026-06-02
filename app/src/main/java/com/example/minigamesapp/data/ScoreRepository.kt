package com.example.minigamesapp.data

class ScoreRepository(private val scoreDao: ScoreDao) {
    suspend fun insertScore(score: Score) = scoreDao.insertScore(score)
    suspend fun getTopScores(): List<Score> = scoreDao.getTopScores()
    suspend fun getTopScoresByGame(gameName: String): List<Score> = scoreDao.getTopScoresByGame(gameName)
    suspend fun deleteAllScores() = scoreDao.deleteAllScores()
    suspend fun getGamesCountByPlayer(playerName: String): Int = scoreDao.getGamesCountByPlayer(playerName)
    suspend fun getAvgScoreByPlayer(playerName: String): Double = scoreDao.getAvgScoreByPlayer(playerName)
}
