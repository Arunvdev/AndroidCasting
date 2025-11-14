package com.example.androidcasting.domain.usecase

import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CastingTarget
import kotlinx.coroutines.flow.Flow

class ObserveCastingTargetsUseCase(private val castingRepository: CastingRepository) {
    operator fun invoke(): Flow<List<CastingTarget>> = castingRepository.availableRenderers()
}
