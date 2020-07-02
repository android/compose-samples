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

import android.util.Log
import com.dropbox.android.external.store4.FetcherResult
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * This should really be a Room database.
 */
private object PodcastsInMemorySourceOfTruth {
    val podcasts: Flow<List<Podcast>>
        get() = _podcasts

    private val _podcasts = MutableStateFlow(emptyList<Podcast>())

    fun insert(podcasts: List<Podcast>) {
        _podcasts.value = podcasts
    }

    fun clear() {
        _podcasts.value = emptyList()
    }
}

/**
 * A [Store] which acts as a data repository for our list of [Podcast]s.
 *
 * This store is built to use our [PodcastsFetcher] to fetch new responses from
 * the network, along with a very simple [SourceOfTruth] in [PodcastsInMemorySourceOfTruth] which
 * stores the entries in memory. Ideally this would use a Room database, or similar.
 *
 * For more information on [Store], see [here](https://github.com/dropbox/Store).
 */
val PodcastStore by lazy {
    StoreBuilder.from(
        fetcher = nonFlowFetcher<Unit, List<Podcast>> {
            try {
                FetcherResult.Data(PodcastsFetcher())
            } catch (t: Throwable) {
                Log.d("PodcastStore", "Error during fetch", t)
                FetcherResult.Error.Exception(t)
            }
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { PodcastsInMemorySourceOfTruth.podcasts },
            writer = { _: Unit, items ->
                PodcastsInMemorySourceOfTruth.insert(items)
            },
            delete = { PodcastsInMemorySourceOfTruth.clear() },
            deleteAll = { PodcastsInMemorySourceOfTruth.clear() }
        )
    ).build()
}
