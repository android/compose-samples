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

package com.example.jetnews.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.copyWithResult
import kotlinx.coroutines.launch

class ArticleViewModel(val postId: String, val postsRepository: PostsRepository) : ViewModel() {

    private val _post = MutableLiveData<UiState<Post>>(UiState())
    val post: LiveData<UiState<Post>> = _post

    val favorites = postsRepository.getFavorites()

    init {
        refreshPost()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    private fun refreshPost() {
        viewModelScope.launch {
            try {
                _post.value = _post.value?.copy(loading = true)
                _post.value = _post.value?.copyWithResult(postsRepository.getPost(postId))
            } finally {
                _post.value = _post.value?.copy(loading = false)
            }
        }
    }
}

class ArticleViewModelFactory(
    private val postId: String,
    private val postsRepository: PostsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ArticleViewModel(postId, postsRepository) as T
    }
}
