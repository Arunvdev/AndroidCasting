package com.example.androidcasting.data.di

import com.example.androidcasting.core.network.LocalHttpServer
import com.example.androidcasting.data.repository.DlnaCastingRepository
import com.example.androidcasting.data.repository.LocalMediaRepository
import com.example.androidcasting.data.repository.MediaCodecRepository
import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CodecRepository
import com.example.androidcasting.domain.repository.MediaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { LocalHttpServer(androidContext().contentResolver) }
    single<MediaRepository> { LocalMediaRepository(androidContext().contentResolver) }
    single<CodecRepository> { MediaCodecRepository(androidContext()) }
    single<CastingRepository> { DlnaCastingRepository(androidContext(), get()) }
}
