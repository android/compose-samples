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
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState()
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    val uiState = MutableStateFlow<PlayerUiState?>(null)

    init {
        viewModelScope.launch {
            val currentEpisode = episodePlayer.currentEpisode
            if (currentEpisode != null) {
                uiState.value = PlayerUiState(
                    episodePlayer.playerState.value
                )
            } else {
                uiState.value = PlayerUiState(
                    EpisodePlayerState(currentEpisode = PlayerEpisode(title = "Nothing playing"))
                )
            }
        }
    }
}
