package com.example.jetcaster.core.player

import com.example.jetcaster.core.data.database.model.Episode
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
import java.time.Duration
import kotlin.reflect.KProperty

class MockEpisodePlayer(
    private val mainDispatcher: CoroutineDispatcher
) : EpisodePlayer {

    private val _playerState = MutableStateFlow(EpisodePlayerState())
    private val _currentEpisode = MutableStateFlow<Episode?>(null)
    private val isPlaying = MutableStateFlow(false)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var timerJob: Job? = null
    init {
        coroutineScope.launch {
            // Combine streams here
            combine(
                _currentEpisode,
                isPlaying,
                timeElapsed
            ) { currentEpisode, isPlaying, timeElapsed ->
                EpisodePlayerState(
                    currentEpisode = currentEpisode,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed
                )
            }.catch {
                // TODO handle error state
                throw it
            }.collect {
                _playerState.value = it
            }
        }
    }

    override val playerState: StateFlow<EpisodePlayerState> = _playerState.asStateFlow()

    override var currentEpisode: Episode? by _currentEpisode

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
                delay(1000L)
                timeElapsed.update { it + Duration.ofSeconds(1) }
            }

            // Stop playing
            timeElapsed.value = Duration.ZERO
            isPlaying.value = false
        }
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

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun previous() {
        TODO("Not yet implemented")
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
