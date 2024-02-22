package com.onemb.onembwidgets.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

object ScreenTimeoutSettingsRepository {

    const val screenTimeout = 60000000
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, ex ->
        if (ex !is CancellationException) {
            Log.e("GlobalSettingsRepository", "Coroutine exception", ex)
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
    private var contentResolver: ContentResolver? = null

    var isScreenTimeoutState: Boolean
        get() = try {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT) > 120000
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        set(value) {
            ioScope.launch {
                screenTimeoutState.emit(isScreenTimeoutState)
            }
        }

    val screenTimeoutState: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(isScreenTimeoutState)
    }

    fun init(context: Context) {
        contentResolver = context.contentResolver
    }

    fun update() {
        ioScope.launch {
            screenTimeoutState.emit(isScreenTimeoutState)
        }
    }
}