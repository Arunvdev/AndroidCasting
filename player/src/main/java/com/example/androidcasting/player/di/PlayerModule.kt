package com.example.androidcasting.player.di

import com.example.androidcasting.player.PreviewPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun providePreviewPlayerManager(manager: PreviewPlayerManager): PreviewPlayerManager = manager
}
