package com.onemb.onembwidgets.widgets.screentimeout

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.onemb.onembwidgets.R

class ScreenTimeoutWidget: GlanceAppWidget() {
    private val SCREEN_TIMEOUT = 60000 * 5

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val applicationContext = LocalContext.current.applicationContext

                Box(
                    modifier = GlanceModifier.
                        fillMaxSize().background(
                            imageProvider = ImageProvider(R.drawable.st)
                        ).clickable {
                        if (Settings.System.canWrite(applicationContext)) {
                            changeScreenTimeout(SCREEN_TIMEOUT, context);
                            Toast.makeText(applicationContext, "Screen timeout changed", Toast.LENGTH_SHORT).show();
                        } else {
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.setData(Uri.parse("package:" + applicationContext.packageName))
                            applicationContext.startActivity(intent)
                            Toast.makeText(applicationContext, "Please grant the WRITE_SETTINGS permission to change screen timeout", Toast.LENGTH_SHORT).show();
                        }
                    })
                {
                    Text("Hello Stack!")
                }
            }
        }
    }
}


private fun changeScreenTimeout(timeout: Int, context :Context) {
    try {
        val contentResolver = context.contentResolver
        // Update the system setting for screen timeout
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
    } catch (e: Exception) {
        // Handle exceptions, such as SecurityException if permission is not granted
        e.printStackTrace()
    }
}