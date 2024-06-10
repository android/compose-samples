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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalHorologistApi::class)
data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState(),
    var trackPositionUiModel: TrackPositionUiModel = TrackPositionUiModel.Actual.ZERO
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
@OptIn(ExperimentalHorologistApi::class)
class PlayerViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiState = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.Empty
        } else {
            PlayerScreenUiState.Ready(PlayerUiState(it, buildPositionModel(it)))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerScreenUiState.Loading
    )

    private fun buildPositionModel(it: EpisodePlayerState) =
        if (it.currentEpisode != null) {
            TrackPositionUiModel.Actual(
                percent = it.timeElapsed.toMillis().toFloat() /
                    (
                        it.currentEpisode?.duration?.toMillis()
                            ?.toFloat() ?: 0f
                        ),
                duration = it.currentEpisode?.duration?.toKotlinDuration()
                    ?: Duration.ZERO.toKotlinDuration(),
                position = it.timeElapsed.toKotlinDuration()
            )
        } else {
            TrackPositionUiModel.Actual.ZERO
        }

    fun onPlay() {
        episodePlayer.play()
    }

    fun onPause() {
        episodePlayer.pause()
    }

    fun onAdvanceBy() {
        episodePlayer.advanceBy(Duration.ofSeconds(10))
    }

    fun onRewindBy() {
        episodePlayer.rewindBy(Duration.ofSeconds(10))
    }

    fun onPlaybackSpeedChange() {
        if (episodePlayer.playerState.value.playbackSpeed == Duration.ofSeconds(2)) {
            episodePlayer.decreaseSpeed(speed = Duration.ofMillis(1000))
        } else {
            episodePlayer.increaseSpeed()
        }
    }
}

sealed class PlayerScreenUiState {
    data object Loading : PlayerScreenUiState()
    data class Ready(
        val playerState: PlayerUiState
    ) : PlayerScreenUiState()

    data object Empty : PlayerScreenUiState()
}
