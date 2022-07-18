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
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.data.PodcastsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastRepository,
    private val podcastStore: PodcastStore = Graph.podcastStore
) : ViewModel() {
    // Holds our currently selected home category
    private val selectedCategory = MutableStateFlow(HomeCategory.Discover)
    // Holds the currently available home categories
    private val categories = MutableStateFlow(HomeCategory.values().asList())

    private val error = MutableStateFlow<Throwable?>(value = null)

    private val refreshing = MutableStateFlow(false)

    private val followedPodcasts = podcastStore.followedPodcastsSortedByLastEpisode(limit = 20)

    val errorMessage = error.map {
        it?.message
    }

    val state =
        combine(
            followedPodcasts,
            refreshing,
            selectedCategory,
            categories,
            errorMessage,
        ) { followed, refreshing, selected, cats, error->
            HomeViewState(featuredPodcasts = followed, refreshing = refreshing, selectedHomeCategory = selected, homeCategories = cats, errorMessage = error)
        }
            .catch { throwable ->
                error.emit(throwable)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState())

    init {
        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force)
            }.onFailure {
                error.emit(it)
            }

            refreshing.value = false
        }
    }

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedCategory.value = category
    }

    fun onPodcastUnfollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcastUri)
        }
    }
}

enum class HomeCategory {
    Library, Discover
}

data class HomeViewState(
    val featuredPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val errorMessage: String? = null
)
