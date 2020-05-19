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

import android.content.Context
import androidx.ui.graphics.imageFromResource
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources synchronously.
 */
class BlockingFakePostsRepository(private val context: Context) : PostsRepository {

    private val postsWithResources: List<Post> by lazy {
        posts.map {
            it.copy(
                image = imageFromResource(context.resources, it.imageId),
                imageThumb = imageFromResource(context.resources, it.imageThumbId)
            )
        }
    }

    override fun getPost(postId: String, callback: (Result<Post?>) -> Unit) {
        callback(Result.Success(postsWithResources.find { it.id == postId }))
    }

    override fun getPosts(callback: (Result<List<Post>>) -> Unit) {
        callback(Result.Success(postsWithResources))
    }
}
