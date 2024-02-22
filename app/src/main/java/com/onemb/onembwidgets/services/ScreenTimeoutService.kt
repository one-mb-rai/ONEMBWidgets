package com.onemb.onembwidgets.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
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

    private fun changeScreenTimeout(timeout: Int, context : Context) {
        try {
            val contentResolver = context.contentResolver

            if(Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT) <= 120000) {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    timeout
                )
            } else {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    120000
                )
            }
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
                changeScreenTimeout(ScreenTimeoutSettingsRepository.screenTimeout, this);
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