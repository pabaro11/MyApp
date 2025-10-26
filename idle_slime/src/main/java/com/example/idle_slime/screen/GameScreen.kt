package com.example.idle_slime.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.idle_slime.R
import com.example.idle_slime.model.SlimeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen() {
    val slime = remember { mutableStateOf(SlimeData()) }
    val scope = rememberCoroutineScope()

    // 코인 자동 증가 루프
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // 5초마다 코인 1개 추가
            slime.value = slime.value.copy(coins = slime.value.coins + 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("LV. ${slime.value.level}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("EXP: ${slime.value.exp}/10")
        Text("COINS: ${slime.value.coins}")

        Spacer(Modifier.height(20.dp))

        // 슬라임 이미지 클릭시 경험치 증가
        Image(
            painter = painterResource(id = R.drawable.slime1),
            contentDescription = "Slime",
            modifier = Modifier
                .size(180.dp)
                .clickable {
                    var exp = slime.value.exp + 1
                    var level = slime.value.level
                    if (exp >= 10) { // 레벨업 조건
                        exp = 0
                        level += 1
                    }
                    slime.value = slime.value.copy(level = level, exp = exp)
                }
        )

        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            slime.value = SlimeData() // 리셋
        }) {
            Text("리셋하기")
        }
    }
}
