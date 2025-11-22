package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CodecRepository

class CheckCompatibilityUseCase(private val codecRepository: CodecRepository) {
    suspend operator fun invoke(mediaItem: MediaItem, target: CastingTarget): List<String> =
        codecRepository.checkCompatibility(mediaItem, target)
}
