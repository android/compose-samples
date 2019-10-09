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

package com.example.jetnews.ui

import android.content.Context
import android.content.Intent
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.graphics.Image
import androidx.ui.layout.FlexColumn
import androidx.ui.material.AlertDialog
import androidx.ui.material.AppBarIcon
import androidx.ui.material.BottomAppBar
import androidx.ui.material.Button
import androidx.ui.material.TopAppBar
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import com.example.jetnews.ui.ArticleActions.Bookmark
import com.example.jetnews.ui.ArticleActions.Like
import com.example.jetnews.ui.ArticleActions.Share

@Composable
fun ArticleScreen(icons: Icons, postId: String) {

    val showDialog = +state { false }
    // getting the post from our list of posts by Id
    val post = posts.find { it.id == postId } ?: return

    if (showDialog.value) {
        FunctionalityNotAvailablePopup {
            showDialog.value = false
        }
    }

    FlexColumn {
        inflexible {
            TopAppBar(
                title = { Text("Published in: ${post.publication?.name}") },
                navigationIcon = {
                    AppBarIcon(icons.back) {
                        navigateTo(Screen.Home)
                    }
                }
            )
        }
        expanded(1f) {
            PostContent(post)
        }
        inflexible {
            BottomBar(post, icons) { showDialog.value = true }
        }
    }
}

@Composable
private fun BottomBar(post: Post, icons: Icons, onUnimplementedAction: () -> Unit) {
    val bookmarkIcon = if (isFavorite(post.id)) {
        icons.bookmarkOn
    } else {
        icons.bookmarkOff
    }
    val context = +ambient(ContextAmbient)
    val actions = listOf(
        Like(icons.heartOff) { onUnimplementedAction() },
        Share(icons.share) { SharePost(post, context) },
        Bookmark(bookmarkIcon) { toggleBookmark(post.id) })
    BottomAppBar(
        color = +themeColor { surface },
        actionData = actions
    ) { data -> AppBarIcon(data.image) { data.action() } }
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onCloseRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = +themeTextStyle { body1 }
            )
        },
        confirmButton = { Button("Close", onClick = onDismiss) }
    )
}

sealed class ArticleActions(val image: Image, val action: () -> Unit) {
    class Like(image: Image, action: () -> Unit) : ArticleActions(image, action)
    class Share(image: Image, action: () -> Unit) : ArticleActions(image, action)
    class Bookmark(image: Image, action: () -> Unit) : ArticleActions(image, action)
}

private fun SharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, "Share post"))
}
