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

import coil.network.HttpException
import com.rometools.modules.itunes.EntryInformation
import com.rometools.modules.itunes.FeedInformation
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

/**
 * A class which fetches some selected podcast RSS feeds.
 *
 * @param okHttpClient [OkHttpClient] to use for network requests
 * @param syndFeedInput [SyndFeedInput] to use for parsing RSS feeds.
 * @param ioDispatcher [CoroutineDispatcher] to use for running fetch requests.
 */
class PodcastsFetcher(
    private val okHttpClient: OkHttpClient,
    private val syndFeedInput: SyndFeedInput,
    private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * It seems that most podcast hosts do not implement HTTP caching appropriately.
     * Instead of fetching data on every app open, we instead allow the use of 'stale'
     * network responses (up to 8 hours).
     */
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    /**
     * Returns a [Flow] which fetches each podcast feed and emits it in turn.
     *
     * The feeds are fetched concurrently, meaning that the resulting emission order may not
     * match the order of [feedUrls].
     */
    operator fun invoke(feedUrls: List<String>): Flow<PodcastRssResponse> = feedUrls.asFlow()
        // We use flatMapMerge here to achieve concurrent fetching/parsing of the feeds.
        .flatMapMerge { feedUrl ->
            flow { emit(fetchPodcast(feedUrl)) }
        }

    private suspend fun fetchPodcast(url: String): PodcastRssResponse {
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        val response = okHttpClient.newCall(request).await()

        // If the network request wasn't successful, throw an exception
        if (!response.isSuccessful) throw HttpException(response)

        // Otherwise we can parse the response using a Rome SyndFeedInput, then map it
        // to a Podcast instance. We run this on the IO dispatcher since the parser is reading
        // from a stream.
        return withContext(ioDispatcher) {
            response.body!!.use { body ->
                syndFeedInput.build(body.charStream()).toPodcastResponse(url)
            }
        }
    }
}

data class PodcastRssResponse(
    val podcast: Podcast,
    val episodes: List<Episode>,
    val categories: Set<Category>
)

/**
 * Map a Rome [SyndFeed] instance to our own [Podcast] data class.
 */
private fun SyndFeed.toPodcastResponse(feedUrl: String): PodcastRssResponse {
    val podcastUri = uri ?: feedUrl
    val episodes = entries.map { it.toEpisode(podcastUri) }

    val feedInfo = getModule(PodcastModuleDtd) as? FeedInformation
    val podcast = Podcast(
        uri = podcastUri,
        title = title,
        description = feedInfo?.summary ?: description,
        author = author,
        copyright = copyright,
        imageUrl = feedInfo?.imageUri?.toString()
    )

    val categories = feedInfo?.categories
        ?.map { Category(name = it.name) }
        ?.toSet() ?: emptySet()

    return PodcastRssResponse(podcast, episodes, categories)
}

/**
 * Map a Rome [SyndEntry] instance to our own [Episode] data class.
 */
private fun SyndEntry.toEpisode(podcastUri: String): Episode {
    val entryInformation = getModule(PodcastModuleDtd) as? EntryInformation
    return Episode(
        uri = uri,
        podcastUri = podcastUri,
        title = title,
        author = author,
        summary = entryInformation?.summary ?: description?.value,
        subtitle = entryInformation?.subtitle,
        published = Instant.ofEpochMilli(publishedDate.time).atOffset(ZoneOffset.UTC),
        duration = entryInformation?.duration?.milliseconds?.let { Duration.ofMillis(it) }
    )
}

/**
 * Most feeds use the following DTD to include extra information related to
 * their podcast. Info such as images, summaries, duration, categories is sometimes only available
 * via this attributes in this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"
