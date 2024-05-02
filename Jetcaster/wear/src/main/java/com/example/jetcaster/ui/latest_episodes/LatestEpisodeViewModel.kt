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

package com.example.jetcaster.ui.latest_episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.domain.GetLatestFollowedEpisodesUseCase
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LatestEpisodeViewModel @Inject constructor(
    episodesFromFavouritePodcasts: GetLatestFollowedEpisodesUseCase,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiState: StateFlow<LatestEpisodeScreenState> =
        episodesFromFavouritePodcasts.invoke().map { episodeToPodcastList ->
            if (episodeToPodcastList.isNotEmpty()) {
                LatestEpisodeScreenState.Loaded(
                    episodeToPodcastList.map {
                        it.toPlayerEpisode()
                    }
                )
            } else {
                LatestEpisodeScreenState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LatestEpisodeScreenState.Loading,
        )

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(episodes)
    }

    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
}

sealed interface LatestEpisodeScreenState {

    data object Loading : LatestEpisodeScreenState

    data class Loaded(
        val episodeList: List<PlayerEpisode>
    ) : LatestEpisodeScreenState

    data object Empty : LatestEpisodeScreenState
}
