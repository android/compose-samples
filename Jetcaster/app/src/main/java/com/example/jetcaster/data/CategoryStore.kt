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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * A data repository for [Category] instances.
 *
 * Currently this is backed only with data in memory. Ideally this would be backed by a
 * Room database, to allow persistence and easier querying.
 *
 * @param mainDispatcher The main app [CoroutineDispatcher]
 * @param computationDispatcher [CoroutineDispatcher] to run computationally heavy tasks on
 */
class CategoryStore(
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val mainDispatcher: CoroutineDispatcher,
    private val computationDispatcher: CoroutineDispatcher
) {
    val categories: Flow<Set<Category>>
        get() = _categories.map { it.keys }

    private val _categories = MutableStateFlow(emptyMap<Category, List<String>>())

    private val scope = CoroutineScope(mainDispatcher)

    init {
        scope.launch {
            podcastStore.items
                .map { it.groupByCategory() }
                // Flow on the default dispatcher since group is non-trivial
                .flowOn(computationDispatcher)
                .collect { _categories.value = it }
        }
    }

    /**
     * Returns a flow containing a list of categories which is sorted by the number
     * of podcasts in each category.
     *
     * The direction of the sorted is controlled via the [descending] parameter.
     */
    fun sortedByCount(
        descending: Boolean = true
    ): Flow<List<Category>> {
        return _categories
            .map { map ->
                if (descending) map.keys.sortedByDescending { map[it]?.size ?: 0 }
                else map.keys.sortedBy { map[it]?.size ?: 0 }
            }
            // Flow on the default dispatcher since sorting is non-trivial
            .flowOn(computationDispatcher)
    }

    /**
     * Returns a flow of lists containing each episode for podcasts in the given [category].
     */
    fun episodesInCategory(
        category: Category,
        sortDescending: Boolean = true
    ): Flow<List<Episode>> {
        return _categories
            .map {
                // First we map to the list of podcast URIs in the category
                it[category] ?: emptyList()
            }
            .distinctUntilChanged()
            .flatMapLatest { podcastUris ->
                // We now create the list of Flow<List<Episode>> for each podcast
                val episodeFlows = podcastUris.map { uri ->
                    episodeStore.episodesForPodcastUri(uri)
                }
                // And then combine them all together, to a flattened list. This contains a
                // List<Episode> for all podcasts in podcastUris
                combine(episodeFlows) { it.toList().flatten() }
            }
            .sortByPublishedDate(sortDescending)
            .flowOn(computationDispatcher)
    }

    /**
     * Flips the relationship, returning a map of [Category] to lists of associated [Podcast] URIs.
     */
    private fun Collection<Podcast>.groupByCategory(): Map<Category, List<String>> {
        return fold(HashMap<Category, MutableList<String>>()) { map, podcast ->
            podcast.categories.forEach { category ->
                val list = map.getOrPut(category) { ArrayList() }
                list += podcast.uri
            }
            map
        }
    }
}
