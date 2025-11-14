package com.example.androidcasting.domain.repository

import com.example.androidcasting.domain.model.CodecInfo
import com.example.androidcasting.domain.model.MediaItem

interface CodecRepository {
    suspend fun analyse(item: MediaItem): CodecInfo
    suspend fun isCompatible(targetId: String, codecInfo: CodecInfo): CodecCompatibility
}

data class CodecCompatibility(
    val videoSupported: Boolean,
    val audioSupported: Boolean,
    val reasons: List<String>
)
