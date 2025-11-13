package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeMedia(): Flow<List<MediaItem>>
    suspend fun refresh()
}
