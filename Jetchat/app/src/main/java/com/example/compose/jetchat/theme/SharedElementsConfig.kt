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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.example.compose.jetchat.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import androidx.navigation3.ui.LocalNavAnimatedContentScope

sealed interface SharedElementKey {
    data class ProfileAvatar(val key: String) : SharedElementKey
    data class ProfileContainer(val key: String) : SharedElementKey
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> {
    null
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> {
    null
}

val FullScreenRoundedRectangle: RoundedPolygon = RoundedPolygon.rectangle(
    perVertexRounding = listOf(
        CornerRounding.Unrounded,
        CornerRounding.Unrounded,
        CornerRounding(0.15f),
        CornerRounding(0.15f),
    ),
).normalized()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun Morph.toShape(progress: Float, startAngle: Int = 0): Shape {
    return object : Shape {
        private var workPath: Path? = null
        private var lastSize = Size.Unspecified

        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            if (size != lastSize || workPath == null) {
                lastSize = size
                workPath = Path()
            } else {
                workPath!!.rewind()
            }
            val path = workPath!!
            toPath(progress = progress, path = path, startAngle = startAngle)
            val scaleMatrix = Matrix().apply { scale(x = size.width, y = size.height) }
            path.transform(scaleMatrix)
            path.translate(size.center - path.getBounds().center)
            return Outline.Generic(path)
        }
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
val MotionScheme.sharedElementTransitionBounds: BoundsTransform
    @Composable
    get() = BoundsTransform { _, _ -> sharedElementTransitionSpec() }

fun <T> MotionScheme.sharedElementTransitionSpec(): FiniteAnimationSpec<T> {
    return tween(600)
}

class MorphOverlayClip(val morph: Morph, private val animatedProgress: () -> Float) : SharedTransitionScope.OverlayClip {
    private val matrix = Matrix()
    private val workPath = Path()

    override fun getClipPath(
        sharedContentState: SharedTransitionScope.SharedContentState,
        bounds: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Path? {
        val progress = animatedProgress.invoke()
        workPath.reset()
        morph.toPath(progress = progress, path = workPath)
        matrix.reset()
        matrix.scale(bounds.width, bounds.height)
        workPath.transform(matrix)
        workPath.translate(bounds.center - workPath.getBounds().center)
        return workPath
    }
}

@Composable
fun Modifier.sharedBoundsRevealWithShapeMorph(
    sharedElementKey: Any?,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShape: RoundedPolygon = FullScreenRoundedRectangle,
    targetShape: RoundedPolygon = MaterialShapes.Cookie9Sided,
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
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShape: RoundedPolygon = FullScreenRoundedRectangle,
    targetShape: RoundedPolygon = MaterialShapes.Cookie9Sided,
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

        val morph = remember(restingShape, targetShape) {
            Morph(restingShape, targetShape)
        }
        val morphClip = remember(morph) {
            MorphOverlayClip(morph) { animatedProgress.value }
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
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionBounds,
    zIndexInOverlay: Float = 2f,
    restingShape: RoundedPolygon? = null,
    targetShape: RoundedPolygon? = null,
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
            val morph = remember(restingShape, targetShape) {
                Morph(restingShape, targetShape)
            }
            val clip = remember(morph) {
                MorphOverlayClip(morph) { animatedProgress.value }
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
                LocalNavAnimatedVisibilityScope provides this,
            ) {
                content()
            }
        }
    }
}
