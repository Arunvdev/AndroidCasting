package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.model.CodecInfo
import com.example.androidcasting.domain.repository.CodecRepository

class CheckCompatibilityUseCase(private val codecRepository: CodecRepository) {
    suspend operator fun invoke(targetId: String, codecInfo: CodecInfo) =
        codecRepository.isCompatible(targetId, codecInfo)
}
