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
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.data.PodcastsFetcher
import com.example.jetcaster.data.SampleFeeds
import com.example.jetcaster.data.sortedByLastEpisodeDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val podcastsFetcher: PodcastsFetcher = Graph.podcastFetcher,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val categoryStore: CategoryStore = Graph.categoryStore
) : ViewModel() {
    // Holds our currently selected category
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                podcastStore.podcasts.sortedByLastEpisodeDate().map { it.take(10) },
                categoryStore.sortedByCount(),
                _selectedCategory
            ) { podcasts, genres, selectedCategory ->
                HomeViewState(
                    featuredPodcasts = podcasts,
                    refreshing = false,
                    errorMessage = null,
                    categories = genres.toList(),
                    selectedCategory = selectedCategory
                )
            }.collect { _state.value = it }
        }

        refresh()
    }

    fun refresh() {
        fetchPodcasts()
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    private fun fetchPodcasts() {
        viewModelScope.launch {
            // First clear the store
            podcastStore.clear()

            // Now fetch the podcasts, and add each to each store
            podcastsFetcher(SampleFeeds).collect {
                podcastStore.addPodcast(it)
            }
        }
    }
}

data class HomeViewState(
    val featuredPodcasts: List<Podcast> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)
