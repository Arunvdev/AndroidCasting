package com.example.androidcasting.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.ui.components.MediaGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaBrowserScreen(
    media: List<MediaItem>,
    onBack: () -> Unit,
    onPreview: (MediaItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Media Browser") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel), contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        MediaGrid(
            media = media,
            onClick = onPreview,
            modifier = Modifier.padding(padding)
        )
    }
}
