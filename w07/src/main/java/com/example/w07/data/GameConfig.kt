package com.example.w07.data

data class GameConfig(
    val targetTimeMs: Long,
    val toleranceMs: Long
)

val DEFAULT_GAME_CONFIG = GameConfig(
    targetTimeMs = 5000L,  // 5초 목표
    toleranceMs = 100L     // ±0.1초 허용
)
