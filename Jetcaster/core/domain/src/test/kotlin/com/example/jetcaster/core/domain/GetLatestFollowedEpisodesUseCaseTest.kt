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

package com.example.jetcaster.core.domain

import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.testing.repository.TestEpisodeStore
import com.example.jetcaster.core.data.testing.repository.TestPodcastStore
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLatestFollowedEpisodesUseCaseTest {

    private val episodeStore = TestEpisodeStore()
    private val podcastStore = TestPodcastStore()

    val useCase = GetLatestFollowedEpisodesUseCase(
        episodeStore = episodeStore,
        podcastStore = podcastStore
    )

    val testEpisodes = listOf(
        Episode(
            uri = "",
            podcastUri = testPodcasts[0].podcast.uri,
            title = "title1",
            published = OffsetDateTime.MIN
        ),
        Episode(
            uri = "",
            podcastUri = testPodcasts[0].podcast.uri,
            title = "title2",
            published = OffsetDateTime.now()
        ),
        Episode(
            uri = "",
            podcastUri = testPodcasts[1].podcast.uri,
            title = "title3",
            published = OffsetDateTime.MAX
        )
    )

    @Test
    fun whenNoFollowedPodcasts_emptyFlow() = runTest {
        val result = useCase()

        episodeStore.addEpisodes(testEpisodes)
        testPodcasts.forEach {
            podcastStore.addPodcast(it.podcast)
        }

        assertTrue(result.first().isEmpty())
    }

    @Test
    fun whenFollowedPodcasts_nonEmptyFlow() = runTest {
        val result = useCase()

        episodeStore.addEpisodes(testEpisodes)
        testPodcasts.forEach {
            podcastStore.addPodcast(it.podcast)
        }
        podcastStore.togglePodcastFollowed(testPodcasts[0].podcast.uri)

        assertTrue(result.first().isNotEmpty())
    }

    @Test
    fun whenFollowedPodcasts_sortedByPublished() = runTest {
        val result = useCase()

        episodeStore.addEpisodes(testEpisodes)
        testPodcasts.forEach {
            podcastStore.addPodcast(it.podcast)
        }
        podcastStore.togglePodcastFollowed(testPodcasts[0].podcast.uri)

        result.first().zipWithNext {
                ep1, ep2 ->
            ep1.episode.published > ep2.episode.published
        }.all { it }
    }
}
