package com.example.androidcasting.ui.viewmodel

import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem

/**
 * UI state shared across navigation destinations.
 */
data class CastingUiState(
    val media: List<MediaItem> = emptyList(),
    val selectedMedia: MediaItem? = null,
    val selectedTarget: CastingTarget? = null,
    val availableTargets: List<CastingTarget> = emptyList(),
    val isCasting: Boolean = false,
    val warnings: List<String> = emptyList()
)
