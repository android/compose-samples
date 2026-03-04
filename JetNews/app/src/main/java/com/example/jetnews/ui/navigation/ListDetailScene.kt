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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.ui.NavDisplay

class ListDetailScene<T : Any>(
    override val key: Any,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    override val previousEntries: List<NavEntry<T>>,
) : Scene<T> {

    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        val listConfiguration = listEntry.metadata[ListMetadataKey] ?: ListConfiguration()

        Row {
            Box(modifier = listConfiguration.modifier) {
                listEntry.Content()
            }
            Box(
                modifier = detailEntry?.metadata?.get(DetailMetadataKey)?.modifier ?: Modifier,
            ) {
                AnimatedContent(
                    targetState = detailEntry,
                    contentKey = { entry -> entry?.contentKey ?: "detailPlaceholder" },
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                ) { entry ->
                    if (entry == null) {
                        listConfiguration.detailPlaceholder()
                    } else {
                        entry.Content()
                    }
                }
            }
        }
    }

    override val metadata = metadata {
        put(NavDisplay.TransitionKey) { fadeIn() togetherWith ExitTransition.KeepUntilTransitionsFinished }
        put(NavDisplay.PopTransitionKey) { fadeIn() togetherWith ExitTransition.KeepUntilTransitionsFinished }
    }

    sealed interface PaneConfiguration {
        val modifier: Modifier
    }

    data class ListConfiguration(override val modifier: Modifier = Modifier, val detailPlaceholder: @Composable () -> Unit = {}) :
        PaneConfiguration

    data class DetailConfiguration(override val modifier: Modifier = Modifier) : PaneConfiguration

    object ListMetadataKey : NavMetadataKey<ListConfiguration>
    object DetailMetadataKey : NavMetadataKey<DetailConfiguration>

    companion object {
        fun list(listConfiguration: ListConfiguration = ListConfiguration()) = metadata {
            put(ListMetadataKey, listConfiguration)
        }

        fun detail(detailConfiguration: DetailConfiguration = DetailConfiguration()) = metadata {
            put(DetailMetadataKey, detailConfiguration)
        }
    }
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(isExpandedScreen: Boolean) = remember(isExpandedScreen) {
    ListDetailSceneStrategy<T>(isExpandedScreen)
}

class ListDetailSceneStrategy<T : Any>(val isExpandedScreen: Boolean) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!isExpandedScreen) return null

        val lastEntry = entries.lastOrNull() ?: return null
        if (ListDetailScene.ListMetadataKey !in lastEntry.metadata &&
            ListDetailScene.DetailMetadataKey !in lastEntry.metadata
        ) {
            return null
        }

        val listEntryIndex = entries.indexOfLast { ListDetailScene.ListMetadataKey in it.metadata }
        if (listEntryIndex == -1) return null

        val listEntry = entries[listEntryIndex]
        val detailEntry = entries.getOrNull(listEntryIndex + 1)
            ?.takeIf { ListDetailScene.DetailMetadataKey in it.metadata }

        return ListDetailScene(
            key = listEntry.contentKey,
            listEntry = listEntry,
            detailEntry = detailEntry,
            previousEntries = entries.take(listEntryIndex),
        )
    }
}
