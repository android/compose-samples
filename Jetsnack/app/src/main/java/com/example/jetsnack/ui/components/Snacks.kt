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

@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationStyleApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMediaQueryApi::class,
)

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
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
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.typography
import com.example.jetsnack.ui.utils.JetsnackThemeWrapper
import com.example.jetsnack.ui.utils.SnackPolygons
import com.example.jetsnack.ui.utils.UiMediaScopeWrapper
import com.example.jetsnack.ui.utils.asShape
import com.example.jetsnack.ui.utils.formatPrice
import com.example.jetsnack.ui.utils.sharedBoundsRevealWithShapeMorph

private val normalTextStyle = Style {
    textStyleWithFontFamilyFix(typography.titleSmall)
    textAlign(TextAlign.Center)
}

@Composable
fun SnackCollection(snackCollection: SnackCollection, onSnackClick: (Long, String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp),
        ) {
            Text(
                text = snackCollection.name,
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                    contentColor(colors.textPrimary)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start),
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    tint = JetsnackTheme.colors.textPrimary,
                    contentDescription = null,
                )
            }
        }
        LazyRow(
            state = rememberLazyListState(),
            modifier = modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
        ) {
            itemsIndexed(snacks, key = { _, item -> item.id }) { _, snack ->
                when (snackCollection.type) {
                    CollectionType.Normal ->
                        SnackItem(
                            snackCollectionId = snackCollection.id,
                            snack = snack,
                            onSnackClick = onSnackClick,
                            showTagLine = false,
                            imageShape = SnackPolygons.snackItemPolygonRounded,
                            style = JetsnackTheme.styles.normalCardStyle,
                            snackTextStyle = normalTextStyle,
                            imageAspectRatio = 4f / 3f,
                        )

                    CollectionType.Highlight ->
                        SnackItem(
                            snackCollectionId = snackCollection.id,
                            snack = snack,
                            onSnackClick = onSnackClick,
                            showAddButton = true,
                            style = JetsnackTheme.styles.highlightGlowCardStyle,
                        )

                    CollectionType.Card ->
                        SnackItem(
                            snackCollectionId = snackCollection.id,
                            snack = snack,
                            onSnackClick = onSnackClick,
                            style = JetsnackTheme.styles.plainCardStyle,
                            imageAspectRatio = 16 / 9f,
                        )
                }
            }
        }
    }
}

@Composable
private fun SnackItem(
    snackCollectionId: Long,
    snack: Snack,
    modifier: Modifier = Modifier,
    style: Style = Style,
    snackTextStyle: Style = Style,
    imageShape: RoundedPolygon = SnackPolygons.snackItemPolygon,
    imageAspectRatio: Float = 1f,
    showTagLine: Boolean = true,
    showAddButton: Boolean = false,
    onSnackClick: (Long, String) -> Unit,
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
        val interactionSource = remember { MutableInteractionSource() }
        JetsnackCard(
            style = Style {
                shape(RoundedCornerShape(roundedCornerAnimation))
            } then style,
            interactionSource = interactionSource,
            modifier = modifier
                .wrapContentHeight()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = snackCollectionId.toString(),
                            type = SnackSharedElementType.Bounds,
                        ),
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = snackDetailBoundsTransform,
                    clipInOverlayDuringTransition = OverlayClip(
                        RoundedCornerShape(
                            roundedCornerAnimation,
                        ),
                    ),
                    enter = fadeIn(),
                    exit = fadeOut(),
                )
                .clickable(
                    onClick = {
                        onSnackClick(
                            snack.id,
                            snackCollectionId.toString(),
                        )
                    },
                    interactionSource = interactionSource,
                    indication = null,
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                val sharedContentState = rememberSharedContentState(
                    key = SnackSharedElementKey(
                        snackId = snack.id,
                        origin = snackCollectionId.toString(),
                        type = SnackSharedElementType.Image,
                    ),
                )
                val targetRoundedPolygon = SnackPolygons.snackDetailPolygon
                val morph = remember { Morph(imageShape, targetRoundedPolygon) }
                val progress = animatedVisibilityScope.transition.animateFloat(
                    transitionSpec = {
                        tween(300, easing = LinearEasing)
                    },
                ) {
                    when (it) {
                        EnterExitState.PreEnter -> 1f
                        EnterExitState.Visible -> 0f
                        EnterExitState.PostExit -> 1f
                    }
                }.value

                SnackImage(
                    imageRes = snack.imageRes,
                    contentDescription = null,
                    style = Style {
                        val shape = if (sharedContentState.isMatchFound) {
                            morph.asShape(progress)
                        } else {
                            imageShape.asShape()
                        }
                        shape(shape)
                    },
                    modifier = Modifier
                        .sharedBoundsRevealWithShapeMorph(
                            sharedContentState = sharedContentState,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = snackDetailBoundsTransform,
                            targetShape = targetRoundedPolygon,
                            restingShape = imageShape,
                        )
                        .fillMaxWidth()
                        .aspectRatio(imageAspectRatio),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = snack.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = Style {
                                textStyleWithFontFamilyFix(typography.titleLarge)
                                contentColor(colors.textSecondary)
                                textAlign(TextAlign.Start)
                            } then snackTextStyle,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .sharedBounds(
                                    rememberSharedContentState(
                                        key = SnackSharedElementKey(
                                            snackId = snack.id,
                                            origin = snackCollectionId.toString(),
                                            type = SnackSharedElementType.Title,
                                        ),
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(nonSpatialExpressiveSpring()),
                                    exit = fadeOut(nonSpatialExpressiveSpring()),
                                    boundsTransform = snackDetailBoundsTransform,
                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                )
                                .fillMaxWidth(),
                        )
                        if (showTagLine) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatPrice(snack.price),
                                style = {
                                    textStyleWithFontFamilyFix(typography.bodyLarge)
                                    contentColor(colors.textHelp)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .sharedBounds(
                                        rememberSharedContentState(
                                            key = SnackSharedElementKey(
                                                snackId = snack.id,
                                                origin = snackCollectionId.toString(),
                                                type = SnackSharedElementType.Price,
                                            ),
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        enter = fadeIn(nonSpatialExpressiveSpring()),
                                        exit = fadeOut(nonSpatialExpressiveSpring()),
                                        boundsTransform = snackDetailBoundsTransform,
                                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                    )
                                    .wrapContentWidth(),
                            )
                        }
                    }
                    if (showAddButton) {
                        Button(
                            onClick = {
                            },
                            style = {
                                shape(CircleShape)
                                size(36.dp)
                                background(colors.brand)
                                dropShadow(Shadow(0.dp))
                                contentPadding(8.dp)
                                externalPadding(8.dp)
                            },
                            modifier = Modifier,
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_add),
                                modifier = Modifier.size(24.dp),
                                tint = JetsnackTheme.colors.textSecondary,
                                contentDescription = stringResource(R.string.add_to_cart_content_description),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) = if (LocalInspectionMode.current) {
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
    style: Style = Style,
) {
    Surface(
        style = Style {
            shape(CircleShape)
        } then style,
        modifier = modifier,
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
fun SnackCardPreviewCard() {
    val snack = snacks.first()
    JetsnackThemeWrapper {
        UiMediaScopeWrapper {
            SnackItem(
                snackCollectionId = 1,
                snack = snack,
                style = JetsnackTheme.styles.normalCardStyle,
                showTagLine = false,
                onSnackClick = { _, _ -> },
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun SnackCardPreviewHighlight() {
    val snack = snacks.first()
    JetsnackThemeWrapper {
        UiMediaScopeWrapper {
            SnackItem(
                snackCollectionId = 1,
                snack = snack,
                style = JetsnackTheme.styles.highlightGlowCardStyle,
                showAddButton = true,
                onSnackClick = { _, _ -> },
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun SnackCardPreviewPlain() {
    val snack = snacks.first()
    JetsnackThemeWrapper {
        UiMediaScopeWrapper {
            SnackItem(
                snackCollectionId = 1,
                snack = snack,
                showTagLine = false,
                style = JetsnackTheme.styles.normalCardStyle,
                onSnackClick = { _, _ -> },
            )
        }
    }
}

@Composable
fun JetsnackPreviewWrapper(content: @Composable () -> Unit) {
    JetsnackTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this,
                ) {
                    content()
                }
            }
        }
    }
}
