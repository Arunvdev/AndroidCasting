package com.example.androidcasting.data.repository

import android.content.ContentResolver
import android.provider.MediaStore
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.model.MediaType
import com.example.androidcasting.domain.repository.MediaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Scans the device media store for downloadable media. The implementation uses
 * [MediaStore] for compatibility and can be extended to support SAF browsing or
 * custom file pickers.
 */
class LocalMediaRepository(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MediaRepository {

    private val mediaFlow = MutableStateFlow<List<MediaItem>>(emptyList())

    override fun observeMedia(): Flow<List<MediaItem>> = mediaFlow.asStateFlow()

    override suspend fun refresh() {
        withContext(dispatcher) {
            val items = mutableListOf<MediaItem>()
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DURATION
            )

            contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                null,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC"
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                while (cursor.moveToNext()) {
                    val mime = cursor.getString(mimeIndex) ?: continue
                    val type = when {
                        mime.startsWith("image") -> MediaType.PHOTO
                        mime.startsWith("video") -> MediaType.VIDEO
                        mime.startsWith("audio") -> MediaType.AUDIO
                        else -> MediaType.FILE
                    }
                    items += MediaItem(
                        id = cursor.getString(idIndex) ?: UUID.randomUUID().toString(),
                        uri = cursor.getString(dataIndex),
                        mimeType = mime,
                        title = cursor.getString(titleIndex) ?: "Unknown",
                        type = type,
                        durationMillis = cursor.getLong(durationIndex),
                        sizeBytes = cursor.getLong(sizeIndex)
                    )
                }
            }
            mediaFlow.value = items
        }
    }
}
