package com.example.androidcasting.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onBrowseClick: () -> Unit,
    onDevicesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cast your photos, videos, audio and downloads",
            textAlign = TextAlign.Center
        )
        Button(onClick = onBrowseClick) {
            Text(text = "Browse Media")
        }
        Button(onClick = onDevicesClick) {
            Text(text = "Choose Device")
        }
    }
}
