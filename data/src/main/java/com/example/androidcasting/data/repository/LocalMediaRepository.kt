package com.example.androidcasting.data.repository

import android.content.ContentResolver
import com.example.androidcasting.domain.repository.MediaRepository

class LocalMediaRepository(private val contentResolver: ContentResolver): MediaRepository
