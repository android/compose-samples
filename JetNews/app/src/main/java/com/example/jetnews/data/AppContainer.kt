/*
 * Copyright 2020 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.FakePostsRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainer(private val applicationContext: Context) {

    private val executorService: ExecutorService by lazy {
        Executors.newFixedThreadPool(4)
    }

    private val mainThreadHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    val postsRepository: PostsRepository by lazy {
        FakePostsRepository(
            executorService = executorService,
            resultThreadHandler = mainThreadHandler,
            resources = applicationContext.resources
        )
    }

    val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}
