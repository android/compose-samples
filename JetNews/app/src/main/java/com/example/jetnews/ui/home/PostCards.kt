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
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconToggleButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.ripple
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.post3
import com.example.jetnews.model.Post
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.navigateTo

@Composable
fun AuthorAndReadTime(post: Post) {
    Row {
        val textStyle = MaterialTheme.typography.body2
        ProvideEmphasis(EmphasisAmbient.current.medium) {
            Text(text = post.metadata.author.name, style = textStyle)
            Text(text = " - ${post.metadata.readTimeMinutes} min read", style = textStyle)
        }
    }
}

@Composable
fun PostImage(post: Post, modifier: Modifier = Modifier.None) {
    val image = post.imageThumb ?: imageResource(R.drawable.placeholder_1_1)
    Image(image, modifier.preferredSize(40.dp, 40.dp))
}

@Composable
fun PostTitle(post: Post) {
    ProvideEmphasis(EmphasisAmbient.current.high) {
        Text(post.title, style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun PostCardSimple(post: Post) {
    Clickable(
        modifier = Modifier.ripple(),
        onClick = { navigateTo(Screen.Article(post.id)) }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            PostImage(post, Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
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

@Composable
fun PostCardHistory(post: Post) {
    Clickable(
        modifier = Modifier.ripple(),
        onClick = { navigateTo(Screen.Article(post.id)) }
    ) {
        Row(Modifier.padding(16.dp)) {
            PostImage(
                post,
                Modifier.padding(end = 16.dp)
            )
            Column(Modifier.weight(1f)) {
                ProvideEmphasis(EmphasisAmbient.current.medium) {
                    Text(
                        text = "BASED ON YOUR HISTORY",
                        style = MaterialTheme.typography.overline
                    )
                }
                PostTitle(post = post)
                AuthorAndReadTime(post)
            }
            Modifier.padding(top = 8.dp, bottom = 8.dp)
            Image(vectorResource(R.drawable.ic_more))
        }
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onBookmark: (Boolean) -> Unit
) {
    IconToggleButton(checked = isBookmarked, onCheckedChange = onBookmark) {
        if (isBookmarked) {
            Icon(vectorResource(R.drawable.ic_bookmarked), Modifier.fillMaxSize())
        } else {
            Icon(vectorResource(R.drawable.ic_bookmark), Modifier.fillMaxSize())
        }
    }
}

@Preview("Bookmark Button")
@Composable
fun PreviewBookmarkButton() {
    val (bookmarked, updateBookmarked) = state { false }
    BookmarkButton(isBookmarked = bookmarked, onBookmark = updateBookmarked)
}

@Preview("Simple post card")
@Composable
fun PreviewSimplePost() {
    ThemedPreview {
        PostCardSimple(post = post3)
    }
}

@Preview("History post card")
@Composable
fun PreviewHistoryPost() {
    ThemedPreview {
        PostCardHistory(post = post3)
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
