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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.model.PlayerEpisode
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
fun Thumbnail(
    podcast: Podcast,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    size: DpSize = DpSize(
        JetcasterAppDefaults.cardWidth.medium,
        JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
) =
    Thumbnail(
        podcast.imageUrl,
        modifier,
        shape,
        size,
        contentScale
    )

@Composable
fun Thumbnail(
    episode: PlayerEpisode,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    size: DpSize = DpSize(
        JetcasterAppDefaults.cardWidth.medium,
        JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
) =
    Thumbnail(
        episode.podcastImageUrl,
        modifier,
        shape,
        size,
        contentScale
    )

@Composable
fun Thumbnail(
    url: String?,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    size: DpSize = DpSize(
        JetcasterAppDefaults.cardWidth.medium,
        JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
) =
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = Modifier
            .size(size)
            .clip(shape)
            .then(modifier)
    )
