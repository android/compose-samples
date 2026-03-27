/*
 * Copyright 2021 The Android Open Source Project
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

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.deeplink.util.DeepLinkPattern
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.components.JetnewsSnackbarHost
import com.example.jetnews.ui.modifiers.interceptKey
import com.example.jetnews.ui.navigation.ListDetailScene
import com.example.jetnews.ui.theme.JetnewsTheme
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

val HomeDeepLinkPattern = DeepLinkPattern(
    HomeKey.serializer(),
    uriPattern = "https://developer.android.com/jetnews".toUri(),
)

fun EntryProviderScope<NavKey>.homeEntry(
    postsRepository: PostsRepository,
    isExpandedScreen: () -> Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
) {
    entry<HomeKey>(
        metadata = ListDetailScene.list(
            ListDetailScene.ListConfiguration(
                modifier = Modifier.width(334.dp),
                detailPlaceholder = {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.home_detail_placeholder),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                },
            ),
        ),
    ) {
        val homeViewModel: HomeViewModel =
            viewModel(factory = HomeViewModel.provideFactory(postsRepository))

        HomeScreen(
            homeViewModel = homeViewModel,
            isExpandedScreen = isExpandedScreen(),
            openDrawer = openDrawer,
            navigateToPost = navigateToPost,
        )
    }
}

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param openDrawer (event) request opening the app drawer
 * @param navigateToPost (event) request navigation to Post screen
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeFeedScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavourite(it) },
        onSelectPost = { navigateToPost(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openDrawer = openDrawer,
        homeListLazyListState = rememberLazyListState(),
        snackbarHostState = snackbarHostState,
        onSearchInputChanged = { homeViewModel.onSearchInputChanged(it) },
        searchInput = uiState.searchInput,
    )
}

/**
 * The home screen displaying just the post feed.
 */
@Composable
fun HomeFeedScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { hasPostsUiState, contentPadding, contentModifier ->
        PostList(
            postsFeed = hasPostsUiState.postsFeed,
            favorites = hasPostsUiState.favorites,
            showExpandedSearch = !showTopAppBar,
            onPostTapped = onSelectPost,
            onToggleFavorite = onToggleFavorite,
            contentPadding = contentPadding,
            modifier = contentModifier,
            state = homeListLazyListState,
            searchInput = searchInput,
            onSearchInputChanged = onSearchInputChanged,
        )
    }
}

/**
 * A display of the home screen that has the list.
 *
 * This sets up the scaffold with the top app bar, and surrounds the [hasPostsContent] with refresh,
 * loading and error handling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasPosts,
        contentPadding: PaddingValues,
        modifier: Modifier,
    ) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = { JetnewsSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showTopAppBar) {
                HomeTopAppBar(
                    openDrawer = openDrawer,
                    topAppBarState = topAppBarState,
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        val contentModifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

        LoadingContent(
            modifier = Modifier.padding(innerPadding),
            empty = when (uiState) {
                is HomeUiState.HasPosts -> false
                is HomeUiState.NoPosts -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshPosts,
            content = {
                when (uiState) {
                    is HomeUiState.HasPosts ->
                        hasPostsContent(uiState, innerPadding, contentModifier)

                    is HomeUiState.NoPosts -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // if there are no posts, and no error, let the user refresh manually
                            TextButton(
                                onClick = onRefreshPosts,
                                modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(
                                contentModifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                            ) { /* empty screen */ }
                        }
                    }
                }
            },
        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or snackbarHostState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, snackbarHostState) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText,
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (empty) {
        emptyContent()
    } else {
        val refreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = loading,
            onRefresh = onRefresh,
            content = { content() },
            state = refreshState,
            indicator = {
                Indicator(
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(),
                    isRefreshing = loading,
                    state = refreshState,
                )
            },
        )
    }
}

/**
 * Display a feed of posts.
 *
 * When a post is clicked on, [onPostTapped] will be called.
 *
 * @param postsFeed (state) the feed to display
 * @param favorites (state) a set of favorite posts
 * @param showExpandedSearch (state) whether the expanded search is shown
 * @param onPostTapped (event) request navigation to Post screen
 * @param onToggleFavorite (event) request that this post toggle its favorite state
 * @param modifier modifier for the root element
 * @param contentPadding the padding to apply to the content
 * @param state the state object to be used to control or observe the list's state
 * @param searchInput (state) the search input
 * @param onSearchInputChanged (event) request that the search input changed
 */
@Composable
private fun PostList(
    postsFeed: PostsFeed,
    favorites: Set<String>,
    showExpandedSearch: Boolean,
    onPostTapped: (postId: String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
    ) {
        if (showExpandedSearch) {
            item {
                HomeSearch(
                    Modifier.padding(horizontal = 16.dp),
                    searchInput = searchInput,
                    onSearchInputChanged = onSearchInputChanged,
                )
            }
        }
        item { PostListTopSection(postsFeed.highlightedPost, onPostTapped) }
        if (postsFeed.recommendedPosts.isNotEmpty()) {
            item {
                PostListSimpleSection(
                    postsFeed.recommendedPosts,
                    onPostTapped,
                    favorites,
                    onToggleFavorite,
                )
            }
        }
        if (postsFeed.popularPosts.isNotEmpty() && !showExpandedSearch) {
            item {
                PostListPopularSection(
                    postsFeed.popularPosts, onPostTapped,
                )
            }
        }
        if (postsFeed.recentPosts.isNotEmpty()) {
            item { PostListHistorySection(postsFeed.recentPosts, onPostTapped) }
        }
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Top section of [PostList]
 *
 * @param post (state) highlighted post to display
 * @param navigateToPost (event) request navigation to Post screen
 */
@Composable
private fun PostListTopSection(post: Post, navigateToPost: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium,
    )
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateToPost(post.id) }),
    )
    PostListDivider()
}

/**
 * Full-width list items for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToPost (event) request navigation to Post screen
 * @param favorites (state) a set of favorite posts
 * @param onToggleFavorite (event) request that this post toggle its favorite state
 */
@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateToPost: (String) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateToPost = navigateToPost,
                isFavorite = favorites.contains(post.id),
                onToggleFavorite = { onToggleFavorite(post.id) },
            )
            PostListDivider()
        }
    }
}

/**
 * Horizontal scrolling cards for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToPost (event) request navigation to Post screen
 */
@Composable
private fun PostListPopularSection(posts: List<Post>, navigateToPost: (String) -> Unit) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (post in posts) {
                PostCardPopular(
                    post,
                    navigateToPost,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PostListDivider()
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToPost (event) request navigation to Post screen
 */
@Composable
private fun PostListHistorySection(posts: List<Post>, navigateToPost: (String) -> Unit) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post, navigateToPost)
            PostListDivider()
        }
    }
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
private fun PostListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    )
}

/**
 * Expanded search UI - includes support for enter-to-send on the search field
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun HomeSearch(modifier: Modifier = Modifier, searchInput: String = "", onSearchInputChanged: (String) -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = searchInput,
        onValueChange = onSearchInputChanged,
        placeholder = { Text(stringResource(R.string.home_search)) },
        leadingIcon = { Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null) },
        modifier = modifier
            .fillMaxWidth()
            .interceptKey(Key.Enter) {
                // submit a search query when Enter is pressed
                submitSearch(onSearchInputChanged, context)
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            },
        singleLine = true,
        // keyboardOptions change the newline key to a search key on the soft keyboard
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        // keyboardActions submits the search query when the search key is pressed
        keyboardActions = KeyboardActions(
            onSearch = {
                submitSearch(onSearchInputChanged, context)
                keyboardController?.hide()
            },
        ),
    )
}

/**
 * Stub helper function to submit a user's search query
 */
private fun submitSearch(onSearchInputChanged: (String) -> Unit, context: Context) {
    onSearchInputChanged("")
    Toast.makeText(
        context,
        "Search is not yet implemented",
        Toast.LENGTH_SHORT,
    ).show()
}

/**
 * TopAppBar for the Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
) {
    val context = LocalContext.current
    val title = stringResource(id = R.string.app_name)
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.ic_jetnews_wordmark),
                contentDescription = title,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(R.drawable.ic_jetnews_logo),
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                )
            }
        },
        actions = {
            IconButton(onClick = {
                Toast.makeText(
                    context,
                    "Search is not yet implemented in this configuration",
                    Toast.LENGTH_LONG,
                ).show()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.cd_search),
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Preview("Home list drawer screen")
@Preview("Home list drawer screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Home list drawer screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewHomeListDrawerScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

@Preview("Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview(
    "Home list navrail screen (dark)",
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013,
)
@Preview("Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}
