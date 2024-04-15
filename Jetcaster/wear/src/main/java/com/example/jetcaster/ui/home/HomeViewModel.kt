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

package com.example.jetcaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.domain.FilterableCategoriesUseCase
import com.example.jetcaster.core.data.domain.PodcastCategoryFilterUseCase
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.PlayerEpisode
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.util.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val podcastCategoryFilterUseCase: PodcastCategoryFilterUseCase,
    private val filterableCategoriesUseCase: FilterableCategoriesUseCase,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {
    // Holds our currently selected podcast in the library
    private val selectedLibraryPodcast = MutableStateFlow<Podcast?>(null)
    // Holds our currently selected home category
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Library)
    // Holds our currently selected category
    private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    // Combines the latest value from each of the flows, allowing us to generate a
    // view state instance which only contains the latest values.
    val uiState = combine(
        selectedHomeCategory,
        podcastStore.followedPodcastsSortedByLastEpisode(limit = 10),
        refreshing,
        _selectedCategory.flatMapLatest { selectedCategory ->
            filterableCategoriesUseCase(selectedCategory)
        },
        _selectedCategory.flatMapLatest {
            podcastCategoryFilterUseCase(it)
        },
        selectedLibraryPodcast.flatMapLatest {
            episodeStore.episodesInPodcast(
                podcastUri = it?.uri ?: "",
                limit = 20
            )
        },
        episodePlayer.playerState.map {
            it.queue
        }
    ) {
            homeCategory,
            podcasts,
            refreshing,
            filterableCategories,
            podcastCategoryFilterResult,
            libraryEpisodes,
            queue ->

        _selectedCategory.value = filterableCategories.selectedCategory

        selectedHomeCategory.value = homeCategory

        HomeViewState(
            selectedHomeCategory = homeCategory,
            featuredPodcasts = podcasts.toPersistentList(),
            refreshing = refreshing,
            filterableCategoriesModel = filterableCategories,
            podcastCategoryFilterResult = podcastCategoryFilterResult,
            libraryEpisodes = libraryEpisodes,
            queue = queue,
            errorMessage = null, /* TODO */
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = HomeViewState())

    init {
        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            refreshing.value = true
            podcastsRepository.updatePodcasts(force)
            refreshing.value = false
        }
    }

    fun onPodcastUnfollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcastUri)
        }
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri)
        }
    }
}

enum class HomeCategory {
    Library,
}

data class HomeViewState(
    val featuredPodcasts: List<PodcastWithExtraInfo> = listOf(),
    val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.Library,
    val filterableCategoriesModel: FilterableCategoriesModel = FilterableCategoriesModel(),
    val podcastCategoryFilterResult: PodcastCategoryFilterResult = PodcastCategoryFilterResult(),
    val libraryEpisodes: List<EpisodeToPodcast> = emptyList(),
    val queue: List<PlayerEpisode> = emptyList(),
    val errorMessage: String? = null
)
