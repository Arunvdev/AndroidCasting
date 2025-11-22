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
    isCasting: Boolean,
    selectedMediaTitle: String?,
    selectedTargetName: String?,
    onBrowseClick: () -> Unit,
    onDevicesClick: () -> Unit,
    onStopCasting: () -> Unit
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
        if (isCasting && selectedMediaTitle != null && selectedTargetName != null) {
            Text(
                text = "Casting \"$selectedMediaTitle\" to $selectedTargetName",
                textAlign = TextAlign.Center
            )
            Button(onClick = onStopCasting) {
                Text(text = "Stop Casting")
            }
        }
        Button(onClick = onBrowseClick) {
            Text(text = "Browse Media")
        }
        Button(onClick = onDevicesClick) {
            Text(text = "Choose Device")
        }
    }
}
