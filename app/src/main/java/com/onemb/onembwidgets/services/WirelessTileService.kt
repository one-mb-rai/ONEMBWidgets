package com.onemb.onembwidgets.services

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.onemb.onembwidgets.repository.WifiAdbRepository
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface


class WirelessTileService : TileService() {

    private val uiJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + uiJob)

    override fun onStartListening() {
        super.onStartListening()

        WifiAdbRepository.update()
        uiScope.launch {
            WifiAdbRepository.wirelessDebugState.collect {
                updateTileState(it)
                CoroutineScope(Dispatchers.IO).launch {
                    if(it) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        this@WirelessTileService.startActivity(intent)
                    }
                    val ipAddress = if (it) retrieveDeviceIpAddress() else ""
                    updateServiceLabel(ipAddress)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

    override fun onClick() {
        super.onClick()

        qsTile?.let {
            WifiAdbRepository.isWirelessDebugEnabled = !WifiAdbRepository.isWirelessDebugEnabled
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
                inetAddress.hostAddress?.let {
                    Log.d("IP", it)
                }
                serviceName = inetAddress.hostAddress
            }
        }
        return serviceName
    }

    private fun updateServiceLabel(ipAddress: String?) {
        val tile = qsTile
        if (tile != null) {
            tile.label = "Wireless Debugging$ipAddress"
            tile.updateTile()
        }
    }
}