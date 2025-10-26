package com.example.jump_slime.model

data class GameState(
    val isJumping: Boolean = false,
    val jumpHeight: Float = 0f,
    val obstacleX: Float = 900f,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false
)
