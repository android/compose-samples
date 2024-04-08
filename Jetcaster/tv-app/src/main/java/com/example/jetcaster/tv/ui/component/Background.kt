/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.model.PlayerEpisode

@Composable
internal fun Background(
    podcast: Podcast,
    modifier: Modifier = Modifier,
    overlay: DrawScope.() -> Unit = {
        val brush = Brush.radialGradient(
            listOf(Color.Black, Color.Transparent),
            center = Offset(0f, size.height),
            radius = size.width * 1.5f
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    }
) = Background(imageUrl = podcast.imageUrl, modifier, overlay)

@Composable
internal fun Background(
    episode: PlayerEpisode,
    modifier: Modifier = Modifier,
    overlay: DrawScope.() -> Unit = {
        val brush = Brush.radialGradient(
            listOf(Color.Black, Color.Transparent),
            center = Offset(0f, size.height),
            radius = size.width * 1.5f
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    }
) = Background(imageUrl = episode.podcastImageUrl, modifier, overlay)

@Composable
internal fun Background(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    overlay: DrawScope.() -> Unit = {
        val brush = Brush.radialGradient(
            listOf(Color.Black, Color.Transparent),
            center = Offset(0f, size.height),
            radius = size.width * 1.5f
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    }
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}

@Composable
internal fun WithBackground(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    overlay: DrawScope.() -> Unit = {
        val brush = Brush.radialGradient(
            listOf(Color.Black, Color.Transparent),
            center = Offset(0f, size.height),
            radius = size.width * 1.5f
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    },
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Background(episode = playerEpisode, overlay = overlay, modifier = Modifier.fillMaxSize())
        content()
    }
}
