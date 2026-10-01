/*
 * Copyright 2026 The Android Open Source Project
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

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.compose.jetchat.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.MorphPolygonShape
import androidx.compose.foundation.shape.PolygonShape
import androidx.compose.foundation.shape.PolygonShapeGeometry.Companion.CornerRounding
import androidx.compose.foundation.shape.PolygonShapeGeometry.CornerRounding.Companion.Unrounded
import androidx.compose.foundation.shape.transformed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope

sealed interface SharedElementKey {
    data class ProfileAvatar(val key: String) : SharedElementKey
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> {
    null
}

val FullScreenRoundedRectangle: PolygonShape = PolygonShape.rectangle(
    topStartRounding = CornerRounding(percent = 15),
    topEndRounding = CornerRounding(percent = 15),
    bottomEndRounding = Unrounded,
    bottomStartRounding = Unrounded,
).transformed(contentScale = ContentScale.Fit)

val Cookie9Sided: PolygonShape = PolygonShape.star(
    numPoints = 9,
    innerRadiusRatio = 0.8f,
    outerRounding = CornerRounding(percent = 50),
).transformed( contentScale = ContentScale.Fit)

val MotionScheme.sharedElementTransitionBounds: BoundsTransform
    @Composable
    get() = BoundsTransform { _, _ -> sharedElementTransitionSpec() }

fun <T> MotionScheme.sharedElementTransitionSpec(): FiniteAnimationSpec<T> {
    return spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
}

@Composable
fun Modifier.sharedBoundsRevealWithShapeMorph(
    sharedElementKey: Any?,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShape: PolygonShape = FullScreenRoundedRectangle,
    targetShape: PolygonShape = Cookie9Sided,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    targetValueByState: @Composable (state: EnterExitState) -> Float = {
        when (it) {
            EnterExitState.PreEnter -> 1f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> 1f
        }
    },
    keepChildrenSizePlacement: Boolean = true,
): Modifier {
    if (sharedElementKey == null || sharedTransitionScope == null || animatedVisibilityScope == null) {
        return this
    }
    with(sharedTransitionScope) {
        return this@sharedBoundsRevealWithShapeMorph.sharedBoundsRevealWithShapeMorph(
            sharedContentState = rememberSharedContentState(sharedElementKey),
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = boundsTransform,
            resizeMode = resizeMode,
            restingShape = restingShape,
            targetShape = targetShape,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
            targetValueByState = targetValueByState,
            keepChildrenSizePlacement = keepChildrenSizePlacement,
        )
    }
}

@Composable
fun Modifier.sharedBoundsRevealWithShapeMorph(
    sharedContentState: SharedTransitionScope.SharedContentState,
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedTransitionScope provided"),
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShape: PolygonShape = FullScreenRoundedRectangle,
    targetShape: PolygonShape = Cookie9Sided,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    targetValueByState: @Composable (state: EnterExitState) -> Float = {
        when (it) {
            EnterExitState.PreEnter -> 1f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> 1f
        }
    },
    keepChildrenSizePlacement: Boolean = true,
): Modifier {
    with(sharedTransitionScope) {
        val animatedProgress = animatedVisibilityScope.transition.animateFloat(
            transitionSpec = { MaterialTheme.motionScheme.sharedElementTransitionSpec() },
            targetValueByState = targetValueByState,
        )

        val morphClip = remember(restingShape, targetShape) {
            OverlayClip(
                MorphPolygonShape(restingShape, targetShape) {
                    animatedProgress.value.coerceIn(0f, 1f)
                },
            )
        }
        val modifier = Modifier
            .drawWithContent {
                animatedProgress.value
                drawContent()
            }
            .then(
                if (keepChildrenSizePlacement) {
                    Modifier
                        .skipToLookaheadSize()
                        .skipToLookaheadPosition()
                } else {
                    Modifier
                },
            )
        return this@sharedBoundsRevealWithShapeMorph
            .sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                resizeMode = resizeMode,
                clipInOverlayDuringTransition = morphClip,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
            )
            .then(modifier)
    }
}

@Composable
fun Modifier.sharedAvatarElement(
    sharedElementKey: Any?,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    zIndexInOverlay: Float = 2f,
    restingShape: PolygonShape? = null,
    targetShape: PolygonShape? = null,
    targetValueByState: @Composable (state: EnterExitState) -> Float = {
        when (it) {
            EnterExitState.PreEnter -> 1f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> 1f
        }
    },
): Modifier {
    if (sharedElementKey == null || sharedTransitionScope == null || animatedVisibilityScope == null) {
        return this
    }
    with(sharedTransitionScope) {
        val (morphClip, drawModifier) = if (restingShape != null && targetShape != null) {
            val animatedProgress = animatedVisibilityScope.transition.animateFloat(
                transitionSpec = { MaterialTheme.motionScheme.sharedElementTransitionSpec() },
                targetValueByState = targetValueByState,
            )
            val clip = remember(restingShape, targetShape) {
                OverlayClip(
                    MorphPolygonShape(restingShape, targetShape) {
                        animatedProgress.value.coerceIn(0f, 1f)
                    },
                )
            }
            val modifier = Modifier.drawWithContent {
                animatedProgress.value
                drawContent()
            }
            clip to modifier
        } else {
            null to Modifier
        }

        return if (morphClip != null) {
            this@sharedAvatarElement
                .then(drawModifier)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(sharedElementKey),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = boundsTransform,
                    zIndexInOverlay = zIndexInOverlay,
                    clipInOverlayDuringTransition = morphClip,
                )
        } else {
            this@sharedAvatarElement.sharedElement(
                sharedContentState = rememberSharedContentState(sharedElementKey),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                zIndexInOverlay = zIndexInOverlay,
            )
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun SharedElementContextPreview(content: @Composable () -> Unit) {
    JetchatTheme {
        AnimatedContent(targetState = Unit) {
            CompositionLocalProvider(
                LocalNavAnimatedContentScope provides this,
                LocalNavAnimatedContentScope provides this,
            ) {
                content()
            }
        }
    }
}
