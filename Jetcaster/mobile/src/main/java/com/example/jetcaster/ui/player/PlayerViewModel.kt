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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.MediaPlayerUseCase
import com.example.jetcaster.core.player.PlayerUiState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCase: MediaPlayerUseCase,
) : ViewModel() {

    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val episodeUri: String =
        Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)

    val playerEpisodeState: StateFlow<PlayerEpisode> =
        useCase.playerEpisodeFlow(episodeUri)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerEpisode()
            )

    val playerUiState: StateFlow<PlayerUiState> =
        useCase.playerUiStateFlow(episodeUri)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerUiState()
            )

    fun onPlay() {
        useCase.play(playerEpisodeState.value)
    }

    fun onPause() {
        useCase.pause()
    }

    fun onStop() {
        useCase.stop()
    }

    fun onPrevious() {
        useCase.previous()
    }

    fun onNext() {
        useCase.next()
    }

    fun onAdvanceBy() {
        useCase.advanceBy()
    }

    fun onRewindBy() {
        useCase.rewindBy()
    }

    fun onSeekingStarted() {
        useCase.onSeekingStarted()
    }

    fun onSeekingFinished(duration: Duration) {
        useCase.onSeekingFinished(duration)
    }

    fun onAddToQueue() {
        useCase.addMediaItem(playerEpisodeState.value)
    }
}
