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
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.player.EpisodePlayer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Duration

data class PlayerUiState(
    val title: String = "",
    val subTitle: String = "",
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
    val isPlaying: Boolean = false,
    val timeElapsed: Duration? = null,
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
class PlayerViewModel(
    episodeStore: EpisodeStore = Graph.episodeStore,
    podcastStore: PodcastStore = Graph.podcastStore,
    private val episodePlayer: EpisodePlayer = Graph.episodePlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val episodeUri: String = Uri.decode(savedStateHandle.get<String>("episodeUri")!!)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            combine(
                episodeStore.episodeAndPodcastWithUri(episodeUri),
                episodePlayer.playerState
            ) { episodeToPlayer, playerState ->
                episodePlayer.currentEpisode = episodeToPlayer.episode
                PlayerUiState(
                    title = episodeToPlayer.episode.title,
                    duration = episodeToPlayer.episode.duration,
                    podcastName = episodeToPlayer.episode.title,
                    summary = episodeToPlayer.episode.summary ?: "",
                    podcastImageUrl = episodeToPlayer.podcast.imageUrl ?: "",
                    isPlaying = playerState.isPlaying,
                    timeElapsed = playerState.timeElapsed
                )
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

    fun onAdvanceBy(duration: Duration) {
        episodePlayer.advanceBy(duration)
    }

    fun onRewindBy(duration: Duration) {
        episodePlayer.rewindBy(duration)
    }

    /**
     * Factory for PlayerViewModel that takes EpisodeStore, PodcastStore and EpisodePlayer as a
     * dependency
     */
    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            episodePlayer: EpisodePlayer = Graph.episodePlayer,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return PlayerViewModel(episodeStore, podcastStore, episodePlayer, handle) as T
                }
            }
    }
}
