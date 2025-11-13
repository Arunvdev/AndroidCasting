package com.example.androidcasting.core.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Centralises security related operations including encrypted storage and
 * runtime integrity hints. The implementation purposely avoids logging any
 * sensitive information and exposes suspension functions to keep all work on
 * background threads.
 */
class SecurityManager(private val context: Context) {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val securePrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun saveToken(key: String, value: ByteArray) = withContext(Dispatchers.IO) {
        securePrefs.edit().putString(key, Base64.encodeToString(value, Base64.NO_WRAP)).apply()
    }

    suspend fun readToken(key: String): ByteArray? = withContext(Dispatchers.IO) {
        securePrefs.getString(key, null)?.let { Base64.decode(it, Base64.NO_WRAP) }
    }

    fun isDebuggable(): Boolean = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
