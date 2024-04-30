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

package com.example.jetcaster.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel that handles the business logic and screen state of the Queue screen.
 */
@HiltViewModel
class QueueViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,

) : ViewModel() {

    val uiState: StateFlow<QueueScreenState> = episodePlayer.playerState.map {
        if (it.queue.isNotEmpty()) {
            QueueScreenState.Loaded(it.queue)
        } else {
            QueueScreenState.Empty
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        QueueScreenState.Loading,
    )

    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }

    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(episodes)
    }

    fun onDeleteQueueEpisodes() {
        episodePlayer.removeAllFromQueue()
    }
}

@ExperimentalHorologistApi
sealed interface QueueScreenState {

    data object Loading : QueueScreenState

    data class Loaded(
        val episodeList: List<PlayerEpisode>
    ) : QueueScreenState

    data object Empty : QueueScreenState
}
