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

package com.example.jetcaster.ui.home.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiscoverViewModel(
    categoryStore: CategoryStore = Graph.categoryStore,
    private val podcastStore: PodcastStore = Graph.podcastStore
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    private val _categories =
        categoryStore.categoriesSortedByPodcastCount()
            .onEach { categories ->
                // If we haven't got a selected category yet, select the first
                if (categories.isNotEmpty() && _selectedCategory.value == null) {
                    _selectedCategory.value = categories[0]
                }
            }

    private val selectedCategoryResentPodcastsFlow =
        _selectedCategory
            .filterNotNull()
            .flatMapLatest {
                categoryStore.podcastsInCategorySortedByPodcastCount(it.id)
            }

    private val selectedCategoryEpisodesFlow =
        _selectedCategory
            .filterNotNull()
            .flatMapLatest {
                categoryStore.episodesFromPodcastsInCategory(it.id)
            }

    val state =
        combine(
            _categories,
            _selectedCategory,
            selectedCategoryResentPodcastsFlow,
            selectedCategoryEpisodesFlow
        ) { categories, selected, selectedRecentPodcasts, selectedEpisodes ->
            DiscoverViewState(
                categories = categories,
                selectedCategory = selected,
                selectedCategoryResentPodcasts = selectedRecentPodcasts,
                selectedCategoryEpisodes = selectedEpisodes
            )
        }
            .flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.Eagerly, DiscoverViewState())

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri)
        }
    }
}

data class DiscoverViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedCategoryResentPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val selectedCategoryEpisodes: List<EpisodeToPodcast> = emptyList()
)
