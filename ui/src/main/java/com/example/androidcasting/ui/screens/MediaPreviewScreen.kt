package com.example.androidcasting.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.model.MediaType
import com.example.androidcasting.player.PreviewPlayerManager
import androidx.media3.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewScreen(
    mediaItem: MediaItem,
    selectedTargetName: String?,
    previewPlayerManager: PreviewPlayerManager,
    onBack: () -> Unit,
    onCast: () -> Unit,
    onSelectTarget: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mediaItem.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (mediaItem.type) {
                MediaType.VIDEO -> VideoPreview(mediaItem.uri.toString(), previewPlayerManager)
                MediaType.IMAGE -> AsyncImage(
                    model = mediaItem.uri,
                    contentDescription = mediaItem.title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                MediaType.AUDIO -> Text(
                    text = "Audio file selected",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = selectedTargetName ?: "No device selected",
                textAlign = TextAlign.Center
            )
            Button(onClick = onSelectTarget) {
                Text(text = "Choose device")
            }
            Button(onClick = onCast, enabled = selectedTargetName != null) {
                Text(text = "Cast this media")
            }
        }
    }
}

@Composable
private fun VideoPreview(uri: String, previewPlayerManager: PreviewPlayerManager) {
    val context = LocalContext.current
    DisposableEffect(uri) {
        previewPlayerManager.preparePreview(uri)
        onDispose {
            previewPlayerManager.player.stop()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                useController = true
                player = previewPlayerManager.player
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
