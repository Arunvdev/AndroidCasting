package com.example.androidcasting.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidcasting.domain.model.MediaItem

@Composable
fun MediaGrid(media: List<MediaItem>, onClick: (MediaItem) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(128.dp), modifier = modifier) {
        items(media) { mediaItem ->
            Text(text = mediaItem.title, modifier = Modifier.clickable { onClick(mediaItem) })
        }
    }
}
