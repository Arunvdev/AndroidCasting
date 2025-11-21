package com.example.androidcasting.ui.viewmodel

import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.model.CastingTarget

data class CastingUiState(
    val media: List<MediaItem> = emptyList(),
    val selectedMedia: MediaItem? = null,
    val selectedTarget: CastingTarget? = null,
    val isCasting: Boolean = false,
    val warnings: List<String> = emptyList()
)
