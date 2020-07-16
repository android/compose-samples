/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.data

import com.example.jetcaster.data.room.PodcastsDao
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Podcast] instances.
 */
class PodcastStore(
    private val podcastDao: PodcastsDao
) {
    /**
     * Return a flow containing the [Podcast] with the given [uri].
     */
    fun podcastWithUri(uri: String): Flow<Podcast> {
        return podcastDao.podcastWithUri(uri)
    }

    /**
     * Returns a flow containing the entire collection of podcasts, sorted by the last episode
     * publish date for each podcast.
     */
    fun podcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithLastEpisodeDate>> {
        return podcastDao.podcastsSortedByLastEpisode(limit)
    }

    /**
     * Add a new [Podcast] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addPodcast(podcast: Podcast) {
        podcastDao.insert(podcast)
    }

    suspend fun isEmpty(): Boolean = podcastDao.count() == 0
}
