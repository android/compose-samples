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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
internal fun PodcastCard(
    podcastInfo: PodcastInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardCardContainer(
        imageCard = {
            Card(
                onClick = onClick,
                interactionSource = it,
                scale = CardScale.None,
                shape = CardDefaults.shape(RoundedCornerShape(12.dp))
            ) {
                Thumbnail(
                    podcastInfo = podcastInfo,
                    size = JetcasterAppDefaults.thumbnailSize.podcast
                )
            }
        },
        title = {
            Text(text = podcastInfo.title, modifier = Modifier.padding(top = 12.dp))
        },
        modifier = modifier,
    )
}
