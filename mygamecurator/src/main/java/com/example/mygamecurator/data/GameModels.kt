package com.example.mygamecurator.data

data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val genres: List<String>,
    val rating: Double,
    val difficulty: String,
    val playTime: Int,
    val multiplayer: Boolean
)
