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

package com.example.jetcaster.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.domain.GetLatestFollowedEpisodesUseCase
import com.example.jetcaster.core.model.PlayerEpisode
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.util.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class LatestEpisodeViewModel @Inject constructor(
    private val episodesFromFavouritePodcasts: GetLatestFollowedEpisodesUseCase,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {
    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(LatestEpisodeViewState())
    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)
    val state: StateFlow<LatestEpisodeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                episodesFromFavouritePodcasts.invoke(),
                refreshing
            ) {
                    libraryEpisodes,
                    refreshing
                ->

                LatestEpisodeViewState(
                    refreshing = refreshing,
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
    }
    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
}
data class LatestEpisodeViewState(
    val refreshing: Boolean = false,
    val libraryEpisodes: List<EpisodeToPodcast> = emptyList(),
    val errorMessage: String? = null
)
