package com.example.androidcasting.domain.di

import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CodecRepository
import com.example.androidcasting.domain.repository.MediaRepository
import com.example.androidcasting.domain.usecase.AnalyseCodecUseCase
import com.example.androidcasting.domain.usecase.CastMediaUseCase
import com.example.androidcasting.domain.usecase.CheckCompatibilityUseCase
import com.example.androidcasting.domain.usecase.GetMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.ObserveCastingTargetsUseCase
import com.example.androidcasting.domain.usecase.RefreshMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.StopCastingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetMediaLibraryUseCase(mediaRepository: MediaRepository) =
        GetMediaLibraryUseCase(mediaRepository)

    @Provides
    @Singleton
    fun provideRefreshMediaLibraryUseCase(mediaRepository: MediaRepository) =
        RefreshMediaLibraryUseCase(mediaRepository)

    @Provides
    @Singleton
    fun provideAnalyseCodecUseCase(codecRepository: CodecRepository) =
        AnalyseCodecUseCase(codecRepository)

    @Provides
    @Singleton
    fun provideCheckCompatibilityUseCase(codecRepository: CodecRepository) =
        CheckCompatibilityUseCase(codecRepository)

    @Provides
    @Singleton
    fun provideObserveCastingTargetsUseCase(castingRepository: CastingRepository) =
        ObserveCastingTargetsUseCase(castingRepository)

    @Provides
    @Singleton
    fun provideCastMediaUseCase(castingRepository: CastingRepository) =
        CastMediaUseCase(castingRepository)

    @Provides
    @Singleton
    fun provideStopCastingUseCase(castingRepository: CastingRepository) =
        StopCastingUseCase(castingRepository)
}
