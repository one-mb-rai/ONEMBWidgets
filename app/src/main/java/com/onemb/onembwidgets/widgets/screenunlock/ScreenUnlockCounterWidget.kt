/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget responsible for displaying a screen unlock counter.
 * It uses a DataStore for persisting counter data and updates the counter on screen unlock events.
 */
package com.onemb.onembwidgets.widgets.screenunlock

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget that displays the screen unlock counter.
 * It updates the counter on screen unlock events and provides a Glance theme for widget appearance.
 */
class ScreenUnlockCounterWidget: GlanceAppWidget() {


    override val stateDefinition = ScreenUnlockStateDefinition


    /**
     * Provides the Glance content for the widget.
     * Displays the screen unlock counter using GlanceTheme and Compose components.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val receiver = ScreenUnlockCounterReceiver()
        val calendarIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val weekArray = arrayOf("SA", "SU", "M", "TU", "W", "TH", "F")
        val dayOfWeek = weekArray[calendarIndex]
        provideContent {
            val screenUnlockState = currentState<ScreenUnlockState>()
            GlanceTheme {
                when (screenUnlockState) {
                    ScreenUnlockState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ScreenUnlockState.Available -> {
                        ScreenUnlockCounterLarge(
                            context,
                            receiver,
                            weekArray,
                            dayOfWeek,
                            screenUnlockState.currentData.counter
                        )
                    }

                    is ScreenUnlockState.Unavailable -> {
                        Text("Data not available")
                    }
                }
            }
        }
    }
}


@Composable
fun ScreenUnlockCounterLarge(
    context: Context,
    receiver: ScreenUnlockCounterReceiver,
    weekArray: Array<String>,
    dayOfWeek: String,
    screenUnlockState: Int
) {
    Column(
        modifier = GlanceModifier.fillMaxSize().background(Color.Transparent)
            .clickable {
                val sharedPreferences = context.getSharedPreferences("ACTION_USER_PRESENT", Context.MODE_PRIVATE)
                val registered = sharedPreferences.getBoolean("registered", false)
                val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
                if (!registered) {
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("registered", true)
                    editor.apply()
                    registerReceiver(
                        context, receiver, filter,
                        ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
                    )
                } else {
                    try {
                        context.unregisterReceiver(receiver)
                    } catch (e:Exception) {
                        Log.e("Error", e.message.toString())
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2000)
                        try {
                            registerReceiver(
                                context, receiver, filter,
                                ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
                            )
                        } catch (e:Exception) {
                            Log.e("Error", e.message.toString())
                        }
                    }
                }
                val workRequest = OneTimeWorkRequestBuilder<ScreenUnlockWorker>()
                    .build()
                WorkManager.getInstance(context).enqueue(workRequest)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = GlanceModifier.padding(all = 16.dp).fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                val modifier = GlanceModifier.defaultWeight()
                for (item in weekArray) {
                    if (item == dayOfWeek) {
                        val modifierActive = GlanceModifier.defaultWeight()
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifierActive,
                        ) {
                            Text(
                                text = item,
                                style = TextStyle(
                                    color = ColorProvider(Color.Red)
                                )
                            )
                        }
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifier
                        ) {
                            Text(
                                text = item,
                                style = TextStyle(
                                    color = ColorProvider(Color.White)
                                )
                            )
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalAlignment = Alignment.Start,
                modifier = GlanceModifier.fillMaxSize().padding(start = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Screen Unlocks: ",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = TextUnit(24F, TextUnitType.Sp)
                        )
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "$screenUnlockState",
                        style = TextStyle(
                            color = ColorProvider(Color.Red),
                            fontSize = TextUnit(24F, TextUnitType.Sp)
                        )
                    )
                }
            }

        }
    }
}

