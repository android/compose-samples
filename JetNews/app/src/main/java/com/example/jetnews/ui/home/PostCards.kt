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

package com.example.jetnews.ui.home

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.toModifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row
import androidx.ui.material.EmphasisLevels
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.post3
import com.example.jetnews.model.Post
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.VectorImage
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.navigateTo

@Composable
fun AuthorAndReadTime(post: Post) {
    Row {

        val textStyle = MaterialTheme.typography().body2
        ProvideEmphasis(emphasis = EmphasisLevels().medium) {
            Text(text = post.metadata.author.name, style = textStyle)
            Text(
                text = " - ${post.metadata.readTimeMinutes} min read",
                style = textStyle
            )
        }
    }
}

@Composable
fun PostImage(modifier: Modifier = Modifier.None, post: Post) {
    val image = post.imageThumb ?: imageResource(R.drawable.placeholder_1_1)
    val imageModifier = ImagePainter(image).toModifier()

    Box(modifier = modifier + LayoutSize(40.dp, 40.dp) + imageModifier)
}

@Composable
fun PostTitle(post: Post) {
    ProvideEmphasis(emphasis = EmphasisLevels().high) {
        Text(post.title, style = MaterialTheme.typography().subtitle1)
    }
}

@Composable
fun PostCardSimple(post: Post) {
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            Row(modifier = LayoutPadding(16.dp)) {
                PostImage(
                    modifier = LayoutPadding(start = 0.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
                    post = post
                )
                Column(modifier = LayoutFlexible(1f)) {
                    PostTitle(post)
                    AuthorAndReadTime(post)
                }
                BookmarkButton(
                    isBookmarked = isFavorite(postId = post.id),
                    onBookmark = { toggleBookmark(postId = post.id) }
                )
            }
        }
    }
}

@Composable
fun PostCardHistory(post: Post) {
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            Row(modifier = LayoutPadding(all = 16.dp)) {
                PostImage(
                    modifier = LayoutPadding(start = 0.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
                    post = post
                )
                Column(modifier = LayoutFlexible(1f)) {
                    ProvideEmphasis(emphasis = EmphasisLevels().medium) {
                        Text(
                            text = "BASED ON YOUR HISTORY",
                            style = MaterialTheme.typography().overline
                        )
                    }
                    PostTitle(post = post)
                    AuthorAndReadTime(post)
                }
                VectorImage(
                    modifier = LayoutPadding(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 8.dp),
                    id = R.drawable.ic_more
                )
            }
        }
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onBookmark: (Boolean) -> Unit
) {
    Ripple(
        bounded = false,
        radius = 24.dp
    ) {
        Toggleable(isBookmarked, onBookmark) {
            Container(modifier = LayoutSize(48.dp, 48.dp)) {
                if (isBookmarked) {
                    VectorImage(id = R.drawable.ic_bookmarked)
                } else {
                    VectorImage(id = R.drawable.ic_bookmark)
                }
            }
        }
    }
}

@Preview("Simple post card")
@Composable
fun PreviewSimplePost() {
    ThemedPreview {
        PostCardSimple(post = post3)
    }
}

@Preview("Simple post card dark theme")
@Composable
fun PreviewSimplePostDark() {
    ThemedPreview(darkThemeColors) {
        PostCardSimple(post = post3)
    }
}

fun toggleBookmark(postId: String) {
    with(JetnewsStatus) {
        if (favorites.contains(postId)) {
            favorites.remove(postId)
        } else {
            favorites.add(postId)
        }
    }
}

fun isFavorite(postId: String) = JetnewsStatus.favorites.contains(postId)
