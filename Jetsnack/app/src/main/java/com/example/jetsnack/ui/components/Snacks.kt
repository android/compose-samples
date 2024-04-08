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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetsnack.R
import com.example.jetsnack.model.CollectionType
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.LocalSharedElementScopes
import com.example.jetsnack.ui.SharedElementScopes
import com.example.jetsnack.ui.SnackSharedElementKey
import com.example.jetsnack.ui.SnackSharedElementType
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.mirroringIcon

private val HighlightCardWidth = 170.dp
private val HighlightCardPadding = 16.dp
private val Density.cardWidthWithPaddingPx
    get() = (HighlightCardWidth + HighlightCardPadding).toPx()

@Composable
fun SnackCollection(
    snackCollection: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    highlight: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = snackCollection.name,
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = mirroringIcon(
                        ltrIcon = Icons.AutoMirrored.Outlined.ArrowForward,
                        rtlIcon = Icons.AutoMirrored.Outlined.ArrowBack
                    ),
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = null
                )
            }
        }
        if (highlight && snackCollection.type == CollectionType.Highlight) {
            HighlightedSnacks(snackCollection.id, index, snackCollection.snacks, onSnackClick)
        } else {
            Snacks(snackCollection.id, snackCollection.snacks, onSnackClick)
        }
    }
}

@Composable
private fun HighlightedSnacks(
    snackCollectionId: Long,
    index: Int,
    snacks: List<Snack>,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowState = rememberLazyListState()
    val cardWidthWithPaddingPx = with(LocalDensity.current) { cardWidthWithPaddingPx }

    val scrollProvider = {
        // Simple calculation of scroll distance for homogenous item types with the same width.
        val offsetFromStart = cardWidthWithPaddingPx * rowState.firstVisibleItemIndex
        offsetFromStart + rowState.firstVisibleItemScrollOffset
    }

    val gradient = when ((index / 2) % 2) {
        0 -> JetsnackTheme.colors.gradient6_1
        else -> JetsnackTheme.colors.gradient6_2
    }

    LazyRow(
        state = rowState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(snacks) { index, snack ->
            HighlightSnackItem(
                snackCollectionId = snackCollectionId,
                snack = snack,
                onSnackClick = onSnackClick,
                index = index,
                gradient = gradient,
                scrollProvider = scrollProvider
            )
        }
    }
}

@Composable
private fun Snacks(
    snackCollectionId: Long,
    snacks: List<Snack>,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
    ) {
        items(snacks) { snack ->
            SnackItem(snack, snackCollectionId, onSnackClick)
        }
    }
}

@Composable
fun SnackItem(
    snack: Snack,
    snackCollectionId: Long,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(
            start = 4.dp,
            end = 4.dp,
            bottom = 8.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = {
                    onSnackClick(snack.id, snackCollectionId.toString())
                })
                .padding(8.dp)
        ) {
            val sharedTransitionScope = LocalSharedElementScopes.current.sharedTransitionScope
                ?: throw IllegalStateException("No sharedTransitionScope found")
            val animatedVisibilityScope = LocalSharedElementScopes.current.animatedVisibilityScope
                ?: throw IllegalStateException("No animatedVisibilityScope found")

            with(sharedTransitionScope) {
                with(animatedVisibilityScope) {
                    SnackImage(
                        imageUrl = snack.imageUrl,
                        elevation = 4.dp,
                        contentDescription = null,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = SnackSharedElementKey(
                                        snackId = snack.id,
                                        origin = snackCollectionId.toString(),
                                        type = SnackSharedElementType.Image
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                exit = ExitTransition.None
                            )
                            .size(120.dp)
                    )
                    Text(
                        text = snack.name,
                        style = MaterialTheme.typography.subtitle1,
                        color = JetsnackTheme.colors.textSecondary,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = SnackSharedElementKey(
                                        snackId = snack.id,
                                        origin = snackCollectionId.toString(),
                                        type = SnackSharedElementType.Title
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .animateEnterExit()
                            .fillMaxWidth()
                    )
                }
            }


        }
    }
}

@Composable
private fun HighlightSnackItem(
    snackCollectionId: Long,
    snack: Snack,
    onSnackClick: (Long, String) -> Unit,
    index: Int,
    gradient: List<Color>,
    scrollProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    val sharedTransitionScope = LocalSharedElementScopes.current.sharedTransitionScope
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope = LocalSharedElementScopes.current.animatedVisibilityScope
        ?: throw IllegalStateException("No Scope found")
    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {
            val sharedContentState = rememberSharedContentState(
                key = SnackSharedElementKey(
                    snackId = snack.id,
                    origin = snackCollectionId.toString(),
                    type = SnackSharedElementType.Bounds
                )
            )
            JetsnackCard(
                modifier = modifier
                    .sharedBounds(
                        sharedContentState,
                        animatedVisibilityScope
                    )
                    .size(
                        width = HighlightCardWidth,
                        height = 250.dp
                    )
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clickable(onClick = {
                            onSnackClick(
                                snack.id,
                                snackCollectionId.toString()
                            )
                        })
                        .fillMaxSize()

                ) {
                    Box(
                        modifier = Modifier
                            .height(160.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = SnackSharedElementKey(
                                            snackId = snack.id,
                                            origin = snackCollectionId.toString(),
                                            type = SnackSharedElementType.Background
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium.copy(
                                        bottomEnd = CornerSize(0.dp),
                                        bottomStart = CornerSize(0.dp)
                                    ))
                                )
                                .animateEnterExit()
                                .height(100.dp)
                                .fillMaxWidth()
                                .offsetGradientBackground(
                                    colors = gradient,
                                    width = {
                                        // The Cards show a gradient which spans 6 cards and scrolls with parallax.
                                        6 * cardWidthWithPaddingPx
                                    },
                                    offset = {
                                        val left = index * cardWidthWithPaddingPx
                                        val gradientOffset = left - (scrollProvider() / 3f)
                                        gradientOffset
                                    }
                                )
                        )

                        SnackImage(
                            imageUrl = snack.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = SnackSharedElementKey(
                                            snackId = snack.id,
                                            origin = snackCollectionId.toString(),
                                            type = SnackSharedElementType.Image
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    exit = ExitTransition.None
                                )
                                .size(120.dp)
                                .align(Alignment.BottomCenter)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    with(animatedVisibilityScope) {
                        Text(
                            text = snack.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.h6,
                            color = JetsnackTheme.colors.textSecondary,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = SnackSharedElementKey(
                                            snackId = snack.id,
                                            origin = snackCollectionId.toString(),
                                            type = SnackSharedElementType.Title
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .animateEnterExit()
                                .skipToLookaheadSize()
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = snack.tagline,
                            style = MaterialTheme.typography.body1,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = SnackSharedElementKey(
                                            snackId = snack.id,
                                            origin = snackCollectionId.toString(),
                                            type = SnackSharedElementType.Tagline
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxWidth()
                        )
                    }

                }
            }
        }
    }
}
@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }

@Composable
fun SnackImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    JetsnackSurface(
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = debugPlaceholder(debugPreview = R.drawable.placeholder),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun SnackCardPreview() {
    JetsnackTheme {
        val snack = snacks.first()
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedElementScopes provides SharedElementScopes(
                        this@SharedTransitionLayout,
                        this
                    )
                ) {
                    HighlightSnackItem(
                        snackCollectionId = 1,
                        snack = snack,
                        onSnackClick = { _, _ -> },
                        index = 0,
                        gradient = JetsnackTheme.colors.gradient6_1,
                        scrollProvider = { 0f }
                    )
                }

            }
        }
    }
}
