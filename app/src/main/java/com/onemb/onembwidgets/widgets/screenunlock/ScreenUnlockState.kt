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

package com.onemb.onembwidgets.widgets.screenunlock

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenUnlockState {
    @Serializable
    data object Loading : ScreenUnlockState

    @Serializable
    data class Available(
        val currentData: ScreenUnlockData
    ) : ScreenUnlockState

    @Serializable
    data class Unavailable(val message: String) : ScreenUnlockState
}

@Serializable
data class ScreenUnlockData(
    var id: Long,
    var date: String,
    var counter: Int
)