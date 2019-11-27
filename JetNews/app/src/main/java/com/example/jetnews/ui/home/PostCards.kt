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
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Row
import androidx.ui.layout.Size
import androidx.ui.layout.Spacing
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.withOpacity
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.post3
import com.example.jetnews.model.Post
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.VectorImage
import com.example.jetnews.ui.navigateTo

@Composable
fun AuthorAndReadTime(post: Post) {
    Row {
        val textStyle = ((+MaterialTheme.typography()).body2).withOpacity(0.6f)
        Text(text = post.metadata.author.name, style = textStyle)
        Text(
            text = " - ${post.metadata.readTimeMinutes} min read",
            style = textStyle
        )
    }
}

@Composable
fun PostImage(modifier: Modifier = Modifier.None, post: Post) {
    val image = post.imageThumb ?: +imageResource(R.drawable.placeholder_1_1)

    Container(modifier = modifier wraps Size(40.dp, 40.dp)) {
        DrawImage(image)
    }
}

@Composable
fun PostTitle(post: Post) {
    Text(post.title, style = ((+MaterialTheme.typography()).subtitle1).withOpacity(0.87f))
}

@Composable
fun PostCardSimple(post: Post) {
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            Row(modifier = Spacing(16.dp)) {
                PostImage(modifier = Spacing(right = 16.dp), post = post)
                Column(modifier = Flexible(1f)) {
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
            Row(modifier = Spacing(all = 16.dp)) {
                PostImage(modifier = Spacing(right = 16.dp), post = post)
                Column(modifier = Flexible(1f)) {
                    Text(
                        text = "BASED ON YOUR HISTORY",
                        style = ((+MaterialTheme.typography()).overline).withOpacity(0.38f)
                    )
                    PostTitle(post = post)
                    AuthorAndReadTime(post)
                }
                VectorImage(
                    modifier = Spacing(top = 8.dp, bottom = 8.dp),
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
            Container(modifier = Size(48.dp, 48.dp)) {
                if (isBookmarked) {
                    VectorImage(id = R.drawable.ic_bookmarked)
                } else {
                    VectorImage(id = R.drawable.ic_bookmark)
                }
            }
        }
    }
}

@Preview
@Composable
fun runPreview() {
    PostCardSimple(post = post3)
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
