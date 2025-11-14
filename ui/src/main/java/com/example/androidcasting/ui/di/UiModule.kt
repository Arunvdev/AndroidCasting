package com.example.androidcasting.ui.di

import com.example.androidcasting.ui.viewmodel.SharedCastingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        SharedCastingViewModel(
            getMediaLibraryUseCase = get(),
            refreshMediaLibraryUseCase = get(),
            observeCastingTargetsUseCase = get(),
            analyseCodecUseCase = get(),
            checkCompatibilityUseCase = get(),
            castMediaUseCase = get(),
            stopCastingUseCase = get(),
            previewPlayerManager = get()
        )
    }
}
