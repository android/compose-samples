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

import androidx.media3.common.Player
import com.example.jetcaster.core.notification.MediaControllerManager
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

interface MediaPlayerListenerUseCase {
    fun playerUiStateFlow(uri: String): Flow<PlayerUiState>
}

class MediaPlayerListenerUseCaseImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : MediaPlayerListenerUseCase {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    override fun playerUiStateFlow(uri: String) = callbackFlow {
        val mediaController = mediaControllerManager.mediaControllerFlow.first()

        val currentPlayerUiState = MutableStateFlow(
            if (mediaController.currentMediaItem?.mediaId == uri) {
                PlayerUiState(
                    isLoading = mediaController.isLoading,
                    isPlaying = mediaController.isPlaying,
                    hasNext = mediaController.hasNextMediaItem(),
                    timeElapsed = Duration.ofMillis(mediaController.currentPosition),
                    playerSpeed = Duration.ofSeconds(1)
                )
            } else {
                PlayerUiState()
            }
        )

        val playerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                currentPlayerUiState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                currentPlayerUiState.update { it.copy(isLoading = isLoading) }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    currentPlayerUiState.update {
                        it.copy(timeElapsed = Duration.ofMillis(newPosition.positionMs))
                    }
                }
            }
        }

        // Start timer when player is playing
        coroutineScope.launch {
            currentPlayerUiState
                .map { it.isLoading.not() && it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    if (isPlaying) {
                        timerJob = coroutineScope.launch {
                            val startDuration = mediaController.currentPosition
                            val maxDuration = mediaController.contentDuration
                            val playerSpeed = currentPlayerUiState.value.playerSpeed

                            while (isActive && startDuration <= maxDuration) {
                                delay(playerSpeed.toMillis())
                                // Update time elapsed
                                currentPlayerUiState.update {
                                    it.copy(timeElapsed = it.timeElapsed + playerSpeed)
                                }
                            }
                        }
                    } else {
                        timerJob?.cancel()
                        timerJob = null
                    }
                }
        }

        // Update when player state changes
        coroutineScope.launch {
            currentPlayerUiState
                .onEach { send(it) }
                .collect()
        }

        mediaController.addListener(playerListener)

        awaitClose {
            mediaController.removeListener(playerListener)
            coroutineScope.cancel()
        }
    }
}
