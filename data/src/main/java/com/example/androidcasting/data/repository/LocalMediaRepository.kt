package com.example.androidcasting.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
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
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DURATION
            )

            val selection = (
                "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                )
            val selectionArgs = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_NONE.toString()
            )

            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Files.getContentUri("external")
            }

            try {
                val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val queryArgs = android.os.Bundle().apply {
                        putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                        putStringArray(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
                        putString(android.content.ContentResolver.QUERY_ARG_SORT_COLUMNS, MediaStore.Files.FileColumns.DATE_MODIFIED)
                        putInt(android.content.ContentResolver.QUERY_ARG_SORT_DIRECTION, android.content.ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
                        putBoolean(android.content.ContentResolver.QUERY_ARG_MATCH_PENDING, true)
                    }
                    contentResolver.query(collection, projection, queryArgs, null)
                } else {
                    @Suppress("DEPRECATION")
                    contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)
                }

                cursor?.use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val mediaTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                    val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val durationIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idIndex)
                        val mime = cursor.getString(mimeIndex) ?: continue
                        val mediaType = cursor.getInt(mediaTypeIndex)
                        val type = when {
                            mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.PHOTO
                            mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.VIDEO
                            mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> MediaType.AUDIO
                            mime.startsWith("image") -> MediaType.PHOTO
                            mime.startsWith("video") -> MediaType.VIDEO
                            mime.startsWith("audio") -> MediaType.AUDIO
                            else -> MediaType.FILE
                        }

                        val itemUri = ContentUris.withAppendedId(collection, id)
                        val duration = if (durationIndex != -1) cursor.getLong(durationIndex) else 0L

                        items += MediaItem(
                            id = id.toString(),
                            uri = itemUri.toString(),
                            mimeType = mime,
                            title = cursor.getString(titleIndex) ?: "Unknown",
                            type = type,
                            durationMillis = duration,
                            sizeBytes = cursor.getLong(sizeIndex)
                        )
                    }
                }
            } catch (security: SecurityException) {
                // Swallow permission errors until the user grants access.
            }

            mediaFlow.value = items
        }
    }
}
