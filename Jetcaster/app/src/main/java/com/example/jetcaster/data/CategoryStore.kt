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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A data repository for [Category] instances.
 *
 * Currently this is backed only with data in memory. Ideally this would be backed by a
 * Room database, to allow persistence and easier querying.
 */
class CategoryStore(
    private val podcastStore: PodcastStore
) {
    val categories: Flow<Set<Category>>
        get() = _categories.map { it.keys }

    private val _categories = MutableStateFlow(emptyMap<Category, List<Podcast>>())

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            podcastStore.podcasts
                .map {
                    withContext(Dispatchers.Default) {
                        // Switch to the default dispatcher, as groupByCategory() is not a
                        // trivial operation
                        it.groupByCategory()
                    }
                }
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
    ): Flow<List<Category>> = _categories.map { map ->
        map.keys.let { genres ->
            when {
                descending -> genres.sortedByDescending { map[it]?.size ?: 0 }
                else -> genres.sortedBy { map[it]?.size ?: 0 }
            }
        }
    }.flowOn(Dispatchers.Default) /* flow on Default since sorting is non-trivial */

    /**
     * Returns a flow of lists containing each episode for podcasts in the given [category].
     */
    fun episodesInCategory(
        category: Category,
        descending: Boolean = true
    ): Flow<List<EpisodeToPodcast>> = _categories.map { map ->
        val result = ArrayList<EpisodeToPodcast>()
        map[category]?.fold(result) { list, podcast ->
            val episodes = podcast.episodes.map { EpisodeToPodcast(it, podcast) }
            list.addAll(episodes)
            list
        }
        if (descending) result.sortByDescending { it.episode.published }
        else result.sortedBy { it.episode.published }
        result
    }.flowOn(Dispatchers.Default) /* flow on Default since sorting is non-trivial */

    /**
     * Flips the relationship, returning a map of [Category] to lists of associated [Podcast]s.
     */
    private fun List<Podcast>.groupByCategory(): Map<Category, List<Podcast>> {
        return fold(HashMap<Category, MutableList<Podcast>>()) { map, podcast ->
            podcast.categories.forEach { category ->
                val list = map.getOrPut(category) { ArrayList() }
                list += podcast
            }
            map
        }
    }
}
