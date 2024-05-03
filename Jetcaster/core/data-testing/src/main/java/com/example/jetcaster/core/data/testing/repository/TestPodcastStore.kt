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

package com.example.jetcaster.core.data.testing.repository

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.PodcastStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// TODO: Move to :testing module upon merging PR #1379
class TestPodcastStore : PodcastStore {

    private val podcastFlow = MutableStateFlow<List<Podcast>>(listOf())
    private val followedPodcasts = mutableSetOf<String>()
    override fun podcastWithUri(uri: String): Flow<Podcast> =
        podcastFlow.map { podcasts ->
            podcasts.first { it.uri == uri }
        }

    override fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo> =
        podcastFlow.map { podcasts ->
            val podcast = podcasts.first { it.uri == podcastUri }
            PodcastWithExtraInfo().apply {
                this.podcast = podcast
            }
        }

    override fun podcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcasts ->
            podcasts.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = followedPodcasts.contains(p.uri)
                }
            }
        }

    override fun followedPodcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcasts ->
            podcasts.filter {
                followedPodcasts.contains(it.uri)
            }.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = true
                }
            }
        }

    override fun searchPodcastByTitle(
        keyword: String,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcastList ->
            podcastList.filter {
                it.title.contains(keyword)
            }.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = true
                }
            }
        }

    override fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcastList ->
            podcastList.filter {
                it.title.contains(keyword)
            }.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = true
                }
            }
        }

    override suspend fun togglePodcastFollowed(podcastUri: String) {
        if (podcastUri in followedPodcasts) {
            unfollowPodcast(podcastUri)
        } else {
            followPodcast(podcastUri)
        }
    }

    override suspend fun followPodcast(podcastUri: String) {
        followedPodcasts.add(podcastUri)
    }

    override suspend fun unfollowPodcast(podcastUri: String) {
        followedPodcasts.remove(podcastUri)
    }

    override suspend fun addPodcast(podcast: Podcast) =
        podcastFlow.update { it + podcast }

    override suspend fun isEmpty(): Boolean =
        podcastFlow.first().isEmpty()
}
