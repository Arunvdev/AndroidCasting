package com.example.androidcasting.player.di

import com.example.androidcasting.player.PreviewPlayerManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playerModule = module {
    single { PreviewPlayerManager(androidContext()) }
}
