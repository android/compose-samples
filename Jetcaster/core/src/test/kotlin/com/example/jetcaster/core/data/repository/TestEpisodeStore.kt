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

package com.example.jetcaster.core.data.repository

import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestEpisodeStore : EpisodeStore {

    private val episodesFlow = MutableStateFlow<List<Episode>>(listOf())
    override fun episodeWithUri(episodeUri: String): Flow<Episode> =
        episodesFlow.map {
            it.first { it.uri == episodeUri }
        }

    override fun episodesInPodcast(podcastUri: String, limit: Int): Flow<List<EpisodeToPodcast>> =
        episodesFlow.map { episodes ->
            episodes.filter {
                it.podcastUri == podcastUri
            }.map { e ->
                EpisodeToPodcast().apply {
                    episode = e
                }
            }
        }

    override suspend fun addEpisodes(episodes: Collection<Episode>) =
        episodesFlow.update {
            it + episodes
        }

    override suspend fun isEmpty(): Boolean =
        episodesFlow.first().isEmpty()
}
