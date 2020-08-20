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

package com.example.owl.ui.utils

import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.listSaver
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.runtime.toMutableStateList

/**
 * A simple navigator which maintains a back stack.
 */
class Navigator<T : Parcelable> private constructor(
    initialBackStack: List<T>,
    backDispatcher: OnBackPressedDispatcher
) {
    constructor(
        initial: T,
        backDispatcher: OnBackPressedDispatcher
    ) : this(listOf(initial), backDispatcher)

    private val backStack = initialBackStack.toMutableStateList()
    private val backCallback = object : OnBackPressedCallback(canGoBack()) {
        override fun handleOnBackPressed() {
            back()
        }
    }.also { callback ->
        backDispatcher.addCallback(callback)
    }
    val current: T get() = backStack.last()

    fun back() {
        backStack.removeAt(backStack.lastIndex)
        backCallback.isEnabled = canGoBack()
    }

    fun navigate(destination: T) {
        backStack += destination
        backCallback.isEnabled = canGoBack()
    }

    private fun canGoBack(): Boolean = backStack.size > 1

    companion object {
        /**
         * Serialize the back stack to save to instance state.
         */
        fun <T : Parcelable> saver(backDispatcher: OnBackPressedDispatcher) =
            listSaver<Navigator<T>, T>(
                save = { navigator -> navigator.backStack.toList() },
                restore = { backstack -> Navigator(backstack, backDispatcher) }
            )
    }
}

/**
 * An effect for handling presses of the device back button.
 */
@Composable
fun backHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    val backCallback = remember(onBack) {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }
    onCommit(enabled) {
        backCallback.isEnabled = enabled
    }

    val dispatcher = BackDispatcherAmbient.current
    onCommit(backCallback) {
        dispatcher.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

/**
 * An [androidx.compose.runtime.Ambient] providing the current [OnBackPressedDispatcher]. You must
 * [provide][androidx.compose.runtime.Providers] a value before use.
 */
internal val BackDispatcherAmbient = staticAmbientOf<OnBackPressedDispatcher> {
    error("No Back Dispatcher provided")
}
