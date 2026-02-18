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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import kotlinx.serialization.Serializable

@Serializable
data class HomeKey(val postId: String? = null) : NavKey

@Serializable
object InterestsKey : NavKey

fun getNavigationKeys(startKey: HomeKey) = listOf(startKey, InterestsKey)

@Composable
fun rememberNavigationState(startKey: HomeKey): NavigationState {
    val currentKeys = rememberNavBackStack(startKey)
    val backStacks = buildMap {
        getNavigationKeys(startKey)
            .forEach {
                put(it::class.toString(), rememberNavBackStack(it))
            }
    }.toMutableMap()
    return remember(startKey) { NavigationState(currentKeys, backStacks) }
}

/**
 * Contains the navigation state
 */
class NavigationState(val currentKeys: NavBackStack<NavKey>, val backStacks: MutableMap<String, NavBackStack<NavKey>>) {

    val currentKey
        get() = currentKeys.last()
    val currentBackStack
        get() = backStacks[currentKey::class.toString()]

    @Composable
    fun getEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider,
            )
        }

        return currentKeys
            .flatMap { decoratedEntries[it::class.toString()]!! }
            .toMutableStateList()
    }
}

/**
 * Models the navigation actions in the app.
 */
class Navigator(val state: NavigationState) {
    fun toHome(postId: String? = null) {
        val newKey = HomeKey(postId)
        if (state.currentKey == newKey) return

        state.currentKeys.clear()
        state.currentKeys.add(newKey)
        state.backStacks[newKey::class.toString()]?.apply {
            clear()
            add(newKey)
        }
    }

    fun toInterests() {
        if (state.currentKey == InterestsKey) return
        state.currentKeys.add(InterestsKey)
    }

    fun pop() {
        state.currentKeys.removeLastOrNull()
    }
}
