/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.conversation

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Ambient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticAmbientOf

/**
 * This [Composable] can be used with a [AmbientBackPressedDispatcher] to intercept a back press (if
 * [enabled]).
 *
 * @param onBackPressed (Event) What to do when back is intercepted
 * @param enabled (state) When to intercept the back navigation
 * @param highPriority (config) Used to make sure this is the first handler in the dispatcher
 *
 */
@Composable
fun backPressHandler(
    onBackPressed: () -> Unit,
    enabled: Boolean = true,
    highPriority: Boolean = false
) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)

    // Remember in Composition a back callback that calls the `onBackPressed` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    val backDispatcher = AmbientBackPressedDispatcher.current

    // On every successful composition, update the callback with the `enabled` value
    DisposableEffect(enabled, highPriority) {
        if (enabled && highPriority) {
            // Since the Navigation Component is also intercepting the back event, make sure
            // that this is the first callback in the dispatcher.
            backCallback.remove()
            backDispatcher.addCallback(backCallback)
        }
        backCallback.isEnabled = enabled
        onDispose { /* backCallback will be removed below. */ }
    }

    DisposableEffect(backDispatcher) {
        // Whenever there's a new dispatcher set up the callback
        backDispatcher.addCallback(backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}

/**
 * This [Ambient] is used to provide an [OnBackPressedDispatcher]:
 *
 * ```
 * Providers(AmbientBackPressedDispatcher provides requireActivity()) { }
 * ```
 *
 * and setting up the callbacks with [backPressHandler].
 */
val AmbientBackPressedDispatcher =
    staticAmbientOf<OnBackPressedDispatcher> { error("No Back Dispatcher provided") }
