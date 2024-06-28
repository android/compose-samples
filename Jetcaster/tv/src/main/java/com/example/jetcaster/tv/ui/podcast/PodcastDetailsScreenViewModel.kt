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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PodcastDetailsScreenViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val podcastStore: PodcastStore,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val podcastUri = handle.get<String>(Screen.Podcast.PARAMETER_NAME)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastFlow =
        handle.getStateFlow<String?>(Screen.Podcast.PARAMETER_NAME, null).flatMapLatest {
            if (it != null) {
                podcastStore.podcastWithUri(it)
            } else {
                flowOf(null)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeListFlow = podcastFlow.flatMapLatest {
        if (it != null) {
            episodeStore.episodesInPodcast(it.uri)
        } else {
            flowOf(emptyList())
        }
    }.map { list ->
        EpisodeList(list.map { it.toPlayerEpisode() })
    }

    private val subscribedPodcastListFlow =
        podcastStore.followedPodcastsSortedByLastEpisode()

    val uiStateFlow = combine(
        podcastFlow,
        episodeListFlow,
        subscribedPodcastListFlow
    ) { podcast, episodeList, subscribedPodcastList ->
        if (podcast != null) {
            val isSubscribed = subscribedPodcastList.any { it.podcast.uri == podcastUri }
            PodcastScreenUiState.Ready(podcast.asExternalModel(), episodeList, isSubscribed)
        } else {
            PodcastScreenUiState.Error
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PodcastScreenUiState.Loading
    )

    fun subscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (!isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastInfo.uri)
            }
        }
    }

    fun unsubscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastInfo.uri)
            }
        }
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }
}

sealed interface PodcastScreenUiState {
    data object Loading : PodcastScreenUiState
    data object Error : PodcastScreenUiState
    data class Ready(
        val podcastInfo: PodcastInfo,
        val episodeList: EpisodeList,
        val isSubscribed: Boolean
    ) : PodcastScreenUiState
}
