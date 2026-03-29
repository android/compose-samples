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

package com.example.jetsnack.ui.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.toPath
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import kotlin.math.max

class MorphOverlayClip(val morph: Morph, private val animatedProgress: () -> Float) : SharedTransitionScope.OverlayClip {
    private val matrix = Matrix()

    override fun getClipPath(
        sharedContentState: SharedTransitionScope.SharedContentState,
        bounds: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Path? {
        matrix.reset()
        val max = max(bounds.width, bounds.height)
        matrix.scale(max, max)

        val path = morph.toPath(progress = animatedProgress.invoke()).asComposePath()
        path.transform(matrix)
        path.translate(bounds.center + Offset(-max / 2f, -max / 2f))
        return path
    }
}

@Composable
fun Modifier.sharedBoundsRevealWithShapeMorph(
    sharedContentState: SharedTransitionScope.SharedContentState,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current,
    boundsTransform: BoundsTransform = { tween(600) },
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShape: RoundedPolygon = RoundedPolygon.rectangle().normalized(),
    targetShape: RoundedPolygon = RoundedPolygon.circle().normalized(),
    renderInOverlayDuringTransition: Boolean = true,
    targetValueByState: @Composable (state: EnterExitState) -> Float = {
        when (it) {
            EnterExitState.PreEnter -> 1f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> 1f
        }
    },
    keepChildrenSizePlacement: Boolean = false,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return this
    with(sharedTransitionScope) {
        val animatedProgress =
            animatedVisibilityScope.transition.animateFloat(
                targetValueByState = targetValueByState,
                transitionSpec = {
                    tween(300, easing = LinearEasing)
                },
            )

        val morph = remember {
            Morph(restingShape, targetShape)
        }
        val morphClip = MorphOverlayClip(morph, { animatedProgress.value })
        val modifier = if (keepChildrenSizePlacement) {
            Modifier
                .skipToLookaheadSize()
                .skipToLookaheadPosition()
        } else {
            Modifier
        }
        return this@sharedBoundsRevealWithShapeMorph
            .sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                resizeMode = resizeMode,
                clipInOverlayDuringTransition = morphClip,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            )
            .then(modifier)
    }
}

fun RoundedPolygon.asShape(): Shape = GenericShape { size: Size, _ ->
    val matrix = Matrix().apply { scale(size.width, size.height) }
    this.addPath(this@asShape.toPath().asComposePath())
    this.transform(matrix)
}

fun Morph.asShape(progress: Float): Shape = GenericShape { size: Size, _ ->
    val matrix = Matrix().apply { scale(size.width, size.height) }
    this.addPath(this@asShape.toPath(progress).asComposePath())
    this.transform(matrix)
}

object SnackPolygons {
    val snackDetailPolygon = RoundedPolygon.pill(height = 1.85f).normalized()

    val snackItemPolygon = RoundedPolygon.rectangle(
        perVertexRounding = listOf(
            CornerRounding(0.25f), CornerRounding(0.25f),
            CornerRounding(0.25f), CornerRounding(0.25f),
        ),
    ).normalized()

    val snackItemPolygonRounded = RoundedPolygon.pill(height = 1.80f).normalized()
}
