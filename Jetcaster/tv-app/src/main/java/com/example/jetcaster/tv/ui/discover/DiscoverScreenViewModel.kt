/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.discover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.tv.model.CategoryList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscoverScreenViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastRepository,
    private val categoryStore: CategoryStore = Graph.categoryStore,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val selectedCategoryFlow = combine(
        categoryStore.categoriesSortedByPodcastCount(),
        _selectedCategory
    ) { categoryList, category ->
        Log.d("category list", "$categoryList")
        category ?: categoryList.firstOrNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastInSelectedCategory = selectedCategoryFlow.flatMapLatest {
        if (it != null) {
            categoryStore.podcastsInCategorySortedByPodcastCount(it.id)
        } else {
            flowOf(emptyList())
        }
    }.map {
        PodcastList(it)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeFlow = selectedCategoryFlow.flatMapLatest {
        if (it != null) {
            categoryStore.episodesFromPodcastsInCategory(it.id, 20)
        } else {
            flowOf(emptyList())
        }
    }.map {
        EpisodeList(it)
    }

    val uiState = combine(
        categoryStore.categoriesSortedByPodcastCount(),
        selectedCategoryFlow,
        podcastInSelectedCategory,
        latestEpisodeFlow,
    ) { categoryList, category, podcastList, latestEpisodes ->
        if (category != null) {
            DiscoverScreenUiState.Ready(
                CategoryList(categoryList),
                category,
                podcastList,
                latestEpisodes
            )
        } else {
            DiscoverScreenUiState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DiscoverScreenUiState.Loading
    )

    init {
        refresh()
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    private fun refresh() {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(false)
        }
    }
}

sealed interface DiscoverScreenUiState {
    data object Loading : DiscoverScreenUiState
    data class Ready(
        val categoryList: CategoryList,
        val selectedCategory: Category,
        val podcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : DiscoverScreenUiState
}
