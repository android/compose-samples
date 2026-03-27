/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.utils

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.core.util.Consumer

@Composable
fun NewIntentEffect(onNewIntent: (Intent) -> Unit) {
    val activity = LocalActivity.current as? ComponentActivity
    val currentOnNewIntent by rememberUpdatedState(onNewIntent)

    DisposableEffect(activity) {
        if (activity == null) return@DisposableEffect onDispose {}

        val onNewIntentListener = Consumer<Intent> { intent ->
            activity.intent = intent
            currentOnNewIntent(intent)
        }

        activity.addOnNewIntentListener(onNewIntentListener)

        onDispose {
            activity.removeOnNewIntentListener(onNewIntentListener)
        }
    }
}
