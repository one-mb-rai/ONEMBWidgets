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

package com.onemb.onembwidgets

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.onemb.onembwidgets.widgets.screenunlock.ScreenUnlockCounterReceiver
import com.onemb.onembwidgets.widgets.screenunlock.ScreenUnlockWorker
import kotlinx.coroutines.delay
import java.time.Duration

class GlobalWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private val uniqueWorkName = GlobalWorker::class.java.simpleName
        private val receiver = ScreenUnlockCounterReceiver()

        /**
         *
         * @param force set to true to replace any ongoing work and expedite the request
         */
        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<GlobalWorker>(
                Duration.ofMinutes(4)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

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
        return try {
            val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
            try {
                registerReceiver(applicationContext, receiver, filter,
                    ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
                )
                val workRequest = OneTimeWorkRequestBuilder<ScreenUnlockWorker>()
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            } catch (e: Exception) {
                try {
                    applicationContext.unregisterReceiver(receiver)
                    delay(5000)
                    registerReceiver(applicationContext, receiver, filter,
                        ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
                    )
                    val workRequest = OneTimeWorkRequestBuilder<ScreenUnlockWorker>()
                        .build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                } catch (_: Exception) { }
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
