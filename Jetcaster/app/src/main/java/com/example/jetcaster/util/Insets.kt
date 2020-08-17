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
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.LayoutModifier
import androidx.compose.ui.Measurable
import androidx.compose.ui.MeasureScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.LayoutDirectionAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.core.view.ViewCompat
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
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param additional Any additional height to add to the status bar size.
 */
fun Modifier.statusBarHeight(additional: Dp = 0.dp) = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that status bar == top which is probably fine, but doesn't work
    // in multi-window, etc.
    InsetsSizeModifier(
        insets = InsetsAmbient.current.systemBars,
        heightSide = VerticalSide.Top,
        additionalHeight = additional
    )
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
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param additional Any additional height to add to the navigation bar size.
 */
fun Modifier.navigationBarHeight(additional: Dp = 0.dp) = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that nav bar == bottom, which is wrong in landscape.
    // It also doesn't handle the IME correctly.
    InsetsSizeModifier(
        insets = InsetsAmbient.current.systemBars,
        heightSide = VerticalSide.Bottom,
        additionalHeight = additional
    )
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
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param side The navigation bar side to use as the source for the width.
 * @param additional Any additional width to add to the status bar size.
 */
fun Modifier.navigationBarWidth(
    side: HorizontalSide,
    additional: Dp = 0.dp
) = composed {
    // TODO: Move to Android 11 WindowInsets APIs when they land in AndroidX.
    // It currently assumes that nav bar == left/right
    InsetsSizeModifier(
        insets = InsetsAmbient.current.systemBars,
        widthSide = side,
        additionalWidth = additional
    )
}

/**
 * Returns the current insets converted into a [InnerPadding].
 *
 * @param start Whether to apply the inset on the start dimension.
 * @param top Whether to apply the inset on the top dimension.
 * @param end Whether to apply the inset on the end dimension.
 * @param bottom Whether to apply the inset on the bottom dimension.
 */
@Composable
fun Insets.toInnerPadding(
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true
): InnerPadding = with(DensityAmbient.current) {
    val layoutDirection = LayoutDirectionAmbient.current
    InnerPadding(
        start = when {
            start && layoutDirection == LayoutDirection.Ltr -> this@toInnerPadding.left.toDp()
            start && layoutDirection == LayoutDirection.Rtl -> this@toInnerPadding.right.toDp()
            else -> 0.dp
        },
        top = when {
            top -> this@toInnerPadding.top.toDp()
            else -> 0.dp
        },
        end = when {
            end && layoutDirection == LayoutDirection.Ltr -> this@toInnerPadding.right.toDp()
            end && layoutDirection == LayoutDirection.Rtl -> this@toInnerPadding.left.toDp()
            else -> 0.dp
        },
        bottom = when {
            bottom -> this@toInnerPadding.bottom.toDp()
            else -> 0.dp
        }
    )
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
) = this then InsetsPaddingModifier(insets, left, top, right, bottom)

private data class InsetsPaddingModifier(
    private val insets: Insets,
    private val applyLeft: Boolean = false,
    private val applyTop: Boolean = false,
    private val applyRight: Boolean = false,
    private val applyBottom: Boolean = false
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
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
            placeable.place(left, top)
        }
    }
}

private data class InsetsSizeModifier(
    private val insets: Insets,
    private val widthSide: HorizontalSide? = null,
    private val additionalWidth: Dp = 0.dp,
    private val heightSide: VerticalSide? = null,
    private val additionalHeight: Dp = 0.dp
) : LayoutModifier {
    private fun targetConstraints(density: Density): Constraints = with(density) {
        val additionalWidthPx = additionalWidth.toIntPx()
        val additionalHeightPx = additionalHeight.toIntPx()
        Constraints(
            minWidth = additionalWidthPx + when (widthSide) {
                HorizontalSide.Left -> insets.left
                HorizontalSide.Right -> insets.right
                null -> 0
            },
            minHeight = additionalHeightPx + when (heightSide) {
                VerticalSide.Top -> insets.top
                VerticalSide.Bottom -> insets.bottom
                null -> 0
            },
            maxWidth = when (widthSide) {
                HorizontalSide.Left -> insets.left + additionalWidthPx
                HorizontalSide.Right -> insets.right + additionalWidthPx
                null -> Constraints.Infinity
            },
            maxHeight = when (heightSide) {
                VerticalSide.Top -> insets.top + additionalHeightPx
                VerticalSide.Bottom -> insets.bottom + additionalHeightPx
                null -> Constraints.Infinity
            }
        )
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val wrappedConstraints = targetConstraints(this).let { targetConstraints ->
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
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height).let {
        val constraints = targetConstraints(this)
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height).let {
        val constraints = targetConstraints(this)
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width).let {
        val constraints = targetConstraints(this)
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width).let {
        val constraints = targetConstraints(this)
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }
}
