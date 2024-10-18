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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetcaster.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.jetcaster.ui.LocalAnimatedVisibilityScope
import com.example.jetcaster.ui.LocalSharedTransitionScope

private val DefaultSpring = spring(
    stiffness = StiffnessMediumLow,
    visibilityThreshold = Rect.VisibilityThreshold
)

private val DefaultBoundsTransform = BoundsTransform { _, _ -> DefaultSpring }

private val ParentClip: OverlayClip =
    object : OverlayClip {
        override fun getClipPath(
            state: SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density
        ): Path? {
            return state.parentSharedContentState?.clipPathInOverlay
        }
    }

class ShapeBasedClip(
    val clipShape: Shape
) : OverlayClip {
    private val path = Path()

    override fun getClipPath(
        state: SharedContentState,
        bounds: Rect,
        layoutDirection: LayoutDirection,
        density: Density
    ): Path {
        path.reset()
        path.addOutline(
            clipShape.createOutline(
                bounds.size,
                layoutDirection,
                density
            )
        )
        path.translate(bounds.topLeft)
        return path
    }
}

/**
 * This modifier performs a NoOp when sharedTransitionScope or AnimatedVisibilityScope is null.
 * And by default pulls the scopes from the Composition Locals [LocalSharedTransitionScope] and [LocalNavAnimatedVisibilityScope].
 * Otherwise, it just calls [Modifier.sharedElement] with the provided parameters.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.safeSharedElement(
    key: Any,
    // todo figure out how to allow this with null scope:
    //  rememberSharedContentState requires a SharedTransitionScope
    // state: SharedContentState,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalAnimatedVisibilityScope.current,
    boundsTransform: BoundsTransform = DefaultBoundsTransform,
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) {
        return this
    }
    with(sharedTransitionScope) {
        return this@safeSharedElement then
            Modifier.sharedElement(
                rememberSharedContentState(key = key),
                animatedVisibilityScope,
                boundsTransform,
                placeHolderSize,
                renderInOverlayDuringTransition,
                zIndexInOverlay,
                clipInOverlayDuringTransition
            )
    }
}
