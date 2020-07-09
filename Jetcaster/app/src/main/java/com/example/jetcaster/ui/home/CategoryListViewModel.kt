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

package com.example.jetcaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.Category
import com.example.jetcaster.data.CategoryStore
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.PodcastStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryEpisodeListViewModel(
    private val category: Category,
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val computationDispatcher: CoroutineDispatcher = Graph.computationDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryEpisodeListViewState())

    val state: StateFlow<CategoryEpisodeListViewState>
        get() = _state

    init {
        viewModelScope.launch {
            categoryStore.episodesInCategory(category)
                .map {
                    // Take the first 20 most recent episodes
                    it.take(20)
                }
                .flatMapLatest { episodes ->
                    // Now we need to join each episode with it's corresponding podcast.
                    // We do that by combining each episode with the result of [podcastWithUri].
                    val flowsOfEpisodeWithPodcasts = episodes.map { episode ->
                        podcastStore.podcastWithUri(episode.podcastUri)
                            .map { EpisodeToPodcast(episode, it) }
                    }
                    combine(flowsOfEpisodeWithPodcasts) {
                        it.toList()
                    }
                }
                .flowOn(computationDispatcher)
                .collect { _state.value = CategoryEpisodeListViewState(it) }
        }
    }
}

data class CategoryEpisodeListViewState(
    val episodes: List<EpisodeToPodcast> = emptyList()
)
