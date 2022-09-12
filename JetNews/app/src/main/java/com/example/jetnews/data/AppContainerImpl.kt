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

package com.example.jetnews.data

import android.content.Context
import com.example.jetnews.data.db.JetNewsDatabase
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.FakePostsRepository
import com.example.jetnews.favorites.FavoriteRepository
import com.example.jetnews.favorites.FavoriteRepositoryImpl

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
    val favoritesRepository: FavoriteRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val favoriteDao get() =  JetNewsDatabase.getDatabase(applicationContext).favoritesDao()

    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository(favoriteDao)
    }

    override val interestsRepository: InterestsRepository by lazy {
       FakeInterestsRepository()
    }

    override val favoritesRepository: FavoriteRepository
       by lazy {
           FavoriteRepositoryImpl(favoriteDao)
       }
}
