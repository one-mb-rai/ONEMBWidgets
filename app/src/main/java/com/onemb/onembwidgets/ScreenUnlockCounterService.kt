package com.onemb.onembwidgets

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.onemb.onembwidgets.widgets.ScreenUnlockCounterReceiver
import com.onemb.onembwidgets.widgets.ScreenUnlockWorker

class ScreenUnlockCounterService : Service() {
    private val receiver = ScreenUnlockCounterReceiver()

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = this.getSharedPreferences("ACTION_USER_PRESENT", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("registered", false)
        editor.apply()
        try {
            unregisterReceiver(receiver)
        } catch (e:Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }



    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = this.getSharedPreferences("ACTION_USER_PRESENT", Context.MODE_PRIVATE)
        val registered = sharedPreferences.getBoolean("registered", false)
        if (!registered) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("registered", true)
            editor.apply()
            val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
            try {
                registerReceiver(receiver, filter)
            } catch (e:Exception) {
                Log.e("Error", e.message.toString())
            }
            val workRequest = OneTimeWorkRequestBuilder<ScreenUnlockWorker>()
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
        }
    }
}