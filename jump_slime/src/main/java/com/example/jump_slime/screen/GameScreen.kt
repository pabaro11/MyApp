package com.example.jump_slime.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jump_slime.R
import com.example.jump_slime.model.GameState
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

data class Obstacle(
    val type: String,
    var x: Float,
    var y: Float,
    var verticalVelocity: Float = 0f,
    var jumpTimer: Int = 0
)

@Composable
fun GameScreen() {
    var game by remember { mutableStateOf(GameState()) }
    var velocity by remember { mutableStateOf(0f) }
    var gameStarted by remember { mutableStateOf(false) }
    val gravity = 1f
    val jumpPower = 18f
    val groundY = 400f
    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }
    var frameIndex by remember { mutableStateOf(0) }
    val runFrames = listOf(R.drawable.slime_run1, R.drawable.slime_run2)
    var speed by remember { mutableStateOf(9f) }

    // 🌇 시간대 상태
    var timeState by remember { mutableStateOf("day") }

    // 일정 시간마다 낮→저녁→밤 순환
    LaunchedEffect(Unit) {
        while (true) {
            delay(15000L)
            timeState = when (timeState) {
                "day" -> "evening"
                "evening" -> "night"
                else -> "day"
            }
        }
    }

    // 배경색 애니메이션
    val targetColor = when (timeState) {
        "day" -> Color(0xFFBDE8FF)
        "evening" -> Color(0xFFFFC180)
        else -> Color(0xFF1E2A78)
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(2000)
    )

    // 프레임 애니메이션
    LaunchedEffect(Unit) {
        while (true) {
            delay(120L)
            if (gameStarted && !game.isGameOver && !game.isJumping) {
                frameIndex = (frameIndex + 1) % runFrames.size
            }
        }
    }

    // 구름 움직임
    var cloudOffset by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(40L)
            if (gameStarted) {
                cloudOffset -= 1f
                if (cloudOffset < -400f) cloudOffset = 400f
            }
        }
    }

    // 메인 루프
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)

            if (gameStarted && !game.isGameOver) {
                // 점프 물리
                var jumpHeight = game.jumpHeight
                if (game.isJumping || jumpHeight > 0f) {
                    jumpHeight += velocity
                    velocity -= gravity
                    if (jumpHeight <= 0f) {
                        jumpHeight = 0f
                        velocity = 0f
                        game = game.copy(isJumping = false)
                    }
                }

                // 장애물 이동 및 생성
                obstacles = obstacles.map { obs ->
                    var newY = obs.y
                    var newVel = obs.verticalVelocity
                    var newTimer = obs.jumpTimer

                    if (obs.type == "red_slime") {
                        newTimer++
                        if (newTimer > 70 && newVel == 0f) {
                            newVel = 6f
                            newTimer = 0
                        }
                        newY -= newVel
                        newVel -= 0.5f
                        if (newY > groundY) {
                            newY = groundY
                            newVel = 0f
                        }
                    }

                    obs.copy(x = obs.x - speed, y = newY, verticalVelocity = newVel, jumpTimer = newTimer)
                }.filter { it.x > -300f }

                // 장애물 간격 조절 (여기 변경하면 빈도 조절 가능)
                if (obstacles.isEmpty() || obstacles.last().x < 600f) {
                    val type = when (Random.nextInt(4)) {
                        0 -> "spike2"
                        1 -> "spike5"
                        2 -> "flyingspike"
                        else -> "red_slime"
                    }
                    val y = when (type) {
                        "flyingspike" -> groundY - 150f
                        "spike2", "spike5" -> groundY + 10f
                        "red_slime" -> groundY
                        else -> groundY
                    }
                    obstacles = obstacles + Obstacle(type, 1000f, y)
                }

                // 충돌 판정
                val slimeX = 150f
                val slimeWidth = 10f
                val slimeHeight = 60f
                val slimeY = groundY - (game.jumpHeight * 1.5f)

                for (obs in obstacles) {
                    val (width, height) = when (obs.type) {
                        "spike2" -> 80f to 30f
                        "spike5" -> 120f to 30f
                        "flyingspike" -> 90f to 30f
                        "red_slime" -> 80f to 50f
                        else -> 70f to 25f
                    }
                    val horizontal = slimeX + slimeWidth > obs.x && slimeX < obs.x + width
                    val vertical = slimeY + slimeHeight > obs.y && slimeY < obs.y + height
                    if (horizontal && vertical) {
                        game = game.copy(isGameOver = true)
                    }
                }

                game = game.copy(
                    jumpHeight = jumpHeight,
                    score = game.score + 1
                )
            }
        }
    }

    // UI 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable {
                if (!gameStarted) {
                    gameStarted = true
                    game = GameState()
                    obstacles = emptyList()
                } else if (game.isGameOver) {
                    game = GameState(highScore = max(game.highScore, game.score))
                    obstacles = emptyList()
                    velocity = 0f
                    speed = 9f
                } else if (!game.isJumping) {
                    velocity = jumpPower
                    game = game.copy(isJumping = true)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameStarted) {
            Text("SCORE: ${game.score}", style = MaterialTheme.typography.bodyLarge)
            Text("BEST: ${max(game.highScore, game.score)}", style = MaterialTheme.typography.bodyLarge)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            // ☁️ 구름
            Image(
                painter = painterResource(id = R.drawable.cloud),
                contentDescription = "Cloud",
                modifier = Modifier
                    .offset(x = cloudOffset.dp, y = 60.dp)
                    .size(180.dp)
            )

            // 🌙 별 (밤일 때만)
            var stars by remember {
                mutableStateOf(
                    List(12) {
                        Pair(
                            Random.nextFloat() * 800f,  // X축 더 넓게
                            Random.nextFloat() * 350f   // Y축 더 높게
                        )
                    }
                )
            }
            var starOffset by remember { mutableStateOf(0f) }

            /* 별 이동 (천천히 왼쪽으로) */
            LaunchedEffect(timeState) {
                while (timeState == "night") {
                    delay(120L)
                    starOffset -= 0.2f
                    if (starOffset < -800f) starOffset = 0f
                }
            }

            if (timeState == "night") {
                stars.forEach { (x, y) ->
                    Box(
                        Modifier
                            .offset(x = ((x + starOffset) % 800f).dp, y = y.dp)
                            .size(Random.nextInt(2, 4).dp)
                            .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                    )
                }
            }


            // 🌿 2단 톤 잔디 (깔끔 + 경계선 추가)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(280.dp)
            ) {
                // 윗부분 (밝은 연두색)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF8FE56E))
                )

                // 경계선 (살짝 어두운 초록)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.Center)
                        .background(Color(0xFF6FBF4A))
                )

                // 아래부분 (짙은 초록색)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color(0xFF4CAF50))
                )
            }



            // 🟩 슬라임
            val slimeY = groundY - (game.jumpHeight * 1.5f)
            Image(
                painter = painterResource(
                    id = when {
                        game.isGameOver -> R.drawable.slime_die
                        game.isJumping -> R.drawable.slime_jump
                        else -> runFrames[frameIndex]
                    }
                ),
                contentDescription = "Slime",
                modifier = Modifier
                    .size(90.dp)
                    .offset(x = 100.dp, y = slimeY.dp)
            )

            // 장애물들
            for (obs in obstacles) {
                val res = when (obs.type) {
                    "spike2" -> R.drawable.spike2
                    "spike5" -> R.drawable.spike5
                    "flyingspike" -> R.drawable.flyingspike
                    "red_slime" -> R.drawable.red_slime
                    else -> R.drawable.spike2
                }
                Image(
                    painter = painterResource(id = res),
                    contentDescription = obs.type,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(x = obs.x.dp, y = obs.y.dp)
                        .size(
                            when (obs.type) {
                                "spike5" -> 120.dp
                                "flyingspike" -> 90.dp
                                "red_slime" -> 70.dp
                                else -> 80.dp
                            }
                        )
                )
            }
        }

        if (!gameStarted) {
            Spacer(Modifier.height(20.dp))
            Text("아무 곳이나 클릭해서 시작!", color = Color.DarkGray, style = MaterialTheme.typography.titleLarge)
        } else if (game.isGameOver) {
            Spacer(Modifier.height(20.dp))
            Text("GAME OVER!", color = Color.Red, style = MaterialTheme.typography.titleLarge)
            Text("다시 시작하려면 탭하세요.", color = Color.DarkGray)
        }
    }
}
