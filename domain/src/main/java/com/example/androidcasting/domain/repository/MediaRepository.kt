package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.MediaItem

interface MediaRepository {
    suspend fun getMediaLibrary(): List<MediaItem>
    suspend fun refreshMediaLibrary(): List<MediaItem>
}
