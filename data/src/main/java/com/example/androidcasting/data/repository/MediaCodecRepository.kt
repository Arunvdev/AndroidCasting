package com.example.androidcasting.data.repository

import android.content.Context
import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CodecRepository
import javax.inject.Inject

class MediaCodecRepository @Inject constructor(private val context: Context) : CodecRepository {
    override suspend fun analyse(mediaItem: MediaItem): List<String> = emptyList()

    override suspend fun checkCompatibility(mediaItem: MediaItem, target: CastingTarget): List<String> =
        emptyList()
}
