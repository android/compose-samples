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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Post
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.home.BookmarkButton
import com.example.jetnews.utils.launchUiStateProducer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Stateful Article Screen that manages state using [launchUiStateProducer]
 *
 * @param postId (state) the post to show
 * @param postsRepository data source for this screen
 * @param onBack (event) request back navigation
 */
@Suppress("DEPRECATION") // allow ViewModelLifecycleScope call
@Composable
fun ArticleScreen(
    postId: String,
    postsRepository: PostsRepository,
    onBack: () -> Unit
) {
    val (post) = launchUiStateProducer(postsRepository, postId) {
        getPost(postId)
    }
    // TODO: handle errors when the repository is capable of creating them
    val postData = post.value.data ?: return

    // [collectAsState] will automatically collect a Flow<T> and return a State<T> object that
    // updates whenever the Flow emits a value. Collection is cancelled when [collectAsState] is
    // removed from the composition tree.
    val favorites by postsRepository.observeFavorites().collectAsState(setOf())
    val isFavorite = favorites.contains(postId)

    // Returns a [CoroutineScope] that is scoped to the lifecycle of [ArticleScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope = rememberCoroutineScope()

    ArticleScreen(
        post = postData,
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = {
            coroutineScope.launch { postsRepository.toggleFavorite(postId) }
        }
    )
}

/**
 * Stateless Article Screen that displays a single post.
 *
 * @param post (state) item to display
 * @param onBack (event) request navigate back
 * @param isFavorite (state) is this item currently a favorite
 * @param onToggleFavorite (event) request that this post toggle it's favorite state
 */
@Composable
fun ArticleScreen(
    post: Post,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {

    var showDialog by savedInstanceState { false }
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
            BottomBar(
                post = post,
                onUnimplementedAction = { showDialog = true },
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }
    )
}

/**
 * Bottom bar for Article screen
 *
 * @param post (state) used in share sheet to share the post
 * @param onUnimplementedAction (event) called when the user performs an unimplemented action
 * @param isFavorite (state) if this post is currently a favorite
 * @param onToggleFavorite (event) request this post toggle it's favorite status
 */
@Composable
private fun BottomBar(
    post: Post,
    onUnimplementedAction: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Surface(elevation = 2.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .preferredHeight(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(Icons.Filled.FavoriteBorder)
            }
            BookmarkButton(
                isBookmarked = isFavorite,
                onClick = onToggleFavorite
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

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 */
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

/**
 * Show a share sheet for a post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 */
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
        ArticleScreen(post, {}, false, {})
    }
}

@Preview("Article screen dark theme")
@Composable
fun PreviewArticleDark() {
    ThemedPreview(darkTheme = true) {
        val post = loadFakePost(post3.id)
        ArticleScreen(post, {}, false, {})
    }
}

@Composable
private fun loadFakePost(postId: String): Post {
    val context = ContextAmbient.current
    val post = runBlocking {
        (BlockingFakePostsRepository(context).getPost(postId) as Result.Success).data
    }
    return post
}
