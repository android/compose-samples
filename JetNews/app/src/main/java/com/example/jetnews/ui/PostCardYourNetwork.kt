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

import androidx.compose.composer

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexSize
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.themeTextStyle
import androidx.ui.graphics.Color
import androidx.ui.material.BaseButton
import androidx.ui.material.surface.Card
import androidx.ui.layout.Padding


@Composable
fun PostCardYourNetwork(post: Post, icons: Icons) {
    Card (shape = RoundedCornerShape(4.dp)) {
        Container(width = 280.dp, height = 240.dp) {
            Column(
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start,
                mainAxisSize = FlexSize.Expand,
                crossAxisSize = FlexSize.Expand
            ) {
                val image = icons.placeholder_4_3
                Container(width = 280.dp, height = 100.dp) {
                    ClippedImage(image)
                }
                Padding(16.dp) {
                    Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                        BaseButton(onClick = {
                            navigateTo(Screen.Article(post.id))

                        }, color = Color.Transparent) {
                            Text(post.title, +themeTextStyle { h6 }, maxLines = 2)
                        }
                        Text(post.metadata.author.name, +themeTextStyle { body2 })
                        Text("${post.metadata.date} - ${post.metadata.readTimeMinutes} min read")
                    }
                }
            }
        }
    }
}

