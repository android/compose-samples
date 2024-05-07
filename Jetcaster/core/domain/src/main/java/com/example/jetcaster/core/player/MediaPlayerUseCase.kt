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

package com.example.jetcaster.core.player

import androidx.media3.session.MediaController
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.notification.MediaControllerManager
import com.example.jetcaster.core.notification.SEEK_TO_DURATION
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toMediaItem
import com.example.jetcaster.core.player.model.toPlayerEpisode
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface MediaPlayerUseCase {
    fun playerUiStateFlow(uri: String): Flow<PlayerUiState>
    fun playerEpisodeFlow(uri: String): Flow<PlayerEpisode>

    fun addMediaItem(playerEpisode: PlayerEpisode)
    fun play(playerEpisode: PlayerEpisode)
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun advanceBy()
    fun rewindBy()
    fun onSeekingStarted()
    fun onSeekingFinished(duration: Duration)
}

class MediaPlayerUseCaseImpl @Inject constructor(
    private val episodeStore: EpisodeStore,
    private val mediaControllerManager: MediaControllerManager,
    private val mediaPlayerListenerUseCase: MediaPlayerListenerUseCase
) : MediaPlayerUseCase {

    private var mediaController: MediaController? = null

    override fun playerUiStateFlow(uri: String): Flow<PlayerUiState> =
        mediaPlayerListenerUseCase.playerUiStateFlow(uri)

    override fun playerEpisodeFlow(uri: String): Flow<PlayerEpisode> =
        combine(
            mediaControllerManager.mediaControllerFlow,
            episodeStore.episodeAndPodcastWithUri(uri).map(EpisodeToPodcast::toPlayerEpisode),
            ::Pair
        )
            .map { (mediaController, playerEpisode) ->
                this.mediaController = mediaController
                playerEpisode
            }

    override fun addMediaItem(playerEpisode: PlayerEpisode) {
        val mediaItem = playerEpisode.toMediaItem()
        mediaController?.addMediaItem(mediaItem)
    }

    override fun play(playerEpisode: PlayerEpisode) {
        val mediaItem = playerEpisode.toMediaItem()

        // Set mediaItem if different from the media item currently playing
        if (mediaController?.currentMediaItem?.mediaId != mediaItem.mediaId) {
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
        }

        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun stop() {
        mediaController?.stop()
        mediaController?.release()
    }

    override fun next() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNextMediaItem()
        }
    }

    override fun previous() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPreviousMediaItem()
        } else {
            mediaController?.seekToDefaultPosition()
        }
    }

    override fun advanceBy() {
        mediaController?.apply {
            seekTo(currentPosition + SEEK_TO_DURATION)
        }
    }

    override fun rewindBy() {
        mediaController?.apply {
            seekTo(currentPosition - SEEK_TO_DURATION)
        }
    }

    override fun onSeekingStarted() {
        mediaController?.seekToDefaultPosition()
    }

    override fun onSeekingFinished(duration: Duration) {
        mediaController?.seekTo(duration.toMillis())
    }
}

data class PlayerUiState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val hasNext: Boolean = false,
    val timeElapsed: Duration = Duration.ZERO,
    val playerSpeed: Duration = Duration.ofSeconds(1),
)
