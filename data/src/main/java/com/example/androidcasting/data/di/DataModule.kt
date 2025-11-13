package com.example.androidcasting.data.di

import android.content.Context
import com.example.androidcasting.core.network.LocalHttpServer
import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CodecRepository
import com.example.androidcasting.domain.repository.MediaRepository
import com.example.androidcasting.data.repository.DlnaCastingRepository
import com.example.androidcasting.data.repository.LocalMediaRepository
import com.example.androidcasting.data.repository.MediaCodecRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMediaRepository(@ApplicationContext context: Context): MediaRepository =
        LocalMediaRepository(context.contentResolver)

    @Provides
    @Singleton
    fun provideCodecRepository(@ApplicationContext context: Context): CodecRepository =
        MediaCodecRepository(context)

    @Provides
    @Singleton
    fun provideCastingRepository(
        @ApplicationContext context: Context,
        localHttpServer: LocalHttpServer
    ): CastingRepository = DlnaCastingRepository(context, localHttpServer)

    @Provides
    @Singleton
    fun provideLocalHttpServer(): LocalHttpServer = LocalHttpServer()
}
