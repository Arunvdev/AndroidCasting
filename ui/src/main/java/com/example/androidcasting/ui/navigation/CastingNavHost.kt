package com.example.androidcasting.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcasting.ui.screens.MediaBrowserScreen
import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel

@Composable
fun CastingNavHost(viewModel: SharedCastingViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "mediaBrowser") {
        composable("mediaBrowser") {
            MediaBrowserScreen(media = uiState.media, onBack = {}, onPreview = {})
        }
    }
}
