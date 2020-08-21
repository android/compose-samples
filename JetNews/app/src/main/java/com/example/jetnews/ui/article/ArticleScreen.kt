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

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.successOr
import com.example.jetnews.model.Post
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.effect.fetchPost
import com.example.jetnews.ui.home.BookmarkButton
import com.example.jetnews.ui.home.isFavorite
import com.example.jetnews.ui.home.toggleBookmark
import com.example.jetnews.ui.state.UiState

@Composable
fun ArticleScreen(postId: String, postsRepository: PostsRepository, onBack: () -> Unit) {
    val postsState = fetchPost(postId, postsRepository)
    if (postsState is UiState.Success<Post>) {
        ArticleScreen(postsState.data, onBack)
    }
}

@Composable
private fun ArticleScreen(post: Post, onBack: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = MaterialTheme.typography.subtitle2,
                        color = contentColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                }
            )
        },
        bodyContent = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            PostContent(post, modifier)
        },
        bottomBar = {
            BottomBar(post) { showDialog = true }
        }
    )
}

@Composable
private fun BottomBar(post: Post, onUnimplementedAction: () -> Unit) {
    Surface(elevation = 2.dp) {
        Row(
            verticalGravity = Alignment.CenterVertically,
            modifier = Modifier
                .preferredHeight(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(Icons.Filled.FavoriteBorder)
            }
            BookmarkButton(
                isBookmarked = isFavorite(postId = post.id),
                onBookmark = { toggleBookmark(postId = post.id) }
            )
            val context = ContextAmbient.current
            IconButton(onClick = { sharePost(post, context) }) {
                Icon(Icons.Filled.Share)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onUnimplementedAction) {
                Icon(vectorResource(R.drawable.ic_text_settings))
            }
        }
    }
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}

private fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, "Share post"))
}

@Preview("Article screen")
@Composable
fun PreviewArticle() {
    ThemedPreview {
        val post = loadFakePost(post3.id)
        ArticleScreen(post, {})
    }
}

@Preview("Article screen dark theme")
@Composable
fun PreviewArticleDark() {
    ThemedPreview(darkTheme = true) {
        val post = loadFakePost(post3.id)
        ArticleScreen(post, {})
    }
}

@Composable
private fun loadFakePost(postId: String): Post {
    var post: Post? = null
    BlockingFakePostsRepository(ContextAmbient.current).getPost(postId) { result ->
        post = result.successOr(null)
    }
    return post!!
}
