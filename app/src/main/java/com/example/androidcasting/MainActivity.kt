package com.example.androidcasting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.example.androidcasting.ui.navigation.CastingNavHost
import com.example.androidcasting.ui.theme.AndroidCastingTheme
import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel

/**
 * Hosts the Compose based navigation flow. The shared [SharedCastingViewModel]
 * coordinates the casting pipeline across feature modules.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SharedCastingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidCastingTheme {
                CastingNavHost(viewModel = viewModel)
            }
        }
    }
}
