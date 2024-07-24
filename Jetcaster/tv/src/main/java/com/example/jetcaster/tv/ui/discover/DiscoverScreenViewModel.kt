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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val categoryStore: CategoryStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)

    private val categoryListFlow = categoryStore
        .categoriesSortedByPodcastCount()
        .map { categoryList ->
            categoryList.map { category ->
                CategoryInfo(
                    id = category.id,
                    name = category.name.filter { !it.isWhitespace() }
                )
            }
        }

    private val selectedCategoryFlow = combine(
        categoryListFlow,
        _selectedCategory
    ) { categoryList, category ->
        category ?: categoryList.firstOrNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastInSelectedCategory = selectedCategoryFlow.flatMapLatest {
        if (it != null) {
            categoryStore.podcastsInCategorySortedByPodcastCount(it.id, limit = 10)
        } else {
            flowOf(emptyList())
        }
    }.map { list ->
        list.map { it.asExternalModel() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeFlow = selectedCategoryFlow.flatMapLatest {
        if (it != null) {
            categoryStore.episodesFromPodcastsInCategory(it.id, 20)
        } else {
            flowOf(emptyList())
        }
    }.map { list ->
        EpisodeList(list.map { it.toPlayerEpisode() })
    }

    val uiState = combine(
        categoryListFlow,
        selectedCategoryFlow,
        podcastInSelectedCategory,
        latestEpisodeFlow,
    ) { categoryList, category, podcastList, latestEpisodes ->
        if (category != null) {
            DiscoverScreenUiState.Ready(
                CategoryInfoList(categoryList),
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

    fun selectCategory(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
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
        val categoryInfoList: CategoryInfoList,
        val selectedCategory: CategoryInfo,
        val podcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : DiscoverScreenUiState
}
