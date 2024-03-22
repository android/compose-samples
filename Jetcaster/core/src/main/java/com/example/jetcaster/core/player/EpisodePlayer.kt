package com.example.jetcaster.core.player

import com.example.jetcaster.core.data.database.model.Episode
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

data class EpisodePlayerState(
    val currentEpisode: Episode? = null,
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
    var currentEpisode: Episode?

    /**
     * Plays the current episode
     */
    fun play()

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
}
