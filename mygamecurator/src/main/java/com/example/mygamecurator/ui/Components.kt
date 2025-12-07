package com.example.mygamecurator.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mygamecurator.data.Game
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

// -------------------- App Top Bar --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onTestButtonClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Game Curator", color = Color.White, fontWeight = FontWeight.Bold)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B0C10),
            titleContentColor = Color.White
        ),
        actions = {
            TextButton(onClick = onTestButtonClick) {
                Text("성향 테스트", color = Color(0xFFFF6F91))
            }
        }
    )
}

// -------------------- Search Section --------------------
@OptIn(ExperimentalMaterial3Api::class)
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

        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            keywords.forEach {
                FilterChip(
                    label = it,
                    selected = selectedKeyword == it,
                    onSelected = { onKeywordSelect(it) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("검색 (게임명/키워드)") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF3D5AFE),
                unfocusedBorderColor = Color(0xFF2E3038),
                focusedLabelColor = Color(0xFF3D5AFE),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.White,
                unfocusedContainerColor = Color(0xFF2E3038),
                focusedContainerColor = Color(0xFF2E3038)
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
        ) {
            Text("검색", color = Color.White)
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

        Text("필터", color = Color.White, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        // 난이도
        Text("난이도", color = Color(0xFF9A9BA1))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("쉬움", "보통", "어려움").forEach { diff ->
                FilterChip(
                    label = diff,
                    selected = selectedDifficulty == diff,
                    onSelected = {
                        onSelectDifficulty(if (selectedDifficulty == diff) null else diff)
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 플레이시간
        Text("플레이시간", color = Color(0xFF9A9BA1))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("짧음", "중간", "김").forEach { t ->
                FilterChip(
                    label = t,
                    selected = selectedTime == t,
                    onSelected = { onSelectTime(if (selectedTime == t) null else t) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 멀티 여부
        Text("멀티 여부", color = Color(0xFF9A9BA1))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                label = "싱글",
                selected = selectedMultiplayer == false,
                onSelected = {
                    onSelectMultiplayer(if (selectedMultiplayer == false) null else false)
                }
            )

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
fun RecommendedGrid(
    games: List<Game>,
    favoriteGameIds: Set<Int>,
    onCardClick: (Game) -> Unit,
    onToggleFavorite: (Game) -> Unit
) {

    Text(
        "추천 게임",
        modifier = Modifier.padding(16.dp),
        color = Color.White,
        fontWeight = FontWeight.Bold
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.heightIn(max = 700.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                onCardClick = onCardClick,
                isFavorite = favoriteGameIds.contains(game.id),
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}


// -------------------- Game Card (Grid) --------------------

@Composable
fun GameCard(
    game: Game,
    onCardClick: (Game) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: (Game) -> Unit
) {
    // 평점 색상 결정 로직
    val ratingColor = when {
        game.rating >= 4.0 -> Color(0xFF4CAF50) // 녹색 (우수)
        game.rating >= 2.5 -> Color(0xFFFFC107) // 노란색 (보통)
        else -> Color(0xFFFF5722) // 주황색 (낮음)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onCardClick(game) }
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F2C))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = game.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 평점 오버레이 및 색상 적용
                Text(
                    text = String.format("%.1f", game.rating),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(ratingColor.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                // 찜하기 버튼
                IconButton(
                    onClick = { onToggleFavorite(game) },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = game.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = game.genres.firstOrNull() ?: "기타",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


// -------------------- 오늘의 게임 카드 (수정 직전 상태: 이미지 크기 확대 미적용) --------------------
@Composable
fun TodayGameCard(game: Game, onCardClick: (Game) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick(game) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F2C))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Today's Game",
                tint = Color(0xFFFF6F91),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("오늘의 추천 게임", color = Color(0xFFFF6F91), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(game.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp) // ★★★ 여기서 이미지 크기가 작았습니다.
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}


// -------------------- 상세 정보 모달 (수정 직전 상태: mapToKorean 인수가 없음) --------------------

@Composable
fun GameDetailsDialog(game: Game, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(game.name, color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = game.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                )
                Spacer(Modifier.height(16.dp))

                Text("평점: ${game.rating}", color = Color.LightGray)
                Text("난이도: ${game.difficulty}", color = Color.LightGray)
                Text("플레이 시간: 약 ${game.playTime}분", color = Color.LightGray)
                Spacer(Modifier.height(8.dp))

                // 장르 목록
                Text("장르:", color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                Text(game.genres.joinToString { it.replaceFirstChar { c -> c.uppercase() } }, color = Color.White) // ★★★ 영어 장르 그대로 표시
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("닫기")
            }
        },
        containerColor = Color(0xFF1F1F2C)
    )
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