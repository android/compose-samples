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

package com.example.jetcaster.ui.podcast

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.model.EpisodeInfo
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.data.model.PodcastInfo
import com.example.jetcaster.core.data.model.asExternalModel
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.ui.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PodcastUiState(
    val podcast: PodcastInfo = PodcastInfo(),
    val episodes: List<EpisodeInfo> = emptyList()
)

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 */
class PodcastDetailsViewModel(
    private val episodeStore: EpisodeStore = Graph.episodeStore,
    private val episodePlayer: EpisodePlayer = Graph.episodePlayer,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val podcastUri: String
) : ViewModel() {

    constructor(
        episodeStore: EpisodeStore = Graph.episodeStore,
        episodePlayer: EpisodePlayer = Graph.episodePlayer,
        podcastStore: PodcastStore = Graph.podcastStore,
        savedStateHandle: SavedStateHandle
    ) : this(
        episodeStore = episodeStore,
        episodePlayer = episodePlayer,
        podcastStore = podcastStore,
        podcastUri = Uri.decode(savedStateHandle.get<String>(Screen.ARG_PODCAST_URI)!!)
    )

    val state: StateFlow<PodcastUiState> =
        combine(
            podcastStore.podcastWithExtraInfo(podcastUri),
            episodeStore.episodesInPodcast(podcastUri)
        ) { podcast, episodeToPodcasts ->
            val episodes = episodeToPodcasts.map { it.episode.asExternalModel() }
            PodcastUiState(
                podcast = podcast.podcast.asExternalModel().copy(isSubscribed = podcast.isFollowed),
                episodes = episodes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PodcastUiState()
        )

    fun toggleSusbcribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    /**
     * Factory for [PodcastDetailsViewModel].
     */
    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            episodePlayer: EpisodePlayer = Graph.episodePlayer,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return PodcastDetailsViewModel(
                        episodeStore = episodeStore,
                        episodePlayer = episodePlayer,
                        podcastStore = podcastStore,
                        savedStateHandle = handle
                    ) as T
                }
            }
    }
}
