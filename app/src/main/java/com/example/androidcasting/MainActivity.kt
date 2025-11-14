package com.example.androidcasting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidcasting.ui.navigation.CastingNavHost
import com.example.androidcasting.ui.theme.AndroidCastingTheme
import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

/**
 * Hosts the Compose based navigation flow. The shared [SharedCastingViewModel]
 * coordinates the casting pipeline across feature modules.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: SharedCastingViewModel by viewModel()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.any { it.value }) {
            lifecycleScope.launch { viewModel.refreshLibrary() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMediaPermissionsIfNeeded()
        setContent {
            AndroidCastingTheme {
                CastingNavHost(viewModel = viewModel)
            }
        }
    }

    private fun requestMediaPermissionsIfNeeded() {
        val permissions = requiredPermissions()
        val needsRequest = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needsRequest) {
            permissionLauncher.launch(permissions)
        } else {
            lifecycleScope.launch { viewModel.refreshLibrary() }
        }
    }

    private fun requiredPermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
