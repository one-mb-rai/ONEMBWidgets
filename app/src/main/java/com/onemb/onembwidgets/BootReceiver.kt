package com.onemb.onembwidgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            val sharedPreferences = context.getSharedPreferences("ACTION_USER_PRESENT", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("registered", false)
            editor.apply()

            val serviceIntent = Intent(context, ScreenUnlockCounterService::class.java)
            context.startService(serviceIntent)
        }
    }
}
