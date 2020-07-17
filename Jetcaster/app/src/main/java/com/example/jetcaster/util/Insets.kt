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

@file:Suppress("NOTHING_TO_INLINE")

package com.example.jetcaster.util

import android.view.View
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.core.view.ViewCompat
import androidx.ui.core.Constraints
import androidx.ui.core.IntrinsicMeasurable
import androidx.ui.core.IntrinsicMeasureScope
import androidx.ui.core.LayoutDirection
import androidx.ui.core.LayoutModifier
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.core.composed
import androidx.ui.core.offset
import androidx.ui.layout.height
import androidx.ui.layout.width
import kotlin.math.min

/**
 * Main holder of our inset values.
 */
@Stable
class DisplayInsets {
    val systemBars = Insets()
    val systemGestures = Insets()
}

@Stable
class Insets {
    var left by mutableStateOf(0)
        internal set
    var top by mutableStateOf(0)
        internal set
    var right by mutableStateOf(0)
        internal set
    var bottom by mutableStateOf(0)
        internal set

    /**
     * TODO: doesn't currently work
     */
    var visible by mutableStateOf(true)
        internal set
}

val InsetsAmbient = staticAmbientOf<DisplayInsets>()

@Composable
fun ProvideDisplayInsets(content: @Composable () -> Unit) {
    val view = ViewAmbient.current

    val displayInsets = remember { DisplayInsets() }

    onCommit(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            displayInsets.systemBars.updateFrom(windowInsets.systemWindowInsets)
            displayInsets.systemGestures.updateFrom(windowInsets.systemGestureInsets)

            // Return the unconsumed insets
            windowInsets
        }

        // Add an OnAttachStateChangeListener to request an inset pass each time we're attached
        // to the window
        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        }
        view.addOnAttachStateChangeListener(attachListener)

        if (view.isAttachedToWindow) {
            // If the view is already attached, we can request an inset pass now
            view.requestApplyInsets()
        }

        onDispose {
            view.removeOnAttachStateChangeListener(attachListener)
        }
    }

    Providers(InsetsAmbient provides displayInsets) {
        content()
    }
}

/**
 * Selectively apply additional space which matches the width/height of any system bars present
 * on the respective edges of the screen.
 *
 * @param enabled Whether to apply padding using the system bar dimensions on the respective edges.
 * Defaults to `true`.
 */
fun Modifier.systemBarsPadding(enabled: Boolean = true) = composed {
    insetsPadding(
        insets = InsetsAmbient.current.systemBars,
        left = enabled,
        top = enabled,
        right = enabled,
        bottom = enabled
    )
}

/**
 * Apply additional space which matches the height of the status height along the top edge
 * of the content.
 */
fun Modifier.statusBarPadding() = composed {
    insetsPadding(insets = InsetsAmbient.current.systemBars, top = true)
}

/**
 * Apply additional space which matches the height of the navigation bar height
 * along the [bottom] edge of the content, and additional space which matches the width of
 * the navigation bar on the respective [left] and [right] edges.
 *
 * @param bottom Whether to apply padding to the bottom edge, which matches the navigation bar
 * height (if present) at the bottom edge of the screen. Defaults to `true`.
 * @param left Whether to apply padding to the left edge, which matches the navigation bar width
 * (if present) on the left edge of the screen. Defaults to `true`.
 * @param right Whether to apply padding to the right edge, which matches the navigation bar width
 * (if present) on the right edge of the screen. Defaults to `true`.
 */
fun Modifier.navigationBarPadding(
    bottom: Boolean = true,
    left: Boolean = true,
    right: Boolean = true
) = composed {
    insetsPadding(
        insets = InsetsAmbient.current.systemBars,
        left = left,
        right = right,
        bottom = bottom
    )
}

/**
 * Updates our mutable state backed [Insets] from an Android system insets.
 */
private fun Insets.updateFrom(insets: androidx.core.graphics.Insets) {
    left = insets.left
    top = insets.top
    right = insets.right
    bottom = insets.bottom
}

/**
 * Declare the height of the content to match the height of the status bar exactly.
 *
 * This is very handy when used with `Spacer` to push content below the status bar:
 * ```
 * Column {
 *     Spacer(Modifier.statusBarHeight())
 *
 *     // Content to be drawn below status bar (y-axis)
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the status bar:
 * ```
 * Spacer(
 *     Modifier.statusBarHeight()
 *         .fillMaxWidth()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this uses [Modifier.height] so has the same characteristics with regards to incoming
 * layout constraints.
 */
fun Modifier.statusBarHeight() = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that status bar == top which is probably fine, but doesn't work
    // in multi-window, etc.
    InsetsSizeModifier(insets = InsetsAmbient.current.systemBars, heightSide = VerticalSide.Top)
}

/**
 * Declare the preferred height of the content to match the height of the navigation bar when present at the bottom of the screen.
 *
 * This is very handy when used with `Spacer` to push content below the navigation bar:
 * ```
 * Column {
 *     // Content to be drawn above status bar (y-axis)
 *     Spacer(Modifier.navigationBarHeight())
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the navigation bar:
 * ```
 * Spacer(
 *     Modifier.navigationBarHeight()
 *         .fillMaxWidth()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this uses [Modifier.height] so has the same characteristics with regards to incoming
 * layout constraints.
 */
fun Modifier.navigationBarHeight() = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that nav bar == bottom, which is wrong in landscape.
    // It also doesn't handle the IME correctly.
    InsetsSizeModifier(insets = InsetsAmbient.current.systemBars, heightSide = VerticalSide.Bottom)
}

enum class HorizontalSide { Left, Right }
enum class VerticalSide { Top, Bottom }

/**
 * Declare the preferred width of the content to match the width of the navigation bar,
 * on the given [side].
 *
 * This is very handy when used with `Spacer` to push content inside from any vertical
 * navigation bars (typically when the device is in landscape):
 * ```
 * Row {
 *     Spacer(Modifier.navigationBarWidth(HorizontalSide.Left))
 *
 *     // Content to be inside the navigation bars (x-axis)
 *
 *     Spacer(Modifier.navigationBarWidth(HorizontalSide.Right))
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the navigation bar:
 * ```
 * Spacer(
 *     Modifier.navigationBarWidth(HorizontalSide.Left)
 *         .fillMaxHeight()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this uses [Modifier.width] so has the same characteristics with regards to incoming
 * layout constraints.
 *
 * @param side The navigation bar side to use as the source for the width.
 */
fun Modifier.navigationBarWidth(side: HorizontalSide) = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that nav bar == left/right
    InsetsSizeModifier(insets = InsetsAmbient.current.systemBars, widthSide = side)
}

/**
 * Allows conditional setting of [insets] on each dimension.
 */
private inline fun Modifier.insetsPadding(
    insets: Insets,
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) = this + InsetsPaddingModifier(insets, left, top, right, bottom)

private data class InsetsPaddingModifier(
    private val insets: Insets,
    private val applyLeft: Boolean = false,
    private val applyTop: Boolean = false,
    private val applyRight: Boolean = false,
    private val applyBottom: Boolean = false
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): MeasureScope.MeasureResult {
        val left = if (applyLeft) insets.left else 0
        val top = if (applyTop) insets.top else 0
        val right = if (applyRight) insets.right else 0
        val bottom = if (applyBottom) insets.bottom else 0
        val horizontal = left + right
        val vertical = top + bottom

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = (placeable.width + horizontal)
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (placeable.height + vertical)
            .coerceIn(constraints.minHeight, constraints.maxHeight)
        return layout(width, height) {
            placeable.placeAbsolute(left, top)
        }
    }
}

private data class InsetsSizeModifier(
    private val insets: Insets,
    private val widthSide: HorizontalSide? = null,
    private val heightSide: VerticalSide? = null
) : LayoutModifier {
    private val targetConstraints
        get() = Constraints(
            minWidth = when (widthSide) {
                HorizontalSide.Left -> insets.left
                HorizontalSide.Right -> insets.right
                null -> 0
            },
            minHeight = when (heightSide) {
                VerticalSide.Top -> insets.top
                VerticalSide.Bottom -> insets.bottom
                null -> 0
            },
            maxWidth = when (widthSide) {
                HorizontalSide.Left -> insets.left
                HorizontalSide.Right -> insets.right
                null -> Constraints.Infinity
            },
            maxHeight = when (heightSide) {
                VerticalSide.Top -> insets.top
                VerticalSide.Bottom -> insets.bottom
                null -> Constraints.Infinity
            }
        )

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): MeasureScope.MeasureResult {
        val wrappedConstraints = targetConstraints.let { targetConstraints ->
            val resolvedMinWidth = if (widthSide != null) {
                targetConstraints.minWidth
            } else {
                min(constraints.minWidth, targetConstraints.maxWidth)
            }
            val resolvedMaxWidth = if (widthSide != null) {
                targetConstraints.maxWidth
            } else {
                min(constraints.maxWidth, targetConstraints.minWidth)
            }
            val resolvedMinHeight = if (heightSide != null) {
                targetConstraints.minHeight
            } else {
                min(constraints.minHeight, targetConstraints.maxHeight)
            }
            val resolvedMaxHeight = if (heightSide != null) {
                targetConstraints.maxHeight
            } else {
                min(constraints.maxHeight, targetConstraints.minHeight)
            }
            Constraints(resolvedMinWidth, resolvedMaxWidth, resolvedMinHeight, resolvedMaxHeight)
        }
        val placeable = measurable.measure(wrappedConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeAbsolute(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
        layoutDirection: LayoutDirection
    ) = measurable.minIntrinsicWidth(height, layoutDirection).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
        layoutDirection: LayoutDirection
    ) = measurable.maxIntrinsicWidth(height, layoutDirection).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
        layoutDirection: LayoutDirection
    ) = measurable.minIntrinsicHeight(width, layoutDirection).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
        layoutDirection: LayoutDirection
    ) = measurable.maxIntrinsicHeight(width, layoutDirection).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }
}
