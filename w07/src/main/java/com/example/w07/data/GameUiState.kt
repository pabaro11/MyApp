package com.example.w07.data

data class GameUiState(
    val config: GameConfig = DEFAULT_GAME_CONFIG,
    val currentUserData: UserData = INITIAL_USER_DATA,
    val gameData: GameData = GameData(),
    val isGameActive: Boolean = true,
    val feedbackMessage: String? = null
)
