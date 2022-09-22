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

package com.example.jetnews.data.posts.impl

import android.util.Log
import com.example.jetnews.data.Result
import com.example.jetnews.data.db.FavoritesDao
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Favorite
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.utils.addOrRemove
import com.example.jetnews.utils.suspendAddOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources after some delay in a background thread.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakePostsRepository(private val favoriteDb: FavoritesDao) : PostsRepository {

    // for now, store these in memory
    val favorites = MutableStateFlow<Set<String>>(setOf())

    // Used to make suspend functions that read and update state safe to call from any thread
    private val mutex = Mutex()

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            delay(800) // pretend we're on a slow network
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(posts)
            }
        }
    }

    override suspend fun observeToggleFavorites(): Flow<Set<String>> = favorites

    override fun observeFavorites(): Flow<Set<String>>{
       return favoriteDb.collectFavorites()
           .flowOn(Dispatchers.IO)
           .map { list -> list.map { it.id }.toSet()}
    }

    override suspend fun toggleFavorite(postId: String) {
        mutex.withLock {
            val set = favorites.value.toMutableSet()
            withContext(Dispatchers.IO){
                getPost(postId).run {
                    when(this){
                        is Result.Success ->{
                            set.suspendAddOrRemove(postId, {
                                favoriteDb.insert(data.toFavorite())
                            }, {removedId ->
                                favoriteDb.deleteFavoriteById(removedId)
                            })
                            favorites.value = set.toSet()
                        }
                        is Result.Error ->{
                            // An error
                        }
                    }
                }
            }
        }
    }



    override suspend fun getFavorites(): Set<String> {
       return favoriteDb.getFavoritePostIds().toSet()
    }

//    override suspend fun isFavoritePost(postId: String): Boolean {
//        return favoriteDb.findFavoritePostId(postId).last() == postId
//    }

    // used to drive "random" failure in a predictable pattern, making the first request always
    // succeed
    private var requestCount = 0

    /**
     * Randomly fail some loads to simulate a real network.
     *
     * This will fail deterministically every 5 requests
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}
