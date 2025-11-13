package com.example.androidcasting.domain.model

data class MediaItem(
    val id: String,
    val uri: String,
    val mimeType: String,
    val title: String,
    val type: MediaType,
    val durationMillis: Long = 0L,
    val sizeBytes: Long = 0L,
    val codecInfo: CodecInfo? = null
)

data class CodecInfo(
    val videoCodec: String?,
    val audioCodec: String?,
    val bitrate: Long?,
    val container: String?,
    val width: Int?,
    val height: Int?,
    val frameRate: Float?
)

enum class MediaType { PHOTO, VIDEO, AUDIO, FILE }
