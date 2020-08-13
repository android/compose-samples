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

package com.example.owl.ui.utils

import android.view.View
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

@Stable
class Insets {
    var left: Dp by mutableStateOf(0.dp)
        internal set
    var top: Dp by mutableStateOf(0.dp)
        internal set
    var right: Dp by mutableStateOf(0.dp)
        internal set
    var bottom: Dp by mutableStateOf(0.dp)
        internal set
}

internal val InsetsAmbient = staticAmbientOf {
    Insets()
}

@Composable
fun ProvideInsets(
    setImmersive: Boolean = true,
    content: @Composable () -> Unit
) {
    var currentInsets by remember { mutableStateOf<WindowInsetsCompat?>(null) }
    val view = ViewAmbient.current
    val density = DensityAmbient.current
    val insets = remember { Insets() }

    onCommit(view.id) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            currentInsets = insets
            insets
        }
        view.requestApplyInsets()
        onDispose {
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
        }
    }
    onCommit(setImmersive) {
        if (setImmersive) {
            // Set immersive flags to draw behind system bars
            @Suppress("DEPRECATION")
            view.systemUiVisibility = view.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    val current = currentInsets
    if (current != null) {
        with(density) {
            insets.left = current.systemWindowInsetLeft.toDp()
            insets.top = current.systemWindowInsetTop.toDp()
            insets.right = current.systemWindowInsetRight.toDp()
            insets.bottom = current.systemWindowInsetBottom.toDp()
        }
    }

    Providers(
        InsetsAmbient provides insets,
        children = content
    )
}

fun Modifier.systemBarPadding() = systemBarPadding(
    left = true,
    top = true,
    right = true,
    bottom = true
)

fun Modifier.systemBarPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
): Modifier = composed {
    val insets = InsetsAmbient.current
    absolutePadding(
        left = if (left) insets.left else 0.dp,
        top = if (top) insets.top else 0.dp,
        right = if (right) insets.right else 0.dp,
        bottom = if (bottom) insets.bottom else 0.dp
    )
}
