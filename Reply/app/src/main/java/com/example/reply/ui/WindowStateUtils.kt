/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.ui

import android.app.Activity
import android.graphics.Rect
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowMetricsCalculator
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Opinionated set of viewport breakpoints
 *     - Compact: Most phones in portrait mode
 *     - Medium: Most foldables and tablets in portrait mode
 *     - Expanded: Most tablets in landscape mode
 *
 * More info: https://material.io/archive/guidelines/layout/responsive-ui.html
 */
enum class WindowSize { Compact, Medium, Expanded }

/**
 * Remembers the [WindowSize] class for the window corresponding to the current window metrics.
 */
@Composable
fun Activity.rememberWindowSizeClass(): WindowSize {
    // Get the size (in pixels) of the window
    val windowSize = rememberWindowSize()

    // Convert the window size to [Dp]
    val windowDpSize = with(LocalDensity.current) {
        windowSize.toDpSize()
    }

    // Calculate the window size class
    return getWindowSizeClass(windowDpSize)
}

/**
 * Remembers the [Size] in pixels of the window corresponding to the current window metrics.
 */
@Composable
private fun Activity.rememberWindowSize(): Size {
    val configuration = LocalConfiguration.current
    // WindowMetricsCalculator implicitly depends on the configuration through the activity,
    // so re-calculate it upon changes.
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    }
    return windowMetrics.bounds.toComposeRect().size
}

/**
 * Partitions a [DpSize] into a enumerated [WindowSize] class.
 */
@VisibleForTesting
fun getWindowSizeClass(windowDpSize: DpSize): WindowSize = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSize.Compact
    windowDpSize.width < 840.dp -> WindowSize.Medium
    else -> WindowSize.Expanded
}

/**
 * Information about the posture of the device
 */
sealed interface DevicePosture {
    object NormalPosture : DevicePosture

    data class TableTopPosture(
        val hingePosition: Rect
    ) : DevicePosture

    data class BookPosture(
        val hingePosition: Rect
    ) : DevicePosture

    data class Separating(
        val hingePosition: Rect,
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}

@OptIn(ExperimentalContracts::class)
fun isTableTopPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}

@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

enum class ReplyNavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, FIXED_NAVIGATION_DRAWER
}

enum class ReplyContentType {
    LIST_ONLY, LIST_AND_DETAIL
}