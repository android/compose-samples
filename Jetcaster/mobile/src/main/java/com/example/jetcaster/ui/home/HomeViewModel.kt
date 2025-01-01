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

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.domain.FilterableCategoriesUseCase
import com.example.jetcaster.core.domain.PodcastCategoryFilterUseCase
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.model.asPodcastToEpisodeInfo
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
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
    private val selectedLibraryPodcast = MutableStateFlow<PodcastInfo?>(null)

    // Holds our currently selected home category
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Discover)

    // Holds the currently available home categories
    private val homeCategories = MutableStateFlow(HomeCategory.entries)

    // Holds our currently selected category
    private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    private val subscribedPodcasts = podcastStore.followedPodcastsSortedByLastEpisode(limit = 10)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            com.example.jetcaster.core.util.combine(
                homeCategories,
                selectedHomeCategory,
                subscribedPodcasts,
                refreshing,
                _selectedCategory.flatMapLatest { selectedCategory ->
                    filterableCategoriesUseCase(selectedCategory)
                },
                _selectedCategory.flatMapLatest {
                    podcastCategoryFilterUseCase(it)
                },
                subscribedPodcasts.flatMapLatest { podcasts ->
                    episodeStore.episodesInPodcasts(
                        podcastUris = podcasts.map { it.podcast.uri },
                        limit = 20
                    )
                }
            ) { homeCategories,
                homeCategory,
                podcasts,
                refreshing,
                filterableCategories,
                podcastCategoryFilterResult,
                libraryEpisodes ->

                _selectedCategory.value = filterableCategories.selectedCategory

                // Override selected home category to show 'DISCOVER' if there are no
                // featured podcasts
                selectedHomeCategory.value =
                    if (podcasts.isEmpty()) HomeCategory.Discover else homeCategory

                HomeScreenUiState(
                    isLoading = refreshing,
                    homeCategories = homeCategories,
                    selectedHomeCategory = homeCategory,
                    featuredPodcasts = podcasts.map { it.asExternalModel() }.toPersistentList(),
                    filterableCategoriesModel = filterableCategories,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    library = libraryEpisodes.asLibrary()
                )
            }.catch { throwable ->
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force)
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.CategorySelected -> onCategorySelected(action.category)
            is HomeAction.HomeCategorySelected -> onHomeCategorySelected(action.category)
            is HomeAction.LibraryPodcastSelected -> onLibraryPodcastSelected(action.podcast)
            is HomeAction.PodcastUnfollowed -> onPodcastUnfollowed(action.podcast)
            is HomeAction.QueueEpisode -> onQueueEpisode(action.episode)
            is HomeAction.TogglePodcastFollowed -> onTogglePodcastFollowed(action.podcast)
        }
    }

    private fun onCategorySelected(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    private fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
    }

    private fun onPodcastUnfollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcast.uri)
        }
    }

    private fun onTogglePodcastFollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    private fun onLibraryPodcastSelected(podcast: PodcastInfo?) {
        selectedLibraryPodcast.value = podcast
    }

    private fun onQueueEpisode(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode)
    }
}

private fun List<EpisodeToPodcast>.asLibrary(): LibraryInfo =
    LibraryInfo(
        episodes = this.map { it.asPodcastToEpisodeInfo() }
    )

enum class HomeCategory {
    Library, Discover
}

@Immutable
sealed interface HomeAction {
    data class CategorySelected(val category: CategoryInfo) : HomeAction
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction
    data class PodcastUnfollowed(val podcast: PodcastInfo) : HomeAction
    data class TogglePodcastFollowed(val podcast: PodcastInfo) : HomeAction
    data class LibraryPodcastSelected(val podcast: PodcastInfo?) : HomeAction
    data class QueueEpisode(val episode: PlayerEpisode) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredPodcasts: PersistentList<PodcastInfo> = persistentListOf(),
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val filterableCategoriesModel: FilterableCategoriesModel = FilterableCategoriesModel(),
    val podcastCategoryFilterResult: PodcastCategoryFilterResult = PodcastCategoryFilterResult(),
    val library: LibraryInfo = LibraryInfo(),
)
