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

package com.example.jetnews.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.SwipeToRefreshLayout
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.theme.snackbarAction
import com.example.jetnews.utils.launchUiStateProducer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Stateful HomeScreen which manages state using [launchUiStateProducer]
 *
 * @param navigateTo (event) request navigation to [Screen]
 * @param postsRepository data source for this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    navigateTo: (Screen) -> Unit,
    postsRepository: PostsRepository,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val (postUiState, refreshPost, clearError) = launchUiStateProducer(postsRepository) {
        getPosts()
    }

    // [collectAsState] will automatically collect a Flow<T> and return a State<T> object that
    // updates whenever the Flow emits a value. Collection is cancelled when [collectAsState] is
    // removed from the composition tree.
    val favorites by postsRepository.observeFavorites().collectAsState(setOf())

    // Returns a [CoroutineScope] that is scoped to the lifecycle of [HomeScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope = rememberCoroutineScope()

    HomeScreen(
        posts = postUiState.value,
        favorites = favorites,
        onToggleFavorite = {
            coroutineScope.launch { postsRepository.toggleFavorite(it) }
        },
        onRefreshPosts = refreshPost,
        onErrorDismiss = clearError,
        navigateTo = navigateTo,
        scaffoldState = scaffoldState
    )
}

/**
 * Responsible for displaying the Home Screen of this application.
 *
 * Stateless composable is not coupled to any specific state management.
 *
 * @param posts (state) the data to show on the screen
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) request the current error be dismissed
 * @param navigateTo (event) request navigation to [Screen]
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    posts: UiState<List<Post>>,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Home,
                closeDrawer = { scaffoldState.drawerState.close() },
                navigateTo = navigateTo
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Jetnews") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState.open() }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            LoadingContent(
                empty = posts.initialLoad,
                emptyContent = { FullScreenLoading() },
                loading = posts.loading,
                onRefresh = onRefreshPosts,
                content = {
                    HomeScreenErrorAndContent(
                        posts = posts,
                        onRefresh = {
                            onRefreshPosts()
                        },
                        onErrorDismiss = onErrorDismiss,
                        navigateTo = navigateTo,
                        favorites = favorites,
                        onToggleFavorite = onToggleFavorite,
                        modifier = modifier
                    )
                }
            )
        }
    )
}

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param loading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param content (slot) the main content to show
 */
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = onRefresh,
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .preferredSize(36.dp)
                            .padding(4.dp)
                    )
                }
            },
            content = content,
        )
    }
}

/**
 * Responsible for displaying any error conditions around [PostList].
 *
 * @param posts (state) list of posts and error state to display
 * @param onRefresh (event) request to refresh data
 * @param onErrorDismiss (event) request that the current error be dismissed
 * @param navigateTo (event) request navigation to [Screen]
 * @param modifier modifier for root element
 */
@Composable
private fun HomeScreenErrorAndContent(
    posts: UiState<List<Post>>,
    onRefresh: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Stack(modifier = modifier.fillMaxSize()) {
        if (posts.data != null) {
            PostList(posts.data, navigateTo, favorites, onToggleFavorite)
        } else if (!posts.hasError) {
            // if there are no posts, and no error, let the user refresh manually
            TextButton(onClick = onRefresh, Modifier.fillMaxSize()) {
                Text("Tap to load content", textAlign = TextAlign.Center)
            }
        }
        ErrorSnackbar(
            showError = posts.hasError,
            onErrorAction = onRefresh,
            onDismiss = onErrorDismiss,
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

/**
 * Display a list of posts.
 *
 * When a post is clicked on, [navigateTo] will be called to navigate to the detail screen for that
 * post.
 *
 * @param posts (state) the list to display
 * @param navigateTo (event) request navigation to [Screen]
 * @param modifier modifier for the root element
 */
@Composable
private fun PostList(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    ScrollableColumn(modifier = modifier) {
        PostListTopSection(postTop, navigateTo)
        PostListSimpleSection(postsSimple, navigateTo, favorites, onToggleFavorite)
        PostListPopularSection(postsPopular, navigateTo)
        PostListHistorySection(postsHistory, navigateTo)
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        CircularProgressIndicator()
    }
}

/**
 * Display an error snackbar when network requests fail.
 *
 * @param showError (state) if true, this snackbar will display
 * @param modifier modifier for root element
 * @param onErrorAction (event) user has requested error action by clicking on it
 * @param onDismiss (event) request that this snackbar be dismissed.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ErrorSnackbar(
    showError: Boolean,
    modifier: Modifier = Modifier,
    onErrorAction: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
    launchInComposition(showError) {
        delay(timeMillis = 5000L)
        if (showError) { onDismiss() }
    }

    AnimatedVisibility(
        visible = showError,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            text = { Text("Can't update latest news") },
            action = {
                TextButton(
                    onClick = {
                        onErrorAction()
                        onDismiss()
                    },
                    contentColor = contentColor()
                ) {
                    Text(
                        text = "RETRY",
                        color = MaterialTheme.colors.snackbarAction
                    )
                }
            }
        )
    }
}

/**
 * Top section of [PostList]
 *
 * @param post (state) highlighted post to display
 * @param navigateTo (event) request navigation to [Screen]
 */
@Composable
private fun PostListTopSection(post: Post, navigateTo: (Screen) -> Unit) {
    ProvideEmphasis(EmphasisAmbient.current.high) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = "Top stories for you",
            style = MaterialTheme.typography.subtitle1
        )
    }
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
    )
    PostListDivider()
}

/**
 * Full-width list items for [PostList]
 *
 * @param posts (state) to display
 * @param navigateTo (event) request navigation to [Screen]
 */
@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateTo = navigateTo,
                isFavorite = favorites.contains(post.id),
                onToggleFavorite = { onToggleFavorite(post.id) }
            )
            PostListDivider()
        }
    }
}

/**
 * Horizontal scrolling cards for [PostList]
 *
 * @param posts (state) to display
 * @param navigateTo (event) request navigation to [Screen]
 */
@Composable
private fun PostListPopularSection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit
) {
    Column {
        ProvideEmphasis(EmphasisAmbient.current.high) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Popular on Jetnews",
                style = MaterialTheme.typography.subtitle1
            )
        }
        ScrollableRow(modifier = Modifier.padding(end = 16.dp)) {
            posts.forEach { post ->
                PostCardPopular(post, navigateTo, Modifier.padding(start = 16.dp, bottom = 16.dp))
            }
        }
        PostListDivider()
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param posts (state) to display
 * @param navigateTo (event) request navigation to [Screen]
 */
@Composable
private fun PostListHistorySection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post, navigateTo)
            PostListDivider()
        }
    }
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Preview("Home screen body")
@Composable
fun PreviewHomeScreenBody() {
    ThemedPreview {
        val posts = loadFakePosts()
        PostList(posts, { }, setOf(), {})
    }
}

@Preview("Home screen, open drawer")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = scaffoldState,
            navigateTo = { }
        )
    }
}

@Preview("Home screen dark theme")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        val posts = loadFakePosts()
        PostList(posts, {}, setOf(), {})
    }
}

@Composable
private fun loadFakePosts(): List<Post> {
    val context = ContextAmbient.current
    val posts = runBlocking {
        BlockingFakePostsRepository(context).getPosts()
    }
    return (posts as Result.Success).data
}

@Preview("Home screen, open drawer dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = scaffoldState,
            navigateTo = { }
        )
    }
}
