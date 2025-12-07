package com.example.gamecurator.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gamecurator.viewmodel.RecommendViewModel
import com.example.gamecurator.ui.component.GameCard
import com.example.gamecurator.ui.component.LoadingView

@Composable
fun RecommendScreen(
    nav: NavController,
    vm: RecommendViewModel = viewModel()
) {
    val state = vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.load()
    }

    if (state.value.loading) {
        LoadingView()
        return
    }

    LazyColumn {
        items(state.value.games) { game ->
            GameCard(game)
        }
    }
}
