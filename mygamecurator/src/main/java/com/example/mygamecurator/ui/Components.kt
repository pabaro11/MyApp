package com.example.mygamecurator.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.mygamecurator.data.Game

// -------------------- Search Section --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onTestButtonClick: () -> Unit) { // ★ onTestButtonClick 파라미터 추가
    TopAppBar(
        title = {
            Text("Game Curator", color = Color.White, fontWeight = FontWeight.Bold)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B0C10),
            titleContentColor = Color.White
        ),
        actions = {
            TextButton(onClick = onTestButtonClick) { // 이 버튼 클릭 시 HomeScreen에서 startTest() 호출됨
                Text("성향 테스트", color = Color(0xFFFF6F91))
            }
        }
    )
}



@Composable
fun SearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    selectedKeyword: String?,
    onKeywordSelect: (String) -> Unit
) {

    Column(Modifier.padding(16.dp)) {

        val keywords = listOf("액션", "공포", "힐링", "RPG", "전략", "오픈월드")

        Row(Modifier.horizontalScroll(rememberScrollState())) {
            keywords.forEach {
                FilterChip(
                    label = it,
                    selected = selectedKeyword == it,
                    onSelected = { onKeywordSelect(it) }
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("검색 (게임명/키워드)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF3D5AFE))
        ) {
            Text("검색")
        }
    }
}


// -------------------- Filter Section --------------------

@Composable
fun FilterSection(
    selectedDifficulty: String?,
    onSelectDifficulty: (String?) -> Unit,
    selectedTime: String?,
    onSelectTime: (String?) -> Unit,
    selectedMultiplayer: Boolean?,
    onSelectMultiplayer: (Boolean?) -> Unit
) {

    Column(Modifier.padding(16.dp)) {

        Text("필터", color = Color.White)

        Spacer(Modifier.height(16.dp))

        Text("난이도", color = Color(0xFF9A9BA1))
        Row {
            listOf("쉬움", "보통", "어려움").forEach { diff ->
                FilterChip(
                    label = diff,
                    selected = selectedDifficulty == diff,
                    onSelected = {
                        onSelectDifficulty(if (selectedDifficulty == diff) null else diff)
                    }
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("플레이시간", color = Color(0xFF9A9BA1))
        Row {
            listOf("짧음", "중간", "김").forEach { t ->
                FilterChip(
                    label = t,
                    selected = selectedTime == t,
                    onSelected = { onSelectTime(if (selectedTime == t) null else t) }
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("멀티 여부", color = Color(0xFF9A9BA1))
        Row {
            FilterChip(
                label = "싱글",
                selected = selectedMultiplayer == false,
                onSelected = {
                    onSelectMultiplayer(if (selectedMultiplayer == false) null else false)
                }
            )
            Spacer(Modifier.width(8.dp))

            FilterChip(
                label = "멀티",
                selected = selectedMultiplayer == true,
                onSelected = {
                    onSelectMultiplayer(if (selectedMultiplayer == true) null else true)
                }
            )
        }
    }
}


// -------------------- Grid Section --------------------

@Composable
fun RecommendedGrid(games: List<Game>) {

    Text(
        "추천 게임",
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(700.dp)
            .padding(horizontal = 8.dp)
    ) {
        items(games) { game ->
            GameGridCard(game)
        }
    }
}


// -------------------- Tag / Filter Chip --------------------

@Composable
fun TagChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2E3038))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(label, color = Color.White)
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color(0xFF3D5AFE) else Color(0xFF2E3038))
            .clickable { onSelected() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (selected) Color.White else Color(0xFFDDDDDD))
    }
}