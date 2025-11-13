package com.example.androidcasting.domain.usecase

import com.example.androidcasting.core.utils.Result
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetMediaLibraryUseCase(private val mediaRepository: MediaRepository) {
    operator fun invoke(): Flow<Result<List<MediaItem>>> =
        mediaRepository.observeMedia()
            .map<Result<List<MediaItem>>> { Result.Success(it) }
            .catch { emit(Result.Error(it)) }
}
