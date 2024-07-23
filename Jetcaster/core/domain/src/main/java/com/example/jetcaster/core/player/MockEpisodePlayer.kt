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
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MockEpisodePlayer(
    private val mainDispatcher: CoroutineDispatcher
) : EpisodePlayer {

    private val _playerState = MutableStateFlow(EpisodePlayerState())
    private val _currentEpisode = MutableStateFlow<PlayerEpisode?>(null)
    private val queue = MutableStateFlow<List<PlayerEpisode>>(emptyList())
    private val isPlaying = MutableStateFlow(false)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val _playerSpeed = MutableStateFlow(DefaultPlaybackSpeed)
    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var timerJob: Job? = null

    init {
        coroutineScope.launch {
            // Combine streams here
            combine(
                _currentEpisode,
                queue,
                isPlaying,
                timeElapsed,
                _playerSpeed
            ) { currentEpisode, queue, isPlaying, timeElapsed, playerSpeed ->
                EpisodePlayerState(
                    currentEpisode = currentEpisode,
                    queue = queue,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed,
                    playbackSpeed = playerSpeed
                )
            }.catch {
                // TODO handle error state
                throw it
            }.collect {
                _playerState.value = it
            }
        }
    }

    override var playerSpeed: Duration = _playerSpeed.value

    override val playerState: StateFlow<EpisodePlayerState> = _playerState.asStateFlow()

    override var currentEpisode: PlayerEpisode? by _currentEpisode
    override fun addToQueue(episode: PlayerEpisode) {
        queue.update {
            it + episode
        }
    }

    override fun removeAllFromQueue() {
        queue.value = emptyList()
    }

    override fun play() {
        // Do nothing if already playing
        if (isPlaying.value) {
            return
        }

        val episode = _currentEpisode.value ?: return

        isPlaying.value = true
        timerJob = coroutineScope.launch {
            // Increment timer by a second
            while (isActive && timeElapsed.value < episode.duration) {
                delay(playerSpeed.toMillis())
                timeElapsed.update { it + playerSpeed }
            }

            // Once done playing, see if
            isPlaying.value = false
            timeElapsed.value = Duration.ZERO

            if (hasNext()) {
                next()
            }
        }
    }

    override fun play(playerEpisode: PlayerEpisode) {
        play(listOf(playerEpisode))
    }

    override fun play(playerEpisodes: List<PlayerEpisode>) {
        if (isPlaying.value) {
            pause()
        }

        // Keep the currently playing episode in the queue
        val playingEpisode = _currentEpisode.value
        var previousList: List<PlayerEpisode> = emptyList()
        queue.update { queue ->
            playerEpisodes.map { episode ->
                if (queue.contains(episode)) {
                    val mutableList = queue.toMutableList()
                    mutableList.remove(episode)
                    previousList = mutableList
                } else {
                    previousList = queue
                }
            }
            if (playingEpisode != null) {
                playerEpisodes + listOf(playingEpisode) + previousList
            } else {
                playerEpisodes + previousList
            }
        }

        next()
    }

    override fun pause() {
        isPlaying.value = false

        timerJob?.cancel()
        timerJob = null
    }

    override fun stop() {
        isPlaying.value = false
        timeElapsed.value = Duration.ZERO

        timerJob?.cancel()
        timerJob = null
    }

    override fun advanceBy(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        timeElapsed.update {
            (it + duration).coerceAtMost(currentEpisodeDuration)
        }
    }

    override fun rewindBy(duration: Duration) {
        timeElapsed.update {
            (it - duration).coerceAtLeast(Duration.ZERO)
        }
    }

    override fun onSeekingStarted() {
        // Need to pause the player so that it doesn't compete with timeline progression.
        pause()
    }

    override fun onSeekingFinished(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        timeElapsed.update { duration.coerceIn(Duration.ZERO, currentEpisodeDuration) }
        play()
    }

    override fun increaseSpeed(speed: Duration) {
        _playerSpeed.value += speed
    }

    override fun decreaseSpeed(speed: Duration) {
        _playerSpeed.value -= speed
    }

    override fun next() {
        val q = queue.value
        if (q.isEmpty()) {
            return
        }

        timeElapsed.value = Duration.ZERO
        val nextEpisode = q[0]
        currentEpisode = nextEpisode
        queue.value = q - nextEpisode
        play()
    }

    override fun previous() {
        timeElapsed.value = Duration.ZERO
        isPlaying.value = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun hasNext(): Boolean {
        return queue.value.isNotEmpty()
    }
}

// Used to enable property delegation
private operator fun <T> MutableStateFlow<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T
) {
    this.value = value
}

private operator fun <T> MutableStateFlow<T>.getValue(thisObj: Any?, property: KProperty<*>): T =
    this.value
