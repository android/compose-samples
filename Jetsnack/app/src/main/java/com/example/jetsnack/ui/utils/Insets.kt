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

package com.example.jetsnack.ui.utils

import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Immutable
data class Insets(
    val left: Dp = 0.dp,
    val top: Dp = 0.dp,
    val right: Dp = 0.dp,
    val bottom: Dp = 0.dp
)

val InsetsAmbient = staticAmbientOf { Insets() }

@Composable
fun ProvideInsets(
    setImmersiveFlags: Boolean = true,
    content: @Composable () -> Unit
) {
    var currentInsets by remember { mutableStateOf<WindowInsetsCompat?>(null) }
    val view = ViewAmbient.current
    onCommit(setImmersiveFlags) {
        if (setImmersiveFlags) {
            // Set immersive flags to draw behind system bars
            @Suppress("DEPRECATION")
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }
    onActive {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            currentInsets = insets
            insets
        }
        view.requestApplyInsets()
    }

    val current = currentInsets
    val insets = if (current != null) {
        with(DensityAmbient.current) {
            Insets(
                left = current.systemWindowInsetLeft.toDp(),
                top = current.systemWindowInsetTop.toDp(),
                right = current.systemWindowInsetRight.toDp(),
                bottom = current.systemWindowInsetBottom.toDp()
            )
        }
    } else {
        Insets()
    }

    Providers(
        InsetsAmbient provides insets,
        children = content
    )
}

fun Modifier.systemBarPadding() = systemBarPadding(
    start = true,
    top = true,
    end = true,
    bottom = true
)

fun Modifier.systemBarPadding(
    horizontal: Boolean = false,
    vertical: Boolean = false
) = systemBarPadding(
    start = horizontal,
    top = vertical,
    end = horizontal,
    bottom = vertical
)

fun Modifier.systemBarPadding(
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false
): Modifier = composed {
    val insets = InsetsAmbient.current
    padding(
        start = if (start) insets.left else 0.dp,
        top = if (top) insets.top else 0.dp,
        end = if (end) insets.right else 0.dp,
        bottom = if (bottom) insets.bottom else 0.dp
    )
}
