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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.androidcasting.domain.model.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewScreen(
    mediaItem: MediaItem,
    selectedTargetName: String?,
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
            AsyncImage(
                model = mediaItem.uri,
                contentDescription = mediaItem.title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
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
