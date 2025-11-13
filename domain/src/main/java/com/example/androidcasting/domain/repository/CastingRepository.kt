package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface CastingRepository {
    fun availableRenderers(): Flow<List<CastingTarget>>
    suspend fun castTo(target: CastingTarget, mediaItem: MediaItem)
    suspend fun stopCasting()
}

data class CastingTarget(
    val id: String,
    val friendlyName: String,
    val protocols: List<String>,
    val supportsCompatibilityMode: Boolean
)
