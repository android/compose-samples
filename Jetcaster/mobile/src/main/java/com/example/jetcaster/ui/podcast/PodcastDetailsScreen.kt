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

package com.example.jetcaster.ui.podcast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.shared.Loading
import com.example.jetcaster.ui.tooling.DevicePreviews
import com.example.jetcaster.util.fullWidthItem
import kotlinx.coroutines.launch

@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is PodcastUiState.Loading -> {
            PodcastDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        is PodcastUiState.Ready -> {
            PodcastDetailsScreen(
                podcast = s.podcast,
                episodes = s.episodes,
                toggleSubscribe = viewModel::toggleSusbcribe,
                onQueueEpisode = viewModel::onQueueEpisode,
                removeFromQueue = viewModel::deleteEpisode,
                navigateToPlayer = navigateToPlayer,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PodcastDetailsLoadingScreen(modifier: Modifier = Modifier) {
    Loading(modifier = modifier)
}

@Composable
fun PodcastDetailsScreen(
    podcast: PodcastInfo,
    episodes: List<EpisodeInfo>,
    toggleSubscribe: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    removeFromQueue: (EpisodeInfo) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.episode_added_to_your_queue)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showBackButton) {
                PodcastDetailsTopAppBar(
                    navigateBack = navigateBack,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        PodcastDetailsContent(
            podcast = podcast,
            episodes = episodes,
            toggleSubscribe = toggleSubscribe,
            removeFromQueue = removeFromQueue,
            onQueueEpisode = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(snackBarText)
                }
                onQueueEpisode(it)
            },
            navigateToPlayer = navigateToPlayer,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
fun PodcastDetailsContent(
    podcast: PodcastInfo,
    episodes: List<EpisodeInfo>,
    removeFromQueue: (EpisodeInfo) -> Unit,
    toggleSubscribe: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        modifier.fillMaxSize(),
    ) {
        fullWidthItem {
            PodcastDetailsHeaderItem(
                podcast = podcast,
                toggleSubscribe = toggleSubscribe,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(episodes, key = { it.uri }) { episode ->
            EpisodeListItem(
                episode = episode,
                podcast = podcast,
                onClick = navigateToPlayer,
                removeFromQueue = removeFromQueue,
                onQueueEpisode = onQueueEpisode,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                showPodcastImage = false,
                showSummary = true,
            )
        }
    }
}

@Composable
fun PodcastDetailsHeaderItem(podcast: PodcastInfo, toggleSubscribe: (PodcastInfo) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(Keyline1),
    ) {
        Column {
            PodcastImage(
                modifier = Modifier
                    .size(280.dp)
                    .clip(MaterialTheme.shapes.large)
                    .align(Alignment.CenterHorizontally),
                podcastImageUrl = podcast.imageUrl,
                contentDescription = podcast.title,
            )
            Text(
                text = podcast.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            PodcastDetailsDescription(
                podcast = podcast,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
            PodcastDetailsHeaderItemButtons(
                isSubscribed = podcast.isSubscribed ?: false,
                onClick = {
                    toggleSubscribe(podcast)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PodcastDetailsDescription(podcast: PodcastInfo, modifier: Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSeeMore by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clickable { isExpanded = !isExpanded },
    ) {
        Text(
            text = podcast.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                showSeeMore = result.hasVisualOverflow
            },
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseOutExpo,
                ),
            ),
        )
        if (showSeeMore) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Text(
                    text = stringResource(id = R.string.see_more),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PodcastDetailsHeaderItemButtons(isSubscribed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isNotificationOn by remember { mutableStateOf(false) }
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    ButtonGroup(
        overflowIndicator = {},
        modifier = modifier,
    ) {
        customItem(
            buttonGroupContent = {
                ToggleButton(
                    checked = isSubscribed,
                    onCheckedChange = { onClick() },
                    colors = ToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        checkedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        checkedContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    shapes = ToggleButtonShapes(
                        shape = RoundedCornerShape(15.dp),
                        pressedShape = RoundedCornerShape(if (isSubscribed) 15.dp else 60.dp),
                        checkedShape = RoundedCornerShape(60.dp),
                    ),
                    modifier = Modifier
                        .width(76.dp)
                        .height(56.dp)
                        .animateWidth(interactionSource = interactionSource1)
                        .semantics(mergeDescendants = true) { },
                    interactionSource = interactionSource1,
                ) {
                    Icon(
                        painterResource(id = if (isSubscribed) R.drawable.ic_check else R.drawable.ic_add),
                        contentDescription = null,
                    )
                }
            },
            menuContent = { },
        )

        customItem(
            buttonGroupContent = {
                ToggleButton(
                    checked = isNotificationOn,
                    onCheckedChange = { isNotificationOn = !isNotificationOn },
                    colors = ToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        checkedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        checkedContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    shapes = ToggleButtonShapes(
                        shape = RoundedCornerShape(100.dp),
                        pressedShape = RoundedCornerShape(if (isNotificationOn) 100.dp else 20.dp),
                        checkedShape = RoundedCornerShape(20.dp),
                    ),
                    interactionSource = interactionSource2,
                    modifier = Modifier
                        .size(56.dp)
                        .animateWidth(interactionSource = interactionSource2),
                ) {
                    Icon(
                        painterResource(id = if (isNotificationOn) R.drawable.ic_notifications_active else R.drawable.ic_notifications),
                        contentDescription = stringResource(R.string.cd_more),
                    )
                }
            },
            menuContent = {},
        )
        customItem(
            buttonGroupContent = { Spacer(modifier.weight(1f)) },
            menuContent = { },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDetailsTopAppBar(navigateBack: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(id = R.string.cd_back),
                )
            }
        },
        modifier = modifier,
    )
}

@Preview
@Composable
fun PodcastDetailsHeaderItemPreview() {
    PodcastDetailsHeaderItem(
        podcast = PreviewPodcasts[0],
        toggleSubscribe = { },
    )
}

@DevicePreviews
@Composable
fun PodcastDetailsScreenPreview() {
    PodcastDetailsScreen(
        podcast = PreviewPodcasts[0],
        episodes = PreviewEpisodes,
        toggleSubscribe = { },
        onQueueEpisode = { },
        navigateToPlayer = { },
        navigateBack = { },
        showBackButton = true,
    )
}
