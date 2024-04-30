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

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.ui.PodcastDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 */
@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    podcastStore: PodcastStore
) : ViewModel() {

    private val podcastUri: String =
        savedStateHandle.get<String>(PodcastDetails.PODCAST_URI).let {
            Uri.decode(it)
        }

    private val podcastFlow = if (podcastUri != null) {
        podcastStore.podcastWithExtraInfo(podcastUri)
    } else {
        flowOf(null)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    private val episodeListFlow = podcastFlow.flatMapLatest {
        if (it != null) {
            episodeStore.episodesInPodcast(it.podcast.uri)
        } else {
            flowOf(emptyList())
        }
    }.map { list ->
        list.map { it.toPlayerEpisode() }
    }

    val uiState: StateFlow<PodcastDetailsScreenState> =
        combine(
            podcastFlow,
            episodeListFlow
        ) { podcast, episodes ->
            if (podcast != null) {
                PodcastDetailsScreenState.Loaded(
                    podcast = podcast.podcast.asExternalModel()
                        .copy(isSubscribed = podcast.isFollowed),
                    episodeList = episodes,
                )
            } else {
                PodcastDetailsScreenState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PodcastDetailsScreenState.Loading,
        )

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(episodes)
    }
}
