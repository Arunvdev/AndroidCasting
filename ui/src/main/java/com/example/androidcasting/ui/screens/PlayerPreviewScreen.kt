package com.example.androidcasting.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CodecCompatibility

@OptIn(UnstableApi::class)
@Composable
fun PlayerPreviewScreen(
    mediaItem: MediaItem?,
    compatibility: CodecCompatibility?,
    warnings: List<String>,
    player: ExoPlayer,
    onBack: () -> Unit,
    onCast: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mediaItem?.title ?: "Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel), contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        setShowPreviousButton(false)
                        setShowNextButton(false)
                    }
                },
                update = { view ->
                    if (view.player !== player) {
                        view.player = player
                    }
                }
            )
            if (warnings.isNotEmpty()) {
                Text(text = warnings.joinToString(separator = "\n"))
            }
            Button(onClick = onCast, enabled = mediaItem != null) {
                Text(text = if (compatibility?.videoSupported == false || compatibility?.audioSupported == false) {
                    "Enable Compatibility Mode"
                } else {
                    "Cast Now"
                })
            }
        }
    }
}
