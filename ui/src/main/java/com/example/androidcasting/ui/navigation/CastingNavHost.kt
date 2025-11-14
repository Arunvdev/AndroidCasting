package com.example.androidcasting.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcasting.ui.screens.CastingControllerScreen
import com.example.androidcasting.ui.screens.DeviceListScreen
import com.example.androidcasting.ui.screens.HomeScreen
import com.example.androidcasting.ui.screens.MediaBrowserScreen
import com.example.androidcasting.ui.screens.PlayerPreviewScreen
import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Browser : Screen("browser")
    data object Devices : Screen("devices")
    data object Preview : Screen("preview")
    data object Controller : Screen("controller")
}

@Composable
fun CastingNavHost(
    viewModel: SharedCastingViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onBrowseClick = { navController.navigate(Screen.Browser.route) },
                onDevicesClick = { navController.navigate(Screen.Devices.route) }
            )
        }
        composable(Screen.Browser.route) {
            val media by viewModel.media.collectAsState()
            MediaBrowserScreen(
                media = media,
                onBack = { navController.popBackStack() },
                onPreview = {
                    viewModel.selectMedia(it)
                    navController.navigate(Screen.Preview.route)
                }
            )
        }
        composable(Screen.Devices.route) {
            val devices by viewModel.devices.collectAsState()
            DeviceListScreen(
                devices = devices,
                onBack = { navController.popBackStack() },
                onDeviceSelected = {
                    viewModel.selectTarget(it)
                    navController.navigate(Screen.Controller.route)
                }
            )
        }
        composable(Screen.Preview.route) {
            val selected by viewModel.selectedMedia.collectAsState()
            val state by viewModel.castingState.collectAsState()
            PlayerPreviewScreen(
                mediaItem = selected,
                compatibility = state.compatibility,
                warnings = state.warnings,
                player = viewModel.previewPlayer,
                onBack = { navController.popBackStack() },
                onCast = {
                    viewModel.prepareCasting()
                    navController.navigate(Screen.Controller.route)
                }
            )
        }
        composable(Screen.Controller.route) {
            val castingState by viewModel.castingState.collectAsState()
            CastingControllerScreen(
                state = castingState,
                onBack = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                onStopCasting = { viewModel.stopCasting() }
            )
        }
    }
}
