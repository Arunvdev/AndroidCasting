package com.example.androidcasting.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.model.MediaType
import com.example.androidcasting.domain.repository.MediaRepository

/**
 * Placeholder local repository that serves a handful of remote sample items so
 * UI features like previews and casting flows can be demonstrated without
 * needing device storage access.
 */
class LocalMediaRepository(private val contentResolver: ContentResolver) : MediaRepository {

    override suspend fun getMediaLibrary(): List<MediaItem> = loadMedia()

    override suspend fun refreshMediaLibrary(): List<MediaItem> = loadMedia()

    private suspend fun loadMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        val images = queryImages()
        val videos = queryVideos()
        val media = (images + videos).sortedByDescending { it.second }

        if (media.isNotEmpty()) media.map { it.first } else demoFallback()
    }

    private fun queryImages(): List<Pair<MediaItem, Long>> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val images = mutableListOf<Pair<MediaItem, Long>>()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: "Photo"
                val added = cursor.getLong(dateIndex)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val item = MediaItem(uri = uri, title = name, type = MediaType.IMAGE, thumbnailUri = uri)
                images += item to added
            }
        }
        return images
    }

    private fun queryVideos(): List<Pair<MediaItem, Long>> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED
        )

        val videos = mutableListOf<Pair<MediaItem, Long>>()
        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: "Video"
                val added = cursor.getLong(dateIndex)
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val item = MediaItem(uri = uri, title = name, type = MediaType.VIDEO, thumbnailUri = uri)
                videos += item to added
            }
        }
        return videos
    }

    private fun demoFallback(): List<MediaItem> = listOf(
        MediaItem(
            uri = Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            title = "Big Buck Bunny",
            type = MediaType.VIDEO,
            thumbnailUri = Uri.parse("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217")
        ),
        MediaItem(
            uri = Uri.parse("https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?w=800"),
            title = "Sunset",
            type = MediaType.IMAGE
        )
    )
}
