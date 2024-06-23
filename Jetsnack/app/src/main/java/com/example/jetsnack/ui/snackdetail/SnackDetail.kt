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
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
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
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Neutral8
import com.example.jetsnack.ui.utils.ShapeBasedClip
import com.example.jetsnack.ui.utils.formatPrice
import com.example.jetsnack.ui.utils.mirroringBackIcon
import com.example.jetsnack.ui.utils.safeAnimateEnterExit
import com.example.jetsnack.ui.utils.safeRenderInSharedTransitionScopeOverlay
import com.example.jetsnack.ui.utils.safeSharedBounds
import com.example.jetsnack.ui.utils.safeSkipToLookaheadSize
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
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

val snackDetailBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

@Composable
fun SnackDetail(
    snackId: Long,
    origin: String,
    upPress: () -> Unit
) {
    val snack = remember(snackId) { SnackRepo.getSnack(snackId) }
    val related = remember(snackId) { SnackRepo.getRelated(snackId) }
   /* val sharedTransitionScope = LocalSharedTransitionScope.current
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
        }*/
        Box(
            Modifier
                .clip(RoundedCornerShape(0.dp))
                .safeSharedBounds(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = origin,
                            type = SnackSharedElementType.Bounds
                        ),
                    clipInOverlayDuringTransition =
                    ShapeBasedClip(RoundedCornerShape(0.dp)),
                    boundsTransform = snackDetailBoundsTransform,
                    exit = fadeOut(nonSpatialExpressiveSpring()),
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                )
                .fillMaxSize()
                .background(color = JetsnackTheme.colors.uiBackground)
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

@Composable
private fun Header(snackId: Long, origin: String) {
    Spacer(
        modifier = Modifier
            .safeSharedBounds(
                key = SnackSharedElementKey(
                    snackId = snackId,
                    origin = origin,
                    type = SnackSharedElementType.Background
                ),
                boundsTransform = snackDetailBoundsTransform,
                enter = fadeIn(nonSpatialExpressiveSpring()),
                exit = fadeOut(nonSpatialExpressiveSpring()),
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
            .height(280.dp)
            .fillMaxWidth()
            .background(Brush.verticalGradient(JetsnackTheme.colors.tornado1))
    )
}

@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .safeRenderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .size(36.dp)
            .safeAnimateEnterExit(
                enter = scaleIn(tween(300, delayMillis = 300)),
                exit = scaleOut(tween(20))
            )
            .background(
                color = Neutral8.copy(alpha = 0.32f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = JetsnackTheme.colors.iconInteractive,
            contentDescription = stringResource(R.string.label_back),
        )
    }
}

@Composable
private fun Body(
    related: List<SnackCollection>,
    scroll: ScrollState
) {
   /* val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No scope found")
    with(sharedTransitionScope) {*/
        Column(modifier = Modifier.safeSkipToLookaheadSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(MinTitleOffset)
            )

            Column(
                modifier = Modifier.verticalScroll(scroll)
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
                            style = MaterialTheme.typography.overline,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )
                        Spacer(Modifier.height(16.dp))
                        var seeMore by remember { mutableStateOf(true) }

                        Text(
                            text = stringResource(R.string.detail_placeholder),
                            style = MaterialTheme.typography.body1,
                            color = JetsnackTheme.colors.textHelp,
                            maxLines = if (seeMore) 5 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis,
                            modifier = HzPadding.safeSkipToLookaheadSize()

                        )
                        val textButton = if (seeMore) {
                            stringResource(id = R.string.see_more)
                        } else {
                            stringResource(id = R.string.see_less)
                        }

                        Text(
                            text = textButton,
                            style = MaterialTheme.typography.button,
                            textAlign = TextAlign.Center,
                            color = JetsnackTheme.colors.textLink,
                            modifier = Modifier
                                .heightIn(20.dp)
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .clickable {
                                    seeMore = !seeMore
                                }
                                .safeSkipToLookaheadSize()
                        )

                        Spacer(Modifier.height(40.dp))
                        Text(
                            text = stringResource(R.string.ingredients),
                            style = MaterialTheme.typography.overline,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.ingredients_list),
                            style = MaterialTheme.typography.body1,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )

                        Spacer(Modifier.height(16.dp))
                        JetsnackDivider()

                        related.forEach { snackCollection ->
                            key(snackCollection.id) {
                                SnackCollection(
                                    snackCollection = snackCollection,
                                    onSnackClick = { _, _ -> },
                                    highlight = false
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .padding(bottom = BottomBarHeight)
                                .navigationBarsPadding()
                                .height(8.dp)
                        )
                    }
                }
            }
        }
}

@Composable
private fun Title(snack: Snack, origin: String, scrollProvider: () -> Int) {
    val maxOffset = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset = with(LocalDensity.current) { MinTitleOffset.toPx() }
/*
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")
*/
/*
    with(sharedTransitionScope) {*/
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
                .background(JetsnackTheme.colors.uiBackground)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = snack.name,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.h4,
                color = JetsnackTheme.colors.textSecondary,
                modifier = HzPadding
                    .safeSharedBounds(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = origin,
                            type = SnackSharedElementType.Title
                        ),
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .wrapContentWidth()
            )
            Text(
                text = snack.tagline,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 20.sp,
                color = JetsnackTheme.colors.textHelp,
                modifier = HzPadding
                    .safeSharedBounds(
                            key = SnackSharedElementKey(
                                snackId = snack.id,
                                origin = origin,
                                type = SnackSharedElementType.Tagline
                            ),
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .wrapContentWidth()
            )
            Spacer(Modifier.height(4.dp))
                Text(
                    text = formatPrice(snack.price),
                    style = MaterialTheme.typography.h6,
                    color = JetsnackTheme.colors.textPrimary,
                    modifier = HzPadding
                        .safeAnimateEnterExit(
                            enter = fadeIn() + slideInVertically { -it / 3 },
                            exit = fadeOut() + slideOutVertically { -it / 3 }
                        )
                        .safeSkipToLookaheadSize()
                )
            Spacer(Modifier.height(8.dp))
            JetsnackDivider(modifier = Modifier)
        }
  /*  }*/
}

@Composable
private fun Image(
    snackId: Long,
    origin: String,
    @DrawableRes
    imageRes: Int,
    scrollProvider: () -> Int
) {
    val collapseRange = with(LocalDensity.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFractionProvider = {
        (scrollProvider() / collapseRange).coerceIn(0f, 1f)
    }

    CollapsingImageLayout(
        collapseFractionProvider = collapseFractionProvider,
        modifier = HzPadding.statusBarsPadding()
    ) {
     /*   val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No sharedTransitionScope found")
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")

        with(sharedTransitionScope) {*/
            SnackImage(
                imageRes = imageRes,
                contentDescription = null,
                modifier = Modifier
                    .safeSharedBounds(
                        key = SnackSharedElementKey(
                            snackId = snackId,
                            origin = origin,
                            type = SnackSharedElementType.Image
                        ),
                        exit = fadeOut(),
                        enter = fadeIn(),
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .fillMaxSize()

            )/*
        }*/
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFractionProvider: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
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
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(imageX, imageY)
        }
    }
}

@Composable
private fun CartBottomBar(modifier: Modifier = Modifier) {
    val (count, updateCount) = remember { mutableIntStateOf(1) }
   /* val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No Shared scope")
    val animatedVisibilityScope =
        LocalNavAnimatedVisibilityScope.current ?: throw IllegalStateException("No Shared scope")
    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {*/
            JetsnackSurface(
                modifier = modifier
                    .safeRenderInSharedTransitionScopeOverlay(zIndexInOverlay = 4f)
                    .safeAnimateEnterExit(
                        enter = slideInVertically(
                            tween(
                                300,
                                delayMillis = 300
                            )
                        ) { it } + fadeIn(tween(300, delayMillis = 300)),
                        exit = slideOutVertically(tween(50)) { it } +
                            fadeOut(tween(50))
                    )
            ) {
                Column {
                    JetsnackDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .then(HzPadding)
                            .heightIn(min = BottomBarHeight)
                    ) {
                        QuantitySelector(
                            count = count,
                            decreaseItemCount = { if (count > 0) updateCount(count - 1) },
                            increaseItemCount = { updateCount(count + 1) }
                        )
                        Spacer(Modifier.width(16.dp))
                        JetsnackButton(
                            onClick = { /* todo */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_cart),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
       /* }
    }*/
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SnackDetailPreview() {
    JetsnackTheme {
        SnackDetail(
            snackId = 1L,
            origin = "details",
            upPress = { }
        )
    }
}
