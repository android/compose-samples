/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetnews.glance.ui

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.R
import com.example.jetnews.data.successOr
import com.example.jetnews.glance.ui.theme.JetnewsGlanceColorScheme
import com.example.jetnews.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JetnewsGlanceAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val application = context.applicationContext as JetnewsApplication
        val postsRepository = application.container.postsRepository

        // Load data needed to render the composable.
        // The widget is configured to refresh periodically using the "android:updatePeriodMillis"
        // configuration, and during each refresh, the data is loaded here.
        // The repository can internally return cached results here if it already has fresh data.
        val initialPostsFeed = withContext(Dispatchers.IO) {
            postsRepository.getPostsFeed().successOr(null)
        }
        val initialBookmarks: Set<String> = withContext(Dispatchers.IO) {
            postsRepository.observeFavorites().first()
        }

        provideContent {
            val scope = rememberCoroutineScope()
            val bookmarks by postsRepository.observeFavorites().collectAsState(initialBookmarks)
            val postsFeed by postsRepository.observePostsFeed().collectAsState(initialPostsFeed)
            val recommendedTopPosts =
                postsFeed?.let { listOf(it.highlightedPost) + it.recommendedPosts } ?: emptyList()

            // Provide a custom color scheme if the SDK version doesn't support dynamic colors.
            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    GlanceTheme.colors
                } else {
                    JetnewsGlanceColorScheme.colors
                }
            ) {
                JetnewsContent(
                    posts = recommendedTopPosts,
                    bookmarks = bookmarks,
                    onToggleBookmark = { scope.launch { postsRepository.toggleFavorite(it) } }
                )
            }
        }
    }

    @Composable
    private fun JetnewsContent(
        posts: List<Post>,
        bookmarks: Set<String>?,
        onToggleBookmark: (String) -> Unit
    ) {
        Column(
            modifier = GlanceModifier
                .background(GlanceTheme.colors.surface)
                .cornerRadius(24.dp)
        ) {
            Header(modifier = GlanceModifier.fillMaxWidth())
            // Set key for each size so that the onToggleBookmark lambda is called only once for the
            // active size.
            key(LocalSize.current) {
                Body(
                    modifier = GlanceModifier.fillMaxWidth(),
                    posts = posts,
                    bookmarks = bookmarks ?: setOf(),
                    onToggleBookmark = onToggleBookmark
                )
            }
        }
    }

    @Composable
    fun Header(modifier: GlanceModifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 10.dp, vertical = 20.dp)
        ) {
            val context = LocalContext.current
            Image(
                provider = ImageProvider(R.drawable.ic_jetnews_logo),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Image(
                contentDescription = context.getString(R.string.app_name),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                provider = ImageProvider(R.drawable.ic_jetnews_wordmark)
            )
        }
    }

    @Composable
    fun Body(
        modifier: GlanceModifier,
        posts: List<Post>,
        bookmarks: Set<String>,
        onToggleBookmark: (String) -> Unit,
    ) {
        val postLayout = LocalSize.current.toPostLayout()
        LazyColumn(modifier = modifier.background(GlanceTheme.colors.background)) {
            itemsIndexed(posts) { index, post ->
                Column(modifier = GlanceModifier.padding(horizontal = 14.dp)) {
                    Post(
                        post = post,
                        bookmarks = bookmarks,
                        onToggleBookmark = onToggleBookmark,
                        modifier = GlanceModifier.fillMaxWidth().padding(15.dp),
                        postLayout = postLayout,
                    )
                    if (index < posts.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}
