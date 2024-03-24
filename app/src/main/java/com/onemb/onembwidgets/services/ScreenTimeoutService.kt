package com.onemb.onembwidgets.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.onemb.onembwidgets.ONEMBApplication
import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository
import kotlinx.coroutines.*


class ScreenTimeoutService : TileService() {


    private val uiJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + uiJob)

    override fun onStartListening() {
        super.onStartListening()

        ScreenTimeoutSettingsRepository.update()
        uiScope.launch {
            ScreenTimeoutSettingsRepository.screenTimeoutState.collect {
                updateTileState(it)
            }
        }
    }

    private fun getTimeout(timeout: Int): Long {
        val value: Long = when (timeout) {
            15000 -> 15000 * 2 // 30 sec
            30000 -> 15000 * 4 // 1 min
            60000 -> 15000 * 8 // 2 min
            120000 -> 15000 * 12 // 3 min
            180000 -> 15000 * 20 // 5 min
            else -> {
                30000
            }
        }
        return value
    }

    private fun changeScreenTimeout(context : Context) {
        try {
            val contentResolver = context.contentResolver
            if(Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT) <= 15000) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Int.MAX_VALUE);
            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Int.MIN_VALUE);
            }
            Log.d("TIME", Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT).toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

    override fun onClick() {
        super.onClick()

        qsTile?.let {
            ScreenTimeoutSettingsRepository.isScreenTimeoutState = !ScreenTimeoutSettingsRepository.isScreenTimeoutState
            if (Settings.System.canWrite(applicationContext)) {
                changeScreenTimeout(ONEMBApplication.getAppContext());
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setData(Uri.parse("package:" + applicationContext.packageName))
                applicationContext.startActivity(intent)
            }
        }
    }

    private fun updateTileState(tileActive: Boolean) {
        qsTile?.let {
            if (tileActive) {
                it.state = Tile.STATE_ACTIVE
            } else {
                it.state = Tile.STATE_INACTIVE
            }
            it.updateTile()
        }
    }
}