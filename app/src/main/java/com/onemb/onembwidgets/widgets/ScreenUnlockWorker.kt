/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onemb.onembwidgets.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.onemb.onembwidgets.db.ScreenUnlockCounterDb
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Date
import java.util.Locale

class ScreenUnlockWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private val uniqueWorkName = ScreenUnlockWorker::class.java.simpleName
        /**
         *
         * @param force set to true to replace any ongoing work and expedite the request
         */
        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<ScreenUnlockWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.UPDATE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        /**
         * Cancel any ongoing worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }



    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(ScreenUnlockCounterWidget::class.java)
        val database = ScreenUnlockCounterDb.getInstance(applicationContext)
        val counterDao = database.screenUnlockCounterDao()
        return try {
            setWidgetState(glanceIds, ScreenUnlockState.Loading)

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val data = ScreenUnlockData(0, "", 0)
            data.counter = counterDao.getCounterByDate(currentDate)?.counter ?: 0
            data.date = counterDao.getCounterByDate(currentDate)?.date ?: ""
            data.id = counterDao.getCounterByDate(currentDate)?.id ?: 0

            setWidgetState(glanceIds, ScreenUnlockState.Available(currentData = data))
            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, ScreenUnlockState.Unavailable(e.message.orEmpty()))
            counterDao.nukeTable()
            if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Update the state of all widgets and then force update UI
     */
    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: ScreenUnlockState) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = ScreenUnlockStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        ScreenUnlockCounterWidget().updateAll(context)
    }
}