package com.example.w05

import android.R.attr.fontWeight
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.w05.ui.theme.MyAppTheme
import kotlinx.coroutines.delay




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                // TODO: 여기에 카운터와 스톱워치 UI를 만들도록 안내
                val count = remember { mutableStateOf(0) }
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CounterApp(count)
                    Spacer(modifier = Modifier.height(32.dp))
                    StopWatchApp()
                    ColorToggleButtonApp()
                }
            }
        }
    }
}


@Composable
fun CounterApp(count: MutableState<Int>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Count: ${count.value}",
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold) // TODO: 상태값 표시
        Row {
            Button(onClick = { count.value++ }) { Text("Increase") }
            Button(onClick = { count.value = 0 }) { Text("Reset") }
        }
    }
}


@Composable
fun StopWatchApp() {
    // 스톱워치의 시간(초)을 저장하는 상태 변수: 초깃값=15 * 60 + 22
    var seconds by remember { mutableStateOf(15 * 60 + 22) }
    // 스톱워치가 실행 중인지 여부를 저장하는 상태 변수
    var isRunning by remember { mutableStateOf(false) }

    // isRunning 상태가 변경될 때마다 LaunchedEffect가 실행됨
    LaunchedEffect(isRunning) {
        // isRunning이 true일 때만 아래의 무한 루프를 실행
        if (isRunning) {
            while (true) {
                delay(1000) // 1초 대기
                seconds++   // 1초 증가
            }
        }
    }

    StopWatchScreen(
        seconds = seconds,
        isRunning = isRunning,
        onStartClick = { isRunning = true },
        onStopClick = { isRunning = false },
        onResetClick = {
            seconds = 0
            isRunning = false
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun StopWatchScreen(
    seconds: Int,
    isRunning: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 분과 초 계산
        val minutes = seconds / 60
        val secondsToDisplay = seconds % 60
        // MM:SS 형식으로 포맷팅
        val timeFormatted = String.format("%02d:%02d", minutes, secondsToDisplay)
        // 경과 시간을 표시하는 텍스트
        Text(
            text = timeFormatted,
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        // 시작, 중지, 초기화 버튼을 가로로 배치
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 시작 버튼
            Button(
                onClick = onStartClick,
                enabled = !isRunning // 실행 중이 아닐 때만 활성화
            ) {
                Text("Start")
            }
            Spacer(modifier = Modifier.width(16.dp))
            // 중지 버튼
            Button(
                onClick = onStopClick,
                enabled = isRunning // 실행 중일 때만 활성화
            ) {
                Text("Stop")
            }
            Spacer(modifier = Modifier.width(16.dp))
            // 초기화 버튼
            Button(onClick = onResetClick) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun ColorToggleButtonApp() {
    // 배경색 상태를 저장하는 변수. 초기값은 Color.Red.
    // 'by' 키워드를 사용하여 MutableState<Color>의 value 속성에 직접 접근하도록 함.
    var currentColor by remember { mutableStateOf(Color.Red) }

    // 원형 버튼을 화면 중앙에 배치하기 위한 외부 Box
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 클릭 가능한 원형 버튼 역할을 하는 내부 Box
        Box(
            modifier = Modifier
                .clip(CircleShape) // 모양을 원형으로 자름
                .background(currentColor) // 현재 색상으로 배경 설정
                .clickable { // 클릭 이벤트 처리
                    // 현재 색상이 빨간색이면 파란색으로, 아니면 빨간색으로 변경
                    currentColor = if (currentColor == Color.Red) Color.Blue else Color.Red
                }
                .padding(30.dp), // 원 안쪽에 여백을 줘서 텍스트와 경계 사이에 공간을 둠
            contentAlignment = Alignment.Center // Box 안의 내용을 중앙에 정렬
        ) {
            Text(
                text = "Click Me",
                color = Color.White, // 텍스트 색상은 흰색으로 고정
                fontSize = 30.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterAppPreview() {
    val count = remember { mutableStateOf(0) }
    CounterApp(count)
}

@Preview(showBackground = true)
@Composable
fun StopWatchPreview() {
    StopWatchApp()
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun ColorToggleButtonAppPreview() {
    ColorToggleButtonApp()
}
