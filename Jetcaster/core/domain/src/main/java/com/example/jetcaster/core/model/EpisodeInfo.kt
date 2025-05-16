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

package com.example.jetcaster.core.model

import com.example.jetcaster.core.data.database.model.Episode
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of an episode.
 */
data class EpisodeInfo(
    val uri: String = "",
    val podcastUri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val summary: String = "",
    val author: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
    val mediaUrls: List<String> = emptyList(),
)

fun Episode.asExternalModel(): EpisodeInfo = EpisodeInfo(
    uri = uri,
    podcastUri = podcastUri,
    title = title,
    subTitle = subtitle ?: "",
    summary = summary ?: "",
    author = author ?: "",
    published = published,
    duration = duration,
    mediaUrls = mediaUrls,
)

fun EpisodeInfo.asDaoModel(): Episode = Episode(
    uri = uri,
    title = title,
    subtitle = subTitle,
    summary = summary,
    author = author,
    published = published,
    duration = duration,
    podcastUri = podcastUri,
    mediaUrls = mediaUrls,
)
