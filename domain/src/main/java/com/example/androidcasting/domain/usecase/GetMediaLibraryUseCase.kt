package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.MediaRepository

class GetMediaLibraryUseCase(private val mediaRepository: MediaRepository) {
    suspend operator fun invoke(): List<MediaItem> = mediaRepository.getMediaLibrary()
}
