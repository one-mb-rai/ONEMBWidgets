/**
 * ScreenUnlockCounterReceiver is a receiver class extending GlanceAppWidgetReceiver,
 * designed for handling broadcast events related to screen unlocks.
 */
package com.onemb.onembwidgets.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.onemb.onembwidgets.ScreenUnlockCounterService
import com.onemb.onembwidgets.db.ScreenUnlockCounterDb
import com.onemb.onembwidgets.db.ScreenUnlockCounterInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * The ScreenUnlockCounterReceiver class extends GlanceAppWidgetReceiver and is responsible
 * for receiving and handling broadcast events related to screen unlocks.
 */
class ScreenUnlockCounterReceiver: GlanceAppWidgetReceiver() {

    /**
     * Returns an instance of the ScreenUnlockCounterWidget, which extends GlanceAppWidget.
     * This is used for widget-related operations.
     */
    override val glanceAppWidget: GlanceAppWidget
        get() = ScreenUnlockCounterWidget()

    /**
     * Overrides the onReceive method to handle the broadcast event.
     * Updates the screen unlock counter using the ScreenUnlockCounterWidget.
     */
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        context.let {
            CoroutineScope(Dispatchers.Default).launch {
                if (intent.action != null && intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                    widgetUpdate(context)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sharedPreferences = context.getSharedPreferences("ACTION_USER_PRESENT", Context.MODE_PRIVATE)
        val registered = sharedPreferences.getBoolean("registered", false)
        if (!registered) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("registered", true)
            editor.apply()
            val serviceIntent = Intent(context, ScreenUnlockCounterService::class.java)
            context.startService(serviceIntent)
        }
        CoroutineScope(Dispatchers.Default).launch {
            widgetUpdate(context)
        }
    }

    private fun widgetUpdate(context: Context) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )
        val database = ScreenUnlockCounterDb.getInstance(context)
        val counterDao = database.screenUnlockCounterDao()

        val existingCounter = counterDao.getCounterByDate(currentDate)

        if (existingCounter == null) {
            val newCounter = ScreenUnlockCounterInterface(date = currentDate, counter = 1)
            counterDao.insert(newCounter)
        } else {
            val updatedCounterValue = existingCounter.counter + 1
            counterDao.updateCounter(currentDate, updatedCounterValue)
        }

        val workRequest = OneTimeWorkRequestBuilder<ScreenUnlockWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        try {
            context.unregisterReceiver(this)
            ScreenUnlockWorker.cancel(context)
        } catch (e:Exception) {
            Log.e("Error", e.message.toString())
        }

    }
}