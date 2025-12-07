package com.example.gamecurator.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gamecurator.data.model.Game

@Composable
fun GameCard(game: Game) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { }
    ) {
        Row(Modifier.padding(12.dp)) {

            Image(
                painter = rememberAsyncImagePainter(game.image),
                contentDescription = null,
                modifier = androidx.compose.ui.Modifier.size(60.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(game.name, style = MaterialTheme.typography.titleMedium)
                Text(game.genre, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
