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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideCardContainer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
internal fun EpisodeCard(
    playerEpisode: PlayerEpisode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardSize: DpSize = JetcasterAppDefaults.thumbnailSize.episode,
) {
    WideCardContainer(
        imageCard = {
            EpisodeThumbnail(playerEpisode, onClick = onClick, modifier = Modifier.size(cardSize))
        },
        title = {
            EpisodeMetaData(
                playerEpisode = playerEpisode,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .width(JetcasterAppDefaults.cardWidth.small * 2)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun EpisodeThumbnail(
    playerEpisode: PlayerEpisode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        scale = CardScale.None,
        shape = CardDefaults.shape(RoundedCornerShape(12.dp)),
        modifier = modifier,
    ) {
        Thumbnail(episode = playerEpisode, size = JetcasterAppDefaults.thumbnailSize.episode)
    }
}

@Composable
private fun EpisodeMetaData(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier
) {
    val duration = playerEpisode.duration
    Column(modifier = modifier) {
        Text(
            text = playerEpisode.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = playerEpisode.podcastName, style = MaterialTheme.typography.bodySmall)
        if (duration != null) {
            Spacer(
                modifier = Modifier.height(JetcasterAppDefaults.gap.podcastRow * 0.8f)
            )
            EpisodeDataAndDuration(offsetDateTime = playerEpisode.published, duration = duration)
        }
    }
}
