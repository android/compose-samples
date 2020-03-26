/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row
import androidx.ui.material.AlertDialog
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TextButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.post3
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.VectorImageButton
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.home.BookmarkButton
import com.example.jetnews.ui.home.isFavorite
import com.example.jetnews.ui.home.toggleBookmark
import com.example.jetnews.ui.navigateTo

@Composable
fun ArticleScreen(postId: String) {

    var showDialog by state { false }
    // getting the post from our list of posts by Id
    val post = posts.find { it.id == postId } ?: return

    if (showDialog) {
        FunctionalityNotAvailablePopup {
            showDialog = false
        }
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = MaterialTheme.typography().subtitle2
                    )
                },
                navigationIcon = {
                    VectorImageButton(R.drawable.ic_back) {
                        navigateTo(Screen.Home)
                    }
                }
            )
        },
        bodyContent = { modifier ->
            PostContent(modifier = modifier, post = post)
        },
        bottomAppBar = {
            BottomBar(post) { showDialog = true }
        }
    )
}

@Composable
private fun BottomBar(post: Post, onUnimplementedAction: () -> Unit) {
    val context = ContextAmbient.current
    Surface(elevation = 2.dp) {
        Container(modifier = LayoutHeight(56.dp) + LayoutSize.Fill) {
            Row {
                BottomBarAction(R.drawable.ic_favorite) {
                    onUnimplementedAction()
                }
                BookmarkButton(
                    isBookmarked = isFavorite(postId = post.id),
                    onBookmark = { toggleBookmark(postId = post.id) }
                )
                BottomBarAction(R.drawable.ic_share) {
                    sharePost(post, context)
                }
                Container(modifier = LayoutFlexible(1f)) { } // TODO: Any element works
                BottomBarAction(R.drawable.ic_text_settings) {
                    onUnimplementedAction()
                }
            }
        }
    }
}

@Composable
private fun BottomBarAction(
    @DrawableRes id: Int,
    onClick: () -> Unit
) {
    Ripple(
        bounded = false,
        radius = 24.dp
    ) {
        Clickable(onClick = onClick) {
            Container(modifier = LayoutPadding(12.dp) + LayoutSize(24.dp, 24.dp)) {
                DrawVector(vectorResource(id))
            }
        }
    }
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onCloseRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography().body2
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
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
        ArticleScreen(post3.id)
    }
}

@Preview("Article screen dark theme")
@Composable
fun PreviewArticleDark() {
    ThemedPreview(darkThemeColors) {
        ArticleScreen(post3.id)
    }
}
