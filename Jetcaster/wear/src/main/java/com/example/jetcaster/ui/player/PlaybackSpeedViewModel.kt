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

package com.example.jetcaster.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for a Plaback Speed Screen.
 *
 * Holds the state of PlaybackSpeed ([playerSpeed]) .
 *
 * Playback speed changes can be made via [increaseSpeed] and [decreaseSpeed].
 *
 */

@HiltViewModel
public open class PlaybackSpeedViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val speedUiState = episodePlayer.playerState.map {
        PlaybackSpeedUiStateMapper.map(it.playbackSpeed)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlaybackSpeedUiState()
    )

    public fun increaseSpeed() {
        episodePlayer.increaseSpeed()
    }

    public fun decreaseSpeed() {
        episodePlayer.decreaseSpeed()
    }
}
