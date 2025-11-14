package com.example.androidcasting.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configures ExoPlayer for local preview playback. Track selector is tuned to
 * prioritise the highest quality video and audio while still allowing fallback
 * to software decoding for exotic codecs.
 */
@Singleton
class PreviewPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters().setForceHighestSupportedBitrate(true))
    }

    val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
    }

    fun preparePreview(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    fun release() {
        player.release()
    }
}
