package com.example.mygamecurator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mygamecurator.data.Game

// ================== Today Game Card ==================
@Composable
fun TodayGameCard(game: Game) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1D22))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. 이미지 (배경)
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. 이미지 위에 어두운 오버레이 추가
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // 3. 텍스트 정보
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "🌟 오늘의 추천 게임",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6F91),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // 평점이 0.0보다 클 때만 평점 표시
                if (game.rating > 0.0) {
                    Text(
                        text = "평점: ${"%.1f".format(game.rating)} | 장르: ${game.genres.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                } else {
                    // 평점 정보가 없을 때 장르만 표시
                    Text(
                        text = "장르: ${game.genres.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


// ================== Grid 카드 ==================
@Composable
fun GameGridCard(game: Game) {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1D22))
            .clickable { }
            .padding(12.dp)
    ) {
        AsyncImage(
            model = game.imageUrl,
            contentDescription = game.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = game.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        // 평점이 0.0보다 클 때만 평점 표시
        if (game.rating > 0.0) {
            Text(
                text = "평점: ${"%.1f".format(game.rating)}",
                color = Color(0xFFBBBBBB),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}