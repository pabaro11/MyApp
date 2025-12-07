// Android Game Recommendation App - Initial Project Structure (Skeleton)
// This file contains the initial structure for your app including
// Today's Game, Preference Test, Search, Filters, and API integration placeholders.

package com.example.gamefinder


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameFinderApp()
        }
    }
}

@Composable
fun GameFinderApp() {
    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        MainScreen(Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text("Game Finder") },
        actions = {
            TextButton(onClick = { /* TODO: 성향 테스트 화면으로 이동 */ }) {
                Text("내 성향 알아보기")
            }
        }
    )
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        TodayGameSection()
        Spacer(modifier = Modifier.height(20.dp))
        SearchSection()
        Spacer(modifier = Modifier.height(20.dp))
        GameListSection()
    }
}

//-----------------------------------------------------
// 1. "오늘의 게임" - 24시간마다 랜덤 게임 표시
//-----------------------------------------------------
@Composable
fun TodayGameSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("오늘의 게임", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))

        // TODO: replace with real random game from DB/API
        AsyncImage(
            model = "https://via.placeholder.com/300x150.png?text=Random+Game",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )
        Text("게임 이름 (랜덤)", style = MaterialTheme.typography.titleMedium)
    }
}

//-----------------------------------------------------
// 2. 검색창 + 키워드 검색 + 필터 검색
//-----------------------------------------------------
@Composable
fun SearchSection() {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("게임 이름 또는 키워드 검색 (공포, 액션, 힐링...)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { /* TODO: 검색 기능 */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("검색")
        }

        Spacer(Modifier.height(10.dp))

        FilterSection()
    }
}

//-----------------------------------------------------
// 3. 필터 기능 (난이도, 플레이 시간, 별점, 멀티 여부, 성향 테스트 기반 추천값)
//-----------------------------------------------------
@Composable
fun FilterSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("필터", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        // TODO: Dropdown, Slider, Checkbox 등으로 실제 필터 UI 구성
        Text("- 난이도 선택 (슬라이더/선택지)")
        Text("- 플레이 시간 (1~3시간, 3~10시간...) ")
        Text("- 별점 필터")
        Text("- 멀티 플레이 가능 여부")
        Text("- 내 성향 테스트 기반 추천 적용")
    }
}

//-----------------------------------------------------
// 4. 게임 리스트 + 스팀 API / IsThereAnyDeal API 가격 표시 + 상점 버튼
//-----------------------------------------------------
@Composable
fun GameListSection() {
    LazyColumn {
        items(5) { index ->
            GameListItem(
                name = "게임 $index",
                imageUrl = "https://via.placeholder.com/300x150.png",
                steamPrice = "59000원",
                lowestPrice = "27000원",
                historicalLow = "19000원"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun GameListItem(name: String, imageUrl: String, steamPrice: String, lowestPrice: String, historicalLow: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))
            Text("정가: $steamPrice")
            Text("현재 최저가: $lowestPrice")
            Text("역대 최저가: $historicalLow")

            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* TODO: 스팀 스토어로 이동 */ }) {
                Text("상점 열기")
            }
        }
    }
}
