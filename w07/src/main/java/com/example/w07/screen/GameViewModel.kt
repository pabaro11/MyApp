package com.example.w07.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.w07.data.GameConfig
import com.example.w07.data.GameData
import com.example.w07.data.GameUiState
import com.example.w07.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null

    init {
        viewModelScope.launch {
            combine(repository.config, repository.user) { config, user ->
                _uiState.update {
                    it.copy(config = config, currentUserData = user)
                }
            }.collect {}
        }
    }

    fun onStartClicked() {
        if (_uiState.value.gameData.isRunning) return

        stopwatchJob?.cancel()
        _uiState.update {
            it.copy(
                gameData = GameData(isRunning = true),
                feedbackMessage = null
            )
        }

        stopwatchJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (_uiState.value.gameData.isRunning) {
                delay(10)
                val elapsed = System.currentTimeMillis() - startTime
                _uiState.update {
                    it.copy(gameData = it.gameData.copy(currentTimeMs = elapsed))
                }
            }
        }
    }

    fun onStopClicked(finalTimeMs: Long) {
        stopwatchJob?.cancel()
        _uiState.update {
            it.copy(gameData = it.gameData.copy(isRunning = false))
        }

        val config = _uiState.value.config
        val diff = abs(finalTimeMs - config.targetTimeMs)
        val isSuccess = diff <= config.toleranceMs

        if (isSuccess) {
            repository.addPoint(1)
            val newPoint = _uiState.value.gameData.currentPoint + 1
            val total = _uiState.value.currentUserData.totalScore + 1

            _uiState.update {
                it.copy(
                    gameData = it.gameData.copy(currentPoint = newPoint),
                    feedbackMessage = "정확! ${diff}ms 오차. 누적 점수: ${total}점"
                )
            }
        } else {
            _uiState.update {
                it.copy(feedbackMessage = "실패... ${diff}ms 오차.")
            }
        }
    }

    fun onConfigUpdated(newConfig: GameConfig) {
        repository.updateConfig(newConfig)
    }
}
