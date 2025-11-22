package com.example.androidcasting.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidcasting.ui.viewmodel.CastingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastingControllerScreen(
    state: CastingUiState,
    onBack: () -> Unit,
    onStopCasting: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Casting Controller") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel), contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            val mediaTitle = state.selectedMedia?.title ?: "None"
            val deviceName = state.selectedTarget?.friendlyName ?: "None"

            Text(text = "Media: $mediaTitle")
            Text(text = "Device: $deviceName")
            if (state.isCasting) {
                Text(text = "Casting in progress")
            }
            if (state.warnings.isNotEmpty()) {
                Text(text = state.warnings.joinToString(separator = "\n"))
            }
            Button(onClick = onStopCasting, enabled = state.isCasting) {
                Text(text = "Stop Casting")
            }
        }
    }
}
