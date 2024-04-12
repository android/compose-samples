package com.example.jetcaster.core.player

import com.example.jetcaster.core.model.PlayerEpisode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class MockEpisodePlayerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockEpisodePlayer = MockEpisodePlayer(testDispatcher)
    private val testEpisodes = listOf(
        PlayerEpisode(
            uri = "uri1",
            duration = Duration.ofSeconds(60)
        ),
        PlayerEpisode(
            uri = "uri2",
            duration = Duration.ofSeconds(60)
        ),
        PlayerEpisode(
            uri = "uri3",
            duration = Duration.ofSeconds(60)
        ),
    )

    @Test
    fun playerAutoPlaysNextEpisode() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = duration
        )
        mockEpisodePlayer.currentEpisode = currEpisode
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        mockEpisodePlayer.play()
        advanceTimeBy(duration.toMillis() + 1)

        assertEquals(testEpisodes.first(), mockEpisodePlayer.currentEpisode)
    }

    @Test
    fun whenNextQueueEmpty_doesNothing() {
        val episode = testEpisodes[0]
        mockEpisodePlayer.currentEpisode = episode
        mockEpisodePlayer.play()

        mockEpisodePlayer.next()

        assertEquals(episode, mockEpisodePlayer.currentEpisode)
    }

    @Test
    fun whenAddToQueue_queueNotEmpty() = runTest(testDispatcher) {
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        advanceUntilIdle()

        val queue = mockEpisodePlayer.playerState.value.queue
        assertEquals(testEpisodes.size, queue.size)
        testEpisodes.forEachIndexed { index, playerEpisode ->
            assertEquals(playerEpisode, queue[index])
        }
    }

    @Test
    fun whenNextQueueNotEmpty_removeFromQueue() = runTest(testDispatcher) {
        mockEpisodePlayer.currentEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = Duration.ofSeconds(60)
        )
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        mockEpisodePlayer.play()
        advanceTimeBy(100)

        mockEpisodePlayer.next()
        advanceTimeBy(100)

        assertEquals(testEpisodes.first(), mockEpisodePlayer.currentEpisode)

        val queue = mockEpisodePlayer.playerState.value.queue
        assertEquals(testEpisodes.size - 1, queue.size)
    }

    @Test
    fun whenNextQueueNotEmpty_notRemovedFromQueue() = runTest(testDispatcher) {
        mockEpisodePlayer.currentEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = Duration.ofSeconds(60)
        )
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        mockEpisodePlayer.play()
        advanceTimeBy(100)

        // TODO override next?
        mockEpisodePlayer.next()
        advanceTimeBy(100)

        assertEquals(testEpisodes.first(), mockEpisodePlayer.currentEpisode)

        val queue = mockEpisodePlayer.playerState.value.queue
        assertEquals(testEpisodes.size - 1, queue.size)
    }

    @Test
    fun whenPreviousQueueEmpty_resetSameEpisode() = runTest(testDispatcher) {
        mockEpisodePlayer.currentEpisode = testEpisodes[0]
        mockEpisodePlayer.play()
        advanceTimeBy(1000L)

        mockEpisodePlayer.previous()
        assertEquals(0, mockEpisodePlayer.playerState.value.timeElapsed.toMillis())
        assertEquals(testEpisodes[0], mockEpisodePlayer.currentEpisode)
    }
}
