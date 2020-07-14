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
import com.example.jetcaster.data.CategoryStore
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.Podcast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PodcastCategoryViewModel(
    private val categoryId: Long,
    private val categoryStore: CategoryStore = Graph.categoryStore
) : ViewModel() {
    private val _state = MutableStateFlow(PodcastCategoryViewState())

    val state: StateFlow<PodcastCategoryViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
                categoryId,
                limit = 10
            )

            val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
                categoryId,
                limit = 20
            )

            // Combine our flows and collect them into the view state StateFlow
            combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
                PodcastCategoryViewState(
                    topPodcasts = topPodcasts,
                    episodes = episodes
                )
            }.collect { _state.value = it }
        }
    }
}

data class PodcastCategoryViewState(
    val topPodcasts: List<Podcast> = emptyList(),
    val episodes: List<EpisodeToPodcast> = emptyList()
)
