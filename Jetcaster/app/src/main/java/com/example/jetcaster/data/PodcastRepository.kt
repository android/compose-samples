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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Data repository for Podcasts.
 */
class PodcastRepository(
    private val podcastsFetcher: PodcastsFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun updatePodcasts() {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else {
            refreshingJob = scope.launch {
                // First clear the stores
                podcastStore.clear()
                episodeStore.clear()

                // Now fetch the podcasts, and add each to each store
                podcastsFetcher(SampleFeeds).collect { podcastWithEpisodes ->
                    podcastStore.addPodcast(podcastWithEpisodes.podcast)
                    episodeStore.addEpisodes(podcastWithEpisodes.episodes)
                }
            }
        }
    }
}
