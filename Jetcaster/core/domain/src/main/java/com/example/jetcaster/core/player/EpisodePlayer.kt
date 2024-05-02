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

import com.example.jetcaster.core.player.model.PlayerEpisode
import java.time.Duration
import kotlinx.coroutines.flow.StateFlow

val DefaultPlaybackSpeed = Duration.ofSeconds(1)
data class EpisodePlayerState(
    val currentEpisode: PlayerEpisode? = null,
    val queue: List<PlayerEpisode> = emptyList(),
    val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false,
    val timeElapsed: Duration = Duration.ZERO,
)

/**
 * Interface definition for an episode player defining high-level functions such as queuing
 * episodes, playing an episode, pausing, seeking, etc.
 */
interface EpisodePlayer {

    /**
     * A StateFlow that emits the [EpisodePlayerState] as controls as invoked on this player.
     */
    val playerState: StateFlow<EpisodePlayerState>

    /**
     * Gets the current episode playing, or to be played, by this player.
     */
    var currentEpisode: PlayerEpisode?

    /**
     * The speed of which the player increments
     */
    var playerSpeed: Duration

    fun addToQueue(episode: PlayerEpisode)

    /*
    * Flushes the queue
    */
    fun removeAllFromQueue()

    /**
     * Plays the current episode
     */
    fun play()

    /**
     * Plays the specified episode
     */
    fun play(playerEpisode: PlayerEpisode)

    /**
     * Plays the specified list of episodes
     */
    fun play(playerEpisodes: List<PlayerEpisode>)

    /**
     * Pauses the currently played episode
     */
    fun pause()

    /**
     * Stops the currently played episode
     */
    fun stop()

    /**
     * Plays another episode in the queue (if available)
     */
    fun next()

    /**
     * Plays the previous episode in the queue (if available). Or if an episode is currently
     * playing this will start the episode from the beginning
     */
    fun previous()

    /**
     * Advances a currently played episode by a given time interval specified in [duration].
     */
    fun advanceBy(duration: Duration)

    /**
     * Rewinds a currently played episode by a given time interval specified in [duration].
     */
    fun rewindBy(duration: Duration)

    /**
     * Signal that user started seeking.
     */
    fun onSeekingStarted()

    /**
     * Seeks to a given time interval specified in [duration].
     */
    fun onSeekingFinished(duration: Duration)

    /**
     * Increases the speed of Player playback by a given time specified in [duration].
     */
    fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    /**
     * Decreases the speed of Player playback by a given time specified in [duration].
     */
    fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))
}
