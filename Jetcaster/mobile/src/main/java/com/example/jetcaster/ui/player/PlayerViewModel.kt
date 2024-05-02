/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.ui.player

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState()
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val episodeUri: String =
        Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithUri(episodeUri).flatMapConcat {
                episodePlayer.currentEpisode = it.toPlayerEpisode()
                episodePlayer.playerState
            }.map {
                PlayerUiState(episodePlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

    fun onPlay() {
        episodePlayer.play()
    }

    fun onPause() {
        episodePlayer.pause()
    }

    fun onStop() {
        episodePlayer.stop()
    }

    fun onPrevious() {
        episodePlayer.previous()
    }

    fun onNext() {
        episodePlayer.next()
    }

    fun onAdvanceBy(duration: Duration) {
        episodePlayer.advanceBy(duration)
    }

    fun onRewindBy(duration: Duration) {
        episodePlayer.rewindBy(duration)
    }

    fun onSeekingStarted() {
        episodePlayer.onSeekingStarted()
    }

    fun onSeekingFinished(duration: Duration) {
        episodePlayer.onSeekingFinished(duration)
    }

    fun onAddToQueue() {
        uiState.episodePlayerState.currentEpisode?.let {
            episodePlayer.addToQueue(it)
        }
    }
}
