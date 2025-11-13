package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CastingTarget

class CastMediaUseCase(private val castingRepository: CastingRepository) {
    suspend operator fun invoke(target: CastingTarget, mediaItem: MediaItem) {
        castingRepository.castTo(target, mediaItem)
    }
}
