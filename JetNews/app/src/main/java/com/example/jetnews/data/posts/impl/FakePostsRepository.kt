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
import android.os.Handler
import androidx.ui.graphics.imageFromResource
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import java.util.concurrent.ExecutorService
import kotlin.random.Random

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources after some delay in a background thread.
 * 1/3 of the times will throw an error.
 *
 * The result is posted to the resultThreadHandler passed as a parameter.
 */
class FakePostsRepository(
    private val executorService: ExecutorService,
    private val resultThreadHandler: Handler,
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

    override fun getPost(postId: String, callback: (Result<Post?>) -> Unit) {
        executeInBackground(callback) {
            resultThreadHandler.post {
                callback(Result.Success(
                    postsWithResources.find { it.id == postId }
                ))
            }
        }
    }

    override fun getPosts(callback: (Result<List<Post>>) -> Unit) {
        executeInBackground(callback) {
            simulateNetworkRequest()
            Thread.sleep(1500L)
            if (shouldRandomlyFail()) {
                throw IllegalStateException()
            }
            resultThreadHandler.post { callback(Result.Success(postsWithResources)) }
        }
    }

    /**
     * Executes a block of code in the past and returns an error in the [callback]
     * if [block] throws an exception.
     */
    private fun executeInBackground(callback: (Result<Nothing>) -> Unit, block: () -> Unit) {
        executorService.execute {
            try {
                block()
            } catch (e: Exception) {
                resultThreadHandler.post { callback(Result.Error(e)) }
            }
        }
    }

    /**
     * Simulates network request
     */
    private var networkRequestDone = false
    private fun simulateNetworkRequest() {
        if (!networkRequestDone) {
            Thread.sleep(2000L)
            networkRequestDone = true
        }
    }

    /**
     * 1/3 requests should fail loading
     */
    private fun shouldRandomlyFail(): Boolean = Random.nextFloat() < 0.33f
}
