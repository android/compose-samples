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

package com.example.jetcaster.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * A data repository for [Episode] instances.
 *
 * Currently this is backed only with data in memory. Ideally this would be backed by a
 * Room database, to allow persistence and easier querying.
 *
 * @param mainDispatcher The main app [CoroutineDispatcher]
 * @param computationDispatcher [CoroutineDispatcher] to run computationally heavy tasks on
 */
class EpisodeStore(
    private val mainDispatcher: CoroutineDispatcher,
    private val computationDispatcher: CoroutineDispatcher
) {
    val items: Flow<Set<Episode>>
        get() = _items

    private val _items = MutableStateFlow(emptySet<Episode>())

    /**
     * Returns a flow containing the list of episodes associated with the podcast with the
     * given [podcastUri].
     */
    fun episodesForPodcastUri(podcastUri: String): Flow<List<Episode>> {
        return _items
            .map { items ->
                items.filter { it.podcastUri == podcastUri }
            }
            .distinctUntilChanged()
            .flowOn(computationDispatcher)
    }

    /**
     * Add a new [Episode] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addEpisodes(episodes: Collection<Episode>) = withContext(mainDispatcher) {
        _items.value = _items.value + episodes
    }

    /**
     * Clear any [Episode]s currently stored in this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun clear() = withContext(mainDispatcher) {
        _items.value = emptySet()
    }

    fun isEmpty() = _items.value.isEmpty()
}

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<Collection<Episode>>.sortByPublishedDate(descending: Boolean): Flow<List<Episode>> {
    return map { results ->
        // Finally we sort the list of episodes according to the parameter
        if (descending) results.sortedByDescending { it.published }
        else results.sortedBy { it.published }
    }
}
