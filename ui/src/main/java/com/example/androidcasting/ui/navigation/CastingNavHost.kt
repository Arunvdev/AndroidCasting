package com.example.androidcasting.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcasting.ui.screens.MediaBrowserScreen
import com.example.androidcasting.ui.screens.MediaPreviewScreen
import com.example.androidcasting.ui.screens.DeviceListScreen
import com.example.androidcasting.ui.screens.HomeScreen
import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel

@Composable
fun CastingNavHost(viewModel: SharedCastingViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onBrowseClick = { navController.navigate("mediaBrowser") },
                onDevicesClick = { navController.navigate("devices") }
            )
        }
        composable("mediaBrowser") {
            MediaBrowserScreen(
                media = uiState.media,
                onBack = { navController.popBackStack() },
                onPreview = {
                    viewModel.selectMedia(it)
                    navController.navigate("preview")
                }
            )
        }
        composable("preview") {
            val selected = uiState.selectedMedia
            if (selected != null) {
                MediaPreviewScreen(
                    mediaItem = selected,
                    selectedTargetName = uiState.selectedTarget?.friendlyName,
                    onBack = { navController.popBackStack() },
                    onCast = {
                        viewModel.startCasting { navController.popBackStack("home", inclusive = false) }
                    },
                    onSelectTarget = { navController.navigate("devices") }
                )
            }
        }
        composable("devices") {
            DeviceListScreen(
                devices = uiState.availableTargets,
                onBack = { navController.popBackStack() },
                onDeviceSelected = {
                    viewModel.selectTarget(it)
                    navController.popBackStack()
                }
            )
        }
    }
}
