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
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.data.PodcastsRepository
import com.example.jetcaster.ui.home.category.PodcastCategoryViewState
import com.example.jetcaster.ui.home.discover.DiscoverViewState
import com.example.jetcaster.util.combine
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastRepository,
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val episodeStore: EpisodeStore = Graph.episodeStore
) : ViewModel() {
    // Holds our currently selected home category
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Discover)
    // Holds the currently available home categories
    private val homeCategories = MutableStateFlow(HomeCategory.entries)
    // Holds our currently selected category
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())
    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val libraryEpisodes =
        podcastStore.followedPodcastsSortedByLastEpisode()
            .flatMapLatest { followedPodcasts ->
                if (followedPodcasts.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        followedPodcasts.map { p ->
                            episodeStore.episodesInPodcast(p.podcast.uri, 5)
                        }
                    ) { allEpisodes ->
                        allEpisodes.toList().flatten().sortedByDescending { it.episode.published }
                    }
                }
            }

    private val discover = combine(
        categoryStore.categoriesSortedByPodcastCount()
            .onEach { categories ->
                // If we haven't got a selected category yet, select the first
                if (categories.isNotEmpty() && _selectedCategory.value == null) {
                    _selectedCategory.value = categories[0]
                }
            },
        _selectedCategory
    ) { categories, selectedCategory ->
        DiscoverViewState(
            categories = categories,
            selectedCategory = selectedCategory
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastCategory = _selectedCategory.flatMapLatest { category ->
        if (category == null) {
            return@flatMapLatest flowOf(PodcastCategoryViewState())
        }

        val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
            category.id,
            limit = 10
        )

        val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
            category.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
            PodcastCategoryViewState(
                topPodcasts = topPodcasts,
                episodes = episodes
            )
        }
    }

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                homeCategories,
                selectedHomeCategory,
                podcastStore.followedPodcastsSortedByLastEpisode(limit = 20),
                refreshing,
                discover,
                podcastCategory,
                libraryEpisodes
            ) { homeCategories,
                selectedHomeCategory,
                podcasts,
                refreshing,
                discoverViewState,
                podcastCategoryViewState,
                libraryEpisodes ->
                HomeViewState(
                    homeCategories = homeCategories,
                    selectedHomeCategory = selectedHomeCategory,
                    featuredPodcasts = podcasts.toPersistentList(),
                    refreshing = refreshing,
                    discoverViewState = discoverViewState,
                    podcastCategoryViewState = podcastCategoryViewState,
                    libraryEpisodes = libraryEpisodes,
                    errorMessage = null, /* TODO */
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force)
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
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
    Library, Discover
}

data class HomeViewState(
    val featuredPodcasts: PersistentList<PodcastWithExtraInfo> = persistentListOf(),
    val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val discoverViewState: DiscoverViewState = DiscoverViewState(),
    val podcastCategoryViewState: PodcastCategoryViewState = PodcastCategoryViewState(),
    val libraryEpisodes: List<EpisodeToPodcast> = emptyList(),
    val errorMessage: String? = null
)
