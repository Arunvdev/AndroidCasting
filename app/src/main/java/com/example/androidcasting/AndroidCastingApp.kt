package com.example.androidcasting

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. The app relies on dependency injection to coordinate
 * modules responsible for discovery, casting and playback. Additional security
 * hardening (anti-tampering, integrity checks) should be configured here in a
 * production environment.
 */
@HiltAndroidApp
class AndroidCastingApp : Application()
