@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mygamecurator.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mygamecurator.api.ApiClient
import com.example.mygamecurator.data.Game
import com.example.mygamecurator.api.Keys
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.draw.clip

// --------------------------------------
// 성향 테스트 데이터 모델
// --------------------------------------
data class TestOption(
    val text: String,
    val scoreImpact: Map<String, Int>
)

data class TestQuestion(
    val question: String,
    val options: List<TestOption>
)

val aptitudeQuestions = listOf(
    TestQuestion("Q1. 평소에 스트레스를 어떻게 해소하나요?", listOf(
        TestOption("활동적이고 긴장감 있는 일", mapOf("액션" to 1, "전략" to -1)),
        TestOption("조용하고 편안한 활동", mapOf("힐링" to 1, "공포" to -1))
    )),
    TestQuestion("Q2. 예상치 못한 상황이 발생했을 때, 당신은?", listOf(
        TestOption("즉시 몸을 움직여 대처한다.", mapOf("액션" to 1, "RPG" to 1)),
        TestOption("잠시 멈춰 계획을 세우고 가장 효율적인 방법을 찾는다.", mapOf("전략" to 1, "힐링" to -1))
    )),
    TestQuestion("Q3. 게임의 세계관에 대한 당신의 선호도는?", listOf(
        TestOption("탄탄하고 복잡한 스토리라인과 자유로운 탐험을 선호한다.", mapOf("RPG" to 1, "오픈월드" to 1)),
        TestOption("단순하더라도 압도적인 긴장감과 몰입감을 주는 세계관을 선호한다.", mapOf("공포" to 1, "액션" to -1))
    )),
    TestQuestion("Q4. 새로운 도전을 할 때 당신의 태도는?", listOf(
        TestOption("시행착오를 겪더라도 스스로 해결하며 점진적으로 성장하는 것을 즐긴다.", mapOf("RPG" to 1, "전략" to 1)),
        TestOption("미리 준비된 도구와 무기를 활용해 정면 돌파하는 것을 즐긴다.", mapOf("액션" to 1, "오픈월드" to 1))
    )),
    TestQuestion("Q5. 현재 느끼는 감정 상태는?", listOf(
        TestOption("흥미진진하고 에너지 넘치는 상태", mapOf("액션" to 1, "공포" to 1)),
        TestOption("차분하고 평화로운 상태", mapOf("힐링" to 1, "전략" to -1))
    ))
)


@Composable
fun HomeScreen(context: Context) {

    val scope = rememberCoroutineScope()
    val rawgApi = ApiClient.rawg
    // ITAD API 참조 제거

    var gameList by remember { mutableStateOf<List<Game>>(emptyList()) }
    var filteredList by remember { mutableStateOf<List<Game>>(emptyList()) }

    var search by remember { mutableStateOf("") }
    var selectedKeyword by remember { mutableStateOf<String?>(null) }

    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedMultiplayer by remember { mutableStateOf<Boolean?>(null) }

    var userPreferredGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var useAptitudeFilter by remember { mutableStateOf(false) }

    // 성향 테스트 상태
    var isTesting by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val initialScores = remember {
        mutableStateMapOf(
            "액션" to 0, "공포" to 0, "힐링" to 0, "RPG" to 0, "전략" to 0, "오픈월드" to 0
        )
    }
    var genreScores by remember { mutableStateOf(initialScores.toMutableMap()) }

    var todayGame by remember { mutableStateOf<Game?>(null) }

    // SharedPreferences
    val prefs = context.getSharedPreferences("today_game", Context.MODE_PRIVATE)
    val savedDate = prefs.getString("date", null)
    val savedId = prefs.getInt("game_id", -1)
    val todayDate = LocalDate.now().toString()
    val genrePrefs = context.getSharedPreferences("user_aptitude", Context.MODE_PRIVATE)

    // 상세 모달 상태
    var selectedGameForDetails by remember { mutableStateOf<Game?>(null) }
    // 찜 목록 상태
    var favoriteGameIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val favPrefs = context.getSharedPreferences("user_favorites", Context.MODE_PRIVATE)


    // --------------------------------------
    // 한국어 → RAWG 매핑
    // --------------------------------------
    val horrorList = listOf("horror", "survival horror", "psychological horror")
    val rpgList = listOf("rpg", "role-playing")
    val actionList = listOf("action", "shooter", "fighting")
    val strategyList = listOf("strategy", "tactical")
    val openWorldList = listOf("open world")
    val healingList = listOf("cozy", "relaxing", "wholesome", "calming")

    fun mapToKorean(raw: String): String? {
        val s = raw.lowercase()

        return when {
            horrorList.any { it in s } -> "공포"
            rpgList.any { it in s } -> "RPG"
            actionList.any { it in s } -> "액션"
            strategyList.any { it in s } -> "전략"
            openWorldList.any { it in s } -> "오픈월드"
            healingList.any { it in s } -> "힐링"
            else -> null
        }
    }

    // --------------------------------------
    // 필터 함수
    // --------------------------------------
    fun applyFilters() {
        val filtered = gameList.filter { game ->

            val matchesSearch =
                search.isBlank() ||
                        game.name.contains(search, true)

            val matchesGenre = when {
                selectedKeyword != null -> {
                    when (selectedKeyword) {
                        "공포" -> game.genres.any { horrorList.any { h -> h in it } }
                        "RPG" -> game.genres.any { rpgList.any { h -> h in it } }
                        "액션" -> game.genres.any { actionList.any { h -> h in it } }
                        "전략" -> game.genres.any { strategyList.any { h -> h in it } }
                        "오픈월드" -> game.genres.any { openWorldList.any { h -> h in it } }
                        "힐링" -> game.genres.any { healingList.any { h -> h in it } }
                        else -> true
                    }
                }
                useAptitudeFilter && userPreferredGenres.isNotEmpty() -> {
                    game.genres.any { gameGenre ->
                        val korGameGenre = mapToKorean(gameGenre)
                        userPreferredGenres.contains(korGameGenre)
                    }
                }
                else -> true
            }

            val matchesDifficulty = selectedDifficulty?.let { it == game.difficulty } ?: true
            val matchesTime = when (selectedTime) {
                "짧음" -> game.playTime < 45
                "중간" -> game.playTime in 45..90
                "김" -> game.playTime > 90
                else -> true
            }
            val matchesMulti = selectedMultiplayer?.let { it == game.multiplayer } ?: true

            matchesSearch && matchesGenre && matchesDifficulty && matchesTime && matchesMulti
        }

        filteredList = filtered
    }

    // --------------------------------------
    // 장르 저장 함수
    // --------------------------------------
    fun savePreferredGenres(genres: List<String>) {
        genrePrefs.edit().apply {
            putStringSet("preferred_genres", genres.toSet())
            apply()
        }
        userPreferredGenres = genres
        useAptitudeFilter = true
        applyFilters()
        Toast.makeText(context, "성향 테스트 결과가 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // --------------------------------------
    // 찜하기/찜하기 취소 토글 함수
    // --------------------------------------
    fun toggleFavorite(game: Game) {
        val newSet = if (favoriteGameIds.contains(game.id)) {
            favoriteGameIds - game.id // 제거
        } else {
            favoriteGameIds + game.id // 추가
        }

        // SharedPreferences에 저장
        favPrefs.edit().apply {
            putStringSet("favorite_ids", newSet.map { it.toString() }.toSet())
            apply()
        }
        favoriteGameIds = newSet
        val message = if (favoriteGameIds.contains(game.id)) "${game.name}을(를) 찜 목록에 추가했습니다." else "${game.name}을(를) 찜 목록에서 제외했습니다."
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // --------------------------------------
    // 테스트 시작 및 완료 핸들러
    // --------------------------------------
    fun startTest() {
        genreScores = initialScores.toMutableMap()
        currentQuestionIndex = 0
        isTesting = true
    }

    fun completeTest() {
        isTesting = false

        val finalGenres = genreScores
            .toList()
            .sortedByDescending { (_, score) -> score }
            .take(2)
            .filter { it.second > 0 }
            .map { it.first }

        if (finalGenres.isNotEmpty()) {
            savePreferredGenres(finalGenres)
        } else {
            Toast.makeText(context, "장르를 찾지 못했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }
    }


    // --------------------------------------
    // RAWG 데이터 로드 (가격 로직 제거)
    // --------------------------------------
    LaunchedEffect(true) {
        scope.launch {
            try {
                // 선호 장르 로드
                val savedGenresSet = genrePrefs.getStringSet("preferred_genres", emptySet())
                userPreferredGenres = savedGenresSet?.toList() ?: emptyList()

                // 찜 목록 로드
                val savedFavorites = favPrefs.getStringSet("favorite_ids", emptySet())
                favoriteGameIds = savedFavorites?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()


                val popular = rawgApi.getPopularGames(pageSize = 100).results
                val horror = rawgApi.searchGames("horror", pageSize = 100).results
                val multi = rawgApi.searchGames("multiplayer", pageSize = 100).results
                val healing = rawgApi.searchGames("cozy OR relaxing", pageSize = 100).results

                val combined = (popular + horror + multi + healing).distinctBy { it.id }

                val filteredRawgList = combined.filter { rawgGame ->
                    val ratingValue = (rawgGame.rating?.toString()?.toDoubleOrNull() ?: 0.0)
                    rawgGame.background_image != null && ratingValue >= 0.0
                }

                val list = filteredRawgList.map { g ->

                    // 가격 관련 변수 및 ITAD 로직 완전히 제거

                    val rawRating = (g.rating?.toString()?.toDoubleOrNull() ?: 0.0)
                    val rating = String.format("%.1f", rawRating).toDouble()

                    val difficulty: String = when {
                        rating >= 4.0 -> "어려움"
                        rating >= 2.5 -> "보통"
                        else -> "쉬움"
                    }

                    val playTime = (30..120).random()
                    val genresEn =
                        (g.genres?.map { it.name.lowercase() } ?: emptyList()) +
                                (g.tags?.map { it.name.lowercase() } ?: emptyList())
                    val isMulti = genresEn.any { it.contains("multiplayer") || it.contains("co-op") || it.contains("online") }

                    Game(
                        id = g.id,
                        name = g.name,
                        imageUrl = g.background_image!!,
                        genres = genresEn,
                        rating = rating,
                        difficulty = difficulty,
                        playTime = playTime,
                        multiplayer = isMulti
                        // 가격 필드 삭제됨
                    )
                }

                gameList = list
                filteredList = list

                // --- 오늘의 게임 로직 ---
                if (list.isNotEmpty()) {
                    val qualityList = list.sortedByDescending { it.rating }
                    val existing = if (savedDate == todayDate)
                        qualityList.find { it.id == savedId }
                    else null

                    todayGame = existing ?: qualityList.random()

                    todayGame?.let { game ->
                        if (existing == null) {
                            prefs.edit().apply {
                                putString("date", todayDate)
                                putInt("game_id", game.id)
                                apply()
                            }
                        }
                    }
                } else {
                    todayGame = null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "게임 데이터를 불러오지 못했습니다. 네트워크를 확인하세요.", Toast.LENGTH_LONG).show()
                todayGame = null
            }
        }
    }

    // --------------------------------------
    // UI
    // --------------------------------------
    Scaffold(
        topBar = {
            AppTopBar(
                onTestButtonClick = {
                    startTest()
                }
            )
        },
        containerColor = Color(0xFF0B0C10)
    ) { padding ->

        if (isTesting) {
            // 테스트 진행 중 화면
            AptitudeTestSection(
                question = aptitudeQuestions[currentQuestionIndex],
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = aptitudeQuestions.size,
                onOptionSelected = { scoreImpact ->
                    scoreImpact.forEach { (genre, score) ->
                        genreScores[genre] = (genreScores[genre] ?: 0) + score
                    }

                    if (currentQuestionIndex < aptitudeQuestions.size - 1) {
                        currentQuestionIndex++
                    } else {
                        completeTest()
                    }
                }
            )
        } else {
            // 평상시 홈 화면
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                // ★★★ 오늘의 게임 카드 UI 호출 (직전 복구 상태)
                todayGame?.let { game ->
                    item {
                        TodayGameCard(
                            game = game,
                            onCardClick = { selectedGameForDetails = it }
                        )
                    }
                }

                // 검색 섹션
                item {
                    Column {
                        if (userPreferredGenres.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val genresText = userPreferredGenres.joinToString(", ")
                                Text(
                                    text = "선호 장르: $genresText",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                FilterChip(
                                    label = if (useAptitudeFilter) "성향 필터 ON" else "성향 필터 OFF",
                                    selected = useAptitudeFilter,
                                    onSelected = {
                                        useAptitudeFilter = !useAptitudeFilter
                                        selectedKeyword = null
                                        applyFilters()
                                    }
                                )
                            }
                        } else {
                            AptitudeTestButton(onTestStart = {
                                startTest()
                            })
                        }

                        SearchSection(
                            searchQuery = search,
                            onSearchQueryChange = { search = it },
                            onSearch = {
                                scope.launch {
                                    val res = rawgApi.searchGames(search, pageSize = 100)

                                    val list = res.results
                                        .filter { it.background_image != null && (it.rating?.toString()?.toDoubleOrNull() ?: 0.0) >= 0.0 }
                                        .map { g ->

                                            val rawRating = (g.rating?.toString()?.toDoubleOrNull() ?: 0.0)
                                            val rating = String.format("%.1f", rawRating).toDouble()
                                            val difficulty: String = when {
                                                rating >= 4.0 -> "어려움"
                                                rating >= 2.5 -> "보통"
                                                else -> "쉬움"
                                            }
                                            val genresEn =
                                                (g.genres?.map { it.name.lowercase() } ?: emptyList()) +
                                                        (g.tags?.map { it.name.lowercase() } ?: emptyList())
                                            val isMulti =
                                                genresEn.any { it.contains("multiplayer") || it.contains("co-op") || it.contains("online") }

                                            Game(
                                                id = g.id,
                                                name = g.name,
                                                imageUrl = g.background_image!!,
                                                genres = genresEn,
                                                rating = rating,
                                                difficulty = difficulty,
                                                playTime = (30..120).random(),
                                                multiplayer = isMulti
                                                // 가격 필드 삭제됨
                                            )
                                        }
                                    gameList = list
                                    applyFilters()
                                    Toast.makeText(context, "검색 완료!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            selectedKeyword = selectedKeyword,
                            onKeywordSelect = {
                                selectedKeyword = if (selectedKeyword == it) null else it
                                useAptitudeFilter = false
                                applyFilters()
                            }
                        )
                    }
                }

                // 필터 섹션
                item {
                    FilterSection(
                        selectedDifficulty = selectedDifficulty,
                        onSelectDifficulty = {
                            selectedDifficulty = it
                            applyFilters()
                        },
                        selectedTime = selectedTime,
                        onSelectTime = {
                            selectedTime = it
                            applyFilters()
                        },
                        selectedMultiplayer = selectedMultiplayer,
                        onSelectMultiplayer = {
                            selectedMultiplayer = it
                            applyFilters()
                        }
                    )
                }

                // 추천 게임 그리드
                item {

                    val finalList = filteredList
                        .sortedByDescending { it.rating }
                        .map { g ->
                            val korGenres = g.genres.mapNotNull { mapToKorean(it) }
                            g.copy(genres = korGenres)
                        }

                    RecommendedGrid(
                        games = finalList,
                        favoriteGameIds = favoriteGameIds,
                        onCardClick = { game ->
                            selectedGameForDetails = game
                        },
                        onToggleFavorite = ::toggleFavorite
                    )
                }
            }

            // 상세 모달 표시 로직
            selectedGameForDetails?.let { game ->
                GameDetailsDialog(
                    game = game,
                    onDismiss = { selectedGameForDetails = null }
                    // ★★★ GameDetailsDialog에 mapToKorean 인수가 추가되기 직전
                )
            }
        }
    }
}

// --------------------------------------
// AptitudeTestButton, AptitudeTestSection
// --------------------------------------
@Composable
fun AptitudeTestButton(onTestStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onTestStart),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✨ 성향 테스트 하기 (선호 장르 저장)",
            color = Color.Cyan,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun AptitudeTestSection(
    question: TestQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    onOptionSelected: (Map<String, Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(top = 56.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "MBTI 스타일 성향 테스트",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (questionNumber.toFloat() / totalQuestions.toFloat()),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = Color(0xFFFF6F91),
            trackColor = Color(0xFF2E3038)
        )
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Q${questionNumber}. (${questionNumber}/${totalQuestions})",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF9A9BA1)
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = question.question,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(Modifier.height(40.dp))

        question.options.forEach { option ->
            Button(
                onClick = { onOptionSelected(option.scoreImpact) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E3038),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(option.text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}