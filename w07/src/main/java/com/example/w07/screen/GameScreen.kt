package com.example.w07.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.w07.ui.theme.MyAppTheme
import java.util.concurrent.TimeUnit

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "플레이어: ${uiState.currentUserData.userName} (총점: ${uiState.currentUserData.totalScore}점)",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("현재 라운드 포인트: ${uiState.gameData.currentPoint}", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("목표 시간: ${formatTime(uiState.config.targetTimeMs)}", style = MaterialTheme.typography.titleLarge)
        Text("오차 범위: ±${formatTime(uiState.config.toleranceMs)}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(32.dp))

        StopwatchDisplay(uiState.gameData.currentTimeMs)
        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.onStartClicked() }, enabled = !uiState.gameData.isRunning) {
                Text("Start", fontSize = 20.sp)
            }
            Button(onClick = { viewModel.onStopClicked(uiState.gameData.currentTimeMs) }, enabled = uiState.gameData.isRunning) {
                Text("Stop", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        uiState.feedbackMessage?.let { msg ->
            Text(
                text = msg,
                color = if (msg.contains("정확")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun StopwatchDisplay(currentTimeMs: Long) {
    Text(
        text = formatTime(currentTimeMs),
        fontSize = 72.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

fun formatTime(timeMs: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMs)
    val totalMillis = timeMs % 1000
    val millis = totalMillis / 10
    return String.format("%02d.%02d", totalSeconds, millis)
}

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    MyAppTheme {
        GameScreen()
    }
}
