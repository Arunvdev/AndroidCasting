package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CodecRepository

class AnalyseCodecUseCase(private val codecRepository: CodecRepository) {
    suspend operator fun invoke(mediaItem: MediaItem): List<String> = codecRepository.analyse(mediaItem)
}
