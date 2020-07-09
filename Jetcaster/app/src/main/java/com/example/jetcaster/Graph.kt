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

package com.example.jetcaster

import android.content.Context
import com.example.jetcaster.data.CategoryStore
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.data.PodcastsFetcher
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {
    lateinit var okHttpClient: OkHttpClient
        private set

    val syndFeedInput by lazy { SyndFeedInput() }

    val podcastFetcher by lazy {
        PodcastsFetcher(
            okHttpClient = okHttpClient,
            syndFeedInput = syndFeedInput,
            ioDispatcher = ioDispatcher
        )
    }

    val podcastStore by lazy {
        PodcastStore(
            mainDispatcher = mainDispatcher,
            computationDispatcher = computationDispatcher
        )
    }

    val episodeStore by lazy {
        EpisodeStore(
            mainDispatcher = mainDispatcher,
            computationDispatcher = computationDispatcher
        )
    }

    val categoryStore by lazy {
        CategoryStore(
            podcastStore = podcastStore,
            episodeStore = episodeStore,
            mainDispatcher = mainDispatcher,
            computationDispatcher = computationDispatcher
        )
    }

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val computationDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    fun provide(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), 20 * 1024 * 1024))
            .apply {
                if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
            }
            .build()
    }
}
