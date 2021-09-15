/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.layout.WindowInfoRepository
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator

/**
 * Opinionated set of viewport breakpoints
 *     - Compact: Most phones in portrait mode
 *     - Medium: Most foldables and tablets in portrait mode
 *     - Expanded: Most tablets in landscape mode
 *
 * More info: https://material.io/archive/guidelines/layout/responsive-ui.html
 */
enum class WindowSize { Compact, Medium, Expanded }

fun getWindowSize(width: Dp): WindowSize = when {
    width.value < 0f -> throw IllegalArgumentException("Dp value cannot be negative")
    width.value < 600f -> WindowSize.Compact
    width.value <= 800f -> WindowSize.Medium
    else -> WindowSize.Expanded
}

/**
 * Remembers the [WindowSize] corresponding to the current window metrics.
 */
@Composable
fun rememberWindowSizeState(): WindowSize {
    val windowMetrics by rememberCurrentWindowMetrics()
    val density = LocalDensity.current

    return remember(windowMetrics, density) {
        with(density) {
            getWindowSize(windowMetrics.bounds.width().toDp())
        }
    }
}

/**
 * Returns the [WindowMetrics] corresponding to the current activities
 * [WindowInfoRepository.currentWindowMetrics] as [State].
 */
@Composable
private fun rememberCurrentWindowMetrics(): State<WindowMetrics> {
    val activity = LocalContext.current.findActivity()
    val currentWindowMetricsFlow = remember(activity) {
        activity.windowInfoRepository().currentWindowMetrics
    }
    return currentWindowMetricsFlow.collectAsState(
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
    )
}

/**
 * Find the closest Activity in a given Context.
 */
private tailrec fun Context.findActivity(): Activity =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> throw IllegalStateException(
            "findActivity should be called in the context of an Activity"
        )
    }
