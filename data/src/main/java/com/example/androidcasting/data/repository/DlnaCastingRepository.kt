package com.example.androidcasting.data.repository

import android.content.Context
import com.example.androidcasting.core.network.LocalHttpServer
import com.example.androidcasting.domain.repository.CastingRepository

class DlnaCastingRepository(private val context: Context, private val localHttpServer: LocalHttpServer): CastingRepository
