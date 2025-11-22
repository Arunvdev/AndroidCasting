package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CastingRepository

class CastMediaUseCase(private val castingRepository: CastingRepository) {
    suspend operator fun invoke(mediaItem: MediaItem, target: CastingTarget) {
        castingRepository.cast(mediaItem, target)
    }
}
