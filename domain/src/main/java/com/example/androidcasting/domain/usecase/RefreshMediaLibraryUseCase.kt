package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.repository.MediaRepository

class RefreshMediaLibraryUseCase(private val mediaRepository: MediaRepository) {
    suspend operator fun invoke() = mediaRepository.refresh()
}
