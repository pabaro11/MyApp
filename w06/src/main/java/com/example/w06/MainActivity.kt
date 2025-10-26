package com.example.w06

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import com.example.w06.ui.theme.MyAppTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// ---------- 데이터 클래스 ----------
data class Bubble(
    val id: Int,
    val position: Offset,
    val radius: Float,
    val color: Color,
    val creationTime: Long = System.currentTimeMillis(),
    val velocityX: Float = Random.nextFloat() * 4 - 2,
    val velocityY: Float = Random.nextFloat() * 4 - 2
)

// ---------- 상태 클래스 ----------
class GameState {
    var bubbles by mutableStateOf(emptyList<Bubble>())
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var timeLeft by mutableStateOf(60)
}

// ---------- 메인 액티비티 ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

// ---------- 버블 게임 UI ----------
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleGameScreen() {
    val gameState = remember { GameState() }
    val context = LocalContext.current

    // ✅ 진동 객체 (안정화 버전)
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }

    @SuppressLint("MissingPermission")
    fun vibrateShort() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }

    // ✅ 타이머
    LaunchedEffect(gameState.isGameOver) {
        if (!gameState.isGameOver) {
            while (true) {
                delay(1000L)
                gameState.timeLeft--
                if (gameState.timeLeft <= 0) {
                    gameState.isGameOver = true
                    break
                }
                val now = System.currentTimeMillis()
                gameState.bubbles = gameState.bubbles.filter { now - it.creationTime < 3000 }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 점수 / 시간 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score: ${gameState.score}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Time: ${gameState.timeLeft}s", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val canvasWidthPx = with(density) { maxWidth.toPx() }
            val canvasHeightPx = with(density) { maxHeight.toPx() }

            // ✅ 버블 이동 루프
            LaunchedEffect(gameState.isGameOver) {
                if (!gameState.isGameOver) {
                    while (true) {
                        delay(16)
                        if (gameState.bubbles.isEmpty()) {
                            val newBubbles = List(3) {
                                Bubble(
                                    id = Random.nextInt(),
                                    position = Offset(
                                        Random.nextFloat() * maxWidth.value,
                                        Random.nextFloat() * maxHeight.value
                                    ),
                                    radius = Random.nextFloat() * 25 + 25,
                                    color = Color(
                                        Random.nextInt(256),
                                        Random.nextInt(256),
                                        Random.nextInt(256),
                                        200
                                    )
                                )
                            }
                            gameState.bubbles = newBubbles
                        }

                        // 랜덤 추가
                        if (Random.nextFloat() < 0.05f && gameState.bubbles.size < 15) {
                            val newBubble = Bubble(
                                id = Random.nextInt(),
                                position = Offset(
                                    Random.nextFloat() * maxWidth.value,
                                    Random.nextFloat() * maxHeight.value
                                ),
                                radius = Random.nextFloat() * 40 + 30,
                                color = Color(
                                    Random.nextInt(256),
                                    Random.nextInt(256),
                                    Random.nextInt(256),
                                    200
                                )
                            )
                            gameState.bubbles += newBubble
                        }

                        // 버블 이동 업데이트
                        gameState.bubbles = gameState.bubbles.map { bubble ->
                            with(density) {
                                var x = bubble.position.x.dp.toPx()
                                var y = bubble.position.y.dp.toPx()
                                val r = bubble.radius.dp.toPx()
                                var vx = bubble.velocityX
                                var vy = bubble.velocityY

                                x += vx
                                y += vy

                                if (x < r || x > canvasWidthPx - r) vx *= -1
                                if (y < r || y > canvasHeightPx - r) vy *= -1

                                x = x.coerceIn(r, canvasWidthPx - r)
                                y = y.coerceIn(r, canvasHeightPx - r)

                                bubble.copy(
                                    position = Offset(x.toDp().value, y.toDp().value),
                                    velocityX = vx,
                                    velocityY = vy
                                )
                            }
                        }
                    }
                }
            }

            // ✅ 버블 클릭 처리 (안정화 버전)
            gameState.bubbles.forEach { bubble ->
                Canvas(
                    modifier = Modifier
                        .size((bubble.radius * 2).dp)
                        .offset(bubble.position.x.dp, bubble.position.y.dp)
                        .clickable {
                            if (!gameState.isGameOver) {
                                gameState.score++
                                vibrateShort()

                                // ⚡ 리스트 수정 시 충돌 방지 (복사 후 교체)
                                val updatedList = gameState.bubbles.toMutableList()
                                updatedList.removeAll { it.id == bubble.id }
                                gameState.bubbles = updatedList
                            }
                        }
                ) {
                    drawCircle(color = bubble.color, radius = size.width / 2)
                }
            }

            // ✅ 게임 종료 다이얼로그
            if (gameState.isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("게임 종료") },
                    text = { Text("최종 점수: ${gameState.score}점") },
                    confirmButton = {
                        TextButton(onClick = {
                            gameState.score = 0
                            gameState.timeLeft = 60
                            gameState.bubbles = emptyList()
                            gameState.isGameOver = false
                        }) { Text("다시 시작") }
                    },
                    dismissButton = {
                        TextButton(onClick = { }) { Text("종료") }
                    }
                )
            }
        }
    }
}

// ---------- 미리보기 ----------
@Preview(showBackground = true)
@Composable
fun PreviewGame() {
    MyAppTheme {
        BubbleGameScreen()
    }
}
