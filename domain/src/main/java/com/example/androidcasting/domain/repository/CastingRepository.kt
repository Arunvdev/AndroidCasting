package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface CastingRepository {
    fun observeTargets(): Flow<List<CastingTarget>>
    suspend fun cast(mediaItem: MediaItem, target: CastingTarget)
    suspend fun stopCasting()
}
