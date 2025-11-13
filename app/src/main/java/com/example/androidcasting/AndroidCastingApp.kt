package com.example.androidcasting

import android.app.Application
import com.example.androidcasting.data.di.dataModule
import com.example.androidcasting.domain.di.domainModule
import com.example.androidcasting.player.di.playerModule
import com.example.androidcasting.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application entry point. The app relies on dependency injection to coordinate
 * modules responsible for discovery, casting and playback. Additional security
 * hardening (anti-tampering, integrity checks) should be configured here in a
 * production environment.
 */
class AndroidCastingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AndroidCastingApp)
            modules(
                dataModule,
                domainModule,
                playerModule,
                uiModule
            )
        }
    }
}
