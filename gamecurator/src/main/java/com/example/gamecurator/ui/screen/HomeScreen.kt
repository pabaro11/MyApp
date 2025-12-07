package com.example.gamecurator.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(nav: NavController) {
    Column(Modifier.padding(20.dp)) {
        Text("🎮 GameCurator", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(20.dp))

        Button(onClick = { nav.navigate("recommend") }) {
            Text("게임 추천 받기")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { nav.navigate("godot") }) {
            Text("Godot 미니게임 실행(예정)")
        }
    }
}
