package com.example.androidcasting.domain.model

import android.net.Uri

/**
 * Represents a piece of local media. [thumbnailUri] can point to a still image
 * so that lists and previews can render quickly even for videos.
 */
data class MediaItem(
    val uri: Uri,
    val title: String,
    val type: MediaType,
    val thumbnailUri: Uri = uri
)

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO
}
