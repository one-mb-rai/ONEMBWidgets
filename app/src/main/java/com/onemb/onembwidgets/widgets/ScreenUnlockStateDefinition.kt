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
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream


/**
 * Provides our own definition of "Glance state" using Kotlin serialization.
 */
object ScreenUnlockStateDefinition : GlanceStateDefinition<ScreenUnlockState> {

    private const val DATA_STORE_FILENAME = "ScreenUnlockState"

    /**
     * Use the same file name regardless of the widget instance to share data between them
     *
     * If you need different state/data for each instance, create a store using the provided fileKey
     */
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, ScreenUnlockStateSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<ScreenUnlockState> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    /**
     * Custom serializer for ScreenUnlockState using Json.
     */
    object ScreenUnlockStateSerializer : Serializer<ScreenUnlockState> {
        override val defaultValue = ScreenUnlockState.Unavailable("no place found")

        override suspend fun readFrom(input: InputStream): ScreenUnlockState = try {
            Json.decodeFromString(
                ScreenUnlockState.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not data: ${exception.message}")
        }

        override suspend fun writeTo(t: ScreenUnlockState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(ScreenUnlockState.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}