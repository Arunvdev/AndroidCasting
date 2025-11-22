package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem

interface CodecRepository {
    suspend fun analyse(mediaItem: MediaItem): List<String>
    suspend fun checkCompatibility(mediaItem: MediaItem, target: CastingTarget): List<String>
}
