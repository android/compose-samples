/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui.utils

import android.view.View
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.staticAmbientOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.core.composed
import androidx.ui.layout.padding
import androidx.ui.unit.Dp
import androidx.ui.unit.dp

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
    var currentInsets by state<WindowInsetsCompat?> { null }
    val view = ViewAmbient.current
    onCommit(setImmersiveFlags) {
        if (setImmersiveFlags) {
            // Set immersive flags to draw behind system bars
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
