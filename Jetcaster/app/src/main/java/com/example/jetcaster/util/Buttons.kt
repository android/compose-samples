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

package com.example.jetcaster.util

import androidx.compose.animation.animate
import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.padding
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToggleFollowPodcastIconButton(
    isFollowed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            // TODO: think about animating these icons
            asset = when {
                isFollowed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            tint = animate(
                when {
                    isFollowed -> contentColor()
                    else -> EmphasisAmbient.current.high.applyEmphasis(Color.Black)
                }
            ),
            modifier = Modifier
                .drawShadow(
                    elevation = animate(if (isFollowed) 0.dp else 1.dp),
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animate(
                        when {
                            isFollowed -> MaterialTheme.colors.surface.copy(0.38f)
                            else -> Color.White
                        }
                    ),
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp)
        )
    }
}
