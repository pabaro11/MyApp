package com.example.gamecurator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecurator.network.ApiClient
import com.example.gamecurator.data.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecommendViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val games: List<Game> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            try {
                val result = ApiClient.api.getRecommend()
                _uiState.value = UiState(false, result.games)
            } catch (e: Exception) {
                _uiState.value = UiState(false)
            }
        }
    }
}
