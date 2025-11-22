package com.example.androidcasting.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.model.MediaType
import com.example.androidcasting.domain.repository.MediaRepository

/**
 * Placeholder local repository that serves a handful of remote sample items so
 * UI features like previews and casting flows can be demonstrated without
 * needing device storage access.
 */
class LocalMediaRepository(private val contentResolver: ContentResolver) : MediaRepository {

    private val sampleMedia = listOf(
        MediaItem(
            uri = Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            title = "Big Buck Bunny",
            type = MediaType.VIDEO,
            thumbnailUri = Uri.parse("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217")
        ),
        MediaItem(
            uri = Uri.parse("https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4"),
            title = "Sample Clip",
            type = MediaType.VIDEO,
            thumbnailUri = Uri.parse("https://via.placeholder.com/800x450.png?text=Sample+Video")
        ),
        MediaItem(
            uri = Uri.parse("https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?w=800"),
            title = "Sunset",
            type = MediaType.IMAGE
        ),
        MediaItem(
            uri = Uri.parse("https://images.unsplash.com/photo-1511765224389-37f0e77cf0eb?w=800"),
            title = "City",
            type = MediaType.IMAGE
        )
    )

    override suspend fun getMediaLibrary(): List<MediaItem> = sampleMedia

    override suspend fun refreshMediaLibrary(): List<MediaItem> = sampleMedia
}
