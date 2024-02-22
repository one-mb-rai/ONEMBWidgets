/**
 * ScreenUnlockCounterReceiver is a receiver class extending GlanceAppWidgetReceiver,
 * designed for handling broadcast events related to screen unlocks.
 */
package com.onemb.onembwidgets.widgets.screentimeout

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver


class ScreenTimeoutReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = ScreenTimeoutWidget()


//    override fun onEnabled(context: Context) {
//        super.onEnabled(context)
//    }
//
//    override fun onDisabled(context: Context) {
//        super.onDisabled(context)
//    }
}