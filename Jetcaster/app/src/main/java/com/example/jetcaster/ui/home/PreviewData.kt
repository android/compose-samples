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

package com.example.jetcaster.ui.home

import com.example.jetcaster.data.Category
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastWithExtraInfo
import java.time.OffsetDateTime
import java.time.ZoneOffset

val PreviewCategories = listOf(
    Category(name = "Crime"),
    Category(name = "News"),
    Category(name = "Comedy")
)

val PreviewPodcasts = listOf(
    Podcast(
        uri = "fakeUri://podcast/1",
        title = "Android Developers Backstage",
        author = "Android Developers"
    ),
    Podcast(
        uri = "fakeUri://podcast/2",
        title = "Google Developers podcast",
        author = "Google Developers"
    )
)

val PreviewPodcastsWithExtraInfo = PreviewPodcasts.mapIndexed { index, podcast ->
    PodcastWithExtraInfo().apply {
        this.podcast = podcast
        this.lastEpisodeDate = OffsetDateTime.now()
        this.isFollowed = index % 2 == 0
    }
}

val PreviewEpisodes = listOf(
    Episode(
        uri = "fakeUri://episode/1",
        podcastUri = PreviewPodcasts[0].uri,
        title = "Episode 140: Bubbles!",
        summary = "In this episode, Romain, Chet and Tor talked with Mady Melor and Artur Tsurkan from the System UI team about... Bubbles!",
        published = OffsetDateTime.of(2020, 6, 2, 9, 27, 0, 0, ZoneOffset.of("PST"))
    )
)
