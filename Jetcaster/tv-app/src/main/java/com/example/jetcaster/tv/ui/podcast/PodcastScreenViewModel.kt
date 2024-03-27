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

package com.example.jetcaster.tv.ui.podcast

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.tv.model.EpisodeList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PodcastScreenViewModel(
    handle: SavedStateHandle,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    episodeStore: EpisodeStore = Graph.episodeStore,
) : ViewModel() {

    private val podcastUri = handle.get<String>("podcastUri") ?: "uri://no/podcast/is/specified"

    private val podcastFlow = podcastStore.podcastWithUri(podcastUri).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeListFlow = podcastFlow.flatMapLatest {
        if (it != null) {
            episodeStore.episodesInPodcast(it.uri)
        } else {
            flowOf(emptyList())
        }
    }.map {
        EpisodeList(it)
    }

    private val subscribedPodcastListFlow = podcastStore.followedPodcastsSortedByLastEpisode()

    val uiStateFlow = combine(
        podcastFlow,
        episodeListFlow,
        subscribedPodcastListFlow
    ) { podcast, episodeList, subscribedPodcastList ->
        if (podcast != null) {
            val isSubscribed = subscribedPodcastList.any { it.podcast.uri == podcastUri }
            PodcastScreenUiState.Ready(podcast, episodeList, isSubscribed)
        } else {
            PodcastScreenUiState.Error
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PodcastScreenUiState.Loading
    )

    fun subscribe(podcast: Podcast, isSubscribed: Boolean) {
        if (!isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcast.uri)
            }
        }
    }

    fun unsubscribe(podcast: Podcast, isSubscribed: Boolean) {
        if (isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcast.uri)
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val factory = object : AbstractSavedStateViewModelFactory() {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return PodcastScreenViewModel(
                    handle
                ) as T
            }
        }
    }
}

sealed interface PodcastScreenUiState {
    data object Loading : PodcastScreenUiState
    data object Error : PodcastScreenUiState
    data class Ready(
        val podcast: Podcast,
        val episodeList: EpisodeList,
        val isSubscribed: Boolean
    ) : PodcastScreenUiState
}
