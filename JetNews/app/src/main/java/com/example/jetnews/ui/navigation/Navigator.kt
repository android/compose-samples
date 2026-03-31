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

package com.example.jetnews.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

data class PopUpTo(val navKey: NavKey, val inclusive: Boolean = false)

class Navigator(val state: NavigationState) {
    val currentStack: NavBackStack<NavKey>
        get() = state.backStacks[state.topLevelKey] ?: error("Stack for ${state.topLevelKey} not found")

    val currentKey: NavKey
        get() = currentStack.last()

    fun navigate(navKey: NavKey, popUpTo: PopUpTo? = null) {
        if (navKey in state.topLevelKeys) {
            state.topLevelKey = navKey
        }

        if (currentKey == navKey && popUpTo == null) return

        if (popUpTo != null) {
            val index = currentStack.indexOfLast { it == popUpTo.navKey }

            if (index != -1) {
                val fromIndex = if (popUpTo.inclusive) index else index + 1
                if (fromIndex < currentStack.size) {
                    currentStack.subList(fromIndex, currentStack.size).clear()
                }
            }
        }

        if (navKey !in state.topLevelKeys && currentKey != navKey) {
            currentStack.add(navKey)
        }
    }

    fun goUp() {
        // If we're at the base of the current top level key's stack, go back to the primary top-level key's stack.
        if (currentKey == state.topLevelKey) {
            state.topLevelKey = state.primaryTopLevelKey
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
