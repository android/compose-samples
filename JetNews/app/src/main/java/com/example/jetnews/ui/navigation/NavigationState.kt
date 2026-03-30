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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import kotlinx.serialization.builtins.ListSerializer

@Composable
fun rememberInitialBackStack(backStack: List<NavKey>): MutableState<List<NavKey>> {
    return rememberSerializable(
        serializer = MutableStateSerializer(ListSerializer(NavKeySerializer())),
    ) {
        mutableStateOf(backStack)
    }
}

@Composable
fun rememberNavigationState(primaryTopLevelKey: NavKey, topLevelKeys: Set<NavKey>, initialBackStack: List<NavKey>): NavigationState {

    val initialTopLevelKey = initialBackStack.first()

    val topLevelKey = rememberSerializable(
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf(initialTopLevelKey)
    }.apply {
        // If a new intent comes in while the activity is already running, the value for
        // topLevelKey needs to be updated to reflect it
        value = initialTopLevelKey
    }

    val backStacks = remember(topLevelKeys, initialBackStack) {
        mutableMapOf<NavKey, NavBackStack<NavKey>>()
    }

    topLevelKeys.forEach { key ->
        val backStack = if (key == initialTopLevelKey) initialBackStack else listOf(key)
        backStacks[key] = key(backStack) {
            rememberNavBackStack(*backStack.toTypedArray())
        }
    }

    return remember(primaryTopLevelKey, topLevelKey, topLevelKeys, backStacks) {
        NavigationState(primaryTopLevelKey, topLevelKey, topLevelKeys, backStacks)
    }
}

/**
 * State holder for the app's navigation.
 *
 * @param primaryTopLevelKey The top-level key of the stack that the app should go back to when navigating up from the base of another
 * top-level key's stack.
 * @param topLevelKey A [MutableState] holding the currently selected top-level key.
 * @param topLevelKeys A set of all available top-level keys in the app.
 * @param backStacks A map containing the [NavBackStack] for each top-level key.
 */
class NavigationState(
    val primaryTopLevelKey: NavKey,
    topLevelKey: MutableState<NavKey>,
    val topLevelKeys: Set<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelKey: NavKey by topLevelKey

    private val currentTopLevelKeys: List<NavKey>
        get() = if (topLevelKey == primaryTopLevelKey) {
            listOf(primaryTopLevelKey)
        } else {
            listOf(primaryTopLevelKey, topLevelKey)
        }

    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
        entryDecorators: List<NavEntryDecorator<NavKey>> = listOf(rememberSaveableStateHolderNavEntryDecorator()),
    ): List<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = entryDecorators,
                entryProvider = entryProvider,
            )
        }

        return currentTopLevelKeys
            .flatMap { decoratedEntries[it] ?: emptyList() }
    }
}
