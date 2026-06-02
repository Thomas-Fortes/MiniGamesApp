package com.example.minigamesapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "player_name") val playerName: String,
    @ColumnInfo(name = "game_name") val gameName: String,
    val score: Int,
    val date: Long = System.currentTimeMillis()
)
