package com.example.w07.repository

import com.example.w07.data.DEFAULT_GAME_CONFIG
import com.example.w07.data.GameConfig
import com.example.w07.data.INITIAL_USER_DATA
import com.example.w07.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameRepository {

    // GameConfig 관리
    private val _config = MutableStateFlow(DEFAULT_GAME_CONFIG)
    val config: StateFlow<GameConfig> = _config.asStateFlow()

    fun updateConfig(newConfig: GameConfig) {
        _config.value = newConfig
    }

    // UserData 관리
    private val _user = MutableStateFlow(INITIAL_USER_DATA)
    val user: StateFlow<UserData> = _user.asStateFlow()

    fun addPoint(point: Int = 1) {
        _user.update { current ->
            current.copy(totalScore = current.totalScore + point)
        }
    }
}
