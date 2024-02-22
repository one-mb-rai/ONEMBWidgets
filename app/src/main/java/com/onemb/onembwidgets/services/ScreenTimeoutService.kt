package com.onemb.onembwidgets.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.onemb.onembwidgets.repository.GlobalSettingsRepository
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface


class ScreenTimeoutService : TileService() {

    private val uiJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + uiJob)

    override fun onStartListening() {
        super.onStartListening()

//        GlobalSettingsRepository.update()
        uiScope.launch {

//            GlobalSettingsRepository.wirelessDebugState.collect {
//                updateTileState(it)
//                val ipAddress = if(it) retrieveDeviceIpAddress() else ""
//                updateServiceLabel(ipAddress)
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

    override fun onClick() {
        super.onClick()

        qsTile?.let {
            GlobalSettingsRepository.isWirelessDebugEnabled = !GlobalSettingsRepository.isWirelessDebugEnabled
        }
    }

    private fun updateTileState(isWirelessDebuggingOn: Boolean) {
        qsTile?.let {
            if (isWirelessDebuggingOn) {
                it.state = Tile.STATE_ACTIVE
            } else {
                it.state = Tile.STATE_INACTIVE
            }
            it.updateTile()
        }
    }

    private fun retrieveDeviceIpAddress(): String? {
        var serviceName: String? = null
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { inetAddress ->
                inetAddress.hostAddress?.let { Log.d("IP", it) }
                serviceName = inetAddress.hostAddress
            }
        }
        return serviceName
    }

    private fun updateServiceLabel(ipAddress: String?) {
        val tile = qsTile
        if (tile != null) {
            val ip = if(ipAddress !="") "($ipAddress:5555)" else ""
            tile.label = "Wireless Debugging$ip"
            tile.updateTile()
        }
    }
}