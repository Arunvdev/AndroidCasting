package com.example.androidcasting.data.repository

import android.media.MediaMetadataRetriever
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.content.Context
import com.example.androidcasting.domain.model.CodecInfo
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CodecCompatibility
import com.example.androidcasting.domain.repository.CodecRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Parses media files and extracts codec, bitrate and dimension metadata using
 * platform APIs. This module can be swapped for FFmpeg when deeper analysis is
 * required.
 */
class MediaCodecRepository(
    private val context: Context
) : CodecRepository {

    override suspend fun analyse(item: MediaItem): CodecInfo = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        val extractor = MediaExtractor()
        val uri = Uri.parse(item.uri)
        try {
            retriever.setDataSource(context, uri)
            extractor.setDataSource(context, uri, null)

            var videoCodec: String? = null
            var audioCodec: String? = null
            var bitrate: Long? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull()
            var width: Int? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            var height: Int? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            var frameRate: Float? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull()

            for (i in 0 until extractor.trackCount) {
                val format: MediaFormat = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
                if (mime.startsWith("video")) {
                    videoCodec = mime
                    width = format.getInteger(MediaFormat.KEY_WIDTH)
                    height = format.getInteger(MediaFormat.KEY_HEIGHT)
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE).toFloat()
                    }
                } else if (mime.startsWith("audio")) {
                    audioCodec = mime
                }
            }

            CodecInfo(
                videoCodec = videoCodec,
                audioCodec = audioCodec,
                bitrate = bitrate,
                container = item.mimeType,
                width = width,
                height = height,
                frameRate = frameRate
            )
        } finally {
            retriever.release()
            extractor.release()
        }
    }

    override suspend fun isCompatible(targetId: String, codecInfo: CodecInfo): CodecCompatibility =
        withContext(Dispatchers.Default) {
            val issues = mutableListOf<String>()
            val videoSupported = codecInfo.videoCodec?.let { SUPPORTED_VIDEO_CODECS.contains(it) } ?: true
            val audioSupported = codecInfo.audioCodec?.let { SUPPORTED_AUDIO_CODECS.contains(it) } ?: true

            if (!videoSupported) {
                issues += "TV may not support this video format."
            }
            if (!audioSupported) {
                issues += "TV may not support this audio codec."
            }
            if (!SUPPORTED_CONTAINERS.contains(codecInfo.container)) {
                issues += "Container may be incompatible, enable compatibility mode."
            }

            CodecCompatibility(
                videoSupported = videoSupported,
                audioSupported = audioSupported,
                reasons = issues
            )
        }

    companion object {
        private val SUPPORTED_VIDEO_CODECS = setOf("video/avc", "video/hevc", "video/mp4v-es", "video/webm")
        private val SUPPORTED_AUDIO_CODECS = setOf("audio/mp4a-latm", "audio/mpeg", "audio/opus", "audio/ac3")
        private val SUPPORTED_CONTAINERS = setOf("video/mp4", "video/avc", "audio/mpeg", "image/jpeg")
    }
}
