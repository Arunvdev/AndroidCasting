package com.example.androidcasting.data.repository

import android.content.Context
import com.example.androidcasting.core.network.LocalHttpServer
import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CastingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class DlnaCastingRepository @Inject constructor(
    private val context: Context,
    private val localHttpServer: LocalHttpServer
) : CastingRepository {

    private val targets = MutableStateFlow(
        listOf(
            CastingTarget("Living Room TV", listOf("DLNA", "HTTP")),
            CastingTarget("Bedroom Chromecast", listOf("Cast", "HTTP"))
        )
    )

    override fun observeTargets(): Flow<List<CastingTarget>> = targets.asStateFlow()

    override suspend fun cast(mediaItem: MediaItem, target: CastingTarget) {
        // In a real implementation this would send DLNA commands. For now we
        // just ensure the local server is running so URIs can be shared.
        localHttpServer.start()
    }

    override suspend fun stopCasting() {
        localHttpServer.stop()
    }
}
