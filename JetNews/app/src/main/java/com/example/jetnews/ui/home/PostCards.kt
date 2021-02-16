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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Post
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview

@Composable
fun AuthorAndReadTime(
    post: Post,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            val textStyle = MaterialTheme.typography.body2
            Text(
                text = post.metadata.author.name,
                style = textStyle
            )
            Text(
                text = " - ${post.metadata.readTimeMinutes} min read",
                style = textStyle
            )
        }
    }
}

@Composable
fun PostImage(post: Post, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(post.imageThumbId),
        contentDescription = null, // decorative
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun PostTitle(post: Post) {
    Text(post.title, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun PostCardSimple(
    post: Post,
    navigateTo: (Screen) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val bookmarkAction = stringResource(if (isFavorite) R.string.unbookmark else R.string.bookmark)
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateTo(Screen.Article(post.id)) })
            .padding(16.dp)
            .semantics {
                // By defining a custom action, we tell accessibility services that this whole
                // composable has an action attached to it. The accessibility service can choose
                // how to best communicate this action to the user.
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = bookmarkAction,
                        action = { onToggleFavorite(); true }
                    )
                )
            }
    ) {
        PostImage(post, Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            PostTitle(post)
            AuthorAndReadTime(post)
        }
        BookmarkButton(
            isBookmarked = isFavorite,
            onClick = onToggleFavorite,
            // Remove button semantics so action can be handled at row level
            modifier = Modifier.clearAndSetSemantics {}
        )
    }
}

@Composable
fun PostCardHistory(post: Post, navigateTo: (Screen) -> Unit) {
    Row(
        Modifier
            .clickable(onClick = { navigateTo(Screen.Article(post.id)) })
            .padding(16.dp)
    ) {
        PostImage(
            post = post,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(Modifier.weight(1f)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "BASED ON YOUR HISTORY",
                    style = MaterialTheme.typography.overline
                )
            }
            PostTitle(post = post)
            AuthorAndReadTime(
                post = post,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.cd_more_actions)
            )
        }
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(
        if (isBookmarked) R.string.unbookmark else R.string.bookmark
    )
    IconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier.semantics {
            // Use a custom click label that accessibility services can communicate to the user.
            // We only want to override the label, not the actual action, so for the action we pass null.
            this.onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = null // handled by click label of parent
        )
    }
}

@Preview("Bookmark Button")
@Composable
fun BookmarkButtonPreview() {
    ThemedPreview {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
fun BookmarkButtonBookmarkedPreview() {
    ThemedPreview {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

@Preview("Simple post card")
@Composable
fun SimplePostPreview() {
    ThemedPreview {
        PostCardSimple(post3, {}, false, {})
    }
}

@Preview("Post History card")
@Composable
fun HistoryPostPreview() {
    ThemedPreview {
        PostCardHistory(post3, {})
    }
}

@Preview("Simple post card dark theme")
@Composable
fun SimplePostDarkPreview() {
    ThemedPreview(darkTheme = true) {
        PostCardSimple(post3, {}, false, {})
    }
}
