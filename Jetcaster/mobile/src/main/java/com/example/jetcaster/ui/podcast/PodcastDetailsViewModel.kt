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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PodcastUiState {
    data object Loading : PodcastUiState
    data class Ready(
        val podcast: PodcastInfo,
        val episodes: List<EpisodeInfo>,
    ) : PodcastUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 */
@HiltViewModel(assistedFactory = PodcastDetailsViewModel.Factory::class)
class PodcastDetailsViewModel @AssistedInject constructor(
    private val episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    private val podcastStore: PodcastStore,
    @Assisted private val podcastUri: String,
) : ViewModel() {

    private val decodedPodcastUri = Uri.decode(podcastUri)

    val state: StateFlow<PodcastUiState> =
        combine(
            podcastStore.podcastWithExtraInfo(decodedPodcastUri),
            episodeStore.episodesInPodcast(decodedPodcastUri)
        ) { podcast, episodeToPodcasts ->
            val episodes = episodeToPodcasts.map { it.episode.asExternalModel() }
            PodcastUiState.Ready(
                podcast = podcast.podcast.asExternalModel().copy(isSubscribed = podcast.isFollowed),
                episodes = episodes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PodcastUiState.Loading
        )

    fun toggleSusbcribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    @AssistedFactory
    interface Factory {
        fun create(podcastUri: String): PodcastDetailsViewModel
    }
}
