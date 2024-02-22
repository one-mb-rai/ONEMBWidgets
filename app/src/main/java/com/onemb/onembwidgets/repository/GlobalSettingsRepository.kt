package com.onemb.onembwidgets.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

object GlobalSettingsRepository {

    private const val ADB_WIFI_ENABLED = "adb_wifi_enabled"

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, ex ->
        if (ex !is CancellationException) {
            Log.e("GlobalSettingsRepository", "Coroutine exception", ex)
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
    private var contentResolver: ContentResolver? = null

    var isWirelessDebugEnabled: Boolean
        get() = try {
            Settings.Global.getInt(contentResolver, ADB_WIFI_ENABLED, 0) == 1
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        set(value) {
            ioScope.launch {
                Settings.Global.putInt(contentResolver, ADB_WIFI_ENABLED, if (value) 1 else 0)
                wirelessDebugState.emit(isWirelessDebugEnabled)
            }
        }

    private val wirelessDebugState: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(isWirelessDebugEnabled)
    }

    fun init(context: Context) {
        contentResolver = context.contentResolver
    }

    fun update() {
        ioScope.launch {
            wirelessDebugState.emit(isWirelessDebugEnabled)
        }
    }
}