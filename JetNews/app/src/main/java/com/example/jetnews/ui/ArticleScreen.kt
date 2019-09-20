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

import androidx.compose.Composable
import androidx.compose.composer
import androidx.ui.core.Text
import androidx.ui.layout.FlexColumn
import androidx.ui.material.AppBarIcon
import androidx.ui.material.TopAppBar

@Composable
fun ArticleScreen(icons: Icons, postId: String) {
    // getting the post from our list of posts by Id
    val post = posts.find { it.id == postId } ?: return

    val navigationIcon: @Composable() () -> Unit = {
        AppBarIcon(icons.back) {
            navigateTo(Screen.Home)
        }
    }
    FlexColumn {
        inflexible {
            TopAppBar<Any>(
                title = {
                    Text("Published in: ${post.publication?.name}")
                },
                navigationIcon = navigationIcon
            )
        }
        flexible(1f) {
            PostContent(post)
        }
    }
}
