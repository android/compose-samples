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

package com.example.jetnews.ui.effect

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.setValue
import androidx.compose.state
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.state.UiState

/**
 * Effect that interacts with the repository to obtain a post with postId to display on the screen.
 *
 * To load asynchronous data, effects are better pattern than using @Model classes since
 * effects are Compose lifecycle aware.
 *
 * FIXME: Is it possible to reuse uiStateFrom for this use case?
 */
@Composable
fun fetchPost(postId: String, postsRepository: PostsRepository): UiState<Post> {

    var postState: UiState<Post> by state<UiState<Post>> { UiState.Loading }

    // Whenever this effect is used in a composable function, it'll load data from the repository
    // when the first composition is applied
    onActive {
        postsRepository.getPost(postId) { result ->
            postState = when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        UiState.Success(result.data)
                    } else {
                        UiState.Error(Exception("postId doesn't exist"))
                    }
                }
                is Result.Error -> UiState.Error(result.exception)
            }
        }
    }

    return postState
}
