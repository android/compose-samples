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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * A data repository for [Podcast] instances.
 *
 * Currently this is backed only with data in memory. Ideally this would be backed by a
 * Room database, to allow persistence and easier querying.
 */
class PodcastStore {
    val podcasts: Flow<List<Podcast>>
        get() = _podcasts

    private val _podcasts = MutableStateFlow(emptyList<Podcast>())

    /**
     * Returns a flow containing a list of all podcasts in the given [category].
     */
    fun podcastsInCategory(category: Category): Flow<List<Podcast>> = _podcasts.map { podcasts ->
        podcasts.filter { podcast ->
            podcast.categories.any { it == category }
        }
    }

    /**
     * Add a new [Podcast] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addPodcast(podcast: Podcast) = withContext(Dispatchers.Main) {
        _podcasts.value = _podcasts.value + podcast
    }

    /**
     * Clear any [Podcast]s currently stored in this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun clear() = withContext(Dispatchers.Main) {
        _podcasts.value = emptyList()
    }
}

/**
 * Returns a flow containing the entire list of podcasts, sorted by the last episode
 * publish date for each podcast.
 */
fun Flow<List<Podcast>>.sortedByLastEpisodeDate(descending: Boolean = true) = map { podcasts ->
    withContext(Dispatchers.Default) {
        // Run on the default dispatcher, since sorting is non-trivial
        if (descending) {
            podcasts.sortedByDescending { it.lastEpisodeDate }
        } else {
            podcasts.sortedBy { it.lastEpisodeDate }
        }
    }
}
