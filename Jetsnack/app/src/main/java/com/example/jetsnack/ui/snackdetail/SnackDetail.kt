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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)

package com.example.jetsnack.ui.snackdetail

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.SnackSharedElementKey
import com.example.jetsnack.ui.SnackSharedElementType
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Neutral8
import com.example.jetsnack.ui.utils.formatPrice
import kotlin.math.max
import kotlin.math.min

private val BottomBarHeight = 56.dp
private val TitleHeight = 128.dp
private val GradientScroll = 180.dp
private val ImageOverlap = 115.dp
private val MinTitleOffset = 56.dp
private val MinImageOffset = 12.dp
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll
private val ExpandedImageSize = 300.dp
private val CollapsedImageSize = 150.dp
private val HzPadding = Modifier.padding(horizontal = 24.dp)

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f,
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f,
)

val snackDetailBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

@Composable
fun SnackDetail(snackId: Long, origin: String, upPress: () -> Unit) {
    val snack = remember(snackId) { SnackRepo.getSnack(snackId) }
    val related = remember(snackId) { SnackRepo.getRelated(snackId) }
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    val roundedCornerAnim by animatedVisibilityScope.transition
        .animateDp(label = "rounded corner") { enterExit: EnterExitState ->
            when (enterExit) {
                EnterExitState.PreEnter -> 20.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 20.dp
            }
        }
    with(sharedTransitionScope) {
        Box(
            Modifier
                .clip(RoundedCornerShape(roundedCornerAnim))
                .sharedBounds(
                    rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = origin,
                            type = SnackSharedElementType.Bounds,
                        ),
                    ),
                    animatedVisibilityScope,
                    clipInOverlayDuringTransition =
                    OverlayClip(RoundedCornerShape(roundedCornerAnim)),
                    boundsTransform = snackDetailBoundsTransform,
                    exit = fadeOut(nonSpatialExpressiveSpring()),
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                )
                .fillMaxSize()
                .background(color = JetsnackTheme.colors.uiBackground),
        ) {
            val scroll = rememberScrollState(0)
            Header(snack.id, origin = origin)
            Body(related, scroll)
            Title(snack, origin) { scroll.value }
            Image(snackId, origin, snack.imageRes) { scroll.value }
            Up(upPress)
            CartBottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun Header(snackId: Long, origin: String) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")

    with(sharedTransitionScope) {
        val brushColors = JetsnackTheme.colors.tornado1

        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val targetOffset = with(LocalDensity.current) {
            1000.dp.toPx()
        }
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = targetOffset,
            animationSpec = infiniteRepeatable(
                tween(50000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "offset",
        )
        Spacer(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snackId,
                            origin = origin,
                            type = SnackSharedElementType.Background,
                        ),
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = snackDetailBoundsTransform,
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                    exit = fadeOut(nonSpatialExpressiveSpring()),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                )
                .height(280.dp)
                .fillMaxWidth()
                .blur(40.dp)
                .drawWithCache {
                    val brushSize = 400f
                    val brush = Brush.linearGradient(
                        colors = brushColors,
                        start = Offset(offset, offset),
                        end = Offset(offset + brushSize, offset + brushSize),
                        tileMode = TileMode.Mirror,
                    )
                    onDrawBehind {
                        drawRect(brush)
                    }
                },
        )
    }
}

@Composable
private fun SharedTransitionScope.Up(upPress: () -> Unit) {
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")
    with(animatedVisibilityScope) {
        IconButton(
            onClick = upPress,
            modifier = Modifier
                .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .size(36.dp)
                .animateEnterExit(
                    enter = scaleIn(tween(300, delayMillis = 300)),
                    exit = scaleOut(tween(20)),
                )
                .background(
                    color = Neutral8.copy(alpha = 0.32f),
                    shape = CircleShape,
                ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = JetsnackTheme.colors.iconInteractive,
                contentDescription = stringResource(R.string.label_back),
            )
        }
    }
}

@Composable
private fun Body(related: List<SnackCollection>, scroll: ScrollState) {
    val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No scope found")
    with(sharedTransitionScope) {
        Column(modifier = Modifier.skipToLookaheadSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(MinTitleOffset),
            )

            Column(
                modifier = Modifier.verticalScroll(scroll),
            ) {
                Spacer(Modifier.height(GradientScroll))
                Spacer(Modifier.height(ImageOverlap))
                JetsnackSurface(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Column {
                        Spacer(Modifier.height(TitleHeight))
                        Text(
                            text = stringResource(R.string.detail_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding,
                        )
                        Spacer(Modifier.height(16.dp))
                        var seeMore by remember { mutableStateOf(true) }
                        with(sharedTransitionScope) {
                            Text(
                                text = stringResource(R.string.detail_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = JetsnackTheme.colors.textHelp,
                                maxLines = if (seeMore) 5 else Int.MAX_VALUE,
                                overflow = TextOverflow.Ellipsis,
                                modifier = HzPadding.skipToLookaheadSize(),

                            )
                        }
                        val textButton = if (seeMore) {
                            stringResource(id = R.string.see_more)
                        } else {
                            stringResource(id = R.string.see_less)
                        }

                        Text(
                            text = textButton,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            color = JetsnackTheme.colors.textLink,
                            modifier = Modifier
                                .heightIn(20.dp)
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .clickable {
                                    seeMore = !seeMore
                                }
                                .skipToLookaheadSize(),
                        )

                        Spacer(Modifier.height(40.dp))
                        Text(
                            text = stringResource(R.string.ingredients),
                            style = MaterialTheme.typography.labelSmall,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.ingredients_list),
                            style = MaterialTheme.typography.bodyLarge,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding,
                        )

                        Spacer(Modifier.height(16.dp))
                        JetsnackDivider()

                        related.forEach { snackCollection ->
                            key(snackCollection.id) {
                                SnackCollection(
                                    snackCollection = snackCollection,
                                    onSnackClick = { _, _ -> },
                                    highlight = false,
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .padding(bottom = BottomBarHeight)
                                .navigationBarsPadding()
                                .height(8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(snack: Snack, origin: String, scrollProvider: () -> Int) {
    val maxOffset = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset = with(LocalDensity.current) { MinTitleOffset.toPx() }
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")

    with(sharedTransitionScope) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TitleHeight)
                .statusBarsPadding()
                .offset {
                    val scroll = scrollProvider()
                    val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
                    IntOffset(x = 0, y = offset.toInt())
                }
                .background(JetsnackTheme.colors.uiBackground),
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = snack.name,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.headlineMedium,
                color = JetsnackTheme.colors.textSecondary,
                modifier = HzPadding
                    .sharedBounds(
                        rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snack.id,
                                origin = origin,
                                type = SnackSharedElementType.Title,
                            ),
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = snackDetailBoundsTransform,
                    )
                    .wrapContentWidth(),
            )
            Text(
                text = snack.tagline,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 20.sp,
                color = JetsnackTheme.colors.textHelp,
                modifier = HzPadding
                    .sharedBounds(
                        rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snack.id,
                                origin = origin,
                                type = SnackSharedElementType.Tagline,
                            ),
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = snackDetailBoundsTransform,
                    )
                    .wrapContentWidth(),
            )
            Spacer(Modifier.height(4.dp))
            with(animatedVisibilityScope) {
                Text(
                    text = formatPrice(snack.price),
                    style = MaterialTheme.typography.titleLarge,
                    color = JetsnackTheme.colors.textPrimary,
                    modifier = HzPadding
                        .animateEnterExit(
                            enter = fadeIn() + slideInVertically { -it / 3 },
                            exit = fadeOut() + slideOutVertically { -it / 3 },
                        )
                        .skipToLookaheadSize(),
                )
            }
            Spacer(Modifier.height(8.dp))
            JetsnackDivider(modifier = Modifier)
        }
    }
}

@Composable
private fun Image(
    snackId: Long,
    origin: String,
    @DrawableRes
    imageRes: Int,
    scrollProvider: () -> Int,
) {
    val collapseRange = with(LocalDensity.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFractionProvider = {
        (scrollProvider() / collapseRange).coerceIn(0f, 1f)
    }

    CollapsingImageLayout(
        collapseFractionProvider = collapseFractionProvider,
        modifier = HzPadding.statusBarsPadding(),
    ) {
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No sharedTransitionScope found")
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")

        with(sharedTransitionScope) {
            SnackImage(
                imageRes = imageRes,
                contentDescription = null,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snackId,
                                origin = origin,
                                type = SnackSharedElementType.Image,
                            ),
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        exit = fadeOut(),
                        enter = fadeIn(),
                        boundsTransform = snackDetailBoundsTransform,
                    )
                    .fillMaxSize(),

            )
        }
    }
}

@Composable
private fun CollapsingImageLayout(collapseFractionProvider: () -> Float, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        check(measurables.size == 1)

        val collapseFraction = collapseFractionProvider()

        val imageMaxSize = min(ExpandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(MinTitleOffset, MinImageOffset, collapseFraction).roundToPx()
        val imageX = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction,
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth,
        ) {
            imagePlaceable.placeRelative(imageX, imageY)
        }
    }
}

@Composable
private fun CartBottomBar(modifier: Modifier = Modifier) {
    val (count, updateCount) = remember { mutableIntStateOf(1) }
    val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No Shared scope")
    val animatedVisibilityScope =
        LocalNavAnimatedVisibilityScope.current ?: throw IllegalStateException("No Shared scope")
    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {
            JetsnackSurface(
                modifier = modifier
                    .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 4f)
                    .animateEnterExit(
                        enter = slideInVertically(
                            tween(
                                300,
                                delayMillis = 300,
                            ),
                        ) { it } + fadeIn(tween(300, delayMillis = 300)),
                        exit = slideOutVertically(tween(50)) { it } +
                            fadeOut(tween(50)),
                    ),
            ) {
                Column {
                    JetsnackDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .then(HzPadding)
                            .heightIn(min = BottomBarHeight),
                    ) {
                        QuantitySelector(
                            count = count,
                            decreaseItemCount = { if (count > 0) updateCount(count - 1) },
                            increaseItemCount = { updateCount(count + 1) },
                        )
                        Spacer(Modifier.width(16.dp))
                        JetsnackButton(
                            onClick = { /* todo */ },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_cart),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SnackDetailPreview() {
    JetsnackPreviewWrapper {
        SnackDetail(
            snackId = 1L,
            origin = "details",
            upPress = { },
        )
    }
}
