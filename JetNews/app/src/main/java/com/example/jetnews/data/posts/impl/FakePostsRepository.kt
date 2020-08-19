/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.data.posts.impl

import android.content.res.Resources
import androidx.compose.ui.graphics.imageFromResource
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources after some delay in a background thread.
 * 1/3 of the times will throw an error.
 *
 * The result is posted to the resultThreadHandler passed as a parameter.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakePostsRepository(
    private val resources: Resources
) : PostsRepository {

    /**
     * Simulates preparing the data for each post.
     *
     * DISCLAIMER: Loading resources with the ApplicationContext isn't ideal as it isn't themed.
     * This should be done from the UI layer.
     */
    private val postsWithResources: List<Post> by lazy {
        posts.map {
            it.copy(
                image = imageFromResource(resources, it.imageId),
                imageThumb = imageFromResource(resources, it.imageThumbId)
            )
        }
    }

    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getPost(postId: String): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = postsWithResources.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPosts(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            delay(800) // pretend we're on a slow network
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(postsWithResources)
            }
        }
    }

    override fun getFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        if (!set.add(postId)) {
            set.remove(postId)
        }
        favorites.value = set.toSet()
    }

    /**
     * Randomly fail some loads to simulate a real network
     */
    private fun shouldRandomlyFail(): Boolean = Random.nextFloat() < 0.20f
}
