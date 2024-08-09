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
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.SnackSharedElementKey
import com.example.jetsnack.ui.SnackSharedElementType
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.snackDetailBoundsTransform
import com.example.jetsnack.ui.theme.JetsnackTheme

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
                style = MaterialTheme.typography.titleLarge,
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
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No sharedTransitionScope found")
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")

        with(sharedTransitionScope) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(onClick = {
                        onSnackClick(snack.id, snackCollectionId.toString())
                    })
                    .padding(8.dp)
            ) {
                SnackImage(
                    imageRes = snack.imageRes,
                    elevation = 1.dp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Image
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = snackDetailBoundsTransform
                        )
                )
                Text(
                    text = snack.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = JetsnackTheme.colors.textSecondary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .wrapContentWidth()
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Title
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(nonSpatialExpressiveSpring()),
                            exit = fadeOut(nonSpatialExpressiveSpring()),
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                            boundsTransform = snackDetailBoundsTransform
                        )
                )
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
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    with(sharedTransitionScope) {
        val roundedCornerAnimation by animatedVisibilityScope.transition
            .animateDp(label = "rounded corner") { enterExit: EnterExitState ->
                when (enterExit) {
                    EnterExitState.PreEnter -> 0.dp
                    EnterExitState.Visible -> 20.dp
                    EnterExitState.PostExit -> 20.dp
                }
            }
        JetsnackCard(
            elevation = 0.dp,
            shape = RoundedCornerShape(roundedCornerAnimation),
            modifier = modifier
                .padding(bottom = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = snackCollectionId.toString(),
                            type = SnackSharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = snackDetailBoundsTransform,
                    clipInOverlayDuringTransition = OverlayClip(
                        RoundedCornerShape(
                            roundedCornerAnimation
                        )
                    ),
                    enter = fadeIn(),
                    exit = fadeOut()
                )
                .size(
                    width = HighlightCardWidth,
                    height = 250.dp
                )
                .border(
                    1.dp,
                    JetsnackTheme.colors.uiBorder.copy(alpha = 0.12f),
                    RoundedCornerShape(roundedCornerAnimation)
                )

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
                                boundsTransform = snackDetailBoundsTransform,
                                enter = fadeIn(nonSpatialExpressiveSpring()),
                                exit = fadeOut(nonSpatialExpressiveSpring()),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                            .height(100.dp)
                            .fillMaxWidth()
                            .offsetGradientBackground(
                                colors = gradient,
                                width = {
                                    // The Cards show a gradient which spans 6 cards and
                                    // scrolls with parallax.
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
                        imageRes = snack.imageRes,
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
                                exit = fadeOut(nonSpatialExpressiveSpring()),
                                enter = fadeIn(nonSpatialExpressiveSpring()),
                                boundsTransform = snackDetailBoundsTransform
                            )
                            .align(Alignment.BottomCenter)
                            .size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = snack.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
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
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(nonSpatialExpressiveSpring()),
                            exit = fadeOut(nonSpatialExpressiveSpring()),
                            boundsTransform = snackDetailBoundsTransform,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                        .wrapContentWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = snack.tagline,
                    style = MaterialTheme.typography.bodyLarge,
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
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(nonSpatialExpressiveSpring()),
                            exit = fadeOut(nonSpatialExpressiveSpring()),
                            boundsTransform = snackDetailBoundsTransform,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                        .wrapContentWidth()
                )
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
    @DrawableRes
    imageRes: Int,
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
                .data(imageRes)
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
    val snack = snacks.first()
    JetsnackPreviewWrapper {
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

@Composable
fun JetsnackPreviewWrapper(content: @Composable () -> Unit) {
    JetsnackTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this
                ) {
                    content()
                }
            }
        }
    }
}
