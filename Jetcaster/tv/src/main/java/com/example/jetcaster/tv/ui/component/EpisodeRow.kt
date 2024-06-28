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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EpisodeRow(
    playerEpisodeList: EpisodeList,
    onSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
    contentPadding: PaddingValues = JetcasterAppDefaults.padding.episodeRowContentPadding,
    focusRequester: FocusRequester = remember { FocusRequester() },
    lazyListState: TvLazyListState = remember(playerEpisodeList) { TvLazyListState() }
) {
    val firstItem = remember { FocusRequester() }
    var previousEpisodeListHash by remember { mutableIntStateOf(playerEpisodeList.hashCode()) }
    val isSameList = previousEpisodeListHash == playerEpisodeList.hashCode()

    TvLazyRow(
        state = lazyListState,
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusProperties {
                enter = {
                    when {
                        lazyListState.layoutInfo.visibleItemsInfo.isEmpty() -> FocusRequester.Cancel
                        isSameList && focusRequester.restoreFocusedChild() -> FocusRequester.Cancel
                        else -> firstItem
                    }
                }
                exit = {
                    previousEpisodeListHash = playerEpisodeList.hashCode()
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
            }
            .then(modifier),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
    ) {
        itemsIndexed(playerEpisodeList) { index, item ->
            val cardModifier = if (index == 0) {
                Modifier.focusRequester(firstItem)
            } else {
                Modifier
            }
            EpisodeCard(
                playerEpisode = item,
                onClick = { onSelected(item) },
                modifier = cardModifier
            )
        }
    }
}
