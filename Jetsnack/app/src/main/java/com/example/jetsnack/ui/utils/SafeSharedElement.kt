@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetsnack.ui.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope


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
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    key: Any,
    // todo figure out how to allow this with null scope:
    //  rememberSharedContentState requires a SharedTransitionScope
    //state: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current,
    boundsTransform: BoundsTransform = DefaultBoundsTransform,
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip
): Modifier {
    if (sharedTransitionScope == null
        || animatedVisibilityScope == null
    ) return this
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

/**
 * This modifier performs a NoOp when sharedTransitionScope or AnimatedVisibilityScope is null.
 * And by default pulls the scopes from the Composition Locals [LocalSharedTransitionScope] and [LocalNavAnimatedVisibilityScope].
 * Otherwise, it just calls [Modifier.sharedBounds] with the provided parameters.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.safeSharedBounds(
    key: Any,
    // todo figure out how to allow this with null scope:
    //  rememberSharedContentState requires a SharedTransitionScope
    //sharedContentState: SharedContentState,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    boundsTransform: BoundsTransform = DefaultBoundsTransform,
    resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Center),
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip
): Modifier {
    if (sharedTransitionScope == null
        || animatedVisibilityScope == null
    ) return this
    with(sharedTransitionScope) {
        return this@safeSharedBounds then
                Modifier.sharedBounds(
                    rememberSharedContentState(key = key),
                    animatedVisibilityScope,
                    enter,
                    exit,
                    boundsTransform,
                    resizeMode,
                    placeHolderSize,
                    renderInOverlayDuringTransition,
                    zIndexInOverlay,
                    clipInOverlayDuringTransition
                )
    }
}
@Composable
fun Modifier.safeSkipToLookaheadSize(
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current
) : Modifier {
    if (sharedTransitionScope == null) return this@safeSkipToLookaheadSize
    with (sharedTransitionScope){
        return this@safeSkipToLookaheadSize then Modifier.skipToLookaheadSize()
    }
}

@Composable
fun Modifier.safeAnimateEnterExit(enter: EnterTransition,
                                  exit: ExitTransition,
                                  label: String = "animateEnterExit") : Modifier {
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current ?: return this@safeAnimateEnterExit
    with (animatedVisibilityScope){
        return this@safeAnimateEnterExit then Modifier.animateEnterExit(enter, exit, label)
    }
}
private val DefaultClipInOverlayDuringTransition: (LayoutDirection, Density) -> Path? =
    { _, _ -> null }

@Composable
fun Modifier.safeRenderInSharedTransitionScopeOverlay(
    /* todo figure out how to safely provide the LocalTransitionScope here.
    renderInOverlay: () -> Boolean = { LocalSharedTransitionScope.current?.isTransitionActive ?: false },*/
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: (LayoutDirection, Density) -> Path? =
        DefaultClipInOverlayDuringTransition
) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: return this@safeRenderInSharedTransitionScopeOverlay
    val renderInOverlay = { sharedTransitionScope.isTransitionActive }
    with (sharedTransitionScope){
        return this@safeRenderInSharedTransitionScopeOverlay then
                Modifier.renderInSharedTransitionScopeOverlay(renderInOverlay,
                    zIndexInOverlay,
                    clipInOverlayDuringTransition)
    }
}