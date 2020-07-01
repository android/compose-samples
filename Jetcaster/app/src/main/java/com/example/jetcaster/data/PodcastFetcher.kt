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

import com.example.jetcaster.Graph
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * A class which fetches the current top-podcasts from a RSS feed. Uses Retrofit & Moshi underneath.
 */
internal object PodcastsFetcher {
    private val retrofit by lazy {
        Retrofit.Builder()
            .client(Graph.okHttpClient)
            .baseUrl("https://rss.itunes.apple.com/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create(Graph.moshi))
            .build()
    }

    private val api by lazy {
        retrofit.create(Api::class.java)
    }

    /**
     * Starts a request on a background thread.
     *
     * @param country The country of which to fetch the top-podcasts from. Defaults to `us`.
     * @param count The maximum number of items to fetch.
     */
    suspend operator fun invoke(
        country: String = defaultCountry,
        count: Int = defaultCount
    ): List<Podcast> = withContext(Dispatchers.IO) {
        api.topPodcasts(country, count).feed.results
    }

    internal interface Api {
        @GET("{country}/podcasts/top-podcasts/all/{count}/non-explicit.json")
        suspend fun topPodcasts(
            @Path("country") country: String,
            @Path("count") count: Int
        ): ApiResponse
    }

    private const val defaultCountry = "us"
    private const val defaultCount = 50
}

@JsonClass(generateAdapter = true)
internal class ApiResponse(val feed: ApiFeedResponse)

@JsonClass(generateAdapter = true)
internal class ApiFeedResponse(val results: List<Podcast>)
