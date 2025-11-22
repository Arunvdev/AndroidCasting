package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.repository.CastingRepository

class StopCastingUseCase(private val castingRepository: CastingRepository) {
    suspend operator fun invoke() = castingRepository.stopCasting()
}
