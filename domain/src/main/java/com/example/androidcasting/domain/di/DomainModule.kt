package com.example.androidcasting.domain.di

import com.example.androidcasting.domain.usecase.AnalyseCodecUseCase
import com.example.androidcasting.domain.usecase.CastMediaUseCase
import com.example.androidcasting.domain.usecase.CheckCompatibilityUseCase
import com.example.androidcasting.domain.usecase.GetMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.ObserveCastingTargetsUseCase
import com.example.androidcasting.domain.usecase.RefreshMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.StopCastingUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetMediaLibraryUseCase(get()) }
    single { RefreshMediaLibraryUseCase(get()) }
    single { AnalyseCodecUseCase(get()) }
    single { CheckCompatibilityUseCase(get()) }
    single { ObserveCastingTargetsUseCase(get()) }
    single { CastMediaUseCase(get()) }
    single { StopCastingUseCase(get()) }
}
