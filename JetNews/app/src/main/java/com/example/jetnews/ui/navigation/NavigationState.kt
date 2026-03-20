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
fun rememberNavigationState(mainTopLevelRoute: NavKey, topLevelRoutes: Set<NavKey>, initialBackStack: List<NavKey>): NavigationState {

    val topLevelRoute = rememberSerializable(
        initialBackStack.first(),
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf(initialBackStack.first())
    }

    val backStacks = topLevelRoutes.associateWith { topLevelRoute ->
        val backStack = when (initialBackStack.first()) {
            topLevelRoute -> initialBackStack
            else -> listOf(topLevelRoute)
        }

        key(backStack.toList()) {
            rememberNavBackStack(*backStack.toTypedArray())
        }
    }

    return remember(mainTopLevelRoute, topLevelRoute, backStacks) {
        NavigationState(mainTopLevelRoute, topLevelRoute, backStacks)
    }
}

class NavigationState(
    val mainTopLevelRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

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

        return getTopLevelRoutesInUse()
            .flatMap { decoratedEntries[it] ?: emptyList() }
    }

    private fun getTopLevelRoutesInUse(): List<NavKey> = if (topLevelRoute == mainTopLevelRoute) {
        listOf(mainTopLevelRoute)
    } else {
        listOf(mainTopLevelRoute, topLevelRoute)
    }
}
