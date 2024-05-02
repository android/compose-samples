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

package com.example.jetcaster.tv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiStateFlow = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.NoEpisodeInQueue
        } else {
            PlayerScreenUiState.Ready(it)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerScreenUiState.Loading
    )

    private val skipAmount = Duration.ofSeconds(10L)

    fun play() {
        if (episodePlayer.playerState.value.currentEpisode == null) {
            episodePlayer.next()
        }
        episodePlayer.play()
    }
    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun pause() = episodePlayer.pause()
    fun next() = episodePlayer.next()
    fun previous() = episodePlayer.previous()
    fun skip() {
        episodePlayer.advanceBy(skipAmount)
    }

    fun rewind() {
        episodePlayer.rewindBy(skipAmount)
    }

    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }
}

sealed interface PlayerScreenUiState {
    data object Loading : PlayerScreenUiState
    data class Ready(
        val playerState: EpisodePlayerState
    ) : PlayerScreenUiState

    data object NoEpisodeInQueue : PlayerScreenUiState
}
