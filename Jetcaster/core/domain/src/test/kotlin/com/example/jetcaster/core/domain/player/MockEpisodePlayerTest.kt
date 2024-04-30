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

package com.example.jetcaster.core.domain.player

import com.example.jetcaster.core.player.MockEpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import java.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
    fun whenPlay_incrementsByPlaySpeed() = runTest(testDispatcher) {
        val playSpeed = Duration.ofSeconds(2)
        val currEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = Duration.ofSeconds(60)
        )
        mockEpisodePlayer.currentEpisode = currEpisode
        mockEpisodePlayer.playerSpeed = playSpeed

        mockEpisodePlayer.play()
        advanceTimeBy(playSpeed.toMillis() + 300)

        assertEquals(playSpeed, mockEpisodePlayer.playerState.value.timeElapsed)
    }

    @Test
    fun whenPlayDone_playerAutoPlaysNextEpisode() = runTest(testDispatcher) {
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
    fun whenNext_queueIsNotEmpty_autoPlaysNextEpisode() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = duration
        )

        mockEpisodePlayer.currentEpisode = currEpisode
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        mockEpisodePlayer.next()
        advanceTimeBy(100)

        assertTrue(mockEpisodePlayer.playerState.value.isPlaying)
    }
    @Test
    fun whenPlayListOfEpisodes_playerAutoPlaysNextEpisode() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currEpisode = PlayerEpisode(
            uri = "currentEpisode",
            duration = duration
        )
        val firstEpisodeFromList = PlayerEpisode(
            uri = "firstEpisodeFromList",
            duration = duration
        )
        val secondEpisodeFromList = PlayerEpisode(
            uri = "secondEpisodeFromList",
            duration = duration
        )
        val episodeListToBeAddedToTheQueue: List<PlayerEpisode> = listOf(
            firstEpisodeFromList, secondEpisodeFromList
        )
        mockEpisodePlayer.currentEpisode = currEpisode

        mockEpisodePlayer.play(episodeListToBeAddedToTheQueue)
        assertEquals(firstEpisodeFromList, mockEpisodePlayer.currentEpisode)

        advanceTimeBy(duration.toMillis() + 1)
        assertEquals(secondEpisodeFromList, mockEpisodePlayer.currentEpisode)

        advanceTimeBy(duration.toMillis() + 1)
        assertEquals(currEpisode, mockEpisodePlayer.currentEpisode)
    }

    @Test
    fun whenNext_queueIsEmpty_doesNothing() {
        val episode = testEpisodes[0]
        mockEpisodePlayer.currentEpisode = episode
        mockEpisodePlayer.play()

        mockEpisodePlayer.next()

        assertEquals(episode, mockEpisodePlayer.currentEpisode)
    }

    @Test
    fun whenAddToQueue_queueIsNotEmpty() = runTest(testDispatcher) {
        testEpisodes.forEach { mockEpisodePlayer.addToQueue(it) }

        advanceUntilIdle()

        val queue = mockEpisodePlayer.playerState.value.queue
        assertEquals(testEpisodes.size, queue.size)
        testEpisodes.forEachIndexed { index, playerEpisode ->
            assertEquals(playerEpisode, queue[index])
        }
    }

    @Test
    fun whenNext_queueIsNotEmpty_removeFromQueue() = runTest(testDispatcher) {
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
    fun whenNext_queueIsNotEmpty_notRemovedFromQueue() = runTest(testDispatcher) {
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
    fun whenPrevious_queueIsEmpty_resetSameEpisode() = runTest(testDispatcher) {
        mockEpisodePlayer.currentEpisode = testEpisodes[0]
        mockEpisodePlayer.play()
        advanceTimeBy(1000L)

        mockEpisodePlayer.previous()
        assertEquals(0, mockEpisodePlayer.playerState.value.timeElapsed.toMillis())
        assertEquals(testEpisodes[0], mockEpisodePlayer.currentEpisode)
    }
}
